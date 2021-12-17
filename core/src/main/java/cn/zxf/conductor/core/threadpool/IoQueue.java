package cn.zxf.conductor.core.threadpool;

import java.util.concurrent.BlockingQueue;

/**
 * IO 任务队列接口
 * <br/>
 * Created by ZXFeng on  2021/11/26.
 */
public interface IoQueue extends BlockingQueue<Runnable> {

    /*** 设置所属线程池 */
    void setParent(IoThreadPool parent);

}
