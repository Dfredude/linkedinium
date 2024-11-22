package org.jab;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jab.data.JobsContext;
import org.jab.bots.LinkedInDriver;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    LinkedInDriver linkedInDriver = null;
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */

    public void testApplyToJob()
    {
        String jobID = "3918492266";
//        linkedInDriver.driver = LinkedInDriver.getDriver();
//        linkedInDriver.driver.get("https://www.linkedin.com/jobs");
//        linkedInDriver.js = LinkedInDriver.getJS();
        linkedInDriver.logIn();
        LinkedInDriver.JobPage jobPage = linkedInDriver.new JobPage(jobID);
        jobPage.applyToJob();
        JobsContext jobsContext = new JobsContext();
        assertTrue(jobsContext.hasBeenAppliedTo(jobID));
    }

    public void testInsertPendingJob()
    {
        String jobID = "3887382003";
        linkedInDriver.logIn();
        LinkedInDriver.JobPage jobPage = linkedInDriver.new JobPage(jobID);
        jobPage.applyToJob();
        JobsContext jobsContext = new JobsContext();
        assertTrue(jobsContext.isJobPending(jobID));
    }

    public void testAppProperties()
    {

//        String appVersion = appProps.getProperty("version");
//        assertEquals("1.0", appVersion);
//        assertEquals("files", catalogProps.getProperty("c1"));
    }
}
