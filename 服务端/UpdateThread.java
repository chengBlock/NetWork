import java.io.*;
import java.net.Socket;

public class UpdateThread implements Runnable {

    Socket socket;
    File file;
    FileInputStream fis;
    DataOutputStream dos;

    public UpdateThread(){}

    public UpdateThread(Socket socket){
        this.socket = socket;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            file = new File("apk/");
            String fileName = file.list()[0];
            File apk = new File("apk" + File.separator + fileName);
            fis = new FileInputStream(apk);

            //writeUTF()文件名发送到Socket
            dos.writeUTF(apk.getName());
            dos.flush();
            dos.writeLong(apk.length());
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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (dos != null) {
                    dos.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}