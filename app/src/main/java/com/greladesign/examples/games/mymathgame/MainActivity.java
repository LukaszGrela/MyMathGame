package com.greladesign.examples.games.mymathgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.greladesign.examples.games.mymathgame.game.Operation;

import java.util.EnumSet;


public class MainActivity extends Activity {

    private Button btnGameAddition;
    private Button btnGameSubstraction;
    private Button btnGameMultiplication;
    private Button btnGameDivision;
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
        //add logic
        btnGameAddition.setOnClickListener(mSelectGameListener);
        btnGameSubstraction.setOnClickListener(mSelectGameListener);
        btnGameMultiplication.setOnClickListener(mSelectGameListener);
        btnGameDivision.setOnClickListener(mSelectGameListener);
    }
}
