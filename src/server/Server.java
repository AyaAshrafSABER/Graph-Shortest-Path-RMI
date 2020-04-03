package server;

import graph.Graph;
import util.parse.Parser;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.ArrayList;

import static java.lang.System.exit;

public class Server {
    private  Graph graph;
    public  Server() {

    }
    public static void main(String[] args) throws RemoteException {
        // default options
        String mode = "interactive";
        String filename = "defaultGraph.txt";
        ArrayList<String> lines = new ArrayList<>();
        Scanner scanner;
        Parser parser = new Parser();

        if (args.length < 1) {
            System.err.println("The running mode should be specified!\n\t-i interactive\n\t-f read from file");
            exit(1);
        }

        if (args[0].equals("-f")) { mode = "file"; }

        if (mode.equals("file")) {
            if (args.length > 1) {
                filename = args[1];
            }
            try {
                File file = new File(filename);
                scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    lines.add(scanner.nextLine());
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {    // input the initial graph from the standard input.
            scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("S")) { scanner.close(); break;}
                lines.add(line);
            }
        }

        Registry registry =  LocateRegistry.createRegistry(5099);
//        registry.rebind("graphServant", new LazyUpdateServant((parser.constructGraph(lines))));
        registry.rebind("graphServant", new InstantUpdateServant((parser.constructGraph(lines))));
    }
}
