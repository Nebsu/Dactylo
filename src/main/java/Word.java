public class Word {
    private String word;
    private boolean bonus;

    public Word(String word){
        this.word = word;
        this.bonus = randomBoolean();
    }

    @Override
    public String toString() {
        return word;
    }

    public boolean randomBoolean() {
        return Math.random() < 0.9;
    }
    public boolean isBonus() {
        return !bonus;
    }
}
