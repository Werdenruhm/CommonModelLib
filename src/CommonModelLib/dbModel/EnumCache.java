package CommonModelLib.dbModel;

import CommonLib.Common;
import DBmethodsLib.DBNull;
import DBmethodsLib.DBmethodsCommon;
import DBmethodsLib.DataRow;
import DBmethodsLib.DataTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import org.json.JSONObject;

/**
 *
 * 
 */
public class EnumCache implements IEnumCache {
    static String getEnumExtPFK(String tableName)
    {
        if (tableName.toLowerCase().startsWith("en_"))
            tableName = tableName.substring(3);
        return tableName + "_en";            
    }
    DBmethodsCommon dbmethods;

    static class AnyEnumTable<T extends AbstractEnumData> extends AnyDictionaryTable {
        final String tableName;
        final Class enumDataExtClass;
        AnyEnumTable(DBmethodsCommon dbmethods, String tableAlias, String tableName) { 
            this(dbmethods, tableAlias, tableName, null);
        }
        AnyEnumTable(DBmethodsCommon dbmethods, String tableAlias, String tableName, T enumDataExt) { 
            super(dbmethods, "select "
                    + (tableName == null ? "e.*" : "e.enum_id, e.enum_en, e.alias, e.orderBy, e.code, e.name, e.comment, e.extKeyA, e.extKeyD")
                    + ", coalesce(e.isdeleted,false) as isdeleted_notnull"
                    + (tableName == null ? ", null as extJson" : ", row_to_json(e_ext.*) as extJson, null as extJsonO, e_ext.*")
                    + (enumDataExt == null ? "" : ", " + enumDataExt.getExtSelectSql())
                    + " from enum as e "
                    + (tableName == null ? "" : "left join " + tableName + " as e_ext on e.enum_id = e_ext." + getEnumExtPFK(tableName))
                    + " where e.enum_en =" + IEnumCache.enum_idSQL(tableAlias), new String[] {"code", "alias", "enum_id"}); 
            this.tableName = tableName;
            if (enumDataExt != null)
                this.enumDataExtClass = enumDataExt.getClass();
            else
                this.enumDataExtClass = null;
            this.onAsyncRefreshed = (dt) -> { errorChkd = false; };
        }
        private boolean errorChkd;
        @Override
        public DataTable get()
        {
            DataTable result = super.get();
            if (tableName != null && !errorChkd)
            {
                for(DataRow r : result.Rows)
                {
                    if (r.get(getEnumExtPFK(tableName)) == DBNull.Value)
                        throw new Error("Incorrect enum " + tableName + " filling: enum row with enum_id = " + r.get("enum_id") + (r.get("alias") == DBNull.Value ? "" : " (alias=" + r.get("alias") + ")") + " does not have coresponding row in " + tableName);
                }
                errorChkd = true;
            }            
            return result;
        }
    }
    final private HashMap<String, AnyEnumTable> anyEnumTables = new HashMap<>();
    final private Object anyDictionaryTablesLOCK = new Object();
    final private AnyDictionaryTable enums;
    public EnumCache(DBmethodsCommon dbmethods)
    {
        this.dbmethods = dbmethods;
        enums = new AnyDictionaryTable(dbmethods, "select enum.*, (select pg_tables.tablename from pg_tables where pg_tables.tablename = lower(enum.alias)) as tableName from enum where enum_en is null", new String[] {"alias"}, (newDt) -> {
            synchronized(anyDictionaryTablesLOCK)
            {
                for(String k : Common.collectionToArray(anyEnumTables.keySet(), String[].class))
                {
                    DataRow[] rws = newDt.Select(new DataTable.SelectFilter("alias", DataTable.SelectFilterType.EqualsCS, k));
                    if (rws.length == 0 || Objects.equals(anyEnumTables.get(k).tableName, rws[0].getNoDBNull("tableName", String.class)))
                        anyEnumTables.remove(k);
                }
            }
        });
    }
    protected AnyDictionaryTable getEnumByAlias(String tableAlias) {
        return getEnumByAlias(tableAlias, null);
    }
     
