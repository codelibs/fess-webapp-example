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
package org.codelibs.fess.webapp.example.helper;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.helper.SystemHelper;

/**
 * An example helper that demonstrates how a Fess WebApp plugin can <b>override</b>
 * a core Fess component by subclassing it and re-registering it under the same
 * component name via the {@code fess+systemHelper.xml} redefine convention.
 *
 * <p>
 * Fess declares the core {@code systemHelper} component in its {@code fess.xml}.
 * Shipping a classpath resource named {@code fess+<componentName>.xml} (here
 * {@code fess+systemHelper.xml}) tells LastaDi to register THIS class in place
 * of the core {@link SystemHelper}. Because a redefine REPLACES the whole
 * component definition, the override XML must repeat every {@code postConstruct}
 * the core definition performs (the design-JSP name mappings) &mdash; see
 * {@code fess+systemHelper.xml} for the details and the maintenance cost this
 * implies.
 * </p>
 *
 * <p>
 * The only behavior this subclass adds is in {@link #parseProjectProperties}: it
 * tolerates a parse failure instead of aborting startup, and records a marker
 * system property so other code can detect that the plugin is active. Replace
 * this with your own override logic.
 * </p>
 *
 * @see ExampleHelper for the complementary "add a brand-new component" example.
 */
public class CustomSystemHelper extends SystemHelper {

    private static final Logger logger = LogManager.getLogger(CustomSystemHelper.class);

    /**
     * Default constructor.
     */
    public CustomSystemHelper() {
        super();
    }

    /**
     * Overrides the core parsing of {@code project.properties} so that Fess keeps
     * starting even when the file cannot be parsed, and marks this plugin as
     * active via the {@code fess.webapp.plugin} system property.
     *
     * @param propPath the path to {@code project.properties}
     */
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
