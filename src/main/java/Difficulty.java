public enum Difficulty {
    EASY("src/main/dictionary/easy.txt"),
    MEDIUM("src/main/dictionary/medium.txt"),
    HARD("src/main/dictionary/hard.txt");

    private String path;

    Difficulty(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
