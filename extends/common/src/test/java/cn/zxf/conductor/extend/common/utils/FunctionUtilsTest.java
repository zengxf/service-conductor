package cn.zxf.conductor.extend.common.utils;

import cn.zxf.conductor.extend.common.enums.FunctionEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * <br/>
 * Created by ZXFeng on 2021/12/24.
 */
@Slf4j
@DisplayName("函数调用帮助类测试")
public class FunctionUtilsTest {

    @Test
    @DisplayName("测试函数调用")
    public void testInvoke() {
        String funName = FunctionEnum.BASE64.funName;
        String str = "123";
        Object res = FunctionUtils.invoke(funName, str, null);
        log.info("base64('{}') => [{}]", str, res);
    }

}