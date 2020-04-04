package client;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.apache.log4j.PropertyConfigurator;
import server.GraphServer;
import util.operation.Operation;
import util.operation.RandomOperationFactory;
import util.parse.Parser;
import util.sleep.RandomExponentialSleep;
import util.sleep.RandomSleep;
import util.sleep.RandomUniformSleep;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

public class Client {

    private static Logger LOGGER;
    private static String rmiURL;
    private static double pQuery, pAdd, pDel;
    private static int minOp, maxOp;
    private static int minOperationCount, maxOperationCount;
    private static RandomSleep sleep;

    private static void runAuto() throws IOException, NotBoundException {
        LOGGER.info("Starting client in auto random mode");
        RandomOperationFactory factory = new RandomOperationFactory(pQuery, pAdd, pDel,minOp, maxOp);
        Random randomGenerator = new Random();
        LOGGER.info("Establishing connection with server on: " + rmiURL);
        GraphServer service = (GraphServer) Naming.lookup(rmiURL);

        int operationCount = ThreadLocalRandom.current().nextInt(minOperationCount, maxOperationCount + 1);
        LOGGER.info("Connection successful, starting requests, request count = "+ operationCount);
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
        LOGGER.info("Starting client in file mode, reading requests from " + filename);
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
        LOGGER.info("Establishing connection with server on: " + rmiURL);
        GraphServer service = (GraphServer) Naming.lookup(rmiURL);
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
        LOGGER.info("Starting client in Interactive mode");
        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser();
        LOGGER.info("Establishing connection with server on: " + rmiURL);
        GraphServer service = (GraphServer) Naming.lookup(rmiURL);
        LOGGER.info("Connection successful, starting requests");
        System.out.println("Enter request in a separate line in the format \"type node1 node2\". Terminate by entering F");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("F") || line.equals("f")) {
                scanner.close();
                break;
            }
            Operation op = parser.parseOperation(line);
            LOGGER.info("request sent: " + op.toString());
            Integer result = service.submitOperation(op);
            if (op.getType() == Operation.Type.QUERY)
                LOGGER.info("response: " + result);
        }
    }

    public static void main(String[] args) throws IOException, NotBoundException {
        initLogger();
        LOGGER.info("Initializing client");

        loadConfigs();
        LOGGER.info("Configs loaded");

        // default options
        String mode = "auto";
        String filename = "defaultRequests.txt";

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
            runAuto();
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
        String log4jConfigFile = "resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
    }

    public static void loadConfigs() throws IOException {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "client.properties";

            inputStream = new FileInputStream("resources/client.properties");
//            inputStream = Client.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            // get the property value and print it out
            String ip = prop.getProperty("GSP.server.ip");
            String port = prop.getProperty("GSP.server.port");
            String name = prop.getProperty("GSP.server.registry.name");
            String sleepClass = prop.getProperty("GSP.client.sleep.class");

            rmiURL = String.format("rmi://%s:%s/%s", ip, port, name);
            pQuery = Double.parseDouble(prop.getProperty("GSP.client.auto.pQuery"));
            pAdd = Double.parseDouble(prop.getProperty("GSP.client.auto.pAdd"));
            pDel = Double.parseDouble(prop.getProperty("GSP.client.auto.pDel"));

            minOp = Integer.parseInt(prop.getProperty("GSP.client.auto.minOp"));
            maxOp = Integer.parseInt(prop.getProperty("GSP.client.auto.maxOp"));

            minOperationCount = Integer.parseInt(prop.getProperty("GSP.client.auto.minOperationCount"));
            maxOperationCount = Integer.parseInt(prop.getProperty("GSP.client.auto.maxOperationCount"));

            if (sleepClass.equals("RandomExponentialSleep")) {
                int mean = Integer.parseInt(prop.getProperty("GSP.client.sleep.mean"));
                int max = Integer.parseInt(prop.getProperty("GSP.client.sleep.max"));
                int min = Integer.parseInt(prop.getProperty("GSP.client.sleep.min"));
                sleep = new RandomExponentialSleep(mean, min, max);
            }
            else if (sleepClass.equals("RandomUniformSleep")) {
                int max = Integer.parseInt(prop.getProperty("GSP.client.sleep.max"));
                int min = Integer.parseInt(prop.getProperty("GSP.client.sleep.min"));
                sleep = new RandomUniformSleep( min, max);
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e);
            LOGGER.fatal("Could not open client properties file");
        } finally {
            assert inputStream != null;
            inputStream.close();
        }
    }

}

