package com.greladesign.examples.games.mymathgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.greladesign.examples.games.mymathgame.game.Operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;


/**
 * Created by lukasz on 2015-06-22.
 */
public class GameActivity extends Activity {
    private static final String TAG = "GameActivity";

    private EnumSet<Operation> mGameId;
    private boolean mHadAnotherGo = false;
    private int mGameRange;
    private Random mRandom;
    private TextView mTvOperation;
    private TextView mTvArgument1;
    private TextView mTvArgument2;
    private List<Button> mAnswers;
    private View.OnClickListener mAnswerClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int answer = Integer.parseInt(((Button) v).getText().toString());
            int a = Integer.parseInt(mTvArgument1.getText().toString());
            int b = Integer.parseInt(mTvArgument2.getText().toString());
            boolean anotherGo = false;
            if (isCorrect(a, b, answer)) {
                Toast.makeText(getBaseContext(), "Very Good!",
                        Toast.LENGTH_LONG).show();
                mHadAnotherGo = false;
            } else {
                if(mHadAnotherGo == false) {
                    mHadAnotherGo = true;
                    anotherGo = (true);
                    Toast.makeText(getBaseContext(), "Not right, have another go",
                            Toast.LENGTH_LONG).show();
                } else {
                    mHadAnotherGo = false;
                    Toast.makeText(getBaseContext(), "Incorrect!",
                            Toast.LENGTH_LONG).show();
                }
            }
            //next question
            prepareQuestion(anotherGo);
        }
    };
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        findViews();
        final Intent intent = getIntent();
        mGameRange = intent.getIntExtra("range", -1);
        final int[] gameIdArray = intent.getIntArrayExtra("operationId");
        mGameId = Operation.fromArray(gameIdArray);

        prepareGame();
        prepareQuestion(false);


    }

    private void prepareGame() {
        long seed = 1979L;
        mRandom = new Random(seed);

        if (mGameId.contains(Operation.ADD)) {
            mTvOperation.setText("+");
            mTvTitle.setText(getResources().getString(R.string.game_type_title_add));
        }
        if (mGameId.contains(Operation.SUBSTRACT)) {
            mTvOperation.setText("−");
            mTvTitle.setText(getResources().getString(R.string.game_type_title_sub));
        }
        if (mGameId.contains(Operation.MULTIPLY)) {
            mTvOperation.setText("×");
            mTvTitle.setText(getResources().getString(R.string.game_type_title_mul));
        }
        if (mGameId.contains(Operation.DIVIDE)) {
            mTvOperation.setText("÷");
            mTvTitle.setText(getResources().getString(R.string.game_type_title_div));
        }
    }

    private void prepareQuestion(boolean anotherGo) {
        if(anotherGo){
            //hide one of incorrect buttonsint
            int index = mRandom.nextInt(3);
            index++;//avoid 0 - here it is the correct answer
            mAnswers.get(index).setVisibility(View.GONE);
        } else {

            int arg1 = mRandom.nextInt(mGameRange);
            arg1++;//avoid 0
            int arg2 = mRandom.nextInt(mGameRange);
            arg2++;//avoid 0

            mTvArgument1.setText("" + arg1);
            mTvArgument2.setText("" + arg2);

            //shuffle
            Collections.shuffle(mAnswers);
            //prepare answers
            final int answer = calculateAnswer(arg1, arg2);
            //correct
            mAnswers.get(0).setText("" + answer);
            //off by one +
            mAnswers.get(1).setText("" + (answer + 1));
            //off by one -
            mAnswers.get(2).setText("" + (answer - 1));
            int random = mRandom.nextBoolean() ? 5 : -5;
            //off by random
            mAnswers.get(3).setText("" + (answer + random));

            for (Button btn : mAnswers) {
                if(btn.getVisibility() == View.GONE) {
                    btn.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private boolean isCorrect(int a, int b, int answer) {
        return answer == calculateAnswer(a, b);
    }

    private int calculateAnswer(int a, int b) {
        if (mGameId.contains(Operation.ADD))
            return (a + b);
        if (mGameId.contains(Operation.SUBSTRACT))
            return (a - b);
        if (mGameId.contains(Operation.MULTIPLY))
            return (a * b);
        if (mGameId.contains(Operation.DIVIDE))
            return (a / b);

        return -1;
    }

    private void findViews() {

        mTvOperation = (TextView) findViewById(R.id.tvOperation);
        mTvArgument1 = (TextView) findViewById(R.id.tvArgument1);
        mTvArgument2 = (TextView) findViewById(R.id.tvArgument2);
        mTvTitle = (TextView) findViewById(R.id.tvTitle);

        Button mBtnAnswer1 = (Button) findViewById(R.id.btnAnswer1);
        Button mBtnAnswer2 = (Button) findViewById(R.id.btnAnswer2);
        Button mBtnAnswer3 = (Button) findViewById(R.id.btnAnswer3);
        Button mBtnAnswer4 = (Button) findViewById(R.id.btnAnswer4);

        mAnswers = new ArrayList<Button>();
        mAnswers.add(mBtnAnswer1);
        mAnswers.add(mBtnAnswer2);
        mAnswers.add(mBtnAnswer3);
        mAnswers.add(mBtnAnswer4);


        mBtnAnswer1.setOnClickListener(mAnswerClickedListener);
        mBtnAnswer2.setOnClickListener(mAnswerClickedListener);
        mBtnAnswer3.setOnClickListener(mAnswerClickedListener);
        mBtnAnswer4.setOnClickListener(mAnswerClickedListener);
    }
}
