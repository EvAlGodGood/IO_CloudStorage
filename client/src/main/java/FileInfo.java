import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {
    private String fileName;
    private long fileSize;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSaze) {
        this.fileSize = fileSaze;
    }

    public FileInfo (Path path){
        try {
            this.fileName = path.getFileName().toString();
            if (Files.isDirectory(path)){
                this.fileSize = -1L;
            } else {
                this.fileSize = Files.size(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Невозможно считать файлы по пути");
        }

    }
}
