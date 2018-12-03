package com.kinstalk.m4.reminder.provider;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;

import com.kinstalk.m4.reminder.constant.RemindConstant;
import com.kinstalk.m4.reminder.entity.CalendarEvent;
import com.kinstalk.m4.reminder.entity.SkillAlarmBean;
import com.kinstalk.m4.reminder.entity.ai.ClockListBean;
import com.kinstalk.m4.reminder.recurrence.RecurrenceHelper;
import com.kinstalk.m4.reminder.util.DebugUtil;
import com.kinstalk.m4.reminder.util.QCardHelper;
import com.kinstalk.m4.reminder.util.SkillTimerUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.provider.CalendarContract.ACCOUNT_TYPE_LOCAL;

public class CalendarProviderHelper {
    private static final Uri calendarsUri = CalendarContract.Calendars.CONTENT_URI;
    private static final Uri eventsUri = CalendarContract.Events.CONTENT_URI;
    private static final Uri remindersUri = CalendarContract.Reminders.CONTENT_URI;
    private static final Uri instancesUri = CalendarContract.Instances.CONTENT_URI;
    static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.RRULE
    };

    static final String[] REMINDER_PROJECTION = new String[]{
            CalendarContract.Instances.BEGIN
    };

    public static void insertAIReminder(Context context, ClockListBean.ClockInfoBean entity) {
        long alarmTime = Long.valueOf(entity.getTrig_time()) * 1000L;
        if (entity.getRepeat_type() == SkillAlarmBean.CLOCK_REPEAT_TYPE.CLOCK_REPEAT_TYPE_WEEK) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(alarmTime));
            calendar.add(Calendar.DATE, -7);
            alarmTime = SkillTimerUtils.nextTime(calendar.getTimeInMillis(), entity.getRepeat_interval());
        }
        String rrule = new RecurrenceHelper().getRrule(entity.getRepeat_type(), entity.getRepeat_interval());
        insertEventAndReminder(context, entity.getEvent(), alarmTime, rrule, entity.getClock_id());
    }

    @SuppressLint("MissingPermission")
    public static boolean insertEventAndReminder(Context context, String title, long beginTime, String rrule, String id) {

        // 获取日历账户的id
        int calId = getCalendarAccount(context);
        if (calId < 0) {
            // 获取账户id失败直接返回，添加日历事件失败
            return false;
        }

        //添加事件
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events._ID, Integer.parseInt(id));
        event.put(CalendarContract.Events.TITLE, title);
        event.put(CalendarContract.Events.CALENDAR_ID, calId);

        event.put(CalendarContract.Events.DTSTART, beginTime);
        event.put(CalendarContract.Events.DTEND, beginTime + 60 * 1000);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        if (!TextUtils.isEmpty(rrule)) {
            event.put(CalendarContract.Events.RRULE, rrule);
        }

        Uri newEvent = context.getContentResolver().insert(eventsUri, event);
        if (newEvent == null) {
            return false;
        }

        //添加提醒
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        values.put(CalendarContract.Reminders.MINUTES, 0);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(remindersUri, values);
        if (uri == null) {
            return false;
        }
        ContentValues preValues = new ContentValues();
        preValues.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        if (beginTime - System.currentTimeMillis() > 24 * 60 * 60 * 1000) {
            preValues.put(CalendarContract.Reminders.MINUTES, 24 * 60);
        } else {
            preValues.put(CalendarContract.Reminders.MINUTES, (int) Math.ceil((double) (beginTime - System.currentTimeMillis()) / (double) (60 * 1000)));
        }
        preValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        context.getContentResolver().insert(remindersUri, preValues);
        return true;
    }

    /**
     * 根据给定的ID，判断是否是当前账号对应的
     *
     * @param eventId
     * @return
     */
    @SuppressLint("MissingPermission")
    public static boolean bAccountForAllApp(Context context, long eventId) {


        // 获取日历账户的id
        int calId = getCalendarAccount(context);
        if (calId < 0) {
            // 获取账户id失败直接返回
            return false;
        }

        String selection = CalendarContract.Events._ID + "=?";
        String[] args = new String[]{String.valueOf(eventId)};

        Cursor cursor = context.getContentResolver().query(
                eventsUri,
                null,
                selection,
                args,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            long accountId = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
            if (accountId == calId) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取提醒事件
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static List<CalendarEvent> getEvents(Context context) {

        //构建Instance表查询范围
        long startMillis = System.currentTimeMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(startMillis);
        endTime.add(Calendar.YEAR, 1);
        long endMillis = endTime.getTimeInMillis();
        DebugUtil.LogD("QueryEvent: start->" + getFormatTime(startMillis) + "  end->" + getFormatTime(endMillis));
        Uri.Builder builder = instancesUri.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);
        //查询Event表

        long accountId = getCalendarAccount(context);
        final long currentTime = System.currentTimeMillis();
        String event_selection = CalendarContract.Events.CALENDAR_ID + "=? AND (" + CalendarContract.Events.DTSTART + ">=? OR "
                + CalendarContract.Events.RRULE + " is not NULL" + ")";
        String[] event_selection_args = new String[]{String.valueOf(accountId),
                String.valueOf(currentTime)
        };
        String eventSort = CalendarContract.Events.DTSTART + " ASC";
        Cursor eventsCursor = context.getContentResolver().query(
                eventsUri,
                EVENT_PROJECTION,
                event_selection,
                event_selection_args,
                eventSort);
        List<CalendarEvent> dataList = new ArrayList<>();
        try {
            if (eventsCursor != null) {
                while (eventsCursor.moveToNext()) {
                    CalendarEvent event = new CalendarEvent();

                    String eid = eventsCursor.getString(eventsCursor.getColumnIndex(CalendarContract.Events._ID));
                    String calendarId = eventsCursor.getString(eventsCursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
                    String title = eventsCursor.getString(eventsCursor.getColumnIndex(CalendarContract.Events.TITLE));
                    String rrule = eventsCursor.getString(eventsCursor.getColumnIndex(CalendarContract.Events.RRULE));
                    if (!TextUtils.isEmpty(rrule)) {
                        String reminder_selection = CalendarContract.Instances.EVENT_ID + " =? AND " + CalendarContract.Instances.BEGIN + " >=?";
                        String[] reminder_selection_args = new String[]{eid, String.valueOf(currentTime)};
                        String reminderSort = CalendarContract.Instances.BEGIN + " ASC";
                        Cursor reminderCursor = context.getContentResolver().query(
                                builder.build(),
                                REMINDER_PROJECTION,
                                reminder_selection,
                                reminder_selection_args,
                                reminderSort);
                        try {
                            if (reminderCursor.moveToFirst()) {
                                long startDate = reminderCursor.getLong(reminderCursor.getColumnIndex(CalendarContract.Instances.BEGIN));
                                event.setStartTime(startDate);
                            } else {
                                long startDate = eventsCursor.getLong(eventsCursor.getColumnIndex(CalendarContract.Events.DTSTART));
                                event.setStartTime(startDate);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (reminderCursor != null) {
                                reminderCursor.close();
                            }
                        }
                        DebugUtil.LogD("QueryEvent.rrule:" + rrule + "      " + RecurrenceHelper.getRruleValue(context, rrule));
                        event.setRruleFormat(RecurrenceHelper.getRruleValue(context, rrule));
                    } else {
                        long startDate = eventsCursor.getLong(eventsCursor.getColumnIndex(CalendarContract.Events.DTSTART));
                        event.setStartTime(startDate);
                    }
                    event.setCalendarId(calendarId);
                    event.setEventId(eid);
                    event.setTitle(title);
                    event.setRrule(rrule);
                    DebugUtil.LogD("QueryEvent.dataFormat:" + "title->" + event.getTitle() + "      dtStart->" + getFormatTime(event.getStartTime()));
                    dataList.add(event);
                }
            }
        } finally {
            if (eventsCursor != null) {
                eventsCursor.close();
            }
        }
        return dataList;
    }

    @SuppressLint("MissingPermission")
    private static int getRemindType(Context context, String eventId) {

        int remindType = RemindConstant.RemindType.Type_Normal;
        String reminder_selection = CalendarContract.Reminders.EVENT_ID + " =?";
        String[] reminder_selection_args = new String[]{eventId};
        Cursor reminderCursor = context.getContentResolver().query(
                remindersUri,
                null,
                reminder_selection,
                reminder_selection_args,
                null);
        try {
            if (reminderCursor.moveToFirst()) {
                remindType = reminderCursor.getInt(reminderCursor.getColumnIndex("type"));
            }
        } finally {
            if (reminderCursor != null) {
                reminderCursor.close();
            }
        }
        return remindType;
    }

    @SuppressLint("MissingPermission")
    public static String getFormatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(time);
        return dateStr;
    }

    /**
     * 删除提醒
     *
     * @param context
     * @param eventId
     */
    @SuppressLint("MissingPermission")
    public static void deleteEvent(Context context, String eventId) {
        try {

            String selection = CalendarContract.Events._ID + " =" + eventId;
            context.getContentResolver().delete(
                    eventsUri,
                    selection,
                    null);

            String reminderSelection = CalendarContract.Reminders.EVENT_ID + " =" + eventId;
            context.getContentResolver().delete(remindersUri, reminderSelection, null);

            QCardHelper.cancelQCard(context, Integer.parseInt(eventId));
            context.sendBroadcast(new Intent("com.kinstalk.her.update.launcher.reminder"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public static void deleteAllEvents(Context context) {
        try {
            // 获取日历账户的id
            int calId = getCalendarAccount(context);
            if (calId < 0) {
                // 获取账户id失败直接返回
                return;
            }

            String select = CalendarContract.Events.CALENDAR_ID + "=?";
            String[] args = new String[]{String.valueOf(calId)};

            Cursor eventsCursor = context.getContentResolver().query(
                    eventsUri,
                    EVENT_PROJECTION,
                    select,
                    args,
                    null);
            if (eventsCursor != null) {
                while (eventsCursor.moveToNext()) {
                    String eventId = eventsCursor.getString(eventsCursor.getColumnIndex(CalendarContract.Events._ID));
                    String selection = CalendarContract.Events._ID + " =" + eventId;
                    context.getContentResolver().delete(
                            eventsUri,
                            selection,
                            null);

                    String reminderSelection = CalendarContract.Reminders.EVENT_ID + " =" + eventId;
                    context.getContentResolver().delete(remindersUri, reminderSelection, null);
                }
                DebugUtil.LogD("CalendarProviderHelper", "deleteAllEvents: deleteAll");
                context.sendBroadcast(new Intent("com.kinstalk.her.update.launcher.reminder"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取日历账户
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    private static int getCalendarAccount(Context context) {

        String selection = CalendarContract.Calendars.ACCOUNT_NAME + "=?";
        String[] selectionArgs = new String[]{"kinstalk_allapp"};

        Cursor userCursor = context.getContentResolver().query(calendarsUri, null, selection, selectionArgs, null);
        try {
            if (userCursor != null && userCursor.moveToFirst()) {
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            }

            return insertCalendarAccount(context);
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private static int insertCalendarAccount(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, "kinstalk_allapp");
        contentValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, ACCOUNT_TYPE_LOCAL);
        contentValues.put(CalendarContract.Calendars.NAME, "kinstalk_allapp");
        contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "kinstalk_allapp");
        contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        contentValues.put(CalendarContract.Calendars.VISIBLE, 1);
        contentValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        contentValues.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
        contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, "kinstalk_allapp");
        contentValues.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri insertUri = calendarsUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "kinstalk_allapp")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, ACCOUNT_TYPE_LOCAL)
                .build();

        Uri result = context.getContentResolver().insert(insertUri, contentValues);
        long id = result == null ? -1 : ContentUris.parseId(result);

        return (int) id;
    }

}
