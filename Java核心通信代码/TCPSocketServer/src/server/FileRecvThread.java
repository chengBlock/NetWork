package server;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class FileRecvThread implements Runnable {

    //client1     server       client2 client3

    //文件Socket
    private Socket socket;
    private DataInputStream dis;

    //初始化Socket及Socket的IO流
    public FileRecvThread(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
//            String fileName = dis.readUTF();
//            long fileLength = dis.readLong();

            int len = 0;
            byte[] buffer = new byte[1024];

            //读到文件尾，返回-1
            while ((len = dis.read(buffer)) != -1) {


                for (Map.Entry<Socket,DataOutputStream> map : MyServer.fileMap.entrySet()) {
                    //获取所有文件socket
                    Socket socket = map.getKey();
                    DataOutputStream dos = map.getValue();
                    if (!socket.equals(this.socket)){
                        dos.write(buffer,0,len);
                        dos.flush();
                    }
//                    socket.shutdownOutput();
                }

            }
            System.out.println(socket.getInetAddress()+ "传输完毕，断开连接");
        }catch (Exception e){
            e.printStackTrace();
            MyServer.fileMap.remove(socket);
            System.out.println("删除socket" + socket.getInetAddress());
            for (Socket socket : MyServer.fileMap.keySet()) {
                try {
                    System.out.println("关闭map中Socket" + socket.getInetAddress());
                    socket.close();
                    MyServer.fileMap.remove(socket);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }finally {
            MyServer.fileMap.clear();

            try{

                if (dis != null) {
                    dis.close();
                }
                for (Socket socket : MyServer.fileMap.keySet()) {
                    try {
                        System.out.println("关闭map中Socket" + socket.getInetAddress());
                        socket.close();
                        MyServer.fileMap.remove(socket);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                if (socket != null) {
                    socket.close();
                }

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

