package cn.zxf.conductor.core.utils;

import cn.hutool.core.util.StrUtil;
import cn.zxf.conductor.core.exception.EmptyCollectionException;
import cn.zxf.conductor.core.exception.BlankStringException;
import cn.zxf.conductor.core.exception.NullObjectException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * 断言帮助类
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
public class AssertUtils {

    /*** 如果对象为空，抛出异常 */
    public static <T> T requireNotNull(T obj, String messageFmt, Object... params) {
        if (obj == null) {
            String message = StrUtil.format(messageFmt, params);
            throw new NullObjectException(message);
        }
        return obj;
    }

    /*** 如果字段串为空，抛出异常 */
    public static String requireNotBlank(String str, String messageFmt, Object... params) {
        if (StringUtils.isBlank(str)) {
            String message = StrUtil.format(messageFmt, params);
            throw new BlankStringException(message);
        }
        return str;
    }

    /*** 如果集合为空，抛出异常 */
    public static <C extends Collection, T> C requireNotEmpty(C collection, String messageFmt, Object... params) {
        if (CollectionUtils.isEmpty(collection)) {
            String message = StrUtil.format(messageFmt, params);
            throw new EmptyCollectionException(message);
        }
        return collection;
    }

}
