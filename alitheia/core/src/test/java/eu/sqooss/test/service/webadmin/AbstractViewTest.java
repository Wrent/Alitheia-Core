package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.Assert;

import org.mockito.Mock;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.apache.velocity.VelocityContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.service.application.ScheduledApplication;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AlitheiaCore.class)
public class AbstractViewTest {

	@Mock
	BundleContext bc;
	@Mock
	VelocityContext vc;
	AbstractView aw;

	@Test
	public void testAbstractView() {
		AlitheiaCore core = mock(AlitheiaCore.class);
		LogManager lm = mock(LogManager.class);
		DBService db = mock(DBService.class);
		MetricActivator ma = mock(MetricActivator.class);
		PluginAdmin pa = mock(PluginAdmin.class);
		Scheduler s = mock(Scheduler.class);
		TDSService tds = mock(TDSService.class);
		UpdaterService us = mock(UpdaterService.class);
		ClusterNodeService cn = mock(ClusterNodeService.class);
		SecurityManager sm = mock(SecurityManager.class);
		Logger logger = mock(Logger.class);

		when(core.getLogManager()).thenReturn(null, lm);
		when(lm.createLogger(anyString())).thenReturn(logger);

		reinitAbstractView();

		Field field;
		try {
			field = getField("sobjCore");

			// test with no AlitheiaCore initialization
			Assert.assertNull(field.get(aw));

			PowerMockito.mockStatic(AlitheiaCore.class);
			given(AlitheiaCore.getInstance()).willReturn(core);

			// test AlitheiaCore initialization
			reinitAbstractView();
			Assert.assertEquals(core, field.get(aw));

			
			when(core.getDBService()).thenReturn(db, null);
			when(core.getPluginAdmin()).thenReturn(pa, null);
			when(core.getScheduler()).thenReturn(s, null);
			when(core.getMetricActivator()).thenReturn(ma, null);
			when(core.getTDSService()).thenReturn(tds, null);
			when(core.getUpdater()).thenReturn(us, null);
			when(core.getClusterNodeService()).thenReturn(null, cn);
			when(core.getMetricActivator()).thenReturn(ma, null);
			when(core.getSecurityManager()).thenReturn(sm, null);
			doNothing().when(logger).debug(anyString());
			
			// test Logger manager and logger
			reinitAbstractView();
			field = getField("sobjLogManager");
			Assert.assertEquals(lm, field.get(aw));
			field = getField("sobjLogger");
			Assert.assertEquals(logger, field.get(aw));

			// test all the other fields			
			field = getField("sobjDB");
			Assert.assertEquals(db, field.get(aw));
			field = getField("sobjPA");
			Assert.assertEquals(pa, field.get(aw));
			field = getField("sobjSched");
			Assert.assertEquals(s, field.get(aw));
			field = getField("compMA");
			Assert.assertEquals(ma, field.get(aw));
			field = getField("sobjTDS");
			Assert.assertEquals(tds, field.get(aw));
			field = getField("sobjUpdater");
			Assert.assertEquals(us, field.get(aw));
			field = getField("sobjSecurity");
			Assert.assertEquals(sm, field.get(aw));
			
			reinitAbstractView();
			field = getField("sobjClusterNode");
			Assert.assertEquals(cn, field.get(aw));
			verify(logger, times(8)).debug(anyString());

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testResourceGetters() {
		Assert.assertEquals("message", AbstractView.getMsg("message"));
		Assert.assertEquals("message", AbstractView.getErr("message"));
		Assert.assertEquals("message", AbstractView.getLbl("message"));
	}

	@Test
	public void testGetMsg() {
		AbstractView.initResources(Locale.ENGLISH);
		Assert.assertEquals("another message",
				AbstractView.getMsg("another message"));
		Assert.assertEquals("Check log for details.",
				AbstractView.getMsg("m0001"));
		Assert.assertEquals("Undefined parameter name!",
				AbstractView.getMsg(null));
	}

	@Test
	public void testGetErr() {
		AbstractView.initResources(Locale.ENGLISH);
		Assert.assertEquals("another message",
				AbstractView.getErr("another message"));
		Assert.assertEquals("Can not add this user to the selected group!",
				AbstractView.getErr("e0001"));
		Assert.assertEquals("Undefined parameter name!",
				AbstractView.getErr(null));
	}

	@Test
	public void testGetLbl() {
		AbstractView.initResources(Locale.ENGLISH);
		Assert.assertEquals("another message",
				AbstractView.getLbl("another message"));
		Assert.assertEquals("Alitheia", AbstractView.getLbl("l0001"));
		Assert.assertEquals("Undefined parameter name!",
				AbstractView.getLbl(null));
	}

	@Test
	public void testGetMessagesBundle() {
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH),
				AbstractView.getMessagesBundle(Locale.ENGLISH));
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH),
				AbstractView.getMessagesBundle(Locale.GERMAN));
		Assert.assertNotSame(
				ResourceBundle.getBundle("ResourceErrors", Locale.ENGLISH),
				AbstractView.getMessagesBundle(Locale.ENGLISH));
	}

	@Test
	public void testGetErrorsBundle() {
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceErrors", Locale.ENGLISH),
				AbstractView.getErrorsBundle(Locale.ENGLISH));
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceErrors", Locale.ENGLISH),
				AbstractView.getErrorsBundle(Locale.GERMAN));
		Assert.assertNotSame(
				ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH),
				AbstractView.getErrorsBundle(Locale.ENGLISH));
	}

	@Test
	public void testGetLabelsBundle() {
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceLabels", Locale.ENGLISH),
				AbstractView.getLabelsBundle(Locale.ENGLISH));
		Assert.assertEquals(
				ResourceBundle.getBundle("ResourceLabels", Locale.ENGLISH),
				AbstractView.getLabelsBundle(Locale.GERMAN));
		Assert.assertNotSame(
				ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH),
				AbstractView.getLabelsBundle(Locale.ENGLISH));
	}

	protected void reinitAbstractView() {
		aw = new AbstractView(bc, vc) {
		};
	}

	protected Field getField(String name) {
		Field field = null;
		try {
			field = AbstractView.class.getDeclaredField(name);
			field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return field;
	}

}
