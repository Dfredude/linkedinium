package org.jab;

import junit.framework.TestCase;
import org.jab.bots.LinkedInDriver;
import org.jab.data.DataTeller;

public class LinkedInTest extends TestCase{
    LinkedInDriver linkedInDriver = null;
    DataTeller dataTeller = new DataTeller();
    public void testLogin()
    {
        linkedInDriver = new LinkedInDriver(dataTeller.getCookie());
        linkedInDriver.setCredentials(dataTeller.getEmail(), dataTeller.getPassword());
        linkedInDriver.logIn();
        assertTrue(true);
    }

    public void testSendConnections()
    {
        linkedInDriver = new LinkedInDriver(dataTeller.getCookie());
        linkedInDriver.setCredentials(dataTeller.getEmail(), dataTeller.getPassword());
        linkedInDriver.logIn();
        // String company = "RBC";
        String company = "geospectrumtech";
        int expectedConnectionsSent = 5;
        linkedInDriver.sendConnections(company, expectedConnectionsSent);
        assertEquals(expectedConnectionsSent, linkedInDriver.getConnectionsSent());
    }
}
