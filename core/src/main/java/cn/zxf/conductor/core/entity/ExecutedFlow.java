package cn.zxf.conductor.core.entity;

import lombok.Getter;
import lombok.experimental.Delegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 可执行的流程（用于对流程的封装）
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
public final class ExecutedFlow implements IFlow {

    @Getter
    @Delegate(types = IFlow.class)
    private Flow delegate;
    private Map<String, Task> taskMap;
    private Map<String, List<TaskLinker>> previousLinkerMap;
    private Map<String, List<TaskLinker>> nextLinkerMap;


    public static ExecutedFlow of(Flow flow) {
        ExecutedFlow exeFlow = new ExecutedFlow();
        exeFlow.delegate = flow;
        exeFlow.assemble();
        return exeFlow;
    }


    // 将零散的任务和连线组装成 Map
    private void assemble() {
        List<Task> tasks = delegate.tasks();
        List<TaskLinker> linkers = delegate.linkers();
        if (tasks == null) {
            this.taskMap = new HashMap<>();
        } else {
            this.taskMap = tasks.stream()
                    .collect(Collectors.toMap(Task::contextSign, Function.identity()));
        }
        if (linkers == null) {
            this.nextLinkerMap = new HashMap<>();
        } else {
            this.previousLinkerMap = linkers.stream()
                    .collect(Collectors.groupingBy(TaskLinker::getNextTaskContextSign));
            this.nextLinkerMap = linkers.stream()
                    .collect(Collectors.groupingBy(TaskLinker::getPreviousTaskContextSign));
        }
    }

    /*** 获取开始任务 */
    @Nullable
    public Task startTask() {
        return this.taskMap.get(this.startTaskContextSign());
    }

    /*** 获取当前任务下面的所有连线 */
    @Nonnull
    public List<TaskLinker> nextLinkers(String curTaskContextSign) {
        return this.nextLinkerMap.getOrDefault(curTaskContextSign, new ArrayList<>());
    }

    /*** 获取当前任务上面的所有连线 */
    @Nonnull
    public List<TaskLinker> previousLinkers(String curTaskContextSign) {
        return this.previousLinkerMap.getOrDefault(curTaskContextSign, new ArrayList<>());
    }

    /*** 根据标识获取任务 */
    @Nullable
    public Task getTaskBySign(String taskContextSign) {
        return this.taskMap.get(taskContextSign);
    }

}
