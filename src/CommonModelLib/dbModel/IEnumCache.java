/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CommonModelLib.dbModel;

/**
 *
 * 
 */
public interface IEnumCache {
    /**
     * getEnumData Get enum element by alias (incl. rows with isdeleted=true)
     * @param alias
     * @return
     * @throws java.lang.IllegalArgumentException - if alias not found
     */
    public AbstractEnumData.CommonEnumData getEnumData(String alias) throws IllegalArgumentException;
    /**
     * getEnumData Get extended enum element by alias (incl. rows with isdeleted=true)
     * @param <T>
     * @param alias
     * @param enumDataExtClass descendant of AbstractEnumData. Its instance will be returned. WARNING: descendants of AbstractEnumData must support paramless constructor!
     * @return
     * @throws java.lang.IllegalArgumentException - if alias not found
     */
    public <T extends AbstractEnumData> T getEnumData(String alias, Class<T> enumDataExtClass) throws IllegalArgumentException;
    
    /**
     * enum_id Get enum_id by alias (incl. if isdeleted=true)
     * @param alias
     * @return
     * @throws java.lang.IllegalArgumentException - if alias not found
     */    
    public int enum_id(String alias) throws IllegalArgumentException;
    public String getAliasByIdInEnum(String tableAlias, int enum_id);
     
    public static String enum_idSQL(String alias)
    {
        return " enum_id('" + alias.replace("'", "''") + "')";
    }
}
