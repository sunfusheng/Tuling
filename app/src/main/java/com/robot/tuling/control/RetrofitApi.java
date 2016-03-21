package com.robot.tuling.control;

import com.robot.tuling.entity.MessageEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sunfusheng on 2016/3/20.
 */
public interface RetrofitApi {

    // 获得图灵API接口URL
    @GET("api")
    Call<MessageEntity> getTulingInfo(@Query("key") String key, @Query("info") String info);

}
