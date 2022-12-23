public class Word {
    private String word;
    private boolean bonus;

    public Word(String word, boolean bonus){
        this.word = word;
        this.bonus = bonus;
    }

    @Override
    public String toString() {
        return word;
    }
}
