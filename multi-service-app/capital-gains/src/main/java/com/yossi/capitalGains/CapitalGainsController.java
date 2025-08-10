package com.yossi.capitalGains;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/capital-gains")
public class CapitalGainsController {

    private final CapitalGainsService capitalGainsService;

    @Autowired
    public CapitalGainsController(CapitalGainsService capitalGainsService) {
        this.capitalGainsService = capitalGainsService;
    }

    

    // 1. Modify the controller to accept the portfolio query parameter
    @GetMapping
    public double getCapitalGains(
            @RequestParam(value = "portfolio", required = false) String portfolio,
            @RequestParam(value = "numsharesgt", required = false) Integer numSharesGt,
            @RequestParam(value = "numshareslt", required = false) Integer numSharesLt) {
    
        if (numSharesGt != null && numSharesGt < 0) {
            throw new IllegalArgumentException("numSharesGt must be non-negative");
        }
        if (numSharesLt != null && numSharesLt < 0) {
            throw new IllegalArgumentException("numSharesLt must be non-negative");
        }
    
        if (numSharesGt != null) {
            return capitalGainsService.getCapitalGainsWithSharesGreaterThan(numSharesGt);
        } else if (numSharesLt != null) {
            return capitalGainsService.getCapitalGainsWithSharesLessThan(numSharesLt);
        } else {
            return capitalGainsService.getCapitalGains();
        }
    }

}









    