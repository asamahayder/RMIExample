import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;



public class Client{

    static String username = "User1";
    static String password = "test1";
    static String authObject = username + ";" + password;
    static Scanner in = new Scanner(System.in);

    static State currentState = State.MAIN;

    static PrinterService service;

    static {
        try {
            service = (PrinterService) Naming.lookup("rmi://localhost:5099/print");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    enum State {
        OFF,
        MAIN,
        PRINT,
        QUEUE,
        TOPQUEUE,
        START,
        STOP,
        RESTART,
        STATUS,
        READCONFIG,
        SETCONFIG
    }

    public static void main(String[] args) throws RemoteException {

        while (currentState != State.OFF){
            switch (currentState){
                case OFF :
                    break;
                case MAIN :
                    mainMenu();
                    break;
                case PRINT :
                    print();
                    break;
                case QUEUE:
                    queue();
                    break;
                case TOPQUEUE:
                    topQueue();
                    break;
                case START:
                    start();
                    break;
                case STOP:
                    stop();
                    break;
                case RESTART:
                    restart();
                    break;
                case STATUS:
                    status();
                    break;
                case READCONFIG:
                    readConfig();
                    break;
                case SETCONFIG:
                    setConfig();
                    break;
            }
        }



        System.out.println(service.print("test", "printer1", authObject));



    }

    private static void setConfig() throws RemoteException{
        System.out.println("Please write a config name");
        String config = in.nextLine();
        System.out.println("Please write a config value");
        String value = in.nextLine();
        String response = service.setConfig(config, value, authObject);
        System.out.println("Response from server: ");
        System.out.println(response + "\n");
        currentState = State.MAIN;
    }

    private static void readConfig() throws RemoteException{
        System.out.println("Please write a config name");
        String config = in.nextLine();
        String response = service.readConfig(config, authObject);
        System.out.println("Response from server: ");
        System.out.println(response + "\n");
        currentState = State.MAIN;
    }

    private static void status() throws RemoteException{
        System.out.println("Please write a printer");
        String printer = in.nextLine();
        String response = service.status(printer, authObject);
        System.out.println("Response from server: ");
        System.out.println(response + "\n");
        currentState = State.MAIN;
    }

    private static void start() throws RemoteException{
        String response = service.start(authObject);
        System.out.println("Response from server: ");
        System.out.println(response + "\n");
        currentState = State.MAIN;
    }

    private static void stop() throws RemoteException{
        String response = service.stop(authObject);
        System.out.println("Response from server: ");
        System.out.println(response + "\n");
        currentState = State.MAIN;
    }

    private static void restart() throws RemoteException{
        String response = service.restart(authObject);
        System.out.println("Response from server: ");
        System.out.println(response + "\n");
        currentState = State.MAIN;
    }

    private static void topQueue() throws RemoteException{
        System.out.println("Please write a printer");
        String printer = in.nextLine();
        System.out.println("Please write a job number (int)");
        int job = in.nextInt();
        String response = service.topQueue(printer, job, authObject);
        System.out.println("Response from server: ");
        System.out.println(response + "\n");
        currentState = State.MAIN;
    }

    private static void queue() throws RemoteException{
        System.out.println("Please write a printer");
        String printer = in.nextLine();
        String response = service.queue(printer, authObject);
        System.out.println("Response from server: ");
        System.out.println(response + "\n");
        currentState = State.MAIN;
    }

    private static void print() throws RemoteException{
        System.out.println("Please write a filename");
        String fileName = in.nextLine();
        System.out.println("Please write a printer");
        String printer = in.nextLine();
        System.out.println("Response from server: ");
        String response = service.print(fileName, printer, authObject);
        System.out.println(response + "\n");
        currentState = State.MAIN;
    }

    private static void mainMenu(){

        System.out.println("Please choose an action by entering number: ");
        System.out.print("1: print - ");
        System.out.print("2: queue - ");
        System.out.print("3: topQueue - ");
        System.out.print("4: start - ");
        System.out.print("5: stop - ");
        System.out.print("6: restart - ");
        System.out.print("7: status - ");
        System.out.print("8: readConfig - ");
        System.out.print("9: setConfig - ");
        System.out.print("0: turn off client\n");

        String choice = in.nextLine();

        switch (choice){
            case "1":
                currentState = State.PRINT;
                break;
            case "2":
                currentState = State.QUEUE;
                break;
            case "3":
                currentState = State.TOPQUEUE;
                break;
            case "4":
                currentState = State.START;
                break;
            case "5":
                currentState = State.STOP;
                break;
            case "6":
                currentState = State.RESTART;
                break;
            case "7":
                currentState = State.STATUS;
                break;
            case "8":
                currentState = State.READCONFIG;
                break;
            case "9":
                currentState = State.SETCONFIG;
                break;
            case "0":
                currentState = State.OFF;
                break;
            default:
                System.out.println("Please choose a valid action by entering number");
        }

    }

}
