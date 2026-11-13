MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);

/** @class ReadLog 열람 기록 컴포넌트.
 * @o2cn 열람 기록 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var log = this.form.get("name"); //컴포넌트 조회
 * //방법 2
 * var log = this.target; //컴포넌트 이벤트 스크립트에서 조회
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.ReadLog = MWF.APPReadLog =  new Class(
    /** @lends MWF.xApplication.process.Xform.ReadLog# */
    {
        Extends: MWF.APP$Module,
        options: {
            /**
             * 데이터 로드 후 이벤트.
             * @event MWF.xApplication.process.Xform.ReadLog#postLoadData
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             * @example
             * //이 이벤트 발생 시 프로세스 데이터 ReadLog를 가져올 수 있음
             * var readLog = this.target.readLog;
             * //readLog를 수정하여 프로세스 기록을 맞춤화할 수 있음
             * do something
             */

            /**
             * 각 행의 프로세스 정보를 로드한 후 발생, this.event로 다음 정보를 가져올 수 있음:
             * <pre><code>
             *{
             * "data" : {}, //현재 행 기록 정보, 열람 완료이면 단일 기록(객체), 열람 대기이면 여러 기록(배열)
             * "node" : node, //현재 노드
             * "log" : object, //열람 기록을 가리킴
             * "type" : "readCompleted"  //"read"는 열람 대기, "readCompleted"는 열람 완료
             *}
             </code></pre>
             * @event MWF.xApplication.process.Xform.ReadLog#postLoadLine
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             */
            "moduleEvents": ["load", "queryLoad", "postLoad", "postLoadData", "postLoadLine"]
        },
        load: function(){
            this._queryLoaded();
            this.node.empty();

            if (!this.json.isDelay){
                this.active();
            }
        },
        /**
         * 열람 기록 활성화. 지연 로드가 설정된 경우 이 메서드로 활성화할 수 있음
         * @example
         * this.form.get("fieldId").active();
         */
        active: function(){
            if (!this.isReadable){
                this.node?.addClass('hide');
                return '';
            }
            this._loadModuleEvents();
            if (this.fireEvent("queryLoad")){
                this._queryLoaded();
                this._loadUserInterface();
                this._loadStyles();
                this._loadDomEvents();
                //this._loadEvents();

                this._afterLoaded();
                this.fireEvent("postLoad");
                this.fireEvent("load");
            }
        },
        _loadUserInterface: function(){
            this.node.setStyle("-webkit-user-select", "text");
            this.node.setStyles(this.form.css.logActivityNode_read);

            this.form.app.action.getReadRecord(this.form.businessData.work.id, function(json){
                this.readLog = json.data;
                this.fireEvent("postLoadData");
                this.loadReadLog();
            }.bind(this));
        },
        loadReadLog: function(){
            switch (this.json.mode){
                case "notShow":
                    return "";
                case "script":
                    this.loadReadLogByScript();
                    break;
                default:
                    this.loadReadLogDefault();
            }
        },
        loadReadLogByScript: function(){
            if (this.json.displayScript && this.json.displayScript.code){
                var code = this.json.displayScript.code;
                this.readLog.each(function(log){
                    if (!this.json.isShowRead && log.type === "read"){
                        //열람 대기 표시 안 함
                    }else{
                        this.form.Macro.environment.log = log;
                        this.form.Macro.environment.list = null;
                        var r = this.form.Macro.exec(code, this);
                        var t = o2.typeOf(r);
                        var lineNode;
                        if (t==="string"){
                            lineNode = new Element("div", {
                                html: r
                            }).inject(this.node);
                            //this.node.appendHTML(r);
                        }else if (t==="element"){
                            lineNode = this.node.appendChild(r);
                        }
                        this.fireEvent("postLoadLine",[{
                            "data" : log,
                            "node" : lineNode,
                            "log" : this,
                            "type" : "readCompleted"
                        }]);
                    }

                }.bind(this));
            }
        },
        loadReadLogDefault: function(){
            var text = this.json.textStyle;
            var readPersons = [];
            var readLogs = [];
            this.lineClass = "logTaskNode";
            this.readLog.each(function(log, i){

                if (this.json.textStyleScript && this.json.textStyleScript.code){
                    this.form.Macro.environment.log = log;
                    this.form.Macro.environment.list = null;
                    text = this.form.Macro.exec(this.json.textStyleScript.code, this);
                }

                if (log.type == "readCompleted"){
                    var div = new Element("div", {styles: this.form.css[this.lineClass]}).inject(this.node);
                    var leftDiv = new Element("div", {styles: this.form.css.logTaskIconNode_record}).inject(div);
                    var rightDiv = new Element("div", {styles: this.form.css.logTaskTextNode}).inject(div);
                    var html = text.replace(/{person}/g, o2.name.cn(log.person || ""))
                        .replace(/{datetime}/g, log.completedTime)
                        .replace(/{date}/g, log.completedTime.substring(0.10))
                        .replace(/{startDatetime}/g, log.startTime)
                        .replace(/{startDate}/g, log.startTime.substring(0.10))
                        .replace(/{startDatetime}/g, log.startTime)
                        .replace(/{unit}/g, o2.txt(o2.name.cn(log.unit)))
                        .replace(/{department}/g, o2.txt(o2.name.cn(log.unit)))
                        .replace(/{identity}/g, o2.name.cn(log.identity))
                        .replace(/{activity}/g, o2.txt(o2.name.cn(log.activityName)))
                        .replace(/{title}/g, o2.txt(log.title) || "")
                        .replace(/{opinion}/g, o2.txt(log.opinion) || "");
                    rightDiv.appendHTML(html);

                    if (this.lineClass === "logTaskNode"){
                        this.lineClass = "logTaskNode_even";
                    }else{
                        this.lineClass = "logTaskNode";
                    }

                    this.fireEvent("postLoadLine",[{
                        "data" : log,
                        "node" : div,
                        "log" : this,
                        "type" : "readCompleted"
                    }]);
                }
                if (!!this.json.isShowRead && log.type == "read"){
                    readPersons.push(o2.name.cn(log.person)+"("+o2.name.cn(log.unit)+")");
                    readLogs.push(log);
                }
            }.bind(this));
            if (readPersons.length){
                var div = new Element("div", {styles: this.form.css[this.lineClass]}).inject(this.node);
                var leftDiv = new Element("div", {styles: this.form.css.logTaskIconNode_record}).inject(div);
                var rightDiv = new Element("div", {styles: this.form.css.logTaskTextNode}).inject(div);
                leftDiv.setStyle("background-image", "url("+"../x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
                rightDiv.appendHTML("<div><font style='font-weight: bold'>"+MWF.xApplication.process.Xform.LP.showReadTitle+": </font><font style='color: #00F'>"+readPersons.join(", ")+"</font></div>");
                this.fireEvent("postLoadLine",[{
                    "data" : readLogs,
                    "node" : div,
                    "log" : this,
                    "type" : "read"
                }]);
            }
        }
    });
