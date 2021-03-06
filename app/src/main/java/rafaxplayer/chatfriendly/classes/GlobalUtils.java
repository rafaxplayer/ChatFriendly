package rafaxplayer.chatfriendly.classes;

import android.text.format.DateUtils;

import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static rafaxplayer.chatfriendly.Chat_Friendly.mAuth;

/**
 * Created by rafax on 16/02/2017.
 */

public class GlobalUtils {

    public static FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }

    public static Long getTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        return tsLong;
    }

    public static String getDate(long timeStamp){

        try{

            Date netDate = (new Date(timeStamp*1000));
            String format="dd/MM/yyyy";
            if(DateUtils.isToday(timeStamp*1000)){
                format="hh:mm:ss a";

            }
            DateFormat sdf = new SimpleDateFormat(format);

            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }
}
