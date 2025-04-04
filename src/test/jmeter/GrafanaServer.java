
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;

public class GrafanaServer {

    private static final Logger log = LogManager.getLogger(GrafanaServer.class);

    @Test
    public void grafanaProcess(){
        try{
            log.info("Starting the Grafana Server");
            ProcessBuilder processBuilder = new ProcessBuilder("grafana-server.exe","--homepath","D:\\OLD\\Softwares\\grafana-v11.6.0");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            log.info("Grafana Started");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (!SharedStatus.jmxExecutionCompleted.get()) {
                try{
                    String line = reader.readLine();
                    if(line != null){
                        System.out.println("Grafana: "+ line);
                    }
                } catch (Exception e) {
                    log.info("Error reading output of Grafana : {}", e.getMessage());
                }
            }
            Thread.sleep(Duration.ofMinutes(1));
            log.info("Waiting 5 mins after jmeter execution done");
            log.info("Stopping Grafana");
            process.destroyForcibly();
            Process process1 = new ProcessBuilder("taskkill", "/F", "/IM", "grafana.exe").start();
            process1.waitFor();
            log.info("Grafana Server Stopped successfully");
        } catch (Exception e) {
            log.info("Error occurred : {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
