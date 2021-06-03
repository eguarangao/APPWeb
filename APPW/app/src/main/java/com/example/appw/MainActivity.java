package com.example.appw;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.appw.Interfaz.UteqLibros;
import com.example.appw.Modelo.LibrosData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
Spinner sp;
TextView MultiLineText;
RequestQueue rq;
private String URL = "https://revistas.uteq.edu.ec/ws/issues.php?j_id=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MultiLineText = findViewById(R.id.MultiLinetext);
        MultiLineText.setMovementMethod(new ScrollingMovementMethod());

        //instanciamos el Spiner
        sp = findViewById(R.id.spinner);

        //instanciamos el RequestQueue
        rq = Volley.newRequestQueue(this);

        ArrayAdapter<CharSequence> lista =
                ArrayAdapter.createFromResource(this
                        , R.array.opcionesWS
                        , android.R.layout.simple_spinner_item);
        sp.setAdapter(lista);
    }

    private void getVolleLibros(){
        JsonArrayRequest dataVolley = new JsonArrayRequest(
                Request.Method.GET,URL,null, response -> {
                    int longitud = response.length();
                    for(int i = 0; i < longitud; i++)
                    {
                        try {
                            JSONObject data = new JSONObject(response.get(i).toString());
                            SpannableString myTextissueId = new SpannableString("issueId: " + data.get("issue_id")+"\n");
                            SpannableString myTextVolumen = new SpannableString("volumen: " + data.get("volume")+"\n\n");
                            SpannableString myTextNumber = new SpannableString("number: " + data.get("number") + "\n");
                            SpannableString myTextYear = new SpannableString("year: " + data.get("year") + "\n");
                            SpannableString myTextDatePublished = new SpannableString("date_published: " + data.get("date_published") + "\n");
                            SpannableString myTextTitle = new SpannableString("title: " + data.get("title") + "\n");
                            SpannableString myTextdoi = new SpannableString("doi: " + data.get("doi") + "\n");
                            SpannableString myTextCover = new SpannableString("cover: " + data.get("cover") + "\n\n");

                            MultiLineText.append( myTextissueId);
                            MultiLineText.append( myTextVolumen);
                            MultiLineText.append( myTextNumber);
                            MultiLineText.append(  myTextYear);
                            MultiLineText.append( myTextDatePublished);
                            MultiLineText.append( myTextTitle);
                            MultiLineText.append( myTextdoi);
                            MultiLineText.append( myTextCover);
                        }
                        catch (JSONException e) {
                            String msj = "Mensaje de error: " + e.getMessage();
                            MultiLineText.setText(msj);
                        }
                    }
                },
                error -> {
                    String msj = "Mensaje de error por Voley: " + error.getMessage();
                    MultiLineText.setText(msj);
                }
        );
        rq.add(dataVolley);
    }

    private void getRetrofitLibro(){
        //Implementamos retrofit y añadimos el convertidor
        Retrofit retrofit = new  Retrofit.Builder().baseUrl("https://revistas.uteq.edu.ec/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
         //Se aplica la interfaz
        UteqLibros LibrosUTEQ_R =  retrofit.create(UteqLibros.class);
        //Lista
        Call<List<LibrosData>> call = LibrosUTEQ_R.getLibros();

         call.enqueue(new Callback<List<LibrosData>>() {
            @Override
            public void onResponse(Call<List<LibrosData>> call, Response<List<LibrosData>> response) {
                if(!response.isSuccessful())
                {
                    MultiLineText.setText("" + response.code());
                    return;
                }
                //add lista
                List<LibrosData> LibrosList = response.body();
                //recorremos
               for (LibrosData data: LibrosList)
                {
                    SpannableString myTextID = new SpannableString("issueID: " + data.getIssueId() + "\n");
                    SpannableString myTextVolume = new SpannableString("volumen: " + data.getVolume() + "\n");
                    SpannableString myTextNumber = new SpannableString("number: " + data.getNumber() + "\n");
                    SpannableString myTextYear = new SpannableString("year: " + data.getYear() + "\n");
                    SpannableString myTextDatePublished = new SpannableString("date_published: " + data.getDatePublished() + "\n");
                    SpannableString myTextTitle = new SpannableString("title: " + data.getTitle() + "\n");
                    SpannableString myTextdoi = new SpannableString("doi: " + data.getDoi() + "\n");
                    SpannableString myTextCover = new SpannableString("cover: " + data.getCover() + "\n\n");
                    //asignamos al MultiLine
                    MultiLineText.append( myTextID);
                    MultiLineText.append( myTextVolume);
                    MultiLineText.append( myTextNumber);
                    MultiLineText.append(  myTextYear);
                    MultiLineText.append( myTextDatePublished);
                    MultiLineText.append( myTextTitle);
                    MultiLineText.append( myTextdoi);
                    MultiLineText.append( myTextCover);
                }
            }

            @Override
            public void onFailure(Call<List<LibrosData>> call, Throwable thr) {
                //Si se presenta algun error mostrar
                String msj = "Error: " + thr.getMessage();
                MultiLineText.setText(msj);
            }
        });
    }

    public void btnAceptar_Click(View view)
    {
        MultiLineText.setText("");
        if(sp.getSelectedItem().toString().toUpperCase().equals("Retrofit".toUpperCase()))
        {
            Toast.makeText(this, "Cargando.....", Toast.LENGTH_SHORT).show();
            getRetrofitLibro();
        }

        else if(sp.getSelectedItem().toString().toUpperCase().equals("Volley".toUpperCase()))
        {
            Toast.makeText(this, "Cargando.....", Toast.LENGTH_SHORT).show();
            getVolleLibros();
        }
        else
        {
            MultiLineText.setText("");
            Toast.makeText(sp.getContext(), "Seleccionar una librería", Toast.LENGTH_SHORT).show();
        }

    }

}