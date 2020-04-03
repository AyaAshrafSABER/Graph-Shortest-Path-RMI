package server;

import graph.FloydWarshallGraph;
import util.operation.Operation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InstantUpdateServant extends UnicastRemoteObject implements GraphServer {

    private FloydWarshallGraph graph;
    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    protected InstantUpdateServant(FloydWarshallGraph graph) throws RemoteException {
        super();
        this.graph = graph;

    }

    @Override
    public Integer submitOperation(Operation op) throws RemoteException {
        if (op.getType() == Operation.Type.QUERY) {
            rwl.readLock().lock();
            Integer result = graph.getShortestPath(op.getOp1(), op.getOp2());
            rwl.readLock().unlock();
            return result;
        } else {      // Add/Delete operation
            rwl.writeLock().lock();
            if (op.getType() == Operation.Type.ADD) {
                graph.addEdge(op.getOp1(), op.getOp2());
            } else if (op.getType() == Operation.Type.DEL) {
                graph.deleteEdge(op.getOp1(), op.getOp2());
            }
            graph.computeShortestPaths();
            rwl.writeLock().unlock();
        }
        return null;
    }

    @Override
    public String echo(String input) throws RemoteException {
        return input;
    }
}
