package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
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
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ClusterNode.class)
public class ProjectsViewTest {
	@Mock private DBService sobjDB;

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
		PowerMockito.mockStatic(ClusterNode.class);
	}
}
