package br.com.job;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.email.JavaSendMail;

public class HelloJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		
	}
	
	//static Log logger = LogFactory.getLog(HelloJob.class); 
	
	/*public void execute(JobExecutionContext context) throws JobExecutionException {
		JavaSendMail sendEmail = new JavaSendMail();
    	try {
			sendEmail.send();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//executa a tarefa.
		//System.out.println("Envia email! " + new Date().toGMTString());	
	}*/
	
}
