MWF.xDesktop.requireApp("MinderEditor", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("MinderEditor", "Main", null, false);
MWF.xApplication.MinderEditor.MainInContainer = new Class({
    Extends: MWF.xApplication.MinderEditor.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "MinderEditor"
    },
    initialize: function(desktop, options, container, content, scrollNode){
        this.setOptions(options);
        this.desktop = desktop;
        this.container = container; //container, 마인드맵 콘텐츠 컨테이너
        this.content = content; //content, 상위 app의 content, 예: 프로세스나 포털 app의 content
        this.scrollNode = scrollNode;  //스크롤바가 나타나는 노드
        this.path = "../x_component_"+this.options.name.replace(/\./g, "_")+"/$Main/";
        this.options.icon = this.path+this.options.style+"/"+this.options.icon;

        this.cssPath =this.path+this.options.style+"/css.wcss";
        this.inBrowser = true;
        this.inContainer = true;
        this._loadCss();
        debugger;
    },
    loadInBrowser: function(){
        this.window = {
            "isHide": false,
            "isMax": true,
            "maxSize": function(){},
            "restore": function(){},
            "setCurrent": function(){},
            "hide": function(){},
            "maxOrRestoreSize": function(){},
            "restoreSize": function(){},
            "close": function(){},
            "titleText" : {
                set : function(){}
            }
        };
        this.window.content = this.content;
        //this.content = this.window.content;

        //this.content.setStyles({"height": "100%", "overflow": "hidden"});

        //window.addEvent("resize", function(){
        //    this.fireAppEvent("resize");
        //}.bind(this));
        //window.onbeforeunload = function(e){
        //    this.fireAppEvent("queryClose");
        //}.bind(this);

        this.fireAppEvent("postLoadWindow");
        this.fireAppEvent("queryLoadApplication");
        this.setContentEvent();
        this.loadApplication(function(){
            this.fireAppEvent("postLoadApplication");
        }.bind(this));

        //this.content.setStyle("height", document.body.getSize().y);

        this.fireAppEvent("postLoad");
    },
    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.container);

        this._createNode()
    }
});