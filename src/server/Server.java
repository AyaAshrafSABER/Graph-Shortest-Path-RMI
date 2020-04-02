package server;

import graph.FloydWarshallGraph;
import graph.Graph;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Server {
    private  Graph graph;
    public  Server() {

    }
    public static void main(String[] args) throws RemoteException {
        HashMap<Integer, HashSet<Integer>> adjacencyList = new HashMap<>();
        adjacencyList.put(1, new HashSet<>(Arrays.asList(2)));
        adjacencyList.put(2, new HashSet<>(Arrays.asList(3, 4)));
        adjacencyList.put(3, new HashSet<>(Arrays.asList(1)));
        adjacencyList.put(4, new HashSet<>(Arrays.asList(1)));
        FloydWarshallGraph graph = new FloydWarshallGraph(adjacencyList);

        Registry registry =  LocateRegistry.createRegistry(5099);
        registry.rebind("graphServent", new FloydWarshallGraphServant((graph)));
    }
}
