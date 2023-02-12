package com.team3.personalfinanceapp.profile_login;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.team3.personalfinanceapp.R;

public class ProfileFragment extends Fragment {
    TextView logout;
    LinearLayout change_password;
    TextView txtUsername;
    TextView edtRegisterEmail;
    TextView profile;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    LinearLayout email_display;
    ImageView img_icon;
    LinearLayout fill_enquiry;

    ProfileEditFragment profileEditFragment = new ProfileEditFragment();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        pref = this.getActivity().getSharedPreferences("user_credentials", MODE_PRIVATE);
        init(view);

        change_password.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            intent.putExtra("change_password", true);
            startActivity(intent);
        });

        fill_enquiry.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            intent.putExtra("fill_enquiry", true);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> commitTransaction(profileEditFragment));

        logout.setOnClickListener(v -> {
            editor = pref.edit();
            editor.clear();
            editor.commit();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(v.getContext() , LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        return view;
    }

    private void init(View view){
        pref = this.getActivity().getSharedPreferences("user_credentials", MODE_PRIVATE);
        logout = view.findViewById(R.id.logout);
        txtUsername = view.findViewById(R.id.txtUsername);
        edtRegisterEmail = view.findViewById(R.id.edtRegisterEmail);
        email_display = view.findViewById(R.id.email_display);
        change_password = view.findViewById(R.id.change_password);
        img_icon = view.findViewById(R.id.img_icon);
        profile = view.findViewById(R.id.profile);
        txtUsername.setText(pref.getString("username",""));
        edtRegisterEmail.setText(pref.getString("email",""));
        SpannableString content = new SpannableString("Edit Profile");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        profile.setText(content);
        boolean check = pref.getBoolean("fbuser", false);
        if(check){
            email_display.setVisibility(View.GONE);
            img_icon.setImageResource(R.drawable.ic_baseline_account_box_24);
            change_password.setVisibility(View.GONE);
//                    floatingActionButton.setImageResource(android.R.drawable.ic_media_pause);

        }
        else{
            img_icon.setImageResource(R.drawable.ic_baseline_email_24);
        }
    }

//    profileEditFragment

    private void commitTransaction(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.addToBackStack(null);
        trans.commit();
    }



//    private void enterPassword(){
//        String title = "Manage Profile";
//        String msg = "Enter Your Password";
//        AlertDialog.Builder dlg = new AlertDialog.Builder(this.getActivity())
//                .setTitle(title)
//                .setMessage(msg)
//                .setPositiveButton("Confirm",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dlg, int which) {
//
//                            } })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                getActivity().finish();
//
//                            } })
//                .setIcon(android.R.drawable.ic_dialog_alert);
//        dlg.setCancelable(false);
//        dlg.show();
//    }
}