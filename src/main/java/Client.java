import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private Socket socket;
    private int id;
    
    public Client(Socket socket) {
        this.socket = socket;
    } 

    public Socket getSocket() {return this.socket;}
    public int getId() {return this.id;}
    public void setId(int id) {this.id = id;}

    @Override
    public String toString() {
        return "Player " + this.id;
    }

    private void sendMessageToServer(String message) throws IOException {
        PrintWriter pr = new PrintWriter(socket.getOutputStream());
        pr.println(message);
        pr.flush();
    }

    private void receiveMessageFromServer() throws IOException {
        InputStreamReader in = new InputStreamReader(socket.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        String str = bf.readLine();
        System.out.println("Server : " + str);
    }

    public static void main(String[] args) {
        try {
            Socket s = new Socket(Server.SERVER_IP, Server.SERVER_PORT);
            Client c = new Client(s);
            c.sendMessageToServer("Hello !");
            c.receiveMessageFromServer();
            s.close();
            // Server.close();
        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("Oh no ...");
        }
    }

}