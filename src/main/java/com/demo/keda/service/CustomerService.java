package com.demo.keda.service;

import com.demo.keda.vo.Account;
import com.demo.keda.vo.AccountDetails;
import com.demo.keda.vo.AccountType;
import com.demo.keda.vo.CustomerAccounts;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CustomerService.class);
    private final ObservationRegistry observationRegistry;

    public CustomerService(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public CustomerAccounts getCustomerAccounts(Long customerId) {
        return new CustomerAccounts(customerId, getAccounts(customerId));
    }

    private List<AccountDetails> getAccounts(Long customerId) {
        List<AccountDetails> accountDetailsList = new ArrayList<>();
        List<Account> accounts = getAccountsForCustomer(customerId);
        for (Account account: accounts) {
            AccountDetails accountDetails = Observation.createNotStarted("account.processing", observationRegistry)
                    .lowCardinalityKeyValue("accountType", String.valueOf(account.accountType()))    // available in metrics
                    .highCardinalityKeyValue("accountId", account.accountId())                      // available in traces
                    .observe(() -> getAccountDetails(account));
            accountDetailsList.add(accountDetails);
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            requestAttributes.setAttribute("numberAccounts", accountDetailsList.size(), RequestAttributes.SCOPE_REQUEST);
        }
        LOGGER.info("Fetched {} accounts for customerId: {}", accountDetailsList.size(), customerId);
        return accountDetailsList;
    }

    // mock method that perhaps calls an external resource to fetch the accounts for a given customer
    private List<Account> getAccountsForCustomer(Long customerId) {
        int numberOfAccounts = (int) (customerId % 5) + 1;
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < numberOfAccounts; i++) {
            accounts.add(new Account(customerId + "00" + i, randomAccountType()));
        }
        return accounts;
    }

    private AccountType randomAccountType() {
        return
                switch (new SecureRandom().nextInt(3)) {
                    case 0 -> AccountType.B;
                    case 1 -> AccountType.C;
                    default -> AccountType.A;
                };
    }

    // mock method that fetches details about a specific account and does some processing
    private AccountDetails getAccountDetails(Account account) {
        try {
            Thread.sleep(10L + (long) (new SecureRandom().nextInt(90)));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new AccountDetails(account);
    }
}
