package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.sun.xml.bind.CycleRecoverable.Context;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.AdminServlet;
import eu.sqooss.impl.service.webadmin.WebAdminRenderer;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerStats;
import eu.sqooss.service.util.Pair;
import eu.sqooss.service.webadmin.WebadminService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AlitheiaCore.class, WebAdminRenderer.class})
public class AdminServletTest {

	@Mock private BundleContext bc;
	@Mock private WebadminService webadmin;
	@Mock private Logger logger;
	@Mock private VelocityEngine ve;
	@Mock private AlitheiaCore core;
	@Mock private DBService dbs;
	@Mock private VelocityContext vc;
	@Mock private Scheduler scheduler;
	@Mock private HttpServletResponse response;
	@Mock private HttpServletRequest request;
	@Mock private Template template;
	@Mock private PrintWriter writer;
	@Mock private ServletOutputStream ostream;
	@Mock private AdminService as;
	@Mock private AdminAction aa;
	
	private AdminServlet servlet;

	@BeforeClass
    public static void setUp() 
	{
    }    
	
	private void initServlet() {
		mockStatic(AlitheiaCore.class);
		mockStatic(WebAdminRenderer.class);
		
		when(AlitheiaCore.getInstance()).thenReturn(core);	
		when(core.getDBService()).thenReturn(dbs);
		when(core.getAdminService()).thenReturn(as);

		//AdminAction aa = as.create(AddProject.MNEMONIC);
		when(as.create(any(String.class))).thenReturn(aa);
		
		servlet = new AdminServlet(bc, webadmin, logger, ve);

		Whitebox.setInternalState(servlet, vc);
		
		Whitebox.setInternalState(AbstractView.class, scheduler);
		when(scheduler.getSchedulerStats()).thenReturn(new SchedulerStats());		
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testConstructor() {
		initServlet();
		
		assertNotNull(servlet);

		//Make sure views are initialized
		assertNotNull(Whitebox.getInternalState(servlet, "vc"));
		assertNotNull(Whitebox.getInternalState(servlet, "adminView"));
		assertNotNull(Whitebox.getInternalState(servlet, "pluginsView"));
		assertNotNull(Whitebox.getInternalState(servlet, "projectsView"));
		
		//Make sure static and dynamic content are initialized
		assertEquals(((Hashtable<String, String>) Whitebox.getInternalState(servlet, "staticContentMap")).size(), 15);
		assertEquals(((Hashtable<String, String>) Whitebox.getInternalState(servlet, "dynamicContentMap")).size(), 10);
	}
	
	@Test
	public void testCreateSubstitutions() {
		initServlet();
		
		try {
			Whitebox.invokeMethod(servlet, "createSubstitutions", request);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		verify(vc, times(10)).put(any(String.class), any(Object.class));
	}

	@Test
	public void testSendPageException() {
		initServlet();
		
		String path = "";
		
		try {
			when(ve.getTemplate(any(String.class))).thenThrow(new IllegalArgumentException());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			Whitebox.invokeMethod(servlet, "sendPage", response, request, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//verify that response status is set correctly
		verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
	

	@Test
	public void testSendPage() {
		initServlet();
		
		String path = "";
		
		try {
			when(ve.getTemplate(any(String.class))).thenReturn(template);
			when(response.getWriter()).thenReturn(writer);
			
			Whitebox.invokeMethod(servlet, "sendPage", response, request, path);
			
			//Verify that merge was called on template
			verify(template, times(1)).merge(any(VelocityContext.class), any(PrintWriter.class));
			//verify that print was called on writer
			verify(writer, times(1)).print(any(String.class));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendResource() throws Exception {
		initServlet();
		when(response.getOutputStream()).thenReturn(ostream);
		
		String sourceFile = "/sqo-oss.png";
		String contentType = "image/x-png";
		
		Pair<String,String> source = new Pair<String,String>(sourceFile, contentType); 
		
		Whitebox.invokeMethod(servlet, "sendResource", response, source);
		
		//verify that whole image was been sent
		verify(ostream, times(6)).write(any(byte[].class), any(int.class), any(int.class));
	}


	@Test(expected=IOException.class)
	public void testSendResourceNotFound() throws Exception {
		initServlet();
		when(response.getOutputStream()).thenReturn(ostream);
		
		String sourceFile = "Non existing file";
		String contentType = "";
		
		Pair<String,String> source = new Pair<String,String>(sourceFile, contentType); 
		
		Whitebox.invokeMethod(servlet, "sendResource", response, source);
	}


	@Test(expected=IllegalArgumentException.class)
	public void testSendResourceNullResp() throws Exception {
		initServlet();
		when(response.getOutputStream()).thenReturn(ostream);

		String sourceFile = "/sqo-oss.png";
		String contentType = "image/x-png";
		
		Pair<String,String> source = new Pair<String,String>(sourceFile, contentType); 
		
		Whitebox.invokeMethod(servlet, "sendResource", (HttpServletResponse) null, source);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSendResourceNullMime() throws Exception {
		initServlet();
		when(response.getOutputStream()).thenReturn(ostream);

		String sourceFile = "/sqo-oss.png";
		String contentType = null;
		
		Pair<String,String> source = new Pair<String,String>(sourceFile, contentType); 
		
		Whitebox.invokeMethod(servlet, "sendResource", response, source);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSendResourceNullPath() throws Exception {
		initServlet();
		when(response.getOutputStream()).thenReturn(ostream);
		
		String sourceFile = "/sqo-oss.png";
		String contentType = null;
		
		Pair<String,String> source = new Pair<String,String>(sourceFile, contentType); 
		
		Whitebox.invokeMethod(servlet, "sendResource", response, source);
	}
	
	@Test
	public void testDoPostFails() throws Exception {
		initServlet();
		
		Whitebox.invokeMethod(servlet, "doPost", request, response);
		
		//verify that response status is set correctly
		verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
	

	@Test
	public void testDoPostGet() throws Exception {
		initServlet();

		when(request.getPathInfo()).thenReturn("");
		
		Whitebox.invokeMethod(servlet, "doPost", request, response);
		
		//TODO verify that get is done
	}

	@Test
	public void testDoPostAddProject() throws Exception {
		initServlet();

		when(ve.getTemplate(any(String.class))).thenReturn(template);
		when(response.getWriter()).thenReturn(writer);
		when(request.getPathInfo()).thenReturn("/addproject/ab1");
		
		Whitebox.invokeMethod(servlet, "doPost", request, response);
		
		//verify result page is sent
		verify(writer, times(1)).print(any(String.class));
	}
	

	@Test
	public void testDoPostDirAddProject() throws Exception {
		initServlet();

		when(request.getPathInfo()).thenReturn("/diraddproject/ab1");
		when(ve.getTemplate(any(String.class))).thenReturn(template);
		when(response.getWriter()).thenReturn(writer);
		
		Whitebox.invokeMethod(servlet, "doPost", request, response);
		

		//verify page is sent
		verify(writer, times(1)).print(any(String.class));
	}
	
	
	
	
}
