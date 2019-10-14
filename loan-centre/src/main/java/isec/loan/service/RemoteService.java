package isec.loan.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

import isec.loan.core.PromptException;
import isec.loan.entity.CallApi;
import isec.loan.mapper.CallApiMapper;

/**
 * @author Administrator
 */
@Service
public class RemoteService {

	private Logger logger = LoggerFactory.getLogger(RemoteService.class);

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	CallApiMapper callApiMapper;

	private static final String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };

	@Value(value = "${dingxiang.url}")
	public String DX_BASE_URL;
	
	@Value(value = "${dingxiang.appId}")
	public String DX_APP_ID;
	
	@Value(value = "${dingxiang.appSecret}")
	public String DX_APP_SECRET;
	
	public String DX_NAME_CARD_VALIDATE_URL="nameCardValidate";
	
	public String DX_PHONE_RISK_SCORE_URL="phone/riskscore";
	
	public String DX_RISK_ASSESS_URL="dxantifraud";
	
	public String DX_CARRIER_ANAY_REPORT_URL="carrierAnayReport";
	
	public String DX_CARRIER_ORIG_REPORT_URL= "carrierOrigReport";
	
	public String DX_TAOBAO_ANAY_REPORT_URL="taobaoAnayReport";
	
	public String DX_BANK_CARD_FOUR_VERIFY_URL="bankcardFourVerify";
	
	
	public JSONObject callDx(String url, JSONObject postData) {
		logger.info("callDx begin url=" + url + " postData=" + postData.toJSONString());
		//记录调用日志
		CallApi callApi=new CallApi();
		callApi.setUserId(postData.getString("userId"));
		callApi.setApiProvider("dingxiang");
		callApi.setApiKey(url.replace(DX_BASE_URL, ""));
		callApi.setRequest(postData.toJSONString());
		
		String timestamp = "" + System.currentTimeMillis() / 1000;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json;charset=utf-8");
		headers.add("customerId", DX_APP_ID);
		headers.add("timestamp", timestamp);
		headers.add("sign", sign(timestamp));

		ResponseEntity<JSONObject> retResult = restTemplate.postForEntity(url,
				new HttpEntity<String>(postData.toJSONString(), headers), JSONObject.class);
		if (retResult.getStatusCodeValue() != HttpStatus.SC_OK) {
			logger.error("顶象接口调用失败 statusCode=" + retResult.getStatusCodeValue());
			callApi.setStatus("error");
			callApiMapper.insert(callApi);
			throw new PromptException("系统出错,请稍后重试");
		}
		JSONObject retJson = retResult.getBody();
		String retCode = retJson.getString("code");
		if (!"200".equals(retCode)) {
			logger.error("顶象接口调用出错  retCode=" + retCode + " errorMessage=" + retJson.getString("msg"));
			callApi.setStatus("fail");
			callApi.setResponse(retJson.toJSONString());
			callApiMapper.insert(callApi);
			throw new PromptException("DX"+retCode,retJson.getString("msg"));
		}
		logger.info("callDx end retData=" + retJson.toJSONString());
		callApi.setStatus("success");
		callApi.setResponse(retJson.toJSONString());
		callApiMapper.insert(callApi);
		return retJson;
	}
	
	
	public String callDxForGet(String url) {
		logger.info("callDxForGet begin url=" + url);
		String timestamp = "" + System.currentTimeMillis() / 1000;
		HttpHeaders headers = new HttpHeaders();
		headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36"); 
		headers.add("Accept", "text/html");
		headers.add("customerId", DX_APP_ID);
		headers.add("timestamp", timestamp);
		headers.add("sign", sign(timestamp));
		ResponseEntity<String> retResult = restTemplate.exchange(url, HttpMethod.GET,
				new HttpEntity<String>(null, headers), String.class);
		if (retResult.getStatusCodeValue() != HttpStatus.SC_OK) {
			logger.error("顶象接口调用失败 statusCode=" + retResult.getStatusCodeValue());
			throw new PromptException("系统出错,请稍后重试");
		}
		logger.info("callDxForGet end retData=" + retResult.getBody());
		return retResult.getBody();
	}
	

//	public JSONObject getDataForDx(String url, JSONObject postData) {
//		try {
//			return callDx(url, postData).getJSONObject("data");
//		} catch (Exception e) {
//			logger.error("顶象接口调用出错 ", e);
//			return null;
//		}
//
//	}
	
	public  String sign(String timestamp) {
		return encode(DX_APP_SECRET + DX_APP_ID + timestamp + DX_APP_SECRET);
	}

	public static String encode(String strObj) {
		String resultString = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteToString(md.digest(strObj.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
		return resultString;
	}

	private static String byteToString(byte[] bByte) {
		StringBuilder sBuffer = new StringBuilder();
		for (byte aBByte : bByte) {
			sBuffer.append(byteToArrayString(aBByte));
		}
		return sBuffer.toString();
	}

	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}

}
