package com.team3.personalfinanceapp.transactions;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.team3.personalfinanceapp.R;

public class SetBudgetDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setTitle(R.string.monthly_budget)
                .setView(inflater.inflate(R.layout.fragment_set_budget_dialog, null))
                .setPositiveButton(R.string.save_budget, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveBudget();
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
        SharedPreferences pref = getActivity().getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        String userid = String.valueOf(pref.getInt("userid", 0));
        SharedPreferences budgetPref = getActivity().getSharedPreferences("user_budget", Context.MODE_PRIVATE);
        SharedPreferences.Editor budgetPrefEditor = budgetPref.edit();
        budgetPrefEditor.putFloat(userid, budget);
        budgetPrefEditor.commit();
}




}