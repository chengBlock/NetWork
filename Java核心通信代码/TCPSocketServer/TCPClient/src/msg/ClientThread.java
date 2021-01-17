package msg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread implements Runnable {

    private Socket socket;
    private BufferedReader reader;

    public ClientThread(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String content = null;
        while (true) {
            try {
                if (((content = reader.readLine()) != null)) {
                    System.out.println(content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
