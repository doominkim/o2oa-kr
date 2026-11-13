package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.NumberTools;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.utils.SystemUtils;
import org.quartz.CronExpression;

public class Query extends ConfigObject {

    public static Query defaultInstance() {
        return new Query();
    }

    public Query() {
        this.extractImage = DEFAULT_EXTRACTIMAGE;
        this.tessLanguage = DEFAULT_TESSLANGUAGE;
        this.index = new Index();

    }

    @Deprecated(forRemoval = true)
    @FieldDescribe("이미지에서 텍스트 추출.")
    private Boolean extractImage = false;

    @Deprecated(forRemoval = true)
    @FieldDescribe("조회 배치 크기.")
    private Integer planQueryBatchSize = DEFAULT_PLANQUERYBATCHSIZE;

    public static final Boolean DEFAULT_EXTRACTOFFICE = true;
    public static final Boolean DEFAULT_EXTRACTPDF = true;
    public static final Boolean DEFAULT_EXTRACTTEXT = true;
    public static final Boolean DEFAULT_EXTRACTIMAGE = false;
    public static final String DEFAULT_TESSLANGUAGE = "chi_sim";
    public static final Integer DEFAULT_PLANQUERYBATCHSIZE = 500;
    public static final Boolean DEFAULT_STATEMENTDELETEENABLE = false;
    public static final Boolean DEFAULT_STATEMENTUPDATEENABLE = false;
    public static final Boolean DEFAULT_STATEMENTINSERTENABLE = false;

    @FieldDescribe("statement 구문의 delete 실행 허용 여부.")
    private Boolean statementDeleteEnable = DEFAULT_STATEMENTDELETEENABLE;

    @FieldDescribe("statement 구문의 update 실행 허용 여부.")
    private Boolean statementUpdateEnable = DEFAULT_STATEMENTUPDATEENABLE;

    @FieldDescribe("statement 구문의 insert 실행 허용 여부.")
    private Boolean statementInsertEnable = DEFAULT_STATEMENTINSERTENABLE;

    public Boolean getStatementDeleteEnable() {
        return BooleanUtils.isTrue(statementDeleteEnable);
    }

    public Boolean getStatementUpdateEnable() {
        return BooleanUtils.isTrue(statementUpdateEnable);
    }

    public Boolean getStatementInsertEnable() {
        return BooleanUtils.isTrue(statementInsertEnable);
    }

    public Integer getPlanQueryBatchSize() {
        return (this.planQueryBatchSize == null || planQueryBatchSize < 1) ? DEFAULT_PLANQUERYBATCHSIZE
                : this.planQueryBatchSize;
    }

    public Boolean getExtractImage() {
        return SystemUtils.IS_OS_WINDOWS && BooleanUtils.isTrue(extractImage);
    }

    public String getTessLanguage() {
        return StringUtils.isNotEmpty(this.tessLanguage) ? this.tessLanguage : DEFAULT_TESSLANGUAGE;
    }

    public void save() throws Exception {
        File file = new File(Config.base(), Config.PATH_CONFIG_QUERY);
        FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
    }

    public void setExtractImage(Boolean extractImage) {
        this.extractImage = extractImage;
    }

    public void setTessLanguage(String tessLanguage) {
        this.tessLanguage = tessLanguage;
    }

    @FieldDescribe("tess 사용 언어.")
    private String tessLanguage = "chi_sim";

    @FieldDescribe("검색 설정.")
    @Schema(description = "검색 설정.")
    private Index index;

    public Index index() {
        return this.index == null ? new Index() : this.index;
    }

    public static class Index extends ConfigObject {

