package isec.loan.task;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import isec.base.util.S;
import isec.loan.entity.Bill;
import isec.loan.entity.Loan;
import isec.loan.entity.enums.BillStatus;
import isec.loan.entity.enums.OdinPushType;
import isec.loan.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 处理逾期账单
 *
 * @author p
 * @date 2019-08-16
 */
@Component
public class DealOverdueBillTask {

    private static final Logger logger = LoggerFactory.getLogger(DealOverdueBillTask.class);

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


    /**
     * cron = "0 0 12 * * ?"
     * 每天中午12点执行 fixedDelay = 1000 * 60 * 30
     */
    @Scheduled(fixedDelay = 6 * 60 * 60 * 1000)
    public void deal() {

        String content;
        PageHelper.startPage(1, 100);
        PageHelper.orderBy("create_time asc");

        //已逾期账单
        List<Bill> overduedBills = billService.findByWhere("deadline < " + S.getCurrentTimestamp() + " and status in (0,4,5) " + " and is_delete =1");
        if (null != overduedBills && overduedBills.size() > 0) {


            logger.info("已逾期账单：{}", JSON.toJSONString(overduedBills));
            for (Bill bill : overduedBills) {

                Loan loan = loanService.findById(bill.getLoanId());

                content = "敬的客户，您在我平台借款 " + moneyCalculateService.fenToYuan(bill.getrBasic()) + " 元，已在 " + S.timestampToDate(bill.getDeadline()) + " 未能还款，借款已逾期。按照借款协议截至目前位置该笔借款已造成 " + moneyCalculateService.getTotalOverdueMoney(loan.getOverdueRate(), bill.getrBasic(), moneyCalculateService.overDueSecond(bill.getDeadline())) + " 元罚金，合计还款金额已增至 " + moneyCalculateService.getBillRepayMoney(bill.getBillId()) + " 。为维护您良好的借款信用记录并避免造成更多损失，请务必尽早还款。感谢您的使用。";
                //站内信
                messageService.sendMessage(bill.getUserId(), "借款已逾期", content);
                //推送
                odinService.sendCommonPush(bill.getUserId(), content, OdinPushType.OVERDUED.getStrkey(), new HashMap<>());
            }
        }
        //即将逾期账单
        List<Bill> overduingBills = billService.findByWhere("deadline > " + S.getCurrentTimestamp() + " and deadline < " + (S.getCurrentTimestamp() + 3 * 24 * 60 * 60) + "  and status = " + BillStatus.UNREPAY.getKey() + " and is_delete =1");

        if (null != overduingBills && overduingBills.size() > 0) {
            logger.info("即将逾期账单：{}", JSON.toJSONString(overduedBills));
            for (Bill bill : overduingBills) {
                content = "尊敬的客户，您在我平台借款 " + moneyCalculateService.fenToYuan(bill.getrBasic()) + " 元，将在 " + S.timestampToDate(bill.getDeadline()) + " 天后逾期，如逾期会加收罚金，为维护您良好的借款信用记录，请您务必确在 " + S.timestampToDate(bill.getDeadline()) + " 前通过App完成还款。感谢您的使用。";
                //站内信
                messageService.sendMessage(bill.getUserId(), "借款即将逾期", content);
                //推送
                odinService.sendCommonPush(bill.getUserId(), content, OdinPushType.OVERDUING.getStrkey(), new HashMap<>());
            }
        }


    }

    /**
     * 10分钟更新一次逾期金额
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void overdueBill() {
        PageHelper.startPage(1, 100);
        PageHelper.orderBy("create_time asc");
        //已逾期账单
        Condition condition = new Condition(Loan.class);
        condition.createCriteria().andCondition("deadline < " + S.getCurrentTimestamp() + " and status in (0,4,5) and is_delete =1");
        List<Bill> overduedBills = billService.getByCondition(condition);
        if (null != overduedBills && overduedBills.size() > 0) {


            logger.info("已逾期账单：{}", JSON.toJSONString(overduedBills));
            for (Bill bill : overduedBills) {

                Loan loan = loanService.findById(bill.getLoanId());

                //更新逾期金额
                bill.setOverdueFine(moneyCalculateService.getTotalOverdueMoney(loan.getOverdueRate(), loan.getBorrowMoney(), moneyCalculateService.overDueSecond(bill.getDeadline())).multiply(new BigDecimal(100)).intValue());
                bill.setUpdateTime(S.getCurrentTimestamp());
                billService.update(bill);
            }
        }
    }


}
