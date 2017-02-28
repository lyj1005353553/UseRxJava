package net;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import urlUtils.UrlManager;

/**
 * Created by Administrator on 2017/2/28.
 * 网络操作工具类
 */

public class RetrofitUtils {
    private static Retrofit retrofit;
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(UrlManager.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    OkHttpClient.Builder httpClient=new OkHttpClient.Builder();

    /**
     * 添加拦截器
     */
    void addInterceptor(){
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original=chain.request();
                Request request=original.newBuilder()
                        .header("User-Agent","com.lyj.ReXXX")
                        .header("Accept","application/vnd.yourapi.v1.full+json")
                        .method(original.method(),original.body())
                        .build();
                return chain.proceed(request);
            }
        });
    }
}
