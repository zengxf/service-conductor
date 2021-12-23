package cn.zxf.conductor.core;

import cn.zxf.conductor.core.entity.*;
import cn.zxf.conductor.core.enums.FlowResultTypeEnum;
import cn.zxf.conductor.core.threadpool.TaskThreadPool;
import cn.zxf.conductor.core.utils.AssertUtils;
import cn.zxf.conductor.core.utils.BooleanUtils;
import cn.zxf.conductor.core.utils.IdUtils;
import cn.zxf.conductor.core.utils.Jexl3Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 流程执行器
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
@Slf4j
@Component
public class FlowExecutor {

    @Autowired(required = false)
    private List<FlowExecuteInterceptor> interceptors;
    private Map<Integer, TaskExecutor> executorMap;


    @Autowired
    public void setExecutors(List<TaskExecutor> executors) {
        this.executorMap = executors.stream()
                .collect(Collectors.toMap(TaskExecutor::matchType, Function.identity()));
    }


    /*** 执行流程 */
    public FlowExecuteResult execute(Flow flow) {
        AssertUtils.requireNotNull(flow, "执行的流程不能为空！");
        log.info("开始执行流程，flow: [{}]", flow.showStr());

        ExecutedFlow exeFlow = ExecutedFlow.of(flow); // 组装下以便执行

        List<FlowExecuteInterceptor> matchInterceptors = this.getMatchInterceptors(exeFlow);
        ExecuteContext context = new ExecuteContext()
                .setGlobalId(IdUtils.genGlobalId());
        FlowExecuteResult res = null;
        Exception preEx = null, exeEx = null;
        boolean sign = true;
        int runIndex = 0;
        FlowExecuteInterceptor blockedInterceptor = null;

        // 前置处理
        for (FlowExecuteInterceptor interceptor : matchInterceptors) {
            try {
                sign = interceptor.preExe(exeFlow, context);
                if (!sign) {
                    blockedInterceptor = interceptor;
                    break;
                }
            } catch (Exception e) {
                preEx = e;
                blockedInterceptor = interceptor;
                break;
            }
            runIndex++;
        }

        // 真正执行
        if (sign && preEx == null) {
            try {
                res = this.doExecute(exeFlow, context);
                log.info("执行结果：[{}]", res);
            } catch (Exception e) {
                exeEx = e;
            }
        }

        // 组装结果
        if (!sign) {
            log.warn("流程拦截器前置处理时，阻止了其继续执行！");
            res = this.buildResultByIntercept(blockedInterceptor);
        } else if (preEx != null) {
            log.warn("流程拦截器前置处理出错！错误信息概要：[{}]", preEx.getMessage());
            res = this.buildResultByPreException(blockedInterceptor, preEx);
        } else if (exeEx != null) {
            log.warn("流程执行出错！错误信息概要：[{}]", exeEx.getMessage());
            res = this.buildResultByExeException(exeEx);
        }

        // 不论 pre 和 handle 处理是否成功，都会后置处理
        for (int i = runIndex - 1; i >= 0; i--) {
            FlowExecuteInterceptor interceptor = matchInterceptors.get(i);
            try {
                interceptor.postExe(exeFlow, context, res);
            } catch (Exception e) {
                log.error("流程拦截器后置处理出错！", e);
            }
        }
        log.info("流程执行结束！flow: [{}]", flow.showStr());

        return res;
    }

