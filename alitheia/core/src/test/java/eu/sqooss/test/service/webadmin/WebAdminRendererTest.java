package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.impl.service.webadmin.WebAdminRenderer;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerStats;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Job.class)
public class WebAdminRendererTest {

	@Mock Scheduler sobjSched;
	@Mock LogManager sobjLogManager;    	
	@Mock Job job1;    	
	@Mock Job job2;
	
	String newline = "\n";

	@BeforeClass
    public static void setUp() 
	{
    }
        
    @Test
    public void testRenderJobFailStatsEmpty() {
    	
    	Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
    	
    	SchedulerStats stats = new SchedulerStats(); 
    	when(sobjSched.getSchedulerStats()).thenReturn(stats);

    	String noFailsTruth = 
    			"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" + newline +
    			"	<thead>" + newline + 
    			"		<tr>" + newline + 
    			"			<td>Job Type</td>" + newline + 
    			"			<td>Num Jobs Failed</td>" + newline + 
    			"		</tr>" + newline + 
    			"	</thead>" + newline + 
    			"	<tbody>" + newline + 
    			"		<tr>" + newline + 
    			"			<td>No failures</td>" + newline + 
    			"			<td>&nbsp;			</td>" + newline + 
    			"		</tr>	</tbody>" + newline + 
    			"</table>";
    	    	
    	String noFails = WebAdminRenderer.renderJobFailStats();
    	assertEquals(noFailsTruth, noFails);    	
    }
    

    @Test
    public void testRenderJobFailStatsMultiple() {
    	
    	Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
    	
    	SchedulerStats stats = new SchedulerStats(); 
    	stats.addFailedJob("Job name 1");
    	stats.addFailedJob("Job name 2");
    	
    	when(sobjSched.getSchedulerStats()).thenReturn(stats);

    	String multipleFailsTruth = 
    			"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" + newline +
    			"	<thead>" + newline + 
    			"		<tr>" + newline + 
    			"			<td>Job Type</td>" + newline + 
    			"			<td>Num Jobs Failed</td>" + newline + 
    			"		</tr>" + newline + 
    			"	</thead>" + newline + 
    			"	<tbody>" + newline + 
    			"		<tr>" + newline + 
    			"			<td>Job name 2</td>" + newline + 
    			"			<td>1			</td>" + newline + 
    			"		</tr>		<tr>" + newline + 
    			"			<td>Job name 1</td>" + newline + 
    			"			<td>1			</td>" + newline + 
    			"		</tr>	</tbody>" + newline + 
    			"</table>";
    	
    	String multipleFails = WebAdminRenderer.renderJobFailStats();
    	assertEquals(multipleFailsTruth, multipleFails);
    }

    @Test
    public void testRenderLogsMultiple() {
    	Whitebox.setInternalState(WebAdminRenderer.class, sobjLogManager);
    	String[] logEntries = {"Log entry 1", "Log entry 2"};
    	when(sobjLogManager.getRecentEntries()).thenReturn(logEntries);
    	String multipleLogEntries = WebAdminRenderer.renderLogs();
    	String multipleLogEntriesTruth = 
    			"					<li>Log entry 1</li>" + newline +
    			"					<li>Log entry 2</li>" + newline;
    	
    	assertEquals(multipleLogEntriesTruth, multipleLogEntries);
    }    
    
    @Test
    public void testRenderLogsNone() {
    	Whitebox.setInternalState(WebAdminRenderer.class, sobjLogManager);
    	when(sobjLogManager.getRecentEntries()).thenReturn(new String[0]);
    	String logEntries = WebAdminRenderer.renderLogs();
    	String logEntriesTruth = 
    			"					<li>&lt;none&gt;</li>" + newline;
    	
    	assertEquals(logEntries, logEntriesTruth);
    	
    	when(sobjLogManager.getRecentEntries()).thenReturn(null);
    	logEntries = WebAdminRenderer.renderLogs();
    	assertEquals(logEntriesTruth, logEntries);
    }
    
    @Test
    public void testGetUptime() {
    	String uptimeTruth = "25:02:03:02";
    	
    	long days = 25L * 24L * 60L * 60L * 1000L;
    	long hours = 2L * 60L * 60L * 1000L;
    	long minutes = 2L * 60L * 1000L;
    	long seconds = 62L * 1000L;
    	long dateEntry = new Date().getTime() - days - hours - minutes - seconds;
    	
    	Whitebox.setInternalState(WebAdminRenderer.class, dateEntry);
    	String uptime = WebAdminRenderer.getUptime();
    	assertEquals(uptimeTruth, uptime);
    }
    