        public static Index defaultInstance() {
            Index o = new Index();
            o.enable = DEFAULT_ENABLE;
            o.mode = DEFAULT_MODE;
            o.sharedDirectoryPath = DEFAULT_SHAREDDIRECTORYPATH;
            o.maxSegments = DEFAULT_MAXSEGMENTS;
            o.optimizeIndexEnable = DEFAULT_OPTIMIZEINDEXENABLE;
            o.optimizeIndexCron = DEFAULT_OPTIMIZEINDEXCRON;
            o.dataStringThreshold = DEFAULT_DATASTRINGTHRESHOLD;
            o.summaryLength = DEFAULT_SUMMARYLENGTH;
            o.attachmentMaxSize = DEFAULT_ATTACHMENTMAXSIZE;
            o.workIndexAttachment = DEFAULT_WORKINDEXATTACHMENT;
            o.workCompletedIndexAttachment = DEFAULT_WORKCOMPLETEDINDEXATTACHMENT;
            o.documentIndexAttachment = DEFAULT_DOCUMENTINDEXATTACHMENT;
            o.lowFreqWorkEnable = DEFAULT_LOWFREQWORKENABLE;
            o.lowFreqWorkCron = DEFAULT_LOWFREQWORKCRON;
            o.lowFreqWorkBatchSize = DEFAULT_LOWFREQWORKBATCHSIZE;
            o.lowFreqWorkMaxCount = DEFAULT_LOWFREQWORKMAXCOUNT;
            o.lowFreqWorkMaxMinutes = DEFAULT_LOWFREQWORKMAXMINUTES;
            o.lowFreqWorkCompletedEnable = DEFAULT_LOWFREQWORKCOMPLETEDENABLE;
            o.lowFreqWorkCompletedCron = DEFAULT_LOWFREQWORKCOMPLETEDCRON;
            o.lowFreqWorkCompletedBatchSize = DEFAULT_LOWFREQWORKCOMPLETEDBATCHSIZE;
            o.lowFreqWorkCompletedMaxCount = DEFAULT_LOWFREQWORKCOMPLETEDMAXCOUNT;
            o.lowFreqWorkCompletedMaxMinutes = DEFAULT_LOWFREQWORKCOMPLETEDMAXMINUTES;
            o.lowFreqDocumentEnable = DEFAULT_LOWFREQDOCUMENTENABLE;
            o.lowFreqDocumentCron = DEFAULT_LOWFREQDOCUMENTCRON;
            o.lowFreqDocumentBatchSize = DEFAULT_LOWFREQDOCUMENTBATCHSIZE;
            o.lowFreqDocumentMaxCount = DEFAULT_LOWFREQDOCUMENTMAXCOUNT;
            o.lowFreqDocumentMaxMinutes = DEFAULT_LOWFREQDOCUMENTMAXMINUTES;
            o.highFreqWorkEnable = DEFAULT_HIGHFREQWORKENABLE;
            o.highFreqWorkCron = DEFAULT_HIGHFREQWORKCRON;
            o.highFreqWorkBatchSize = DEFAULT_HIGHFREQWORKBATCHSIZE;
            o.highFreqWorkMaxCount = DEFAULT_HIGHFREQWORKMAXCOUNT;
            o.highFreqWorkMaxMinutes = DEFAULT_HIGHFREQWORKMAXMINUTES;
            o.highFreqWorkCompletedEnable = DEFAULT_HIGHFREQWORKCOMPLETEDENABLE;
            o.highFreqWorkCompletedCron = DEFAULT_HIGHFREQWORKCOMPLETEDCRON;
            o.highFreqWorkCompletedBatchSize = DEFAULT_HIGHFREQWORKCOMPLETEDBATCHSIZE;
            o.highFreqWorkCompletedMaxCount = DEFAULT_HIGHFREQWORKCOMPLETEDMAXCOUNT;
            o.highFreqWorkCompletedMaxMinutes = DEFAULT_HIGHFREQWORKCOMPLETEDMAXMINUTES;
            o.highFreqDocumentEnable = DEFAULT_HIGHFREQDOCUMENTENABLE;
            o.highFreqDocumentCron = DEFAULT_HIGHFREQDOCUMENTCRON;
            o.highFreqDocumentBatchSize = DEFAULT_HIGHFREQDOCUMENTBATCHSIZE;
            o.highFreqDocumentMaxCount = DEFAULT_HIGHFREQDOCUMENTMAXCOUNT;
            o.highFreqDocumentMaxMinutes = DEFAULT_HIGHFREQDOCUMENTMAXMINUTES;
            o.cleanupThresholdDays = DEFAULT_CLEANUPTHRESHOLDDAYS;
            o.searchEnable = DEFAULT_SEARCHENABLE;
            o.searchTitleBoost = DEFAULT_SEARCHTITLEBOOST;
            o.searchSummaryBoost = DEFAULT_SEARCHSUMMARYBOOST;
            o.searchBodyBoost = DEFAULT_SEARCHBODYBOOST;
            o.searchAttachmentBoost = DEFAULT_SEARCHATTACHMENTBOOST;
            o.searchPageSize = DEFAULT_SEARCHPAGESIZE;
            o.searchMaxPageSize = DEFAULT_SEARCHMAXPAGESIZE;
            o.searchMaxHits = DEFAULT_SEARCHMAXHITS;
            o.facetMaxGroups = DEFAULT_FACETMAXGROUPS;
            o.facetGroupOrder = DEFAULT_FACETGROUPORDER;
            o.highlightFragmentSize = DEFAULT_HIGHLIGHTFRAGMENTSIZE;
            o.highlightFragmentCount = DEFAULT_HIGHLIGHTFRAGMENTCOUNT;
            o.highlightPre = DEFAULT_HIGHLIGHTPRE;
            o.highlightPost = DEFAULT_HIGHLIGHTPOST;
            o.moreLikeThisSize = DEFAULT_MORELIKETHISSIZE;
            o.moreLikeThisMaxSize = DEFAULT_MORELIKETHISMAXSIZE;
            o.moreLikeThisMinTermFreq = DEFAULT_MORELIKETHISMINTERMFREQ;
            o.moreLikeThisMinDocFreq = DEFAULT_MORELIKETHISMINDOCFREQ;
            o.moreLikeThisScoreThreshold = DEFAULT_MORELIKETHISSCORETHRESHOLD;
            o.creatorUnitUnknown = DEFAULT_CREATORUNITUNKNOWN;
            o.creatorPersonUnknown = DEFAULT_CREATORPERSONUNKNOWN;
            o.creatorPersonCipher = DEFAULT_CREATORPERSONCIPHER;
            return o;
        }

        public static final Boolean DEFAULT_ENABLE = true;

        public static final String FIELD_HIGHLIGHTING = "highlighting";
        public static final String READERS_SYMBOL_ALL = "ALL";

        public static final String DIRECTORY_SEARCH = "search";

        public static final String MODE_LOCALDIRECTORY = "localDirectory";
        public static final String MODE_SHAREDDIRECTORY = "sharedDirectory";

        public static final String DEFAULT_SHAREDDIRECTORYPATH = "/repository/index";
        public static final String DEFAULT_MODE = MODE_LOCALDIRECTORY;
        public static final Integer DEFAULT_MAXSEGMENTS = 1;
        public static final Integer DEFAULT_CLEANUPTHRESHOLDDAYS = 180;

        public static final Integer DEFAULT_DATASTRINGTHRESHOLD = 50;
        public static final Integer DEFAULT_SUMMARYLENGTH = 250;
        public static final Integer DEFAULT_ATTACHMENTMAXSIZE = 5;
        public static final Boolean DEFAULT_WORKINDEXATTACHMENT = false;
        public static final Boolean DEFAULT_WORKCOMPLETEDINDEXATTACHMENT = true;
        public static final Boolean DEFAULT_DOCUMENTINDEXATTACHMENT = true;

        public static final Boolean DEFAULT_LOWFREQDOCUMENTENABLE = true;
        public static final String DEFAULT_LOWFREQDOCUMENTCRON = "40 15 19 * * ?";
        public static final Integer DEFAULT_LOWFREQDOCUMENTBATCHSIZE = 100;
        public static final Integer DEFAULT_LOWFREQDOCUMENTMAXCOUNT = 50000;
        public static final Integer DEFAULT_LOWFREQDOCUMENTMAXMINUTES = 60;

        public static final Boolean DEFAULT_LOWFREQWORKENABLE = true;
        public static final String DEFAULT_LOWFREQWORKCRON = "40 15 20 * * ?";
        public static final Integer DEFAULT_LOWFREQWORKBATCHSIZE = 100;
        public static final Integer DEFAULT_LOWFREQWORKMAXCOUNT = 50000;
        public static final Integer DEFAULT_LOWFREQWORKMAXMINUTES = 60;

