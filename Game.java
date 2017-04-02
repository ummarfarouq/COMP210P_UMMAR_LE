
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Game implements Serializable {


    private final int roundSize;
    private final int numberOfRounds;

    private PlayerHuman playerHuman1;
    private PlayerHuman playerHuman2;

    private int currentQuestionPlayer1;
    private int currentQuestionPlayer2;
    private int correctAnswersPlayer1;
    private int correctAnswersPlayer2;
    private boolean GameFinished;


    private ArrayList<Question> GameQuestions;
    private ArrayList<AnswerCombination> GameAnswers;
    private HashMap<Question, AnswerCombination> answeredQuestions;

    public Game(PlayerHuman playerHuman, int roundSize, int numberOfRounds) {

        this.roundSize=roundSize;
        this.numberOfRounds=numberOfRounds;

        this.currentQuestionPlayer1=0;
        this.currentQuestionPlayer2=0;
        this.correctAnswersPlayer1=0;
        this.correctAnswersPlayer2=0;

        this.playerHuman1 = playerHuman;
        this.playerHuman2 =null;

        this.GameQuestions = QuestionFileHandler.getQuestions(numberOfRounds*roundSize);
        this.GameAnswers = new ArrayList<AnswerCombination>();
        this.answeredQuestions = new HashMap<>();

        for (int i=0; i<numberOfRounds*roundSize; i++) {
            GameAnswers.add(new AnswerCombination());
            answeredQuestions.put(GameQuestions.get(i), GameAnswers.get(i));
        }
    }

    public void addOpponent (PlayerHuman playerHuman) {
        this.playerHuman2 = playerHuman;
    }

    public Question getCurrentQuestion (PlayerHuman playerHuman) {
        if (playerHuman.equals(this.playerHuman1)) return this.GameQuestions.get(currentQuestionPlayer1);
        else return this.GameQuestions.get(currentQuestionPlayer2);
    }

    public int getCurrentQuestionNumber (PlayerHuman playerHuman) {
        if (playerHuman.equals(this.playerHuman1)) return this.currentQuestionPlayer1+1;
        else return this.currentQuestionPlayer2+1;
    }

    public int getTotalQuestionNumber () {
        return this.numberOfRounds*this.roundSize;
    }

    public int getCurrentRoundNumber (PlayerHuman playerHuman) {
        if (playerHuman.equals(this.playerHuman1)) return (this.currentQuestionPlayer1 / roundSize) + 1;
        else return (this.currentQuestionPlayer2 / roundSize) + 1;
    }

    public PlayerHuman getOpponent (PlayerHuman playerHuman) {
        if (playerHuman.equals(this.playerHuman1) && this.playerHuman2 !=null) return this.playerHuman2;
        else if (playerHuman.equals(this.playerHuman1) && this.playerHuman2 ==null) return null;
        else return this.playerHuman1;
    }

    public String getOpponentName (PlayerHuman playerHuman) {
        if (playerHuman.equals(this.playerHuman1) && this.playerHuman2 !=null) return this.playerHuman2.getName();
        else if (playerHuman.equals(this.playerHuman1) && this.playerHuman2 ==null) return "Not yet set";
        else return this.playerHuman1.getName();
    }

    public boolean isAnswerAllowed (PlayerHuman playerHuman) {
        if (this.GameFinished) return false;

        if (playerHuman.equals(playerHuman1)) {
            return this.currentQuestionPlayer1/this.roundSize <= this.currentQuestionPlayer2/this.roundSize;
        } else {
            return this.currentQuestionPlayer2/this.roundSize <= this.currentQuestionPlayer1/this.roundSize;
        }
    }






    public boolean isFinished () {
        return this.GameFinished;
    }

    public void answer (PlayerHuman playerHuman, Answer answer) {
        this.answeredQuestions.get(getCurrentQuestion(playerHuman)).setAnswer(playerHuman, answer);
        if (answer.isCorrectchoice()) {
            if (playerHuman.equals(this.playerHuman1)) this.correctAnswersPlayer1++;
            else this.correctAnswersPlayer2++;
        }
        this.nextQuestion(playerHuman);
    }

    public void skip (PlayerHuman playerHuman) {

        this.nextQuestion(playerHuman);
    }

    public void cheat (PlayerHuman playerHuman) {

        this.nextQuestion(playerHuman);
    }

    public boolean isSingleGame() {
        return this.playerHuman2 ==null;
    }

    public PlayerHuman getPlayerHuman1() {
        return this.playerHuman1;
    }

    public PlayerHuman getPlayerHuman2() {
        return this.playerHuman2;
    }

    public PlayerHuman getWinner () {
        if (this.isFinished()) {
            if (this.correctAnswersPlayer1>this.correctAnswersPlayer2) return this.playerHuman1;
            if (this.correctAnswersPlayer1<this.correctAnswersPlayer2) return this.playerHuman2;
        }
        return null;

    }

    public int getNumberOfCorrectQuestions (PlayerHuman playerHuman) {
        if (playerHuman.equals(this.playerHuman1)) {
            return this.correctAnswersPlayer1;
        } else {
            return this.correctAnswersPlayer2;
        }
    }

    private void nextQuestion(PlayerHuman playerHuman) {
        if (playerHuman.equals(this.playerHuman1)) {
            this.currentQuestionPlayer1++;
        }

        if (this.playerHuman2 != null && playerHuman.equals(this.playerHuman2)) {
            this.currentQuestionPlayer2++;
        }

        if (currentQuestionPlayer1>=roundSize*numberOfRounds && currentQuestionPlayer2>=roundSize*numberOfRounds) this.GameFinished =true;
    }
}