package CommonModelLib.objectModel;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 *
 * 
 */
public interface FileStorePaths
{

    /**
     * Returs full path of file on storage (if file exists) 
     * @param filePathSuffix
     * @return
     * @throws java.nio.file.NoSuchFileException (if file not exists)
     */
    public Path getExistingFile(String filePathSuffix) throws NoSuchFileException;

}