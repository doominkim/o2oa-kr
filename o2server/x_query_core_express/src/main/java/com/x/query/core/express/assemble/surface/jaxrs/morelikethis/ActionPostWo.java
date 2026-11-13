package com.x.query.core.express.assemble.surface.jaxrs.morelikethis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.jaxrs.index.ActionPostWo")
public class ActionPostWo extends GsonPropertyObject {

    @FieldDescribe("최대 점수.")
    @Schema(description = "최대 점수.")
    private Float maxScore;

    @FieldDescribe("최소 점수.")
    @Schema(description = "최소 점수.")
    private Float minScore;

    @FieldDescribe("유사 객체.")
    @Schema(description = "유사 객체.")
    private List<WoMoreLikeThis> moreLikeThisList = new ArrayList<>();

    @FieldDescribe("반환 개수.")
    @Schema(description = "반환 개수.")
    private Long count;

    public Float getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Float maxScore) {
        this.maxScore = maxScore;
    }

    public Float getMinScore() {
        return minScore;
    }

    public void setMinScore(Float minScore) {
        this.minScore = minScore;
    }

    public List<WoMoreLikeThis> getMoreLikeThisList() {
        return moreLikeThisList;
    }

    public void setMoreLikeThisList(List<WoMoreLikeThis> moreLikeThisList) {
        this.moreLikeThisList = moreLikeThisList;
    }

    public static class WoMoreLikeThis extends GsonPropertyObject {

        @FieldDescribe("분류.")
        @Schema(description = "분류.")
        private String category;

        @FieldDescribe("유형.")
        @Schema(description = "유형.")
        private String type;

        @FieldDescribe("디렉터리 식별자.")
        @Schema(description = "디렉터리 식별자.")
        private String key;

        @FieldDescribe("식별자.")
        @Schema(description = "식별자.")
        private String flag;

        @FieldDescribe("제목.")
        @Schema(description = "제목.")
        private String title;

        @FieldDescribe("생성 시간.")
        @Schema(description = "생성 시간.")
        private Date createTime;

        @FieldDescribe("갱신 시간.")
        @Schema(description = "갱신 시간.")
        private Date updateTime;

        @FieldDescribe("생성자.")
        @Schema(description = "생성자.")
        private String creatorPerson;

        @FieldDescribe("생성자 조직.")
        @Schema(description = "생성자 조직.")
        private String creatorUnit;

        @FieldDescribe("점수.")
        @Schema(description = "점수.")
        private Float score;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Float getScore() {
            return score;
        }

        public void setScore(Float score) {
            this.score = score;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public String getCreatorPerson() {
            return creatorPerson;
        }

        public void setCreatorPerson(String creatorPerson) {
            this.creatorPerson = creatorPerson;
        }

        public String getCreatorUnit() {
            return creatorUnit;
        }

        public void setCreatorUnit(String creatorUnit) {
            this.creatorUnit = creatorUnit;
        }

    }

}