package kr.wdream.Wdream.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.wdream.Wdream.Cell.ContentsCell;
import kr.wdream.storyshop.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by deobeuldeulim on 2016. 12. 1..
 */

public class ContentsAdapter extends BaseAdapter {
    private static final String LOG_TAG = "ContentsAdapter";

    private Context context;
    private int layout;
    private List<ContentsCell> list;
    private LayoutInflater inflater;

    private String MallDomain = "http://www.ktopshop.co.kr/";
    private String locationURL = "";

    public ContentsAdapter(Context context, int layout, List<ContentsCell> list){
        this.context = context;
        this.layout = layout;
        this.list = list;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder{
        LinearLayout button;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null)
            convertView = inflater.inflate(layout, parent, false);

        holder = new ViewHolder();
        holder.button = (LinearLayout) convertView.findViewById(R.id.lyt_btn_prdt);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationURL = MallDomain + list.get(position).getPrdt_path();

                gotoWeb(locationURL);
            }
        });

        URL url = null;

        try {
            url = new URL(list.get(position).getMdl_img_path());

            InputStream is = url.openStream();

            final Bitmap bm = BitmapFactory.decodeStream(is);

            ((ImageView) convertView.findViewById(R.id.img_prdt)).setImageBitmap(bm);
            ((TextView) convertView.findViewById(R.id.txt_prdt_title)).setText(list.get(position).getPrdt_nm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void gotoWeb(String locationURL){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationURL));
        context.startActivity(intent);
    }
}
