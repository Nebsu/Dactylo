package misc;

import java.util.Random;

public final class Word {
    
    private final String word;
    private final char type;

    public Word(String word){
        this.word = word;
        this.type = randomBoolean();
    }
    public Word(String word, char type){
        this.word = word;
        this.type = type;
    }

    @Override
    public String toString() {
        return word;
    }

    public char randomBoolean() {
        Random random = new Random();
        int n = random.nextInt(10);
        if (n == 0) {
            return 'b';
        } else if (n == 1 || n == 2 || n == 3 || n == 4) {
            return 'm';
        } else {
            return 'n';
        }
    }

    public char getType() {
        return type;
    }

}