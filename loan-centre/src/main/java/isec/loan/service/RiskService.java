package isec.loan.service;

import com.alibaba.fastjson.JSONObject;
import isec.base.util.S;
import isec.loan.core.AbstractService;
import isec.loan.entity.Risk;
import isec.loan.entity.UserInfo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by p on 2019/07/17.
 */
@Service
@Transactional
public class RiskService extends AbstractService<Risk> {

    private Logger logger = LoggerFactory.getLogger(RiskService.class);

    public static final String PHONE_BOOK_API_KEY = "phoneBook";

    @Autowired
    RemoteService remoteService;

    @Autowired
    UserInfoService userInfoService;

    @Value(value = "${dingxiang.callBackServer}")
    private String CALL_BACK_SERVER;

    @Value(value = "${dingxiang.accreditUrl}")
    private String DINGXIAN_ACCREDIT_URL;

    public String saveMobileRiskScore(String userId, String mobile) {
        JSONObject postData = new JSONObject();
        postData.put("mobile", mobile);
        return saveRisk(userId, remoteService.DX_PHONE_RISK_SCORE_URL, postData);
    }

    public String getCarrierAccreditUrl(String mobile, String name, String idcard, String backUrl) {
        String timestamp = "" + System.currentTimeMillis() / 1000;
        StringBuffer url = new StringBuffer(DINGXIAN_ACCREDIT_URL + "carrier");
        url.append("?userId=" + S.getUuid());
        url.append("&timeStamp=" + timestamp);
        url.append("&sign=" + remoteService.sign(timestamp));
        url.append("&customerId=" + remoteService.DX_APP_ID);

        url.append("&backUrl=" + urlEncoder(backUrl));
        url.append("&dataUrl=" + urlEncoder(CALL_BACK_SERVER + "risk/callBackOfCarrierGatherFinish"));
        url.append("&quitOnLoginDone=1");
        url.append("&name=" + name);
        url.append("&mobile=" + mobile);
        url.append("&idcard=" + idcard);
        return url.toString();
    }


    public String getTaoBaoAccreditUrl(String userId, String backUrl) {
        String timestamp = "" + System.currentTimeMillis() / 1000;
        StringBuffer url = new StringBuffer(DINGXIAN_ACCREDIT_URL + "taobao");
        url.append("?userId=" + userId);
        url.append("&timeStamp=" + timestamp);
        url.append("&sign=" + remoteService.sign(timestamp));
        url.append("&customerId=" + remoteService.DX_APP_ID);

        url.append("&backUrl=" + urlEncoder(backUrl));
        url.append("&dataUrl=" + urlEncoder(CALL_BACK_SERVER + "risk/callBackOfTaoBaoGatherFinish"));
        url.append("&quitOnLoginDone=0");
        return url.toString();
    }


    private String urlEncoder(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String saveRiskAssess(String userId, String mobile, String name, String idcard) {
        JSONObject postData = new JSONObject();
        postData.put("mobile", mobile);
        postData.put("name", name);
        postData.put("idcard", idcard);
        return saveRisk(userId, remoteService.DX_RISK_ASSESS_URL, postData);
    }

    public void saveCarrierReport(String userId, String mobile, String taskId) {
        String status = null;
        status = saveCarrierOrigReport(userId, mobile, taskId);
        if (!"success".equals(status)) {
            return;
        }
        status = saveCarrierAnayReport(userId, mobile, taskId);
        if (!"success".equals(status)) {
            return;
        }
        UserInfo userInfo = userInfoService.findById(userId);
        userInfo.setOperatorVerify(1);
        userInfoService.update(userInfo);
    }


    public String saveCarrierOrigReport(String userId, String mobile, String taskId) {
        JSONObject postData = new JSONObject();
        postData.put("mobile", mobile);
        postData.put("taskId", taskId);
        return saveRisk(userId, remoteService.DX_CARRIER_ORIG_REPORT_URL, postData);
    }

    public String saveCarrierAnayReport(String userId, String mobile, String taskId) {
        JSONObject postData = new JSONObject();
        postData.put("mobile", mobile);
        postData.put("taskId", taskId);
        return saveRisk(userId, remoteService.DX_CARRIER_ANAY_REPORT_URL, postData);
    }

    public String saveTaoBaoAnayReport(String userId, String taskId) {
        JSONObject postData = new JSONObject();
        postData.put("taskId", taskId);
        return saveRisk(userId, remoteService.DX_TAOBAO_ANAY_REPORT_URL, postData);
    }


    public String saveRisk(String userId, String apiKey, JSONObject postData) {

        Risk risk = new Risk();
        risk.setUserId(userId);
        risk.setApiKey(apiKey);
        risk.setRequest(postData.toJSONString());
        risk.setMobile(postData.getString("mobile"));
        risk.setName(postData.getString("name"));
        risk.setIdcard(postData.getString("idcard"));
        try {
            JSONObject retJson = remoteService.callDx(remoteService.DX_BASE_URL + apiKey, postData);
            risk.setResponse(retJson.toJSONString());
            risk.setStatus("success");

            //更新芝麻分
            if(remoteService.DX_TAOBAO_ANAY_REPORT_URL.equals(apiKey)) {
            	updateZhimaScore(userId, retJson);
            }
        } catch (Exception e) {
            logger.error("saveRisk error ", e);
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            risk.setResponse(error.toJSONString());
            risk.setStatus("fail");
        }
        saveOrUpdateRisk(risk);
        return risk.getStatus();
    }

    /**
     * //更新芝麻分和芝麻认证状态
     *
     * @param userId
     * @param retJson
     */
	private void updateZhimaScore(String userId, JSONObject retJson) {

		UserInfo userInfo = userInfoService.findById(userId);
		if (null != userInfo) {
			int taobaoZmscore = retJson.getJSONObject("data").getJSONObject("wealthInfo").getJSONObject("totalssets")
					.getIntValue("taobaoZmscore");
			userInfo.setZhimaScore(taobaoZmscore);
			userInfo.setZhimaVerify(1);
			userInfo.setUpdateTime(S.getCurrentTimestamp());
			userInfoService.update(userInfo);
			logger.info("update zhimaScore and zhimaVerify success,userId:{}", userId);
		}
	}

    public void saveOrUpdateRisk(Risk risk) {
        String where = "loan_id ='' and user_id='" + risk.getUserId() + "'" + " and api_key='"
                + risk.getApiKey() + "'";
        List<Risk> riskList = this.findByWhere(where);
        if (CollectionUtils.isEmpty(riskList)) {
            logger.info(" create new risk");
            risk.setCreateTime(S.getCurrentTimestamp());
            this.save(risk);
        } else {
            logger.info(" update old risk");
            risk.setUpdateTime(S.getCurrentTimestamp());
            this.updateByWhere(risk, where);
        }
    }

    public void saveOrUpdateRiskOnUserId(Risk risk) {
        String where = "user_id='" + risk.getUserId() + "'" + " and api_key='" + risk.getApiKey() + "'";
        List<Risk> riskList = this.findByWhere(where);
        if (CollectionUtils.isEmpty(riskList)) {
            logger.info(" create new risk");
            risk.setCreateTime(S.getCurrentTimestamp());
            risk.setStatus("success");
            this.save(risk);
        } else {
            logger.info(" update old risk");
            risk.setUpdateTime(S.getCurrentTimestamp());
            this.updateByWhere(risk, where);
        }
    }

}
