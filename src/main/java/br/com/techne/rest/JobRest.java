package br.com.techne.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/job")
public interface JobRest {

	@POST
	@Path("/{className}")
	@Consumes("application/json")
	Response executeJob(String params, @PathParam("className") String className) throws Exception;

}
