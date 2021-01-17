package file;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

public class FileRecv {
    public static void main(String[] args) throws Exception {


        Socket socket = new Socket("47.95.39.148",6788);
        DataInputStream dis = new DataInputStream(
                socket.getInputStream()
        );

        String fileName = dis.readUTF();        //读文件名
        long fileLength = dis.readLong();        //读文件长度
        long recvSum = 0;       //统计收到的总字节
        System.out.println(fileName);
        File fileDir = new File("D:\\A");   //接收文件目录
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        //写文件流
        FileOutputStream fos = new FileOutputStream(fileDir.getAbsolutePath() + File.separator + fileName);

        //从socket接收文件
        int len = 0;
        //缓存字节数组
        byte[] buffer = new byte[1024];
        //dis.read(buffer)返回读入的字节数
        while ((len = dis.read(buffer)) != -1) {
            fos.write(buffer,0,len);
            fos.flush();
            recvSum +=len;
            System.out.println("len:" + len + "  " + recvSum + "/" + fileLength + "=" + (float)recvSum/fileLength);
        }
        System.out.println(fileLength + " (B) recieved" );

        dis.close();
        socket.close();
    }
}
