package isec.loan.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ActionRecordControllerTest extends BasicTest{

    @Before
    public void setupMockMvc() throws Exception {
        super.loginBySmsCode("18888888888");
    }

    @Test
    public void alertLoanSucc() throws Exception {

        // POST 参数
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);

        mockMvc.perform(MockMvcRequestBuilders.post("/actionRecord/alertLoanSucc").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
}