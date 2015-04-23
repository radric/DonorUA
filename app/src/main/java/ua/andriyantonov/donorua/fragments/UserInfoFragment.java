package ua.andriyantonov.donorua.fragments;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.activities.RecipientsActivity;
import ua.andriyantonov.donorua.activities.UserInfoActivity;
import ua.andriyantonov.donorua.data.Utils;

/**
 * Created by andriy on 23.04.15.
 */
public class UserInfoFragment extends Fragment implements View.OnClickListener {

    public UserInfoFragment(){}

    private static final String SELECTED_BL_GROUP_KEY = "bl_group_key";
    private static final String SELECTED_RH_FACTOR_KEY = "rh_factor_key";
    private static final String SELECTED_USER_CITY_KEY = "user_city_key";
    private int mBloodGroup =0;
    private String  mRhFactor ="", mUserCity, mUserCityId;
    private String[] mCitiesArray;

    @InjectView(R.id.user_bloodType_btn1) ImageButton mBloodType_btn1;
    @InjectView(R.id.user_bloodType_btn2) ImageButton mBloodType_btn2;
    @InjectView(R.id.user_bloodType_btn3) ImageButton mBloodType_btn3;
    @InjectView(R.id.user_bloodType_btn4) ImageButton mBloodType_btn4;
    @InjectView(R.id.user_rh_factor_pos) ImageButton mRh_factor_pos;
    @InjectView(R.id.user_rh_factor_neg) ImageButton mRh_factor_neg;
    @InjectView(R.id.user_saveRegister)  Button mUpdateData;
    @InjectView(R.id.user_autocomplete_city) AutoCompleteTextView mAutoCompleteCity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        ButterKnife.inject(this, rootView);

        checkSavedInstanceState(savedInstanceState);
        mBloodType_btn1.setOnClickListener(this);
        mBloodType_btn2.setOnClickListener(this);
        mBloodType_btn3.setOnClickListener(this);
        mBloodType_btn4.setOnClickListener(this);
        mRh_factor_pos.setOnClickListener(this);
        mRh_factor_neg.setOnClickListener(this);
        mUpdateData.setOnClickListener(this);

        setsAutocompleteTextViewListeners();
        initCityDropDouw();
        if (savedInstanceState == null){
            setUserInfo();
        }
        return rootView;
    }

