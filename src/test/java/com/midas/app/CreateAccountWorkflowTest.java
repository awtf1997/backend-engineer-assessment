package com.midas.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import com.midas.app.activities.CreateAccountActivity;
import com.midas.app.models.Account;
import com.midas.app.models.enums.Provider;
import com.midas.app.workflows.CreateAccountWorkflow;
import com.midas.app.workflows.impl.CreateAccountWorkflowImpl;
import com.stripe.exception.ApiException;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public class CreateAccountWorkflowTest {

  @Mock CreateAccountActivity activity;

  @InjectMocks CreateAccountWorkflow workflow = new CreateAccountWorkflowImpl();

  Account account;

  @Before
  public void getAccount() {
    UUID id = UUID.randomUUID();
    String providerAccountId = "acct_123";
    String firstName = "abc";
    String lastName = "xyz";
    Provider provider = Provider.STRIPE;
    account =
        Account.builder()
            .id(id)
            .firstName(firstName)
            .lastName(lastName)
            .provider(provider)
            .providerAccountId(providerAccountId)
            .build();
  }

  @Test
  public void testCreateAccount() throws Exception {
    when(activity.createPaymentAccount(account)).thenReturn(account);
    when(activity.saveAccount(account)).thenReturn(account);
    Account actualAccount = workflow.createAccount(account);
    assertEquals(account.getId(), actualAccount.getId());
    assertEquals("abc", actualAccount.getFirstName());
    assertEquals("xyz", actualAccount.getLastName());
    assertEquals(Provider.STRIPE, actualAccount.getProvider());
    assertEquals("acct_123", actualAccount.getProviderAccountId());
  }

  @Test
  public void testCreateAccountThrowsStripeException() throws Exception {
    when(activity.createPaymentAccount(account))
        .thenThrow(
            new ApiException(
                "message",
                "requestId",
                "code",
                HttpStatus.FAILED_DEPENDENCY.value(),
                new Throwable()));
    assertThrows(ApiException.class, () -> workflow.createAccount(account));
  }

  @Test
  public void testCreateAccountThrowsDatabaseException() throws Exception {
    when(activity.createPaymentAccount(account)).thenReturn(account);
    when(activity.saveAccount(account)).thenThrow(new RuntimeException("message"));
    assertThrows(RuntimeException.class, () -> workflow.createAccount(account));
  }
}
