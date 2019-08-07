package isec.loan.service;

import isec.loan.entity.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MoneyCalculateService {


    @Autowired
    LoanService loanService;

    /**
     * 实际到账金额
     *
     * @param borrowMoney
     * @param riskCost
     * @return
     */
    public int getRealMoney(int borrowMoney, int riskCost) {
        return (borrowMoney - riskCost) / 100;
    }


    /**
     * 分转换成元
     *
     * @param money
     * @return
     */
    public String transferMoneyToYuan(int money) {
        return String.valueOf(money / 100);
    }

    /**
     * 应还金额
     *
     * @param rete
     * @param borrowMoney
     * @param days
     * @return
     */
    public BigDecimal getRepayMoney(BigDecimal rete, int borrowMoney, int days) {
        borrowMoney = borrowMoney / 100;
        rete = rete.divide(new BigDecimal(100));
        return rete.divide(new BigDecimal(12 * 30), 10, RoundingMode.DOWN).multiply(new BigDecimal(borrowMoney)).multiply(new BigDecimal(days))
                .add(new BigDecimal(borrowMoney));
    }

    /**
     * 每天逾期费用
     *
     * @param overdueRate
     * @param borrowMoney
     * @return
     */
    public BigDecimal getOverdueMoney(BigDecimal overdueRate, int borrowMoney) {
        return new BigDecimal(borrowMoney / 100).multiply(overdueRate.divide(new BigDecimal(100)));
    }


    /**
     * 总期费用
     *
     * @param overdueRate
     * @param borrowMoney
     * @return
     */
    public BigDecimal getTotalOverdueMoney(BigDecimal overdueRate, int borrowMoney, int days) {
        return new BigDecimal(borrowMoney / 100).multiply(overdueRate.divide(new BigDecimal(100)).multiply(BigDecimal.valueOf(days)));
    }

    /**
     * 实际到账金额
     *
     * @param loanId
     * @return
     */
    public int getRealMoney(String loanId) {
        Loan loan = loanService.findById(loanId);
        if (null != loan) {
            return loan.getBorrowMoney() - loan.getRiskCost();
        }
        return 0;
    }

    /**
     * 借款利息
     *
     * @param loanId 借款编号
     * @return
     */
    public int getRateMoney(String loanId) {

        Loan loan = loanService.findById(loanId);
        if (null != loan) {
            //借款金额
            int borrowMoney = loan.getBorrowMoney();

            return loan.getRete().divide(new BigDecimal(12 * 3000)).multiply(new BigDecimal(borrowMoney)).multiply(new BigDecimal(loan.getDays()))
                    .intValue();
        }
        return 0;

    }

    /**
     * 账单金额【应还金额】
     *
     * @param loanId 借款编号
     * @return
     */
    public int getRepayMoney(String loanId) {

        Loan loan = loanService.findById(loanId);
        if (null != loan) {
            //借款金额
            int borrowMoney = loan.getBorrowMoney();

            return new BigDecimal(getRateMoney(loanId))
                    .add(new BigDecimal(borrowMoney)).intValue();
        }
        return 0;

    }


}
