package app;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.calculator.Calculator;
import app.calculator.CalculatorImpl;
import app.dto.CalculationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.ApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
public class AppTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private  ApplicationContext ctx;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testSum(CapturedOutput output) throws Exception {

        var data = new CalculationDTO(5, 10);

        var response = mockMvc.perform(post("/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andReturn();

        var content = response.getResponse().getContentAsString();

        assertThatJson(content).and(
                v -> v.node("result").isEqualTo(15)
        );

        assertThat(output).contains("Was called method: sum() with parameters: [5, 10]");
    }

    @Test
    void testMult(CapturedOutput output) throws Exception {
        var data = new CalculationDTO(5, 10);

        var response = mockMvc.perform(post("/mult")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andReturn();

        var content = response.getResponse().getContentAsString();

        assertThatJson(content).and(
                v -> v.node("result").isEqualTo(50)
        );
        assertThat(output).contains("Was called method: mult() with parameters: [5, 10]");
    }

    // Проверяем, что логирование происходит не в контролере,
    // а при вызове методов класса, отмеченного аннотацией
    @Test
    void testContext(CapturedOutput output) throws Exception {

        // Проверяем, что методы исходного класса калькулятора не содержат логирование
        Calculator initialCalc = new CalculatorImpl();
        initialCalc.sum(3, 4);
        assertThat(output).doesNotContain("Was called method: sum() with parameters: [3, 4]");

        initialCalc.mult(3, 4);
        assertThat(output).doesNotContain("Was called method: mult() with parameters: [3, 4]");

        // Получаем бин калькулятор из контекста приложения
        Calculator calculatorInContext = ctx.getBean(Calculator.class);

        // Проверяем, что при вызове его методов происходит логирование
        calculatorInContext.sum(3, 4);
        assertThat(output).contains("Was called method: sum() with parameters: [3, 4]");

        calculatorInContext.mult(4, 5);
        assertThat(output).contains("Was called method: mult() with parameters: [4, 5]");
    }
}