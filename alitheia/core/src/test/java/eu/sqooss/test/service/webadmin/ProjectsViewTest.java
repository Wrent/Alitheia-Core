package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.apache.velocity.VelocityContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.ProjectsView;
import eu.sqooss.impl.service.webadmin.WebAdminRenderer;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ClusterNode.class, ProjectVersion.class, Bug.class, MailMessage.class})
public class ProjectsViewTest {
	@Mock private DBService sobjDB;
	@Mock private ClusterNodeService sobjClusterNode;
	@Mock private StoredProject projectMock;
	@Mock private UpdaterService sobjUpdater;

	@BeforeClass
    public static void setUp() 
	{
    }

	@Test
	public void testGetProjectByIdNull() {
		assertNull(ProjectsView.getProjectById((Long)null));
	}

	@Test
	public void testGetProjectById() {
		Whitebox.setInternalState(ProjectsView.class, sobjDB);
		
		StoredProject proj = new StoredProject();
		Long id = 1L;
		when(sobjDB.findObjectById(StoredProject.class, id)).thenReturn(proj);
		
		assertEquals(ProjectsView.getProjectById(id), proj);
	}
	
	@Test
	public void testGetProjects() {
		ClusterNode clusterNode = new ClusterNode();
		
		PowerMockito.mockStatic(ClusterNode.class);
		when(ClusterNode.thisNode()).thenReturn(clusterNode);

		assertEquals(clusterNode.getProjects(), ProjectsView.getProjects());
	}
	

	@Test
	public void testGetLastProjectVersionNull() {
		ProjectsView.initResources(null);
		assertEquals(ProjectsView.getLastProjectVersion(null), "n/a");
	}

	@Test
	public void testGetLastProjectVersionNonExisting() {
		PowerMockito.mockStatic(ProjectVersion.class);
		when(ProjectVersion.getLastProjectVersion(any(StoredProject.class)))
			.thenReturn(null);
		
		StoredProject project = new StoredProject();
		ProjectsView.initResources(null);
		
		assertEquals(ProjectsView.getLastProjectVersion(project), "n/a");
	}
	

	@Test
	public void testGetLastProjectVersion() {
		ProjectVersion pv = new ProjectVersion();
		pv.setRevisionId("1.0.4");
		StoredProject project = new StoredProject();
		
		ProjectsView.initResources(null);
		
		PowerMockito.mockStatic(ProjectVersion.class);
		when(ProjectVersion.getLastProjectVersion(project))
			.thenReturn(pv);
		
		assertEquals("0(1.0.4)", ProjectsView.getLastProjectVersion(project));
	}

	@Test
	public void testGetLastBugNull() {
		ProjectsView.initResources(null);
		assertEquals(ProjectsView.getLastBug(null), "n/a");
	}

	@Test
	public void testGetLastBugNonExisting() {
		StoredProject project = new StoredProject();
		
		ProjectsView.initResources(null);

		PowerMockito.mockStatic(Bug.class);
		when(Bug.getLastUpdate(project)).thenReturn(null);
		
		assertEquals("n/a", ProjectsView.getLastBug(project));
	}
	
	@Test
	public void testGetLastBug() {
		StoredProject project = new StoredProject();
		Bug bug = new Bug();
		bug.setBugID("Mock bug ID");
		
		ProjectsView.initResources(null);

		PowerMockito.mockStatic(Bug.class);
		when(Bug.getLastUpdate(project)).thenReturn(bug);
		
		assertEquals("Mock bug ID", ProjectsView.getLastBug(project));
	}
	

//    
//    public static String getLastEmailDate(StoredProject project) {
//    	String lastDate = getLbl("l0051");
//    	if(project != null) {
//	        MailMessage mm = MailMessage.getLatestMailMessage(project);
//	        if (mm != null) {
//	        	lastDate = mm.getSendDate().toString();
//	        }
//    	}
//        return lastDate;
//    }

	@Test
	public void testGetLastEmailDateNull() {
		ProjectsView.initResources(null);
		assertEquals("n/a", ProjectsView.getLastEmailDate(null));
	}
	@Test
	public void testGetLastEmailDateNonExisting() {
		StoredProject project = new StoredProject();
		
		ProjectsView.initResources(null);

		PowerMockito.mockStatic(MailMessage.class);
		when(MailMessage.getLatestMailMessage(project)).thenReturn(null);
		
		assertEquals("n/a", ProjectsView.getLastEmailDate(project));		
	}
	
