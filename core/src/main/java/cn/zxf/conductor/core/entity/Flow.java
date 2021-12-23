package cn.zxf.conductor.core.entity;

import cn.zxf.conductor.core.constant.ConductorConstant;

/**
 * 编排流程的定义
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
public abstract class Flow implements IFlow {

    /*** 规则优先级 */
    @Override
    public int priority() {
        return ConductorConstant.DEF_FLOW_PRIORITY;
    }

}
