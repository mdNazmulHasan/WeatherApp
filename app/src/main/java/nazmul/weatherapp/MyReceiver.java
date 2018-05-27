package nazmul.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle=intent.getExtras();
        Intent intent1 = new Intent(context, MyIntentService.class);
        intent1.putExtra("data",bundle);
        context.startService(intent1);
    }
}
