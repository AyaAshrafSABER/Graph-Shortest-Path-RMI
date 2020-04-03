package server;

import graph.Graph;

import util.parse.Parser;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.ArrayList;

import static java.lang.System.exit;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class Server {
    private  Graph graph;
    private static Logger LOGGER;

    public static void main(String[] args) throws RemoteException {
        // default options
        String mode = "interactive";
        String filename = "defaultGraph.txt";
        ArrayList<String> lines = new ArrayList<>();
        Scanner scanner;
        Parser parser = new Parser();

        if (args.length < 1) {
            System.err.println("The running mode should be specified!\n\t-i interactive\n\t-f read from file");
            exit(1);
        }

        if (args[0].equals("-f")) { mode = "file"; }

        if (mode.equals("file")) {
            if (args.length > 1) {
                filename = args[1];
            }
            try {
                File file = new File(filename);
                scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    lines.add(scanner.nextLine());
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {    // input the initial graph from the standard input.
            scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("S")) { scanner.close(); break;}
                lines.add(line);
            }
        }

        initLogger();
        LOGGER.info("Initializing server");

        LOGGER.info("Graph processed, starting service");

        int port = 5099;
        Registry registry =  LocateRegistry.createRegistry(5099);
//        registry.rebind("graphServant", new LazyUpdateServant((parser.constructGraph(lines)), LOGGER));
        registry.rebind("graphServant", new InstantUpdateServant(parser.constructGraph(lines), LOGGER));

        LOGGER.info("Service started successfully on port " + port);
    }

    private static void initLogger() {
        String dir = "server-logs";
        System.setProperty("name", "server");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        System.setProperty("log.directory", dir);
        System.setProperty("current.date.time", dateFormat.format(new Date()));

        File directory = new File(dir);
        if (! directory.exists()){
            directory.mkdir();
        }

        LOGGER = LogManager.getLogger(Server.class);
        String log4jConfigFile = System.getProperty("user.dir") + File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
    }
}
