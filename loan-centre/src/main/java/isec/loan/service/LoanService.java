package isec.loan.service;

import isec.base.util.S;
import isec.loan.core.AbstractService;
import isec.loan.entity.Loan;
import isec.loan.entity.LoanUserInfo;
import isec.loan.entity.Product;
import isec.loan.entity.UserInfo;
import isec.loan.mapper.LoanUserInfoMapper;
import isec.loan.mapper.RiskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


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
        loanUserInfoMapper.insert(loanUserInfo);

        // 发送消息
        messageService.sendMessage(userId, "已成功申请借款",
                "尊敬的客户，您已与 [" + S.getCurDate() + "] 申请借款 [" + moneyCalculateService.transferMoneyToYuan(product.getBorrowMoney()) + "] " +
                        "元，请耐心等待审核，如审核成功会通过支付宝进行放款，请注意查收，感谢您的使用。");

        // 修改数据借款
        riskMapper.updateRiskLoan(userId, loan.getLoanId());

    }

}
