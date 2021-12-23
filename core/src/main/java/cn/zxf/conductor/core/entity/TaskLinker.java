package cn.zxf.conductor.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务之间的连线
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
@Data
@Accessors
public class TaskLinker {

    private String previousTaskContextSign; // 上一个任务的标识
    private String conditionExpression;     // 执行下一个任务的条件表达式
    private String nextTaskContextSign;     // 下一个任务的标识

}
