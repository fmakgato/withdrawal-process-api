package com.finance.withdrawalprocess.controllers;

import com.finance.withdrawalprocess.entity.Investor;
import com.finance.withdrawalprocess.entity.Product;
import com.finance.withdrawalprocess.entity.Withdrawal;
import com.finance.withdrawalprocess.enums.WithdrawalStatus;
import com.finance.withdrawalprocess.models.ProcessRequest;
import com.finance.withdrawalprocess.services.WithdrawalService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("withdrawal")
public class WithdrawalController {
    // Documentation - http://localhost:8092/v3/api-docs
    // Doc swagger-ui - http://localhost:8092/swagger-ui/
    // DB Console - http://localhost:8092/h2-console
    /*
    SELECT * FROM INVESTOR;
    SELECT * FROM PRODUCT;
    SELECT * FROM USER_ROLE;
    SELECT * FROM WITHDRAWAL;
    */

    private final WithdrawalService withdrawalService;

    @Autowired
    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @ApiOperation(value = "Retrieving investor information from the DB")
    @GetMapping("/information/{investorId}") // http://localhost:8092/withdrawal/information/1
    public ResponseEntity<Investor> retrieveInvestorInformation(@PathVariable long investorId) {
        Investor investor = this.withdrawalService.retrieveInvestorInformation(investorId);
        return ResponseEntity.ok(investor);
    }

    @ApiOperation(value = "Creating a new investor and save onto the DB")
    @PostMapping("/create/investor") // http://localhost:8092/withdrawal/create/investor
    public ResponseEntity<Investor> createNewInvestor(@RequestBody Investor investor) {
        return ResponseEntity.ok(this.withdrawalService.createNewInvestor(investor));
    }

    @ApiOperation(value = "Linking a product to an investor and save onto the DB")
    @PostMapping("/link/investor") // http://localhost:8092/withdrawal/link/investor
    public ResponseEntity<Product> linkInvestorToProduct(@RequestBody ProcessRequest processRequest) {
        return ResponseEntity.ok(this.withdrawalService.linkInvestorToProduct(processRequest));
    }

    @ApiOperation(value = "Creating a new product and save onto the DB")
    @PostMapping("/create/product") // http://localhost:8092/withdrawal/create/product
    public ResponseEntity<Product> createNewProduct(@RequestBody Product product) {
        return ResponseEntity.ok(this.withdrawalService.createNewProduct(product));
    }

    @ApiOperation(value = "Retrieving a list of products linked to a certain investor the DB")
    @GetMapping("/products/{investorId}") // http://localhost:8092/withdrawal/products/1
    public ResponseEntity<List<Product>> getInvestorProducts(@PathVariable long investorId) {
        return ResponseEntity.ok(this.withdrawalService.getInvestorProducts(investorId));
    }

    @ApiOperation(value = "Creating a new withdrawal and save onto the DB")
    @PostMapping("/create") // http://localhost:8092/withdrawal/create
    public ResponseEntity<Withdrawal> createNewWithdrawal(@RequestBody ProcessRequest request) {
        Withdrawal withdrawalResponse = this.withdrawalService.createNewWithdrawal(request);
        return ResponseEntity.ok(withdrawalResponse);
    }

    @ApiOperation(value = "Processing a withdrawal and save onto the DB")
    @PostMapping("/process/withdrawal") // http://localhost:8092/withdrawal/process/withdrawal
    public ResponseEntity<Withdrawal> processWithdrawal(@RequestBody ProcessRequest request) {
        Withdrawal withdrawalResponse = this.withdrawalService.processWithdrawal(request);

        if (!Objects.equals(withdrawalResponse.getStatus(), WithdrawalStatus.DONE.toString())) {
            this.withdrawalService.processWithdrawal(request);
        }

        return ResponseEntity.ok(withdrawalResponse);
    }
}
