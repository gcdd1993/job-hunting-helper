package io.github.gcdd1993.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author gaochen
 * Created on 2019/6/12.
 */
@Slf4j
@UtilityClass
public class HttpUtils {

    public static String get(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36")
                .addHeader("Accept", "*/*")
                .build();

        try {
            Response response = client.newCall(request).execute();
            Thread.sleep(200);
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                return null;
            }
        } catch (Exception ex) {
            log.error("获取接口 {} 数据出错", url, ex);
            return null;
        }
    }
}
