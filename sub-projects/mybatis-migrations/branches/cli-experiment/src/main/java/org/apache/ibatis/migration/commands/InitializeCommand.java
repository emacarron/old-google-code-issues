/*
 *    Copyright 2009-2011 The MyBatis Team
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
package org.apache.ibatis.migration.commands;

import java.io.File;
import java.util.Properties;

import org.apache.ibatis.migration.CommandLine;
import org.apache.ibatis.migration.MigrationException;

import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Creates (if necessary) and initializes a migration path." )
public class InitializeCommand extends BaseCommand {

  public InitializeCommand(CommandLine commandLine)
  {
    super(commandLine);
  }

  public void execute() {
    commandLine.getPrintStream().println("Initializing: " + commandLine.basePath);

    createDirectoryIfNecessary(commandLine.basePath);
    ensureDirectoryIsEmpty(commandLine.basePath);

    createDirectoryIfNecessary(commandLine.envPath);
    createDirectoryIfNecessary(commandLine.scriptPath);
    createDirectoryIfNecessary(commandLine.driverPath);

    copyResourceTo("org/apache/ibatis/migration/template_README", baseFile("README"));
    copyResourceTo("org/apache/ibatis/migration/template_environment.properties", environmentFile());
    copyResourceTo("org/apache/ibatis/migration/template_bootstrap.sql", scriptFile("bootstrap.sql"));
    copyResourceTo("org/apache/ibatis/migration/template_changelog.sql", scriptFile(getNextIDAsString() + "_create_changelog.sql"));
    copyResourceTo("org/apache/ibatis/migration/template_migration.sql", scriptFile(getNextIDAsString() + "_first_migration.sql"),
        new Properties() {
          {
            setProperty("description", "First migration.");
          }
        });
    commandLine.getPrintStream().println("Done!");
    commandLine.getPrintStream().println();
  }

  protected void ensureDirectoryIsEmpty(File path) {
    String[] list = path.list();
    if (list.length != 0) {
      for (String entry : list) {
        if (!entry.startsWith(".")) {
          throw new MigrationException("Directory must be empty (.svn etc allowed): " + path.getAbsolutePath());
        }
      }
    }
  }

  protected void createDirectoryIfNecessary(File path) {
    if (!path.exists()) {
      commandLine.getPrintStream().println("Creating: " + path.getName());
      if (!path.mkdirs()) {
        throw new MigrationException("Could not create directory path for an unknown reason. Make sure you have access to the directory.");
      }
    }
  }


}
