package main.database;

public class ConfigDTO {

    private String value;

    public ConfigDTO(String description) {
        this.value = description;
    }

    public String getDescription() {
        return value;
    }
}
