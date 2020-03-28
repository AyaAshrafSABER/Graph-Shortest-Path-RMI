package Client;

import Server.GraphServant;
import Server.GraphServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class Client {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {

        GraphServer service = (GraphServer) Naming.lookup("rmi://localhost:5099/graphServent");
        System.out.println("----------" + service.echo("  hi ")+ " " + service.getClass().getName());
    }
}
