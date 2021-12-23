package cn.zxf.conductor.core.entity;

import cn.zxf.conductor.core.FlowExecuteInterceptor;
import cn.zxf.conductor.core.enums.FlowResultTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 流程执行结果
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
@Data
@Accessors(chain = true)
public class FlowExecuteResult {

    /*** 结果类型，参考：{@link FlowResultTypeEnum} */
    private Integer resultType;
    private Exception exception;                // 异常时的异常
    private Integer timeout;                    // 超时时的超时时间设置 ms(毫秒)
    private FlowExecuteInterceptor interceptor; // 被拦截时的拦截器，方便跟踪
    private TaskExecuteResult lastResult;       // 最后一个任务执行的结果

}
