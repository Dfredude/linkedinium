package org.jab.bots;

import org.jab.data.DataTeller;
import org.jab.data.JobsContext;
import org.jab.drivers.AccountDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

public class LinkedInDriver extends AccountDriver implements JobBoardApplyBot {
    private final DataTeller dataTeller = new DataTeller();
    private final static String SearchQuery = "Software Engineer";
    private final static String location = "Canada";
    private final static JobsContext jobsContext = new JobsContext();

    public void sendConnections(String company, int number) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        driver.get(String.format("https://linkedin.com/company/%s/people", company));
        // Get all connect buttons
        String connectBtnXPath = "//span[text()[contains(.,'Connect')]]";
        // Wait for connect buttons to load
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        List<WebElement> connectBtns = driver.findElements(By.xpath(connectBtnXPath));
        while (this.getConnectionsSent() < number ){
            for (int i = this.getConnectionsSent(); i < connectBtns.size() && i < number; i++){
                WebElement connectBtn = connectBtns.get(i);
                js.executeScript("arguments[0].scrollIntoView(true);", connectBtn);
                js.executeScript("arguments[0].click();", connectBtn);
                try {
                    WebElement add_note_btn = driver.findElement(By.xpath("//button[@aria-label='Add a note']"));
                    String recipient_name = driver.findElement(By.xpath("//div[@role='dialog']//strong")).getText();
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
                    add_note_btn.click();
                    WebElement note_input = driver.findElement(By.xpath("//textarea[@name='message']"));
                    note_input.sendKeys("Hi " + recipient_name + ",\n" +
                            "\n" +
                            "I hope you’re doing well. I came across your profile and was impressed by your work in your field. I would love to connect.\n" +
                            "\n" +
                            "Best regards,\n" +
                            "Alfredo Lucio");
                    WebElement send_btn = driver.findElement(By.xpath("//button[@aria-label='Send invitation']"));
                    send_btn.click();
                    this.incrementConnectionsSent();
                } catch (NoSuchElementException e){
                    System.out.println(e.getMessage());
                }
            }
            // Scroll
            try {
                js.executeScript("window.scrollBy(0, 2000)");
                WebElement showMoreResultsBtn = driver.findElement(By.cssSelector(".scaffold-finite-scroll__load-button"));
                if (showMoreResultsBtn.isEnabled()) showMoreResultsBtn.click();
            } catch (NoSuchElementException e){
                System.out.println("No show more results button found or not enabled");
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
            // Wait for page to load
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            connectBtns = driver.findElements(By.xpath(connectBtnXPath));
        }
        System.out.println("Sent connect invitation to " + getConnectionsSent() + " people");
    }

    public class JobPage {
        private final String job_id;
        public JobPage(String job_id){
            this.job_id = job_id;
        }
        private void goToJobPage(){
            driver.get("https://www.linkedin.com/jobs/view/" + job_id);
        }

