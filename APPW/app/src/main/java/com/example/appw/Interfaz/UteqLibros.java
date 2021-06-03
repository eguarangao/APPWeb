package com.example.appw.Interfaz;

import com.example.appw.Modelo.LibrosData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UteqLibros {
    @GET("/ws/issues.php?j_id=1")
    Call< List<LibrosData> > getLibros();
}
