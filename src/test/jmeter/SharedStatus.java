import java.util.concurrent.CountDownLatch;

public class SharedStatus {
    public static final CountDownLatch jmxCompletedLatch = new CountDownLatch(1);
}
