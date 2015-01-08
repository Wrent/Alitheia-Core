package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.awt.List;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.ProjectDeleteJob;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
public class ProjectDeleteJobTest {

	@Mock private StoredProject project;// = new StoredProject("Mock stored project name");
	@Mock private AlitheiaCore core;
	@Mock private DBService db;
	@Mock private ProjectVersion v1;
	@Mock private ProjectVersion v2;
	@Mock private PluginAdmin pa;
	@Mock private AlitheiaPlugin ap;
	private ProjectDeleteJob job = new ProjectDeleteJob(core, project);
	
	String newline = "\n";

	@BeforeClass
    public static void setUp() 
	{
		mockStatic(StoredProjectConfig.class);
    }

	@Test
	public void testPriority()
	{
		assertEquals(0xFF, job.priority());
	}

	@Test
	public void testToString()
	{
		job = new ProjectDeleteJob(core, project);
		
		when(project.toString()).thenReturn("Mock stored project name");
		assertEquals("ProjectDeleteJob - Project:{Mock stored project name}", job.toString());
	}

	@Test
	@PrepareForTest(StoredProjectConfig.class)
	public void testRun()
	{	
		job = new ProjectDeleteJob(core, project);
		
		when(core.getDBService()).thenReturn(db);
		when(db.attachObjectToDBSession(any(StoredProject.class))).thenReturn(project);
		when(project.getProjectVersions()).thenReturn(Arrays.asList(new ProjectVersion[] {v1, v2}));
		
		Plugin[] entities = new Plugin[] {}; //{new Plugin(), new Plugin()};
		doReturn(Arrays.asList(entities)).when(db).doHQL(any(String.class));

//		when(core.getPluginAdmin()).thenReturn(pa);
//		when(pa.getPluginInfo(entities[0].getHashcode())).thenReturn(new PluginInfo());
//		when(pa.getPlugin(any(PluginInfo.class))).thenReturn(ap);
		
		when(StoredProjectConfig.fromProject(any(StoredProject.class)))
			.thenReturn(Arrays.asList(new StoredProjectConfig[] {}));
		
		try {
			Whitebox.invokeMethod(job, "run"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Verify that db is called correctly
		verify(db, times(1)).isDBSessionActive();
		verify(db, times(1)).attachObjectToDBSession(any(StoredProject.class));
		
		//verify that parents have been cleared
		verify(v1, times(1)).getParents();
		verify(v2, times(1)).getParents();
	}
	

}
