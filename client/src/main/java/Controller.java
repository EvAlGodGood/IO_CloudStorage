import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class Controller implements Initializable {

    private String clientPath = "/client/Client_Files";
    @FXML
    TableView<FileInfo> tableFileClient;

    @FXML
    ComboBox<String> disksBox;

    @FXML
    TextField textFieldPathClient;

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void btnSendFileAction(ActionEvent actionEvent) throws IOException {
        System.out.println("\"" + getCurrentPath()+"\\" + getSelektedFileName() + "\"");
        sendFile(new Socket("localhost", 8189), new File(getCurrentPath()+"\\" + getSelektedFileName() /*"./client/Client_Files/12345"*/));
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

            TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
            fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
            fileTypeColumn.setPrefWidth(30);

            TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");
            fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
            fileNameColumn.setPrefWidth(200);

            TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
            fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getSize()));
            fileSizeColumn.setPrefWidth(80);
            fileSizeColumn.setCellFactory(column -> {
                return new TableCell<FileInfo, Long>() {
                    @Override
                    protected void updateItem (Long item, boolean emty){
                        super.updateItem(item, emty);
                        if (item == null || emty) {
                            setText(null);
                            setStyle("");
                        } else {
                            String text=String.format("%,d bytes", item);
                            if (item == -1L){
                                text = "[DIR]";
                            }
                            setText(text);
                        }
                    }
                };
            });

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
            fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
            fileDateColumn.setPrefWidth(120);

            tableFileClient.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);//вывели столбы в табл.
            tableFileClient.getSortOrder().add(fileTypeColumn); //сортировка по первому стоблцу (выкинутб и столбец все что связано
            //сортировать по любому другому )

            disksBox.getItems().clear();
            for(Path p : FileSystems.getDefault().getRootDirectories()){
                disksBox.getItems().add(p.toString());
            }
            disksBox.getSelectionModel().select(0);

            tableFileClient.setOnMouseClicked(new EventHandler<MouseEvent>() { //клик по
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2){//при двойном клике
                        Path path = Paths.get(textFieldPathClient.getText()).resolve(tableFileClient.getSelectionModel().getSelectedItem().getFileName()); //берем путь
                        if (Files.isDirectory(path)){//если выбрана директория идем в нее.
                            updateList(path);
                        }
                    }
                }
            });

            updateList(Paths.get(".")); //по умолчанию входим



    }
    public void updateList(Path path) {
        System.out.println("\"" + getCurrentPath()+"\\" + getSelektedFileName() + "\"");
        try {
            textFieldPathClient.setText(path.normalize().toAbsolutePath().toString()); //кинули путь в текстовое поле.

            tableFileClient.getItems().clear();
            tableFileClient.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            tableFileClient.sort();
        } catch (IOException e) {
            Alert alert = new Alert (Alert.AlertType.WARNING, "Не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait(); //ждать пока не нажат ОК
        }

    }

    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource(); //запросили источник события
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()));


    }

    public String getSelektedFileName(){//какой выбран файл
        if(!tableFileClient.isFocused()){//проверка на фокус так нужен файл из конкретной (одной) таблицы
            return null;
        }
        return tableFileClient.getSelectionModel().getSelectedItem().getFileName();
    }
    public String getCurrentPath(){//какой путь
        return textFieldPathClient.getText();
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(textFieldPathClient.getText()).getParent();
        if (upperPath != null){
            updateList(upperPath);
        }
    }
}
