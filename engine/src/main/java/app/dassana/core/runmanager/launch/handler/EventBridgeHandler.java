package app.dassana.core.runmanager.launch.handler;

import app.dassana.core.runmanager.launch.model.ProcessingResponse;
import app.dassana.core.runmanager.launch.model.RunMode;
import app.dassana.core.runmanager.workflow.Decorator;
import io.micronaut.context.annotation.Value;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;

@Singleton
public class EventBridgeHandler {

  @Value("${env.dassanaEventBridgeBusName}")
  private String dassanaEventBridgeBusName;

  @Inject private EventBridgeClient eventBridgeClient;

  public static final String NORMALIZER_NOT_FOUND = "normalizer_not_found";

  public void handleEventBridge(ProcessingResponse processingResponse, String normalizerId) {

    if (StringUtils.isEmpty(normalizerId)) {
      normalizerId = NORMALIZER_NOT_FOUND;
    }

    RunMode runMode = processingResponse.getRequest().getRunMode();
    if (runMode == null) {
      runMode = RunMode.TEST;
    }

    if (StringUtils.isNotEmpty(dassanaEventBridgeBusName) && runMode.equals(RunMode.PROD)) {
      PutEventsRequestEntry putEventsRequestEntry = PutEventsRequestEntry.builder()
          .eventBusName(dassanaEventBridgeBusName)
          .detail(processingResponse.getDecoratedJson())
          .source(Decorator.DASSANA_KEY)
          .detailType(normalizerId)
          .build();

      PutEventsResponse putEventsResponse = eventBridgeClient.putEvents(
          PutEventsRequest.builder().entries(putEventsRequestEntry).build());

      if (putEventsResponse.failedEntryCount() > 0) {
        throw new RuntimeException(
            "Unable to send events to event-bridge due to error: ".concat(putEventsResponse.toString()));
      }
    }


  }

}
