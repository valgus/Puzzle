package domain.rest.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UserException extends WebApplicationException {

    private final String message;
    public UserException(Response.Status status, String message) {
        super(Response.status(status).
                entity(new ExceptionBody(message)).build());
        this.message = message;
    }
    @Override
    public String getMessage(){
        return message;
    }
}