        public void applyToJob(){
            if (job_id == null){
                throw new NullPointerException("Must have a job_id to apply to a job");
            }
            // Check if job has been applied to in DB
            if (jobsContext.hasBeenAppliedTo(job_id) || jobsContext.isJobPending(job_id)){
                System.out.println("Job has already been gone through: " + job_id);
                return;
            }
            goToJobPage();
            // String xPathSelector = "//button[@data-job-id='" + job_id + "']";
            By easyApplyCSSSelector = By.cssSelector("button[data-job-id]");
            // Wait for page to load
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            List<WebElement> applyBtns = driver.findElements(easyApplyCSSSelector);
            boolean isEasyApply = applyBtns.size() > 0;
            if (!isEasyApply){
                System.out.println("Not an Easy Apply job");
                return;
            }
            // Click on Easy Apply button
            wait = getWait();
            wait.until(d -> d.findElement(easyApplyCSSSelector).isEnabled());
            applyBtns = driver.findElements(easyApplyCSSSelector);
            WebElement apply_btn = applyBtns.get(applyBtns.size() - 1);

            // Workaround for clicking on button
            js.executeScript("arguments[0].scrollIntoView(true);", apply_btn);
            js.executeScript("arguments[0].click();", apply_btn);

            // If Job search safety reminder is displayed, then click on continue
            WebElement continue_applying_btn = driver.findElement(easyApplyCSSSelector);
            if (continue_applying_btn.getText().contains("Continue applying")){
                js.executeScript("arguments[0].scrollIntoView(true);", continue_applying_btn);
                js.executeScript("arguments[0].click();", continue_applying_btn);
            }
            // Continue with application until submit button
            while (driver.findElements(By.cssSelector("[data-easy-apply-next-button]")).size() > 0){
                try {
//                    continue_applying_btn = driver.findElement(By.cssSelector("[data-easy-apply-next-button]"));
                    handleEmptyInputs();
                    WebElement next_btn = driver.findElement(By.cssSelector("[data-easy-apply-next-button]"));
                    js.executeScript("arguments[0].scrollIntoView(true);", next_btn);
                    next_btn.click();
                } catch (NoSuchElementException e) {
                    System.out.println("No next button found");
                    System.out.println("Error: " + e.getMessage());
                    // Wait for user to fill out fields
                }
            }

            // Check for input fields in last page
            handleEmptyInputs();

            // Check if there's a review button
            if (driver.findElements(By.cssSelector("[aria-label='Review your application']")).size() > 0){
                WebElement review_btn = driver.findElement(By.cssSelector("[aria-label='Review your application']"));
                review_btn.click();
            }

            // Submit application and uncheck follow company beforehand
            try {
                WebElement follow_company = driver.findElement(By.id("follow-company-checkbox"));
                js.executeScript("arguments[0].scrollIntoView(true);", follow_company);
                if (follow_company.isSelected()){
                    // JS workaround
                    js.executeScript("arguments[0].click();", follow_company);
//                follow_company.click();
                }
            } catch (NoSuchElementException e){
                System.out.println("No follow company checkbox found");
            }

            try {
                WebElement submitBtn = driver.findElement(By.xpath("//button[@aria-label='Submit application']"));
                submitBtn.click();
                System.out.println("Submitted application");
                jobsContext.increaseCounter(job_id);
                return;

            } catch (Exception e) {
                // No sumbit btn found
                // Most likely it's a review button
                System.out.println("No submit button found - skipping job: " + job_id);
                System.out.println("Error: " + e.getMessage());
            }
            jobsContext.insertPendingJob(job_id);


        }