    protected <T extends AbstractEnumData> AnyDictionaryTable getEnumByAlias(String tableAlias, T enumDataExt) {
        tableAlias = Objects.requireNonNull(tableAlias);
        if (!anyEnumTables.containsKey(tableAlias) 
            || 
            (enumDataExt != null && enumDataExt.getClass() != anyEnumTables.get(tableAlias).enumDataExtClass))
        {
            synchronized (anyDictionaryTablesLOCK)
            {
                if (!anyEnumTables.containsKey(tableAlias) 
                    || 
                    (enumDataExt != null && enumDataExt.getClass() != anyEnumTables.get(tableAlias).enumDataExtClass))
                {
                    DataRow[] rws = enums.get().Select(new DataTable.SelectFilter("alias", DataTable.SelectFilterType.EqualsCS, tableAlias));
                    if (rws.length == 0) throw new RuntimeException("MUSTNEVERTHROW: tableAlias '" + tableAlias + "' not found!");
                    anyEnumTables.put(
                        tableAlias,
                        new AnyEnumTable<>(dbmethods, tableAlias, rws[0].getNoDBNull("tableName", String.class), enumDataExt)
                    );
                }
            }
        }
        return anyEnumTables.get(tableAlias);
    }

    @Override
    public AbstractEnumData.CommonEnumData getEnumData(String alias) throws IllegalArgumentException 
    {
        return getEnumData(alias, AbstractEnumData.CommonEnumData.class);
    }
    public ArrayList<AbstractEnumData.CommonEnumData> getEnumData(String tableAlias, DataTable.SelectFilter filter) throws IllegalArgumentException 
    {
        return getEnumData(tableAlias, filter, AbstractEnumData.CommonEnumData.class);
    }
    @Override
    public <T extends AbstractEnumData> T getEnumData(String alias, Class<T> enumDataExtClass) throws IllegalArgumentException 
    {
        String tableAlias = alias.replaceAll("\\..+","");
        ArrayList<T> result = getEnumData(tableAlias, new DataTable.SelectFilter("alias", DataTable.SelectFilterType.EqualsCS, alias), enumDataExtClass);
        if (result.isEmpty())
            throw new IllegalArgumentException("MUSTNEVERTHROW: alias '" + alias + "' not found!");
        if (result.size() > 1)
            throw new IllegalArgumentException("MUSTNEVERTHROW: alias '" + alias + "' found " + result.size() + " times!");
        return result.get(0);
    }
    public <T extends AbstractEnumData> ArrayList<T> getEnumData(String tableAlias, DataTable.SelectFilter filter, Class<T> enumDataExtClass) throws IllegalArgumentException 
    {
        T tmpel;
        try {
            tmpel = enumDataExtClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("descendants of AbstractEnumData must support paramless constructor! Error getting new new instance of " + enumDataExtClass.getCanonicalName() + ":" + ex.toString());
        }
        ArrayList<T> result = new ArrayList<>();
        for(DataRow r : getEnumByAlias(tableAlias, (tmpel instanceof AbstractEnumData.CommonEnumData ? null : tmpel)).get().Select(filter))
        {
            T el;
            try {
                el = enumDataExtClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            el.enum_id = (Integer)r.get("enum_id");
            el.alias = (String)r.get("alias");
            el.code = r.getNoDBNull("code", String.class);
            el.isDeleted = (Boolean)r.get("isdeleted_notnull");
            if (r.get("extJson") != DBNull.Value)
            {
                if (r.get("extJsonO") == DBNull.Value)
                    r.put("extJsonO", new JSONObject(r.get("extJson").toString()));
                el.ext = (JSONObject)r.get("extJsonO");
            }
            el.fillExt(r);
            result.add(el);
        }
        return result;
    }

    @Override
    public int enum_id(String alias) throws IllegalArgumentException {
        String tableAlias = alias.replaceAll("\\..+","");
        DataRow[] rws = getEnumByAlias(tableAlias).get().Select(new DataTable.SelectFilter("alias", DataTable.SelectFilterType.EqualsCS, alias));
        if (rws.length == 0) throw new IllegalArgumentException("MUSTNEVERTHROW: alias '" + alias + "' not found!");
        return rws[0].getNoDBNull("enum_id", Integer.class); 
    }
    @Override
    public String getAliasByIdInEnum(String tableAlias, int enum_id) {
        DataRow[] rws = getEnumByAlias(tableAlias).get().Select(new DataTable.SelectFilter("enum_id", DataTable.SelectFilterType.EqualsCS, enum_id));
        if (rws.length == 0) throw new IllegalArgumentException("MUSTNEVERTHROW: enum_id='" + enum_id + "' not found in table " + tableAlias + "!");
        return rws[0].getNoDBNull("alias", String.class); 
    }

}
