package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import isec.base.util.S;
import isec.loan.common.In;
import isec.loan.entity.ActionRecord;
import isec.loan.entity.Bill;
import isec.loan.entity.Loan;
import isec.loan.entity.User;
import isec.loan.service.ActionRecordService;
import isec.loan.service.BillService;
import isec.loan.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author p
 * @date 2019/07/23
 */
@RestController
@RequestMapping(value = "actionRecord")
@Validated
public class ActionRecordController {

    @Autowired
    BillService billService;

    @Autowired
    LoanService loanService;

    @Autowired
    ActionRecordService actionRecordService;


    private Logger logger = LoggerFactory.getLogger(ActionRecordController.class);

    /**
     * @return
     */
    @PostMapping(value = "alertLoanSucc")
    public JSONObject alertLoanSucc(@In User user) {
        String where = "user_id='" + user.getUserId() + "'" + " and trade_type = 1 and action_type = 1 and trade_status = 0 and update_time = 0 ";
        List<ActionRecord> records = actionRecordService.findByWhere(where);
        if (null != records && records.size() >0) {
            ActionRecord actionRecord = records.get(0);
            actionRecord.setUpdateTime(S.getCurrentTimestamp());
            actionRecordService.update(actionRecord);

            Bill bill = billService.findById(actionRecord.getTradeId());
            if (null != bill) {
                Loan loan = loanService.findById(bill.getLoanId());
                if (null != loan) {
                    JSONObject result = new JSONObject();
                    result.put("borrowMoney", new BigDecimal(loan.getBorrowMoney()).divide(new BigDecimal(100)));
                    result.put("realMoney", new BigDecimal(loan.getBorrowMoney() - loan.getRiskCost()).divide(new BigDecimal(100), 2, RoundingMode.DOWN));
                    result.put("deadline", bill.getDeadline());
                    return result;
                }

            }


        }
        return null;
    }


}
