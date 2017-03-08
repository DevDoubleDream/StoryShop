package kr.wdream.Wdream.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kr.wdream.Wdream.Cell.BasketProduct;
import kr.wdream.storyshop.R;

/**
 * Created by parksangeun on 2017. 3. 4..
 */

public class BasketAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private ArrayList<BasketProduct> arrayProduct;

    private ViewHolder holder;

    private int productNumber;
    private int productPrice;
    private int totalPrice;

    public BasketAdapter(Context context, ArrayList<BasketProduct> arrayProduct){
        this.arrayProduct = arrayProduct;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

            convertView = inflater.inflate(R.layout.item_basketproduct, null);

            holder.imgProduct = (ImageView)convertView.findViewById(R.id.imgProduct);
            holder.txtProductTitle = (TextView)convertView.findViewById(R.id.txtTitle);
            holder.txtProductPrice = (TextView)convertView.findViewById(R.id.txtPrice);
            holder.btnMinus         = (ImageView)convertView.findViewById(R.id.btnMinus);
            holder.edtCount         = (EditText)convertView.findViewById(R.id.edtCount);
            holder.btnPlus          = (ImageView)convertView.findViewById(R.id.btnPlus);
            holder.btnChange        = (Button)convertView.findViewById(R.id.btnChange);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        BasketProduct product = arrayProduct.get(position);

        new GetImageTask(holder.imgProduct, product.getProductImagePath());

        productNumber   = Integer.parseInt(product.getProductNumber());
        productPrice    = Integer.parseInt(product.getProductPrice());
        totalPrice      = productNumber * productPrice;

        holder.txtProductTitle.setText(product.getProductTitle());
        holder.txtProductPrice.setText(String.valueOf(totalPrice));
        holder.edtCount.setText(product.getProductNumber());



        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productNumber == 1) {

                } else {
                    productNumber--;
                    holder.edtCount.setText(String.valueOf(productNumber));

                    totalPrice = productPrice * productNumber;
                    holder.txtProductPrice.setText(String.valueOf(totalPrice));
                }
            }
        });
        holder.btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 장바구니 변경 ! API 태우는 곳
            }
        });
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productNumber == 99) {

                } else {
                    productNumber++;
                    holder.edtCount.setText(String.valueOf(productNumber));

                    totalPrice = productPrice * productNumber;
                    holder.txtProductPrice.setText(String.valueOf(totalPrice));
                }
            }
        });


        return convertView;
    }

    public class ViewHolder{
        ImageView imgProduct;
        TextView txtProductTitle;
        TextView txtProductPrice;

        ImageView btnMinus;
        EditText edtCount;
        ImageView btnPlus;
        Button btnChange;
    }

}

class GetImageTask extends AsyncTask{
    private ImageView imgView;
    private String imgPath;

    private Bitmap bm;

    public GetImageTask(ImageView imgView, String imgPath){

        this.imgView = imgView;
        this.imgPath = imgPath;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            URL url = new URL(imgPath);

            InputStream inputStream = url.openStream();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            bm = BitmapFactory.decodeStream(inputStream, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (bm != null) {
            imgView.setImageBitmap(bm);
        } else {
            imgView.setImageResource(R.drawable.m_i_shop_finish);
        }

    }
}
