package cn.zxf.conductor.core.threadpool;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * CPU 型任务线程池
 * <br/>
 * Created by ZXFeng on  2021/12/9.
 */
@Slf4j
public class CpuThreadPool implements MyThreadPool{

    @Setter
    private ThreadPoolExecutor parent;

    @Override
    public void execute(Runnable command) {
        parent.execute(command);
    }

    @Override
    public <V> Future<V> submit(Callable<V> callable) {
        return parent.submit(callable);
    }

}
