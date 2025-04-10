import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Influxdb {

    private static final Logger log = LogManager.getLogger(Influxdb.class);

    @Test(dependsOnGroups = {"jmx"})
    public void influxProcess(){
        try{
            log.info("Starting the Influxdb Server");
            ProcessBuilder processBuilder = new ProcessBuilder("influxd");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            log.info("Influxdb Started Successfully");
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while (!SharedStatus.jmxCompletedLatch.await(1, TimeUnit.SECONDS)) {
                    while (reader.ready()) {
                        String line = reader.readLine();
                        if (line != null && !line.isBlank() && line.contains("Listening on HTTP")) {
                            System.out.println("Influxd: " + line);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.info("Waiting 5 mins after jmeter execution done");
            Thread.sleep(Duration.ofMinutes(1));
            log.info("Stopping Influx");
            process.destroy();
            log.info("Influxd Stopped successfully");
        } catch (Exception e) {
            log.info("Error occurred : {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
