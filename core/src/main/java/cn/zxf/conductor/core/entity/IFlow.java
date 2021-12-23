package cn.zxf.conductor.core.entity;

import java.util.List;

/**
 * 流程接口
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
public interface IFlow {

    /*** 流程的开始任务标识 */
    String startTaskContextSign();

    /*** 所有的任务节点 */
    List<Task> tasks();

    /*** 所有的任务节点的连线 */
    List<TaskLinker> linkers();

    /*** 规则优先级 */
    int priority();

    /*** 显示字符串，用于日志打印 */
    String showStr();

}
