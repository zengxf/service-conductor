package cn.zxf.conductor.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
@Slf4j
@DisplayName("表达式解析测试")
public class Jexl3UtilsTest extends Jexl3Utils {

    @Test
    @DisplayName("测试数组越界")
    public void testIndexOver() {
        Map<String, Object> context = Map.of(
                "$root", Map.of(
                        "arr", new int[]{1, 2, 3},
                        "list", List.of("aa", "bb", "cc")
                )
        );
        String exp;

        exp = "$root.arr[1]";
        log.info("[{}] => [{}]\n", exp, execute(exp, context));

        exp = "$root.list[1]";
        log.info("[{}] => [{}]\n", exp, execute(exp, context));

        exp = "$root.arr[5]";   // 会报 ArrayIndexOutOfBoundsException
        log.info("[{}] => [{}]\n", exp, execute(exp, context));

        exp = "$root.list[5]";  // 会报 IndexOutOfBoundsException
        log.info("[{}] => [{}]\n", exp, execute(exp, context));
    }

}
