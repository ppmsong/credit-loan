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
public class BillControllerTest extends BasicTest {


    @Before
    public void setupMockMvc() throws Exception {
        super.loginBySmsCode("15172538022");
    }


    @Test
    public void createBill() throws Exception {
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("loanId", "3f919b247e434c63a6592801775a0c7c");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/bill/createBill").params(paramValues).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }


    

    @Test
    public void myBill() throws Exception {
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/bill/myBill").params(paramValues).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void addPayFlow() throws Exception {
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);
        paramValues.add("billId", "20190801095953011178632");
        paramValues.add("repayment", "6000");
        paramValues.add("req_param", "1");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/bill/addPayFlow").params(paramValues).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
}