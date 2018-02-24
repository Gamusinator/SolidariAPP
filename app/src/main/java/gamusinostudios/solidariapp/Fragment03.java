package gamusinostudios.solidariapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment01.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment01#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment03 extends Fragment {

    String[] resultatsEstadistics;
    String email;
    String respostadelscullons;

    TextView totalAnuncis, totalPersonal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment03, container, false);
        // Inflate the layout for this fragment

        SharedPreferences prefs = getActivity().getSharedPreferences("SolidariAPP", MODE_PRIVATE);
        email = prefs.getString("email", null);

        totalAnuncis = v.findViewById(R.id.TextViewTotalAnuncis);
        totalPersonal = v.findViewById(R.id.TextViewTotalPersonal);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            carregarValors();
            totalAnuncis.setText(resultatsEstadistics[0]);
            totalPersonal.setText(resultatsEstadistics[1]);
        }

    }

    private void carregarValors() {

        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpPost httpPost;
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost("http://35.177.198.220/solidariapp/scripts/estadistiques.php");//url del servidor
        //empezamos añadir nuestros datos
        nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("email", email));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            respostadelscullons = result.toString();
            resultatsEstadistics = respostadelscullons.split(",");


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
