package com.papbl.proyekakhirpapbl;

import com.papbl.proyekakhirpapbl.model_api.ModelAPIAgens;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("get_agens.php")
    Call<ModelAPIAgens> getAgensByCityName(@Query("kota") String cityName);
}