        private void handleEmptyInputs() {
            // Check for text input fields
            List<WebElement> input_fields = driver.findElements(By.cssSelector(
                    "input[id*=form-component-formElement-urn-li-jobs-applyformcommon-easyApplyFormElement"));
            for (WebElement input_field : input_fields) {
                if (input_field.getAttribute("value").equals("") && input_field.getAttribute("required") != null) {
                    // Check if data is available in yml file
                    WebElement label;
                    try {
                        label = input_field.findElement(By.xpath("./preceding-sibling::label"));
                    } catch (NoSuchElementException e) {
                        // If no label, then it's a single-typehead-entity
                        label = input_field.findElement(By.xpath("../preceding-sibling::label/span"));
                    }
                    String field_question = label.getText();
                    try {
                        String field_value = dataTeller.getAnswer(field_question);
                        if (field_value != null) {
                            input_field.sendKeys(field_value);
                            // If it's a single-typehead-entity, then escape dropdown
                            if (input_field.getAttribute("id").contains("single-typeahead-entity")) {
                                input_field.sendKeys(Keys.ESCAPE);
                                // Wait for dropdown to close by itself
                                wait = getWait();
                                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[id^=typeahead-overlay]")));
                            }
                        } else {
                            throw new Exception("Text Field requires your attention: " + field_question);
                        }
                    } catch (Exception e) {
                        String alertScript = "alert(\"" + e.getMessage() + "\")";
                        js.executeScript(alertScript);
                    }

                }
            }

            // Check for empty radio select fields
            List<WebElement> radio_selects_field_sets = driver.findElements(By.cssSelector("fieldset[id^=radio-button-form-component-formElement-urn-li-jobs-applyformcommon-easyApplyFormElement]"));
            for (WebElement radio_select_field_set : radio_selects_field_sets){
                List<WebElement> radio_selects = radio_select_field_set.findElements(By.cssSelector("input[type=radio]"));
                WebElement radio_select_y = radio_selects.get(0);
                WebElement radio_select_n = radio_selects.get(1);
                if (radio_select_y.isSelected() || radio_select_n.isSelected()) continue;
                WebElement span = radio_select_field_set.findElement(By.cssSelector("[data-test-form-builder-radio-button-form-component__title]>span[aria-hidden=true]"));
                String field_name = span.getText();
                try {
                    String field_value = dataTeller.getAnswer(field_name);
                    if (field_value != null){
                        boolean isYes = field_value.toLowerCase().equals("yes");
                        try{
                            if (field_value.equals("Yes")){
                                isYes = true;
                                js.executeScript("arguments[0].scrollIntoView(true);", radio_select_y);
                                radio_select_y.click();
                            } else if (field_value.equals("No")){
                                isYes = false;
                                js.executeScript("arguments[0].scrollIntoView(true);", radio_select_n);
                                radio_select_n.click();
                            }
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                            if (isYes) js.executeScript("arguments[0].click();", radio_select_y);
                            else js.executeScript("arguments[0].click();", radio_select_n);

                        }

                    } else {
                        throw new Exception("Radio Select requires your attention: " + field_name);
                    }
                } catch (Exception e){
                    String alertScript = "alert(\"" + e.getMessage() + "\")";
                    js.executeScript(alertScript);
                }

            }

            // Check for checkbox fields
            List<WebElement> checkbox_fields = driver.findElements(By.cssSelector("input[type=checkbox]"));
            for(WebElement checkbox_field : checkbox_fields){
                if (checkbox_field.isSelected()) continue;
                String field_name = checkbox_field.findElement(By.xpath("./following-sibling::label")).getText();
                if (field_name.equals("")) continue;
                if (checkbox_field.getAttribute("required") != null || field_name.toLowerCase().contains("terms")) {
                    js.executeScript("arguments[0].scrollIntoView(true);", checkbox_field);
                    js.executeScript("arguments[0].click();", checkbox_field);
                }
            }

            // Check for empty dropdown fields
            List<WebElement> dropdown_fields = driver.findElements(By.cssSelector("select"));
            for (WebElement dropdown_field : dropdown_fields){
                if (dropdown_field.getAttribute("required") != null && (dropdown_field.getAttribute("value").startsWith("Selec") || dropdown_field.getAttribute("value").equals("") || dropdown_field.getAttribute("value") == null)){
                    // Find label span
                    Select dropdown = new Select(dropdown_field);
                    WebElement lbl = dropdown_field.findElement(By.xpath("./preceding-sibling::label"));
                    WebElement lblText = lbl.findElement(By.xpath("./span[1]"));
                    String field_name = lblText.getText();
                    String field_value = dataTeller.getAnswer(field_name);
                    try{
                        dropdown.selectByVisibleText(field_value);
                    } catch (Exception e){
                        // Check options for select
                        List<WebElement> options = dropdown.getOptions();
                        // Select first option
                        dropdown.selectByVisibleText(options.get(1).getText());
                        System.out.println("<Select> - " + field_name + " requires your attention");
                        System.out.println("Selected first option: " + options.size());
                    }
                }
            }
        }
    }

    public boolean loginManually(){
        // Go to login page
        driver.get("https://www.linkedin.com/login");
        // Logging manually by sending credentials
        String email = dataTeller.getEmail();
        String password = dataTeller.getPassword();
        driver.findElement(By.id("username")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("[type='submit'")).click();
        // Wait for page to load
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        // Check if login was successful by checking cookie
        if (driver.manage().getCookieNamed("li_at") == null){
            System.out.println("Login failed");
            throw new RuntimeException("Login failed");
        } else this.loggedIn = true;
        return true;
    }

    public LinkedInDriver(String username, String password){
        // Program starts here
        driver.get("https://linkedin.com/jobs");
        this.username = username;
        this.password = password;
        logIn();
        // driver.findElement(By.cssSelector("[href='https://www.linkedin.com/jobs/?']")).click();
        // Wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
    }

    public LinkedInDriver(String sessionToken){
        // Program starts here
        driver.get("https://linkedin.com");
        this.sessionCookie = sessionToken;
    }
    public void logIn(){
        // li_at Set cookie session
        if (sessionCookie != null) {
            try {
                driver.manage().addCookie(new org.openqa.selenium.Cookie("li_at", sessionCookie));
            } catch (Exception e){
                System.out.println("Error setting sessionCookie");
                System.out.println(e.getMessage());
            }
        } else {
            try {
                loginManually();
                sessionCookie = driver.manage().getCookieNamed("li_at").getValue();
                if (dataTeller.setCookie(sessionCookie)){
                    System.out.println("Cookie set successfully: " + sessionCookie);
                } else {
                    System.out.println("Error setting sessionCookie: " + sessionCookie);
                }

            } catch (Exception e){
                System.out.println("Error logging in manually");
                System.out.println(e.getMessage());
            }
        }

        // Refresh
        driver.navigate().refresh();
    }

