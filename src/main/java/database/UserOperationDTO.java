package database;

public class UserOperationDTO {
    private int userId;
    private int operationId;

    public UserOperationDTO(int userId, int operationId) {
        this.userId = userId;
        this.operationId = operationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }
}
