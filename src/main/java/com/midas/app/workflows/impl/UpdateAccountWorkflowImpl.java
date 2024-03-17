package com.midas.app.workflows.impl;

import com.midas.app.activities.UpdateAccountActivity;
import com.midas.app.models.Account;
import com.midas.app.workflows.UpdateAccountWorkflow;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@WorkflowImpl(taskQueues = UpdateAccountWorkflow.QUEUE_NAME)
public class UpdateAccountWorkflowImpl implements UpdateAccountWorkflow {
  private final Logger logger = Workflow.getLogger(UpdateAccountWorkflowImpl.class);
  @Autowired private UpdateAccountActivity account;

  @Override
  public Account updateAccount(Account details) throws Exception {
    logger.info("Initiating updateAccount() in UpdateAccountWorkflowImpl");
    Optional<Account> existingAccountOpt = account.getAccount(details.getId());
    if (existingAccountOpt.isEmpty())
      throw new IllegalArgumentException("No such User Account exists");
    Account existingAccount = existingAccountOpt.get();
    details.setProvider(existingAccount.getProvider());
    details.setProviderAccountId(existingAccount.getProviderAccountId());
    details = account.updatePaymentAccount(details);
    details = account.saveAccount(details);
    logger.info("Exiting updateAccount() in UpdateAccountWorkflowImpl");
    return details;
  }
}
