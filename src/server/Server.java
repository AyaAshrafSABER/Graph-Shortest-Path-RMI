package server;

import graph.FloydWarshallGraph;
import graph.Graph;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class Server {
    private  Graph graph;
    private static Logger LOGGER;

    public static void main(String[] args) throws RemoteException {
        initLogger();
        LOGGER.info("Initializing server");
        HashMap<Integer, HashSet<Integer>> adjacencyList = new HashMap<>();
        adjacencyList.put(1, new HashSet<>(Arrays.asList(2)));
        adjacencyList.put(2, new HashSet<>(Arrays.asList(3, 4)));
        adjacencyList.put(3, new HashSet<>(Arrays.asList(1)));
        adjacencyList.put(4, new HashSet<>(Arrays.asList(1)));
        FloydWarshallGraph graph = new FloydWarshallGraph(adjacencyList);

        LOGGER.info("Graph processed, starting service");

        int port = 5099;
        Registry registry =  LocateRegistry.createRegistry(5099);
        registry.rebind("graphServent", new FloydWarshallGraphServant(graph, LOGGER));

        LOGGER.info("Service started successfuly on port " + port);
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
