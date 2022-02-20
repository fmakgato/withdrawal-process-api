package com.finance.withdrawalprocess.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "withdrawal")
@Table(name = "Withdrawal")
public class Withdrawal {

    @Id
    @SequenceGenerator(name = "withdrawal_sequence", sequenceName = "withdrawal_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "withdrawal_sequence")
    @Column(name = "withdrawal_id", nullable = false, updatable = false)
    private long withdrawalId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "withdrawal_amount", nullable = false)
    private double withdrawalAmount;

    @Column(name = "available_amount", nullable = false)
    private double availableAmount;

    @Column(name = "available_amount_before", nullable = false)
    private double availableAmountBefore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Column(name = "investor_id", nullable = false)
    private long investorId;

    @Column(name = "withdrawal_start_date", nullable = false)
    private LocalDateTime withdrawalStartDate;

    @Column(name = "withdrawal_processed_date")
    private LocalDateTime withdrawalProcessedDate;

    public long getWithdrawalId() {
        return withdrawalId;
    }

    public void setWithdrawalId(long withdrawalId) {
        this.withdrawalId = withdrawalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getWithdrawalAmount() {
        return withdrawalAmount;
    }

    public void setWithdrawalAmount(double withdrawalAmount) {
        this.withdrawalAmount = withdrawalAmount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(long investor) {
        this.investorId = investor;
    }

    public double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public double getAvailableAmountBefore() {
        return availableAmountBefore;
    }

    public void setAvailableAmountBefore(double previousAmount) {
        this.availableAmountBefore = previousAmount;
    }

    public LocalDateTime getWithdrawalStartDate() {
        return withdrawalStartDate;
    }

    public void setWithdrawalStartDate(LocalDateTime withdrawalDate) {
        this.withdrawalStartDate = withdrawalDate;
    }

    public LocalDateTime getWithdrawalProcessedDate() {
        return withdrawalProcessedDate;
    }

    public void setWithdrawalProcessedDate(LocalDateTime withdrawalProcessedDate) {
        this.withdrawalProcessedDate = withdrawalProcessedDate;
    }
}
