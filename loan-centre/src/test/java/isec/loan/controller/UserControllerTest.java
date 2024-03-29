package isec.loan.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import isec.base.util.S;
import isec.base.util.http.HttpClientManager;
import isec.loan.entity.Contacter;
import isec.loan.entity.Loan;
import isec.loan.entity.enums.IsDelete;
import isec.loan.entity.enums.LoanStatus;
import isec.loan.entity.enums.SmsCodeType;
import isec.loan.entity.enums.TgType;
import isec.loan.service.LoanService;
import isec.loan.service.SmsCodeService;
import isec.loan.service.TelegramService;
import isec.loan.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest extends BasicTest {

    @Autowired
    SmsCodeService smsCodeService;
    @Autowired
    UserService userService;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    TelegramService telegramService;

    @Before
    public void setupMockMvc() throws Exception {
        super.loginBySmsCode("18888888888");
    }

    @Test
    public void loginBySmsCode() throws Exception {

        String smsCode = S.getSix();
        String mobile = "18621871999";
//		String mobile = "18621"+smsCode;
        smsCodeService.sendSmsCode(SmsCodeType.TYPE_LOGIN.getKey(), mobile, smsCode, "");
        // POST 参数
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("mobile", mobile);
        paramValues.add("code", smsCode);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/loginBySmsCode").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok")).andExpect(jsonPath("$.data.mobile").isNotEmpty())
                .andExpect(jsonPath("$.data.token").isNotEmpty()).andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void loginByPassword() throws Exception {

        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("mobile", "15172538022");
        paramValues.add("password", "1");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/loginByPassword").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok")).andDo(MockMvcResultHandlers.print()).andReturn();

    }

    @Test
    public void editPassword() throws Exception {

        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);
        paramValues.add("oldPwd", "1");
        paramValues.add("password", "1");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/editPassword").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok")).andDo(MockMvcResultHandlers.print()).andReturn();

    }

    @Test
    public void saveContacter() throws Exception {
        Contacter contacter1 = new Contacter();
        contacter1.setName("name1");
        contacter1.setRelateion("朋友");
        contacter1.setMobile("12312342134");

        Contacter contacter2 = new Contacter();
        contacter2.setName("name2");
        contacter2.setRelateion("同事");
        contacter2.setMobile("12312342134");

        List<Contacter> contacterList = new ArrayList<Contacter>();
        contacterList.add(contacter1);
        contacterList.add(contacter2);

        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);
        paramValues.add("contacterList", JSON.toJSONString(contacterList));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/saveContacter").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok")).andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void saveBank() throws Exception {

        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);

//		paramValues.add("name", "张三");
//		paramValues.add("idcard", "428952159741359852");
        paramValues.add("bankName", "建设银行");
        paramValues.add("bankCardno", "4545454545454");
        paramValues.add("bankMobile", "15898561024");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/saveBank").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok")).andDo(MockMvcResultHandlers.print()).andReturn();

    }


    @Test
    public void savePhoneBook() throws Exception {

        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);

        paramValues.add("phoneBook", "{}");


        mockMvc.perform(MockMvcRequestBuilders.post("/user/savePhoneBook").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok")).andDo(MockMvcResultHandlers.print()).andReturn();

    }


    @Test
    public void saveUserAppLog() throws Exception {

        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);

        paramValues.add("deviceName", "deviceName");
        paramValues.add("mac", "mac");
        paramValues.add("iemi", "iemi");
        paramValues.add("location", "location");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/saveUserAppLog").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok")).andDo(MockMvcResultHandlers.print()).andReturn();

    }

    @Test
    public void test() {
/*//    public static void main(String[] args) {
        Map<String, String> datas = Maps.newHashMap();
        //String proxyIp = "47.88.218.44";
        //String proxyPort = "33318";
        String token = "805272506:AAHQLoDQSk3In7sl4VTJcmdLwlBHI3Qjg_8";
        String api = String.format("https://api.telegram.org/bot%s/sendMessage", token);
        String chatId = "-202324455";

        datas.put("chat_id", chatId);
        datas.put("text", "测试tg消息");
        datas.put("parse_mode", "Markdown");
        try {
            HttpClientManager client = HttpClientManager.getClient();
            String s = "";
//	    	if("prod".equals(env)) {
//		    	s = client.httpPost(api, datas);						//不需要代理的情况
//	    	}else {
            s = client.httpPostSocket5Proxy(api, 8838, datas);    //需要代理的情况
//	    	}
        } catch (Exception e) {
            e.printStackTrace();
        }*/

//     telegramService.sendTgMsg3("1",TgType.OUT_MONEY.getKey(),"测试");
    }

}