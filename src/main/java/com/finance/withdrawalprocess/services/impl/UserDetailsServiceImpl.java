package com.finance.withdrawalprocess.services.impl;

import com.finance.withdrawalprocess.repository.InvestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final InvestorRepository investorRepository;

    @Autowired
    public UserDetailsServiceImpl(InvestorRepository investorRepository) {
        this.investorRepository = investorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailAddress) throws UsernameNotFoundException {
        return this.investorRepository.findByEmailAddress(emailAddress).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with emailAddress %s not found.", emailAddress))
        );
    }

}