//    @Override
//    public void onResume(){
//        ((UserInfoActivity) getActivity()).getSupportActionBar().
//                setTitle(getString(R.string.drawer_item_user_info));
//        super.onResume();
//    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putInt(SELECTED_BL_GROUP_KEY, mBloodGroup);
        bundle.putString(SELECTED_RH_FACTOR_KEY, mRhFactor);
        bundle.putString(SELECTED_USER_CITY_KEY, mUserCity);
    }

    public void setUserInfo(){
        clearBloodTypeBtnBackground();
        clearRhFactorBtnBackground();
        mUserCity = Utils.loadUserCity(getActivity());
        mAutoCompleteCity.setText(mUserCity);
        Utils.loadUserBloodGroup(getActivity());
        int userBloodType = Utils.sUserBloodType;
        switch (userBloodType) {
            case 1:
                mBloodType_btn1.setAlpha((float) 1);
                mRh_factor_pos.setAlpha((float) 1);
                mBloodGroup = 1;
                mRhFactor = getString(R.string.rh_factor_pos);
                break;
            case 2:
                mBloodType_btn1.setAlpha((float) 1);
                mRh_factor_neg.setAlpha((float) 1);
                mBloodGroup = 1;
                mRhFactor = getString(R.string.rh_factor_neg);
                break;
            case 3:
                mBloodType_btn2.setAlpha((float) 1);
                mRh_factor_pos.setAlpha((float) 1);
                mBloodGroup = 2;
                mRhFactor = getString(R.string.rh_factor_pos);
                break;
            case 4:
                mBloodType_btn2.setAlpha((float) 1);
                mRh_factor_neg.setAlpha((float) 1);
                mBloodGroup = 2;
                mRhFactor = getString(R.string.rh_factor_neg);
                break;
            case 5:
                mBloodType_btn3.setAlpha((float) 1);
                mRh_factor_pos.setAlpha((float) 1);
                mBloodGroup = 3;
                mRhFactor = getString(R.string.rh_factor_pos);
                break;
            case 6:
                mBloodType_btn3.setAlpha((float) 1);
                mRh_factor_neg.setAlpha((float) 1);
                mBloodGroup = 3;
                mRhFactor = getString(R.string.rh_factor_neg);
                break;
            case 7:
                mBloodType_btn4.setAlpha((float) 1);
                mRh_factor_pos.setAlpha((float) 1);
                mBloodGroup = 4;
                mRhFactor = getString(R.string.rh_factor_pos);
                break;
            case 8:
                mBloodType_btn4.setAlpha((float) 1);
                mRh_factor_neg.setAlpha((float) 1);
                mBloodGroup = 4;
                mRhFactor = getString(R.string.rh_factor_neg);
                break;
        }
    }

    public void initCityDropDouw(){
        Utils.getCitiesData(getActivity());
        mCitiesArray = Utils.sCitiesArray;
        ArrayAdapter<String> mCitiesAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.drop_down_cities,
                mCitiesArray
        );
        mAutoCompleteCity.setAdapter(mCitiesAdapter);
    }

    private void setsAutocompleteTextViewListeners(){
        mAutoCompleteCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mUserCity = parent.getItemAtPosition(position).toString();
                hideKeyboard();
                mAutoCompleteCity.setTextColor(getActivity().getResources().
                        getColor(R.color.material_drawer_primary_dark));
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
     * Hides keyboard when some item(city) was chosen from list
     */
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_bloodType_btn1:
                clearBloodTypeBtnBackground();
                mBloodType_btn1.setAlpha((float) 1);
                mBloodGroup = 1;
                break;
            case R.id.user_bloodType_btn2:
                clearBloodTypeBtnBackground();
                mBloodType_btn2.setAlpha((float) 1);
                mBloodGroup = 2;
                break;
            case R.id.user_bloodType_btn3:
                clearBloodTypeBtnBackground();
                mBloodType_btn3.setAlpha((float) 1);
                mBloodGroup = 3;
                break;
            case R.id.user_bloodType_btn4:
                clearBloodTypeBtnBackground();
                mBloodType_btn4.setAlpha((float) 1);
                mBloodGroup = 4;
                break;
            case R.id.user_rh_factor_pos:
                clearRhFactorBtnBackground();
                mRh_factor_pos.setAlpha((float) 1);
                mRhFactor = getString(R.string.rh_factor_pos);
                break;
            case R.id.user_rh_factor_neg:
                clearRhFactorBtnBackground();
                mRh_factor_neg.setAlpha((float) 1);
                mRhFactor = getString(R.string.rh_factor_neg);
                break;
            case R.id.user_saveRegister:
                updateAndSave();
                break;
        }
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
    public void updateAndSave(){
        if (!Arrays.asList(mCitiesArray).contains(mUserCity)){
            Toast.makeText(getActivity(), "Оберіть місто із випадаючого списку", Toast.LENGTH_SHORT).show();
        } else {
            int cityIndex = Arrays.asList(mCitiesArray).indexOf(mUserCity);
            mUserCityId = Utils.sCitiesIdArray[cityIndex];
            Utils.saveRegStatus(getActivity(), true);
            Utils.saveUserBloodType(getActivity(), mBloodGroup, mRhFactor);
            Utils.saveUserCity(getActivity(), mUserCity);
            Utils.saveUserCityId(getActivity(), mUserCityId);

            Toast.makeText(getActivity(), "Дані оновлено та збережено", Toast.LENGTH_LONG).show();
        }
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
        }
    }

}
