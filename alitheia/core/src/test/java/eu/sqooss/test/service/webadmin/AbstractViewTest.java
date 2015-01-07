package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.Assert;

import org.junit.Test;

import eu.sqooss.impl.service.webadmin.AbstractView;

public class AbstractViewTest {

	static AbstractView aw;
	
	@Test
    public void testGetMessagesBundle() {
		Assert.assertEquals(ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH), AbstractView.getMessagesBundle(Locale.ENGLISH));
		Assert.assertEquals(ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH), AbstractView.getMessagesBundle(Locale.GERMAN));
		Assert.assertNotSame(ResourceBundle.getBundle("ResourceErrors", Locale.ENGLISH), AbstractView.getMessagesBundle(Locale.ENGLISH));   
	}
	
	@Test
    public void testGetErrorsBundle() {
		Assert.assertEquals(ResourceBundle.getBundle("ResourceErrors", Locale.ENGLISH), AbstractView.getErrorsBundle(Locale.ENGLISH));
		Assert.assertEquals(ResourceBundle.getBundle("ResourceErrors", Locale.ENGLISH), AbstractView.getErrorsBundle(Locale.GERMAN));
		Assert.assertNotSame(ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH), AbstractView.getErrorsBundle(Locale.ENGLISH));   
	}
	
	@Test
    public void testGetLabelsBundle() {
		Assert.assertEquals(ResourceBundle.getBundle("ResourceLabels", Locale.ENGLISH), AbstractView.getLabelsBundle(Locale.ENGLISH));
		Assert.assertEquals(ResourceBundle.getBundle("ResourceLabels", Locale.ENGLISH), AbstractView.getLabelsBundle(Locale.GERMAN));
		Assert.assertNotSame(ResourceBundle.getBundle("ResourceMessages", Locale.ENGLISH), AbstractView.getLabelsBundle(Locale.ENGLISH));   
	}

}
