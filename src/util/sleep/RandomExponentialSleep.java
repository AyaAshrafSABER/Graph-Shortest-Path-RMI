package util.sleep;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RandomExponentialSleep implements RandomSleep {
    private double lambda;
    private long min, max;

    public RandomExponentialSleep(long mean) {
        this.lambda = 1.0/mean;
        this.min = 0;
        this.max = Long.MAX_VALUE;
    }

    public RandomExponentialSleep(long mean, long min, long max) {
        this.lambda = 1.0/mean;
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
        long r = (long)(lambda * Math.log(1-Math.random())/(-lambda));
        if (r < min) r = min;
        else if (r > max) r= max;
        return r;
    }
}
