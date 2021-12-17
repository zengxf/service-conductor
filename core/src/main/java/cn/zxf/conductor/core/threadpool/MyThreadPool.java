package cn.zxf.conductor.core.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 自定义线程池接口
 * <br/>
 * Created by ZXFeng on  2021/11/26.
 */
public interface MyThreadPool {

    /*** 执行任务 */
    void execute(Runnable command);

    /*** 提交任务 */
    <V> Future<V> submit(Callable<V> callable);

}
