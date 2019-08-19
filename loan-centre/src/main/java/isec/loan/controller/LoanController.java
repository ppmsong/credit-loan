package isec.loan.controller;

import isec.base.util.S;
import isec.loan.common.In;
import isec.loan.core.PromptException;
import isec.loan.entity.ActionRecord;
import isec.loan.entity.Loan;
import isec.loan.entity.User;
import isec.loan.entity.UserInfo;
import isec.loan.entity.enums.IsDelete;
import isec.loan.entity.enums.LoanStatus;
import isec.loan.entity.enums.OdinPushType;
import isec.loan.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Condition;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;

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
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    ActionRecordService actionRecordService;
    @Autowired
    MessageService messageService;
    @Autowired
    OdinService odinService;


    /**
     * 创建借款订单
     *
     * @param user
     * @param productId
     */
    @RequestMapping("/createLoan")
    public void createLoan(@In User user, @NotBlank(message = "productId不能为空") String productId) {
    	Condition condition = new Condition(Loan.class);
        condition.createCriteria().andCondition("user_id = '" + user.getUserId() + "' and is_delete = "+IsDelete.NO.getKey()+" and " +
                "loan_status <  " + LoanStatus.CLOSED.getKey());
        List<Loan> loanList = loanService.getByCondition(condition);

        if (loanList.size() > 0) {
            throw new PromptException("你存在未关闭的借款");
        }
        //手机风险评分
        String status=riskService.saveMobileRiskScore(user.getUserId(), user.getMobile());
        if (!"success".equals(status)) {
        	 throw new PromptException("saveMobileRiskScore 失败");
        }
        UserInfo userInfo = userInfoService.findById(user.getUserId());
        //贷前综合风险报告
        status=riskService.saveRiskAssess(user.getUserId(), user.getMobile(),userInfo.getName(),userInfo.getIdcard());
        if (!"success".equals(status)) {
       	 	throw new PromptException("saveRiskAssess 失败");
        }
        loanService.getByCondition(condition);
        if (loanList.size() > 0) {
            throw new PromptException("已申请借款");
        }
        //创建借款订单
        loanService.createLoan(user.getUserId(), productId);
    }

    /**
     * 贷款申请拒绝，审核失败
     *
     * @param loanId 贷款申请编号
     */
    @RequestMapping("/refuse")
    public void refuse(@NotBlank(message = "贷款申请编号loanId不能为空") String loanId,String operatorId) {

        Loan loan = loanService.findById(loanId);
        if (null == loan) {
            throw new PromptException("贷款申请记录不存在");
        }

        loan.setLoanStatus(LoanStatus.FAILED.getKey());
        loan.setUpdateTime(S.getCurrentTimestamp());
        loanService.update(loan);

        //创建还款操作记录
        ActionRecord actionRecord = new ActionRecord(loan.getLoanId(), 1, loan.getUserId(), 1, "", loan.getLoanStatus());
        actionRecordService.save(actionRecord);

        //修改用户认证状态
        UserInfo userInfo = userInfoService.findById(loan.getUserId());
        if (null != userInfo) {
            userInfo.setZhimaVerify(0);
            userInfo.setOperatorVerify(0);
            userInfo.setBankVerify(0);
            userInfo.setContactVerify(0);
            userInfo.setUpdateTime(S.getCurrentTimestamp());
            userInfoService.update(userInfo);
        }

        String content = "尊敬的客户，您申请的借款 " + loanId + " 因提交资料问题未能通过审核，建议您核查相关资料和信息后重新尝试，本次交易已关闭，感谢您的使用。";
        //站内信
        messageService.sendMessage(loan.getUserId(), "借款失败，未能通过审核", content);
        //推送
        odinService.sendCommonPush(loan.getUserId(), content, OdinPushType.LOAN_FAIL.getStrkey(), new HashMap<>());
    }

}
