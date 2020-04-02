package client;

import server.GraphServer;
import util.operation.Operation;
import util.operation.RandomOperationFactory;
import util.sleep.RandomUniformSleep;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.util.Random;

public class Client {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        RandomOperationFactory factory = new RandomOperationFactory(0.5, 0.3, 0.2, 1, 20);
        Random randomGenerator = new Random();
        RandomUniformSleep sleep = new RandomUniformSleep(10, 1000);
        int operationCount = randomGenerator.nextInt(20);

        for (int i = 0; i < operationCount; i++) {
            Operation request = factory.getOperation();
            System.out.println("Reguest: " + request.toString());
            GraphServer service = (GraphServer) Naming.lookup("rmi://localhost:5099/graphServent");
            System.out.println("---------- "  + service.getClass().getName() + " -----------");
            System.out.println("Result: " + service.submitOperation(request));
            try {
                sleep.sleep();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
