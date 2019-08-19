package isec.loan.service;

import isec.base.bean.JumpTo;
import isec.base.util.S;
import isec.loan.core.AbstractService;
import isec.loan.entity.*;
import isec.loan.entity.enums.IsDelete;
import isec.loan.entity.enums.OdinPushType;
import isec.loan.mapper.LoanUserInfoMapper;
import isec.loan.mapper.RiskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;


@Service
@Transactional
public class LoanService extends AbstractService<Loan> {

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

    public void createLoan(String userId, String productId) {
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
        loanUserInfoMapper.insert(loanUserInfo);

        String content = "尊敬的客户，您已于 " + S.getCurDate() + " 申请借款 " + moneyCalculateService.transferMoneyToYuan(product.getBorrowMoney()) + " " +
                "元，请耐心等待审核，如审核成功，则会通过支付宝进行放款，请注意查收，感谢您的使用。";

        // 发送消息
        messageService.sendMessage(userId, "已成功申请借款", content);

        //奥丁推送
//        odinService.sendCommonPush(userId, content, OdinPushType.APPLY_LOAN.getStrkey(), new HashMap<>());

        // 修改数据借款
        riskMapper.updateRiskLoan(userId, loan.getLoanId());

        //记录借款操作记录
        ActionRecord actionRecord = new ActionRecord(loan.getLoanId(), 1, userId, 1, "", loan.getLoanStatus());
        actionRecordService.save(actionRecord);


    }

}
