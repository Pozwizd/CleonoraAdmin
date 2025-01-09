package com.example.cleonoraadmin.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SalesChartDataDTO {
    private List<String> labels;
    private Map<String, List<Long>> datasets;

}