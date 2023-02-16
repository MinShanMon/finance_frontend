package com.team3.personalfinanceapp.profile_login;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.team3.personalfinanceapp.Fragment.HomeFragment;
import com.team3.personalfinanceapp.HomeNav;
import com.team3.personalfinanceapp.MainActivity;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.RegisteredUsers;
import com.team3.personalfinanceapp.utils.UserApi;
import com.team3.personalfinanceapp.utils.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileEditFragment extends Fragment {

    TextInputEditText username;
    TextInputEditText textEmailAddress;
    TextInputEditText textPassword;
    Button confirm_button;
    LinearLayout email_display;
    LinearLayout current_password;
    TextView profile;
    TextView error_msg;
    ImageView img_backArrow;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    UserApi apiInterface;
    private ProfileFragment listener;

//    ProfileFragment profileFragment = new ProfileFragment();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public ProfileEditFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile_edit, container, false);
        init(view);

        confirm_button.setOnClickListener(v -> confirm());

        img_backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);
                commitTransaction(listener);
            }
        });

        setupOnBackPressed();
        return view;
    }

    private void init(View view){
        username = view.findViewById(R.id.username);
        textEmailAddress = view.findViewById(R.id.textEmailAddress);
        textPassword = view.findViewById(R.id.textPassword);
        confirm_button = view.findViewById(R.id.confirm_button);
        email_display = view.findViewById(R.id.email_display);
        profile = view.findViewById(R.id.profile);
        error_msg = view.findViewById(R.id.error_msg);
        img_backArrow = view.findViewById(R.id.img_backArrow);
        current_password = view.findViewById(R.id.current_password);
        apiInterface = APIClient.getClient().create(UserApi.class);
        pref = this.getActivity().getSharedPreferences("user_credentials", MODE_PRIVATE);
        boolean check = pref.getBoolean("fbuser", false);
        username.setText(pref.getString("username", ""));
        if(check){
            email_display.setVisibility(View.GONE);
            current_password.setVisibility(View.GONE);
            profile.setText(getString(R.string.change_name));
        }
        else{
            textEmailAddress.setText(pref.getString("email", ""));
            profile.setText(getString(R.string.username_email));
        }
    }

    private void setupOnBackPressed(){
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                commitTransaction(listener);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity mainActivity = (MainActivity) context;
        listener = mainActivity.getProfileFragment();
    }

    private void commitTransaction(Fragment fragment) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.commit();
    }

    private void confirm(){
        pref = this.getActivity().getSharedPreferences("user_credentials", MODE_PRIVATE);
        editor = pref.edit();
        boolean check = pref.getBoolean("fbuser", false);
        String name = username.getText().toString();
        String pass = textPassword.getText().toString();
        String email = textEmailAddress.getText().toString();
        RegisteredUsers user = new RegisteredUsers();
        if(name.isEmpty()){
            error_msg.setText("Name cannot be Empty");
            return;
        }

        if(!check){
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                error_msg.setText("please enter valid email");
                return;
            }
            if(email.isEmpty()){
                error_msg.setText("Email cannot be empty");
                return;
            }
            if(pass.isEmpty()){
                error_msg.setText("Password cannot be empty");
                return;
            }
            if(!pass.equals(pref.getString("password", ""))){
                error_msg.setText("Wrong Password");
                return;
            }
            if(!email.equals(pref.getString("email", ""))){
                user.setEmail(email);
                editor.putString("email", email);
            }
        }
        user.setId(pref.getInt("userid", 0));
        user.setFullName(name);
        editor.putString("username", name);
        editor.commit();

//        commitTransaction(profileFragment);
        Call<RegisteredUsers> userLoginCall = apiInterface.editProfile(user, "Bearer "+ pref.getString("token", ""));
        userLoginCall.enqueue(new Callback<RegisteredUsers>() {
            @Override
            public void onResponse(Call<RegisteredUsers> call, Response<RegisteredUsers> response) {
                if(response.body() == null){
                    error_msg.setText("email already exit!!! Check Your Email.");
                    return;
                }
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<RegisteredUsers> call, Throwable t) {

            }
        });
    }
}