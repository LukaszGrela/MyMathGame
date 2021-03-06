package com.greladesign.examples.games.mymathgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.greladesign.examples.games.mymathgame.game.AnswerIndicator;
import com.greladesign.examples.games.mymathgame.game.Operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;


/**
 * Created by Łukasz 'Severiaan' Grela on 2015-06-22.
 */
public class GameActivity extends AppCompatActivity {
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


    public static final String INTENT_RANGE_LOWER = "intent_range_lower";
    public static final String INTENT_RANGE_UPPER = "intent_range_upper";
    public static final String INTENT_OPERATION_ID = "intent_operationId";
    public static final String INTENT_ALLOW_SECOND_TRY = "intent_allowSecondTry";
    private static final String TAG = "GameActivity";
    private static final String GAME_ANSWER_CORRECT = "AnswerIndicator-correct";
    private static final String GAME_ANSWER_INCORRECT = "AnswerIndicator-incorrect";
    private static final String GAME_ANSWER_2NDTRY = "AnswerIndicator-2ndtry";

    //private static final String TAG = "GameActivity";

    private TextView mTvOperation;
    private TextView mTvArgument1;
    private TextView mTvArgument2;
    private TextView mTvTitle;
    private TextView mTvIncorrect;
    private TextView mTvCorrect;
    private List<Button> mAnswers;
    private View.OnClickListener mAnswerClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int answer = Integer.parseInt(((Button) v).getText().toString());
            int a = Integer.parseInt(mTvArgument1.getText().toString());
            int b = Integer.parseInt(mTvArgument2.getText().toString());
            boolean anotherGo = false;
            if (isCorrect(a, b, answer)) {
                //Toast.makeText(getBaseContext(), "Very Good!",
                //        Toast.LENGTH_SHORT).show();
                showCorrectPopup();
                mHadAnotherGo = false;
                //
                mCorrectCount++;
            } else {
                if (!mHadAnotherGo && mAllowSecondTry) {
                    mHadAnotherGo = true;
                    anotherGo = (true);
                    //Toast.makeText(getBaseContext(), "Not right, have another go",
                    //        Toast.LENGTH_SHORT).show();
                    show2ndTryPopup();
                    v.setVisibility(View.INVISIBLE);//hide incorrect answer
                } else {
                    mHadAnotherGo = false;
                    //Toast.makeText(getBaseContext(), "Incorrect!",
                    //        Toast.LENGTH_SHORT).show();
                    showInorrectPopup();
                    mIncorrectCount++;
                }
            }
            //next question
            prepareQuestion(anotherGo);
            updateStats();
        }
    };

    private void updateStats() {
        mTvIncorrect.setText(mIncorrectCount + "");
        mTvCorrect.setText(mCorrectCount + "");
    }


    private boolean mAllowSecondTry;
    private int mCorrectCount = 0;
    private int mIncorrectCount = 0;
    private int mGameRangeDifference;
    private int mGameRangeLower;
    private int mGameRangeUpper;
    private EnumSet<Operation> mGameId;
    private boolean mHadAnotherGo = false;
    private Random mRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViews();

        final Intent intent = getIntent();
        mGameRangeLower = intent.getIntExtra(INTENT_RANGE_LOWER, -1);
        Log.i(TAG, "Lower" + mGameRangeLower);
        mGameRangeUpper = intent.getIntExtra(INTENT_RANGE_UPPER, -1);
        Log.i(TAG, "Upper" + mGameRangeUpper);
        mGameRangeDifference = mGameRangeUpper - mGameRangeLower;
        Log.i(TAG, "Difference" + mGameRangeDifference);
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
                int sum = mRandom.nextInt(mGameRangeDifference) + mGameRangeLower;
                if (sum == 0) sum++;//more than 0
                Log.i(TAG, "Chosen sum:"+sum);
                arg1 = mRandom.nextInt(sum);
                arg1++;//avoid 0
                arg2 = sum - arg1;
            } else if (mGameId.contains(Operation.SUBSTRACT)) {
                int diff = mRandom.nextInt(mGameRangeDifference) + mGameRangeLower;
                if (diff == 0) diff++;//more than 0
                Log.i(TAG, "Chosen diff:"+diff);
                arg1 = mRandom.nextInt(mGameRangeUpper - diff) + diff;
                arg2 = arg1 - diff;
            } else if (mGameId.contains(Operation.DIVIDE)) {
                final int a = mRandom.nextInt(mGameRangeDifference) + mGameRangeLower;
                int b = mRandom.nextInt(mGameRangeDifference) + mGameRangeLower;
                if (b == 0) b++;//avoid 0 (division by 0 is illegal:)
                //
                final int mul = a * b;
                Log.i(TAG, "a:"+a);
                Log.i(TAG, "b:"+b);
                Log.i(TAG, "Chosen mul:"+mul);
                //
                arg1 = mul;
                arg2 = mRandom.nextBoolean() ? a : b;
                if(arg1 == 0 && arg2 == 0) {
                    arg2 = mRandom.nextInt(mGameRangeDifference) + mGameRangeLower;
                    if (arg2 == 0) arg2++;//avoid 0 (division by 0 is illegal:)
                }

            } else {
                arg1 = mRandom.nextInt(mGameRangeDifference) + mGameRangeLower;
                if (arg1 == 0) arg1++;//avoid 0
                arg2 = mRandom.nextInt(mGameRangeDifference) + mGameRangeLower;
                if (arg2 == 0) arg2++;//avoid 0
            }
            Log.i(TAG, "arg1:"+arg1);
            Log.i(TAG, "arg2:"+arg2);


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

    private void showCorrectPopup(){

        DialogFragment dialog = new AnswerIndicator();
        final Bundle arguments = new Bundle();

            arguments.putInt(AnswerIndicator.BUNDLE_ID_ICON_RES, R.drawable.img_answer_indicator_positive);
            arguments.putInt(AnswerIndicator.BUNDLE_ID_MESSAGE_RES, R.string.game_indicator_correct_msg);
            arguments.putInt(AnswerIndicator.BUNDLE_ID_TITLE_RES, R.string.game_indicator_correct_title);
            arguments.putInt(AnswerIndicator.BUNDLE_ID_BACKGROUND_RES, R.color.game_color_positive);

        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), GAME_ANSWER_CORRECT);
    }
    private void showInorrectPopup(){
        DialogFragment dialog = new AnswerIndicator();
        final Bundle arguments = new Bundle();

        arguments.putInt(AnswerIndicator.BUNDLE_ID_ICON_RES, R.drawable.img_answer_indicator_negative);
        arguments.putInt(AnswerIndicator.BUNDLE_ID_MESSAGE_RES, R.string.game_indicator_incorrect_msg);
        arguments.putInt(AnswerIndicator.BUNDLE_ID_TITLE_RES, R.string.game_indicator_incorrect_title);
        arguments.putInt(AnswerIndicator.BUNDLE_ID_BACKGROUND_RES, R.color.game_color_negative);

        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), GAME_ANSWER_INCORRECT);
    }
    private void show2ndTryPopup(){
        DialogFragment dialog = new AnswerIndicator();
        final Bundle arguments = new Bundle();

        arguments.putInt(AnswerIndicator.BUNDLE_ID_ICON_RES, R.drawable.img_answer_indicator_negative);
        arguments.putInt(AnswerIndicator.BUNDLE_ID_MESSAGE_RES, R.string.game_indicator_2ndtry_msg);
        arguments.putInt(AnswerIndicator.BUNDLE_ID_TITLE_RES, R.string.game_indicator_2ndtry_title);
        arguments.putInt(AnswerIndicator.BUNDLE_ID_BACKGROUND_RES, R.color.game_color_neutral);

        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), GAME_ANSWER_2NDTRY);
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
