/*-
 * #%L
 * wildfly-liquibase-itests
 * %%
 * Copyright (C) 2017 James Netherton
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.jamesnetherton.extension.liquibase.test.deployment;

import com.github.jamesnetherton.liquibase.arquillian.ChangeLogDefinition;
import com.github.jamesnetherton.liquibase.arquillian.LiquibaseTestSupport;
import com.github.jamesnetherton.liquibase.arquillian.ResourceLocation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class LiquibaseWarWebInfDeploymentTest extends LiquibaseTestSupport {

    @ChangeLogDefinition(resourceLocation = ResourceLocation.WEB_INF)
    private String tableNameWebInf;

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, "liquibase-war-webinf-deployment-test.war");
    }

    @Test
    public void testWarDeploymentWebInfResource() throws Exception {
        assertTableModified(tableNameWebInf);
    }
}
