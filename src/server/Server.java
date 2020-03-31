package server;

import graph.FloydWarshallGraph;
import graph.Graph;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private Graph graph;
    public  Server(){

    }
    public static void main(String[] args) throws RemoteException {

       Registry registry =  LocateRegistry.createRegistry(5099);
       registry.rebind("graphServent", new FloydWarshallGraphServant(new FloydWarshallGraph()));
    }
}
