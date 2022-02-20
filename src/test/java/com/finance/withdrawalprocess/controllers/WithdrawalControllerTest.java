package com.finance.withdrawalprocess.controllers;

import com.finance.withdrawalprocess.entity.Investor;
import com.finance.withdrawalprocess.entity.Product;
import com.finance.withdrawalprocess.entity.UserRole;
import com.finance.withdrawalprocess.entity.Withdrawal;
import com.finance.withdrawalprocess.enums.ProductType;
import com.finance.withdrawalprocess.enums.Role;
import com.finance.withdrawalprocess.enums.WithdrawalStatus;
import com.finance.withdrawalprocess.models.ProcessRequest;
import com.finance.withdrawalprocess.repository.InvestorRepository;
import com.finance.withdrawalprocess.repository.ProductRepository;
import com.finance.withdrawalprocess.repository.WithdrawalRepository;
import com.finance.withdrawalprocess.services.impl.WithdrawalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
class WithdrawalControllerTest {

    @Mock
    WithdrawalServiceImpl withdrawalService;

    @Mock
    WithdrawalRepository withdrawalRepository;

    @Mock
    ProductRepository productRepository;
    
    @Mock
    InvestorRepository investorRepository;

    @InjectMocks
    WithdrawalController withdrawalController;


    @Sql(executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD,scripts = {
            "classpath:/data/drop-tables.sql",
            "classpath:/data/create-tables.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = {
            "classpath:/data/drop-tables.sql"})
    @BeforeEach
    void setUp() {
        withdrawalController = new WithdrawalController(withdrawalService);
    }

    @Test
    void createNewWithdrawal() {
        ProcessRequest processRequest = new ProcessRequest();
        processRequest.setProductId(1);
        processRequest.setInvestorId(1);
        processRequest.setWithdrawalId(0);

        Withdrawal withdrawal = mockWithdrawal();
        withdrawal.setStatus(WithdrawalStatus.STARTED.toString());

        Mockito.when(withdrawalService.createNewWithdrawal(any())).thenReturn(withdrawal);
        Mockito.when(withdrawalRepository.save(any())).thenReturn(withdrawal);

        ResponseEntity<Withdrawal> response = withdrawalController.createNewWithdrawal(processRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(34000.00, response.getBody().getAvailableAmount());
        assertEquals(WithdrawalStatus.STARTED.toString(), response.getBody().getStatus());
    }

    @Test
    void processWithdrawalExecuting() {
        ProcessRequest processRequest = new ProcessRequest();
        processRequest.setProductId(1);
        processRequest.setInvestorId(1);
        processRequest.setWithdrawalId(1);

        Withdrawal withdrawal = mockWithdrawal();
        withdrawal.setStatus(WithdrawalStatus.EXECUTING.toString());

        Mockito.when(withdrawalService.processWithdrawal(any())).thenReturn(withdrawal);
        Mockito.when(withdrawalRepository.save(any())).thenReturn(withdrawal);

        this.createNewWithdrawal();

        ResponseEntity<Withdrawal> response = withdrawalController.processWithdrawal(processRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(34000.00, response.getBody().getAvailableAmount());
        assertEquals(WithdrawalStatus.EXECUTING.toString(), response.getBody().getStatus());
    }

    @Test
    void processWithdrawalDone() {
        ProcessRequest processRequest = new ProcessRequest();
        processRequest.setProductId(1);
        processRequest.setInvestorId(1);
        processRequest.setWithdrawalId(1);

        Withdrawal withdrawal = mockWithdrawal();
        withdrawal.setStatus(WithdrawalStatus.DONE.toString());

        Mockito.when(withdrawalService.processWithdrawal(any())).thenReturn(withdrawal);
        Mockito.when(withdrawalRepository.save(any())).thenReturn(withdrawal);

        this.createNewWithdrawal();

        ResponseEntity<Withdrawal> response = withdrawalController.processWithdrawal(processRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(34000.00, response.getBody().getAvailableAmount());
        assertEquals(WithdrawalStatus.DONE.toString(), response.getBody().getStatus());
    }

    @Test
    void getInvestorProducts() {
        Mockito.when(withdrawalService.getInvestorProducts(1)).thenReturn(mockProducts());
        Mockito.when(productRepository.findAll()).thenReturn(mockProducts());

        ResponseEntity<List<Product>> response = withdrawalController.getInvestorProducts(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void retrieveInvestorInformation() {
        Mockito.when(withdrawalService.retrieveInvestorInformation(1)).thenReturn(mockInvestor());
        Mockito.when(investorRepository.findByInvestorId(1)).thenReturn(mockInvestor());

        ResponseEntity<Investor> response = withdrawalController.retrieveInvestorInformation(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("France", response.getBody().getFirstName());
    }
    
    private Investor mockInvestor() {

        return Investor.builder()
                .investorId(1)
                .emailAddress("sfmakgato@gmail.com")
                .dateOfBirth(LocalDate.of(1921, 12, 12))
                .firstName("France")
                .mobileNumber("0764730789")
                .lastName("Makgato")
                .address("123 Mokomene 0811")
                .enabled(true)
                .locked(false)
                .password("password")
                .userRole(UserRole.builder().roleName(Role.ADMIN_ROLE.toString()).build())
                .build();
    }

    private List<Product> mockProducts() {

        Product savingsProduct = Product.builder()
                .productID(2)
                .productType(ProductType.SAVINGS.toString())
                .currentBalance(36000.00)
                .productName("SavingsProduct")
                .investor(mockInvestor())
                .build();

        Product retirementProduct = Product.builder()
                .productID(1)
                .productType(ProductType.RETIREMENT.toString())
                .currentBalance(500000.00)
                .productName("RetirementProduct")
                .investor(mockInvestor())
                .build();

        List<Product> products = new ArrayList<>();
        products.add(savingsProduct);
        products.add(retirementProduct);

        return products;
    }

    private Withdrawal mockWithdrawal() {

        return Withdrawal.builder()
                .withdrawalAmount(2000.00)
                .availableAmountBefore(Objects.requireNonNull(mockProducts()).get(0).getCurrentBalance())
                .withdrawalStartDate(LocalDateTime.now())
                .availableAmount(34000.00)
                .product(Objects.requireNonNull(mockProducts()).get(0))
                .withdrawalId(1)
                .build();
    }
}
