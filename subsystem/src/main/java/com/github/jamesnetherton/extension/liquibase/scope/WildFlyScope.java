package com.github.jamesnetherton.extension.liquibase.scope;

/*-
 * #%L
 * wildfly-liquibase-subsystem
 * %%
 * Copyright (C) 2017 - 2020 James Netherton
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

import liquibase.Scope;
import liquibase.logging.core.JavaLogService;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.servicelocator.StandardServiceLocator;
import liquibase.ui.ConsoleUIService;

import java.util.HashMap;
import java.util.Map;

public class WildFlyScope extends Scope {

    public WildFlyScope() {
        this(null, new HashMap<>());
    }

    public WildFlyScope(Scope parent, Map<String, Object> scopeValues) {
        super(parent, scopeValues);
        scopeValues.put(Attr.logService.name(), new JavaLogService());
        scopeValues.put(Attr.resourceAccessor.name(), new ClassLoaderResourceAccessor());
        scopeValues.put(Attr.serviceLocator.name(), new StandardServiceLocator());
        scopeValues.put(Attr.ui.name(), new ConsoleUIService());
    }
}
