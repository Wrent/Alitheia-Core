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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

public class ProjectsView extends AbstractView {

    // Action parameter's values
    private static String ACT_REQ_ADD_PROJECT   = "reqAddProject";
    private static String ACT_CON_ADD_PROJECT   = "conAddProject";
    private static String ACT_REQ_REM_PROJECT   = "reqRemProject";
    private static String ACT_CON_REM_PROJECT   = "conRemProject";
    private static String ACT_REQ_SHOW_PROJECT  = "conShowProject";
    private static String ACT_CON_UPD_ALL       = "conUpdateAll";
    private static String ACT_CON_UPD           = "conUpdate";
    private static String ACT_CON_UPD_ALL_NODE  = "conUpdateAllOnNode";

    // Servlet parameters
    private static String REQ_PAR_ACTION        = "reqAction";
    private static String REQ_PAR_PROJECT_ID    = "projectId";
    private static String REQ_PAR_PRJ_NAME      = "projectName";
    private static String REQ_PAR_PRJ_WEB       = "projectHomepage";
    private static String REQ_PAR_PRJ_CONT      = "projectContact";
    private static String REQ_PAR_PRJ_BUG       = "projectBL";
    private static String REQ_PAR_PRJ_MAIL      = "projectML";
    private static String REQ_PAR_PRJ_CODE      = "projectSCM";
    private static String REQ_PAR_SYNC_PLUGIN   = "reqParSyncPlugin";
    private static String REQ_PAR_UPD           = "reqUpd";
    
    private static ArrayList<String> errors = new ArrayList<String>();
    
    /**
     * Instantiates a new projects view.
     *
     * @param bundlecontext the <code>BundleContext</code> object
     * @param vc the <code>VelocityContext</code> object
     */
    public ProjectsView(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }
    
    public static void exec(HttpServletRequest req) {

        // Initialize the resource bundles with the request's locale
        initResources(req.getLocale());
        
        // Request values
        String reqValAction        = "";
        Long   reqValProjectId     = null;

        // Selected project
        StoredProject selProject = null;

        //Delete old errors
    	errors.clear();

        // ===============================================================
        // Handle the servlet's request object
        // ===============================================================
        if (req != null) {
            // DEBUG: Dump the servlet's request parameter
            if (DEBUG) {
                //b.append(debugRequest(req));
            	//TODO find alternative
            }

            // Retrieve the selected editor's action (if any)
            reqValAction = req.getParameter(REQ_PAR_ACTION);
            
            // Retrieve the selected project's DAO (if any)
            reqValProjectId = fromString(req.getParameter(REQ_PAR_PROJECT_ID));
            if (reqValProjectId != null) {
                selProject = sobjDB.findObjectById(
                        StoredProject.class, reqValProjectId);
            }
            
            if (reqValAction == null) {
                reqValAction = "";
            } else if (reqValAction.equals(ACT_CON_ADD_PROJECT)) {
            	selProject = addProject(req);
            } else if (reqValAction.equals(ACT_CON_REM_PROJECT)) {
            	selProject = removeProject(selProject);
            } else if (reqValAction.equals(ACT_CON_UPD)) {
            	triggerUpdate(selProject, req.getParameter(REQ_PAR_UPD));
            } else if (reqValAction.equals(ACT_CON_UPD_ALL)) {
            	triggerAllUpdate(selProject);
            } else if (reqValAction.equals(ACT_CON_UPD_ALL_NODE)) {
            	triggerAllUpdateNode(selProject);
            } else {
            	// Retrieve the selected plug-in's hash-code
        		String reqValSyncPlugin = req.getParameter(REQ_PAR_SYNC_PLUGIN);
        		syncPlugin(selProject, reqValSyncPlugin);
            }
        }
        
    }

    private static StoredProject addProject(HttpServletRequest r) {
    	
        AdminService as = AlitheiaCore.getInstance().getAdminService();
    	AdminAction aa = as.create(AddProject.MNEMONIC);
    	aa.addArg("scm", r.getParameter(REQ_PAR_PRJ_CODE));
    	aa.addArg("name", r.getParameter(REQ_PAR_PRJ_NAME));
    	aa.addArg("bts", r.getParameter(REQ_PAR_PRJ_BUG));
    	aa.addArg("mail", r.getParameter(REQ_PAR_PRJ_MAIL));
    	aa.addArg("web", r.getParameter(REQ_PAR_PRJ_WEB));
    	as.execute(aa);
    	
    	if (aa.hasErrors()) {
            vc.put("RESULTS", aa.errors());
            return null;
    	} else { 
            vc.put("RESULTS", aa.results());
            return StoredProject.getProjectByName(r.getParameter(REQ_PAR_PRJ_NAME));
    	}		
    }
    
    // ---------------------------------------------------------------
    // Remove project
    // ---------------------------------------------------------------
    private static StoredProject removeProject(StoredProject selProject) {
    	if (selProject != null) {
			// Deleting large projects in the foreground is very slow
			ProjectDeleteJob pdj = new ProjectDeleteJob(sobjCore, selProject);
			try {
				sobjSched.enqueue(pdj);
			} catch (SchedulerException e1) {
				errors.add(getErr("e0034"));
			}
			selProject = null;
		} else {
			errors.add(getErr("e0034"));
		}
    	return selProject;
    }

