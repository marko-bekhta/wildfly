/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.as.jpa.beanmanager;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.el.ELAwareBeanManager;

import org.jboss.weld.util.ForwardingBeanManager;

/**
 * Proxy for Jakarta Contexts and Dependency Injection BeanManager
 *
 * @author Scott Marlow
 */
public class ProxyBeanManager extends ForwardingBeanManager {

    private volatile BeanManager delegate;

    // ForwardingBeanManager.delegate() returns BeanManager in Weld 6 and ELAwareBeanManager in Weld 7.
    // ELAwareBeanManager (from cdi-el-api) exists in both CDI 4.1 and 5.0 and is what Weld's BeanManager
    // implementations provide, so returning it satisfies the override for both Weld 6 (covariant) and Weld 7.
    @Override
    public ELAwareBeanManager delegate() {
        return (ELAwareBeanManager) delegate;
    }

    public void setDelegate(BeanManager delegate) {
        this.delegate = delegate;
    }
}
