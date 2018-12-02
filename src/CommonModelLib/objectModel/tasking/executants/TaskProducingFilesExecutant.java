package CommonModelLib.objectModel.tasking.executants;

import CommonModelLib.objectModel.FileStorePaths;
import CommonLib.Common;
import CommonModelLib.dbModel.IEnumCache;
import CommonModelLib.objectModel.tasking.TaskInfo;
import DBmethodsLib.DBmethodsCommon;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * 
 * @param <Ttaskinfo>
 * @param <Tienumcache>
 */
public abstract class TaskProducingFilesExecutant<Ttaskinfo extends TaskInfo, Tienumcache extends IEnumCache> extends TaskExecutant<Ttaskinfo, Tienumcache>
{
    public static class TaskProducingFilesExecutantCtor<Ttaskinfo extends TaskInfo, Tienumcache extends IEnumCache> extends TaskExecutant.TaskExecutantCtor<Ttaskinfo, Tienumcache>
    {
        public Common.Func1<Integer, String[]> msgIdsGenerator;
        public FileStorePaths fileStorePaths;
    }
    private final Common.Func1<Integer, String[]> msgIdsGenerator;
    protected final FileStorePaths fileStorePaths;
    public TaskProducingFilesExecutant(TaskProducingFilesExecutantCtor<Ttaskinfo, Tienumcache> ctor) {
        super(ctor);
        this.fileStorePaths = Objects.requireNonNull(ctor.fileStorePaths);
        this.msgIdsGenerator = Objects.requireNonNull(ctor.msgIdsGenerator);
    }
    
    protected String[] generateMsgIds(int cnt)
    {
        return msgIdsGenerator.call(cnt);
    }
    
    /**
     * Reads file from storage and ungzips
     * @param fileStorePaths
     * @param filePathSuffix
     * @return
     * @throws java.io.IOException
     */
    public static byte[] readAndUngzipFileBody(FileStorePaths fileStorePaths, String filePathSuffix) throws IOException
    {
        return Common.UnGZip(readFile(fileStorePaths, filePathSuffix));
    }
    
    /**
     * Reads file from storage
     * @param fileStorePaths
     * @param filePathSuffix
     * @return
     * @throws java.io.IOException
     */
    public static byte[] readFile(FileStorePaths fileStorePaths, String filePathSuffix) throws IOException
    {
        Path f = fileStorePaths.getExistingFile(filePathSuffix);
        return Files.readAllBytes(f);
    }
    
    public static abstract class TaskProducingFilesResult<Tfile extends TaskProducingFilesResult.TaskProducingFilesFile> extends TaskExecutant.TaskResult
    {        
        public static abstract class TaskProducingFilesFile<Ttaskinfo extends TaskInfo>
        {
            private Long file_id;
            public final boolean isExistingFile;
            public TaskProducingFilesFile(boolean isExistingFile)
            {
                if (isExistingFile)
                    throw new RuntimeException("For existing file use TaskProducingFilesFile(boolean isExistingFile) constructor");
                this.isExistingFile = isExistingFile;
            }
            public TaskProducingFilesFile(boolean isExistingFile, long file_id)
            {
                if (!isExistingFile)
                    throw new RuntimeException("For existing file use TaskProducingFilesFile(boolean isExistingFile, long file_id) constructor");
                this.isExistingFile = isExistingFile;
                this.file_id = file_id;
            }
            public static void fillFile_ids(DBmethodsCommon dbmethods, TaskProducingFilesFile[] fls)
            {
                String idsSQL = null;
                for (int n = 0; n < fls.length; n++)
                    if (!fls[n].isExistingFile)
                        idsSQL = (idsSQL == null ? "" : idsSQL + "\n union all \n") + "select nextval('file_file_id_seq')"; 
                Long[] file_ids;
                if (idsSQL != null)
                {
                    try {
                        file_ids = Common.collectionToArray(dbmethods.select_from(true, idsSQL)[0].Rows, Long[].class, (r) -> (Long)r.get(0));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    for (int n = 0, m = 0; n < fls.length; n++)
                        if (!fls[n].isExistingFile)
                        {
                            fls[n].file_id = file_ids[m];
                            m++;
                        }
                }
            }
            public long getFile_id(DBmethodsCommon dbmethods)
            {
                if (file_id == null)
                {
                    fillFile_ids(dbmethods, new TaskProducingFilesFile[] { this });
                }
                return file_id;
            }
            public abstract byte[] fileBody();
            public abstract String en_FileFormat();
            public abstract String filePath(LocalDateTime pathDate, DBmethodsCommon dbmethods, Ttaskinfo taskInfo);
        }        
        public abstract Tfile[] getFiles() throws MustBeNoFilesException;
        public static class MustBeNoFilesException extends Exception { }
    }
    
    /**
     * PL/pgSQL-variable name of type bigint[], that will contain file_id array
     */
    public static final String file_ids_varName = "v_file_ids";
    
}