        public static final Boolean DEFAULT_LOWFREQWORKCOMPLETEDENABLE = true;
        public static final String DEFAULT_LOWFREQWORKCOMPLETEDCRON = "40 15 21 * * ?";
        public static final Integer DEFAULT_LOWFREQWORKCOMPLETEDBATCHSIZE = 100;
        public static final Integer DEFAULT_LOWFREQWORKCOMPLETEDMAXCOUNT = 500000;
        public static final Integer DEFAULT_LOWFREQWORKCOMPLETEDMAXMINUTES = 120;

        public static final Boolean DEFAULT_OPTIMIZEINDEXENABLE = true;
        public static final String DEFAULT_OPTIMIZEINDEXCRON = "40 15 23 * * ?";

        public static final Boolean DEFAULT_HIGHFREQWORKENABLE = true;
        public static final String DEFAULT_HIGHFREQWORKCRON = "2 0/2 7-19 * * ?";
        public static final Integer DEFAULT_HIGHFREQWORKBATCHSIZE = 500;
        public static final Integer DEFAULT_HIGHFREQWORKMAXCOUNT = 5000;
        public static final Integer DEFAULT_HIGHFREQWORKMAXMINUTES = 2;

        public static final Boolean DEFAULT_HIGHFREQWORKCOMPLETEDENABLE = true;
        public static final String DEFAULT_HIGHFREQWORKCOMPLETEDCRON = "2 1/2 7-19 * * ?";
        public static final Integer DEFAULT_HIGHFREQWORKCOMPLETEDBATCHSIZE = 500;
        public static final Integer DEFAULT_HIGHFREQWORKCOMPLETEDMAXCOUNT = 5000;
        public static final Integer DEFAULT_HIGHFREQWORKCOMPLETEDMAXMINUTES = 2;

        public static final Boolean DEFAULT_HIGHFREQDOCUMENTENABLE = true;
        public static final String DEFAULT_HIGHFREQDOCUMENTCRON = "2 1/2 7-19 * * ?";
        public static final Integer DEFAULT_HIGHFREQDOCUMENTBATCHSIZE = 500;
        public static final Integer DEFAULT_HIGHFREQDOCUMENTMAXCOUNT = 5000;
        public static final Integer DEFAULT_HIGHFREQDOCUMENTMAXMINUTES = 2;

        public static final Boolean DEFAULT_SEARCHENABLE = true;

        public static final Float DEFAULT_SEARCHTITLEBOOST = 4.0f;
        public static final Float DEFAULT_SEARCHSUMMARYBOOST = 3.0f;
        public static final Float DEFAULT_SEARCHBODYBOOST = 2.0f;
        public static final Float DEFAULT_SEARCHATTACHMENTBOOST = 1.0f;

        public static final Integer DEFAULT_SEARCHPAGESIZE = 20;
        public static final Integer DEFAULT_SEARCHMAXPAGESIZE = 5000;

        public static final Integer DEFAULT_SEARCHMAXHITS = 500000;

        public static final Integer DEFAULT_FACETMAXGROUPS = 100;

        public static final String FACETGROUPORDER_KEYASC = "keyAsc";
        public static final String FACETGROUPORDER_KEYDESC = "keyDesc";
        public static final String FACETGROUPORDER_COUNTASC = "countAsc";
        public static final String FACETGROUPORDER_COUNTDESC = "countDesc";

        public static final String DEFAULT_FACETGROUPORDER = FACETGROUPORDER_KEYASC;

        public static final String DEFAULT_HIGHLIGHTPRE = "<em>";
        public static final String DEFAULT_HIGHLIGHTPOST = "</em>";
        public static final Integer DEFAULT_HIGHLIGHTFRAGMENTSIZE = 64;
        public static final Integer DEFAULT_HIGHLIGHTFRAGMENTCOUNT = 3;

        public static final Integer DEFAULT_MORELIKETHISSIZE = 10;
        public static final Integer DEFAULT_MORELIKETHISMAXSIZE = 100;
        public static final Integer DEFAULT_MORELIKETHISMINTERMFREQ = 2;
        public static final Integer DEFAULT_MORELIKETHISMINDOCFREQ = 2;
        public static final Float DEFAULT_MORELIKETHISSCORETHRESHOLD = 8.0f;

        private static final String UNKNOWN = "unknown";

        public static final String DEFAULT_CREATORUNITUNKNOWN = UNKNOWN;
        public static final String DEFAULT_CREATORPERSONUNKNOWN = UNKNOWN;
        public static final String DEFAULT_CREATORPERSONCIPHER = "시스템 서비스";

        @FieldDescribe("사용 여부.")
        @Schema(description = "사용 여부.")
        private Boolean enable;

        @FieldDescribe("인덱스 모드:localDirectory(로컬 파일 시스템), sharedDirectory(공유 파일 시스템 디렉터리).")
        @Schema(description = "인덱스 모드:localDirectory(로컬 파일 시스템), sharedDirectory(공유 파일 시스템 디렉터리).")
        private String mode;

        @FieldDescribe("공유 파일 시스템 디렉터리.")
        @Schema(description = "공유 파일 시스템 디렉터리.")
        private String sharedDirectoryPath;

        @FieldDescribe("인덱스 세그먼트 수.")
        @Schema(description = "인덱스 세그먼트 수.")
        private Integer maxSegments;

        @FieldDescribe("인덱스 최적화 사용 여부.")
        @Schema(description = "인덱스 최적화 사용 여부.")
        private Boolean optimizeIndexEnable;

        @FieldDescribe("인덱스 최적화 정기 실행 설정, 기본값:" + DEFAULT_OPTIMIZEINDEXCRON)
        @Schema(description = "인덱스 최적화 정기 실행 설정, 기본값:" + DEFAULT_OPTIMIZEINDEXCRON)
        private String optimizeIndexCron;

        @FieldDescribe("업무 데이터 최대 텍스트 길이 임계값, 초과 시 인덱스 기록 생략.")
        @Schema(description = "업무 데이터 최대 텍스트 길이 임계값, 초과 시 인덱스 기록 생략.")
        private Integer dataStringThreshold;

