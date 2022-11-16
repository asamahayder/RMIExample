package main.database;

public class RoleOperationDTO {
    private int roleId;
    private int operationId;

    public RoleOperationDTO(int roleId, int operationId) {
        this.roleId = roleId;
        this.operationId = operationId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

}
