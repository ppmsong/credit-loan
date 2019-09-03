package isec.loan.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

import isec.loan.service.LoanService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoanControllerTest extends BasicTest {

	@Autowired
	LoanService loanService;
	
	@Before
	public void setupMockMvc() throws Exception {
		super.loginBySmsCode("18888888888");
	}

	@Test
	public void createLoan() throws Exception {

		MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
		paramValues.add("token", token);
		paramValues.add("productId", "1");

		mockMvc.perform(
				MockMvcRequestBuilders.post("/loan/createLoan").params(paramValues).accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 断言
				.andExpect(jsonPath("$.code").value("ok"))
				.andDo(MockMvcResultHandlers.print()).andReturn();

	}

    @Test
    public void refuse() throws Exception {

		MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
		paramValues.add("token", token);
		paramValues.add("loanId", "L20190816022431840595164");
		paramValues.add("operator", "1");

		mockMvc.perform(
				MockMvcRequestBuilders.post("/loan/refuse").params(paramValues).accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 断言
				.andExpect(jsonPath("$.code").value("ok"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
    }
    
    @Test
    public void resetUserVerifyByClose() throws Exception {
    	loanService.resetUserVerifyByClose();
    }

    @Test
    public void closeLoan() throws Exception {
		MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
		paramValues.add("token", token);
		paramValues.add("loanId", "L20190829022036654297745");

		mockMvc.perform(
				MockMvcRequestBuilders.post("/loan/closeLoan").params(paramValues).accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 断言
				.andExpect(jsonPath("$.code").value("ok"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
    }
}