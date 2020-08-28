import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo {
    public enum FileType {
        FILE("F"), DIRECTORY ("D");

        private String name;

        public String getName(){
            return name;
        }


        FileType(String name){
            this.name=name;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    private FileType type;

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    private Long size;

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    private LocalDateTime lastModified;


    public FileInfo (Path path) {



        try {
            this.fileName=path.getFileName().toString();
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            if (this.type == FileType.DIRECTORY){
                this.size = -1L;
            }
            this.size = Files.size(path);
            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }

    }
}
