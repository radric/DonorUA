package ua.andriyantonov.donorua.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import ua.andriyantonov.donorua.R;

/**
 * Created by andriy on 22.04.15.
 */
public class NeedToKnowDetailFragment extends Fragment {
    public NeedToKnowDetailFragment(){
    }

    private final static String SCROLL_POSITION_KEY = "ntk scroll position key";

    @Optional
    @InjectView(R.id.need_to_know_item_textview)TextView mItemTextView;
    @InjectView(R.id.need_to_know_scrollview) ScrollView mScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_need_to_know_detail, container, false);
        ButterKnife.inject(this, rootView);

        int item = 0;
        Bundle bundle = getArguments();
        item = bundle.getInt(NeedToKnowFragment.POSITION_KEY);

        switch (item){
            case 0:
                mItemTextView.setText(Html.fromHtml(getString(R.string.need_to_know_item_1_text)));
                break;
            case 1:
                mItemTextView.setText(Html.fromHtml(getString(R.string.need_to_know_item_2_text)));
                break;
            case 2:
                mItemTextView.setText(Html.fromHtml(getString(R.string.need_to_know_item_3_text)));
                break;
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLL_POSITION_KEY)){
            final int[] scrollPosition = savedInstanceState.getIntArray(SCROLL_POSITION_KEY);
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(scrollPosition[0], scrollPosition[1]);
                }
            });
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putIntArray(SCROLL_POSITION_KEY, new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});
        super.onSaveInstanceState(bundle);
    }
}
