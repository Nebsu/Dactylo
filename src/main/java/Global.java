import client.Client;
import server.Server;

public class Global {

    public static final int SERVER_PORT = 3610;
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 600;
    public static final String GAME_TITLE = "Dactylo";
    public static final int WORDS_TO_LEVEL_UP = 20;
    public static final Server SERVER = new Server(SERVER_PORT);
    public static final Client CLIENT = new Client("localhost", SERVER_PORT);

}