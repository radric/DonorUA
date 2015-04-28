package ua.andriyantonov.donorua.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.activities.RecipientsActivity;
import ua.andriyantonov.donorua.data.DonorContract;
import ua.andriyantonov.donorua.adapter.RecipientsAdapter;
import ua.andriyantonov.donorua.sync.DonorSyncAdapter;

import static ua.andriyantonov.donorua.data.DonorContract.*;

/**
 * Created by andriy on 30.03.15.
 */
public class RecipientsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private RecipientsAdapter mRecipientsAdapter;
    private final static int RECIPIENTS_LOADER = 0;
    private final static String RECIPIENT_CHOSEN_KEY = "recipient_chosen_key";
    private static final String POSITION_KEY = "selected_position";
    private int mPosition = ListView.INVALID_POSITION;
    public static boolean sRecipientChosen;

    @InjectView(R.id.no_needed_ll) LinearLayout mLinearLayout;
    @InjectView(R.id.listview_recipients) ListView mListView;

    private static final String[] RESIPIENTS_COLUMS = {
            RecipientsEntry.TABLE_NAME + "." + RecipientsEntry._ID,
            RecipientsEntry.COLUMN_LAST_NAME,
            RecipientsEntry.COLUMN_FIRST_NAME,
            RecipientsEntry.COLUMN_BLOOD_GROUP_ID,
            CentersEntry.COLUMN_CENTER_NAME,
            CentersEntry.COLUMN_ADDRESS,
    };

    public static final int COL_REC_ENTRY_ID = 0;
    public static final int COL_REC_LAST_NAME = 1;
    public static final int COL_REC_FIRST_NAME = 2;
    public static final int COL_REC_BLOOD_GR = 3;
    public static final int COL_CENT_NAME = 4;
    public static final int COL_CENT_ADDRESS = 5;

    public interface Callback{
        public void onItemSelected(Uri uri);
    }
    public RecipientsFragment() {
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(saveInstanceState);
    }
    @Override
    public void onResume(){
        ((RecipientsActivity) getActivity()).getSupportActionBar().
                setTitle(getString(R.string.drawer_item_recipients));
        super.onResume();
    }

    private void updateRecipientList() {
        DonorSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipients, container, false);
        ButterKnife.inject(this, rootView);

        mRecipientsAdapter = new RecipientsAdapter(getActivity(), null, 0);
        sRecipientChosen = false;

        mListView.setAdapter(mRecipientsAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null){
                    ((Callback)getActivity())
                            .onItemSelected(DonorContract.RecipientsEntry.
                                    buildRecipientUri(cursor.getInt(COL_REC_ENTRY_ID)));
                }
                mPosition = position;
                sRecipientChosen = true;
            }
        });

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(POSITION_KEY)){
                mPosition = savedInstanceState.getInt(POSITION_KEY);
            }
            if (savedInstanceState.containsKey(RECIPIENT_CHOSEN_KEY)){
                sRecipientChosen = savedInstanceState.getBoolean(RECIPIENT_CHOSEN_KEY);
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        if (mPosition != ListView.INVALID_POSITION){
            bundle.putInt(POSITION_KEY, mPosition);
        }
        if (sRecipientChosen){
            bundle.putBoolean(RECIPIENT_CHOSEN_KEY, sRecipientChosen);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(RECIPIENTS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RecipientsEntry.COLUMN_RECIPIENT_ID;
        
        return new CursorLoader(getActivity(),
                RecipientsEntry.CONTENT_URI,
                RESIPIENTS_COLUMS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mRecipientsAdapter.swapCursor(cursor);
        if (cursor.getCount() == 0){
            if (mLinearLayout.getVisibility() == View.GONE){
                mLinearLayout.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            }
        } else {
            if (mListView.getVisibility() == View.GONE){
                mListView.setVisibility(View.VISIBLE);
                mLinearLayout.setVisibility(View.GONE);
            }
            if (mPosition != ListView.INVALID_POSITION){
                mListView.smoothScrollToPosition(mPosition);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecipientsAdapter.swapCursor(null);
    }

    public void userInfoChanged(){
        updateRecipientList();
        getLoaderManager().restartLoader(RECIPIENTS_LOADER, null, this);
    }

}
