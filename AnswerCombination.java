import java.io.Serializable;
import java.util.HashMap;

public class AnswerCombination implements Serializable {

    private HashMap<PlayerHuman, Answer> answers;

    public AnswerCombination() {
        this.answers = new HashMap<>();
    }


    public Answer getAnswer(PlayerHuman playerHuman) {
        return this.answers.get(playerHuman);
    }

    public void setAnswer (PlayerHuman playerHuman, Answer answer) {
        this.answers.put(playerHuman, answer);
    }

}

