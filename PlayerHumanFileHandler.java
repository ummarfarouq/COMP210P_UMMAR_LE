import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class PlayerHumanFileHandler {

    private static File QuizFile = new File("Players.dat");
    private static ArrayList<PlayerHuman> playerHumanCatalogue = new ArrayList<PlayerHuman>();

    public static void PlayerLoad() {
        if (!QuizFile.exists()) return;

        try (
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(QuizFile))
        ) {

            playerHumanCatalogue = (ArrayList<PlayerHuman>) inputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void PlayerAdd(String name) {
        playerHumanCatalogue.add(new PlayerHuman(name));
        PlayerSave();
    }

    public static void PlayerSave() {
        try {
            //QuizFile.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(QuizFile));
            out.writeObject(playerHumanCatalogue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int PlayerSearch(String name) {
        for (int i = 0; i< playerHumanCatalogue.size(); i++) {
            if (playerHumanCatalogue.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    public static PlayerHuman getPlayer (int index) {
        return playerHumanCatalogue.get(index);
    }

}

