package domain.rest.exceptions;

public class ExceptionBody {
    private String message;

    public ExceptionBody(String message) {
        this.message = message;
    }

    private ExceptionBody() {
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj instanceof ExceptionBody && hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }
}