package com.picpay.transactions.API.service;

import com.picpay.transactions.API.model.Account;
import com.picpay.transactions.API.model.Transaction;
import com.picpay.transactions.API.model.TransactionCodes;
import com.picpay.transactions.API.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;

    @Autowired
    public TransactionService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public double deposit(String number, double amount) throws Exception {
        String transactionType = TransactionCodes.DEPOSIT;
        Optional<Account> existentAccount = accountRepository.findById(number);
        if(existentAccount.isPresent()) {
            try {
                Transaction transaction = new Transaction(transactionType, existentAccount.get(), amount);
                transaction.validate();
            } catch (Exception e) {
                throw new Exception(e);
            }
        } else {
            return -1;
        }
    }

    public double withdraw(String number, double amount) throws Exception {
        if(
                LocalDateTime.now().getDayOfWeek() == DayOfWeek.SUNDAY ||
                        LocalDateTime.now().getDayOfWeek() == DayOfWeek.SATURDAY &&
                                amount > 1000
        ) {
            Optional<Account> existentAccount = accountRepository.findById(number);
            if(existentAccount.isPresent()) {
                try {
                    Account account = existentAccount.get();
                    double newAccountValue = account.getValue() - amount;
                    if(newAccountValue >= 0) {
                        account.setValue(newAccountValue);
                        accountRepository.save(account);
                        return newAccountValue;
                    }
                    return -2;
                } catch (Exception e) {
                    throw new Exception(e);
                }
            } else {
                return -1;
            }
        } else {
            return -3;
        }
    }

    public double transaction(String originNumber, String targetNumber, double amount) throws Exception {
        Optional<Account> existentAccount = accountRepository.findById(number);
        if(existentAccount.isPresent()) {
            try {
                Account account = existentAccount.get();
                double newAccountValue = account.getValue() - amount;
                if(newAccountValue >= 0) {
                    account.setValue(newAccountValue);
                    accountRepository.save(account);
                    return newAccountValue;
                }
                return -2;
            } catch (Exception e) {
                throw new Exception(e);
            }
        } else {
            return -1;
        }
    }

    public int validate(Transaction transaction) {
        int validation;
        switch (transaction.getType()) {
            case TransactionCodes.WITHDRAW -> {
                validation = validateWithdraw(transaction);
            }
            case TransactionCodes.TED -> {
                validation = validateTed(transaction);
            }
            case TransactionCodes.DEPOSIT -> {
                validation = TransactionCodes.ALLOWED;
            }
            case TransactionCodes.PIX -> {
                validation = validatePix(transaction);
            }
            default -> {
                validation = TransactionCodes.INVALID_TRANSACTION;
            }
        }
        return validation;
    }

    private int validateWithdraw(Transaction transaction) {
        if(
            transaction.getDateTime().getDayOfWeek() == DayOfWeek.SUNDAY ||
            transaction.getDateTime().getDayOfWeek() == DayOfWeek.SATURDAY &&
            transaction.getValue() > 1000
        ) {
            return TransactionCodes.MAX_VALUE_EXCEDED;
        } else if(
            transaction.getValue() > transaction.getOrigin().getValue()
        ) {
            return TransactionCodes.MAX_VALUE_EXCEDED;
        }
        return TransactionCodes.ALLOWED;
    }

    private int validateTed(Transaction transaction) {
        if(
            transaction.getDateTime().getDayOfWeek() == DayOfWeek.SUNDAY ||
            transaction.getDateTime().getDayOfWeek() == DayOfWeek.SATURDAY
        ) {
            return TransactionCodes.NOT_ALLOWED_DAY;
        } else if(
            transaction.getDateTime().getHour() < 8 ||
            transaction.getDateTime().getHour() > 17
        ) {
            return TransactionCodes.NOT_ALLOWED_TIME;
        } else if(
            transaction.getValue() > (transaction.getOrigin().getValue() + transaction.getOrigin().getLimit())
        ) {
            return TransactionCodes.NO_LIMIT;
        }
        return TransactionCodes.ALLOWED;
    }

    private int validatePix(Transaction transaction) {
        if(
            transaction.getValue() > transaction.getOrigin().getValue()
        ) {
            return TransactionCodes.MAX_VALUE_EXCEDED;
        }
        return TransactionCodes.ALLOWED;
    }
}
