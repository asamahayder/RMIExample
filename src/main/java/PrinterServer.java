import database.Database;
import database.UserDTO;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PrinterServer {

    private static final int PORT = 5099;

    public static void main(String[] args) throws RemoteException {
        Database db = new Database();
        UserDTO user = db.selectUser("User8");
        System.out.println(user.toString());

        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind("print", new PrinterServant());
    }
}
