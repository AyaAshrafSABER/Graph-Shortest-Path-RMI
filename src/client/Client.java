package client;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.apache.log4j.PropertyConfigurator;
import server.GraphServer;
import util.operation.Operation;
import util.operation.RandomOperationFactory;
import util.parse.Parser;
import util.sleep.RandomUniformSleep;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Random;
import java.util.Scanner;

public class Client {

    private static Logger LOGGER;

    private static void runAuto(double pQuery, double pAdd)
            throws RemoteException, NotBoundException, MalformedURLException {
        RandomOperationFactory factory = new RandomOperationFactory(pQuery, pAdd, 1 - pAdd - pQuery,
                1, 20);
        Random randomGenerator = new Random();
        RandomUniformSleep sleep = new RandomUniformSleep(100, 1000);
        LOGGER.info("Establishing connection with server");
        GraphServer service = (GraphServer) Naming.lookup("rmi://localhost:5099/graphServant");
        LOGGER.info("Connection successful, starting requests");
        int operationCount = randomGenerator.nextInt(20);
        for (int i = 0; i < operationCount; i++) {
            Operation request = factory.getOperation();
            LOGGER.info("request sent: " + request.toString());
            Integer result = service.submitOperation(request);
            if (request.getType() == Operation.Type.QUERY)
                LOGGER.info("response: " + result);
            try {
                sleep.sleep(LOGGER);
            } catch (InterruptedException e) {
                LOGGER.warn("InterruptedException thrown while sleeping");
            }
        }
    }

    private static void runFromFile(String filename) throws RemoteException, NotBoundException, MalformedURLException {
        ArrayList<Operation> operations;
        Parser parser = new Parser();
        ArrayList<String> lines = new ArrayList<>();
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        operations = parser.constructOperations(lines);
        RandomUniformSleep sleep = new RandomUniformSleep(100, 1000);
        LOGGER.info("Establishing connection with server");
        GraphServer service = (GraphServer) Naming.lookup("rmi://localhost:5099/graphServant");
        LOGGER.info("Connection successful, starting requests");
        for (Operation op: operations) {
            LOGGER.info("request sent: " + op.toString());
            Integer result = service.submitOperation(op);
            if (op.getType() == Operation.Type.QUERY)
                LOGGER.info("response: " + result);
            try {
                sleep.sleep(LOGGER);
            } catch (InterruptedException e) {
                LOGGER.warn("InterruptedException thrown while sleeping");
            }
        }
    }

    private static void runInteractive() throws RemoteException, NotBoundException, MalformedURLException {
        Scanner scanner = new Scanner(System.in);
        RandomUniformSleep sleep = new RandomUniformSleep(100, 1000);
        Parser parser = new Parser();
        LOGGER.info("Establishing connection with server");
        GraphServer service = (GraphServer) Naming.lookup("rmi://localhost:5099/graphServant");
        LOGGER.info("Connection successful, starting requests");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("F")) {
                scanner.close();
                break;
            }
            Operation op = parser.parseOperation(line);
            LOGGER.info("request sent: " + op.toString());
            Integer result = service.submitOperation(op);
            if (op.getType() == Operation.Type.QUERY)
                LOGGER.info("response: " + result);
            try {
                sleep.sleep(LOGGER);
            } catch (InterruptedException e) {
                LOGGER.warn("InterruptedException thrown while sleeping");
            }
        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {

        initLogger();
        LOGGER.info("Initializing client");

        // default options
        String mode = "auto";
        String filename = "defaultRequests.txt";
        double pQuery = 0.5;
        double pAdd = 0.2;

        if (args.length < 1) {
            System.err.println("The running mode should be specified!\n\t" +
                    "-i interactive\n\t-f read from file\n\t-a auto generate operations");
        }
        if (args[0].equals("-i")) { mode = "interactive";}
        else if (args[0].equals("-f")) {mode = "file";}

        if (mode.equals("file")) {
            if (args.length > 1) {
                filename = args[1];
            }
            runFromFile(filename);
        } else if (mode.equals("auto")) {
            if (args.length > 2) {
                pQuery = Double.parseDouble(args[1]);
                pAdd = Double.parseDouble(args[2]);
            }
            runAuto(pQuery, pAdd);
        } else {    // input the operations from the standard input.
                runInteractive();
        }
        LOGGER.info("Finished all requests, exiting.");
    }

    private static void initLogger() {
        Random rnd = new Random();
        String dir = "client-logs";
        System.setProperty("name", "client");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        System.setProperty("log.directory", dir);
        System.setProperty("current.date.time", dateFormat.format(new Date()));
        System.setProperty("thread.id", "_" + String.valueOf(rnd.nextInt()));

        File directory = new File(dir);
        if (! directory.exists()){
            directory.mkdir();
        }

        LOGGER = LogManager.getLogger(Client.class);
        String log4jConfigFile = System.getProperty("user.dir") + File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
        LOGGER.info("Finished all requests, exiting.");
    }

}

