package com.picpay.transactions.API.repository;

import com.picpay.transactions.API.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
