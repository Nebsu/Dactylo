import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class ServerConnection implements Runnable {

    private Socket socket;
    private int id;
    private BufferedReader in;
    private PrintWriter out;
    
    public ServerConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            String response;
            while (true) {
                response = in.readLine();
                if (response == null) break;
                System.out.println("[SERVER] " + response);
            }
        } catch (SocketException e) {
            try {
                System.out.println("[SERVER] Client " + id + " disconnected");
                this.socket.close();
                Thread.interrupted();
            } catch (IOException io) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}