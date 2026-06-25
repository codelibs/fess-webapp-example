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

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.webapp.example.UnitWebappTestCase;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link ExampleHelper} is registered through the additive
 * {@code app++.xml} merge convention and that its method works.
 *
 * <p>
 * The test retrieves the component from the DI container exactly as Fess does
 * at runtime, which proves the {@code app++.xml} wiring is correct.
 * </p>
 */
public class ExampleHelperTest extends UnitWebappTestCase {

    @Test
    public void test_exampleHelper_isRegisteredViaDi() {
        // app++.xml registered "exampleHelper"; look it up by name.
        final ExampleHelper exampleHelper = ComponentUtil.getComponent("exampleHelper");
        assertNotNull("exampleHelper should be registered via app++.xml", exampleHelper);
    }

    @Test
    public void test_getPluginName() {
        final ExampleHelper exampleHelper = ComponentUtil.getComponent("exampleHelper");
        assertEquals("default plugin name", "fess-webapp-example", exampleHelper.getPluginName());
    }

    @Test
    public void test_getPluginLabel_includesVersion() {
        // Register a core SystemHelper so the helper can read the product version.
        final SystemHelper systemHelper = new SystemHelper() {
            @Override
            public String getProductVersion() {
                return "15.7.0";
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");

        final ExampleHelper exampleHelper = ComponentUtil.getComponent("exampleHelper");
        assertEquals("label combines plugin name and Fess version", "fess-webapp-example (Fess 15.7.0)", exampleHelper.getPluginLabel());
    }
}
