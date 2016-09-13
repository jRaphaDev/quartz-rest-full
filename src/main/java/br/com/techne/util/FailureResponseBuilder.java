package br.com.techne.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;

import br.com.techne.util.StatusException;
import br.com.techne.util.FailureReason;


@Provider
public class FailureResponseBuilder implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {
        FailureReason failureReason = new FailureReason();
        failureReason.setFailureReason(ex.getMessage());
        if (ex instanceof JsonParseException || ex instanceof UnrecognizedPropertyException || ex instanceof JsonMappingException) {
            return Response.status(Status.BAD_REQUEST).entity(failureReason).
                    type(MediaType.APPLICATION_JSON).
                    build();
        } else if (ex instanceof StatusException) {
            return Response.status(((StatusException) ex).getStatusType()).entity(failureReason).type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex).type(MediaType.APPLICATION_JSON).build();
        }
    }
}
