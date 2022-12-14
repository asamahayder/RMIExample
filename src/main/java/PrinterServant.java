import database.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
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
        if (!isAuthenticated(authObject, "print")) return "Not authenticated!";
        if (!isAuthorized(authObject, "print")) return "Not authorized!";
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
        if (!isAuthenticated(authObject, "queue")) return "Not authenticated!";
        if (!isAuthorized(authObject, "queue")) return "Not authorized!";
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
        if (!isAuthenticated(authObject, "topQueue")) return "Not authenticated!";
        if (!isAuthorized(authObject, "topQueue")) return "Not authorized!";
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
        if (!isAuthenticated(authObject, "start")) return "Not authenticated!";
        if (!isAuthorized(authObject, "start")) return "Not authorized!";
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
        if (!isAuthenticated(authObject, "stop")) return "Not authenticated!";
        if (!isAuthorized(authObject, "stop")) return "Not authorized!";
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
        if (!isAuthenticated(authObject, "restart")) return "Not authenticated!";
        if (!isAuthorized(authObject, "restart")) return "Not authorized!";
        if (!started) return "Printer server not started.";

        stop(authObject);

        start(authObject);

        return "Server successfully restarted";
    }

    @Override
    public String status(String printer, String authObject) throws RemoteException {
        if (!isAuthenticated(authObject, "status")) return "Not authenticated!";
        if (!isAuthorized(authObject, "status")) return "Not authorized!";
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
        if (!isAuthenticated(authObject, "readConfig")) return "Not authenticated!";
        if (!isAuthorized(authObject, "readConfig")) return "Not authorized!";
        if (!started) return "Printer server not started.";

        Database db = new Database();
        ConfigDTO configDTO = db.getConfig(parameter);
        if (configDTO != null){
            String value = configDTO.getDescription();
            return value;
        }else{
            return "Value not found!!";
        }

    }

    @Override
    public String setConfig(String parameter, String value, String authObject) throws RemoteException {
        if (!isAuthenticated(authObject, "setConfig")) return "Not authenticated!";
        if (!isAuthorized(authObject, "setConfig")) return "Not authorized!";
        if (!started) return "Printer server not started.";

        Database db = new Database();
        db.setConfig(parameter, value);

        return "Config updated!";
    }

    @Override
    public String makeChanges(String authObject) throws RemoteException {
        if (!isAuthenticated(authObject, "makeChanges")) return "Not authenticated!";
        if (!isAuthorized(authObject, "makeChanges")) return "Not authorized!";
        if (!started) return "Printer server not started.";

        Database db = new Database();
        boolean result = db.makeChangesToUsers();

        if (result)return "Success";
        else return "Error!";

    }

    private boolean isAuthorized(String authObject, String methodName){

        if (authObject == null) {
            logger.log(Level.WARNING, "authObject was null");
            return false;
        }

        if (methodName == null) {
            logger.log(Level.WARNING, "method was null");
            return false;
        }

        try{
            Database db = new Database();

            //First step is to get which authorization method to use
            ConfigDTO authorizationMethod = db.getConfig("authorization_method");
            String username = authObject.split(";")[0];

            UserDTO userDTO = db.selectUser(username);

            if (authorizationMethod.getDescription().equals("list_based")){
                //Getting all rows from userOperations that has userId
                List<OperationDTO> operationDTOS = db.selectUserOperations(userDTO);

                for (OperationDTO operationDTO : operationDTOS) {
                    if (operationDTO.getOperation().equals(methodName)){
                        logger.log(Level.INFO, "User " + username + " has been authorized successfully to use " + methodName);
                        return true;
                    }
                }

                logger.log(Level.INFO, "User " + username + " does not have permission to execute " + methodName);
                return false;

            }else{
                //Getting allowed operations from user role
                List<OperationDTO> operationDTOS = db.selectRoleOperations(userDTO);
                for (OperationDTO operationDTO : operationDTOS) {
                    if (operationDTO.getOperation().equals(methodName)){
                        logger.log(Level.INFO, "User " + username + " has been authorized successfully to use " + methodName);
                        return true;
                    }
                }

                logger.log(Level.INFO, "User " + username + " does not have permission to execute " + methodName);
                return false;
            }

        }catch (Exception e){
            logger.log(Level.WARNING, "Could not connect to database");
            return false;
        }
    }

    private boolean isAuthenticated(String authObject, String methodName) {
        logger.log(Level.INFO, "Method " + methodName + " has been invoked");
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
