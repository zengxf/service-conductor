package cn.zxf.conductor.core.utils;

/**
 * 编排相关的常量定义
 * <br/>
 * Created by ZXFeng on 2021/9/22.
 */
public interface ConductorConstant {

    /*** 默认任务超时时间 */
    Integer DEF_TASK_TIMEOUT_MS = 60000;
    /*** 默认 HTTP 连接超时时间 */
    Integer DEF_API_CONNECT_TIMEOUT_MS = 30000;
    /*** 空字符 */
    String EMPTY_STR = "";
    /*** 默认字符集 */
    String DEF_CHARSET = "UTF-8";

    /*** 解析数组定义的 item Key */
    String ITEM_KEY = "$item";
    /*** 全局 ID Key */
    String GLOBAL_ID_KEY = "$globalId";

    /*** 调试默认流程 ID */
    String DEF_DEBUG_RULE_ID = "debug-temp-flow";
    /*** 调试默认流程名称 */
    String DEF_DEBUG_RULE_NAME = "调试临时流程";

    /*** 左括号 */
    String LEFT_BRACKET = "(";
    /*** 右括号 */
    String RIGHT_BRACKET = ")";


    /*** 线程池常量 */
    interface ThreadPoolConst {
        String
                DEF_KEY = "def",
                CPU_KEY = "cpu",
                TYPE_PREFIX = "type-",
                TASK_PREFIX = "task-pool-thread-",
                RULE_PREFIX = "flow-pool-thread-",
                REJECT_INFO = "队列已满，拒绝执行任务！",
                RULE_REJECT_INFO = "流程任务队列已满，拒绝提交！";
        int
                CORE_SIZE = 10,
                MAX_SIZE = 20,
                QUEUE_SIZE = 200,
                KEEP_ALIVE_MINUTES = 10,
                THREAD_PRIORITY = Thread.NORM_PRIORITY;
        boolean
                THREAD_DAEMON = false;
    }

    /*** HTTP 连接池常量 */
    interface HttpPoolConst {
        int
                MAX_TOTAL = 100,
                MAX_PER_ROUTE = 20,
                TTL_MS = 60_000;
    }

}