	// ---------------------------------------------------------------
	// Trigger an update
	// ---------------------------------------------------------------
	private static void triggerUpdate(StoredProject selProject, String mnem) {
		AdminService as = AlitheiaCore.getInstance().getAdminService();
		AdminAction aa = as.create(UpdateProject.MNEMONIC);
		aa.addArg("project", selProject.getId());
		aa.addArg("updater", mnem);
		as.execute(aa);

		if (aa.hasErrors()) {
            vc.put("RESULTS", aa.errors());
        } else { 
            vc.put("RESULTS", aa.results());
        }
	}

	// ---------------------------------------------------------------
	// Trigger update on all resources for that project
	// ---------------------------------------------------------------
	private static void triggerAllUpdate(StoredProject selProject) {
	    AdminService as = AlitheiaCore.getInstance().getAdminService();
        AdminAction aa = as.create(UpdateProject.MNEMONIC);
        aa.addArg("project", selProject.getId());
        as.execute(aa);

        if (aa.hasErrors()) {
            vc.put("RESULTS", aa.errors());
        } else { 
            vc.put("RESULTS", aa.results());
        }
	}
	
	// ---------------------------------------------------------------
	// Trigger update on all resources on all projects of a node
	// ---------------------------------------------------------------
    private static void triggerAllUpdateNode(StoredProject selProject) {
		Set<StoredProject> projectList = ClusterNode.thisNode().getProjects();
		
		for (StoredProject project : projectList) {
			triggerAllUpdate(project);
		}
	}
	
	// ---------------------------------------------------------------
	// Trigger synchronize on the selected plug-in for that project
	// ---------------------------------------------------------------
    private static void syncPlugin(StoredProject selProject, String reqValSyncPlugin) {
		if ((reqValSyncPlugin != null) && (selProject != null)) {
			PluginInfo pInfo = sobjPA.getPluginInfo(reqValSyncPlugin);
			if (pInfo != null) {
				AlitheiaPlugin pObj = sobjPA.getPlugin(pInfo);
				if (pObj != null) {
					compMA.syncMetric(pObj, selProject);
					sobjLogger.debug("Syncronise plugin (" + pObj.getName()
							+ ") on project (" + selProject.getName() + ").");
				}
			}
		}
    }

	public static StoredProject getProjectById(Long id){
		StoredProject project = null;
		if(id != null)
			project = sobjDB.findObjectById(StoredProject.class, id);

		return project;
	}
    
    public static List<String> getErrors() {
    	return ProjectsView.errors;
    }
    
    public static Set<StoredProject> getProjects() {
    	return ClusterNode.thisNode().getProjects();
    }
    
    public static String getLastProjectVersion(StoredProject project) {
    	String lastVersion = getLbl("l0051");
        ProjectVersion v = ProjectVersion.getLastProjectVersion(project);
        if (v != null) {
            lastVersion = String.valueOf(v.getSequence()) + "(" + v.getRevisionId() + ")";
        }
        return lastVersion;
    }
    
    public static String getLastEmailDate(StoredProject project) {
    	String lastDate = getLbl("l0051");
        MailMessage mm = MailMessage.getLatestMailMessage(project);
        if (mm != null) {
        	lastDate = mm.getSendDate().toString();
        }
        return lastDate;
    }
    
    public static String getLastBug(StoredProject project) {
    	String lastBug = getLbl("l0051");
    	Bug bug = Bug.getLastUpdate(project);
        if (bug != null) {
        	lastBug = bug.getBugID();
        }
        return lastBug;
    }
    
    public static String getEvalState(StoredProject project) {
        String evalState = getLbl("project_not_evaluated");
        if (project.isEvaluated()) {
        	evalState = getLbl("project_is_evaluated");
        }
        return evalState;
    }
    
    public static String getClusternode(StoredProject project) {
	    String nodename = null;
	    if (project.getClusternode() != null) {
	        nodename = project.getClusternode().getName();
	    } else {
	        nodename = "(local)";
	    }
	    return nodename;
    }

    public static Set<Updater> getUpdaters(int selProjectId, String updaterStage) {
    	Set<Updater> updaters;
    	StoredProject selProject = sobjDB.findObjectById(StoredProject.class, selProjectId);
    	 
    	//TODO move to separate function?
    	UpdaterStage stage;
    	if(updaterStage == "inference")
    		stage = UpdaterStage.INFERENCE;
    	else if(updaterStage == "import")
    		stage = UpdaterStage.IMPORT;
    	else if(updaterStage == "parse")
    		stage = UpdaterStage.PARSE;
		else
    		stage = UpdaterStage.DEFAULT;

    	if(selProject != null)
    		updaters = sobjUpdater.getUpdaters(selProject, stage);
    	else
    		updaters =  Collections.emptySet();
    	return updaters;
    }
    
    public static String getClusterName() {
    	return sobjClusterNode.getClusterNodeName();
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

