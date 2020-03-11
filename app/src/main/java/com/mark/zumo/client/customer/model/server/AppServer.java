package com.mark.zumo.client.customer.model.server;

import com.mark.zumo.client.customer.entity.Store;

import java.util.List;

import io.reactivex.Maybe;
import retrofit2.http.GET;
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
}
