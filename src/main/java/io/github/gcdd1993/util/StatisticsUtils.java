package io.github.gcdd1993.util;

import io.github.gcdd1993.model.JobInfo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计
 *
 * @author gaochen
 * Created on 2019/6/14.
 */
@Slf4j
@UtilityClass
public class StatisticsUtils {

    /**
     * 统计薪资平均值
     *
     * @param jobInfoList list of job info
     * @return average of salary
     */
    public static Double avgSalary(List<JobInfo> jobInfoList) {
        if (jobInfoList == null || jobInfoList.isEmpty()) {
            return 0.0;
        }
        Double sum = jobInfoList.stream().map(JobInfo::getSalaryAvg)
                .reduce(0.0, Double::sum);
        return sum / jobInfoList.size();
    }

    /**
     * 最高或最低薪资排名
     *
     * @param jobInfoList 最高薪资job信息
     * @param top         if false 按照最低薪资，if true 按照最高薪资排名
     * @return list of job info with top salary
     */
    public static List<JobInfo> sort(List<JobInfo> jobInfoList, boolean top) {
        // 取出最高薪资
        Comparator<JobInfo> jobInfoComparator = top ?
                Comparator.comparing(JobInfo::getSalaryTop).reversed() :
                Comparator.comparing(JobInfo::getSalaryBottom);
        return jobInfoList.stream()
                .sorted(jobInfoComparator)
                .collect(Collectors.toList());
    }

}
