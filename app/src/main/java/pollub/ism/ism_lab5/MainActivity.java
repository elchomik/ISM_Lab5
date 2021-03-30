package pollub.ism.ism_lab5;

import androidx.appcompat.app.AppCompatActivity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private  EditText nazwaZapis=null;
    private  EditText notatka=null;
    private Spinner nazwaCzytaj=null;


    private ArrayList<String> nazwaPlikow=null;
    private ArrayAdapter<String> adapterSpinera=null;

    private final String NAZWA_PREFERENCES="Aplikacja do notatek";
    private final String KLUCZ_DO_PREFERENCES="Zapisane nazwy plików";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button zapisz = findViewById(R.id.przyciskZapisz);
        Button odczytaj = findViewById(R.id.przyciskCzytaj);
        nazwaZapis=findViewById(R.id.editTextNazwaZapisz);
        notatka=findViewById(R.id.editTextNotatka);
        nazwaCzytaj=findViewById(R.id.spinnerNazwaCzytaj);

        zapisz.setOnClickListener(v -> zapisanieNotatki());

        odczytaj.setOnClickListener(v-> odczytanieNotatki());
    }

    @Override
    protected void onPause() {
        zapiszSharePreferences();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nazwaPlikow=new ArrayList<>();
        adapterSpinera=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        nazwaCzytaj.setAdapter(adapterSpinera);
        odczytajSharePreferences();
    }

    private void zapisanieNotatki(){
        String nazwaPliku=nazwaZapis.getText().toString(),informacje="Udało się zapisać";

        if(!zapiszDoPliku(nazwaPliku,notatka)){
            informacje="Nie udało się zapisać";
        }
        Toast.makeText(this,informacje,Toast.LENGTH_SHORT).show();

    }

    private  void odczytanieNotatki(){
        String nazwaPliku=nazwaCzytaj.getSelectedItem().toString(),informacje="Udało się przeczytać";
        notatka.getText().clear();

        if(!odczytajZPliku(nazwaPliku,notatka)) informacje="Nie udało się przeczytać";

        Toast.makeText(this,informacje,Toast.LENGTH_SHORT).show();
    }

    private boolean zapiszDoPliku(String nazwaPliku,EditText poleEdycyjne){
        boolean sukces=true;

        File katalog=getApplicationContext().getExternalFilesDir(null);
        File plik=new File(katalog+File.separator+nazwaPliku);
        BufferedWriter zapisywacz=null;
        try{
            zapisywacz=new BufferedWriter(new FileWriter(plik));
            zapisywacz.write(poleEdycyjne.getText().toString());

        }catch (Exception e){
            sukces=false;
        }finally {
            try {
                Objects.requireNonNull(zapisywacz).close();
            }catch (Exception e){
                sukces=false;
            }
        }

        return sukces;
    }

    private boolean odczytajZPliku(String nazwaPliku,EditText poleEdycyjne){
        boolean sukces=true;

        File katalog=getApplicationContext().getExternalFilesDir(null);
        File plik=new File(katalog+File.separator+nazwaPliku);
        BufferedReader odczytywacz=null;

        if(plik.exists()){
            try{
                odczytywacz=new BufferedReader(new FileReader(plik));
                String linia=odczytywacz.readLine()+"\n";
                while (linia!=null){
                    poleEdycyjne.getText().append(linia);
                    linia=odczytywacz.readLine();
                }
            } catch (Exception e) {
                sukces=false;
        }finally {
                if(odczytywacz!=null){
                    try{
                        odczytywacz.close();
                    }catch (Exception e){
                        sukces=false;
                    }
                }
            }
            }
        return sukces;
    }

   private void zapiszSharePreferences(){
       SharedPreferences preferences=getSharedPreferences(NAZWA_PREFERENCES,MODE_PRIVATE);

       SharedPreferences.Editor edytor=preferences.edit();

       edytor.putStringSet(KLUCZ_DO_PREFERENCES, new HashSet<>(nazwaPlikow));

       edytor.apply();

   }

   private void odczytajSharePreferences(){
        SharedPreferences sh=getSharedPreferences(NAZWA_PREFERENCES,MODE_PRIVATE);
       Set<String> zapisaneNazwy=sh.getStringSet(KLUCZ_DO_PREFERENCES,null);

       if (zapisaneNazwy!=null){
           nazwaPlikow.clear();

           nazwaPlikow.addAll(zapisaneNazwy);
           adapterSpinera.notifyDataSetChanged();
       }

   }
}