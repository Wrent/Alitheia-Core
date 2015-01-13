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

import eu.sqooss.impl.service.webadmin.*;
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
		Whitebox.setInternalState(JobsView.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		when(sobjSched.getSchedulerStats()).thenReturn(stats);
		Assert.assertEquals(stats.getFailedJobTypes(),
				JobsView.getFailedJobStats());
	}

	@Test
	public void testGetFailedJobsStatsMultiple() {
		Whitebox.setInternalState(JobsView.class, sobjSched);

		SchedulerStats stats = new SchedulerStats();
		stats.addFailedJob("Job name 1");
		stats.addFailedJob("Job name 2");

		when(sobjSched.getSchedulerStats()).thenReturn(stats);
		Assert.assertEquals(stats.getFailedJobTypes(),
				JobsView.getFailedJobStats());
		Assert.assertEquals("Job name 1", JobsView.getFailedJobStats()
				.keySet().toArray()[1]);
		Assert.assertEquals(1,
				(int) JobsView.getFailedJobStats().get("Job name 2"));
	}
	
	@Test
	public void testGetLogsNone() {
		Whitebox.setInternalState(LogsView.class, sobjLogManager);
		 when(sobjLogManager.getRecentEntries()).thenReturn(new String[0]);
		 Assert.assertTrue(LogsView.getLogs().size() == 0);
		
		 when(sobjLogManager.getRecentEntries()).thenReturn(null);
		 Assert.assertTrue(LogsView.getLogs().size() == 0);
	}
	
	@Test
	public void testGetLogsMultiple() {
		Whitebox.setInternalState(LogsView.class, sobjLogManager);
		 String[] logEntries = {"Log entry 1", "Log entry 2"};
		 when(sobjLogManager.getRecentEntries()).thenReturn(logEntries);
		 Assert.assertTrue(LogsView.getLogs().size() == 2);
		 Assert.assertEquals("Log entry 1", LogsView.getLogs().get(0));
	}

	@Test
	public void testGetUptime() {
		String uptimeTruth = "25:02:03:02";

		long days = 25L * 24L * 60L * 60L * 1000L;
		long hours = 2L * 60L * 60L * 1000L;
		long minutes = 2L * 60L * 1000L;
		long seconds = 62L * 1000L;
		long dateEntry = new Date().getTime() - days - hours - minutes
				- seconds;

		Whitebox.setInternalState(AbstractView.class, dateEntry);
		String uptime = AbstractView.getUptime();
		assertEquals(uptimeTruth, uptime);
	}

	@Test
	public void testGetWaitingJobsEmpty() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		when(sobjSched.getSchedulerStats()).thenReturn(stats);
		Assert.assertEquals(stats.getWaitingJobTypes(),
				JobsView.getWaitingJobs());
	}

	@Test
	public void testGetWaitingJobsMultiple() {
		Whitebox.setInternalState(JobsView.class, sobjSched);

		SchedulerStats stats = new SchedulerStats();
		stats.addWaitingJob("Job name 1");
		stats.addWaitingJob("Job name 2");

		when(sobjSched.getSchedulerStats()).thenReturn(stats);
		Assert.assertEquals(stats.getWaitingJobTypes(),
				JobsView.getWaitingJobs());
		Assert.assertEquals("Job name 1", JobsView.getWaitingJobs()
				.keySet().toArray()[1]);
		Assert.assertEquals(1,
				(int) JobsView.getWaitingJobs().get("Job name 2"));
	}


	@Test
	public void testGetRunningJobsEmpty() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		when(sobjSched.getSchedulerStats()).thenReturn(stats);

		Assert.assertEquals(stats.getRunJobs(), JobsView.getRunningJobs());
	}
	
	@Test
	public void testGetRunningJobsMultiple() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		stats.addRunJob(job1);
		stats.addRunJob(job2);
		when(sobjSched.getSchedulerStats()).thenReturn(stats);

		Assert.assertEquals(stats.getRunJobs(), JobsView.getRunningJobs());
	}

	@Test
	public void testGetFailedJobsNone() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		Job[] jobs = new Job[] {};
		when(sobjSched.getFailedQueue()).thenReturn(jobs);
		Assert.assertEquals(jobs, JobsView.getFailedJobs());
	}

	@Test
	public void testGetFailedJobsMultiple() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		Job[] jobs = new Job[] {job1, job2, null};
		when(sobjSched.getFailedQueue()).thenReturn(jobs);
		when(job2.getErrorException()).thenReturn(new IllegalArgumentException("Exception text 2"));
		Assert.assertEquals(jobs, JobsView.getFailedJobs());
	}
	
//	@Test
//	public void testIsFailedJobsEmpty() {
//		Whitebox.setInternalState(JobsView.class, sobjSched);
//		Assert.assertTrue(JobsView.isFailedJobsEmpty());
//		Job[] jobs1 = new Job[] {};
//		Job[] jobs2 = new Job[] {job1, job2, null};
//		when(sobjSched.getFailedQueue()).thenReturn(jobs1, jobs2);
//		Assert.assertTrue(JobsView.isFailedJobsEmpty());
//		Assert.assertFalse(JobsView.isFailedJobsEmpty());
//	}
}