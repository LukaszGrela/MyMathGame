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
 * Created by Łukasz 'Severiaan' Grela on 2015-06-22.
 */
public class GameActivity extends Activity {
    //Done - add/fix range handling for each game
    //TODO: save game state (seed, number of steps taken etc.)
    //TODO: SquiDB statistics logging
    //TODO: Statistics - good + 2nd attempts/bad answers
    //TODO: Statistics - timings - duration of answer
    //TODO: Game type selection - timed (e.g. 1,2 or 3mins)
    //TODO: Game type selection - time-pressure, X number of question with time limit per answer (e.g. 6, 4, 2 sec)
    //TODO: Game type selection - continous restricted by a number of mistakes (e.g. 3, 6 or 10)
    //TODO: Game type selection - finite, restricted by a number of questions (e.g. 10, 25, 50, 75 or 100)
    //TODO: visual make-up


    public static final String INTENT_RANGE = "intent_range";
    public static final String INTENT_OPERATION_ID = "intent_operationId";
    public static final String INTENT_ALLOW_SECOND_TRY = "intent_allowSecondTry";

    //private static final String TAG = "GameActivity";

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
                //
                mCorrectCount++;
            } else {
                if (!mHadAnotherGo && mAllowSecondTry) {
                    mHadAnotherGo = true;
                    anotherGo = (true);
                    Toast.makeText(getBaseContext(), "Not right, have another go",
                            Toast.LENGTH_LONG).show();
                    v.setVisibility(View.INVISIBLE);//hide incorrect answer
                } else {
                    mHadAnotherGo = false;
                    Toast.makeText(getBaseContext(), "Incorrect!",
                            Toast.LENGTH_LONG).show();
                    mIncorrectCount++;
                }
            }
            //next question
            prepareQuestion(anotherGo);
            updateStats();
        }
    };
    private boolean mAllowSecondTry;

    private void updateStats() {
        mTvIncorrect.setText(mIncorrectCount + "");
        mTvCorrect.setText(mCorrectCount + "");
    }

    private TextView mTvTitle;
    private TextView mTvIncorrect;
    private TextView mTvCorrect;
    private int mCorrectCount = 0;
    private int mIncorrectCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViews();

        final Intent intent = getIntent();
        mGameRange = intent.getIntExtra(INTENT_RANGE, -1);
        final int[] gameIdArray = intent.getIntArrayExtra(INTENT_OPERATION_ID);
        mGameId = Operation.fromArray(gameIdArray);
        mAllowSecondTry = intent.getBooleanExtra(INTENT_ALLOW_SECOND_TRY, false);

        prepareGame();
        prepareQuestion(false);
        updateStats();
    }

    private void prepareGame() {
        //reset counters
        mCorrectCount = 0;
        mIncorrectCount = 0;

        long seed = 1979L;
        mRandom = new Random(seed);

        if (mGameId.contains(Operation.ADD)) {
            mTvOperation.setText(getResources().getString(R.string.game_type_label_add));
            mTvTitle.setText(getResources().getString(R.string.game_type_title_add));
        }
        if (mGameId.contains(Operation.SUBSTRACT)) {
            mTvOperation.setText(getResources().getString(R.string.game_type_label_sub));
            mTvTitle.setText(getResources().getString(R.string.game_type_title_sub));
        }
        if (mGameId.contains(Operation.MULTIPLY)) {
            mTvOperation.setText(getResources().getString(R.string.game_type_label_mul));
            mTvTitle.setText(getResources().getString(R.string.game_type_title_mul));
        }
        if (mGameId.contains(Operation.DIVIDE)) {
            mTvOperation.setText(getResources().getString(R.string.game_type_label_div));
            mTvTitle.setText(getResources().getString(R.string.game_type_title_div));
        }
    }

    private void prepareQuestion(boolean anotherGo) {
        if (!anotherGo) {

            int arg1;
            int arg2;
            if (mGameId.contains(Operation.ADD)) {
                //we need to make sure sum will be within range
                final int sum = mRandom.nextInt(mGameRange) + 1;//more than 0
                arg1 = mRandom.nextInt(sum);
                arg1++;//avoid 0
                arg2 = sum - arg1;
            } else if (mGameId.contains(Operation.SUBSTRACT)) {
                final int diff = mRandom.nextInt(mGameRange);
                arg1 = mRandom.nextInt(mGameRange - diff) + diff;
                arg2 = arg1 - diff;
            } else if (mGameId.contains(Operation.DIVIDE)) {
                final int a = mRandom.nextInt(mGameRange);
                int b = mRandom.nextInt(mGameRange);
                b++;//avoid 0 (division by 0 is illegal:)

                final int mul = a * b;

                arg1 = mul;
                arg2 = mRandom.nextBoolean() ? a : b;
                if(arg1 == 0 && arg2 == 0) {
                    arg2 = mRandom.nextInt(mGameRange);
                    arg2++;//avoid 0 (division by 0 is illegal:)
                }

            } else {
                arg1 = mRandom.nextInt(mGameRange);
                arg1++;//avoid 0
                arg2 = mRandom.nextInt(mGameRange);
                arg2++;//avoid 0
            }


            mTvArgument1.setText("" + arg1);
            mTvArgument2.setText("" + arg2);

            //shuffle answer buttons
            Collections.shuffle(mAnswers);
            //prepare answers
            final int answer = calculateAnswer(arg1, arg2);
            //correct
            mAnswers.get(0).setText("" + answer);
            //off by one +
            mAnswers.get(1).setText("" + (answer + 1));
            //off by one -
            mAnswers.get(2).setText("" + (answer - 1));
            //off by randomly picked -5 or 5
            int random = mRandom.nextBoolean() ? 5 : -5;
            mAnswers.get(3).setText("" + (answer + random));

            for (Button btn : mAnswers) {
                if (btn.getVisibility() == View.INVISIBLE) {
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
        if (mGameId.contains(Operation.DIVIDE)) {
            if (b == 0) return -1;
            return (a / b);
        }

        return -1;
    }

    private void findViews() {

        mTvOperation = (TextView) findViewById(R.id.tvOperation);
        mTvArgument1 = (TextView) findViewById(R.id.tvArgument1);
        mTvArgument2 = (TextView) findViewById(R.id.tvArgument2);
        mTvTitle = (TextView) findViewById(R.id.tvTitle);

        mTvCorrect = (TextView) findViewById(R.id.tvCorrect);
        mTvIncorrect = (TextView) findViewById(R.id.tvIncorrect);

        Button mBtnAnswer1 = (Button) findViewById(R.id.btnAnswer1);
        Button mBtnAnswer2 = (Button) findViewById(R.id.btnAnswer2);
        Button mBtnAnswer3 = (Button) findViewById(R.id.btnAnswer3);
        Button mBtnAnswer4 = (Button) findViewById(R.id.btnAnswer4);

        mAnswers = new ArrayList<>();
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
