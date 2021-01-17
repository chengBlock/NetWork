import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class MsgRecvThread implements Runnable {

    //当前线程处理对应的socket
    Socket socket = null;
    //socket对应的输入流
    BufferedReader reader = null;

    public MsgRecvThread(Socket socket) throws Exception{

        this.socket = socket;
        reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
    }

    @Override
    public void run() {
        try{
            String content = null;
            //不断读取Socket接收到的数据
            while ((content = readFromClient()) != null) {
                //向其它客户端发送
                for (Socket s : MyServer.msgSockets) {
                    if (!s.equals(this.socket)) {
                        PrintStream printStream = new PrintStream(
                                s.getOutputStream()
                        );
                        printStream.println(content);
                    }
                    //不能close，会关闭socket
//                   printStream.close();
                }
                System.out.println(content);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //定义读取客户端数据的方法
    private String readFromClient(){
        try{
	    String line = reader.readLine();
	    if(line.equals(null)){
                socket.close();
	    }else{
           	 return "["+socket.getInetAddress()+"] : "+line;
            }
            return null;
        } catch (IOException e) {
            //遇到异常，说明socket对应客户端已关闭，删除该客户端
            System.out.println("delete：" + socket.getInetAddress());
            MyServer.msgSockets.remove(socket);
            for (Socket socket : MyServer.msgSockets) {
                if (socket == null || socket.isClosed()) {
                    MyServer.msgSockets.remove(socket);
                }
            }
            e.printStackTrace();
        }
        return null;
    }
}
