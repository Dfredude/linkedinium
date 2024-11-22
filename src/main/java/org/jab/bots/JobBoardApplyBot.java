package org.jab.bots;

import java.util.List;

public interface JobBoardApplyBot {
    String jobBoardURL = "indeed.com";
    void applyToJobs();
    void logIn();
    List<Job> searchJobs();
    void applyToJob(Job job);

    class Job {
        String jobID;
        String jobTitle;
        String jobURL;
    }
}