    @Test    
    public void testRenderJobWaitStatsEmpty() {
    	Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
    	
    	SchedulerStats stats = new SchedulerStats(); 
    	when(sobjSched.getSchedulerStats()).thenReturn(stats);

    	String noFailsTruth = 
    			"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" + newline +
    			"	<thead>" + newline + 
    			"		<tr>" + newline + 
    			"			<td>Job Type</td>" + newline + 
    			"			<td>Num Jobs Waiting</td>" + newline + 
    			"		</tr>" + newline + 
    			"	</thead>" + newline + 
    			"	<tbody>" + newline + 
    			"		<tr>" + newline + 
    			"			<td>No failures</td>" + newline + 
    			"			<td>&nbsp;			</td>" + newline + 
    			"		</tr>	</tbody>" + newline + 
    			"</table>";
    	    	
    	String noFails = WebAdminRenderer.renderJobWaitStats();
    	assertEquals(noFailsTruth, noFails);  
    }
    
    @Test    
    public void testRenderJobWaitStatsMultiple() {
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
    	
    	SchedulerStats stats = new SchedulerStats(); 
    	stats.addWaitingJob("Job name 1");
    	stats.addWaitingJob("Job name 2");
    	
    	when(sobjSched.getSchedulerStats()).thenReturn(stats);

    	String jobsTruth = 
    			"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" + newline +
    			"	<thead>" + newline + 
    			"		<tr>" + newline + 
    			"			<td>Job Type</td>" + newline + 
    			"			<td>Num Jobs Waiting</td>" + newline + 
    			"		</tr>" + newline + 
    			"	</thead>" + newline + 
    			"	<tbody>" + newline + 
    			"		<tr>" + newline + 
    			"			<td>Job name 2</td>" + newline + 
    			"			<td>1			</td>" + newline + 
    			"		</tr>		<tr>" + newline + 
    			"			<td>Job name 1</td>" + newline + 
    			"			<td>1			</td>" + newline + 
    			"		</tr>	</tbody>" + newline + 
    			"</table>";
    	
    	String jobsOut = WebAdminRenderer.renderJobWaitStats();
    	assertEquals(jobsTruth, jobsOut);
    }

    @Test
    public void testRenderJobRunStatsEmpty() {
    	String jobsTruth = "No running jobs";
    	
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);    	
    	SchedulerStats stats = new SchedulerStats(); 
    	when(sobjSched.getSchedulerStats()).thenReturn(stats);
    	    	
