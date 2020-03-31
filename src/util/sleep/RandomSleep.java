package util.sleep;

import java.util.logging.Logger;

public interface RandomSleep {
    public void sleep() throws InterruptedException;
    public void sleep(Logger logger) throws InterruptedException;
}
