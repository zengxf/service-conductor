package cn.zxf.conductor.core.entity;

import cn.zxf.conductor.core.TaskExecuteInterceptor;
import cn.zxf.conductor.core.enums.TaskResultTypeEnum;
import lombok.Data;

/**
 * 任务执行结果
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
@Data
public abstract class TaskExecuteResult {

    /*** 结果类型，参考：{@link TaskResultTypeEnum} */
    protected Integer resultType;
    protected Exception exception;  // 异常时的异常
    protected Integer timeout;      // 超时时的超时时间设置 ms(毫秒)
    protected TaskExecuteInterceptor interceptor;  // 被拦截时的拦截器，方便跟踪

    // ------

    public <T extends TaskExecuteResult> T resultType(TaskResultTypeEnum typeEnum) {
        this.resultType = typeEnum.code;
        return (T) this;
    }

    public <T extends TaskExecuteResult> T interceptor(TaskExecuteInterceptor interceptor) {
        this.interceptor = interceptor;
        return (T) this;
    }

    public <T extends TaskExecuteResult> T exception(Exception exception) {
        this.exception = exception;
        return (T) this;
    }

    public <T extends TaskExecuteResult> T timeout(Integer timeout) {
        this.timeout = timeout;
        return (T) this;
    }

    // ------

    public boolean isNormal() {
        return TaskResultTypeEnum.NORMAL.code.equals(this.resultType);
    }

    public boolean isSuccess() {
        return this.isNormal() && this.hasSuccess();
    }

    protected abstract boolean hasSuccess();

    public boolean isTimeout() {
        return TaskResultTypeEnum.TIMEOUT.code.equals(this.resultType);
    }

    public boolean isNotNormal() {
        return !this.isNormal();
    }

}
