import java.io.Serializable;

public class PlayerHuman implements Serializable {

    private String name;
    private String password;

    public PlayerHuman(String name) {
        this.name = name;
    }

    public PlayerHuman(String name, String Password) {
        this.name = name;
        this.password = name;
    }

    public String getName() {
        return this.name.toUpperCase();
    }

    public boolean equals (PlayerHuman playerHuman) {
        return playerHuman.getName().equalsIgnoreCase(this.name);
    }
}