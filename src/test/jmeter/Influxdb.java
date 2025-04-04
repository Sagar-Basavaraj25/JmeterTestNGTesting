import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;

public class Influxdb {

    private static final Logger log = LogManager.getLogger(Influxdb.class);

    @Test
    public void influxProcess(){
        try{
            log.info("Starting the Influxdb Server");
            ProcessBuilder processBuilder = new ProcessBuilder("influxd.exe");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            log.info("Influxdb Started Successfully");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (!SharedStatus.jmxExecutionCompleted.get()) {
                try{
                    String line = reader.readLine();
                    if(line != null){
                        System.out.println("Influxd : "+ line);
                    }
                } catch (Exception e) {
                    log.info("Error reading output of Grafana : {}", e.getMessage());
                }
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
