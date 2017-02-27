package kr.wdream.Wdream.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Models.Product;
import kr.wdream.storyshop.R;

/**
 * Created by deobeuldeulim on 2017. 2. 23..
 */

public class ProductsAdapter extends BaseAdapter {

    private ArrayList<Product> arrayProduct = new ArrayList<Product>();
    private Context context;
    private LayoutInflater inflater;

    private ViewHolder holder;

    private Bitmap bitmap;

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

        URL url = null;
        try {
            url = new URL(ConstantModel.API_IMAGE_URL + product.getStrProductImgPath());


            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; // 메모리 부족을 방지하기 위한 샘플링

            InputStream inputStream = connection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            holder.imgProduct.setImageBitmap(bitmap);

        } catch (Exception e){

        }



        holder.txtTitle.setText(product.getStrProductTitle());
        holder.txtPrice.setText(product.getStrItemMoney());

        return convertView;
    }
}
