package isec.loan.controller;

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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AlipayControllerTest extends BasicTest {

    @Autowired
    MockMvc mockMvc;

    @Before
    public void setupMockMvc() throws Exception {
        super.loginBySmsCode("18621424445");
    }

    @Test
    public void queryCreditScore() throws Exception {

        // POST 参数
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);
        paramValues.add("authCode", "8ed8703635444dcebd8c044c1c45UE57");


        mockMvc.perform(MockMvcRequestBuilders.post("/alipay/grantCreditScore").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content()
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
//                // 断言
                .andExpect(jsonPath("$.code").value("ok"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void bindAliAccount() throws Exception {

        // POST 参数
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);
        paramValues.add("authCode", "5b52360ed5e347d79861c83688c3TX70");

        mockMvc.perform(MockMvcRequestBuilders.post("/alipay/bindAliAccount").params(paramValues)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
}