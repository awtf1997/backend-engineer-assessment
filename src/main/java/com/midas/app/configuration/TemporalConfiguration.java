package com.midas.app.configuration;

import com.midas.app.activities.CreateAccountActivity;
import com.midas.app.activities.UpdateAccountActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfiguration {
  @Bean
  public CreateAccountActivity getCreateAccountActivityBean() {
    RetryOptions retryoptions =
        RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(1))
            .setMaximumInterval(Duration.ofSeconds(100))
            .setBackoffCoefficient(2)
            .setMaximumAttempts(1)
            .build();
    ActivityOptions defaultActivityOptions =
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setRetryOptions(retryoptions)
            .build();
    return Workflow.newActivityStub(CreateAccountActivity.class, defaultActivityOptions);
  }

  @Bean
  public UpdateAccountActivity getUpdateAccountActivityBean() {
    RetryOptions retryoptions =
        RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(1))
            .setMaximumInterval(Duration.ofSeconds(100))
            .setBackoffCoefficient(2)
            .setMaximumAttempts(5)
            .build();
    ActivityOptions defaultActivityOptions =
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setRetryOptions(retryoptions)
            .build();
    return Workflow.newActivityStub(UpdateAccountActivity.class, defaultActivityOptions);
  }
}
