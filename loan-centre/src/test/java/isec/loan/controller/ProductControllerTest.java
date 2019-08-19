package isec.loan.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSON;

import isec.base.util.S;
import isec.loan.entity.Contacter;
import isec.loan.entity.UserInfo;
import isec.loan.entity.enums.SmsCodeType;
import isec.loan.service.SmsCodeService;
import isec.loan.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest extends BasicTest {

	@Autowired
	SmsCodeService smsCodeService;
	@Autowired
	UserService userService;

	@Before
	public void setupMockMvc() throws Exception {
		super.loginBySmsCode("18621244622");
	}

	@Test
	public void queryProductList() throws Exception {

		MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();

		mockMvc.perform(MockMvcRequestBuilders.post("/product/queryProductList").params(paramValues)
				.accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 断言
				.andExpect(jsonPath("$.code").value("ok")).andExpect(jsonPath("$.data").isNotEmpty())
				.andDo(MockMvcResultHandlers.print()).andReturn();

	}

	
	@Test
	public void queryProductDetails() throws Exception {

		MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
		paramValues.add("productId", "2");
		
		mockMvc.perform(MockMvcRequestBuilders.post("/product/queryProductDetails").params(paramValues)
				.accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 断言
				.andExpect(jsonPath("$.code").value("ok")).andExpect(jsonPath("$.data").isNotEmpty())
				.andDo(MockMvcResultHandlers.print()).andReturn();

	}
	
	
}