package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import isec.base.util.S;
import isec.loan.entity.enums.SmsCodeType;
import isec.loan.service.SmsCodeService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
public class BasicTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    SmsCodeService smsCodeService;

    protected static String token;

    public void loginBySmsCode(String mobile) throws Exception {


        String smsCode = S.getSix();
        smsCodeService.sendSmsCode(SmsCodeType.TYPE_LOGIN.getKey(), mobile, smsCode, "");
        // POST 参数
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("mobile", mobile);
        paramValues.add("code", smsCode);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/user/loginBySmsCode").params(paramValues)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok"))
                .andExpect(jsonPath("$.data.mobile").isNotEmpty())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andDo(MockMvcResultHandlers.print()).andReturn();

        JSONObject loginResult = JSONObject.parseObject(mvcResult.getResponse().getContentAsString());


        token = loginResult.getJSONObject("data").getString("token");
    }

}
