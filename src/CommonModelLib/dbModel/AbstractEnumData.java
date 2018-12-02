package CommonModelLib.dbModel;

import DBmethodsLib.DataRow;
import org.json.JSONObject;

/**
 * WARNING: descendants of AbstractEnumData must support paramless constructor!
 * 
 */
public abstract class AbstractEnumData {
    public int enum_id; public String alias; public String code; public boolean isDeleted;
    public JSONObject ext;
    public abstract String getExtSelectSql();
    public abstract void fillExt(DataRow r);
    
    public final static class CommonEnumData extends AbstractEnumData {
        @Override
        public String getExtSelectSql() { return ""; }
        @Override
        public void fillExt(DataRow r) { } 
    }
}
