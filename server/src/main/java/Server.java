import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    DataInputStream is;
    DataOutputStream os;
    ServerSocket server;

    public Server() throws IOException {
        System.out.println("Сервер запущен...");
        server = new ServerSocket(8189); //запуск сервера на на порту 8189
        Socket socket = server.accept(); //проверка клиента
        System.out.println("Client accepted!"); //подтверждение в консоль что клиент поднят

        is = new DataInputStream(socket.getInputStream());//прием
        //os = new DataOutputStream(socket.getOutputStream());
        String fileName = is.readUTF();
        //получение/-/name/-/size/-/тело файла    должен ответить ОК (если передача удачна те size совпал)
        //отправка/-/name/-/size/-/тело файла       должен получить ОК (если передача удачна те size совпал)

        System.out.println("fileName: " + fileName);
        File file = new File ("./server/Server_Files/" + fileName); //создали файл с полученным от клиента именем
        file.createNewFile();
        try (FileOutputStream oss = new FileOutputStream(file)){ //вписали из сокета в файл кусками из буфера
            byte [] buffer = new byte [256];
            while (true){
                int r = is.read(buffer);
                if (r == -1) break;
                oss.write(buffer, 0, r);
            }
        }
        System.out.println("Файл отправлен на сервер!");
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

}
