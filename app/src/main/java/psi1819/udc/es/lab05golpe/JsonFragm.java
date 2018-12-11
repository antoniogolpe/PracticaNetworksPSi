package psi1819.udc.es.lab05golpe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class JsonFragm extends Fragment {

    private EditText et_url;
    private Button but_json;
    private ListView list;
    private ArrayAdapter adapter;
    private ArrayList<String> strings = new ArrayList<String>();

    public JsonFragm() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_json, container, false);

        et_url = (EditText) view.findViewById(R.id.et_url);
        but_json = (Button) view.findViewById(R.id.but_json);
        list = (ListView) view.findViewById(R.id.lv_json);

        adapter = new ArrayAdapter<String>(getContext().getApplicationContext(),android.R.layout.simple_list_item_1, strings);

        list.setAdapter(adapter);

        but_json.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_url.getText().toString().trim().length()==0){
                    Toast.makeText(getContext(),"El campo URL no puede quedar vacio",Toast.LENGTH_SHORT).show();
                }else{
                    new MyAsyncTask().execute();
                }
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private List<String> JSONManager (String resp) {
        List<String> result = new ArrayList<String>();
        try {
            JSONObject object = (JSONObject) new JSONTokener(resp)
                    .nextValue();
            JSONObject object1 = object.getJSONObject("posiciones");
            JSONArray earthquakes = object1.getJSONArray("posicion");
            for (int i = 0; i < earthquakes.length(); i++) {
                JSONObject tmp = (JSONObject) earthquakes.get(i);
                result.add("idlinea:"+tmp.get("idlinea")+" idtrayecto:"+tmp.get("idtrayecto")
                        +" idautobus:" +tmp.getString("idautobus")+" horaactualizacion:"+tmp.get("horaactualizacion")
                        +" fechaactualizacion:"+tmp.get("fechaactualizacion")+" idparada:"+tmp.get("idparada")
                        +" minutos:" +tmp.getString("minutos")+" distancia:"+tmp.get("distancia")
                        +" matricula:" +tmp.getString("matricula")+" modelo:"+tmp.get("modelo"));
            }
        } catch (JSONException e) {
            e.printStackTrace(); }
        return result;
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    //Clase privada AsyncTask
    private class MyAsyncTask extends AsyncTask<String, Void, String> {
        String s=null;
        List<String> stringList = new ArrayList<String>();

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConn=null;
            try{
                URL url = new URL(et_url.getText().toString());
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(urlConn.getInputStream());
                    // Acciones a realizar con el flujo de datos
                    s = readStream(in);
                    in.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                urlConn.disconnect();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Async Task ","finalizado");
            stringList=JSONManager(s);
            adapter.clear();

            for(int i=0 ; i<stringList.size() ; i++) {
                strings.add(stringList.get(i));
            }
            adapter.notifyDataSetChanged();

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
