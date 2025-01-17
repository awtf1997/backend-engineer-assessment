package com.midas.app.controllers;

import com.midas.app.mappers.Mapper;
import com.midas.app.models.Account;
import com.midas.app.models.enums.Provider;
import com.midas.app.services.AccountService;
import com.midas.generated.api.AccountsApi;
import com.midas.generated.model.AccountDto;
import com.midas.generated.model.CreateAccountDto;
import com.midas.generated.model.UpdateAccountDto;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class AccountController implements AccountsApi {
  @Autowired private AccountService accountService;
  private final Logger logger = LoggerFactory.getLogger(AccountController.class);

  /**
   * POST /accounts : Create a new user account Creates a new user account with the given details
   * and attaches a supported payment provider such as &#39;stripe&#39;.
   *
   * @param createAccountDto User account details (required)
   * @return User account created (status code 201)
   */
  @Override
  public ResponseEntity<AccountDto> createUserAccount(CreateAccountDto createAccountDto) {
    logger.info("Creating account for user with email: {}", createAccountDto.getEmail());
    try {
      var account =
          accountService.createAccount(
              Account.builder()
                  .id(UUID.randomUUID())
                  .firstName(createAccountDto.getFirstName())
                  .lastName(createAccountDto.getLastName())
                  .email(createAccountDto.getEmail())
                  .provider(Provider.valueOf(createAccountDto.getProvider()))
                  .build());

      return new ResponseEntity<>(Mapper.toAccountDto(account), HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * GET /accounts : Get list of user accounts Returns a list of user accounts.
   *
   * @return List of user accounts (status code 200)
   */
  @Override
  public ResponseEntity<List<AccountDto>> getUserAccounts() {
    logger.info("Retrieving all accounts");

    var accounts = accountService.getAccounts();
    var accountsDto = accounts.stream().map(Mapper::toAccountDto).toList();

    return new ResponseEntity<>(accountsDto, HttpStatus.OK);
  }

  /**
   * PUT /accounts : Updates an existing user account with the given details
   *
   * @param updateAccountDto User account details (required)
   * @return User account created (status code 201)
   */
  @Override
  public ResponseEntity<AccountDto> updateUserAccount(UpdateAccountDto updateAccountDto) {
    logger.info("Updating account for user with id: {}", updateAccountDto.getId());
    try {
      var account =
          accountService.updateAccount(
              Account.builder()
                  .id(updateAccountDto.getId())
                  .firstName(updateAccountDto.getFirstName())
                  .lastName(updateAccountDto.getLastName())
                  .email(updateAccountDto.getEmail())
                  .build());
      return new ResponseEntity<>(Mapper.toAccountDto(account), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
