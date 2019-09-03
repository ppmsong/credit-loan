package isec.loan.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isec.base.util.S;
import isec.loan.entity.Bill;
import isec.loan.entity.Loan;
import isec.loan.mapper.PayFlowMapper;

@Service
public class MoneyCalculateService {


    @Autowired
    LoanService loanService;
    @Autowired
    BillService billService;
    @Autowired
    PayFlowMapper payFlowMapper;

    /**
     * 借款实际到账金额
     *
     * @param borrowMoney
     * @param riskCost
     * @return
     */
    public BigDecimal getRealMoney(int borrowMoney, int riskCost) {

        return fenToYuan(borrowMoney - riskCost);
    }


    /**
     * 分转换成元
     *
     * @param money
     * @return
     */
    public String transferMoneyToYuan(int money) {
        return String.valueOf(new BigDecimal(money).divide(new BigDecimal(100), 2, RoundingMode.DOWN));
    }

    /**
     * 产品详情页显示
     * 应还金额 = 借款金额+借款金额*每日利率*天数+借款金额*逾期每日利率*逾期天数
     *
     * @param rete
     * @param borrowMoney
     * @param days
     * @return
     */
    public BigDecimal getProductRepayMoney(BigDecimal rete, int borrowMoney, int days) {

        return (getRateMoney(rete, borrowMoney, days)
                .add(new BigDecimal(borrowMoney).divide(new BigDecimal(100), 10, RoundingMode.DOWN))).setScale(2, RoundingMode.DOWN);
    }

    /**
     * 账单逾期时间（精确到秒）
     *
     * @param deadline 截止时间
     * @return
     */
    public int overDueSecond(long deadline) {
        long over = S.getCurrentTimestamp() - deadline;
        if (over > 0) {
            return (int) Math.ceil(S.getCurrentTimestamp() - deadline);
        }
        return 0;
    }

    /**
     * 账单页显示
     * 应还金额 = 应还金额（借款金额+借款金额*每日利率*天数）+借款金额*逾期每日利率*逾期天数
     *
     * @param billId 账单号
     * @return
     */
    public BigDecimal getBillRepayMoney(String billId) {

        Bill bill = billService.findById(billId);
        if (null != bill) {
            //本金
            int r_basic = bill.getrBasic();
            //利息
            int r_interest = bill.getrInterest();
            //到期应还金额
            int repayment_amount = bill.getRepaymentAmount();

            //计算预期利息
            Loan loan = loanService.findById(bill.getLoanId());
            //总预期利息
            BigDecimal alloverdue = new BigDecimal(0);
            if (null != loan) {
                alloverdue = getTotalOverdueMoney(loan.getOverdueRate(), r_basic, overDueSecond(bill.getDeadline()));
            }
            //账单应还金额return
            return fenToYuan(repayment_amount).add(alloverdue).setScale(2,BigDecimal.ROUND_DOWN);

        }
        return new BigDecimal(0);
    }

    /**
     * 每天逾期费用
     *
     * @param overdueRate 每天逾期利率（%）
     * @param borrowMoney 借款金额（分）
     * @return
     */
    public BigDecimal getDayOverdueMoney(BigDecimal overdueRate, int borrowMoney) {
        return new BigDecimal(borrowMoney).divide(new BigDecimal(100), 2, RoundingMode.DOWN).multiply(overdueRate.divide(new BigDecimal(100), 2, RoundingMode.DOWN)).setScale(2,RoundingMode.DOWN);
    }

    /**
     * 每秒逾期费用
     *
     * @param overdueRate 天逾期利率（%）
     * @param borrowMoney 借款金额（分）
     * @return
     */
    public BigDecimal getSecondOverdueMoney(BigDecimal overdueRate, int borrowMoney) {
        return new BigDecimal(borrowMoney).divide(new BigDecimal(100), 2, RoundingMode.DOWN).multiply(overdueRate.divide(new BigDecimal(100 * 24 * 60 * 60), 10, RoundingMode.DOWN));
    }


    /**
     * 总期费用(元)
     *
     * @param overdueRate    每天逾期利率（%）
     * @param borrowMoney    借款金额（分）
     * @param overdueSeconds 逾期时间（秒）.setScale(2,RoundingMode.DOWN)
     * @return
     */
    public BigDecimal getTotalOverdueMoney(BigDecimal overdueRate, int borrowMoney, int overdueSeconds) {
        if (overdueSeconds > 0) {
            //根据逾期描述计算逾期天数（超过1秒就算一天）
            return getDayOverdueMoney(overdueRate, borrowMoney).multiply(BigDecimal.valueOf(overdueDays(overdueSeconds))).setScale(2,BigDecimal.ROUND_DOWN);
        }
        return new BigDecimal(0);
    }

    /**
     * 根据逾期描述计算逾期天数
     *
     * @param overdueSeconds 逾期秒数
     * @return
     */
    public int overdueDays(int overdueSeconds) {

        BigDecimal days = new BigDecimal(overdueSeconds).divide(new BigDecimal(24 * 60 * 60),10,RoundingMode.DOWN);
        return days.setScale(0, BigDecimal.ROUND_UP).intValue();

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
     * 生成账单时候计算借款利息
     * 借款利息 = 借款金额 * （借款年利率/12/30）* 借款天数
     *
     * @return
     */
    public BigDecimal getRateMoney(BigDecimal rete, int borrowMoney, int days) {

        BigDecimal borrow = fenToYuan(borrowMoney);
        rete = rete.divide(new BigDecimal(100), 10, RoundingMode.DOWN);
        return rete.divide(new BigDecimal(12 * 30), 10, RoundingMode.DOWN).multiply(borrow).multiply(new BigDecimal(days));


    }


    /**
     * 分转为元
     *
     * @param fen
     * @return
     */
    public BigDecimal fenToYuan(int fen) {
        return new BigDecimal(fen).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
    }
 
    //已还金额
    public int getRePayMoney(String billId) {
    	return payFlowMapper.selectRePayMoney(billId);
    }
    
    //剩余应还金额
	public int getNeedMoney(String billId) {
		return getBillRepayMoney(billId).multiply(new BigDecimal(100)).intValue()
				- payFlowMapper.selectRePayMoney(billId);
	}
   


}
