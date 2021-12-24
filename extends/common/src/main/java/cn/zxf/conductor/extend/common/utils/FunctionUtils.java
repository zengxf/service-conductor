package cn.zxf.conductor.extend.common.utils;

import cn.zxf.conductor.core.utils.AssertUtils;
import cn.zxf.conductor.extend.common.enums.FunctionEnum;
import cn.zxf.conductor.extend.common.exception.FunctionInvokeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 支持使用的函数调用帮助类
 * <br/>
 * Created by ZXFeng on 2021/12/24.
 */
@Slf4j
public class FunctionUtils {

    /*** 调用函数 */
    public static Object invoke(String funName, Object curValue, List<Object> params) {
        List<Object> invokeParams = new ArrayList<>();
        invokeParams.add(curValue);
        if (CollectionUtils.isNotEmpty(params)) {
            invokeParams.addAll(params);
        }
        return invoke(funName, invokeParams);
    }

    /*** 调用函数 */
    public static Object invoke(String funName, @Nonnull List<Object> params) {
        AssertUtils.requireNotEmpty(params, "参数集不能为空！");
        FunctionEnum fun = FunctionEnum.get(funName);
        try {
            Method method = fun.clazz.getMethod(fun.method, fun.paramTypes);
            Object[] args = params.toArray();              // 要声明为数组
            Object res = method.invoke(null, args);    // 支持函数的方法都要求用统一的工具类静态方法封装
            return res;
        } catch (Exception e) {
            throw new FunctionInvokeException("函数调用出错！", e);
        }
    }

}
