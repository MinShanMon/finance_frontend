
package com.team3.personalfinanceapp.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.HEAD;

public class APIClient {

    private APIClient() {
        throw new IllegalStateException("Utility class");
    }

    static final String BASE_URL_CLOUD = "http://adteam3restapi-env.eba-p5rw8sy5.ap-northeast-1.elasticbeanstalk.com/api/";

    static final String BASE_URL_LOCAL = "http://10.0.2.2:8081/api/";

    static final String BANK_URL = "https://api.ocbc.com:8243/transactional/account/1.0/";

    static HttpLoggingInterceptor logger = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logger)
            .build();

    public static Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL_LOCAL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public static Retrofit getBankClient() {
    static UserService getUserService(){
        UserService userService = getClient().create(UserService.class);
        return userService;
    }

    static Retrofit getBankClient() {
        return new Retrofit.Builder()
                .baseUrl(BANK_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}
