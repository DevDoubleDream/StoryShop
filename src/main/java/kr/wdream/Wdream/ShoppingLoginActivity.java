package kr.wdream.Wdream;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import kr.wdream.Wdream.Models.ConstantModel;
import kr.wdream.Wdream.Models.LoginUser;
import kr.wdream.Wdream.Task.ShoppingLoginTask;
import kr.wdream.Wdream.common.PxToDp;
import kr.wdream.storyshop.R;
import kr.wdream.ui.Components.LayoutHelper;

/**
 * Created by deobeuldeulim on 2017. 3. 7..
 */

public class ShoppingLoginActivity extends Activity implements View.OnClickListener {

    //private static font
    private static final int TEXT_TITLE_FONT_SIZE = 17;
    private static final int TEXT_USERNAME_FONT_SIZE = 16;
    private static final int TEXT_PASSWORD_FONT_SIZE = 16;
    private static final int TEXT_BUTTON_FONT_SIZE = 18;
    private static final int TEXT_AUTOLOGIN_FONT_SIZE = 14;
    private static final int TEXT_MAX_LINE = 1;

    private static final int LYT_NAVI_HEIGHT = 55;
    private static final int EDT_USERNAME_LEFT = 16;
    private static final int EDT_USERNAME_TOP = 66;
    private static final int EDT_USERNAME_RIGHT = 16;
    private static final int EDT_USERNAME_HEIGHT = 48;
    private static final int EDT_PASSWORD_LEFT = 16;
    private static final int EDT_PASSWORD_TOP = 12;
    private static final int EDT_PASSWORD_RIGHT = 16;
    private static final int EDT_PASSWORD_HEIGHT = 48;
    private static final int BTN_LOGIN_HEIGHT = 50;
    private static final int BTN_LOGIN_LEFT = 16;
    private static final int BTN_LOGIN_TOP = 14;
    private static final int BTN_LOGIN_RIGHT = 16;
    private static final int BTN_AUTO_LEFT = 16;
    private static final int BTN_AUTO_TOP = 13;
    private static final int BTN_BACK_WIDTH = 32;
    private static final int BTN_FINISH_WIDTH = 32;
    private static final int IMG_AUTO_WIDTH = 18;
    private static final int IMG_AUTO_HEIGHT = 18;
    private static final int IMG_BACK_WIDTH = 16;
    private static final int IMG_BACK_HEIGHT = 16;
    private static final int IMG_FINISH_WIDTH = 15;
    private static final int IMG_FINISH_HEIGHT = 16;
    private static final int TXT_AUTO_LEFT = 10;

    //UI
    private LinearLayout    lytRoot;
    private LinearLayout    btnAutoLogin;
    private RelativeLayout  lytNavigation;
    private RelativeLayout  btnBack;
    private RelativeLayout  btnFinish;
    private TextView        txtNaviTitle;
    private TextView        txtAutoCheck;
    private ImageView       imgFinish;
    private ImageView       imgAutoCheck;
    private ImageView       imgBack;
    private EditText        edtUsername;
    private EditText        edtPassword;
    private Button          btnLogin;

