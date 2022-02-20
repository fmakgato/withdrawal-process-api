package com.finance.withdrawalprocess.repository;

import com.finance.withdrawalprocess.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {

    Investor findByInvestorId(long investorId);

    Optional<Investor> findByEmailAddress(String emailAddress);

    @Transactional
    @Modifying
    @Query("update Investor a " +
            "set a.enabled = true where a.emailAddress = ?1")
    int enableInvestor(String emailAddress);
}
