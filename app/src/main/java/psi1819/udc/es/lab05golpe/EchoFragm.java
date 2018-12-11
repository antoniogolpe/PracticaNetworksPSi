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
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class EchoFragm extends Fragment {
    String TAG = "Servidor Echo";

    private EditText et_name;
    private EditText et_host;
    private EditText et_port;
    private EditText et_msg;
    private Button bt_send;
    private Button bt_cancel;
    private ListView lv_display;
    private ArrayAdapter adapter;
    private ArrayList<String> strings = new ArrayList<String>();
    private MyAsyncTask myAsyncTask;

    public EchoFragm() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_echo, container, false);

        et_name = (EditText) view.findViewById(R.id.et_name);
        et_host = (EditText) view.findViewById(R.id.et_host);
        et_port = (EditText) view.findViewById(R.id.et_port);
        et_msg = (EditText) view.findViewById(R.id.et_msg);
        bt_send = (Button) view.findViewById(R.id.but_send);
        bt_cancel = (Button) view.findViewById(R.id.but_cancel);
        lv_display = (ListView) view.findViewById(R.id.lv_display);

        adapter = new ArrayAdapter<String>(getContext().getApplicationContext(),android.R.layout.simple_list_item_1, strings);

        lv_display.setAdapter(adapter);

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name.getText().toString().trim().length()==0){
                    Toast.makeText(getContext(),"El campo Nname no puede quedar vacio",Toast.LENGTH_SHORT).show();
                }else if (et_host.getText().toString().trim().length()==0){
                    Toast.makeText(getContext(),"El campo Host no puede quedar vacio",Toast.LENGTH_SHORT).show();
                }else if (et_port.getText().toString().trim().length()==0){
                    Toast.makeText(getContext(),"El campo Port no puede quedar vacio",Toast.LENGTH_SHORT).show();
                }else {
                    et_name.setEnabled(false);
                    et_host.setEnabled(false);
                    et_port.setEnabled(false);
                    myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute(et_name.getText().toString(), et_host.getText().toString(), et_port.getText().toString());
                }

            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_name.setEnabled(true);
                et_host.setEnabled(true);
                et_port.setEnabled(true);
                adapter.clear();
                adapter.notifyDataSetChanged();
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
            Log.d("Async Task ","inicio");

            StringBuffer data = new StringBuffer();
            String rawData;
            try {
                int port = Integer.valueOf(params[2]);
                Socket socket; // parameters host and port

                try {
                    socket = new Socket(params[1], port);
                    OutputStream out = socket.getOutputStream();
                    PrintWriter pw = new PrintWriter(out, true); // set to true for autoflush
                    pw.println(params[0]); // It can be a http command: GET /index.html
                    InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    socket.setSoTimeout(3000);
                    while ( (rawData = br.readLine()) != null) { // blocking method
                        data.append(rawData);
                    }

                    socket.close(); // close socket after each use it is easy but not efficient !!
                } catch (IOException e) {
                    e.printStackTrace();
                }
                s = data.toString();
                return s;
            }catch (Exception ex){
                return data.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Async Task ","finalizado");
            strings.add(s);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
