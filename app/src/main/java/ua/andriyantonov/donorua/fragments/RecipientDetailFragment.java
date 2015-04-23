package ua.andriyantonov.donorua.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.data.Utils;
import ua.andriyantonov.donorua.data.DonorContract.RecipientsEntry;

import static ua.andriyantonov.donorua.data.DonorContract.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipientDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = RecipientDetailFragment.class.getSimpleName();

    private final static String SCROLL_POSITION_KEY = "ntk scroll position key";
    public static final String RECIPIENT_SHARE_HASHTAG = "#DonorUA";
    public static final String RECIP_DETAIL_URI = "RECIP_DETAIL_URI";
    private String mShareStr, mPhoneStr, mCenterLong, mCenterLat, mCenterNameStr;
    private ShareActionProvider mShareActionProvider;
    private Boolean mImageLoaded = false, fragmentLoaded;
    private Uri mUri;

    private static final int RECIPIENT_DETAIL_LOADER = 0;

    private static final String[] RESIPIENTS_COLUMS = {
            RecipientsEntry.TABLE_NAME + "." + RecipientsEntry._ID,
            RecipientsEntry.COLUMN_LAST_NAME,
            RecipientsEntry.COLUMN_FIRST_NAME,
            RecipientsEntry.COLUMN_BIRTH_DAY,
            RecipientsEntry.COLUMN_BLOOD_GROUP_ID,
            RecipientsEntry.COLUMN_DONATION_TYPE,
            RecipientsEntry.COLUMN_DISEASE,
            RecipientsEntry.COLUMN_CONTACT_PERSON,
            RecipientsEntry.COLUMN_CONTACT_PHONE,
            RecipientsEntry.COLUMN_DESC,
            RecipientsEntry.COLUMN_PHOTO_IMAGE,
            CentersEntry.COLUMN_CENTER_NAME,
            CentersEntry.COLUMN_ADDRESS,
            CentersEntry.COLUMN_LONG,
            CentersEntry.COLUMN_LAT,
    };

    public static final int COL_REC_ENTRY_ID = 0;
    public static final int COL_REC_LAST_NAME = 1;
    public static final int COL_REC_FIRST_NAME = 2;
    public static final int COL_REC_BIRTH_DAY = 3;
    public static final int COL_REC_BLOOD_GR = 4;
    public static final int COL_REC_DON_TYPE = 5;
    public static final int COL_REC_DISEASE = 6;
    public static final int COL_REC_CONT_PERSON = 7;
    public static final int COL_REC_CONT_PHONE = 8;
    public static final int COL_REC_DESC = 9;
    public static final int COL_REC_IMAGE = 10;
    public static final int COL_CENT_NAME = 11;
    public static final int COL_CENT_ADDRESS = 12;
    public static final int COL_CENT_LONG = 13;
    public static final int COL_CENT_LAT = 14;

    @InjectView(R.id.detail_recipient_name_textview) TextView mRecName;
    @InjectView(R.id.detail_recipient_disease_textview) TextView mRecDisease;
    @InjectView(R.id.detail_recipient_bloodgroup_textview) TextView mRecBloodGroup;
    @InjectView(R.id.detail_recipient_need_donation_textview) TextView mRecNeedDonation;
    @InjectView(R.id.detail_recipient_center_name_textview) TextView mRecCenterName;
    @InjectView(R.id.detail_recipient_center_address_textview) TextView mRecCenterAddress;
    @InjectView(R.id.detail_recipient_contact_person_textview) TextView mRecContactPerson;
    @InjectView(R.id.detail_recipient_contact_phone_textview) TextView mRecContactPhone;
    @InjectView(R.id.detail_recipient_description_textview) TextView mRecDescription;
    @InjectView(R.id.detail_recipient_imageview) ImageView mRecImage;
    @InjectView(R.id.loadingPhoto_progressbar) ProgressBar mProgressBar;
    @InjectView(R.id.recipient_detail_main_layout) ScrollView sRecipientDetailLayout;


    public RecipientDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipient_detail,container,false);
        ButterKnife.inject(this, rootView);
        fragmentLoaded = true;
        if (RecipientsFragment.sRecipientChosen){
            sRecipientDetailLayout.setVisibility(View.VISIBLE);
        }

        Bundle arguments = getArguments();
        if (arguments != null){
            mUri = arguments.getParcelable(RECIP_DETAIL_URI);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLL_POSITION_KEY)){
            final int[] scrollPosition = savedInstanceState.getIntArray(SCROLL_POSITION_KEY);
            sRecipientDetailLayout.post(new Runnable() {
                @Override
                public void run() {
                    sRecipientDetailLayout.scrollTo(scrollPosition[0], scrollPosition[1]);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putIntArray(SCROLL_POSITION_KEY, new int[]{sRecipientDetailLayout.getScrollX(),
                sRecipientDetailLayout.getScrollY()});
        super.onSaveInstanceState(bundle);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        if (mProgressBar != null){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        fragmentLoaded = false;
    }

    /**
     * Calls contactPhone in case callButton was pressed
     */
    @OnClick(R.id.call_button)
    public void Call(ImageButton imageButton){
        if (mPhoneStr != null){
            dialogToCall();
        }
    }

    /**
     * Shows center location on map
     */
    @OnClick(R.id.show_center_on_map)
    public void ShowCenterOnMap(ImageButton imageButton){
        if (mCenterLong != null && mCenterLat != null && mCenterNameStr != null){
           dialogShowCenterOnMap();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        if (fragmentLoaded){
            inflater.inflate(R.menu.menu_recipient_detail, menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            if (mShareStr != null){
                mShareActionProvider.setShareIntent(createSharedRecipientIntent());
            }
        }
    }

    /**
     * Creates Intent for SharedIntentProvider.
     * If mImageLoaded is true - share image + text,
     * if false - share just text.
     */
    private Intent createSharedRecipientIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (mImageLoaded){
            Drawable mDrawable = mRecImage.getDrawable();
            Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();

            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                    mBitmap, getString(R.string.app_name), null);

            Uri bmpUri = Uri.parse(path);

            shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, mShareStr + RECIPIENT_SHARE_HASHTAG);

            shareIntent.setType("text|image/*");
        } else {
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mShareStr + RECIPIENT_SHARE_HASHTAG);
        }
        return shareIntent;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(RECIPIENT_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void dialogToCall(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Зателефонувати?");
        alertDialog.setMessage(mRecContactPerson.getText().toString() + ": \n" +
                mRecContactPhone.getText().toString());
        alertDialog.setButton("Так", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(Utils.getMobileNum(mPhoneStr)));
                startActivity(intent);
            }
        });
        alertDialog.setButton2("Ні", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setIcon(R.mipmap.ic_call);
        alertDialog.show();
    }

    private void dialogShowCenterOnMap(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Показати лікарню на мапі?");
        alertDialog.setMessage(mCenterNameStr);
        alertDialog.setButton("Так", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri geoLocation = Uri.parse("geo:0,0?q=" + mCenterLat + "," + mCenterLong +"(" + mCenterNameStr + ")");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
                }
            }
        });
        alertDialog.setButton2("Ні", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setIcon(R.mipmap.ic_show_center_on_map);
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri){
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    RESIPIENTS_COLUMS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (cursor != null && cursor.moveToFirst()) {

            String lastNameStr = cursor.getString(COL_REC_LAST_NAME);
            String firstNameStr = cursor.getString(COL_REC_FIRST_NAME);
            mRecName.setText(lastNameStr + " " + firstNameStr);

            String birthDay = cursor.getString(COL_REC_BIRTH_DAY);

            //TODO потім якось десь колись виправити конвертер віку
//            mRecAge.setText(Utils.getCorrectAgeFormat(birthDay));

            String diseaseStr = cursor.getString(COL_REC_DISEASE);
            mRecDisease.setText(diseaseStr);

            int bloodGroupInt = cursor.getInt(COL_REC_BLOOD_GR);
            mRecBloodGroup.setText(Utils.getBloodGroupFormat(getActivity(), bloodGroupInt));

            int needDonationStr = cursor.getInt(COL_REC_DON_TYPE);
            mRecNeedDonation.setText(Utils.getNeededDonationType(needDonationStr));

            mCenterNameStr = cursor.getString(COL_CENT_NAME);
            mRecCenterName.setText(mCenterNameStr);

            String centerAddressStr = cursor.getString(COL_CENT_ADDRESS);
            mRecCenterAddress.setText(centerAddressStr);

            String personStr = cursor.getString(COL_REC_CONT_PERSON);
            mRecContactPerson.setText(personStr);

            mPhoneStr = cursor.getString(COL_REC_CONT_PHONE);
            mRecContactPhone.setText(mPhoneStr);

            String descriptionStr = cursor.getString(COL_REC_DESC);
            mRecDescription.setText(descriptionStr);

            mCenterLong = cursor.getString(COL_CENT_LONG);
            mCenterLat = cursor.getString(COL_CENT_LAT);

            String imageUrl = cursor.getString(COL_REC_IMAGE);
            if (Utils.checkConnection(getActivity())){
                mProgressBar.setVisibility(View.VISIBLE);
            }
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .resize(300, 300)
                    .into(mRecImage, new Callback(){
                        @Override
                        public void onSuccess() {
                            if (!mImageLoaded && mShareActionProvider != null &&
                                    fragmentLoaded){
                                if (mShareStr != null){
                                    mImageLoaded = true;
                                    mShareActionProvider.setShareIntent(createSharedRecipientIntent());
                                }
                            }
                            if (fragmentLoaded){
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                        @Override
                        public void onError() {
                            if (fragmentLoaded){
                                mRecImage.setImageResource(R.mipmap.ic_launcher);
                            }
                        }
                    });

            mShareStr = String.format(getActivity().getString(R.string.help_to_find),
                    mRecBloodGroup.getText(),
                    mRecName.getText(),
                    personStr,
                    mPhoneStr,
                    centerAddressStr);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void userInfoChanged() {
        sRecipientDetailLayout.setVisibility(View.INVISIBLE);
    }


}
