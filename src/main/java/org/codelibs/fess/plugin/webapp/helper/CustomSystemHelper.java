package org.codelibs.fess.plugin.webapp.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.helper.SystemHelper;

public class CustomSystemHelper extends SystemHelper {

    private static final Logger logger = LogManager.getLogger(CustomSystemHelper.class);

    @Override
    protected void parseProjectProperties() {
        try {
            super.parseProjectProperties();
        } catch (Exception e) {
            logger.warn("Cannot parse project.properties.", e);
        }
        System.setProperty("fess.webapp.plugin", "true");
    }
}
