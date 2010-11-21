/*
 *    Copyright 2010 The myBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.guice.configuration;

import java.util.Map.Entry;

import org.apache.ibatis.session.Configuration;

/**
 * 
 *
 * @version $Id$
 */
final class EachAlias extends AbstractConfigurationEach<Entry<String, Class<?>>> {

    public EachAlias(final Configuration configuration) {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    public void doHandle(Entry<String, Class<?>> alias) {
        this.getConfiguration().getTypeAliasRegistry().registerAlias(alias.getKey(), alias.getValue());
    }

}
