package com.ptm.controller;

import com.ptm.service.StatisticsService;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/employees")
    public ResponseResult<List<Map<String, Object>>> employeeStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String name) {
        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();
        return statisticsService.getEmployeeStats(year, month, name);
    }

    @GetMapping("/projects")
    public ResponseResult<List<Map<String, Object>>> projectStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String name) {
        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();
        return statisticsService.getProjectStats(year, month, name);
    }

    @GetMapping("/employees/export")
    public void exportEmployees(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpServletResponse response) throws Exception {
        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();
        List<Map<String, Object>> data = statisticsService.getEmployeeStats(year, month, null).getData();

        org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("员工工时统计");
        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        String[] cols = {"姓名", "角色", "本月工时", "累计工时"};
        for (int i = 0; i < cols.length; i++) {
            header.createCell(i).setCellValue(cols[i]);
        }
        int r = 1;
        for (Map<String, Object> row : data) {
            org.apache.poi.ss.usermodel.Row xr = sheet.createRow(r++);
            xr.createCell(0).setCellValue(str(row.get("userName")));
            xr.createCell(1).setCellValue(str(row.get("roleName")));
            xr.createCell(2).setCellValue(num(row.get("monthHours")));
            xr.createCell(3).setCellValue(num(row.get("totalHours")));
        }
        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("员工工时统计.xlsx", "UTF-8"));
        try (OutputStream os = response.getOutputStream()) { wb.write(os); }
        wb.close();
    }

    @GetMapping("/projects/export")
    public void exportProjects(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpServletResponse response) throws Exception {
        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();
        List<Map<String, Object>> data = statisticsService.getProjectStats(year, month, null).getData();

        org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("项目工时统计");
        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        String[] cols = {"项目名称", "所属部门", "本月工时", "累计工时"};
        for (int i = 0; i < cols.length; i++) {
            header.createCell(i).setCellValue(cols[i]);
        }
        int r = 1;
        for (Map<String, Object> row : data) {
            org.apache.poi.ss.usermodel.Row xr = sheet.createRow(r++);
            xr.createCell(0).setCellValue(str(row.get("projectName")));
            xr.createCell(1).setCellValue(str(row.get("dept")));
            xr.createCell(2).setCellValue(num(row.get("monthHours")));
            xr.createCell(3).setCellValue(num(row.get("totalHours")));
        }
        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("项目工时统计.xlsx", "UTF-8"));
        try (OutputStream os = response.getOutputStream()) { wb.write(os); }
        wb.close();
    }

    private String str(Object o) { return o == null ? "" : String.valueOf(o); }
    private double num(Object o) { return o instanceof Number ? ((Number) o).doubleValue() : 0; }
}
