package com.aispeech.aios.music.pojo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;

public abstract class BaseInfo implements Serializable, Cloneable {

    public static final String KEYWORDS_SEPERATOR = ",";
    /**
     * 名字
     */
    private String name;
    /**
     * 名字全拼
     */
    private String[] namePinyin;
    /**
     * 匹配类型(T9)
     */
    private MatchType matchType = MatchType.TYPE_NONE;
    /**
     * 待搜索文本在keywords匹配的起始位置：如果无法匹配，则该值为Integer.MAX_VALUE，如果能匹配，该值为实际的起始位置
     */
    private int matchPosition = Integer.MAX_VALUE;
    /**
     * 名字首字母T9
     */
    private String firstLetterT9Number;
    public static final Comparator<BaseInfo> COMPARATOR = new Comparator<BaseInfo>() {

        @Override
        public int compare(BaseInfo lhs, BaseInfo rhs) {
            int result = 0;
            int lValue = lhs.getMatchType().value();
            int rValue = rhs.getMatchType().value();
            if (lValue > rValue) {
                result = 1;
            } else if (lValue < rValue) {
                result = -1;
            } else {
                int lScore = lhs.getMatchPosition();
                int rScore = rhs.getMatchPosition();
                if (lValue == MatchType.TYPE_NAME_MATCH.value) {
                    if (lhs.getName() != null) {
                        lScore += lhs.getName().length();
                    }
                    if (rhs.getName() != null) {
                        rScore += rhs.getName().length();
                    }
                } else if (lValue == MatchType.TYPE_FIRST_LETTER_MATCH.value) {
                    if (lhs.getFirstLetterT9Number() != null) {
                        lScore += lhs.getFirstLetterT9Number().length();
                    }
                    if (rhs.getFirstLetterT9Number() != null) {
                        rScore += rhs.getFirstLetterT9Number().length();
                    }
                } else if (lValue == MatchType.TYPE_FULL_PINYIN_MATCH.value) {
                    if (lhs.getFirstPinyin() != null) {
                        lScore += lhs.getFirstPinyin().length();
                    }
                    if (rhs.getFirstPinyin() != null) {
                        rScore += rhs.getFirstPinyin().length();
                    }
                }
                if (lScore == rScore) {
                    result = 0;
                } else {
                    result = lScore < rScore ? -1 : 1;
                }
            }

            // AILog.d("Compare", String.valueOf(result) + ", \nlhs=" + lhs +
            // "\nrhs=" + rhs);
            return result;
        }
    };
    /**
     * 名字全拼T9
     */
    private String fullT9Number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getPinyin() {
        return namePinyin;
    }

    ;

    public void setPinyin(String[] fullPinyin) {
        this.namePinyin = fullPinyin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof BaseInfo)) {
            return false;
        }

        BaseInfo obj = (BaseInfo) o;
        if (this.name != null && this.name.equals(obj.getName())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (this.name != null && this.name.length() > 0) {
            return this.name.hashCode();
        }

        return 0;
    }

    public abstract JSONObject toJson() throws JSONException;

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType type) {
        this.matchType = type;
    }

    public int getMatchPosition() {
        return matchPosition;
    }

    public void setMatchPosition(int matchPosition) {
        this.matchPosition = matchPosition;
    }

    public String getFirstLetterT9Number() {
        return firstLetterT9Number;
    }

    public void setFirstLetterT9Number(String firstLetterT9Number) {
        this.firstLetterT9Number = firstLetterT9Number;
    }

    public String getFullT9Number() {
        return fullT9Number;
    }

    public void setFullT9Number(String fullT9Number) {
        this.fullT9Number = fullT9Number;
    }

    public String getFirstPinyin() {
        return (namePinyin != null && namePinyin.length > 0) ? namePinyin[0] : "";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BaseInfo clone = null;
        return clone;
    }

    /**
     * 匹配类型， 文本匹配/首字母匹配／全拼匹配／号码匹配
     */
    public enum MatchType {
        /**
         * 不匹配
         */
        TYPE_NONE(Integer.MAX_VALUE),
        /**
         * 文本匹配
         */
        TYPE_NAME_MATCH(0),
        /**
         * 首字母匹配
         */
        TYPE_FIRST_LETTER_MATCH(1),
        /**
         * 全拼匹配
         */
        TYPE_FULL_PINYIN_MATCH(2),
        /**
         * 号码匹配
         */
        TYPE_PHONE_MATCH(3);

        /**
         * 为了过滤分组排序用
         */
        private int value = -1;

        private MatchType(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }
}
