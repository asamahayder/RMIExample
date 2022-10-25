import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrinterService extends Remote {

    String print(String fileName, String printer) throws RemoteException;

    String queue(String printer) throws RemoteException;

    String topQueue(String printer, int job) throws RemoteException;

    String start() throws RemoteException;

    String stop() throws RemoteException;

    String restart() throws RemoteException;

    String status(String printer) throws RemoteException;

    String readConfig(String parameter) throws RemoteException;

    String setConfig(String parameter, String value) throws RemoteException;





}
