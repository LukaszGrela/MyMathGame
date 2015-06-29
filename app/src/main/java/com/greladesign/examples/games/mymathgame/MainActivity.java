package com.greladesign.examples.games.mymathgame;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greladesign.examples.games.mymathgame.game.Operation;

import java.util.EnumSet;


public class MainActivity extends AppCompatActivity implements GameSetupDialogue.GameSetupListener{


    private static final String TAG = "MainActivity";
    private static final String GAME_SETUP_DIALOGUE_ADD_SUB = "GameSetupDialogueAddSub";
    private static final String GAME_SETUP_DIALOGUE_MUL_DIV = "GameSetupDialogueMulDiv";


    private Button btnGameAddition;
    private Button btnGameSubstraction;
    private Button btnGameMultiplication;
    private Button btnGameDivision;

    private Range mAddSubRange;
    private Range mMulDivRange;

    private View.OnClickListener mRangeSelectionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click");
            DialogFragment dialog = new GameSetupDialogue();
            final Bundle arguments = new Bundle();
            if (v == mChangeAddSubRange) {
                Log.i(TAG, "Changing the adding/subtracting range");
                arguments.putInt("lower", mAddSubRange.getLower());
                arguments.putInt("upper", mAddSubRange.getUpper());
                dialog.setArguments(arguments);
                dialog.show(getSupportFragmentManager(), GAME_SETUP_DIALOGUE_ADD_SUB);
            }
            if (v == mChangeMulDivRange) {
                Log.i(TAG, "Changing the multiply/division range");
                arguments.putInt("lower", mMulDivRange.getLower());
                arguments.putInt("upper", mMulDivRange.getUpper());
                dialog.setArguments(arguments);
                dialog.show(getSupportFragmentManager(), GAME_SETUP_DIALOGUE_MUL_DIV);
            }
        }
    };
    private View.OnClickListener mSelectGameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnOperationAdd:
                    startNewGame(mAddSubRange, EnumSet.of(Operation.ADD));
                    break;
                case R.id.btnOperationSubstract:
                    startNewGame(mAddSubRange,EnumSet.of(Operation.SUBSTRACT));
                    break;
                case R.id.btnOperationMultiply:
                    startNewGame(mMulDivRange,EnumSet.of(Operation.MULTIPLY));
                    break;
                case R.id.btnOperationDivide:
                    startNewGame(mMulDivRange,EnumSet.of(Operation.DIVIDE));
                    break;
            }
        }
    };

    private CheckBox mCbAllowSecondTry;
    private TextView mTvAddSubRangeDescription;
    private TextView mTvMulDivRangeDescription;
    private TextView mChangeAddSubRange;
    private TextView mChangeMulDivRange;

    private void startNewGame(Range range, EnumSet<Operation> operationId) {
        final Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra(GameActivity.INTENT_RANGE_LOWER, range.getLower());
        gameIntent.putExtra(GameActivity.INTENT_RANGE_UPPER, range.getUpper());
        gameIntent.putExtra(GameActivity.INTENT_OPERATION_ID, Operation.toArray(operationId));
        gameIntent.putExtra(GameActivity.INTENT_ALLOW_SECOND_TRY, mCbAllowSecondTry.isChecked());
        startActivity(gameIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAddSubRange = new Range(0,100);
        mMulDivRange = new Range(0,10);

        findViews();
    }

    private void findViews() {
        btnGameAddition = (Button) findViewById(R.id.btnOperationAdd);
        btnGameSubstraction = (Button) findViewById(R.id.btnOperationSubstract);
        btnGameMultiplication = (Button) findViewById(R.id.btnOperationMultiply);
        btnGameDivision = (Button) findViewById(R.id.btnOperationDivide);
        //
        mCbAllowSecondTry = (CheckBox) findViewById(R.id.cbAllowSecondTry);
        //
        LinearLayout mAddSubRangeLayout = (LinearLayout) findViewById(R.id.llRangeAddSub);
        if(mAddSubRangeLayout != null) {
            mTvAddSubRangeDescription = (TextView) mAddSubRangeLayout.findViewById(R.id.tvDetails);
            mChangeAddSubRange = (TextView) mAddSubRangeLayout.findViewById(R.id.tvChangeBtn);
            mChangeAddSubRange.setOnClickListener(mRangeSelectionListener);
        }
        LinearLayout mMulDivRangeLayout = (LinearLayout) findViewById(R.id.llRangeMulDiv);
        if(mMulDivRangeLayout != null) {
            mTvMulDivRangeDescription = (TextView) mMulDivRangeLayout.findViewById(R.id.tvDetails);
            mChangeMulDivRange = (TextView) mMulDivRangeLayout.findViewById(R.id.tvChangeBtn);
            mChangeMulDivRange.setOnClickListener(mRangeSelectionListener);
        }
        //add logic
        btnGameAddition.setOnClickListener(mSelectGameListener);
        btnGameSubstraction.setOnClickListener(mSelectGameListener);
        btnGameMultiplication.setOnClickListener(mSelectGameListener);
        btnGameDivision.setOnClickListener(mSelectGameListener);
        //
        updateRangeLabels();
    }

    private void updateRangeLabels() {
        final Resources res = getResources();
        mTvMulDivRangeDescription.setText(res.getString(R.string.main_menu_range, mMulDivRange.getLower(),mMulDivRange.getUpper()));
        mTvAddSubRangeDescription.setText(res.getString(R.string.main_menu_range, mAddSubRange.getLower(),mAddSubRange.getUpper()));
    }

    @Override
    public void onGameSetupModified(String tag, int from, int to) {
        final Resources res = getResources();
        Log.i(TAG, "Range: " + res.getString(R.string.main_menu_range, from, to));

        if (tag.equals(GAME_SETUP_DIALOGUE_MUL_DIV) || tag.equals(GAME_SETUP_DIALOGUE_ADD_SUB)) {
            if (tag.equals(GAME_SETUP_DIALOGUE_ADD_SUB)) {
                mAddSubRange.setLower(from);
                mAddSubRange.setUpper(to);
            } else {
                mMulDivRange.setLower(from);
                mMulDivRange.setUpper(to);
            }

            updateRangeLabels();
        }
    }

    private class Range {

        private int mLower;
        private int mUpper;

        public Range(int lower, int upper) {
            if(lower > upper) {
                throw new IllegalArgumentException("lower must be less than or equal to upper");
            }
            mLower = lower;
            mUpper = upper;
        }

        public int getLower() {
            return mLower;
        }

        public int getUpper() {
            return mUpper;
        }

        public void setLower(int lower) {
            if(lower > getUpper()) {
                throw new IllegalArgumentException("lower must be less than or equal to upper");
            }
            mLower = lower;
        }

        public void setUpper(int upper) {
            if(upper <= getLower()) {
                throw new IllegalArgumentException("upper must be greater than lower");
            }
            mUpper = upper;
        }
    }
}
