package cn.zxf.conductor.core.utils;

import java.util.Objects;

/**
 * 布尔类型判断帮助类
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
public class BooleanUtils {

    /*** 判断：如果是 Boolean.TRUE 则返回为 true */
    public static boolean isTrue(Object value) {
        return Objects.equals(Boolean.TRUE, value);
    }

    /*** 判断：如果是 null 或不是 Boolean.TRUE 则返回为 true */
    public static boolean notTrue(Object value) {
        return !isTrue(value);
    }

}
