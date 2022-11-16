package main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrinterService extends Remote {

    String print(String fileName, String printer, String authObject) throws RemoteException;

    String queue(String printer, String authObject) throws RemoteException;

    String topQueue(String printer, int job, String authObject) throws RemoteException;

    String start(String authObject) throws RemoteException;

    String stop(String authObject) throws RemoteException;

    String restart(String authObject) throws RemoteException;

    String status(String printer, String authObject) throws RemoteException;

    String readConfig(String parameter, String authObject) throws RemoteException;

    String setConfig(String parameter, String value, String authObject) throws RemoteException;





}
