package util.sleep;

import org.apache.log4j.Logger;

public interface RandomSleep {
    public void sleep() throws InterruptedException;
    public void sleep(Logger logger) throws InterruptedException;
}
