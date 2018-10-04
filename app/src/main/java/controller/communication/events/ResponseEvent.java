package controller.communication.events;

/**
 * Created by cyprien on 09/11/15.
 */
public class ResponseEvent extends RemoteEvent {
    public enum Response {
        ServerShutdown, ServiceShutdown, Ok, Failure
    }

    private Response response;

    @Override
    public String toString() {
        return "ResponseEvent{" +
                "response=" + response +
                '}';
    }

    public Response getResponse() {
        return response;
    }

    public ResponseEvent(Response response) {

        this.response = response;
    }
}
