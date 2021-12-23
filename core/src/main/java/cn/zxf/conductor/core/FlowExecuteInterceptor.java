package cn.zxf.conductor.core;

import cn.zxf.conductor.core.constant.InterceptorConstant;
import cn.zxf.conductor.core.entity.ExecutedFlow;
import cn.zxf.conductor.core.entity.Flow;
import cn.zxf.conductor.core.entity.FlowExecuteResult;
import org.springframework.core.Ordered;

/**
 * 流程执行拦截器
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
public interface FlowExecuteInterceptor extends Ordered, InterceptorConstant.FlowLevel {

    @Override
    default int getOrder() {
        return DEF_ORDER;
    }

    /**
     * 是否匹配，不匹配则不添加到到拦截链路里面
     *
     * @return true: 匹配
     */
    default boolean isMatch(ExecutedFlow flow) {
        return true;
    }

    /**
     * 执行前调用
     *
     * @return true: 继续执行
     */
    boolean preExe(ExecutedFlow flow, ExecuteContext context) throws Exception;

    /*** 执行完调用 */
    void postExe(ExecutedFlow flow, ExecuteContext context, FlowExecuteResult result) throws Exception;

}
