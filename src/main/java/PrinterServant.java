import database.Database;
import database.Filer;
import database.Hasher;
import database.UserDTO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PrinterServant extends UnicastRemoteObject implements PrinterService {

    boolean started;
    ArrayList<Printer> printers;
    Logger logger = Logger.getLogger(PrinterServant.class.getName());
    FileHandler fh;

    public PrinterServant() throws RemoteException {
        super();
        printers = new ArrayList<>();
        printers.add(new Printer("a"));
        printers.add(new Printer("b"));
        printers.add(new Printer("c"));
        started = false;
        try {
            fh = new FileHandler(Filer.getPath() + "log.log");
            logger.addHandler(fh);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fh.setFormatter(simpleFormatter);

            logger.log(Level.INFO, "Logger started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String print(String fileName, String printer, String authObject) throws RemoteException {
        if (!isAuthenticated(authObject)) return "Not authenticated!";
        if (!started) return "Printer server not started.";

        boolean printerFound = false;

        for (Printer p :printers) {
            if (p.getName().equals(printer)){
                p.addJob(fileName);
                printerFound = true;
            }
        }


        if (printerFound){
            return "Job placed in queue";
        }else {
            return "Printer not found";
        }
    }

    @Override
    public String queue(String printer, String authObject) throws RemoteException {
        if (!isAuthenticated(authObject)) return "Not authenticated!";
        if (!started) return "Printer server not started.";
        boolean printerFound = false;

        StringBuilder builder = new StringBuilder();

        for (Printer p :printers) {
            if (p.getName().equals(printer)){
                for (PrinterJob job :
                        p.getJobs()) {
                    builder.append(job.jobId).append(": ").append(job.jobContent).append("\n");
                }
                printerFound = true;
            }
        }


        if (printerFound){
            return builder.toString();
        }else {
            return "Printer not found";
        }
    }

    @Override
    public String topQueue(String printer, int job, String authObject) throws RemoteException {
        if (!isAuthenticated(authObject)) return "Not authenticated!";
        if (!started) return "Printer server not started.";
        boolean printerFound = false;
        boolean jobFound = false;

        for (Printer p :printers) {
            if (p.getName().equals(printer)){
                jobFound = p.topQueue(job);
                printerFound = true;
            }
        }


        if (!printerFound) return "Printer not found";

        if (!jobFound) return "Job not found";

        return "Job moved to top of queue";
    }

    @Override
    public String start(String authObject) throws RemoteException {
        if (!isAuthenticated(authObject)) return "Not authenticated!";
        started = true;

        StringBuilder builder = new StringBuilder();

        builder.append("Printer server started. Available printers: \n");
        for (Printer p :
                printers) {
            builder.append("Printer: ");
            builder.append(p.getName()).append("\n");
        }

        return builder.toString();
    }

    @Override
    public String stop(String authObject) throws RemoteException {
        if (!isAuthenticated(authObject)) return "Not authenticated!";
        if (!started) return "Printer server not started.";
        started = false;

        for (Printer p :
                printers) {
            p.clearJobs();
        }

        return "Server stopped";
    }

    @Override
    public String restart(String authObject) throws RemoteException {
        if (!isAuthenticated(authObject)) return "Not authenticated!";
        if (!started) return "Printer server not started.";

        stop(authObject);

        start(authObject);

        return "Server successfully restarted";
    }

    @Override
    public String status(String printer, String authObject) throws RemoteException {
        if (!isAuthenticated(authObject)) return "Not authenticated!";
        if (!started) return "Printer server not started.";
        boolean printerFound = false;
        StringBuilder builder = new StringBuilder();

        for (Printer p :printers) {
            if (p.getName().equals(printer)){
                builder.append("Name: ").append(p.getName()).append("\n");
                builder.append("Active Jobs: ").append(p.getJobs().toString()).append("\n");
                builder.append("Finished Jobs: ").append(p.getFinishedJobs().toString()).append("\n");
                printerFound = true;
            }
        }

        if (!printerFound) return "Printer not found";

        return builder.toString();
    }

    @Override
    public String readConfig(String parameter, String authObject) throws RemoteException {
        if (!isAuthenticated(authObject)) return "Not authenticated!";
        if (!started) return "Printer server not started.";

        Database db = new Database();
        String value = db.getConfig(parameter).getDescription();

        if (value != null) return value;
        else return "Value not found!!";
    }

    @Override
    public String setConfig(String parameter, String value, String authObject) throws RemoteException {
        if (!isAuthenticated(authObject)) return "Not authenticated!";
        if (!started) return "Printer server not started.";

        Database db = new Database();
        db.setConfig(parameter, value);

        return "Config updated!";
    }

    private boolean isAuthenticated(String authObject) {

        if (authObject == null) {
            logger.log(Level.WARNING, "authObject was null");
            return false;
        }

        try{
            Database db = new Database();

            String username = authObject.split(";")[0];
            String password = authObject.split(";")[1];

            UserDTO userDTO = db.selectUser(username);

            if (userDTO == null) {
                logger.log(Level.INFO, "User was not found in database: " + username);
                return false;
            }

            if (Hasher.hashPassword(password, userDTO.getSalt()).equals(userDTO.getPassword())) {
                logger.log(Level.INFO, "User has been authenticated: " + username + ";" + password);
                return true;
            }

            logger.log(Level.INFO, "User was not authenticated: " + username + ";" + password);
            return false;

        }catch (Exception e){
            logger.log(Level.WARNING, "Could not connect to database");
            return false;
        }
    }
}
