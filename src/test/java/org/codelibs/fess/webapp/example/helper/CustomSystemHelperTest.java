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
import java.nio.file.Paths;

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.webapp.example.UnitWebappTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Verifies that {@link CustomSystemHelper} replaces the core {@code systemHelper}
 * component through the {@code fess+systemHelper.xml} redefine convention, and
 * that its {@link CustomSystemHelper#parseProjectProperties} override is resilient
 * to a parse failure.
 *
 * <p>
 * This test loads {@code test_systemhelper.xml}, which {@code <include>}s the
 * plugin's {@code fess+systemHelper.xml}, then retrieves {@code systemHelper} from
 * the DI container the same way Fess looks it up. This confirms the override XML
 * is valid and registers {@link CustomSystemHelper} in place of the core
 * {@code systemHelper}.
 * </p>
 */
public class CustomSystemHelperTest extends UnitWebappTestCase {

    @Override
    protected String prepareConfigFile() {
        return "test_systemhelper.xml";
    }

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        // SystemHelper#init() reads FessConfig, so provide a minimal one before
        // the container resolves "systemHelper".
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            // Returning blank keeps SystemHelper#updateSystemProperties() from
            // reading the "systemProperties" component, which this minimal test
            // container does not register.
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
        super.setUp(testInfo);
    }

    @Override
    public void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_systemHelper_isOverridden() {
        // fess+systemHelper.xml redefined "systemHelper" as CustomSystemHelper.
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        assertNotNull("systemHelper should be registered", systemHelper);
        assertTrue("systemHelper should be the overridden CustomSystemHelper", systemHelper instanceof CustomSystemHelper);
    }

    @Test
    public void test_parseProjectProperties_setsPluginFlagOnFailure() {
        // A non-existent path makes super.parseProjectProperties fail; the
        // override must swallow the error and still set the marker property.
        System.clearProperty("fess.webapp.plugin");
        final CustomSystemHelper helper = new CustomSystemHelper();
        final Path missing = Paths.get("no/such/project.properties");

        helper.parseProjectProperties(missing);

        assertEquals("plugin marker should be set even on parse failure", "true", System.getProperty("fess.webapp.plugin"));
    }
}
