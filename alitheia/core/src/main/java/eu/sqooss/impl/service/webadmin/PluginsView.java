/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.impl.service.webadmin;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.hibernate.Hibernate;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.pa.PluginInfo.ConfigurationType;
import eu.sqooss.service.util.StringUtils;

public class PluginsView extends AbstractView{

	private static StringBuilder e;
	// Info object of the selected plug-in
    private static PluginInfo selPI = null;
	private static String reqValPropName = null;
	private static String reqValPropDescr     = null;
	private static String reqValPropType      = null;
	private static String reqValPropValue     = null;
	private static String reqValAction        = "";
	private static String reqValHashcode      = null;  
	private static boolean reqValShowProp     = false;         // Show plug-in properties
	private static boolean reqValShowActv     = false;         // Show plug-in activators
    
    public PluginsView(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
        e = new StringBuilder();
    }

    public static boolean isPluginsListEmpty() {
    	return sobjPA.listPlugins().isEmpty();
    }
    
    public static Collection<PluginInfo> getPluginsList() {
    	return sobjPA.listPlugins();
    }
    
    public static String getErrorMessages() {
    	if(e.length() > 0) {
    		return e.toString();
    	} else {
    		return null;
    	}
    }
    
    public PluginInfo getSelectedPlugin() {
    	return selPI;
    }
    
    public static String getValPropName() {
    	return (reqValPropName != null) ? reqValPropName : "";
    }
    
    public static String getValPropType() {
    	return (reqValPropType != null) ? reqValPropType : "";
    }
    
    public static String getValPropDescr() {
    	return (reqValPropDescr != null) ? reqValPropDescr : "";
    }
    
    public static String getValPropValue() {
    	return (reqValPropValue != null) ? reqValPropValue : "";
    }
    
    public static String getValAction() {
    	return (reqValAction != null) ? reqValAction : "";
    }
    
    public static String getValHashcode() {
    	return (reqValHashcode != null) ? reqValHashcode : "";
    }
    
    public static boolean getValShowProp() {
    	return reqValShowProp;
    }
    
    public static boolean getValShowActv() {
    	return reqValShowActv;
    }
    
    public static ConfigurationType[] getConfigurationTypes() {
    	return ConfigurationType.values();
    }
    
    public static List<Metric> getPluginMetrics(PluginInfo pi) {
    	return sobjPA.getPlugin(pi).getAllSupportedMetrics();
    }
    
    public static boolean isPluginMetricsEmpty(PluginInfo pi) {
    	List<Metric> metrics = sobjPA.getPlugin(pi).getAllSupportedMetrics();
       	if ((metrics == null) || (metrics.isEmpty())) {
       		return true;
       	}
       	return false;
    }
    
    public static Set<PluginConfiguration> getPluginConfiguration(PluginInfo pi) {
    	return Plugin.getPluginByHashcode(pi.getHashcode()).getConfigurations();
    }
    
    public static boolean isPluginConfigurationEmpty(PluginInfo pi) {
    	Set<PluginConfiguration> config = Plugin.getPluginByHashcode(pi.getHashcode()).getConfigurations();
    	if ((config == null) || (config.isEmpty())) {
    		return true;
    	} else {
    		return false;
    	}
    }
 
    
    public static Collection<PluginInfo> getAllPlugins() {
    	return sobjPA.listPlugins();
    }
    
