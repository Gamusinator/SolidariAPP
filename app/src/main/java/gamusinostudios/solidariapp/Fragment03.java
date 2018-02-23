package gamusinostudios.solidariapp;

import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

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

    TextView totalAnuncis, totalPersonal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment03, container, false);
        // Inflate the layout for this fragment

        SharedPreferences prefs = getActivity().getSharedPreferences("SolidariAPP", MODE_PRIVATE);
        email = prefs.getString("email", null);


        String txt = post();

        totalAnuncis = v.findViewById(R.id.TextViewTotalAnuncis);
        totalPersonal = v.findViewById(R.id.TextViewTotalPersonal);

        totalAnuncis.setText(txt);
        //totalPersonal.setText(resultatsEstadistics[2]);

        return v;
    }

//    public void carregarValors(View v) {
//        RequestQueue queue = Volley.newRequestQueue(v.getContext());
//        String URL = "http://35.177.198.220/solidariapp/scripts/estadistiques.php";
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                String string = response;
//                resultatsEstadistics = string.split(","); //Aquí tenemos la array cargada con los nombres de fichero
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //Respuesta incorrecta
//            }
//        });
//        queue.add(stringRequest);
//    }

    public String post() {
        String URL = "http://35.177.198.220/solidariapp/scripts/estadistiques.php";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse resp = httpclient.execute(httppost);
            HttpEntity ent = resp.getEntity();/*y obtenemos una respuesta*/

            String text = EntityUtils.toString(ent);

            return text;


        } catch (Exception e) {
            return "error";
        }
    }

}
