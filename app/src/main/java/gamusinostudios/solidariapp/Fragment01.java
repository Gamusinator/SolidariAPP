package gamusinostudios.solidariapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;


/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment01.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment01#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment01 extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    View vista;
    private LinearLayout Prof_Section;
    private Button SignOut;
    private SignInButton SignIn;
    private TextView Name, Email;
    private ImageView Prof_Pic;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment01, container, false);
        vista = v;

        Prof_Section = v.findViewById(R.id.perfil);
        SignOut = v.findViewById(R.id.btnLogOff);
        SignIn = v.findViewById(R.id.btnLogOn);
        Name = v.findViewById(R.id.nomPerfil);
        Email = v.findViewById(R.id.correuPerfil);
        Prof_Pic = v.findViewById(R.id.fotoPerfil);

        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);

        //amaguem el Layout amb les dades de l'usuari mentre no estigui connectat.
        Prof_Section.setVisibility(v.GONE);



        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onClick(View vi) {

        switch (vi.getId())
        {
            case R.id.btnLogOn:
                signIn();
                break;
            case R.id.btnLogOff:
                signOut();
                break;
            case R.id.btnPubli:
                //obrir publicitat
                break;
            default:
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn(){
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(getContext()).enableAutoManage(getActivity(),this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                update(false);
            }
        });
    }

    private void handleResult(GoogleSignInResult result){

        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            if (account.getPhotoUrl() != null){
                String img_url=account.getPhotoUrl().toString();
                Glide.with(this).load(img_url).into(Prof_Pic);
            }else{
                Prof_Pic.setImageResource(R.mipmap.ic_launcher);
            }
            Name.setText(name);
            Email.setText(email);

            update(true);
        }
        else{
            update(false);
        }
    }

    private void update(boolean isLogin){

        if (isLogin){
            Prof_Section.setVisibility(vista.VISIBLE);
            SignIn.setVisibility(vista.GONE);
        }else{
            Prof_Section.setVisibility(vista.GONE);
            SignIn.setVisibility(vista.VISIBLE);
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
}
