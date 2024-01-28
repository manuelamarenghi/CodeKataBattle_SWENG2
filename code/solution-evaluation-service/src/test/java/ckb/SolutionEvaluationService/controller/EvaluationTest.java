package ckb.SolutionEvaluationService.controller;

import ckb.SolutionEvaluationService.service.EvaluationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EvaluationTest {

    @Autowired
    private EvaluationService evaluationService;

    @Test
    public void test() {
        int res = evaluationService.executeStaticAnalysis("c", "/home/luca/Projects/triennale/WordChecker/");
        System.out.println(res);
    }

}
