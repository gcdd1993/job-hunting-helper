package io.github.gcdd1993.crawler.processor;

import io.github.gcdd1993.model.JobInfo;
import io.github.gcdd1993.util.HttpUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import us.codecraft.xsoup.Xsoup;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author gaochen
 * Created on 2019/6/13.
 */
@Slf4j
@UtilityClass
public class BossZhiPinCrawler {

    private static final Pattern BASE_URL_PATTERN = Pattern.compile("https://www\\.zhipin\\.com/c\\d+/\\?query=\\S+&page=\\d+");

    /**
     * 抓取数据，延时1s
     *
     * @param url current page url
     */
    public static List<JobInfo> process(String url) {
        String htmlContent = HttpUtils.get(url);
        Document document = Jsoup.parse(htmlContent);

        if (BASE_URL_PATTERN.matcher(url).matches()) {
            log.info("current url is {}", url);

            List<Element> nodes = Xsoup.compile("//div[@class='job-list']/ul/li/div[@class='job-primary']").evaluate(document).getElements();
            List<JobInfo> jobInfoList = nodes.stream()
                    .map(BossZhiPinCrawler::parse)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            jobInfoList.forEach(jobInfo -> log.info("职位信息 --> {}", jobInfo));

            return jobInfoList;
        } else {
            log.warn("current url {} can not be processed.", url);
            return Collections.emptyList();
        }
    }

    /**
     * 获取下一页
     *
     * @param url current page
     */
    public static String nextPage(String url) {
        String htmlContent = HttpUtils.get(url);
        Document document = Jsoup.parse(htmlContent);

        String nextUrl = Xsoup.compile("//div[@class='page']/a[@class='next']/@href").evaluate(document).get();
        if (nextUrl != null && !nextUrl.contains("javascript")) {
            log.info("next page --> {}", nextUrl);
            return "https://www.zhipin.com" + nextUrl;
        } else {
            log.info("can not find next page.");
            return null;
        }
    }

    /**
     * 解析每行数据
     *
     * @param node 节点
     * @return job info {@link JobInfo}
     */
    private static JobInfo parse(Element node) {
        try {
            String name = Xsoup.compile("//div[@class='info-primary']/h3[@class='name']/a/div[@class='job-title']/text()").evaluate(node).get();
            // 2-3k
            String salary = Xsoup.compile("//div[@class='info-primary']/h3[@class='name']/a/span[@class='red']/text()").evaluate(node).get();
            String url = "https://www.zhipin.com" + Xsoup.compile("//div[@class='info-primary']/h3[@class='name']/a/@href").evaluate(node).get();
            String s = Xsoup.compile("//div[@class='info-primary']/p").evaluate(node).get();
            String[] nodeSplit = s.replace("<p>", "")
                    .replace("</p>", "")
                    .split("<em class=\"vline\"></em>");
            String experience = nodeSplit[1];
            String education = nodeSplit[2];

            // 企业信息
            String company = Xsoup.compile("//div[@class='info-company']/div[@class='company-text']/h3/a/text()").evaluate(node).get();
            String industry = Xsoup.compile("//div[@class='info-company']/div[@class='company-text']/p/text()").evaluate(node).get();
            String s1 = Xsoup.compile("//div[@class='info-company']/div[@class='company-text']/p").evaluate(node).get();

            String[] nodeSplit1 = s1.replace("<p>", "")
                    .replace("</p>", "")
                    .split("<em class=\"vline\"></em>");
            String stage = nodeSplit1[0];
            String scale = nodeSplit1[1];

            // salary 15-30K 15-30K·14薪
            int bottom;
            int top;
            double avg;
            int months = 12;
            if (salary.contains("-")) {
                if (salary.contains("·")) {
                    // 有14薪
                    String[] salaries = salary.split("·");
                    months = Integer.valueOf(salaries[1].replace("薪", ""));
                    salary = salaries[0];
                }
                String[] ks = salary.replace("K", "").split("-");
                bottom = Integer.valueOf(ks[0]);
                top = Integer.valueOf(ks[1]);
                avg = (bottom + top) / 2 * (months / 12);
            } else {
                log.warn("薪资抓取失败，丢弃 {}", node.text());
                return null;
            }
            return JobInfo.builder()
                    .name(name)
                    .company(company)
                    .industry(industry)
                    .stage(stage)
                    .scale(scale)
                    .experience(experience)
                    .education(education)
                    .salaryBottom(bottom)
                    .salaryTop(top)
                    .salaryAvg(avg)
                    .url(url)
                    .build();
        } catch (Exception ex) {
            log.warn("未知错误", ex);
            return null;
        }
    }

}
