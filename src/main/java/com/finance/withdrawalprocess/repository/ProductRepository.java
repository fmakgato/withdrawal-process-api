package com.finance.withdrawalprocess.repository;

import com.finance.withdrawalprocess.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "select * from product where investor_id = 1", nativeQuery = true)
    List<Product> findAllByInvestor_InvestorId(long investorId);

    Product findByProductID(long productId);
}
