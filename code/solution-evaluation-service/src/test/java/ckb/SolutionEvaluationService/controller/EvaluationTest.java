package ckb.SolutionEvaluationService.controller;

import ckb.SolutionEvaluationService.service.EvaluationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EvaluationTest {

    @Autowired
    private EvaluateController evaluateController;

    @Test
    public void test() {
        evaluateController.evaluate("https://github.com/SigCatta/WordChecker.git");
    }

}
