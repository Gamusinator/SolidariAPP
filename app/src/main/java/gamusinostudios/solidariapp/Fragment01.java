package gamusinostudios.solidariapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import static android.content.Context.MODE_PRIVATE;


/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment01.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment01#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment01 extends Fragment implements View.OnClickListener{

    private LinearLayout Prof_Section;
    private TextView Name, Email;
    private ImageView Prof_Pic;
    private boolean isLogin;
    String name;
    String email;
    String img_url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment01, container, false);

        Prof_Section = v.findViewById(R.id.perfil);
        Name = v.findViewById(R.id.nomPerfil);
        Email = v.findViewById(R.id.correuPerfil);
        Prof_Pic = v.findViewById(R.id.fotoPerfil);

        SharedPreferences prefs = getActivity().getSharedPreferences("SolidariAPP", MODE_PRIVATE);
        name = prefs.getString("name", null);
        email = prefs.getString("email", null);
        img_url = prefs.getString("pic", null);
        isLogin = prefs.getBoolean("login", false);

        Prof_Section.setVisibility(View.GONE);

        update(isLogin);
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onClick(View vi) {

        switch (vi.getId())
        {
            case R.id.btnPubli:
                //obrir publicitat
                break;
            default:
                break;
        }
    }


    private void update(boolean isLogin){

        if (isLogin){
            Prof_Section.setVisibility(View.VISIBLE);
            Name.setText(name);
            Email.setText(email);
            if (img_url == null){
                Prof_Pic.setImageResource(R.mipmap.ic_launcher_round);
            }else {
                Glide.with(this).load(img_url).into(Prof_Pic);
            }
        }else{
            Prof_Section.setVisibility(View.GONE);
        }
    }
}
