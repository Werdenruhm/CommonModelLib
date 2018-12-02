package CommonModelLib.dbModel;

import DBmethodsLib.DBmethodsCommon;
import CommonLib.Common;
import DBmethodsLib.DataTable;

/**
 *
 * 
 */
public class AnyDictionaryTable extends DBmethodsLib.AnyDictionaryTable
{        
    private static final String dbcSqlPg = "select JavaHandlerControlVar_getCACHERESET()";
    public AnyDictionaryTable(DBmethodsCommon dbmethods, String sqlexpr) { super(dbmethods, sqlexpr, dbcSqlPg); }
    public AnyDictionaryTable(DBmethodsCommon dbmethods, String sqlexpr, String[] indexes) { super(dbmethods, sqlexpr, indexes, dbcSqlPg); }
    public AnyDictionaryTable(DBmethodsCommon dbmethods, String sqlexpr, String[] indexes, Common.Action1<DataTable> onAsyncRefreshed) { super(dbmethods, sqlexpr, indexes, onAsyncRefreshed, dbcSqlPg); }
}