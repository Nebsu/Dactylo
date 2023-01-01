package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class ClientHandler implements Runnable {

    private Socket socket;
    private int id;
    private BufferedReader in;

    public ClientHandler(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.id = id;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Socket getSocket() {return this.socket;}
    public int getId() {return this.id;}
 
    @Override
    public void run() {
        try {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("client loop");
                Gson gson = new Gson();
                LinkedTreeMap<String, Object> map = gson.fromJson(request, LinkedTreeMap.class);
                String message = (String) map.get("message");
                if (message.equals("Connection")) {
                    String name = (String) map.get("pseudo") + String.valueOf(id);
                    // System.out.println(name);
                    Server.addPlayer(name);
                    Server.showToEveryone();
                }
            }
        } catch (SocketException se) {
            System.out.println("Client " + id + " disconnected");
        } catch (IOException e) {
            System.err.println("IO Exception Client Handler");
            System.err.println(e.getStackTrace());
        } finally {
            try {
                in.close();
                this.socket.close();
                Thread.interrupted();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("IO Exception Client Handler");
            }
        }
    }

}