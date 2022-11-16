package main.database;

public class RoleTreeDTO {

    private int roleId;
    private int childId;

    public RoleTreeDTO(int roleId, int childId) {
        this.roleId = roleId;
        this.childId = childId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }
}
