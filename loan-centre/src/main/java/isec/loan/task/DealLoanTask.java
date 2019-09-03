package isec.loan.task;

import isec.base.util.S;
import isec.loan.entity.Loan;
import isec.loan.entity.enums.IsDelete;
import isec.loan.entity.enums.LoanStatus;
import isec.loan.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 处理借款
 *
 * @author p
 * @date 2019-08-27
 */
@Component
public class DealLoanTask {

    private static final Logger logger = LoggerFactory.getLogger(DealLoanTask.class);

    @Autowired
    BillService billService;
    @Autowired
    LoanService loanService;
    @Autowired
    MessageService messageService;
    @Autowired
    OdinService odinService;
    @Autowired
    MoneyCalculateService moneyCalculateService;
    @Autowired
    SettingsService settingsService;


    /**
     * 关闭超过1小时没有领取的借款
     * cron = "0 0 12 * * ?"
     * fixedDelay = 10 * 60 * 1000
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void closeUnclosedLoan() {

        List<Loan> loans = loanService.findByWhere("loan_status = " + LoanStatus.LOANING.getKey() + " and is_delete = " + IsDelete.NO.getKey() + " and receive_loan_time < " + (S.getCurrentTimestamp() - Long.valueOf(settingsService.findBy("set_key","'accept_loan_interval'").getSetVal())));

        if (null != loans && loans.size() > 0) {
            for (Loan loan : loans) {
                loanService.closeLoan(loan.getLoanId());
            }
        }

    }


    /**
     * 订单关闭后清除用户认证状态
     * fixedDelay = 10 * 60 * 1000
     */

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void resetUserVerifyByClose() {
        loanService.resetUserVerifyByClose();
    }


}
