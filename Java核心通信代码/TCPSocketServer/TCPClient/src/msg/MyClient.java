package msg;

import java.io.*;
import java.net.Socket;

public class MyClient {
    public static void main(String[] args) {

        try {
            Socket socket = new Socket("47.95.39.148",6666);
            new Thread(new ClientThread(socket)).start();

            PrintStream printStream = new PrintStream(
                    socket.getOutputStream()
            );

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            String line = null;
            while ((line = reader.readLine()) != null) {
                printStream.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
