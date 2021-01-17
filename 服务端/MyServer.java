import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {

    //保存已连接的socket，线程包装安全
    public static List<Socket> msgSockets = Collections.synchronizedList(
            new ArrayList<>()
    );
    public static Map<Socket, DataOutputStream> fileMap = Collections.synchronizedMap(
            new HashMap<>()
    );

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        //文本消息，6789
        ServerSocket msgSS = new ServerSocket(6789);
        //文件消息，6788
        ServerSocket fileSS = new ServerSocket(6788);
        //更新
        ServerSocket updateAPKSS = new ServerSocket(6787);

        //更新cxt
        ServerSocket updateAPKSSCXT = new ServerSocket(6786);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("msg blocked...");
                        Socket msgSocket = msgSS.accept();
                        System.out.println("msgSocket connected");
                        threadPool.execute(new MsgRecvThread(msgSocket));
                        msgSockets.add(msgSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        System.out.println("fileSocket blocked...");
                        Socket socket = fileSS.accept();
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                        System.out.println("fileSocket connected");
                        fileMap.put(socket,dos);
                        threadPool.execute(new FileRecvThread(socket));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket socket = updateAPKSS.accept();
                        threadPool.execute(new UpdateThread(socket));
                        System.out.println(socket.getLocalAddress());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket socket = updateAPKSSCXT.accept();
                        threadPool.execute(new UpdateThreadCXT(socket));
                        System.out.println(socket.getLocalAddress());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
