/*
 * This is the source code of Telegram for Android v. 1.3.x.
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
import kr.wdream.storyshop.LocaleController;
import kr.wdream.tgnet.TLRPC;
import kr.wdream.storyshop.ContactsController;
import kr.wdream.storyshop.MessagesController;
import kr.wdream.storyshop.R;
import kr.wdream.ui.Cells.DividerCell;
import kr.wdream.ui.Cells.GreySectionCell;
import kr.wdream.ui.Cells.LetterSectionCell;
import kr.wdream.ui.Cells.TextCell;
import kr.wdream.ui.Cells.UserCell;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactsAdapter extends BaseSectionsAdapter {

    private static final String LOG_TAG = "ContactsAdapter";

    private Context mContext;
    private int onlyUsers;
    private boolean needPhonebook;
    private HashMap<Integer, TLRPC.User> ignoreUsers;
    private HashMap<Integer, ?> checkedMap;
    private boolean scrolling;
    private boolean isAdmin;

    public ContactsAdapter(Context context, int onlyUsersType, boolean arg2, HashMap<Integer, TLRPC.User> arg3, boolean arg4) {
        Log.d("ContactsAdapter", "onCreate");
        mContext = context;
        onlyUsers = onlyUsersType;
        needPhonebook = arg2;
        ignoreUsers = arg3;
        isAdmin = arg4;
    }

    public void setCheckedMap(HashMap<Integer, ?> map) {
        checkedMap = map;
    }

    public void setIsScrolling(boolean value) {
        scrolling = value;
    }

    @Override
    public Object getItem(int section, int position) {
        Log.d(LOG_TAG, "onlyUsers : " + onlyUsers);
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;

        Log.d(LOG_TAG, "usersSectionsDist : " + usersSectionsDict);
        Log.d(LOG_TAG, "sortedUsersSectionsArray : " + sortedUsersSectionsArray);
        if (onlyUsers != 0 && !isAdmin) {
            if (section < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
                if (position < arr.size()) {
                    Log.d("ContactsAdapter", "user_id : " + MessagesController.getInstance().getUser(arr.get(position).user_id));
                    return MessagesController.getInstance().getUser(arr.get(position).user_id);
                }
            }
            return null;
        } else {
            if (section == 0) {
                return null;
            } else {
                if (section - 1 < sortedUsersSectionsArray.size()) {
                    ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                    if (position < arr.size()) {
                        return MessagesController.getInstance().getUser(arr.get(position).user_id);
                    }
                    return null;
                }
            }
        }

        Log.d(LOG_TAG, "needPhonebook : " + needPhonebook);

        if (needPhonebook) {
            return ContactsController.getInstance().phoneBookContacts.get(position);
        }
        return null;
    }

    @Override
    public boolean isRowEnabled(int section, int row) {
        Log.d("ContactsAdapter", "isRowEnabled");
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;

        Log.d(LOG_TAG, "usersSectionsDict : " + usersSectionsDict);
        Log.d(LOG_TAG, "sortedUsersSectionsArray : " + sortedUsersSectionsArray);

        if (onlyUsers != 0 && !isAdmin) {
//여기 안찍혔음
            ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
            Log.d(LOG_TAG, "row : " + row);
            Log.d(LOG_TAG, "arr : " + arr);

            return row < arr.size();
        } else {

            Log.d(LOG_TAG, "onlyUser : " + onlyUsers);
            Log.d(LOG_TAG, "isAdmin : " + isAdmin);

            if (section == 0) {
                Log.d(LOG_TAG, "section : " + section);
                if (needPhonebook || isAdmin) {
                    Log.d(LOG_TAG, "needPhonebook + is Admin = true");
                    if (row == 1) {
                        Log.d(LOG_TAG, "row == 1");
                        return false;
                    }
                } else {
                    Log.d(LOG_TAG, "needPhonebook + is Admin = false");
                    if (row == 3) {
                        Log.d(LOG_TAG, "row == 3");
                        return false;
                    }
                }
                return true;
            } else if (section - 1 < sortedUsersSectionsArray.size()) {

                Log.d(LOG_TAG, "section : " + section);

                ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                return row < arr.size();
            }
        }
        return true;
    }

    @Override
    public int getSectionCount() {
        Log.d("ContactsAdapter", "getSectionCount");

        ArrayList<String> sortedUsersSectionsArray = onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
        int count = sortedUsersSectionsArray.size();
        if (onlyUsers == 0) {
            count++;
        }
        if (isAdmin) {
            count++;
        }
        if (needPhonebook) {
            count++;
        }
        return count;
    }

    @Override
    public int getCountForSection(int section) {
        Log.d("ContactsAdapter", "getCountForSection : " + section);

        // 메신저에 등록되어 있는 친구 리스트 (이름 앞글자 + 상대방 아이디 ? 정도인듯)
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;

        Log.d("ContactsAdapter", "usersSectionsDict : " + usersSectionsDict);

        //유저 이름(앞글자)
        ArrayList<String> sortedUsersSectionsArray = onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
        Log.d("ContactsAdapter", "sortedUsersSectionsArray : " + sortedUsersSectionsArray);

        if (onlyUsers != 0 && !isAdmin) {
            Log.d("ContactsAdapter", "onlyUser : " + onlyUsers + ", isAdmin : " + isAdmin);
            if (section < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
                Log.d("ContactsAdapter", "arr : " + arr);
                int count = arr.size();
                if (section != (sortedUsersSectionsArray.size() - 1) || needPhonebook) {
                    count++;
                }
                Log.d(LOG_TAG, "count : :: : : : : " + section + " "+ count);
                return count;
            }
        } else {
            if (section == 0) {
                if (needPhonebook || isAdmin) {
                    Log.d("ContactsAdapter", "return 2");
                    return 2;
                } else {
                    Log.d("ContactsAdapter", "return 4");
                    return 4;
                }
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                Log.d("ContactsAdapter", "section < sortedUsers : " + arr);
                int count = arr.size();
                if (section - 1 != (sortedUsersSectionsArray.size() - 1) || needPhonebook) {
                    count++;
                }
                return count;
            }
        }
        if (needPhonebook) {
            return ContactsController.getInstance().phoneBookContacts.size();
        }
        return 0;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        Log.d("ContactsAdapter", "getSectionHeaderView");
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;

        if (convertView == null) {
            convertView = new LetterSectionCell(mContext);
        }
        if (onlyUsers != 0 && !isAdmin) {
            if (section < sortedUsersSectionsArray.size()) {
                ((LetterSectionCell) convertView).setLetter(sortedUsersSectionsArray.get(section));
            } else {
                ((LetterSectionCell) convertView).setLetter("");
            }
        } else {
            if (section == 0) {
                ((LetterSectionCell) convertView).setLetter("");
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                ((LetterSectionCell) convertView).setLetter(sortedUsersSectionsArray.get(section - 1));
            } else {
                ((LetterSectionCell) convertView).setLetter("");
            }
        }
        return convertView;
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        Log.d("ContactsAdapter", "getItemView");
        int type = getItemViewType(section, position);
        if (type == 4) {
            if (convertView == null) {
                convertView = new DividerCell(mContext);
                convertView.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 28 : 72), 0, AndroidUtilities.dp(LocaleController.isRTL ? 72 : 28), 0);
            }
        } else if (type == 3) {
            if (convertView == null) {
                convertView = new GreySectionCell(mContext);
                ((GreySectionCell) convertView).setText(LocaleController.getString("Contacts", R.string.Contacts).toUpperCase());
            }
        } else if (type == 2) {
            if (convertView == null) {
                convertView = new TextCell(mContext);
            }
            TextCell actionCell = (TextCell) convertView;
            if (needPhonebook) {
                actionCell.setTextAndIcon(LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.menu_invite);
            } else if (isAdmin) {
                actionCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink), R.drawable.menu_invite);
            } else {
                if (position == 0) {
                    actionCell.setTextAndIcon(LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_newgroup);
                } else if (position == 1) {
                    actionCell.setTextAndIcon(LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret);
                } else if (position == 2) {
                    actionCell.setTextAndIcon(LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast);
                }
            }
        } else if (type == 1) {
            if (convertView == null) {
                convertView = new TextCell(mContext);
            }
            ContactsController.Contact contact = ContactsController.getInstance().phoneBookContacts.get(position);
            TextCell textCell = (TextCell) convertView;
            if (contact.first_name != null && contact.last_name != null) {
                textCell.setText(contact.first_name + " " + contact.last_name);
            } else if (contact.first_name != null && contact.last_name == null) {
                textCell.setText(contact.first_name);
            } else {
                textCell.setText(contact.last_name);
            }
        } else if (type == 0) {
            if (convertView == null) {
                convertView = new UserCell(mContext, 58, 1, false);
                ((UserCell) convertView).setStatusColors(0xffa8a8a8, 0xff3b84c0);
            }

            HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
            ArrayList<String> sortedUsersSectionsArray = onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;

            ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - (onlyUsers != 0 && !isAdmin ? 0 : 1)));
            TLRPC.User user = MessagesController.getInstance().getUser(arr.get(position).user_id);
            ((UserCell) convertView).setData(user, null, null, 0);
            if (checkedMap != null) {
                ((UserCell) convertView).setChecked(checkedMap.containsKey(user.id), !scrolling);
            }
            if (ignoreUsers != null) {
                if (ignoreUsers.containsKey(user.id)) {
                    convertView.setAlpha(0.5f);
                } else {
                    convertView.setAlpha(1.0f);
                }
            }
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int section, int position) {
        Log.d("ContactsAdapter", "getItemViewType");
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
        if (onlyUsers != 0 && !isAdmin) {
            ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
            return position < arr.size() ? 0 : 4;
        } else {
            if (section == 0) {
                if (needPhonebook || isAdmin) {
                    if (position == 1) {
                        return 3;
                    }
                } else {
                    if (position == 3) {
                        return 3;
                    }
                }
                return 2;
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                return position < arr.size() ? 0 : 4;
            }
        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }
}
