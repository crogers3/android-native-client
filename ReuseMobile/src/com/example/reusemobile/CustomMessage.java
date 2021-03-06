package com.example.reusemobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reusemobile.logging.Sting;

public class CustomMessage extends ActionBarActivity {
    private int itemId;
    private EditText customMessage;
    private Activity activity;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_message);
        activity = this;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        
        Intent intent = getIntent();
        itemId = intent.getIntExtra(MainStream.ITEM_ID, -1);
        customMessage = (EditText) findViewById(R.id.custom_message);
        sendButton = (Button) findViewById(R.id.send_custom_message_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Sting.logActivityStart(this);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.custom_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            finish();
            return true;
        case  R.id.action_settings:
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void send(View view) {
        Sting.logButtonPush(this, Sting.SEND_CUSTOM_RESPONSE);
        String message = customMessage.getText().toString();
        if (!message.equals("")) {
            (new SendMessage()).execute(String.valueOf(itemId), message);
        } else {
            Toast.makeText(this, "You must specify items to claim.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_custom_message,
                    container, false);
            return rootView;
        }
    }
    
    private class SendMessage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
         // Create a new HttpClient and Post Header
            String port = GlobalApplication.getServerPort();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://armadillo.xvm.mit.edu:" + port + "/api/thread/claim/");
            String email = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("username", "");
            httppost.addHeader("USERNAME", email);
            String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token", "");
            httppost.addHeader("TOKEN", token);
            String id = params[0];
            String message = params[1];
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("item_id", id.toString()));
                nameValuePairs.add(new BasicNameValuePair("text", message));
                nameValuePairs.add(new BasicNameValuePair("email", "false"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                
                if(response.getStatusLine().getStatusCode() != 200) {
                    Log.e("Item Details", String.valueOf(itemId));
                    Sting.logError(activity, Sting.CUSTOM_RESPONSE_ERROR, response.getStatusLine().getReasonPhrase());
                    return "An error occured in item claim:\n" + response.getStatusLine().getReasonPhrase();
                }
                boolean wasSuccessful = new JSONObject(EntityUtils.toString(response.getEntity())).getBoolean("success");
                if(!wasSuccessful) {
                    Sting.logError(activity, Sting.CUSTOM_RESPONSE_ERROR, "Item Already Claimed");
                    return "Item claim failed";
                }
                
                return null;
            } catch (Exception e) {
                Sting.logError(activity, Sting.CUSTOM_RESPONSE_ERROR, "Exception: " + e.getLocalizedMessage());
                return "An exception occured during item claim:\n" + e.getLocalizedMessage();
            }
        }
        
        @Override
        protected void onPreExecute() {
            sendButton.setText("Sending...");
            sendButton.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result == null) {
                Toast.makeText(getApplicationContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                sendButton.setText(getResources().getText(R.string.custom_message_send_button));
                sendButton.setEnabled(true);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
