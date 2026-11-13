MWF.xApplication.Template = MWF.xApplication.Template || {};
MWF.xApplication.Template.widget = MWF.xApplication.Template.widget || {};
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.Template.widget.ColorPicker = new Class({
    Implements: [Options, Events],
    Extends: MTooltips,
    options: {
        style : "default",
        axis: "y",      //화살표를 x축에 표시할지 y축에 표시할지
        position : { //node 고정 위치
            x : "auto", //x축의 left center right, auto는 시스템 자동 계산
            y : "auto" //y축의 top middle bottom,  auto는 시스템 자동 계산
        },
        //event : "click", //이벤트 유형, target이 있을 때 유효, mouseenter는 mouseleave에 대응, click은 container의  click에 대응
        nodeStyles : {
            "min-width" : "200px"
        }
    },
    initialize : function( container, target, app, data, options, targetCoordinates ){
        //target 또는 targetCoordinates 중 하나를 전달할 수 있음
        //target 전달 시 tooltip을 트리거하는 노드를 의미하며, 이 클래스는 this.options.event에 따라 target 이벤트를 자동 바인딩
        //targetCoordinates 전달 시 tooltip을 트리거하는 위치를 의미하며, 이 클래스는 트리거 이벤트를 바인딩하지 않음
        if( options ){
            this.setOptions(options);
        }
        this.container = container;
        this.target = target;
        this.targetCoordinates = targetCoordinates;
        this.app = app;
        if(app)this.lp = app.lp;
        this.data = data;

        this.path = "../x_component_Template/widget/$ColorPicker/";
        this.cssPath = "../x_component_Template/widget/$ColorPicker/"+this.options.style+"/css.wcss";
        this._loadCss();

        if( this.target ){
            this.setTargetEvents();
        }
    },
    _loadCss: function(reload){
        var key = encodeURIComponent(this.cssPath);
        if (!reload && MWF.widget.css[key]){
            this.css = MWF.widget.css[key];
        }else{
            this.cssPath = (this.cssPath.indexOf("?")!=-1) ? this.cssPath+"&v="+COMMON.version : this.cssPath+"?v="+COMMON.version;
            var r = new Request.JSON({
                url: o2.filterUrl(this.cssPath),
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function(responseJSON, responseText){
                    this.css = responseJSON;
                    MWF.widget.css[key] = responseJSON;
                }.bind(this),
                onError: function(text, error){
                    alert(error + text);
                }
            });
            r.send();
        }
    },
    _customNode : function( node ){
        MWF.UD.getDataJson("colorPicker", function(json) {

            this.contentNode = new Element("div").inject( this.node );

            this.previewArea = new Element("div", {styles : this.css.previewArea }).inject( this.contentNode );
            this.previewNode = new Element("div", {styles : this.css.previewNode }).inject( this.previewArea );
            this.previewValueNode = new Element("div", {styles : this.css.previewValueNode }).inject( this.previewArea );

            if( json ){
                this.laterPickedColor = json.laterPickedColor || [];
                this.createLaterPickedNode( this.laterPickedColor );
            }
            this.themeArea = new Element("div").inject( this.contentNode );
            new Element("div", {styles : this.css.titleNode, text : "테마 색" }).inject( this.themeArea );

            this.themeNode = new Element("div", {styles : this.css.colorContainer }).inject( this.themeArea );
            var themeColorList = [
                "#ffffff","#000000","#eeece1","#1f497d","#4f81bd","#c0504d","#9bbb59","#8064a2","#4bacc6","#f79646"
            ];
            for( var i=0; i<themeColorList.length; i++ ){
                this.createColorNode( themeColorList[i], this.themeNode );
            }

            this.theme2Node = new Element("div", {styles : this.css.colorContainer }).inject( this.themeArea );
            this.theme2Node.setStyle( "margin-bootom" , "5px" );
            var themeColorList2 = [
                "#f2f2f2","#808080","#ddd8c2","#c6d9f1","#dbe5f1","#f2dbdb","#eaf1dd","#e5dfec","#daeef3","#fde9d9",
                "#d9d9d9","#595959","#c4bc96","#8db3e2","#b8cce4","#e5b8b7","#d6e3bc","#ccc0d9","#b6dde8","#fbd4b4",
                "#bfbfbf","#404040","#938953","#548dd4","#95b3d7","#d99594","#c2d69b","#b2a1c7","#92cddc","#fabf8f",
                "#a6a6a6","#262626","#4a442a","#17365d","#365f91","#943634","#76923c","#5f497a","#31849b","#e36c0a",
                "#7f7f7f","#0d0d0d","#1c1a10","#0f243e","#243f60","#622423","#4e6128","#3f3151","#205867","#974706"
            ];
            for( var i=0; i<themeColorList2.length; i++ ){
                this.createColorNode( themeColorList2[i], this.theme2Node );
            }

            this.standardArea = new Element("div").inject( this.contentNode );
            new Element("div", {styles : this.css.titleNode, text : "표준 색" }).inject( this.standardArea );
            this.standardNode = new Element("div", {styles : this.css.colorContainer }).inject( this.standardArea );
            var standardColorList = [
                "#c00000", "#ff0000","#ffc000","#ffff00","#92d050","#00b050","#00b0f0","#0070c0","#002060","#7030a0"
            ];
            for( var i=0; i<standardColorList.length; i++ ){
                this.createColorNode( standardColorList[i], this.standardNode );
            }

        }.bind(this));


        //this.resultInput = new Element("input").inject(this.contentNode);
    },
    createColorNode : function( color, container ){
        var _self = this;
        var colorNode = new Element("div", { styles : {
            border : "1px solid "+color,
            "background-color" : color
        }, events : {
            mouseover : function(){
                _self.previewNode.setStyle("background-color",this);
                _self.previewValueNode.set("text",this);
            }.bind( color ),
            click : function(){
                _self.clickColorNode( this );
                _self._select( this );
                _self.fireEvent("select",[this]);
                _self.hide();
            }.bind(color)
        }}).inject( container );
        colorNode.setStyles( this.css.colorNode )
    },
    clickColorNode : function( color ){
        var list = this.laterPickedColor = this.laterPickedColor || [];
        list.unshift( color );
        list = this.unique( list );
        list = list.slice(0, 10);
        MWF.UD.putData("colorPicker", {
            "laterPickedColor": list
        }, null, false);
        this.createLaterPickedNode( list );
    },
    createLaterPickedNode : function( colorList ){
        if( this.laterPickedArea ){
            this.laterPickedNode.empty();
        }else{
            this.laterPickedArea = new Element("div").inject( this.contentNode );
            new Element("div", {styles : this.css.titleNode, text : "최근 사용" }).inject( this.laterPickedArea );
            this.laterPickedNode = new Element("div", {styles : this.css.colorContainer }).inject( this.laterPickedArea );
        }
        for( var i=0; i< colorList.length; i++ ){
            this.createColorNode( colorList[i], this.laterPickedNode );
        }
    },
    unique : function( array ){
        var res = [];
        var json = {};
        for(var i = 0; i < array.length; i++){
            if(!json[array[i]]){
                res.push(array[i]);
                json[array[i]] = 1;
            }
        }
        return res;
    },
    _select: function( color ){

    }
});

