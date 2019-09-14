package br.com.rcarvs.bibliotecappufsj;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmService {

    private Context context;
    private PendingIntent mAlarmSender;
    public AlarmService(Context context) {
        this.context = context;

    }

    public void startAlarm(Integer segundosAdicionais,Integer id,String titulo,String mensagem){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, segundosAdicionais);
        long firstTime = c.getTimeInMillis();
        // Schedule the alarm!
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intentao = new Intent(context, AlarmReceiver.class);
        intentao.putExtra("id",id);
        intentao.putExtra("titulo",titulo);
        intentao.putExtra("mensagem",mensagem);

        mAlarmSender = PendingIntent.getBroadcast(context, 0, intentao, 0);
        am.set(AlarmManager.RTC_WAKEUP, firstTime, mAlarmSender);
    }

    public void cancelAlarms(){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent updateServiceIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingUpdateIntent = PendingIntent.getService(context, 0, updateServiceIntent, 0);

        // Cancel alarms
        try {
            alarmManager.cancel(pendingUpdateIntent);
        } catch (Exception e) {
            Log.e("pau", "AlarmManager update was not canceled. " + e.toString());
        }
    }
}