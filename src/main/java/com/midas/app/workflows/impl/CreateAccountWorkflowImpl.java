package com.midas.app.workflows.impl;

import com.midas.app.activities.CreateAccountActivity;
import com.midas.app.models.Account;
import com.midas.app.workflows.CreateAccountWorkflow;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@WorkflowImpl(taskQueues = CreateAccountWorkflow.QUEUE_NAME)
public class CreateAccountWorkflowImpl implements CreateAccountWorkflow {
  private final Logger logger = Workflow.getLogger(CreateAccountWorkflowImpl.class);
  @Autowired private CreateAccountActivity account;

  @Override
  public Account createAccount(Account details) throws Exception {
    logger.info("Initiating createAccount() in CreateAccountWorkFlowImpl");
    details = account.createPaymentAccount(details);
    details = account.saveAccount(details);
    logger.info("Exiting createAccount() in CreateAccountWorkFlowImpl");
    return details;
  }
}
