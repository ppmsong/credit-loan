package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import isec.base.util.S;
import isec.loan.configurer.Config;
import isec.loan.entity.enums.OdinPushType;
import isec.loan.entity.enums.SmsCodeType;
import isec.loan.service.OdinService;
import isec.loan.service.SmsCodeService;
import org.junit.Test;
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
import sun.applet.Main;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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

    @Autowired
    Config config;

    @Autowired
    OdinService odinService;


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

    @Test
    public void testSign() {

//       System.out.println( ((new BigDecimal(33).divide(new BigDecimal(100),2,RoundingMode.DOWN))));

//        odinService.sendCommonPush("01c767c29916459789fe1f8042d70f40", "123", OdinPushType.LOAN_SUCCESS.getStrkey(), new HashMap<>());

//        System.out.println(new BigDecimal(12.1).setScale( 0, BigDecimal.ROUND_UP));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(S.getCurrentTimestamp()*1000));

    }


}
