package br.com.rest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import br.com.email.SendMail;
import br.com.rest.JobRest;

public class DefaultJobRest implements JobRest {
	
	private Scheduler scheduler;
	Properties prop = new Properties();
	InputStream input = null;
	
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

		input = getClass().getClassLoader().getResourceAsStream("quartz.properties");
		prop.load(input);
		
		StringBuffer msg = new StringBuffer(
				  "Every move you make, "
        		+ "Every bond you break, "
        		+ "Every step you take, "
        		+ "I'll be watching you !!! ");
       
		List<String> to = new ArrayList<>();
		to.add("raphael.santos@techne.com.br");
		to.add("leandro.chaves@techne.com.br");
		
		String username = prop.getProperty("email.username");
		String password = prop.getProperty("email.password");
		System.out.println(username + " >>>> " + password);

		//new SendMail(username, password, to, "Title", msg.toString());
		new SendMail("seuemail@gmail.com", "password", to, "Title 2", msg.toString());

		this.scheduler = new StdSchedulerFactory().getScheduler();
		
		JobDetail job = JobBuilder.newJob(SendMail.class)
    			.withIdentity(jobKey)
    			.withDescription("description")
    			.usingJobData("job do", "send email")
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

	@Override
	public Response getExcecutingJobs() throws Exception {
		try {
			List<JobExecutionContext> list = scheduler.getCurrentlyExecutingJobs(); 
			
			/*loop all group
		    for (String groupName : scheduler.getJobGroupNames()) {

		    	for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

		    		String jobName = jobKey.getName();
		    		String jobGroup = jobKey.getGroup();

		    		//get job's trigger
		    		@SuppressWarnings("unused")
					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

		    		System.out.println("[jobName] : " + jobName + " [groupName] : "	+ jobGroup + " - ");
		    	}
		    }*/
			return Response.ok().entity(list).build();
		}catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e).build();
		}
	}

}
