package gamusinostudios.solidariapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static android.content.Context.MODE_PRIVATE;


/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment01.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment01#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment01 extends Fragment implements View.OnClickListener, RewardedVideoAdListener {

    private ConstraintLayout Prof_Section, LayoutPubli, LayoutErrorConn;
    private TextView Name, Email, AnuncisVistos;
    private ImageView Prof_Pic;
    private boolean isLogin;
    private ImageButton mostrarAnuncis;
    String name,email,img_url,dataDesada;
    int sumaAnuncis;

    private RewardedVideoAd mRewardedVideoAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment01, container, false);
        MobileAds.initialize(getContext(),
                "ca-app-pub-6311459401782434~5408360072");

        Prof_Section = v.findViewById(R.id.perfil);
        LayoutPubli = v.findViewById(R.id.LayourPubli);
        LayoutErrorConn = v.findViewById(R.id.LayoutErrorConn);
        Name = v.findViewById(R.id.nomPerfil);
        Email = v.findViewById(R.id.correuPerfil);
        Prof_Pic = v.findViewById(R.id.fotoPerfil);
        AnuncisVistos = v.findViewById(R.id.anuncisVistos);
        mostrarAnuncis = v.findViewById(R.id.btnPubli);

        //Carreguem les dades desades al telèfon
        SharedPreferences prefs = getActivity().getSharedPreferences("SolidariAPP", MODE_PRIVATE);
        name = prefs.getString("name", null);
        email = prefs.getString("email", null);
        img_url = prefs.getString("pic", null);
        isLogin = prefs.getBoolean("login", false);
        dataDesada = prefs.getString("date", null);
        sumaAnuncis = prefs.getInt("anuncisVistos",0);


        Prof_Section.setVisibility(View.GONE);
        LayoutPubli.setVisibility(View.GONE);
        LayoutErrorConn.setVisibility(View.VISIBLE);

        update(isLogin);
        mostrarAnuncis.setOnClickListener(this);



        //Anuncis
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        loadRewardedVideoAd();
        comprovarCanviDia();
        actualitzarTextViewAnuncis();


        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onClick(View vi) {

        switch (vi.getId())
        {
            case R.id.btnPubli:
                //obrir publicitat
                startVideoAd();
                break;
            default:
                break;
        }
    }


    private void update(boolean isLogin){

        if (isLogin){
            Prof_Section.setVisibility(View.VISIBLE);
            LayoutPubli.setVisibility(View.VISIBLE);
            LayoutErrorConn.setVisibility(View.GONE);
            Name.setText(name);
            Email.setText(email);
            if (img_url == null){
                Prof_Pic.setImageResource(R.mipmap.user);

            }else {
                Glide.with(this).load(img_url).into(Prof_Pic);
            }
        }else{
            Prof_Section.setVisibility(View.GONE);
            LayoutPubli.setVisibility(View.GONE);
            LayoutErrorConn.setVisibility(View.VISIBLE);
        }
    }

    //Funcions per la publicitat recompensada


    private void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded())
        mRewardedVideoAd.loadAd("ca-app-pub-6311459401782434/7682614713",
                new AdRequest.Builder().build());
    }

    public void startVideoAd(){
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }else{
            Toast.makeText(getContext(), R.string.loadingAd, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        //l'usuari obté la recommpensa (+1 anunci vist)

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("SolidariAPP", MODE_PRIVATE).edit();

        sumaAnuncis++;
        //desem el valor dels anuncis vistos al shared preferences
        editor.putInt("anuncisVistos", sumaAnuncis);
        editor.apply();
        actualitzarTextViewAnuncis();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(getContext());
        super.onPause();
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(getContext());
        super.onResume();
    }

    @Override
    public void onDestroy() {
        //no destruim la publicitat perquè sino, no es torna a mostrar fins ke reiniciem l'aplicacio
        //mRewardedVideoAd.destroy(getContext());
        super.onDestroy();
    }

    public void actualitzarTextViewAnuncis(){
        String num = String.valueOf(sumaAnuncis);
        AnuncisVistos.setText(num);
    }

    public void comprovarCanviDia(){

        String dataAvui = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        if (!dataAvui.equalsIgnoreCase(dataDesada)){
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("SolidariAPP", MODE_PRIVATE).edit();
            sumaAnuncis = 0;
            editor.putString("date", dataAvui);
            editor.putInt("anuncisVistos", sumaAnuncis);
            editor.apply();
        }
    }
}
