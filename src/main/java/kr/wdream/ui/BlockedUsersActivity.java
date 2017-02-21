/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package kr.wdream.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import kr.wdream.PhoneFormat.PhoneFormat;
import kr.wdream.storyshop.LocaleController;
import kr.wdream.tgnet.TLRPC;
import kr.wdream.storyshop.MessagesController;
import kr.wdream.storyshop.NotificationCenter;
import kr.wdream.ui.Adapters.BaseFragmentAdapter;
import kr.wdream.ui.Cells.TextInfoCell;
import kr.wdream.ui.Cells.UserCell;
import kr.wdream.ui.ActionBar.ActionBar;
import kr.wdream.ui.ActionBar.ActionBarMenu;
import kr.wdream.ui.ActionBar.BaseFragment;
import kr.wdream.ui.Components.LayoutHelper;

public class BlockedUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ContactsActivity.ContactsActivityDelegate {

    private static final String TAG = "BlockedUsersActivity";

    private ListView listView;
    private ListAdapter listViewAdapter;
    private FrameLayout progressView;
    private TextView emptyTextView;
    private int selectedUserId;


    private final static int block_user = 1;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        Log.d(TAG, "onFragmentCreate()");
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
        MessagesController.getInstance().getBlockedUsers(false);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        Log.d(TAG, "onFragmentDestory()");
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
    }

    @Override
    public View createView(Context context) {
        Log.d(TAG, "createView");
        actionBar.setBackButtonImage(kr.wdream.storyshop.R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("BlockedUsers", kr.wdream.storyshop.R.string.BlockedUsers));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                Log.d(TAG, "ActionBarMenu Item Click");
                if (id == -1) {
                    Log.d(TAG, "뒤로가기 버튼 클릭");
                    finishFragment();
                } else if (id == block_user) {
                    Log.d(TAG, "차단 친구 추가하기");
                    Bundle args = new Bundle();
                    args.putBoolean("onlyUsers", true);
                    args.putBoolean("destroyAfterSelect", true);
                    args.putBoolean("returnAsResult", true);
                    ContactsActivity fragment = new ContactsActivity(args);
                    fragment.setDelegate(BlockedUsersActivity.this);

                    presentFragment(fragment);
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(block_user, kr.wdream.storyshop.R.drawable.plus);

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        emptyTextView = new TextView(context);
        emptyTextView.setTextColor(0xff808080);
        emptyTextView.setTextSize(20);
        emptyTextView.setGravity(Gravity.CENTER);
        emptyTextView.setVisibility(View.INVISIBLE);
        emptyTextView.setText(LocaleController.getString("NoBlocked", kr.wdream.storyshop.R.string.NoBlocked));

        // 차단 친구 없을 경우 표시 될 TextView 추가
        frameLayout.addView(emptyTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        emptyTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        progressView = new FrameLayout(context);
        frameLayout.addView(progressView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ProgressBar progressBar = new ProgressBar(context);
        progressView.addView(progressBar, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        listView = new ListView(context);
        listView.setEmptyView(emptyTextView);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setAdapter(listViewAdapter = new ListAdapter(context));
        listView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "차단 친구목록 클릭");
                if (i < MessagesController.getInstance().blockedUsers.size()) {
                    Bundle args = new Bundle();
                    args.putInt("user_id", MessagesController.getInstance().blockedUsers.get(i));
                    Log.d(TAG, "blockedUsers.get(i) : " + MessagesController.getInstance().blockedUsers.get(i));
                    presentFragment(new ProfileActivity(args));
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < 0 || i >= MessagesController.getInstance().blockedUsers.size() || getParentActivity() == null) {
                    return true;
                }
                selectedUserId = MessagesController.getInstance().blockedUsers.get(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                CharSequence[] items = new CharSequence[]{LocaleController.getString("Unblock", kr.wdream.storyshop.R.string.Unblock)};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            MessagesController.getInstance().unblockUser(selectedUserId);
                        }
                    }
                });
                showDialog(builder.create());

                return true;
            }
        });

        if (MessagesController.getInstance().loadingBlockedUsers) {
            progressView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            listView.setEmptyView(null);
        } else {
            progressView.setVisibility(View.GONE);
            listView.setEmptyView(emptyTextView);
        }
        return fragmentView;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = (Integer)args[0];
            if ((mask & MessagesController.UPDATE_MASK_AVATAR) != 0 || (mask & MessagesController.UPDATE_MASK_NAME) != 0) {
                updateVisibleRows(mask);
                Log.d(TAG, "UPDATE_MASK_AVATAR != 0");
            }
        } else if (id == NotificationCenter.blockedUsersDidLoaded) {
            if (progressView != null) {
                progressView.setVisibility(View.GONE);
            }
            if (listView != null && listView.getEmptyView() == null) {
                listView.setEmptyView(emptyTextView);
            }
            if (listViewAdapter != null) {
                listViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateVisibleRows(int mask) {
        if (listView == null) {
            return;
        }
        int count = listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = listView.getChildAt(a);
            if (child instanceof UserCell) {
                ((UserCell) child).update(mask);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listViewAdapter != null) {
            listViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void didSelectContact(final TLRPC.User user, String param) {
        Log.d(TAG,"didSelectContact");
        if (user == null) {
            return;
        }
        MessagesController.getInstance().blockUser(user.id);
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return i != MessagesController.getInstance().blockedUsers.size();
        }

        @Override
        public int getCount() {
            if (MessagesController.getInstance().blockedUsers.isEmpty()) {
                return 0;
            }
            return MessagesController.getInstance().blockedUsers.size() + 1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    view = new UserCell(mContext, 1, 0, false);
                }
                TLRPC.User user = MessagesController.getInstance().getUser(MessagesController.getInstance().blockedUsers.get(i));
                if (user != null) {
                    String number;
                    if (user.bot) {
                        number = LocaleController.getString("Bot", kr.wdream.storyshop.R.string.Bot).substring(0, 1).toUpperCase() + LocaleController.getString("Bot", kr.wdream.storyshop.R.string.Bot).substring(1);
                    } else if (user.phone != null && user.phone.length() != 0) {
                        number = PhoneFormat.getInstance().format("+" + user.phone);
                    } else {
                        number = LocaleController.getString("NumberUnknown", kr.wdream.storyshop.R.string.NumberUnknown);
                    }
                    ((UserCell) view).setData(user, null, number, 0);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new TextInfoCell(mContext);
                    ((TextInfoCell) view).setText(LocaleController.getString("UnblockText", kr.wdream.storyshop.R.string.UnblockText));
                }
            }
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            if(i == MessagesController.getInstance().blockedUsers.size()) {
                return 1;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEmpty() {
            return MessagesController.getInstance().blockedUsers.isEmpty();
        }
    }
}
