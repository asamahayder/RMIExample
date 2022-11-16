package main.database;

import java.util.Arrays;

public class UserDTO {

    private String name;
    private String password;
    private int roleId;
    private byte[] salt;

    public UserDTO(String name, String password, int roleId, byte[] salt) {
        this.name = name;
        this.password = password;
        this.salt = salt;
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", salt=" + Arrays.toString(salt) +
                ", role=" + roleId +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public int getRole() { return roleId; }
}
