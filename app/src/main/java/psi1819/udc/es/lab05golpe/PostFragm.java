package psi1819.udc.es.lab05golpe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class PostFragm extends Fragment {

    EditText et_user, et_pass, et_ip;
    Button bt_post;
    TextView tv_result;
    View view;

    public PostFragm() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private String peticionPost(){
        HttpURLConnection urlConn=null;
        String s = null;
        try {

            URL url = new URL(et_ip.getText().toString());
            urlConn = (HttpURLConnection) url.openConnection();
            String postParameters = "username="+ URLEncoder.encode(et_user.getText().toString(),"UTF-8")
                    +"&password="+URLEncoder.encode(et_pass.getText().toString(),"UTF-8");

            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");
            urlConn.setFixedLengthStreamingMode(postParameters.getBytes().length);
            PrintWriter out = new PrintWriter(urlConn.getOutputStream());
            out.print(postParameters);
            out.close();
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConn.getInputStream());
                s=readStream(in);
                in.close();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally { urlConn.disconnect(); }
        return s;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post, container, false);

        et_user = (EditText) view.findViewById(R.id.et_username);
        et_pass = (EditText) view.findViewById(R.id.et_passwd);
        et_ip = (EditText) view.findViewById(R.id.et_ip);
        bt_post = (Button) view.findViewById(R.id.but_post);

        bt_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_user.getText().toString().trim().length()==0){
                    Toast.makeText(getContext(),"El campo Username no puede quedar vacio",Toast.LENGTH_SHORT).show();
                }else if (et_pass.getText().toString().trim().length()==0){
                    Toast.makeText(getContext(),"El campo Password no puede quedar vacio",Toast.LENGTH_SHORT).show();
                }else if (et_ip.getText().toString().trim().length()==0){
                    Toast.makeText(getContext(),"El campo IP no puede quedar vacio",Toast.LENGTH_SHORT).show();
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

    //Clase privada AsyncTask
    private class MyAsyncTask extends AsyncTask<String, Void, String> {
        String s=null;

        @Override
        protected String doInBackground(String... params) {
            tv_result = (TextView) view.findViewById(R.id.tv_result);

             s = peticionPost();

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Async Task ","finalizado");
            String[] textos = s.split("<");
            tv_result.setText(textos[0]);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
