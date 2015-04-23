package ua.andriyantonov.donorua.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.activities.RecipientsActivity;
import ua.andriyantonov.donorua.data.Utils;
import ua.andriyantonov.donorua.sync.DonorSyncAdapter;

/**
 * Created by andriy on 10.04.15.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private static final String SELECTED_BL_GROUP_KEY = "bl_group_key";
    private static final String SELECTED_RH_FACTOR_KEY = "rh_factor_key";
    private static final String SELECTED_USER_CITY_KEY = "user_city_key";
    private int mBloodGroup =0;
    private String mRhFactor ="" , mUserCity, mUserCityId;

    private String[] mCitiesArray;
    private Context mContext;
    private Handler mHandler = new Handler();
    private boolean mIsOnline;

    @InjectView(R.id.reg_bloodType_btn1) ImageButton mBloodType_btn1;
    @InjectView(R.id.reg_bloodType_btn2) ImageButton mBloodType_btn2;
    @InjectView(R.id.reg_bloodType_btn3) ImageButton mBloodType_btn3;
    @InjectView(R.id.reg_bloodType_btn4) ImageButton mBloodType_btn4;
    @InjectView(R.id.reg_rh_factor_pos) ImageButton mRh_factor_pos;
    @InjectView(R.id.reg_rh_factor_neg) ImageButton mRh_factor_neg;
    @InjectView(R.id.reg_saveRegister) Button mSaveRegister;
    @InjectView(R.id.reg_autocomplete_city) AutoCompleteTextView mAutoCompleteCity;
    @InjectView(R.id.reg_loadingCities_progressbar)ProgressBar mProgressBar;
    @InjectView(R.id.city_textview) TextView mCityTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundleInstance){
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.inject(this, rootView);
        mContext = getActivity();

        checkSavedInstanceState(savedBundleInstance);
        hideKeyboard();

        mBloodType_btn1.setOnClickListener(this);
        mBloodType_btn2.setOnClickListener(this);
        mBloodType_btn3.setOnClickListener(this);
        mBloodType_btn4.setOnClickListener(this);
        mRh_factor_pos.setOnClickListener(this);
        mRh_factor_neg.setOnClickListener(this);
        mSaveRegister.setOnClickListener(this);

        setsAutocompleteTextViewListeners();

        mHandler.postDelayed(runnable, 500);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putInt(SELECTED_BL_GROUP_KEY, mBloodGroup);
        bundle.putString(SELECTED_RH_FACTOR_KEY, mRhFactor);
        bundle.putString(SELECTED_USER_CITY_KEY, mUserCity);
    }

    private void setsAutocompleteTextViewListeners(){
        mAutoCompleteCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mUserCity = parent.getItemAtPosition(position).toString();
                hideKeyboard();
                mAutoCompleteCity.setTextColor(mContext.getResources().
                        getColor(R.color.material_drawer_primary_dark));

                int cityIndex = Arrays.asList(mCitiesArray).indexOf(mUserCity);
                mUserCityId = Utils.sCitiesIdArray[cityIndex];
            }
        });
        mAutoCompleteCity.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean finishEdit = false;
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    hideKeyboard();
                    finishEdit = true;
                }
                return finishEdit;
            }
        });
    }

    /**
     * Checks if some data was already chosen by user
     */
    private void checkSavedInstanceState(Bundle bundle){
        if (bundle != null && bundle.containsKey(SELECTED_BL_GROUP_KEY)){
            mBloodGroup = bundle.getInt(SELECTED_BL_GROUP_KEY);
            switch (mBloodGroup){
                case 1:
                    clearBloodTypeBtnBackground();
                    mBloodType_btn1.setAlpha((float) 1);
                    break;
                case 2:
                    clearBloodTypeBtnBackground();
                    mBloodType_btn2.setAlpha((float) 1);
                    break;
                case 3:
                    clearBloodTypeBtnBackground();
                    mBloodType_btn3.setAlpha((float) 1);;
                    break;
                case 4:
                    clearBloodTypeBtnBackground();
                    mBloodType_btn4.setAlpha((float) 1);;
                    break;
            }
        }
        if (bundle != null && bundle.containsKey(SELECTED_RH_FACTOR_KEY)){
            mRhFactor = bundle.getString(SELECTED_RH_FACTOR_KEY);
            if (mRhFactor.equals(getString(R.string.rh_factor_pos))){
                clearRhFactorBtnBackground();
                mRh_factor_pos.setAlpha((float) 1);
            } else if (mRhFactor.equals(getString(R.string.rh_factor_neg))){
                clearRhFactorBtnBackground();
                mRh_factor_neg.setAlpha((float) 1);
            }
        }
        if (bundle != null && bundle.containsKey(SELECTED_USER_CITY_KEY)){
            mUserCity = bundle.getString(SELECTED_USER_CITY_KEY);
            mAutoCompleteCity.setText(mUserCity);
            mAutoCompleteCity.setTextColor(mContext.getResources().
                    getColor(R.color.material_drawer_primary_dark));
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkConnectivity();
            Utils.getCitiesData(getActivity());
            mCitiesArray = Utils.sCitiesArray;

            if (mCitiesArray.length == 0) {
                mHandler.postDelayed(runnable, 500);
                DonorSyncAdapter.syncImmediately(getActivity());
            }
            if (mCitiesArray.length > 0){
                ArrayAdapter<String> mCitiesAdapter = new ArrayAdapter<String>(
                        mContext,
                        R.layout.drop_down_cities,
                        mCitiesArray
                );
                mCitiesAdapter.setDropDownViewResource(R.layout.drop_down_cities);
                mAutoCompleteCity.setAdapter(mCitiesAdapter);
                mProgressBar.setVisibility(View.INVISIBLE);
                mAutoCompleteCity.setVisibility(View.VISIBLE);
                mCityTextView.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * Hides keyboard when some item(city) was chosen from list
     */
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager)
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        if (mIsOnline){
            switch (v.getId()){
                case R.id.reg_bloodType_btn1:
                    clearBloodTypeBtnBackground();
                    mBloodType_btn1.setAlpha((float) 1);
                    mBloodGroup = 1;
                    break;
                case R.id.reg_bloodType_btn2:
                    clearBloodTypeBtnBackground();
                    mBloodType_btn2.setAlpha((float) 1);
                    mBloodGroup = 2;
                    break;
                case R.id.reg_bloodType_btn3:
                    clearBloodTypeBtnBackground();
                    mBloodType_btn3.setAlpha((float) 1);
                    mBloodGroup = 3;
                    break;
                case R.id.reg_bloodType_btn4:
                    clearBloodTypeBtnBackground();
                    mBloodType_btn4.setAlpha((float) 1);
                    mBloodGroup = 4;
                    break;
                case R.id.reg_rh_factor_pos:
                    clearRhFactorBtnBackground();
                    mRh_factor_pos.setAlpha((float) 1);
                    mRhFactor = getString(R.string.rh_factor_pos);
                    break;
                case R.id.reg_rh_factor_neg:
                    clearRhFactorBtnBackground();
                    mRh_factor_neg.setAlpha((float) 1);
                    mRhFactor = getString(R.string.rh_factor_neg);
                    break;
                case R.id.reg_saveRegister:
                    registration();
                    break;
            }
        } else { dialogConnectionOff(); }
    }


    public void clearBloodTypeBtnBackground(){
        mBloodType_btn1.setAlpha((float) 0.3);
        mBloodType_btn2.setAlpha((float) 0.3);
        mBloodType_btn3.setAlpha((float) 0.3);
        mBloodType_btn4.setAlpha((float) 0.3);
    }

    public void clearRhFactorBtnBackground(){
        mRh_factor_pos.setAlpha((float) 0.3);
        mRh_factor_neg.setAlpha((float) 0.3);
    }

    /**
     * Checks if user choose all data
     * If false - toast message
     * If true - saves all data and intent to MainActivity
     */
    public void registration(){
        if (mBloodGroup == 0){
            Toast.makeText(mContext, "Оберіть группу крові", Toast.LENGTH_SHORT).show();
        } else if (mRhFactor.isEmpty()){
            Toast.makeText(mContext, "Оберіть резус фактор", Toast.LENGTH_SHORT).show();
        } else if (!Arrays.asList(mCitiesArray).contains(mUserCity)){
            Toast.makeText(mContext, "Оберіть місто із випадаючого списку", Toast.LENGTH_SHORT).show();
        } else {
            Utils.saveRegStatus(mContext, true);
            Utils.saveUserBloodType(mContext, mBloodGroup, mRhFactor);
            Utils.saveUserCity(mContext, mUserCity);
            Utils.saveUserCityId(mContext, mUserCityId);

            Intent intent = new Intent(mContext, RecipientsActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Checks if device connected to internet
     */
    public void checkConnectivity(){
        mIsOnline = Utils.checkConnection(getActivity());
        if (mIsOnline) {
            if (mProgressBar.getVisibility() == View.INVISIBLE){
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    public void dialogConnectionOff(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Відсутній доступ до мережі...");
        alertDialog.setMessage("Увімкніть будь-ласка мережу та спробуйте знову");
        alertDialog.setButton("Добре, дякую!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.show();
    }
}
