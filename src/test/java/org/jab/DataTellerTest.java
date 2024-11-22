package org.jab;

import junit.framework.TestCase;
import org.jab.data.DataTeller;

public class DataTellerTest extends TestCase {
    public void testGetAnswer() {
        DataTeller dataTeller = new DataTeller();
        String answer = dataTeller.getAnswer("Are you legally authorized to work for any employer in Canada?");
        assertEquals("Yes I have work permit", answer);
        answer = dataTeller.getAnswer("How many years of work experience do you have with Microsoft Outlook?");
        assertEquals("99", answer);
    }

    public void testGetUnwantedKeywords() {
        DataTeller dataTeller = new DataTeller();
        assertEquals(6, dataTeller.getUnwantedKeywords().size());
    }

    public void testGetUnknownAnswer() {
        DataTeller dataTeller = new DataTeller();
        String answer = dataTeller.getAnswer("How many years of work experience do you have with JMeter?");
        assertEquals("1", answer);
    }

    public void testGetLinkedInEmail() {
        DataTeller dataTeller = new DataTeller();
        assertEquals("YOUR_EMAIL@MAIL.COM", dataTeller.getEmail());
    }
}
