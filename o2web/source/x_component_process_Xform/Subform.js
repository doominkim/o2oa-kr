MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Subform 하위 양식 컴포넌트.
 * @o2cn 하위 양식
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var subform = this.form.get("fieldId"); //컴포넌트 가져오기
 * //방법 2
 * var subform = this.target; //컴포넌트 자체 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Subform = MWF.APPSubform = new Class(
    /** @lends MWF.xApplication.process.Xform.Subform# */
{
    Extends: MWF.APP$Module,
    options: {
        /**
         * 하위 양식 설계를 가져왔으나 아직 html 삽입 및 내부 컴포넌트 생성 전.
         * @event MWF.xApplication.process.Xform.Subform#beforeModulesLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 하위 양식 설계를 가져와 html을 삽입했고 컴포넌트 json도 가져왔으나 내부 컴포넌트는 아직 생성 전.
         * @example
         * //하위 양식의 모든 컴포넌트 id 가져오기
         * var moduleIdList = Object.keys(this.target.subformData.json.moduleList);
         * @event MWF.xApplication.process.Xform.Subform#modulesLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 하위 양식 내부 컴포넌트 로드 완료.
         * @example
         * //하위 양식의 모든 컴포넌트 id 가져오기
         * var moduleIdList = Object.keys(this.target.subformData.json.moduleList);
         * //하위 양식의 모든 컴포넌트 가져오기
         * var moduleList = moduleIdList.map(function(id){
         *     return this.form.get(id, subformId); //subformId는 현재 하위 양식 ID, 레이아웃 컴포넌트는 id가 충돌할 수 있어 subformId로 현재 하위 양식의 컴포넌트를 특정
         * }.bind(this))
         * @event MWF.xApplication.process.Xform.Subform#afterModulesLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "beforeModulesLoad", "modulesLoad", "afterModulesLoad"]
    },

    _loadUserInterface: function () {
        /**
         * @ignore
         * @member parentLine
         * @memberOf MWF.xApplication.process.Xform.Subform#
         */

        this.node.empty();

        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        this.modules = [];
        this.moduleList = {};

        if (this.json.isDelay) {
            if (this.form.subformLoadedCount) {
                this.form.subformLoadedCount++;
            } else {
                this.form.subformLoadedCount = 1
            }
            this.form.checkSubformLoaded();
            this.checked = true;
        } else {

            this.getSubform(function () {
                this.loadSubform();
            }.bind(this));
        }
    },
    /**
     * @summary 하위 양식이 지연 로드로 설정된 경우 active 메서드로 활성화
     * @param {Function} callback 활성화 후 콜백 메서드, 이미 활성화된 경우에도 실행됨.
     * @example
     * var subform = this.form.get("fieldId");
     * subform.active(function(){
     *     //do someting
     * })
     */
    active: function (callback) {
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        if (!this.loaded) {
            this.reload(callback)
        } else {
            if (callback) callback();
        }
    },
    /**
     * @summary 하위 양식 다시 로드
     * @param {Function} callback 새로 고침 후 콜백
     * @example
     * this.form.get("fieldId").reload(function(){
     *     //do someting
     * })
     */
    reload: function (callback) {
        this._loadReadEditAbeld();
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }
        
        this.clean();

        this.getSubform(function () {
            this.loadSubform();
            if (callback) callback();
        }.bind(this));
    },
    clean: function(){
        (this.modules || []).each(function(module){
            if( module.json && module.json.type === "Subform" ){
                if(module.clean)module.clean();
            }
        }.bind(this));

        Object.each(this.moduleList || {}, function (module, formKey) {
            if(!module)return;

            if(["Datatable", "Datatemplate"].includes(module.type) ){
                var id = module.id + '..';
                var modules = this.form.all;
                for(var key in modules){
                    if(key.startsWith(id)){
                        var m = modules[key];
                        delete modules[key];
                        delete this.form.json.moduleList[key];
                        delete this.form.forms[key];
                        this.form.modules.erase(m);
                    }
                }
            }

            if (this.form.all[module.id]) delete this.form.all[module.id];
            if (this.form.forms[module.id])delete this.form.forms[module.id];
            this.form.modules.erase(module);

            if( module.name ){
                delete this.form.allForName[module.name];
            }

            delete this.form.json.moduleList[formKey];
        }.bind(this));

        if( this.subformData && this.subformData.json.id ){
            var id = this.subformData.json.id;
            if( this.form.subformLoaded && this.form.subformLoaded.length ){
                this.form.subformLoaded.erase(id);
            }
            if( this.parentformIdList && this.parentformIdList.length){
                this.parentformIdList.erase(id);
            }
        }

        this.modules = [];
        this.moduleList = {};

        this.node.empty();
    },
    loadCss: function () {
        if (this.subformData.json.css && this.subformData.json.css.code) {

            var cssText = this.subformData.json.css.code;

            //주석 삭제
            //cssText = cssText.replace(/\/\*[\s\S]*?\*\/\n|([^:]|^)\/\/.*\n$/g, '').replace(/\\n/, '');

            cssText = cssText.replace(/\/\*[\s\S]*?\*\//g, '')  // 여러 줄 주석 제거
                .replace(/\\n/g, '');             // \n 제거

            cssText = this.form.parseCSS(cssText);

            var rex = new RegExp("(.+)(?=\\{)", "g");
            var match;
            var id = this.form.json.id.replace(/\-/g, "");
            var prefix = ".css" + id + " ";

            while ((match = rex.exec(cssText)) !== null) {
                var rulesStr = match[0];
                var startWith = rulesStr.substring(0, 1);
                if (startWith === "@" || startWith === ":" || rulesStr.indexOf("%") !== -1) {

                }else if (rulesStr.trim()==='from' || rulesStr.trim()==='to'){

                } else {
                    if (rulesStr.indexOf(",") != -1) {
                        //var rules = rulesStr.split(/\s*,\s*/g);
                        var rules = rulesStr.split(/,/g);
                        rules = rules.map(function (r) {
                            return prefix + r;
                        });
                        var rule = rules.join(",");
                        cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                        rex.lastIndex = rex.lastIndex + (prefix.length * rules.length);

                    } else {
                        var rule = prefix + match[0];
                        cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                        rex.lastIndex = rex.lastIndex + prefix.length;
                    }
                }
            }

            var styleNode = $("style" + this.form.json.id);
            if (!styleNode) {
                var styleNode = document.createElement("style");
                styleNode.setAttribute("type", "text/css");
                styleNode.id = "style" + this.form.json.id;
                styleNode.inject(this.form.container, "before");
            }

            if (styleNode.styleSheet) {
                var setFunc = function () {
                    styleNode.styleSheet.cssText += cssText;
                };
                if (styleNode.styleSheet.disabled) {
                    setTimeout(setFunc, 10);
                } else {
                    setFunc();
                }
            } else {
                var cssTextNode = document.createTextNode(cssText);
                styleNode.appendChild(cssTextNode);
            }
        }
    },
    checkSubformNested: function (id) {
        if (!id) return true;
        if (this.parentformIdList) {
            return !this.parentformIdList.contains(id);
        } else {
            return ![this.form.json.id].contains(id);
        }
    },
    checkSubformUnique: function (id) {
        if (!id) return true;
        if (!this.form.subformLoaded) return true;
        return !this.form.subformLoaded.contains(id);
    },
    getParentformIdList: function () {
        var parentformIdList;
        if (this.parentformIdList) {
            parentformIdList = Array.clone(this.parentformIdList);
            parentformIdList.push(this.subformData.json.id)
        } else {
            parentformIdList = [this.form.json.id, this.subformData.json.id];
        }
        return parentformIdList;
    },
    loadSubform: function () {
        if (this.subformData) {
            if (!this.checkSubformNested(this.subformData.json.id)) {
                this.form.notice(MWF.xApplication.process.Xform.LP.subformNestedError, "error");
            } else if (!this.checkSubformUnique(this.subformData.json.id)) {
                this.form.notice(MWF.xApplication.process.Xform.LP.subformUniqueError, "error");
            } else {
                //this.form.addEvent("postLoad", function(){

                this.fireEvent("beforeModulesLoad");

                this.loadCss();

                this.modules = [];
                this.moduleList = {};

                this.node.set("html", this.subformData.html);
                Object.each(this.subformData.json.moduleList, function (module, key) {
                    var formKey = key;
                    if( this.json.isReadonly )module.isReadonly = true;
                    if (this.form.json.moduleList[key]) {
                        formKey = this.json.id + "_" + key;
                        var moduleNode = this.node.getElement("#" + key);
                        if (moduleNode) moduleNode.set("id", formKey);
                        module.id = formKey;
                        module._originId = key;
                        module._subform = this.json.id;
                    }
                    this.form.json.moduleList[formKey] = module;
                    this.moduleList[formKey] = module;
                }.bind(this));

                this.fireEvent("modulesLoad");

                var moduleNodes = this.form._getModuleNodes(this.node);
                moduleNodes.each(function (node) {
                    if (node.get("MWFtype") !== "form") {
                        var _self = this;
                        var json = this.form._getDomjson(node);
                        //if( json.type === "Subform" || json.moduleName === "subform" )this.form.subformCount++;
                        var module = this.form._loadModule(json, node, function () {
                            this.parentformIdList = _self.getParentformIdList();
                        });
                        this.form.modules.push(module);
                        this.modules.push(module);
                    }
                }.bind(this));

                this.form.subformLoaded.push(this.subformData.json.id);

                this.fireEvent("afterModulesLoad");

                //}.bind(this));
            }
        }
        if (!this.checked) {
            if (this.form.subformLoadedCount) {
                this.form.subformLoadedCount++;
            } else {
                this.form.subformLoadedCount = 1
            }
            this.form.checkSubformLoaded();
        }

        /**
         * @summary 양식 로드(활성화) 여부.
         * @member {Boolean}
         * @example
         * if( !this.form.get("fieldId").loaded ){ //하위 양식 로드 여부 판단
         *     this.form.get("fieldId").active(); //로드되지 않았으면 활성화
         * }
         */
        this.loaded = true;
        this.checked = true;
    },
    getSubform: function (callback) {
        var method = (this.form.json.mode !== "Mobile" && !layout.mobile) ? "getForm" : "getFormMobile";

        if (this.json.subformType === "script") {
            if (this.json.subformScript && this.json.subformScript.code) {
                var data = this.form.Macro.exec(this.json.subformScript.code, this);
                if (data) {
                    var formName, app;
                    if (typeOf(data) === "string") {
                        formName = data;
                    } else {
                        if (data.application) app = data.application;
                        if (data.subform) formName = data.subform;
                    }
                    if (formName) {
                        if (!app) app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                        MWF.Actions.get("x_processplatform_assemble_surface")[method](formName, app, function (json) {
                            this.getSubformData(json.data);
                            if (callback) callback();
                        }.bind(this));
                    } else {
                        this.subformData = null;
                        if (callback) callback();
                    }
                } else {
                    this.subformData = null;
                    if (callback) callback();
                }
            }
        } else {
            if (this.json.subformSelected && this.json.subformSelected !== "none") {
                var subformData = (this.form.app.relatedFormMap) ? this.form.app.relatedFormMap[this.json.subformSelected] : null;
                if (subformData) {
                    this.getSubformData({"data": subformData.data});
                    if (callback) callback();
                } else {
                    var app;
                    if (this.json.subformAppSelected) {
                        app = this.json.subformAppSelected;
                    } else {
                        app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                    }
                    MWF.Actions.get("x_processplatform_assemble_surface")[method](this.json.subformSelected, app, function (json) {
                        this.getSubformData(json.data);
                        if (callback) callback();
                    }.bind(this));
                }
            } else {
                this.subformData = null;
                if (callback) callback();
            }
        }
    },
    getSubformData: function (data) {
        if (!data || typeOf(data) !== "object") return;
        var subformDataStr = null;
        // if ( this.form.json.mode !== "Mobile" && !layout.mobile){
        //     subformDataStr = data.data;
        // }else{
        //     subformDataStr = data.mobileData;
        // }
        subformDataStr = data.data;
        this.subformData = null;
        if (subformDataStr) {
            if( this.form.isParseLanguage ) {
                var jsonStr = o2.bindJson(MWF.decodeJsonString(subformDataStr), {"lp": MWF.xApplication.process.Xform.LP.form});
                this.subformData = JSON.decode(jsonStr);
            }else{
                this.subformData = JSON.decode(MWF.decodeJsonString(subformDataStr));
            }
            this.subformData.updateTime = data.updateTime;
        }
    }
});


