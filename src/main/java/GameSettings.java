public class GameSettings {
    private static String difficulty = String.valueOf(Difficulty.EASY);
    private static int words_max_length = 15;

    private GameSettings() {}

    public static String getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(String difficulty) {
        GameSettings.difficulty = difficulty;
    }

    public static int getWords_max_length() {
        return words_max_length;
    }

    public static void setWords_max_length(int words_max_length) {
        GameSettings.words_max_length = words_max_length;
    }
}
