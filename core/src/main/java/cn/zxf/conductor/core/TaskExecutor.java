package cn.zxf.conductor.core;

import cn.zxf.conductor.core.entity.Task;
import cn.zxf.conductor.core.entity.TaskExecuteResult;
import cn.zxf.conductor.core.enums.TaskResultTypeEnum;
import cn.zxf.conductor.core.threadpool.TaskThreadPool;
import cn.zxf.conductor.core.utils.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 任务执行器
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
@Slf4j
public abstract class TaskExecutor<T extends Task, R extends TaskExecuteResult> {

    @Autowired(required = false)
    protected List<TaskExecuteInterceptor> interceptors;

    /*** 匹配任务的类型 */
    public abstract Integer matchType();

    /*** 执行任务 */
    public R execute(T task, ExecuteContext context) {
        AssertUtils.requireNotNull(task, "待执行的任务为空");
        log.info("开始执行任务，task: [{}]", task);

        List<TaskExecuteInterceptor> matchInterceptors = this.getMatchInterceptors(task);
        R res = null;
        Exception preEx = null, exeEx = null;
        boolean sign = true, isTimeout = false;
        int runIndex = 0;
        int timeout = task.timeoutMs();
        TaskExecuteInterceptor blockedInterceptor = null;

        // 前置处理
        for (TaskExecuteInterceptor interceptor : matchInterceptors) {
            try {
                sign = interceptor.preExe(task, context);
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
                Future<R> future = this.submitTask(task, context);
                res = future.get(timeout, TimeUnit.MILLISECONDS);
                log.info("执行结果：[{}]", res);
            } catch (TimeoutException e) {
                isTimeout = true;
            } catch (Exception e) {
                exeEx = e;
            }
        }

        // 组装结果
        res = this.assembleResult(res, preEx, exeEx, sign, isTimeout, timeout, blockedInterceptor);

        // 后置处理
        for (int i = runIndex - 1; i >= 0; i--) {
            TaskExecuteInterceptor interceptor = matchInterceptors.get(i);
            try {
                interceptor.postExe(task, context, res);
            } catch (Exception e) {
                log.error("任务拦截器后置处理出错！", e);
            }
        }

        log.info("任务执行结束！task: [{}], result-type: [{}]", task.showStr(), res.getResultType());
        return res;
    }

    // 组装结果
    private R assembleResult(
            R res, Exception preEx, Exception exeEx,
            boolean sign, boolean isTimeout, int timeout,
            TaskExecuteInterceptor interceptor
    ) {
        if (!sign) {
            log.warn("任务拦截器前置处理时，阻止了其继续执行！");
            return this.buildResultByIntercept(interceptor);
        }
        if (preEx != null) {
            log.warn("任务拦截器前置处理出错！错误信息概要：[{}]", preEx.getMessage());
            return this.buildResultByPreException(interceptor, preEx);
        }
        if (exeEx != null) {
            log.warn("任务执行出错！错误信息概要：[{}]", exeEx.getMessage());
            return this.buildResultByExeException(exeEx);
        }
        if (isTimeout) {
            log.warn("任务执行超时！timeout：[{}]ms", timeout);
            return this.buildResultByTimeout(timeout);
        }
        return res.resultType(TaskResultTypeEnum.NORMAL);
    }

    // 获取匹配的拦截器，已处理 null 的情况，方便上面统一处理
    private List<TaskExecuteInterceptor> getMatchInterceptors(T task) {
        List<TaskExecuteInterceptor> matches;
        if (CollectionUtils.isNotEmpty(interceptors)) {
            matches = interceptors.stream() // 处理maven编译报错
                    .filter(interceptor -> interceptor.isMatch(task))
                    .collect(Collectors.toList());
        } else {
            matches = new ArrayList<>();
        }
        return matches;
    }

    private Future<R> submitTask(T task, ExecuteContext context) {
        Callable<R> callable = () -> {
            R result = this.doExecute(task, context);
            return result;
        };
        Future<R> future = TaskThreadPool.doSubmit(callable);
        return future;
    }

    // ------

    // 构建返回结果
    private R buildResultByIntercept(TaskExecuteInterceptor blockedInterceptor) {
        R res = this.newResultInstance()
                .interceptor(blockedInterceptor)
                .resultType(TaskResultTypeEnum.INTERCEPT);
        return res;
    }

    private R buildResultByTimeout(int timeout) {
        R res = this.newResultInstance()
                .resultType(TaskResultTypeEnum.TIMEOUT)
                .timeout(timeout);
        return res;
    }

    private R buildResultByPreException(TaskExecuteInterceptor blockedInterceptor, Exception ex) {
        R res = this.newResultInstance()
                .interceptor(blockedInterceptor)
                .resultType(TaskResultTypeEnum.INTERCEPTOR_PRE_EXE_EXCEPTION)
                .exception(ex);
        return res;
    }

    private R buildResultByExeException(Exception ex) {
        R res = this.newResultInstance()
                .resultType(TaskResultTypeEnum.EXECUTE_EXCEPTION)
                .exception(ex);
        return res;
    }

    // ------

    /*** 子类的具体执行。再次交给线程池执行，以做超时判断 */
    protected abstract R doExecute(T task, ExecuteContext context) throws Exception;

    /*** 子类实例化具体的返回结果 */
    protected abstract R newResultInstance();

}
