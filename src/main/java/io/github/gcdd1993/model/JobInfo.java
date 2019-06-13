package io.github.gcdd1993.model;

import lombok.Builder;
import lombok.Data;

/**
 * 职位模型
 *
 * @author gaochen
 * Created on 2019/6/13.
 */
@Data
@Builder
public class JobInfo {

    /**
     * 职位名称
     */
    private String name;

    /**
     * 企业名称
     */
    private String company;

    /**
     * 薪资上限
     */
    private Integer salaryTop;

    /**
     * 薪资下限
     */
    private Integer salaryBottom;

    /**
     * 薪资平均值
     */
    private Double salaryAvg;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 公司所处阶段，已上市，D轮，天使轮。。。
     */
    private String stage;

    /**
     * 公司规模
     */
    private String scale;

    /**
     * 工作经验要求，3-5年或者5年以上。。
     */
    private String experience;

    /**
     * 学历要求
     */
    private String education;

    /**
     * 抓取地址
     */
    private String url;

}
