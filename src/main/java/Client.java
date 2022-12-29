import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(Server.SERVER_IP, Server.SERVER_PORT);
        ServerConnection connection = new ServerConnection(socket);
        new Thread(connection).start();

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        while (true) {
            System.out.print("> ");
            String command = keyboard.readLine();
            if (command.equals("exit")) break;
            out.println(command);
        }
        socket.close();
        System.exit(0);
    }

}