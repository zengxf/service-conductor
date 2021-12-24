package cn.zxf.conductor.core.exception;

import cn.hutool.core.util.StrUtil;

/**
 * 枚举未找到异常
 * <br/>
 * Created by ZXFeng on 2021/12/24.
 */
public class EnumNotFoundException extends ConductorException {

    public EnumNotFoundException(String message, Object... params) {
        super(StrUtil.format(message, params));
    }

}