    private FlowExecuteResult doExecute(ExecutedFlow flow, ExecuteContext context) throws Exception {
        FlowExecuteResult result = new FlowExecuteResult();

        // 执行子任务逻辑：遍历子任务，判断子任务的条件，然后加到队列
        Queue<Task> queue = new LinkedList<>();
        Task first = flow.startTask();
        AssertUtils.requireNotNull(first, "开始任务为空！");
        queue.offer(first);

        // 执行顺序是：广度优先遍历执行
        while (!queue.isEmpty()) {
            int size = queue.size();
            Map<Task, Future<TaskExecuteResult>> taskResMap = new HashMap<>(size);

            for (int i = 0; i < size; i++) {    // 同级任务并行执行
                Task curTask = queue.poll();
                Future<TaskExecuteResult> future = this.executeTask(curTask, context);
                taskResMap.put(curTask, future);
            } // for end

            signFor:
            for (Map.Entry<Task, Future<TaskExecuteResult>> entry : taskResMap.entrySet()) {
                Task curTask = entry.getKey();  // 当前任务执行完的处理逻辑
                Future<TaskExecuteResult> future = entry.getValue();
                TaskExecuteResult taskResult = future.get();
                context.setupTaskObj(curTask, taskResult);
                result.setLastResult(taskResult);

                List<TaskLinker> nextLinkers = flow.nextLinkers(curTask.contextSign());
                if (CollectionUtils.isEmpty(nextLinkers)) {
                    log.info("[{}] 的子任务为空！", curTask.showStr());
                    continue signFor;
                }

                for (TaskLinker nextLinker : nextLinkers) {
                    Task nextTask = flow.getTaskBySign(nextLinker.getNextTaskContextSign());
                    AssertUtils.requireNotNull(nextTask,
                            "下一个任务为空！curSign: [{}], nextSign: [{}]",
                            curTask.contextSign(), nextLinker.getNextTaskContextSign());
                    String exp = nextLinker.getConditionExpression();
                    Object condition;
                    if (nextTask.isJoin()) {
                        if (StringUtils.isEmpty(exp)) {
                            condition = true; // join 的话，条件可以为空
                        } else {
                            condition = Jexl3Utils.execute(exp, context.toMap());
                        }
                    } else {
                        AssertUtils.requireNotBlank(exp, "任务条件表达式未定义！");
                        condition = Jexl3Utils.execute(exp, context.toMap());
                    }
                    log.info("[{}] 的子任务 [{}] 的执行条件为：[{} => {}]",
                            curTask.showStr(), nextTask.showStr(), exp, condition);
                    boolean canAdd = BooleanUtils.isTrue(condition); // 条件为真
                    if (canAdd && nextTask.isJoin())
                        canAdd = this.canAddByJoin(queue, nextTask, context, flow);
                    if (canAdd) // 可以添加
                        queue.offer(nextTask);
                }
            } // for map end
        } // while end

        return result.setResultType(FlowResultTypeEnum.NORMAL.code);
    }

    // 根据是不是 Join 判断要不要添加
    private boolean canAddByJoin(Queue<Task> queue, Task task, ExecuteContext context, ExecutedFlow flow) {
        String curTaskSign = task.contextSign();
        boolean isAdded = queue.stream()
                .anyMatch(added -> StringUtils.equals(curTaskSign, added.contextSign()));
        if (isAdded)    // 判断是否已经添加。这一步有点多余，上面并行完后，会串行处理
            return false;

        Map<String, Object> executedTaskMap = context.toMap();
        Set<String> executedTaskSignSet = executedTaskMap.keySet();
        log.info("所有已执行的任务有：[{}]", executedTaskSignSet);

        List<TaskLinker> previousLinkers = flow.previousLinkers(curTaskSign);
        Set<String> superTaskSignSet = previousLinkers.stream()
                .map(TaskLinker::getPreviousTaskContextSign)
                .collect(Collectors.toSet());
        log.info("任务 [{}] 需等待的所有上级任务有：[{}]", curTaskSign, superTaskSignSet);

        boolean allExecuted = executedTaskSignSet.containsAll(superTaskSignSet);
        log.info("所有上级任务是否已执行：[{}]", allExecuted);
        return allExecuted;
    }

    // 执行任务
    private Future<TaskExecuteResult> executeTask(Task task, ExecuteContext context) throws Exception {
        AssertUtils.requireNotNull(task, "任务不能为空");
        AssertUtils.requireNotBlank(task.contextSign(), "任务标识不能为空");

        TaskExecutor executor = executorMap.get(task.type());
        AssertUtils.requireNotNull(executor, "找不到任务的执行器，task-type: [{}]", task.type());

        Future<TaskExecuteResult> future = TaskThreadPool.submit(executor, task, context);
        return future;
    }

    // 获取匹配的拦截器，已处理 null 的情况，方便上面统一处理
    private List<FlowExecuteInterceptor> getMatchInterceptors(ExecutedFlow flow) {
        List<FlowExecuteInterceptor> matches;
        if (CollectionUtils.isNotEmpty(interceptors)) {
            matches = interceptors.stream()
                    .filter(interceptor -> interceptor.isMatch(flow))
                    .collect(Collectors.toList());
        } else {
            matches = new ArrayList<>();
        }
        return matches;
    }

    // ------

    // 构建返回结果
    private FlowExecuteResult buildResultByIntercept(FlowExecuteInterceptor blockedInterceptor) {
        FlowExecuteResult res = new FlowExecuteResult()
                .setInterceptor(blockedInterceptor)
                .setResultType(FlowResultTypeEnum.INTERCEPT.code);
        return res;
    }

    private FlowExecuteResult buildResultByPreException(FlowExecuteInterceptor blockedInterceptor, Exception ex) {
        FlowExecuteResult res = new FlowExecuteResult()
                .setInterceptor(blockedInterceptor)
                .setResultType(FlowResultTypeEnum.INTERCEPTOR_PRE_EXE_EXCEPTION.code)
                .setException(ex);
        return res;
    }

    private FlowExecuteResult buildResultByExeException(Exception ex) {
        FlowExecuteResult res = new FlowExecuteResult()
                .setResultType(FlowResultTypeEnum.EXECUTE_EXCEPTION.code)
                .setException(ex);
        return res;
    }

}
