package kr.wdream.Wdream;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by deobeuldeulim on 2016. 12. 7..
 */

public class ShoppingDialog extends Dialog implements View.OnClickListener {

    private static final String LOG_TAG = "ShoppingDialog";

    private Button btnCancel, btnSubmit;
    private EditText edtId, edtPw;
    private LinearLayout lytEdit;
    private TextView txtDisMessage, txtTitle;

    private boolean connect;
    private String id;
    private String pw;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public ShoppingDialog(Context context) {
        super(context);
    }

    public ShoppingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ShoppingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public ShoppingDialog(Context context, boolean connect){
        super(context);

        sp = context.getSharedPreferences("connectMall", Context.MODE_PRIVATE);
        this.connect = connect;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(kr.wdream.storyshop.R.layout.dialog_shopping);

        btnCancel = (Button)findViewById(kr.wdream.storyshop.R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        btnSubmit = (Button)findViewById(kr.wdream.storyshop.R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        edtId = (EditText)findViewById(kr.wdream.storyshop.R.id.edtId);
        edtPw = (EditText)findViewById(kr.wdream.storyshop.R.id.edtPw);

        txtTitle = (TextView)findViewById(kr.wdream.storyshop.R.id.txtTitle);
        txtDisMessage = (TextView)findViewById(kr.wdream.storyshop.R.id.txtDisMessage);
        lytEdit = (LinearLayout)findViewById(kr.wdream.storyshop.R.id.lytEditText);

        if (connect) {
            txtTitle.setText(getContext().getString(kr.wdream.storyshop.R.string.connectTrueTitle));
            lytEdit.setVisibility(View.GONE);
            txtDisMessage.setText(getContext().getString(kr.wdream.storyshop.R.string.connectTrueMessage));
        }else{
            txtTitle.setText(getContext().getString(kr.wdream.storyshop.R.string.connectFalseTitle));
            txtDisMessage.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            dismiss();
        }

        if (v == btnSubmit) {
            editor = sp.edit();
            if (connect) {
                editor.putString("id", "");
                editor.putString("pw", "");
                editor.putBoolean("connecting", false);

                editor.commit();

                dismiss();
            }else{
                id = edtId.getText().toString();
                pw = edtPw.getText().toString();

                editor.putString("id", id);
                editor.putString("pw", pw);
                editor.putBoolean("connecting", true);

                editor.commit();

                dismiss();
            }
        }
    }
}
