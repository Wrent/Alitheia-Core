package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;

public class AbstractViewTest {

	static AbstractView aw;

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

}
