package util.sleep;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RandomUniformSleep implements RandomSleep {
    private long min, max;


    public RandomUniformSleep(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public void sleep() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(generateRandom());
    }

    @Override
    public void sleep(Logger logger) throws InterruptedException {
        long r = generateRandom();
        logger.info("Sleeping for " + r + " milliseconds");
        TimeUnit.MILLISECONDS.sleep(r);
    }

    private long generateRandom() {
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }
}
