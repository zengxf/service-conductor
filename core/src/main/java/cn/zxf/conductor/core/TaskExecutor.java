package cn.zxf.conductor.core;

import cn.zxf.conductor.core.entity.Task;
import cn.zxf.conductor.core.entity.TaskExecuteResult;

/**
 * 任务执行器
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
public class TaskExecutor<T extends Task, R extends TaskExecuteResult> {

    /*** 执行任务 */
    public R execute(T task, ExecuteContext context) {
        return null;
    }

}
