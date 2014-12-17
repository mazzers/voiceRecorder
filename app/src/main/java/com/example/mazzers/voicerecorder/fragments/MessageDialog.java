package com.example.mazzers.voicerecorder.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mazzers.voicerecorder.R;

/**
 * Created by mazzers on 17. 12. 2014.
 */
public class MessageDialog extends DialogFragment implements View.OnClickListener {
    Button btnYes,btnCancel;
    EditText message_editText;
    public static String message;
    Bundle bundle;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Title!");
        View v = inflater.inflate(R.layout.message_dialog_layout, null);
        btnYes = (Button) v.findViewById(R.id.btnYes);
        btnCancel = (Button) v.findViewById(R.id.btnNo);
        message_editText = (EditText) v.findViewById(R.id.message_editText);

        v.findViewById(R.id.btnYes).setOnClickListener(this);
        v.findViewById(R.id.btnNo).setOnClickListener(this);
        //v.findViewById(R.id.btnMaybe).setOnClickListener(this);
        bundle = new Bundle();
        return v;
    }


    public void callYes(){
        message = message_editText.getText().toString();
        dismiss();

    }
    public void callNo(){
        message="empty";
        dismiss();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnYes:
                callYes();
                dismiss();
                break;
            case R.id.btnNo:
                callNo();
                dismiss();
                break;



        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        //message="Empty";
        bundle.putString("message",message);
        Fragment fragment = new RecorderFragment();
        fragment.setArguments(bundle);
        super.onDismiss(dialog);
    }

    public static String getMessage(){
        return message;
    }
}
