MWF.require("MWF.widget.Common", null, false);
/** @classdesc $Module 컴포넌트 클래스, 모든 컴포넌트의 부모 클래스.
 * @class
 * @o2category FormComponents
 * @hideconstructor
 * */
MWF.xApplication.process.Xform.$Module = MWF.APP$Module =  new Class(
    /** @lends MWF.xApplication.process.Xform.$Module# */
    {
    Implements: [Events],
    options: {
        /**
         * 컴포넌트 로드 전 발생. queryLoad 실행 시점에는 현재 컴포넌트가 form에 등록되지 않아 this.form.get("fieldId")로 가져올 수 없으며, this.target으로 가져와야 함.
         * @event MWF.xApplication.process.Xform.$Module#queryLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 컴포넌트 로드 후 발생.
         * @event MWF.xApplication.process.Xform.$Module#postLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 컴포넌트 로드 후 발생.
         * @event MWF.xApplication.process.Xform.$Module#load
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad"]
    },
    initialize: function(node, json, form, options){
        /**
         * @summary 컴포넌트 노드, mootools로 감싼 Dom 객체이며 네이티브 js와 mootools 메서드로 접근·조작 가능.
         * @see https://mootools.net/core/docs/1.6.0/Element/Element
         * @member {Element}
         * @example
         *  //스크립트에서 이 컴포넌트를 가져올 수 있음
         * var field = this.form.get("fieldId"); //컴포넌트 객체 조회
         * field.node.setStyle("font-size","12px"); //노드에 스타일 설정
         */
        this.node = $(node);
        this.node.store("module", this);

        /**
         * @summary 컴포넌트 설정 정보(id, 유형, 읽기 전용 여부 등). 컴포넌트의 queryLoad 이벤트에서 이 설정을 수정해 컴포넌트를 변경할 수 있음.
         * @member {JsonObject}
         * @example
         *  //스크립트에서 이 컴포넌트를 가져올 수 있음
         * var json = this.form.get("fieldId").json; //컴포넌트 객체 가져오기
         * var id = json.id; //컴포넌트 id 가져오기
         * var type = json.type; //컴포넌트 유형 가져오기, 예: Textfield는 텍스트 입력 컴포넌트, Select는 드롭다운 컴포넌트
         *
         * //컴포넌트 queryLoad 이벤트에서 컴포넌트를 읽기 전용으로 설정.
         * //현재 컴포넌트의 queryLoad 이벤트 실행 시점에는 form에 등록되지 않아 this.form.get("fieldId")로 가져올 수 없으며, this.target으로 가져와야 함.
         * var json = this.target.json;
         * json.isReadonly = true; //컴포넌트를 읽기 전용으로 설정.
         */
        this.json = json;

        /**
         * @summary 컴포넌트가 속한 양식 객체.
         * @member {MWF.xApplication.process.Xform.Form}
         * @example
         * var form = this.form.get("fieldId").form; //컴포넌트가 속한 양식 객체 가져오기
         * var container = form.container; //양식 컨테이너 가져오기
         */
        this.form = form;


        /**
         * 현재 컴포넌트가 데이터 테이블 또는 데이터 템플릿 안에 있을 때, 이 속성으로 속한 행(항목) 객체를 가져올 수 있음.
         * @member {MWF.xApplication.process.Xform.Datatemplate.Line|MWF.xApplication.process.Xform.DatatablePC.Line|MWF.xApplication.process.Xform.DatatableMobile.Line}
         * @example
         * //현재 컴포넌트가 속한 데이터 템플릿/데이터 테이블의 행(항목) 객체 가져오기
         * var line = this.target.parentLine;
         * //현재 필드가 속한 행 인덱스 가져오기
         * var index = line.getIndex();
         * //현재 필드가 속한 항목의 subject 필드 값 가져오기
         * var data = line.getModule("subject").getData();
         * //현재 필드가 속한 항목의 subject 필드 값 설정
         * line.getModule("subject").setData("test1");
         */
        this.parentLine = null;
    },
    /**
     * @summary 컴포넌트 검증 설정에 따라 검증.
     *  @param {String} [routeName] - 선택, 경로 이름.
     *  @example
     *  if( !this.form.get('fieldId').validate() ){
     *      return false;
     *  }
     *  @example
     *  //필드 검증이 비동기이면 promise 반환.
     *  const result = this.form.get('fieldId').validate();
     *  result.then(flag=>{
     *      //flag는 검증 통과 여부
     *  })
     *  @return {Boolean|Promise} 검증 통과 여부, 필드 검증이 비동기이면 promise 반환.
     */
    validate: function (routeName, opinion) {
        if( this.validationMode )this.validationMode();
        if( this.validation ){
            return this.validation(routeName, opinion);
        }else{
            return true;
        }
    },
    validation: function (routeName, opinion) {
        //!this.isReadonly() && this.json.showMode!=="disabled" && !this.node?.isDisplayNone()
        this.moduleValidationAG = null;
        if (!this.isReadonly() && this.json.showMode!=="disabled"){
            if (this.getInputData){
                this._setBusinessData(this.getInputData("change"));
            }
            if (this.node.checkValidity){
                if (!this.node.checkValidity()) {
                    if ( this.node && !this.node.isIntoView()) this.node.scrollIntoView({ behavior: "smooth", block: "center" });
                    return false;
                }
            }
            if (this.validationFormat){
                if (!this.validationFormat()) return false;
            }
            if (!this.validationConfig(routeName, opinion)) return false;

            return this._validation(routeName, opinion);
        }
        return true;
    },
    _validation: function(routeName){
        if (!this.json.validation) return true;
        if (!this.json.validation.code) return true;

        this.currentRouteName = routeName;
        var checkResult = this.form.Macro.exec(this.json.validation.code, this);
        this.currentRouteName = "";

        const isAsync = checkResult instanceof Promise;
        this.moduleValidationAG = isAsync ? checkResult : null;

        if (!isAsync && !checkResult) {
            checkResult = MWF.xApplication.process.Xform.LP.notValidation;
        }

        if (!isAsync && String(checkResult) !== "true") {
            this.notValidationMode(checkResult);
            return false;
        }

        if (isAsync) {
            return checkResult.then(f => {
                const result = String(f) === "true";
                if (!result) {
                    const msg = f || MWF.xApplication.process.Xform.LP.notValidation;
                    this.notValidationMode(msg);
                }
                return result;
            });
        }
        return true;
    },
    saveValidation: function () {
        return true;
    },
    /**
     * 현재 컴포넌트가 데이터 소스 컴포넌트 안에 있을 때, 이 메서드로 상위 데이터 소스/하위 데이터 소스/하위 데이터 항목 컴포넌트를 가져올 수 있음.
     * @param {String} [type] 가져올 유형, "source"는 데이터 소스, "subSource"는 하위 데이터 소스, "subSourceItem"은 하위 데이터 항목 컴포넌트를 의미.
     * 이 인자를 생략하면 현재 컴포넌트에서 가장 가까운 위 컴포넌트를 가져옴.
     * @return {Source|SubSource|SubSourceItem}。
     * @example
     * var source = this.target.getSource(); //현재 컴포넌트가 속한 상위 데이터 소스/하위 데이터 소스/하위 데이터 항목 컴포넌트 가져오기.
     * var data = source.data; //데이터 가져오기
     *
     * var source = this.form.get("fieldId").getSource("source"); //데이터 소스 컴포넌트 가져오기
     * var data = source.data; //데이터 가져오기
     */
    getSource: function( type ){
        if( type ){
            var parent = this.node.getParent();
            while(parent && parent.get("MWFtype")!= type ){
                parent = parent.getParent();
            }
            return (parent) ? parent.retrieve("module") : null;
        }else{
            return this._getSource();
        }
    },
    _getSource: function(){
        var parent = this.node.getParent();
        while(parent && (
            parent.get("MWFtype")!="source" &&
            parent.get("MWFtype")!="subSource" &&
            parent.get("MWFtype")!="subSourceItem"
        )) parent = parent.getParent();
        return (parent) ? parent.retrieve("module") : null;
    },
    /**
     * 현재 컴포넌트가 속한 조상 컴포넌트 가져오기.
     * @param {String} [type] 가져올 컴포넌트 유형.
     * 이 인자를 생략하면 현재 컴포넌트에서 가장 가까운 조상 컴포넌트를 가져옴. type 유효값은 다음과 같음:
     * <div>form- 양식</div>
     * <div>common- 공통 컴포넌트</div>
     * <div>datatable- 데이터 테이블</div>
     * <div>datatableline- 데이터 테이블 행</div>
     * <div>datatemplate- 데이터 템플릿</div>
     * <div>datatemplateline- 데이터 템플릿 행</div>
     * <div>div- 컨테이너 컴포넌트</div>
     * <div>elcommon- Element 공통 컴포넌트</div>
     * <div>elcontainer- Element 컨테이너 컴포넌트</div>
     * <div>subform- 하위 양식</div>
     * <div>source- 데이터 소스 컴포넌트</div>
     * <div>subsource- 하위 데이터 소스</div>
     * <div>subsourceitem- 하위 데이터 항목 컴포넌트</div>
     * <div>tab- 탭 컴포넌트</div>
     * <div>tabpage- 탭 컴포넌트의 개별 탭</div>
     * <div>table- 표</div>
     * <div>tabletd- 셀</div>
     * <div>widget- 위젯</div>
     * @param {Function} [validateFunction] 추가 검증 함수, 인자는 일치하는 유형의 컴포넌트이며 false를 반환하면 계속 위로 해당 유형 컴포넌트를 찾고 true를 반환하면 해당 컴포넌트를 반환.
     * @return {MWF.xApplication.process.Xform.$Module}。
     * @example
     * var module = this.target.getParentModule(); //가장 가까운 조상 가져오기.
     *
     * var datatemplateLine = this.target.getParentModule("datatemplateline"); //현재 컴포넌트가 속한 데이터 템플릿 행 가져오기.
     *
     * var module = this.target.getParentModule(null, function(module){
     *     return module.json.id === "div_1";
     * }); //현재 컴포넌트에서 id가 div_1인 부모 컴포넌트 가져오기.
     */
    getParentModule: function( type, validateFunction ){
        var lcType = ( type || "" ).toLowerCase();
        if( lcType === "form" )return this.form;

        var module, vm;

        var parent;
        if( ["datatableline","datatemplateline", "tabpage", "tab", "widget", "table"].contains( lcType ) ){
            parent = this.node;
        }else{
            parent = this.node.getParent();
        }
        while(parent) {
            module = null;
            vm = null;
            var MWFtype = parent.get("MWFtype");
            if( MWFtype ){
                module = parent.retrieve("module");
                if( module ){
                    switch (lcType) {
                        case "":
                            vm = module;
                            break;
                        case "table":
                            if( module.table )vm = module.table;
                            break;
                        case "widget":
                            if( module.widget )vm = module.widget;
                            break;
                        case "tab":
                            if( module.tab )vm = module.tab;
                            break;
                        case "tabpage":
                            if( module.page && module.tab )vm = module.page;
                            break;
                        case "datatableline":
                            if( module.parentLine && module.parentDatatable )vm = module.parentLine;
                            break;
                        case "datatemplateline":
                            if( module.parentLine && module.parentDatatemplate )vm = module.parentLine;
                            break;
                        case "subsourceitem":
                            if( MWFtype.toLowerCase() === "subsourceitem" )vm = module;
                            break;
                        case "tabletd":
                            if( module.json.type === "Table$Td" )vm = module;
                            break;
                        default:
                            if( module.json.type.toLowerCase() === lcType )vm = module;
                            break;
                    }
                }
                if( vm ){
                    if( !validateFunction ){
                        return vm;
                    }else if( validateFunction && validateFunction.call(this.form.Macro, vm) ){
                        return vm;
                    }
                }
                parent = parent.getParent();
            }else{
                parent = parent.getParent();
            }
        }
        return null;
    },
    isReadonly : function(){
        return !!(!this.isEditable || this.readonly || this.json.isReadonly || this.form.json.isReadonly || this.json.showMode==="read" || this.isSectionMergeRead());
    },
    isAllSectionShow: function(){
        return this.json.showAllSection && this.json.section === "yes" && this.isSectionData();
    },
    isSectionMergeRead: function(){
        return this.json.sectionMerge === "read" && this.json.section !== "yes" && this.isSectionData()
    },
    isSectionMergeEdit: function(){
        return this.json.sectionMerge === "edit" && this.json.section !== "yes" && this.isSectionData()
    },
    isSectionData: function(){ //데이터 구간 처리 여부
        var data = this.getBusinessDataById();
        return o2.typeOf( data ) === "object";
    },
    getSortedSectionData: function(){ //병합·정렬된 데이터 가져오기
        var data = this.getBusinessDataById();
        var array = [];
        for( var key in data ){
            array.push({
                sectionKey: key,
                key: key,
                data: data[key]
            })
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
    //구간 병합의 구간 값
    _getMergeSectionKey: function( data ){
        switch (this.json.sectionKey){
            case "person":
                return layout.desktop.session.user.id;
            case "unit":
                return (this.form.businessData.task) ? (this.form.businessData.task.unitDn || this.form.businessData.task.unit) : "";
            case "activity":
                return (this.form.businessData.work) ? this.form.businessData.work.activity : "";
            case "splitValue":
                return (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
            case "script":
                var d;
                if( this.json.sectionKeyScript && this.json.sectionKeyScript.code){
                    this.form.Macro.environment.event = data;
                    d = this.form.Macro.exec(this.json.sectionKeyScript.code, this);
                    this.form.Macro.environment.event = null;
                }else{
                    d = "";
                }
                return d;
            default:
                return "";
        }
    },
    getSectionKeyWithMerge: function(data, callback){
        switch (this.json.sectionKey) {
            case "person":
                if( !this.form.sectionKeyPersonMap )this.form.sectionKeyPersonMap = {};
                if( this.form.sectionKeyPersonMap[data.key] ){
                    callback(this.form.sectionKeyPersonMap[data.key]);
                    return;
                }

                //한 번만 조회. callback을 저장해 두었다가 비동기 호출 완료 후 한꺼번에 실행
                if( !this.form.sectionKeyCallbackMap )this.form.sectionKeyCallbackMap = {};
                var map = this.form.sectionKeyCallbackMap;
                if( !map[ data.key ] )map[ data.key ] = [];
                if( !map[ data.key ].length ){
                    Promise.resolve( o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({
                        "personList": [data.key]
                    })).then(function(json){
                        var key = json.data.length ? json.data[0].name : data.key;
                        this.form.sectionKeyPersonMap[data.key] = key;
                        while( map[ data.key ].length ){
                            map[ data.key ].shift()( key );
                        }
                    }.bind(this));
                }
                map[ data.key ].push( callback );

                break;
            case "unit":
                callback( data.key.split("@")[0] );
                break;
            case "activity":
            case "splitValue":
                callback( data.key );
                break;
            case "script":
                var d;
                if( this.json.sectionKeyScript && this.json.sectionKeyScript.code){
                    this.form.Macro.environment.event = data;
                    d = this.form.Macro.exec(this.json.sectionKeyScript.code, this);
                    this.form.Macro.environment.event = null;
                }else{
                    d = "";
                }
                callback( d );
                break;
        }
    },
    _loadMergeReadNode: function(keepHtml, position) {
        if( this.json.type.startsWith('OO')){
            var node = new Element('div', {
                styles: this.form.css.mergedReadOOModule
            }).inject(this.node, 'before');
            this.node.destroy();
            this.node = node;
            this.node.setStyles( this._parseStyles( this.json.styles ) );
            this.node.setStyles( this._parseStyles( this.json.recoveryStyles ) );
        }
        if (!keepHtml) {
            this.node.empty();
            this.node.set({
                "nodeId": this.json.id,
                "MWFType": this.json.type
            });
        }
        var contentNode;
        if( this.json.type.startsWith('OO')){
            var labelNode = new Element('div', {
                text: this.json.label || '',
                styles: this.form.css.mergedReadOOModuleLabel
            }).inject(this.node);
            contentNode = new Element('div',
                { styles: this.form.css.mergedReadOOModuleContent }
            ).inject(this.node);


            var styleText, properties = this.json.properties;
            if( properties['label-Align'] || properties['label-align'] ){
                styleText = labelNode.getAttribute('style');
                labelNode.setAttribute( 'style', styleText + ";" + (properties['label-Align'] || properties['label-align']) );
            }
            if( properties['label-style'] ){
                styleText = labelNode.getAttribute('style');
                labelNode.setAttribute('style', styleText + ";" + properties['label-style']);
            }
            if( properties['view-style'] ){
                styleText = contentNode.getAttribute('style');
                contentNode.setAttribute('style', styleText + ";" + properties['view-style']);
            }
        }
        switch (this.json.mergeTypeRead) {
            case "htmlScript":
                this._loadMergeReadNodeByHtml(contentNode);
                break;
            case "dataScript":
                this._loadMergeReadNodeByData(contentNode);
                break;
            default:
                this._loadMergeReadNodeByDefault(position, contentNode);
                break;
        }
    },
    _loadMergeReadNodeByHtml: function(content){
        if (this.json.sectionMergeReadHtmlScript && this.json.sectionMergeReadHtmlScript.code) {
            var html = this.form.Macro.exec(this.json.sectionMergeReadHtmlScript.code, this);
            (content || this.node).set("html", html);
        }
    },
    _loadMergeReadNodeByData: function(){
        if (this.json.sectionMergeReadDataScript && this.json.sectionMergeReadDataScript.code) {
            var data = this.form.Macro.exec(this.json.sectionMergeReadDataScript.code, this);

        }
    },
    _loadMergeReadNodeByDefault: function( position, content ){
        var data = this.getSortedSectionData();
        var sectionNodeStyles = this._parseStyles(this.json.sectionNodeStyles);
        var sectionKeyStyles = this._parseStyles(this.json.sectionKeyStyles);
        var sectionContentStyles = this._parseStyles(this.json.sectionContentStyles);
        data.each(function(d){
            var node = new Element("div.mwf_sectionnode", {
                styles : sectionNodeStyles
            }).inject(content || this.node, position || "bottom");

            if( this.json.showSectionKey ){
                var keyNode = new Element("div.mwf_sectionkey", {
                    styles : sectionKeyStyles
                }).inject(node);
                this.getSectionKeyWithMerge( d, function (key) {
                    if( o2.typeOf(key) === "string" ){
                        keyNode.set("text", key + (this.json.keyContentSeparator || ""));
                    }else{
                        Promise.resolve(key).then(function (k) {
                            keyNode.set("text", k + (this.json.keyContentSeparator || ""));
                        }.bind(this))
                    }
                }.bind(this));
            }
            var contentNode = new Element("div.mwf_sectioncontent", {
                styles : sectionContentStyles
            }).inject(node);
            this._loadMergeReadContentNode( contentNode, d )
        }.bind(this))
    },
    _loadMergeReadContentNode: function( contentNode, data ){
        contentNode.set("text", data.data)
    },
    _loadMergeEditNode: function(){
        if( this.json.mergeTypeEdit === "script" ){
            this._loadMergeEditNodeByScript();
        }else{
            this._loadMergeEditNodeByDefault();
        }
    },
    _loadMergeEditNodeByScript: function(){
        if (this.json.sectionMergeEditScript && this.json.sectionMergeEditScript.code) {
            var data = this.form.Macro.exec(this.json.sectionMergeEditScript.code, this);
            this._setBusinessData( data );
            this._loadNode();
        }
    },
    _loadMergeEditNodeByDefault: function(){
        var data = this.getSortedSectionData();
        data = data.map(function(d){ return d.data; });
        this._setBusinessData( data.join("") );
        this._loadNode();
    },
    /**
     * @summary 컴포넌트 숨기기.
     * @example
     * this.form.get("fieldId").hide(); //컴포넌트 숨기기
     */
    hide: function(){
        if(this.node){
            var dsp = this.node.getStyle("display");
            if (dsp!=="none") this.node.store("mwf_display", dsp);
            //this.node.setStyle("display", "none");
            this.node.style.setProperty('display', 'none', 'important');
            //if (this.iconNode) this.iconNode.setStyle("display", "none");
        }
        if(this.downloadingNode){
            this.downloadingNode.style.setProperty('display', 'none', 'important');
        }
        if (this.iconNode) this.iconNode.style.setProperty('display', 'none', 'important');
    },
    /**
     * @summary 컴포넌트 표시.
     * @example
     * this.form.get("fieldId").show(); //컴포넌트 표시
     */
    show: function(){
        var dsp = this.node.retrieve("mwf_display");
        this.node.setStyle("display", dsp || '');
        if (this.iconNode) this.iconNode.setStyle("display", "block");
    },
    load: function(){
        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            this._queryLoaded();
            this._loadUserInterface();
            this._loadStyles();
            this._loadDomEvents();
            //this._loadEvents();
            this._afterLoaded();
            this.fireEvent("postLoad");

            if( this.moduleSelectAG && typeOf(this.moduleSelectAG.then) === "function" ){
                this.moduleSelectAG.then(function () {
                    this.fireEvent("load");
                    this.isLoaded = true;
                }.bind(this))
            }else{
                this.fireEvent("load");
                this.isLoaded = true;
            }
        }
    },
    reload: function(){
        this._loadReadEditAbeld();
        this._loadUserInterface();
    },

    _queryLoaded: function(){
        this._loadReadEditAbeld();
    },
    _afterLoaded: function(){},
    
    _loadReadEditAbeld: function(){
        this.node?.removeClass('hide');
        this._loadPoweer();
        this.isReadable = this._isReadable();
        this.isEditable = !this.isReadable ? false : this._isEditable();
        this.isHideUnreadable = this._isHideUnreadable();
    },
    _isEditable: function () {
        //편집 가능 판정이 no이면 반드시 편집 불가
        if (this.editable==='no') return false;

        //현재 편집 가능 판정이 no가 아니면 편집 가능 여부를 정하기 위해 부모 요소를 확인해야 함.
        let p = this.getParentModule();
        while (p) {
            //부모 요소의 편집 가능 판정이 no이면 현재 요소도 편집 불가
            if (p.editable=== 'no') return false;
            p = p.getParentModule();
        }

        //나머지 경우는 부모·현재 요소 모두 편집 가능 판정이 no가 아니므로, 현재 요소가 읽기 가능하면 편집 가능
        return this.readable!=='no';
    },
    _isReadable: function () {
        //읽기 가능 판정이 no이면 반드시 읽기 불가
        if (this.readable==='no') return false;

        //현재 읽기 가능 판정이 no가 아니면 읽기 가능 여부를 정하기 위해 부모 요소를 확인해야 함.
        let p = this.getParentModule();
        while (p) {
            //부모 요소의 읽기 가능 판정이 no이면 현재 요소도 읽기 불가
            if (p.readable=== 'no') return false;
            p = p.getParentModule();
        }

        //나머지 경우는 부모·현재 요소 모두 읽기 가능 판정이 no가 아니므로 현재 요소는 읽기 가능
        return true;
    },
    _isHideUnreadable: function(){
        if (this.json.hideCannotRead === 'no') return false;
        if (this.json.hideCannotRead === 'yes') return true;
        let p = this.getParentModule();
        while (p) {
            if (p.json.hideCannotRead === 'yes') return true;
            if (p.json.hideCannotRead === 'no') return false;
            p = p.getParentModule();
        }
        return false;
    },
    _loadPoweer: function () {
        this.editable = this._checkEditAbled();
        if (this.editable==='yes'){
            //편집 가능이 설정되어 있고 yes로 판정되면 readable은 반드시 yes이며 읽기 가능 설정은 무시.
            this.readable = 'yes';
        }else{
            this.readable = this._checkReadAbled();
            //편집 가능이 설정되어 있고 script_yes로 판정되면 readable이 yes여야 편집 가능.
            if (this.editable==='script_yes'){
                this.editable = (this.readable==='no') ? 'no' : 'yes';
            }
            // this.editable = (this.editable==='script_yes' && this.readable==='no') ? 'no' : 'yes';
        }
    },
    _checkReadAbled: function () {
        return this._checkpowerAbled('readByActivity', 'readByOrg', 'readByScript', 'readByActivityValue', 'readByOrgValue', 'readByScriptValue', 'readActivityList', 'readerList', 'excludeReadActivityList', 'excludeReaderList');
    },
    _checkEditAbled: function () {
        return this._checkpowerAbled('editByActivity', 'editByOrg', 'editByScript', 'editByActivityValue', 'editByOrgValue', 'editByScriptValue', 'editActivityList', 'editorList', 'excludeEditActivityList', 'excludeEditorList');
    },
    /**
     * 문자열을 정수로 변환할 수 있는지 판단
     * @param {string} str
     * @returns {boolean}
     */
    _isIntegerString: function(str) {
        return typeof str === "string" && /^-?\d+$/.test(str);
    },

    _getActivityId: function(review=true){
        if (review && this.form.businessData.review){
            return {uid: this.form.businessData.review.activityUnique}
        }else{
            return {uid: this.form.businessData.activity?.unique, aid: this.form.businessData.activity?.id}
        }
    },
    _checkpowerAbled: function (activity, org, script, activityValue, orgValue, scriptValue, processActivity, processOrg, unProcessActivity, unProcessOrg) {
        const accessList = this.form.businessData?.control?.itemAccessList;
        const ids = this.json.id.split(/\.\./g);
        const id = ids.map(i=> (/^-?\d+$/.test(i) ? '*' : i) ).join('.')
        const access = accessList?.find((acc)=>{
            return acc.path === id;
        });
        
        if (access){
            const hasByActivity = (access[processActivity] && access[processActivity].length) || (access[unProcessActivity] && access[unProcessActivity].length);
            const hasByOrg = (access[processOrg] && access[processOrg].length) || (access[unProcessOrg] && access[unProcessOrg].length);
            const hasByScript = this.json[script] && this.json[script].includes('true') && (this.json[scriptValue]?.code || this.json[scriptValue]);

            if (hasByActivity || hasByOrg || hasByScript){
                const {uid, aid} = this._getActivityId();
                if (hasByActivity && access[unProcessActivity]?.length){
                    const n = access[unProcessActivity].findIndex((act)=>{                      
                        return o2.typeOf(act)==="object" ? (act.unique === uid || act.id === aid) : act === uid;
                    });
                    if (n!==-1) return 'no';
                }
                if (hasByOrg && access[unProcessOrg]?.length){
                    const n = access[unProcessOrg].findIndex((org)=>{
                        const dn = org.distinguishedName || org;
                        return layout.session.userDetail.distinguishedName === dn || layout.session.userDetail.list.includes(dn);
                    });
                    if (n!==-1) return 'no';
                }


                if (hasByActivity){
                    const i = access[processActivity].findIndex((act)=>{                      
                        return o2.typeOf(act)==="object" ? (act.unique === uid || act.id === aid) : act === uid;
                    });
                    if (i!==-1) return 'yes';
                }

                if (hasByOrg){
                    const i = access[processOrg].findIndex((org)=>{
                        const dn = org.distinguishedName || org;
                        return layout.session.userDetail.distinguishedName === dn || layout.session.userDetail.list.includes(dn);
                    });
                    if (i!==-1) return 'yes';
                }

                if (hasByScript){
                    var flag = this.form.Macro.exec((this.json[scriptValue]?.code || this.json[scriptValue]), this);
                    return !!flag ? 'script_yes' : 'no';
                }
                return (!access[processActivity]?.length && !access[processOrg]?.length) ? 'unset' : 'no';
            }

        }else{
            const hasByActivity = this.json[activity] && this.json[activity].includes('true') && this.json[activityValue] && this.json[activityValue].length;
            const hasByOrg = this.json[org] && this.json[org].includes('true') && this.json[orgValue] && this.json[orgValue].length;
            const hasByScript = this.json[script] && this.json[script].includes('true') && (this.json[scriptValue]?.code || this.json[scriptValue]);

            if (hasByActivity || hasByOrg || hasByScript){
                if (hasByActivity){
                    let {uid, aid} = this._getActivityId(false);
                    // let uid = this.form.businessData.activity && this.form.businessData.activity.unique;
                    // let aid = this.form.businessData.activity && this.form.businessData.activity.id;
                    if (!uid){
                        uid = this.form.businessData.document && this.form.businessData.document.docStatus;
                    }
                    // if (activityId){
                        const i = this.json[activityValue].findIndex((act)=>{                      
                            return o2.typeOf(act)==="object" ? (act.unique === uid || act.id === aid) : act === uid;
                        });
                        if (i!==-1) return 'yes';
                    // }
                }
                if (hasByOrg){
                    const i = this.json[orgValue].findIndex((org)=>{
                        const dn = org.distinguishedName || org;
                        return layout.session.userDetail.distinguishedName === dn || layout.session.userDetail.list.includes(dn);
                    });
                    if (i!==-1) return 'yes';
                }
                if (hasByScript){
                    var flag = this.form.Macro.exec((this.json[scriptValue]?.code || this.json[scriptValue]), this);
                    return !!flag ? 'yes' : 'no';
                }
                return 'no';
            }
        }
        return 'unset'
    },
    
    _loadUserInterface: function(){
        //	this.node = this.node;
    },
    addRelated: function(path){
        if (!this.form.relatedModules) this.form.relatedModules = {};
        if (!this.form.relatedModules[path]) this.form.relatedModules[path] = new Set();
        this.form.relatedModules[path].add(this);
    },

    /**
         * 현재 컴포넌트에 표시 연동 데이터를 추가하며, 이 데이터가 바뀌면 현재 컴포넌트의 표시 상태를 갱신.
         * @param {String|Array<String>} [path] 연동할 데이터 경로.
         * @param {Function} [cb] 현재 컴포넌트 표시 여부를 확인하는 불리언 반환 함수, true를 반환하면 표시, false를 반환하면 숨김
         * @example
         * //컴포넌트의 load 이벤트에 아래 코드를 추가:
         * this.target.addRelatedDisplay('dataType', ()=>{
         *  return this.data.dataType==='1'
         * });
         * 
         * //위 코드를 추가하면 “dataType” 값이 바뀔 때마다 현재 컴포넌트의 숨김/표시를 확인함. dataType 값이 “1”이면 현재 컴포넌트를 표시하고, 아니면 숨김.
         */
    addRelatedDisplay: function(path, cb){
        if (cb){
            const ps = Array.isArray(path) ? path : [path];
            ps.forEach((p)=>{
                if (!this.form.relatedDisplayModules) this.form.relatedDisplayModules = {};
                if (!this.form.relatedDisplayModules[p]) this.form.relatedDisplayModules[p] = new Set();
                this.form.relatedDisplayModules[p].add({
                    module: this,
                    display: cb
                });
            }); 
            this._checkDisplay(cb);
        }
    },

    /**
         * 현재 컴포넌트에 값 연동 데이터를 추가하며, 이 데이터가 바뀌면 현재 컴포넌트 값을 갱신.
         * @param {String|Array<String>} [path] 연동할 데이터 경로.
         * @param {Function} [cb] 연동 데이터가 바뀌면 실행되는 함수, 반환값으로 현재 컴포넌트 값을 갱신.
         * @example
         * //field 계열 컴포넌트(예: 텍스트 상자)의 load 이벤트에 아래 코드를 추가:
         * this.target.addRelatedValue(['price', 'count'], ()=>{
         *  return this.data.price * this.data.count;
         * });
         * 
         * //위 코드를 추가하면 price 또는 count 값이 바뀔 때마다 현재 컴포넌트 값을 계산하며, 값은 price에 count를 곱한 값.
         */
    addRelatedValue: function(path, cb){
        if (cb){
            const ps = Array.isArray(path) ? path : [path];
            ps.forEach((p)=>{
                if (!this.form.relatedValueModules) this.form.relatedValueModules = {};
                if (!this.form.relatedValueModules[p]) this.form.relatedValueModules[p] = new Set();
                this.form.relatedValueModules[p].add({
                    module: this,
                    value: cb
                });
            }); 
            this._checkValue(cb);
        }
    },

    _loadStyles: function(){
        if (this.json.styles){
            this.node.setStyles( this._parseStyles(this.json.styles) );
        }
        // if (this.json.styles) Object.each(this.json.styles, function(value, key){
        //     if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1 || value.indexOf("x_cms_assemble_control")!=-1)){
        //         var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
        //         var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
        //         var host3 = MWF.Actions.getHost("x_cms_assemble_control");
        //         if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
        //             value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
        //         }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
        //             value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
        //         }
        //         if (value.indexOf("/x_portal_assemble_surface")!==-1){
        //             value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
        //         }else if (value.indexOf("x_portal_assemble_surface")!==-1){
        //             value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
        //         }
        //         if (value.indexOf("/x_cms_assemble_control")!==-1){
        //             value = value.replace("/x_cms_assemble_control", host3+"/x_cms_assemble_control");
        //         }else if (value.indexOf("x_cms_assemble_control")!==-1){
        //             value = value.replace("x_cms_assemble_control", host3+"/x_cms_assemble_control");
        //         }
        //         value = o2.filterUrl(value);
        //     }
        //     this.node.setStyle(key, value);
        // }.bind(this));
    },
    _parseStyles: function( styles ){
        var s = {};
        Object.each(styles || {}, function(v, key){
            var value = v.toString();
            if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1 || value.indexOf("x_cms_assemble_control")!=-1) && !value.includes("../")){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                var host3 = MWF.Actions.getHost("x_cms_assemble_control");
                if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                    value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                    value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface")!==-1){
                    value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                    value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
                if (value.indexOf("/x_cms_assemble_control")!==-1){
                    value = value.replace("/x_cms_assemble_control", host3+"/x_cms_assemble_control");
                }else if (value.indexOf("x_cms_assemble_control")!==-1){
                    value = value.replace("x_cms_assemble_control", host3+"/x_cms_assemble_control");
                }
                value = o2.filterUrl(value);
            }
            s[key] = value;
        }.bind(this));
        return s;
    },
    _loadModuleEvents : function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    this.node.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    this.node.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    addModuleEvent: function(key, fun){
        if (this.options.moduleEvents.indexOf(key)!==-1){
            this.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }else{
            this.node.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }
    },
    _getBusinessData: function(id){
        var v;
        if (this.json.section=="yes"){
            v = this._getBusinessSectionData(id);
        }else {
            if (this.json.type==="Opinion"){
                v = this._getBusinessSectionDataByPerson(id);
            }else{
                // return this.form.businessData.data[this.json.id] || "";
                var value = this.getBusinessDataById(null, id);
                return (o2.typeOf(value)!=="null") ? value : "";
                //return this.getBusinessDataById() || "";
            }
        }
        //if (o2.typeOf(v)==="string") v = o2.dtxt(v);
        return v;
    },
    _getBusinessSectionData: function(id){
        switch (this.json.sectionBy){
            case "person":
                return this._getBusinessSectionDataByPerson(id);
            case "unit":
                return this._getBusinessSectionDataByUnit(id);
            case "activity":
                return this._getBusinessSectionDataByActivity(id);
            case "splitValue":
                return this._getBusinessSectionDataBySplitValue(id);
            case "script":
                return this._getBusinessSectionDataByScript(((this.json.sectionByScript) ? this.json.sectionByScript.code : ""), id);
            default:
                // return this.form.businessData.data[this.json.id] || "";
                return this.getBusinessDataById(null, id) || "";
        }
    },
    _getBusinessSectionDataByPerson: function(id){
        this.form.sectionListObj[id||this.json.id] = layout.desktop.session.user.id;
        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById(null, id);
        return (dataObj) ? (dataObj[layout.desktop.session.user.id] || "") : "";
    },
    _getBusinessSectionDataByUnit: function(id){
        this.form.sectionListObj[id || this.json.id] = "";
        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById(null, id);
        if (!dataObj) return "";
        var key = (this.form.businessData.task) ? (this.form.businessData.task.unitDn || this.form.businessData.task.unit) : "";
        if (key) this.form.sectionListObj[id||this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByActivity: function(id){
        this.form.sectionListObj[id||this.json.id] = "";
        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById(null, id);
        if (!dataObj) return "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";
        if (key) this.form.sectionListObj[id||this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataBySplitValue: function(id){
        this.form.sectionListObj[id||this.json.id] = "";
        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById(null, id);
        if (!dataObj) return "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
        if (key) this.form.sectionListObj[id||this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByScript: function(code, id){
        this.form.sectionListObj[id||this.json.id] = "";
        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById(null, id);
        if (!dataObj) return "";
        var key = this.form.Macro.exec(code, this);
        if (key) this.form.sectionListObj[id||this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },
    _setEnvironmentData: function(v){
        if (this.json.section=="yes"){
            this._setEnvironmentSectionData(v);
        }else {
            if (this.json.type==="Opinion"){
                this._setEnvironmentSectionDataByPerson(v);
            }else{
                this.setEnvironmentDataById(v);
            }
        }
    },
    _setEnvironmentSectionData: function(v){
        switch (this.json.sectionBy){
            case "person":
                var key = layout.desktop.session.user.id;
                this._setEnvironmentSectionDataByKey(key, v);
                break;
            case "unit":
                var key = (this.form.businessData.task) ? (this.form.businessData.task.unitDn || this.form.businessData.task.unit) : "";
                this._setEnvironmentSectionDataByKey(key, v);
                break;
            case "activity":
                var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";
                this._setEnvironmentSectionDataByKey(key, v);
                break;
            case "splitValue":
                var key = (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
                this._setEnvironmentSectionDataByKey(key, v);
                break;
            case "script":
                var key = this.form.Macro.exec(this.json.sectionByScript.code, this);
                this._setEnvironmentSectionDataByKey(key, v);
                break;
            default:
                this.setEnvironmentDataById(v);
        }
    },
    _setEnvironmentSectionDataByKey: function(key, v){
        if (key){
            var evdata = this.getBusinessDataById(this.form.Macro.environment.data);
            var evdata;
            if (!evdata){
                evdata = this.setEnvironmentDataById({});
            }
            if (!evdata.hasOwnProperty(key)){
                evdata.add(key, v);
            }else{
                evdata[key] = v;
            }
        }
    },
    setEnvironmentDataById: function(v){
        //xx..0..xx 형태의 id를 가진 필드를 분리
        var evdata = this.form.Macro.environment.data;
        if(this.json.id.indexOf("..") < 1){
            if (!evdata.hasOwnProperty(this.json.id)){
                evdata.add(this.json.id, v);
            }else{
                evdata[this.json.id] = v;
            }

        }else{
            var idList = this.json.id.split("..");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            //var data = this.form.businessData.data;
            var lastIndex = idList.length - 1;

            for(var i=0; i<=lastIndex; i++){
                var id = idList[i];
                if( !id && id !== 0 )return;

                if( i === lastIndex ){
                    if (!evdata.hasOwnProperty(id)){
                        evdata.add(id, v);
                    }else{
                        evdata[id] = v;
                    }
                }else{
                    var nexId = idList[i+1];
                    if(o2.typeOf(nexId) === "number"){ //다음 ID가 숫자
                        if( !evdata[id] && o2.typeOf(evdata[id]) !== "array" ){
                            evdata.add(id, []);
                        }
                        if( nexId > evdata[id].length ){ //최대 인덱스 초과, 버림
                            return;
                        }
                    }else{ //다음 ID가 문자열
                        if( !evdata[id] || o2.typeOf(evdata[id]) !== "object"){
                            evdata.add(id, {});
                        }
                    }
                    evdata = evdata[id];
                }
            }
        }
        return evdata;
    },

    _checkDisplay: function(display){
        if (display){
            return Promise.resolve(display(this)).then((dsp)=>{
                dsp ? this.node.removeClass('hide') : this.node.addClass('hide');
                return dsp;
            });
        }
    },
    _checkValue: function(value){
        if (value){
            return Promise.resolve(value(this)).then((value)=>{
                this.setData?.(value);
                return value;
            });
        }
    },
    _setBusinessData: function(v, id){
        //if (o2.typeOf(v)==="string") v = o2.txt(v);
        if (!this.isEditable) return;
        if (this.json.section=="yes"){
            this._setBusinessSectionData(v, id);
        }else {
            if (this.json.type==="Opinion"){
                this._setBusinessSectionDataByPerson(v, id);
            }else{
                this.setBusinessDataById(v, id);
                if (this.json.isTitle && this.json.moduleName !== "associatedDocument") this.form.businessData.data.$work.title = v;
            }
        }

        this._checkAllRelated(id);
    },
    _checkAllRelated: function(id){
        const mid = (id || this.json.id);
        // const dataId = mid.split('..').map((s)=>{
        //     return /^\d+$/.test(s) ? '*' : s
        // }).join('.');
        const dataId = mid.split('..').join('.');

        if (this.form.relatedModules && this.form.relatedModules[dataId]){
            this.form.relatedModules[dataId].forEach((module)=>{
                module?.reload();
            })
        }
        if (this.form.relatedDisplayModules && this.form.relatedDisplayModules[dataId]){
            this.form.relatedDisplayModules[dataId].forEach((o)=>{
                o.module?._checkDisplay(o.display);
            })
        }
        if (this.form.relatedValueModules && this.form.relatedValueModules[dataId]){
            this.form.relatedValueModules[dataId].forEach((o)=>{
                o.module?._checkValue(o.value);
            })
        }
    },
    _setBusinessSectionData: function(v, id){
        switch (this.json.sectionBy){
            case "person":
                this._setBusinessSectionDataByPerson(v, id);
                break;
            case "unit":
                this._setBusinessSectionDataByUnit(v, id);
                break;
            case "activity":
                this._setBusinessSectionDataByActivity(v, id);
                break;
            case "splitValue":
                this._setBusinessSectionDataBySplitValue(v, id);
                break;
            case "script":
                this._setBusinessSectionDataByScript(this.json.sectionByScript.code, v, id);
                break;
            default:
                this.setBusinessDataById(v, id);
        }
    },
    _setBusinessSectionDataByPerson: function(v, id){
        var key = layout.desktop.session.user.id;
        this._setBusinessSectionDataByKey(key, v, id);
    },
    _setBusinessSectionDataByUnit: function(v, id){
        var key = (this.form.businessData.task) ? (this.form.businessData.task.unitDn || this.form.businessData.task.unit) : "";
        this._setBusinessSectionDataByKey(key, v, id);
    },
    _setBusinessSectionDataByActivity: function(v, id){
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";
        this._setBusinessSectionDataByKey(key, v, id);
    },
    _setBusinessSectionDataBySplitValue: function(v, id){
        var key = (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
        this._setBusinessSectionDataByKey(key, v, id);
    },
    _setBusinessSectionDataByScript: function(code, v, id){
        var key = this.form.Macro.exec(code, this);
        this._setBusinessSectionDataByKey(key, v, id);
    },
    _setBusinessSectionDataByKey: function(key, v, id){
        if (key){
            var dataObj = this.getBusinessDataById(null, id);
            var evdata;
            if (!dataObj){
                dataObj = {};
                evdata = this.setBusinessDataById(dataObj, id);
            }
            dataObj[key] = v;
            if (evdata) evdata.check(key, v);
        }
    },
    getBusinessDataById: function(d, id){
        var data = d || this.form.businessData.data;
        var thisId = id || this.json.id;
        //xx..0..xx 형태의 id를 가진 필드를 분리
        if(thisId.indexOf("..") < 1){
            return data[thisId];
        }else{
            var idList = thisId.split("..");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            var lastIndex = idList.length - 1;

            for(var i=0; i<=lastIndex; i++){
                var id = idList[i];
                if( !id && id !== 0 )return null;
                if( ["object","array"].contains(o2.typeOf(data)) ){
                    if( i === lastIndex ){
                        return data[id];
                    }else{
                        data = data[id];
                    }
                }else{
                    return null;
                }
            }
        }
    },
    _checkEvdata: function(evdata, id, v){
        switch (o2.typeOf(evdata)){
            case "array":

                break;
            default:
                evdata.check(id, v);
        }
    },
    setBusinessDataById: function(v, id){
        //xx..0..xx 형태의 id를 가진 필드를 분리
        var evdata = this.form.Macro.environment.data;
        var data = this.form.businessData.data;
        var thisId = id || this.json.id;
        if(thisId.indexOf("..") < 1){
            data[thisId] = v;
            this._checkEvdata(evdata, thisId, v);
            //this.form.businessData.data[this.json.id] = v;
        }else{
            var idList = thisId.split("..");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            //var data = this.form.businessData.data;
            var lastIndex = idList.length - 1;

            for(var i=0; i<=lastIndex; i++){
                var id = idList[i];
                if( !id && id !== 0 )return;

                if( i === lastIndex ){
                    data[id] = v;
                    //evdata.check(id, v);
                    this._checkEvdata(evdata, id, v);
                }else{
                    var nexId = idList[i+1];
                    if(o2.typeOf(nexId) === "number"){ //다음 ID가 숫자
                        if( !data[id] && o2.typeOf(data[id]) !== "array" ){
                            data[id] = [];
                            //evdata.check(id, []);
                            this._checkEvdata(evdata, id, []);
                        }
                        if( nexId > data[id].length ){ //최대 인덱스 초과, 버림
                            return;
                        }
                    }else{ //다음 ID가 문자열
                        if( !data[id] || o2.typeOf(data[id]) !== "object"){
                            data[id] = {};
                            //evdata.check(id, {});
                            this._checkEvdata(evdata, id, {});
                        }
                    }
                    data = data[id];
                    evdata = evdata[id];
                }
            }
        }
        return evdata;
    },


    getOriginalDataById: function(d, id){
        var data = d || this.form.businessData.originalData;
        var thisId = id || this.json.id;
        //xx..0..xx 형태의 id를 가진 필드를 분리
        if(thisId.indexOf("..") < 1){
            return data[thisId];
        }else{
            var idList = thisId.split("..");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            var lastIndex = idList.length - 1;

            for(var i=0; i<=lastIndex; i++){
                var id = idList[i];
                if( !id && id !== 0 )return null;
                if( ["object","array"].contains(o2.typeOf(data)) ){
                    if( i === lastIndex ){
                        return data[id];
                    }else{
                        data = data[id];
                    }
                }else{
                    return null;
                }
            }
        }
    },
    setOriginalDataById: function(v, id){
        //xx..0..xx 형태의 id를 가진 필드를 분리
        var data = this.form.businessData.originalData;
        var thisId = id || this.json.id;
        if(thisId.indexOf("..") < 1){
            data[thisId] = v;
        }else{
            var idList = thisId.split("..");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            //var data = this.form.businessData.data;
            var lastIndex = idList.length - 1;

            for(var i=0; i<=lastIndex; i++){
                var id = idList[i];
                if( !id && id !== 0 )return;

                if( i === lastIndex ){
                    data[id] = v;
                }else{
                    var nexId = idList[i+1];
                    if(o2.typeOf(nexId) === "number"){ //다음 ID가 숫자
                        if( !data[id] && o2.typeOf(data[id]) !== "array" ){
                            data[id] = [];
                        }
                        if( nexId > data[id].length ){ //최대 인덱스 초과, 버림
                            return;
                        }
                    }else{ //다음 ID가 문자열
                        if( !data[id] || o2.typeOf(data[id]) !== "object"){
                            data[id] = {};
                        }
                    }
                    data = data[id];
                }
            }
        }
    },

    saveDataById: function(fieldId, data){
        if(this.form.isReadonly()){
            return;
        }
        var clone = function (data){
            switch(o2.typeOf(data)){
                case "object": return Object.clone(data);
                case "array": return Array.clone(data);
                default: return data;
            }
        };
        var appName = this.form.app.options.name;
        if( !['process.Work', 'cms.Document'].includes(appName) ){
            return;
        }
        if(appName === 'process.Work' && this.form.isDraftWork()){
            return;
        }
        if(appName === 'process.Work' && !this.form.businessData.work.dataChanged){
            return;
        }
        var originalData = this.form.businessData.originalData || {};
        var thisId = fieldId || this.json.id;
        if(o2.typeOf(data) === "null"){
            data = this.getBusinessDataById();
        }
        if(thisId.indexOf("..") < 1){
            this._saveDataByPath([thisId], data, ()=>{
                originalData[thisId] = clone( data );
            });
        }else{
            var idList = thisId.split("..");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            var lastIndex = idList.length - 1;
            var preOriginalData = originalData;

            for(var i=0; i<=lastIndex; i++){
                var id = idList[i];
                if( !id && id !== 0 )return;

                var exist = originalData && originalData.hasOwnProperty(id);
                if( !exist || i === 8 ) { //originalData는 중간 경로를 포함하지 않으며 경로 길이는 최대 8까지 지원, 초과분은 이후 전체 데이터를 가져와 저장
                    var paths = idList.slice(0, i+1);
                    var pathData = this.getBusinessDataById(null, paths.join('..'));
                    this._saveDataByPath(paths, pathData, ()=>{
                        !!preOriginalData && (preOriginalData[idList[i]] = clone(pathData));
                    });
                    return;
                }else if( i === lastIndex ) {
                    this._saveDataByPath(idList, data, ()=>{
                        originalData[id] = clone(data);
                    });
                }else{
                    preOriginalData = originalData;
                    originalData = originalData[id];
                }
            }
        }
    },
    _saveDataByPath: function(paths, data, success){
        if(o2.typeOf(data) === "null"){
            data = '';
        }
        if (paths.length > 8) {
            throw new Error(`경로 "${paths.join('/')}"의 단계가 제한(8단계)을 초과했습니다. 현재: ${paths.length}`);
        }
        var hasPath = paths.length > 0;
        var action, methodName, args;
        switch (this.form.app.options.name){
            case 'process.Work':
                action = o2.Actions.load('x_processplatform_assemble_surface').DataAction;
                methodName = hasPath ? `updateWithWorkPath${paths.length-1}` : 'updateWithWork';
                args = [this.form.businessData.work.id];
                break;
            case 'cms.Document':
                action = o2.Actions.load('x_cms_assemble_control').DataAction;
                methodName = hasPath ? `updateWithDocumentWithPath${paths.length-1}` : 'updateWithDocument';
                args = [this.form.businessData.document.id];
                break;
            default:
                return;
        }
        paths = paths.map(p => p.toString());

        if (hasPath) {
            args = args.concat(paths);
        }
        args = args.concat([data, success, null, false]);

        return action[methodName](...args);
    },

    setValue: function(){
    },
    focus: function(){
        this.node.focus();
    },

    _getModuleByPath: function( path ){
        /*
        참고: 시스템 데이터는 다층 경로를 허용하며, id에서 ..으로 계층을 구분:
        1. 단일 계층 또는 최상위 계층은 "fieldId"로 입력하며, 양식의 직접 컴포넌트를 의미.
        2. 다층 데이터 템플릿이면 "./fieldId"는 현재 컴포넌트 id와 같은 계층의 컴포넌트, "../fieldId"는 상위 컴포넌트와 같은 계층의 컴포넌트를 의미하며, 이런 식으로 이어짐.
        3. 다층 데이터 템플릿이면 "datatemplateId.*.datatemplateId2.*.fieldId"로 전체 계층 경로를 표현할 수도 있음. datatemplateId는 첫 번째 계층 데이터 템플릿의 id, datatemplateId2는 두 번째 계층의 id.
         */
        if(!path)return;
        var idList = this.json.id.split("..");
        if( path.contains("*") ){ //path에 *를 허용하며 현재 path의 계층으로 대체
            var paths = path.split(".");
            for( var i=0; i<paths.length; i++ ){
                if( paths[i].contains("*") && idList[i] ){
                    var key = paths[i].replace("*", idList[i]);
                    key = this.form.Macro.exec("return "+key, this);
                    paths[i] = (key||"").toString();
                }
            }
            path = paths.join("..");
        }else if( path.contains("./") ){

            var lastName = path.substring(path.indexOf("./")+2, path.length);
            var level = (path.substring(0, path.indexOf("./"))+".").split(".").length-1; // /앞에 있는 . 개수

            var idList_copy = Array.clone(idList);
            if( idList_copy.length > level*2 ){
                for( var i=0; i<level; i++ ){
                    idList_copy.pop();
                    if( i > 0)idList_copy.pop();
                }
                path = idList_copy.join("..")+".."+lastName;
            }else{
                idList_copy[idList_copy.length-1] = lastName;
                path = idList_copy.join("..")
            }
        }

        return this.form.all[path];
    },
    clearDefaultMargin: function (){
        var marginStyle = this.node.getStyle('margin-right');
        var paddingStyle = this.node.getStyle('padding-right');
        if( marginStyle === '20px' || paddingStyle === '4px' ){
            var map = {};
            Object.each(this.json.recoveryStyles || {}, function(value, key){
                var k = key.toLowerCase();
                if( ['margin','margin-right','padding', 'padding-right'].contains(k) ){
                    map[k] = value;
                }
            });
            if( marginStyle === '20px' && map['margin'] !== '20px' && map['margin-right'] !== '20px') {
                this.node.setStyle('margin-right', '0');
            }
            if( paddingStyle === '4px' && map['padding'] !== '4px' && map['padding-right'] !== '4px') {
                this.node.setStyle('padding-right', '0');
            }
        }
    },
    _loadOONodeDownloading: function (){
        if(!this.node.offsetParent){
            //this.node.destroy();
            return;
        }
        this.node.setStyle('display', 'none');
        let valueNode, labelNode;
        const node = new Element('div.oo-node-downloading', {
            'id': this.json.id,
            'MWFType': this.json.type
        }).inject(this.node, 'after');
        Array.from(this.node.classList).forEach(clazz=>{
            node.addClass(clazz);
        })
        this.node.destroy();

        node.setStyles(this.form.css.OODownloadingNodeStyle);

        if (this.json.styles) {
            node.setStyles(this.json.styles);
        }

        if (this.json.label) {
            labelNode = new Element('div.item-label',{
                text: this.json.label
            }).inject(node);
        }

        valueNode = new Element('div').inject(node);

        if(this.json.properties){
            if(labelNode) {
                if(this.json.properties['label-style']){
                    labelNode.setAttribute('style', this.json.properties['label-style']);
                }
                if(this.json.properties['label-Align']){
                    let justifyContent;
                    switch (this.json.properties['label-Align']) {
                        case 'right': justifyContent = 'flex-end'; break;
                        case 'center': justifyContent = 'center';  break;
                        default: justifyContent = 'flex-start'; break;
                    }
                    labelNode.setStyle('justify-content', justifyContent);
                }
            }
            if(!this.json.inDatatable && this.json.properties['view-style']) {
                valueNode.setAttribute('style', this.json.properties['view-style']);
            }
        }
        if(labelNode){
            labelNode.setStyles(this.form.css.OODownloadingLabelNodeStyle);
        }
        valueNode.setStyles(this.form.css.OODownloadingValueNodeStyle);

        this.downloadingNode = node;
        this.downloadingLabelNode = labelNode;
        this.downloadingValueNode = valueNode;

        this._afterLoadOONodeDownloading();
    },
    _afterLoadOONodeDownloading: function (){
    }
});
