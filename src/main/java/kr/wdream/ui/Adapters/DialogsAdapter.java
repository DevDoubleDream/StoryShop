/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package kr.wdream.ui.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import kr.wdream.storyshop.AndroidUtilities;
import kr.wdream.storyshop.MessagesController;
import kr.wdream.storyshop.support.widget.RecyclerView;
import kr.wdream.tgnet.TLRPC;
import kr.wdream.ui.Cells.DialogCell;
import kr.wdream.ui.Cells.LoadingCell;

import java.util.ArrayList;

public class DialogsAdapter extends RecyclerView.Adapter {

    private static final String LOG_TAG = "DialogsAdapter";

    private Context mContext;
    private int dialogsType;
    private long openedDialogId;
    private int currentCount;

    private class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }

    public DialogsAdapter(Context context, int type) {
        mContext = context;
        dialogsType = type;

        Log.d(LOG_TAG, "DialogsAdapter initiate : " + dialogsType);
    }

    public void setOpenedDialogId(long id) {
        openedDialogId = id;
    }

    public boolean isDataSetChanged() {
        int current = currentCount;
        return current != getItemCount() || current == 1;
    }


    // DialogsActivity의 List에 표시 될 목록 가져오기
    private ArrayList<TLRPC.TL_dialog> getDialogsArray() {
        if (dialogsType == 0) {
            Log.d(LOG_TAG, "MessagesController.dialogs : " + MessagesController.getInstance().dialogs);
            return MessagesController.getInstance().dialogs;
        } else if (dialogsType == 1) {
            return MessagesController.getInstance().dialogsServerOnly;
        } else if (dialogsType == 2) {
            return MessagesController.getInstance().dialogsGroupsOnly;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        int count = getDialogsArray().size();
        if (count == 0 && MessagesController.getInstance().loadingDialogs) {
            return 0;
        }
        if (!MessagesController.getInstance().dialogsEndReached) {
            count++;
        }
        currentCount = count;
        return count;
    }

    public TLRPC.TL_dialog getItem(int i) {
        ArrayList<TLRPC.TL_dialog> arrayList = getDialogsArray();
        if (i < 0 || i >= arrayList.size()) {
            return null;
        }
        return arrayList.get(i);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof DialogCell) {
            ((DialogCell) holder.itemView).checkCurrentDialogIndex();
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.d(LOG_TAG, "onCreateViewHolder");
        View view = null;
        if (viewType == 0) {
            view = new DialogCell(mContext);
        } else if (viewType == 1) {
            view = new LoadingCell(mContext);
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 0) {
            DialogCell cell = (DialogCell) viewHolder.itemView;
            cell.useSeparator = (i != getItemCount() - 1);
            TLRPC.TL_dialog dialog = getItem(i);
            if (dialogsType == 0) {
                if (AndroidUtilities.isTablet()) {
                    cell.setDialogSelected(dialog.id == openedDialogId);
                }
            }
            cell.setDialog(dialog, i, dialogsType);
        }
    }

    @Override
    public int getItemViewType(int i) {
        if (i == getDialogsArray().size()) {
            return 1;
        }
        return 0;
    }
}
