import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class PrinterServant extends UnicastRemoteObject implements PrinterService {

    boolean started;
    ArrayList<Printer> printers;

    public PrinterServant() throws RemoteException {
        super();
        printers = new ArrayList<>();
        printers.add(new Printer("a"));
        printers.add(new Printer("b"));
        printers.add(new Printer("c"));
        started = false;

    }

    @Override
    public String print(String fileName, String printer) throws RemoteException {

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
    public String queue(String printer) throws RemoteException {
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
    public String topQueue(String printer, int job) throws RemoteException {
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
    public String start() throws RemoteException {
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
    public String stop() throws RemoteException {
        if (!started) return "Printer server not started.";
        started = false;

        for (Printer p :
                printers) {
            p.clearJobs();
        }

        return "Server stopped";
    }

    @Override
    public String restart() throws RemoteException {
        if (!started) return "Printer server not started.";

        stop();

        start();

        return "Server successfully restarted";
    }

    @Override
    public String status(String printer) throws RemoteException {
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
    public String readConfig(String parameter) throws RemoteException {
        if (!started) return "Printer server not started.";

        //TODO

        return null;
    }

    @Override
    public String setConfig(String parameter, String value) throws RemoteException {
        if (!started) return "Printer server not started.";

        //TODO

        return null;
    }
}
