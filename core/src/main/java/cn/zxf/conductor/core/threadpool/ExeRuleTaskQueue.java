/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.zxf.conductor.core.threadpool;

import cn.zxf.conductor.core.utils.AssertUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

/**
 * 执行流程任务队列
 * <br/>
 * Created by ZXFeng on 2021/11/23.
 */
@Slf4j
public class ExeFlowTaskQueue extends PriorityBlockingQueue<Runnable> implements IoQueue {

    @Setter
    private IoThreadPool parent;
    private final int capacity;
    private final String rejectedInfo;

    public ExeFlowTaskQueue(int capacity, String rejectedInfo) {
        super(capacity);
        this.capacity = capacity;
        this.rejectedInfo = rejectedInfo;
    }

    // 先扩展线程池，后入队列
    @Override
    public boolean offer(Runnable runnable) {
        log.info("流程任务入队！");
        AssertUtils.requireNotNull(parent, "没有设置目标线程池！");
        int size = super.size();
        log.info("线程数状况，created: [{}], max: [{}], queue-size: [{}]",
                parent.getPoolSize(), parent.getMaxPoolSize(), size);
        // we are maxed out on threads, simply queue the object
        if (parent.getPoolSize() == parent.getMaxPoolSize())
            return super.offer(runnable);
        // if we have less threads than maximum force creation of a new thread
        if (parent.getPoolSize() < parent.getMaxPoolSize())
            return false; // 让线程池创建线程
        if (size >= this.capacity) {
            log.error("流程任务队列已满，拒绝提交！queue-size: [{}]", size);
            throw new RejectedExecutionException(rejectedInfo);
        }
        // if we reached here, we need to add it to the queue
        return super.offer(runnable);
    }

}
