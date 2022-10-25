package database;

import java.util.Arrays;

public class UserDTO {

    private String name;
    private String password;
    private byte[] salt;

    public UserDTO(String name, String password, byte[] salt) {
        this.name = name;
        this.password = password;
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", salt=" + Arrays.toString(salt) +
                '}';
    }
}
