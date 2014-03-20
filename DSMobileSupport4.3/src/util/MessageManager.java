package util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class MessageManager {

   public static void showShortMessage(Context context, CharSequence message){
	   int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, message, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);

		toast.show();
   }
}
