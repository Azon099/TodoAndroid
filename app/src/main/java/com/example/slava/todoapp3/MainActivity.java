package com.example.slava.todoapp3;
/*
Крайне недоволен чистотой кода и решениями примененными тут, к моменту завершения(30.09.2017 01:06) в голове уже рой идей по оптимизации.
Но не сегодня...
TODO:
    * Переписать ListView и Adapter на базе класса Page
    * Собрать 3 класса в 1(стоит ли?)
    * Очень много неприятных мелочей
    * Улучшить работоспособность запросов

И вообще, извинения за исполнение некоторых задач я оставил в адаптере
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    ListView lvMain;
    private CustomAdapter mAdapter;
    public Page mainPage = new Page();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MyFont.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        startRefresh();
        FloatingActionButton ff = (FloatingActionButton) findViewById(R.id.refresh);
        ff.setOnClickListener(new View.OnClickListener() {//Кнопка обновления всего и всея
            @Override
            public void onClick(View view) {
                reloadAll();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addTodo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Добавление новой задачи
                List<String> Projects = new ArrayList<String>();// = new ArrayList<String>;
                for (int i = 0; i < mainPage.Projects.size(); i++) {
                    Project proj = mainPage.Projects.get(i);
                    Projects.add(proj.title);
                }
                Intent intent = new Intent(MainActivity.this, addTodoActivity.class);
                intent.putExtra("Projects", (ArrayList<String>) Projects);
                startActivityForResult(intent,1);
            }
        });
    }

    public void reloadAll(){
        mainPage.clearAll();
        mAdapter.clearAdapter();
        lvMain.setAdapter(mAdapter);
        res = null;
        startRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        startRefresh();
    }

    JsonObject res;
    public void startRefresh(){//Тут магия *_*
        Ion.with(this)
                .load("http://10.0.2.2:3000/todo_controller/mobileAppGet")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result != null) {
                            res = result;
                            parseJson();
                        }else{
                        //    tv.setText(e.getMessage());
                        }
                    }
                });

    }

    public void parseJson(){//Это однозначно одна из величайших ошибок допущенных мною, но оно работает.
        //Не лучшее решение но сил уюе нету ....
        JsonArray projects = res.getAsJsonArray("Projects");
           mainPage.clearAll();
            for (int i = 0; i < projects.size(); i++) {
                //tv.setText(Test);
                Project proj = new Project();
                JsonElement ProjectElement = projects.get(i);
                JsonObject ProjectObject = ProjectElement.getAsJsonObject();
                JsonElement title = ProjectObject.get("title");
                JsonElement id = ProjectObject.get("id");
                proj.title = title.getAsString();
                proj.id = id.getAsInt();
                JsonArray Todos = ProjectObject.getAsJsonArray("Todos");
                for (int j = 0; j < Todos.size(); j++) {
                    Todo todo = new Todo();
                    JsonObject todoElement = Todos.get(j).getAsJsonObject();
                    JsonElement text = todoElement.get("text");
                    JsonElement isCompleted = todoElement.get("isCompleted");
                    JsonElement project_id = todoElement.get("project_id");
                    JsonElement todo_id = todoElement.get("id");
                    todo.id = todo_id.getAsInt();
                    todo.text = text.getAsString();
                    todo.project_id = project_id.getAsInt();
                    todo.isCompleted = isCompleted.getAsBoolean();
                    proj.todos.add(todo);
                }
                mainPage.Projects.add(proj);
            }
        //tv.setText("GOODJOB");
        refreshList();
    }
    public void checkBoxClick(View view){//Тонна быдлокода
        //tv.setText("YYYYYYYEEEEAHHHHH");
        View row = (View) view.getParent();
        CustomAdapter.ViewHolder vHold = (CustomAdapter.ViewHolder) row.getTag();
        if(vHold.checkBox.isChecked()){
            vHold.textView.setPaintFlags(vHold.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            vHold.textView.setPaintFlags(0);
        }

        String rowText = vHold.textView.getText().toString();
        String url = "http://10.0.2.2:3000/todos/";
        String query = Uri.encode(String.valueOf(mainPage.getTodoId(rowText)) + "|" + vHold.checkBox.isChecked());
        downloadUrl(url + query);
    }

    private void downloadUrl(final String urlString) {//Еще немного магии :)
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
           // tv.setText(SetServerString);
        }
        catch(Exception ex)
        {
           // tv.setText(ex.getMessage());
        }
        return null;
    }

    public void refreshList(){//Выглядит неплохо, но есть чувство, что можно лучше :(
        if (mAdapter == null){
            mAdapter = new CustomAdapter(this);
        }
        lvMain = (ListView) findViewById(R.id.lvMain);
        for (int i = 0; i < mainPage.Projects.size(); i++){
            Project proj = mainPage.Projects.get(i);
            mAdapter.addSectionHeaderItem(proj.title);
            for (int j = 0; j < proj.todos.size(); j++){
                Todo todo = proj.todos.get(j);
                mAdapter.addItem(todo.text, todo.isCompleted);

            }
        }
        lvMain.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }
}
