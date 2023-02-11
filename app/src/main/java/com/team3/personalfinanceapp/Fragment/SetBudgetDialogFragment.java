package com.team3.personalfinanceapp.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.team3.personalfinanceapp.MainActivity;
import com.team3.personalfinanceapp.R;

public class SetBudgetDialogFragment extends DialogFragment {

    private String userId;
    private EditText budgetAmtField;
    private SharedPreferences budgetPref;

    private setBudgetListener listener;

    public interface setBudgetListener {
        public void onDialogPositiveClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity mainActivity = (MainActivity) context;
        listener = mainActivity.getHomeFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        SharedPreferences pref = getActivity().getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        userId = String.valueOf(pref.getInt("userid", 0));

        budgetPref = getActivity().getSharedPreferences("user_budget", Context.MODE_PRIVATE);
        float prevBudget = budgetPref.getFloat(userId, 0);

        View budgetView = inflater.inflate(R.layout.fragment_set_budget_dialog, null);
        budgetAmtField = budgetView.findViewById(R.id.budget_amt);
        budgetAmtField.setText(String.valueOf(prevBudget));

        builder.setTitle(R.string.monthly_budget)
                .setView(budgetView)
                .setPositiveButton(R.string.save_budget, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveBudget();
                        listener.onDialogPositiveClick();
                    }
                })
                .setNegativeButton(R.string.cancel_budget, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SetBudgetDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void saveBudget() {
        EditText budgetAmtField = getDialog().findViewById(R.id.budget_amt);
        float budget = Float.parseFloat(budgetAmtField.getText().toString());
        SharedPreferences.Editor budgetPrefEditor = budgetPref.edit();
        budgetPrefEditor.putFloat(userId, budget);
        budgetPrefEditor.commit();
    }
}