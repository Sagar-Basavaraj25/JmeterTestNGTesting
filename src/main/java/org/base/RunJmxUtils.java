package org.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RunJmxUtils {
    public static void main(String[] args) throws InterruptedException {
        Process influxProcess = null;
        Process grafanaProcess = null;

        try {
            // Start InfluxDB
            influxProcess = startProcess("influxd", "InfluxDB");
            // Start Grafana
            grafanaProcess = startProcess("grafana-server.exe --homepath D:\\OLD\\Softwares\\grafana-v11.6.0", "Grafana");
            LocalDateTime today = LocalDateTime.now();
            String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String logs = "target/jmeterLogs/" + dateString + ".jtl";
            String report = "target/jmeterReports/"+dateString;
            String command = "jmeter -n -t "+"target/jmxFiles/jsonPlaceholderCollection_20250402_144330.jmx"+" -l "+logs+" -e -o "+report;
            ProcessBuilder jmeterBuilder = new ProcessBuilder("cmd.exe","/c",command);
            jmeterBuilder.redirectErrorStream(true);
            System.out.println(jmeterBuilder.command());
            Process jmeterProcess = jmeterBuilder.start();
            logProcessOutput(jmeterProcess, "JMeter");

            // Wait for JMeter execution to complete
            int exitCode = jmeterProcess.waitFor();
            System.out.println("JMeter process exited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Stop InfluxDB and Grafana
            Thread.sleep(Duration.ofMinutes(5));
            stopProcess(influxProcess, "InfluxDB");
            stopProcess(grafanaProcess, "Grafana");
        }
    }

    private static Process startProcess(String command, String processName) {
        try {
            ProcessBuilder processBuilder;
            if(command.contains("grafana")){
                processBuilder = new ProcessBuilder(command.split(" "));
            }else{
                processBuilder = new ProcessBuilder(command);
            }
            // Redirect error stream to prevent hanging
            processBuilder.redirectErrorStream(true);
            // Start the process
            Process process = processBuilder.start();
            // Log process output in a separate thread
            logProcessOutput(process, processName);
            System.out.println(processName + " started successfully.");
            return process;
        } catch (Exception e) {
            System.err.println("Error starting " + processName + ": " + e.getMessage());
            return null;
        }
    }

    private static void logProcessOutput(Process process, String processName) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(processName + ": " + line);
                }
            } catch (Exception e) {
                System.err.println("Error reading output of " + processName + ": " + e.getMessage());
            }
        }).start();
    }

    private static void stopProcess(Process process, String processName) {
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
            try {
                if(processName.equalsIgnoreCase("grafana")){
                    Process process1 = new ProcessBuilder("taskkill", "/F", "/IM", "grafana.exe").start();
                    process1.waitFor();
                }
                process.waitFor();
                System.out.println(processName + " stopped.");
            } catch (Exception e) {
                System.err.println("Error stopping " + processName + ": " + e.getMessage());
            }
        }else {
            System.out.println(processName + " is not running.");
        }
    }

    private static void startGrafana(){
        new Thread(()->{
            try {
                WebDriver driver = new ChromeDriver();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.get("http://localhost:3000/login");
                driver.findElement(By.name("user")).sendKeys();
                driver.findElement(By.name("password")).sendKeys();
                driver.findElement(By.xpath("//button[@type='submit']")).click();
                driver.findElement(By.xpath("//a[@title='Dashboards']")).click();
                driver.findElement(By.xpath("//a[text()='Apache JMeter Dashboard using Core InfluxdbBackendListenerClient']")).click();

            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }
}