    @Override
    public List<Job> searchJobs() {
        // Type to search for software in Halifax
        WebElement search_box = driver.findElement(By.cssSelector("[id^=jobs-search-box-keyword-id]"));
        // search_box.click();
        search_box.sendKeys(SearchQuery);
        wait.until(d -> d.findElement(By.cssSelector("[id^=jobs-search-box-location-id]")).isDisplayed());
        WebElement location_box = driver.findElement(By.cssSelector("[id^=jobs-search-box-location-id]"));
        // Clear location box
        location_box.clear();
        search_box.click();
        location_box.sendKeys(location);
        WebElement search_btn = driver.findElement(By.cssSelector(".jobs-search-box__submit-button"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        js.executeScript("arguments[0].click();", search_btn); // Search for jobs
        // Get all jobs available
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Wait for jobs URL to load
        // Filter for Easy Apply jobs
        wait.until(d -> !d.getCurrentUrl().equals("https://www.linkedin.com/jobs/"));
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        String c = driver.getCurrentUrl();
        driver.get(c + "&f_AL=true");

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3)); // Wait for jobs to load
        List<WebElement> jobs;
        List<String> job_ids = new java.util.ArrayList<>();
        int page_number = 1;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        WebElement next_page_btn = driver.findElement(By.cssSelector("div"));
        while (next_page_btn != null){
            try {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
                jobs = driver.findElements(By.cssSelector("[data-occludable-job-id]"));
                job_ids.addAll(retrieveJobIDs(jobs));
                // Go to next search result page
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
                next_page_btn = driver.findElement(By.cssSelector(String.format("li[data-test-pagination-page-btn='%d'] button", ++page_number)));
                js.executeScript("arguments[0].scrollIntoView(true);", next_page_btn);
                js.executeScript("arguments[0].click();", next_page_btn);
            } catch (NoSuchElementException e){
                System.out.println("No next page found");
                break;
            } catch (Exception e){
                System.out.println("Error getting job ids: Trying to find elements once again");
                System.out.println(e.getMessage());
                jobs = driver.findElements(By.cssSelector("[data-occludable-job-id]"));
                job_ids = retrieveJobIDs(jobs);
            }
        }

        List<Job> jobs_list = new java.util.ArrayList<>();
        for (String job_id : job_ids){
            Job job = new Job();
            job.jobID = job_id;
            jobs_list.add(job);
        }
        return jobs_list;
    }

    @Override
    public void applyToJob(Job job) {
        new JobPage(job.jobID).applyToJob();
    }

    public List<String> retrieveJobIDs(List<WebElement> jobs_elements){
        List<String> job_ids = new java.util.ArrayList<>();
        for (WebElement job : jobs_elements) {
            // Scroll into view
            js.executeScript("arguments[0].scrollIntoView(true);", job);
            String jobTitle = job.findElement(By.cssSelector("[data-control-id]")).getText().toLowerCase();
            String companyName = job.findElement(By.cssSelector(".artdeco-entity-lockup__subtitle span")).getText().toLowerCase();
            // Skip jobs that contain keywords
            List<String> keywords = dataTeller.getUnwantedKeywords();
            List<String> unwanted_companies = dataTeller.getUnwantedCompanies();
            boolean skip = false;
            for (String keyword: keywords) {
                if (jobTitle.contains(keyword.toLowerCase())){
                    System.out.println("Skipping job: " + jobTitle);
                    skip = true;
                    break;
                }
            }
            for (String company: unwanted_companies){
                if (companyName.contains(company.toLowerCase())){
                    System.out.println("Skipping job: " + jobTitle + " from " + companyName);
                    skip = true;
                    break;
                }
            }
            if (skip) continue;
            // Wait for job to be displayed properly
            wait.until(d -> job.isDisplayed());
            String job_id = job.getAttribute("data-occludable-job-id");


            if (job_id != null) job_ids.add(job_id);
            else {
                System.out.println("Job ID is null");
            }
        }
        return job_ids;
    }

    public void applyToJobs(){
        List<Job> jobs = searchJobs();
        // Apply to all jobs
        try {
            for (Job job : jobs) {
                applyToJob(job);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            driver.quit();
        }
        System.out.println("All jobs applied to");
    }

}
