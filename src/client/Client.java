package client;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.apache.log4j.PropertyConfigurator;
import server.GraphServer;
import util.operation.Operation;
import util.operation.RandomOperationFactory;
import util.sleep.RandomUniformSleep;

import java.io.File;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Client {
    private static Logger LOGGER;

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        initLog();
        LOGGER.info("Initializing client");

        RandomOperationFactory factory = new RandomOperationFactory(0.5, 0.3, 0.2, 1, 20);
        Random randomGenerator = new Random();
        RandomUniformSleep sleep = new RandomUniformSleep(1, 100);
        int operationCount = randomGenerator.nextInt(20);

        LOGGER.info("Establishing connection with server");
        GraphServer service = (GraphServer) Naming.lookup("rmi://localhost:5099/graphServent");

        LOGGER.info("Connection successful, starting requests");
        for (int i = 0; i < 1000; i++) {
            Operation request = factory.getOperation();
            LOGGER.info("request sent: " + request.toString());

            if (request.getType() == Operation.Type.QUERY)
                LOGGER.info("response: " + service.submitOperation(request));
            try {
                sleep.sleep(LOGGER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LOGGER.info("Finished all requests, exiting.");
    }

    private static void initLog() {
        Random rnd = new Random();
        String dir = "client-logs";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        System.setProperty("log.directory", dir);
        System.setProperty("current.date.time", dateFormat.format(new Date()));
        System.setProperty("thread.id", String.valueOf(rnd.nextInt()));

        File directory = new File(dir);
        if (! directory.exists()){
            directory.mkdir();
        }

        LOGGER = LogManager.getLogger(Client.class);
        String log4jConfigFile = System.getProperty("user.dir") + File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
    }
}
