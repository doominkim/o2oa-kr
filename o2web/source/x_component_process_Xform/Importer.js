MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "Button", null, false);
/** @class Importer 데이터 가져오기 컴포넌트. 가져오기 모델로 데이터를 가져오며, 콘텐츠 관리 문서, 프로세스 관리 work, 자체 테이블 데이터 가져오기를 지원.
 * @o2cn 데이터 가져오기 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var importer = this.form.get("fieldId"); //컴포넌트 가져오기
 * //방법 2
 * var importer = this.target; //컴포넌트 자체 스크립트에서 조회
 * @extends MWF.xApplication.process.Xform.Button
 * @o2category FormComponents
 * @since v6.2
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Importer = MWF.APPImporter =  new Class(
    /** @lends MWF.xApplication.process.Xform.Importer# */
    {
	Implements: [Events],
    Extends: MWF.xApplication.process.Xform.Button,
    options: {
        /**
         * importer(가져오기 모델 객체) 로드 시 실행, this.target.importer로 가져오기 모델 객체 조회 가능.
         * @event MWF.xApplication.process.Xform.Importer#loadImporter
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 가져오기 전 발생, this.event는 가져올 데이터이며 this.event를 수정해 데이터를 변경할 수 있음.
         * @event MWF.xApplication.process.Xform.Importer#beforeImport
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         * @example
         * <caption>this.event 데이터 형식은 다음과 같음:</caption>
         *[
         *  [ "제목1","홍길동","남","대학교 학사","컴퓨터","2001-1-2","2019-9-2" ], //첫 번째 행 데이터
         *  [ "제목2","김철수","남","전문대","수학","1998-1-2","2018-9-2" ]  //두 번째 행 데이터
         *]
         */
        /**
         * 데이터가 생성되어 프런트에서 데이터 검증 시 발생, this.event는 가져올 데이터.
         * @event MWF.xApplication.process.Xform.Importer#validImport
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         * @example
         * <caption>this.event 데이터 형식은 다음과 같음:</caption>
         * {
         *     "data" : [
         *          [ "제목1","홍길동","남","대학교 학사","컴퓨터","2001-1-2","2019-9-2" ], //첫 번째 행 데이터
         *          [ "제목2","김철수","남","전문대","수학","1998-1-2","2018-9-2" ]  //두 번째 행 데이터
         * 	    ],
         *     "rowList": [], //가져올 행 객체 배열, 데이터 형식은 이 장 API의 afterCreateRowData 설명 참고.
         *     "validted" : true  //검증 통과 여부, 이 이벤트에서 수정해 강제 가져오기 여부를 결정할 수 있음
         * }
         */
        /**
         * 프런트 검증 성공 후 서버에서 가져오기를 마친 뒤 발생, this.event는 서버가 반환한 가져오기 결과.
         * @event MWF.xApplication.process.Xform.Importer#afterImport
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         * @example
         * <caption>this.event 형식은 다음과 같음:</caption>
         * {
         *     "status": "가져오기 성공", //가져오기 결과: 상태는 "가져오기 성공","일부 성공","가져오기 실패"
         *     "data": {}, //프런트에서 구성한 가져올 데이터
         *     "rowList": [], //프런트에서 구성한 행 객체 배열
         *     "count" : 10, //가져오기 총 개수
         *     "failCount": 0, //실패 개수
         *     "distribution": "" //가져오기 시 오류 정보
         * }
         */
        /**
         * 가져올 각 행의 데이터를 생성하기 전에 발생, this.event는 현재 행 객체를 가리키며 this.event.importedData를 수정하여 데이터를 변경할 수 있습니다.
         * @event MWF.xApplication.process.Xform.Importer#beforeCreateRowData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 가져올 각 행의 데이터를 생성한 후에 발생, this.event는 현재 행 객체를 가리킵니다.
         * @event MWF.xApplication.process.Xform.Importer#afterCreateRowData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         * @example
         * <caption>this.event 형식은 다음과 같음:</caption>
         * {
         *     "importData": [ "제목1","홍길동","남","대학교 학사","컴퓨터","2001-1-2","2019-9-2" ], //가져온 데이터
         *     "data" : {//가져오기 모델로 생성한 업무 데이터
         *  	   {
         *  	    "subject", "제목1", //subject는 가져오기 모델 열 설정의 경로
         *  	 	"name" : "홍길동",
         *  	    ...
         *     },
         *     "document": { //가져오기 대상이 콘텐츠 관리이면 document 객체 포함
         *          "title": "제목1"
         *          "identity": "xxx@xxx@I"
         *          ...
         *     },
         *     "work": { //가져오기 대상이 프로세스 관리이면 work 객체 포함
         *          "title": "제목1"
         *          "identity": "xxx@xxx@I"
         *          ...
         *     },
         *     "errorTextList" : [],  //오류 정보
         *     "errorTextListExcel": [] //오류 화면에서 Excel 내보내기 시 오류 정보
         * }
         */
        "moduleEvents": ["queryLoad","postLoad", "afterLoad", "loadImporter",
            "beforeImport", "validImport","afterImport", "beforeCreateRowData", "afterCreateRowData"]
    },
    _loadUserInterface: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        var button = this.node.getElement("button");
        if (!button) button = new Element("button");
        button.inject(this.node, "after");
        this.node.destroy();
        this.node = button;
        this.node.set({
            "id": this.json.id,
            "text": this.json.name || this.json.id,
            "styles": this.form.json.buttonStyle || this.form.css.buttonStyles,
            "MWFType": this.json.type
        });
        if( this.json.properties ){
            this.node.set(this.json.properties )
        }
        this.node.addEvent("click", function(){
            this.upload();
        }.bind(this));
        if( this.json.allowDownloadTempalte && this.json.downloadTempalteFieldId ){
            this.setDownloadEvent();
        }
    },
    getImporter: function(callback){
        var options;
        if( this.json.queryImportModel.id ){
            options = { "id" : this.json.queryImportModel.id };
        }else{
            options = {
                "application": this.json.queryImportModel.application || this.json.queryImportModel.appName,
                "name": this.json.queryImportModel.alias || this.json.queryImportModel.name
            }
        }
        MWF.xDesktop.requireApp("query.Query", "Importer", function () {
            /**
             * @summary 가져오기 모델 객체.
             * @member {MWF.xApplication.query.Query.Importer}
             * @example
             * var importer = this.form.get("fieldId").importer; //컴포넌트 조회
             * if(importer)importer.importFromExcel(); //가져오기 실행
             */
            this.importer = new MWF.xApplication.query.Query.Importer(this.form.app.content, options, {
                "onQueryLoad": function () {
                    this.fireEvent("loadImporter")
                }.bind(this),
                "onBeforeImport": function (importedData) {
                    this.fireEvent("beforeImport", [importedData])
                }.bind(this),
                "onValidImport": function (arg) {
                    this.fireEvent("validImport", [arg])
                }.bind(this),
                "onAfterImport": function ( infor ) {
                    this.fireEvent("afterImport", [infor])
                }.bind(this),
                "onBeforeCreateRowData": function (row) {
                    this.fireEvent("beforeCreateRowData", [row])
                }.bind(this),
                "onAfterCreateRowData": function (row) {
                    this.fireEvent("afterCreateRowData", [row])
                }.bind(this),
            }, this.form.app, this.form.Macro);
            if(callback)callback();
        }.bind(this));
    },
    upload: function () {
        if( this.importer ){
            this.importer.importFromExcel();
        }else{
            this.getImporter(function(){
                this.importer.load();
            }.bind(this))
        }

    },
    setDownloadEvent: function () {
        this.bindEvent = function () {
            var module = this._getModuleByPath(this.json.downloadTempalteFieldId);

            if(module)module.node.addEvent("click", function () {
                this.downloadTemplate();
            }.bind(this))

            this.fireEvent("afterLoad");

            //로드 완료 후 이벤트 삭제
            this.form.removeEvent("afterModulesLoad", this.bindEvent );
        }.bind(this);

        //양식의 모든 컴포넌트가 로드된 후에 외부 컴포넌트를 가져와야 함
        this.form.addEvent("afterModulesLoad", this.bindEvent );
    },
    downloadTemplate: function(){
        if( this.importer ){
            this.importer.downloadTemplate( this.getExcelName() );
        }else{
            this.getImporter(function(){
                this.importer.downloadTemplate( this.getExcelName() );
            }.bind(this))
        }
    },

    getExcelName: function(){
        var title;
        if( this.json.excelName && this.json.excelName.code ){
            title = this.form.Macro.exec(this.json.excelName.code, this);
        }
        return title || ""
    }

});
