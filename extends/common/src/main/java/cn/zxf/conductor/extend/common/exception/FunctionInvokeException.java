package cn.zxf.conductor.extend.common.exception;

import cn.zxf.conductor.core.exception.ConductorException;

/**
 * 函数调用异常
 * <br/>
 * Created by ZXFeng on 2021/12/24.
 */
public class FunctionInvokeException extends ConductorException {

    public FunctionInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

}
