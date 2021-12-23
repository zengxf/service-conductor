package cn.zxf.conductor.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * ID 帮助类
 * <br/>
 * Created by ZXFeng on 2021/12/23.
 */
@Slf4j
public class IdUtils {

    /*** 全局 ID 生成规则：毫秒级的时间戳 +3 位随机数 */
    public static String genGlobalId() {
        String id = String.format(
                "%ty%<tm-%<td%<tH-%<tM%<tS-%<tL-%d",
                System.currentTimeMillis(),
                new Random().nextInt(1000)
        );
        return id;
    }

}
