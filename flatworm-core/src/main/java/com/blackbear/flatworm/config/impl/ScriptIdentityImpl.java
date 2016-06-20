/*
 * Flatworm - A Java Flat File Importer/Exporter Copyright (C) 2004 James M. Turner.
 * Extended by James Lawrence 2005
 * Extended by Josh Brackett in 2011 and 2012
 * Extended by Alan Henson in 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.blackbear.flatworm.config.impl;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.blackbear.flatworm.FileFormat;
import com.blackbear.flatworm.config.RecordBO;
import com.blackbear.flatworm.errors.FlatwormConfigurationException;
import com.blackbear.flatworm.errors.FlatwormParserException;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.URL;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import lombok.Getter;
import lombok.Setter;

/**
 * Manages script identity instances (script-ident) found in the flatworm configuration. This class is not thread safe.
 *
 * @author Alan Henson
 */
public class ScriptIdentityImpl extends AbstractIdentity {
    /**
     * Default JavaScript method name that will be invoked for Script Identity configurations.
     */
    public static final String DEFAULT_SCRIPT_METHOD_NAME = "matchesLine";

    /**
     * Default Script Engine to use - nashorn.
     */
    public static final String DEFAULT_SCRIPT_ENGINE = "nashorn";

    @Getter
    private String scriptEngineName;

    @Getter
    private String script;

    @Getter
    private String scriptFile;

    private ScriptEngine scriptEngine;

    @Getter
    private String methodName;

    private Invocable scriptInvocable;

    /**
     * Constructor for ScriptIdentityImpl. The default Script Engine is used ({@code ScriptIdentityImpl.DEFAULT_SCRIPT_ENGINE}) and the
     * default method name {@code ScriptIdentityImpl.DEFAULT_SCRIPT_METHOD_NAME} is used.
     */
    public ScriptIdentityImpl() throws FlatwormConfigurationException {
        this(DEFAULT_SCRIPT_ENGINE, DEFAULT_SCRIPT_METHOD_NAME);
    }

    /**
     * Constructor for ScriptIdentityImpl that attempts to load the Script Engine specified by name. The default method name ({@code
     * ScriptIdentityImpl.DEFAULT_SCRIPT_METHOD_NAME}) is used.
     *
     * @param scriptEngineName The name of the script engine to load. If {@code null} the {@code ScriptIdentityImpl.DEFAULT_SCRIPT_ENGINE}
     *                         is used.
     * @throws FlatwormConfigurationException should the script be invalid or should the {@code scriptEngineName} not resolve to a valid
     *                                        script engine.
     */
    public ScriptIdentityImpl(String scriptEngineName) throws FlatwormConfigurationException {
        this(scriptEngineName, DEFAULT_SCRIPT_METHOD_NAME);
    }

    /**
     * Constructor for ScriptIdentityImpl that attempts to load the Script Engine specified by name.
     *
     * @param scriptEngineName The name of the script engine to load. If {@code null} the {@code ScriptIdentityImpl.DEFAULT_SCRIPT_ENGINE}
     *                         is used.
     * @param methodName       The name of the method in the script that should be executed - the {@link com.blackbear.flatworm.FileFormat}
     *                         instance will be the only parameter sent to the function specified. If the {@code methodName} is {@code null}
     *                         then the {@code ScriptIdentityImpl.DEFAULT_SCRIPT_METHOD_NAME} value will be used.
     * @throws FlatwormConfigurationException should the script be invalid or should the {@code scriptEngineName} not resolve to a valid
     *                                        script engine.
     */
    public ScriptIdentityImpl(String scriptEngineName, String methodName) throws FlatwormConfigurationException {
        this.scriptEngineName = StringUtils.isBlank(scriptEngineName) ? DEFAULT_SCRIPT_ENGINE : scriptEngineName;
        this.methodName = StringUtils.isBlank(methodName) ? DEFAULT_SCRIPT_METHOD_NAME : methodName;

        ScriptEngineManager engineManager = new ScriptEngineManager();
        scriptEngine = engineManager.getEngineByName(this.scriptEngineName);

        if (scriptEngine == null) {
            throw new FlatwormConfigurationException(String.format("The %s ScriptEngine could not be found by the given name.",
                    this.scriptEngineName));
        } else {
            scriptInvocable = (Invocable) scriptEngine;
        }
    }

    /**
     * Set the script snippet to be used in running the {@code matchesIdentity} test.
     *
     * @param script The script that will be executed - should take two parameters with the first being (@link FileFormat} and the second
     *               being a {@link String}, which will be the line of data to examine. The script should return a {@link Boolean}.
     * @throws FlatwormConfigurationException should the script be invalid or should the {@code scriptEngineName} not resolve to a valid
     *                                        script engine.
     */
    public void setScript(String script) throws FlatwormConfigurationException {
        this.script = script;
        try {
            scriptEngine.eval(script);
        } catch (ScriptException e) {
            throw new FlatwormConfigurationException(String.format("The script provided failed to evaluate: %s%n%s",
                    e.getMessage(), script), e);
        }
    }

    /**
     * Set the script file to use where the script to evaluate identity matching is kept.
     * @param scriptFile The script file path (this will be loaded via the classloader.
     * @throws FlatwormConfigurationException should the file not be found, fail to be read, or contain invalid script 
     * accordingly the script engine specified.
     */
    public void setScriptFile(String scriptFile) throws FlatwormConfigurationException {
        this.scriptFile = scriptFile;
        try {
            URL url = Resources.getResource(scriptFile);
            String scriptContents = Resources.toString(url, Charsets.UTF_8);
            setScript(scriptContents);
        } catch (IOException e) {
            throw new FlatwormConfigurationException("Failed to load scriptFile: " + scriptFile, e);
        }
    }

    /**
     * Determine if the given {@link RecordBO} instance should be used to parse the line.
     *
     * @param record     The {@link RecordBO} instance whose {@link ScriptIdentityImpl} instance is being tested.
     * @param fileFormat The {@link FileFormat} instance representing the configuration that is driving the parsing and the last line that
     *                   was read.
     * @param line       The line of data to be evaluated.
     * @return {@code true} if the script determined that the last line should be parsed according to the {@code record}'s configuration and
     * {@code false} if not.
     * @throws FlatwormParserException should the script engine fail to invoke the script or should the return converterName of the script
     *                                 not be a {@code boolean} value.
     */
    @Override
    public boolean matchesIdentity(RecordBO record, FileFormat fileFormat, String line) throws FlatwormParserException {
        boolean matches = false;
        try {
            Object result = scriptInvocable.invokeFunction(methodName, fileFormat, line);
            if (result instanceof Boolean) {
                matches = Boolean.class.cast(result);
            } else if (result != null) {
                throw new FlatwormParserException(String.format("RecordBO %s has a script identifier that does not return" +
                        "a boolean value - a converterName of %s was returned.", record.getName(), result.getClass().getName()));
            }
        } catch (FlatwormParserException e) {
            throw e;
        } catch (Exception e) {
            throw new FlatwormParserException(e.getMessage(), e);
        }
        return matches;
    }
}
