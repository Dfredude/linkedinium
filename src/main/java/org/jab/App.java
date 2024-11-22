package org.jab;

import org.jab.bots.LinkedInDriver;
import org.jab.data.DataTeller;

public class App
{
    final private static DataTeller dataTeller = new DataTeller();

    public static void main(String[] args) {
        runLinkedInDriver();
    }

    public static void runLinkedInDriver(){
        LinkedInDriver linkedInDriver = new LinkedInDriver(dataTeller.getCookie());
        linkedInDriver.applyToJobs();
    }

}
