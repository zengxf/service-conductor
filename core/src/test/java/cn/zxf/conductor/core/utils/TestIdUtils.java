package cn.zxf.conductor.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
@Slf4j
@DisplayName("测试 ID 帮助类")
public class TestIdUtils {

    @Test
    @DisplayName("全局 ID")
    public void test() {
        String id = IdUtils.genGlobalId();
        log.info("GlobalId: [{}]", id);
    }

}
