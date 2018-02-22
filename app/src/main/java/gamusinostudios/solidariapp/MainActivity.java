package gamusinostudios.solidariapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 777;

    String name;
    String email;
    String img_url;
    String dataDesada;
    int anuncisVistos;
    boolean exists;

    FragmentManager fragmentManager=getSupportFragmentManager();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_generador:
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new Fragment01()).commit();
                    return true;
                case R.id.navigation_inici:
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new Fragment02()).commit();
                    return true;
                case R.id.navigation_estadistiques:
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new Fragment03()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_inici );

        //Publicidad!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //Publicidad!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        FloatingActionButton fab = findViewById(R.id.share);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Acción del boton compartir
                CompartirAPP();
            }
        });

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();

        SharedPreferences prefs = getSharedPreferences("SolidariAPP", MODE_PRIVATE);
        String firstLogin = prefs.getString("email", null);
        if (firstLogin == null){
            exists = false;
            signIn();
        }else{
            exists = true;
        }


    }

    public void CompartirAPP() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Col·labora! (Aquí posarem el link de google play");
        try {
            startActivity(Intent.createChooser(intent, "Compartir APP"));
        } catch (android.content.ActivityNotFoundException ex) {
            //do something else
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("MIAPP", connectionResult.getErrorMessage());
    }

    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    private void handleResult(GoogleSignInResult result){

        SharedPreferences.Editor editor = getSharedPreferences("SolidariAPP", MODE_PRIVATE).edit();
        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            name = account.getDisplayName();
            email = account.getEmail();
            if (account.getPhotoUrl() != null){
                img_url=account.getPhotoUrl().toString();

            }else{
                img_url = null;
            }

            editor.putBoolean("login", true);
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("pic", img_url);
            editor.apply();
            Toast.makeText(this, R.string.load, Toast.LENGTH_SHORT).show();

            if (!exists) {
                //Action 1 = inserir usuari a la base de dades
                new Insertar(MainActivity.this, 1).execute();
            }
        }
        else{
            Toast.makeText(this, R.string.loadFail, Toast.LENGTH_SHORT).show();
            editor.putBoolean("login", false);
            editor.apply();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==REQ_CODE)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Metodes per registrar l'usuari a la base de dades
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Inserim l'usuari al mysql
    private boolean insertarUsuari(){
        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpPost httpPost;
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost("http://35.177.198.220/solidariapp/scripts/RegistrarUsuari.php");//url del servidor
        //empezamos añadir nuestros datos
        nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("email",email));
        nameValuePairs.add(new BasicNameValuePair("nom",name));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpClient.execute(httpPost);
            return true;


        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e){
            e.printStackTrace();

        }catch (IOException e){
            e.printStackTrace();
        }
        return  false;
    }

    //desem anuncis vistos al mysql
    private boolean desarAnuncis(){

        //Carreguem les dades desades al telèfon
        SharedPreferences prefs = this.getSharedPreferences("SolidariAPP", MODE_PRIVATE);
        anuncisVistos = prefs.getInt("anuncisVistos",0);
        dataDesada = prefs.getString("date", null);

        if (dataDesada == null) dataDesada = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpPost httpPost;
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost("http://35.177.198.220/solidariapp/scripts/desarAnuncis.php");//url del servidor
        //empezamos añadir nuestros datos
        nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("email",email));
        nameValuePairs.add(new BasicNameValuePair("date",dataDesada));
        nameValuePairs.add(new BasicNameValuePair("anuncisVistos",Integer.toString(anuncisVistos)));//hem de pasar el numero a string per poderlo passar amb aquest metode...
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpClient.execute(httpPost);
            return true;


        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e){
            e.printStackTrace();

        }catch (IOException e){
            e.printStackTrace();
        }
        return  false;
    }
    //AsyncTask para insertar Personas
    class Insertar extends AsyncTask<String,String,String> {

        private Activity context;
        private int action;
        //L'int action s'utilitza per escollir una accio:
        //accio 1 = inserir usuari
        //accio 2 = actualitzar anuncis/dia de l'usuari

        Insertar(Activity context, int action){
            this.context=context;
            this.action=action;
        }

        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            switch (action){
                case 1:
                    //inserim usuari al mysql
                    if(insertarUsuari())
                        context.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Toast.makeText(context, "Gràcies per formar part d\'aquest projecte", Toast.LENGTH_LONG).show();
                            }
                        });
                    else
                        context.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Toast.makeText(context, "Hi ha hagut un error al inserir l\'usuari a la base de dades", Toast.LENGTH_LONG).show();
                            }
                        });
                    break;
                case 2:
                    //actualizem els anuncis vistos, al mysql
                    if(desarAnuncis())
                        context.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Toast.makeText(context, "Fins aviat!", Toast.LENGTH_LONG).show();
                            }
                        });
                    else
                        context.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Toast.makeText(context, "Hi ha hagut un error inesperat al desar les dades.", Toast.LENGTH_LONG).show();
                            }
                        });
                    break;
                default:
                    break;
            }
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Metodes per desar els anuncis visualitzats al MySQL
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        new MainActivity.Insertar(MainActivity.this, 2).execute();
        super.onStop();
    }
}
