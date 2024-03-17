package com.midas.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import com.midas.app.activities.UpdateAccountActivity;
import com.midas.app.models.Account;
import com.midas.app.models.enums.Provider;
import com.midas.app.workflows.UpdateAccountWorkflow;
import com.midas.app.workflows.impl.UpdateAccountWorkflowImpl;
import com.stripe.exception.ApiException;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UpdateAccountWorkflowTest {
  @Mock UpdateAccountActivity activity;

  @InjectMocks UpdateAccountWorkflow workflow = new UpdateAccountWorkflowImpl();

  Account account;
  Account updatedAccount;

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
    String updatedFirstName = "abc";
    String updatedLastName = "xyz";
    updatedAccount =
        Account.builder().id(id).firstName(updatedFirstName).lastName(updatedLastName).build();
  }

  @Test
  public void testUpdateAccount() throws Exception {
    when(activity.getAccount(updatedAccount.getId())).thenReturn(Optional.of(account));
    when(activity.updatePaymentAccount(updatedAccount)).thenReturn(updatedAccount);
    when(activity.saveAccount(updatedAccount)).thenReturn(updatedAccount);
    Account actualUpdatedAccount = workflow.updateAccount(updatedAccount);
    assertEquals(account.getId(), actualUpdatedAccount.getId());
    assertEquals("abc", actualUpdatedAccount.getFirstName());
    assertEquals("xyz", actualUpdatedAccount.getLastName());
    assertEquals(Provider.STRIPE, actualUpdatedAccount.getProvider());
    assertEquals("acct_123", actualUpdatedAccount.getProviderAccountId());
  }

  @Test
  public void testUpdateAccountThrowsIllegalArgumentException() {
    when(activity.getAccount(account.getId()))
        .thenThrow(new IllegalArgumentException("Account by the user id is not found"));
    assertThrows(IllegalArgumentException.class, () -> workflow.updateAccount(account));
  }

  @Test
  public void testUpdateAccountThrowsStripeException() throws Exception {
    when(activity.getAccount(updatedAccount.getId())).thenReturn(Optional.of(account));
    when(activity.updatePaymentAccount(updatedAccount))
        .thenThrow(
            new ApiException(
                "message",
                "requestId",
                "code",
                HttpStatus.FAILED_DEPENDENCY.value(),
                new Throwable()));
    assertThrows(ApiException.class, () -> workflow.updateAccount(updatedAccount));
  }

  @Test
  public void testCreateAccountThrowsDatabaseException() throws Exception {
    when(activity.getAccount(updatedAccount.getId())).thenReturn(Optional.of(account));
    when(activity.updatePaymentAccount(updatedAccount)).thenReturn(updatedAccount);
    when(activity.saveAccount(updatedAccount)).thenThrow(new RuntimeException("message"));
    assertThrows(RuntimeException.class, () -> workflow.updateAccount(updatedAccount));
  }
}
