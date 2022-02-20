package com.finance.withdrawalprocess.configurations;

import com.finance.withdrawalprocess.entity.Investor;
import com.finance.withdrawalprocess.entity.Product;
import com.finance.withdrawalprocess.entity.UserRole;
import com.finance.withdrawalprocess.enums.ProductType;
import com.finance.withdrawalprocess.enums.Role;
import com.finance.withdrawalprocess.repository.InvestorRepository;
import com.finance.withdrawalprocess.repository.ProductRepository;
import com.finance.withdrawalprocess.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SqlDataPopulationConfig {

    private final PasswordEncoderConfig passwordEncoderConfig;

    public SqlDataPopulationConfig(PasswordEncoderConfig passwordEncoderConfig) {
        this.passwordEncoderConfig = passwordEncoderConfig;
    }

    @Bean
    CommandLineRunner commandLineRunner(
            ProductRepository productRepository,
            InvestorRepository investorRepository,
            UserRoleRepository userRoleRepository) {
        return args -> {
            UserRole adminUserRole = UserRole.builder()
                    .roleName(Role.ADMIN_ROLE.toString())
                    .build();

            userRoleRepository.save(adminUserRole);

            Investor investor1 = Investor.builder()
                    .investorId(1)
                    .emailAddress("sfmakgato@gmail.com")
                    .dateOfBirth(LocalDate.of(1921,12,12))
                    .firstName("France")
                    .mobileNumber("0764730789")
                    .lastName("Makgato")
                    .address("Stand No:123 Mokomene, Matoks, 0811")
                    .password(passwordEncoderConfig.bCryptPasswordEncoder().encode("password"))
                    .locked(false)
                    .enabled(true)
                    .userRole(adminUserRole)
                    .build();

            Investor investor2 = Investor.builder()
                    .investorId(2)
                    .emailAddress("sethuwane@gmail.com")
                    .dateOfBirth(LocalDate.of(2000,10,29))
                    .firstName("Sethuwane")
                    .mobileNumber("0680067725")
                    .lastName("Mkhonto")
                    .address("55 Fake Street, Polokwane, 0700")
                    .password(passwordEncoderConfig.bCryptPasswordEncoder().encode("password"))
                    .locked(false)
                    .enabled(true)
                    .userRole(adminUserRole)
                    .build();

            List<Investor> investors = new ArrayList<>();
            investors.add(investor1);
            investors.add(investor2);

            investorRepository.saveAll(investors);

            Product retirementProduct = Product.builder()
                    .productID(1)
                    .productType(ProductType.RETIREMENT.toString())
                    .currentBalance(500000.00)
                    .productName("RetirementProduct")
                    .investor(investor1)
                    .build();

            Product savingsProduct = Product.builder()
                    .productID(2)
                    .productType(ProductType.SAVINGS.toString())
                    .currentBalance(36000.00)
                    .productName("SavingsProduct")
                    .investor(investor1)
                    .build();

            List<Product> products = new ArrayList<>();
            products.add(retirementProduct);
            products.add(savingsProduct);

            productRepository.saveAll(products);
        };
    }
}
