package server;

import graph.FloydWarshallGraph;
import graph.Graph;
import util.operation.Operation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FloydWarshallGraphServant extends UnicastRemoteObject implements GraphServer {
    private FloydWarshallGraph graph;
    private boolean graphModified;
    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    protected FloydWarshallGraphServant(FloydWarshallGraph graph) throws RemoteException {
        super();
        this.graph = graph;
        this.graphModified = false;
    }

    @Override
    public Integer submitOperation(Operation op) throws RemoteException {
        if (op.getType() == Operation.Type.QUERY) {
            if (graphModified) {    // TODO: Ensure thread safety
                rwl.writeLock().lock();
                graph.computeShortestPaths();
                graphModified = false;
                rwl.writeLock().unlock();
            }
            rwl.readLock().lock();
            Integer result = graph.getShortestPath(op.getOp1(), op.getOp2());
            rwl.readLock().unlock();
            return result;
        }
        else {      // Add/Delete operation TODO: Ensure thread safety
            rwl.writeLock().lock();
            if (op.getType() == Operation.Type.ADD) {
                graph.addEdge(op.getOp1(), op.getOp2());
            }
            else if (op.getType() == Operation.Type.DEL) {
                graph.deleteEdge(op.getOp1(), op.getOp2());
            }
            graphModified = true;
            rwl.writeLock().unlock();
        }
        return null;
    }

    @Override
    public String echo(String input) throws RemoteException {
        return input;
    }
}
