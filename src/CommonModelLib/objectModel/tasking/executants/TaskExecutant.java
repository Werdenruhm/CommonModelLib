package CommonModelLib.objectModel.tasking.executants;

import CommonModelLib.objectModel.tasking.TaskInfo;
import CommonLib.Common;
import CommonModelLib.dbModel.IEnumCache;
import DBmethodsLib.DBaggregator;
import java.time.LocalDateTime;
import java.util.Objects;
import CommonModelLib.objectModel.Versioned;

/**
 *
 * 
 * @param <Ttaskinfo>
 * @param <Tienumcache>
 */
public abstract class TaskExecutant<Ttaskinfo extends TaskInfo, Tienumcache extends IEnumCache> implements Versioned {
    
    public final Ttaskinfo taskInfo; 
    protected final DBaggregator dbaggregator; 
    public final Tienumcache enumCache;
    protected final Common.Log worker_msgLog;
    protected final Common.Log worker_excLog;
    private final Common.Func<Boolean> worker_hasStopSignal;
    private final Common.Func<LocalDateTime> dbNow;
    
    public boolean worker_hasStopSignal() { return worker_hasStopSignal.call(); }
    public LocalDateTime dbNow() { return dbNow.call(); }
    
    public static class TaskExecutantCtor<Ttaskinfo extends TaskInfo, Tienumcache extends IEnumCache>
    {
        public Ttaskinfo taskInfo; 
        public DBaggregator dbaggregator; 
        public Tienumcache enumCache;
        public Common.Log worker_msgLog;
        public Common.Log worker_excLog;
        public Common.Func<Boolean> worker_hasStopSignal;
        public Common.Func<LocalDateTime> dbNow;
    }
    public TaskExecutant(TaskExecutantCtor<Ttaskinfo, Tienumcache> ctor)
    {
        this.taskInfo = Objects.requireNonNull(ctor.taskInfo); 
        this.dbaggregator = Objects.requireNonNull(ctor.dbaggregator); 
        this.enumCache = Objects.requireNonNull(ctor.enumCache);
        this.worker_msgLog = Objects.requireNonNull(ctor.worker_msgLog);
        this.worker_excLog = Objects.requireNonNull(ctor.worker_excLog);
        this.worker_hasStopSignal = Objects.requireNonNull(ctor.worker_hasStopSignal);
        this.dbNow = Objects.requireNonNull(ctor.dbNow);
    }
    
    
    
    public static class TaskResult
    {

        /**
         * PL/pgSQL, that must be executed to complete task
         */
        public String PLpgSQL;
    }        
    public abstract TaskResult doTask() throws Exception, TaskRetryException;            
}
