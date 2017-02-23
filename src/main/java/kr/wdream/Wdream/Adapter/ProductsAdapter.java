package kr.wdream.Wdream.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.wdream.Wdream.Models.Product;
import kr.wdream.storyshop.R;

/**
 * Created by deobeuldeulim on 2017. 2. 23..
 */

public class ProductsAdapter extends BaseAdapter {

    private ArrayList<Product> arrayProduct = new ArrayList<Product>();
    private Context context;
    private LayoutInflater inflater;

    public ProductsAdapter(Context context, ArrayList<Product> arrayProduct){
        this.context      = context;
        this.arrayProduct = arrayProduct;
        inflater          = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder{
        ImageView imgProduct;
        TextView  txtTitle;
        TextView  txtPrice;
    }

    @Override
    public int getCount() {
        return arrayProduct.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayProduct.get(position);
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

            convertView = inflater.inflate(R.layout.item_product, null);

            holder.imgProduct = (ImageView) convertView.findViewById(R.id.imgProduct);
            holder.txtTitle   = (TextView)  convertView.findViewById(R.id.txtTitle);
            holder.txtPrice   = (TextView)  convertView.findViewById(R.id.txtPrice);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Product product = arrayProduct.get(position);

        return convertView;
    }
}
