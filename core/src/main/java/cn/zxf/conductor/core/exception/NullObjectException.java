package cn.zxf.conductor.core.exception;

/**
 * 空对象异常
 * <br/>
 * Created by ZXFeng on  2021/12/9.
 */
public class NullObjectException extends ConductorException {

    private static final long serialVersionUID = 1L;

    public NullObjectException(String message) {
        super(message);
    }

}