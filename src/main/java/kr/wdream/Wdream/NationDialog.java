package kr.wdream.Wdream;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by deobeuldeulim on 2016. 12. 1..
 */

public class NationDialog extends Dialog {

    private TextView txtTitle;
    private ListView list;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    private boolean trans;
    private String title;

    private String[][] nations = {
            {"한국","Korea","kr","m_b_chat_korea"},
            {"일본","Japan","jp","m_b_chat_japan"},
            {"중국(간체)","China","cn1","m_b_chat_china"},
            {"중국(번체)","Taiwan","cn2","m_b_chat_tiwan"},
            {"인도(힌디어)","India","in","m_b_chat_indo"},
            {"인도네시아","Indonesia","id","m_b_chat_id"},
            {"사우디아라비아","Saudi Arabia","sa","m_b_chat_saudi"},
            {"터키","Turkey","tr","m_b_chat_turkey"},
            {"프랑스","French","fr","m_b_chat_france"},
            {"독일","Germany","de","m_b_chat_de"},
            {"이탈리아","Italy","it","m_b_chat_itali"},
            {"영국","United Kingdom","gb","m_b_chat_gb"},
            {"러시아","Russian","ru","m_b_chat_rusia"},
            {"캐나다","Canada","ca","m_b_chat_canada"},
            {"멕시코","Mexico","mx","m_b_chat_maxico"},
            {"미국","USA","us","m_b_chat_usa"},
            {"아르헨티나","Argentina","ar","m_b_chat_ar"},
            {"브라질","Brasil","br","m_b_chat_brazil"},
            {"남아프리카 공화국","South Africa","za","m_b_chat_sa"},
            {"오스트레일리아","Australia","au","m_b_chat_au"}};

    private Handler handler;

    public NationDialog(Context context) {
        super(context);
    }

    public NationDialog(Context context, boolean trans, Handler handler){
        super(context);

        this.handler = handler;
        this.trans = trans;

        if(trans){
            title = "사용하는 언어를 선택하세요.";
        }else{
            title = "번역할 언어를 선택하세요.";
        }
    }

    public NationDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected NationDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(kr.wdream.storyshop.R.layout.dialog_nation);

        arrayList = new ArrayList<String>();

        for (int i=0; i<nations.length; i++){
            arrayList.add(nations[i][0]);
        }

        txtTitle = (TextView)findViewById(kr.wdream.storyshop.R.id.txtTitle);
        txtTitle.setText(title);
        list = (ListView)findViewById(kr.wdream.storyshop.R.id.list);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, arrayList);
        list.setAdapter(adapter);

        final Message msg = new Message();
        final Bundle bundle = new Bundle();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(trans){
                    msg.what = 1000;
                    bundle.putString("nation", nations[position][2]);
                    bundle.putString("flag", nations[position][3]);
                    msg.setData(bundle);

                    handler.sendMessage(msg);

                    dismiss();
                }else{
                    msg.what = 2000;
                    bundle.putString("nation", nations[position][2]);
                    bundle.putString("flag", nations[position][3]);
                    msg.setData(bundle);

                    handler.sendMessage(msg);

                    dismiss();
                }
            }
        });
    }
}
