package server;

import graph.FloydWarshallGraph;
import org.apache.log4j.Logger;
import util.operation.Operation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LazyUpdateServant extends UnicastRemoteObject implements GraphServer {
    private FloydWarshallGraph graph;
    private boolean graphModified;
    private ReadWriteLock rwl = new ReentrantReadWriteLock();
    private Logger logger;

    protected LazyUpdateServant(FloydWarshallGraph graph, Logger logger) throws RemoteException {
        super();
        this.graph = graph;
        this.graphModified = false;
        this.logger = logger;
    }

    @Override
    public Integer submitOperation(Operation op) throws RemoteException {
        this.logger.info("received request: " + op.toString());

        if (op.getType() == Operation.Type.QUERY) {
            if (graphModified) {    // TODO: Ensure thread safety
                rwl.writeLock().lock();
                this.logger.info("Update triggered, reindexing shortest paths table");
                graph.computeShortestPaths();
                graphModified = false;
                rwl.writeLock().unlock();
            }
            rwl.readLock().lock();
            Integer result = graph.getShortestPath(op.getOp1(), op.getOp2());
            rwl.readLock().unlock();
            this.logger.info("read request completed successfully, returning response: " + result);
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
        this.logger.info("write request completed successfully");
        return null;
    }

    @Override
    public String echo(String input) throws RemoteException {
        return input;
    }
}
