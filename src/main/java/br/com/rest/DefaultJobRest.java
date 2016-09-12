package br.com.rest;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import br.com.email.Authentication;
import br.com.email.JavaSendMail;
import br.com.job.HelloJob;
import br.com.rest.JobRest;

public class DefaultJobRest implements JobRest {

	private Scheduler scheduler;
		
	@Override
	public Response executeJob(int seconds) {
		try {
			JobKey jobKey = JobKey.jobKey("jobA", "my-jobs");
			executeTrigger(jobKey, seconds);
			return Response.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e).build();
		}
	}
	
	private void executeTrigger(JobKey jobKey, int seconds) throws Exception {
		
		this.scheduler = new StdSchedulerFactory().getScheduler();
		
		JobDetail job = JobBuilder.newJob(JavaSendMail.class)
    			.withIdentity(jobKey)
    			.withDescription("description")
    			.usingJobData("jobDo", "send email")
    			.build();
    	
    	TriggerKey triggerKey = TriggerKey.triggerKey("dummyTriggerName1", "my-jobs");
    	Trigger trigger = TriggerBuilder.newTrigger()
    			.startNow()
    			.withIdentity(triggerKey)
    			.withDescription("description")
    			.withSchedule(
    					CronScheduleBuilder.cronSchedule("0/"+seconds+" * * * * ?"))
    			.forJob(job)
    			.build();

    	//Agendamento
    	if(scheduler.checkExists(jobKey)){
    		scheduler.deleteJob(jobKey);
    	}
    	
    	
	    scheduler.start();
	    scheduler.scheduleJob(job, trigger);
    	
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response getExcecutingJobs() throws Exception {
		try {
			List<JobExecutionContext> list = scheduler.getCurrentlyExecutingJobs(); 
			
			//loop all group
		    for (String groupName : scheduler.getJobGroupNames()) {

		    	for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

		    		String jobName = jobKey.getName();
		    		String jobGroup = jobKey.getGroup();

		    		//get job's trigger
		    		@SuppressWarnings("unused")
					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

		    		System.out.println("[jobName] : " + jobName + " [groupName] : "	+ jobGroup + " - ");
		    	}
		    }
			return Response.ok().entity(list).build();
		}catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e).build();
		}
	}

}
