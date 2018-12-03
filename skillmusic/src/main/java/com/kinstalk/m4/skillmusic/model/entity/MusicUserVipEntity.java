package com.kinstalk.m4.skillmusic.model.entity;

public class MusicUserVipEntity {
    private int ret;
    private int sub_ret;
    private String msg;

    private MusicUserVipInfo vip_info;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public int getSub_ret() {
        return sub_ret;
    }

    public void setSub_ret(int sub_ret) {
        this.sub_ret = sub_ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MusicUserVipInfo getVip_info() {
        return vip_info;
    }

    public void setVip_info(MusicUserVipInfo vip_info) {
        this.vip_info = vip_info;
    }

    public static class MusicUserVipInfo {
        private String end_time;
        private String start_time;
        private String vip_pay_page;
        private String vip_name;
        private int vip_flag;

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getVip_pay_page() {
            return vip_pay_page;
        }

        public void setVip_pay_page(String vip_pay_page) {
            this.vip_pay_page = vip_pay_page;
        }

        public String getVip_name() {
            return vip_name;
        }

        public void setVip_name(String vip_name) {
            this.vip_name = vip_name;
        }

        public int getVip_flag() {
            return vip_flag;
        }

        public void setVip_flag(int vip_flag) {
            this.vip_flag = vip_flag;
        }
    }
}
