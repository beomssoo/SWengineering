package com.example.mp01;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mp01.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;

/*
 * 190507 윤승기릿
 * 변경사항 :
 * parsing할 사이트를 naver shopping이 아닌, 그냥 naver에서 해 봄(반만 됨)
 * 복잡한 것을 피하려고 notification, timer는 지웠음.
 * 얻은것 : list형태로 나오는 아이템은 잘 나옴. 단, query가 정확할수록 좋은 값을 얻게됨. 이건 아가리를 잘 털어야겠다.
 * */

public class MainActivity extends AppCompatActivity {

    //XML UI 선언
    EditText input;
    Button button;
    TextView webParsingOutput;
    TextView webParsingOutput2;

    TextView debug;
    TextView jisu;

    



    String HTMLPageURL ="https://search.naver.com/search.naver?query=";
    String HTMLContentInStringFormat="";
    String temperature="";
    String small="";
    String humidity="";
    String verysmall="";
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
        jisu=findViewById(R.id.jisu);


        //debugUI
        debug.setText("onCreate()");

        webParsingOutput.setMovementMethod(new ScrollingMovementMethod());
        jisu.setMovementMethod(new ScrollingMovementMethod());
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
                String sample3="";

                String userQuery = input.getText().toString(); // 내가 검색창에 "성남날씨"라고 치면, 스트링으로 바꿔서 userQuery에 할당
                HTMLPageURL+=userQuery; // HTMLPagURL과 concatenation
                //Log.d("tag","JsoupAsyncTask.doInBackground()-HTMLPageURL(modified) : "+HTMLPageURL);
                //이곳에 debug.setText(HTMLPageURL) 넣으면 펑! 하고 터져버림
                Document doc = Jsoup.connect(HTMLPageURL).get(); // 성남날씨가포함된 url를 connect

                //여기가 리스트뷰li.on.now dd.weather_item._dotWrapper
                Elements temp= doc.select("div.info_data p.info_temperature span.todaytemp"); // 그 doc에서 해당 html태그 titles에 할당
                Elements temp2 = doc.select("dd.lv2 span.num"); // 미세먼지
                Elements temp3 = doc.select("li.on.now dd.weather_item._dotWrapper");//현재습도
                String r=temp.text();
                int c=Integer.parseInt(r);


                for(Element e: temp) {
                    sample = e.text();
                    temperature = userQuery + " : " + sample + "℃";
                }

                for(Element e: temp2){
                    if(temp2.equals("미세먼지")) {
                        sample2 = e.text();
                        small = "미세먼지 : " + sample2;
                    }
                }

                for(Element e: temp3){
                    sample3 = e.text();
                    humidity = "현재 습도 : " + sample3;
                }


            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
            HTMLContentInStringFormat = temperature+"\n"+small+"\n"+humidity;
            webParsingOutput.setText(HTMLContentInStringFormat);
            HTMLPageURL = "https://search.naver.com/search.naver?query=";
            HTMLContentInStringFormat="";
           // jisu=9/5*temp-0.55(1-sample3/100)((9/5)*sample-26)+32;





        }


    }
}