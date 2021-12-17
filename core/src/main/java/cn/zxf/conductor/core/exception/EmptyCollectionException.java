package cn.zxf.conductor.core.exception;

/**
 * 空集合异常
 * <br/>
 * Created by ZXFeng on 2021/9/23.
 */
public class EmptyCollectionException extends ConductorException {
	private static final long serialVersionUID = 1L;

	public EmptyCollectionException(String message) {
        super(message);
    }

}
