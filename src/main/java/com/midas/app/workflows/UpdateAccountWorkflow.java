package com.midas.app.workflows;

import com.midas.app.models.Account;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface UpdateAccountWorkflow {
  String QUEUE_NAME = "update-account-workflow";
  String UPDATE_ACCOUNT = "update-account";

  /**
   * updateAccount updates an existing account in the system or provider.
   *
   * @param details is the details of the account to be updated.
   * @return Account
   */
  @WorkflowMethod
  Account updateAccount(Account details) throws Exception;
}
