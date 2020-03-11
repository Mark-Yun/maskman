package com.mark.zumo.client.customer.model.server;

import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.entity.Sub;
import com.mark.zumo.client.customer.entity.Token;

import java.util.List;

import io.reactivex.Maybe;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mark on 20. 3. 8.
 */
public interface AppServer {

    @GET("stores/json")
    Maybe<List<Store>> queryStoreList(@Query("lat2") double latitude1,
                                      @Query("lng2") double longitude1,
                                      @Query("lat1") double latitude2,
                                      @Query("lng1") double longitude2);

    @GET("sub")
    Maybe<Sub> querySub(@Query("user_id") final String user_id,
                        @Query("code") final String code);

    @GET("sub")
    Maybe<List<Sub>> querySubList(@Query("user_id") final String user_id);

    @POST("sub")
    Maybe<Sub> createSub(@Body final Sub sub);

    @DELETE("sub/{user_id}/{code}")
    Maybe<Sub> deleteSub(@Path("user_id") final String user_id,
                         @Path("code") final String code);

    @POST("token")
    Maybe<Token> createToken(@Body Token token);
}
