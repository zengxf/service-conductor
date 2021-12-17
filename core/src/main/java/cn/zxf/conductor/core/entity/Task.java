package cn.zxf.conductor.core.entity;

import cn.zxf.conductor.core.utils.ConductorConstant;

/**
 * 任务的定义
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
public class Task {

    /*** 任务类型 */
    public int type() {
        return 0;
    }

    /*** 显示字符串，用于日志打印 */
    public String showStr() {
        return ConductorConstant.EMPTY_STR;
    }

    /*** 上下文标识 */
    public String contextSign() {
        return ConductorConstant.EMPTY_STR;
    }

    /*** 线程分配标识 */
    public String threadSign() {
        return ConductorConstant.EMPTY_STR;
    }

    /*** 是否需要同步等待 */
    public boolean isJoin() {
        return false;
    }

}
