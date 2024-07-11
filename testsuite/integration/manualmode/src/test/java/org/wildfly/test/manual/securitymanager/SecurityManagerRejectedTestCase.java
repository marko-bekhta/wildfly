/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wildfly.test.manual.securitymanager;

import static org.junit.Assert.assertFalse;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.shared.util.AssumeTestGroupUtil;
import org.junit.After;
import org.junit.AssumptionViolatedException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.core.testrunner.ServerControl;

/**
 * Tests that starting a server with the SecurityManager enabled fails in an EE11 environment
 */
@RunAsClient()
@RunWith(Arquillian.class)
@ServerControl(manual = true)
public class SecurityManagerRejectedTestCase {

    private static final String SERVER_CONFIG_NAME = "forced-security-manager";
    @ArquillianResource
    private static volatile ContainerController containerController;

    @BeforeClass
    public static void ee11Only() {

        // If we are running in a testsuite execution with the SM explicitly enabled everywhere,
        // we can't be expecting servers to fail to boot with the SM.
        // So no point going further
        AssumeTestGroupUtil.assumeSecurityManagerDisabled();

        // Use a missing ManagedBean class as an indicator that we are in an EE 11+ environment.
        try {
            SecurityManagerRejectedTestCase.class.getClassLoader().loadClass("jakarta.annotation.ManagedBean");
            throw new AssumptionViolatedException("Not an EE 11+ environment");
        } catch (ClassNotFoundException e) {
            // not found means we want the test
        }
    }

    @After
    public void ensureContainerStopped() {
        // If the test fails, don't leave a running server behind
        if (containerController.isStarted(SERVER_CONFIG_NAME)) {
            containerController.stop(SERVER_CONFIG_NAME);
        }
    }

    @Test
    public void testServerStart() {
        assertFalse(containerController.isStarted(SERVER_CONFIG_NAME));
        try {
            // This config has -secmgr hard coded in its startup args, so it should fail to start
            containerController.start(SERVER_CONFIG_NAME);
        } catch (Exception ok) {
            // good. fall through and confirm the effect of this is the container wasn't started
        }
        assertFalse(containerController.isStarted(SERVER_CONFIG_NAME));
    }
}
