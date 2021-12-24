package cn.zxf.conductor.extend.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cn.zxf.conductor.extend.common.utils.DigestUtils.md5;

/**
 * <br/>
 * Created by ZXFeng on 2021/12/24.
 */
@Slf4j
@DisplayName("摘要帮助类测试")
public class DigestUtilsTest {

    @Test
    @DisplayName("测试 MD5 摘要")
    public void testMd5() {
        String str;

        str = "";
        log.info("md5('{}') => [{}]", str, md5(str));

        str = "123";
        log.info("md5('{}') => [{}]", str, md5(str));
    }


}