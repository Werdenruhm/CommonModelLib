package CommonModelLib.objectModel.tasking;

import org.json.JSONObject;

/**
 *
 * 
 */
public class TaskInfo {
    public final JSONObject taskRow;
    public TaskInfo(JSONObject taskRow)
    {
        this.taskRow = taskRow;
    }
    public long task_id;
    public Long task_pid;
    public int type_en;
    public String type_alias;
    public Integer queue; public boolean isQueue() { return queue != null; } 
    public JSONObject param_value;
    public static final JSONObject forCheck = new JSONObject();
}
