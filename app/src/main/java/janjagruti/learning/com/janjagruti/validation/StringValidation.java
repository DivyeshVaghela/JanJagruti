package janjagruti.learning.com.janjagruti.validation;

import android.util.Patterns;

import java.util.regex.Pattern;

public class StringValidation {

    public static boolean isValidEmailAddress(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidMobileNo(CharSequence mobileNo){

        Pattern mobileNoPattern = Pattern.compile("^[0-9]{10}$");
        return mobileNoPattern.matcher(mobileNo).matches();
    }
}
