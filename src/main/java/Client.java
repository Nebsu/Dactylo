import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(Server.SERVER_IP, Server.SERVER_PORT);
        ServerConnection connection = new ServerConnection(socket);
        new Thread(connection).start();

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Gson gson = new Gson();
        Boolean b = true;
        String s = gson.toJson(b);
        out.println(s);
        socket.close();
        System.exit(0);
    }

}