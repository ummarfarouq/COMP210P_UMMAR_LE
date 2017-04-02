
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class QuestionFileHandler {
    private static File QuizFile = new File("Questions.dat");
    private static ArrayList<Question> ListOfQuestions = new ArrayList<Question>();

    public static void QuestionsLoad() {

        if (!QuizFile.exists()) return;

        try (
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(QuizFile))
        ) {

            ListOfQuestions = (ArrayList<Question>) inputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void QuestionAdd(String question, ArrayList<Answer> answers) {
        ListOfQuestions.add(new Question(question,answers));
        QuestionSave();
    }

    public static void QuestionSave() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(QuizFile))) {
            out.writeObject(ListOfQuestions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String formatQuestionCatalogue () {
        String str = "";
        for (int i = 0; i< ListOfQuestions.size(); i++) {
            str += ListOfQuestions.get(i).format();
            str += "\n \n";
        }
        return str;
    }

    public static int getTotalQuestionNumber() {
        return ListOfQuestions.size();
    }

    public static ArrayList<Question> getQuestions (int numRequestedQuestions) {
        ArrayList<Question> duelQuestions = new ArrayList<Question>();
        ArrayList<Integer> usedQuestionIndexes = new ArrayList<Integer>();
        int numAvailableQuestions = ListOfQuestions.size();
        int randomQuestionIndex = (int) (Math.random() * numAvailableQuestions);

        for (int i=0; i<numRequestedQuestions; i++) {
            duelQuestions.add(ListOfQuestions.get(randomQuestionIndex));
            usedQuestionIndexes.add(randomQuestionIndex);

            //get next random question index
            boolean foundNewRandomIndex=false;
            while (!foundNewRandomIndex) {
                randomQuestionIndex = (int) (Math.random() * numAvailableQuestions);
                int matches=0;
                for (int k=0; k<usedQuestionIndexes.size(); k++) {
                    if (randomQuestionIndex==(int)usedQuestionIndexes.get(k)) matches++;
                }
                if (matches==0) foundNewRandomIndex=true;
            }
        }

        return duelQuestions;
    }
}
