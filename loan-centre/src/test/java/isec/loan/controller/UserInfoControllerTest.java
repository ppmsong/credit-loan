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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserInfoControllerTest extends BasicTest {

    @Autowired
    MockMvc mockMvc;

    @Before
    public void setupMockMvc() throws Exception {
        super.loginBySmsCode("15172538022");
    }

    @Test
    public void verifyRealName() throws Exception {

        // POST 参数
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);
        paramValues.add("name", "张三");
        paramValues.add("idcard", "4203281998");

        mockMvc.perform(MockMvcRequestBuilders.post("/userInfo/verifyRealName").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok"));
    }

    @Test
    public void getUserInfo() throws Exception {

        // POST 参数
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);

        mockMvc.perform(MockMvcRequestBuilders.post("/userInfo/getUserInfo").params(paramValues)
                .accept(MediaType.APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok"));
    }
}