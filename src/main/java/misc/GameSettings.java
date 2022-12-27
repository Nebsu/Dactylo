package misc;

public class GameSettings {
    private static String difficulty = String.valueOf(Difficulty.EASY.getPath());
    private static String difficultyName = "Easy";
    private static int words_max_length = 15;

    private GameSettings() {}

    public static String getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(String difficulty) {
        if(difficulty.equals("Easy")) {
            GameSettings.difficulty = String.valueOf(Difficulty.EASY.getPath());
        } else if(difficulty.equals("Medium")) {
            GameSettings.difficulty = String.valueOf(Difficulty.MEDIUM.getPath());
        } else if(difficulty.equals("Hard")) {
            GameSettings.difficulty = String.valueOf(Difficulty.HARD.getPath());
        }
    }

    public static int getWords_max_length() {
        return words_max_length;
    }

    public static void setWords_max_length(int words_max_length) {
        GameSettings.words_max_length = words_max_length;
    }

    public static String getDifficultyName() {
        return difficultyName;
    }

    public static void setDifficultyName(String difficultyName) {
        GameSettings.difficultyName = difficultyName;
    }
}
