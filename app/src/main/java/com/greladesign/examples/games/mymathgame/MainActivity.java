package com.greladesign.examples.games.mymathgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greladesign.examples.games.mymathgame.game.Operation;

import java.util.EnumSet;


public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";
    private Button btnGameAddition;
    private Button btnGameSubstraction;
    private Button btnGameMultiplication;
    private Button btnGameDivision;
    private View.OnClickListener mRangeSelectionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click");
            if (v == mChangeAddSubRange) {
                Log.i(TAG, "Changing the adding/subtracting range");
            }
            if (v == mChangeMulDivRange) {
                Log.i(TAG, "Changing the multiply/division range");
            }
        }
    };
    private View.OnClickListener mSelectGameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnOperationAdd:
                    startNewGame(100, EnumSet.of(Operation.ADD));
                    break;
                case R.id.btnOperationSubstract:
                    startNewGame(100,EnumSet.of(Operation.SUBSTRACT));
                    break;
                case R.id.btnOperationMultiply:
                    startNewGame(10,EnumSet.of(Operation.MULTIPLY));
                    break;
                case R.id.btnOperationDivide:
                    startNewGame(10,EnumSet.of(Operation.DIVIDE));
                    break;
            }
        }
    };
    private CheckBox mCbAllowSecondTry;
    private TextView mTvAddSubRangeDescription;
    private TextView mTvMulDivRangeDescription;
    private TextView mChangeAddSubRange;
    private TextView mChangeMulDivRange;

    private void startNewGame(int range, EnumSet<Operation> operationId) {
        final Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra(GameActivity.INTENT_RANGE, range);
        gameIntent.putExtra(GameActivity.INTENT_OPERATION_ID,Operation.toArray(operationId));
        gameIntent.putExtra(GameActivity.INTENT_ALLOW_SECOND_TRY,mCbAllowSecondTry.isChecked());
        startActivity(gameIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }
}
