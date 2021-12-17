package cn.zxf.conductor.core.config;

import cn.zxf.conductor.core.utils.ConductorConstant;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * 线程池总配置类
 * <br/>
 * Created by ZXFeng on  2021/12/9.
 */
@Data
@Component
@ConfigurationProperties(prefix = "composer.engine.thread-pool")
public class ThreadPoolConfig {

    private Item def;   // 默认配置
    private Item cpu;   // CPU 型任务配置
    private Item flow;  // 流程的配置
    /*
    key: type-10    // "type-"是前缀，10 是任务类型
    value: configs  // 配置为 List，根据 sign 返回具体的配置
     */
    private Map<String, List<Item>> task; // 具体的配置

    /*** 获取默认配置 */
    @Nonnull
    public Item getDefault() {
        if (this.def != null)
            return this.def;
        return Inner.DEF_CFG;
    }

    /*** 根据任务类型和任务 sign 来获取配置 */
    @Nullable
    public Item getTaskItem(Integer taskType, String taskSign) {
        if (task == null)
            return null;
        List<Item> items = task.get(ConductorConstant.ThreadPoolConst.TYPE_PREFIX + taskType);
        if (items == null)
            return null;
        return items.stream()
                .filter(item -> StringUtils.equals(item.sign, taskSign))
                .findFirst()
                .orElse(null);
    }

    // -------------

    /*** 线程池单项配置 */
    @Data
    @Accessors(chain = true)
    public static class Item {
        private String sign;                // 标识
        private String namePrefix;          // 线程名称前缀
        private Integer coreSize;           // 核心线程数
        private Integer maxSize;            // 最大线程数
        private Integer queueSize;          // 队列长度
        private Integer keepAliveMinutes;   // 线程存活分钟数
        private String rejectInfo;          // 拒绝提示信息
        private Boolean threadDaemon;       // 线程守护标志
        private Integer threadPriority;     // 线程优先级
    }

    // 用于延迟初始化
    private static class Inner implements ConductorConstant.ThreadPoolConst {
        private final static Item DEF_CFG;

        static {
            DEF_CFG = new Item()
                    .setCoreSize(CORE_SIZE)
                    .setMaxSize(MAX_SIZE)
                    .setQueueSize(QUEUE_SIZE)
                    .setKeepAliveMinutes(KEEP_ALIVE_MINUTES)
                    .setNamePrefix(TASK_PREFIX)
                    .setRejectInfo(REJECT_INFO)
                    .setThreadDaemon(THREAD_DAEMON)
                    .setThreadPriority(THREAD_PRIORITY);
        }
    }

}
