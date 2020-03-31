package server;

import graph.FloydWarshallGraph;
import graph.Graph;
import util.operation.Operation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FloydWarshallGraphServant extends UnicastRemoteObject implements GraphServer {
    private FloydWarshallGraph graph;
    private boolean graphModified;

    protected FloydWarshallGraphServant(FloydWarshallGraph graph) throws RemoteException {
        super();
        this.graph = graph;
        this.graphModified = false;
    }

    @Override
    public Integer submitOperation(Operation op) throws RemoteException {
        if (op.getType() == Operation.Type.QUERY) {
            if (graphModified) {    // TODO: Ensure thread safety
                graph.computeShortestPaths();
                graphModified = false;
            }
            return graph.getShortestPath(op.getOp1(), op.getOp2());
        }
        else {      // Add/Delete operation TODO: Ensure thread safety
            if (op.getType() == Operation.Type.ADD) {
                graph.addEdge(op.getOp1(), op.getOp2());
            }
            else if (op.getType() == Operation.Type.DEL) {
                graph.deleteEdge(op.getOp1(), op.getOp2());
            }
            graphModified = true;
        }
        return null;
    }

    @Override
    public String echo(String input) throws RemoteException {
        return input;
    }
}
