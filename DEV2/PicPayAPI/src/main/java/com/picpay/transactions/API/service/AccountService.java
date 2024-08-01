package com.picpay.transactions.API.service;

import com.picpay.transactions.API.model.Account;
import com.picpay.transactions.API.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findOne(String number) {
        return accountRepository.findById(number).orElseThrow(() ->
                new RuntimeException("Account not found.")
        );
    }

    public Account createOne(Account account) {
        return accountRepository.save(account);
    }

    public Account updatePartial(String number, Map<String, Object> updates) throws Exception {
        Optional<Account> existentAccount = accountRepository.findById(number);
        if(existentAccount.isPresent()) {
            try {
                Account account = existentAccount.get();
                Map<String, Method> actions = account.fillSettersMap();

                for(String key : updates.keySet()) {
                    Method action = actions.get(key);
                    if(action != null) {
                        action.invoke(account, updates.get(key));
                    }
                }
                return accountRepository.save(account);
            } catch (Exception e) {
                throw new Exception(e);
            }
        } else {
            return null;
        }
    }

    public Account update(String number, Account updates) {
        Optional<Account> existentAccount = accountRepository.findById(number);
        if(existentAccount.isPresent()) {
            Account account = existentAccount.get();
            account.setValue(updates.getValue());
            account.setLimit(updates.getLimit());
            return accountRepository.save(account);
        } else {
            return null;
        }
    }

    public Account deleteOne(String number) {
        Optional<Account> account = accountRepository.findById(number);
        if(account.isPresent()) {
            accountRepository.deleteById(number);
            return account.get();
        }
        return null;
    }
}
