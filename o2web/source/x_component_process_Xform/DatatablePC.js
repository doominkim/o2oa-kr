/**
 * 데이터 표 데이터 구조.
 * @typedef {Array} DatatableData
 * @example
 { //데이터 표 데이터 항목
  "data": [
    {
      "org": [{
        "distinguishedName": "홍길동@bf007525-99a3-4178-a474-32865bdddec8@I",
        "id": "bf007525-99a3-4178-a474-32865bdddec8",
        "name": "홍길동",
        "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
        "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
        "unitLevelName": "란더종횡/마케팅부",
        "unitName": "마케팅부"
      }],
      "org_1": [{
        "distinguishedName": "홍길동@bf007525-99a3-4178-a474-32865bdddec8@I",
        "id": "bf007525-99a3-4178-a474-32865bdddec8",
        "name": "홍길동",
        "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
        "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
        "unitLevelName": "란더종횡/마케팅부",
        "unitName": "마케팅부"
      }, {
        "distinguishedName": "김철수@bf007525-99a3-4178-a474-32865bdddec8@I",
        "id": "bf007525-99a3-4178-a474-32865bdddec8",
        "name": "김철수",
        "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
        "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
        "unitLevelName": "란더종횡/마케팅부",
        "unitName": "마케팅부"
      }],
      "number": "111",
      "textfield": "항저우",
      "attachment": [
        {
          "activityName": "기안",
          "extension": "jpg",
          "id": "9514758e-9e28-4bfe-87d7-824f2811f173",
 		  "businessId": "1234758e-9e28-4bfe-87d7-824f2811f173",
          "lastUpdateTime": "2020-12-09 21:48:03",
          "length": 452863.0,
          "name": "111.jpg",
          "person": "김철수@lisi@P"
        }
      ]
    }
    ...
  ],
  "total": {
    "number": 222, //합계는 필드 id 사용
    "textfield": 2
  }
}
 */
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class DatatablePC 데이터 표 컴포넌트. 표 형식의 다중 행 데이터 편집 컴포넌트.
 * @o2cn 데이터 표 PC
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var datatable = this.form.get("name"); //컴포넌트 가져오기
 * //방법 2
 * var datatable = this.target; //컴포넌트 이벤트 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @since v6.2
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.DatatablePC = MWF.APPDatatablePC = new Class(
	/** @lends MWF.xApplication.process.Xform.DatatablePC# */
	{
		Implements: [Events],
		Extends: MWF.APP$Module,
		isEdit: false,
		options: {
			/**
			 * 모든 내용 로드 후 실행(비동기 로드 포함).
			 * @event MWF.xApplication.process.Xform.DatatablePC#afterLoad
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목을 초기화했지만 아직 로드하지 않았을 때마다 발생. this.event로 항목 객체를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.DatatablePC#beforeLoadLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 각 항목이 로드된 후 발생. this.event로 항목 객체를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.DatatablePC#afterLoadLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 데이터 표가 변경될 때 발생. this.event.lines로 수정된 항목 배열을, this.event.type으로 수정 유형을 가져올 수 있음.<br/>
			 * <table>
			 *     <tr><th><b>this.event.type</b></th><th><b>발생 유형</b></th><th><b>this.event.lines</b></th></tr>
			 *     <tr><td>addline</td><td>행 추가</td><td>추가한 행 배열</td></tr>
			 *     <tr><td>deleteline</td><td>행 삭제</td><td>삭제한 행 배열</td></tr>
			 *     <tr><td>editcomplete</td><td>행 편집 완료(현재 편집 행 앞의 √를 클릭하면 실행. 여러 행 동시 편집 시 무시)</td><td>편집한 행 배열</td></tr>
			 *     <tr><td>editmodule</td><td>필드 값 변경 시(여러 행 동시 편집 시 이 이벤트 발생, 단일 행 편집 시 무시)</td><td>this.event.lines는 편집한 행 배열<br/>this.event.module은 수정한 필드</td></tr>
			 *     <tr><td>move</td><td>위쪽 화살표로 행 순서 조정</td><td>데이터 표의 모든 행</td></tr>
			 *     <tr><td>import</td><td>데이터 가져오기 후</td><td>데이터 표의 모든 행</td></tr>
			 * </table>
			 * @event MWF.xApplication.process.Xform.DatatablePC#change
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 추가 시 발생. this.event.line으로 해당 항목 객체를, this.event.ev로 이벤트를 발생시킨 Event를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.DatatablePC#addLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 편집 시 발생(여러 행 동시 편집 시에는 발생하지 않음). this.event로 해당 항목 객체를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.DatatablePC#editLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 편집 완료 시 발생(현재 편집 행 앞의 √를 클릭하면 실행. 여러 행 동시 편집 시에는 발생하지 않음). this.event로 해당 항목 객체를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.DatatablePC#completeLineEdit
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 편집 취소 시 발생(현재 편집 행 앞의 — 를 클릭하면 실행. 여러 행 동시 편집 시에는 발생하지 않음). this.event로 해당 항목 객체를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.DatatablePC#cancelLineEdit
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 삭제 전에 발생. this.event로 해당 항목 객체를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.DatatablePC#deleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 삭제 후 발생.
			 * @event MWF.xApplication.process.Xform.DatatablePC#afterDeleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 내보내기 전에 발생.
			 * @event MWF.xApplication.process.Xform.DatatablePC#beforeExport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * excel 내보내기 시 발생. this.event는 내보낼 데이터를 가리키며, this.event를 수정해 데이터를 변경할 수 있음.
			 * @event MWF.xApplication.process.Xform.DatatablePC#export
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 * @example
			 * <caption>this.event 데이터 형식은 다음과 같음:</caption>
			 * {
			 *  	data : [
			 *   		["이름","성별","학력","전공","생년월일","졸업일"], //제목
			 *  		[ "홍길동","남","대학교 학사","컴퓨터","2001-1-2","2019-9-2" ], //첫 번째 행 데이터
			 *  		[ "김철수","남","전문대학","수학","1998-1-2","2018-9-2" ]  //두 번째 행 데이터
			 * 	], //내보낼 데이터
			 *     colWidthArray : [100, 50, 100, 200, 150, 150], //열별 너비
			 *     title : "xxxx" //내보낼 excel 파일 제목
			 * }
			 */
			/**
			 * excel 가져오기에서 데이터 검증 후 발생. this.event는 가져온 데이터를 가리킴.
			 * @event MWF.xApplication.process.Xform.DatatablePC#validImport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 * @example
			 * <caption>this.event 데이터 형식은 다음과 같음:</caption>
			 * {
			 *  	data : [
			 *  	   {
			 *  	 	"이름" : "홍길동",
			 *  	 	"성별" : "남",
			 *  	 	"학력" : "대학교 학사",
			 *  	    "전공" : "컴퓨터",
			 *  	    "생년월일" : "aa01-1-2",
			 *  	 	"졸업일" : "2019-9-2",
			 *  	 	"errorTextList" : [
			 *  	 	    "5열: aa01-1-2는 올바른 날짜 형식이 아닙니다."
			 *  	 	] //검증에서 발견된 오류 정보, 해당 행 데이터가 올바르면 이 필드가 없음
			 *  	 }
			 *  	 ...
			 *     ], //가져온 데이터
			 *     "validted" : true  //검증 통과 여부, 이 이벤트에서 수정해 강제 가져오기 여부를 결정할 수 있음
			 * }
			 */
			/**
			 * 가져오기 전에 발생.
			 * @event MWF.xApplication.process.Xform.DatatablePC#beforeImport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * excel 가져오기에서 데이터 검증에 성공해 데이터 표에 다시 설정하려 할 때 발생. this.event는 정리된 가져오기 데이터를 가리키며, 형식은 {@link DatatableData} 참고.
			 * @event MWF.xApplication.process.Xform.DatatablePC#import
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * excel 가져오기에서 데이터를 데이터 표에 다시 설정한 후 발생. this.event는 정리된 가져오기 데이터를 가리키며, 형식은 {@link DatatableData} 참고.
			 * @event MWF.xApplication.process.Xform.DatatablePC#afterImport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			"moduleEvents": ["queryLoad","postLoad","load", "afterLoad",
				"beforeLoadLine", "afterLoadLine", "change", "addLine", "deleteLine", "afterDeleteLine", "editLine",
				"completeLineEdit", "cancelLineEdit", "beforeExport", "export", "beforeImport", "import", "validImport", "afterImport", "validation", "validationLine"]
		},

		initialize: function(node, json, form, options){
			this.node = $(node);
			this.node.store("module", this);
			this.json = json;
			this.form = form;
			this.field = true;
			this.fieldModuleLoaded = false;
		},
		load: function(){
			this._loadModuleEvents();
            if (this.fireEvent("queryLoad")){
				this._queryLoaded();
                // if( this.isSectionMergeEdit() ){ //구간 병합, 구간 값 병합 데이터 삭제 후 편집
                //     if( this.json.mergeTypeEdit === "script" ){
                //         this._loadMergeEditNodeByScript();
                //     }else{
                //         this._loadMergeEditNodeByDefault();
                //     }
                // }else{
				//     this._loadUserInterface();
			    // }
				debugger;
				if (!this.isReadable && !!this.isHideUnreadable){
					this.node?.addClass('hide');
					return;
				}

				this._loadUserInterface();
                this._loadStyles();
                this._loadDomEvents();
                //this._loadEvents();

                this._afterLoaded();
                this.fireEvent("afterLoad");
                // this.fireEvent("load");
			}
		},
		_loadMergeEditNodeByScript: function(){
			if (this.json.sectionMergeEditScript && this.json.sectionMergeEditScript.code) {
				var data = this.form.Macro.exec(this.json.sectionMergeEditScript.code, this);
				this._setBusinessData( data );
				//this._loadUserInterface();
			}
		},
		_loadMergeEditNodeByDefault: function(){
			var data = this.getSortedSectionData();
			var businessData = [];
			data.each(function(d){
				d.data = d.data || {};
				businessData = businessData.concat( d.data.data || [] );
			});
			this._setBusinessData({
				data: businessData
			});
			//this._loadUserInterface();
		},
		_loadUserInterface: function(){
			//구간 병합 후 편집
			if( this.isSectionMergeEdit() ){ //구간 병합, 구간 값 병합 데이터 삭제 후 편집
				if( this.json.mergeTypeEdit === "script" ){
					this._loadMergeEditNodeByScript();
				}else{
					this._loadMergeEditNodeByDefault();
				}
			}

			//구간 병합 표시
			this.isMergeRead = this.isSectionMergeRead();

			//구간 사용 및 모든 구간 표시
			this.sectionBy = this._getSectionBy();
			this.isShowAllSection = this.isAllSectionShow();

			// this.editModules = [];

			// if( !layout.mobile ){
			// 	this.node.setStyle("overflow-x", "auto");
			// 	this.node.setStyle("overflow-y", "hidden");
			// }
			this.table = this.node.getElement("table");
			this.tBody = this.table.getElement("tbody");

			this.editable = !(this.readonly || (this.json.isReadonly === true) || (this.form.json.isReadonly === true)) && this.isEditable;
			if( this.isMergeRead )this.editable = false;
			if (this.editable && this.json.editableScript && this.json.editableScript.code){
				this.editable = this.form.Macro.exec(((this.json.editableScript) ? this.json.editableScript.code : ""), this);
			}

			this.deleteable = this.json.deleteable !== "no";
			this.addable = this.json.addable !== "no";
			this.sortable = this.json.sortable !== "no";

			//가져오기 허용
			this.importenable  = this.editable && (this.json.impexpType === "impexp" || this.json.impexpType === "imp");
			//내보내기 허용
			this.exportenable  = this.isReadable && this.json.impexpType === "impexp" || this.json.impexpType === "exp";

			//여러 행 동시 편집 여부
			this.multiEditMode = this.json.editMode === "multi";

			//작업 열 표시 여부
			this.isOperationRowHidden = !this.deleteable && !this.addable && this.multiEditMode;

			//합계 열 존재 여부
			this.totalFlag = false;
			this.totalColumns = [];
			this.totalNumberModuleIds = [];

			// this.hiddenColIndexList = [];

			if( this.isShowAllSection ){
				this.data = this._getAllSectionData()
			}else if( this.isMergeRead ){
				this.data = this.getSectionMergeReadData()
			}else{
				this.data = this.getValue();
				if( !this._getBusinessData() ){
					this.isNew = true;
					this._setValue(this.data);
				}
			}


			this.lineList = [];
			this.sectionlineList = [];

			this.loadDatatable();
		},
		/**
		 * @summary 컴포넌트가 읽기 전용인지 판단.
		 * @example
		 * var readonly = this.form.get('datatable').isReadonly();
		 * @return {Boolean} 읽기 전용 여부.
		 */
		isReadonly : function(){
			return !this.editable;
		},
		/*
		 * @summary 데이터 표 다시 로드.
		 * @example
		 *  this.form.get("fieldId").reload(); //다시 로드
		 */
		reload: function(){
			this._loadReadEditAbeld();

			this.reloading = true;
			this._removeEl();

			if (!this.isReadable && !!this.isHideUnreadable){
				this.node?.addClass('hide');
				return;
			}

			// this.editModules = [];

			//합계 열 존재 여부
			this.totalFlag = false;
			this.totalColumns = [];
			this.totalNumberModuleIds = [];

			this.checkMerge( this.getValue() );

			this.clearSubModules();

			this.lineList = [];
			this.sectionlineList = [];

			this.loadDatatable();
			this.reloading = false;
		},
		loadDatatable: function(){
			this.loading = true;
			this._loadStyles();

			this._loadTitleTr();
			this._loadTemplate();
			this._loadTotalTr();

			this.fireEvent("load");
			this._loadDatatable(function(){
				this._loadImportExportAction();
				this.fieldModuleLoaded = true;
				this.loading = false;
				this.fireEvent("postLoad");
			}.bind(this));
		},
		_removeEl: function(){
			var node;
			if( this.titleTr ){
				node = this.titleTr.getElement("th.mwf_addlineaction");
				if( node )node.destroy();
				node = this.titleTr.getElement("th.mwf_moveaction");
				if( node )node.destroy();
			}
			if( this.templateTr ){
				node = this.templateTr.getElement("td.mwf_editaction");
				if( node )node.destroy();
				node = this.templateTr.getElement("td.mwf_moveaction");
				if( node )node.destroy();
			}
			if( this.totalTr ){
				this.totalTr.destroy();
				this.totalTr = null;
			}
			if( this.exportActionNode ){
				this.exportActionNode.destroy();
				this.totalTr = null;
			}
			if( this.importActionNode ){
				this.importActionNode.destroy();
				this.totalTr = null;
			}
		},
		_loadTitleTr: function(){
			this.titleTr = this.table.getElement("tr");

			var ths = this.titleTr.getElements("th");
			if (this.json.border){
				ths.setStyles({
					"border-bottom": this.json.border,
					"border-right": this.json.border
				});
			}
			if (this.json.titleStyles)ths.setStyles(this.json.titleStyles);

			//datatable$Title Module
			ths.each(function(th, index){
				var json = this.form._getDomjson(th);
				th.store("dataTable", this);
				th.addClass("mwf_origional");
				if (json){
					var module = this.form._loadModule(json, th);

					this.form.modules.push(module);
					if( json.isShow === false ){
						th.hide(); //숨김 열
					}else if( this.reloading && json.isShow === true){
						th.setStyle("display", "");
					}
					if((json.total === "number") || (json.total === "count"))this.totalFlag = true;
				}
			}.bind(this));


			if(this.editable){
				var actionTh = new Element("th.mwf_addlineaction", {"styles": {"width": "46px"}}).inject(this.titleTr, "top"); //작업 열
				if(this.isOperationRowHidden){
					actionTh.setStyle('display', 'none');
				}
				if(this.addable){
					var addLineAction = new Element("div.addLineAction.ooicon-create", {
						"styles": this.form.css.addLineAction,
						"events": {
							"click": function(e){
								if( this.json.addTo === "first" ){
									this._insertLineByIndex(e.target, 0);
								}else{
									this._addLine(e.target, true);
								}
							}.bind(this)
						}
					}).inject(actionTh);
				}
				var moveTh;
				if( this.sortable ){
					moveTh = new Element("th.mwf_moveaction", {"styles": this.form.css.gridMoveActionCell || {}}).inject(this.titleTr, "bottom"); //합계 열
				}
				if (this.json.border){
					Array.each([actionTh,moveTh], function(th){
						if(th)th.setStyles({
							"border-bottom": this.json.border,
							"border-right": this.json.border
						})
					}.bind(this));
				}
				if (this.json.titleStyles){
					Object.each(this.json.titleStyles, function( value, key ){
						if( key && key.toLowerCase() !== "width" ){
							actionTh.setStyle(key, value);
							if(moveTh)moveTh.setStyle(key, value);
						}
					});
				}
			}
		},
		_loadTemplate: function(){
			// this.templateJson = {};

			var trs = this.table.getElements("tr");

			var ths = this.table.getElements("th.mwf_origional");
			this.templateTr = trs[trs.length-1];

			this.templateNode = this.templateTr;

			var tds = this.templateNode.getElements("td");

			this.columnCount = tds.length;

			if (this.json.border) {
				tds.setStyles({
					"border-bottom": this.json.border,
					"border-right": this.json.border,
					"background": "transparent"
				});
			}
			if (this.json.contentStyles)tds.setStyles(this.json.contentStyles);

			//datatable$Data Module
			tds.each(function(td, index){
				var json = this.form._getDomjson(td);
				var thjson = this.form._getDomjson(ths[index]);
				td.store("dataTable", this);
				td.addClass("mwf_origional");
				if (json){
					// var module = this.form._loadModule(json, td);
					// this.form.modules.push(module);
					if( json.cellType === "sequence" )td.addClass("mwf_sequence"); //순번 열
					if( thjson?.isShow === false ){
						td.hide(); //숨김 열
					}else if( this.reloading && (!thjson.isShow || thjson.isShow === true)){
						td.setStyle("display", "");
					}
				}
			}.bind(this));

			if(this.editable){
				var eTd = new Element("td.mwf_editaction",{"styles": this.json.actionStyles || {}}).inject(this.templateNode, "top"); //작업 열
				if(this.isOperationRowHidden){
					eTd.setStyle('display', 'none');
				}
				this.columnCount = this.columnCount+1;
				var mTd;
				if( this.sortable ){
					mTd= new Element("td.mwf_moveaction", {"styles": this.form.css.gridMoveActionCell || {}}).inject(this.templateNode, "bottom");
					this.columnCount = this.columnCount+1;
				} //정렬 열
				if (this.json.border){
					Array.each([eTd,mTd], function(td){
						if(td)td.setStyles({
							"border-bottom": this.json.border,
							"border-right": this.json.border,
							"background": "transparent"
						})
					}.bind(this));
				}
				if (this.json.contentStyles){

					Object.each(this.json.contentStyles, function( value, key ){
						if( key && key.toLowerCase() !== "width" ){
							eTd.setStyle(key, value);
							if(mTd)mTd.setStyle(key, value);
						}
					});

					// eTd.setStyles(this.json.contentStyles);
					// if(mTd)mTd.setStyles(this.json.contentStyles);
				}
			}

			this.templateHtml = this.templateNode.get("html");
			// var moduleNodes = this.form._getModuleNodes(this.templateNode);
			// moduleNodes.each(function (node) {
			// 	if (node.get("MWFtype") !== "form") {
			// 		var json = this.form._getDomjson(node);
			// 		this.templateJson[json.id] = json ;
			// 	}
			// }.bind(this));
			this.templateNode.hide();
		},
		_loadTotalTr: function(){
			if( !this.totalFlag )return;
			this.totalTr = new Element("tr.mwf_totaltr", {"styles": this.form.css.datagridTotalTr}).inject(this.tBody||this.table);

			if( this.isShowSectionKey() && !(this.json.totalRowBySection || [] ).contains("module")){
				this.totalTr.hide()
			}
			if( this.isShowSectionBy() && !(this.json.totalRowBy || [] ).contains("module") ){
				this.totalTr.hide()
			}

			var ths = this.titleTr.getElements("th");
			//datatable$Title Module
			ths.each(function(th, index){
				var td = new Element("td", {"html": "<span>&nbsp;</span>", "styles": this.form.css.datagridTotalTd}).inject(this.totalTr);
				if (this.json.amountStyles) td.setStyles(this.json.amountStyles);

				if(!th.offsetParent){
					td.setStyle('display', 'none');
				}

				var json = this.form._getDomjson(th);
				if (json){
					if( json.isShow === false ){
						td.hide(); //숨김 열
					}else if( this.reloading && json.isShow === true){
						td.setStyle("display", "");
					}
					if ((json.total === "number") || (json.total === "count")){
						this.totalColumns.push({
							"th" : th,
							"td" : td,
							"index": index,
							"type": json.total
						})
					}
				}
			}.bind(this));

			var tds = this.templateTr.getElements("td");
			//datatable$Data Module
			tds.each(function(td, index){
				var json = this.form._getDomjson(td);
				if (json){
					//합계 열
					var tColumn = this.totalColumns.find(function(a){ return  a.index === index });
					if(tColumn){
						var moduleNodes = this.form._getModuleNodes(td); //합계 열 안의 입력 컴포넌트 가져오기
						if( moduleNodes.length > 0 ){
							tColumn.moduleJson = this.form._getDomjson(moduleNodes[0]);
							if(tColumn.type === "number")this.totalNumberModuleIds.push( tColumn.moduleJson.id );
						}
					}
				}
			}.bind(this));
		},
		_getTotalTr: function(){
			return this.totalTr;
		},
		_loadTotal: function( saveChanged ){
			var totalData = {};
			if (!this.totalFlag)return totalData;
			if (!this._getTotalTr())this._loadTotalTr();
			var data;
			if( this.isShowAllSection ){
				data = { data : [] };
				Object.each( this.getBusinessDataById(), function (d, key) {
					if( !["data","total"].contains(key) ){
						data.data = data.data.concat( d.data )
					}
				})
			}else if( this.isMergeRead ){
				data = this.data;
			}else{
				data = this.getValue();
			}
			this.totalColumns.each(function(column, index){
				var json = column.moduleJson;
				if(!json)return;

				var total = this._loadColumnTotal( column, data );
				if( typeOf(total) !== "null" )totalData[json.id] = total;
			}.bind(this));

			var isChanged = false;
			if( !!saveChanged ){
				var old = data.total || {};
				if( Object.keys(totalData).length !== Object.keys(old).length ){
					isChanged = true;
				}
				if( !isChanged ){
					for( var key in totalData ){
						if( totalData[key] !== old[key] ){
							isChanged = true;
							break;
						}
					}
				}
				if( isChanged ){
					var path = !!this.sectionBy ?
						`${this.json.id}..${this.sectionBy}..total` :
						`${this.json.id}..total`;
					this.saveDataById( path, totalData);
				}
			}
			data.total = totalData;
			return totalData;
		},
		_loadColumnTotal: function(column, data){
			var json = column.moduleJson;
			if(!json)return;

			var pointLength = 0; //소수점 이하 최대 자릿수
			var tmpV;
			if (column.type === "count"){
				tmpV = data.data.length;
			}else if(column.type === "number"){
				tmpV = new Decimal(0);
				for (var i=0; i<data.data.length; i++){
					var d = data.data[i];
					if(d[json.id]){
						var v = this.formatDecimals( json, d[json.id].toFloat() );
						tmpV = tmpV.plus(v ? v.toFloat() : 0);
						if( v.indexOf(".") > -1 ){
							pointLength = Math.max(pointLength, v.split(".")[1].length);
						}
					}
				}
			}

			if( isNaN( tmpV ) ) {
				column.td.set("text", "");
				return;
			}

			var s = tmpV.toString(), total;
			if(json.type==='OOCurrency'){
				var obj = this.formatCurrency(json, s);
				column.td.set("text", obj.text );
				total = obj.value.toString();
			}else{
				if( json.decimals && (json.decimals!=="*")){
					total = this.formatDecimals( json, s.toFloat());
				}else if( pointLength <= 0 || s === "0" ){
					total = s;
				}else if( s.indexOf(".") > -1 ){
					var length = s.split(".")[1].length;
					total = length < pointLength ? (s + "0".repeat(pointLength-length)) : s
				}else{
					total = s +"."+ "0".repeat(pointLength);
				}

				column.td.set("text", this.formatSeparate( json, total ) );
				if( json.currencySymbol && json.isCurrencySymbol !== false ){
					new Element("span", {"text": json.currencySymbol, "style":"padding-right:5px"}).inject( column.td, "top" );
				}
			}
			return total;
		},
		formatCurrency: function( json, total ){
			var opt = {};
			if( json.preset === 'currency' ){
				opt.currency = json.currency;
				opt.prefixuse = json.prefixuse;
			}else{
				opt.prefix = (json.isPrefix!==false && json.prefix ) || '';
				opt.suffix = json.suffix|| '';
				opt.thousands = (json.isThousands!==false && json.thousands ) || '';
				opt.decimal = json.decimal || '.';
			}
			opt.precision = json.hasOwnProperty('precision') ? json.precision : 2;
			['allowblank','disablenegative', 'round'].forEach(function(key){
				if( json.hasOwnProperty(key) ){
					opt[key] = json[key];
				}
			});
			['maximum', 'minimum'].forEach(function(key){
				if( json.hasOwnProperty(key) && json[key] !== '' ){
					opt[key] = json[key];
				}
			});
			var OOCurrency = window.customElements.get('oo-currency');
			var text = OOCurrency.formatCurrency(total, opt, opt.currency || '');
			var value = OOCurrency.unformatCurrency(text, opt, opt.currency || '');
			return {text: text, value: value};
		},
		formatDecimals: function( json, v ){
			var str;
			if (json.decimals && (json.decimals!=="*")) { //소수점 자릿수

				var decimals = json.decimals.toInt();

				var p = Math.pow(10, decimals);
				var f_x = Math.round(v * p) / p;
				str = f_x.toString();

				if (decimals > 0) {
					var pos_decimal = str.indexOf('.');
					if (pos_decimal < 0) {
						pos_decimal = str.length;
						str += '.';
					}
					var decimalStr = (str).substr(pos_decimal + 1, (str).length);
					while (decimalStr.length < decimals) {
						str += '0';
						decimalStr += 0;
					}
				}
			}
			return str || v.toString();
		},
		formatSeparate: function(json, str){
			if( typeOf( str ) === "number" )str = str.toString();
			if( json.digitsToSeparate && parseInt(json.digitsToSeparate) > 1 ){
				var digits = parseInt(json.digitsToSeparate);
				var reg = new RegExp( "(\\d{"+digits+"}\\B)" ,"g");
				var arr = str.split(".");
				var i = arr[0].split("").reverse().join("")
					.replace(reg, "$1,")
					.split("").reverse().join("");
				str = arr.length > 1 ? ( i + "." + arr[1] ) : i ;
			}
			return str;
		},
		isTotalNumberModule: function( id ){
			return this.totalNumberModuleIds.contains(id)
		},

		isShowSectionKey: function(){
			return this.json.showSectionKey && this.isMergeRead ;
		},
		isShowSectionBy: function(){
			return this.json.showSectionBy && this.isShowAllSection ;
		},
		isSectionData: function(){ //데이터 구간 처리 여부
			var data = this.getBusinessDataById();
			if( o2.typeOf( data ) === "object" ){
				var keys = Object.keys(data);
				for( var i=0; i<keys.length; i++ ){
					var key = keys[i];
					if( key !== "data" && key !== "total" ){
						if( o2.typeOf(data[key]) === "object" && o2.typeOf(data[key].data) === "array" ){
							return true;
						}
					}
				}
			}
			return false;
		},
		/**
		 * @summary 데이터 표가 구간 병합 표시, 구간 병합 편집으로 설정된 경우 이 메서드로 모든 구간 데이터를 가져올 수 있음.
		 * @return {Object} 객체.
		 * @example
		 * var data = this.form.get("fieldId").getAllSectionData();
		 * //data 형식은 다음과 같음:
		 * {
		 *  	"3455b82a-399c-4ee4-b9b9-e70ae40fbaf1": { //구간1의 key와 data
		 *			"data": [
		 *				{
		 *					"good": "yf",
		 *					"number_2": 11,
		 *					"prize": 1
		 *				}
		 *			]
		 *		},
		 *  	"83de86fc-60bc-4b4c-955c-1085915865a4": { //구간2의 key와 data
		 *  		"data": [
		 *  			{
		 *  				"good": "yf",
		 *  				"number_2": 11,
		 *  				"prize": 10
		 *  			}
		 *  		]
		 *  	}
		 *  }
		 */
		getAllSectionData: function(){
			return this.getBusinessDataById();
		},
		_getAllSectionData: function(){
			var bData = this.getBusinessDataById();
			var flag = false;
			if( !bData ){
				flag = true;
				bData = {};
			}
			if( !bData[this.sectionBy] ){
				flag = true;
				this.isNew = true;
				bData[this.sectionBy] = this.getValue();
			}

			if( flag )this.setBusinessDataById( bData );
			this.dataWithSectionBy = this.getAllSortedSectionData();
			return flag ? this.getBusinessDataById() : bData;
		},
		getAllSortedSectionData: function(){ //병합·정렬된 데이터 가져오기
			var data = this.getBusinessDataById();
			var array = [];
			for( var key in data ){
				if( !["data","total"].contains(key) )array.push({
					sectionKey: key,
					key: key,
					data: data[key]
				})
			}
			if( this.json.sectionDisplaySortScript && this.json.sectionDisplaySortScript.code){
				array.sort( function(a, b){
					this.form.Macro.environment.event = {
						"a": a,
						"b": b
					};
					var flag = this.form.Macro.exec(this.json.sectionDisplaySortScript.code, this);
					this.form.Macro.environment.event = null;
					return flag;
				}.bind(this))
			}
			return array;
		},
		getSortedSectionData: function(){ //병합·정렬된 데이터 가져오기
			var data = this.getBusinessDataById();
			var array = [];
			for( var key in data ){
				if( !["data","total"].contains(key) )array.push({
					sectionKey: key,
					key: key,
					data: data[key]
				});
			}
			if( this.json.sectionMergeSortScript && this.json.sectionMergeSortScript.code){
				array.sort( function(a, b){
					this.form.Macro.environment.event = {
						"a": a,
						"b": b
					};
					var flag = this.form.Macro.exec(this.json.sectionMergeSortScript.code, this);
					this.form.Macro.environment.event = null;
					return flag;
				}.bind(this))
			}
			return array;
		},
		getSectionMergeReadData: function(){
			switch (this.json.mergeTypeRead) {
				case "dataScript":
					if (this.json.sectionMergeReadDataScript && this.json.sectionMergeReadDataScript.code) {
						return this.form.Macro.exec(this.json.sectionMergeReadDataScript.code, this);
					}else{
						return {"data":[]};
					}
				default:
					var sortedData = this.getSortedSectionData();
					if( this.json.showSectionKey ){
						this.dataWithSectionKey = sortedData;
					}
					var data = [];
					//구간 값을 각 행의 데이터에 넣음
					sortedData.each(function(d){
						( d.data.data || [] ).each(function( obj ){
							if( o2.typeOf(obj) === "object" ){
								// obj.sectionKey = d.sectionKey;
								data.push( obj )
							}
						});
						// data = data.concat( d.data.data );
					});
					return { "data": data };
			}
		},

		_createLineNode: function( beforeNode ){
            var tr;
            if( beforeNode ) {
				tr = new Element("tr").inject(beforeNode, "after");
            }else if( this.totalTr ){
                tr = new Element("tr").inject(this.totalTr, "before");
            }else{
                tr = new Element("tr").inject(this.tBody || this.table);
            }
            return tr;
        },
		_checkAddAction: function(){
		},
		_loadStyles: function(){
			if (this.json.border) {
				this.table.setStyles({
					"border-top": this.json.border,
					"border-left": this.json.border
				});
			}
			if( this.json.recoveryStyles ){
				this.node.setStyles(this.json.recoveryStyles);
			}
			this.node.setStyles(this.json.styles);
			this.table.setStyles(this.json.tableStyles);
			this.table.set(this.json.properties);
		},
		getDefaultValue: function(){
			var value;
			if (this.json.defaultData && this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
			if (value && !value.then) if (o2.typeOf(value)==="array") value = {"data": value || [], "total":{}};
			if(!value && this.multiEditMode){
				value = {"data": [], "total":{}};
				var count = this.json.defaultCount ? this.json.defaultCount.toInt() : 0;
				for( var i=0; i<count; i++ ){
					value.data.push({})
				}
			}
			return value;
		},
		_getValue: function(){
			if (this.moduleValueAG) return this.moduleValueAG;
			var value = this._getBusinessData();
			if (!value){
				value = this.getDefaultValue();
			}
			return value || {"data": [], "total":{}};
		},
		getValue: function(){
			if (!this.isReadable) return {"data": [], "total":{}};
			return this._getValue();
		},

		_setValue: function(value){
			if (!!value && o2.typeOf(value.then)=="function"){
				var p = o2.promiseAll(value).then(function(v){
					this.__setValue(v);
				}.bind(this), function(){});
				this.moduleValueAG = p;
				p.then(function(){
					this.moduleValueAG = null;
				}.bind(this), function(){
					this.moduleValueAG = null;
				}.bind(this));
			}else{
				this.moduleValueAG = null;
				this.__setValue(value);
			}
		},
		__setValue: function(value){
			this._setBusinessData(value);
			this.moduleValueAG = null;
			return value;
		},

		_loadDatatable: function(callback, operation){
			//operation은 setLineData, addLine, insertLine, deleteLines, deleteLine, moveUpList일 수 있음
			var p = o2.promiseAll(this.data).then(function(v){
				this.data = v;
				if( this.isShowAllSection ){
					this._loadSectionLineList_EditSection(function(){
						this._checkAddAction();
						this._loadTotal();
						if(callback)callback();
					}.bind(this), operation)
				}else if( this.isShowSectionKey() ){
					this._loadSectionLineList(function(){
						this._checkAddAction();
						this._loadTotal();
						if(callback)callback();
					}.bind(this), operation)
				}else{
					this._loadLineList(function(){
						this._checkAddAction();
						this._loadTotal();
						if(callback)callback();
                    }.bind(this), operation);
				}

				this.moduleValueAG = null;
				return v;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
			this.moduleValueAG = p;
			if (this.moduleValueAG) this.moduleValueAG.then(function(){
				this.moduleValueAG = null;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
		},
		_loadSectionLineList_EditSection: function(callback, operation){
			var map = this.unchangedSectionLineMap || {};
			// Object.each(map, function (sline, idx) {
            //     sline.setIndex( idx.toInt(), (idx.toInt()+1).toString() );
            // });
			this.dataWithSectionBy.each(function(data, idx){
				var isEdited = false;
				if( this.isSectionLineEditable( data ) && this.editable ){
					isEdited = true;
				}
				var isNew = false;
				var sectionLine, beforeNode = idx > 0 ? this.sectionlineList[idx-1].getLastTr() : this.templateNode;
				if( map[data.sectionKey] ) {
					sectionLine =  map[data.sectionKey];
					sectionLine.setIndex( beforeNode, data, idx, isEdited, isNew, operation );
				}else{
					var node = this._createLineNode( beforeNode );
					sectionLine = this._loadSectionLine_EditSection(node, data, idx, isEdited, isNew);
				}
				if( this.sectionBy && this.sectionBy === data.sectionKey ){
					this.sectionLineEdited = sectionLine;
				}
				this.sectionlineList.push(sectionLine);
			}.bind(this))
			if (callback) callback();
		},
		_loadSectionLine_EditSection: function(container, data, index, isEdited, isNew){
			var sectionLine = new MWF.xApplication.process.Xform.DatatablePC.SectionLine(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isEditable: this.editable && this.isSectionLineEditable(data),
				isDeleteable: this.deleteable && this.isSectionLineEditable(data),
				isAddable: this.addable && this.isSectionLineEditable(data)
			});
			// this.fireEvent("beforeLoadLine", [line]);
			sectionLine.load();
			// this.fireEvent("afterLoadLine", [line]);
			return sectionLine;
		},
		isSectionLineEditable: function(data){
			return this.isShowAllSection && this.sectionBy && this.sectionBy === data.sectionKey;
		},

		_loadSectionLineList: function(callback, operation){
			var map = this.unchangedSectionLineMap || {};
			this.dataWithSectionKey.each(function(data, idx){
				var sectionLine;
				var isEdited = false;
				var isNew = false;
				var beforeNode = idx > 0 ? this.sectionlineList[idx-1].getLastTr() : this.templateNode;
				if( map[data.sectionKey] ) {
					sectionLine =  map[data.sectionKey];
					sectionLine.setIndex( beforeNode, data, idx, isEdited, isNew, operation );
				}else {
					var node = this._createLineNode( beforeNode );
					sectionLine = this._loadSectionLine(node, data, idx, isEdited, isNew);
				}
				this.sectionlineList.push(sectionLine);
			}.bind(this))
			if (callback) callback();
		},
		_loadSectionLine: function(container, data, index, isEdited, isNew){
			var sectionLine = new MWF.xApplication.process.Xform.DatatablePC.SectionLine(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isEditable: this.editable,
				isDeleteable: this.deleteable,
				isAddable: this.addable,
				isMergeRead: this.isMergeRead
			});
			// this.fireEvent("beforeLoadLine", [line]);
			sectionLine.load();
			// this.fireEvent("afterLoadLine", [line]);
			return sectionLine;
		},

		_loadLineList: function(callback, operation){
			var map = this.unchangedLineMap || {};
            Object.each(map, function (line, idx) {
                line.setIndex( idx.toInt() );
            });
            this.data.data.each(function(data, idx){
                if( !data )return;
                var idxStr = idx.toString();
                var beforeNode = idx > 0 ? this.lineList[idx - 1].node : this.templateNode;
                if( map[idxStr] ){
                	if( !operation || operation === "moveUpList" ){
						map[idxStr].node.inject( beforeNode, "after" );
					}
					this.lineList.push( map[idxStr] );
                }else{
                    var isNew = this.isNew || (o2.typeOf(this.newLineIndex) === "number" ? idx === this.newLineIndex : false);
                    var isEdited = (!this.multiEditMode && o2.typeOf(this.newLineIndex) === "number") ? idx === this.newLineIndex : this.multiEditMode;
                    var node = this._createLineNode( beforeNode );
                    var line = this._loadLine(node, data, idx, isEdited, isNew );
					if( !this.multiEditMode && isEdited ){
						this.currentEditedLine = line;
					}
                    this.lineList.push(line);
                }
            }.bind(this));
            this.isNew = false;
            this.newLineIndex = null;
            if (callback) callback();
        },
		_loadLine: function(container, data, index, isEdited, isNew){
			var line = new MWF.xApplication.process.Xform.DatatablePC.Line(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isEditable: this.editable,
				isDeleteable: this.deleteable,
				isAddable: this.addable,
				isMergeRead: this.isMergeRead
			});
			this.fireEvent("beforeLoadLine", [line]);
			line.load();
			this.fireEvent("afterLoadLine", [line]);
			return line;
		},
		isMax : function(){
			var maxCount = this.json.maxCount ? this.json.maxCount.toInt() : 0;
			if( this.editable && maxCount > 0 ) {
				if( this.isShowAllSection ){
					if( this.sectionLineEdited && this.sectionLineEdited.lineList.length >= maxCount )return true;
				}else{
					if( this.lineList.length >= maxCount )return true;
				}
			}
			return false;
		},
		isMin : function(){
			var minCount = this.json.minCount ? this.json.minCount.toInt() : 0;
			if( this.editable && minCount > 0 ) {
				if( this.isShowAllSection ){
					if( this.sectionLineEdited && this.sectionLineEdited.lineList.length <= minCount )return true;
				}else {
					if (this.lineList.length <= minCount) return true;
				}
			}
			return false;
		},
		_setLineData: function(line, d){
			if( line.sectionLine ){
				var data = this.getBusinessDataById();
				var sdata = data[ line.sectionLine.sectionKey ];
				if( sdata && sdata.data ){
					sdata.data[line.options.indexInSectionLine] = d;
					this.setAllSectionData( data, false, "setLineData" );
				}
			}else{
				var index = line.options.index;
				var data = this.getInputData();
				data.data[index] = d;
				this.setData( data, false, "setLineData" );
			}
		},
		_addLine: function(ev, edited, d){
			const checkResult = this._completeLineEdit(ev, true);

			if(checkResult instanceof Promise){
				return checkResult.then(flag=>{
					if(!!flag ){
						return this.__addLine(ev, edited, d);
					}
				});
			}

			if( !!checkResult ){
				return this.__addLine(ev, edited, d);
			}
		},
		__addLine: function(ev, edited, d){
			//if( !this._completeLineEdit(ev, true) )return;
			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}

			var data, index, line, changedData;
			if( this.isShowAllSection ){
				data = this.getBusinessDataById();
				var sdata = data[ this.sectionBy ];
				if( !sdata ){
					sdata = data[ this.sectionBy ] = { data: [] };
				}
				index = sdata.data.length;

				// if( d && !d.sectionKey )d.sectionKey = this.sectionBy;
				// sectionKey: this.sectionBy
				changedData = d||{};
				sdata.data.push(changedData);
				this.newLineIndex = index;

				this.setAllSectionData( data, false, "addLine");
				line = this.sectionLineEdited.lineList[index];
				line.isNewAdd = true;

				if( this.multiEditMode ){
					this.saveArrayData('addLine', index, null, changedData, this.sectionBy);
				}else{
					line.saveArrayArgs = ['addLine', index, null, changedData, this.sectionBy];
				}

			}else{
				index = this.lineList.length;
				data = this.getInputData();

				changedData = d||{};
				data.data.push(changedData);
				this.newLineIndex = index;

				this.setData( data , false, "addLine");
				line = this.getLine(index);
				line.isNewAdd = true;

				if( this.multiEditMode ) {
					this.saveArrayData('addLine', index, null, changedData);
				}else{
					line.saveArrayArgs = ['addLine', index, null, changedData];
				}
			}


			this.validationMode();
			this.fireEvent("addLine", [{"line":line, "ev":ev}]);

			this.fireEvent("change", [{"lines":[line], "type":"addline"}]);

			return line;
		},
		_insertLine: function(ev, beforeLine){
			const checkResult = this._completeLineEdit(ev, true);

			if(checkResult instanceof Promise){
				return checkResult.then(flag=>{
					if(!!flag ){
						return this.__insertLine(ev, beforeLine);
					}
				});
			}

			if( !!checkResult ){
				return this.__insertLine(ev, beforeLine);
			}
		},
		__insertLine: function(ev, beforeLine){
			debugger;
			//if( !this._completeLineEdit(ev, true) )return;
			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}

			//데이터 기반 사용
			var data, index, line;
			if( this.isShowAllSection ){
				index = beforeLine.options.indexInSectionLine + 1;

				data = this.getBusinessDataById();
				var sdata = data[ this.sectionBy ];
				if( !sdata ){
					sdata = data[ this.sectionBy ] = { data: [] };
				}
				sdata.data.splice(index, 0, {});
				this.newLineIndex = index;

				this.setAllSectionData( data, false, "insertLine");
				line = this.sectionLineEdited.lineList[index];
				line.isNewAdd = true;

				if( this.multiEditMode ) {
					this.saveArrayData('insertLine', index, null, {}, this.sectionBy);
				}else{
					line.saveArrayArgs = ['insertLine', index, null, {}, this.sectionBy];
				}
			}else {
				index = beforeLine.options.index + 1;

				data = this.getInputData();
				data.data.splice(index, 0, {});
				this.newLineIndex = index;
				this.setData(data, false, "insertLine");
				line = this.getLine(index);
				line.isNewAdd = true;

				if( this.multiEditMode ) {
					this.saveArrayData('insertLine', index, null, {});
				}else{
					line.saveArrayArgs = ['insertLine', index, null, {}];
				}
			}

			this.validationMode();
			this.fireEvent("addLine",[{"line":line, "ev":ev}]);
			this.fireEvent("change", [{"lines":[line], "type":"addline"}]);
			return line;
		},
		_insertLineByIndex: function(ev, index, d){
			const checkResult = this._completeLineEdit(ev, true);

			if(checkResult instanceof Promise){
				return checkResult.then(flag=>{
					if(!!flag ){
						return this.__insertLineByIndex(ev, index, d);
					}
				});
			}

			if( !!checkResult ){
				return this.__insertLineByIndex(ev, index, d);
			}
		},
		__insertLineByIndex: function(ev, index, d){
			//if( !this._completeLineEdit(ev, true) )return;
			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}

			var data, line, changedData, needSave=true;
			if( this.isShowAllSection ){
				data = this.getBusinessDataById();
				var sdata = data[ this.sectionBy ];
				if( !sdata ){
					sdata = data[ this.sectionBy ] = { data: [] };
				}
				if (sdata.data.length < index) return null;
				changedData = d || {};
				sdata.data.splice(index, 0, changedData);
				this.newLineIndex = index;

				this.setAllSectionData( data , false, "insertLine" );
				line = this.sectionLineEdited.lineList[index];
				line.isNewAdd = true;

				if(this.multiEditMode){
					this.saveArrayData('insertLine', index, null, changedData, this.sectionBy);
				}else{
					line.saveArrayArgs = ['insertLine', index, null, changedData, this.sectionBy];
				}
			}else {
				//데이터 기반 사용
				data = this.getInputData();
				if (data.data.length < index) return null;
				changedData = d || {};
				data.data.splice(index, 0, changedData);
				this.newLineIndex = index;
				this.setData(data, false, "insertLine");
				line = this.getLine(index);
				line.isNewAdd = true;
				if(this.multiEditMode) {
					this.saveArrayData('insertLine', index, null, changedData);
				}else{
					line.saveArrayArgs = ['insertLine', index, null, changedData];
				}
			}

			this.validationMode();
			this.fireEvent("addLine",[{"line":line, "ev":ev}]);
			this.fireEvent("change", [{"lines":[line], "type":"addline"}]);
			return line;
		},
		_deleteSelectedLine: function(ev){
			var selectedLine = this.lineList.filter(function (line) { return line.selected; });
			if( selectedLine.length === 0 ){
				this.form.notice( MWF.xApplication.process.Xform.LP.selectItemNotice,"info");
				return false;
			}
			var minCount = this.json.minCount ? this.json.minCount.toInt() : 0;
			if( minCount > 0 ){
				if( this.lineList.length - selectedLine.length < minCount ){
					var text = MWF.xApplication.process.Xform.LP.minItemNotice.replace("{n}", minCount );
					this.form.notice(text,"info");
					return false;
				}
			}
			var _self = this;
			this.form.confirm("warn", ev, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteSelectedItemNotice, 300, 120, function(){

				_self._delLines( selectedLine );

				this.close();

			}, function(){
				this.close();
			}, null, null, this.form.json.confirmStyle);

		},
		_delLines: function(lines){
			var _self = this;
			var saveFlag = false;

			var data;
			if( this.isShowAllSection ){
				data = this.getBusinessDataById();
			}else{
				data = _self.getInputData();
			}

			lines.reverse().each(function(line){
				_self.fireEvent("deleteLine", [line]);

				if(line.deleteAttachment())saveFlag = true;

				if( line.sectionLine ){
					var d = data[ line.sectionLine.sectionKey ];
					if( d && d.data ){
						var index = line.options.indexInSectionLine;
						d.data.splice(index, 1);
						if(this.currentEditedLine === line)this.currentEditedLine = null;
						_self.saveArrayData('delete', index, null, null, line.sectionLine.sectionKey);
					}
				}else {
					var index = line.options.index;
					data.data.splice(index, 1);
					if (this.currentEditedLine === line) this.currentEditedLine = null;
					_self.saveArrayData('delete', index);
				}

				_self.fireEvent("afterDeleteLine");
			});

			if( this.isShowAllSection ){
				_self.setAllSectionData(data, false, "deleteLines");
			}else{
				_self.setData( data , false, "deleteLines");
			}
			this.validationMode();

			_self.fireEvent("change", [{"lines":lines, "type":"deletelines"}]);

			if(saveFlag)this.saveFormData();
		},
		_deleteLine: function(ev, line){
			const checkResult = this._completeLineEdit(ev, true);

			if(checkResult instanceof Promise){
				checkResult.then(flag=>{
					if(!!flag ){
						this.__deleteLine(ev, line);
					}
				});
				return;
			}

			if( !!checkResult ){
				this.__deleteLine(ev, line);
			}
		},
		__deleteLine: function(ev, line){
			//if( !this._completeLineEdit(ev, true) )return;
			if( this.isMin() ){
				var text = MWF.xApplication.process.Xform.LP.minItemCountNotice.replace("{n}", this.json.minCount );
				this.form.notice(text,"info");
				return false;
			}
			var _self = this;
			this.form.confirm("warn", ev, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteDatagridLine, 300, 120, function(){
				_self._delLine(line);
				this.close();
			}, function(){
				this.close();
			}, null, null, this.form.json.confirmStyle);
		},
		_delLine: function(line, ignore){
			this.fireEvent("deleteLine", [line]);

			var saveFlag = line.deleteAttachment();
			//데이터 기반 사용
			var data;
			if( line.sectionLine ){
				var data = this.getBusinessDataById();
				var d = data[ line.sectionLine.sectionKey ];
				if( d && d.data ){
					var index = line.options.indexInSectionLine;
					d.data.splice(index, 1);
					if(!ignore)this.saveArrayData('delete', index, null, null, line.sectionLine.sectionKey);
				}
				if(this.currentEditedLine === line)this.currentEditedLine = null;
				this.setAllSectionData( data, false, "deleteLine" );
			}else{
				data = this.getInputData();
				var index = line.options.index;
				data.data.splice(index, 1);
				if(this.currentEditedLine === line)this.currentEditedLine = null;
				this.setData( data , false, "deleteLine");
				if(!ignore)this.saveArrayData('delete', index);
			}

			this.validationMode();
			if(!ignore)this.fireEvent("afterDeleteLine");

			this.fireEvent("change", [{"lines":[line], "type":"deleteline"}]);

			if(saveFlag)this.saveFormData();
		},
		_cancelLineEdit: function(){
			var line = this.currentEditedLine;
			if( !line )return true;
			line.validationMode();
			if( line.isNewAdd ){
				// var saveFlag = line.deleteAttachment();
				this._delLine( line, true );
				this.currentEditedLine = null;
				// if(saveFlag)this.form.saveFormData();
			}else{
				// line.data = Object.clone(line.getOriginalDataWithCheckAttachment());
				line.resetDataWithOriginalData();
				line.changeEditMode(false);
				this._loadTotal();
				if( line.sectionLine )line.sectionLine._loadTotal();
				if(line.attachmentChangeFlag){
					this.saveFormData();
					line.attachmentChangeFlag = false;
				}
				this.currentEditedLine = null;
				this.fireEvent("cancelLineEdit", [line]);
			}
			return true;
		},
		_completeLineEdit: function( ev, fireChange, ignoerSave ){
			var line = this.currentEditedLine;
			if( !line )return true;
			if( !line.validation() )return false;

			if( line.moduleValidationAG ){
				return line.moduleValidationAG.then((flag)=>{
					return !flag ? false : this.__completeLineEdit(ev, fireChange, ignoerSave);
				})
			}else{
				return this.__completeLineEdit(ev, fireChange, ignoerSave);
			}
		},
		__completeLineEdit: function( ev, fireChange, ignoerSave ){
			var line = this.currentEditedLine;
			if( !line )return true;
			// if( !line.validation() )return false;

			var originalData, originalDataStr, dataStr;
			originalData = line.originalData;
			if( fireChange ){
				if( line.originalData && o2.typeOf(line.originalData) === "object"){
					originalDataStr = JSON.stringify(line.originalData)
				}
				dataStr = JSON.stringify(line.data);
			}

			var saved = false;

			var isNewAdd = line.isNewAdd;

			line.isNewAdd = false;
			// line.data = line.getData();
			line.computeModuleData("save");
			line.originalData = Object.clone(line.data);
			line.changeEditMode(false);
			this._loadTotal( true);
			if( line.sectionLine )line.sectionLine._loadTotal( true );

			if(line.attachmentChangeFlag && !ignoerSave){
				this.saveFormData();
				saved = true;
				line.attachmentChangeFlag = false;
			}
			this.currentEditedLine = null;
			this.validationMode();
			this.fireEvent("completeLineEdit", [line]);
			if( fireChange && originalDataStr !== dataStr ){
				this.fireEvent("change", [{"lines":[line], "type":"editcomplete", "originalData": originalData}]);
			}

			this._checkAllRelated();

			if( !saved && !line.isSaved() ){
				if( isNewAdd && line.saveArrayArgs ){
					var args = line.saveArrayArgs;
					args[3] = line.data;
					this.saveArrayData(...args);
				}else{
					this.saveDataById(line.getId() ,line.data);
				}
			}

			return true;
		},
		_moveUpLine: function(ev, line){
			const checkResult = this._completeLineEdit(null, true);

			if(checkResult instanceof Promise){
				return checkResult.then(flag=>{
					if(!!flag ){
						this.__moveUpLine(ev, line);
					}
				});
			}

			if( !!checkResult ){
				this.__moveUpLine(ev, line);
			}
		},
		__moveUpLine: function(ev, line){
			//if( this.currentEditedLine && !this._completeLineEdit(null, true) )return false;

			var data, upData, curData;
			if( this.isShowAllSection ){
				if (line.options.indexInSectionLine === 0) return;

				data = this.getBusinessDataById();
				var sdata = data[ this.sectionBy ];
				if( !sdata )return;

				var index = line.options.indexInSectionLine;

				upData = sdata.data[ index - 1];
				curData = sdata.data[index];
				sdata.data[index] = upData;
				sdata.data[index - 1] = curData;

				this.setAllSectionData( data, false, "moveUpList" );
				this.saveArrayData('move', index, index - 1, null, this.sectionBy);
			}else {
				if (line.options.index === 0) return;

				data = this.getInputData();
				var index = line.options.index;
				upData = data.data[index - 1];
				curData = data.data[index];
				data.data[index] = upData;
				data.data[index - 1] = curData;
				this.setData(data, false, "moveUpList");
				this.saveArrayData('move', index, index - 1);
			}
			this.fireEvent("change", [{lines: this.lineList, "type":"move", line: line}]);
		},
		_changeEditedLine: function(line){
			if( this.currentEditedLine ){
				if( line === this.currentEditedLine )return;
				//if( !this._completeLineEdit( null,true ) )return;
			}
			const checkResult = this._completeLineEdit(null, true);
			if(checkResult instanceof Promise){
				checkResult.then(flag=>{
					if(!!flag ){
						this.currentEditedLine = line;
						line.changeEditMode(true);
					}
				});
				return
			}

			if( !!checkResult ){
				line.changeEditMode(true);
				/**
				 * 데이터 표에서 현재 편집 중인 항목. 데이터 표가 “여러 행 동시 편집”이면 이 속성이 없음.
				 * @member {MWF.xApplication.process.Xform.DatatablePC.Line | MWF.xApplication.process.Xform.DatatableMobile.Line | Null}
				 * @example
				 * //데이터 표 “dt1”에서 편집 중인 항목 가져오기.
				 * var line = this.form.get("dt1").currentEditedLine;
				 * //데이터 가져오기
				 * var data = line.getData();
				 * //데이터 설정
				 * line.setData({"subject":"111"});
				 * //subject 필드 값 가져오기
				 * var data = line.get("subject").getData();
				 * //subject 필드 값 설정
				 * line.get("subject").setData("test1");
				 */
				this.currentEditedLine = line;
			}
		},

		// editValidation: function(){
		// 	var flag = true;
		// 	this.editModules.each(function(field, key){
		// 		if (field.json.type!=="sequence" && field.validationMode ){
		// 			field.validationMode();
		// 			if (!field.validation()) flag = false;
		// 		}
		// 	}.bind(this));
		// 	return flag;
		// },


		_afterLoaded: function(){
		},
		// /**
		//  * @summary 데이터 표 값을 기본값으로 재설정하거나 비움.
		//  *  @example
		//  * this.form.get('fieldId').resetData();
		//  */
		resetData: function(){
			//var value = this.getDefaultValue() || {"data": [], "total":{}};
			var value = this.getValue();
			this.setData( value , false, 'resetData' );
		},
		/**매개변수가 Promise인 경우 문서 참고: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|Promise로 양식 비동기 처리}<br/>
		 * 양식에 해당 컴포넌트가 없으면 this.data[fieldId] = data로 값을 설정할 수 있음.
		 * @summary 데이터 표에 값 설정. 모든 구간 데이터를 설정하려면 setAllSectionData 메서드 사용.
		 * @param data{DatatableData|Promise} 필수, 배열 또는 Promise.
		 * @param fireChange{boolean} 선택, change 이벤트 발생 여부, 기본값 false.
		 * @example
		 *  this.form.get("fieldId").setData({data:[]}); //빈 값 설정
		 * @example
		 *  //양식에 컴포넌트가 있는지 확실하지 않으면 판단 필요
		 *  if( this.form.get('fieldId') ){ //양식에 해당 컴포넌트가 있는지 판단
		 *      this.form.get('fieldId').setData( data );
		 *  }else{
		 *      this.data['fieldId'] = data;
		 *  }
		 *@example
		 *  //Promise 사용
		 *  var field = this.form.get("fieldId");
		 *  var promise = new Promise(function(resolve, reject){ //비동기 요청 시작
		 *    var oReq = new XMLHttpRequest();
		 *    oReq.addEventListener("load", function(){ //load 이벤트 바인딩
		 *      resolve(oReq.responseText);
		 *    });
		 *    oReq.open("GET", "/data.json"); //데이터가 data.json에 있다고 가정
		 *    oReq.send();
		 *  });
		 *  promise.then( function(){
		 *    var data = field.getData(); //이 시점에는 비동기 요청이 이미 완료되어 getData 메서드로 data.json의 값을 얻음
		 * })
		 *  field.setData( promise );
		 */
		setData: function(data, fireChange, operation){
			if (!data){
				data = this._getValue();
			}else{
				//todo total 계산
			}
			this._setData(data, fireChange, operation);
		},
		_setData: function(data, fireChange, operation){
			var p = o2.promiseAll(this.data).then(function(v){
				this.data = v;
				// if (o2.typeOf(data)==="object") data = [data];
				this.__setData(data, fireChange, operation);
				this.moduleValueAG = null;
				return v;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
			this.moduleValueAG = p;
			if (this.moduleValueAG) this.moduleValueAG.then(function(){
				this.moduleValueAG = null;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
		},
		__setData: function(data, fireChange, operation){
            // if( typeOf( data ) === "object" && typeOf(data.data) === "array"  ){
			if( this.isShowAllSection ){
				//외부에서 현재 구간 편집에 대한 setData 호환, 내부 setData는 여기를 거치지 않고 바로 setAllSectionData로 감
				this._setEditedSectionData(data, fireChange, operation);
				return;
			}else if( this.isMergeRead ){
				//병합 및 읽기 전용, setData 불가
				throw new Error("The data table is merged and read-only, you can use the 'setAllSectionData' method to set all section data");
			}

            var old;
            if(fireChange)old = Object.clone(this._getBusinessData() || {});

			this._setUnchangedLineMap(data, operation);

            this._setBusinessData(data);

			this.data = data;

			if( !operation && !this.saving){
				this.saving = true;
				//this.saveFormData();
				if( this.sectionBy ){
					this.saveDataById(this.json.id + '..' + this.sectionBy, this._getBusinessData());
				}else{
					this.saveDataById();
				}
				this.saving = false;
			}

            if (this.data){
                this.clearSubModules();
            }

            this.lineList = [];
            this.sectionlineList = [];

			if( this.currentEditedLine )this.currentEditedLine = null;

            this._loadDatatable( function () {
				if (fireChange && JSON.stringify(old) !== JSON.stringify(data)) this.fireEvent("change");
				this.unchangedLineMap = null;
			}.bind(this), operation);
        },
		_setEditedSectionData: function(data, fireChange, operation){
			if( this.isShowAllSection ){
				var d = this.getBusinessDataById();
				d[ this.sectionBy ] = data || { data: [] };
				this.setAllSectionData( d, fireChange , operation);
			}
		},
		deleteAttachment: function( attId ){
			this.form.workAction.deleteAttachment(attId, this.form.businessData.work.id);
		},
		saveFormData: function(){
			var appName = this.form.app.options.name;
			if( ['process.Work', 'cms.Document'].includes(appName) ){
				this.form.saveFormData();
			}
		},
		saveArrayData: function(type, index, toIndex, data, sectionBy){
			//return;
			if(this.form.app.options.name !== 'process.Work' || this.form.isDraftWork() || !this.form.businessData.work.dataChanged){
				return;
			}
			if( this.isMergeRead ){ //병합 및 읽기 전용, 처리하지 않음
				return;
			}
			var method = ['insertLine','addLine'].contains(type) ? 'add' : type;

			debugger;

			var originalData = this.getOriginalDataById();
			if( !originalData ){
				if( method === 'add' ){
					//this.saveFormData();
					this.saveDataById();
				}
				return;
			}

			var oData = !!sectionBy ? (originalData[ sectionBy ] && originalData[ sectionBy ].data) : originalData.data;
			if( !oData ){
				if(method === 'add'){
					//this.saveFormData();
					this.saveDataById();
				}
				return;
			}

			o2.Actions.load('x_processplatform_assemble_surface').DataAction.updateArrayDataWithJob(
				this.form.businessData.work.job,
				{
					method: method,
					index: index,
					toIndex: toIndex,
					data: data,
					path: this.json.id.split('..').join('.') + ( sectionBy ? ('.'+ sectionBy) : '' ) +'.data'
				},
				()=>{
					switch (type){
						case 'addLine':
							oData.push(Object.clone(data));
							break;
						case 'insertLine':
							oData.splice(index, 0, Object.clone(data));
							break;
						case 'delete':
							oData.splice(index, 1);
							break;
						case 'move':
							var upData = oData[toIndex];
							var curData = oData[index];
							oData[index] = upData;
							oData[toIndex] = curData;
							break;
					}
				}, null, false
			);
		},
		/**
		 * @summary 데이터 표가 구간 병합 표시, 구간 병합 편집으로 설정된 경우 이 메서드로 모든 구간 데이터를 설정할 수 있음.
		 * @param data{Object} 필수, 객체.
		 * @param fireChange{boolean} 선택, change 이벤트 발생 여부, 기본값 false.
		 * @example
		 *  this.form.get("fieldId").setAllSectionData({}); //빈 값 설정
		 * @example
		 *  this.data['fieldId'].setAllSectionData({
		 *  	"3455b82a-399c-4ee4-b9b9-e70ae40fbaf1": { //구간1의 key와 data
		 *			"data": [
		 *				{
		 *					"good": "yf",
		 *					"number_2": 11,
		 *					"prize": 1
		 *				}
		 *			]
		 *		},
		 *  	"83de86fc-60bc-4b4c-955c-1085915865a4": { //구간2의 key와 data
		 *  		"data": [
		 *  			{
		 *  				"good": "yf",
		 *  				"number_2": 11,
		 *  				"prize": 10
		 *  			}
		 *  		]
		 *  	}
		 *  });
		 */
		setAllSectionData: function(data, fireChange, operation){
			// if( this.isSectionMergeEdit() ){
			// 	//병합 및 편집, setAllSectionData 불가
			// 	throw new Error("The data table is in merge editing state, you can use the 'setData' method to set the data");
			// }

			var old;
			if(fireChange)old = Object.clone(this.getBusinessDataById() || {});

			//삭제해도 소용없음(데이터를 비교해 제출하기 때문). 비우려면 data.data = []; data.total = {}로 설정
			// if( data && data.data )delete data.data;
			// if( data && data.total )delete data.total;

			this._setUnchangedSectionLineMap(data);

			this.setBusinessDataById(data);

			if( operation ){
				this.data = this.isShowAllSection ? this._getAllSectionData() : data;
			}else{
				this.checkMerge(data);
			}

			if (this.data){
				this.clearSubModules();
			}

			if (fireChange && JSON.stringify(old) !== JSON.stringify(data)) this.fireEvent("change");

			if( !operation ){
				//this.saveFormData();
				this.saveDataById();
			}

			this.lineList = [];
			this.sectionlineList = [];

			if( this.currentEditedLine )this.currentEditedLine = null;

			this._loadDatatable(function () {
				Object.each(this.unchangedSectionLineMap, function(sline, key){
					sline.isUnchangedAll = null;
					sline.unchangedLineMap = null;
				});
				this.unchangedSectionLineMap = null;
			}.bind(this), operation);
		},
		checkMerge: function(data){
			//구간 병합 후 편집
			var isMergeEidt = this.isSectionMergeEdit();
			if( isMergeEidt ){ //구간 병합, 구간 값 병합 데이터 삭제 후 편집
				if( this.json.mergeTypeEdit === "script" ){
					this._loadMergeEditNodeByScript();
				}else{
					this._loadMergeEditNodeByDefault();
				}
			}
			//구간 병합 표시
			this.isMergeRead = this.isSectionMergeRead();
			//구간 사용 및 모든 구간 표시
			this.sectionBy = this._getSectionBy();
			this.isShowAllSection = this.isAllSectionShow();

			if( this.isShowAllSection ){
				this.data = this._getAllSectionData();
			}else if( this.isMergeRead ) {
				this.data = this.getSectionMergeReadData();
			}else if( isMergeEidt ){
				this.data = this._getBusinessData()
			}else{
				this.data = data;
			}
		},
        _setUnchangedSectionLineMap: function( data, operation ){
			if( !data ){
				return;
			}
			var map = {};
			if( this.sectionlineList && this.sectionlineList.length ){
				this.sectionlineList.each(function (sline, i) {
					for( var key in data ){
						if( key === sline.sectionKey ){
							var m = sline._setUnchangedLineMap(data[key], operation);
							if( m )map[key] = m;
							break;
						}
					}
				}.bind(this));
			}
			this.unchangedSectionLineMap = map;
        },
		_setUnchangedLineMap: function(data, operation){
			if( !data ){
				this.unchangedLineMap = {};
				return;
			}
			var fromOutside = !operation;
			var lineDataList = this.lineList.map(function (line) {
				if( line.options.isEditable !== this.editable )return "";
				if( line.options.isDeleteable !== this.deleteable )return "";
				if( line.options.isAddable !== this.addable )return "";
				return fromOutside ?  JSON.stringify(line.data) : line.data;
			}.bind(this));

			var dStr, map = {};
			data.data.each(function (d, idx) {
				if(fromOutside)dStr = JSON.stringify(d);
				for (var i = 0; i < this.lineList.length; i++) {
					var isEqual= fromOutside ? (dStr === lineDataList[i]) : (d === lineDataList[i] );
					if ( isEqual ) {
						map[idx] = this.lineList[i];
						lineDataList[i] = "";
						break;
					}
				}
			}.bind(this));
			this.unchangedLineMap = map;
		},
        clearSubModules: function(){
            if( this.sectionlineList && this.sectionlineList.length ){
				this.sectionlineList.each(function (sline, i) {
					sline.clearSubModules();
				}.bind(this));
            }else{
				var map = this.unchangedLineMap || {};
				var lines = [];
				Object.values(map).each(function (d) {
					lines = lines.concat(d);
				});
				for (var i=0; i<this.lineList.length; i++){
					var l = this.lineList[i];
					if(!lines.contains(l))l.clearSubModules();
				}
            }
        },
		resetId: function(){
			if( this.sectionlineList && this.sectionlineList.length ){
				this.sectionlineList.each(function (sline, i) {
					sline.resetId();
				}.bind(this));
			}else{
				for (var i=0; i<this.lineList.length; i++){
					this.lineList[i].resetId();
				}
			}
		},
		/**
		 * @summary 데이터 표가 비어 있는지 판단.
		 * @example
		 * if( this.form.get('fieldId').isEmpty() ){
		 *     this.form.notice('최소 한 건의 데이터를 추가해야 합니다', 'warn');
		 * }
		 * @return {Boolean} 비어 있는지 여부
		 */
		isEmpty: function(){
			var data = this.getInputData();
			if( !data || !data.data )return true;
			if( o2.typeOf( data.data ) === "array" ){
				return data.data.length === 0;
			}
			//????
			// if( o2.typeOf( data ) === "object" ){
			// 	return Object.keys(data).length === 0;
			// }
			return false;
		},
		//api 관련 시작
		/**
		 * 해당 항목 가져오기.
		 * @param {Number} index 항목 순번, 0부터 시작
		 * @return {MWF.xApplication.process.Xform.DatatablePC.Line | Null} 해당 데이터 표 항목
		 * @example
		 * //데이터 표 “dt1”의 첫 번째 항목 가져오기.
		 * var line = this.form.get("dt1").getLine(0);
		 * //첫 번째 행 데이터 가져오기
		 * var data = line.getData();
		 * //첫 번째 행 데이터 설정
		 * line.setData({"subject":"111"});
		 * //첫 번째 항목의 subject 필드 값 가져오기
		 * var data = line.get("subject").getData();
		 * //subject 필드 값 설정
		 * line.get("subject").setData("test1");
		 */
		getLine: function(index){
			var line = this.lineList[index];
			return line || null;
		},
		/**
		 * 데이터 표 끝에 항목 추가.
		 * @param {Object} [data] 추가할 항목 데이터.
		 * @return {MWF.xApplication.process.Xform.DatatablePC.Line} 추가된 데이터 표 항목
		 * @example
		 * var line = this.form.get("dt1").addLine();
		 */
		addLine: function( data ){
			return this._addLine( null, null,  data );
		},
		/**
		 * 데이터 표의 지정 위치에 항목 추가.
		 * @param {Number} index 항목 순번, 0부터 시작. 인덱스가 현재 데이터 표 항목 수를 넘으면 삽입에 실패하고 null 반환.
		 * @param {Object} [data] 추가할 항목 데이터.
		 * @return {MWF.xApplication.process.Xform.DatatablePC.Line | Null} 삽입된 데이터 표 항목
		 * @example
		 * var line = this.form.get("dt1").insertLine(0);
		 */
		insertLine: function(index, data){
			return this._insertLineByIndex(null,  index, data);
		},
		/**
		 * 지정 위치의 항목 삭제.
		 * @param {Number} index 항목 순번, 0부터 시작. 인덱스가 현재 데이터 표 항목 수를 넘으면 삭제 실패.
		 * @example
		 * //첫 번째 항목 바로 삭제
		 * this.form.get("dt1").deleteLine(0);
		 */
		deleteLine: function(index, ev){
			var line = this.lineList[index];
			if( !line )return null;
			// if( ev ){
			// 	this._deleteLine(ev, line);
			// }else{
			this._delLine(line);
			// }
		},
		/**
		 * 해당 양식 컴포넌트 가져오기, get과 동일.
		 * @param {Number} index 항목 순번, 0부터 시작
		 * @param {String} id 컴포넌트 식별자
		 * @return {FormComponent} 해당 양식 컴포넌트
		 * @example
		 * //데이터 표 “dt1”의 첫 번째 항목의 subject 필드 가져오기.
		 * var module = this.form.get("dt1").getModule(0, "subject");
		 * //subject 필드 값 가져오기
		 * var data = module.getData();
		 * //subject 필드 값 설정
		 * module.setData("test1");
		 */
		getModule: function(index, id){
			var line = this.lineList[index];
			if( !line )return null;
			return line.getModule(id);
		},
		/**
		 * 해당 양식 컴포넌트 가져오기, getModule과 동일.
		 * @param {Number} index 항목 순번, 0부터 시작
		 * @param {String} id 컴포넌트 식별자
		 * @return {FormComponent} 해당 양식 컴포넌트
		 * @example
		 * //데이터 표 “dt1”의 첫 번째 항목의 subject 필드 가져오기.
		 * var module = this.form.get("dt1").get(0, "subject");
		 * //subject 필드 값 가져오기
		 * var data = module.getData();
		 * //subject 필드 값 설정
		 * module.setData("test1");
		 */
		get: function(index, id){
			return this.getModule(index, id);
		},
		//api 관련

		/**
		 * 스크립트에서 this.data[fieldId]로도 컴포넌트 값을 가져올 수 있음.
		 * 차이는 다음과 같음:<br/>
		 * 1. Promise를 사용하는 경우<br/>
		 * 비동기 함수 생성기(Promise)로 컴포넌트에 값을 할당할 때 getData 메서드로 즉시 가져오면 수정 전 값이 반환될 수 있으며, Promise 실행이 완료되면 수정 후 값이 반환됨.<br/>
		 * this.data[fieldId]로 즉시 가져오면 비동기 함수 생성기가 반환될 수 있으며, Promise 실행이 완료되면 수정 후 값이 반환됨.<br/>
		 * {@link https://www.yuque.com/o2oa/ixsnyt/ws07m0#EggIl|자세한 차이는 링크 참고}<br/>
		 * 2. 양식에 해당 컴포넌트가 없을 때는 this.data[fieldId]로 값을 가져올 수 있지만, this.form.get('fieldId')로는 컴포넌트를 가져올 수 없음.
		 * @summary 데이터 표 데이터 조회.
		 * @example
		 * var data = this.form.get('fieldId').getData();
		 *@example
		 *  //양식에 컴포넌트가 있는지 확실하지 않으면 판단 필요
		 *  var data;
		 *  if( this.form.get('fieldId') ){ //양식에 해당 컴포넌트가 있는지 판단
		 *      data = this.form.get('fieldId').getData();
		 *  }else{
		 *      data = this.data['fieldId']; //데이터에서 필드 값을 직접 가져오기
		 *  }
		 *  @example
		 *  //Promise 사용
		 *  var field = this.form.get("fieldId");
		 *  var promise = new Promise(function(resolve, reject){ //비동기 요청 시작
		 *    var oReq = new XMLHttpRequest();
		 *    oReq.addEventListener("load", function(){ //load 이벤트 바인딩
		 *      resolve(oReq.responseText);
		 *    });
		 *    oReq.open("GET", "/data.json"); //데이터가 data.json에 있다고 가정
		 *    oReq.send();
		 *  });
		 *  promise.then( function(){
		 *    var data = field.getData(); //이 시점에는 비동기 요청이 이미 완료되어 getData 메서드로 data.json의 값을 얻음
		 * })
		 *  field.setData( promise );
		 * @return {DatatableData}
		 */
		getData: function(){
			if( this.importer ){
				this.importer.destroySimulateModule();
			}
			var data;
			if (this.editable!==false){

				if( this.currentEditedLine ){
					this._completeLineEdit(null, true, !!this.form.saving);
				}

				// this.lineList.each(function(line, index){
				// 	if( !this.multiEditMode && line.options.isEdited ){
				// 		line.data = line.getData();
				// 	}else{
				// 		line.data = line.getData();
				// 	}
				// });
				if( this.multiEditMode){
					this.lineList.each(function (line) {
						line.computeModuleData("save");
					})
				}
				data = this._getBusinessData();
				return o2.typeOf(data) === 'object' ? Object.clone(data) : data;
			}else{
				data = this._getBusinessData();
				return o2.typeOf(data) === 'object' ? Object.clone(data) : data;
			}
		},
		getInputData: function(){
			if( this.importer ){
				this.importer.destroySimulateModule();
			}
			if (this.editable!==false){
				return this._getBusinessData();
			}else{
				return this._getBusinessData();
			}
		},
		//구간 기준
		_getSectionBy: function(){
			if (this.json.section!=="yes"){
				return "";
			}else {
				switch (this.json.sectionBy){
					case "person":
						return layout.desktop.session.user.id;
					case "unit":
						return (this.form.businessData.task) ? (this.form.businessData.task.unitDn || this.form.businessData.task.unit) : "";
					case "activity":
						return (this.form.businessData.work) ? this.form.businessData.work.activity : "";
					case "splitValue":
						return (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
					case "script":
						if( this.json.sectionByScript && this.json.sectionByScript.code){
							return this.form.Macro.exec(this.json.sectionByScript.code, this) || "";
						}else{
							return "";
						}
					default:
						return "";
				}
			}
		},
		createErrorNode: function(text){
			node = new Element("div", {styles:{
                "margin-top": "0.3em"
            }});
            var iconNode = new Element("div.ooicon-error", {
                "styles": {
                    "width": "20px",
                    "height": "1.2em",
                    "float": "left",
                    "display": "flex",
					"color": "red",
                    "align-items": "center",
                    "justify-content": "center"
                    // "background": "url("+"../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
                }
            }).inject(node);
            var textNode = new Element("div", {
                "styles": {
                    "height": "auto",
                    "line-height": "1.2em",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "keep-all"
                },
                "text": text
            }).inject(node);
			return node;
		},
		notValidationMode: function(text){
			if (!this.isNotValidationMode){
				this.isNotValidationMode = true;
				this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
				this.node.setStyle("border", "1px solid red");

				this.errNode = this.createErrorNode(text).inject(this.node, "after");
				this.showNotValidationMode(this.node);

				var parentNode = this.errNode;
				while( parentNode && parentNode.offsetParent === null ){
					parentNode = parentNode.getParent();
				}

				if (parentNode && !parentNode.isIntoView()) parentNode.scrollIntoView(false);
			}
		},
		showNotValidationMode: function(node){
			var p = node.getParent("div");
			if (p){
				if (p.get("MWFtype") == "tab$Content"){
					if (p.getParent("div").getStyle("display")=="none"){
						var contentAreaNode = p.getParent("div").getParent("div");
						var tabAreaNode = contentAreaNode.getPrevious("div");
						var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
						var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
						tabNode.click();
						p = tabAreaNode.getParent("div");
					}
				}
				this.showNotValidationMode(p);
			}
		},
		validationMode: function(){
			if (this.isNotValidationMode){
				this.isNotValidationMode = false;
				this.node.setStyles(this.node.retrieve("borderStyle"));
				if (this.errNode){
					this.errNode.destroy();
					this.errNode = null;
				}
			}
			this.lineList?.each(function(line){
				line.validationMode();
			})
		},

		validationConfigItem: function(routeName, data){
			var flag = (data.status=="all") ? true: (routeName == data.decision);
			if (flag){
				//???
				var n = this.getInputData();
				if( o2.typeOf(n)==="object"){
					var arr = [];
					Object.each( n, function (d, key) {
						if(o2.typeOf(d) === "array")arr = arr.concat(d);
					});
					n = arr;
				}
				var v = (data.valueType=="value") ? n : n.length;
				switch (data.operateor){
					case "isnull":
						if (!v){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "notnull":
						if (v){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "gt":
						if (v>data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "lt":
						if (v<data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "equal":
						if (v==data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "neq":
						if (v!=data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "contain":
						if (v.indexOf(data.value)!=-1){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "notcontain":
						if (v.indexOf(data.value)==-1){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
				}
			}
			return true;
		},
		validationConfig: function(routeName, opinion){
			if (this.json.validationConfig){
				if (this.json.validationConfig.length){
					for (var i=0; i<this.json.validationConfig.length; i++) {
						var data = this.json.validationConfig[i];
						if (!this.validationConfigItem(routeName, data)) return false;
					}
				}
				return true;
			}
			return true;
		},
		saveValidation: function () {
			return this.validationCurrentEditedLine();
		},
		validationCurrentEditedLine: function () {
			var line = this.currentEditedLine;
			if( !line )return true;
			if( !line.validation() )return false;
			return line.moduleValidationAG ?? true;
		},
		_validation: function(routeName, opinion){
			// if (this.isEdit){
			// 	if (!this.editValidation()){
			// 		return false;
			// 	}
			// }
			if (!this.isReadable || !this.isEditable) return true;

			if (!this.validationConfig(routeName, opinion))  return false;

			const currentLineFlag = this.validationCurrentEditedLine();
			if( !currentLineFlag )return false;

			const validationFlag = this.__validation(routeName);
			if( !validationFlag )return false;

			const promiseList = [];
			if (currentLineFlag instanceof Promise) promiseList.push(currentLineFlag);
			if (validationFlag instanceof Promise) promiseList.push(validationFlag);

			if( promiseList.length === 0){
				return true;
			}

			this.moduleValidationAG = Promise.all(promiseList).then(resultArr => {
				return resultArr.every(res => String(res) === "true");
			});

			return this.moduleValidationAG;
		},
		__validation: function(routeName){
			if (!this.json.validation) return true;
			if (!this.json.validation.code) return true;

			this.currentRouteName = routeName;
			var flag = this.form.Macro.exec(this.json.validation.code, this);
			this.currentRouteName = "";

			const isAsyncCheck = flag && typeof flag.then === "function";
			const isSyncPass = String(flag) === "true";

			// 동기 검증 실패
			if (!isAsyncCheck && !isSyncPass) {
				const errMsg = flag || MWF.xApplication.process.Xform.LP.lineNotValidation;
				this.notValidationMode(errMsg);
				return false;
			}

			// 비동기 검증 처리
			if (isAsyncCheck) {
				return flag.then(f => {
					const pass = String(f) === "true";
					if (!pass) {
						const errMsg = f || MWF.xApplication.process.Xform.LP.lineNotValidation;
						this.notValidationMode(errMsg);
					}
					return pass;
				});
			}

			// 동기 검증 통과
			return true;
		},
		validation: function(routeName, opinion){
			this.moduleValidationAG = null;
			if (this.isReadonly() || this.json.showMode==="disabled" || this.node?.isDisplayNone() || !this.isEditable) return true;

			const checkResult = this._validation(routeName, opinion);

			if (checkResult instanceof Promise) {
				return checkResult.then(pass => {
					this.fireEvent("validation", [pass]);
					return pass;
				});
			}

			this.fireEvent("validation", [checkResult]);
			return checkResult;
		},
		getAttachmentRandomSite: function(){
			var i = (new Date()).getTime();
			return this.json.id+i;
		},
		_loadImportExportAction: function(){
			this.impexpNode = this.node.getElement("div.impexpNode");
			if( this.impexpNode )this.impexpNode.destroy();
			this.impexpNode = null;

			if( !this.exportenable && !this.importenable )return;

			var position = ["leftTop","centerTop","rightTop"].contains( this.json.impexpPosition || "" ) ? "top" : "bottom";
			var container = new Element("div", {style: "overflow:hidden;"}).inject(this.node, position);

			this.importExportAreaNode = new Element("div").inject( container );
			if( ["leftTop","leftBottom"].contains( this.json.impexpPosition || "" ) ){
				this.importExportAreaNode.setStyles({ "float" : "left" })
			}else if( ["rightTop","rightBottom"].contains( this.json.impexpPosition || "" ) ){
				this.importExportAreaNode.setStyles({ "float" : "right" })
			}else{
				this.importExportAreaNode.setStyles({ "margin" : "0px auto" })
			}

            var styles;
			if( this.exportenable ){
				this.exportActionNode = new Element("div", {
					text : this.json.exportActionText || MWF.xApplication.process.Xform.LP.datagridExport
				}).inject(this.importExportAreaNode);
				if( this.json.exportActionStyles ){
					styles = this.json.exportActionStyles
				}else{
					styles = this.form.css.gridExportActionStyles;
				}
				this.exportActionNode.setStyles(styles);

				this.exportActionNode.addEvent("click", function () {
					this.exportToExcel();
				}.bind(this))

				if( this.form.json.formStyleType === 'v10' ){
					this.exportActionNode.addClass('form-content-button');
				}
			}

			if( this.importenable ){
				this.importActionNode = new Element("div", {
					text : this.json.importActionText || MWF.xApplication.process.Xform.LP.datagridImport
				}).inject(this.importExportAreaNode);
				if( this.json.importActionStyles ){
					styles = this.json.importActionStyles;
				}else{
					styles = this.form.css.gridImportActionStyles;
				}
				this.importActionNode.setStyles(styles);

				this.importActionNode.addEvent("click", function () {
					this.importFromExcel();
				}.bind(this))

				if( this.form.json.formStyleType === 'v10' ){
					this.importActionNode.addClass('form-content-button');
				}
			}

			if( ["centerTop","centerBottom"].contains( this.json.impexpPosition ) ){
				var width = 2;

				if( this.exportActionNode ){
					width = width + this.exportActionNode.getSize().x +
						this.exportActionNode.getStyle("padding-left").toFloat() +
						+ this.exportActionNode.getStyle("padding-right").toFloat() +
						+ this.exportActionNode.getStyle("margin-left").toFloat() +
						+ this.exportActionNode.getStyle("margin-right").toFloat()
				}

				if( this.importActionNode ){
					width = width + this.importActionNode.getSize().x +
						this.importActionNode.getStyle("padding-left").toFloat() +
						+ this.importActionNode.getStyle("padding-right").toFloat() +
						+ this.importActionNode.getStyle("margin-left").toFloat() +
						+ this.importActionNode.getStyle("margin-right").toFloat()
				}

				this.importExportAreaNode.setStyle( "width", width+"px" );
			}
		},
		exportToExcel: function(){
			this.exporter = new MWF.xApplication.process.Xform.DatatablePC.Exporter(this);
			this.exporter.exportToExcel();
		},
		importFromExcel: function(){
			this.importer = new MWF.xApplication.process.Xform.DatatablePC.Importer(this);
			this.importer.importFromExcel();
		}
	});

MWF.xApplication.process.Xform.DatatablePC$Title = MWF.APPDatatablePC$Title = new Class({
	Extends: MWF.APP$Module,
	_loadUserInterface: function(){
		if (!this.isReadable){
            this.json.isShow = false;
			return '';
        }

		if(this.json.recoveryStyles){
			this.node.setStyles(this.json.recoveryStyles);
		}
		if (this.json.prefixIcon || this.json.suffixIcon){
			var text = this.node.get("text");
			this.node.empty();

			var lineheight = this.node.getStyle("line-height") || "28px";
			if( lineheight === 'normal' )lineheight = '1em';
            this.wrapNode = new Element("div", {
                "styles": {
					"display": "flex",
					"align-items": "center",
					"justify-content": "center"
                }
            }).inject(this.node);

			if (this.json.prefixIcon){
				this.prefixNode = new Element("div", {"styles": {
					"width": "20px",
					"min-width": "20px",
					"height": lineheight,
					"background": "url("+this.json.prefixIcon+") center center no-repeat"
				}}).inject(this.wrapNode);
			}

			this.textNode = new Element("div", {"styles": {
					"line-height": lineheight,
					"vertical-align": "top",
					"padding": "1px"
				}, "text": text}).inject(this.wrapNode);

			if (this.json.suffixIcon){
				this.suffixNode = new Element("div", {"styles": {
					"width": "20px",
					"min-width": "20px",
					"height": lineheight,
					"background": "url("+this.json.suffixIcon+") center center no-repeat"
				}}).inject(this.wrapNode);
			}
		}
	},
	getOffsetY: function(){
		return (this.node.getStyle("padding-top") || 0).toInt()
			+ (this.node.getStyle("padding-bottom") || 0).toInt()
			+ (this.node.getStyle("border-top") || 0).toInt()
			+ (this.node.getStyle("border-bottom") || 0).toInt()
	}
});

MWF.xApplication.process.Xform.DatatablePC$Data = MWF.APPDatatablePC$Data =  new Class({
	Extends: MWF.APP$Module,
	_loadUserInterface: function(){
		if(this.json.recoveryStyles){
			this.node.setStyles(this.json.recoveryStyles);
		}
	}
});

MWF.xApplication.process.Xform.DatatablePC.SectionLine =  new Class({
	Implements: [Options, Events],
	options: {
		isNew: false,
		isEdited: true, //편집 중 여부
		isEditable: true, //편집 가능 여부
		isDeleteable: true, //삭제 가능 여부
		isAddable: true, //추가 가능 여부
		isMergeRead: false, //병합 열람
		index: 0,
		indexText: "0"
	},
	initialize: function (node, datatable, data, options) {
		this.setOptions(options);
		this.sectionKeyNode = node;
		this.datatable = datatable;
		this.data = data;
		this.form = this.datatable.form;
		this.lineList = [];
		this.totalColumns = [];
		this.totalNumberModuleIds = [];
		this.sectionKey = this.data.sectionKey;
	},
	load: function () {
		if( this.datatable.isShowSectionKey() || this.datatable.isShowSectionBy() ){
			this.loadSectionKeyNode();
		}else{
			this.sectionKeyNode.hide();
		}
		this._loadTotalTr();

		if( this.data.data &&  this.data.data.data ){
			( this.data.data.data || [] ).each(function(d, idx){
				if( !d )return;
				var node = this._createLineNode();
				var isEdited = false, isNew = false;
				var dt = this.datatable;
				if( this.options.isEdited ){
					isNew = dt.isNew || (o2.typeOf(dt.newLineIndex) === "number" ? idx === dt.newLineIndex : false);
					isEdited = (!dt.multiEditMode && o2.typeOf(dt.newLineIndex) === "number") ? idx === dt.newLineIndex : dt.multiEditMode;
					dt.isNew = false;
					dt.newLineIndex = null;
				}
				var line = this._loadLine( node, d, idx, isEdited, isNew );
				if( !dt.multiEditMode && isEdited ){
					dt.currentEditedLine = line;
				}
				this.lineList.push(line);
				this.datatable.lineList.push(line);
			}.bind(this));
			this._loadTotal();
		}
	},
	resetId: function(){
		this.lineList.each(function (line) {
			line.resetId();
		}.bind(this))
	},
	setIndex: function( preNode, data, index, isEdited, isNew, operation ){
		if( this.isUnchangedAll && index === this.options.index )return;

		this.data = data;

		this.options.index = index;
		this.options.indexText = (index+1).toString();

		this.sectionKeyNode.inject(preNode, "after");

		this.lineList = [];
		var map = this.unchangedLineMap || {};
		Object.each(map, function (line, idx) {
            line.setIndex( this.datatable.lineList.length + idx.toInt(), idx.toInt() );
        }.bind(this));
		if( this.data.data &&  this.data.data.data ){
			this.data.data.data.each(function (d, idx) {
				if( !d )return;
				var idxStr = idx.toString();
				var beforeNode = idx > 0 ? this.lineList[idx - 1].node : this.sectionKeyNode;
				if( map[idxStr] ){
					if( !operation || operation === "moveUpList" ){
						map[idxStr].node.inject( beforeNode, "after" );
					}
					this.lineList.push( map[idxStr] );
					this.datatable.lineList.push(map[idxStr]);
				}else{
					var node = this._createLineNode(  beforeNode );
					var isEdited = false, isNew = false;
					if( this.options.isEdited ){
						var dt = this.datatable;
						isNew = dt.isNew || (o2.typeOf(dt.newLineIndex) === "number" ? idx === dt.newLineIndex : false);
						isEdited = (!dt.multiEditMode && o2.typeOf(dt.newLineIndex) === "number") ? idx === dt.newLineIndex : dt.multiEditMode;
						dt.isNew = false;
						dt.newLineIndex = null;
					}
					var line = this._loadLine( node, d, idx, isEdited, isNew );
					this.lineList.push(line);
					this.datatable.lineList.push(line);
				}
			}.bind(this))
		}

		if( this.totalTr )this.totalTr.inject( this.lineList.getLast().node, "after" );

		this._loadTotal();

	},
	_loadLine: function(container, data, index, isEdited, isNew){
		var line = new MWF.xApplication.process.Xform.DatatablePC.Line(container, this.datatable, data, {
			indexInSectionLine : index,
			indexInSectionLineText : (index+1).toString(),
			index: this.datatable.lineList.length,
			indexText : (this.datatable.lineList.length + 1).toString(),
			isNew: isNew,
			isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.options.isEdited,
			isEditable: this.options.isEditable,
			isDeleteable: this.options.isDeleteable,
			isAddable: this.options.isAddable,
			isMergeRead: this.options.isMergeRead,
			sectionKey: this.sectionKey
		}, this);
		this.datatable.fireEvent("beforeLoadLine", [line]);
		line.load();
		this.datatable.fireEvent("afterLoadLine", [line]);
		return line;
	},
	_createLineNode: function( beforeNode ){
		var tr;
		if( beforeNode ){
			tr = new Element("tr").inject(beforeNode, "after");
		}else if( this.totalTr ){
			tr = new Element("tr").inject(this.totalTr, "before");
		}else{
			tr = this.datatable._createLineNode();
			// tr = new Element("tr").inject(this.tBody || this.table);
		}
		return tr;
	},
	loadSectionKeyNode: function () {
		var styleName = this.datatable.isShowSectionKey() ? "sectionKeyStyles" : "sectionByStyles";
		var sectionKeyStyles = this.datatable._parseStyles( this.datatable.json[styleName] || {} );
		var keyNode = new Element("td.mwf_sectionkey", {
			colspan: this.datatable.columnCount,
			styles : sectionKeyStyles
		}).inject( this.sectionKeyNode );
		this.keyNode = keyNode;
		var separator;
		if( this.datatable.isShowSectionKey() ){
			separator = this.datatable.json.keyContentSeparator;
		}else{
			separator = this.datatable.json.keyContentSeparatorSectionBy;
		}
		this.datatable.getSectionKeyWithMerge( this.data, function (key) {
			if( o2.typeOf(key) === "string" ){
				keyNode.set("text", key + (separator || ""));
			}else{
				Promise.resolve(key).then(function (k) {
					keyNode.set("text", k + (separator || ""));
				}.bind(this))
			}
		}.bind(this));
	},
	_setUnchangedLineMap: function(data, operation){
		var fromOutside = !operation;
		var dt = this.datatable;
		var editalbe;
		if( dt.isShowAllSection ){
			editalbe = dt.isShowAllSection && dt.sectionBy && dt.sectionBy === this.sectionKey;
		}
		var lineDataList = this.lineList.map(function (line) {
			if( dt.isShowAllSection ){
				if( line.options.isEditable !== (dt.editable && editalbe) )return "";
				if( line.options.isDeleteable !== (dt.deleteable && editalbe) )return "";
				if( line.options.isAddable !== (dt.addable && editalbe) )return "";
			}else{
				if( line.options.isEditable !== dt.editable )return "";
				if( line.options.isDeleteable !== dt.deleteable )return "";
				if( line.options.isAddable !== dt.addable )return "";
			}
			return fromOutside ?  JSON.stringify(line.data) : line.data;
		}.bind(this));

		var dStr, map = {};
		data.data.each(function (d, idx) {
			if(fromOutside)dStr = JSON.stringify(d);
			for (var i = 0; i < this.lineList.length; i++) {
				var isEqual = fromOutside ? (dStr === lineDataList[i]) : (d === lineDataList[i] );
				if ( isEqual ) {
					map[idx] = this.lineList[i];
					lineDataList[i] = "";
					break;
				}
			}
		}.bind(this));


		this.isUnchangedAll = data.data.length === this.lineList.length;
		if( this.isUnchangedAll ){
			for( var i=0; i<data.data.length; i++ ){
				var line = map[i.toString()];
				if( !line || line.options.indexInSectionLine !== i ){
					this.isUnchangedAll = false;
					break;
				}
			}
		}

		this.unchangedLineMap = map;

		return Object.keys(map).length ? this : null;
	},
	clearSubModules: function(){
		if( this.isUnchangedAll )return;

		var map = this.unchangedLineMap || {};
		var hasUnchangedLine = Object.keys(map).length > 0;

		if( !hasUnchangedLine ){
			if( this.sectionKeyNode ){
				this.sectionKeyNode.destroy();
				this.sectionKeyNode = null;
			}
		}

		var lines = [];
		Object.values(map).each(function (d) {
			lines = lines.concat(d);
		});
		for (var i=0; i<this.lineList.length; i++){
			var l = this.lineList[i];
			if(!lines.contains(l)){
				l.clearSubModules();
			}
		}
		// for (var i=0; i<this.lineList.length; i++){
		// 	this.lineList[i].clearSubModules();
		// }

		if( !hasUnchangedLine ) {
			if (this.totalTr) {
				this.totalTr.destroy();
				this.totalTr = null;
			}
		}
	},
	isTotalTrShow: function(){
		var dt = this.datatable;
		if( dt.isShowSectionKey() && ( dt.json.totalRowBySection || [] ).contains("section") )return true;
		if( dt.isShowSectionBy() && ( dt.json.totalRowBy || [] ).contains("section") )return true;
		return false;
	},
	_loadTotalTr: function(){
		if( !this.datatable.totalFlag )return false;
		this.totalTr = new Element("tr.mwf_totaltr", {"styles": this.form.css.datagridTotalTr_section}).inject(this.sectionKeyNode, "after");
		if( !this.isTotalTrShow() )this.totalTr.hide();
		var ths = this.datatable.titleTr.getElements("th");
		//datatable$Title Module
		ths.each(function(th, index){
			var td = new Element("td", {"text": "", "styles": this.form.css.datagridTotalTd}).inject(this.totalTr);
			if (this.datatable.json.sectionAmountStyles) td.setStyles(this.datatable.json.sectionAmountStyles);

			if(!th.offsetParent){
				td.setStyle('display', 'none');
			}

			var json = this.form._getDomjson(th);
			if (json){
				if( json.isShow === false ){
					td.hide(); //숨김 열
				}else if( this.reloading && json.isShow === true){
					td.setStyle("display", "");
				}
				if ((json.total === "number") || (json.total === "count")){
					this.totalColumns.push({
						"th" : th,
						"td" : td,
						"index": index,
						"type": json.total
					})
				}
			}
		}.bind(this));

		var tds = this.datatable.templateTr.getElements("td");
		//datatable$Data Module
		tds.each(function(td, index){
			var json = this.form._getDomjson(td);
			if (json){
				//합계 열
				var tColumn = this.totalColumns.find(function(a){ return  a.index === index });
				if(tColumn){
					var moduleNodes = this.form._getModuleNodes(td); //합계 열 안의 입력 컴포넌트 가져오기
					if( moduleNodes.length > 0 ){
						tColumn.moduleJson = this.form._getDomjson(moduleNodes[0]);
						if(tColumn.type === "number")this.totalNumberModuleIds.push( tColumn.moduleJson.id );
					}
				}
			}
		}.bind(this));
	},
	_getTotalTr: function(){
		return this.totalTr;
	},
	_loadTotal: function( saveChanged ){
		var totalData = {};
		if( !this.datatable.totalFlag )return totalData;
		if (!this._getTotalTr())this._loadTotalTr();
		var data;
		if( this.datatable.isShowAllSection ){
			Object.each( this.datatable.getBusinessDataById(), function (d, k) {
				if( this.sectionKey === k )data = d
			}.bind(this))
		}else{
			data = this.data.data;
		}
		this.totalColumns.each(function(column, index){
			var json = column.moduleJson;
			if(!json)return;

			var total = this.datatable._loadColumnTotal( column, data );
            if( typeOf(total) !== "null" )totalData[json.id] = total;
		}.bind(this));

		var isChanged = false;
		if( !!saveChanged && !this.options.isMergeRead ){
			var old = data.total || {};
			if( Object.keys(totalData).length !== Object.keys(old).length ){
				isChanged = true;
			}
			if( !isChanged ){
				for( var key in totalData ){
					if( totalData[key] !== old[key] ){
						isChanged = true;
						break;
					}
				}
			}
			if( isChanged ){
				this.datatable.saveDataById( this.getTotalPath(), totalData);
			}
		}
		data.total = totalData;
		return totalData;
	},
	getTotalPath: function(){
		var sectionKey = this.sectionKey || this.datatable.sectionBy;
		if( this.datatable.isShowAllSection ){
			return this.datatable.json.id + ".." + sectionKey + "..total";
		}else if( sectionKey ){
			return this.datatable.json.id + ".." + sectionKey + "..total";
		}else{
			return this.datatable.json.id + "..total";
		}
	},
	isTotalNumberModule: function( id ){
		return this.totalNumberModuleIds.contains(id)
	},
	getLastTr: function () {
		if( this.totalTr )return this.totalTr;
		if( this.lineList.length )return this.lineList.getLast().node;
		return this.sectionKeyNode;
	}
});

MWF.xApplication.process.Xform.DatatablePC.Line =  new Class({
	Implements: [Options, Events],
	options: {
		isNew: false,
		isEdited : true, //편집 중 여부
		isEditable : true, //편집 가능 여부
		isDeleteable: true, //삭제 가능 여부
		isAddable: true, //추가 가능 여부
		isMergeRead: false, //병합 열람
		index : 0,
		indexText : "0",
		indexInSectionLine: 0,
		indexInSectionLineText : "0",
		sectionKey: ""
	},
	initialize: function (node, datatable, data, options, sectionLine) {

		this.setOptions(options);

		this.node = node;
		this.datatable = datatable;
		this.data = data;
		this.form = this.datatable.form;
		this.sectionLine = sectionLine;

		// if( !this.datatable.multiEditMode && !this.options.isNew){
		// 	this.originalData = Object.clone(data);
		// }

		this.init();

	},
	init: function(){
		this.modules = [];
		this.all = {};
		this.all_templateId = {};

		this.fields = [];
		this.allField = {};
		this.allField_templateId = {};

		this.changedAttachmentMap = {};
	},
	load: function(){
		if( !this.datatable.multiEditMode && this.options.isEdited )this.datatable.currentEditedLine = this;

		this.loadModules();
		this.loadSequence();
		this.createActions();
		this.loadZebraStyle();
		this.loadEditedStyle();
		this.addNodeEvent();

		if( !this.datatable.multiEditMode )this.originalData = Object.clone(this.data);

		// if(this.options.isNew && this.options.isEdited){
		// 	this.data = this.getData();
		// 	if( !this.datatable.multiEditMode )this.originalData = Object.clone(this.data);
		// 	this.options.isNew = false;
		// }
	},
	resetId: function(){
		this.setIndex();
	},
	getId: function(){
		var sectionKey = this.options.sectionKey || this.datatable.sectionBy;
		if( this.datatable.isShowAllSection ){
			return this.datatable.json.id + ".." + sectionKey + "..data.." + this.options.indexInSectionLine;
		}else if( sectionKey ){
			return this.datatable.json.id + ".." + sectionKey + "..data.." + this.options.index;
		}else{
			return this.datatable.json.id + "..data.." + this.options.index;
		}
	},
	setIndex: function(index, indexInSectionLine){
		var hasIndexArg = typeOf(index) !== "null";
		var hasIndexInSectionLineArg = typeOf(indexInSectionLine) !== "null";

		if( hasIndexArg && hasIndexInSectionLineArg){
			if( this.options.index === index && this.options.indexInSectionLine === indexInSectionLine )return;
		}else if( hasIndexArg && !hasIndexInSectionLineArg){
			if( this.options.index === index )return;
		}else if(!hasIndexArg && hasIndexInSectionLineArg){
			if( this.options.indexInSectionLine === indexInSectionLine )return;
		}

		if( hasIndexArg ){
			this.options.index = index;
			this.options.indexText = (index.toInt()+1).toString();
		}

        if( hasIndexInSectionLineArg ){
			this.options.indexInSectionLine = indexInSectionLine;
			this.options.indexInSectionLineText = (indexInSectionLine.toInt()+1).toString();
		}

        //병합 상태 또는 분할 상태
        var sectionKey = this.options.sectionKey || this.datatable.sectionBy;
        this.modules.each(function (module) {
            var json = module.json;
            var id, oldId = json.id, templateJsonId = json.originialId;
            if( this.datatable.isShowAllSection ){
                id = this.datatable.json.id + ".." + sectionKey + "..data.." + this.options.indexInSectionLine + ".." + json.originialId;
            }else if( sectionKey ){
                id = this.datatable.json.id + ".." + sectionKey + "..data.." + this.options.index + ".." + json.originialId;
            }else{
                id = this.datatable.json.id + "..data.." + this.options.index + ".." + json.originialId;
            }
            json.id = id;
            switch (module.json.type) {
				case "Select":
					(module.areaNode || module.node).set("id", id);
					break;
				default:
					module.node.set("id", id);
					break;
			}

            if( json.type==="Attachment" || json.type==="AttachmentDg" ){
				json.originialSite = json.site || json.originialId;
                json.site = this.getAttachmentSite(json, templateJsonId, sectionKey);
            }

            delete this.all[oldId];
            this.all[id] = module;

            delete this.allField[oldId];
            this.allField[id] = module;

			if(this.form.all[oldId] && this.form.all[oldId] === module){
                delete this.form.all[oldId];
            }
            this.form.all[id] = module;

            if(this.form.forms[oldId] && this.form.forms[oldId] === module){
                delete this.form.forms[oldId];
            }
            if( module.field )this.form.forms[id] = module;

            if( hasIndexArg || hasIndexInSectionLineArg ){
				this.loadSequence();
				this.loadZebraStyle();
			}
        }.bind(this));
    },
	loadModules: function(){
		this.node.set("html", this.datatable.templateHtml);
		var moduleNodes = this.form._getModuleNodes(this.node, true);

		//병합 상태 또는 분할 상태
		var sectionKey = this.options.sectionKey || this.datatable.sectionBy;
		moduleNodes.each(function (node) {
			var mwfType = node.get("MWFtype");
			if (mwfType === "form")return;

			var _self = this;

			var tJson = this.form._getDomjson(node);
			if( tJson ){
				var json = Object.clone(tJson);

				if( !this.options.isEdited || !this.options.isEditable ){
					if( json.hasOwnProperty('showMode') ){
						json.showMode = 'read';
					}else{
						json.isReadonly = true;
					}

				}

				var templateJsonId = json.id;

				var index = this.options.index;

				var id;
				if( this.datatable.isShowAllSection ){
					id = this.datatable.json.id + ".." + sectionKey + "..data.." + this.options.indexInSectionLine + ".." + json.id;
				}else if( sectionKey ){
					id = this.datatable.json.id + ".." + sectionKey + "..data.." + index + ".." + json.id;
				}else{
				    id = this.datatable.json.id + "..data.." + index + ".." + json.id;
				}

                json.originialId = templateJsonId;
				json.id = id;
				node.set("id", id);

				if( json.type==="Attachment" || json.type==="AttachmentDg" ){
					json.type = "AttachmentDg";
					json.ignoreSite = true;
					json.originialSite = json.site || json.originialId;
					json.site = this.getAttachmentSite(json, templateJsonId, sectionKey);
				}

				if (this.form.all[id]) this.form.all[id] = null;
				if (this.form.forms[id])this.form.forms[id] = null;

				var hasData = this.data.hasOwnProperty(templateJsonId);

				json.inDatatable = true;
				var module = this.form._loadModule(json, node, function () {
					if( _self.options.isMergeRead ){
						this.field = false; //데이터 저장을 원하지 않음
						this._getBusinessData = function(){
							return _self.data[templateJsonId];
						};
						this._setBusinessData = function () {};
					}
					if( _self.widget )this.widget = _self.widget;
					this.parentLine = _self;
					this.parentDatatable = _self.datatable;

					//읽기 전용 메서드 값은 페이지 로드 시 또는 new 시점에 한 번 계산
					if( this.json.compute === "show" ){
						var needComputeShow = false;
						if( _self.datatable.loading ) {
							needComputeShow = true;
						}else if( _self.options.isNew && !_self.reloading ){
							needComputeShow = true;
						}
						if( !needComputeShow ){
							this.json.compute = "create"; //
							if( this.options.moduleEvents && this.options.moduleEvents.length ){ //compute 복원
								var eventName = ( this.options.moduleEvents || [] ).contains("afterLoad") ? "afterLoad" : "load";
								var resetCompute = function () {
									this.json.compute = "show";
									this.removeEvent( eventName, resetCompute );
								}.bind(this)
								this.addEvent(eventName, resetCompute);
							}
						}
					}

				});
				if(!module.parentLine)module.parentLine = this;
				if(!module.parentDatatable)module.parentDatatable = this.datatable;

				if((json.type==="Attachment" || json.type==="AttachmentDg")){
					module.addEvent("change", function(){
						if( this.datatable.multiEditMode ){
							_self.datatable.saveFormData();
						}else{
							_self.attachmentChangeFlag = true;
						}
					}.bind(this));
				}else if( this.datatable.multiEditMode && module.field && json.type!=="OOFiles" ){
					module.addEvent("change", function(){
						this.saveDataById();
					});
				}

				this.form.modules.push(module);

				this.modules.push(module);
				this.all[id] = module;
				this.all_templateId[templateJsonId] = module;

				if (module.field) {
					// if(hasData){
					// 	module.setData(this.data[templateJsonId]);
					// }else if(this.options.isEdited){
					// 	this.data[templateJsonId] = module.getData();
					// }
					if(this.options.isEdited ) {
						if (json.type !== "Attachment" && json.type !== "AttachmentDg"){
							if( module.json.compute === "save" && module.getInputData ){
								this.data[templateJsonId] = module.getInputData();
							}else{
								this.data[templateJsonId] = module.getData();
							}
						}
					}else if(!hasData && module.getValue ){
						this.data[templateJsonId] = module.getValue();
					}
					this.allField[id] = module;
					this.allField_templateId[templateJsonId] = module;
					this.fields.push( module );

					if( this.options.isEdited && this.datatable.multiEditMode ){
						module.addEvent("change", function(){
							this.datatable.fireEvent("change", [{lines: [this], type: "editmodule", module: module}]);
						}.bind(this))
					}
					//이 필드는 집계 숫자 필드
					if(this.datatable.multiEditMode && this.isTotalNumberModule(templateJsonId)){
						//module
						module.addEvent("change", function(){
							this.datatable._loadTotal( true );
							if( this.sectionLine )this.sectionLine._loadTotal( true );
						}.bind(this))
					}
				}
			}
		}.bind(this));
	},
	isTotalNumberModule: function(id){
		if( this.sectionLine ){
			return this.sectionLine.isTotalNumberModule(id)
		}else{
			return this.datatable.isTotalNumberModule(id)
		}
	},
	getIndex: function(){
		return this.options.index;
	},
	getModule: function(templateJsonId){
		var _subform = this.datatable.json._subform;
		if( _subform ){
			var module = this.all_templateId[_subform+"_"+templateJsonId];
			if( module )return module;
		}
		return this.all_templateId[templateJsonId];
	},
	get: function(templateJsonId){
		return this.getModule( templateJsonId );
	},
	getAttachmentSite: function(json, templateJsonId, sectionKey){
		//site 길이를 최대 64로 보장, 그렇지 않으면 서버에서 오류 발생

		var index = this.options.index;

		var baseSite;
		baseSite =  "." + index + "."  + (json.site || templateJsonId);

		var maxLength;
		var sectionId = "";
		if( sectionKey ){
			maxLength = Math.floor((63 - baseSite.length)/2 );

			sectionId = (sectionKey.length > maxLength) ? sectionKey.substr(sectionKey.length-maxLength, maxLength) : sectionKey;
			sectionId = "." + sectionId;
		}else{
			maxLength = 64 - baseSite.length;
		}

		var templateId = this.datatable.json.id;
		templateId = (templateId.length > maxLength) ? templateId.substr(templateId.length-maxLength, maxLength) : templateId;

		return templateId + sectionId + baseSite;
	},
	deleteAttachment: function(){
		var saveFlag = false;
		for( var key in this.allField){
			var module = this.allField[key];
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ){
				var array = module._getBusinessData();
				(array || []).each(function(d){
					saveFlag = true;
					this.datatable.deleteAttachment(d.id);

					for( var i=0; i<this.form.businessData.attachmentList.length; i++ ){
						var attData = this.form.businessData.attachmentList[i];
						if( (attData.businessId && attData.businessId === d.businessId) || attData.id === d.id ){
							this.form.businessData.attachmentList.erase(attData);
							break;
						}
					}

				}.bind(this))
			}
		}
		return saveFlag;
	},
	loadSequence: function(){
		var sequenceTd = this.node.getElements(".mwf_sequence");
		if(sequenceTd){
			if( this.datatable.isShowSectionKey() ) {
				if (this.datatable.json.sequenceBySection === "section") {
					sequenceTd.set("text", this.options.indexInSectionLineText);
				} else {
					sequenceTd.set("text", this.options.indexText)
				}
			}else if( this.datatable.isShowSectionBy() ){
				if( this.datatable.json.sequenceBy === "section" ){
					sequenceTd.set("text", this.options.indexInSectionLineText );
				}else {
					sequenceTd.set("text", this.options.indexText)
				}
			}else{
				sequenceTd.set("text", this.options.indexText)
			}
		}
	},
	loadZebraStyle: function(){
		if ((this.options.index%2)===0 && this.datatable.json.zebraColor){
			this.node.setStyle("background-color", this.datatable.json.zebraColor);
		}else if(this.datatable.json.backgroundColor){
			this.node.setStyle("background-color", this.datatable.json.backgroundColor);
		}
	},
	loadEditedStyle: function(){
		if (!this.datatable.multiEditMode && this.options.isEdited && this.datatable.json.editStyles){
			var tds = this.node.getElements("td");
			tds.setStyles(this.datatable.json.editStyles);
		}
	},
	addNodeEvent: function(){
		if(!this.datatable.multiEditMode && this.options.isEditable){
			this.editFun = function(){
				if( !this.options.isEdited ){
					this.datatable._changeEditedLine(this)
				}
			}.bind(this);
			this.node.addEvent("click", this.editFun)
		}
	},
	createActions: function () {
		//편집 불가, 바로 반환
		if(!this.options.isEditable)return;

		var editActionTd = this.node.getElement(".mwf_editaction");
		var moveActionTd = this.node.getElement(".mwf_moveaction");

		if(this.datatable.multiEditMode){ //다중 행 편집 모드
			if(this.options.isAddable)this.createAddAction(editActionTd);
			if(this.options.isDeleteable)this.createDelAction(editActionTd);
		}else{ //단일 행 편집 모드
			if(this.options.isAddable)this.createAddAction(editActionTd);
			if(this.options.isDeleteable)this.createDelAction(editActionTd);
			this.createCompleteAction(editActionTd);
			this.createCancelEditAction(editActionTd);
			this.checkActionDisplay();
		}
		if( moveActionTd )this.createMoveAction(moveActionTd);

	},
	checkActionDisplay: function(){
		if( this.options.isEdited ){
			if( this.addLineAction )this.addLineAction.hide();
			if( this.delLineAction )this.delLineAction.hide();
			if( this.completeLineAction )this.completeLineAction.show();
			if( this.cancelLineEditAction )this.cancelLineEditAction.show();
		}else{
			if( this.addLineAction )this.addLineAction.show();
			if( this.delLineAction )this.delLineAction.show();
			if( this.completeLineAction )this.completeLineAction.hide();
			if( this.cancelLineEditAction )this.cancelLineEditAction.hide();
		}
	},
	createAddAction: function(td){
		this.addLineAction = new Element("div.addLineAction.ooicon-create", {
			"styles": this.form.css.addLineAction,
			"events": {
				"click": function(ev){
					this.datatable._insertLine( ev, this );
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	createCompleteAction: function(td){
		this.completeLineAction = new Element("div.completeLineAction.ooicon-checkmark", {
			"styles": this.form.css.completeLineAction,
			"events": {
				"click": function(ev){
					this.datatable._completeLineEdit(ev, true);
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	createCancelEditAction: function(td){
		this.cancelLineEditAction = new Element("div.cancelLineEditAction.ooicon-process-cancel", {
			"styles": this.form.css.delLineAction,
			"events": {
				"click": function(ev){
					this.datatable._cancelLineEdit(ev, this);
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	createDelAction: function(td){
		this.delLineAction = new Element("div.delLineAction.ooicon-delete", {
			"styles": this.form.css.delLineAction,
			"events": {
				"click": function(ev){
					this.datatable._deleteLine( ev, this );
					// if( this.datatable.currentEditedLine === this )this.datatable.currentEditedLine = null;
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	createMoveAction: function(td){
		this.moveAction = new Element("div", {
			"styles": this.form.css.datatableMoveLineAction,
			"text": "↑",
			"events": {
				"click": function(ev){
					this.datatable._moveUpLine( ev, this );
					// if( this.datatable.currentEditedLine === this )this.datatable.currentEditedLine = null;
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	changeEditMode: function( isEdited ){
		if( isEdited === this.options.isEdited )return;
		if( !this.options.isEditable )return;
		this.options.isEdited = isEdited;
		this.reload();
		if( this.options.isEdited ){
			this.datatable.fireEvent("editLine",[this]);
		}
	},
	reload: function(){
		this.reloading = true;
		for(var key in this.all){
			var module = this.all[key];
			this.form.modules.erase(module);
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.empty();
		if(this.editFun){
			this.node.removeEvent("click", this.editFun);
			this.editFun = null;
		}
		this.init();
		this.load();
		this.reloading = false;
	},
	clearSubModules: function () { //module 제거
		for(var key in this.all){
			var module = this.all[key];
			//데이터 템플릿이나 데이터 표가 중첩된 경우 하위도 제거
			if(module.clearSubModules)module.clearSubModules();
			this.form.modules.erase(module);
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.destroy();
		this.init();
	},
	computeModuleData: function( when ){
		for( var key in this.allField_templateId){
			var module = this.allField_templateId[key];
			if( module.json.compute === when ){
				this.data[key] = module.getData();
			}
		}
	},
	getData: function () {
		var data = this.data;
		for( var key in this.allField_templateId){
			var module = this.allField_templateId[key];
			// var id = key.split("..").getLast();
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ){
				data[key] = module._getBusinessData();
			}else{
				data[key] = module.getData();
			}
		}
		return data;
	},
	setData: function (data) {
		this.datatable._setLineData(this, data);
	},
	_validation: function(){
		if( !this.options.isEdited || !this.options.isEditable )return true;

		const fieldsPass = this.validationFields();
		if (fieldsPass === false) return false;

		const linePass = this.validationCompleteLine();
		if (linePass === false) return false;

		const promises = [];
		if( fieldsPass instanceof Promise)promises.push(fieldsPass);
		if( linePass instanceof Promise)promises.push(linePass);
		if( promises.length === 0 )return true;

		this.moduleValidationAG = Promise.all(promises).then(resultArr => {
			return resultArr.every(res => String(res) === "true");
		});

		return this.moduleValidationAG;
	},
	validation: function(){
		// if (this.isReadonly() || this.json.showMode!=="disabled" || this.node?.isDisplayNone() || !this.isEditable) return true;
		this.moduleValidationAG = null;
		const checkRes = this._validation();

		if (checkRes instanceof Promise) {
			return checkRes.then(isPass => {
				this.datatable.fireEvent("validationLine", [this, isPass]);
				return isPass;
			});
		}

		this.datatable.fireEvent("validationLine", [this, checkRes]);
		return checkRes;
	},
	validationFields: function(){
		if( !this.options.isEdited || !this.options.isEditable )return true;

		let syncFailed = false;
		const asyncPromiseList = [];

		this.fields.forEach(field => {
			if (field.json.type === "sequence" || !field.validationMode) return;

			field.validationMode();
			// 동기 검증 실패 표시
			if (!field.validation()) {
				syncFailed = true;
			}
			// 필드 비동기 검증 Promise 수집
			if (field.moduleValidationAG) {
				asyncPromiseList.push(field.moduleValidationAG);
			}
		});

		// 어느 필드든 동기 검증에 실패하면 바로 차단
		if (syncFailed) return false;
		// 비동기 검증 없음, 동기 검증 모두 통과
		if (asyncPromiseList.length === 0) return true;

		// 필드 비동기 검증 병합
		return Promise.all(asyncPromiseList).then(arr => {
			const hasError = arr.some(item => String(item) !== "true");
			return !hasError;
		});
	},
	validationCompleteLine: function(){
		if( !this.options.isEdited || !this.options.isEditable )return true;
		var flag = true;
		if( !this.datatable.multiEditMode ){
			if (this.datatable.json.validationCompleteLine && this.datatable.json.validationCompleteLine.code){
				flag = this.form.Macro.exec(this.datatable.json.validationCompleteLine.code, this);
				if (!flag) flag = MWF.xApplication.process.Xform.LP.lineNotValidation;
			}
		}

		const isAsyncCheck = flag && typeof flag.then === "function";
		const isSyncPass = String(flag) === "true";

		// 동기 검증 실패
		if (!isAsyncCheck && !isSyncPass) {
			this.notValidationMode(flag, !layout.mobile);
			return false;
		}

		if(isAsyncCheck){
			return flag.then(f => {
				const pass = String(f) === "true";
				if (!pass) {
					this.notValidationMode(f, !layout.mobile);
				}
				return pass;
			});
		}

		return true;
	},
	createErrorNode: function(text, isTr){
		var node, tr, td;
		if( this.form.json.errorStyle ){
			if( this.form.json.errorStyle.type === "notice" ){
				if( !this.form.errorNoticing ){ //팝업이면
					this.form.errorNoticing = true;
					this.form.notice(text, "error", this.node, null, null, {
						onClose : function () {
							this.form.errorNoticing = false;
						}.bind(this)
					});
				}
			}else{
				if( isTr ){
					tr = new Element("tr");
					td = new Element("td", {"colspan": this.datatable.columnCount}).inject(tr);
				}
				node = new Element("div",{
					"styles" : this.form.json.errorStyle.node,
					"text": text
				});
				if( td )node.inject(td);
				if( this.form.json.errorStyle.close ){
					var closeNode = new Element("div",{
						"styles" : this.form.json.errorStyle.close ,
						"events": {
							"click" : function(){
								// (tr || node).destroy();
								this.validationMode()
							}.bind(this)
						}
					}).inject(node);
				}
			}
		}else{
			if( isTr ){
				tr = new Element("tr");
				td = new Element("td", {"colspan": this.datatable.columnCount}).inject(tr);
			}
			node = new Element("div", {styles:{
				"margin-top": "0.3em"
			}});
			if( td )node.inject(td);
			var iconNode = new Element("div", {
				"styles": {
					"width": "20px",
					"height": "1.2em",
					"float": "left",
					"display": "flex",
					"align-items": "center",
					"justify-content": "center"
					// "background": "url("+"../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
				}
			}).inject(node);
			var textNode = new Element("div", {
				"styles": {
					"height": "auto",
					"line-height": "1.2em",
					"margin-left": "20px",
					"color": "red",
					"word-break": "keep-all"
				},
				"text": text
			}).inject(node);
		}
		return tr || node;
	},
	notValidationMode: function(text, isTr){
		if (!this.isNotValidationMode){
			this.isNotValidationMode = true;
			this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
			this.node.setStyle("border-color", "red");

			this.errNode = this.createErrorNode(text, isTr);
			//if (this.iconNode){
			//    this.errNode.inject(this.iconNode, "after");
			//}else{
			this.errNode.inject(this.node, "after");
			//}
			this.showNotValidationMode(this.node);

			var parentNode = this.errNode;
			while( parentNode && parentNode && parentNode.offsetParent === null ){
				parentNode = parentNode.getParent();
			}

			if (!parentNode.isIntoView()) parentNode.scrollIntoView(false);
		}
	},
	showNotValidationMode: function(node){
		var p = node.getParent("div");
		if (p){
			var mwftype = p.get("MWFtype") || p.get("mwftype");
			if (mwftype == "tab$Content"){
				if (p.getParent("div").getStyle("display")=="none"){
					var contentAreaNode = p.getParent("div").getParent("div");
					var tabAreaNode = contentAreaNode.getPrevious("div");
					var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
					var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
					tabNode.click();
					p = tabAreaNode.getParent("div");
				}
			}
			this.showNotValidationMode(p);
		}
	},
	validationMode: function(){
		if (this.isNotValidationMode){
			this.isNotValidationMode = false;
			this.node.setStyles(this.node.retrieve("borderStyle"));
			if (this.errNode){
				this.errNode.destroy();
				this.errNode = null;
			}
		}
	},

	resetDataWithOriginalData: function () {
		var data = this.originalData;
		var attachmentList = this.form.businessData.attachmentList;
		for( var key in this.allField_templateId){
			var module = this.allField_templateId[key];
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ){
				data[key] = (data[key] || []).filter(function(att, index){
					for( var i=0; i<attachmentList.length; i++){
						if( (attachmentList[i].businessId && attachmentList[i].businessId === att.businessId) || attachmentList[i].id === att.id )return true;
					}
					return false;
				}.bind(this))
			}
			this.data[key] = data[key];
		}
		return data;
	},
	isSaved: function () {
		var origData = this.datatable.getOriginalDataById(null, this.getId());
		return this.isEqual(this.data, origData);
	},
	isEqual: function (obj1, obj2, cache = new WeakMap()) {
		if (obj1 === obj2) return true;

		if (obj1 === null || obj2 === null) return false;

		if (typeof obj1 !== 'object' || typeof obj2 !== 'object') return false;

		// 순환 참조 처리(무한 재귀 방지)
		if (cache.has(obj1)) return cache.get(obj1) === obj2;
		cache.set(obj1, obj2);

		if (Array.isArray(obj1) && Array.isArray(obj2)) {
			if (obj1.length !== obj2.length) return false;
			for (let i = 0; i < obj1.length; i++) {
				if (!this.isEqual(obj1[i], obj2[i], cache)) return false;
			}
			return true;
		}

		if (!Array.isArray(obj1) && !Array.isArray(obj2)) {
			// 모든 자체 속성 가져오기(프로토타입 체인 제외)
			const keys1 = Object.keys(obj1);
			const keys2 = Object.keys(obj2);

			if (keys1.length !== keys2.length) return false;

			for (const key of keys1) {
				if (!obj2.hasOwnProperty(key) || !this.isEqual(obj1[key], obj2[key], cache)) {
					return false;
				}
			}
			return true;
		}

		return false;
	}
});

MWF.xApplication.process.Xform.DatatablePC.ImporterDatabale = new Class({
	Extends: MWF.xApplication.process.Xform.DatatablePC,
	initialize: function(mainDatatable){
		this.mainDatatable = mainDatatable;
		this.json = Object.clone( mainDatatable.json );
		this.json.id = this.json.id + "_o2simula";

		this.node = new Element("div", {
			id: this.json.id
		});
		this.node.store("module", this);

		this.form = mainDatatable.form;
		this.field = false;
		this.fieldModuleLoaded = false;
	},
	load: function(){

		this.deleteFormData = function(data){
			delete data[this.id];
			this._self.form.removeEvent("getData", this._self.deleteFormData);
		}.bind({_self: this, id: this.json.id});
		this.form.addEvent("getData", this.deleteFormData);

		this._loadModuleEvents();
		if (this.fireEvent("queryLoad")){
			this._queryLoaded();
			this._loadUserInterface();
			this._afterLoaded();
			this.fireEvent("afterLoad");
			// this.fireEvent("load");
		}
	},
	_loadUserInterface: function(){
		// this.fireEvent("queryLoad");

		// this.editModules = [];

		this.table = new Element("table").inject(this.node);
		this.tBody = new Element("tbody").inject(this.table);

		this.editable = true;

		//여러 행 동시 편집 여부
		this.multiEditMode = true;

		this.data = {"data": [], "total":{}};
		this._setValue(this.data);

		this.lineList = [];

		this.loadDatatable();
	},
	loadDatatable: function(){
		this.loading = true;

		//this._loadTitleTr();
		//this._loadTemplate();
		//this._loadTotalTr();

		// this.templateNode = this.mainDatatable.templateNode;
		// this.templateTr = this.mainDatatable.templateTr;
		this.columnCount = this.mainDatatable.columnCount;
		this.templateHtml = this.mainDatatable.templateHtml;

		this.fireEvent("load");
		this._loadDatatable(function(){
			this.fieldModuleLoaded = true;
			this.loading = false;
			this.fireEvent("postLoad");
		}.bind(this));
	},
	_loadDatatable: function(callback){
		this._loadLineList(function(){
			//this._loadTotal();
			if(callback)callback();
		}.bind(this));
	},
	_loadLine: function(container, data, index, isEdited, isNew){
		var line = new MWF.xApplication.process.Xform.DatatablePC.ImporterLine(container, this, data, {
			index : index,
			indexText : (index+1).toString()
		});
		this.fireEvent("beforeLoadLine", [line]);
		line.load();
		this.fireEvent("afterLoadLine", [line]);
		return line;
	},
	_addLine: function(ev, edited, d){
		var data, index, line;

		index = this.lineList.length;
		data = this.data;

		data.data.push(d||{});
		this.newLineIndex = index;

		this.setData( data );
		line = this.getLine(index);
		line.isNewAdd = true;

		this.fireEvent("addLine", [{"line":line, "ev":ev}]);

		this.fireEvent("change", [{"lines":[line], "type":"addline"}]);

		return line;
	},
	// _delLine: function(line){
	// 	this.fireEvent("deleteLine", [line]);
	//
	// 	//데이터 기반 사용
	// 	var data = this.data;
	// 	data.data.splice(line.options.index, 1);
	//
	// 	if(this.currentEditedLine === line)this.currentEditedLine = null;
	// 	this.setData( data );
	//
	// 	// this.validationMode();
	// 	this.fireEvent("afterDeleteLine");
	//
	// 	this.fireEvent("change", [{"lines":[line], "type":"deleteline"}]);
	// },
	destroy: function(){
		this.clearSubModules();
		var id = this.json.id;
		if (this.form.all[id]) delete this.form.all[id];
		if (this.form.forms[id])delete this.form.forms[id];
		// if( this.form.businessData.data.hasOwnProperty(id) )delete this.form.businessData.data[id];
		// if( this.form.Macro.environment.data.hasOwnProperty(id) )delete this.form.Macro.environment.data[id];
		this.node.destroy();
	}
});

//Excel 가져오기 시 생성된 컴포넌트
MWF.xApplication.process.Xform.DatatablePC.ImporterLine =  new Class({
	Implements: [Options, Events],
	options: {
		isNew: true,
		isEdited : true, //편집 중 여부
		isEditable : true, //편집 가능 여부
		isDeleteable: true, //삭제 가능 여부
	},
	initialize: function (node, datatable, data, options) {

		this.setOptions(options);

		this.node = node;
		this.datatable = datatable;
		this.data = data;
		this.form = this.datatable.form;

		this.init()
	},
	init: function(){
		this.modules = [];
		this.all = {};
		this.all_templateId = {};

		this.fields = [];
		this.allField = {};
		this.allField_templateId = {};
	},
	load: function(){
		// if( !this.datatable.multiEditMode && this.options.isEdited )this.datatable.currentEditedLine = this;

		this.loadModules();

		// if( !this.datatable.multiEditMode )this.originalData = Object.clone(this.data);
	},
	loadModules: function(){
		this.node.set("html", this.datatable.templateHtml);
		var moduleNodes = this.form._getModuleNodes(this.node, true);

		//병합 상태 또는 분할 상태
		// var sectionKey = this.options.sectionKey || this.datatable.sectionBy;
		moduleNodes.each(function (node) {
			var mwfType = node.get("MWFtype");
			if (mwfType === "form")return;

			var _self = this;

			var tJson = this.form._getDomjson(node);
			if( tJson ){
				var json = Object.clone(tJson);

				if( !this.options.isEdited || !this.options.isEditable )json.isReadonly = true;

				var templateJsonId = json.id;

				var index = "0"; //this.options.index;

				var id;
				// if( this.datatable.isShowAllSection ){
				// 	id = this.datatable.json.id + ".." + sectionKey + "..data.." + this.options.indexInSectionLine + ".." + json.id;
				// }else if( sectionKey ){
				// 	id = this.datatable.json.id + ".." + sectionKey + "..data.." + index + ".." + json.id;
				// }else{
					id = this.datatable.json.id + "..data.." + index + ".." + json.id;
				// }

				json.id = id;
				node.set("id", id);

				// if( json.type==="Attachment" || json.type==="AttachmentDg" ){
				// 	json.type = "AttachmentDg";
				// 	json.ignoreSite = true;
				// 	json.site = this.getAttachmentSite(json, templateJsonId, sectionKey);
				// }

				if (this.form.all[id]) this.form.all[id] = null;
				if (this.form.forms[id])this.form.forms[id] = null;

				var hasData = this.data.hasOwnProperty(templateJsonId);

				var module = this.form._loadModule(json, node, function () {

					// this.field = false; //데이터 저장을 원하지 않음
					// this._getBusinessData = function(){
					// 	return _self.data[templateJsonId];
					// };
					// this._setBusinessData = function () {};

					if( _self.widget )this.widget = _self.widget;
					this.parentLine = _self;
					this.parentDatatable = _self.datatable;

					//읽기 전용 메서드 값은 페이지 로드 시 또는 new 시점에 한 번 계산
					if( this.json.compute === "show" ){
						var needComputeShow = false;
						if( _self.datatable.loading ) {
							needComputeShow = true;
						}else if( _self.options.isNew && !_self.reloading ){
							needComputeShow = true;
						}
						if( !needComputeShow ){
							this.json.compute = "create"; //
							if( this.options.moduleEvents && this.options.moduleEvents.length ){ //compute 복원
								var eventName = ( this.options.moduleEvents || [] ).contains("afterLoad") ? "afterLoad" : "load";
								var resetCompute = function () {
									this.json.compute = "show";
									this.removeEvent( eventName, resetCompute );
								}.bind(this)
								this.addEvent(eventName, resetCompute);
							}
						}
					}

				}, true);
				if(!module.parentLine)module.parentLine = this;
				if(!module.parentDatatable)module.parentDatatable = this.datatable;

				this.form.modules.push(module);

				this.modules.push(module);
				this.all[id] = module;
				this.all_templateId[templateJsonId] = module;

				if (module.field) {
					if(this.options.isEdited ) {
						if (json.type !== "Attachment" && json.type !== "AttachmentDg"){
							if( module.json.compute === "save" && module.getInputData ){
								this.data[templateJsonId] = module.getInputData();
							}else{
								this.data[templateJsonId] = module.getData();
							}
						}
					}else if(!hasData && module.getValue ){
						this.data[templateJsonId] = module.getValue();
					}
					this.allField[id] = module;
					this.allField_templateId[templateJsonId] = module;
					this.fields.push( module );
				}
			}
		}.bind(this));
	},
	clearSubModules: function () { //module 제거
		for(var key in this.all){
			var module = this.all[key];
			//데이터 템플릿이나 데이터 표가 중첩된 경우 하위도 제거
			if(module.clearSubModules)module.clearSubModules();
			this.form.modules.erase(module);
			// if( this.form.businessData.data.hasOwnProperty(key) )delete this.form.businessData.data[key];
			// if( this.form.Macro.environment.data.hasOwnProperty(key) )delete this.form.Macro.environment.data[key];
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.destroy();
		this.init();
	},
	getModule: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	get: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	// computeModuleData: function( when ){
	// 	for( var key in this.allField_templateId){
	// 		var module = this.allField_templateId[key];
	// 		if( module.json.compute === when ){
	// 			this.data[key] = module.getData();
	// 		}
	// 	}
	// },
	getData: function () {
		var data = this.data;
		for( var key in this.allField_templateId){
			var module = this.allField_templateId[key];
			// var id = key.split("..").getLast();
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ){
				data[key] = module._getBusinessData();
			}else{
				data[key] = module.getData();
			}
		}
		return data;
	},
	// validation: function(){
	// 	if( !this.validationFields())return false;
	// 	if( !this.validationCompleteLine())return false;
	// 	return true;
	// },
	// validationFields: function(){
	// 	var flag = true;
	// 	this.fields.each(function(field, key){
	// 		if (field.json.type!="sequence" && field.validationMode ){
	// 			field.validationMode();
	// 			if (!field.validation()) flag = false;
	// 		}
	// 	}.bind(this));
	// 	return flag;
	// },
	validationCompleteLine: function(){
		var flag = true;
		if( !this.datatable.multiEditMode ){
			if (this.datatable.json.validationCompleteLine && this.datatable.json.validationCompleteLine.code){
				flag = this.form.Macro.exec(this.datatable.json.validationCompleteLine.code, this);
				if (!flag) flag = MWF.xApplication.process.Xform.LP.lineNotValidation;
			}
		}
		return flag;
	}
});

MWF.xApplication.process.Xform.DatatablePC.Exporter = new Class({
	Implements: [Options, Events],
	options: {
	},
	initialize: function (datatable, options) {

		this.setOptions(options);

		this.datatable = datatable;
		this.form = this.datatable.form;

		this.columnJsonList = [];
	},
	exportToExcel : function () {
		MWF.require("MWF.widget.Mask", null, false);
		this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
		// 모바일 대응
		if (layout.mobile) {
			this.mask.load();
		} else {
			this.mask.loadNode(this.form.app.content);
		}

		this.datatable.fireEvent("beforeExport");

		this.getColumnList();

		var resultArr = [];
		var titleArr = this.getTitleArray();
		resultArr.push( titleArr );

		var lineList = [];

		if( this.datatable.isShowAllSection ){
			lineList = this.datatable.sectionLineEdited ? this.datatable.sectionLineEdited.lineList : this.datatable.lineList;
		}else if( this.datatable.isMergeRead ) {
			lineList = this.datatable.lineList;
		}else{
			lineList = this.datatable.lineList;
		}

		lineList.each(function (line, index) {
			var lineData = this.getLineExportData(line, index);
			var p = Promise.all(lineData).then(function (arr) {
				return arr;
			});
			resultArr.push( p );
		}.bind(this));

		Promise.all(resultArr).then(function ( rstArr ) {
			var colWidthArr = this.getColWidthArray();
			var excelName = this.getExcelName();

			var arg = {
				data : rstArr,
				colWidthArray : colWidthArr,
				title : excelName
			};
			this.datatable.fireEvent("export", [arg]);

			new MWF.xApplication.process.Xform.DatatablePC.ExcelUtils( this.datatable ).exportToExcel(
				arg.data || rstArr,
				arg.title || excelName,
				arg.colWidthArray || colWidthArr,
				this.getDateIndexArray(),  //날짜 형식 열 인덱스
				null,
				function () {
					if (this.mask) {
						this.mask.hide();
						this.mask = null;
					}
				}.bind(this)
			);
		}.bind(this))

	},
	getColumnList: function(){
		this.columnJsonList = [];

		var ths = this.datatable.titleTr.getElements("th.mwf_origional");
		var tds = this.datatable.templateTr.getElements("td.mwf_origional");

		ths.each(function(th, index){
			var thJson = this.form._getDomjson( th );

			var mJson;
			if(tds[index]){
				var mNodes = this.form._getModuleNodes(tds[index]); //합계 열 안의 입력 컴포넌트 가져오기
				if( mNodes.length > 0 )mJson = this.form._getDomjson(mNodes[0]);
			}

			if(thJson && mJson && this.isAvaliableColumn(thJson, mJson)){
				this.columnJsonList.push({
					"thJson": thJson,
					"title": th.get("text"),
					"mJson" : mJson,
					"dataType": thJson.impExpType,
					"available": true
				})
			}
		}.bind(this));
	},
	getLineExportData: function(line, index ){
		var exportData = [];
		this.columnJsonList.each(function (column) {

			var module;
			if( column.mJson && column.available ){
				module = line.all_templateId[column.mJson.id];
			}
			if ( !module ) {
				exportData.push("");
			}else{
				var text;
				if ( column.mJson.type === "Label" && module.node) {
					text = module.node.get("text");
				}else{
					text = module.getExcelData( column.dataType );
				}

				if( !text && o2.typeOf(text) !== "number" ){
					text = "";
				}

				exportData.push( text );
			}
		}.bind(this));
		return exportData;
	},
	// getLineExportData: function(line, index ){
	// 	var exportData = [];
	// 	this.columnJsonList.each(function (column) {
	//
	// 		var module;
	// 		if( column.mJson && column.available ){
	// 			module = line.all_templateId[column.mJson.id];
	// 		}
	// 		if ( !module ) {
	// 			exportData.push("");
	// 		}else{
	// 			var value = module.getData();
	// 			var text = "";
	//
	// 			if( value ){
	// 				switch (column.mJson.type) {
	// 					case "Org":
	// 					case "Reader":
	// 					case "Author":
	// 					case "Personfield":
	// 					case "Orgfield":
	// 						if (o2.typeOf(value) === "array") {
	// 							var textArray = [];
	// 							value.each(function (item) {
	// 								if (o2.typeOf(item) === "object") {
	// 									textArray.push(item.distinguishedName);
	// 								} else {
	// 									textArray.push(item);
	// 								}
	// 							}.bind(this));
	// 							text = textArray.join(", \n");
	// 						} else if (o2.typeOf(value) === "object") {
	// 							text = value.distinguishedName;
	// 						} else {
	// 							text = value;
	// 						}
	// 						break;
	// 					case "Combox":
	// 					case "Address":
	// 						text = o2.typeOf(value) === "array" ? value.join(", ") : value;
	// 						break;
	// 					case "Checkbox":
	// 						var options = module.getOptionsObj();
	// 						var value = o2.typeOf(value) === "array" ? value : [value];
	// 						var arr = [];
	// 						value.each( function( a, i ){
	// 							var idx = options.valueList.indexOf( a );
	// 							arr.push( idx > -1 ? options.textList[ idx ] : "") ;
	// 						});
	// 						text = arr.join(", ");
	// 						break;
	// 					case "Radio":
	// 					case "Select":
	// 						var options = module.getOptionsObj();
	// 						var idx = options.textList.indexOf( value );
	// 						text = idx > -1 ? options.valueList[ idx ] : "";
	// 						break;
	// 					case "Textarea":
	// 						text = value;
	// 						break;
	// 					case "Calendar":
	// 						text = value;
	// 						break;
	// 					case "Elautocomplete":
	// 						text = value;
	// 						break;
	// 					case "Elcascader":
	// 						if( typeOf(value) === "text" )
	// 						break;
	// 					default:
	// 						text = value;
	// 						break;
	// 				}
	// 			} else if ( column.mJson.type === "Label" && module.node) {
	// 				text = module.node.get("text");
	// 			}
	//
	// 			if( !text && o2.typeOf(text) !== "number" ){
	// 				text = "";
	// 			}
	//
	// 			exportData.push( text );
	// 		}
	// 	}.bind(this));
	// 	return exportData;
	// },
	isAvaliableColumn : function(thJson, mJson){
		if (thJson && ( thJson.isShow === false || thJson.isImpExp === false ))return false; //숨김 열, 가져오기/내보내기 불가
		if (mJson && (mJson.type == "sequence" || mJson.cellType == "sequence") )return false; //순번 열
		if (mJson && ["Image","Button","ImageClipper","WritingBoard","Attachment","AttachmentDg","Label",
			"Elbutton","Elcarousel","Eldropdown","Elicon","Eltree"].contains(mJson.type) )return false; //이미지, 첨부파일, Label 열은 가져오기/내보내기 안 함
		// if (type==="import" && module && ["Label"].contains(module.json.type))return false; //Label은 가져오기 안 함
		return true;
	},
	getColWidthArray : function(){
		var colWidthArr = [];
		this.columnJsonList.each(function(c, index){
			if ( c.available ) {
				if (c.mJson && ["Org","Reader","Author","Personfield","Orgfield"].contains(c.mJson.type)) {
					colWidthArr.push(340);
				} else if (c.mJson && c.mJson.type === "Address") {
					colWidthArr.push(170);
				} else if (c.mJson && c.mJson.type === "Textarea") {
					colWidthArr.push(260);
				} else if (c.mJson && c.mJson.type === "Htmleditor") {
					colWidthArr.push(500);
				} else if (c.mJson && c.mJson.type === "TinyMCEEditor") {
					colWidthArr.push(500);
				} else if (c.mJson && c.mJson.type === "Calendar") {
					colWidthArr.push(150);
				} else {
					colWidthArr.push(150);
				}
			}
		}.bind(this));
		return colWidthArr;
	},
	getDateIndexArray : function(){
		var dateIndexArr = []; //날짜 형식 열 인덱스
		var idx=0;
		this.columnJsonList.each(function(c){
			if ( c.available && c.mJson ) {
				if(c.mJson.type === "Calendar")dateIndexArr.push(idx);
				idx++;
			}
		}.bind(this));
		return dateIndexArr;
	},
	getTitleArray : function(){
		var arr = [];
		this.columnJsonList.each(function(c, index){
			if ( c.available && c.mJson )arr.push(c.title);
		}.bind(this));
		return arr;
	},
	getExcelName: function(){
		var title;
		if( this.datatable.json.excelName && this.datatable.json.excelName.code ){
			title = this.form.Macro.exec(this.datatable.json.excelName.code, this);
		}else{
			title = MWF.xApplication.process.Xform.LP.datatableExportDefaultName;
		}
		var titleA = title.split(".");
		if( ["xls","xlst"].contains( titleA[titleA.length-1].toLowerCase() ) ){
			titleA.splice( titleA.length-1 );
		}
		title = titleA.join(".");
		return title;
	},

	exportWithImportDataToExcel : function ( importedData ) {
		MWF.require("MWF.widget.Mask", null, false);
		this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
		// 모바일 대응
		if (layout.mobile) {
			this.mask.load();
		} else {
			this.mask.loadNode(this.form.app.content);
		}


		this.datatable.fireEvent("beforeExport");

		this.getColumnList();

		var resultArr = [];

		var titleArr = this.getTitleArray("import");
		titleArr.push( MWF.xApplication.process.Xform.LP.validationInfor );
		resultArr.push( titleArr );

		importedData.each( function( lineData, lineIndex ){
			var array = [];
			this.columnJsonList.each( function (obj, i) {
				array.push( ( lineData[ obj.title ] || '' ).replace(/&#10;/g, "\n") );
			});
			array.push( lineData.errorTextListExcel ? lineData.errorTextListExcel.join("\n") : ""  );

			resultArr.push( array );
		}.bind(this));

		var colWidthArr = this.getColWidthArray();
		colWidthArr.push( 300 ); //안내 메시지

		var excelName = this.getExcelName();

		var arg = {
		    data : resultArr,
		    colWidthArray : colWidthArr,
		    title : excelName,
		    withError : true
		};
		this.datatable.fireEvent("export", [arg]);

		new MWF.xApplication.process.Xform.DatatablePC.ExcelUtils( this.datatable ).exportToExcel(
		    arg.data || resultArr,
		    arg.title || excelName,
		    arg.colWidthArray || colWidthArr,
		    this.getDateIndexArray(), //날짜 형식 열 인덱스
			null,
			function () {
				if (this.mask) {
					this.mask.hide();
					this.mask = null;
				}
			}.bind(this)
		 );
	}
});

MWF.xApplication.process.Xform.DatatablePC.Importer = new Class({
	Implements: [Options, Events],
	options: {
	},
	initialize: function (datatable, options) {

		this.setOptions(options);

		this.datatable = datatable;
		this.form = this.datatable.form;

		this.lp = MWF.xApplication.process.Xform.LP;
		this.columnText =  this.lp.importValidationColumnText;
		this.columnTextExcel = this.lp.importValidationColumnTextExcel;
		this.excelUtil = new MWF.xApplication.process.Xform.DatatablePC.ExcelUtils( this.datatable );

		this.columnJsonList = [];
	},
	isAvaliableColumn : function(thJson, mJson){
		if (thJson && ( thJson.isShow === false || thJson.isImpExp === false ))return false; //숨김 열, 가져오기/내보내기 불가
		if (mJson && (mJson.type == "sequence" || mJson.cellType == "sequence") )return false; //순번 열
		if (mJson && ["Image","Button","ImageClipper","WritingBoard","Attachment","AttachmentDg","Label",
			"Elbutton","Elbutton","Elcarousel","Eldropdown","Elicon","Eltree"].contains(mJson.type) )return false; //이미지, 첨부파일, Label 열은 가져오기/내보내기 안 함
		// if (type==="import" && module && ["Label"].contains(module.json.type))return false; //Label은 가져오기 안 함
		return true;
	},
	importFromExcel : function () {
		this.datatable.fireEvent("beforeImport");

		this.getColumnList();
		var dateColArray = this.getDateIndexArray(); //날짜 열
		var orgTitleArray = this.getOrgTitleArray();

		this.excelUtil.upload( dateColArray, function (data) {

			MWF.require("MWF.widget.Mask", null, false);
			this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
			// 모바일 대응
			if (layout.mobile) {
				this.mask.load();
			} else {
				this.mask.loadNode(this.form.app.content);
			}

			this.importedData = data;
			if( !this.checkCount() )return;

			this.loadSimulateModule();
			this.columnJsonList.each(function (c) {
				c.module = this.importerLine.getModule(c.mJson.id)
			}.bind(this));

			if( orgTitleArray.length > 0 ){
				this.listAllOrgData( orgTitleArray, function () {
					this.checkAndImport();
				}.bind(this));
			}else{
				this.checkAndImport();
			}


		}.bind(this));
	},
	checkAndImport: function () {
		this.checkData( function (flag) {

			if( !flag ){
				this.openErrorDlg();
			}else{
				this.importData()
			}
			this.destroySimulateModule();

		}.bind(this));
	},
	destroySimulateModule: function(){
		if( !this.importerDatatable ){
			this.form.disallowSaving = false;
			return;
		}

		this.importerDatatable.destroy();
		this.importerDatatable = null;
		this.form.disallowSaving = false;
	},
	loadSimulateModule: function(){
		this.form.disallowSaving = true;
		this.importerDatatable = new MWF.xApplication.process.Xform.DatatablePC.ImporterDatabale( this.datatable );
		this.importerDatatable.load();

		this.importerLine = this.importerDatatable.addLine({});
	},
	getColumnList: function(){
		this.columnJsonList = [];

		var ths = this.datatable.titleTr.getElements("th.mwf_origional");
		var tds = this.datatable.templateTr.getElements("td.mwf_origional");
		var idx = 0;

		ths.each(function(th, index){
			var thJson = this.form._getDomjson( th );

			var mJson;
			if(tds[index]){
				var mNodes = this.form._getModuleNodes(tds[index]); //합계 열 안의 입력 컴포넌트 가져오기
				if( mNodes.length > 0 )mJson = this.form._getDomjson(mNodes[0]);
			}

			if(thJson && mJson && this.isAvaliableColumn(thJson, mJson)){
				this.columnJsonList.push({
					"thJson": thJson,
					"title": th.get("text"),
					"mJson" : mJson,
					"field": mJson.id,
					"index": idx,
					"dataType": thJson.impExpType
					// "module": this.importerLine.getModule(mJson.id)
					// "module": this.simelateModuleMap[mJson.id]
				});
				idx++;
			}
		}.bind(this));

		return this.columnJsonList;
	},
	getDateIndexArray : function(){
		var dateIndexArr = []; //날짜 형식 열 인덱스
		var idx=0;
		this.columnJsonList.each(function(c){
			if ( c.mJson && c.mJson.type === "Calendar" && (c.mJson.format === "%Y-%m-%d" || c.mJson.format === "%Y-%m-%d %H:%M:%S")) {
				dateIndexArr.push(idx);
			}
			idx++;
		}.bind(this));
		return dateIndexArr;
	},
	getOrgTitleArray : function(){
		var orgTitleArr = [];
		this.columnJsonList.each(function(c){
			if ( c.mJson && ["Org","Reader","Author","Personfield","Orgfield"].contains(c.mJson.type) ) {
				orgTitleArr.push(c.title);
			}
		}.bind(this));
		return orgTitleArr;
	},
	parseImportedData: function(){
		var data = [];
		this.importedData.each( function( ilineData ){
			var lineData = {};

			this.columnJsonList.each( function (obj, i) {
				var index = obj.index;
				var module = obj.module;
				var json = obj.mJson;
				var text = obj.title;

				var d = ilineData[text] || "";

				var value;
				if( d === "" || d === undefined || d === null ){
					value = "";
				}else{
					switch (json.type) {
						case "Org":
						case "Reader":
						case "Author":
						case "Personfield":
						case "Orgfield":
							var arr = this.stringToArray(d);
							if( arr.length === 0 ){
								value = this.getOrgData( d );
							}else{
								value = [];
								arr.each( function(d, idx){
									var obj = this.getOrgData( d );
									value.push( obj );
								}.bind(this));
							}
							break;
						default:
							value = d; //줄바꿈 문자&#10;
							break;
					}
				}

				lineData[ json.id ] = value;

			}.bind(this));

			data.push( lineData );
		}.bind(this));

		return data;
	},
	stringToArray: function(string){
		return string.replace(/&#10;/g,",").split(/\s*,\s*/g ).filter(function(s){
			return !!s;
		});
	},
	importData: function(){
		var data = this.parsedData; //this.parseImportedData(idata);

		this.datatable.fireEvent("import", [data] );

		this.datatable.setData( { "data" : data }, null, 'import' );

		this.datatable.fireEvent("afterImport", [data] );

		this.datatable.fireEvent("change", [{lines: this.datatable.lineList, type : "import"}]);

		if (this.mask) {
			this.mask.hide();
			this.mask = null;
		}

		this.form.notice( MWF.xApplication.process.Xform.LP.importSuccess );

	},
	openErrorDlg : function(){
		var eData = this.importedData;
		var _self = this;

		var objectToString = function (obj, type) {
			if(!obj)return "";
			var arr = [];
			Object.each(obj,  function (value, key) {
				if( type === "style" ){
					arr.push( key + ":"+ value +";" )
				}else{
					arr.push( key + "='"+ value +"'" )
				}
			})
			return arr.join(" ")
		}

		var htmlArray = ["<table "+ objectToString( this.datatable.json.impExpTableProperties ) +" style='"+objectToString( this.datatable.json.impExpTableStyles, "style" )+"'>"];

		var titleStyle = objectToString(this.datatable.json.impExpTableTitleStyles, "style");
		htmlArray.push("<tr>");
		this.columnJsonList.each(function (obj, i) {
			htmlArray.push( "<th style='"+titleStyle+"'>"+obj.title+"</th>" );
		});
		htmlArray.push("<th style='"+titleStyle+"'> "+MWF.xApplication.process.Xform.LP.validationInfor +"</th>");
		htmlArray.push("</tr>" );

		var contentStyles = Object.clone( this.datatable.json.impExpTableContentStyles );
		if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
		var contentStyle = objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

		eData.each( function( lineData, lineIndex ){

			htmlArray.push( "<tr>" );
			this.columnJsonList.each( function (obj, i) {
				htmlArray.push( "<td style='"+contentStyle+"'>"+ ( lineData[ obj.title ] || '' ).replace(/&#10;/g,"<br/>") +"</td>" ); //줄바꿈 문자&#10;
			});
			htmlArray.push( "<td style='"+contentStyle+"'>"+( lineData.errorTextList ? lineData.errorTextList.join("<br/>") : "" )+"</td>" );
			htmlArray.push( "</tr>" );

		}.bind(this));
		htmlArray.push( "</table>" );

		var width = this.datatable.json.impExpDlgWidth || 1000;
		var height = this.datatable.json.impExpDlgHeight || 700;
		width = width.toInt();
		height = height.toInt();

		var div = new Element("div", { style : "padding:10px;", html : htmlArray.join("") });
		var dlg = o2.DL.open({
			"style" : this.form.json.dialogStyle || "user",
			"title": MWF.xApplication.process.Xform.LP.importFail,
			"content": div,
			"offset": {"y": 0},
			"isMax": true,
			"width": width,
			"height": height,
			"buttonList": [
				{
					"type": "exportWithError",
					"text": MWF.xApplication.process.Xform.LP.datagridExport,
					"action": function () { _self.exportWithImportDataToExcel(eData); }
				},
				{
					"type": "cancel",
					"text": MWF.LP.process.button.cancel,
					"action": function () { dlg.close(); }
				}
			],
			"onPostClose": function(){
				dlg = null;
			}.bind(this)
		});

		if (this.mask) {
			this.mask.hide();
			this.mask = null;
		}

	},
	checkCount: function(){
		var idata = this.importedData;

		var lp = MWF.xApplication.process.Xform.LP;

		var exceeded = false;
		var maxCount = this.datatable.json.maxCount ? this.datatable.json.maxCount.toInt() : 0;
		if( maxCount > 0 && idata.length > maxCount )exceeded = true;

		var less = false;
		var minCount = this.datatable.json.minCount ? this.datatable.json.minCount.toInt() : 0;
		if( minCount > 0 && idata.length < minCount) less = true;

		if( exceeded ) {
			var text = lp.importTooManyNotice.replace("{n1}", idata.length).replace("{n2}", this.datatable.json.maxCount);
			this.form.notice(text, "error");
			return false;
		}

		if( less ){
			var text = lp.importTooFewNotice.replace("{n1}", idata.length).replace("{n2}", this.datatable.json.minCount );
			this.form.notice(text,"error");
			return false;
		}
		return true;
	},
	checkData : function( callback){
		this.parsedData = this.parseImportedData();
		this.isImportSuccess = true;

		this.checkLineData(0, function () {
			var arg = {
				validted : this.isImportSuccess,
				data : this.importedData,
				parsedData : this.parsedData
			};
			this.datatable.fireEvent( "validImport", [arg] );
			callback( arg.validted )
		}.bind(this));
	},
	checkLineData: function(lineIndex, callback){
		if( lineIndex < this.importedData.length ){
			this._checkLineData(this.importedData[lineIndex], lineIndex, function (flag) {
				lineIndex++;
				if( !flag )this.isImportSuccess = false;
				this.checkLineData(lineIndex, callback);
			}.bind(this));
		}else{
			if(callback)callback();
		}
	},
	_checkLineData: function(lineData, lineIndex, callback){
		lineData.errorTextList = lineData.errorTextList || [];
		lineData.errorTextListExcel = lineData.errorTextListExcel || [];

		var parsedLineData = (this.parsedData && this.parsedData[lineIndex]) ? this.parsedData[lineIndex] : [];

		this.checkModuleData(0, lineData, parsedLineData, function () {
			var flag = !lineData.errorTextList.length;
			callback(flag);
		});
	},
	checkModuleData: function(index, lineData, parsedLineData, callback){
		if( index < this.columnJsonList.length ){
			var result = this._checkModuleData(this.columnJsonList[index], lineData, parsedLineData);
			Promise.resolve(result).then(function (flag) {
				index++;
				this.checkModuleData(index, lineData, parsedLineData, callback);
			}.bind(this))
		}else{
			if(callback)callback();
		}
	},
	_checkModuleData: function (columnJson, lineData, parsedLineData) {
		var index = columnJson.index;
		var json = columnJson.mJson;
		var module = columnJson.module;
		var text = columnJson.title;

		var colInfor = this.columnText.replace( "{n}", index+1 );
		var colInforExcel = this.columnTextExcel.replace( "{n}", this.excelUtil.index2ColName( index ) );

		var d = lineData[text] || "";
		var parsedD = parsedLineData[json.id] || "";
		var lp = this.lp;
		var flag = true;

		if(d){
			switch (json && json.type) {
				case "Org":
				case "Reader":
				case "Author":
				case "Personfield":
				case "Orgfield":
					var arr = this.stringToArray(d);
					arr.each( function(d, idx){
						var obj = this.getOrgData( d );
						if( obj.errorText ){
							lineData.errorTextList.push( colInfor + obj.errorText + lp.fullstop );
							lineData.errorTextListExcel.push( colInforExcel + obj.errorText + lp.fullstop );
							flag = false;
						}
					}.bind(this));
					break;
				case "Number":
				case "Currency":
				case "OOCurrency":
				case "Elnumber":
					if (isNaN(d)){
						lineData.errorTextList.push( colInfor + d + lp.notValidNumber + lp.fullstop );
						lineData.errorTextListExcel.push( colInforExcel + d + lp.notValidNumber + lp.fullstop );
						flag = false;
					}
					break;
				case "Calendar":
				case "Eldate":
				case "Eldatetime":
					if( json.format === "%Y-%m-%d" || json.format === "%Y-%m-%d %H:%M:%S" ){
						if( !( isNaN(d) && !isNaN(Date.parse(d) ))){
							lineData.errorTextList.push(colInfor + d + lp.notValidDate + lp.fullstop );
							lineData.errorTextListExcel.push( colInforExcel + d + lp.notValidDate + lp.fullstop );
							flag = false;
						}
					}
					break;
				default:
					break;
			}
		}
		if (module && module.setData && json.type !== "Address"){
			var hasError = false;
			if(["Org","Reader","Author","Personfield","Orgfield"].contains(json.type)){
				if(o2.typeOf(parsedD)==="array" && parsedD.length){
					hasError = parsedD.some(function (item) { return item.errorText; });
					flag = false;
				}
			}
			if(!hasError){
				module.setExcelData(parsedD, columnJson.dataType );
				var ps = [];
				if( module.moduleExcelAG )ps.push( module.moduleExcelAG );
				if( module.moduleValueAG && !ps.contains(module.moduleValueAG) )ps.push( module.moduleValueAG );
				if( module.moduleSelectAG && !ps.contains(module.moduleSelectAG) )ps.push( module.moduleSelectAG );
				return Promise.all( ps ).then(function () {
					var result = module.validationExcel();
					if ( result && result.length ){
						lineData.errorTextList.push(colInfor + result.join("\n") );
						lineData.errorTextListExcel.push( colInforExcel + result.join("\n"));
						flag = false;
					}
					parsedLineData[json.id] = module.getData();
					return flag;
				})
			}
		}
		return flag
	},
	exportWithImportDataToExcel: function(eData){
		var exporter = new MWF.xApplication.process.Xform.DatatablePC.Exporter(this.datatable);
		exporter.exportWithImportDataToExcel(eData)
	},
	getOrgData : function( str ){
		str = str.trim();
		var flag = str.substr(str.length-2, 2);
		switch (flag.toLowerCase()){
			case "@i":
				return this.identityMap[str] || {"errorText": str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			case "@p":
				return this.personMap[str] || {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			case "@u":
				return this.unitMap[str] ||  {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			case "@g":
				return this.groupMap[str] ||  {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			default:
				return this.identityMap[str] ||
					this.personMap[str] ||
					this.unitMap[str] ||
					this.groupMap[str] ||
					{"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };

		}
	},
	listAllOrgData : function (orgTitleList, callback) {
		var identityList = [], personList = [], unitList = [], groupList = [];
		if( orgTitleList.length > 0 ){
			this.importedData.each( function( lineData, lineIndex ){
				// if( lineIndex === 0 )return;

				orgTitleList.each( function (title, index) {

					if( !lineData[title] )return;

					var arr = this.stringToArray(lineData[title]);
					arr.each( function( a ){
						a = a.trim();
						var flag = a.substr(a.length-2, 2);
						switch (flag.toLowerCase()){
							case "@i":
								identityList.push( a ); break;
							case "@p":
								personList.push( a ); break;
							case "@u":
								unitList.push( a ); break;
							case "@g":
								groupList.push( a ); break;
							default:
								identityList.push( a );
								personList.push( a );
								unitList.push( a );
								groupList.push( a );
								break;
						}
					})
				}.bind(this))
			}.bind(this));
			var identityLoaded, personLoaded, unitLoaded, groupLoaded;
			var check = function () {
				if( identityLoaded && personLoaded && unitLoaded && groupLoaded ){
					if(callback)callback();
				}
			};

			this.identityMap = {};
			if( identityList.length ){
				identityList = identityList.unique();
				o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({ identityList : identityList }, function (json) {
					json.data.each( function (d) { this.identityMap[ d.matchKey ] = d; }.bind(this));
					identityLoaded = true;
					check();
				}.bind(this))
			}else{
				identityLoaded = true;
				check();
			}

			this.personMap = {};
			if( personList.length ){
				personList = personList.unique();
				o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({ personList : personList, useNameFind: true }, function (json) {
					json.data.each( function (d) { this.personMap[ d.matchKey ] = d; }.bind(this));
					personLoaded = true;
					check();
				}.bind(this))
			}else{
				personLoaded = true;
				check();
			}

			this.unitMap = {};
			if( unitList.length ){
				unitList = unitList.unique();
				o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({ unitList : unitList, useNameFind: true }, function (json) {
					json.data.each( function (d) { this.unitMap[ d.matchKey ] = d; }.bind(this));
					unitLoaded = true;
					check();
				}.bind(this))
			}else{
				unitLoaded = true;
				check();
			}

			this.groupMap = {};
			if( groupList.length ){
				groupList = groupList.unique();
				o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({ groupList : groupList }, function (json) {
					json.data.each( function (d) { this.groupMap[ d.matchKey ] = d; }.bind(this));
					groupLoaded = true;
					check();
				}.bind(this))
			}else{
				groupLoaded = true;
				check();
			}
		}
	}
});

MWF.xDesktop.requireApp("Template", "utils.ExcelUtils", null, false);
MWF.xApplication.process.Xform.DatatablePC.ExcelUtils = new Class({
	Extends: MWF.xApplication.Template.utils.ExcelUtils,
	initialize: function(){
		this.sheet2JsonOptions = {};
		this.pollyfill();
	}
});
