import java.io.Serializable;
import java.util.ArrayList;



public class Question implements Serializable {

    private String question;
    private ArrayList<Answer> answers;

    public Question(String question, ArrayList<Answer> answers) {
        this.question=question;
        this.answers=answers;
    }

    private void AnswersRandom() {
        int numAnswers = this.answers.size();
        int[] randomIndex = new int[numAnswers];
        randomIndex[0] = (int) (Math.random() * numAnswers);
        randomIndex[1]=randomIndex[0];
        while (randomIndex[1]==randomIndex[0]) {
            randomIndex[1]=(int) (Math.random() * numAnswers);
        }
        randomIndex[2]=randomIndex[1];
        while (randomIndex[2]==randomIndex[1] || randomIndex[2]==randomIndex[0]) {
            randomIndex[2]=(int) (Math.random() * numAnswers);
        }
        randomIndex[3]=randomIndex[2];
        while (randomIndex[3]==randomIndex[2] || randomIndex[3]==randomIndex[1] || randomIndex[3]==randomIndex[0]) {
            randomIndex[3]=(int) (Math.random() * numAnswers);
        }
        ArrayList<Answer> RemAnswers = new ArrayList<Answer>();
        for (int i=0; i<numAnswers; i++) {
            RemAnswers.add(answers.get(randomIndex[i]));
        }
        this.answers=RemAnswers;
    }


    public String format () {
        this.AnswersRandom();
        String str = this.question + "\n A: " + this.answers.get(0).format() + "\n B: " + this.answers.get(1).format() + "\n C: " + this.answers.get(2).format() + "\n D: " + this.answers.get(3).format();
        return str;
    }

    public ArrayList<Answer> getAnswers () {
        return this.answers;
    }

    public Answer getCorrectAnswer () {
        int index=-1;
        for (int i=0; i<this.answers.size(); i++) {
            if (this.answers.get(i).isCorrectchoice()) index=i;
        }

        return this.answers.get(index);
    }




}


