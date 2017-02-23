/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package kr.wdream.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Px;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import kr.wdream.Wdream.Adapter.ContentsAdapter;
import kr.wdream.Wdream.Adapter.SettingAdapter;
import kr.wdream.Wdream.Cell.ContentsCell;
import kr.wdream.Wdream.ShoppingDialog;
import kr.wdream.Wdream.Util.ContentsUtil;
import kr.wdream.Wdream.common.PxToDp;
import kr.wdream.storyshop.AndroidUtilities;
import kr.wdream.storyshop.BuildVars;
import kr.wdream.storyshop.ChatObject;
import kr.wdream.storyshop.DialogObject;
import kr.wdream.storyshop.ImageLoader;
import kr.wdream.storyshop.LocaleController;
import kr.wdream.storyshop.MessageObject;
import kr.wdream.storyshop.R;
import kr.wdream.storyshop.UserObject;
import kr.wdream.storyshop.query.SearchQuery;
import kr.wdream.storyshop.query.StickersQuery;
import kr.wdream.storyshop.support.widget.LinearLayoutManager;
import kr.wdream.storyshop.support.widget.RecyclerView;
import kr.wdream.storyshop.FileLog;
import kr.wdream.tgnet.TLRPC;
import kr.wdream.storyshop.ContactsController;
import kr.wdream.storyshop.MessagesController;
import kr.wdream.storyshop.MessagesStorage;
import kr.wdream.storyshop.NotificationCenter;
import kr.wdream.storyshop.UserConfig;
import kr.wdream.ui.ActionBar.BottomSheet;
import kr.wdream.ui.Adapters.DialogsAdapter;
import kr.wdream.ui.Adapters.DialogsSearchAdapter;
import kr.wdream.ui.Adapters.SearchAdapter;
import kr.wdream.ui.Cells.HintDialogCell;
import kr.wdream.ui.Cells.ProfileSearchCell;
import kr.wdream.ui.Cells.UserCell;
import kr.wdream.ui.Cells.DialogCell;
import kr.wdream.ui.ActionBar.ActionBar;
import kr.wdream.ui.ActionBar.ActionBarMenu;
import kr.wdream.ui.ActionBar.ActionBarMenuItem;
import kr.wdream.ui.ActionBar.BaseFragment;
import kr.wdream.ui.ActionBar.MenuDrawable;
import kr.wdream.ui.Components.LetterSectionsListView;
import kr.wdream.ui.Components.PlayerView;
import kr.wdream.ui.Components.EmptyTextProgressView;
import kr.wdream.ui.Components.LayoutHelper;
import kr.wdream.ui.Components.RecyclerListView;
import kr.wdream.ui.ActionBar.Theme;

