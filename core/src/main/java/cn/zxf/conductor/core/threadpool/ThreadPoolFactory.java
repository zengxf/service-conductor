package cn.zxf.conductor.core.threadpool;

import cn.hutool.core.util.StrUtil;
import cn.zxf.conductor.core.config.ThreadPoolConfig;
import cn.zxf.conductor.core.utils.ConductorConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂类
 * <br/>
 * Created by ZXFeng on  2021/11/26.
 */
@Slf4j
public class ThreadPoolFactory implements ConductorConstant.ThreadPoolConst {

    /*** 根据配置实例化 IO 型线程池 */
    public static IoThreadPool ofIo(ThreadPoolConfig.Item config) {
        IoThreadPool pool = ofIo(false, config);
        return pool;
    }

    /*** 根据配置实例化流程线程池 */
    public static IoThreadPool ofFlow(ThreadPoolConfig.Item config) {
        IoThreadPool pool = ofIo(true, config);
        return pool;
    }

    /*** 根据配置实例化 CPU 型线程池 */
    public static CpuThreadPool ofCpu(
            ThreadPoolConfig.Item config
    ) {
        String rejectInfo = Optional.ofNullable(config.getRejectInfo()).orElse(REJECT_INFO);
        Integer queueSize = Optional.ofNullable(config.getQueueSize()).orElse(QUEUE_SIZE);
        BlockingQueue<Runnable> workQueue;
        if (queueSize < 100) {
            workQueue = new ArrayBlockingQueue<>(queueSize);
        } else {
            workQueue = new LinkedBlockingQueue<>(queueSize);
        }
        ThreadPoolExecutor target = buildThreadPoolTarget(config, workQueue, rejectInfo);
        CpuThreadPool pool = new CpuThreadPool();
        pool.setParent(target);
        return pool;
    }

    // 根据配置实例化 IO 型线程池
    private static IoThreadPool ofIo(boolean isFlow, ThreadPoolConfig.Item config) {
        log.info("实例化线程池配置: isFlow: [{}], config: [{}]", isFlow, config);
        Integer queueSize = Optional.ofNullable(config.getQueueSize()).orElse(QUEUE_SIZE);
        String rejectInfo;
        IoQueue workQueue;
        if (isFlow) {
            rejectInfo = Optional.ofNullable(config.getRejectInfo()).orElse(RULE_REJECT_INFO);
            workQueue = new ExeFlowTaskQueue(queueSize, rejectInfo);
        } else {
            rejectInfo = Optional.ofNullable(config.getRejectInfo()).orElse(REJECT_INFO);
            workQueue = new IoTaskQueue(queueSize);
        }
        IoThreadPool pool = ofIo(config, workQueue, rejectInfo);
        workQueue.setParent(pool);
        return pool;
    }

    // 实例化 IO 型线程池
    private static IoThreadPool ofIo(
            ThreadPoolConfig.Item config, BlockingQueue<Runnable> workQueue, String rejectInfo
    ) {
        ThreadPoolExecutor target = buildThreadPoolTarget(config, workQueue, rejectInfo);
        IoThreadPool pool = new IoThreadPool();
        pool.setParent(target);
        return pool;
    }

    // 构建真正的线程池 Target
    private static ThreadPoolExecutor buildThreadPoolTarget(
            ThreadPoolConfig.Item config, BlockingQueue<Runnable> workQueue, String rejectInfo
    ) {
        AtomicInteger poolNumber = new AtomicInteger(1);
        String prefix = Optional.ofNullable(config.getNamePrefix()).orElse(TASK_PREFIX);
        ThreadFactory threadFactory = (r) -> {
            String name = prefix + poolNumber.getAndIncrement();
            boolean daemon = Optional.ofNullable(config.getThreadDaemon()).orElse(THREAD_DAEMON);
            int priority = Optional.ofNullable(config.getThreadPriority()).orElse(THREAD_PRIORITY);
            Thread thread = new Thread(r, name);
            thread.setDaemon(daemon);
            thread.setPriority(priority);
            return thread;
        };
        RejectedExecutionHandler rejectedHandler = (task, executor) -> {
            throw new RejectedExecutionException(StrUtil.format("[{}] {}", prefix, rejectInfo));
        };
        Integer coreSize = Optional.ofNullable(config.getCoreSize()).orElse(CORE_SIZE);
        Integer maxSize = Optional.ofNullable(config.getMaxSize()).orElse(MAX_SIZE);
        Integer keepAliveMinutes = Optional.ofNullable(config.getKeepAliveMinutes()).orElse(KEEP_ALIVE_MINUTES);
        ThreadPoolExecutor target = new ThreadPoolExecutor(
                coreSize, maxSize, keepAliveMinutes, TimeUnit.MINUTES,
                workQueue, threadFactory, rejectedHandler
        );
        return target;
    }

}
