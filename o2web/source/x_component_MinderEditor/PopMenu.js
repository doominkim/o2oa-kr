var Popmenu = MWF.xApplication.MinderEditor.PopMenu = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, editor, minder, app) {
        this.container = container;
        this.app = app;
        this.lp = MWF.xApplication.MinderEditor.LP;
        this.actions = this.app.restActions;
        this.editor = editor;
        this.minder = minder;
        this.receiver = editor.receiver;
        var fsm = this.fsm = editor.fsm;

        this.path = "../x_component_MinderEditor/$PopMenu/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.nodeMenu = new MWF.xApplication.MinderEditor.NodePopMenu(this.container, null, this.app, null, {
            nodeStyles : this.css.tooltipNode,
            onPostLoad : function(){
                this.nodeMenu.isActive = true;
            }.bind(this),
            onHide : function(){
                this.nodeMenu.isActive = false;
            }.bind(this)
        }, {});
        this.nodeMenu.popmenu = this;
        this.nodeMenu.minder = this.minder;

        fsm.when('normal -> popmenu', function(exit, enter, reason) {
            var node = this.minder.getSelectedNode();
            var position;
            if (node) {
                var box = node.getRenderBox();
                position = {
                    x: box.cx,
                    y: box.cy
                };
                this.active('main', position)
            }
        }.bind(this));

        fsm.when('popmenu -> popmenu', function(exit, enter, reason) {
            var node = this.minder.getSelectedNode();
            var position;
            if (node) {
                var box = node.getRenderBox();
                position = {
                    x: box.cx,
                    y: box.cy
                };
                this.active('main', position)
            }
        }.bind(this));

        fsm.when('popmenu -> normal', function(exit, enter, reason, e) {
            //if (reason == 'shortcut-handle') {
                //var handleResult = this.dispatch(e);
                //if (handleResult) {
                //    e.preventDefault();
                //} else {
                //    this.minder.dispatchKeyEvent(e);
                //}
            //}else
            if( reason == "popmenu-idle" ){
                if( this.nodeMenu.isActive ){
                    this.nodeMenu.hide();
                }
            }
        }.bind(this));

        //fsm.when('popmenu -> input', function(exit, enter, reason, e) {
        //    if( reason == "input-request" ){
        //    }
        //}.bind(this));

        fsm.when('modal -> normal', function(exit, enter, reason, e) {
            if (reason == 'import-text-finish') {
                this.receiver?.element.focus();
            }
        }.bind(this));
    },
    dispatch: function(e){
        if( this.nodeMenu.isActive ){
            var handleResult = this.nodeMenu.dispatchKey(e);
            if (handleResult) {
                return true;
                e.preventDefault();
            } else {
                this.minder.dispatchKeyEvent(e);
            }
        }
        //console.log( e.key );
        return false;
    },
    active : function( type, position ){
        if( type == "main" ){
            this.nodeMenu.targetCoordinates = {
                top: parseInt( position.y + this.editor.Content_Offset_Top ),
                left: position.x,
                width: 1,
                height: 1,
                right: position.x+1,
                bottom: parseInt( position.y ) + this.editor.Content_Offset_Top + 1
            };
            this.nodeMenu.load();
            this.nodeMenu.checkStatus();
        }else{
            this.nodeMenu.hide();
        }
    },
    state : function(){
        return this.nodeMenu.state;
    },
    load: function (callback) {

    },
    destroy: function () {
        this.node.destroy();
        delete  this;
    }
});
Popmenu.STATE_IDLE = "idle";

