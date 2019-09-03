package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import isec.base.util.S;
import isec.loan.common.In;
import isec.loan.common.MapBox;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Condition;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    AdminUserService adminUserService;
    @Autowired
    SettingsService settingsService;
    @Autowired
    MoneyCalculateService moneyCalculateService;


    /**
     * 创建借款订单
     *
     * @param user
     * @param productId
     */
    @RequestMapping("/createLoan")
    public Map<String, Object> createLoan(@In User user, @NotBlank(message = "productId不能为空") String productId) {
        Condition condition = new Condition(Loan.class);
        condition.createCriteria().andCondition("user_id = '" + user.getUserId() + "' and is_delete = " + IsDelete.NO.getKey() + " and " +
                "loan_status <  " + LoanStatus.FINISHED.getKey());
        List<Loan> loanList = loanService.getByCondition(condition);

        if (loanList.size() > 0) {
            throw new PromptException("你存在未关闭的借款");
        }
        //手机风险评分
        String status = riskService.saveMobileRiskScore(user.getUserId(), user.getMobile());
        if (!"success".equals(status)) {
            throw new PromptException("saveMobileRiskScore 失败");
        }
        UserInfo userInfo = userInfoService.findById(user.getUserId());
        //贷前综合风险报告
        status = riskService.saveRiskAssess(user.getUserId(), user.getMobile(), userInfo.getName(), userInfo.getIdcard());
        if (!"success".equals(status)) {
            throw new PromptException("saveRiskAssess 失败");
        }
        loanService.getByCondition(condition);
        if (loanList.size() > 0) {
            throw new PromptException("已申请借款");
        }
        //创建借款订单
        int affectRows = loanService.createLoan(user.getUserId(), productId);

        return MapBox.instance().put("affectRows", affectRows).toMap();

    }

    /**
     * 贷款申请拒绝，审核失败
     *
     * @param loanId     贷款申请记录编号
     * @param operatorId 操作员ID
     */
    @RequestMapping("/refuse")
    public void refuse(@NotBlank(message = "贷款申请编号loanId不能为空") String loanId, String operatorId) {

        Loan loan = loanService.findById(loanId);
        if (null == loan) {
            throw new PromptException("贷款申请记录不存在");
        }

        if (null == adminUserService.findById(operatorId)) {
            throw new PromptException("非法的操作员编号");
        }

        loan.setLoanStatus(LoanStatus.FAILED.getKey());
        loan.setUpdateTime(S.getCurrentTimestamp());
        loanService.update(loan);

        //创建还款操作记录
        JSONObject info = new JSONObject();
        info.put("operator_id", operatorId);
        ActionRecord actionRecord = new ActionRecord(loan.getLoanId(), 1, loan.getUserId(), 1, info.toJSONString(), loan.getLoanStatus());
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

    /**
     * 贷款审核通过
     *
     * @param loanId     贷款编号
     * @param operatorId 操作员ID
     */
    @RequestMapping("/pass")
    public void pass(@NotBlank(message = "贷款申请编号loanId不能为空") String loanId, String operatorId) {


        Loan loan = loanService.findById(loanId);
        if (null == loan) {
            throw new PromptException("贷款申请记录不存在");
        }

        if (null == adminUserService.findById(operatorId)) {
            throw new PromptException("非法的操作员编号");
        }

        //待放款（用户自己领取）
        loan.setLoanStatus(LoanStatus.LOANING.getKey());
        loan.setReceiveLoanTime(S.getCurrentTimestamp() + Integer.parseInt(settingsService.findOneByWhere("set_key='accept_loan_interval'").getSetVal()));
        loan.setUpdateTime(S.getCurrentTimestamp());
        loan.setResetUserVerifyTime(loan.getReceiveLoanTime() + Integer.parseInt(settingsService.findOneByWhere("set_key='reset_user_verify_time'").getSetVal()));
        loanService.update(loan);

        //创建还款操作记录
        JSONObject info = new JSONObject();
        info.put("operator_id", operatorId);
        ActionRecord actionRecord = new ActionRecord(loan.getLoanId(), 1, loan.getUserId(), 1, info.toJSONString(), loan.getLoanStatus());
        actionRecordService.save(actionRecord);

        String content = "尊敬的客户，您申请的借款 " + loanId + " 已通过平台审核，借款金额 " + moneyCalculateService.fenToYuan(loan.getBorrowMoney()) + " 元已就绪，请前往订单列表中确认借款。如" + new BigDecimal(settingsService.findBy("set_key", "'accept_loan_interval'").getSetVal()).divide(new BigDecimal(60)).setScale(0, BigDecimal.ROUND_DOWN) + "分钟未确认系统将自动关闭账单。";
        //站内信
        messageService.sendMessage(loan.getUserId(), "申请成功，您的资料已通过审核", content);
        //推送
        odinService.sendCommonPush(loan.getUserId(), content, OdinPushType.LOAN_PASS.getStrkey(), new HashMap<>());

    }


    /**
     * 领取过期关闭loan
     *
     * @param user
     * @param loanId
     */
    @PostMapping("/closeLoan")
    public void closeLoan(@In User user, String loanId) {
        loanService.closeLoan(loanId);
    }
}
