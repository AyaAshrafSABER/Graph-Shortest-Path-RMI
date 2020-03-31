package server;

import util.operation.Operation;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GraphServer extends Remote {
    public Integer submitOperation(Operation op) throws RemoteException;
    public String echo(String input )throws RemoteException; //ToDo: tobedeleted
}
