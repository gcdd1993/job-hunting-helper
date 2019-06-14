package io.github.gcdd1993;

import io.github.gcdd1993.crawler.processor.BossZhiPinCrawler;
import io.github.gcdd1993.model.JobInfo;
import io.github.gcdd1993.util.ExcelUtils;
import io.github.gcdd1993.util.StatisticsUtils;
import lombok.extern.slf4j.Slf4j;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.email.Recipient;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

import javax.activation.FileDataSource;
import javax.mail.Message;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 主入口，PC端只能展示10页，作为每天咨询够了
 * 如果要全部数据，可以选择爬取手机端页面
 *
 * @author gaochen
 * Created on 2019/6/14.
 */
@Slf4j
public class MainSpiderTask {

    /**
     * param1: 城市
     * 无锡|101190200
     * 杭州|101210100
     * param2: 职位
     * param3: 当前页
     */
    private static final String BASE_URL = "https://www.zhipin.com/c%s/?query=%s&page=%d";

    /**
     * 主方法入口
     * <p>
     * args[0] : 行业 Java
     * args[1] : 城市代码列表，','分割，例如 无锡|101190200
     * args[2] : 关心的企业全称列表，','分割，关心的企业将会全部展示
     * </p>
     *
     * @param args 参数列表
     */
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(MainSpiderTask.class.getResourceAsStream("/config.properties"), "UTF-8"));
        String industry = properties.getProperty("industry");
        String[] cities = properties.getProperty("cities").split(",");
        String[] companies = properties.getProperty("companies").split(",");

        StringBuilder sb = new StringBuilder();
        Map<String, List<JobInfo>> jobInfoMap = new HashMap<>(16);
        for (String city : cities) {
            String[] citySplit = city.split("\\|");
            String startPage = String.format(BASE_URL, citySplit[1], industry, 1);

            // 获取所有列表页
            List<JobInfo> jobInfoList = new ArrayList<>(256);
            String currentUrl = startPage;
            for (; ; ) {
                String nextPage = BossZhiPinCrawler.nextPage(currentUrl);
                if (nextPage != null) {
                    jobInfoList.addAll(BossZhiPinCrawler.process(nextPage));
                    currentUrl = nextPage;
                } else {
                    break;
                }
            }

            // 平均薪资
            Double avgSalary = StatisticsUtils.avgSalary(jobInfoList);
            // 最高薪资
            List<JobInfo> topJobs = StatisticsUtils.sort(jobInfoList, true);
            // 最低薪资
            List<JobInfo> bottomJobs = StatisticsUtils.sort(jobInfoList, false);

            List<JobInfo> favoriteJobInfos = jobInfoList.stream().filter(jobInfo -> {
                for (String favorite : companies) {
                    if (jobInfo.getCompany().contains(favorite)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());

            log.info("{} 的平均薪资为 {}", citySplit[0], avgSalary);
            appendInfo(sb, citySplit[0], avgSalary);

            jobInfoMap.put(citySplit[0] + "职位信息", jobInfoList);
            jobInfoMap.put(citySplit[0] + "最高薪资Rank", topJobs);
            jobInfoMap.put(citySplit[0] + "最低薪资Rank", bottomJobs);
            jobInfoMap.put(citySplit[0] + "您关注的公司的职位信息", favoriteJobInfos);
        }

        String file = ExcelUtils.export("Boss直聘招聘信息-" + industry, jobInfoMap);

        // 发邮件
        Mailer mailer = MailerBuilder.withSMTPServer("smtp.qq.com", 465, "1398371419@qq.com", "sxawopourtksfhed")
                .withTransportStrategy(TransportStrategy.SMTPS)
                .buildMailer();

        Email email = EmailBuilder.startingBlank()
                .withSubject("Boss直聘招聘信息-" + industry)
                .withRecipient(new Recipient("gaochen", "1398371419@qq.com", Message.RecipientType.TO))
                .from("1398371419@qq.com")
                .withHTMLText(sb.toString())
                .withAttachment("Boss直聘招聘信息-" + industry, new FileDataSource(file))
                .buildEmail();
        mailer.sendMail(email);
    }

    private static void appendInfo(StringBuilder sb, String city, Double avgSalary) {
        sb.append("<p font=red>")
                .append(city)
                .append("</p>的平均薪资为<p font=red>")
                .append(avgSalary)
                .append("K</p><br>");
    }

}
