package cn.zxf.conductor.core.exception;

/**
 * 编排异常
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
public class ConductorException extends RuntimeException {

    public ConductorException(String message) {
        super(message);
    }

    public ConductorException(String message, Throwable cause) {
        super(message, cause);
    }

}
