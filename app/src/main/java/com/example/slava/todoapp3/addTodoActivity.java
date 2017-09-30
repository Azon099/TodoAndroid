package com.example.slava.todoapp3;


import android.content.Context;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;



public class addTodoActivity extends AppCompatActivity {
    Spinner spinner;
    EditText todoText;
    String mainURL = "http://secondtodoapp.herokuapp.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        //Плохой, но рабочий вариант.
        ArrayList<String> Projects = getIntent().getStringArrayListExtra("Projects");
        todoText = (EditText) findViewById(R.id.textTodo);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Projects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.projects);
        spinner.setAdapter(adapter);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MyFont.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }
//Как и в прошлый раз, не без магии, но тут она явно рангом ниже
    public void acceptTodo(View v){
        Integer index= spinner.getSelectedItemPosition();
        String url = mainURL+"/todos/add/";
        String query = Uri.encode(String.valueOf(index + 1) + "|" + todoText.getText());
        downloadUrl(url + query);
        List<String> str = new ArrayList<String>();
        str.add(index.toString());
        str.add(todoText.getText().toString());
        Intent intent = new Intent();
        intent.putExtra("newTodo", (ArrayList<String>) str);
        setResult(RESULT_OK, intent);
        //tv.setText("Done");
    }

    private void downloadUrl(final String urlString) {
        new Thread(new Runnable()
        {
            public void run()
            {
                final List<String> addressList = getTextFromWeb(urlString); // format your URL
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //update ui
                        finish();
                    }
                });
            }
        }).start();
    }

    public List<String> getTextFromWeb(String urlString)
    {

        HttpClient Client = new DefaultHttpClient();
        try
        {
            String SetServerString = "";
            HttpGet httpget = new HttpGet(urlString);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            SetServerString = Client.execute(httpget, responseHandler);
      //      tv.setText(SetServerString);
        }
        catch(Exception ex)
        {
           // tv.setText(ex.getMessage());
        }
        return null;
    }

    public void cancelTodoCreating(View v){
        finish();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }
}