package io.github.gcdd1993.crawler.processor;

import io.github.gcdd1993.model.JobInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

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
public class BossZhiPinProcessor implements PageProcessor {

    private static final Pattern BASE_URL_PATTERN = Pattern.compile("https://www\\.zhipin\\.com/c\\d+/\\?query=\\S+&page=\\d+");

    @Getter
    private Site site = Site.me()
            .setTimeOut(3000)
            .setSleepTime(200)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36")
            .setRetryTimes(3);

    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        if (BASE_URL_PATTERN.matcher(url).matches()) {
            log.info("current url is {}", url);

            Html html = page.getHtml();
            List<Selectable> nodes = html.xpath("//div[@class='job-list']/ul/li/div[@class='job-primary']").nodes();
            List<JobInfo> jobInfoList = nodes.stream().map(this::parse).filter(Objects::nonNull).collect(Collectors.toList());
            jobInfoList.forEach(jobInfo -> log.info("职位信息 --> {}", jobInfo));

            page.putField("job_info_list", jobInfoList);
        } else {
            log.warn("current url {} can not be processed.", url);
        }
    }

    /**
     * 解析每行数据
     *
     * @param node 节点
     * @return job info {@link JobInfo}
     */
    private JobInfo parse(Selectable node) {
        String name = node.xpath("//div[@class='info-primary']/h3[@class='name']/a/div[@class='job-title']/text()").get();
        // 2-3k
        String salary = node.xpath("//div[@class='info-primary']/h3[@class='name']/a/span[@class='red']/text()").get();
        String url = "https://www.zhipin.com" + node.xpath("//div[@class='info-primary']/h3[@class='name']/a/@href").get();
        String s = node.xpath("//div[@class='info-primary']/p").get();
        String[] nodeSplit = s.replace("<p>", "")
                .replace("</p>", "")
                .split("<em class=\"vline\"></em>");
        String experience = nodeSplit[1];
        String education = nodeSplit[2];

        // 企业信息
        String company = node.xpath("//div[@class='info-company']/div[@class='company-text']/h3/a/text()").get();
        String industry = node.xpath("//div[@class='info-company']/div[@class='company-text']/p/text()").get();
        String s1 = node.xpath("//div[@class='info-company']/div[@class='company-text']/p").get();

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
            log.warn("薪资抓取失败，丢弃 {}", node.get());
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
    }

}
