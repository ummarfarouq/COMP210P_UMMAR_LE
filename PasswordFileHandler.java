import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class PasswordFileHandler {

    private static File file = new File("Password.dat");
    private static ArrayList<Password> passwordCatalogue = new ArrayList<Password>();

    public static void loadPasswords () {
        if (!file.exists()) return;

        try (
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))
        ) {

            passwordCatalogue = (ArrayList<Password>) inputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void addPassword (String password) {
        passwordCatalogue.add(new Password(password));
        savePasswords();
    }

    public static void savePasswords () {
        try {
            //file.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(passwordCatalogue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int searchPassword (String password) {
        for (int i=0; i<passwordCatalogue.size(); i++) {
            if (passwordCatalogue.get(i).getPassword().equals(password)) return i;
        }
        return -1;
    }

    public static Password getPassword (int indexP) {
        return passwordCatalogue.get(indexP);
    }

}

