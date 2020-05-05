package com.juliusbaer.selenium.browser.automation;

import java.util.logging.Logger;

public class StoppingAtMaxCountEmailSentException extends Exception {
    public StoppingAtMaxCountEmailSentException(int maxcount, Logger logger) {
        logger.warning("Stopping at maxcount " + Integer.toString(maxcount) + " - " + this.getMessage());
    }
}
