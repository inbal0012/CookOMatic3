package com.example.adopy.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.adopy.R;
import com.example.adopy.Activities.SigninActivity;

public class Dialogs {
    Activity activity;

    public Dialogs(Activity activity) {
        this.activity = activity;
    }

    public void showLoginDialog(int id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.AlertTheme).setCancelable(true);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout_login, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView body = dialogView.findViewById(R.id.body);

        switch (id) {
            case R.id.fabFav:
                body.setText(activity.getString(R.string.add_fav_dialog_body));
                break;
            case R.id.fabAdd:
                body.setText(activity.getString(R.string.add_pet_dialog_body));
                break;
            case R.id.nav_profile:
                body.setText(activity.getString(R.string.profile_dialog_body));
                break;
            case R.id.chats_recycler_view:
            case R.id.nav_chats:
                body.setText(activity.getString(R.string.chats_dialog_body));
                break;



        }

        FrameLayout mDialogLogin = dialogView.findViewById(R.id.frmLogin);
        mDialogLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(activity, SigninActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(homeIntent);
                alertDialog.dismiss();
            }
        });

        FrameLayout mDialogExit = dialogView.findViewById(R.id.frmExit);
        mDialogExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();

    }
}
