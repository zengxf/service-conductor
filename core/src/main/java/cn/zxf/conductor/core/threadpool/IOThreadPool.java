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

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * IO 型任务线程池
 * <br/>
 * 参考 Tomcat 的 ThreadPoolExecutor
 * <br/>
 * Created by ZXFeng on 2021/11/23.
 */
@Slf4j
public class IoThreadPool implements MyThreadPool {

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

    // ----------

    public int getPoolSize() {
        return parent.getPoolSize();
    }

    public int getMaxPoolSize() {
        return parent.getMaximumPoolSize();
    }

}
