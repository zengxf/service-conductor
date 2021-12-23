package cn.zxf.conductor.core.threadpool;

import cn.zxf.conductor.core.entity.Flow;
import cn.zxf.conductor.core.entity.FlowExecuteResult;
import cn.zxf.conductor.core.FlowExecutor;
import cn.zxf.conductor.core.config.ThreadPoolConfig;
import cn.zxf.conductor.core.constant.ConductorConstant;
import cn.zxf.conductor.core.utils.SpringUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 流程执行线程池
 * <br/>
 * 进程退出时，用 shutdown() 还是 shutdownNow()，以后再确定
 * <br/>
 * Created by ZXFeng on 2021/9/13.
 */
@Slf4j
public class FlowThreadPool {

    private static final Object lock = new Object();
    private static final long TIME_WINDOW = 60_000; // 时间窗口，一分钟
    private static MyThreadPool pool;

    /*** 提交任务执行 */
    public static void execute(FlowExecutor engine, Flow flow) {
        ofPool().execute(ExeFlowTask.of(engine, flow));
    }

    /*** 提交任务执行 */
    public static Future<FlowExecuteResult> submit(FlowExecutor engine, Flow flow) {
        ExeFlowFuture future = ExeFlowFuture.of(engine, flow);
        ofPool().execute(future);
        return future;
    }

    // 组装线程池
    private static MyThreadPool ofPool() {
        if (pool != null)
            return pool;
        ThreadPoolConfig poolConfig = SpringUtils.getBean(ThreadPoolConfig.class);
        ThreadPoolConfig.Item config = poolConfig.getFlow();
        if (config == null) {
            config = poolConfig.getDefault();
            config.setNamePrefix(ConductorConstant.ThreadPoolConst.RULE_PREFIX);
        }
        synchronized (lock) {
            if (pool != null)
                return pool;
            pool = ThreadPoolFactory.ofFlow(config);
        }
        return pool;
    }

    /*** Future 任务封装 */
    public static class ExeFlowFuture extends FutureTask<FlowExecuteResult> implements Comparable<ExeFlowFuture> {
        private ExeFlowTask task;

        public ExeFlowFuture(ExeFlowTask task) {
            super(() -> task.execute());
            this.task = task;
        }

        public static ExeFlowFuture of(FlowExecutor engine, Flow flow) {
            ExeFlowTask task = ExeFlowTask.of(engine, flow);
            ExeFlowFuture self = new ExeFlowFuture(task);
            return self;
        }

        @Override
        public int compareTo(ExeFlowFuture obj) {
            return this.task.compareTo(obj.task);
        }
    }

    /*** 任务封装 */
    @Data
    @Accessors(chain = true)
    public static class ExeFlowTask implements Runnable, Comparable<ExeFlowTask> {
        private long timestamp;
        private int priority;
        private FlowExecutor engine;
        private Flow flow; // 以后优化成 flowId，方便 GC

        @Override
        public void run() {
            this.execute();
        }

        public FlowExecuteResult execute() {
            return engine.execute(flow);
        }

        public static ExeFlowTask of(FlowExecutor engine, Flow flow) {
            return new ExeFlowTask()
                    .setEngine(engine)
                    .setFlow(flow)
                    .setTimestamp(System.currentTimeMillis())
                    .setPriority(flow.priority());
        }

        /*** 对编排流程进行优先级排序，一段时间窗口内的任务 priority 越小（优先级则越高），越早执行 */
        @Override
        public int compareTo(ExeFlowTask t2) {
            ExeFlowTask t1 = this;
            long tms1 = t1.timestamp / TIME_WINDOW;
            long tms2 = t2.timestamp / TIME_WINDOW;
            if (tms1 > tms2)
                return 1;
            if (tms1 < tms2)
                return -1;
            return t1.priority - t2.priority;
        }
    }

}
