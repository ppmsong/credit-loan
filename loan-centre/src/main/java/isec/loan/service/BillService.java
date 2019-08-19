package isec.loan.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import isec.base.util.Dttm;
import isec.base.util.S;
import isec.loan.configurer.Config;
import isec.loan.core.AbstractService;
import isec.loan.core.PromptException;
import isec.loan.entity.*;
import isec.loan.entity.enums.*;
import isec.loan.mapper.BillMapper;
import isec.loan.mapper.LoanMapper;
import isec.loan.mapper.LoanUserInfoMapper;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 账单服务
 *
 * @author Administrator
 */
@Service
@Transactional
public class BillService extends AbstractService<Bill> {

    @Autowired
    MoneyCalculateService moneyCalculateService;

    @Autowired
    PayService payService;

    @Autowired
    BillService billService;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Resource
    LoanMapper loanMapper;

    @Resource
    BillMapper billMapper;

    @Resource
    LoanUserInfoMapper loanUserInfoMapper;

    @Autowired
    Config config;

    @Autowired
    ActionRecordService actionRecordService;

    @Autowired
    AdminUserService adminUserService;

    @Autowired
    OdinService odinService;

    @Autowired
    UserInfoService userInfoService;

    /**
     * 生成账单
     *
     * @param loanId
     * @return
     */
    public boolean createBill(String loanId, String operatorId) {

        if (null == adminUserService.findById(operatorId)) {
            throw new PromptException("非法的操作员编号");
        }

        Loan loan = loanMapper.selectByPrimaryKey(loanId);

        if (null == loan) {
            throw new PromptException("贷款编号非法");
        }

        if (LoanStatus.CHECKING.getKey() != loan.getLoanStatus()) {
            throw new PromptException("该贷款状态异常，无法放款");
        }

        //校验用户信息
        LoanUserInfo loanUserInfo = loanUserInfoMapper.selectByPrimaryKey(loanId);
        if (null == loanUserInfo) {
            throw new PromptException("贷款用户信息异常，无法放款");
        }

        if (S.isBlank(loanUserInfo.getAlipayAccount())) {
            throw new PromptException("贷款用户尚未绑定支付宝账号，无法放款");
        }

        if (null != billService.findBy("loan_id", "'" + loanId + "'")) {
            throw new PromptException("该笔贷款账单已生成，请不要重复操作");
        }

        // 判断是否存在账单

        //放款金额
        int realMoney = moneyCalculateService.getRealMoney(loanId);
        String billId = "B" + S.createReqNo();
        //创建出款流水记录

        PayFlow payFlow = new PayFlow(loanId, TradeType.TYPE_LOAN.getKey(), loan.getUserId(), userService.findById(loan.getUserId()).getMobile(), "借款", realMoney, 1, "");
        payService.save(payFlow);

        //出款接口出款
        JSONObject result = payService.bill(S.createReqNo(), loanUserInfo.getAlipayAccount(), String.valueOf(realMoney));
        payFlow.setRequestParam(result.getString("req"));
        payFlow.setCallback(result.getString("callback"));
        payFlow.setUpdateTime(S.getCurrentTimestamp());

        //放款成功
        if (result.getString("code").equals("ok")) {

            payFlow.setOutTradeNo(result.getString("bill_no"));
            payFlow.setStatus(TradeStatus.TRADE_SUCCESS.getKey());
            payService.update(payFlow);

            //创建账单记录
            Bill bill = new Bill(loan.getUserId(), loan.getLoanId(), moneyCalculateService.getProductRepayMoney(loan.getRete(), loan.getBorrowMoney(), loan.getDays()).multiply(new BigDecimal(100)).intValue(), loan.getBorrowMoney(), moneyCalculateService.getRateMoney(loan.getRete(), loan.getBorrowMoney(), loan.getDays()).multiply(new BigDecimal(100)).intValue(),
                    S.getCurrentTimestamp() + loan.getDays() * 24 * 3600);
            bill.setBillId(billId);
            if (save(bill) == 0) {
                throw new PromptException("出款成功，创建账单异常");
            }

            //  修改借款状态
            loan.setLoanStatus(LoanStatus.REPAYING.getKey());
            loan.setUpdateTime(S.getCurrentTimestamp());
            loanMapper.updateByPrimaryKeySelective(loan);

            //创建还款操作记录
            ActionRecord actionRecord = new ActionRecord(bill.getBillId(), 1, bill.getUserId(), 1, "", bill.getStatus());
            actionRecordService.save(actionRecord);

            //  发送通知内容
            String content = "尊敬的客户，您申请的借款" + loanId + "已通过平台审核，借款金额 " + moneyCalculateService.transferMoneyToYuan(loan.getBorrowMoney()) + " " + "元已成功转至您的支付宝 " + userInfoService.findById(loan.getUserId()).getAlipayNickName() + " 中，请注意查收。您的借款周期为 " + loan.getDays() + " 天，请在" + Dttm.timeStamp2Date(String.valueOf(bill.getDeadline()), "") + " " + "前通过App还款，如过期会加收罚金。感谢您的使用。";
            //站内信
            messageService.sendMessage(loan.getUserId(), "借款成功，借款已转至支付宝",
                    content);
            //推送
            odinService.sendCommonPush(loan.getUserId(), content, OdinPushType.LOAN_SUCCESS.getStrkey(), new HashMap<>());

            return true;
        }

        payFlow.setOutTradeNo(result.getString("bill_no"));
        payFlow.setStatus(TradeStatus.TRADE_FAILED.getKey());
        payService.update(payFlow);

        return false;

    }