    	String jobsOut = WebAdminRenderer.renderJobRunStats();
    	assertEquals(jobsTruth, jobsOut);
    }

    @Test
    public void testRenderJobRunStatsMultiple() {
    	String jobsTruth =
    			"<ul>" + newline +
				"	<li>job	</li>" + newline +
				"	<li>job	</li>" + newline +
				"</ul>" + newline;
    	
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);    	
    	SchedulerStats stats = new SchedulerStats();
    	stats.addRunJob(job1);
    	stats.addRunJob(job2);
    	when(sobjSched.getSchedulerStats()).thenReturn(stats);
    	    	
    	String jobsOut = WebAdminRenderer.renderJobRunStats();
    	assertEquals(jobsTruth, jobsOut);
    }
    
    @Test
    public void testRenderFailedJobsNone() {
    	String jobsTruth = 
    			"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" + newline +
				"	<thead>" + newline +
				"		<tr>" + newline +
				"			<td>Job Type</td>" + newline +
				"			<td>Exception type</td>" + newline +
				"			<td>Exception text</td>" + newline +
				"			<td>Exception backtrace</td>" + newline +
				"		</tr>" + newline +
				"	</thead>" + newline +
				"	<tbody>" + newline +
				"<tr><td colspan=\"4\">No failed jobs.</td></tr>	</tbody>" + newline +
				"</table>";
    	
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
    	String jobsOut = WebAdminRenderer.renderFailedJobs();
    	assertEquals(jobsTruth, jobsOut);
    	
    	when(sobjSched.getFailedQueue()).thenReturn(new Job[] {});
    	jobsOut = WebAdminRenderer.renderFailedJobs();
    	assertEquals(jobsTruth, jobsOut);
    }
    
    @Test
    public void testRenderFailedJobsMultiple() {
    	String jobsTruth = 
    			"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" + newline +
				"	<thead>" + newline +
				"		<tr>" + newline +
				"			<td>Job Type</td>" + newline +
				"			<td>Exception type</td>" + newline +
				"			<td>Exception text</td>" + newline +
				"			<td>Exception backtrace</td>" + newline +
				"		</tr>" + newline +
				"	</thead>" + newline +
				"	<tbody>" + newline +
				"		<tr>" + newline +
				"			<td>job</td>" + newline +
				"			<td><b>NA</b></td>" + newline +
				"			<td><b>NA<b></td>" + newline +
				"			<td><b>NA</b>			</td>" + newline +
				"		</tr>		<tr>" + newline +
				"			<td>job</td>" + newline +
				"			<td>java.lang. IllegalArgumentException</td>" + newline +
				"			<td>Exception text 2</td>" + newline +
				"			<td>eu.sqooss.test.service.webadmin.WebAdminRendererTest. testRenderFailedJobsMultiple(), (WebAdminRendererTest.java:284)<br/>sun.reflect.NativeMethodAccessorImpl. invoke0(), (NativeMethodAccessorImpl.java:-2)<br/>sun.reflect.NativeMethodAccessorImpl. invoke(), (NativeMethodAccessorImpl.java:57)<br/>sun.reflect.DelegatingMethodAccessorImpl. invoke(), (DelegatingMethodAccessorImpl.java:43)<br/>java.lang.reflect.Method. invoke(), (Method.java:606)<br/>org.junit.internal.runners.TestMethod. invoke(), (TestMethod.java:66)<br/>org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl$PowerMockJUnit44MethodRunner. runTestMethod(), (PowerMockJUnit44RunnerDelegateImpl.java:310)<br/>org.junit.internal.runners.MethodRoadie$2. run(), (MethodRoadie.java:86)<br/>org.junit.internal.runners.MethodRoadie. runBeforesThenTestThenAfters(), (MethodRoadie.java:94)<br/>org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl$PowerMockJUnit44MethodRunner. executeTest(), (PowerMockJUnit44RunnerDelegateImpl.java:294)<br/>org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl$PowerMockJUnit44MethodRunner. runBeforesThenTestThenAfters(), (PowerMockJUnit44RunnerDelegateImpl.java:282)<br/>org.junit.internal.runners.MethodRoadie. runTest(), (MethodRoadie.java:84)<br/>org.junit.internal.runners.MethodRoadie. run(), (MethodRoadie.java:49)<br/>org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl. invokeTestMethod(), (PowerMockJUnit44RunnerDelegateImpl.java:207)<br/>org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl. runMethods(), (PowerMockJUnit44RunnerDelegateImpl.java:146)<br/>org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl$1. run(), (PowerMockJUnit44RunnerDelegateImpl.java:120)<br/>org.junit.internal.runners.ClassRoadie. runUnprotected(), (ClassRoadie.java:34)<br/>org.junit.internal.runners.ClassRoadie. runProtected(), (ClassRoadie.java:44)<br/>org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl. run(), (PowerMockJUnit44RunnerDelegateImpl.java:118)<br/>org.powermock.modules.junit4.common.internal.impl.JUnit4TestSuiteChunkerImpl. run(), (JUnit4TestSuiteChunkerImpl.java:101)<br/>org.powermock.modules.junit4.common.internal.impl.AbstractCommonPowerMockRunner. run(), (AbstractCommonPowerMockRunner.java:53)<br/>org.powermock.modules.junit4.PowerMockRunner. run(), (PowerMockRunner.java:53)<br/>org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference. run(), (JUnit4TestReference.java:50)<br/>org.eclipse.jdt.internal.junit.runner.TestExecution. run(), (TestExecution.java:38)<br/>org.eclipse.jdt.internal.junit.runner.RemoteTestRunner. runTests(), (RemoteTestRunner.java:459)<br/>org.eclipse.jdt.internal.junit.runner.RemoteTestRunner. runTests(), (RemoteTestRunner.java:675)<br/>org.eclipse.jdt.internal.junit.runner.RemoteTestRunner. run(), (RemoteTestRunner.java:382)<br/>org.eclipse.jdt.internal.junit.runner.RemoteTestRunner. main(), (RemoteTestRunner.java:192)<br/>			</td>" + newline +
				"		</tr>	</tbody>" + newline +
				"</table>";
    	
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
    	when(sobjSched.getFailedQueue()).thenReturn(new Job[] {job1, job2, null});
    	when(job2.getErrorException()).thenReturn(new IllegalArgumentException("Exception text 2"));

    	String jobsOut = WebAdminRenderer.renderFailedJobs();    	
    	assertEquals(jobsTruth, jobsOut);
    }
}