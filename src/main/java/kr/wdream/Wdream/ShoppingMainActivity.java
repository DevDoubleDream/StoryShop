package kr.wdream.Wdream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * Created by deobeuldeulim on 2017. 2. 22..
 */

public class ShoppingMainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView();
    }

    public class MainLayout extends View{
        public MainLayout(Context context){
            super(context);;
        }
    }
}
