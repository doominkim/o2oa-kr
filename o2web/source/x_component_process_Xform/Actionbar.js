MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.require("MWF.widget.Tree", null, false);
//MWF.require("MWF.widget.Toolbar", null, false);

/** @class Actionbar 작업 막대 컴포넌트.
 * @o2cn 작업 막대
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var actionbar = this.form.get("name"); //작업 막대 가져오기
 * //방법 2
 * var actionbar = this.target; //작업 막대와 작업 자체의 이벤트 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Actionbar = MWF.APPActionbar =  new Class(
    /** @lends MWF.xApplication.process.Xform.Actionbar# */
    {
        Extends: MWF.APP$Module,
        options: {
            /**
             * 컴포넌트 로드 전 발생. 현재 컴포넌트의 queryLoad 이벤트 시점에는 form에 등록되지 않아 this.form.get("fieldId")로 가져올 수 없으며, this.target으로 현재 컴포넌트를 가져와야 함.
             * @event MWF.xApplication.process.Xform.Actionbar#queryLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             */
            /**
             * 컴포넌트 로드 시 발생.
             * @event MWF.xApplication.process.Xform.Actionbar#load
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             */
            /**
             * 컴포넌트 로드 후 이벤트. 로드 과정에 비동기 처리가 있어 이 시점에는 작업 막대가 아직 생성되지 않았을 수 있음.
             * @event MWF.xApplication.process.Xform.Actionbar#postLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             */
            /**
             * 컴포넌트 로드 후 이벤트. 이 시점에는 작업 막대가 생성됨
             * @event MWF.xApplication.process.Xform.Actionbar#afterLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             */
            "moduleEvents": ["load", "queryLoad", "postLoad", "afterLoad"]
        },
        /**
         * @summary 작업 막대 다시 로드. afterLoad 이벤트가 발생함
         * @example
         * this.form.get("name").reload(); //작업 막대 표시
         */
        reload : function(){
            this._loadUserInterface();
        },
        _loadUserInterface: function(){
            // if (this.form.json.mode == "Mobile"){
            //     this.node.empty();
            // }else if (COMMON.Browser.Platform.isMobile){
            //     this.node.empty();
            // }else{
            if (!this.isReadable){
                this.node?.addClass('hide');
                return '';
            }


            this.toolbarNode = this.node.getFirst("div");
            if(!this.toolbarNode)return;

            if(this.downloading){
                this.node.style.setProperty('display', 'none', 'important');
                return;
            }

            this.toolbarNode.empty();

            MWF.require("MWF.widget.Toolbar", function(){
                /**
                 * @summary Toolbar 컴포넌트, 플랫폼은 이 컴포넌트로 작업 막대를 생성.
                 * @member {o2.widget.Toolbar}
                 * @example
                 *  //스크립트에서 이 컴포넌트를 가져올 수 있음
                 * var toolbarWidget = this.form.get("fieldId").toolbarWidget; //컴포넌트 객체 가져오기
                 */
                this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {
                    "style": this.json.style,
                    "onPostLoad" : function(){
                        this.fireEvent("afterLoad");
                    }.bind(this)
                }, this);
                if (this.json.actionStyles) this.toolbarWidget.css = this.json.actionStyles;
                //alert(this.readonly)

                if( this.json.multiTools ){ //사용자 정의 작업과 시스템 작업이 섞인 경우 system : true로 시스템과 사용자 정의를 구분
                    var addReadActionFlag = !this.json.hideSystemTools && !this.json.hideReadedAction; //열람 완료 추가 필요 여부
                    var jsonStr = JSON.stringify(this.json.multiTools);
                    jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.process.Xform.LP.form});
                    this.json.multiTools = JSON.parse(jsonStr);
                    this.json.multiTools.each( function (tool) {
                        if( tool.system ){
                            if( !this.json.hideSystemTools ){
                                if( tool.id === "action_readed" )addReadActionFlag = false;
                                this.setToolbars([tool], this.toolbarNode, this.readonly);
                            }
                        }else{
                            this.setCustomToolbars([tool], this.toolbarNode);
                        }
                    }.bind(this));
                    if( addReadActionFlag ){
                        var addActions = [
                            {
                                "type": "MWFToolBarButton",
                                "img": "read.png",
                                "title": MWF.xApplication.process.Xform.LP.setReaded,
                                "action": "readedWork",
                                "text": MWF.xApplication.process.Xform.LP.readed,
                                "id": "action_readed",
                                "control": "allowReadProcessing",
                                "condition": "",
                                "read": true
                            }
                        ];
                        this.setToolbars(addActions, this.toolbarNode, this.readonly);
                    }
                    this.toolbarWidget.load();
                }else{
                    if (this.json.hideSystemTools){
                        this.setCustomToolbars(this.json.tools, this.toolbarNode);
                        this.toolbarWidget.load();
                    }else{
                        if (this.json.defaultTools){
                            var addActions = [
                                {
                                    "type": "MWFToolBarButton",
                                    "img": "read.png",
                                    "title": MWF.xApplication.process.Xform.LP.setReaded,
                                    "action": "readedWork",
                                    "text": MWF.xApplication.process.Xform.LP.readed,
                                    "id": "action_readed",
                                    "control": "allowReadProcessing",
                                    "condition": "",
                                    "read": true
                                }
                            ];
                            //this.form.businessData.control.allowReflow =

                            //this.json.defaultTools.push(o);
                            this.setToolbars(this.json.defaultTools, this.toolbarNode, this.readonly);
                            if( !this.json.hideReadedAction ){
                                this.setToolbars(addActions, this.toolbarNode, this.readonly);
                            }

                            this.setCustomToolbars(this.json.tools, this.toolbarNode);
                            this.toolbarWidget.load();
                        }else{
                            MWF.getJSON(this.form.path+"toolbars.json", function(json){
                                this.setToolbars(json, this.toolbarNode, this.readonly, true);
                                this.setCustomToolbars(this.json.tools, this.toolbarNode);
                                this.toolbarWidget.load();
                            }.bind(this), null);
                        }
                    }
                }

                if (this.toolbarWidget.items["action_goBack"]){
                    // 반려 가능한 활동이 있는지 비동기 판단
                    if (this.form.businessData.task) o2.Actions.load('x_processplatform_assemble_surface').WorkAction.V2ListActivityGoBack(this.form.businessData.task.work, function(json){
                        if (json.data.length){
                            this.toolbarWidget.items["action_goBack"].show();
                        }
                    }.bind(this));
                }

            }.bind(this));
        },

        setCustomToolbars: function(tools, node){
            var path = "../x_component_process_FormDesigner/Module/Actionbar/";
            var iconPath = "";
            if( this.json.customIconStyle ){
                iconPath = this.json.customIconStyle+"/";
            }
            tools.each(function(tool){
                var flag = true;
                if (this.readonly){
                    flag = tool.readShow;
                }else{
                    flag = tool.editShow;
                }
                if (flag){
                    flag = true;
                    if (tool.control){
                        flag = this.form.businessData.control[tool.control]
                    }
                    if (tool.condition){
                        var hideFlag = this.form.Macro.exec(tool.condition, this);
                        flag = !hideFlag;
                    }
                    if (flag){
                        var actionNode = new Element("div", {
                            "id": tool.id,
                            "MWFnodetype": tool.type,
                            "MWFButtonImage": this.json.iconType==="font" ? "" : path+""+this.form.options.style+"/custom/"+iconPath+tool.img,
                            "MWFButtonIcon": tool.icon,
                            "title": tool.title,
                            "MWFButtonAction": "runCustomAction",
                            "MWFButtonText": tool.text
                        }).inject(node);
                        if( this.json.customIconOverStyle ){
                            actionNode.set("MWFButtonImageOver" , path+""+this.form.options.style +"/custom/"+this.json.customIconOverStyle+ "/" +tool.img );
                        }
                        if( tool.properties ){
                            actionNode.set(tool.properties);
                        }
                        if (tool.actionScript){
                            actionNode.store("script", tool.actionScript);
                        }
                        if (tool.sub){
                            var subNode = node.getLast();
                            this.setCustomToolbars(tool.sub, subNode);
                        }
                    }
                }
            }.bind(this));
        },

        getImagePath: function(img, iscustom){
            var path = "../x_component_process_FormDesigner/Module/Actionbar/";
            if( iscustom ){
                var iconPath = this.json.customIconStyle ? (this.json.customIconStyle+ "/") : "";
                return path+""+this.form.options.style+"/custom/"+iconPath+img;
            }else{
                return path+(this.options.style||"default") +"/tools/"+ (this.json.iconStyle || this.json.style || "default") +"/"+img;
            }
        },
        getImageOverPath: function(img, iscustom){
            var path = "../x_component_process_FormDesigner/Module/Actionbar/";
            if( iscustom && this.json.customIconOverStyle ){
                return path+""+this.form.options.style +"/custom/"+this.json.customIconOverStyle+ "/" +img
            }else{
                return path+""+(this.options.style||"default")+"/tools/"+( this.json.iconOverStyle || "default" )+"/"+img;
            }
        },
        setToolbarItem: function(tool, node, readonly, noCondition){
            var path = "../x_component_process_FormDesigner/Module/Actionbar/";
            var flag = true;
            if (tool.control){
                flag = this.form.businessData.control[tool.control]
            }
            if (!noCondition) if (tool.condition){
                var hideFlag = this.form.Macro.exec(tool.condition, this);
                flag = flag && (!hideFlag);
            }

            if (tool.id == "action_downloadAll" || tool.id == "action_print"){
                if (!this.form.businessData.work.startTime){
                    flag = false;
                }
            }
            if (tool.id == "action_delete"){
                if (!this.form.businessData.work || !this.form.businessData.work.id){
                    flag = false;
                }
            }

            if (tool.id == "action_rollback") tool.read = true;
            if (readonly) if (!tool.read) flag = false;
            if (flag){
                var actionNode = new Element("div", {
                    "id": tool.id,
                    "MWFnodetype": tool.type,
                    //"MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img,
                    "MWFButtonImage": this.json.iconType==="font" ? "" : this.getImagePath(tool.img, tool.customImg),
                    "MWFButtonIcon": tool.icon,
                    "title": tool.title,
                    "MWFButtonAction": tool.action,
                    "MWFButtonText": tool.text,
                    "MWFHide": (tool.id === "action_goBack") ? 'true' : ''
                }).inject(node);
                if( this.json.iconOverStyle ){
                    actionNode.set("MWFButtonImageOver" , this.getImageOverPath(tool.img, tool.customImg) );
                }
                if( tool.properties ){
                    actionNode.set(tool.properties);
                }
                if (tool.sub){
                    var subNode = node.getLast();
                    this.setToolbars(tool.sub, subNode, readonly, noCondition);
                }
            }
        },
        /**
         * @summary 작업 id로 작업 가져오기, 이 메서드는 작업 막대의 afterLoad 이벤트와 작업의 작업 스크립트에서 유효.
         *  @param {String} id - 필수, 작업 id.
         *  @return {o2.widget.ToolbarButton} 작업
         *  @example
         *  var actionbar = this.form.get("name"); //작업 막대 가져오기
         *  var item = actionbar.getItem( "action_delete" ); //삭제 작업 가져오기
         *  item.node.hide(); //삭제 작업 노드 숨기기
         *  item.node.click(); //작업의 click 이벤트 발생
         */
        getItem : function( id ){
            if( this.toolbarWidget && id ){
                return this.toolbarWidget.items[id]
            }
        },
        /**
         * @summary 모든 작업 가져오기, 이 메서드는 작업 막대의 afterLoad 이벤트와 작업의 작업 스크립트에서 유효.
         *  @return {Array} 작업 배열
         *  @example
         *  var actionbar = this.form.get("name"); //작업 막대 가져오기
         *  var itemList = actionbar.getAllItem(); //작업 배열 가져오기
         *  itemList[1].node.hide(); //첫 번째 작업 숨기기
         */
        getAllItem : function(){
            return this.toolbarWidget ? this.toolbarWidget.childrenButton : [];
        },
        setToolbars: function(tools, node, readonly, noCondition){
            tools.each(function(tool){
                this.setToolbarItem(tool, node, readonly, noCondition);
            }.bind(this));
        },
        runCustomAction: function(bt){
            var script = bt.node.retrieve("script");
            this.form.Macro.exec(script, this);
        },
        saveWork: function(){
            this.form.saveWork();
        },
        closeWork: function(){
            this.form.closeWork();
        },
        flowWork: function(){
            this.form.flowWork();
        },
        processWork: function(){
            this.form.processWork();
        },
        resetWork: function(){
            this.form.resetWork();
        },
        addTask: function(){
            this.form.addTask();
        },
        retractWork: function(e, ev){
            this.form.retractWork(e, ev);
        },
        rerouteWork: function(e, ev){
            this.form.rerouteWork(e, ev);
        },
        deleteWork: function(){
            this.form.deleteWork();
        },
        printWork: function(){
            this.form.printWork();
        },
        readedWork: function(b,e){
            this.form.readedWork(e);
        },
        addSplit: function(e){
            this.form.addSplit(e);
        },
        rollback: function(e){
            this.form.rollback(e);
        },
        downloadAll: function(e){
            this.form.downloadAll(e);
        },
        monitor: function(e){
            this.form.monitor(e);
        },
        pressWork: function(e){
            this.form.pressWork(e);
        },
        pauseTask: function(e){
            var p = this.form.pauseTask(e);
            if (p){
                p.then(function(){
                    e.setText(MWF.xApplication.process.Xform.LP.resume);
                    e.options.action = "resumeTask";

                    var img = e.picNode.getElement("img");
                    var src = img.get("src");
                    src = src.substr(0, src.lastIndexOf("/"));
                    src = src+"/resume.png";
                    img.set("src", src);

                }.bind(this), function(){});
            }
        },
        resumeTask: function(e){
            var p = this.form.resumeTask(e);
            if (p){
                p.then(function(){
                    e.setText( MWF.xApplication.process.Xform.LP.pause);
                    e.options.action = "pauseTask";

                    var img = e.picNode.getElement("img");
                    var src = img.get("src");
                    src = src.substr(0, src.lastIndexOf("/"));
                    src = src+"/pause.png";
                    img.set("src", src);

                }.bind(this), function(){});
            }
        },
        goBack: function(e){
            this.form.goBack(e);
        },

        terminate: function(e, ev){
            this.form.terminateWork(e, ev);
        }

    });
