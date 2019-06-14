package io.github.gcdd1993.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
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
@ExcelTarget("职位信息")
public class JobInfo {

    /**
     * 职位名称
     */
    @Excel(name = "职位名称")
    private String name;

    /**
     * 企业名称
     */
    @Excel(name = "企业名称")
    private String company;

    /**
     * 薪资上限
     */
    @Excel(name = "最高薪资（单位：K）")
    private Integer salaryTop;

    /**
     * 薪资下限
     */
    @Excel(name = "最低薪资（单位：K）")
    private Integer salaryBottom;

    /**
     * 薪资平均值
     */
    @Excel(name = "平均薪资（单位：K）", numFormat = "##.000")
    private Double salaryAvg;

    /**
     * 所属行业
     */
    @Excel(name = "所属行业")
    private String industry;

    /**
     * 公司所处阶段，已上市，D轮，天使轮。。。
     */
    @Excel(name = "企业所处阶段")
    private String stage;

    /**
     * 公司规模
     */
    @Excel(name = "企业规模")
    private String scale;

    /**
     * 工作经验要求，3-5年或者5年以上。。
     */
    @Excel(name = "工作经验要求（单位：年）")
    private String experience;

    /**
     * 学历要求
     */
    @Excel(name = "学历要求")
    private String education;

    /**
     * 抓取地址
     */
    @Excel(name = "链接")
    private String url;

}
