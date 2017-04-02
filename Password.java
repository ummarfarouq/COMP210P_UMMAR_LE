import java.io.Serializable;

/**
 * Created by Farouq on 01/04/2017.
 */
import java.io.Serializable;

public class Password implements Serializable {

    private String password;

    public Password(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password.toUpperCase();
    }

    public boolean equals (Password password) {
        return password.getPassword().equalsIgnoreCase(this.password);
    }
}
