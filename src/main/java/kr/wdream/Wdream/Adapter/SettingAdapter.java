package kr.wdream.Wdream.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by deobeuldeulim on 2016. 12. 5..
 */

public class SettingAdapter extends BaseAdapter {
    private static final String LOG_TAG = "SettingAdapter";

    private Context context;
    private LayoutInflater inflater;
    private int layout;

    private String[] titleArray;

    private String[] descArray;

    private Drawable[] resources;


    public SettingAdapter(Context context, int layout){
        this.context = context;
        this.layout = layout;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        titleArray = new String[]{  context.getResources().getString(kr.wdream.storyshop.R.string.myaccount).toString(),
                context.getResources().getString(kr.wdream.storyshop.R.string.shoppingmall).toString(),
                context.getResources().getString(kr.wdream.storyshop.R.string.notification).toString(),
                context.getResources().getString(kr.wdream.storyshop.R.string.center).toString(),
                context.getResources().getString(kr.wdream.storyshop.R.string.version).toString(),
                context.getResources().getString(kr.wdream.storyshop.R.string.unsign).toString()};

        descArray = new String[] {  context.getResources().getString(kr.wdream.storyshop.R.string.myaccount_description).toString(),
                context.getResources().getString(kr.wdream.storyshop.R.string.shop_description).toString(),
                context.getResources().getString(kr.wdream.storyshop.R.string.noti_description).toString(),
                context.getResources().getString(kr.wdream.storyshop.R.string.center_description).toString(),
                context.getResources().getString(kr.wdream.storyshop.R.string.version_description).toString(),
                context.getResources().getString(kr.wdream.storyshop.R.string.unsign_description).toString()};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resources = new Drawable[]{ context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_account, null),
                    context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_shop, null),
                    context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_noti, null),
                    context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_center, null),
                    context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_version, null),
                    context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_dis, null)};
        }else{
            resources = new Drawable[]{ context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_account),
                    context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_shop),
                    context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_noti),
                    context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_center),
                    context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_version),
                    context.getResources().getDrawable(kr.wdream.storyshop.R.drawable.m_i_main_dis),};
        }

    }
    @Override
    public int getCount() {
        return titleArray.length;
    }

    @Override
    public Object getItem(int position) {
        return titleArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null)
            convertView = inflater.inflate(layout, parent, false);

        holder = new ViewHolder();

        holder.txtTitle = (TextView) convertView.findViewById(kr.wdream.storyshop.R.id.txtTitle);
        holder.txtDescription = (TextView) convertView.findViewById(kr.wdream.storyshop.R.id.txtDescription);
        holder.imgIcon = (ImageView) convertView.findViewById(kr.wdream.storyshop.R.id.imgIcon);

        holder.txtTitle.setText(titleArray[position]);
        holder.txtDescription.setText(descArray[position]);
        holder.imgIcon.setImageDrawable(resources[position]);

        return convertView;

    }

    private class ViewHolder{
        TextView txtTitle;
        TextView txtDescription;
        ImageView imgIcon;
    }
}
