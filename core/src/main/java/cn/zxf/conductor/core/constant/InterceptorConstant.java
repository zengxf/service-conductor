package cn.zxf.conductor.core.constant;

/**
 * 拦截器相关常量
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
public interface InterceptorConstant {

    /* 拦截器的执行顺序统一在此设置，以方便管理 */

    /*** 任务拦截器执行顺序的定义 */
    interface TaskOrder {
        int
                LOGGING_ORDER = 15,          // 日志记录
                DEAD_CHAIN_CHECK_ORDER = 25, // 死链检测
                PARAM_PARSE_ORDER = 35,      // 参数解析
                DEF_ORDER = 100;             // 默认
    }

    /*** 流程拦截器执行顺序的定义 */
    interface FlowLevel {
        int
                LOGGING_ORDER = 15,          // 日志记录
                STATUS_CHECK_ORDER = 25,     // 状态检测
                DEAD_CHAIN_CHECK_ORDER = 35, // 死链检测
                DEF_ORDER = 100;             // 默认
    }

}
