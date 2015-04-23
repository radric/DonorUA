package ua.andriyantonov.donorua.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.fragments.RecipientDetailFragment;
import ua.andriyantonov.donorua.fragments.RecipientsFragment;

public class RecipientDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_detail);

        if (savedInstanceState == null) {

            Bundle arguments =  new Bundle();
            arguments.putParcelable(RecipientDetailFragment.RECIP_DETAIL_URI, getIntent().getData());

            RecipientDetailFragment rdf = new RecipientDetailFragment();
            rdf.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipient_detail_container, rdf)
                    .commit();
        }
    }
}
