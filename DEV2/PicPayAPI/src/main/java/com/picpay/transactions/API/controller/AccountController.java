package com.picpay.transactions.API.controller;

import com.picpay.transactions.API.model.Account;
import com.picpay.transactions.API.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    private final AccountService accountService;
    private final Validator validator;

    @Autowired
    public AccountController(AccountService accountService, Validator validator) {
        this.accountService = accountService;
        this.validator = validator;
    }

    @GetMapping("list")
    public ResponseEntity<?> listAccounts() {
        try {
            List<Account> account = accountService.findAll();
            return ResponseEntity.status(200).body(account);
        } catch(Exception e) {
            return ResponseEntity.status(404).body("No account found.");
        }
    }

    @GetMapping("get/{number}")
    public ResponseEntity<?> getAccount(@PathVariable String number) {
        try {
            Account account = accountService.findOne(number);
            return ResponseEntity.status(200).body(account);
        } catch(Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("create")
    public ResponseEntity<?> insertAccount(@Valid @RequestBody Account account, BindingResult result) {
        if(!result.hasErrors()) {
            Account createdAccount = accountService.createOne(account);
            if(createdAccount.getNumber() != null) {
                return ResponseEntity.status(200).body(createdAccount.toString());
            } else {
                return ResponseEntity.status(500).body("Failed creating the account");
            }
        } else {
            return ResponseEntity.status(400).body(getErrorsMap(result.getFieldErrors()));
        }
    }

    @DeleteMapping("remove/{number}")
    public ResponseEntity<?> removeAccount(@PathVariable String number) {
        Account removedAccount = accountService.deleteOne(number);
        if(removedAccount != null) {
            return ResponseEntity.status(204).body(removedAccount);
        }
        return ResponseEntity.status(404).body("Account not found");
    }

    @PutMapping("/update/{number}")
    public ResponseEntity<?> updateAccount(@PathVariable String number, @Valid @RequestBody Account updateData, BindingResult result) {
        if(!result.hasErrors()) {
            Account updatedAccount = accountService.update(number, updateData);
            if(updatedAccount != null) {
                return ResponseEntity.status(204).body(updatedAccount);
            }
            return ResponseEntity.status(404).body("Account not found");
        } else {
            return ResponseEntity.status(400).body(getErrorsMap(result.getFieldErrors()));
        }
    }

    @PatchMapping("/partial-update/{number}")
    public ResponseEntity<?> updatePartialAccount(@PathVariable String number, @RequestBody Map<String, Object> updateData) {
        try {
            Account account = accountService.findOne(number);

            Map<String, Method> actions = account.fillSettersMap();
            for (String key : updateData.keySet()) {
                Method action = actions.get(key);
                if (action != null) {
                    action.invoke(account, updateData.get(key));
                }
            }

            DataBinder binder = new DataBinder(account);
            binder.setValidator(validator);
            binder.validate();
            BindingResult result = binder.getBindingResult();

            if (!result.hasErrors()) {
                Account updatedAccount = accountService.updatePartial(number, updateData);
                return ResponseEntity.status(204).body(updatedAccount);
            } else {
                return ResponseEntity.status(400).body(getErrorsMap(result.getFieldErrors()));
            }
        } catch(IllegalArgumentException iae) {
            return ResponseEntity.status(400).body(iae.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Account not found.")) {
                return ResponseEntity.status(404).body(e.getMessage());
            } else {
                return ResponseEntity.status(500).body(e.getMessage());
            }
        } catch(Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    private static Map<String, String> getErrorsMap(List<FieldError> errors) {
        Map<String, String> errorMap = new HashMap<String, String>();
        for (FieldError error : errors) {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errorMap.put(field, message);
        }
        return errorMap.size() > 0 ? errorMap : null;
    }
}
