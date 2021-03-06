/*******************************************************************************
 * JBoss, Home of Professional Open Source
 * Copyright 2010-2013, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package org.richfaces.examples.richrates.ui;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.test.selenium.listener.ConsoleStatusTestListener;
import org.jboss.test.selenium.listener.FailureLoggingTestListener;
import org.openqa.selenium.WebDriver;
import org.richfaces.examples.richrates.CalculatorBean;
import org.richfaces.examples.richrates.annotation.ExchangeRates;
import org.testng.annotations.Listeners;

@Listeners({ ConsoleStatusTestListener.class, FailureLoggingTestListener.class })
public abstract class AbstractWebDriverTest extends Arquillian {

    @Drone
    private WebDriver driver;
    @ArquillianResource
    private URL deployedRoot;
    
    protected void loadPage(String url) {
        driver.get(deployedRoot.toExternalForm() + url);
    }

    @Deployment(testable = false)
    public static WebArchive createTestArchive() {
        File[] deps = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve()
            .withTransitivity().asFile();
        WebArchive war = ShrinkWrap.create(WebArchive.class, "richrates.war")
            .addPackages(false, CalculatorBean.class.getPackage(), ExchangeRates.class.getPackage())
            .addAsLibraries(deps);

        war.merge(ShrinkWrap.create(ExplodedImporter.class, "tmp1.war").importDirectory("src/main/webapp")
            .as(WebArchive.class));
        war.merge(
            ShrinkWrap.create(ExplodedImporter.class, "tmp2.war").importDirectory("src/main/resources")
                .as(WebArchive.class), "WEB-INF/classes");

        return war;
    }

    protected WebDriver getDriver() {
        return driver;
    }

}
