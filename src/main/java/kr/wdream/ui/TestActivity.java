package kr.wdream.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import kr.wdream.storyshop.LocaleController;
import kr.wdream.tgnet.TLRPC;
import kr.wdream.ui.Adapters.BaseSectionsAdapter;
import kr.wdream.ui.Adapters.ContactsAdapter;
import kr.wdream.ui.Components.LetterSectionsListView;

import java.util.HashMap;

/**
 * Created by deobeuldeulim on 2016. 11. 28..
 */

public class TestActivity extends Activity implements View.OnClickListener{
    private static final String LOG_TAG = "TestActivity";

    private Button tab1, tab2, tab3, tab4;

    private LinearLayout lytTab1, lytTab2, lytTab3, lytTab4;

    private ListView list;
    private LetterSectionsListView letterListView;

    private BaseSectionsAdapter listViewAdapter;

    private boolean onlyUsers;
    private boolean needPhonebook;
    private HashMap<Integer, TLRPC.User> ignoreUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(kr.wdream.storyshop.R.layout.activity_test);

        initView();

    }

    private void initView(){
        tab1 = (Button)findViewById(kr.wdream.storyshop.R.id.tab1);
        tab1.setOnClickListener(this);
        tab2 = (Button)findViewById(kr.wdream.storyshop.R.id.tab2);
        tab2.setOnClickListener(this);
        tab3 = (Button)findViewById(kr.wdream.storyshop.R.id.tab3);
        tab3.setOnClickListener(this);
        tab4 = (Button)findViewById(kr.wdream.storyshop.R.id.tab4);
        tab4.setOnClickListener(this);

        lytTab1 = (LinearLayout)findViewById(kr.wdream.storyshop.R.id.lytTab1);
        lytTab2 = (LinearLayout)findViewById(kr.wdream.storyshop.R.id.lytTab2);
        lytTab3 = (LinearLayout)findViewById(kr.wdream.storyshop.R.id.lytTab3);
        lytTab4 = (LinearLayout)findViewById(kr.wdream.storyshop.R.id.lytTab4);

        list = (ListView)findViewById(kr.wdream.storyshop.R.id.list);
        listViewAdapter = new ContactsAdapter(this, 0, false, null, false);

        list.setAdapter(listViewAdapter);
        list.setVisibility(View.GONE);
        list.setFastScrollEnabled(true);
        list.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        list.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);

        letterListView = (LetterSectionsListView)findViewById(kr.wdream.storyshop.R.id.letterListView);
        letterListView.setAdapter(listViewAdapter);

        letterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(LOG_TAG, "onClick");

                TLRPC.User user = (TLRPC.User) listViewAdapter.getItem(position);
                if(user==null)
                    return;

                Bundle bundle = new Bundle();
                bundle.putInt("user_id", user.id);

                Intent intent = new Intent(TestActivity.this, LaunchActivity.class);
                intent.putExtra("TestActivity", bundle);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == tab1) {
            lytTab1.setVisibility(View.VISIBLE);
            lytTab2.setVisibility(View.GONE);
            lytTab3.setVisibility(View.GONE);
            lytTab4.setVisibility(View.GONE);
        }
        if (v == tab2) {
            lytTab1.setVisibility(View.GONE);
            lytTab2.setVisibility(View.VISIBLE);
            lytTab3.setVisibility(View.GONE);
            lytTab4.setVisibility(View.GONE);
        }
        if (v == tab3) {
            lytTab1.setVisibility(View.GONE);
            lytTab2.setVisibility(View.GONE);
            lytTab3.setVisibility(View.VISIBLE);
            lytTab4.setVisibility(View.GONE);
        }
        if (v == tab4) {
            lytTab1.setVisibility(View.GONE);
            lytTab2.setVisibility(View.GONE);
            lytTab3.setVisibility(View.GONE);
            lytTab4.setVisibility(View.VISIBLE);
        }
    }
}
