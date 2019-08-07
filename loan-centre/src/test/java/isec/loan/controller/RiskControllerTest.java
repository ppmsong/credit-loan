package isec.loan.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import isec.loan.entity.Risk;
import isec.loan.service.RiskService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RiskControllerTest extends BasicTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	RiskService riskService;

	@Before
	public void setupMockMvc() throws Exception {
		super.loginBySmsCode("15172538022");
	}

	String userId = "7d2088dd3c104ec7bbc26beccc409e62";
	String name = "xxx";
	String mobile = "13882387507";
	String idcard = "420306198409152816";
	String taskId="a7fd7080-b43d-11e9-8d6a-00163e089f37";

	@Test
	public void saveMobileRiskScore() {
		riskService.saveMobileRiskScore(userId, mobile);
	}

	@Test
	public void saveRiskAssess() {
		riskService.saveRiskAssess(userId, mobile, name, idcard);
	}

	
	@Test
	public void saveCarrierReport() {
		//riskService.saveCarrierOrigReport(userId, mobile,taskId);
		riskService.saveCarrierAnayReport(userId, mobile, taskId);
	}
	
	@Test
	public void queryRisk() {
		List<Risk> riskList=riskService.findByWhere(" mobile='"+"15172538022"+"'");
		for (Risk risk : riskList) {
			System.out.println("carrierReport= "+risk.getApiKey()+" "+risk.getResponse());
		}
		 
	}
	
	
	@Test
	public void getAccreditUrl() {
		System.out.println("accreditUrl=" + riskService.getCarrierAccreditUrl(mobile, name, idcard,""));
	}

	@Test
	public void getCarrierAccreditUrl() throws Exception {

		// POST 参数
		MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
		paramValues.add("token", token);
		paramValues.add("backUrl", "/ssss/sss");
		 
		mockMvc.perform(MockMvcRequestBuilders.post("/risk/getCarrierAccreditUrl").params(paramValues)
				.accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 断言
				.andExpect(jsonPath("$.code").value("ok")).andExpect(jsonPath("$.data").isNotEmpty())
				.andDo(MockMvcResultHandlers.print()).andReturn();
	}
	
	
	@Test
	public void getTaoBaoAccreditUrl() throws Exception {
		
		// POST 参数
		MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
		paramValues.add("token", token);
		paramValues.add("backUrl", "/ssss/sss");
		 
		mockMvc.perform(MockMvcRequestBuilders.post("/risk/getTaoBaoAccreditUrl").params(paramValues)
				.accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 断言
				.andExpect(jsonPath("$.code").value("ok")).andExpect(jsonPath("$.data").isNotEmpty())
				.andDo(MockMvcResultHandlers.print()).andReturn();
	}

	@Test
	public void callBackOfTaoBaoGatherFinish() throws Exception {
		// POST 参数
		MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
		paramValues.add("userId", "bafea7b22e23483a943323df8b732f50");
		paramValues.add("taskId", "efa1277a-b8dd-11e9-93f7-00163e1385a8");
		paramValues.add("result", "true");

		mockMvc.perform(MockMvcRequestBuilders.get("/risk/callBackOfTaoBaoGatherFinish").params(paramValues)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 断言
				.andExpect(jsonPath("$.code").value("ok")).andExpect(jsonPath("$.data").isNotEmpty())
				.andDo(MockMvcResultHandlers.print()).andReturn();
	}
}