package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;

import junit.framework.Assert;

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

	@Mock
	Scheduler sobjSched;
	@Mock
	LogManager sobjLogManager;
	@Mock
	Job job1;
	@Mock
	Job job2;

	String newline = "\n";

	@BeforeClass
	public static void setUp() {
	}

	@Test
	public void testGetFailedJobsStatsEmpty() {
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		when(sobjSched.getSchedulerStats()).thenReturn(stats);
		Assert.assertEquals(stats.getFailedJobTypes(),
				WebAdminRenderer.getFailedJobStats());
	}

	@Test
	public void testGetFailedJobsStatsMultiple() {
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);

		SchedulerStats stats = new SchedulerStats();
		stats.addFailedJob("Job name 1");
		stats.addFailedJob("Job name 2");

		when(sobjSched.getSchedulerStats()).thenReturn(stats);
		Assert.assertEquals(stats.getFailedJobTypes(),
				WebAdminRenderer.getFailedJobStats());
		Assert.assertEquals("Job name 1", WebAdminRenderer.getFailedJobStats()
				.keySet().toArray()[1]);
		Assert.assertEquals(1,
				(int) WebAdminRenderer.getFailedJobStats().get("Job name 2"));
	}

	// @Test
	// public void testRenderLogsMultiple() {
	// Whitebox.setInternalState(WebAdminRenderer.class, sobjLogManager);
	// String[] logEntries = {"Log entry 1", "Log entry 2"};
	// when(sobjLogManager.getRecentEntries()).thenReturn(logEntries);
	// String multipleLogEntries = WebAdminRenderer.renderLogs();
	// String multipleLogEntriesTruth =
	// "					<li>Log entry 1</li>" + newline +
	// "					<li>Log entry 2</li>" + newline;
	//
	// assertEquals(multipleLogEntriesTruth, multipleLogEntries);
	// }
	//
	// @Test
	// public void testRenderLogsNone() {
	// Whitebox.setInternalState(WebAdminRenderer.class, sobjLogManager);
	// when(sobjLogManager.getRecentEntries()).thenReturn(new String[0]);
	// String logEntries = WebAdminRenderer.renderLogs();
	// String logEntriesTruth =
	// "					<li>&lt;none&gt;</li>" + newline;
	//
	// assertEquals(logEntries, logEntriesTruth);
	//
	// when(sobjLogManager.getRecentEntries()).thenReturn(null);
	// logEntries = WebAdminRenderer.renderLogs();
	// assertEquals(logEntriesTruth, logEntries);
	// }

	@Test
	public void testGetUptime() {
		String uptimeTruth = "25:02:03:02";

		long days = 25L * 24L * 60L * 60L * 1000L;
		long hours = 2L * 60L * 60L * 1000L;
		long minutes = 2L * 60L * 1000L;
		long seconds = 62L * 1000L;
		long dateEntry = new Date().getTime() - days - hours - minutes
				- seconds;

		Whitebox.setInternalState(WebAdminRenderer.class, dateEntry);
		String uptime = WebAdminRenderer.getUptime();
		assertEquals(uptimeTruth, uptime);
	}

	@Test
	public void testRenderJobWaitStatsEmpty() {
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);

		SchedulerStats stats = new SchedulerStats();
		when(sobjSched.getSchedulerStats()).thenReturn(stats);

		String noFailsTruth = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">"
				+ newline
				+ "	<thead>"
				+ newline
				+ "		<tr>"
				+ newline
				+ "			<td>Job Type</td>"
				+ newline
				+ "			<td>Num Jobs Waiting</td>"
				+ newline
				+ "		</tr>"
				+ newline
				+ "	</thead>"
				+ newline
				+ "	<tbody>"
				+ newline
				+ "		<tr>"
				+ newline
				+ "			<td>No failures</td>"
				+ newline
				+ "			<td>&nbsp;			</td>"
				+ newline
				+ "		</tr>	</tbody>"
				+ newline + "</table>";

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

		String jobsTruth = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">"
				+ newline
				+ "	<thead>"
				+ newline
				+ "		<tr>"
				+ newline
				+ "			<td>Job Type</td>"
				+ newline
				+ "			<td>Num Jobs Waiting</td>"
				+ newline
				+ "		</tr>"
				+ newline
				+ "	</thead>"
				+ newline
				+ "	<tbody>"
				+ newline
				+ "		<tr>"
				+ newline
				+ "			<td>Job name 2</td>"
				+ newline
				+ "			<td>1			</td>"
				+ newline
				+ "		</tr>		<tr>"
				+ newline
				+ "			<td>Job name 1</td>"
				+ newline
				+ "			<td>1			</td>"
				+ newline + "		</tr>	</tbody>" + newline + "</table>";

		String jobsOut = WebAdminRenderer.renderJobWaitStats();
		assertEquals(jobsTruth, jobsOut);
	}

	@Test
	public void testGetRunningJobsEmpty() {
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		when(sobjSched.getSchedulerStats()).thenReturn(stats);

		Assert.assertEquals(stats.getRunJobs(), WebAdminRenderer.getRunningJobs());
	}
	
	@Test
	public void testGetRunningJobsMultiple() {
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		stats.addRunJob(job1);
		stats.addRunJob(job2);
		when(sobjSched.getSchedulerStats()).thenReturn(stats);

		Assert.assertEquals(stats.getRunJobs(), WebAdminRenderer.getRunningJobs());
	}

	@Test
	public void testGetFailedJobsNone() {
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
		Job[] jobs = new Job[] {};
		when(sobjSched.getFailedQueue()).thenReturn(jobs);
		Assert.assertEquals(jobs, WebAdminRenderer.getFailedJobs());
	}

	@Test
	public void testGetFailedJobsMultiple() {
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
		Job[] jobs = new Job[] {job1, job2, null};
		when(sobjSched.getFailedQueue()).thenReturn(jobs);
		when(job2.getErrorException()).thenReturn(new IllegalArgumentException("Exception text 2"));
		Assert.assertEquals(jobs, WebAdminRenderer.getFailedJobs());
	}
	
	@Test
	public void testIsFailedJobsEmpty() {
		Whitebox.setInternalState(WebAdminRenderer.class, sobjSched);
		Assert.assertTrue(WebAdminRenderer.isFailedJobsEmpty());
		Job[] jobs1 = new Job[] {};
		Job[] jobs2 = new Job[] {job1, job2, null};
		when(sobjSched.getFailedQueue()).thenReturn(jobs1, jobs2);
		Assert.assertTrue(WebAdminRenderer.isFailedJobsEmpty());
		Assert.assertFalse(WebAdminRenderer.isFailedJobsEmpty());
	}
}