package cn.zxf.conductor.core;

import cn.zxf.conductor.core.constant.InterceptorConstant;
import cn.zxf.conductor.core.entity.Task;
import cn.zxf.conductor.core.entity.TaskExecuteResult;
import org.springframework.core.Ordered;

/**
 * 任务执行拦截器
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
public interface TaskExecuteInterceptor extends Ordered, InterceptorConstant.TaskOrder {

    @Override
    default int getOrder() {
        return DEF_ORDER;
    }

    /**
     * 是否匹配，不匹配则不添加到到拦截链路里面
     *
     * @return true: 匹配
     */
    default boolean isMatch(Task task) {
        return true;
    }

    /**
     * 执行前调用
     *
     * @return true: 继续执行
     */
    boolean preExe(Task task, ExecuteContext context) throws Exception;

    /*** 执行完调用 */
    void postExe(Task task, ExecuteContext context, TaskExecuteResult result) throws Exception;

}
