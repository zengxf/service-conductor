package cn.zxf.conductor.extend.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 编码帮助类
 * <br/>
 * Created by ZXFeng on 2021/12/24.
 */
public class CodeUtils {

    /*** Base64 编码 */
    public static String encodeBase64(String str) {
        if (StringUtils.isEmpty(str))
            return StrUtil.EMPTY;
        String encode = Base64.encode(str);
        return encode;
    }

    /*** Base64 解码 */
    public static String decodeBase64(String base64Str) {
        if (StringUtils.isEmpty(base64Str))
            return StrUtil.EMPTY;
        String str = Base64.decodeStr(base64Str);
        return str;
    }

}
