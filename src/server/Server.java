package server;

import graph.FloydWarshallGraph;
import util.parse.Parser;
import graph.Graph;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import util.sleep.RandomExponentialSleep;
import util.sleep.RandomUniformSleep;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import static java.lang.System.exit;
import java.text.SimpleDateFormat;

public class Server {
    private static FloydWarshallGraph graph;
    private static Logger LOGGER;
    private static String name;
    private static int port;
    private static String servantClass;

    public static void main(String[] args) throws IOException {
        initLogger();
        loadConfigs();

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
            LOGGER.info("Starting server in file mode");
            if (args.length > 1) {
                filename = args[1];
            }
            try {
                LOGGER.info("Reading initial graph from " + filename);
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
            LOGGER.info("Starting server in interactive mode");
            System.out.println("Enter each edge in a separate line in the format \"node1 node2\". Terminate by entering S");
            scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("S") || line.equals("s")) { scanner.close(); break;}
                lines.add(line);
            }
        }

        graph = parser.constructGraph(lines);

        LOGGER.info("Initial graph processed, starting service");

        GraphServer servant = null;
        if (servantClass.equals("InstantUpdateServant")) {
            servant = new InstantUpdateServant(graph, LOGGER);
        }
        else if (servantClass.equals("LazyUpdateServant")) {
            servant = new LazyUpdateServant(graph, LOGGER);
        }

        Registry registry =  LocateRegistry.createRegistry(port);
        registry.rebind(name, servant);

        LOGGER.info(String.format("Service started successfully on port %d with name \"%s\"", port, name));
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
        String log4jConfigFile = "configs/log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
    }

    public static void loadConfigs() throws IOException {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "server.properties";

            inputStream = new FileInputStream("configs/" + propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            port = Integer.parseInt(prop.getProperty("GSP.rmi.port"));
            name = prop.getProperty("GSP.rmi.registry.name");
            servantClass = prop.getProperty("GSP.servant.class");

        } catch (Exception e) {
            LOGGER.fatal("Could not open server properties file");
            System.out.println("Exception: " + e);
        } finally {

            assert inputStream != null;
            inputStream.close();
        }
    }
}
