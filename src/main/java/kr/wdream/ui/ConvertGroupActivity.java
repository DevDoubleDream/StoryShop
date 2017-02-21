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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import kr.wdream.storyshop.AndroidUtilities;
import kr.wdream.storyshop.LocaleController;
import kr.wdream.storyshop.MessagesController;
import kr.wdream.storyshop.NotificationCenter;
import kr.wdream.ui.ActionBar.ActionBar;
import kr.wdream.ui.ActionBar.BaseFragment;
import kr.wdream.ui.Adapters.BaseFragmentAdapter;
import kr.wdream.ui.Cells.TextInfoPrivacyCell;
import kr.wdream.ui.Cells.TextSettingsCell;
import kr.wdream.ui.Components.LayoutHelper;

public class ConvertGroupActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private static final String LOG_TAG ="ConvertGroupActivity";

    private ListAdapter listAdapter;

    private int convertInfoRow;
    private int convertRow;
    private int convertDetailRow;
    private int rowCount;

    private int chat_id;

    public ConvertGroupActivity(Bundle args) {
        super(args);
        chat_id = args.getInt("chat_id");
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        convertInfoRow = rowCount++;
        convertRow = rowCount++;
        convertDetailRow = rowCount++;

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);

        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    }

    @Override
    public View createView(Context context) {
        Log.d(LOG_TAG, "createView");

        actionBar.setBackButtonImage(kr.wdream.storyshop.R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("ConvertGroup", kr.wdream.storyshop.R.string.ConvertGroup));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(0xfff0f0f0);

        ListView listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                if (i == convertRow) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("ConvertGroupAlert", kr.wdream.storyshop.R.string.ConvertGroupAlert));
                    builder.setTitle(LocaleController.getString("ConvertGroupAlertWarning", kr.wdream.storyshop.R.string.ConvertGroupAlertWarning));
                    builder.setPositiveButton(LocaleController.getString("OK", kr.wdream.storyshop.R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MessagesController.getInstance().convertToMegaGroup(getParentActivity(), chat_id);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", kr.wdream.storyshop.R.string.Cancel), null);
                    showDialog(builder.create());
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
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
            return i == convertRow;
        }

        @Override
        public int getCount() {
            return rowCount;
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
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(0xffffffff);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == convertRow) {
                    textCell.setText(LocaleController.getString("ConvertGroup", kr.wdream.storyshop.R.string.ConvertGroup), false);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new TextInfoPrivacyCell(mContext);
                }
                if (i == convertInfoRow) {
                    ((TextInfoPrivacyCell) view).setText(AndroidUtilities.replaceTags(LocaleController.getString("ConvertGroupInfo2", kr.wdream.storyshop.R.string.ConvertGroupInfo2)));
                    view.setBackgroundResource(kr.wdream.storyshop.R.drawable.greydivider);
                } else if (i == convertDetailRow) {
                    ((TextInfoPrivacyCell) view).setText(AndroidUtilities.replaceTags(LocaleController.getString("ConvertGroupInfo3", kr.wdream.storyshop.R.string.ConvertGroupInfo3)));
                    view.setBackgroundResource(kr.wdream.storyshop.R.drawable.greydivider_bottom);
                }
            }
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == convertRow) {
                return 0;
            } else if (i == convertInfoRow || i == convertDetailRow) {
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
            return false;
        }
    }
}
