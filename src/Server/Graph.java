package Server;

public interface Graph {
    boolean addEdge(int node1, int node2);
    boolean removeEdge(int node1 , int node2);
    int getShortestPath(int node1, int node2);

}
