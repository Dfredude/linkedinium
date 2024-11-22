package org.jab.drivers;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class AccountDriver {
    protected WebDriver driver = getDriver();
    protected WebDriverWait wait = getWait();
    protected JavascriptExecutor js = (JavascriptExecutor) driver;
    protected String username;
    protected String password;
    protected String sessionCookie;
    protected boolean loggedIn;
    private int connectionsSent = 0;

    public void setCredentials(String email, String password) {
        this.username = email;
        this.password = password;
    }

    public int getConnectionsSent() {
        return connectionsSent;
    }

    public void incrementConnectionsSent() {
        connectionsSent++;
    }

    protected interface JobPage {
        void applyToJob();
        void goToJobPage(String jobID);
    }
    protected WebDriver getDriver() {
        if (driver == null){
            ChromeOptions options = new ChromeOptions().setBinary("C:\\chrome-win64\\chrome.exe");
            driver = new ChromeDriver(options);
        }
        return driver;
    }

    public WebDriverWait getWait() {
        if (wait == null) {
            wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        }
        return wait;
    }

    public JavascriptExecutor getJS(){
        if (js == null){
            js = (JavascriptExecutor) driver;
        }
        return js;
    }
}
