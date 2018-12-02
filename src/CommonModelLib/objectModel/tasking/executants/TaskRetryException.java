package CommonModelLib.objectModel.tasking.executants;

/**
 * Retry task later
 * 
 */
public class TaskRetryException extends Exception {
    public final int sleep_sec;
    public TaskRetryException(int sleep_sec) {
        if (sleep_sec < 0 || sleep_sec > 3600)
            throw new java.lang.IllegalArgumentException("sleep_sec must be >= 0 and <= 3600");
        this.sleep_sec = sleep_sec;
    }    
}
