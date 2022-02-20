package com.finance.withdrawalprocess.services;

import com.finance.withdrawalprocess.entity.Investor;
import com.finance.withdrawalprocess.entity.Product;
import com.finance.withdrawalprocess.entity.Withdrawal;
import com.finance.withdrawalprocess.models.ProcessRequest;

import java.util.List;

public interface WithdrawalService {

    Investor retrieveInvestorInformation(long investorId);

    List<Product> getInvestorProducts(long investorId);

    Withdrawal createNewWithdrawal(ProcessRequest request);

    Investor createNewInvestor(Investor investor);

    Product linkInvestorToProduct(ProcessRequest processRequest);

    Product createNewProduct(Product product);

    Withdrawal processWithdrawal(ProcessRequest request);
}
