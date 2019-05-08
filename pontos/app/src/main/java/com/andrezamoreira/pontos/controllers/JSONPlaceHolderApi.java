package com.andrezamoreira.pontos.controllers;

import com.andrezamoreira.pontos.models.SpecificUser;
import com.andrezamoreira.pontos.models.TimeEntries;
import com.andrezamoreira.pontos.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JSONPlaceHolderApi {

    @Headers({"Content-Type: application/json"})
    @GET("workspaces/5ab54394b079877ff6187947/users")
    Call<List<User>> getUser();

    @GET("users/{idUsuario}")
    Call<SpecificUser> getSpecificUser(@Path("idUsuario") String idUsuario);

    @POST("workspaces/5ab54394b079877ff6187947/timeEntries/user/{idUsuario}/entriesInRange")
    Call<List<TimeEntries>> getTimeEntries(@Path("idUsuario") String idUsuario);

}
