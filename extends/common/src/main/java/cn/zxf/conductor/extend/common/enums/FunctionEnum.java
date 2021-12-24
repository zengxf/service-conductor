package cn.zxf.conductor.extend.common.enums;

import cn.hutool.core.util.StrUtil;
import cn.zxf.conductor.core.exception.EnumNotFoundException;
import cn.zxf.conductor.extend.common.utils.CodeUtils;
import lombok.AllArgsConstructor;

import java.util.stream.Stream;

/**
 * 支持使用的函数的定义
 * <br/>
 * Created by ZXFeng on 2021/12/24.
 */
@AllArgsConstructor
public enum FunctionEnum {

    BASE64("base64", CodeUtils.class, "encodeBase64", new Class[]{String.class}, String.class),
    ;

    public final String funName;    // 函数名
    public final Class clazz;       // 用于反射调用的类
    public final String method;     // 用于反射调用的方法名
    public final Class[] paramTypes;// 用于反射调用的方法参数
    public final Class returnType;  // 用于反射调用的方法返回类型

    public static FunctionEnum get(String funName) {
        return Stream.of(FunctionEnum.values())
                .filter(type -> type.funName.equals(funName))
                .findFirst()
                .orElseThrow(() -> new EnumNotFoundException("不支持的函数！function: [{}]", funName));
    }

}
