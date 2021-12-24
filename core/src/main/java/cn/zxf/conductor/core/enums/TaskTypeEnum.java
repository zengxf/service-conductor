package cn.zxf.conductor.core.enums;

import lombok.AllArgsConstructor;

/**
 * 任务类型
 * <br/>
 * 此作参考，自定义扩展，不要与此冲突
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
@AllArgsConstructor
public enum TaskTypeEnum {

    START(1, "开始任务"),
    JOIN(2, "等待任务"),
    END(3, "结束任务"),

    OPEN_API(10, "OpenAPI 任务"),
    ;

    public final Integer code;
    public final String desc;

}
