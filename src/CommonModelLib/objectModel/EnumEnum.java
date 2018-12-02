package CommonModelLib.objectModel;

import CommonModelLib.dbModel.AbstractEnumData;
import CommonModelLib.dbModel.IEnumCache;

/**
 *
 * 
 */
public interface EnumEnum {
    String getAlias();
    default String getAliasSql() { return IEnumCache.enum_idSQL(getAlias());}
    default <T> T checkAndGet(Integer idToCheck, T toGet ) { 
        if (idToCheck == null) 
            throw new NullPointerException("Enum was not checked and inited");
        return toGet;
    }
    int getId();
    String getCode();
    void fillByDB(int id, String code);
    public static <T extends EnumEnum> void checkAndFillByDB(IEnumCache enumCache, Class<T> enumClass)
    {
        T[] ees = enumClass.getEnumConstants();
        String errMsg = null;
        for (T ee : ees)
        {
            try
            {
                AbstractEnumData.CommonEnumData ed = enumCache.getEnumData(ee.getAlias());
                boolean isDepr = ee.getClass().isAnnotationPresent(Deprecated.class);
                if (isDepr != ed.isDeleted)
                    throw new RuntimeException("enum element state missmatch: in DB isDeleted=" + ed.isDeleted + ", but " + (isDepr?"":"no") + " @Deprecated annotation found!");
                ee.fillByDB(ed.enum_id, ed.code);
            }
            catch (Exception ex)
            {
                errMsg = (errMsg == null ? "" : errMsg + "\r\n") + "Enum element " + ee.getClass().getCanonicalName() + " check by DB exception: " + ex.getMessage();
            }
        }
        if (errMsg != null)
            throw new RuntimeException("Enum " + enumClass.getCanonicalName() + " check and init error: " + errMsg);
    }
    public static <T extends EnumEnum> T getById(int idValue, Class<T> enumClass)
    {
        T[] ees = enumClass.getEnumConstants();
        for (T ee : ees)
        {
            if (idValue == ee.getId())
                return ee;
        }
        throw new RuntimeException("Enum " + enumClass.getCanonicalName()  + " does not contains element with enum_id == " + idValue + "!");
    }
    public static <T extends EnumEnum> T getByAlias(String alias, Class<T> enumClass)
    {
        T[] ees = enumClass.getEnumConstants();
        for (T ee : ees)
        {
            if (ee.getAlias().equals(alias) && ee.getId()>0)
                return ee;
        }
        throw new RuntimeException("Enum " + enumClass.getCanonicalName()  + " does not contains element with alias == " + alias + "!");
    }
}
