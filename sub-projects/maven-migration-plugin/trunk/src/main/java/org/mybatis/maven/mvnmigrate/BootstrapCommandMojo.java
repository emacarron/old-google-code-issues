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
package org.mybatis.maven.mvnmigrate;

import org.apache.ibatis.migration.commands.BootstrapCommand;

/**
 * Goal which execute the ibatis migration bootstrap command.
 *
 * @version $Id$
 * @goal bootstrap
 */
public class BootstrapCommandMojo extends AbstractCommandMojo<BootstrapCommand> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected BootstrapCommand createCommandClass() {
        return new BootstrapCommand(this.getRepository(), this.getEnvironment(), this.isForce());
    }

}
