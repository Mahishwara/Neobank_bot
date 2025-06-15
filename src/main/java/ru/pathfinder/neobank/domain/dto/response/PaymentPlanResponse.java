package ru.pathfinder.neobank.domain.dto.response;

import java.util.List;

public record PaymentPlanResponse(
        String clientId,
        String accountId,
        String creditNumber,
        double amount,
        String startCreditDate,  // dd-MM-yyyy
        String endCreditDate,    // dd-MM-yyyy
        long rate,
        int period,
        String creditName,
        long monthPayment,
        long totalAmount,
        boolean paidInCurrentMonth,
        int currencyNumber,
        List<PaymentPlanItem> paymentPlan
) {
    public record PaymentPlanItem(
            String paymentDate,
            long monthPayment,
            long repaymentDept,
            long paymentPercent,
            long balanceAmount,
            int paymentNumber
    ) {}
}
