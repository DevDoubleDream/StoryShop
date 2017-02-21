/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package kr.wdream.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import kr.wdream.storyshop.AndroidUtilities;
import kr.wdream.storyshop.LocaleController;
import kr.wdream.storyshop.MessagesController;
import kr.wdream.storyshop.UserConfig;
import kr.wdream.ui.Cells.DrawerActionCell;
import kr.wdream.ui.Cells.DividerCell;
import kr.wdream.ui.Cells.EmptyCell;
import kr.wdream.ui.Cells.DrawerProfileCell;

public class DrawerLayoutAdapter extends BaseAdapter {

    private Context mContext;

    public DrawerLayoutAdapter(Context context) {
        mContext = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return !(i == 1 || i == 5);
    }

    @Override
    public int getCount() {
        return UserConfig.isClientActivated() ? 10 : 0;
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
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int type = getItemViewType(i);
        if (type == 0) {
            DrawerProfileCell drawerProfileCell;
            if (view == null) {
                view = drawerProfileCell = new DrawerProfileCell(mContext);
            } else {
                drawerProfileCell = (DrawerProfileCell) view;
            }
            drawerProfileCell.setUser(MessagesController.getInstance().getUser(UserConfig.getClientUserId()));
        } else if (type == 1) {
            if (view == null) {
                view = new EmptyCell(mContext, AndroidUtilities.dp(8));
            }
        } else if (type == 2) {
            if (view == null) {
                view = new DividerCell(mContext);
            }
        } else if (type == 3) {
            if (view == null) {
                view = new DrawerActionCell(mContext);
            }
            DrawerActionCell actionCell = (DrawerActionCell) view;
            if (i == 2) {
                actionCell.setTextAndIcon(LocaleController.getString("NewGroup", kr.wdream.storyshop.R.string.NewGroup), kr.wdream.storyshop.R.drawable.menu_newgroup);
            } else if (i == 3) {
                actionCell.setTextAndIcon(LocaleController.getString("NewSecretChat", kr.wdream.storyshop.R.string.NewSecretChat), kr.wdream.storyshop.R.drawable.menu_secret);
            } else if (i == 4) {
                actionCell.setTextAndIcon(LocaleController.getString("NewChannel", kr.wdream.storyshop.R.string.NewChannel), kr.wdream.storyshop.R.drawable.menu_broadcast);
            } else if (i == 6) {
                actionCell.setTextAndIcon(LocaleController.getString("Contacts", kr.wdream.storyshop.R.string.Contacts), kr.wdream.storyshop.R.drawable.menu_contacts);
            } else if (i == 7) {
                actionCell.setTextAndIcon(LocaleController.getString("InviteFriends", kr.wdream.storyshop.R.string.InviteFriends), kr.wdream.storyshop.R.drawable.menu_invite);
            } else if (i == 8) {
                actionCell.setTextAndIcon(LocaleController.getString("Settings", kr.wdream.storyshop.R.string.Settings), kr.wdream.storyshop.R.drawable.menu_settings);
            } else if (i == 9) {
                actionCell.setTextAndIcon(LocaleController.getString("TelegramFaq", kr.wdream.storyshop.R.string.TelegramFaq), kr.wdream.storyshop.R.drawable.menu_help);
            }
        }

        return view;
    }

    @Override
    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        } else if (i == 1) {
            return 1;
        } else if (i == 5) {
            return 2;
        }
        return 3;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return !UserConfig.isClientActivated();
    }
}
