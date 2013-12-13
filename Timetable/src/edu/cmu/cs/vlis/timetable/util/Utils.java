package edu.cmu.cs.vlis.timetable.util;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.model.GraphUser;

import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

@SuppressLint("SimpleDateFormat")
public class Utils {

    @SuppressLint("SimpleDateFormat")
    public static String getFormattedDate(Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy");
        return sdf.format(c.getTime());
    }

    public static String getFormattedCurrentDateForHomeTabToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd");
        return sdf.format(new Date());
    }

    public static String getFromattedTimeForHomeTabTaskLecture(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(d);
    }

    // compare only the hour and minute
    @SuppressWarnings("deprecation")
    public static String getTimeDiffBetweenDates(Date d1, Date d2) {
        int hour = d2.getHours() - d1.getHours();
        int minute = d2.getMinutes() - d1.getMinutes();
        if (minute < 0) {
            hour--;
            minute += 60;
        }

        // if hour <= 0 and minute <= 0, which means d1 is later than d2, to
        // ignore negative minute,
        // return "0 minute" directly
        if (hour < 0 || (hour == 0 && minute == 0)) return "0 minute";

        StringBuffer diffResult = new StringBuffer();
        if (hour > 0) {
            if (hour == 1) diffResult.append("1 hour ");
            else diffResult.append(String.valueOf(hour) + " hours ");
        }

        if (minute > 0) {
            if (hour > 0) diffResult.append("and ");
            if (minute == 1) diffResult.append("1 minute ");
            else diffResult.append(String.valueOf(minute) + " minutes ");
        }

        return diffResult.toString();
    }

    public static Date parseDate(String date) throws IllegalArgumentException { 
        SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy");
        try {
            return sdf.parse(date);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException();
        }
    }

    public static String getFBUserMasterPWD() {
        return "c87b097e488e726a06357272056bbac157d577ab";
    }

    public static void setUserData(Intent intent, GraphUser user) {
        intent.putExtra("email", user.getProperty("email").toString());
        intent.putExtra("fname", user.getFirstName());
        intent.putExtra("lname", user.getLastName());
        intent.putExtra("gender", user.getProperty("gender").toString());
    }

    public static boolean isValidEmailAddr(String email) {
        if (email == null) return false;
        String emailreg = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(emailreg);
    }

    public static void displayNetworkErrorMessage(NetworkStatus networkStatus, Context context) {
        String toastText = null;
        switch (networkStatus) {
        case UNAUTHORIZED:
            toastText = "your session is expired, please log in again...";
            break;
        case NETWORK_ERROR:
            toastText = "network error... cannot fetch data from server";
            break;
        default:
            toastText = "unknown error... error code is " + networkStatus;
            break;
        }

        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
    }

    public static void displayErrorMessage(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static String convertSingleToDouble(int digit) {
        return (digit < 10 ? "0" : "") + digit;
    }
    
    public static void setButtonAsDrawable(Button button, int resId) {
        int bottom = button.getPaddingBottom();
        int top = button.getPaddingTop();
        int right = button.getPaddingRight();
        int left = button.getPaddingLeft();     
        
        button.setBackgroundResource(resId);    
        
        button.setPadding(left, top, right, bottom);
    }
    
    public static Bitmap convertBase64StringToBitmap(String base64String) {
        byte[] byteArray = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
    
    public static String compressAndConvertBitmapToBase64String(Bitmap bitmap) {
        // compress the bitmap
        ByteArrayOutputStream imageByteOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, imageByteOutputStream);

        // convert the bitmap to base64 String 
        byte[] imageByteArray = imageByteOutputStream.toByteArray();
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }
}