        @FieldDescribe("요약 길이.")
        @Schema(description = "요약 길이.")
        private Integer summaryLength;

        @FieldDescribe("첨부파일 인덱스 임계값(MB), 초과하는 첨부파일은 인덱싱하지 않음.")
        @Schema(description = "첨부파일 인덱스 임계값(MB), 초과하는 첨부파일은 인덱싱하지 않음.")
        private Integer attachmentMaxSize;

        @FieldDescribe("진행 중인 업무 문서의 첨부파일 인덱싱 여부.")
        @Schema(description = "진행 중인 업무 문서의 첨부파일 인덱싱 여부.")
        private Boolean workIndexAttachment;

        @FieldDescribe("완료된 업무 문서의 첨부파일 인덱싱 여부.")
        @Schema(description = "완료된 업무 문서의 첨부파일 인덱싱 여부.")
        private Boolean workCompletedIndexAttachment;

        @FieldDescribe("콘텐츠 관리 문서의 첨부파일 인덱싱 여부.")
        @Schema(description = "콘텐츠 관리 문서의 첨부파일 인덱싱 여부.")
        private Boolean documentIndexAttachment;

        @FieldDescribe("진행 중 업무 문서 저빈도 인덱스 사용 여부.")
        @Schema(description = "진행 중 업무 문서 저빈도 인덱스 사용 여부.")
        private Boolean lowFreqWorkEnable;

