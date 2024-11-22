package org.jab.data;

import junit.framework.TestCase;

public class JobsContextTest extends TestCase {
    final private JobsContext jc = new JobsContext();
//    public JobsContextTest(String testName) {
//        super(testName);
//    }
    public void testIncreaseCounter() {
        int initial_count = jc.getTotalCount();
        String jobID = "3772650869";
        jc.increaseCounter(jobID);
        int final_count = jc.getTotalCount();
        assertEquals(initial_count + 1, final_count);
    }

    public void testGetTotalCount() {
        int count = jc.getTotalCount();
        System.out.println(count);
        assertTrue(count >= 0);
    }
}
