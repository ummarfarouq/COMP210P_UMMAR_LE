import java.io.Serializable;

public class Answer implements Serializable {


    private final String text;
    private final boolean correctchoice;

    public Answer(String text, boolean correctchoice) {
        this.text = text;
        this.correctchoice = correctchoice;
    }

    public boolean isCorrectchoice() {
        return correctchoice;
    }

    public String format() {
        return this.text;
    }

}
