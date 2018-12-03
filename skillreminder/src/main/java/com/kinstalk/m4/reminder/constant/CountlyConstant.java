package com.kinstalk.m4.reminder.constant;

/**
 * Created by pop on 17/7/10.
 */

public class CountlyConstant {

    //触屏交互
    /**
     * 查看提醒列表
     */
    public static final String T_VIEW_REMINDERS_LIST = "t_view_reminders_list";
    /**
     * 删除提醒
     */
    public static final String T_DELETE_REMINDER = "t_delete_reminder";
    /**
     * 关闭提醒
     */
    public static final String T_CLOSE_REMINDER = "t_close_reminder";

    //语音交互
    /**
     * 新增提醒成功
     * 通过segmentation记录：
     * 1. 提醒类型：比如 闹钟或者纪念日
     * 2. 闹钟的时间/周期
     * 3. 提醒的内容
     */
    public static final String V_ADD_REMINDER_SUCCEED = "v_add_reminder_succeed";
    /**
     * 识别错误
     */
    public static final String V_FAIL_IDENTIFICATION_ERROR = "v_fail_identification_error";
    /**
     * 识别正确，缺少时间因素
     */
    public static final String V_FAIL_LACK_OF_TIME = "v_fail_lack_of_time";
    /**
     * 识别正确，缺少内容因素
     */
    public static final String V_FAIL_LACK_OF_CONTENT = "v_fail_lack_of_content";
    /**
     * 不支持提醒
     */
    public static final String V_FAIL_UNSUPPORT = "v_fail_unsupport";
    /**
     * 关闭提醒-成功
     */
    public static final String V_CLOSE_REMINDER_SUCCEED = "v_close_reminder_succeed";
    /**
     * 关闭提醒-失败
     */
    public static final String V_CLOSE_REMINDER_FAIL = "v_close_reminder_fail";

    public static final String T_ADD_SUCCESSED = "t_add_succeed";

}
