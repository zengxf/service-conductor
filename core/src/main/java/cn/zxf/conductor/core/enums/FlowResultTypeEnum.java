package cn.zxf.conductor.core.enums;

import lombok.AllArgsConstructor;

/**
 * 流程执行结果类型
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
@AllArgsConstructor
public enum FlowResultTypeEnum {

    INTERCEPT(10, "拦截器阻止"),
    INTERCEPTOR_PRE_EXE_EXCEPTION(11, "拦截器前置处理异常"),
    EXECUTE_EXCEPTION(20, "执行时异常"),
    TIMEOUT(30, "任务超时"),
    NOT_COMPLETE(40, "正常但未完全执行完"),
    NORMAL(100, "正常返回"),
    ;

    public final Integer code;
    public final String desc;

}
