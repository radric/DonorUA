package ua.andriyantonov.donorua.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.fragments.RegisterFragment;


public class RegisterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.register_container, new RegisterFragment())
                    .commit();
        }
    }

    /**
     * Closed application if BackBtn pressed
     */
    @Override
    public void onBackPressed(){
        finish();
    }
}
