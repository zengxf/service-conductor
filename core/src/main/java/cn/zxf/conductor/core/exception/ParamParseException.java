package cn.zxf.conductor.core.exception;

import cn.hutool.core.util.StrUtil;

/**
 * 参数解析异常
 * <br/>
 * Created by ZXFeng on 2021/9/23.
 */
public class ParamParseException extends ConductorException {

	public ParamParseException(String messageFmt, Object... params) {
        super(StrUtil.format(messageFmt, params));
    }

    public ParamParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
