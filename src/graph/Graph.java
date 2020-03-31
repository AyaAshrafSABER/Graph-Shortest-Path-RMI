package graph;

public interface Graph {
    void addEdge(Integer node1, Integer node2);
    void deleteEdge(Integer node1 , Integer node2);
    int getShortestPath(Integer node1, Integer node2);

}
