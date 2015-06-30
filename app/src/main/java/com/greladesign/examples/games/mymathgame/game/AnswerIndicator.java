package com.greladesign.examples.games.mymathgame.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.greladesign.examples.games.mymathgame.R;

/**
 * Created by Łukasz 'Severiaan' Grela on 29/06/2015.
 */
public class AnswerIndicator extends DialogFragment {

    final public static String  BUNDLE_ID_TITLE_RES = "bundle_id_title_res";
    final public static String  BUNDLE_ID_ICON_RES = "bundle_id_icon_res";
    final public static String  BUNDLE_ID_MESSAGE_RES = "bundle_id_message_res";
    final public static String  BUNDLE_ID_BACKGROUND_RES = "bundle_id_background_res";
    final public static String  BUNDLE_ID_BACKGROUND_DRAWABLE_RES = "bundle_id_background_drawable_res";


    View mView;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();

        final int titleId = args.getInt(BUNDLE_ID_TITLE_RES,-1);
        final int messageId = args.getInt(BUNDLE_ID_MESSAGE_RES,-1);
        final int iconId = args.getInt(BUNDLE_ID_ICON_RES,-1);
        final int backgroundColorId = args.getInt(BUNDLE_ID_BACKGROUND_RES,-1);
        final int backgroundDrawableId = args.getInt(BUNDLE_ID_BACKGROUND_DRAWABLE_RES,-1);



        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mView = inflater.inflate(R.layout.answer_layout, null);
        final Resources res = getResources();

        if(backgroundDrawableId == -1) {
            if(backgroundColorId != -1) {
                //apply colour
                mView.setBackgroundColor(res.getColor(backgroundColorId));
            }
        } else {
            //apply drawable
            mView.setBackgroundDrawable(res.getDrawable(backgroundDrawableId));
        }


        final ImageView icon = (ImageView) mView.findViewById(R.id.ivIcon);
        if (icon!=null) {
           icon.setImageDrawable(res.getDrawable(iconId));
        }
        final TextView tvDetails = (TextView) mView.findViewById(R.id.tvDetails);
        if (tvDetails != null) {
           tvDetails.setText(res.getText(messageId));
        }
        final TextView tvTitle = (TextView) mView.findViewById(R.id.tvTitle);
        if (tvTitle != null) {
           tvTitle.setText(res.getText(titleId));
        }

        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return true;
            }
        });

        builder.setView(mView)
                /*.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setTitle(titleId)*/;

        final Dialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(true);

        //TODO: start timer for auto-dismiss
        //


        return dialog;
    }

}
