package CommonModelLib.dbModel;

import CommonLib.Common;
import DBmethodsLib.DBmethodsCommon;
import DBmethodsLib.DataRow;
import DBmethodsLib.DataTable;
import CommonModelLib.objectModel.Versioned;

/**
 *
 * 
 * @param <Tcls> JavaClass
 * @param <Tctorarg> JavaClass ctor single param
 */
public abstract class AnyDictionaryTableWithJavaClass<Tcls extends Versioned, Tctorarg> extends AnyDictionaryTable 
{
    public final String javaClassColName; final String javaClassVerColName; final String PKColName; 
    final JavaClassInstantiator<Tcls, Tctorarg> javaClassInstantiator; final Tctorarg ctorArgsForCheck;
    public AnyDictionaryTableWithJavaClass(DBmethodsCommon Dbmethods, String sqlexpr, String[] indexes, String javaClassColName, String javaClassVerColName, String PKColName, Class<Tcls> Tcls_class, Tctorarg ctorArgsForCheck) 
    {
        this(AnyDictionaryTableWithJavaClass::prefilledStaticErrHandler, Dbmethods, sqlexpr, indexes, javaClassColName, javaClassVerColName, PKColName, (clsName, ctor) -> Common.getAnyInstance(clsName, Tcls_class, ctor), ctorArgsForCheck);
    }
    public AnyDictionaryTableWithJavaClass(Common.Action1<Throwable> checkJavaClassesErrHandler, DBmethodsCommon Dbmethods, String sqlexpr, String[] indexes, String javaClassColName, String javaClassVerColName, String PKColName, Class<Tcls> Tcls_class, Tctorarg ctorArgsForCheck) 
    {
        this(checkJavaClassesErrHandler, Dbmethods, sqlexpr, indexes, javaClassColName, javaClassVerColName, PKColName, (clsName, ctor) -> Common.getAnyInstance(clsName, Tcls_class, ctor), ctorArgsForCheck);
    }
    public AnyDictionaryTableWithJavaClass(Common.Action1<Throwable> checkJavaClassesErrHandler, DBmethodsCommon Dbmethods, String sqlexpr, String[] indexes, String javaClassColName, String javaClassVerColName, String PKColName, JavaClassInstantiator<Tcls, Tctorarg> javaClassInstantiator, Tctorarg ctorArgsForCheck) 
    {
        super(Dbmethods, sqlexpr, indexes,
            (dt) -> {
                try
                {
                    checkJavaClasses(dt, javaClassColName, javaClassVerColName, PKColName, javaClassInstantiator, ctorArgsForCheck);
                }
                catch(Throwable th)
                {
                    checkJavaClassesErrHandler.call(th);
                }
            }                    
        );
        this.javaClassColName = javaClassColName;
        this.javaClassVerColName = javaClassVerColName;
        this.PKColName = PKColName;
        this.javaClassInstantiator = javaClassInstantiator;    
        this.ctorArgsForCheck = ctorArgsForCheck;            
    }        
    static <Tcls extends Versioned, Tctorarg> void checkJavaClasses(DataTable dt, String javaClassColName, String javaClassVerColName, String PKColName, JavaClassInstantiator<Tcls, Tctorarg> javaClassInstantiator, Tctorarg ctorArgsForCheck)
    {
        String errMsg = null;
        for(DataRow r : dt.Rows)
        {   
            try
            {
                String clNm = r.getNoDBNull(javaClassColName, String.class);
                if (Common.stringIsNullOrEmpty(clNm))
                    throw new NullPointerException("'" + javaClassColName + "' field must have value!");
                Tcls inst = javaClassInstantiator.call(clNm, ctorArgsForCheck);
                if (inst.getVersion() != r.getNoDBNull(javaClassVerColName, Integer.class))
                     throw new RuntimeException("version not match: expected (in DB) [" + r.get(javaClassVerColName).toString() + "], found [" + inst.getVersion() + "]");
            }
            catch(Exception ex)
            {
                errMsg = (errMsg == null ? "" : errMsg + ", ")
                    + "Exception checking class '" + r.get(javaClassColName).toString() + "'  (" + PKColName + " = " + r.get(PKColName).toString() + "):" + ex.toString();
            }
        }
        if (errMsg != null)
            throw new RuntimeException(errMsg);                        
    }
    public void checkJavaClasses()
    {
        checkJavaClasses(get(), javaClassColName, javaClassVerColName, PKColName, javaClassInstantiator, ctorArgsForCheck);
    }
    public Tcls newInstance(DataRow r, Tctorarg arg) throws ReflectiveOperationException
    {
        return javaClassInstantiator.call(r.getNoDBNull(javaClassColName, String.class), arg);
    }
    public Tcls newCheckInstance(DataRow r) throws ReflectiveOperationException
    {
        return javaClassInstantiator.call(r.getNoDBNull(javaClassColName, String.class), ctorArgsForCheck);
    }
    @FunctionalInterface
    public interface JavaClassInstantiator<Tcls extends Versioned, Tctorarg>
    {
        public Tcls call(String javaClassName, Tctorarg javaCtorArg) throws ReflectiveOperationException;
    }
    
    
    
    private static Common.Action1<Throwable> prefilledstaticerrhandler;
    public static void prefilledStaticErrHandler(Throwable th)
    {
        if (prefilledstaticerrhandler != null)
            prefilledstaticerrhandler.call(th);
        else
            throw new Error(th);
    }
    public static void setPrefilledStaticErrHandler(Common.Action1<Throwable> h)
    {
        if (prefilledstaticerrhandler == null)
            prefilledstaticerrhandler = h;
        else
            throw new RuntimeException("setPrefilledStaticErrHandler must be called only once");
    }

}
