package cn.zxf.conductor.core.entity;

import cn.zxf.conductor.core.constant.ConductorConstant;
import cn.zxf.conductor.core.enums.TaskTypeEnum;

/**
 * 任务的定义
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
public abstract class Task<IN extends InputParam> {

    /*** 任务类型，参考：{@link TaskTypeEnum} */
    public abstract int type();

    /*** 任务超时时间（ms) */
    public int timeoutMs() {
        return ConductorConstant.DEF_TASK_TIMEOUT_MS;
    }

    /*** 任务入参，将作为表达式解析的一部分 */
    public IN inputParam() {
        return null;
    }

    /*** 显示字符串，用于日志打印 */
    public abstract String showStr();

    /*** 上下文标识 */
    public abstract String contextSign();

    /*** 线程分配标识 */
    public String threadSign() {
        return ConductorConstant.EMPTY_STR;
    }

    /*** 是否需要同步等待 */
    public boolean isJoin() {
        return ConductorConstant.DEF_TASK_JOIN;
    }

}
