import java.io.File;

public class Update {
    public static void main(String[] args) {
        File file = new File("apk//");
        for (String fileName : file.list()) {
            System.out.println(fileName);
        }
    }
}