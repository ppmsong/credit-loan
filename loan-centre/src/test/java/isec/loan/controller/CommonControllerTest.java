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
public class CommonControllerTest extends BasicTest {


    @Before
    public void setupMockMvc() throws Exception {
        super.loginBySmsCode("18621244622");
    }


    @Test
    public void globleSetting() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post("/common/globleSetting")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 断言
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }    
    
    
    
    @Test
	public void queryAdverts() throws Exception {
    	MultiValueMap<String, String> paramValues = new LinkedMultiValueMap<>();
		paramValues.add("advPosition", "1");
		
		mockMvc.perform(MockMvcRequestBuilders.post("/common/queryAdverts").params(paramValues).accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				// 断言
				.andExpect(jsonPath("$.code").value("ok")).andExpect(jsonPath("$.data").isNotEmpty())
				.andDo(MockMvcResultHandlers.print()).andReturn();
	}    
}