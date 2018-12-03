package com.kinstalk.her.weather.model.entity;

import android.text.TextUtils;

import com.kinstalk.her.weather.R;
import com.kinstalk.her.weather.WeatherApplication;

import java.util.List;

/**
 * Created by siqing on 17/7/17.
 */

public class AIResult {

    /**
     * engine : advtech
     * code : 0
     * semantic : {"slots":{"location":{"city":"北京","type":"LOC_BASIC"}}}
     * service : weather
     * playtts : 0
     * answer : {"text":"北京今天多云，温度25到34度，当前温度32度。","type":"T"}
     * voiceID : FZWAPXQXMGHRFPVUPHNFGHHAFTEMYMYL
     * data : {"result":[{"city":"北京","minTemp":"25","maxTemp":"34","weather":"多云","wind":"南风","date":"2017-07-17","airQuality":"良","pm25":"85"}]}
     */

    private String engine;
    private int code;
    private SemanticBean semantic;
    private String service;
    private int playtts;
    private AnswerBean answer;
    private String voiceID;
    private DataBean data;

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SemanticBean getSemantic() {
        return semantic;
    }

    public void setSemantic(SemanticBean semantic) {
        this.semantic = semantic;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getPlaytts() {
        return playtts;
    }

    public void setPlaytts(int playtts) {
        this.playtts = playtts;
    }

    public AnswerBean getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerBean answer) {
        this.answer = answer;
    }

    public String getVoiceID() {
        return voiceID;
    }

    public void setVoiceID(String voiceID) {
        this.voiceID = voiceID;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class SemanticBean {
        /**
         * slots : {"location":{"city":"北京","type":"LOC_BASIC"}}
         */

        private SlotsBean slots;

        public SlotsBean getSlots() {
            return slots;
        }

        public void setSlots(SlotsBean slots) {
            this.slots = slots;
        }

        public static class SlotsBean {
            /**
             * location : {"city":"北京","type":"LOC_BASIC"}
             */

            private LocationBean location;

            public LocationBean getLocation() {
                return location;
            }

            public void setLocation(LocationBean location) {
                this.location = location;
            }

            public static class LocationBean {
                /**
                 * city : 北京
                 * type : LOC_BASIC
                 */

                private String city;
                private String type;

                public String getCity() {
                    return city;
                }

                public void setCity(String city) {
                    this.city = city;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }
        }
    }

    public static class AnswerBean {
        /**
         * text : 北京今天多云，温度25到34度，当前温度32度。
         * type : T
         */

        private String text;
        private String type;
        private int format;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getFormat() {
            return format;
        }

        public void setFormat(int format) {
            this.format = format;
        }
    }

    public static class DataBean {
        private List<ResultBean> result;

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        public static class ResultBean {
            /**
             * city : 北京
             * minTemp : 25
             * maxTemp : 34
             * weather : 多云
             * wind : 南风
             * date : 2017-07-17
             * airQuality : 良
             * pm25 : 85
             */

            private String city;
            private String weather;
            private String wind;
            private String date;
            private String airQuality;
            private String pm25;
            private String curTemp;
            private String is_asked;
            private String wind_lv;
            private String minTemp;
            private String maxTemp;

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getWeather() {
                return weather;
            }

            public void setWeather(String weather) {
                this.weather = weather;
            }

            public String getWind() {
                return wind;
            }

            public void setWind(String wind) {
                this.wind = wind;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getAirQuality() {
                if (!TextUtils.isEmpty(airQuality)) {
                    return airQuality.replace(WeatherApplication.shareInstance().getString(R.string.text_pollution), "");
                }
                return airQuality;
            }

            public void setAirQuality(String airQuality) {
                this.airQuality = airQuality;
            }

            public String getPm25() {
                return pm25;
            }

            public void setPm25(String pm25) {
                this.pm25 = pm25;
            }

            public String getCurTemp() {
                return curTemp;
            }

            public void setCurTemp(String curTemp) {
                this.curTemp = curTemp;
            }

            public String getIs_asked() {
                return is_asked;
            }

            public void setIs_asked(String is_asked) {
                this.is_asked = is_asked;
            }

            public String getWind_lv() {
                return wind_lv;
            }

            public void setWind_lv(String wind_lv) {
                this.wind_lv = wind_lv;
            }

            public String getMinTemp() {
                return minTemp;
            }

            public void setMinTemp(String minTemp) {
                this.minTemp = minTemp;
            }

            public String getMaxTemp() {
                return maxTemp;
            }

            public void setMaxTemp(String maxTemp) {
                this.maxTemp = maxTemp;
            }

            @Override
            public String toString() {
                return "ResultBean{" +
                        "city='" + city + '\'' +
                        ", weather='" + weather + '\'' +
                        ", wind='" + wind + '\'' +
                        ", date='" + date + '\'' +
                        ", airQuality='" + airQuality + '\'' +
                        ", pm25='" + pm25 + '\'' +
                        ", curTemp='" + curTemp + '\'' +
                        ", is_asked='" + is_asked + '\'' +
                        ", wind_lv='" + wind_lv + '\'' +
                        ", minTemp='" + minTemp + '\'' +
                        ", maxTemp='" + maxTemp + '\'' +
                        '}';
            }
        }
    }
}
