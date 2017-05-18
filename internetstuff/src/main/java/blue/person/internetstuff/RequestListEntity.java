package blue.person.internetstuff;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by getbl on 2017/5/5.
 */

public class RequestListEntity {
    /**
     * info : OK
     * status : 200
     * proposal : 22422076
     * keyword : Impossible
     * candidates : [{"soundname":"","krctype":2,"nickname":"彩虹甲虫","originame":"","accesskey":"C3D2BF9DD8A47A3FFB622B660D820B8D","origiuid":"0","score":60,"hitlayer":7,"duration":227000,"sounduid":"0","transname":"","uid":"410927974","transuid":"0","song":"Impossible","id":"22422076","adjust":0,"singer":"Måns Zelmerlöw","language":""},{"soundname":"","krctype":2,"nickname":"一彎傷痕","originame":"","accesskey":"CE3981C62F140043C3346008E8B0344B","origiuid":"0","score":10,"hitlayer":7,"duration":231993,"sounduid":"0","transname":"","uid":"1518498","transuid":"0","song":"Impossible","id":"11578677","adjust":0,"singer":"shout out louds","language":"英语"}]
     */

    private String info;
    private int status;
    private String proposal;
    private String keyword;
    private List<CandidatesBean> candidates;

    public static RequestListEntity objectFromData(String str) {

        return new com.google.gson.Gson().fromJson(str, RequestListEntity.class);
    }

    public static List<RequestListEntity> arrayRequestListEntityFromData(String str) {

        Type listType = new com.google.gson.reflect.TypeToken<ArrayList<RequestListEntity>>() {
        }.getType();

        return new com.google.gson.Gson().fromJson(str, listType);
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProposal() {
        return proposal;
    }

    public void setProposal(String proposal) {
        this.proposal = proposal;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<CandidatesBean> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidatesBean> candidates) {
        this.candidates = candidates;
    }

    public static class CandidatesBean {
        /**
         * soundname :
         * krctype : 2
         * nickname : 彩虹甲虫
         * originame :
         * accesskey : C3D2BF9DD8A47A3FFB622B660D820B8D
         * origiuid : 0
         * score : 60
         * hitlayer : 7
         * duration : 227000
         * sounduid : 0
         * transname :
         * uid : 410927974
         * transuid : 0
         * song : Impossible
         * id : 22422076
         * adjust : 0
         * singer : Måns Zelmerlöw
         * language :
         */

        private String soundname;
        private int krctype;
        private String nickname;
        private String originame;
        private String accesskey;
        private String origiuid;
        private int score;
        private int hitlayer;
        private int duration;
        private String sounduid;
        private String transname;
        private String uid;
        private String transuid;
        private String song;
        private String id;
        private int adjust;
        private String singer;
        private String language;

        public static CandidatesBean objectFromData(String str) {

            return new com.google.gson.Gson().fromJson(str, CandidatesBean.class);
        }

        public static List<CandidatesBean> arrayCandidatesBeanFromData(String str) {

            Type listType = new com.google.gson.reflect.TypeToken<ArrayList<CandidatesBean>>() {
            }.getType();

            return new com.google.gson.Gson().fromJson(str, listType);
        }

        public String getSoundname() {
            return soundname;
        }

        public void setSoundname(String soundname) {
            this.soundname = soundname;
        }

        public int getKrctype() {
            return krctype;
        }

        public void setKrctype(int krctype) {
            this.krctype = krctype;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getOriginame() {
            return originame;
        }

        public void setOriginame(String originame) {
            this.originame = originame;
        }

        public String getAccesskey() {
            return accesskey;
        }

        public void setAccesskey(String accesskey) {
            this.accesskey = accesskey;
        }

        public String getOrigiuid() {
            return origiuid;
        }

        public void setOrigiuid(String origiuid) {
            this.origiuid = origiuid;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getHitlayer() {
            return hitlayer;
        }

        public void setHitlayer(int hitlayer) {
            this.hitlayer = hitlayer;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getSounduid() {
            return sounduid;
        }

        public void setSounduid(String sounduid) {
            this.sounduid = sounduid;
        }

        public String getTransname() {
            return transname;
        }

        public void setTransname(String transname) {
            this.transname = transname;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getTransuid() {
            return transuid;
        }

        public void setTransuid(String transuid) {
            this.transuid = transuid;
        }

        public String getSong() {
            return song;
        }

        public void setSong(String song) {
            this.song = song;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getAdjust() {
            return adjust;
        }

        public void setAdjust(int adjust) {
            this.adjust = adjust;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
