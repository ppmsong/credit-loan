package isec.loan.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import isec.loan.common.In;
import isec.loan.core.PromptException;
import isec.loan.entity.Loan;
import isec.loan.entity.User;
import isec.loan.entity.enums.LoanStatus;
import isec.loan.service.LoanService;
import isec.loan.service.RiskService;
import tk.mybatis.mapper.entity.Condition;

/**
 * 借款操作
 *
 * @author Administrator
 */
@RestController
@RequestMapping(value = "loan")
@Validated
public class LoanController {

    @Autowired
    LoanService loanService;

    @Autowired
    RiskService riskService;
    /**
     * 创建借款订单
     *
     * @param user
     * @param productId
     */
    @RequestMapping("/createLoan")
    public void createLoan(@In User user, @NotBlank(message = "productId不能为空") String productId) {
        Condition condition = new Condition(Loan.class);
        condition.createCriteria().andCondition("user_id = '" + user.getUserId() + "' and is_delete = 0 and " +
                "loan_status <  " + LoanStatus.CLOSED.getKey());
        List<Loan> loanList = loanService.getByCondition(condition);

        if (loanList.size() > 0) {
            throw new PromptException("你存在未关闭的借款");
        }
        riskService.saveMobileRiskScore(user.getUserId(), user.getMobile());
        
        loanService.createLoan(user.getUserId(), productId);
    }

}
