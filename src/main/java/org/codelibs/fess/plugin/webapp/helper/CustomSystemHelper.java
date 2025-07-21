/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.plugin.webapp.helper;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.helper.SystemHelper;

/**
 * Custom system helper for Fess webapp plugin that extends the default SystemHelper.
 * This helper enables webapp plugin functionality by setting the appropriate system property
 * and provides enhanced error handling for project properties parsing.
 */
public class CustomSystemHelper extends SystemHelper {

    /**
     * Default constructor for CustomSystemHelper.
     * Initializes the custom system helper with webapp plugin capabilities.
     */
    public CustomSystemHelper() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(CustomSystemHelper.class);

    @Override
    protected void parseProjectProperties(final Path propPath) {
        try {
            super.parseProjectProperties(propPath);
        } catch (final Exception e) {
            logger.warn("Cannot parse project.properties.", e);
        }
        System.setProperty("fess.webapp.plugin", "true");
    }
}
