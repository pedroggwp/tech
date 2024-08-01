package com.picpay.transactions.API.model;

import org.aspectj.apache.bcel.classfile.Code;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class Transaction {
    private String type;
    private Account origin;
    private Account target;
    private Double value;
    private LocalDateTime dateTime;

    public Transaction() {}
    public Transaction(String type, Account origin, Account target, Double value, LocalDateTime dateTime) {
        this.type = type;
        this.origin = origin;
        this.target = target;
        this.value = value;
        this.dateTime = dateTime;
    }

    public Transaction(String type, Account origin, Account target, Double value) {
        this.type = type;
        this.origin = origin;
        this.target = target;
        this.value = value;
        this.dateTime = LocalDateTime.now();
    }

    public Transaction(String type, Account account, Double value) {
        this.type = type;
        switch (type) {
            case TransactionCodes.DEPOSIT -> this.target = account;
            case TransactionCodes.WITHDRAW -> this.origin = account;
        }
        this.value = value;
        this.dateTime = LocalDateTime.now();
    }

    public String getType() {
        return this.type;
    }

    public Account getOrigin() {
        return this.origin;
    }

    public Account getTarget() {
        return this.target;
    }

    public Double getValue() {
        return this.value;
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }
}
