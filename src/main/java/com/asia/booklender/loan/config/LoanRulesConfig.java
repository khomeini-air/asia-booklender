package com.asia.booklender.loan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Loan configuration defined at application.yml
 */
@Configuration
@ConfigurationProperties(prefix = "loan.rules")
@Data
public class LoanRulesConfig {
    /* loan.rules.max_active_loans */
    private int maxActiveLoans = 3;

    /* loan.rules.loan_duration_days */
    private int loanDurationDays = 14;

    /* loan.rules.enforce_overdue_restriction */
    private boolean enforceOverdueRestriction = true;

    /* loan.rules.low_inventory_threshold */
    private int lowInventoryThreshold = 2;
}
