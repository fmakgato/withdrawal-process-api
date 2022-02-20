package com.finance.withdrawalprocess.services.impl;

import com.finance.withdrawalprocess.configurations.PasswordEncoderConfig;
import com.finance.withdrawalprocess.entity.Investor;
import com.finance.withdrawalprocess.entity.Product;
import com.finance.withdrawalprocess.entity.Withdrawal;
import com.finance.withdrawalprocess.enums.ProductType;
import com.finance.withdrawalprocess.enums.Role;
import com.finance.withdrawalprocess.enums.WithdrawalStatus;
import com.finance.withdrawalprocess.models.ProcessRequest;
import com.finance.withdrawalprocess.repository.InvestorRepository;
import com.finance.withdrawalprocess.repository.ProductRepository;
import com.finance.withdrawalprocess.repository.WithdrawalRepository;
import com.finance.withdrawalprocess.services.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final ProductRepository productRepository;
    private final InvestorRepository investorRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;

    @Autowired
    public WithdrawalServiceImpl(
            WithdrawalRepository withdrawalRepository,
            ProductRepository productRepository,
            InvestorRepository investorRepository,
            PasswordEncoderConfig passwordEncoderConfig) {
        this.withdrawalRepository = withdrawalRepository;
        this.productRepository = productRepository;
        this.investorRepository = investorRepository;
        this.passwordEncoderConfig = passwordEncoderConfig;
    }

    @Override
    public Investor retrieveInvestorInformation(long investorId) {
        Investor investor;

        try {
            investor = this.investorRepository.findByInvestorId(investorId);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Investor details not found for id: %d message: %s", investorId, e.getMessage()));
        }

        return investor;
    }

    @Override
    public List<Product> getInvestorProducts(long investorId) {
        List<Product> products;

        try {
            products = this.productRepository.findAllByInvestor_InvestorId(investorId);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to get investor products for investorId: %d", investorId));
        }

        return products;
    }

    @Override
    public Withdrawal createNewWithdrawal(ProcessRequest request) {

        Withdrawal withdrawalResponse;
        Withdrawal withdrawal;

        try {

            withdrawal = this.checkAndSetWithdrawal(request);

            this.checkValidations(request, withdrawal);

            withdrawalResponse = this.withdrawalProcessing(withdrawal, request);

            return this.withdrawalRepository.save(withdrawalResponse);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to create a new order for product id: %d, message %s", request.getProductId(), e.getMessage()));
        }
    }

    @Override
    public Investor createNewInvestor(Investor investor) {
        Investor newInvestor;
        investor.setPassword(passwordEncoderConfig.bCryptPasswordEncoder().encode(investor.getPassword()));

        try {
            if (investor.getUserRole().getRoleName() == null) {
                investor.getUserRole().setRoleName(Role.ADMIN_ROLE.toString());
            }

            newInvestor = this.investorRepository.save(investor);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create new investor");
        }

        return newInvestor;
    }

    @Override
    public Product linkInvestorToProduct(ProcessRequest processRequest) {
        Product product;
        Investor investor;
        Product investorProductLink;

        try {
            product = this.productRepository.findByProductID(processRequest.getProductId());
            investor = this.investorRepository.findByInvestorId(processRequest.getInvestorId());

            product.setInvestor(investor);

            investorProductLink = this.productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to link product with investorId: %d", processRequest.getProductId()));
        }

        return investorProductLink;
    }

    @Override
    public Product createNewProduct(Product product) {
        Product newProduct;

        try {
            newProduct = this.productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to create product with name %s", product.getProductName()));
        }

        return newProduct;
    }

    @Override
    public Withdrawal processWithdrawal(ProcessRequest request) {
        Withdrawal withdrawal = this.checkAndSetWithdrawal(request);

        return this.withdrawalProcessing(withdrawal, request);
    }

    private void checkValidations(ProcessRequest request, Withdrawal withdrawal) {

        double withdrawalLimit = (request.getWithdrawalAmount() / withdrawal.getProduct().getCurrentBalance()) * 100;

        if (request.getWithdrawalAmount() > withdrawal.getProduct().getCurrentBalance() || withdrawal.getProduct().getCurrentBalance() <= 0) {
            throw new RuntimeException(String.format("You have insufficient funds in product: %s", withdrawal.getProduct().getProductType()));
        }

        if (withdrawalLimit > 90) {
            throw new RuntimeException(String.format("Investors cannot withdraw an AMOUNT more than 90 percent of the current balance of product: %s", withdrawal.getProduct().getProductType()));
        }

        if (withdrawal.getProduct().getProductType().equals(ProductType.RETIREMENT.toString()) && (Period.between(withdrawal.getProduct().getInvestor().getDateOfBirth(), LocalDate.now()).getYears() < 65)) {
            throw new RuntimeException(String.format("Your age must be greater than 65 for product: %s", withdrawal.getProduct().getProductType()));
        }
    }

    private Withdrawal checkAndSetWithdrawal(ProcessRequest request) {
        Product product;
        Investor investor;
        Withdrawal withdrawal;

        try {
            investor = this.investorRepository.findByInvestorId(request.getInvestorId());
            product = this.productRepository.findByProductID(request.getProductId());

            if (request.getWithdrawalId() <= 0) {
                withdrawal = new Withdrawal();

                withdrawal.setWithdrawalStartDate(LocalDateTime.now());
                withdrawal.setAvailableAmountBefore(product.getCurrentBalance());
                withdrawal.setAvailableAmount(product.getCurrentBalance());
                withdrawal.setWithdrawalAmount(request.getWithdrawalAmount());
                withdrawal.setInvestorId(investor.getInvestorId());
            } else {
                withdrawal = this.withdrawalRepository.findByWithdrawalId(request.getWithdrawalId());
            }

            product.setInvestor(investor);
            withdrawal.setProduct(product);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to set withdrawal with withdrawalId: %d, message: %s", request.getWithdrawalId(), e.getMessage()));
        }

        return withdrawal;
    }

    private Withdrawal withdrawalProcessing(Withdrawal withdrawal, ProcessRequest request) {

        this.checkAndSetWithdrawal(request);

        if (withdrawal.getWithdrawalId() <= 0) {
            withdrawal.setStatus(WithdrawalStatus.STARTED.toString());
        }

        double newAvailableBalance = withdrawal.getProduct().getCurrentBalance() - request.getWithdrawalAmount();

        if (Objects.equals(withdrawal.getStatus(), WithdrawalStatus.STARTED.toString()) && withdrawal.getWithdrawalId() > 0) {
            withdrawal.getProduct().setCurrentBalance(newAvailableBalance);
            withdrawal.setStatus(WithdrawalStatus.EXECUTING.toString());

            this.productRepository.save(withdrawal.getProduct());
        }

        if (Objects.equals(withdrawal.getStatus(), WithdrawalStatus.EXECUTING.toString()) &&
                (withdrawal.getProduct().getCurrentBalance() != newAvailableBalance)) {
            withdrawal.setAvailableAmount(newAvailableBalance);
            withdrawal.setStatus(WithdrawalStatus.DONE.toString());
            withdrawal.setWithdrawalProcessedDate(LocalDateTime.now());
        }

        return withdrawal;
    }
}
