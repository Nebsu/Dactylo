import controller.Game;
import misc.Word;
import org.junit.jupiter.api.Assertions;

public class Test {
    @org.junit.jupiter.api.Test
    public void checkWord() {
        Word word = new Word("test");
        String word2 = "test";
        boolean b = word.toString().equals(word2);
        Assertions.assertTrue(b);
    }

    @org.junit.jupiter.api.Test
    public void checkWord2() {
        Word word = new Word("test");
        String word2 = "testsss";
        boolean b = word.toString().equals(word2);
        Assertions.assertFalse(b);
    }

    @org.junit.jupiter.api.Test
    public void checkBonus() {
        Word word = new Word("test", 'b');
        Assertions.assertEquals(word.getType() == 'b', true);
    }
}
