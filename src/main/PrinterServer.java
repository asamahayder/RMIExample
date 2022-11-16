package main;

import main.database.Database;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PrinterServer {

    private static final int PORT = 5099;

    public static void main(String[] args) throws RemoteException {
        Database.createDatabase();
        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind("print", new PrinterServant());
    }
}
