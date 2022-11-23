package database;

import java.util.Arrays;

public class UserDTO {

    private int id;
    private String name;
    private String password;
    private int roleId;
    private byte[] salt;

    public UserDTO(int id, String name, String password, int roleId, byte[] salt) {
        this.id = id;
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

    public int getId() {
        return id;
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
