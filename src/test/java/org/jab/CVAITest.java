package org.jab;

import junit.framework.TestCase;
import org.jab.data.CVAI;
import org.junit.jupiter.api.DisplayName;

public class CVAITest extends TestCase {
    @DisplayName("Asking a question to generative AI model should return an answer back")
    public void testAsk() {
        String answer = CVAI.ask("How many years of SQL experience do you have?");
        assertNotNull(answer);
        assertTrue(answer.contains("3"));
        answer = CVAI.ask("How many years of work experience do you have with Microsoft Outlook?");
        assertTrue(answer.contains("99"));
    }
}
