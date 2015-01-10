/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.util.StringUtils;
//TODO why is this not a static class?
/**
 * The WebAdminRender class provides functions for rendering content
 * to be displayed within the WebAdmin interface.
 *
 * @author, Paul J. Adams <paul.adams@siriusit.co.uk>
 * @author, Boryan Yotov <b.yotov@prosyst.com>
 */
public class WebAdminRenderer  extends AbstractView {
    /**
     * Represents the system time at which the WebAdminRender (and
     * thus the system) was started. This is required for the system
     * uptime display.
     */
    private static long startTime = new Date().getTime();

    public WebAdminRenderer(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }
    
    public static HashMap<String,Integer> getFailedJobStats() {
    	return sobjSched.getSchedulerStats().getFailedJobTypes();
    }

    public static Job[] getFailedJobs() {
    	return sobjSched.getFailedQueue();
    }
    
    public static boolean isFailedJobsEmpty() {
    	Job[] jobs = sobjSched.getFailedQueue();
    	if ((jobs != null) && (jobs.length > 0)) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    /**
     * Creates an HTML safe list of the logs
     * 
     * @return List with HTML safe log strings
     */
    public static List<String> getLogs() {
        String[] names = sobjLogManager.getRecentEntries();
        ArrayList<String> logs = new ArrayList<String>();
        for (String name : names) {
        	logs.add(StringUtils.makeXHTMLSafe(name));
        }
        return logs;
    }

    /**
     * Returns a string representing the uptime of the Alitheia core
     * in dd:hh:mm:ss format
     */
    public static String getUptime() {
        long remainder;
        long currentTime = new Date().getTime();
        long timeRunning = currentTime - startTime;

        // Get the elapsed time in days, hours, mins, secs
        int days = new Long(timeRunning / 86400000).intValue();
        remainder = timeRunning % 86400000;
        int hours = new Long(remainder / 3600000).intValue();
        remainder = remainder % 3600000;
        int mins = new Long(remainder / 60000).intValue();
        remainder = remainder % 60000;
        int secs = new Long(remainder / 1000).intValue();

        return String.format("%d:%02d:%02d:%02d", days, hours, mins, secs);
    }

    //TODO ADD REFACTORING Assumes nothing about schedulerstats in sobjSched
    //TODO add description
    //TODO This is a copy of the renderJobFailStats, is this called anywhere?
    public static String renderJobWaitStats() {
        StringBuilder result = new StringBuilder();
        HashMap<String,Integer> wjobs = sobjSched.getSchedulerStats().getWaitingJobTypes();
        result.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
        result.append("\t<thead>\n");
        result.append("\t\t<tr>\n");
        result.append("\t\t\t<td>Job Type</td>\n");
        result.append("\t\t\t<td>Num Jobs Waiting</td>\n");
        result.append("\t\t</tr>\n");
        result.append("\t</thead>\n");
        result.append("\t<tbody>\n");

        String[] jobfailures = wjobs.keySet().toArray(new String[1]);
        for(String key : jobfailures) {
            result.append("\t\t<tr>\n\t\t\t<td>");
            result.append(key==null ? "No failures" : key);
            result.append("</td>\n\t\t\t<td>");
            result.append(key==null ? "&nbsp;" : wjobs.get(key));
            result.append("\t\t\t</td>\n\t\t</tr>");
        }
        result.append("\t</tbody>\n");
        result.append("</table>");
        return result.toString();
    }

    //TODO add description
    //TODO multiple returns
    public static String renderJobRunStats() {
        StringBuilder result = new StringBuilder();
        List<String> rjobs = sobjSched.getSchedulerStats().getRunJobs();
        if (rjobs.size() == 0) {
            return "No running jobs";
        }
        result.append("<ul>\n");
        for(String s : rjobs) {
            result.append("\t<li>");
            result.append(s);
            result.append("\t</li>\n");
        }
        result.append("</ul>\n");
        return result.toString();
    }
   
}

