MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.require("MWF.widget.Tab", null, false);
/** @class Tab 탭 컴포넌트.
 * @o2cn 탭 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var tab = this.form.get("fieldId"); //컴포넌트 가져오기
 * //방법 2
 * var tab = this.target; //컴포넌트 자체 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Tab = MWF.APPTab =  new Class(
    /** @lends MWF.xApplication.process.Xform.Tab# */
{
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        this.elements = [];
        this.containers = [];

        var style = "form";
        if (layout.mobile) style = "mobileForm";

        /**
         * @summary tab 컴포넌트, 플랫폼이 이 컴포넌트로 탭 기능을 구현
         * @member {MWF.widget.Tab}
         * @example
         *  //스크립트에서 이 컴포넌트를 가져올 수 있음
         * var tab = this.form.get("fieldId").tab; //컴포넌트 객체 가져오기
         * var pages = tab.pages //각 탭 가져오기
         * pages[1].addEvent("queryShow", function(){
         *     //탭 표시 전 이벤트 추가
         * })
         * pages[1].addEvent("postShow", function(){
         *     //탭 표시 후 이벤트 추가
         * })
         * pages[1]._showTab(); //두 번째 탭 표시
         */
		this.tab = new MWF.widget.Tab(this.node, {"style": style});
		
		this._setTabWidgetStyles();

		this.tab.tabNodeContainer = this.node.getFirst("div");
		this.tab.contentNodeContainer = this.tab.tabNodeContainer.getNext("div");

		var lastNode = this.tab.tabNodeContainer.getLast();
		var tabs;
		if (lastNode && lastNode.hasClass("tabNodeContainerLeft")){
            this.tab.tabNodeContainerRight = this.tab.tabNodeContainer.getFirst();
            this.tab.tabNodeContainerLeft = lastNode;
            this.tab.tabNodeContainerArea = lastNode.getFirst();

            var menuNode = this.node.getElement(".MWFMenu");
            if (menuNode) menuNode.destroy();
            this.tab.load();
            tabs = this.tab.tabNodeContainerArea.getChildren("div");
        }else{
            tabs = this.tab.tabNodeContainer.getChildren("div");
            this.tab.load();
        }

		var contents = this.tab.contentNodeContainer.getChildren("div");
		tabs.each(function(tab, idx){
			this.tab.rebuildTab(contents[idx], contents[idx].getFirst(), tab);
		}.bind(this));

		if( this.json.defaultIndex && this.tab.pages[this.json.defaultIndex]){
            this.tab.pages[this.json.defaultIndex]._showTab();
        }else{
            this.tab.pages[0]._showTab();
        }
        this.loadSubModule();

        this.elements.forEach((e)=>{
            if (!e.isReadable){
                const page = this.tab.pages.find((p)=>{
                    return p.tabNode === e.node;
                })
                if (page){
                    page.closeTab();
                }
            }
        });

	},
    loadSubModule: function(){
        this.tab.pages.each(function(page){
            var node = page.tabNode;
            var json = this.form._getDomjson(node);
            var tab = this;
            var module = this.form._loadModule(json, node, function(){
                if( tab.widget )this.widget = tab.widget;
                this.tab = tab;
            });
            this.elements.push(module);
            this.form.modules.push(module);

            if( json.width && json.width.toInt()>60 ){
                node.setStyle("width", json.width+"px");
            }
            if( json.description){
                node.set("title", json.description);
            }
            if (page.isShow){
                this.showContentModule.call(page, this);
            }else{
                if (this.json.isDelay){
                    var _self = this;
                    page.showContentModuleFun = function(){_self.showContentModule.call(page, _self)};
                    page.addEvent("show", page.showContentModuleFun);
                }else{
                    this.showContentModule.call(page, this);
                }
            }
        }.bind(this));
    },
    showContentModule: function(_self){
	    var page = this;
        var node = this.contentNode;
        node.isLoadModule = true;
        var json = _self.form._getDomjson(node);
        var tab = _self;
        var module = _self.form._loadModule(json, node, function(){
            if( _self.widget )this.widget = _self.widget;
            this.tab = tab;
            this.page = page;
        });
        _self.containers.push(module);
        _self.form.modules.push(module);

        if (this.showContentModuleFun) this.removeEvent("show", this.showContentModuleFun);
    },

	_setTabWidgetStyles: function(){
        if (this.json.tabNodeContainer) this.tab.css.tabNodeContainer = Object.clone(this.json.tabNodeContainer);
        if (this.json.contentNodeContainer) this.tab.css.contentNodeContainer = Object.clone(this.json.contentNodeContainer);
		this.tab.css.tabNode = Object.clone(this.json.tabStyles);
		this.tab.css.tabTextNode = Object.clone(this.json.tabTextStyles);
		this.tab.css.tabNodeCurrent = Object.clone(this.json.tabCurrentStyles);
		this.tab.css.tabTextNodeCurrent = Object.clone(this.json.tabTextCurrentStyles);
	}
});
MWF.xApplication.process.Xform.tab$Page = MWF.APPTab$Page =  new Class({
    Extends: MWF.APP$Module,
     _loadUserInterface: function(){
        
    },
});
MWF.xApplication.process.Xform.tab$Content = MWF.APPTab$Content =  new Class({
    Extends: MWF.APP$Module,
    _loadUserInterface: function(){
        var _self = this;
        this.form._loadModules(this.node, function () {
            if( _self.widget )this.widget = _self.widget;
        });
    }
});
