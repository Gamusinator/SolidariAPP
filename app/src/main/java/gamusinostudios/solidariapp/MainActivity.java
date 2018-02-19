package gamusinostudios.solidariapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentContainer;
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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 777;

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

        signIn();
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
            String name = account.getDisplayName();
            String email = account.getEmail();
            String img_url;
            if (account.getPhotoUrl() != null){
                img_url=account.getPhotoUrl().toString();

            }else{
                img_url = null;
            }
            editor.putBoolean("login", true);
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("pic", img_url);
            Toast.makeText(this, "Dades de perfil carregades correctament", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "No s'ha pogut accedir al prefil de Google", Toast.LENGTH_SHORT).show();
            editor.putBoolean("login", false);
        }
        //tant si conecta, com si no, desem els canvis al shared preferences
        editor.apply();
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

}
