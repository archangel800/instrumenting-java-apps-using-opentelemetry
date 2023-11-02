package tutorial.buildon.aws.o11y;

import java.util.Objects;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloAppController {

    private static final Logger log =
        LoggerFactory.getLogger(HelloAppController.class);

    @Value("otel.traces.api.version")
    private String tracesApiVersion;
    private final Tracer tracer =
            GlobalOpenTelemetry.getTracer("io.opentelemetry.traces.hello", tracesApiVersion);

    @RequestMapping(method= RequestMethod.GET, value="/hello")
    public Response hello() {
        Response response = buildResponse();
        Span span = tracer.spanBuilder("mySpan").startSpan();
        try (Scope scope = span.makeCurrent()) {
            if(response.isValid())
            log.info("The response is valid.");
        }
        finally {
            span.end();
        }
        if (response.isValid()) {
            log.info("The response is valid.");
        }
        return response;
    }

    @WithSpan //this is equivalent of creating span manually like we did in the above code
    private Response buildResponse() {
        return new Response("Hello World");
    }

    private record Response (String message) {
        private Response {
            Objects.requireNonNull(message);
        }
        private boolean isValid() {
            return true;
        }
    }
    
}
