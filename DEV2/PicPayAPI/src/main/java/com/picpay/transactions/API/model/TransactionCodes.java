package com.picpay.transactions.API.model;

public interface TransactionCodes {
    String PIX = "pix";
    String TED = "ted";
    String DEPOSIT = "deposit";
    String WITHDRAW = "withdraw";
    int ALLOWED_WITH_LIMIT = 2;
    int ALLOWED = 1;
    int INVALID_TRANSACTION = 0;
    int NO_LIMIT = -1;
    int MAX_VALUE_EXCEDED = -2;
    int NOT_ALLOWED_DAY = -3;
    int NOT_ALLOWED_TIME = -4;

}
