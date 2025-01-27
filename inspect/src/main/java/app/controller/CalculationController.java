package app.controller;

import app.calculator.Calculator;
import app.dto.CalculationDTO;
import app.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculationController {

    @Autowired
    private Calculator calculator;

    @PostMapping("/sum")
    public ResultDTO getSum(@RequestBody CalculationDTO data) {
        int result = calculator.sum(data.getFirstOperand(), data.getSecondOperand());
        return new ResultDTO(result);
    }

    @PostMapping("/mult")
    public ResultDTO getMult(@RequestBody CalculationDTO data) {
        int result = calculator.mult(data.getFirstOperand(), data.getSecondOperand());
        return new ResultDTO(result);
    }
}