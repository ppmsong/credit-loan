package isec.loan.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
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
public class MessageControllerTest extends BasicTest {


    @Before
    public void setupMockMvc() throws Exception {
        super.loginBySmsCode("15172538022");
    }

    @Test
    public void sendMessage() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders.post("/user/sendMessage")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok"))
//                .andExpect(jsonPath("$.data.mobile").isNotEmpty())
//                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }


    @Test
    public void queryMessageList() throws Exception {
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("page", "1");
        paramValues.add("pageSize", "5");
        paramValues.add("token", token);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/queryMessageList").params(paramValues)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.data.messageList").isNotEmpty())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void queryMessageDetails() throws Exception {
        // POST 参数
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("messageId", "44257d3d895a481cbe964fd8326cda30");
        paramValues.add("token", token);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/queryMessageDetails").params(paramValues)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.data.title").isNotEmpty())
                .andExpect(jsonPath("$.data.content").isNotEmpty())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void getMessageCount() throws Exception {
        // POST 参数
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/getMessageCount").params(paramValues)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.data.messageCount").isNotEmpty())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
}