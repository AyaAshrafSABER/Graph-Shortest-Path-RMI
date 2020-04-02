package client;

import server.GraphServer;
import util.operation.Operation;
import util.operation.RandomOperationFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        RandomOperationFactory factory = new RandomOperationFactory(0.5, 0.3, 0.2, 3, 10);
        Operation request = factory.getOperation();
        System.out.println(request.toString());
        GraphServer service = (GraphServer) Naming.lookup("rmi://localhost:5099/graphServent");
        System.out.println("----------" + service.echo("  hi ")+ " " + service.getClass().getName());
        System.out.println("Result " + service.submitOperation(request));
    }
}
