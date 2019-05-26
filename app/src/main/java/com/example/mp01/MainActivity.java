package com.example.mp01;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    //XML UI 선언
    EditText input;
    Button button;
    TextView webParsingOutput;
    TextView webParsingOutput2;

    TextView debug;

    



    String HTMLPageURL ="https://search.naver.com/search.naver?query=";
    String HTMLContentInStringFormat="";
    String temperature="";
    String small="";
    PendingIntent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //XML Inflation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI 끌당
        input = findViewById(R.id.input);
        webParsingOutput = findViewById(R.id.output);
        webParsingOutput2 = findViewById(R.id.output2);
        button = findViewById(R.id.button);
        debug =findViewById(R.id.debug);

        //debugUI
        debug.setText("onCreate()");

        webParsingOutput.setMovementMethod(new ScrollingMovementMethod());
        intent = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debug.setText("webParsing.onClick()");
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });
    }

    private class JsoupAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute(){
            debug.setText("JsoupAsyncTask.onPreExecute()");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params){
            debug.setText("JsoupAsyncTask.doInBackground()");
            try{

                String sample="";
                String sample2="";
                String userQuery = input.getText().toString();
                String userQuery2 = input.getText().toString();
                // 내가 검색창에 "성남날씨"라고 치면, 스트링으로 바꿔서 userQuery에 할당
                HTMLPageURL+=userQuery; // HTMLPagURL과 concatenation
                //Log.d("tag","JsoupAsyncTask.doInBackground()-HTMLPageURL(modified) : "+HTMLPageURL);
                //이곳에 debug.setText(HTMLPageURL) 넣으면 펑! 하고 터져버림
                Document doc = Jsoup.connect(HTMLPageURL).get(); // 성남날씨가포함된 url를 connect

                //여기가 리스트뷰
                Elements temp= doc.select("span.todaytemp"); // 그 doc에서 해당 html태그 titles에 할당
                Elements a = doc.select("dd.lv2 span.num");

                for(Element e: temp) {
                    sample = e.text();
                    temperature = userQuery + " : " + sample + "℃";
                }

                for(Element s: a){
                    sample2 = s.text();
                    small = userQuery2 + " : " + sample2;
                }

            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected  void onPostExecute(Void result){
            HTMLContentInStringFormat = temperature+"\n"+small;
            webParsingOutput.setText(HTMLContentInStringFormat);
            HTMLPageURL = "https://search.naver.com/search.naver?query=";
            HTMLContentInStringFormat="";

        }
    }
}