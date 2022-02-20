package com.finance.withdrawalprocess.repository;

import com.finance.withdrawalprocess.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    Withdrawal findByWithdrawalId(long withdrawalId);
}
