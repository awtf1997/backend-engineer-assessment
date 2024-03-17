package com.midas.app.workflows.impl;

import com.midas.app.activities.UpdateAccountActivity;
import com.midas.app.models.Account;
import com.midas.app.workflows.UpdateAccountWorkflow;
import com.stripe.exception.StripeException;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.Optional;
import org.slf4j.Logger;

@WorkflowImpl(taskQueues = UpdateAccountWorkflow.QUEUE_NAME)
public class UpdateAccountWorkflowImpl implements UpdateAccountWorkflow {
  private final Logger logger = Workflow.getLogger(UpdateAccountWorkflowImpl.class);
  private final RetryOptions retryoptions =
      RetryOptions.newBuilder()
          .setInitialInterval(Duration.ofSeconds(1))
          .setMaximumInterval(Duration.ofSeconds(100))
          .setBackoffCoefficient(2)
          .setMaximumAttempts(5)
          .build();
  private final ActivityOptions defaultActivityOptions =
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(30))
          .setRetryOptions(retryoptions)
          .build();
  private final UpdateAccountActivity account =
      Workflow.newActivityStub(UpdateAccountActivity.class, defaultActivityOptions);

  @Override
  public Account updateAccount(Account details) throws IllegalArgumentException, StripeException {
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
