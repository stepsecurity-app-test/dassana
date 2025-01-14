package app.dassana.core.runmanager.workflow;

import static app.dassana.core.runmanager.contentmanager.ContentManager.GENERAL_CONTEXT;
import static app.dassana.core.runmanager.contentmanager.ContentManager.NORMALIZE;
import static app.dassana.core.runmanager.contentmanager.ContentManager.POLICY_CONTEXT;
import static app.dassana.core.runmanager.contentmanager.ContentManager.RESOURCE_CONTEXT;
import static app.dassana.core.runmanager.contentmanager.ContentManager.WORKFLOW_ID;

import app.dassana.core.runmanager.contentmanager.ContentManagerApi;
import app.dassana.core.runmanager.launch.model.Request;
import app.dassana.core.workflowmanager.model.policycontext.PolicyContext;
import app.dassana.core.workflowmanager.model.resource.ResourceContext;
import app.dassana.core.workflowmanager.workflow.model.Error;
import app.dassana.core.workflowmanager.workflow.model.WorkflowOutputWithRisk;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.json.JSONObject;

@Singleton
public class Decorator {

  @Inject
  ContentManagerApi contentManagerApi;
  public static final String DASSANA_KEY = "dassana";

  //todo: refactor to make it readable and maintainable
  public String getDassanaDecoratedJson(Request request,
      WorkflowOutputWithRisk normalizationOutput,
      Optional<WorkflowOutputWithRisk> policyContextWorkflowOutput,
      Optional<WorkflowOutputWithRisk> resourceContextWorkflowOutput,
      Optional<WorkflowOutputWithRisk> generalContextWorkflowOutput) throws Exception {


    //put the output back in original data
    var messageBody = new JSONObject(request.getInputJson());
    Map<String, Object> dassanaMap = new HashMap<>();
    List<Error> allErrors = new LinkedList<>(normalizationOutput.getErrorList());

    //handle normalization decoration
    JSONObject jsonObjectForNormalization = new JSONObject();
    jsonObjectForNormalization.put("step-output", normalizationOutput.getStepOutput());
    jsonObjectForNormalization.put("output", normalizationOutput.getOutput());
    jsonObjectForNormalization.put(WORKFLOW_ID, normalizationOutput.getWorkflowId());
    jsonObjectForNormalization.put("workflowType", NORMALIZE);
    dassanaMap.put("normalize", jsonObjectForNormalization);
    if (generalContextWorkflowOutput.isPresent()) {
      JSONObject generalContextJsonObj = new JSONObject();
      generalContextJsonObj.put(WORKFLOW_ID, generalContextWorkflowOutput.get().getWorkflowId());
      generalContextJsonObj.put("workflowType", GENERAL_CONTEXT);
      generalContextJsonObj.put("output", generalContextWorkflowOutput.get().getOutput());
//      where the workflow steps are coming in ...
      generalContextJsonObj.put("step-output", generalContextWorkflowOutput.get().getStepOutput());
      Map<String, Object> riskObj = new HashMap<>();
      riskObj.put("riskValue", generalContextWorkflowOutput.get().getRisk().getRiskValue());
      riskObj.put("condition", generalContextWorkflowOutput.get().getRisk().getCondition());
      riskObj.put("id", generalContextWorkflowOutput.get().getRisk().getId());
      generalContextJsonObj.put("risk", riskObj);
      dassanaMap.put(GENERAL_CONTEXT, generalContextJsonObj);
      allErrors.addAll(generalContextWorkflowOutput.get().getErrorList());


    }

    if (policyContextWorkflowOutput.isPresent()) {
      PolicyContext policyContext = (PolicyContext) request.getWorkflowIdToWorkflowMap()
          .get(policyContextWorkflowOutput.get().getWorkflowId());

      JSONObject jsonObject = new JSONObject();
      jsonObject.put(WORKFLOW_ID, policyContext.getId());
      jsonObject.put("workflowType", POLICY_CONTEXT);
      jsonObject.put("output", policyContextWorkflowOutput.get().getOutput());
      jsonObject.put("step-output", policyContextWorkflowOutput.get().getStepOutput());

      Map<String, Object> riskObj = new HashMap<>();
      riskObj.put("riskValue", policyContextWorkflowOutput.get().getRisk().getRiskValue());
      riskObj.put("condition", policyContextWorkflowOutput.get().getRisk().getCondition());
      riskObj.put("id", policyContextWorkflowOutput.get().getRisk().getId());
      jsonObject.put("risk", riskObj);
      dassanaMap.put(POLICY_CONTEXT, jsonObject);
      allErrors.addAll(policyContextWorkflowOutput.get().getErrorList());


    }

    if (resourceContextWorkflowOutput.isPresent()) {
      ResourceContext resourceContext = (ResourceContext) request.getWorkflowIdToWorkflowMap()
          .get(resourceContextWorkflowOutput.get().getWorkflowId());

      JSONObject jsonObject = new JSONObject();
      jsonObject.put(WORKFLOW_ID, resourceContext.getId());
      jsonObject.put("workflowType", RESOURCE_CONTEXT);
      jsonObject.put("output", resourceContextWorkflowOutput.get().getOutput());
      jsonObject.put("step-output", resourceContextWorkflowOutput.get().getStepOutput());

      Map<String, Object> riskObj = new HashMap<>();
      riskObj.put("riskValue", resourceContextWorkflowOutput.get().getRisk().getRiskValue());
      riskObj.put("condition", resourceContextWorkflowOutput.get().getRisk().getCondition());
      riskObj.put("id", resourceContextWorkflowOutput.get().getRisk().getId());
      jsonObject.put("risk", riskObj);
      dassanaMap.put(RESOURCE_CONTEXT, jsonObject);
      allErrors.addAll(resourceContextWorkflowOutput.get().getErrorList());

    }

    if (allErrors.size() > 0) {
      dassanaMap.put("errors", allErrors);
    }

    messageBody.put(DASSANA_KEY, dassanaMap);
    messageBody.remove("workflows");

    return messageBody.toString();


  }


}
