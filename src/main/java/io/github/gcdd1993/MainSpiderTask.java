package io.github.gcdd1993;

import io.github.gcdd1993.crawler.processor.BossZhiPinProcessor;
import io.github.gcdd1993.model.JobInfo;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * TODO
 *
 * @author gaochen
 * Created on 2019/6/14.
 */
@Slf4j
public class MainSpiderTask {

    /**
     * param1: 城市
     * param2: 职位
     * param3: 当前页
     */
    private static final String BASE_URL = "https://www.zhipin.com/c%s/?query=%s&page=%d";

    /**
     * 主方法入口
     * <p>
     * args[0] : 页数 500
     * args[1] : 行业 Java
     * args[2] : 城市代码列表，','分割，例如 无锡 101190200
     * args[3] : 关心的企业全称列表，','分割，关心的企业将会全部展示
     * </p>
     *
     * @param args 参数列表
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Integer page = Integer.valueOf(args[0]);
        String industry = args[1];
        String[] cities = args[2].split(",");
        String[] companies = args[3].split(",");

        for (String city : cities) {
            List<String> urls = IntStream.rangeClosed(1, page)
                    .boxed()
                    .map(p -> String.format(BASE_URL, city, industry, p))
                    .collect(Collectors.toList());

            List<ResultItems> resultItemsList = Spider.create(new BossZhiPinProcessor())
                    .thread(4)
                    .getAll(urls);

            List<JobInfo> jobInfoList = resultItemsList.stream().map(resultItems -> (List<JobInfo>) resultItems.get("job_info_list"))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());



        }

    }

}
