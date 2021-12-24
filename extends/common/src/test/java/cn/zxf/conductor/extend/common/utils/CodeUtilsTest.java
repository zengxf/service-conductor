package cn.zxf.conductor.extend.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static cn.zxf.conductor.extend.common.utils.CodeUtils.decodeBase64;
import static cn.zxf.conductor.extend.common.utils.CodeUtils.encodeBase64;
import static cn.zxf.conductor.extend.common.utils.DigestUtils.md5;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <br/>
 * Created by ZXFeng on 2021/12/24.
 */
@Slf4j
@DisplayName("编码帮助类测试")
public class CodeUtilsTest {

    @Test
    @DisplayName("测试 Base64 编码")
    public void testEncodeBase64() {
        String str;

        str = "";
        log.info("base64('{}') => [{}]", str, encodeBase64(str));

        str = "123";
        log.info("base64('{}') => [{}]", str, encodeBase64(str));
    }

    @Test
    @DisplayName("测试 Base64 解码")
    public void testDecodeBase64() {
        String str;

        str = "";
        log.info("debase64('{}') => [{}]", str, decodeBase64(str));

        str = "MTIz";
        log.info("debase64('{}') => [{}]", str, decodeBase64(str));
    }

    @Test
    @DisplayName("测试反射调用")
    public void testByReflect() throws Exception {
        String str = "123";
        Method method = CodeUtils.class.getMethod("encodeBase64", String.class);
        Object res = method.invoke(null, str);
        log.info("base64('{}') => [{}]", str, res);
    }

}