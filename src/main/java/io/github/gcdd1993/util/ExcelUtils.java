package io.github.gcdd1993.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import io.github.gcdd1993.model.JobInfo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author gaochen
 * Created on 2019/6/14.
 */
@Slf4j
@UtilityClass
public class ExcelUtils {

    private static final String BASE_PATH = System.getProperty("user.dir");

    /**
     * 导出基金信息到Excel
     *
     * @param jobInfoMap Map Of job info
     * @return fileName
     */
    public static String export(String title, Map<String, List<JobInfo>> jobInfoMap) {
        List<Map<String, Object>> list = new ArrayList<>(jobInfoMap.size());
        jobInfoMap.forEach((k, v) -> {
            Map<String, Object> excelMap = new HashMap<>(3);
            ExportParams params = new ExportParams();
            params.setSheetName(k);
            excelMap.put("title", params);
            excelMap.put("entity", JobInfo.class);
            excelMap.put("data", v);

            list.add(excelMap);
        });

        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);

        try {
            String fileName = BASE_PATH +
                    "/" +
                    title +
                    "-" +
                    DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()) +
                    ".xls";
            FileOutputStream fos = new FileOutputStream(new File(fileName));
            workbook.write(fos);
            fos.close();

            return fileName;
        } catch (IOException e) {
            log.error("导出Excel出错", e);
            return null;
        }
    }

}
