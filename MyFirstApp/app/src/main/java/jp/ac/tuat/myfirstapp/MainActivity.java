package jp.ac.tuat.myfirstapp;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.ac.tuat.cs.wifidirectkurogo.WifiDirectManager;
import jp.ac.tuat.cs.wifidirectkurogo.WifiDirectServiceListener;
import jp.ac.tuat.cs.wifidirectkurogo.message.MinimalMessage;
import jp.ac.tuat.cs.wifidirectkurogo.peer.Peer;

public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "jp.ac.tuat.myfirstapp.MESSAGE";
    public static ArrayAdapter<String> adapter;

    private WifiDirectManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = new WifiDirectManager();
        manager.setListener(new WifiDirectServiceListener() {
            EditText editText = (EditText) findViewById(R.id.edit_message);
            @Override
            public void onPeersChanged(List<Peer> peers) {

                int peerSize = peers.size();
                editText.append("Size == " + String.valueOf(peerSize));
            }
            @Override
            public void comein(MinimalMessage arg0) {
                Log.d("SomethingWiFiDirect", "comein");

                //editText.append("comein: "+arg0.describeContents()+" from: "+arg0.getFromString()+",");
                editText.append("goout: "+arg0.describeContents());
            }
            @Override
            public void goout(MinimalMessage arg0) {
                Log.d("SomethingWiFiDirect", "goout");
                //editText.append("goout: "+arg0.describeContents()+" to: "+arg0.getTo()+",");
                editText.append("goout: "+arg0.describeContents());
            }
            @Override
            public void onDataReceived(Serializable object) {
                Log.d("SomethingWiFiDirect", "onDataReceived object = " + object);
                /*
                if(object instanceof RoutePacket){
                    editText.append("rev: "+(((RoutePacket) object).msg)+" ,");
                }
                */
                editText.append("rev: "+object);
                /*
                ListView myListView = (ListView) findViewById(R.id.listView);

                ArrayList<String> myStringArray1 = new ArrayList<String>();
                myStringArray1.add(object.toString());
                adapter = new StableArrayAdapter(MainActivity.this, R.layout.row, myStringArray1);

                myListView.setAdapter(adapter);*/
            }
        });
        manager.bind(getApplicationContext());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void sendMessage(View view){
        /*
    	Intent intent = new Intent(this, SendMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        */
        // test wifi direct
        //EditText dst = (EditText) findViewById(R.id.Dst_field);
         //manager.broadcast(dst.getText().toString());
        //manager.broadcast("helloworld");

        RoutePacket p = new RoutePacket();
        p.msg = "helloworld";

        manager.send(p,"b2:ee:7b:a4:7b:91");


        // be:ee:7b:a4:fb:91 = .1 = ap
        // be:ee:7b:a4:fb:a7 = .57 = client
        // b2:ee:7b:a4:fb:a7
        // b2:ee:7b:a4:7b:91

        /*
        RoutePacket p = new RoutePacket();
        EditText dst = (EditText) findViewById(R.id.Dst_field);
        p.msg = "hoge! ";//dst.getText().toString();
        //String macAddr = "b2ee7ba47b91";//dst.getText().toString();
        String macAddr = "b2:ee:7b:a4:7b:91";
        manager.send(p, macAddr);
        Log.d("sendMessage", "macAddr = "+macAddr);
        Log.d("sendMessage", "p.msg = "+p.msg);
        */
    }

    public void getMessage(View view){
        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        //startActivity(intent);
    }

    public WifiDirectManager getManager() {
        return manager;
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