import java.util.ArrayList;
import java.util.HashMap;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {

    private static final String LOG_TAG = "DialogsActivity";

    private RecyclerListView listView;
    private LinearLayoutManager layoutManager;
    private DialogsAdapter dialogsAdapter;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private ProgressBar progressView;
    private LinearLayout emptyView;
    private ActionBarMenuItem passcodeItem;
    private ImageView floatingButton;

    private AlertDialog permissionDialog;

    private int prevPosition;
    private int prevTop;
    private boolean scrollUpdated;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

    private boolean checkPermission = true;

    private String selectAlertString;
    private String selectAlertStringGroup;
    private String addToGroupAlertString;
    private int dialogsType;

    public static boolean dialogsLoaded;
    private boolean searching;
    private boolean searchWas;
    private boolean onlySelect;
    private long selectedDialog;
    private String searchString;
    private long openedDialogId;
    private boolean cantSendToChannels;

    private DialogsActivityDelegate delegate;



    // Tab Button 선언
    private LinearLayout tab1, tab2, tab3, tab4;

    // Tab Button 이미지 선언
    private ImageView imgTab1, imgTab2, imgTab3, imgTab4;

    private LetterSectionsListView listContacts;
//    private BaseSectionsAdapter contactsAdapter;
    private Context context;

    private RelativeLayout lytDialogs;

    private boolean clicktab1 = true;
    private boolean clicktab2 = false;
    private boolean clicktab3 = false;
    private boolean clicktab4 = false;

    private LinearLayout contentLayout;


    private SearchAdapter searchAdapter;
    private boolean tab1Searching;

    private LinearLayout settingLayout;
    private LinearLayout.LayoutParams tabParams;

    public boolean connect;
    public SharedPreferences sp;

    //SettingLayout Button 선언
    private LinearLayout btnMyInfo;
    private LinearLayout btnConnectShop;
    private LinearLayout btnNotificationCenter;
    private LinearLayout btnCSCenter;
    private LinearLayout btnVersion;
    private LinearLayout btnDisConnect;

    public interface DialogsActivityDelegate {
        void didSelectDialog(DialogsActivity fragment, long dialog_id, boolean param);
    }

    public DialogsActivity(Bundle args) {
        super(args);
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        Log.d("DialogsActivity", "DialogsActivity start");

        if (getArguments() != null) {
            Log.d("DialogsActivity", "onArguments not null : " + getArguments());

            onlySelect = arguments.getBoolean("onlySelect", false);
            cantSendToChannels = arguments.getBoolean("cantSendToChannels", false);
            dialogsType = arguments.getInt("dialogsType", 0);
            selectAlertString = arguments.getString("selectAlertString");
            selectAlertStringGroup = arguments.getString("selectAlertStringGroup");
            addToGroupAlertString = arguments.getString("addToGroupAlertString");
        }


        //옵저버 등록
        if (searchString == null) {
            Log.d("DialogsActivity", "searchString is null");
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadHints);
        }


        if (!dialogsLoaded) {
            Log.d(LOG_TAG, "dialogsLoaded = " + dialogsLoaded);

            //대화 내용 리스트에 추가
            MessagesController.getInstance().loadDialogs(0, 100, true);

            //초대 메시지 체크 ?? 왜 필요한지 모르게씀
            ContactsController.getInstance().checkInviteText();
            StickersQuery.checkFeaturedStickers();
            dialogsLoaded = true;
        }
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (searchString == null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadHints);
        }
        delegate = null;
    }

    @Override
    public View createView(final Context context) {
        Log.d(LOG_TAG, "createView");

        sp = context.getSharedPreferences("connectMall", Context.MODE_PRIVATE);
        connect = sp.getBoolean("connecting", false);

        searchAdapter = new SearchAdapter(context, null, true, false, false, true);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        searching = false;
        searchWas = false;

        this.context = context;

        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Theme.loadRecources(context);
            }
        });

        //액션바 새로 생성
        //검색 버튼 및 검색 EditText생성
        ActionBarMenu menu = actionBar.createMenu();
        if (!onlySelect && searchString == null) {
            passcodeItem = menu.addItem(1, kr.wdream.storyshop.R.drawable.lock_close);
            updatePasscodeButton();
        }
        final ActionBarMenuItem item = menu.addItem(0, kr.wdream.storyshop.R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {
                searching = true;
                if (listView != null) {
                    if (searchString != null) {
                        listView.setEmptyView(searchEmptyView);
                        progressView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                    }
                    if (!onlySelect) {
                        floatingButton.setVisibility(View.GONE);
                    }
                }

                if (clicktab1) {
                    tab1Searching = true;
                }

                updatePasscodeButton();
            }

            @Override
            public boolean canCollapseSearch() {
                if (clicktab2) {
                    if (searchString != null) {
                        finishFragment();
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void onSearchCollapse() {
                if(clicktab2) {
                    searching = false;
                    searchWas = false;
                    if (listView != null) {
                        searchEmptyView.setVisibility(View.GONE);
                        if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                            emptyView.setVisibility(View.GONE);
                            listView.setEmptyView(progressView);
                        } else {
                            progressView.setVisibility(View.GONE);
                            listView.setEmptyView(emptyView);
                        }
                        if (!onlySelect) {
                            floatingButton.setVisibility(View.VISIBLE);
                            floatingHidden = true;
                            floatingButton.setTranslationY(AndroidUtilities.dp(100));
                            hideFloatingButton(false);
                        }
                        if (listView.getAdapter() != dialogsAdapter) {
                            listView.setAdapter(dialogsAdapter);
                            dialogsAdapter.notifyDataSetChanged();
                        }
                    }
                    if (dialogsSearchAdapter != null) {
                        dialogsSearchAdapter.searchDialogs(null);
                    }
                }
                if(clicktab1){
                    searchAdapter.searchDialogs(null);
                    searching = false;
                    searchWas = false;
                    listContacts.setAdapter(LaunchActivity.contactsAdapter);
                    LaunchActivity.contactsAdapter.notifyDataSetChanged();
                    listContacts.setFastScrollAlwaysVisible(true);
                    listContacts.setFastScrollEnabled(true);
                    listContacts.setVerticalScrollBarEnabled(false);
                }
                updatePasscodeButton();
            }

            @Override
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (clicktab2) {
                    if (text.length() != 0 || dialogsSearchAdapter != null && dialogsSearchAdapter.hasRecentRearch()) {
                        contentLayout.setVisibility(View.GONE);
                        listContacts.setVisibility(View.GONE);
                        lytDialogs.setVisibility(View.VISIBLE);
                        searchWas = true;
                        if (dialogsSearchAdapter != null && listView.getAdapter() != dialogsSearchAdapter) {
                            listView.setAdapter(dialogsSearchAdapter);
                            dialogsSearchAdapter.notifyDataSetChanged();
                        }
                        if (searchEmptyView != null && listView.getEmptyView() != searchEmptyView) {
                            emptyView.setVisibility(View.GONE);
                            progressView.setVisibility(View.GONE);
                            searchEmptyView.showTextView();
                            listView.setEmptyView(searchEmptyView);
                        }
                    }
                    if (dialogsSearchAdapter != null) {
                        dialogsSearchAdapter.searchDialogs(text);
                    }
                }
                if (clicktab1) {
                    if (LaunchActivity.contactsAdapter == null) {
                        return;
                    }
                    if (text.length() != 0) {
                        searchWas = true;
                        if (listView != null) {
                            listContacts.setAdapter(searchAdapter);
                            searchAdapter.notifyDataSetChanged();
                            listContacts.setFastScrollAlwaysVisible(false);
                            listContacts.setFastScrollEnabled(false);
                            listContacts.setVerticalScrollBarEnabled(true);
                        }
                    }
                    searchAdapter.searchDialogs(text);
                }
            }
        });
        item.getSearchField().setHint(LocaleController.getString("Search", kr.wdream.storyshop.R.string.Search));
        if (onlySelect) {
            actionBar.setBackButtonImage(kr.wdream.storyshop.R.drawable.ic_ab_back);
            actionBar.setTitle(LocaleController.getString("SelectChat", kr.wdream.storyshop.R.string.SelectChat));
        } else {
            if (searchString != null) {
                actionBar.setBackButtonImage(kr.wdream.storyshop.R.drawable.ic_ab_back);
            } else {
                actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                actionBar.setTitle(LocaleController.getString("AppNameBeta", kr.wdream.storyshop.R.string.AppNameBeta));
            } else {
                actionBar.setTitle(LocaleController.getString("AppName", kr.wdream.storyshop.R.string.AppName));
            }
        }
        actionBar.setAllowOverlayTitle(true);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                Log.d(LOG_TAG, "DialogsActivity ItemClick");
                if (id == -1) {
                    if (onlySelect) {
                        finishFragment();
                        Log.d(LOG_TAG, "onySelect finishFragment");
                    } else if (parentLayout != null) {
                        parentLayout.getDrawerLayoutContainer().openDrawer(false);
                        Log.d(LOG_TAG, "parentLayout.getDrawerLayoutContainer().openDrawer(false);");
                    }
                } else if (id == 1) {
                    UserConfig.appLocked = !UserConfig.appLocked;
                    UserConfig.saveConfig(false);
                    updatePasscodeButton();
                    Log.d(LOG_TAG, "updatePasscodeButton");
                }
            }
        });

        initView();

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LaunchActivity.contactsAdapter != null) {
            LaunchActivity.contactsAdapter.notifyDataSetChanged();
        }

        if (dialogsAdapter != null) {
            dialogsAdapter.notifyDataSetChanged();
        }
        if (dialogsSearchAdapter != null) {
            dialogsSearchAdapter.notifyDataSetChanged();
        }
        if (checkPermission && !onlySelect && Build.VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                checkPermission = false;
                if (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", kr.wdream.storyshop.R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionContacts", kr.wdream.storyshop.R.string.PermissionContacts));
                        builder.setPositiveButton(LocaleController.getString("OK", kr.wdream.storyshop.R.string.OK), null);
                        showDialog(permissionDialog = builder.create());
                    } else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", kr.wdream.storyshop.R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionStorage", kr.wdream.storyshop.R.string.PermissionStorage));
                        builder.setPositiveButton(LocaleController.getString("OK", kr.wdream.storyshop.R.string.OK), null);
                        showDialog(permissionDialog = builder.create());
                    } else {
                        askForPermissons();
                    }
                }
            }
        }
    }

    public void initView(){

        RelativeLayout relativeLayout = new RelativeLayout(context);
        fragmentView = relativeLayout;

        LinearLayout lytTab = new LinearLayout(context);
        lytTab.setId(kr.wdream.storyshop.R.id.lytTab);

        tabParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        tabParams.setMargins(0,0,0,5);

        tab1 = new LinearLayout(context);
        tab1.setGravity(Gravity.CENTER);
        imgTab1 = new ImageView(context);
        imgTab1.setImageResource(R.drawable.m_i_main_flist_n);
        tab1.addView(imgTab1, LayoutHelper.createLinear(21,20));
        tab1.setOnClickListener(this);

        tab2 = new LinearLayout(context);
        tab2.setGravity(Gravity.CENTER);
        imgTab2 = new ImageView(context);
        imgTab2.setImageResource(R.drawable.m_i_main_clist_n);
        tab2.addView(imgTab2, LayoutHelper.createLinear(21,20));
        tab2.setOnClickListener(this);

        tab3 = new LinearLayout(context);
        tab3.setGravity(Gravity.CENTER);
        imgTab3 = new ImageView(context);
        imgTab3.setImageResource(R.drawable.m_i_main_content_n);
        tab3.addView(imgTab3, LayoutHelper.createLinear(21,20));
        tab3.setOnClickListener(this);

        tab4 = new LinearLayout(context);
        tab4.setGravity(Gravity.CENTER);
        imgTab4 = new ImageView(context);
        imgTab4.setImageResource(R.drawable.m_i_main_setting_n);
        tab4.addView(imgTab4, LayoutHelper.createLinear(21,20));
        tab4.setOnClickListener(this);



        lytDialogs = new RelativeLayout(context);
        RelativeLayout.LayoutParams lytDialogsParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lytDialogsParams.addRule(RelativeLayout.BELOW, lytTab.getId());
        lytDialogs.setLayoutParams(lytDialogsParams);
        lytDialogs.setVisibility(View.GONE);
        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(true);
        listView.setItemAnimator(null);
        listView.setInstantClick(true);
        listView.setLayoutAnimation(null);
        listView.setTag(4);
        listView.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);
        listView.setVerticalScrollBarEnabled(false);
        listView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        listView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);
        listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (listView == null || listView.getAdapter() == null) {
                    return;
                }
                long dialog_id = 0;
                int message_id = 0;
                RecyclerView.Adapter adapter = listView.getAdapter();
                if (adapter == dialogsAdapter) {
                    TLRPC.TL_dialog dialog = dialogsAdapter.getItem(position);
                    if (dialog == null) {
                        return;
                    }
                    dialog_id = dialog.id;
                } else if (adapter == dialogsSearchAdapter) {
                    Object obj = dialogsSearchAdapter.getItem(position);
                    if (obj instanceof TLRPC.User) {
                        dialog_id = ((TLRPC.User) obj).id;
                        if (dialogsSearchAdapter.isGlobalSearch(position)) {
                            ArrayList<TLRPC.User> users = new ArrayList<>();
                            users.add((TLRPC.User) obj);
                            MessagesController.getInstance().putUsers(users, false);
                            MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                        }
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.User) obj);
                        }
                    } else if (obj instanceof TLRPC.Chat) {
                        if (dialogsSearchAdapter.isGlobalSearch(position)) {
                            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
                            chats.add((TLRPC.Chat) obj);
                            MessagesController.getInstance().putChats(chats, false);
                            MessagesStorage.getInstance().putUsersAndChats(null, chats, false, true);
                        }
                        if (((TLRPC.Chat) obj).id > 0) {
                            dialog_id = -((TLRPC.Chat) obj).id;
                        } else {
                            dialog_id = AndroidUtilities.makeBroadcastId(((TLRPC.Chat) obj).id);
                        }
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.Chat) obj);
                        }
                    } else if (obj instanceof TLRPC.EncryptedChat) {
                        dialog_id = ((long) ((TLRPC.EncryptedChat) obj).id) << 32;
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.EncryptedChat) obj);
                        }
                    } else if (obj instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) obj;
                        dialog_id = messageObject.getDialogId();
                        message_id = messageObject.getId();
                        dialogsSearchAdapter.addHashtagsFromMessage(dialogsSearchAdapter.getLastSearchString());
                    } else if (obj instanceof String) {
                        Log.d(LOG_TAG, "obj String : openSearchField");
                        actionBar.openSearchField((String) obj);
                    }
                }

                if (dialog_id == 0) {
                    return;
                }

                if (onlySelect) {
                    didSelectResult(dialog_id, true, false);
                } else {
                    Bundle args = new Bundle();
                    int lower_part = (int) dialog_id;
                    int high_id = (int) (dialog_id >> 32);
                    if (lower_part != 0) {
                        if (high_id == 1) {
                            args.putInt("chat_id", lower_part);
                        } else {
                            if (lower_part > 0) {
                                args.putInt("user_id", lower_part);
                            } else if (lower_part < 0) {
                                if (message_id != 0) {
                                    TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_part);
                                    if (chat != null && chat.migrated_to != null) {
                                        args.putInt("migrated_to", lower_part);
                                        lower_part = -chat.migrated_to.channel_id;
                                    }
                                }
                                args.putInt("chat_id", -lower_part);
                            }
                        }
                    } else {
                        args.putInt("enc_id", high_id);
                    }
                    if (message_id != 0) {
                        args.putInt("message_id", message_id);
                    } else {
                        if (actionBar != null) {
                            actionBar.closeSearchField();
                        }
                    }
                    if (AndroidUtilities.isTablet()) {
                        if (openedDialogId == dialog_id && adapter != dialogsSearchAdapter) {
                            return;
                        }
                        if (dialogsAdapter != null) {
                            dialogsAdapter.setOpenedDialogId(openedDialogId = dialog_id);
                            updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                        }
                    }
                    if (searchString != null) {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats);
                            presentFragment(new ChatActivity(args));
                        }
                    } else {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            presentFragment(new ChatActivity(args));
                        }
                    }
                }
            }
        });
        listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {
                if (onlySelect || searching && searchWas || getParentActivity() == null) {
                    if (searchWas && searching || dialogsSearchAdapter.isRecentSearchDisplayed()) {
                        RecyclerView.Adapter adapter = listView.getAdapter();
                        if (adapter == dialogsSearchAdapter) {
                            Object item = dialogsSearchAdapter.getItem(position);
                            if (item instanceof String || dialogsSearchAdapter.isRecentSearchDisplayed()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", kr.wdream.storyshop.R.string.AppName));
                                builder.setMessage(LocaleController.getString("ClearSearch", kr.wdream.storyshop.R.string.ClearSearch));
                                builder.setPositiveButton(LocaleController.getString("ClearButton", kr.wdream.storyshop.R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (dialogsSearchAdapter.isRecentSearchDisplayed()) {
                                            dialogsSearchAdapter.clearRecentSearch();
                                        } else {
                                            dialogsSearchAdapter.clearRecentHashtags();
                                        }
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", kr.wdream.storyshop.R.string.Cancel), null);
                                showDialog(builder.create());
                                return true;
                            }
                        }
                    }
                    return false;
                }
                TLRPC.TL_dialog dialog;
                ArrayList<TLRPC.TL_dialog> dialogs = getDialogsArray();
                if (position < 0 || position >= dialogs.size()) {
                    return false;
                }
                dialog = dialogs.get(position);
                selectedDialog = dialog.id;

                BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
                int lower_id = (int) selectedDialog;
                int high_id = (int) (selectedDialog >> 32);

                if (DialogObject.isChannel(dialog)) {
                    final TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_id);
                    CharSequence items[];
                    if (chat != null && chat.megagroup) {
                        items = new CharSequence[]{LocaleController.getString("ClearHistoryCache", kr.wdream.storyshop.R.string.ClearHistoryCache), chat == null || !chat.creator ? LocaleController.getString("LeaveMegaMenu", kr.wdream.storyshop.R.string.LeaveMegaMenu) : LocaleController.getString("DeleteMegaMenu", kr.wdream.storyshop.R.string.DeleteMegaMenu)};
                    } else {
                        items = new CharSequence[]{LocaleController.getString("ClearHistoryCache", kr.wdream.storyshop.R.string.ClearHistoryCache), chat == null || !chat.creator ? LocaleController.getString("LeaveChannelMenu", kr.wdream.storyshop.R.string.LeaveChannelMenu) : LocaleController.getString("ChannelDeleteMenu", kr.wdream.storyshop.R.string.ChannelDeleteMenu)};
                    }
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", kr.wdream.storyshop.R.string.AppName));
                            if (which == 0) {
                                if (chat != null && chat.megagroup) {
                                    builder.setMessage(LocaleController.getString("AreYouSureClearHistorySuper", kr.wdream.storyshop.R.string.AreYouSureClearHistorySuper));
                                } else {
                                    builder.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", kr.wdream.storyshop.R.string.AreYouSureClearHistoryChannel));
                                }
                                builder.setPositiveButton(LocaleController.getString("OK", kr.wdream.storyshop.R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        MessagesController.getInstance().deleteDialog(selectedDialog, 2);
                                    }
                                });
                            } else {
                                if (chat != null && chat.megagroup) {
                                    if (!chat.creator) {
                                        builder.setMessage(LocaleController.getString("MegaLeaveAlert", kr.wdream.storyshop.R.string.MegaLeaveAlert));
                                    } else {
                                        builder.setMessage(LocaleController.getString("MegaDeleteAlert", kr.wdream.storyshop.R.string.MegaDeleteAlert));
                                    }
                                } else {
                                    if (chat == null || !chat.creator) {
                                        builder.setMessage(LocaleController.getString("ChannelLeaveAlert", kr.wdream.storyshop.R.string.ChannelLeaveAlert));
                                    } else {
                                        builder.setMessage(LocaleController.getString("ChannelDeleteAlert", kr.wdream.storyshop.R.string.ChannelDeleteAlert));
                                    }
                                }
                                builder.setPositiveButton(LocaleController.getString("OK", kr.wdream.storyshop.R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        MessagesController.getInstance().deleteUserFromChat((int) -selectedDialog, UserConfig.getCurrentUser(), null);
                                        if (AndroidUtilities.isTablet()) {
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, selectedDialog);
                                        }
                                    }
                                });
                            }
                            builder.setNegativeButton(LocaleController.getString("Cancel", kr.wdream.storyshop.R.string.Cancel), null);
                            showDialog(builder.create());
                        }
                    });
                    showDialog(builder.create());
                } else {
                    final boolean isChat = lower_id < 0 && high_id != 1;
                    TLRPC.User user = null;
                    if (!isChat && lower_id > 0 && high_id != 1) {
                        user = MessagesController.getInstance().getUser(lower_id);
                    }
                    final boolean isBot = user != null && user.bot;
                    builder.setItems(new CharSequence[]{LocaleController.getString("ClearHistory", kr.wdream.storyshop.R.string.ClearHistory),
                            isChat ? LocaleController.getString("DeleteChat", kr.wdream.storyshop.R.string.DeleteChat) :
                                    isBot ? LocaleController.getString("DeleteAndStop", kr.wdream.storyshop.R.string.DeleteAndStop) : LocaleController.getString("Delete", kr.wdream.storyshop.R.string.Delete)}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", kr.wdream.storyshop.R.string.AppName));
                            if (which == 0) {
                                builder.setMessage(LocaleController.getString("AreYouSureClearHistory", kr.wdream.storyshop.R.string.AreYouSureClearHistory));
                            } else {
                                if (isChat) {
                                    builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", kr.wdream.storyshop.R.string.AreYouSureDeleteAndExit));
                                } else {
                                    builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", kr.wdream.storyshop.R.string.AreYouSureDeleteThisChat));
                                }
                            }
                            builder.setPositiveButton(LocaleController.getString("OK", kr.wdream.storyshop.R.string.OK), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (which != 0) {
                                        if (isChat) {
                                            TLRPC.Chat currentChat = MessagesController.getInstance().getChat((int) -selectedDialog);
                                            if (currentChat != null && ChatObject.isNotInChat(currentChat)) {
                                                MessagesController.getInstance().deleteDialog(selectedDialog, 0);
                                            } else {
                                                MessagesController.getInstance().deleteUserFromChat((int) -selectedDialog, MessagesController.getInstance().getUser(UserConfig.getClientUserId()), null);
                                            }
                                        } else {
                                            MessagesController.getInstance().deleteDialog(selectedDialog, 0);
                                        }
                                        if (isBot) {
                                            MessagesController.getInstance().blockUser((int) selectedDialog);
                                        }
                                        if (AndroidUtilities.isTablet()) {
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, selectedDialog);
                                        }
                                    } else {
                                        MessagesController.getInstance().deleteDialog(selectedDialog, 1);
                                    }
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", kr.wdream.storyshop.R.string.Cancel), null);
                            showDialog(builder.create());
                        }
                    });
                    showDialog(builder.create());
                }
                return true;
            }
        });


        listContacts = new LetterSectionsListView(context);
        RelativeLayout.LayoutParams contactParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        contactParams.addRule(RelativeLayout.BELOW, lytTab.getId());
        listContacts.setLayoutParams(contactParams);


        Log.d(LOG_TAG, "contactsAdapter : " + LaunchActivity.contactsAdapter.getItem(0));


        listContacts.setAdapter(LaunchActivity.contactsAdapter);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "postDelayed Start");
                handler.sendEmptyMessage(0);
            }
        }, 1500);

        listContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int section = LaunchActivity.contactsAdapter.getSectionForPosition(position);
                int row = LaunchActivity.contactsAdapter.getPositionInSectionForPosition(position);

                Object item = LaunchActivity.contactsAdapter.getItem(section, row);

                if (0 == position) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, ContactsController.getInstance().getInviteText());
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteFriends", kr.wdream.storyshop.R.string.InviteFriends)), 500);
                } else if (item instanceof ContactsController.Contact) {
                    ContactsController.Contact contact = (ContactsController.Contact) item;
                    String usePhone = null;
                    if (!contact.phones.isEmpty()) {
                        usePhone = contact.phones.get(0);
                    }
                    if (usePhone == null || getParentActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("InviteUser", kr.wdream.storyshop.R.string.InviteUser));
                    builder.setTitle(LocaleController.getString("AppName", kr.wdream.storyshop.R.string.AppName));
                    final String arg1 = usePhone;
                    builder.setPositiveButton(LocaleController.getString("OK", kr.wdream.storyshop.R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", arg1, null));
                                intent.putExtra("sms_body", LocaleController.getString("InviteText", kr.wdream.storyshop.R.string.InviteText));
                                getParentActivity().startActivityForResult(intent, 500);
                            } catch (Exception e) {
                                FileLog.e("tmessages", e);
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", kr.wdream.storyshop.R.string.Cancel), null);
                    showDialog(builder.create());
                } else {
                    TLRPC.User user = (TLRPC.User) LaunchActivity.contactsAdapter.getItem(position);

                    if(user == null)
                        return;
                    Bundle args = new Bundle();
                    args.putInt("user_id", user.id);

                    if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                        presentFragment(new ChatActivity(args), false);
                    }

                }
            }
        });

        contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setVisibility(View.GONE);

        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentParams.addRule(RelativeLayout.BELOW, lytTab.getId());


        ArrayList<String> settingContent = new ArrayList<String>();
        settingContent.add(0, "내 정보변경");
        settingContent.add(1, "내 정보변경");
        settingContent.add(2, "내 정보변경");
        settingContent.add(3, "내 정보변경");
        settingContent.add(4, "내 정보변경");
        settingContent.add(5, "내 정보변경");
        settingContent.add(6, "내 정보변경");

        // Contents Layout 생성
        GridView gridContents = new GridView(context);
        gridContents.setNumColumns(GridView.AUTO_FIT);
        gridContents.setGravity(Gravity.CENTER);

        ContentsAdapter contentsAdapter = new ContentsAdapter(context);
        gridContents.setAdapter(contentsAdapter);

        contentLayout.addView(gridContents, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // Setting Layout 생성하기
        settingLayout = new LinearLayout(context);
        settingLayout.setOrientation(LinearLayout.VERTICAL);
        settingLayout.setVisibility(View.GONE);

        settingLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));

        createSettingLayout();

        // TabBar 관련
        lytTab.addView(tab1, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 1));
        lytTab.addView(tab2, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 1));
        lytTab.addView(tab3, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 1));
        lytTab.addView(tab4, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 1));

        relativeLayout.addView(lytTab, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 50));

        RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        lytDialogs.addView(listView,listParams);
        relativeLayout.addView(lytDialogs, lytDialogsParams);
        relativeLayout.addView(listContacts, contactParams);
        relativeLayout.addView(contentLayout, contentParams);
        relativeLayout.addView(settingLayout, contentParams);

        searchEmptyView = new EmptyTextProgressView(context);
        searchEmptyView.setVisibility(View.GONE);
        searchEmptyView.setShowAtCenter(true);
        searchEmptyView.setText(LocaleController.getString("NoResult", kr.wdream.storyshop.R.string.NoResult));
        relativeLayout.addView(searchEmptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        emptyView = new LinearLayout(context);
        emptyView.setOrientation(LinearLayout.VERTICAL);
        emptyView.setVisibility(View.GONE);
        emptyView.setGravity(Gravity.CENTER);
//        relativeLayout.addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        emptyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString("NoChats", kr.wdream.storyshop.R.string.NoChats));
        textView.setTextColor(0xff959595);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        emptyView.addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        textView = new TextView(context);
        String help = LocaleController.getString("NoChatsHelp", kr.wdream.storyshop.R.string.NoChatsHelp);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            help = help.replace('\n', ' ');
        }
        textView.setText(help);
        textView.setTextColor(0xff959595);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(6), AndroidUtilities.dp(8), 0);
        textView.setLineSpacing(AndroidUtilities.dp(2), 1);
        emptyView.addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        progressView = new ProgressBar(context);
        progressView.setVisibility(View.GONE);

        RelativeLayout.LayoutParams progParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        progressView.setLayoutParams(progParams);
