package cn.zxf.conductor.core.exception;

import cn.hutool.core.util.StrUtil;

/**
 * 参数校验异常
 * <br/>
 * Created by ZXFeng on 2021/9/24.
 */
public class ParamValidateException extends ConductorException {

	public ParamValidateException(String message) {
        super(message);
    }

    public ParamValidateException(String messageFormat, Object... objects) {
        this(StrUtil.format(messageFormat, objects));
    }

}
