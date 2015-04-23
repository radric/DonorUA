package ua.andriyantonov.donorua.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.data.Utils;
import ua.andriyantonov.donorua.fragments.RecipientsFragment;

/**
 * Created by andriy on 07.04.15.
 */
public class RecipientsAdapter extends CursorAdapter {

    public static class ViewHolder{
        public final TextView recipientName;
        public final TextView bloodGroup;
        public final TextView centerName;
        public final TextView centerAddress;

        public ViewHolder (View view) {
            recipientName = (TextView)view.findViewById(R.id.list_item_recipient_name_textview);
            bloodGroup = (TextView)view.findViewById(R.id.list_item_recipient_bloodgroup_textview);
            centerName = (TextView)view.findViewById(R.id.list_item_recipient_center_name_textview);
            centerAddress = (TextView)view.findViewById(R.id.list_item_recipient_center_address_textview);
        }
    }

    public RecipientsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_recipients, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder)view.getTag();

        String lastNameStr = cursor.getString(RecipientsFragment.COL_REC_LAST_NAME);
        String firstNameStr = cursor.getString(RecipientsFragment.COL_REC_FIRST_NAME);
        viewHolder.recipientName.setText(lastNameStr + " " + firstNameStr);

        int bloodGroupInt = cursor.getInt(RecipientsFragment.COL_REC_BLOOD_GR);
        viewHolder.bloodGroup.setText(Utils.getBloodGroupFormat(context, bloodGroupInt));

        String centerNameStr = cursor.getString(RecipientsFragment.COL_CENT_NAME);
        viewHolder.centerName.setText(centerNameStr);

        String centerAddress = cursor.getString(RecipientsFragment.COL_CENT_ADDRESS);
        viewHolder.centerAddress.setText(centerAddress);
    }
}
