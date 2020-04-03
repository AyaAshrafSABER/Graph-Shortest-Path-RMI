package client;

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
import java.util.Random;
import java.util.Scanner;

public class Client {

    private static void runAuto(double pQuery, double pAdd)
            throws RemoteException, NotBoundException, MalformedURLException {
        RandomOperationFactory factory = new RandomOperationFactory(pQuery, pAdd, 1 - pAdd - pQuery,
                1, 20);
        Random randomGenerator = new Random();
        RandomUniformSleep sleep = new RandomUniformSleep(100, 1000);
        GraphServer service = (GraphServer) Naming.lookup("rmi://localhost:5099/graphServant");
        int operationCount = randomGenerator.nextInt(20);
        for (int i = 0; i < operationCount; i++) {
            Operation op = factory.getOperation();
            try {
                System.out.println("Result: " + service.submitOperation(op));
                sleep.sleep();
            } catch (Exception e) {
                e.printStackTrace();
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
        GraphServer service = (GraphServer) Naming.lookup("rmi://localhost:5099/graphServant");
        for (Operation op: operations) {
            try {
                System.out.println("Result: " + service.submitOperation(op));
                sleep.sleep();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void runInteractive() throws RemoteException, NotBoundException, MalformedURLException {
        Scanner scanner = new Scanner(System.in);
        RandomUniformSleep sleep = new RandomUniformSleep(100, 1000);
        Parser parser = new Parser();
        GraphServer service = (GraphServer) Naming.lookup("rmi://localhost:5099/graphServant");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("F")) {
                scanner.close();
                break;
            }
            Operation op = parser.parseOperation(line);
            try {
                System.out.println("Result: " + service.submitOperation(op));
                sleep.sleep();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
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
    }
}

