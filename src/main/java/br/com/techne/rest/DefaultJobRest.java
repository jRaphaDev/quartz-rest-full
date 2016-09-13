package br.com.techne.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.techne.params.EnviaEmailParams;
import br.com.techne.rest.JobRest;
import br.com.techne.util.FailureResponseBuilder;
import br.com.techne.util.StatusException;

public class DefaultJobRest implements JobRest {
	
	private Scheduler scheduler;
	private static Logger log = LoggerFactory.getLogger(DefaultJobRest.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Response executeJob(String params, String className) {
		try {
			
			Class classParams = Class.forName("br.com.techne.params." + className+"Params");
			Class classJob = Class.forName("br.com.techne.job." + className);
			
			//transforma o "json"(string) em objeto java.
			ObjectMapper mapper = new ObjectMapper();
			EnviaEmailParams enviaEmailParams = mapper.readValue(params, classParams);

			//verifica se os parametros obrigatórios estao no json;
			verificaParamentros(enviaEmailParams);

			JobKey jobKey = JobKey.jobKey(enviaEmailParams.getJobNome(), enviaEmailParams.getJobGroup());

			//passa os parametros para o job
			classJob.getConstructor(String.class).newInstance(params);
			//new EnviaEmail(params);		
			
			scheduler = new StdSchedulerFactory().getScheduler();

			//cria o job
			JobDetail job = JobBuilder.newJob(classJob)
				.requestRecovery(true)
				.storeDurably(true)
	  			.withIdentity(jobKey)
	  			.withDescription(enviaEmailParams.getJobDescription())
	  			.build();

			//cria a trigger
			TriggerKey triggerKey = TriggerKey.triggerKey(enviaEmailParams.getTriggerNome(), 
					enviaEmailParams.getTriggerGroup());

		  	Trigger trigger = TriggerBuilder.newTrigger()
		  			.startNow()
		  			.withIdentity(triggerKey)
		  			.withDescription(enviaEmailParams.getTriggerDescription())
		  			.withSchedule(
		  				CronScheduleBuilder.cronSchedule(enviaEmailParams.getPeriodicidade()))
		  			.forJob(job)
		  			.build();
	
		  	if(scheduler.checkExists(jobKey)){
		  		scheduler.deleteJob(jobKey);
		  	}
	  	
		    scheduler.start();
		    scheduler.scheduleJob(job, trigger);
			
		    log.info("--------------- Job criado ---------------");
			return Response.ok().entity(params).build();

		} catch (Exception e) {
			System.out.println("________________________________");
			log.error(e.getMessage());
			return new FailureResponseBuilder().toResponse(e);
		}

	}

	private void verificaParamentros(EnviaEmailParams enviaEmailParams) throws Exception {
		try {
			
			if (enviaEmailParams.getJobNome().equals(null)) {
				log.error(" O nome do job é obrigatório.");
				throw new StatusException(Status.BAD_REQUEST.getStatusCode() ," O nome do job é obrigatório.");
			}
			
			if (enviaEmailParams.getJobGroup().equals(null)) {
				log.error(" O grupo do job é obrigatório.");
				throw new StatusException(Status.BAD_REQUEST.getStatusCode() ," O grupo do job é obrigatório.");
			}
			
			if (enviaEmailParams.getTriggerNome().equals(null)) {
				log.error(" O nome da trigger é obrigatório.");
				throw new StatusException(Status.BAD_REQUEST.getStatusCode() ," O nome da trigger é obrigatório.");
			}
			
			if (enviaEmailParams.getTriggerGroup().equals(null)){
				log.error(" O grupo da trigger é obrigatório.");
				throw new StatusException(Status.BAD_REQUEST.getStatusCode() ," O grupo da trigger é obrigatório.");
			}
			
			if (enviaEmailParams.getPeriodicidade().equals(null)) {
				log.error(" A periodicidade é obrigatória.");
				throw new StatusException(Status.BAD_REQUEST.getStatusCode() ," A periodicidade é obrigatória.");
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

}
