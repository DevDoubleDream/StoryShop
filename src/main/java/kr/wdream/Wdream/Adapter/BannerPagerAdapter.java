package kr.wdream.Wdream.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kr.wdream.storyshop.R;

/**
 * Created by deobeuldeulim on 2017. 2. 21..
 */

public class BannerPagerAdapter extends PagerAdapter {

    private LayoutInflater inflater;

    public BannerPagerAdapter(Context context){
        super();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = inflater.inflate(R.layout.pager_banner, null);
        ImageView imgBanner = (ImageView)v.findViewById(R.id.imgBanner);
        if (position == 0) {
            imgBanner.setImageResource(R.drawable.test_banner);
        } else if (position == 1) {

        }

        return v;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
