package cn.zxf.conductor.core;

import cn.zxf.conductor.core.constant.ConductorConstant;
import cn.zxf.conductor.core.entity.InputParam;
import cn.zxf.conductor.core.entity.Task;
import cn.zxf.conductor.core.entity.TaskExecuteResult;
import cn.zxf.conductor.core.utils.AssertUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 流程执行上下文
 * <br/>
 * Created by ZXFeng on 2021/12/17.
 */
@Data
@Accessors(chain = true)
public class ExecuteContext {

    protected String globalId;                  // 请求全局 ID
    protected Map<String, TaskObj> taskObjMap   // 任务数据保存
            = new LinkedHashMap<>();

    // ---------

    /*** 将任务解析后的入参及执行结果等相关数据填充到上下文 */
    public void setupTaskObj(Task task, TaskExecuteResult result) {
        String key = AssertUtils.requireNotBlank(task.contextSign(), "任务标识不能为空");
        TaskObj obj = new TaskObj();
        obj.input = task.inputParam();
        obj.result = result;
        taskObjMap.put(key, obj);
    }

    /*** 将上下文转换成 Map 供表达式解析引擎使用 */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.putAll(taskObjMap);
        map.put(ConductorConstant.GLOBAL_ID_KEY, globalId);
        return map;
    }

    // ---------

    /*** 任务相关数据的封装 */
    public static class TaskObj {
        public InputParam input;
        public TaskExecuteResult result;
    }

}
