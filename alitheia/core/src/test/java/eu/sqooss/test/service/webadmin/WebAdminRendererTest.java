package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.powermock.reflect.Whitebox;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.webadmin.WebAdminRenderer;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerStats;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito.*;
import org.mockito.runners.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WebAdminRendererTest {

	//static WebAdminRenderer ;
	@Mock Scheduler sobjSched;
	@InjectMocks WebAdminRenderer renderer = new WebAdminRenderer(null, null);
	

    @BeforeClass
    public static void setUp() {
    	//BundleContext bc = new BundleContext();
    	//bc.setProperty("eu.sqooss.logbuffer.pattern")
    	
    	//renderer = new WebAdminRenderer(null, null);
    	
    	//Set up the AlitheiaCore instance
    	//AlitheiaCore.testInstance(); bc can't be null
//    	
//    	AlitheiaCore core = new AlitheiaCore(bc);
    }
    
    @Test
    public void testWebadminServiceImpl() {
        assertNotNull(renderer);
    }
    
    @Test
    public void testRenderJobFailStats() {
    	MockitoAnnotations.initMocks(this);
    	
    	Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
    	
    	when(sobjSched.getSchedulerStats()).thenReturn(new SchedulerStats());
    	String str = WebAdminRenderer.renderJobFailStats();
    }
    
    
}
