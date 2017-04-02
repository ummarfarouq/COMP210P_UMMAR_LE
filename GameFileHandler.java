

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GameFileHandler {
    private static File file = new  File ("Game.dat");
    private static ArrayList<Game> allGames = new ArrayList<Game>();

    public static void loadGame() {

        if (!file.exists()) return;

        try (
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))
        ) {

            allGames = (ArrayList<Game>) inputStream.readObject();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(allGames);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addGame(Game game) {
        allGames.add(game);
        saveGame();
    }

    public static Game getSingleGame(PlayerHuman playerHuman) {
        for (int i = 0; i< allGames.size(); i++) {
            if (allGames.get(i).isSingleGame() && !allGames.get(i).getPlayerHuman1().equals(playerHuman)) {
                return allGames.get(i);
            }
        }
        return null;
    }

    public static ArrayList<Game> getOngoingPlayerGame(PlayerHuman playerHuman) {
        ArrayList<Game> ongoingPlayerGames = new ArrayList<Game>();
        for (int i = 0; i< allGames.size(); i++) {
            Game tempGame = allGames.get(i);
            if ( !tempGame.isFinished() && /* tempGame.isAnswerAllowed(playerHuman) && */ (playerHuman.equals(tempGame.getPlayerHuman1())|| (tempGame.getPlayerHuman2()!=null&& playerHuman.equals(tempGame.getPlayerHuman2())) ) ) ongoingPlayerGames.add(tempGame);
        }
        return ongoingPlayerGames;
    }

    public static ArrayList<Game> getFinishedPlayerGame(PlayerHuman playerHuman) {
        ArrayList<Game> finishedPlayerGames = new ArrayList<Game>();
        for (int i = 0; i< allGames.size(); i++) {
            Game tempGame = allGames.get(i);
            if ( tempGame.isFinished() && (playerHuman.equals(tempGame.getPlayerHuman1())|| playerHuman.equals(tempGame.getPlayerHuman2())) ) finishedPlayerGames.add(tempGame);
        }
        return finishedPlayerGames;
    }
}
