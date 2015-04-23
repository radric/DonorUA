package ua.andriyantonov.donorua.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.activities.NeedToKnowActivity;
import ua.andriyantonov.donorua.activities.NeedToKnowDetailActivity;
import ua.andriyantonov.donorua.activities.RecipientsActivity;

/**
 * Created by andriy on 22.04.15.
 */
public class NeedToKnowFragment extends Fragment {

    public NeedToKnowFragment() {
    }

    public static final String POSITION_KEY = "selected_need_to_know_position";
    private int mPosition;

    @InjectView(R.id.need_to_know_listview) ListView mListVIew;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_need_to_know, container, false);
        ButterKnife.inject(this, rootView);

        String[] mItems = {
                getString(R.string.ntk_menu_1),
                getString(R.string.ntk_menu_2),
                getString(R.string.ntk_menu_3),
        };
        List<String> mList = new ArrayList<>(Arrays.asList(mItems));
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_need_to_know,
                R.id.need_to_know_item_listview,
                mList
        );
        mListVIew.setAdapter(mAdapter);
        mListVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((CallbackToKnow)getActivity()).onItemSelected(position);
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)){
            mPosition = savedInstanceState.getInt(POSITION_KEY);
        }
        if (mPosition != 0){
            switch (mPosition){
                case 1:
            }
        }
        return rootView;
    }

    public interface CallbackToKnow{
        public void onItemSelected(int position);
    }

    @Override
    public void onResume(){
        ((NeedToKnowActivity) getActivity()).getSupportActionBar().
                setTitle(getString(R.string.drawer_item_need_to_know));
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);

        if (mPosition != ListView.INVALID_POSITION){
            bundle.putInt(POSITION_KEY, mPosition);
        }
    }

}
