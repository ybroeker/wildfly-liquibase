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
import liquibase.ScopeManager;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassLoader based ScopeManager to ensure each deployment gets its own unique scope
 */
public class WildFlyScopeManager extends ScopeManager {

    private static final WildFlyScopeManager INSTANCE = new WildFlyScopeManager();
    private static final Map<ClassLoader, Scope> SCOPES = new HashMap<>();

    private WildFlyScopeManager() {
    }

    public static WildFlyScopeManager getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized Scope getCurrentScope() {
        System.out.println("=====> Getting current scope: " + SCOPES.get(Thread.currentThread().getContextClassLoader()));
        return SCOPES.get(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public synchronized void setCurrentScope(Scope scope) {
        System.out.println("=====> Setting current scope: " + scope + " for : " + Thread.currentThread().getContextClassLoader());
        SCOPES.put(Thread.currentThread().getContextClassLoader(), scope);
    }

    public synchronized void removeScope(ClassLoader classLoader) {
        SCOPES.remove(classLoader);
    }

    @Override
    public Scope init(Scope scope) throws Exception {
        return scope;
    }
}
