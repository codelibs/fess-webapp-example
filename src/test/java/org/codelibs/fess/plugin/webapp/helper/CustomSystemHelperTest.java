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
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.utflute.lastaflute.LastaFluteTestCase;

public class CustomSystemHelperTest extends LastaFluteTestCase {

    @Override
    protected String prepareConfigFile() {
        return "test_app.xml";
    }

    @Override
    protected boolean isSuppressTestCaseTransaction() {
        return true;
    }

    @Override
    public void setUp() throws Exception {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getAppValue() {
                return "";
            }

            @Override
            public String getPathEncoding() {
                return "UTF-8";
            }

            @Override
            public String[] getSupportedLanguagesAsArray() {
                return new String[] { "ja" };
            }

        });
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_checkProperty() {
        assertEquals("true", System.getProperty("fess.webapp.plugin"));
    }

    public void test_parseProjectProperties_withValidPath() {
        // Given
        CustomSystemHelper helper = new CustomSystemHelper();
        Path validPath = Paths.get("src/test/resources/test_app.xml");

        // When
        helper.parseProjectProperties(validPath);

        // Then
        assertEquals("true", System.getProperty("fess.webapp.plugin"));
    }

    public void test_parseProjectProperties_withNullPath() {
        // Given
        CustomSystemHelper helper = new CustomSystemHelper();

        // When
        helper.parseProjectProperties(null);

        // Then
        assertEquals("true", System.getProperty("fess.webapp.plugin"));
    }

    public void test_parseProjectProperties_withNonExistentPath() {
        // Given
        CustomSystemHelper helper = new CustomSystemHelper();
        Path nonExistentPath = Paths.get("non/existent/path/project.properties");

        // When
        helper.parseProjectProperties(nonExistentPath);

        // Then
        assertEquals("true", System.getProperty("fess.webapp.plugin"));
    }

    public void test_parseProjectProperties_systemPropertyAlwaysSet() {
        // Given
        CustomSystemHelper helper = new CustomSystemHelper();
        System.clearProperty("fess.webapp.plugin");
        assertNull("Property should be cleared initially", System.getProperty("fess.webapp.plugin"));

        // When
        helper.parseProjectProperties(Paths.get("invalid/path"));

        // Then
        assertEquals("true", System.getProperty("fess.webapp.plugin"));
    }

    public void test_parseProjectProperties_multipleCalls() {
        // Given
        CustomSystemHelper helper = new CustomSystemHelper();
        Path testPath = Paths.get("src/test/resources/test_app.xml");

        // When
        helper.parseProjectProperties(testPath);
        helper.parseProjectProperties(testPath);
        helper.parseProjectProperties(null);

        // Then
        assertEquals("true", System.getProperty("fess.webapp.plugin"));
    }

    public void test_inheritance_extendsSystemHelper() {
        // Given
        CustomSystemHelper helper = new CustomSystemHelper();

        // Then
        assertTrue("CustomSystemHelper should extend SystemHelper", helper instanceof org.codelibs.fess.helper.SystemHelper);
    }

    public void test_loggerConfiguration() {
        // Given
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(CustomSystemHelper.class.getName());

        // Then
        assertNotNull("Logger should be configured", loggerConfig);
    }

    public void test_parseProjectProperties_withEmptyPath() {
        // Given
        CustomSystemHelper helper = new CustomSystemHelper();
        Path emptyPath = Paths.get("");

        // When
        helper.parseProjectProperties(emptyPath);

        // Then
        assertEquals("true", System.getProperty("fess.webapp.plugin"));
    }

    public void test_parseProjectProperties_propertyPersistence() {
        // Given
        CustomSystemHelper helper1 = new CustomSystemHelper();
        CustomSystemHelper helper2 = new CustomSystemHelper();
        System.clearProperty("fess.webapp.plugin");

        // When
        helper1.parseProjectProperties(null);
        String propertyAfterFirst = System.getProperty("fess.webapp.plugin");
        helper2.parseProjectProperties(null);
        String propertyAfterSecond = System.getProperty("fess.webapp.plugin");

        // Then
        assertEquals("true", propertyAfterFirst);
        assertEquals("true", propertyAfterSecond);
        assertEquals("Property should remain consistent", propertyAfterFirst, propertyAfterSecond);
    }

    public void test_parseProjectProperties_threadSafety() throws InterruptedException {
        // Given
        final CustomSystemHelper helper = new CustomSystemHelper();
        final int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        final boolean[] results = new boolean[threadCount];

        // When
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                helper.parseProjectProperties(null);
                results[index] = "true".equals(System.getProperty("fess.webapp.plugin"));
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        for (boolean result : results) {
            assertTrue("All threads should see the property set", result);
        }
    }
}
