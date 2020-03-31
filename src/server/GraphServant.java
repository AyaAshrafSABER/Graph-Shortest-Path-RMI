package server;

import util.operation.Operation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GraphServant extends UnicastRemoteObject implements GraphServer {

    protected GraphServant() throws RemoteException {
        super();
    }

    @Override
    public boolean submitOperation(Operation op) throws RemoteException {
        return false;
    }

    @Override
    public String echo(String input) throws RemoteException {
        return input;
    }
}
