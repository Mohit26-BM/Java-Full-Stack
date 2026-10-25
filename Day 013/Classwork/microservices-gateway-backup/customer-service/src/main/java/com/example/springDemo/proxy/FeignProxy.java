package com.example.springDemo.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.springDemo.dto.LoanResponse;
import com.example.springDemo.dto.SavingsAccountResponse;

@FeignClient(name = "account-service")
public interface FeignProxy {

    @GetMapping("/api/v1/accounts/savings/{customerId}")
    List<SavingsAccountResponse> getSavingsByCustomerId(@PathVariable("customerId") Long customerId);

    @GetMapping("/api/v1/accounts/loans/{customerId}")
    List<LoanResponse> getLoansByCustomerId(@PathVariable("customerId") Long customerId);

    @GetMapping("/api/v1/accounts/port")
    String getPort();
}