    public List<Map<String, String>> getMyBill(String userId, Integer page, Integer pageSize, int status) {

        PageHelper.startPage(page, pageSize);
        PageHelper.orderBy("create_time desc");

        Condition condition = new Condition(Loan.class);
        condition.createCriteria().andCondition("user_id = '" + userId + "'");
        if (status == 1) {
            condition.createCriteria().andCondition("status in (1, 2, 3)");
        } else {
            condition.createCriteria().andCondition("status in (5, 6) ");
        }

        List<Map<String, String>> data = new ArrayList<>();
        List<Loan> LoanList = loanMapper.selectByCondition(condition);


        for (Loan loan : LoanList) {
            Condition billCondition = new Condition(Bill.class);
            billCondition.createCriteria().andCondition("user_id = '" + userId + "' and loan_id = '" + loan.getLoanId() + "'");

            Bill bill = null;
            // 借款信息
            List<Bill> billList = billMapper.selectByCondition(billCondition);

            if (null != billList && billList.size() > 0) {
                bill = billList.get(0);
            }


            Map<String, String> billData = new HashMap<>();

            // 3 还款中  4 展期  5已关闭
            if (null != bill && ArrayUtils.contains(new int[]{3, 4, 5}, loan.getLoanStatus())) {
                billData.put("billId", bill.getBillId());
                billData.put("loanId", bill.getLoanId());
                billData.put("r_basic", String.valueOf(moneyCalculateService.transferMoneyToYuan(bill.getrBasic())));
                billData.put("r_interest", String.valueOf(moneyCalculateService.transferMoneyToYuan(bill.getrInterest())));
                billData.put("riskCost", String.valueOf(moneyCalculateService.transferMoneyToYuan(loan.getRiskCost())));
                billData.put("createTime", String.valueOf(bill.getCreateTime()));
                billData.put("deadline", String.valueOf(bill.getDeadline()));
                //应还金额
                billData.put("repayment_amount", String.valueOf(moneyCalculateService.getBillRepayMoney(bill.getBillId())));

                // 判断是否逾期
                if (bill.getDeadline() < S.getCurrentTimestamp() &&(bill.getStatus() == BillStatus.UNREPAY.getKey()||bill.getStatus() == BillStatus.OVERDUED.getKey()) ) {
                    //逾期天数（精确到秒）
                    int overdueSeconds = moneyCalculateService.overDueSecond(bill.getDeadline());
                    billData.put("overDueDays", String.valueOf(moneyCalculateService.overdueDays(overdueSeconds)));
                    BigDecimal overDue = moneyCalculateService.getTotalOverdueMoney(loan.getOverdueRate(), bill.getrBasic(), overdueSeconds);
                    billData.put("allOverdue", String.valueOf(overDue));

                    billData.put("status", "7");
                } else {
                    billData.put("status", String.valueOf(loan.getLoanStatus()));
                }

                if (LoanStatus.CLOSED.getKey() == loan.getLoanStatus()) {
                    billData.put("createTime", String.valueOf(bill.getUpdateTime()));
                }
            } else {
                billData.put("loanId", loan.getLoanId());
                billData.put("r_basic", String.valueOf(moneyCalculateService.transferMoneyToYuan(loan.getBorrowMoney())));
                billData.put("riskCost", String.valueOf(moneyCalculateService.transferMoneyToYuan(loan.getRiskCost())));
                billData.put("createTime", String.valueOf(loan.getCreateTime()));
                billData.put("status", String.valueOf(loan.getLoanStatus()));
                // 1- 申请中  2 待放款   5-申请失败
            }


            data.add(billData);
        }

        return data;
    }
}