//        relativeLayout.addView(progressView);
//        relativeLayout.addView(progressView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        floatingButton = new ImageView(context);
        floatingButton.setVisibility(onlySelect ? View.GONE : View.VISIBLE);
        floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        floatingButton.setBackgroundResource(kr.wdream.storyshop.R.drawable.floating_states);
        floatingButton.setImageResource(kr.wdream.storyshop.R.drawable.floating_pencil);
        Log.d(LOG_TAG, "setVisibility.VISIBLE_floating : " + floatingButton.getVisibility());
        floatingButton.setVisibility(View.GONE);
        Log.d(LOG_TAG, "setVisibility.GONE_floating : " + floatingButton.getVisibility());

        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(2), AndroidUtilities.dp(4)).setDuration(200));
            animator.addState(new int[]{}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(4), AndroidUtilities.dp(2)).setDuration(200));
            floatingButton.setStateListAnimator(animator);
            floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint("NewApi")
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
                }
            });
        }
        RelativeLayout.LayoutParams floatingParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        floatingParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        floatingParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        relativeLayout.addView(floatingButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, LocaleController.isRTL ? 14 : 0, 0, LocaleController.isRTL ? 0 : 14, 14));
        relativeLayout.addView(floatingButton, floatingParams);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putBoolean("destroyAfterSelect", true);
                presentFragment(new ContactsActivity(args));
            }
        });

        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && searching && searchWas) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = Math.abs(layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();

                if (searching && searchWas) {
                    if (visibleItemCount > 0 && layoutManager.findLastVisibleItemPosition() == totalItemCount - 1 && !dialogsSearchAdapter.isMessagesSearchEndReached()) {
                        dialogsSearchAdapter.loadMoreSearchMessages();
                    }
                    return;
                }
                if (visibleItemCount > 0) {
                    if (layoutManager.findLastVisibleItemPosition() >= getDialogsArray().size() - 10) {
                        MessagesController.getInstance().loadDialogs(-1, 100, !MessagesController.getInstance().dialogsEndReached);
                    }
                }

                if (floatingButton.getVisibility() != View.GONE) {
                    final View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean goingDown;
                    boolean changed = true;
                    if (prevPosition == firstVisibleItem) {
                        final int topDelta = prevTop - firstViewTop;
                        goingDown = firstViewTop < prevTop;
                        changed = Math.abs(topDelta) > 1;
                    } else {
                        goingDown = firstVisibleItem > prevPosition;
                    }
                    if (changed && scrollUpdated) {
                        hideFloatingButton(goingDown);
                    }
                    prevPosition = firstVisibleItem;
                    prevTop = firstViewTop;
                    scrollUpdated = true;
                }
            }
        });

        if (searchString == null) {
            dialogsAdapter = new DialogsAdapter(context, dialogsType);
            Log.d("Dialog", "dialogsSize : " + dialogsAdapter.getItemCount());

            if (AndroidUtilities.isTablet() && openedDialogId != 0) {
                dialogsAdapter.setOpenedDialogId(openedDialogId);
            }
            listView.setAdapter(dialogsAdapter);
        }
        int type = 0;
        if (searchString != null) {
            type = 2;
        } else if (!onlySelect) {
            type = 1;
        }
        dialogsSearchAdapter = new DialogsSearchAdapter(context, type, dialogsType);
        dialogsSearchAdapter.setDelegate(new DialogsSearchAdapter.DialogsSearchAdapterDelegate() {
            @Override
            public void searchStateChanged(boolean search) {
                if (searching && searchWas && searchEmptyView != null) {
                    if (search) {
                        searchEmptyView.showProgress();
                    } else {
                        searchEmptyView.showTextView();
                    }
                }
            }

            @Override
            public void didPressedOnSubDialog(int did) {
                if (onlySelect) {
                    didSelectResult(did, true, false);
                } else {
                    Bundle args = new Bundle();
                    if (did > 0) {
                        args.putInt("user_id", did);
                    } else {
                        args.putInt("chat_id", -did);
                    }
                    if (actionBar != null) {
                        actionBar.closeSearchField();
                    }
                    if (AndroidUtilities.isTablet()) {
                        if (dialogsAdapter != null) {
                            dialogsAdapter.setOpenedDialogId(openedDialogId = did);
                            updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                        }
                    }
                    if (searchString != null) {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats);
                            presentFragment(new ChatActivity(args));
                        }
                    } else {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            presentFragment(new ChatActivity(args));
                        }
                    }
                }
            }

            @Override
            public void needRemoveHint(final int did) {
                if (getParentActivity() == null) {
                    return;
                }
                TLRPC.User user = MessagesController.getInstance().getUser(did);
                if (user == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", kr.wdream.storyshop.R.string.AppName));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", kr.wdream.storyshop.R.string.ChatHintsDelete, ContactsController.formatName(user.first_name, user.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", kr.wdream.storyshop.R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SearchQuery.removePeer(did);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", kr.wdream.storyshop.R.string.Cancel), null);
                showDialog(builder.create());
            }
        });

        if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
            searchEmptyView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            listView.setEmptyView(progressView);
        } else {
            searchEmptyView.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);
            listView.setEmptyView(emptyView);
        }
        if (searchString != null) {
            actionBar.openSearchField(searchString);
        }

        if (!onlySelect && dialogsType == 0) {
            relativeLayout.addView(new PlayerView(context, this), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 39, Gravity.TOP | Gravity.LEFT, 0, -36, 0, 0));
        }

        tab1.performClick();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askForPermissons() {
        Activity activity = getParentActivity();
        if (activity == null) {
            return;
        }
        ArrayList<String> permissons = new ArrayList<>();
        if (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissons.add(Manifest.permission.READ_CONTACTS);
            permissons.add(Manifest.permission.WRITE_CONTACTS);
            permissons.add(Manifest.permission.GET_ACCOUNTS);
        }
        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissons.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissons.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        String[] items = permissons.toArray(new String[permissons.size()]);
        activity.requestPermissions(items, 1);
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (permissionDialog != null && dialog == permissionDialog && getParentActivity() != null) {
            askForPermissons();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!onlySelect && floatingButton != null) {
            floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    floatingButton.setTranslationY(floatingHidden ? AndroidUtilities.dp(100) : 0);
                    floatingButton.setClickable(!floatingHidden);
                    if (floatingButton != null) {
                        if (Build.VERSION.SDK_INT < 16) {
                            floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length <= a || grantResults[a] != PackageManager.PERMISSION_GRANTED) {
                    continue;
                }
                switch (permissions[a]) {
                    case Manifest.permission.READ_CONTACTS:
                        ContactsController.getInstance().readContacts();
                        break;
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        ImageLoader.getInstance().checkMediaPaths();
                        break;
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            if (dialogsAdapter != null) {
                if (dialogsAdapter.isDataSetChanged()) {
                    dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows(MessagesController.UPDATE_MASK_NEW_MESSAGE);
                }
            }
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.notifyDataSetChanged();
            }
            if (listView != null) {
                try {
                    if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                        searchEmptyView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                        listView.setEmptyView(progressView);
                    } else {
                        progressView.setVisibility(View.GONE);
                        if (searching && searchWas) {
                            emptyView.setVisibility(View.GONE);
                            listView.setEmptyView(searchEmptyView);
                        } else {
                            searchEmptyView.setVisibility(View.GONE);
                            listView.setEmptyView(emptyView);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e); //TODO fix it in other way?
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.updateInterfaces) {
            updateVisibleRows((Integer) args[0]);
        } else if (id == NotificationCenter.appDidLogout) {
            dialogsLoaded = false;
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.contactsDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.openedChatChanged) {
            if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                boolean close = (Boolean) args[1];
                long dialog_id = (Long) args[0];
                if (close) {
                    if (dialog_id == openedDialogId) {
                        openedDialogId = 0;
                    }
                } else {
                    openedDialogId = dialog_id;
                }
                if (dialogsAdapter != null) {
                    dialogsAdapter.setOpenedDialogId(openedDialogId);
                }
                updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
            }
        } else if (id == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.messageReceivedByAck || id == NotificationCenter.messageReceivedByServer || id == NotificationCenter.messageSendError) {
            updateVisibleRows(MessagesController.UPDATE_MASK_SEND_STATE);
        } else if (id == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } if (id == NotificationCenter.needReloadRecentDialogsSearch) {
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (id == NotificationCenter.didLoadedReplyMessages) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.reloadHints) {
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.notifyDataSetChanged();
            }
        }
    }

    private ArrayList<TLRPC.TL_dialog> getDialogsArray() {
        if (dialogsType == 0) {
            return MessagesController.getInstance().dialogs;
        } else if (dialogsType == 1) {
            return MessagesController.getInstance().dialogsServerOnly;
        } else if (dialogsType == 2) {
            return MessagesController.getInstance().dialogsGroupsOnly;
        }
        return null;
    }

    private void updatePasscodeButton() {
        if (passcodeItem == null) {
            return;
        }
        if (UserConfig.passcodeHash.length() != 0 && !searching) {
            passcodeItem.setVisibility(View.VISIBLE);
            if (UserConfig.appLocked) {
                passcodeItem.setIcon(kr.wdream.storyshop.R.drawable.lock_close);
            } else {
                passcodeItem.setIcon(kr.wdream.storyshop.R.drawable.lock_open);
            }
        } else {
            passcodeItem.setVisibility(View.GONE);
        }
    }

    private void hideFloatingButton(boolean hide) {
        if (floatingHidden == hide) {
            return;
        }
        floatingHidden = hide;
        ObjectAnimator animator = ObjectAnimator.ofFloat(floatingButton, "translationY", floatingHidden ? AndroidUtilities.dp(100) : 0).setDuration(300);
        animator.setInterpolator(floatingInterpolator);
        floatingButton.setClickable(!hide);
        animator.start();
    }

    private void updateVisibleRows(int mask) {
        if (listView == null) {
            return;
        }
        int count = listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = listView.getChildAt(a);
            if (child instanceof DialogCell) {
                if (listView.getAdapter() != dialogsSearchAdapter) {
                    DialogCell cell = (DialogCell) child;
                    if ((mask & MessagesController.UPDATE_MASK_NEW_MESSAGE) != 0) {
                        cell.checkCurrentDialogIndex();
                        if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                            cell.setDialogSelected(cell.getDialogId() == openedDialogId);
                        }
                    } else if ((mask & MessagesController.UPDATE_MASK_SELECT_DIALOG) != 0) {
                        if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                            cell.setDialogSelected(cell.getDialogId() == openedDialogId);
                        }
                    } else {
                        cell.update(mask);
                    }
                }
            } else if (child instanceof UserCell) {
                ((UserCell) child).update(mask);
            } else if (child instanceof ProfileSearchCell) {
                ((ProfileSearchCell) child).update(mask);
            } else if (child instanceof RecyclerListView) {
                RecyclerListView innerListView = (RecyclerListView) child;
                int count2 = innerListView.getChildCount();
                for (int b = 0; b < count2; b++) {
                    View child2 = innerListView.getChildAt(b);
                    if (child2 instanceof HintDialogCell) {
                        ((HintDialogCell) child2).checkUnreadCounter(mask);
                    }
                }
            }
        }
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        delegate = dialogsActivityDelegate;
    }

    public void setSearchString(String string) {
        searchString = string;
    }

    public boolean isMainDialogList() {
        return delegate == null && searchString == null;
    }

    private void didSelectResult(final long dialog_id, boolean useAlert, final boolean param) {
        if (addToGroupAlertString == null) {
            if ((int) dialog_id < 0 && ChatObject.isChannel(-(int) dialog_id) && (cantSendToChannels || !ChatObject.isCanWriteToChannel(-(int) dialog_id))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", kr.wdream.storyshop.R.string.AppName));
                builder.setMessage(LocaleController.getString("ChannelCantSendMessage", kr.wdream.storyshop.R.string.ChannelCantSendMessage));
                builder.setNegativeButton(LocaleController.getString("OK", kr.wdream.storyshop.R.string.OK), null);
                showDialog(builder.create());
                return;
            }
        }
        if (useAlert && (selectAlertString != null && selectAlertStringGroup != null || addToGroupAlertString != null)) {
            if (getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", kr.wdream.storyshop.R.string.AppName));
            int lower_part = (int) dialog_id;
            int high_id = (int) (dialog_id >> 32);
            if (lower_part != 0) {
                if (high_id == 1) {
                    TLRPC.Chat chat = MessagesController.getInstance().getChat(lower_part);
                    if (chat == null) {
                        return;
                    }
                    builder.setMessage(LocaleController.formatStringSimple(selectAlertStringGroup, chat.title));
                } else {
                    if (lower_part > 0) {
                        TLRPC.User user = MessagesController.getInstance().getUser(lower_part);
                        if (user == null) {
                            return;
                        }
                        builder.setMessage(LocaleController.formatStringSimple(selectAlertString, UserObject.getUserName(user)));
                    } else if (lower_part < 0) {
                        TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_part);
                        if (chat == null) {
                            return;
                        }
                        if (addToGroupAlertString != null) {
                            builder.setMessage(LocaleController.formatStringSimple(addToGroupAlertString, chat.title));
                        } else {
                            builder.setMessage(LocaleController.formatStringSimple(selectAlertStringGroup, chat.title));
                        }
                    }
                }
            } else {
                TLRPC.EncryptedChat chat = MessagesController.getInstance().getEncryptedChat(high_id);
                TLRPC.User user = MessagesController.getInstance().getUser(chat.user_id);
                if (user == null) {
                    return;
                }
                builder.setMessage(LocaleController.formatStringSimple(selectAlertString, UserObject.getUserName(user)));
            }

            builder.setPositiveButton(LocaleController.getString("OK", kr.wdream.storyshop.R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    didSelectResult(dialog_id, false, false);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", kr.wdream.storyshop.R.string.Cancel), null);
            showDialog(builder.create());
        } else {
            if (delegate != null) {
                delegate.didSelectDialog(DialogsActivity.this, dialog_id, param);
                delegate = null;
            } else {
                finishFragment();
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == tab1) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.setMargins(0,0,0,0);
            tab1.setLayoutParams(params);
            tab2.setLayoutParams(params);
            tab3.setLayoutParams(params);
            tab4.setLayoutParams(params);

            tab1.setBackgroundColor(Color.parseColor("#F5F5F5"));
            tab2.setBackgroundColor(Color.parseColor("#EEEEEE"));
            tab3.setBackgroundColor(Color.parseColor("#EEEEEE"));
            tab4.setBackgroundColor(Color.parseColor("#EEEEEE"));
            floatingButton.setVisibility(View.GONE);
            lytDialogs.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
            settingLayout.setVisibility(View.GONE);

            listContacts.setVisibility(View.VISIBLE);

            clicktab1 = true;

            clicktab2 = false;
            clicktab3 = false;
            clicktab4 = false;

            imgTab1.setImageResource(R.drawable.m_i_main_flist_s);
            imgTab2.setImageResource(R.drawable.m_i_main_clist_n);
            imgTab3.setImageResource(R.drawable.m_i_main_content_n);
            imgTab4.setImageResource(R.drawable.m_i_main_setting_n);

        }

        if (v == tab2) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.setMargins(0,0,0,0);
            tab1.setLayoutParams(params);
            tab2.setLayoutParams(params);
            tab3.setLayoutParams(params);
            tab4.setLayoutParams(params);

            tab1.setBackgroundColor(Color.parseColor("#EEEEEE"));
            tab2.setBackgroundColor(Color.parseColor("#F5F5F5"));
            tab3.setBackgroundColor(Color.parseColor("#EEEEEE"));
            tab4.setBackgroundColor(Color.parseColor("#EEEEEE"));
            floatingButton.setVisibility(View.VISIBLE);
            lytDialogs.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
            settingLayout.setVisibility(View.GONE);

            listContacts.setVisibility(View.GONE);


            clicktab2 = true;

            clicktab1 = false;
            clicktab3 = false;
            clicktab4 = false;

            imgTab1.setImageResource(R.drawable.m_i_main_flist_n);
            imgTab2.setImageResource(R.drawable.m_i_main_clist_s);
            imgTab3.setImageResource(R.drawable.m_i_main_content_n);
            imgTab4.setImageResource(R.drawable.m_i_main_setting_n);
        }

        if (v == tab3) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.setMargins(0,0,0,0);
            tab1.setLayoutParams(params);
            tab2.setLayoutParams(params);
            tab3.setLayoutParams(params);
            tab4.setLayoutParams(params);

            tab1.setBackgroundColor(Color.parseColor("#EEEEEE"));
            tab2.setBackgroundColor(Color.parseColor("#EEEEEE"));
            tab3.setBackgroundColor(Color.parseColor("#F5F5F5"));
            tab4.setBackgroundColor(Color.parseColor("#EEEEEE"));
            floatingButton.setVisibility(View.GONE);
            lytDialogs.setVisibility(View.GONE);
            listContacts.setVisibility(View.GONE);
            settingLayout.setVisibility(View.GONE);

            contentLayout.setVisibility(View.VISIBLE);

            clicktab3 = true;

            clicktab1 = false;
            clicktab2 = false;
            clicktab4 = false;

            imgTab1.setImageResource(R.drawable.m_i_main_flist_n);
            imgTab2.setImageResource(R.drawable.m_i_main_clist_n);
            imgTab3.setImageResource(R.drawable.m_i_main_content_s);
            imgTab4.setImageResource(R.drawable.m_i_main_setting_n);
        }

        if (v == tab4) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.setMargins(0,0,0,0);
            tab1.setLayoutParams(params);
            tab2.setLayoutParams(params);
            tab3.setLayoutParams(params);
            tab4.setLayoutParams(params);

            tab1.setBackgroundColor(Color.parseColor("#EEEEEE"));
            tab2.setBackgroundColor(Color.parseColor("#EEEEEE"));
            tab3.setBackgroundColor(Color.parseColor("#EEEEEE"));
            tab4.setBackgroundColor(Color.parseColor("#F5F5F5"));

            floatingButton.setVisibility(View.GONE);
            lytDialogs.setVisibility(View.GONE);
            listContacts.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);

            settingLayout.setVisibility(View.VISIBLE);

            clicktab3 = true;

            clicktab1 = false;
            clicktab2 = false;
            clicktab4 = false;

            imgTab1.setImageResource(R.drawable.m_i_main_flist_n);
            imgTab2.setImageResource(R.drawable.m_i_main_clist_n);
            imgTab3.setImageResource(R.drawable.m_i_main_content_n);
            imgTab4.setImageResource(R.drawable.m_i_main_setting_s);
            //                    presentFragment(new SettingsActivity());
        }

        if (v == btnMyInfo){
            presentFragment(new ChangeNameActivity());
        }

        if (v == btnConnectShop) {
            ShoppingDialog shoppingDialog = new ShoppingDialog(context);
            shoppingDialog.show();
        }

        if (v == btnNotificationCenter){
            presentFragment(new NotificationsSettingsActivity());
        }

        if (v == btnCSCenter) {
            presentFragment(new CenterActivity());
        }

        if (v == btnVersion) {
            presentFragment(new AppVersionActivity());
        }

        if (v == btnDisConnect) {
            Toast.makeText(context, "준비중입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                LaunchActivity.contactsAdapter.notifyDataSetChanged();
            }
        }
    };

    private void createSettingLayout(){
        PxToDp pxToDp = new PxToDp(context);

        // 구분선을 위한 레이아웃
        LinearLayout lytLine1 = new LinearLayout(context);
        LinearLayout lytLine2 = new LinearLayout(context);
        LinearLayout lytLine3 = new LinearLayout(context);
        LinearLayout lytLine4 = new LinearLayout(context);
        LinearLayout lytLine5 = new LinearLayout(context);
        LinearLayout lytLine6 = new LinearLayout(context);
        LinearLayout lytLine7 = new LinearLayout(context);

        LinearLayout.LayoutParams paramLine = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxToDp.dpToPx(1));
        paramLine.setMargins(pxToDp.dpToPx(36),0,0,0);

        lytLine1.setLayoutParams(paramLine);
        lytLine2.setLayoutParams(paramLine);
        lytLine3.setLayoutParams(paramLine);
        lytLine4.setLayoutParams(paramLine);

        lytLine1.setBackgroundColor(Color.parseColor("#E5E5E5"));
        lytLine2.setBackgroundColor(Color.parseColor("#E5E5E5"));
        lytLine3.setBackgroundColor(Color.parseColor("#E5E5E5"));
        lytLine4.setBackgroundColor(Color.parseColor("#E5E5E5"));
        lytLine5.setBackgroundColor(Color.parseColor("#E5E5E5"));
        lytLine6.setBackgroundColor(Color.parseColor("#E5E5E5"));
        lytLine7.setBackgroundColor(Color.parseColor("#E5E5E5"));

        // 첫번째 덩어리 생성
        LinearLayout lytMySetting = new LinearLayout(context);
        lytMySetting.setOrientation(LinearLayout.VERTICAL);
        lytMySetting.setBackgroundColor(Color.WHITE);
        lytMySetting.setPadding(pxToDp.dpToPx(20),0,0,0);


        // 내 정보 변경 버튼 생성
        btnMyInfo = new LinearLayout(context);
        btnMyInfo.setOrientation(LinearLayout.HORIZONTAL);
        btnMyInfo.setGravity(Gravity.CENTER_VERTICAL);
        btnMyInfo.setOnClickListener(this);

        ImageView imgMyInfo = new ImageView(context);
        imgMyInfo.setImageResource(R.drawable.m_i_setting_myinfo);

        TextView txtMyInfo = new TextView(context);
        txtMyInfo.setText("내 정보 변경");
        txtMyInfo.setTextColor(Color.parseColor("#404040"));
        txtMyInfo.setTextSize(16);
        txtMyInfo.setPadding(pxToDp.dpToPx(23),0,0,0);
        txtMyInfo.setGravity(Gravity.CENTER_VERTICAL);


        btnMyInfo.addView(imgMyInfo, LayoutHelper.createLinear(20,19));
        btnMyInfo.addView(txtMyInfo, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 쇼핑몰 연동 설정 버튼 생성
        btnConnectShop = new LinearLayout(context);
        btnConnectShop.setOrientation(LinearLayout.HORIZONTAL);
        btnConnectShop.setGravity(Gravity.CENTER_VERTICAL);
        btnConnectShop.setOnClickListener(this);

        ImageView imgConnectShop = new ImageView(context);
        imgConnectShop.setImageResource(R.drawable.m_i_main_shop);

        TextView txtConnectShop = new TextView(context);
        txtConnectShop.setText("쇼핑몰 연동 설정");
        txtConnectShop.setTextColor(Color.parseColor("#404040"));
        txtConnectShop.setTextSize(16);
        txtConnectShop.setPadding(pxToDp.dpToPx(23),0,0,0);
        txtConnectShop.setGravity(Gravity.CENTER_VERTICAL);

        btnConnectShop.addView(imgConnectShop, LayoutHelper.createLinear(20,19));
        btnConnectShop.addView(txtConnectShop, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 알림 및 소리 버튼 생성
        btnNotificationCenter = new LinearLayout(context);
        btnNotificationCenter.setOrientation(LinearLayout.HORIZONTAL);
        btnNotificationCenter.setGravity(Gravity.CENTER_VERTICAL);
        btnNotificationCenter.setOnClickListener(this);

        ImageView imgNotificationCenter = new ImageView(context);
        imgNotificationCenter.setImageResource(R.drawable.m_i_setting_alert);

        TextView txtNotificationCenter = new TextView(context);
        txtNotificationCenter.setText("알림 및 설정");
        txtNotificationCenter.setTextColor(Color.parseColor("#404040"));
        txtNotificationCenter.setTextSize(16);
        txtNotificationCenter.setPadding(pxToDp.dpToPx(23),0,0,0);
        txtNotificationCenter.setGravity(Gravity.CENTER_VERTICAL);

        btnNotificationCenter.addView(imgNotificationCenter, LayoutHelper.createLinear(20,19));
        btnNotificationCenter.addView(txtNotificationCenter, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        //첫번째 덩어리에 버튼 3개 붙이기
        lytMySetting.addView(btnMyInfo, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 60));
        lytMySetting.addView(lytLine1);
        lytMySetting.addView(btnConnectShop, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 60));
        lytMySetting.addView(lytLine2);
        lytMySetting.addView(btnNotificationCenter, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 60));

        // 두번째 덩어리 생성
        LinearLayout lytSettingCenter = new LinearLayout(context);
        lytSettingCenter.setOrientation(LinearLayout.VERTICAL);
        lytSettingCenter.setBackgroundColor(Color.WHITE);
        lytSettingCenter.setPadding(pxToDp.dpToPx(20),0,0,0);
        lytSettingCenter.setGravity(Gravity.CENTER_VERTICAL);

        // 고객센터 버튼 생성
        btnCSCenter = new LinearLayout(context);
        btnCSCenter.setOrientation(LinearLayout.HORIZONTAL);
        btnCSCenter.setGravity(Gravity.CENTER_VERTICAL);
        btnCSCenter.setOnClickListener(this);

        ImageView imgCSCenter = new ImageView(context);
        imgCSCenter.setImageResource(R.drawable.m_i_setting_cs);

        TextView txtCSCenter = new TextView(context);
        txtCSCenter.setText("고객센터");
        txtCSCenter.setTextColor(Color.parseColor("#404040"));
        txtCSCenter.setTextSize(16);
        txtCSCenter.setPadding(pxToDp.dpToPx(23),0,0,0);
        txtCSCenter.setGravity(Gravity.CENTER_VERTICAL);

        btnCSCenter.addView(imgCSCenter, LayoutHelper.createLinear(20,19));
        btnCSCenter.addView(txtCSCenter, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 버전 정보 버튼 생성
        btnVersion = new LinearLayout(context);
        btnVersion.setOrientation(LinearLayout.HORIZONTAL);
        btnVersion.setGravity(Gravity.CENTER_VERTICAL);
        btnVersion.setOnClickListener(this);

        ImageView imgVersion = new ImageView(context);
        imgVersion.setImageResource(R.drawable.m_i_setting_version);

        TextView txtVersion = new TextView(context);
        txtVersion.setText("버전정보");
        txtVersion.setTextColor(Color.parseColor("#404040"));
        txtVersion.setTextSize(16);
        txtVersion.setPadding(pxToDp.dpToPx(23),0,0,0);
        txtVersion.setGravity(Gravity.CENTER_VERTICAL);

        TextView txtExposeVersion = new TextView(context);
        txtExposeVersion.setText(BuildVars.BUILD_VERSION_STRING);
        txtExposeVersion.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        txtExposeVersion.setPadding(0, 0, pxToDp.dpToPx(23), 0);
        txtExposeVersion.setTextSize(13);
        txtExposeVersion.setTextColor(Color.parseColor("#999999"));

        btnVersion.addView(imgVersion, LayoutHelper.createLinear(20,19));
        btnVersion.addView(txtVersion, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT));
        btnVersion.addView(txtExposeVersion, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT,LayoutHelper.MATCH_PARENT));

        // 탈퇴 버튼 생성
        btnDisConnect = new LinearLayout(context);
        btnDisConnect.setOrientation(LinearLayout.HORIZONTAL);
        btnDisConnect.setGravity(Gravity.CENTER_VERTICAL);
        btnDisConnect.setOnClickListener(this);

        ImageView imgDisConnect = new ImageView(context);
        imgDisConnect.setImageResource(R.drawable.m_i_main_dis);

        TextView txtDisConnect = new TextView(context);
        txtDisConnect.setText("알림 및 설정");
        txtDisConnect.setTextColor(Color.parseColor("#404040"));
        txtDisConnect.setTextSize(16);
        txtDisConnect.setPadding(pxToDp.dpToPx(23),0,0,0);
        txtDisConnect.setGravity(Gravity.CENTER_VERTICAL);

        btnDisConnect.addView(imgDisConnect, LayoutHelper.createLinear(20,19));
        btnDisConnect.addView(txtDisConnect, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        //첫번째 덩어리에 버튼 3개 붙이기
        lytSettingCenter.addView(btnCSCenter, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 60));
        lytSettingCenter.addView(lytLine3);
        lytSettingCenter.addView(btnVersion, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 60));
        lytSettingCenter.addView(lytLine4);
        lytSettingCenter.addView(btnDisConnect, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 60));

        //settingLayout에 생성한 덩어리들 붙이기
        settingLayout.addView(lytMySetting, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 180));
        settingLayout.addView(lytLine5, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1, 0, 0, 0, 10));
        settingLayout.addView(lytLine6, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1));
        settingLayout.addView(lytSettingCenter, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 180));
        settingLayout.addView(lytLine7, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1));
    }
}
