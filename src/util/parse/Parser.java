package util.parse;

import graph.FloydWarshallGraph;
import util.operation.Operation;

import java.util.*;

public class Parser {

    public FloydWarshallGraph constructGraph(ArrayList<String> lines) {
        HashMap<Integer, HashSet<Integer>> adjacencyList = new HashMap<>();
        for (String line: lines) {
            String[] numbers = line.split("\\s+");
            if (adjacencyList.containsKey(Integer.parseInt(numbers[0]))) {
                adjacencyList.get(Integer.parseInt(numbers[0])).add(Integer.parseInt(numbers[1]));
            } else {
                adjacencyList.put(Integer.parseInt(numbers[0]),
                        new HashSet<>(Collections.singletonList(Integer.parseInt(numbers[1]))));
            }
        }
        return new FloydWarshallGraph(adjacencyList);
    }

    public ArrayList<Operation> constructOperations(ArrayList<String> lines) {
        ArrayList<Operation> operations = new ArrayList<>();
        for (String line: lines) {
            operations.add(parseOperation(line));
        }
        return operations;
    }

    public Operation parseOperation(String line) {
        Operation.Type type;
        String[] parts = line.split("\\s+");
        if (parts[0].equals("A")) {
            type = Operation.Type.ADD;
        } else if (parts[0].equals("D")) {
            type = Operation.Type.DEL;
        } else {
            type = Operation.Type.QUERY;
        }
        return new Operation(type, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }
}
