package com.champs21.spellingbee;

import com.champs21.schoolapp.utils.UserHelper;

/**
 * Created by BLACK HAT on 27-May-15.
 */
public class SpellingbeeConstants {

    public static long SPELLINGBEE_TIMER = 30000;
    public static int PER_ITEM_PICK_COUNT = 2;
    public static int CHECK_POINT = 20;

    public static final String KEY_CHECKPOINT_POSITION = UserHelper.getUserFreeId()+"KEY_CHECKPOINT_POSITION"; //id needed

    public static final String KEY_DATA_CHECKPOINT = "KEY_DATA_CHECKPOINT";

    public static int PER_ITEM_PICK_COUNT_EASY = 50;
    public static int PER_ITEM_PICK_COUNT_MEDIUM = 50;
    public static int PER_ITEM_PICK_COUNT_HARD = 20;
    public static int PER_ITEM_PICK_COUNT_EXTREME_HARD = 20;


    public static final String KEY_SAVE_FULL_DATA = UserHelper.getUserFreeId()+"KEY_SAVE_FULL_DATA"; //id needed
    public static final String KEY_SAVE_FULL_SCORE = UserHelper.getUserFreeId()+"KEY_SAVE_FULL_SCORE"; //id needed


    public static final String KEY_RESULT_PAGE_TYPED_WORD = "KEY_RESULT_PAGE_TYPED_WORD";
    public static final String KEY_RESULT_PAGE_ACTUAL_WORD = "KEY_RESULT_PAGE_ACTUAL_WORD";
    public static final String KEY_RESULT_PAGE_TIME_TAKEN = "KEY_RESULT_PAGE_TIME_TAKEN";
    public static final String KEY_RESULT_PAGE_CURRENT_SCORE = "KEY_RESULT_PAGE_CURRENT_SCORE";


    public static final String CURRENT_BANK_NUMBER = UserHelper.getUserFreeId()+"CURRENT_BANK_NUMBER"; //id needed

    public static final String CURRENT_SCORE_LEADERBOARD = "CURRENT_SCORE_LEADERBOARD";

    public static final String KEY_SCORE_FOR_FB_SHARE = "KEY_SCORE_FOR_FB_SHARE";

}
