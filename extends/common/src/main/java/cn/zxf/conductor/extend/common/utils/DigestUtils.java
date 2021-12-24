package cn.zxf.conductor.extend.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import org.apache.commons.lang3.StringUtils;

/**
 * 摘要帮助类
 * <br/>
 * Created by ZXFeng on 2021/12/24.
 */
public class DigestUtils {

    /*** MD5 摘要 */
    public static String md5(String str) {
        if (StringUtils.isEmpty(str))
            return StrUtil.EMPTY;
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String digestHex = md5.digestHex(str);
        return digestHex;
    }


}
