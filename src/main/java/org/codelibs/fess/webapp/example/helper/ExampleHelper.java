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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * A minimal example helper that demonstrates how a Fess WebApp plugin can add a
 * brand-new component to Fess's DI container via the additive {@code app++.xml}
 * merge convention.
 *
 * <p>
 * This class intentionally does something small and easy to read: it builds a
 * short label that identifies the plugin. Use it as a starting point for your
 * own helper, action, or service.
 * </p>
 *
 * <p>
 * Note how it follows the standard Fess idioms:
 * </p>
 * <ul>
 * <li>{@code @PostConstruct} for one-time initialization after DI wiring.</li>
 * <li>{@link ComponentUtil} to look up other Fess components (here the core
 * {@link SystemHelper}) instead of overriding or copying them.</li>
 * </ul>
 */
public class ExampleHelper {

    private static final Logger logger = LogManager.getLogger(ExampleHelper.class);

    /** Display name of this plugin. */
    protected String pluginName = "fess-webapp-example";

    /**
     * Default constructor.
     */
    public ExampleHelper() {
        // nothing
    }

    /**
     * Initializes this helper once after the DI container has wired it up.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialized {}.", getClass().getSimpleName());
        }
    }

    /**
     * Returns a short label identifying this plugin and the running Fess
     * version. The version is read from the core {@link SystemHelper} component
     * via {@link ComponentUtil}, demonstrating how a plugin can reuse core
     * components without overriding them.
     *
     * @return a label such as {@code "fess-webapp-example (Fess 15.8)"}
     */
    public String getPluginLabel() {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String version = systemHelper != null ? systemHelper.getProductVersion() : "unknown";
        return pluginName + " (Fess " + version + ")";
    }

    /**
     * Returns the configured plugin name.
     *
     * @return the plugin name
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * Sets the plugin name. Exposed so it can be configured from the DI XML if
     * desired.
     *
     * @param pluginName the plugin name to use
     */
    public void setPluginName(final String pluginName) {
        this.pluginName = pluginName;
    }
}