    public static void exec(HttpServletRequest req) {
    	// Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Stores the accumulated error messages
        e = new StringBuilder();
        selPI = null;
        reqValPropName = null;
        reqValPropDescr     = null;
        reqValPropType      = null;
        reqValPropValue     = null;
        reqValAction        = "";
        reqValHashcode      = null;
        reqValShowProp     = false;         // Show plug-in properties
        reqValShowActv     = false;         // Show plug-in activators
        // Indentation spacer
        long in = 6;

        // Request parameters
        String reqParAction        = "action";
        String reqParHashcode      = "pluginHashcode";
        String reqParPropName      = "propertyName";
        String reqParPropDescr     = "propertyDescription";
        String reqParPropType      = "propertyType";
        String reqParPropValue     = "propertyValue";
        String reqParShowProp      = "showProperties";
        String reqParShowActv      = "showActivators";
        // Recognized "action" parameter's values
        String actValInstall       = "installPlugin";
        String actValUninstall     = "uninstallPlugin";
        String actValSync          = "syncPlugin";
        String actValReqAddProp    = "createProperty";
        String actValReqUpdProp    = "updateProperty";
        String actValConAddProp    = "confirmProperty";
        String actValConRemProp    = "removeProperty";
        // Request values
        
        

        // Proceed only when at least one plug-in is registered
        if (!isPluginsListEmpty()){
            // ===============================================================
            // Parse the servlet's request object
            // ===============================================================
            if (req != null) {
                // DEBUG: Dump the servlet's request parameter
                if (DEBUG) {
                    b.append(debugRequest(req));
                }

                // Retrieve the selected editor's action (if any)
                reqValAction = req.getParameter(reqParAction);
                if (reqValAction == null) {
                    reqValAction = "";
                };
                // Retrieve the various display flags
                if ((req.getParameter(reqParShowProp) != null)
                        && (req.getParameter(reqParShowProp).equals("true"))) {
                    reqValShowProp = true;
                }
                if ((req.getParameter(reqParShowActv) != null)
                        && (req.getParameter(reqParShowActv).equals("true"))) {
                    reqValShowActv = true;
                }
                // Retrieve the selected configuration property's values
                if ((reqValAction.equals(actValConAddProp))
                        || (reqValAction.equals(actValReqUpdProp))
                        || (reqValAction.equals(actValConRemProp))) {
                    // Name, description, type and value
                    reqValPropName  = req.getParameter(reqParPropName);
                    reqValPropDescr = req.getParameter(reqParPropDescr);
                    reqValPropType  = req.getParameter(reqParPropType);
                    reqValPropValue = req.getParameter(reqParPropValue);
                }
                // Retrieve the selected plug-in's hash code
                reqValHashcode = req.getParameter(reqParHashcode);
                // Plug-in based actions
                if (reqValHashcode != null) {
                    // =======================================================
                    // Plug-in install request
                    // =======================================================
                    if (reqValAction.equals(actValInstall)) {
                        if (sobjPA.installPlugin(reqValHashcode) == false) {
                            e.append("Plug-in can not be installed!"
                                    + " Check log for details.");
                        }
                        // Persist the DB changes
                        else {
                            PluginInfo pInfo =
                                sobjPA.getPluginInfo(reqValHashcode);
                            sobjPA.pluginUpdated(sobjPA.getPlugin(pInfo));
                        }
                    }
                    // =======================================================
                    // Plug-in un-install request
                    // =======================================================
                    else if (reqValAction.equals(actValUninstall)) {
                        if (sobjPA.uninstallPlugin(reqValHashcode) == false) {
                            e.append("Plug-in can not be uninstalled."
                                    + " Check log for details.");
                        } else {
                            e.append("A job was scheduled to remove the plug-in");
                        }
                    } 
                }
                // Retrieve the selected plug-in's info object
                if (reqValHashcode != null) {
                    selPI = sobjPA.getPluginInfo(reqValHashcode);
                }
                // Plug-in info based actions
                if ((selPI != null) && (selPI.installed)) {
                    // =======================================================
                    // Plug-in synchronize (on all projects) request
                    // =======================================================
                    if (reqValAction.equals(actValSync)) {
                        compMA.syncMetrics(sobjPA.getPlugin(selPI));
                    }
                    // =======================================================
                    // Plug-in's configuration property removal
                    // =======================================================
                    else if (reqValAction.equals(actValConRemProp)) {
                        if (selPI.hasConfProp(
                                reqValPropName, reqValPropType)) {
                            try {
                                if (selPI.removeConfigEntry(
                                        sobjDB,
                                        reqValPropName,
                                        reqValPropType)) {
                                    // Update the Plug-in Admin's information
                                    sobjPA.pluginUpdated(
                                            sobjPA.getPlugin(selPI));
                                    // Reload the PluginInfo object
                                    selPI = sobjPA.getPluginInfo(
                                            reqValHashcode);
                                }
                                else {
                                    e.append("Property removal"
                                            + " has failed!"
                                            + " Check log for details.");
                                }
                            }
                            catch (Exception ex) {
                                e.append(ex.getMessage());
                            }
                        }
                        else {
                            e.append ("Unknown configuration property!");
                        }
                        // Return to the update view upon error
                        if (e.toString().length() > 0) {
                            reqValAction = actValReqUpdProp;
                        }
                    }
                    // =======================================================
                    // Plug-in's configuration property creation/update
                    // =======================================================
                    else if (reqValAction.equals(actValConAddProp)) {
                        // Check for a property update
                        boolean update = selPI.hasConfProp(
                                reqValPropName, reqValPropType);
                        // Update configuration property
                        if (update) {
                            try {
                                if (selPI.updateConfigEntry(
                                        sobjDB,
                                        reqValPropName,
                                        reqValPropValue)) {
                                    // Update the Plug-in Admin's information
                                    sobjPA.pluginUpdated(
                                            sobjPA.getPlugin(selPI));
                                    // Reload the PluginInfo object
                                    selPI =
                                        sobjPA.getPluginInfo(reqValHashcode);
                                }
                                else {
                                    e.append("Property update"
                                            + " has failed!"
                                            + " Check log for details.");
                                }
                            }
                            catch (Exception ex) {
                                e.append(ex.getMessage());
                            }
                        }
                        // Create configuration property
                        else {
                            try {
                                if (selPI.addConfigEntry(
                                        sobjDB,
                                        reqValPropName,
                                        reqValPropDescr,
                                        reqValPropType,
                                        reqValPropValue)) {
                                    // Update the Plug-in Admin's information
                                    sobjPA.pluginUpdated(
                                            sobjPA.getPlugin(selPI));
                                    // Reload the PluginInfo object
                                    selPI =
                                        sobjPA.getPluginInfo(reqValHashcode);
                                }
                                else {
                                    e.append("Property creation"
                                            + " has failed!"
                                            + " Check log for details.");
                                }
                            }
                            catch (Exception ex) {
                                e.append(ex.getMessage());
                            }
                        }
                        // Return to the create/update view upon error
                        if (e.toString().length() > 0) {
                            if (update) reqValAction = actValReqUpdProp;
                            else reqValAction = actValReqAddProp;
                        }
                    }
                }
            }
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
