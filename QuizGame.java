/**
 * Created by Farouq on 26/03/2017.
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.io.*;
import java.util.Collections;
import java.util.InputMismatchException;

public class QuizGame {

    static int c = 0;
    static int q = 0;
    static String choiceanswer;
    static String restart;




    String line = null;

    static String iny;


    static Scanner scan = new Scanner ( System.in );

    private static Scanner in = new Scanner(System.in);
    private static boolean playing = true;
    private static PlayerHuman loggedPlayerHuman = null;
    private static Password loggedPassword = null;
    private static Game activeGame = null;
    private final static int roundSize=3; //questions per round
    private final static int numberOfRounds=3; //total number of rounds
    static  int MaximumProbabality;
    static int MinimumProbabality;


    public static void main(String[] args) {

            PlayerHumanFileHandler.PlayerLoad();
            QuestionFileHandler.QuestionsLoad();
            GameFileHandler.loadGame();
            while (playing==true) showSelection();
        }









    private static void showSelection () {
        activeGame =null;
        System.out.println("\nLog I)n  N)ew Game  C)ontinue Game  D)isplay Games  L)oad questions A)PlayComputer  Log O)ut  Q)uit");
        String mode = "";

        if (in.hasNext()) {
            mode = in.next();
        }

        if (!mode.toLowerCase().matches("[incdsloqa]")) {
            System.out.println("Invalid input!");
            showSelection();
        }

        if (mode.toLowerCase().equals("l")) createQuestion();
        else if (mode.toLowerCase().equals("i")) login();
        else if (mode.toLowerCase().equals("n")) newGame();
        else if (mode.toLowerCase().equals("c")) continueGame();
        else if (mode.toLowerCase().equals("d")) displayStatistics();
        else if (mode.toLowerCase().equals("o")) logout();
        else if (mode.toLowerCase().equals("q")) quitGame();
        else if (mode.toLowerCase().equals("a")) PlayComputer();


    }

    private static void login () {
        if (loggedPlayerHuman !=null) {
            System.out.println("You are already logged in as "+ loggedPlayerHuman.getName()+"!\nPlease log out first.");
            return;
        }



        String playerName=null;
        String playerPassword=null;

        //if (!loggedPlayerHuman.equals(playerName) && !loggedPassword.equals(playerPassword) ){

        System.out.print("\nUser name: ");
        if (in.hasNext()) playerName=in.next().toUpperCase();

        System.out.print("Password: ");
        if (in.hasNext()) playerPassword=in.next().toUpperCase();

        int index = PlayerHumanFileHandler.PlayerSearch(playerName);
        int indexP = PasswordFileHandler.searchPassword(playerPassword);

        if (index>=0 && indexP>=0) {
            loggedPlayerHuman = PlayerHumanFileHandler.getPlayer(index);
            loggedPassword=PasswordFileHandler.getPassword(indexP);
        } else {
            PlayerHumanFileHandler.PlayerAdd(playerName);
            index = PlayerHumanFileHandler.PlayerSearch(playerName);
            loggedPlayerHuman = PlayerHumanFileHandler.getPlayer(index);

            PasswordFileHandler.addPassword(playerPassword);
            indexP = PasswordFileHandler.searchPassword(playerPassword);
            loggedPassword=PasswordFileHandler.getPassword(indexP);
        }
    }


    private static void newGame() {
        if (loggedPlayerHuman ==null) {
            System.out.println("\nLog in to start a new game!");
            return;
        }

        if (QuestionFileHandler.getTotalQuestionNumber()<roundSize*numberOfRounds) {
            System.out.println("\nThe questions haven't been loaded yet. Please Load the Question by clicking L ");
            return;
        }

        if (GameFileHandler.getSingleGame(loggedPlayerHuman)!=null) {
            activeGame = GameFileHandler.getSingleGame(loggedPlayerHuman);
            activeGame.addOpponent(loggedPlayerHuman);
            System.out.println("\nYou were added to a game with " + activeGame.getPlayerHuman1().getName() + " as opponent!");
        } else {
            activeGame = new Game(loggedPlayerHuman, roundSize, numberOfRounds);
            GameFileHandler.addGame(activeGame);
            System.out.println("\nA new game has been created!");
        }

        GameFileHandler.saveGame();
        game();

    }

    private static void continueGame() {
        if (loggedPlayerHuman ==null) {
            System.out.println("\nLog in to continue a game!");
            return;
        }
        activeGame = chooseOngoingPlayerGame();
        if (activeGame ==null) return;

        GameFileHandler.saveGame();
        game();
    }

    private static void displayStatistics () {
        if (loggedPlayerHuman ==null) {
            System.out.println("\nLog in to display your games!");
            return;
        }

        ArrayList<Game> ongoingPlayerGames = GameFileHandler.getOngoingPlayerGame(loggedPlayerHuman);
        ArrayList<Game> finishedPlayerGames = GameFileHandler.getFinishedPlayerGame(loggedPlayerHuman);

        System.out.println("\nFINISHED GAMES:");
        if (finishedPlayerGames.size()==0) System.out.println("none");
        for (int i = 0; i< finishedPlayerGames.size(); i++) {
            Game tempGame = finishedPlayerGames.get(i);

            if (loggedPlayerHuman.equals(tempGame.getWinner())) {
                System.out.println("<> Game against " + tempGame.getOpponentName(loggedPlayerHuman) + ".\t You won " + tempGame.getNumberOfCorrectQuestions(loggedPlayerHuman) + ":" + tempGame.getNumberOfCorrectQuestions(tempGame.getOpponent(loggedPlayerHuman)));
            } else {
                System.out.println("<> Game against " + tempGame.getOpponentName(loggedPlayerHuman) + ".\t You lost " + tempGame.getNumberOfCorrectQuestions(loggedPlayerHuman) + ":" + tempGame.getNumberOfCorrectQuestions(tempGame.getOpponent(loggedPlayerHuman)));
            }
        }

        System.out.println("\nONGOING GAMESS:");
        if (ongoingPlayerGames.size()==0) System.out.println("none");
        for (int i = 0; i< ongoingPlayerGames.size(); i++) {
            Game tempGame = ongoingPlayerGames.get(i);
            String firstPhrase=null;
            String score=null;
            if (tempGame.getOpponent(loggedPlayerHuman)==null) {
                firstPhrase = "<> Game without opponent yet.";
                score = "Your current score is " + tempGame.getNumberOfCorrectQuestions(loggedPlayerHuman) + ":0";
            } else {
                firstPhrase = "<> Game against " + tempGame.getOpponentName(loggedPlayerHuman) + ".\t";
                score = "Your current score is " + tempGame.getNumberOfCorrectQuestions(loggedPlayerHuman) + ":" + tempGame.getNumberOfCorrectQuestions(tempGame.getOpponent(loggedPlayerHuman));
            }

            if (tempGame.getCurrentQuestionNumber(loggedPlayerHuman)> tempGame.getTotalQuestionNumber()) {
                System.out.println(firstPhrase + "\tYou finished this game. Waiting for " + tempGame.getOpponentName(loggedPlayerHuman) + ".\t" + score);
            } else {
                System.out.println(firstPhrase + "\tNext question: " + tempGame.getCurrentQuestionNumber(loggedPlayerHuman) + "/" + tempGame.getTotalQuestionNumber() + ".\t" + score);
            }
        }
        System.out.println();
    }








    public static void PlayComputer(){

            int chooseProbability=0;


            boolean bone=false;

            while(!bone){

                try{
                    while (chooseProbability < 1 || chooseProbability> 3) {


                        System.out.println("Choose the probability of Computer getting it right\n");

                        System.out.println("1: 100%\t2: 67%\t3: 33%\n");

                        System.out.print("Enter Selection Probability:");

                        chooseProbability=scan.nextInt();
                    }
                    bone=true;
                }catch(InputMismatchException e){
                    scan.nextLine();
                    System.out.println("Not a correctly written whole number.\n");
                    System.out.println("Try again.\n");
                }

            }


            if (chooseProbability==1) {

        Question1();
        Question1100c();
        Question3();
        Question3100c();
        Question4();
        Question4100c();
        Question5();
        Question5100c();
        Question6();
        Question6100c();
        Question7();
        Question7100c();
        leaderboard();
        Restart();

    }

            if (chooseProbability==2) {
        Question1();
        Question166c();
        Question3();
        Question366c();
        Question4();
        Question466c();
        Question5();
        Question566c();
        Question6();
        Question666c();
        Question7();
        Question766c();
        leaderboard();
        Restart();
    }

            if (chooseProbability==3) {
        Question1();
        Question133c();
        Question3();
        Question333c();
        Question4();
        Question433c();
        Question5();
        Question533c();
        Question6();
        Question633c();
        Question7();
        Question733c();
        leaderboard();
        Restart();

    }
            }




    private static void game() {
        boolean finished=false;

        if (!activeGame.isAnswerAllowed(loggedPlayerHuman)) {
            System.out.println("\nYou cannot continue this game right now. You have to wait for your opponent to answer.");
            return;
        }

        System.out.println("\nWelcome to your game, " + loggedPlayerHuman.getName());
        System.out.println("\nEnter the letter of your answer or Q to quit and save the game");

        while (!finished) {
            if (!activeGame.isAnswerAllowed(loggedPlayerHuman)) {
                System.out.println("\nYou cannot continue this game right now. You either have to wait for your opponent to answer or the game is finished.");
                return;
            }

            System.out.println("\n____________________________________________________________________");
            System.out.println("Question " + activeGame.getCurrentQuestionNumber(loggedPlayerHuman) + ", Round " + activeGame.getCurrentRoundNumber(loggedPlayerHuman));
            System.out.println("Your opponent: " + activeGame.getOpponentName(loggedPlayerHuman));
            System.out.println("");
            Question question = activeGame.getCurrentQuestion(loggedPlayerHuman);
            String questionText = activeGame.getCurrentQuestion(loggedPlayerHuman).format();
            Answer answerA = question.getAnswers().get(0);
            Answer answerB = question.getAnswers().get(1);
            Answer answerC = question.getAnswers().get(2);
            Answer answerD = question.getAnswers().get(3);
            System.out.println(questionText);
            System.out.println("\n\n P: Cheat \t\t S: Skip Question");
            System.out.println("\n____________________________________________________________________");

            in.nextLine();
            String input="";
            if (in.hasNext()) {
                input = in.next();
            }

            if (!input.toLowerCase().matches("[abcdps]")) {
                System.out.println("Invalid input!");
                continue;

            } else if (input.toLowerCase().matches("[abcdps]")) {
                switch (input.toLowerCase()) {
                    case "a":
                        activeGame.answer(loggedPlayerHuman, answerA);
                        if (answerA.isCorrectchoice()) System.out.println("You answered correctly!");
                        else System.out.println("Your answer is wrong.");
                        System.out.println("Correct answer: "+ question.getCorrectAnswer().format());
                        break;
                    case "b":
                        activeGame.answer(loggedPlayerHuman, answerB);
                        if (answerB.isCorrectchoice()) System.out.println("You answered correctly!");
                        else System.out.println("Your answer is wrong.");
                        System.out.println("Correct answer: "+ question.getCorrectAnswer().format());
                        break;
                    case "c":
                        activeGame.answer(loggedPlayerHuman, answerC);
                        if (answerC.isCorrectchoice()) System.out.println("You answered correctly!");
                        else System.out.println("Your answer is wrong.");
                        System.out.println("Correct answer: "+ question.getCorrectAnswer().format());
                        break;
                    case "d":
                        activeGame.answer(loggedPlayerHuman, answerD);
                        if (answerD.isCorrectchoice()) System.out.println("You answered correctly!");
                        else System.out.println("Your answer is wrong.");
                        System.out.println("Correct answer: "+ question.getCorrectAnswer().format());
                        break;
                    case "p":
                        activeGame.cheat(loggedPlayerHuman);
                        System.out.println("The correct answer is: "+ question.getCorrectAnswer().format());
                        break;
                    case "s":
                        activeGame.skip(loggedPlayerHuman);
                        System.out.println("You have skipped");
                        break;
                    default:
                        System.out.println("An unknown error occured. Please try again!");
                        return;
                }


            } else finished = true;

            GameFileHandler.saveGame();
        }
    }

    private static Game chooseOngoingPlayerGame() {
        ArrayList<Game> ongoingPlayerGames = GameFileHandler.getOngoingPlayerGame(loggedPlayerHuman);

        if (ongoingPlayerGames.size()<1) {
            System.out.println("You have no active games, please start a new one!");
            return null;
        }

        System.out.println("\nEnter the number of game to be continued. Number in brackets () -> game cannot be continued right now.");
        for (int i = 0; i< ongoingPlayerGames.size(); i++) {
            Game tempGame = ongoingPlayerGames.get(i);
            String firstPhrase=null;
            if (tempGame.getOpponent(loggedPlayerHuman)==null) firstPhrase = "Game without opponent yet.";
            else firstPhrase = "Game against " + tempGame.getOpponentName(loggedPlayerHuman) + ".";

            if (tempGame.isAnswerAllowed(loggedPlayerHuman))	System.out.println(i+1 + ": " + firstPhrase + "\tRound " + tempGame.getCurrentRoundNumber(loggedPlayerHuman) + ", Question: " + tempGame.getCurrentQuestionNumber(loggedPlayerHuman));
            else System.out.println("(" + (i+1) + "): "+ firstPhrase +"\tRound " + tempGame.getCurrentRoundNumber(loggedPlayerHuman) + ", Question: " + tempGame.getCurrentQuestionNumber(loggedPlayerHuman) + "/" + tempGame.getTotalQuestionNumber() + ".");
        }

        in.nextLine();
        String input = "";
        if (in.hasNext()) {
            input = in.next();
        }
        if (!input.matches("\\d+") || Integer.parseInt(input)> ongoingPlayerGames.size()) {
            System.out.println("Invalid input!");
            return null;
        } else if (!ongoingPlayerGames.get(Integer.parseInt(input)-1).isAnswerAllowed(loggedPlayerHuman)) {
            System.out.println("You cannot continue this game right now. Please wait for your opponent to play.");
            return null;
        } else {
            return ongoingPlayerGames.get(Integer.parseInt(input)-1);
        }
    }

    private static void createQuestion () {

        String question = "What is the capital of Malaysia?";
        Answer correctAnswer = new Answer("Kuala Lumpur",true);
        Answer wrongAnswer1 = new Answer("Bogota",false);
        Answer wrongAnswer2 = new Answer ("Niarobi",false);
        Answer wrongAnswer3 = new Answer ("Geneva",false);


        ArrayList<Answer> answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);


        question = "What is the capital of Unites States of America?";
        correctAnswer = new Answer("Washington",true);
        wrongAnswer1 = new Answer("New York",false);
        wrongAnswer2 = new Answer ("London",false);
        wrongAnswer3 = new Answer ("Dallas",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Kenya?";
        correctAnswer = new Answer("Nairobi",true);
        wrongAnswer1 = new Answer("Kampala",false);
        wrongAnswer2 = new Answer ("Cape Town",false);
        wrongAnswer3 = new Answer ("Cairo",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Egypt?";
        correctAnswer = new Answer("Cairo",true);
        wrongAnswer1 = new Answer("Tunis",false);
        wrongAnswer2 = new Answer ("Khartoum",false);
        wrongAnswer3 = new Answer ("Shanghai",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Spain?";
        correctAnswer = new Answer("Madrid",true);
        wrongAnswer1 = new Answer("Barcelona",false);
        wrongAnswer2 = new Answer ("Lisbon",false);
        wrongAnswer3 = new Answer ("Cairo",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Macedonia?";
        correctAnswer = new Answer("Skopje",true);
        wrongAnswer1 = new Answer("Bamako",false);
        wrongAnswer2 = new Answer ("Male",false);
        wrongAnswer3 = new Answer ("Majuro",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of China?";
        correctAnswer = new Answer("Biejing",true);
        wrongAnswer1 = new Answer("Shanghai",false);
        wrongAnswer2 = new Answer ("Guanzao",false);
        wrongAnswer3 = new Answer ("Bangui",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Mozambique?";
        correctAnswer = new Answer("Maputo",true);
        wrongAnswer1 = new Answer("Nauri",false);
        wrongAnswer2 = new Answer ("Wellington",false);
        wrongAnswer3 = new Answer ("Oslo",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of United Arab Emirates?";
        correctAnswer = new Answer("Abu Dhabi",true);
        wrongAnswer1 = new Answer("Dubai",false);
        wrongAnswer2 = new Answer ("Sharjah",false);
        wrongAnswer3 = new Answer ("Muscat",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Australia?";
        correctAnswer = new Answer("Canberra",true);
        wrongAnswer1 = new Answer("Sydney",false);
        wrongAnswer2 = new Answer ("Oslo",false);
        wrongAnswer3 = new Answer ("Amesterdam",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Mynamar(Burma)?";
        correctAnswer = new Answer("Naypyidaw",true);
        wrongAnswer1 = new Answer("Tirana",false);
        wrongAnswer2 = new Answer ("New Delhi",false);
        wrongAnswer3 = new Answer ("Valetta",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Mozambique?";
        correctAnswer = new Answer("Maputo",true);
        wrongAnswer1 = new Answer("Nauri",false);
        wrongAnswer2 = new Answer ("Wellington",false);
        wrongAnswer3 = new Answer ("Oslo",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Bulgaria?";
        correctAnswer = new Answer("Sofia",true);
        wrongAnswer1 = new Answer("Bogota",false);
        wrongAnswer2 = new Answer ("Santiago",false);
        wrongAnswer3 = new Answer ("Moroni",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Bangladesh?";
        correctAnswer = new Answer("Dhaka",true);
        wrongAnswer1 = new Answer("Mumbai",false);
        wrongAnswer2 = new Answer ("New Dhelhi",false);
        wrongAnswer3 = new Answer ("Ottowa",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Denmark?";
        correctAnswer = new Answer("Copenhagen",true);
        wrongAnswer1 = new Answer("Berlin",false);
        wrongAnswer2 = new Answer ("Helsinki",false);
        wrongAnswer3 = new Answer ("Tallinn",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Serbia?";
        correctAnswer = new Answer("Belgrade",true);
        wrongAnswer1 = new Answer("Apia",false);
        wrongAnswer2 = new Answer ("Paramaribo",false);
        wrongAnswer3 = new Answer ("Kilinov",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Sierra Leone?";
        correctAnswer = new Answer("Freetown",true);
        wrongAnswer1 = new Answer("Ljubljana",false);
        wrongAnswer2 = new Answer ("Honiara",false);
        wrongAnswer3 = new Answer ("Mbabane",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of South Korea?";
        correctAnswer = new Answer("Seoul",true);
        wrongAnswer1 = new Answer("Juba",false);
        wrongAnswer2 = new Answer ("Pyongyang",false);
        wrongAnswer3 = new Answer ("Managua",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Pakistan?";
        correctAnswer = new Answer("Islamabad",true);
        wrongAnswer1 = new Answer("Manilla",false);
        wrongAnswer2 = new Answer ("Karachi",false);
        wrongAnswer3 = new Answer ("Warsaw",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Qatar?";
        correctAnswer = new Answer("Doha",true);
        wrongAnswer1 = new Answer("Riyadh",false);
        wrongAnswer2 = new Answer ("Ramallah",false);
        wrongAnswer3 = new Answer ("Lima",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Palestine?";
        correctAnswer = new Answer("Jerusalem",true);
        wrongAnswer1 = new Answer("",false);
        wrongAnswer2 = new Answer ("Santiago",false);
        wrongAnswer3 = new Answer ("Moroni",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Nigeria?";
        correctAnswer = new Answer("Abuja",true);
        wrongAnswer1 = new Answer("Lagos",false);
        wrongAnswer2 = new Answer ("Accra",false);
        wrongAnswer3 = new Answer ("Ogadoudou",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Greece?";
        correctAnswer = new Answer("Athens",true);
        wrongAnswer1 = new Answer("Conokry",false);
        wrongAnswer2 = new Answer ("Budapest",false);
        wrongAnswer3 = new Answer ("Moroni",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Togo?";
        correctAnswer = new Answer("Lome",true);
        wrongAnswer1 = new Answer("Ankara",false);
        wrongAnswer2 = new Answer ("Ashgabat",false);
        wrongAnswer3 = new Answer ("Dili",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Lesotho?";
        correctAnswer = new Answer("Maseru",true);
        wrongAnswer1 = new Answer("Mateo",false);
        wrongAnswer2 = new Answer ("Monrovia",false);
        wrongAnswer3 = new Answer ("Dublin",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Iceland?";
        correctAnswer = new Answer("Reykjavik",true);
        wrongAnswer1 = new Answer("Tegucigalpa",false);
        wrongAnswer2 = new Answer ("Belmopan",false);
        wrongAnswer3 = new Answer ("Porto-Novo",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Peru?";
        correctAnswer = new Answer("Lima",true);
        wrongAnswer1 = new Answer("Niamey",false);
        wrongAnswer2 = new Answer ("Windhoek",false);
        wrongAnswer3 = new Answer ("Kathmandu",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Macedonia?";
        correctAnswer = new Answer("Skopje",true);
        wrongAnswer1 = new Answer("Antananarivo",false);
        wrongAnswer2 = new Answer ("Lilinov",false);
        wrongAnswer3 = new Answer ("Nouakchott",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Belize?";
        correctAnswer = new Answer("Belmopan",true);
        wrongAnswer1 = new Answer("Thimphu",false);
        wrongAnswer2 = new Answer ("Sarajjov",false);
        wrongAnswer3 = new Answer ("Podgorica",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Laos?";
        correctAnswer = new Answer("Vientiane",true);
        wrongAnswer1 = new Answer("Riga",false);
        wrongAnswer2 = new Answer ("Vaduz",false);
        wrongAnswer3 = new Answer ("Hanoi",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Venezuela?";
        correctAnswer = new Answer("Caracas",true);
        wrongAnswer1 = new Answer("Lome",false);
        wrongAnswer2 = new Answer ("Mbabane",false);
        wrongAnswer3 = new Answer ("Paramaribo",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Swaziland?";
        correctAnswer = new Answer("Bern",true);
        wrongAnswer1 = new Answer("Niamb",false);
        wrongAnswer2 = new Answer ("Windhoek",false);
        wrongAnswer3 = new Answer ("Kathmandu",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);

        question = "What is the capital of Switzerland?";
        correctAnswer = new Answer("Bern",true);
        wrongAnswer1 = new Answer("Geneva",false);
        wrongAnswer2 = new Answer ("Tehran",false);
        wrongAnswer3 = new Answer ("Amman",false);

        question = "What is the capital of Honduras ?";
        correctAnswer = new Answer("Tegucigalpa",true);
        wrongAnswer1 = new Answer("Bissau",false);
        wrongAnswer2 = new Answer ("Asmara",false);
        wrongAnswer3 = new Answer ("Malabu",false);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);


        answers = new ArrayList<Answer>();
        answers.add(correctAnswer);
        answers.add(wrongAnswer1);
        answers.add(wrongAnswer2);
        answers.add(wrongAnswer3);

        QuestionFileHandler.QuestionAdd(question, answers);




    }


    private static void logout() {
        if (loggedPlayerHuman ==null) {
            System.out.println("You are not logged in, hence you cannot log out!");
        } else {
            loggedPlayerHuman =null;
            System.out.println("You are now logged out. Have a nice day!");
        }
    }

    private static void quitGame() {
        playing=false;
    }

    public static void Question1() {

        System.out.println("Enter the full name of your choice as the answer");
        System.out.println("Enter 'cheat' to show the answer or 'skip' to skip to question");
        System.out.println("\n____________________________________________________________________");
        System.out.println("Question 1:");

        System.out.println("Your Turn:\n");


        System.out.println("What is the Capital of Malaysia?");
        System.out.println("Kuala Lumpur");
        System.out.println("Selangor");
        System.out.println("Johor Bahru\n");

        System.out.print("Your answer: ");


        iny = scan.nextLine(); iny = scan.nextLine();

        if(iny.equalsIgnoreCase("Kuala Lumpur"))
        {
            System.out.println("Correct\n");
            c++;
            System.out.println("You score "+c+" points.\n");


        }
        if(iny.equalsIgnoreCase("cheat")) {
            System.out.print("The correct answer is Kuala Lumpur. \n");
        }
        if(iny.equalsIgnoreCase("skip")) {
            System.out.print("You skipped the question \n");
        }
        else
        {

            System.out.println("Wrong\n");

        }
    }



    public static void Question1100c() {

        System.out.println("Computer Turn:\n ");


        System.out.println("Computer answer: ");


        iny = QuestionRandomGen1100();

        if(iny.equalsIgnoreCase("Kuala Lumpur"))
        {

            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");

        }
        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }
    }

    public static void Question166c() {

        System.out.println("Computer Turn:\n ");


        System.out.println("Computer answer: ");


        iny = QuestionRandomGen166();

        if(iny.equalsIgnoreCase("Kuala Lumpur"))
        {

            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");

        }
        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }
    }

    public static void Question133c() {

        System.out.println("Computer Turn:\n ");


        System.out.println("Computer answer: ");


        iny = QuestionRandomGen133();

        if(iny.equalsIgnoreCase("Kuala Lumpur"))
        {

            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");

        }
        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }
    }




    public static void Question3() {
        System.out.println("Enter the full name of your choice as the answer");
        System.out.println("Enter 'cheat' to show the answer or 'skip' to skip to question");
        System.out.println("\n____________________________________________________________________");
        System.out.println("Question 2:");

        System.out.println("Your Turn:\n");


        System.out.println("What is the capital of China?");
        System.out.println("Shanghai");
        System.out.println("Guangzhou");
        System.out.println("Beijing\n");

        System.out.println("Your answer: ");

        iny = scan.nextLine();

        if (iny.equalsIgnoreCase("Beijing")) {
            System.out.println("Correct\n");
            c++;
            System.out.println("You score "+c+" points.\n");

        }
        if(iny.equalsIgnoreCase("cheat")) {
            System.out.print("The correct answer is Beijing. \n");
        }
        if(iny.equalsIgnoreCase("skip")) {
            System.out.print("You skipped the question \n");
        }
        else {
            System.out.println("Wrong\n");

        }
    }

    public static void Question3100c() {

        System.out.println("Computer Turn:\n");


        System.out.println("Computer answer: ");

        iny = QuestionRandomGen3100();

        if (iny.equalsIgnoreCase("Beijing")) {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        } else {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }
    }

    public static void Question366c() {

        System.out.println("Computer Turn:\n");


        System.out.println("Computer answer: ");

        iny = QuestionRandomGen366();

        if (iny.equalsIgnoreCase("Beijing")) {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        } else {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }
    }

    public static void Question333c() {

        System.out.println("Computer Turn:\n");


        System.out.println("Computer answer: ");

        iny = QuestionRandomGen333();

        if (iny.equalsIgnoreCase("Beijing")) {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        } else {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }
    }

    public static void Question4(){
        System.out.println("Enter the full name of your choice as the answer");
        System.out.println("Enter 'cheat' to show the answer or 'skip' to skip to question");
        System.out.println("\n____________________________________________________________________");
        System.out.println("Question 3:");

        System.out.println("Your Turn:\n");

        System.out.println("What is the Capital of the England?");
        System.out.println("Brighton");
        System.out.println("London");
        System.out.println("Edinburgh\n");

        System.out.print("Your answer: ");

        iny = scan.nextLine();

        if (iny.equalsIgnoreCase("London")) {
            System.out.println("Correct\n");
            c++;
            System.out.println("You score "+c+" points.\n");

        }
        if(iny.equalsIgnoreCase("cheat")) {
            System.out.print("The correct answer is London. \n");
        }
        if(iny.equalsIgnoreCase("skip")) {
            System.out.print("You skipped the question \n");
        }
        else {
            System.out.print(iny);
            System.out.println("Wrong\n");

        }


    }

    public static void Question4100c(){

        System.out.println("Computer Turn:\n");


        System.out.print("Computer answer: ");

        iny = QuestionRandomGen4100();

        if (iny.equalsIgnoreCase("London")) {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        } else {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }


    }

    public static void Question466c(){

        System.out.println("Computer Turn:\n");


        System.out.print("Computer answer: ");

        iny = QuestionRandomGen466();

        if (iny.equalsIgnoreCase("London")) {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        } else {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }


    }

    public static void Question433c(){

        System.out.println("Computer Turn:\n");


        System.out.print("Computer answer: ");

        iny = QuestionRandomGen433();

        if (iny.equalsIgnoreCase("London")) {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        } else {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }


    }

    public static void Question5(){
        System.out.println("Enter the full name of your choice as the answer");
        System.out.println("Enter 'cheat' to show the answer or 'skip' to skip to question");
        System.out.println("\n____________________________________________________________________");
        System.out.println("Question 4:");

        System.out.println("Your Turn:\n");

        System.out.println("What is the Capital of Australia?");
        System.out.println("Canberra");
        System.out.println("Victoria");
        System.out.println("Melbourne\n");

        System.out.print("Your answer: ");

        iny = scan.nextLine();

        if(iny.equalsIgnoreCase("Canberra"))
        {
            System.out.println("Correct\n");
            c++;
            System.out.println("You score "+c+" points.\n");

        }
        if(iny.equalsIgnoreCase("cheat")) {
            System.out.print("The correct answer is Canberra. \n");
        }
        if(iny.equalsIgnoreCase("skip")) {
            System.out.print("You skipped the question \n");
        }
        else
        {
            System.out.println("Wrong\n");

        }
    }

    public static void Question5100c(){

        System.out.println("Computer Turn:\n");



        System.out.print("Computer answer: ");

        iny = QuestionRandomGen5100();

        if(iny.equalsIgnoreCase("Canberra"))
        {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        }
        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }
    }

    public static void Question566c(){

        System.out.println("Computer Turn:\n");



        System.out.print("Computer answer: ");

        iny = QuestionRandomGen566();

        if(iny.equalsIgnoreCase("Canberra"))
        {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        }
        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }
    }

    public static void Question533c(){

        System.out.println("Computer Turn:\n");



        System.out.print("Computer answer: ");

        iny = QuestionRandomGen533();

        if(iny.equalsIgnoreCase("Canberra"))
        {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        }
        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");

        }
    }

    public static void Question6(){
        System.out.println("Enter the full name of your choice as the answer");
        System.out.println("Enter 'cheat' to show the answer or 'skip' to skip to question");
        System.out.println("\n____________________________________________________________________");
        System.out.println("Question 5:");

        System.out.println("Your Turn:\n");

        System.out.println("What is the Capital of United States?");
        System.out.println("California");
        System.out.println("Texas");
        System.out.println("Washington\n");

        System.out.print("Your answer: ");

        iny = scan.nextLine();

        if(iny.equalsIgnoreCase("Washington"))
        {
            System.out.println("Correct\n");
            c++;
            System.out.println("You score "+c+" points.\n ");

        }
        if(iny.equalsIgnoreCase("cheat")) {
            System.out.print("The correct answer is Washington. \n");
        }
        if(iny.equalsIgnoreCase("skip")) {
            System.out.print("You skipped the question \n");
        }
        else
        {
            System.out.println("Wrong\n");

        }

    }

    public static void Question6100c(){

        System.out.println("Computer Turn:\n");



        System.out.print("Computer answer: ");

        iny = QuestionRandomGen6100();;

        if(iny.equalsIgnoreCase("Washington"))
        {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        }

        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");
        }

    }

    public static void Question666c(){

        System.out.println("Computer Turn:\n");



        System.out.print("Computer answer: ");

        iny = QuestionRandomGen666();;

        if(iny.equalsIgnoreCase("Washington"))
        {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        }

        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");
        }

    }

    public static void Question633c(){

        System.out.println("Computer Turn:\n");



        System.out.print("Computer answer: ");

        iny = QuestionRandomGen633();;

        if(iny.equalsIgnoreCase("Washington"))
        {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        }

        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");
        }

    }

    public static void Question7(){
        System.out.println("Enter the full name of your choice as the answer");
        System.out.println("Enter 'cheat' to show the answer or 'skip' to skip to question");
        System.out.println("\n____________________________________________________________________");
        System.out.println("Question 6:");

        System.out.println("Your Turn:\n");

        System.out.println("What is the Capital of United Arab Emirates?");
        System.out.println("Doha");
        System.out.println("Dubai");
        System.out.println("Abu Dhabi\n");

        System.out.print("Your answer: ");

        iny = scan.nextLine();

        if(iny.equalsIgnoreCase("Abu Dhabi"))
        {
            System.out.println("Correct\n");
            c++;
            System.out.println("You score "+c+" points.\n");

        }
        if(iny.equalsIgnoreCase("cheat")) {
            System.out.print("The correct answer is Abu Dhabi. \n");
        }
        if(iny.equalsIgnoreCase("skip")) {
            System.out.print("You skipped the question \n");
        }
        else
        {
            System.out.println("Wrong\n");

        }






    }

    public static void Question7100c(){

        System.out.println("Computer Turn:\n");


        System.out.print("Computer answer: ");

        iny = QuestionRandomGen7100();;

        if(iny.equalsIgnoreCase("Abu Dhabi"))
        {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        }
        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");
        }

        System.out.println("You score " + c + " out of 6");
        System.out.println("Computer score " + q + " out of 6");
        if(c>q){
            System.out.println("Congratulation! You won!");
        }
        else{
            System.out.println("Sorry, you lost! Try again next time.");
        }



    }

    public static void Question766c(){

        System.out.println("Computer Turn:\n");


        System.out.print("Computer answer: ");

        iny = QuestionRandomGen766();;

        if(iny.equalsIgnoreCase("Abu Dhabi"))
        {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        }
        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");
        }

        System.out.println("You score " + c + " out of 6");
        System.out.println("Computer score " + q + " out of 6");
        if(c>q){
            System.out.println("Congratulation! You won!");
        }
        else{
            System.out.println("Sorry, you lost! Try again next time.");
        }



    }

    public static void Question733c(){

        System.out.println("Computer Turn:\n");


        System.out.print("Computer answer: ");

        iny = QuestionRandomGen733();;

        if(iny.equalsIgnoreCase("Abu Dhabi"))
        {
            System.out.println(iny);
            System.out.println("Correct\n");
            q++;
            System.out.println("Computer score "+q+" points.\n");
        }
        else
        {
            System.out.println(iny);
            System.out.println("Wrong\n");
        }

        System.out.println("You score " + c + " out of 6");
        System.out.println("Computer score " + q + " out of 6");
        if(c>q){
            System.out.println("Congratulation! You won!");
        }
        else{
            System.out.println("Sorry, you lost! Try again next time.");
        }



    }

    public static String QuestionRandomGen1100(){


        Random rn1 = new Random();


        int Random1 = 1;

        switch(Random1){
            case 1:
                return choiceanswer="Kuala Lumpur";
            case 2:
                return choiceanswer= "Montreal";
            case 3:
                return choiceanswer = "Johor Bahru";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen166(){


        Random rn1 = new Random();


        int Random1 = rn1.nextInt(2 - 1 + 1) + 1;

        switch(Random1){
            case 1:
                return choiceanswer="Kuala Lumpur";
            case 2:
                return choiceanswer= "Montreal";
            case 3:
                return choiceanswer = "Johor Bahru";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen133(){


        Random rn1 = new Random();


        int Random1 = rn1.nextInt(3 - 1 + 1) + 1;

        switch(Random1){
            case 1:
                return choiceanswer="Kuala Lumpur";
            case 2:
                return choiceanswer= "Montreal";
            case 3:
                return choiceanswer = "Johor Bahru";

        }

        return choiceanswer;

    }


    public static String QuestionRandomGen366(){


        Random rn1 = new Random();


        int Random1 = rn1.nextInt(2 - 1 + 1) + 1;

        switch(Random1){
            case 1:

                return choiceanswer="Beijing";
            case 2:

                return choiceanswer= "Shanghai";
            case 3:

                return choiceanswer = "Guanzhao";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen3100(){


        Random rn1 = new Random();


        int Random1 = 1;

        switch(Random1){
            case 1:

                return choiceanswer="Beijing";
            case 2:

                return choiceanswer= "Shanghai";
            case 3:

                return choiceanswer = "Guanzhao";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen333(){


        Random rn1 = new Random();


        int Random1 = rn1.nextInt(3 - 1 + 1) + 1;

        switch(Random1){
            case 1:

                return choiceanswer="Beijing";
            case 2:

                return choiceanswer= "Shanghai";
            case 3:

                return choiceanswer = "Guanzhao";

        }

        return choiceanswer;

    }



    public static String QuestionRandomGen4100(){


        Random rn1 = new Random();


        int Random1 = 1;

        switch(Random1){
            case 1:


                return choiceanswer="London";
            case 2:


                return choiceanswer= "Geneva";
            case 3:

                return choiceanswer = "Brighton";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen466(){


        Random rn1 = new Random();




        int Random1 = rn1.nextInt(2 - 1 + 1) + 1;

        switch(Random1){
            case 1:


                return choiceanswer="London";
            case 2:


                return choiceanswer= "Geneva";
            case 3:

                return choiceanswer = "Brighton";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen433(){


        Random rn1 = new Random();




        int Random1 = rn1.nextInt(3 - 1 + 1) + 1;

        switch(Random1){
            case 1:


                return choiceanswer="London";
            case 2:


                return choiceanswer= "Geneva";
            case 3:

                return choiceanswer = "Brighton";

        }

        return choiceanswer;

    }


    public static String QuestionRandomGen5100(){


        Random rn1 = new Random();


        int Random1 = 1;

        switch(Random1){
            case 1:

                return choiceanswer="Canberra";
            case 2:

                return choiceanswer= "Singapore";
            case 3:

                return choiceanswer = "Victoria";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen566(){


        Random rn1 = new Random();


        int Random1 = rn1.nextInt(2 - 1 + 1) + 1;

        switch(Random1){
            case 1:

                return choiceanswer="Canberra";
            case 2:

                return choiceanswer= "Singapore";
            case 3:

                return choiceanswer = "Victoria";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen533(){


        Random rn1 = new Random();


        int Random1 = rn1.nextInt(3 - 1 + 1) + 1;

        switch(Random1){
            case 1:

                return choiceanswer="Canberra";
            case 2:

                return choiceanswer= "Singapore";
            case 3:

                return choiceanswer = "Victoria";

        }

        return choiceanswer;

    }



    public static String QuestionRandomGen6100(){


        Random rn1 = new Random();


        int Random1 = 1;

        switch(Random1){
            case 1:

                return choiceanswer="Washington";
            case 2:

                return choiceanswer= "Paris";
            case 3:

                return choiceanswer = "Berlin";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen666(){


        Random rn1 = new Random();


        int Random1 = rn1.nextInt(2 - 1 + 1) + 1;

        switch(Random1){
            case 1:

                return choiceanswer="Washington";
            case 2:

                return choiceanswer= "Paris";
            case 3:

                return choiceanswer = "Berlin";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen633(){


        Random rn1 = new Random();


        int Random1 = rn1.nextInt(3 - 1 + 1) + 1;

        switch(Random1){
            case 1:

                return choiceanswer="Washington";
            case 2:

                return choiceanswer= "Paris";
            case 3:

                return choiceanswer = "Berlin";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen7100(){


        Random rn1 = new Random();


        int Random1 = 1;

        switch(Random1){
            case 1:

                return choiceanswer="Abu Dhabi";
            case 2:

                return choiceanswer= "Doha";
            case 3:

                return choiceanswer = "Muscat";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen766(){


        Random rn1 = new Random();


        int Random1 = rn1.nextInt(2 - 1 + 1) + 1;

        switch(Random1){
            case 1:

                return choiceanswer="Abu Dhabi";
            case 2:

                return choiceanswer= "Doha";
            case 3:

                return choiceanswer = "Muscat";

        }

        return choiceanswer;

    }

    public static String QuestionRandomGen733(){


        Random rn1 = new Random();


        int Random1 = rn1.nextInt(3 - 1 + 1) + 1;

        switch(Random1){
            case 1:

                return choiceanswer="Abu Dhabi";
            case 2:

                return choiceanswer= "Doha";
            case 3:

                return choiceanswer = "Muscat";

        }

        return choiceanswer;

    }

    private static void leaderboard() {


        int scoreIndex = 0; // Index to store the score in array
        ArrayList<Integer> highScores = new ArrayList<Integer>();
        String fileName = "scores.txt";
        String line = null;


        // Write current score and read scores from text file
        try {
            // Set append to true to write score to next line.
            FileWriter fileWriter = new FileWriter(fileName,true);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            bufferedWriter.write(Integer.toString(c));
            bufferedWriter.newLine();

            // Always close files.
            bufferedWriter.close();

            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                // Store the scores in an array list
                highScores.add(Integer.parseInt(line));
            }

            Collections.sort(highScores);
            Collections.reverse(highScores);
            // Always close files.
            bufferedReader.close();
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '" + fileName + "'");
        }

        System.out.println("-------Your top highest scores!--------");
        for(int i = 0; i < highScores.size(); i++ ){
            // break out of loop if count reaches 5, we only want the top 5 scores!
            if(i == 5){
                break;
            }
            System.out.print("High Score " + (i+1) + " : " + highScores.get(i)+ ", \n");
        }

    }

    private static void Restart(){
        System.out.println("Do you wanna restart or quit the quiz?");
        System.out.println("Enter r to restart or q to quit.");
        restart = scan.nextLine();
        if(restart.equalsIgnoreCase("r")){


            System.out.println("Choose the probability of Computer getting it right\n");

            System.out.println("1: 100%\t2: 67%\t3: 33%\n");

            System.out.print("Enter Selection:");

            int chooseProbability=scan.nextInt();

            if (chooseProbability==1) {

                Question1();
                Question1100c();
                Question3();
                Question3100c();
                Question4();
                Question4100c();
                Question5();
                Question5100c();
                Question6();
                Question6100c();
                Question7();
                Question7100c();
                leaderboard();
                Restart();

            }

            if (chooseProbability==2) {
                Question1();
                Question166c();
                Question3();
                Question366c();
                Question4();
                Question466c();
                Question5();
                Question566c();
                Question6();
                Question666c();
                Question7();
                Question766c();
                leaderboard();
                Restart();
            }

            if (chooseProbability==3) {
                Question1();
                Question133c();
                Question3();
                Question333c();
                Question4();
                Question433c();
                Question5();
                Question533c();
                Question6();
                Question633c();
                Question7();
                Question733c();
                leaderboard();
                Restart();

            }


        }
        if(restart.equalsIgnoreCase("q")){
            System.out.println("The game has ended.");

        }
        else{
            System.out.println("Invalid output, please try again");

            Restart();


        }

    }



}