MWF.xApplication.process.Xform.SubmitForm = MWF.APPSubmitform = new Class({
    Extends: MWF.APPSubform,
    _loadUserInterface: function () {
        // this.node.empty();
        this.getSubform(function () {
            this.loadSubform();
        }.bind(this));
    },
    reload: function () {
        // this.node.empty();
        this.getSubform(function () {
            this.loadSubform();
        }.bind(this));
    },
    show: function ( defaultRoute ) {
        if (this.json.submitScript && this.json.submitScript.code) {
            this.form.Macro.environment.defaultRoute = defaultRoute;
            this.form.Macro.exec(this.json.submitScript.code, this);
        }
        // this.fireSubFormEvent("load");
    },
    // fireSubFormEvent : function( name ){
    //     var events = this.subformData.json.events;
    //     if( events && events[name] && events[name]["code"] ){
    //         this.form.Macro.exec(events[name]["code"], this);
    //     }
    // },
    loadSubform: function () {
        if (this.subformData) {
            if (!this.checkSubformUnique(this.subformData.json.id)) { //제출 양식이 양식에 이미 삽입되어 있으면 해당 양식을 팝업으로 띄움
                // this.form.notice(MWF.xApplication.process.Xform.LP.subformUniqueError, "error");
                this.isEmbedded = true;
                this.fireEvent("afterModulesLoad");
            } else if (!this.checkSubformNested(this.subformData.json.id)) {
                this.form.notice(MWF.xApplication.process.Xform.LP.subformNestedError, "error");
            } else {
                //this.form.addEvent("postLoad", function(){

                // this.fireSubFormEvent("queryLoad");

                this.fireEvent("beforeModulesLoad");

                this.loadCss();

                this.node.set("html", this.subformData.html);
                Object.each(this.subformData.json.moduleList, function (module, key) {
                    var formKey = key;
                    if (this.form.json.moduleList[key]) {
                        formKey = this.json.id + "_" + key;
                        var moduleNode = this.node.getElement("#" + key);
                        if (moduleNode) moduleNode.set("id", formKey);
                        module.id = formKey;
                        module._originId = key;
                        module._subform = this.json.id;
                    }
                    this.form.json.moduleList[formKey] = module;
                }.bind(this));

                var moduleNodes = this.form._getModuleNodes(this.node);
                moduleNodes.each(function (node) {
                    if (node.get("MWFtype") !== "form") {
                        var _self = this;
                        var json = this.form._getDomjson(node);
                        //if( json.type === "Subform" || json.moduleName === "subform" )this.form.subformCount++;
                        var module = this.form._loadModule(json, node, function () {
                            this.parentformIdList = _self.getParentformIdList();
                        });
                        this.form.modules.push(module);
                    }
                }.bind(this));

                this.form.subformLoaded.push(this.subformData.json.id);
                this.fireEvent("afterModulesLoad");
                // this.fireSubFormEvent("postLoad");
                // this.fireSubFormEvent("load");
                // this.fireSubFormEvent("afterLoad");
            }
        }
        // if( this.form.subformLoadedCount ){
        //     this.form.subformLoadedCount++;
        // }else{
        //     this.form.subformLoadedCount = 1
        // }
        // this.form.checkSubformLoaded();
    },
    getSubform: function (callback) {
        var method = (this.form.json.mode !== "Mobile" && !layout.mobile) ? "getForm" : "getFormMobile";
        if (this.json.submitFormType === "script") {
            if (this.json.submitFormScript && this.json.submitFormScript.code) {
                var data = this.form.Macro.exec(this.json.submitFormScript.code, this);
                if (data) {
                    var formName, app;
                    if (typeOf(data) === "string") {
                        formName = data;
                    } else {
                        if (data.application) app = data.application;
                        if (data.form) formName = data.form;
                    }
                    if (formName) {
                        if (!app) app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                        MWF.Actions.get("x_processplatform_assemble_surface")[method](formName, app, function (json) {
                            this.getSubformData(json.data);
                            if (callback) callback();
                        }.bind(this));
                    } else {
                        this.subformData = null;
                        if (callback) callback();
                    }
                } else {
                    this.subformData = null;
                    if (callback) callback();
                }
            }
        } else {
            if (this.json.submitFormSelected && this.json.submitFormSelected !== "none") {
                var app;
                if (this.json.submitFormAppSelected) {
                    app = this.json.submitFormAppSelected;
                } else {
                    app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                }
                MWF.Actions.get("x_processplatform_assemble_surface")[method](this.json.submitFormSelected, app, function (json) {
                    this.getSubformData(json.data);
                    if (callback) callback();
                }.bind(this));
            } else {
                this.subformData = null;
                if (callback) callback();
            }
        }
    }
});
