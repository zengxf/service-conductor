package cn.zxf.conductor.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
@Slf4j
@DisplayName("测试断言")
class AssertUtilsTest {

    @Test
    @DisplayName("测试断言空对象")
    public void testObjNull() {
        Object obj = null;
        try {
            AssertUtils.requireNotNull(obj, "对象不能为空！");
        } catch (Exception e) {
            log.error("error!", e);
        }
    }

}