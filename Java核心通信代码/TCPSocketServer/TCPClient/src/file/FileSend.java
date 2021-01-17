package file;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class FileSend {

    //放置各客户端socket
//    public static List<Socket> sockets = Collections.synchronizedList(
//            new ArrayList<>()
//    );

    public static void main(String[] args) throws Exception {


        Socket socket = new Socket("47.95.39.148",6788);
//        sockets.add(socket);


        DataOutputStream dos = new DataOutputStream(
                socket.getOutputStream()
        );

        //文件
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入文件地址：");
        String filePath = scanner.nextLine();
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);

        //writeUTF()文件名发送到Socket
        dos.writeUTF(file.getName());
        dos.flush();
        dos.writeLong(file.length());
        dos.flush();

        int len = 0;
        byte[] buffer = new byte[1024];

        long recvSum = 0;

        while ((len = fis.read(buffer)) != -1){
            dos.write(buffer,0,len);
            dos.flush();
            recvSum += len;
            System.out.println(recvSum + "/" + file.length() + "=" + (float)recvSum/file.length());
        }
        System.out.println(file.length() + "(B) sent");
    }

}
