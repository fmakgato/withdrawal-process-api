package com.finance.withdrawalprocess.services;

import com.finance.withdrawalprocess.configurations.PasswordEncoderConfig;
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
import com.finance.withdrawalprocess.repository.UserRoleRepository;
import com.finance.withdrawalprocess.repository.WithdrawalRepository;
import com.finance.withdrawalprocess.services.impl.WithdrawalServiceImpl;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
class WithdrawalServiceImplTest {

    WithdrawalServiceImpl withdrawalService;
    WithdrawalRepository withdrawalRepository;
    ProductRepository productRepository;
    InvestorRepository investorRepository;
    UserRoleRepository userRoleRepository;
    PasswordEncoderConfig passwordEncoderConfig;
    String token = "asdf1234";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeEach
    void setUp() {
        withdrawalRepository = Mockito.mock(WithdrawalRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        investorRepository = Mockito.mock(InvestorRepository.class);
        passwordEncoderConfig = Mockito.mock(PasswordEncoderConfig.class);
        userRoleRepository = Mockito.mock(UserRoleRepository.class);
        withdrawalService = new WithdrawalServiceImpl(
                withdrawalRepository, productRepository, investorRepository, passwordEncoderConfig
        );
    }

    @Test
    void retrieveInvestorInformation() {
        when(investorRepository.findByInvestorId(0)).thenReturn(mockInvestor());

        Investor investor = withdrawalService.retrieveInvestorInformation(0);

        Assertions.assertNotNull(investor);
        Assertions.assertEquals("Makgato", investor.getLastName());
    }

    @Test
    void retrieveInvestorInformationError() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Not found");

        when(investorRepository.findByInvestorId(0)).thenThrow(RuntimeException.class);
        withdrawalService.retrieveInvestorInformation(1);
    }

    @Test
    void createNewWithdrawalInvalidAge() {
        ProcessRequest processRequest = new ProcessRequest();
        processRequest.setProductId(1);
        processRequest.setInvestorId(1);

        Investor investor = mockInvestor();
        investor.setDateOfBirth(LocalDate.now());

        Product product = mockProducts().get(0);
        product.setInvestor(investor);
        product.setProductType(ProductType.RETIREMENT.toString());

        Withdrawal withdrawal = mockWithdrawal();
        withdrawal.setProduct(product);

        when(productRepository.findByProductID(1)).thenReturn(product);
        when(investorRepository.findByInvestorId(1)).thenReturn(investor);

        try {
            withdrawalService.createNewWithdrawal(processRequest);
        } catch (Exception e) {
            expectedException.expect(RuntimeException.class);
            Assertions.assertTrue(e.getMessage().contains("Your age must be greater than 65 for product: RETIREMENT"));
        }
    }

    @Test
    void createNewWithdrawalLimitError() {
        ProcessRequest processRequest = new ProcessRequest();
        processRequest.setProductId(1);
        processRequest.setInvestorId(1);

        when(productRepository.findByProductID(1)).thenReturn(mockProducts().get(0));
        when(investorRepository.findByInvestorId(1)).thenReturn(mockInvestor());

        try {
            withdrawalService.createNewWithdrawal(processRequest);
        } catch (Exception e) {
            expectedException.expect(RuntimeException.class);
            Assertions.assertTrue(e.getMessage().contains("Investors cannot withdraw an AMOUNT more than 90 percent"));
        }
    }

    @Test
    void createNewWithdrawalInsufficientFunds() {
        ProcessRequest processRequest = new ProcessRequest();
        processRequest.setProductId(1);
        processRequest.setInvestorId(1);

        Withdrawal withdrawal = mockWithdrawal();
        withdrawal.getProduct().setCurrentBalance(0.00);

        when(productRepository.findByProductID(1)).thenReturn(mockProducts().get(0));
        when(investorRepository.findByInvestorId(1)).thenReturn(mockInvestor());

        try {
            withdrawalService.createNewWithdrawal(processRequest);
        } catch (Exception e) {
            expectedException.expect(RuntimeException.class);
            Assertions.assertTrue(e.getMessage().contains("You have insufficient funds in product"));
        }
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
                .status(WithdrawalStatus.STARTED.toString())
                .availableAmount(34000.00)
                .product(Objects.requireNonNull(mockProducts()).get(0))
                .withdrawalId(1)
                .build();
    }
}