    //Properties
    private boolean isAuto = false;
    private HashMap<String,String> resultLogin = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        setContentView(lytRoot);

    }

    private void initView(){
        lytRoot       = new LinearLayout(this);
        lytNavigation = new RelativeLayout(this);
        btnAutoLogin  = new LinearLayout(this);
        btnBack       = new RelativeLayout(this);
        btnFinish     = new RelativeLayout(this);
        imgBack       = new ImageView(this);
        imgFinish     = new ImageView(this);
        imgAutoCheck  = new ImageView(this);
        txtNaviTitle  = new TextView(this);
        txtAutoCheck  = new TextView(this);
        edtPassword   = new EditText(this);
        edtUsername   = new EditText(this);
        btnLogin      = new Button(this);

        btnAutoLogin.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtUsername.setTextColor(Color.BLACK);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lytRoot.setOrientation(LinearLayout.VERTICAL);
        lytRoot.setBackgroundColor(Color.parseColor("#EEEEEE"));

        lytNavigation.setBackgroundResource(R.drawable.m_i_detail_text_background_g);

        txtNaviTitle.setTextSize(TEXT_TITLE_FONT_SIZE);
        txtNaviTitle.setTextColor(Color.parseColor("#333333"));
        txtNaviTitle.setText("로그인");
        txtAutoCheck.setTextSize(TEXT_AUTOLOGIN_FONT_SIZE);
        txtAutoCheck.setTextColor(Color.parseColor("#666666"));
        txtAutoCheck.setText("자동 로그인");

        imgBack.setImageResource(R.drawable.m_i_login_back);
        imgFinish.setImageResource(R.drawable.m_i_login_finish);
        imgAutoCheck.setBackgroundResource(R.drawable.m_i_login_edt_background);

        edtUsername.setHint("아이디");
        edtUsername.setInputType(InputType.TYPE_CLASS_TEXT);
        edtUsername.setTextSize(TEXT_USERNAME_FONT_SIZE);
        edtUsername.setPadding(PxToDp.dpToPx(EDT_USERNAME_LEFT), 0, PxToDp.dpToPx(EDT_USERNAME_RIGHT), 0);
        edtUsername.setMaxLines(TEXT_MAX_LINE);
        edtUsername.setTextColor(Color.BLACK);
        edtUsername.setBackgroundResource(R.drawable.m_i_login_edt_background);
        edtUsername.setGravity(Gravity.CENTER_VERTICAL);

        edtPassword.setHint("비밀번호");
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtPassword.setTextSize(TEXT_PASSWORD_FONT_SIZE);
        edtPassword.setPadding(PxToDp.dpToPx(EDT_PASSWORD_LEFT), 0, PxToDp.dpToPx(EDT_PASSWORD_RIGHT), 0);
        edtPassword.setMaxLines(TEXT_MAX_LINE);
        edtPassword.setTextColor(Color.BLACK);
        edtPassword.setBackgroundResource(R.drawable.m_i_login_edt_background);
        edtPassword.setGravity(Gravity.CENTER_VERTICAL);

        btnBack.setGravity(Gravity.CENTER_VERTICAL);
        btnBack.addView(imgBack, LayoutHelper.createRelative(IMG_BACK_WIDTH, IMG_BACK_HEIGHT, RelativeLayout.ALIGN_PARENT_RIGHT));
        btnFinish.addView(imgFinish, LayoutHelper.createRelative(IMG_FINISH_WIDTH, IMG_FINISH_HEIGHT, RelativeLayout.CENTER_VERTICAL));
        btnLogin.setText("로그인");
        btnLogin.setTextSize(TEXT_BUTTON_FONT_SIZE);
        btnLogin.setTextColor(Color.WHITE);
        btnLogin.setBackgroundColor(Color.parseColor("#00CCE9"));
        btnAutoLogin.setOrientation(LinearLayout.HORIZONTAL);
        btnAutoLogin.addView(imgAutoCheck, LayoutHelper.createLinear(IMG_AUTO_WIDTH, IMG_AUTO_HEIGHT));
        btnAutoLogin.addView(txtAutoCheck, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        lytNavigation.addView(btnBack, LayoutHelper.createRelative(BTN_BACK_WIDTH, LayoutHelper.MATCH_PARENT, RelativeLayout.ALIGN_PARENT_LEFT));
        lytNavigation.addView(txtNaviTitle, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, RelativeLayout.CENTER_IN_PARENT));
        lytNavigation.addView(btnFinish, LayoutHelper.createRelative(BTN_FINISH_WIDTH, LayoutHelper.MATCH_PARENT, RelativeLayout.ALIGN_PARENT_RIGHT));

        // View Added to RootLayout
        lytRoot.addView(lytNavigation, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LYT_NAVI_HEIGHT));
        lytRoot.addView(edtUsername, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, EDT_USERNAME_HEIGHT, EDT_USERNAME_LEFT, EDT_USERNAME_TOP, EDT_USERNAME_RIGHT, 0));
        lytRoot.addView(edtPassword, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, EDT_PASSWORD_HEIGHT, EDT_PASSWORD_LEFT, EDT_PASSWORD_TOP, EDT_PASSWORD_RIGHT, 0));
        lytRoot.addView(btnLogin, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, BTN_LOGIN_HEIGHT, BTN_LOGIN_LEFT, BTN_LOGIN_TOP, BTN_LOGIN_RIGHT, 0));
        lytRoot.addView(btnAutoLogin, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, BTN_AUTO_LEFT, BTN_AUTO_TOP, 0, 0));
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = null;
            if (msg.getData() != null) {
                bundle = msg.getData();
                resultLogin = (HashMap<String,String>) bundle.getSerializable("resultLogin");
            }
            switch(msg.what){
                case ConstantModel.LOGIN_SHOP_SUCCESS:
                    Log.d("상은", "resultLogin : " +resultLogin);

                    String userId = resultLogin.get("userId");
                    String userCode = resultLogin.get("userCode");
                    String userName = resultLogin.get("userName");
                    String rgstViz = resultLogin.get("rgstViz");
                    String vizAt   = resultLogin.get("vizAt");

                    if (isAuto) {
                        LoginUser.setAutoLoginUser(ShoppingLoginActivity.this, userId, userCode, userName, rgstViz, vizAt);
                        Toast.makeText(ShoppingLoginActivity.this, resultLogin.get("msg"), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        LoginUser.setLoginUser(userId,userCode, userName, rgstViz, vizAt);
                        Toast.makeText(ShoppingLoginActivity.this, resultLogin.get("msg"), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    break;

                case ConstantModel.LOGIN_SHOP_FAILED:
                    edtUsername.setTextColor(Color.RED);
                    Toast.makeText(ShoppingLoginActivity.this, resultLogin.get("msg"), Toast.LENGTH_SHORT).show();
                        break;

                case ConstantModel.LOGIN_SHOP_ERROR:
                    edtUsername.setTextColor(Color.RED);
                    Toast.makeText(ShoppingLoginActivity.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    @Override
    public void onClick(View v){

        if (v == btnBack || v == btnFinish) {
            finish();
        }

        if (v == btnAutoLogin) {
            if (isAuto) {
                isAuto = false;
                imgAutoCheck.setImageBitmap(null);
            } else {
                isAuto = true;
                imgAutoCheck.setImageResource(R.drawable.m_i_login_auto);
            }
        }

        if (v == btnLogin) {
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();
            new ShoppingLoginTask(username, password, handler).execute();
        }
    }
}