	@Test
	public void testGetLastEmailDate() {
		StoredProject project = new StoredProject();
		MailMessage mm = new MailMessage();
		Calendar cal = Calendar.getInstance();
		cal.set(2015, 1, 12, 15, 23, 10);		
		mm.setSendDate(cal.getTime());
		
		ProjectsView.initResources(null);

		PowerMockito.mockStatic(MailMessage.class);
		when(MailMessage.getLatestMailMessage(project)).thenReturn(mm);
		
		assertEquals("Thu Feb 12 15:23:10 CET 2015", ProjectsView.getLastEmailDate(project));		
	}

	@Test
	public void testGetEvalStateNull() {
		ProjectsView.initResources(null);
		assertEquals("No", ProjectsView.getEvalState(null));
	}

	@Test
	public void testGetEvalStateNo() {
		when(projectMock.isEvaluated()).thenReturn(false);
		ProjectsView.initResources(null);

		assertEquals("No", ProjectsView.getEvalState(projectMock));	
	}

	@Test
	public void testGetEvalStateYes() {
		when(projectMock.isEvaluated()).thenReturn(true);
		ProjectsView.initResources(null);

		assertEquals("Yes", ProjectsView.getEvalState(projectMock));	
	}
	
	@Test
	public void testGetClusternodeNull() {
		ProjectsView.initResources(null);
		assertEquals("n/a", ProjectsView.getClusternode(null));
	}
	@Test
	public void testGetClusternodeLocal() {
		ProjectsView.initResources(null);
		when(projectMock.getClusternode()).thenReturn(null);
		
		assertEquals("(local)", ProjectsView.getClusternode(projectMock));
	}
	@Test
	public void testGetClusternodeRemote() {
		ProjectsView.initResources(null);
		when(projectMock.getClusternode()).thenReturn(new ClusterNode("Mock node name"));
		
		assertEquals("Mock node name", ProjectsView.getClusternode(projectMock));
	}

	@Test
	public void testGetUpdatersNull() {
		ProjectsView.initResources(null);
		Whitebox.setInternalState(ProjectsView.class, sobjUpdater);
		
		assertTrue(ProjectsView.getUpdaters(null, "default").isEmpty());
	}

	@Test
	public void testGetUpdaters() {
		StoredProject project = new StoredProject();
		ProjectsView.initResources(null);
		Whitebox.setInternalState(ProjectsView.class, sobjUpdater);
		
		HashSet<Updater> defaultSet = new HashSet<Updater>(Arrays.asList((Updater)null));
		HashSet<Updater> inferencetSet = new HashSet<Updater>(Arrays.asList((Updater)null, (Updater)null));
		HashSet<Updater> importSet = new HashSet<Updater>(Arrays.asList((Updater)null, (Updater)null, (Updater)null));
		HashSet<Updater> parseSet = new HashSet<Updater>();
		
		when(sobjUpdater.getUpdaters(project, UpdaterStage.DEFAULT)).thenReturn(defaultSet);
		when(sobjUpdater.getUpdaters(project, UpdaterStage.INFERENCE)).thenReturn(inferencetSet);
		when(sobjUpdater.getUpdaters(project, UpdaterStage.IMPORT)).thenReturn(importSet);
		when(sobjUpdater.getUpdaters(project, UpdaterStage.PARSE)).thenReturn(parseSet);

		assertEquals(defaultSet, ProjectsView.getUpdaters(project, "default"));
		assertEquals(inferencetSet, ProjectsView.getUpdaters(project, "inference"));
		assertEquals(importSet, ProjectsView.getUpdaters(project, "import"));
		assertEquals(parseSet, ProjectsView.getUpdaters(project, "parse"));
	}
	
	
	
	@Test
	public void testGetClusterName() {
		String clusterNodeName = "Mock cluster node name";
		Whitebox.setInternalState(ProjectsView.class, sobjClusterNode);
		when(sobjClusterNode.getClusterNodeName()).thenReturn(clusterNodeName);
    	
		assertEquals(ProjectsView.getClusterName(), clusterNodeName);
    }
	
	@Test
	public void testGetErrors() {
		ArrayList<String> errors = new ArrayList<String>(Arrays.asList("dummy error"));
		Whitebox.setInternalState(ProjectsView.class, errors);
		
		assertEquals(errors, ProjectsView.getErrors());
	}
}
