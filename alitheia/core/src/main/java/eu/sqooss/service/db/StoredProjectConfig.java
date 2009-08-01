/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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

package eu.sqooss.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;

public class StoredProjectConfig extends DAObject {

	private ConfigurationOption confOpt;
	private StoredProject project;
	private String value;
	
	public StoredProjectConfig() {}
	
	public StoredProjectConfig(ConfigurationOption co, String value, 
			StoredProject sp) {
		this.confOpt = co;
		this.value = value;
		this.project = sp;
	}
	
	public ConfigurationOption getConfOpt() {
		return confOpt;
	}
	
	public void setConfOpt(ConfigurationOption confOpt) {
		this.confOpt = confOpt;
	}
	
	public StoredProject getProject() {
		return project;
	}
	
	public void setProject(StoredProject project) {
		this.project = project;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public static List<StoredProjectConfig> fromProject(StoredProject sp) {
		DBService dbs = AlitheiaCore.getInstance().getDBService();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("project", sp);
		
		return dbs.findObjectsByProperties(StoredProjectConfig.class, params);
	}
}
