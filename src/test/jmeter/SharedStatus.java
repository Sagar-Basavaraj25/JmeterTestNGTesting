import java.util.concurrent.atomic.AtomicBoolean;

public class SharedStatus {
    public static AtomicBoolean jmxExecutionCompleted = new AtomicBoolean(false);
}
