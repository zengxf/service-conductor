package cn.zxf.conductor.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.*;
import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * JEXL3 表达式工具类的封装
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
@Slf4j
public class Jexl3Utils {

    /*** 执行：解析表达式 */
    @Nullable
    public static Object execute(String expression, Map<String, ?> context) {
        if (StringUtils.isEmpty(expression))
            return null;
        Exception ex;
        try {
            JexlEngine jexl = new Engine();
            JexlExpression exp = jexl.createExpression(expression);  // 创建一个表达式
            JexlContext jc = new MapContext();  // 创建上下文并添加数据
            context.forEach(jc::set);
            Object value = exp.evaluate(jc);
            return value;
        } catch (JexlException jexlEx) {
            Throwable th = jexlEx.getCause();
            if (th instanceof IndexOutOfBoundsException) { // 单独处理数组越界
                log.warn("数组越界！expression: [{}].", expression);
                return null; // 容错，返回 null
            }
            ex = jexlEx;
        } catch (Exception e) {
            ex = e;
        }
        throw new RuntimeException("解析表达式出错！expression: [" + expression + "].", ex);
    }

}
