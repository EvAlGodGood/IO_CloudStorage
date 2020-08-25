import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class Controller implements Initializable {

    private String clientPath = "/client/Client_Files";

    @FXML
    ListView<FileInfo> listFileClient;

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void btnSendFileAction(ActionEvent actionEvent) throws IOException {
        sendFile(new Socket("localhost", 8189), new File("./client/Client_Files/12345"));
    }

    public void btnGetFileAction(ActionEvent actionEvent) {
    }

    public static void sendFile(Socket socket, File file) throws IOException {
        InputStream is = new FileInputStream(file); //считали из файла

        try(DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {//выбросили считанное в сокет
            byte [] buffer = new byte[256];
            os.writeUTF(file.getName());// отправили имя файла серверу
            //подобным образом нужно давать команду на получение или отправку сервером файлов чтобы он переключался

            while (true){
                int r = is.read(buffer);
                if (r == -1) break;
                os.write(buffer, 0, r);
            }

            System.out.println("Файл получен сервером.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Path root = Paths.get("client/Client_Files");
        try {
            listFileClient.getItems().addAll(scanFile(root));

            listFileClient.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
                @Override
                public ListCell<FileInfo> call(ListView<FileInfo> param) {
                    return new ListCell<FileInfo>(){
                        @Override
                        protected void updateItem(FileInfo item, boolean emty){
                            super.updateItem(item, emty);
                            if (item == null || emty){
                                setText(null);
                                setStyle("");
                            } else {
                                String formattedFileName = String.format("%-30s",item.getFileName());
                                String formattedFileSize = String.format("%,20d",item.getFileSize());
                                String text = String.format("%s %s", formattedFileName,formattedFileSize);
                                setText(text);
                            }
                        }
                    };

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        //listFileClient.getItems().addAll("fsafksdofk","sfsewf");
    }

    public List<FileInfo> scanFile (Path root) throws IOException {
        return Files.list(root).map(FileInfo::new).collect(Collectors.toList());
    }
}
