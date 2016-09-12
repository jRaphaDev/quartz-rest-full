package br.com.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("/Job")
public interface JobRest {

	@GET
	@Path("/{seconds}")
	Response executeJob(@PathParam("seconds") int seconds);
	
	@GET
	@Produces("application/json")
	Response getExcecutingJobs() throws Exception;
}
