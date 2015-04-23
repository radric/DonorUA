package ua.andriyantonov.donorua.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.fragments.NeedToKnowDetailFragment;
import ua.andriyantonov.donorua.fragments.NeedToKnowFragment;

public class NeedToKnowDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_to_know_detail);

        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putInt(NeedToKnowFragment.POSITION_KEY,
                    getIntent().getIntExtra(NeedToKnowFragment.POSITION_KEY,0));

            NeedToKnowDetailFragment fragment = new NeedToKnowDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.need_to_know_detail_container, fragment)
                    .commit();
        }
    }
}
