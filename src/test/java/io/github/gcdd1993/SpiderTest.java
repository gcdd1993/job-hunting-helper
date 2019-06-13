package io.github.gcdd1993;

import io.github.gcdd1993.crawler.processor.BossZhiPinProcessor;
import io.github.gcdd1993.model.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;

import java.util.Arrays;
import java.util.List;

/**
 * TODO
 *
 * @author gaochen
 * Created on 2019/6/13.
 */
@Slf4j
public class SpiderTest {

    @Test
    public void spider() {
        Spider.create(new BossZhiPinProcessor())
                .test("https://www.zhipin.com/c101190200/?query=Java&page=2");
    }

    @Test
    public void getAll() {
        List<ResultItems> resultItems = Spider.create(new BossZhiPinProcessor())
                .getAll(Arrays.asList("https://www.zhipin.com/c101190200/?query=Java&page=1",
                        "https://www.zhipin.com/c101190200/?query=Java&page=2"));
        resultItems.forEach(resultItem -> {
            log.info("获取结果 --> {}", (List<JobInfo>) resultItem.get("job_info_list"));
        });
    }
}
