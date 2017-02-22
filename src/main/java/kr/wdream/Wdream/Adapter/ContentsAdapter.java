package kr.wdream.Wdream.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.wdream.storyshop.R;

/**
 * Created by deobeuldeulim on 2016. 12. 1..
 */

public class ContentsAdapter extends BaseAdapter {

    private String[] btnList = {"쇼핑", "포인트", "가맹점", "센터"};

    private LayoutInflater inflater;


    public ContentsAdapter(Context context){
        super();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return btnList.length;
    }

    @Override
    public Object getItem(int position) {
        return btnList[position];
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

                convertView = inflater.inflate(R.layout.item_contents, null);

                holder.btnMenu = (LinearLayout)convertView.findViewById(R.id.btnMenu);
                holder.imgMenuIcon = (ImageView) convertView.findViewById(R.id.imgMenuButton);
                holder.txtMenuTitle = (TextView) convertView.findViewById(R.id.txtMenuTitle);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.txtMenuTitle.setText(btnList[position]);

            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        return convertView;
    }

    private class ViewHolder{
        LinearLayout btnMenu;

        ImageView imgMenuIcon;
        TextView txtMenuTitle;
    }
}
