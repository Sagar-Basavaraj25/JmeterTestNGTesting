import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
import java.time.Duration;

public class GrafanaDashboard {

    private static final Logger log = LogManager.getLogger(GrafanaDashboard.class);

    @Test(dependsOnGroups = {"jmx"})
    public void dashboard(){
        try{
            log.info("Grafana dashboard is starting in 20 seconds");
            WebDriver driver = new ChromeDriver();
            Thread.sleep(Duration.ofSeconds(20));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.get("http://localhost:3000/login");
            driver.findElement(By.name("user")).sendKeys("admin");
            driver.findElement(By.name("password")).sendKeys("cds@123");
            driver.findElement(By.xpath("//button[@type='submit']")).click();
            driver.findElement(By.xpath("//a[@title='Dashboards']")).click();
            driver.findElement(By.xpath("//a[text()='Apache JMeter Dashboard using Core InfluxdbBackendListenerClient']")).click();
            SharedStatus.jmxCompletedLatch.await();
            log.info("Waiting for 5 mins after Jmeter Execution");
            Thread.sleep(Duration.ofMinutes(1));
            driver.findElement(By.xpath("//button[@aria-label='Profile']")).click();
            driver.findElement(By.xpath("//span[text()='Sign out']")).click();
            driver.quit();
        } catch (Exception e) {
            log.info("Error Occurred : {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
