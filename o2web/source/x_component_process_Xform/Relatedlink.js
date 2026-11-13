MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Relatedlink 관련 추천 컴포넌트, 현재 프로세스 인스턴스와의 관련성 점수에 따라 다른 프로세스 인스턴스를 나열(권한 포함).
 * @o2cn 관련 추천 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var relatedlink = this.form.get("relatedlink"); //컴포넌트 가져오기
 * //방법 2
 * var relatedlink = this.target; //컴포넌트 자체 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @since v7.3
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Relatedlink = MWF.APPRelatedlink =  new Class(
    /** @lends MWF.xApplication.process.Xform.Relatedlink# */
    {
	Implements: [Events],
    Extends: MWF.xApplication.process.Xform.$Module,
    options: {
        /**
         * 데이터 로드 후 이벤트.
         * @event MWF.xApplication.process.Xform.Relatedlink#postLoadData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         * @example
         * //이 이벤트 발생 시 링크 데이터 linkData를 가져올 수 있음
         * var linkData = this.target.linkData;
         * //linkData를 수정하여 링크 데이터를 맞춤화할 수 있음
         * do something
         */
        /**
         * 가져올 각 행의 데이터를 생성한 후 발생, this.event는 현재 링크 객체를 가리킴.
         * @event MWF.xApplication.process.Xform.Relatedlink#postLoadLink
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         * @example
         * <caption>this.event 형식은 다음과 같음:</caption>
         * {
         *  node: DomElement,
         *  data: {
         *   "title", "제목1",
         *    ...
         *  }
         * }
         */
        "moduleEvents": ["queryLoad","postLoad", "afterLoad", "postLoadData", "postLoadLink"]
    },
    _loadUserInterface: function(){
        this.node.empty();

        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        this.node.setStyle("-webkit-user-select", "text");

        switch (this.json.activeType) {
            case "click":
                this.loadButton();
                break;
            case "delay":
                break;
            case "immediately":
                this.loadContent();
                break;
        }
    },
    loadButton: function(){
        this.buttonNode = new Element("div.rlbutton", {
            "styles": this.json.buttonStyles || {},
            "text": this.json.buttonText,
            "events":{
                click: function () {
                    this.active();
                    this.buttonNode.destroy();
                    this.buttonNode = null;
                }.bind(this)
            }
        }).inject( this.node );
    },
    /**
     * @summary 관련 추천이 지연 활성화로 설정된 경우 active 메서드로 활성화
     * @param {Function} callback 활성화 후 콜백
     * @example
     * var relativeLink = this.form.get("fieldId");
     * relativeLink.active(function(){
     *     //do someting
     * })
     */
    active: function( callback ){
	    if( !this.loaded ){
	        this.loadContent( callback );
	    }else{
	        if(callback)callback();
        }
    },
    loadContent: function( callback ){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }
        o2.Actions.load("x_query_assemble_surface").MoreLikeThisAction.post({
            flag: this.getFlag(),
            category: this.getCategory(),
            count: (this.json.count || 6).toInt()
        }, function(json){
            this.linkData = json.data.moreLikeThisList;
            this.fireEvent("postLoadData");
            this.loadLinks();
            this.loaded = true;
            this.fireEvent("afterLoad");
            if(callback)callback();
        }.bind(this));
    },
    getFlag: function(){
        return this.form.businessData.work.id;
        // if (this.form.businessData.work && !this.form.businessData.work.completedTime) {
        //     return this.form.businessData.work.id;
        // } else {
        //     return this.form.businessData.workCompleted.id;
        // }
    },
    getCategory: function(){
        return "processPlatform";
    },
    loadLinks: function(){
        switch (this.json.mode){
            case "script":
                this.loadLinkByScript();
                break;
            case "text":
                this.loadLinkDefault();
                break;
            default:
                this.loadLinkTable();
        }
    },
    loadLinkTable: function(){
        var table = new Element("table", {
            "styles": this.json.tableStyles || this.form.css.relatedlinkTable,
            "border": "0",
            "cellSpacing": "0",
            "cellpadding": "3px",
            "width": "100%"
        }).inject(this.node);
        var tr = table.insertRow(0); //.setStyles( this.form.css.relatedlinkTableTitleLine );

        var lp = MWF.xApplication.process.Xform.LP;
        var thStyle = this.json.tableTitleCellStyles || this.form.css.relatedlinkTableTitle;
        var td = tr.insertCell(0).setStyles(thStyle);
        td.set("text", lp.title);
        td = tr.insertCell(1).setStyles(thStyle);
        td.set("text", lp.creatorPerson);
        td = tr.insertCell(2).setStyles(thStyle);
        td.set("text", lp.creatorUnit);
        td = tr.insertCell(3).setStyles(thStyle);
        td.set("text", lp.createTime);
        td = tr.insertCell(4).setStyles(thStyle);
        td.set("text", lp.updateTime);
        td = tr.insertCell(5).setStyles(thStyle);
        td.set("text", lp.score);

        var tdStyle = this.json.tableContentCellStyles || this.form.css.relatedlinkTableCell;
        this.linkData.each(function (log, idx) {
            var tr = table.insertRow(table.rows.length);
            tr.setStyles( this.json.tableContentLineStyles || this.form.css.relatedlinkTableLine );
            tr.addEvents({
                "mouseover": function () {
                    tr.setStyles( this.json.tableContentLineStyles_over || this.form.css.relatedlinkTableLine_over )
                }.bind(this),
                "mouseout": function () {
                    tr.setStyles( this.json.tableContentLineStyles || this.form.css.relatedlinkTableLine )
                }.bind(this)
            })

            var td = tr.insertCell(0).setStyles( tdStyle );
            td.set("text", log.title);
            td.setStyles( this.json.tableContentCellStyles_title || this.form.css.relatedlinkTableCell_title );

            td = tr.insertCell(1).setStyles(tdStyle);
            td.set("text", log.creatorPerson);
            td = tr.insertCell(2).setStyles(tdStyle);
            td.set("text", log.creatorUnit);
            td = tr.insertCell(3).setStyles(tdStyle);
            td.set("text", log.createTime);
            td = tr.insertCell(4).setStyles(tdStyle);
            td.set("text", log.updateTime);
            td = tr.insertCell(5).setStyles(tdStyle);
            td.set("text", log.score);

            this.setOpenEvent(tr, log);

            this.fireEvent("postLoadLink", [{
                node: tr,
                data: log
            }]);
        }.bind(this))
    },
    loadLinkByScript: function(){
        if (this.json.displayScript && this.json.displayScript.code){
            var code = this.json.displayScript.code;
            this.linkData.each(function(log){
                this.form.Macro.environment.link = log;
                var r = this.form.Macro.exec(code, this);

                var t = o2.typeOf(r);
                if (t==="string"){
                    this.node.appendHTML(r);
                }else if (t==="element"){
                    this.node.appendChild(r);
                }
                var node = this.node.getLast();
                this.setOpenEvent(node, log);
                this.fireEvent("postLoadLink", [{
                    node: node,
                    data: log
                }]);
            }.bind(this));
        }
    },
    loadLinkDefault: function(){
        var text = this.json.textStyle;
        var readPersons = [];
        this.lineClass = "relatedlinkNode";
        this.linkData.each(function(log, i){
            var div = new Element("div", {styles: this.form.css[this.lineClass]}).inject(this.node);
            var rightDiv = new Element("div", {styles: this.form.css.relatedlinkTextNode}).inject(div);
            var html = text.replace(/{flag}/g, log.flag)
                .replace(/{createDate}/g, log.createTime.substring(0.10))
                .replace(/{createTime}/g, log.createTime)
                .replace(/{updateDate}/g, log.updateTime.substring(0.10))
                .replace(/{updateTime}/g, log.updateTime)
                .replace(/{creatorUnit}/g, o2.name.cn(log.creatorUnit))
                .replace(/{creatorPerson}/g, o2.name.cn(log.creatorPerson))
                .replace(/{title}/g, log.title || "");
            rightDiv.appendHTML(html);

            if (this.lineClass === "relatedlinkNode"){
                this.lineClass = "relatedlinkNode_even";
            }else{
                this.lineClass = "relatedlinkNode";
            }
            this.setOpenEvent(div, log);
            this.fireEvent("postLoadLink", [{
                node: div,
                data: log
            }]);
        }.bind(this));
    },
    setOpenEvent: function(node, data){
        node.setStyle("cursor", "pointer");
        if (data.category==="cms"){
            node.addEvent("click", function(ev){
                this.openCMSDocument( data, false);
                ev.stopPropagation();
            }.bind(this));
        }else{
            node.addEvent("click", function(ev){
                this.openWork(data);
                ev.stopPropagation();
            }.bind(this));
        }
    },
    openWork: function(data){
        var appId = "process.Work"+data.flag;
        // if (layout.desktop.apps[appId]){
        //     if (!layout.desktop.apps[appId].window){
        //         layout.desktop.apps[appId] = null;
        //         layout.openApplication(null, layout.desktop.apps[appId].options.name, layout.desktop.apps[appId].options, layout.desktop.apps[appId].options.app, false, this, false);
        //     }else{
        //         layout.desktop.apps[appId].setCurrent();
        //     }
        // }else {
            var op = {
                "jobId": data.flag,
                "appId": appId
            };
            return layout.desktop.openApplication(this.event, "process.Work", op);
        // }
    },
    openCMSDocument : function( data, isEdited ){
        var appId = "cms.Document"+data.flag;
        // if (layout.desktop.apps[appId]){
        //     if (!layout.desktop.apps[appId].window){
        //         layout.desktop.apps[appId] = null;
        //         layout.openApplication(null, layout.desktop.apps[appId].options.name, layout.desktop.apps[appId].options, layout.desktop.apps[appId].options.app, false, this, false);
        //     }else{
        //         layout.desktop.apps[appId].setCurrent();
        //     }
        // }else {
            var options = {
                "documentId": data.flag,
                "appId": appId,
                "readonly" : !isEdited
            };
            layout.desktop.openApplication(null, "cms.Document", options);
        // }
    }
	
}); 
