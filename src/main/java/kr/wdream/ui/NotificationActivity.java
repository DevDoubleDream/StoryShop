package kr.wdream.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import kr.wdream.Wdream.Cell.NotiVO;
import kr.wdream.Wdream.Util.NotiUtil;
import kr.wdream.storyshop.LocaleController;
import kr.wdream.storyshop.R;
import kr.wdream.ui.ActionBar.ActionBar;
import kr.wdream.ui.ActionBar.BaseFragment;
import kr.wdream.ui.Components.LayoutHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationActivity extends BaseFragment{

    private static final String LOG_TAG = "NotificationActivity";

    private ArrayList<NotiVO> resultNoti;
    private NotiAdapter adapter;
    private ListView listView;
    @Override
    public View createView(Context context) {
        resultNoti = new ArrayList<NotiVO>();

        new NotiGetTask(context).execute();


        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Notification", R.string.notification));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });


        FrameLayout frameLayout = new FrameLayout(context);
        fragmentView = frameLayout;
        fragmentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //        ((Frame) fragmentView).setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        listView = new ListView(context);
        listView.setAdapter(adapter);

        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        return fragmentView;
    }


    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {

    }

    public class NotiGetTask extends AsyncTask{
        private ProgressDialog prog;
        private Context context;

        public NotiGetTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            prog = new ProgressDialog(context);
            prog.setTitle("공지사항");
            prog.setMessage("공지사항을 가져오고 있습니다. 잠시만 기다려주세요");
            prog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            HashMap<String,String> paramNoti = new HashMap<String,String>();
            paramNoti.put("cmd", "/notice/process.json");
            paramNoti.put("app_id", "toptalk");
            paramNoti.put("mode", "L");

            try {
                resultNoti = NotiUtil.getNotiList(paramNoti);
                adapter = new NotiAdapter(context, resultNoti);
                Log.d(LOG_TAG, "adapter Count : " + adapter.getCount());

                handler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            prog.dismiss();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    };

    public class NotiAdapter extends BaseAdapter{
        private ArrayList<NotiVO> resultNoti;
        private LayoutInflater layoutInflater;


        public NotiAdapter(Context context, ArrayList<NotiVO> resultNoti){
            this.resultNoti = resultNoti;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return resultNoti.size();
        }

        @Override
        public Object getItem(int position) {
            return resultNoti.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_noti, null);

                holder.textTitle = (TextView) convertView.findViewById(R.id.txtNotiTitle);
                holder.textDate = (TextView) convertView.findViewById(R.id.txtNotiDate);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            NotiVO data = resultNoti.get(position);

            holder.textTitle.setText(data.getSubject());

            String date = data.getUpdate_dt().substring(0,10);

            date = date.replaceAll("-", ". ");

            holder.textDate.setText(date);

            return convertView;
        }

        public class ViewHolder{
            TextView textTitle;
            TextView textDate;
        }
    }
}