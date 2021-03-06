/*-
 * #%L
 * wildfly-liquibase-subsystem
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
package com.github.jamesnetherton.extension.liquibase;

import liquibase.servicelocator.PackageScanClassResolver;
import liquibase.servicelocator.ServiceLocator;

import com.github.jamesnetherton.extension.liquibase.deployment.LiquibaseChangeLogExecutionProcessor;
import com.github.jamesnetherton.extension.liquibase.deployment.LiquibaseChangeLogParseProcessor;
import com.github.jamesnetherton.extension.liquibase.deployment.LiquibaseDependenciesProcessor;
import com.github.jamesnetherton.extension.liquibase.deployment.LiquibaseJBossAllParser;
import com.github.jamesnetherton.extension.liquibase.service.ChangeLogModelService;
import com.github.jamesnetherton.extension.liquibase.service.ChangeLogConfigurationRegistryService;
import com.github.jamesnetherton.extension.liquibase.service.ServiceHelper;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.jbossallxml.JBossAllXmlParserRegisteringProcessor;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;

class LiquibaseSubsystemAdd extends AbstractBoottimeAddStepHandler {

    private static final int STRUCTURE_LIQUIBASE_JBOSS_ALL = Phase.STRUCTURE_PARSE_JBOSS_ALL_XML - 0x01;
    private static final int DEPENDENCIES_LIQUIBASE = Phase.DEPENDENCIES_SINGLETON_DEPLOYMENT + 0x01;
    private static final int INSTALL_LIQUIBASE_CHANGE_LOG = Phase.INSTALL_MDB_DELIVERY_DEPENDENCIES + 0x01;
    private static final int INSTALL_LIQUIBASE_MIGRATION_EXECUTION = INSTALL_LIQUIBASE_CHANGE_LOG + 0x01;

    @Override
    protected void performBoottime(OperationContext context, ModelNode operation, ModelNode model) throws OperationFailedException {
        LiquibaseLogger.ROOT_LOGGER.info("Activating Liquibase Subsystem");

        ServiceTarget serviceTarget = context.getServiceTarget();

        ChangeLogConfigurationRegistryService registryService = new ChangeLogConfigurationRegistryService();

        ServiceName modelUpdateServiceName = ChangeLogModelService.getServiceName();
        ChangeLogModelService modelUpdateService = new ChangeLogModelService(registryService);
        ServiceHelper.installService(modelUpdateServiceName, serviceTarget, modelUpdateService);

        context.addStep(new AbstractDeploymentChainStep() {
            public void execute(DeploymentProcessorTarget processorTarget) {
                DeploymentUnitProcessor parser = new JBossAllXmlParserRegisteringProcessor<>(LiquibaseJBossAllParser.ROOT_ELEMENT,
                        LiquibaseConstants.LIQUIBASE_CHANGELOG_BUILDERS, new LiquibaseJBossAllParser());
                processorTarget.addDeploymentProcessor(LiquibaseExtension.SUBSYSTEM_NAME, Phase.STRUCTURE, STRUCTURE_LIQUIBASE_JBOSS_ALL, parser);
                processorTarget.addDeploymentProcessor(LiquibaseExtension.SUBSYSTEM_NAME, Phase.DEPENDENCIES, DEPENDENCIES_LIQUIBASE, new LiquibaseDependenciesProcessor());
                processorTarget.addDeploymentProcessor(LiquibaseExtension.SUBSYSTEM_NAME, Phase.INSTALL, INSTALL_LIQUIBASE_CHANGE_LOG, new LiquibaseChangeLogParseProcessor());
                processorTarget.addDeploymentProcessor(LiquibaseExtension.SUBSYSTEM_NAME, Phase.INSTALL, INSTALL_LIQUIBASE_MIGRATION_EXECUTION, new LiquibaseChangeLogExecutionProcessor(registryService));
            }
        }, OperationContext.Stage.RUNTIME);

        // Avoid using TCCL for class loading
        ServiceLocator.setInstance(new WildFlyLiquibaseServiceLocator());
    }

    static class WildFlyLiquibaseServiceLocator extends ServiceLocator {
        @Override
        protected PackageScanClassResolver defaultClassLoader() {
            PackageScanClassResolver classResolver = super.defaultClassLoader();
            classResolver.addClassLoader(ServiceLocator.class.getClassLoader());
            return classResolver;
        }
    }
}
