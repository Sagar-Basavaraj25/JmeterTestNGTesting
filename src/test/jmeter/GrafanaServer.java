
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class GrafanaServer {

    private static final Logger log = LogManager.getLogger(GrafanaServer.class);

    @Test(dependsOnGroups = {"jmx"})
    public void grafanaProcess(){
        try{
            log.info("Starting the Grafana Server");
            ProcessBuilder processBuilder = new ProcessBuilder("grafana-server.exe","--homepath","D:\\OLD\\Softwares\\grafana-v11.6.0");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            log.info("Grafana Started");
            //SharedStatus.jmxCompletedLatch.await();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while (!SharedStatus.jmxCompletedLatch.await(1, TimeUnit.SECONDS)) {
                    while (reader.ready()) {
                        String line = reader.readLine();
                        if (line != null && !line.isBlank() && line.contains("HTTP Server Listen")) {
                            System.out.println("Grafana: " + line);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.info("Waiting 5 mins after jmeter execution done");
            Thread.sleep(Duration.ofMinutes(1));
            log.info("Stopping Grafana");
            if (process.isAlive()) {
                process.destroy();
                process.waitFor(10, TimeUnit.SECONDS);
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            }
            Process process1 = new ProcessBuilder("taskkill", "/F", "/IM", "grafana.exe").start();
            process1.waitFor();
            log.info("Grafana Server Stopped successfully");
        } catch (Exception e) {
            log.info("Error occurred : {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
