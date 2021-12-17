package cn.zxf.conductor.core.threadpool;

import cn.zxf.conductor.core.ExecuteContext;
import cn.zxf.conductor.core.entity.Task;
import cn.zxf.conductor.core.entity.TaskExecuteResult;
import cn.zxf.conductor.core.TaskExecutor;
import cn.zxf.conductor.core.config.ThreadPoolConfig;
import cn.zxf.conductor.core.utils.AssertUtils;
import cn.zxf.conductor.core.utils.ConductorConstant;
import cn.zxf.conductor.core.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 任务执行线程池
 * <br/>
 * 进程退出时，用 shutdown() 还是 shutdownNow()，以后再确定
 * <br/>
 * Created by ZXFeng on 2021/9/13.
 */
@Slf4j
public class TaskThreadPool implements ConductorConstant.ThreadPoolConst {

    private static ThreadLocal<MyThreadPool> localPool = new ThreadLocal<>();
    private static final Object lock = new Object();
    private static final Map<String, MyThreadPool> poolMap
            = new ConcurrentHashMap<>();

    /*** 提交任务执行（执行真正的操作），任务的超时时间外部控制 */
    public static <R extends TaskExecuteResult> Future<R> doSubmit(Callable<R> callable) {
        MyThreadPool pool = localPool.get();
        AssertUtils.requireNotNull(pool, "请先使用 submit() 方法提交任务！");
        Future<R> future = pool.submit(callable);
        return future;
    }

    /*** 提交任务执行（执行任务的拦截器部分），任务的超时时间外部控制 */
    public static <T extends Task, R extends TaskExecuteResult> Future<R> submit(
            TaskExecutor<T, R> executor, T task, ExecuteContext context
    ) {
        MyThreadPool pool = pool(task);
        Future<R> future = pool.submit(() -> {
            localPool.set(pool);
            try {
                R result = executor.execute(task, context);
                return result;
            } finally {
                localPool.remove();
            }
        });
        return future;
    }

    // 根据任务获取线程池
    private static MyThreadPool pool(Task task) {
        String sign = task.threadSign();
        int type = task.type();
        ThreadPoolConfig poolConfig = SpringUtils.getBean(ThreadPoolConfig.class);
        String key;
        boolean isCpu = false;
        ThreadPoolConfig.Item config;
        if (StringUtils.equals(sign, CPU_KEY)) {
            key = CPU_KEY;
            isCpu = true;
            config = poolConfig.getCpu();
        } else {
            key = String.format("%s%d-%s", TYPE_PREFIX, type, sign);
            config = poolConfig.getTaskItem(type, sign);
        }
        if (config == null) { // 找不到独立配置，就用默认的，默认的只创建一个
            key = DEF_KEY;
            config = poolConfig.getDefault();
        }
        return ofPool(key, isCpu, config);
    }

    // 组装线程池
    private static MyThreadPool ofPool(String key, boolean isCpu, ThreadPoolConfig.Item config) {
        MyThreadPool pool = poolMap.get(key);
        if (pool != null)
            return pool;
        AssertUtils.requireNotNull(config, "配置不能为空！");
        synchronized (lock) {
            pool = poolMap.get(key);
            if (pool != null)
                return pool;
            if (isCpu)
                pool = ThreadPoolFactory.ofCpu(config);
            else
                pool = ThreadPoolFactory.ofIo(config);
            poolMap.put(key, pool);
        }
        return pool;
    }

}
