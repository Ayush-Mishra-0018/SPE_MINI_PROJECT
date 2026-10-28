
package com.calculator.devops.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import com.calculator.devops.service.CalculatorService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @MockitoBean
    private CalculatorService calculatorService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAddTwoNumbers() throws Exception {
        when(calculatorService.add(10.0, 20.0)).thenReturn(30.0);

        mockMvc.perform(get("/api/v1/add")
                        .param("a", "10")
                        .param("b", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.a").value(10.0))
                .andExpect(jsonPath("$.b").value(20.0))
                .andExpect(jsonPath("$.result").value(30.0));
    }

    @Test
    void shouldReturnBadRequestWhenParameterIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/add")
                        .param("a", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Missing or invalid request parameter"))
                .andExpect(jsonPath("$.path").value("/api/v1/add"));
    }
}


