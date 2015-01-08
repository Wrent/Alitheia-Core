package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AdminServlet;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.webadmin.WebadminService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AlitheiaCore.class)
public class AdminServletTest {

	@Mock private BundleContext bc;
	@Mock private WebadminService webadmin;
	@Mock private Logger logger;
	@Mock private VelocityEngine ve;
	@Mock private AlitheiaCore core;
	@Mock private DBService dbs;
	
	private AdminServlet servlet;

    public static void setUp() 
	{
    }
    
	
	@Test
	public void testConstructor() {
		mockStatic(AlitheiaCore.class);
		when(AlitheiaCore.getInstance()).thenReturn(core);	
		when(core.getDBService()).thenReturn(dbs);
		
		servlet = new AdminServlet(bc, webadmin, logger, ve);
		
		assertNotNull(servlet);
		
		//TODO verify private variables?
	}
}
