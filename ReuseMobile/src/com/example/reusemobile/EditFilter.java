package com.example.reusemobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reusemobile.logging.Sting;

public class EditFilter extends ActionBarActivity {
    private EditText nameEdit;
    private EditText keywordsEdit;
    private String oldName;
    private String oldKeywords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_filter);
        
        Intent intent = getIntent();
        oldName = intent.getStringExtra("name");
        oldKeywords = intent.getStringExtra("keywords");
        
        nameEdit = (EditText) findViewById(R.id.edit_filter_name);
        nameEdit.setText(oldName);
        keywordsEdit = (EditText) findViewById(R.id.edit_filter_keywords);
        keywordsEdit.setText(oldKeywords);
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
        getMenuInflater().inflate(R.menu.edit_filter, menu);
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
    
    public void save(View view) {
        Sting.logButtonPush(this, Sting.SAVE_FILTER);
        String name = nameEdit.getText().toString();
        String[] keywords = keywordsEdit.getText().toString().trim().split(" ");
        if (!name.equals("") && keywords.length > 0) {
            changeFilter(oldName, name, keywords);
            startActivity(new Intent(this, ManageFilters.class));
            finish();
        } else {
            Toast.makeText(this, "Name and keywords must be filled", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void changeFilter(String oldName, String name, String[] keywords) {
        StringBuilder value = new StringBuilder();
        for (String filter : keywords) {
            value.append(filter).append(' ');
        }
        getSharedPreferences(GlobalApplication.filterPreferences, Context.MODE_PRIVATE).edit().remove(oldName).commit();
        getSharedPreferences(GlobalApplication.filterPreferences, Context.MODE_PRIVATE).edit().putString(name, value.toString().trim()).commit();
    }

}