        @FieldDescribe("진행 중 업무 문서 저빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_LOWFREQWORKCRON)
        @Schema(description = "진행 중 업무 문서 저빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_LOWFREQWORKCRON)
        private String lowFreqWorkCron;

        @FieldDescribe("진행 중 업무 문서 저빈도 인덱스 일괄 조회 크기.")
        @Schema(description = "진행 중 업무 문서 저빈도 인덱스 일괄 조회 크기.")
        private Integer lowFreqWorkBatchSize;

        @FieldDescribe("진행 중 업무 문서 저빈도 인덱스 1회 최대 처리 개수.")
        @Schema(description = "진행 중 업무 문서 저빈도 인덱스 1회 최대 처리 개수.")
        private Integer lowFreqWorkMaxCount;

        @FieldDescribe("진행 중 업무 문서 저빈도 인덱스 1회 최대 처리 시간(분).")
        @Schema(description = "진행 중 업무 문서 저빈도 인덱스 1회 최대 처리 시간(분).")
        private Integer lowFreqWorkMaxMinutes;

        @FieldDescribe("완료된 업무 문서 저빈도 인덱스 사용 여부.")
        @Schema(description = "완료된 업무 문서 저빈도 인덱스 사용 여부.")
        private Boolean lowFreqWorkCompletedEnable;

        @FieldDescribe("완료된 업무 문서 저빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_LOWFREQWORKCOMPLETEDCRON)
        @Schema(description = "완료된 업무 문서 저빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_LOWFREQWORKCOMPLETEDCRON)
        private String lowFreqWorkCompletedCron;

        @FieldDescribe("완료된 업무 문서 저빈도 인덱스 일괄 조회 크기.")
        @Schema(description = "완료된 업무 문서 저빈도 인덱스 일괄 조회 크기.")
        private Integer lowFreqWorkCompletedBatchSize;

        @FieldDescribe("완료된 업무 문서 저빈도 인덱스 1회 최대 처리 개수.")
        @Schema(description = "완료된 업무 문서 저빈도 인덱스 1회 최대 처리 개수.")
        private Integer lowFreqWorkCompletedMaxCount;

        @FieldDescribe("완료된 업무 문서 저빈도 인덱스 1회 최대 처리 시간(분).")
        @Schema(description = "완료된 업무 문서 저빈도 인덱스 1회 최대 처리 시간(분).")
        private Integer lowFreqWorkCompletedMaxMinutes;

        @FieldDescribe("콘텐츠 관리 문서 저빈도 인덱스 사용 여부.")
        @Schema(description = "콘텐츠 관리 문서 저빈도 인덱스 사용 여부.")
        private Boolean lowFreqDocumentEnable;

        @FieldDescribe("콘텐츠 관리 문서 저빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_LOWFREQDOCUMENTCRON)
        @Schema(description = "콘텐츠 관리 문서 저빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_LOWFREQDOCUMENTCRON)
        private String lowFreqDocumentCron;

        @FieldDescribe("콘텐츠 관리 문서 저빈도 인덱스 일괄 조회 크기.")
        @Schema(description = "콘텐츠 관리 문서 저빈도 인덱스 일괄 조회 크기.")
        private Integer lowFreqDocumentBatchSize;

        @FieldDescribe("콘텐츠 관리 문서 저빈도 인덱스 1회 최대 처리 개수.")
        @Schema(description = "콘텐츠 관리 문서 저빈도 인덱스 1회 최대 처리 개수.")
        private Integer lowFreqDocumentMaxCount;

        @FieldDescribe("콘텐츠 관리 문서 저빈도 인덱스 1회 최대 처리 시간(분).")
        @Schema(description = "콘텐츠 관리 문서 저빈도 인덱스 1회 최대 처리 시간(분).")
        private Integer lowFreqDocumentMaxMinutes;

        @FieldDescribe("진행 중 업무 문서 고빈도 인덱스 사용 여부.")
        @Schema(description = "진행 중 업무 문서 고빈도 인덱스 사용 여부.")
        private Boolean highFreqWorkEnable;

        @FieldDescribe("진행 중 업무 문서 고빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_HIGHFREQWORKCRON)
        @Schema(description = "진행 중 업무 문서 고빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_HIGHFREQWORKCRON)
        private String highFreqWorkCron;

        @FieldDescribe("진행 중 업무 문서 고빈도 인덱스 일괄 조회 크기.")
        @Schema(description = "진행 중 업무 문서 고빈도 인덱스 일괄 조회 크기.")
        private Integer highFreqWorkBatchSize;

        @FieldDescribe("진행 중 업무 문서 고빈도 인덱스 1회 최대 처리 개수.")
        @Schema(description = "진행 중 업무 문서 고빈도 인덱스 1회 최대 처리 개수.")
        private Integer highFreqWorkMaxCount;

        @FieldDescribe("진행 중 업무 문서 고빈도 인덱스 1회 최대 처리 시간(분).")
        @Schema(description = "진행 중 업무 문서 고빈도 인덱스 1회 최대 처리 시간(분).")
        private Integer highFreqWorkMaxMinutes;

        @FieldDescribe("완료된 업무 문서 고빈도 인덱스 사용 여부.")
        @Schema(description = "완료된 업무 문서 고빈도 인덱스 사용 여부.")
        private Boolean highFreqWorkCompletedEnable;

        @FieldDescribe("완료된 업무 문서 고빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_HIGHFREQWORKCOMPLETEDCRON)
        @Schema(description = "완료된 업무 문서 고빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_HIGHFREQWORKCOMPLETEDCRON)
        private String highFreqWorkCompletedCron;

        @FieldDescribe("완료된 업무 문서 고빈도 인덱스 일괄 조회 크기.")
        @Schema(description = "완료된 업무 문서 고빈도 인덱스 일괄 조회 크기.")
        private Integer highFreqWorkCompletedBatchSize;

        @FieldDescribe("완료된 업무 문서 고빈도 인덱스 1회 최대 처리 개수.")
        @Schema(description = "완료된 업무 문서 고빈도 인덱스 1회 최대 처리 개수.")
        private Integer highFreqWorkCompletedMaxCount;

        @FieldDescribe("완료된 업무 문서 고빈도 인덱스 1회 최대 처리 시간(분).")
        @Schema(description = "완료된 업무 문서 고빈도 인덱스 1회 최대 처리 시간(분).")
        private Integer highFreqWorkCompletedMaxMinutes;

        @FieldDescribe("콘텐츠 관리 문서 고빈도 인덱스 사용 여부.")
        @Schema(description = "콘텐츠 관리 문서 고빈도 인덱스 사용 여부.")
        private Boolean highFreqDocumentEnable;

        @FieldDescribe("콘텐츠 관리 문서 고빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_HIGHFREQDOCUMENTCRON)
        @Schema(description = "콘텐츠 관리 문서 고빈도 인덱스 정기 실행 설정, 기본값:" + DEFAULT_HIGHFREQDOCUMENTCRON)
        private String highFreqDocumentCron;

        @FieldDescribe("콘텐츠 관리 문서 고빈도 인덱스 일괄 조회 크기.")
        @Schema(description = "콘텐츠 관리 문서 고빈도 인덱스 일괄 조회 크기.")
        private Integer highFreqDocumentBatchSize;

        @FieldDescribe("콘텐츠 관리 문서 고빈도 인덱스 1회 최대 처리 개수.")
        @Schema(description = "콘텐츠 관리 문서 고빈도 인덱스 1회 최대 처리 개수.")
        private Integer highFreqDocumentMaxCount;

        @FieldDescribe("콘텐츠 관리 문서 고빈도 인덱스 1회 최대 처리 시간(분).")
        @Schema(description = "콘텐츠 관리 문서 고빈도 인덱스 1회 최대 처리 시간(분).")
        private Integer highFreqDocumentMaxMinutes;

        @FieldDescribe("검색 콘텐츠 정리 임계값(일).")
        @Schema(description = "검색 콘텐츠 정리 임계값(일).")
        private Integer cleanupThresholdDays;

        @FieldDescribe("검색 사용 여부.")
        @Schema(description = "검색 사용 여부.")
        private Boolean searchEnable;

        @FieldDescribe("검색 제목 가중치.")
        @Schema(description = "검색 제목 가중치.")
        private Float searchTitleBoost;

        @FieldDescribe("검색 요약 가중치.")
        @Schema(description = "검색 요약 가중치.")
        private Float searchSummaryBoost;

        @FieldDescribe("검색 내용 가중치.")
        @Schema(description = "검색 내용 가중치.")
        private Float searchBodyBoost;

        @FieldDescribe("검색 첨부파일 가중치.")
        @Schema(description = "검색 첨부파일 가중치.")
        private Float searchAttachmentBoost;

        @FieldDescribe("검색 페이지 크기.")
        @Schema(description = "검색 페이지 크기.")
        private Integer searchPageSize;

        @FieldDescribe("검색 최대 페이지 크기.")
        @Schema(description = "검색 최대 페이지 크기.")
        private Integer searchMaxPageSize;

        @FieldDescribe("검색 최대 적중 개수.")
        @Schema(description = "검색 최대 적중 개수.")
        private Integer searchMaxHits;

        @FieldDescribe("차원 최대 그룹 수.")
        @Schema(description = "차원 최대 그룹 수.")
        private Integer facetMaxGroups;

        @FieldDescribe("차원 그룹 정렬 방식:keyAsc(그룹 값 기준 오름차순 정렬), keyDesc(그룹 값 기준 내림차순 정렬), countAsc(그룹 개수 기준 오름차순 정렬), countDesc(그룹 개수 기준 내림차순 정렬).")
        @Schema(description = "진행 중 업무 문서 저빈도 인덱스 정기 실행 설정.")
        private String facetGroupOrder;

        @FieldDescribe("하이라이트 반환 조각 길이.")
        @Schema(description = "하이라이트 반환 조각 길이.")
        private Integer highlightFragmentSize;
        @FieldDescribe("하이라이트 반환 조각 개수.")
        @Schema(description = "하이라이트 반환 조각 개수.")
        private Integer highlightFragmentCount;
        @FieldDescribe("하이라이트 접두사, 기본값 <em>.")
        @Schema(description = "하이라이트 접두사, 기본값 <em>.")
        private String highlightPre;
        @FieldDescribe("하이라이트 접미사, 기본값 </em>.")
        @Schema(description = "하이라이트 접미사, 기본값 </em>.")
        private String highlightPost;

        @FieldDescribe("유사 검색 반환 문서 개수.")
        @Schema(description = "유사 검색 반환 문서 개수.")
        private Integer moreLikeThisSize;

        @FieldDescribe("유사 검색 최대 반환 문서 개수.")
        @Schema(description = "유사 검색 최대 반환 문서 개수.")
        private Integer moreLikeThisMaxSize;

        @FieldDescribe("유사 검색에서 검색어가 최소 몇 번 나타나야 유효 검색어로 간주하는지.")
        @Schema(description = "유사 검색에서 검색어가 최소 몇 번 나타나야 유효 검색어로 간주하는지.")
        private Integer moreLikeThisMinTermFreq;

        @FieldDescribe("유사 검색에서 검색어가 최소 몇 개 문서에 나타나야 유효 검색어로 간주하는지.")
        @Schema(description = "유사 검색에서 검색어가 최소 몇 개 문서에 나타나야 유효 검색어로 간주하는지.")
        private Integer moreLikeThisMinDocFreq;

        @FieldDescribe("유사 검색 점수 임계값, 이 점수보다 낮으면 무시.")
        @Schema(description = "유사 검색 점수 임계값, 이 점수보다 낮으면 무시.")
        private Float moreLikeThisScoreThreshold;

        @FieldDescribe("creatorUnit이 비어 있을 때의 기본값.")
        @Schema(description = "creatorUnit이 비어 있을 때의 기본값.")
        private String creatorUnitUnknown;

        @FieldDescribe("creatorPerson이 비어 있을 때의 기본값.")
        @Schema(description = "creatorPerson이 비어 있을 때의 기본값.")
        private String creatorPersonUnknown;

        @FieldDescribe("creatorPerson 값이 cipher일 때의 대체 값.")
        @Schema(description = "creatorPerson 값이 cipher일 때의 대체 값.")
        private String creatorPersonCipher;

        public Boolean getEnable() {
            return BooleanUtils.isNotFalse(this.enable);
        }

        public String getCreatorUnitUnknown() {
            return StringUtils.isEmpty(this.creatorUnitUnknown) ? DEFAULT_CREATORUNITUNKNOWN
                    : this.creatorUnitUnknown;
        }

        public String getCreatorPersonUnknown() {
            return StringUtils.isEmpty(this.creatorPersonUnknown) ? DEFAULT_CREATORPERSONUNKNOWN
                    : this.creatorPersonUnknown;
        }

        public String getCreatorPersonCipher() {
            return StringUtils.isEmpty(this.creatorPersonCipher) ? DEFAULT_CREATORPERSONCIPHER
                    : this.creatorPersonCipher;
        }

        public Boolean getOptimizeIndexEnable() {
            return null == this.optimizeIndexEnable ? DEFAULT_OPTIMIZEINDEXENABLE : this.optimizeIndexEnable;
        }

        public String getOptimizeIndexCron() {
            if (StringUtils.isNotEmpty(this.optimizeIndexCron)
                    && CronExpression.isValidExpression(this.optimizeIndexCron)) {
                return this.optimizeIndexCron;
            } else {
                return DEFAULT_OPTIMIZEINDEXCRON;
            }
        }

        public Integer getMaxSegments() {
            return NumberTools.nullOrLessThan(this.maxSegments, 1) ? DEFAULT_MAXSEGMENTS
                    : this.maxSegments;
        }

        public Boolean getWorkIndexAttachment() {
            return (null == this.workIndexAttachment) ? DEFAULT_WORKINDEXATTACHMENT : this.workIndexAttachment;
        }

        public Boolean getWorkCompletedIndexAttachment() {
            return (null == this.workCompletedIndexAttachment) ? DEFAULT_WORKCOMPLETEDINDEXATTACHMENT
                    : this.workCompletedIndexAttachment;
        }

        public Boolean getDocumentIndexAttachment() {
            return (null == this.documentIndexAttachment) ? DEFAULT_DOCUMENTINDEXATTACHMENT
                    : this.documentIndexAttachment;
        }

        public Boolean getHighFreqWorkEnable() {
            return null == highFreqWorkEnable ? DEFAULT_HIGHFREQWORKENABLE : this.highFreqWorkEnable;
        }

        public String getHighFreqWorkCron() {
            if (StringUtils.isNotEmpty(this.highFreqWorkCron)
                    && CronExpression.isValidExpression(this.highFreqWorkCron)) {
                return this.highFreqWorkCron;
            } else {
                return DEFAULT_HIGHFREQWORKCRON;
            }
        }

        public Integer getHighFreqWorkBatchSize() {
            return NumberTools.nullOrLessThan(this.highFreqWorkBatchSize, 1) ? DEFAULT_HIGHFREQWORKBATCHSIZE
                    : this.highFreqWorkBatchSize;
        }

        public Integer getHighFreqWorkMaxCount() {
            return NumberTools.nullOrLessThan(this.highFreqWorkMaxCount, 1) ? DEFAULT_HIGHFREQWORKMAXCOUNT
                    : this.highFreqWorkMaxCount;
        }

        public Integer getHighFreqWorkMaxMinutes() {
            return NumberTools.nullOrLessThan(this.highFreqWorkMaxMinutes, 1) ? DEFAULT_HIGHFREQWORKMAXMINUTES
                    : this.highFreqWorkMaxMinutes;
        }

        public Boolean getLowFreqWorkEnable() {
            return null == lowFreqWorkEnable ? DEFAULT_LOWFREQWORKENABLE : this.lowFreqWorkEnable;
        }

        public String getLowFreqWorkCron() {
            if (StringUtils.isNotEmpty(this.lowFreqWorkCron)
                    && CronExpression.isValidExpression(this.lowFreqWorkCron)) {
                return this.lowFreqWorkCron;
            } else {
                return DEFAULT_LOWFREQWORKCRON;
            }
        }

        public Integer getLowFreqWorkBatchSize() {
            return NumberTools.nullOrLessThan(this.lowFreqWorkBatchSize, 1) ? DEFAULT_LOWFREQWORKBATCHSIZE
                    : this.lowFreqWorkBatchSize;
        }

        public Integer getLowFreqWorkMaxCount() {
            return NumberTools.nullOrLessThan(this.lowFreqWorkMaxCount, 1) ? DEFAULT_LOWFREQWORKMAXCOUNT
                    : this.lowFreqWorkMaxCount;
        }

        public Integer getLowFreqWorkMaxMinutes() {
            return NumberTools.nullOrLessThan(this.lowFreqWorkMaxMinutes, 1) ? DEFAULT_LOWFREQWORKMAXMINUTES
                    : this.lowFreqWorkMaxMinutes;
        }

        public Integer getMoreLikeThisSize() {
            return NumberTools.nullOrLessThan(this.moreLikeThisSize, 1) ? DEFAULT_MORELIKETHISSIZE
                    : this.moreLikeThisSize;
        }

        public Integer getMoreLikeThisMaxSize() {
            return NumberTools.nullOrLessThan(this.moreLikeThisMaxSize, 1) ? DEFAULT_MORELIKETHISMAXSIZE
                    : this.moreLikeThisMaxSize;
        }

        public Integer getMoreLikeThisMinTermFreq() {
            return NumberTools.nullOrLessThan(this.moreLikeThisMinTermFreq, 1) ? DEFAULT_MORELIKETHISMINTERMFREQ
                    : this.moreLikeThisMinTermFreq;
        }

        public Integer getMoreLikeThisMinDocFreq() {
            return NumberTools.nullOrLessThan(this.moreLikeThisMinDocFreq, 1) ? DEFAULT_MORELIKETHISMINDOCFREQ
                    : this.moreLikeThisMinDocFreq;
        }

        public Float getMoreLikeThisScoreThreshold() {
            return NumberTools.nullOrLessThan(this.moreLikeThisScoreThreshold, 0f) ? DEFAULT_MORELIKETHISSCORETHRESHOLD
                    : this.moreLikeThisScoreThreshold;
        }

        public Integer getHighlightFragmentSize() {
            return NumberTools.nullOrLessThan(this.highlightFragmentSize, 1) ? DEFAULT_HIGHLIGHTFRAGMENTSIZE
                    : this.highlightFragmentSize;
        }

        public Integer getHighlightFragmentCount() {
            return NumberTools.nullOrLessThan(this.highlightFragmentCount, 1) ? DEFAULT_HIGHLIGHTFRAGMENTCOUNT
                    : this.highlightFragmentCount;
        }

        public String getHighlightPre() {
            return StringUtils.isEmpty(this.highlightPre) ? DEFAULT_HIGHLIGHTPRE : this.highlightPre;
        }

        public String getHighlightPost() {
            return StringUtils.isEmpty(this.highlightPost) ? DEFAULT_HIGHLIGHTPOST : this.highlightPost;
        }

        public String getFacetGroupOrder() {
            return StringUtils.isBlank(facetGroupOrder) ? DEFAULT_FACETGROUPORDER
                    : facetGroupOrder;
        }

        public Integer getFacetMaxGroups() {
            return NumberTools.nullOrLessThan(this.facetMaxGroups, 1) ? DEFAULT_FACETMAXGROUPS
                    : this.facetMaxGroups;
        }

        public Integer getSearchMaxHits() {
            return NumberTools.nullOrLessThan(searchMaxHits, -1) ? DEFAULT_SEARCHMAXHITS : this.searchMaxHits;
        }

        public Integer getSearchPageSize() {
            return NumberTools.nullOrLessThan(searchPageSize, 1) ? DEFAULT_SEARCHPAGESIZE : searchPageSize;
        }

        public Integer getSearchMaxPageSize() {
            return NumberTools.nullOrLessThan(searchMaxPageSize, 1) ? DEFAULT_SEARCHMAXPAGESIZE : searchMaxPageSize;
        }

        public Float getSearchTitleBoost() {
            return NumberTools.nullOrLessThan(searchTitleBoost, 0) ? DEFAULT_SEARCHTITLEBOOST : this.searchTitleBoost;
        }

        public Float getSearchSummaryBoost() {
            return NumberTools.nullOrLessThan(searchSummaryBoost, 0) ? DEFAULT_SEARCHSUMMARYBOOST
                    : this.searchSummaryBoost;
        }

        public Float getSearchBodyBoost() {
            return NumberTools.nullOrLessThan(searchBodyBoost, 0) ? DEFAULT_SEARCHBODYBOOST : this.searchBodyBoost;
        }

        public Float getSearchAttachmentBoost() {
            return NumberTools.nullOrLessThan(searchAttachmentBoost, 0) ? DEFAULT_SEARCHATTACHMENTBOOST
                    : this.searchAttachmentBoost;
        }

        public Boolean getSearchEnable() {
            return null == searchEnable ? DEFAULT_SEARCHENABLE : this.searchEnable;
        }

        public String getLowFreqWorkCompletedCron() {
            if (StringUtils.isNotEmpty(this.lowFreqWorkCompletedCron)
                    && CronExpression.isValidExpression(this.lowFreqWorkCompletedCron)) {
                return this.lowFreqWorkCompletedCron;
            } else {
                return DEFAULT_LOWFREQWORKCOMPLETEDCRON;
            }
        }

        public String getLowFreqDocumentCron() {
            if (StringUtils.isNotEmpty(this.lowFreqDocumentCron)
                    && CronExpression.isValidExpression(this.lowFreqDocumentCron)) {
                return this.lowFreqDocumentCron;
            } else {
                return DEFAULT_LOWFREQDOCUMENTCRON;
            }
        }

        public String getHighFreqWorkCompletedCron() {
            if (StringUtils.isNotEmpty(this.highFreqWorkCompletedCron)
                    && CronExpression.isValidExpression(this.highFreqWorkCompletedCron)) {
                return this.highFreqWorkCompletedCron;
            } else {
                return DEFAULT_HIGHFREQWORKCOMPLETEDCRON;
            }
        }

        public String getHighFreqDocumentCron() {
            if (StringUtils.isNotEmpty(this.highFreqDocumentCron)
                    && CronExpression.isValidExpression(this.highFreqDocumentCron)) {
                return this.highFreqDocumentCron;
            } else {
                return DEFAULT_HIGHFREQDOCUMENTCRON;
            }
        }

        public String getSharedDirectoryPath() {
            return StringUtils.isBlank(sharedDirectoryPath)
                    ? DEFAULT_SHAREDDIRECTORYPATH
                    : adjustSharedDirectoryPath();
        }

        private String adjustSharedDirectoryPath() {
            return StringUtils.startsWith(this.sharedDirectoryPath, "/") ? this.sharedDirectoryPath
                    : "/" + sharedDirectoryPath;
        }

        public Integer getCleanupThresholdDays() {
            return NumberTools.nullOrLessThan(cleanupThresholdDays, 1)
                    ? DEFAULT_CLEANUPTHRESHOLDDAYS
                    : this.cleanupThresholdDays;
        }

        public Integer getLowFreqWorkCompletedMaxMinutes() {
            return NumberTools.nullOrLessThan(lowFreqWorkCompletedMaxMinutes, 1)
                    ? DEFAULT_LOWFREQWORKCOMPLETEDMAXMINUTES
                    : this.lowFreqWorkCompletedMaxMinutes;
        }

        public Integer getLowFreqWorkCompletedMaxCount() {
            return NumberTools.nullOrLessThan(lowFreqWorkCompletedMaxCount, 1)
                    ? DEFAULT_LOWFREQWORKCOMPLETEDMAXCOUNT
                    : this.lowFreqWorkCompletedMaxCount;
        }

        public Integer getLowFreqWorkCompletedBatchSize() {
            return NumberTools.nullOrLessThan(lowFreqWorkCompletedBatchSize, 1)
                    ? DEFAULT_LOWFREQWORKCOMPLETEDBATCHSIZE
                    : this.lowFreqWorkCompletedBatchSize;
        }

        public Boolean getLowFreqWorkCompletedEnable() {
            return null == lowFreqWorkCompletedEnable ? DEFAULT_LOWFREQWORKCOMPLETEDENABLE
                    : this.lowFreqWorkCompletedEnable;
        }

        public Integer getLowFreqDocumentMaxMinutes() {
            return NumberTools.nullOrLessThan(this.lowFreqDocumentMaxMinutes, 1)
                    ? DEFAULT_LOWFREQDOCUMENTMAXMINUTES
                    : this.lowFreqDocumentMaxMinutes;
        }

        public Integer getLowFreqDocumentMaxCount() {
            return NumberTools.nullOrLessThan(lowFreqDocumentMaxCount, 1)
                    ? DEFAULT_LOWFREQDOCUMENTMAXCOUNT
                    : this.lowFreqDocumentMaxCount;
        }

        public Integer getLowFreqDocumentBatchSize() {
            return NumberTools.nullOrLessThan(lowFreqDocumentBatchSize, 1)
                    ? DEFAULT_LOWFREQDOCUMENTBATCHSIZE
                    : this.lowFreqDocumentBatchSize;
        }

        public Boolean getLowFreqDocumentEnable() {
            return null == lowFreqDocumentEnable ? DEFAULT_LOWFREQDOCUMENTENABLE
                    : this.lowFreqDocumentEnable;
        }

        public Integer getHighFreqDocumentMaxMinutes() {
            return NumberTools.nullOrLessThan(this.highFreqDocumentMaxMinutes, 1)
                    ? DEFAULT_HIGHFREQDOCUMENTMAXMINUTES
                    : this.highFreqDocumentMaxMinutes;
        }

        public Integer getHighFreqDocumentMaxCount() {
            return NumberTools.nullOrLessThan(highFreqDocumentMaxCount, 1)
                    ? DEFAULT_HIGHFREQDOCUMENTMAXCOUNT
                    : this.highFreqDocumentMaxCount;
        }

        public Integer getHighFreqDocumentBatchSize() {
            return NumberTools.nullOrLessThan(highFreqDocumentBatchSize, 1)
                    ? DEFAULT_HIGHFREQDOCUMENTBATCHSIZE
                    : this.highFreqDocumentBatchSize;
        }

        public Boolean getHighFreqDocumentEnable() {
            return null == highFreqDocumentEnable ? DEFAULT_HIGHFREQDOCUMENTENABLE
                    : this.highFreqDocumentEnable;
        }

        public Integer getHighFreqWorkCompletedMaxMinutes() {
            return NumberTools.nullOrLessThan(this.highFreqWorkCompletedMaxMinutes, 1)
                    ? DEFAULT_HIGHFREQWORKCOMPLETEDMAXMINUTES
                    : this.highFreqWorkCompletedMaxMinutes;
        }

        public Integer getHighFreqWorkCompletedMaxCount() {
            return NumberTools.nullOrLessThan(highFreqWorkCompletedMaxCount, 1)
                    ? DEFAULT_HIGHFREQWORKCOMPLETEDMAXCOUNT
                    : this.highFreqWorkCompletedMaxCount;
        }

        public Integer getHighFreqWorkCompletedBatchSize() {
            return NumberTools.nullOrLessThan(highFreqWorkCompletedBatchSize, 1)
                    ? DEFAULT_HIGHFREQWORKCOMPLETEDBATCHSIZE
                    : this.highFreqWorkCompletedBatchSize;
        }

        public Boolean getHighFreqWorkCompletedEnable() {
            return null == highFreqWorkCompletedEnable ? DEFAULT_HIGHFREQWORKCOMPLETEDENABLE
                    : this.highFreqWorkCompletedEnable;
        }

        public Integer getAttachmentMaxSize() {
            return NumberTools.nullOrLessThan(this.attachmentMaxSize, 0) ? DEFAULT_ATTACHMENTMAXSIZE
                    : this.attachmentMaxSize;
        }

        public Integer getSummaryLength() {
            return NumberTools.nullOrLessThan(this.summaryLength, 1) ? DEFAULT_SUMMARYLENGTH : this.summaryLength;
        }

        public Integer getDataStringThreshold() {
            return NumberTools.nullOrLessThan(this.dataStringThreshold, 1) ? DEFAULT_DATASTRINGTHRESHOLD
                    : this.dataStringThreshold;
        }

        public String getMode() {
            if (StringUtils.equalsIgnoreCase(MODE_SHAREDDIRECTORY, this.mode)) {
                return MODE_SHAREDDIRECTORY;
            } else {
                return MODE_LOCALDIRECTORY;
            }
        }

    }
}
