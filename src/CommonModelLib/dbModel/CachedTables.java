package CommonModelLib.dbModel;

import CommonLib.Common;
import java.util.HashMap;

/**
 *
 * 
 */
public class CachedTables {

    private final static HashMap<String, AnyDictionaryTable> name = new HashMap<>();

    public static boolean isCached(String sqlExpr) {
        return (name.containsKey(sqlExpr));
    }
    public static AnyDictionaryTable cache(String sqlExpr, Common.Func<AnyDictionaryTable> anyDictionaryTable) 
    {
        if (!isCached(sqlExpr))
        {
            synchronized(name)
            {
                if (!isCached(sqlExpr))
                {
                    name.put(sqlExpr, anyDictionaryTable.call());
                }
            }
        }
        return name.get(sqlExpr);
   }

    public static AnyDictionaryTable cache(String sqlExpr) 
    {
        return cache(sqlExpr, () -> new AnyDictionaryTable(Connection.getReadOnlyMasterDb(), sqlExpr));         
    }


}
