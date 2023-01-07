package misc;

import network.Player;

public final class Global {

    private Global() {}

    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 600;
    public static final String GAME_TITLE = "Dactylo";
    public static final int WORDS_TO_LEVEL_UP = 20;
    public static final int DEFAULT_HEALTH = 20;
    public static final int DEFAULT_LEVEL = 1;
    public static final int DEFAULT_WORD_COUNT = 0;
    public static final String FILE_PATH = "src/main/save/data.txt";
    public static final String LEADERBOARD_FILE_PATH = "src/main/save/leaderboard.txt";
    public static final Player PLAYER = new Player(GameSettings.getUsername());
    public static final int MAX_LIST_SIZE_MULTI = 15;

}