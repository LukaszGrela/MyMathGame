package com.greladesign.examples.games.mymathgame;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by lukasz.grela on 29/06/2015.
 */
public class GameSetupDialogue extends DialogFragment {
    private static final String TAG = "GameSetupDialogue";
    private static GameSetupListener sDummyListener = new GameSetupListener() {
        @Override
        public void onGameSetupModified(String tag, int from, int to) {
            /* dummy */
        }
    };
    private GameSetupListener mListener;

    public interface GameSetupListener {
        void onGameSetupModified(String tag, int from, int to);
    }



    private int mTMPFrom;
    private int mTMPTo;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.i(TAG, "tag="+getTag());
        final Bundle args = getArguments();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View gameSetupView = inflater.inflate(R.layout.activity_game_setup, null);
        //
        // ----------------------------------------
        mTMPFrom = args.getInt("lower",0);
        mTMPTo = args.getInt("upper",100);
        final String[] from = {"0","10","20","30","40","50","60","70","80","90","100"};
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, from);
        final Spinner spinnerFrom = (Spinner) gameSetupView.findViewById(R.id.spinFrom);
        spinnerFrom.setAdapter(fromAdapter);
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getSelectedItem();
                Log.i(TAG, "Selected from:" + item);
                mTMPFrom = Integer.parseInt(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* do nothing */
            }
        });
        //
        final String[] to = {"0","10","20","30","40","50","60","70","80","90","100"};
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, to);
        final Spinner spinnerTo = (Spinner) gameSetupView.findViewById(R.id.spinTo);
        spinnerTo.setAdapter(toAdapter);
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getSelectedItem();
                Log.i(TAG, "Selected to:" + item);
                mTMPTo = Integer.parseInt(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* do nothing */
            }
        });
        //
        spinnerFrom.setSelection(tmpGetPosition((ArrayAdapter<String>) spinnerFrom.getAdapter(), mTMPFrom+""));
        spinnerTo.setSelection(tmpGetPosition((ArrayAdapter<String>) spinnerTo.getAdapter(), mTMPTo+""));
        // ----------------------------------------
        //
        final int positiveLabelId = R.string.game_setup_positive_btn;
        final int titleLabelId = R.string.game_setup_title;
        //
        builder.setView(gameSetupView)
                .setPositiveButton(positiveLabelId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mListener != null) {
                            mListener.onGameSetupModified(getTag(), mTMPFrom,mTMPTo);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setTitle(titleLabelId);
        //
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GameSetupListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement GameSetupListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = sDummyListener;
    }

    private int tmpGetPosition(ArrayAdapter<String> adapter, String item) {
        return adapter.getPosition(item);
    }
}
