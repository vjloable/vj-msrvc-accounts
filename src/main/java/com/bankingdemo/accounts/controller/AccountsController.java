package com.bankingdemo.accounts.controller;

import com.bankingdemo.accounts.config.AccountsServiceConfig;
import com.bankingdemo.accounts.model.*;
import com.bankingdemo.accounts.repository.AccountsRepository;
import com.bankingdemo.accounts.service.client.CardsFeignClient;
import com.bankingdemo.accounts.service.client.LoansFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountsController {
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    AccountsServiceConfig accountsConfig;

    @Autowired
    LoansFeignClient loansFeignClient;

    @Autowired
    CardsFeignClient cardsFeignClient;

    @PostMapping("/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer){
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        if (accounts != null){
            return accounts;
        } else {
            return null;
        }
    }

    @PostMapping("/myCustomerDetails")
    public CustomerDetails myCustomerDetails(@RequestBody Customer customer){
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoanDetails(customer);
        List<Cards> cards = cardsFeignClient.getCardDetails(customer);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        customerDetails.setCards(cards);

        return customerDetails;
    }

    @GetMapping("/account/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
                accountsConfig.getMailDetails(), accountsConfig.getActiveBranches());
        String jsonStr = ow.writeValueAsString(properties);
        return jsonStr;
    }

}
