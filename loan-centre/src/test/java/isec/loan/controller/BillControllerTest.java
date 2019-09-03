package isec.loan.controller;

import isec.loan.service.PayService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
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
public class BillControllerTest extends BasicTest {

    @Autowired
    PayService payService;

    @Before
    public void setupMockMvc() throws Exception {
        super.loginBySmsCode("18888888888");
    }


    @Test
    public void createBill() throws Exception {
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("loanId", "L20190818031356308573005");
        paramValues.add("operatorId", "1");

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

    @Test
    public void replaymentNotily() throws Exception {

        String billId = "P20190828110754799188647";
        MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders.post("/pay/replaymentNotily").content("{\"transaction_id\":\"" + billId + "\",\"signature\":\"f1ee00f1f4805254d145a9d1ef035d3a\",\"transaction_fee\":15,\"sub_channel_type\":\"CS_ALI_WAP\",\"id\":\"59ca10d7f99b43fab30ddc13bb94a991\",\"channel_type\":\"BC\",\"transaction_type\":\"PAY\",\"message_detail\":{\"bill_id\":\"59ca10d7f99b43fab30ddc13bb94a991\",\"mob\":\"15813862111\",\"notify_time\":\"2019-04-13 17:06:35\",\"gmt_payment\":15,\"transactionFee\":15,\"out_trade_no\":\"2019041322001437381028123776\",\"inner_trade_no\":\"20190413050616481685444\",\"trade_status\":\"\",\"sign_type\":\"\",\"cs_merbill_id\":\"20190413170617977728048\",\"channel_trade_no\":\"2019041322001437381028123776\",\"trade_success\":true},\"timestamp\":1555146395213,\"trade_success\":true}")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()).andReturn();

        if (mvcResult.getResponse().getContentAsString().equals("success")) {
            System.out.println("还款成功");
        } else {
            System.out.println(mvcResult.getResponse().getContentAsString());
        }
    }


    @Test
    public void billDetail() throws Exception {
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);
        paramValues.add("billId", "B20190819020733805836695");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/bill/billDetail").params(paramValues).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
    
    @Test
    public void rePay() throws Exception {
        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);
        paramValues.add("billId", "B20190814055305328209873");
        paramValues.add("amount", "1.00");
        mockMvc.perform(
                MockMvcRequestBuilders.post("/bill/rePay").params(paramValues).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
				.andExpect(jsonPath("$.code").value("ok")).andExpect(jsonPath("$.data").isNotEmpty())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void receiveLoan() throws Exception {

        MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
        paramValues.add("token", token);
        paramValues.add("loanId", "L20190826054041294660956");
        mockMvc.perform(
                MockMvcRequestBuilders.post("/bill/receiveLoan").params(paramValues).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andExpect(jsonPath("$.code").value("ok"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void test() {
        payService.checkOrder("P20190828035451813223291");
    }
}