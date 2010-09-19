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

import java.io.File;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.ibatis.migration.commands.BaseCommand;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mybatis.maven.mvnmigrate.util.MavenOutputStream;


/**
 * Provides to an abstract class that extends {@link AbstractMojo}.
 *
 * @version $Id$
 */
abstract public class AbstractCommandMojo<T extends BaseCommand> extends AbstractMojo {

    protected Locale locale = Locale.ENGLISH;
    
    /**
     * Location of migrate repository.
     *
     * @parameter expression="${migration.path}" default-value="."
     */
    protected File repository;

    /**
     * Environment to configure. Default environment is 'development'.
     *
     * @parameter expression="${migration.env}" default-value="development"
     */
    protected String environment;

    /**
     * Forces script to continue even if SQL errors are encountered.
     *
     * @parameter  expression="${migration.force}" default-value="false"
     */
    protected boolean force;

    /**
     * Skip migration actions.
     *
     * @parameter  expression="${migration.skip}" default-value="false"
     */
    protected boolean skip;

    /**
     * The command to execute.
     */
    private T command;
    
    
    
    /**
     * 
     */
    public AbstractCommandMojo() {
        super();
    }    
       
    
    /**
     * execute the command.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isSkip()) return;
        init();
        command.execute();
    }
    

    /**
     * Initialize the ibatis command.
     */
    protected void init() throws MojoFailureException  {

        try {
            this.command = createCommandClass();
            final PrintStream out = new PrintStream(new MavenOutputStream(getLog()));
            this.command.setPrintStream(out);
            this.command.setDriverClassLoader(getClass().getClassLoader());
            if (getLog().isInfoEnabled()){
                String[] args = { this.command.getClass().getSimpleName(), getBundle(locale).getString("migration.plugin.name") };
                MessageFormat format = new MessageFormat(getBundle(locale).getString("migration.plugin.execution.command"));
                getLog().info(format.format(args));
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoFailureException(this, e.getMessage(), e
                    .getLocalizedMessage());
        }
    }

    /**
     * Return the command.
     * @return {@link BaseCommand} the command created.
     */
    protected T getCommand() {
        return this.command;
    }

    /**
     * Test if the skip flag is setted.
     * @return the skip flag.
     */
    protected boolean isSkip() {
        if (this.skip && this.getLog().isInfoEnabled()) {
            String[] args = { getBundle(locale).getString("migration.plugin.name") };
            MessageFormat format = new MessageFormat(getBundle(locale).getString("migration.plugin.execution.command.skipped"));
            getLog().info(format.format(args));
        }
        return skip;
    }

    /**
     * The current locale.
     *
     * @param locale
     */
    protected ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle("migration-plugin", locale, this.getClass().getClassLoader());
    }
    
    /**
     * Creates the specific mojo command.
     * 
     * @return The command created.
     */
    abstract T createCommandClass();
}


