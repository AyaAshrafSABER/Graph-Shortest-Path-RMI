package graph;

import java.util.*;

class FloydWarshallGraph implements Graph {
    final static int INF = Integer.MAX_VALUE/2;
    HashMap<Integer, HashSet<Integer>> adjacencyList;
    HashMap<Integer, HashMap<Integer, Integer>> dist;

    public FloydWarshallGraph() {
        this.adjacencyList = new HashMap<>();
        this.dist = new HashMap<>();
        computeShortestPaths();
    }

    public FloydWarshallGraph(HashMap<Integer, HashSet<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
        this.dist = new HashMap<>();
        computeShortestPaths();
    }

    void computeShortestPaths() {
        dist = new HashMap<>();
        HashMap<Integer, Integer> emptyMap = new HashMap<>();
        HashMap<Integer, Integer> iMap;

        Integer distIK, distKJ, distIJ;
        for (Integer i : adjacencyList.keySet()) {
            iMap = new HashMap<>();
            iMap.put(i, 0);
            dist.put(i, iMap);
        }

        for (Integer i : adjacencyList.keySet()) {
            for (Integer j : adjacencyList.get(i)) {
                iMap = new HashMap<>();
                iMap = dist.getOrDefault(i, iMap);
                iMap.put(j, 1);
                dist.put(i, iMap);
            }
        }

        for (Integer k : adjacencyList.keySet()) {
            for (Integer j : adjacencyList.keySet()) {
                for (Integer i : adjacencyList.keySet()) {
                    distIK  = dist.getOrDefault(i, emptyMap).getOrDefault(k, INF);
                    distKJ = dist.getOrDefault(k, emptyMap).getOrDefault(j, INF);
                    distIJ = dist.getOrDefault(i, emptyMap).getOrDefault(j, INF);

                    if (distIK + distKJ < distIJ) {
                        iMap = new HashMap<>();
                        iMap = dist.getOrDefault(i, iMap);
                        iMap.put(j, distIK + distKJ);
                        dist.put(i, iMap);
                    }
                }
            }
        }


        // Print the shortest distance matrix
    }

    public void addNodeIfNotExist(Integer i) {
        if (adjacencyList.containsKey(i)) return;

        adjacencyList.put(i, new HashSet<>());
        dist.put(i, new HashMap<>());
        dist.get(i).put(i, 0);
    }

    public void addEdge(Integer i, Integer j) {
        addNodeIfNotExist(j);

        HashSet<Integer> iSet = new HashSet<>();
        iSet = adjacencyList.getOrDefault(i, iSet);
        iSet.add(j);
        this.adjacencyList.put(i, iSet);
        computeShortestPaths();
    }

    public void deleteEdge(Integer i, Integer j) {
        if (!adjacencyList.containsKey(i)) return;
        adjacencyList.get(i).remove(j);
        computeShortestPaths();
    }

    public int getShortestPath(Integer i, Integer j) {
        if (!dist.containsKey(i)) return -1;
        return dist.get(i).getOrDefault(j, -1);
    }


    public void printShortestPath() {
        System.out.println("The following matrix shows the shortest "+
                "distances between every pair of vertices");

        for (Integer i : dist.keySet()) {
            System.out.print(i + "[ ");
            for (Integer j : dist.get(i).keySet()) {
                System.out.print(j + ": " + dist.get(i).get(j) + ", ");
            }
            System.out.println("]");
        }
    }


}