MWF.xApplication.MinderEditor.NodePopMenu = new Class({
    Extends: MTooltips,
    options : {
        axis: "x",      //화살표를 x축에 표시할지 y축에 표시할지
        position : { //node 고정 위치
            x : "auto", //x축 left center right, auto는 시스템이 자동 계산
            y : "bottom" //y축 top middle bottom, auto는 시스템이 자동 계산
        },
        priorityOfAuto :{
            x : [ "right", "left" ], //position x가 auto일 때의 우선순위
            y : [ "bottom", "middle", "top" ] //position y가 auto일 때의 우선순위
        },
        event : "mouseenter", //이벤트 유형, target이 있을 때 유효, mouseenter는 mouseleave에 대응, click은 container의  click에 대응
        hasArrow : false,
        isAutoHide : false
    },
    load: function(){
        this.fireEvent("queryLoad",[this]);
        if( this.isEnable() ){
            if( this.node ){
                this.show();
            }else{
                this.create();
            }
        }
        this.stat = "main";
        this.fireEvent("postLoad",[this]);
    },
    hide: function(){
        if( this.node ){
            this.node.setStyle("display","none");
            this.status = "hidden";
            if( this.maskNode ){
                this.maskNode.setStyle("display","none");
            }
            if( this.commands.activeTooltip ){
                this.commands.activeTooltip.hide();
            }
            this.fireEvent("hide",[this]);
        }
    },
    _customNode : function( node, contentNode ){
        this.itemNodeList = [];
        this.itemNodeObject = {};

        this.availableCommands = ["appendChild","appendParent","appendSibling","arrangeUp","arrangeDown",
            "edit","remove","hyperLink","image","priority","progress"
            //"clearstyle","copystyle","pastestyle","fontfamily","fontsize","bold","forecolor","background",
            //"selectAll"
        ];
        this.commands = new MWF.xApplication.MinderEditor.Commands( this.app , {
            type : "popmenu",
            onPostExecCommand: function( commandsObj, command ){
                this.state = "idle";
                this.hide();
            }.bind( this )
        });
        this.commands.selectOptions = {
            //"style" : "minderPopmenu",
            "tooltipsOptions": {
                axis: "x",      //화살표를 x축에 표시할지 y축에 표시할지
                position : { //node 고정 위치
                    x : "auto", //x축 left center right, auto는 시스템이 자동 계산
                    y : "auto" //y축 top middle bottom, auto는 시스템이 자동 계산
                },
                priorityOfAuto :{
                    x : [ "right", "left" ], //position x가 auto일 때의 우선순위
                    y : [ "bottom" ] //position y가 auto일 때의 우선순위
                },
                event : "mouseenter", //이벤트 유형, target이 있을 때 유효, mouseenter는 mouseleave에 대응, click은 container의  click에 대응
                hiddenDelay : 200, //ms, target이 있고 이벤트 유형이 mouseenter일 때 유효
                displayDelay : 0,   //ms, target이 있고 이벤트 유형이 mouseenter일 때 유효
                "onQueryLoad": function (tooltips) {
                    if (tooltips.selector.command && this.commands.commands) {
                        tooltips.disable = this.commands.commands[tooltips.selector.command].disable();
                    }
                }.bind(this),
                "onPostLoad": function (tooltips) {
                    this.commands.activeTooltip = tooltips;
                }.bind(this),
                "onHide": function (tooltips) {
                    if (this.commands.activeTooltip == tooltips)this.commands.activeTooltip = null;
                }.bind(this),
                event: "mouseenter" //이벤트 유형, target이 있을 때 유효, mouseenter는 mouseleave에 대응, click은 container의  click에 대응
            }
        };

        contentNode.addEvent('contextmenu', function(e) {
            e.preventDefault();
        });

        this.createItemList( contentNode );

        this.state = "idle";

        //
        //this.minder.on('interactchange', function() {
        //    this.hide();
        //}.bind(this));
    },
    createItemList:function(node){
        var _popmenu = this.popmenu;
        this.css = _popmenu.css;

        this.listContentNode = new Element("div.listContentNode",{
            "styles":this.css.listContentNode
        }).inject( node );

        this.listNode = new Element("div.listNode",{
            "styles":this.css.listNode
        }).inject(this.listContentNode);

        var commands = this.commands.commands;

        this.availableCommands.each( function( name ){
            if( commands[name] ){
                this.createItem( commands[name], name );
            }
        }.bind(this));
    },
    createItem: function( command, name ){
        var _self = this;
        var node = new Element("div.listItemNode",{
            "text" : command.locale || null
        }).inject(this.listNode);

        var keyNode = new Element("div.listItemKeyNode", {
            "styles" : this.css.listItemKeyNode,
            "text" : typeOf( command.key ) == "array" ? command.key.join(",") :  (command.key || "")
        }).inject(node);
        node.keyNode = keyNode;

        this.setNormalStye( node, keyNode, command );
        if( command.disable() ){
            this.setDisableStye( node, keyNode, command )
        }

        var title = (command.title || "");  //+ ( command.key ? (" 단축키:" + command.key) : "" );
        node.set("title", title);

        node.addEvents({
            mouseover : function(){
                if( !command.disable() ){
                    this.setActiveStye( node, keyNode, command )
                }else{
                    this.setDisableStye( node, keyNode, command )
                }
            }.bind(this),
            mouseout : function(){
                if( !command.disable() ) {
                    this.setNormalStye( node, keyNode, command )
                }else{
                    this.setDisableStye( node, keyNode, command )
                }
            }.bind(this)
        });

        if( command.action ){
            node.addEvent("click", function( ev ){
                if( !command.disable() ) {
                    command.action();
                    _self.checkStatus();
                    _self.state = "idle";
                    _self.hide();
                    _self.fireEvent( "postExecCommand", [_self.commands, command ] );
                    ev.stopPropagation();
                }
            }.bind(name))
        }
        if( command.init ){
            command.init( node, name );
        }

        this.itemNodeList.push( node );
        this.itemNodeObject[ name ] = node;
    },
    setDisableStye : function( node,keyNode, command ){
        node.setStyles( this.css.listItemNode_disable );
        keyNode.setStyles( this.css.listItemKeyNode_disable );
        if( command.icon ){
            node.setStyle( "background-image" , "url(../x_component_MinderEditor/$Main/"+ this.popmenu.options.style +"/icon/"+command.icon+"_disable.png)" );
        }
    },
    setActiveStye : function( node, keyNode, command ){
        node.setStyles( this.css.listItemNode_over );
        keyNode.setStyles( this.css.listItemKeyNode_over );
        if( command.icon ) {
            node.setStyle("background-image", "url(../x_component_MinderEditor/$Main/" + this.popmenu.options.style + "/icon/" + command.icon + "_menu.png)")
        }
    },
    setNormalStye : function( node, keyNode, command ){
        node.setStyles(this.css.listItemNode);
        keyNode.setStyles( this.css.listItemKeyNode );
        if( command.icon ) {
            node.setStyle("background-image", "url(../x_component_MinderEditor/$Main/" + this.popmenu.options.style + "/icon/" + command.icon + "_normal.png)")
        }
    },
    checkStatus : function(){
        for( var name in this.itemNodeObject ){
            var node = this.itemNodeObject[name];
            if( this.commands.commands[ name ] ){
                var command = this.commands.commands[ name ];
                if( command.disable() ){
                    this.setDisableStye( node, node.keyNode, command )
                }else{
                    this.setNormalStye( node, node.keyNode, command )
                }
            }
        }
    },
    dispatchKey : function( e ){
        var key = this.commands.getKey( e );
        var command = this.commands.keyCommands[ key ];
        if( command && !command.disable() && this.itemNodeObject[command.name]){
            if( command.action ){
                command.action();
                this.checkStatus();
                this.state = "idle";
                this.commands.fireEvent( "postExecCommand", [ this.commands, command] );
                return true;
            }else if( command.keyAction ){
                if( this.commands.activeTooltip )this.commands.activeTooltip.hide();
                command.keyAction();
                this.state = "expand";
                return true;
            }else{
                this.state = "main";
                return true;
            }
        }
        var defaultCommand = this.commands.defaultKeyCommands[ key ];
        if( defaultCommand && !defaultCommand.disable() && this.itemNodeObject[defaultCommand.name] ){
            this.state = "idle";
            return false
        }
        this.state = "main";
        return true;

    }
    //dispatchKey : function( e, callback ){
    //    var key = e.key;
    //    if( e.shiftKey && e.keyCode != 16 ){
    //        key = "Shift + " + e.key.capitalize();
    //    }
    //    if( e.ctrlKey && e.keyCode != 17 ){
    //        key = "Ctrl + " + e.key.capitalize();
    //    }
    //    if( e.altKey && e.keyCode != 18 ){
    //        key = "Alt + " + e.key.capitalize();
    //    }
    //    var node = this.keyObject[ key ];
    //    if( node ){
    //        node.click( e );
    //        this.hide();
    //        if(callback)callback( e, true );
    //        return true;
    //    }else{
    //        if(callback)callback( e, false );
    //        return false;
    //    }
    //}
});