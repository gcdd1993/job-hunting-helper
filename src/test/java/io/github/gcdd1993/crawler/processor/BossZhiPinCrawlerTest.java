package io.github.gcdd1993.crawler.processor;

import io.github.gcdd1993.model.JobInfo;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * TODO
 *
 * @author gaochen
 * Created on 2019/6/14.
 */
public class BossZhiPinCrawlerTest {

    @Test
    public void process() {
        List<JobInfo> jobInfos = BossZhiPinCrawler.process("https://www.zhipin.com/c101190200/?query=Java&page=1");
        jobInfos.forEach(System.out::println);
    }

    @Test
    public void nextPage() {
        String nextPage = BossZhiPinCrawler.nextPage("https://www.zhipin.com/c101190200/?query=Java&page=1");
        System.out.println(nextPage);
    }
}