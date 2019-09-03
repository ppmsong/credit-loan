package isec.loan.service;

import isec.base.util.S;
import isec.loan.core.AbstractService;
import isec.loan.entity.*;
import isec.loan.entity.enums.IsDelete;
import isec.loan.entity.enums.LoanStatus;
import isec.loan.entity.enums.OdinPushType;
import isec.loan.entity.enums.TgType;
import isec.loan.mapper.LoanMapper;
import isec.loan.mapper.LoanUserInfoMapper;
import isec.loan.mapper.RiskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;


@Service
@Transactional
public class LoanService extends AbstractService<Loan> {

    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);


    @Autowired
    ProductService productService;
    @Autowired
    UserInfoService userInfoService;
    @Resource
    LoanUserInfoMapper loanUserInfoMapper;
    @Autowired
    MessageService messageService;
    @Autowired
    MoneyCalculateService moneyCalculateService;
    @Autowired
    ActionRecordService actionRecordService;
    @Autowired
    OdinService odinService;
    @Resource
    RiskMapper riskMapper;
    @Autowired
    TelegramService telegramService;
    @Resource
    LoanMapper loanMapper;
    @Autowired
    SettingsService settingsService;


    public int createLoan(String userId, String productId) {
        Product product = productService.findById(productId);
        Loan loan = new Loan(userId, productId, product.getBorrowMoney(), product.getRete(), product.getRiskCost(),
                product.getDays(), product.getOverdueRate());
        this.save(loan);

        UserInfo userInfo = userInfoService.findById(userId);
        LoanUserInfo loanUserInfo = new LoanUserInfo();
        loanUserInfo.setLoanId(loan.getLoanId());
        loanUserInfo.setUserId(userId);
        loanUserInfo.setName(userInfo.getName());
        loanUserInfo.setIdcard(userInfo.getIdcard());
        loanUserInfo.setAlipayAccount(userInfo.getAlipayAccount());
        loanUserInfo.setZhimaScore(userInfo.getZhimaScore());
        loanUserInfo.setBankName(userInfo.getBankName());
        loanUserInfo.setBankCardno(userInfo.getBankCardno());
        loanUserInfo.setBankMobile(userInfo.getBankMobile());
        loanUserInfo.setContacter(userInfo.getContacter());
        loanUserInfo.setCreateTime(S.getCurrentTimestamp());
        loanUserInfo.setIsDelete(IsDelete.NO.getKey());
        int affectRows = loanUserInfoMapper.insert(loanUserInfo);

        String content = "尊敬的客户，您已于 " + S.getCurDate() + " 申请借款 " + moneyCalculateService.transferMoneyToYuan(product.getBorrowMoney()) + " " +
                "元，请耐心等待审核，如审核成功，则会通过支付宝进行放款，请注意查收，感谢您的使用。";

        // 发送消息
        messageService.sendMessage(userId, "已成功申请借款", content);

        //奥丁推送
        odinService.sendCommonPush(userId, content, OdinPushType.APPLY_LOAN.getStrkey(), new HashMap<>());

        // 修改数据借款
        riskMapper.updateRiskLoan(userId, loan.getLoanId());

        //记录借款操作记录
        ActionRecord actionRecord = new ActionRecord(loan.getLoanId(), 1, userId, 1, "", loan.getLoanStatus());
        actionRecordService.save(actionRecord);

        //发送申请预警
        telegramService.sendTgMsg3(TgType.APPLY_LOAN.getKey(), userId, loan.getLoanId());

        //未处理申请打到10的倍数时候发送预警
        if (findByWhere(" loan_status = 1 and is_delete = " + IsDelete.NO.getKey()).size() % 10 == 0) {
            telegramService.sendTgMsg3(TgType.APPLY_LOAN_2.getKey(), userId, loan.getLoanId());

        }

        return affectRows;

    }


    public void resetUserVerifyByClose() {
        List<Loan> list = loanMapper.selectCloseOverdueLoan();
        for (Loan loan : list) {
            userInfoService.resetUserInfo(loan.getUserId());
        }
    }

    public boolean closeLoan(String loanId) {
        Loan loan = findById(loanId);
        if (null != loan && LoanStatus.LOANING.getKey() == loan.getLoanStatus()) {
            loan.setUpdateTime(S.getCurrentTimestamp());
            loan.setLoanStatus(LoanStatus.CLOSED.getKey());
            update(loan);

            logger.info("订单{}过期未领取被关闭o(*￣︶￣*)o", loanId);
            String content = "尊敬的客户，您未能在规定时间内确定借款，此次借款交易系统将自动关闭。如需借款请重新申请，感谢您的使用。";
            //站内信
            messageService.sendMessage(loan.getUserId(), "抱歉，您的借款未及时确认，订单已关闭", content);
            //推送
            odinService.sendCommonPush(loan.getUserId(), content, OdinPushType.UN_RECEIVE_CLOSED.getStrkey(), new HashMap<>());
            return true;
        }
        return false;
    }


}
