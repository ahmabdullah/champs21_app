package com.champs21.spellingbee;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by BLACK HAT on 04-Jun-15.
 */
public class UserIdGeneration {

    public static String[] method = new String[]{"c", "p", "s", "m", "d"};
    public static String[] operator = new String[]{"m", "p"};
    public static boolean encoded_left = true;
    public static boolean encoded_right = true;
    public static boolean encoded_method = true;
    public static boolean encoded_operator = true;
    public static boolean encoded_send_id = true;


    private Map<String, String> mapArray = new HashMap<String, String>();


    public Map<String, String> createUserToken(int user_id)
    {
        String leftstring = String.valueOf(getRandomNumberInRange(1000, 10000000));
        String  rightstring = String.valueOf(getRandomNumberInRange(100, 100000));

        int leftvalue = leftstring.length();
        int rightvalue = rightstring.length();

        String method_main = method[getRandomNumberInRange(0, method.length-1)];
        String operator_main = operator[getRandomNumberInRange(0, operator.length-1)];


        String encoded_method = createMethodEncoded(method_main);

        int encripted_user_id = createEncriptedUserID(encoded_method, operator_main, user_id, leftvalue, rightvalue);

        String user_id_created = leftstring + String.valueOf(encripted_user_id) + rightstring;

        mapArray.put("left", String.valueOf(leftvalue));
        mapArray.put("right", String.valueOf(rightvalue));
        mapArray.put("method", encoded_method);
        mapArray.put("operator", operator_main);
        mapArray.put("user_id_token", user_id_created);


        String val = "";
        if (encoded_right)
        {
            val = getBase64Encode(mapArray.get("right"));
            mapArray.put("right", val);

        }

        if (encoded_left)
        {
            val = getBase64Encode(mapArray.get("left"));
            mapArray.put("left", val);
        }

        if (encoded_send_id)
        {
            val = getBase64Encode(mapArray.get("user_id_token"));
            mapArray.put("user_id_token", val);
        }

        if (UserIdGeneration.this.encoded_method)
        {
            val = getBase64Encode(mapArray.get("method"));
            mapArray.put("method", val);
        }

        if (encoded_operator)
        {
            val = getBase64Encode(mapArray.get("operator"));
            mapArray.put("operator", val);
        }


        return mapArray;

    }


    private String getBase64Encode(String text)
    {
        byte[] data = null;
        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);


        return base64;

    }


    private String createMethodEncoded(String method_main)
    {
        int length = getRandomNumberInRange(2, 5);

        String characters = "abefghijklnoqrtuvwxyz";


        int charactersLength = characters.length();

        String encoded_method = "";
        String randomString1 = "";



        for (int i = 0; i < length; i++)
        {
            randomString1 = randomString1+String.valueOf(characters.charAt(getRandomNumberInRange(0, charactersLength - 1)));
        }

        encoded_method= randomString1 + method_main;


        String randomString2 = "";
        for (int i = 0; i < length; i++)
        {
            randomString2 = randomString1+String.valueOf(characters.charAt(getRandomNumberInRange(0, charactersLength - 1)));
        }


        encoded_method= encoded_method + randomString2;

        return encoded_method;
    }


    public int createEncriptedUserID(String method, String operator, int send_id_without_lr, int left, int right)
    {
        int send_id_decrepted = 0;

        if(method.contains(UserIdGeneration.this.method[0]))
        {
            String concated_value = String.valueOf(left)+String.valueOf(right);
            int value = Integer.parseInt(concated_value);

            if (operator == UserIdGeneration.this.operator[0])
            {
                send_id_decrepted = send_id_without_lr + value;
            }
            else if (operator == UserIdGeneration.this.operator[1])
            {
                send_id_decrepted = send_id_without_lr - value;
            }

        }

        else if(method.contains(UserIdGeneration.this.method[1]))
        {

            int value = left+right;

            if (operator == UserIdGeneration.this.operator[0])
            {
                send_id_decrepted = send_id_without_lr + value;
            }
            else if (operator == UserIdGeneration.this.operator[1])
            {
                send_id_decrepted = send_id_without_lr - value;
            }

        }

        else if(method.contains(UserIdGeneration.this.method[2]))
        {

            int value = left - right;

            if (operator == UserIdGeneration.this.operator[0])
            {
                send_id_decrepted = send_id_without_lr + value;
            }
            else if (operator == UserIdGeneration.this.operator[1])
            {
                send_id_decrepted = send_id_without_lr - value;
            }

        }


        else if(method.contains(UserIdGeneration.this.method[3]))
        {


            int value = left * right;



            if (operator == UserIdGeneration.this.operator[0])
            {
                send_id_decrepted = send_id_without_lr + value;
            }
            else if (operator == UserIdGeneration.this.operator[1])
            {
                send_id_decrepted = send_id_without_lr - value;
            }

        }

        else if(method.contains(UserIdGeneration.this.method[4]))
        {
            int value = (int) Math.ceil(left / right); //need to ceil


            if (operator == UserIdGeneration.this.operator[0])
            {
                send_id_decrepted = send_id_without_lr + value;
            }
            else if (operator == UserIdGeneration.this.operator[1])
            {
                send_id_decrepted = send_id_without_lr - value;
            }

        }



        return send_id_decrepted;
    }




    private int getRandomNumberInRange(int minVal, int maxVal)
    {
        Random r = new Random();
        int num = r.nextInt(maxVal - minVal) + minVal;

        return num;
    }

}
