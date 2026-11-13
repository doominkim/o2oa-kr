//MWF.xDesktop.requireApp("Minder", "Actions.RestActions", null, false);

MWF.require("MWF.widget.Tree", null, false);
MWF.xApplication.Minder.MainPc = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Minder",
		"icon": "icon.png",
		"width": "1200",
        "height": "700",
        "isResize": false,
		"title": MWF.xApplication.Minder.LP.title,
        "defaultAction" : "openMineExplorer"
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Minder.LP;
        this.restActions = MWF.Actions.get("x_mind_assemble_control"); //new MWF.xApplication.Minder.Actions.RestActions();
	},
	loadApplication: function(callback){
		this.createNode();
		this.loadApplicationContent();
		if (callback) callback();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": this.css.node
		}).inject(this.content);
	},
	loadApplicationContent: function() {
        this.naviNode = new Element("div.naviNode",{
            "styles" : this.css.naviNode
        }).inject(this.node);
        // new Element("div.naviTopNode",{
        //     "styles" : this.css.naviTopNode
        // }).inject(this.naviNode);

        this.contentNode = new Element("div.contentNode",{
            "styles" : this.css.contentNode
        }).inject(this.node);

        //this.histroy = new MWF.xApplication.Minder.History(this);

        this.resizeContent();
        this.resizeFun = this.resizeContent.bind( this );
        this.addEvent("resize", this.resizeFun);

        this.loadNavi();
    },
    loadNavi : function(){
        var naviJson = [
            {
                "title": this.lp.myFiles,
                "action": "openMineExplorer",
                "icon": "ooicon-personnel"
            },
            {
                "title": this.lp.shareFiles,
                "action": "openSharedExplorer",
                "icon": "ooicon-fawen"
            },
            {
                "title": this.lp.editorFiles,
                "action": "openReceivedExplorer",
                "icon": "ooicon-shouwen"
            },
            {
                "title": this.lp.trashBin,
                "action": "openRecycleExplorer",
                "icon": "ooicon-maintain"
            }
        ];
        naviJson.each( function( d ){
            this.createNaviNode( d );
        }.bind(this))
    },
    openMineExplorer: function(){
        MWF.xDesktop.requireApp("Minder", "MineExplorer", null, false);
        if(this.currentExplorer){
            this.currentExplorer.destroy();
        }
        var options = (this.status && this.status.explorerStatus ) ? this.status.explorerStatus : {};
        this.currentExplorer = new MWF.xApplication.Minder.MineExplorer( this.contentNode, this, options );
        this.currentExplorer.load();
    },
    openSharedExplorer: function(){
        MWF.xDesktop.requireApp("Minder", "SharedExplorer", null, false);
        if(this.currentExplorer){
            this.currentExplorer.destroy();
        }
        var options = (this.status && this.status.explorerStatus ) ? this.status.explorerStatus : {};
        this.currentExplorer = new MWF.xApplication.Minder.SharedExplorer( this.contentNode, this, options );
        this.currentExplorer.load();
    },
    openReceivedExplorer: function(){
        MWF.xDesktop.requireApp("Minder", "ReceivedExplorer", null, false);
        if(this.currentExplorer){
            this.currentExplorer.destroy();
        }
        var options = (this.status && this.status.explorerStatus ) ? this.status.explorerStatus : {};
        this.currentExplorer = new MWF.xApplication.Minder.ReceivedExplorer( this.contentNode, this, options );
        this.currentExplorer.load();
    },
    openRecycleExplorer: function(){
        MWF.xDesktop.requireApp("Minder", "RecycleBinExplorer", null, false);
        if(this.currentExplorer){
            this.currentExplorer.destroy();
        }
        var options = (this.status && this.status.explorerStatus ) ? this.status.explorerStatus : {};
        this.currentExplorer = new MWF.xApplication.Minder.RecycleBinExplorer( this.contentNode, this, options );
        this.currentExplorer.load();
    },
    createNaviNode : function( d ){
        var _self = this;
        var node = new Element("div",{
            styles : this.css.naviItemNode,
            events : {
                click : function( ev ){
                    if( _self.currentAction == d.action )return;
                    node.setStyles( _self.css.naviItemNode_selected );
                    if(_self.currentNaviItemNode)_self.currentNaviItemNode.setStyles( _self.css.naviItemNode ).removeClass( 'mainColor_color' );
                    if(_self.currentNaviItemIconNode)_self.currentNaviItemIconNode.removeClass( 'mainColor_color' );
                    _self.currentNaviItemNode = node;
                    _self.currentNaviItemIconNode = iconNode;
                    _self.currentAction = d.action;
                    _self[ d.action ]();

                    node.addClass('mainColor_color');
                    iconNode.addClass('mainColor_color');
                },
                mouseenter: function (){
                    if( _self.currentNaviItemNode !== node )node.addClass('mainColor_color');
                    if( _self.currentNaviItemNode !== node )iconNode.addClass('mainColor_color');
                },
                mouseleave: function (){
                    if( _self.currentNaviItemNode !== node )node.removeClass('mainColor_color');
                    if( _self.currentNaviItemNode !== node )iconNode.removeClass('mainColor_color');
                }
            }
        }).inject( this.naviNode );
        var iconNode = new Element(`div.${d.icon}`, {
            styles : this.css.naviItemIconNode
        }).inject(node);
        new Element(`div`, {
            text : d.title,
            styles : this.css.naviItemTextNode
        }).inject(node);
        if( this.status && this.status.action && this.status.action == d.action ){
            node.click();
        }else if( this.options.defaultAction == d.action  ){
            node.click();
        }
    },
    recordStatus : function(){
        return {
            action : this.currentAction,
            explorerStatus : this.currentExplorer.recordStatus()
        }
    },
    resizeContent : function(){
        // var size = this.content.getSize();
        // this.naviNode.setStyle("height", size.y);
    },
    getDateDiff : function (publishTime) {
        if(!publishTime)return "";
        var dateTimeStamp = Date.parse(publishTime.replace(/-/gi, "/"));
        var minute = 1000 * 60;
        var hour = minute * 60;
        var day = hour * 24;
        var halfamonth = day * 15;
        var month = day * 30;
        var year = month * 12;
        var now = new Date().getTime();
        var diffValue = now - dateTimeStamp;
        if (diffValue < 0) {
            //날짜가 맞지 않으면 팝업으로 알림
            //alert("종료 날짜는 시작 날짜보다 이전일 수 없습니다!");
        }
        var yesterday = new Date().decrement('day', 1);
        var beforYesterday = new Date().decrement('day', 2);
        var yearC = diffValue / year;
        var monthC = diffValue / month;
        var weekC = diffValue / (7 * day);
        var dayC = diffValue / day;
        var hourC = diffValue / hour;
        var minC = diffValue / minute;
        if (yesterday.getFullYear() == dateTimeStamp.getFullYear() && yesterday.getMonth() == dateTimeStamp.getMonth() && yesterday.getDate() == dateTimeStamp.getDate()) {
            result = "어제 " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
        } else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
            result = "그저께 " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
        } else if (yearC > 1) {
            result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
        } else if (monthC >= 1) {
            //result= parseInt(monthC) + "개월 전";
            // s.getFullYear()+"년";
            result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
        } else if (weekC >= 1) {
            result = parseInt(weekC) + "주 전";
        } else if (dayC >= 1) {
            result = parseInt(dayC) + "일 전";
        } else if (hourC >= 1) {
            result = parseInt(hourC) + "시간 전";
        } else if (minC >= 1) {
            result = parseInt(minC) + "분 전";
        } else
            result = "방금";
        return result;
    }
});

MWF.xApplication.Minder.MainMobile = new Class({
    Extends: MWF.xApplication.Minder.MainPc,
    options: {
        "style": "mobile"
    },
    loadApplication: function(callback){
        this.content.setStyle('height', '100%');
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.content);
        this.node.loadCss(`../x_component_Minder/$Main/${this.options.style}/style.css`);
        this.loadView();
        if (callback) callback();
    },
    reload: function(){
        this.node.empty();
        this.loadView();
    },
    loadView: function(){
        this.node.loadHtml(
            `../x_component_Minder/$Main/${this.options.style}/main.html`,
            {
                module: this,
                bind: {
                    lp: this.lp
                }
            },
            ()=>{
                this.loadFolder();
            }
        );
    },
    loadFolder: function (){
        var setProperty = (tree)=>{
            tree.text = tree.name;
            tree.selectable = 'yes';
            tree.icon = 'folder-open';
            tree.expanded = true;
            tree.children?.forEach( (child)=>{
                setProperty(child);
            });
            return tree;
        };
        var p = o2.Actions.load('x_mind_assemble_control').MindFolderInfoAction.treeMyFolder();
        p.then((json)=>{
            var rootData = {
                id : "root",
                name : "루트 폴더",
                orderNumber : "1",
                description : "",
                selected: true,
                children: json.data
            };
            var tree = setProperty(rootData);
            this.nav.setMenu([tree]);
        });
        this.nav.addEventListener('select', (e)=>{
            var {text, id} = e.detail.data;
            this.closeFolder();
            this.folderName.set('text', text);
            this.loadList(id, e.detail.data);
        });
    },
    switchFolder: function(e){
        if( this.folderArea.hasClass('visible') ){
            this.hideFolderArea();
            this.mask.addClass('hide');
            this.arrow.addClass('up');
        }else{
            this.showFolderArea();
            this.mask.removeClass('hide');
            this.arrow.removeClass('up');
        }
    },
    closeFolder: function(e){
        this.hideFolderArea();
        this.mask.addClass('hide');
        this.arrow.addClass('up');
    },
    showFolderArea: function(){
        window.setTimeout(()=>{
            this.folderArea.removeClass('invisible');
            this.folderArea.addClass('visible');
        }, 10);
    },
    hideFolderArea: function(){
        window.setTimeout(()=>{
            this.folderArea.addClass('invisible');
            this.folderArea.removeClass('visible');
        }, 10);
    },
    loadList: function(folderId, folderData){
        if(folderData){
            this.currentFolderData = folderData;
        }
        if(folderId){
            this.currentFolderId = folderId;
        }
        this.listArea.empty();
        var p = o2.Actions.load('x_mind_assemble_control').MindInfoAction.listNextWithFilter('(0)', 100, {"folderId": folderId});
        p.then((json)=>{
            this.listArea.loadHtml(
                `../x_component_Minder/$Main/${this.options.style}/list.html`,
                {
                    module: this,
                    bind: {
                        lp: this.lp,
                        data: json.data
                    }
                },
                ()=>{}
            );
            this.fireEvent("postLoad");
        });
    },
    getCurrentFolderId: function (data){
        return this.currentFolderData.id;
    },
    getCurrentFolderData: function (){
        return this.currentFolderData;
    },
    showItemAction: function(e, data){
        this.actionSelect.removeClass('hide').addClass('invisibility');
        this.actionSelect._elements.box.click();
        this.currentItemData = data;
    },
    handleItemAction: function (e){
        switch(this.actionSelect.value){
            case "rename":
                this.rename(this.currentItemData);
                this.actionSelect.value = '';
                this.actionSelect._inputValue();
                break;
            case "delete":
                this.remove(this.currentItemData);
                this.actionSelect.value = '';
                this.actionSelect._inputValue();
                break;
        }
        this.currentItemData = null;
    },
    remove : function(data){
        if( !data )return;
        var _self = this;

        var p = MWF.getCenterPosition(this.content, 300, 150);
        var event = {
            "event": {
                "x": p.x,
                "y": p.y - 200,
                "clientX": p.x,
                "clientY": p.y - 200
            }
        };

        this.confirm("warn", event, "파일 삭제 확인", `${data.name} 파일을 삭제하시겠습니까?`, 350, 120, function () {
            o2.Actions.load('x_mind_assemble_control').MindInfoAction.destoryFromNormal(data.id, ()=> {
                _self.notice("파일을 삭제했습니다.");
                _self.loadList();
            });
            this.close();
        }, function () {
            this.close();
        });
    },
    rename : function(data){
        MWF.xDesktop.requireApp("Minder", "Common", null, false);
        if( !data )return;
        var form = new MWF.xApplication.Minder.ReNameForm(this, {
            name : data.name
        }, {
            style: 'v10_mobile',
            hasTop: false,
            bottom: 0,
            height: '50%',
            'minHeight': 400,
            width: '100%',
            "closeByClickMask" : true,
            id : data.id,
            onSave: ()=>{
                this.loadList();
            }
        }, {
            app: this
        });
        form.edit();
    },
    createMinder: function(){
        MWF.xDesktop.requireApp("Minder", "Common", null, false);
        var form = new MWF.xApplication.Minder.NewNameForm(this, {
        }, {
            style: 'v10_mobile',
            hasTop: false,
            bottom: 0,
            height: '50%',
            'minHeight': 400,
            width: '100%',
            "closeByClickMask" : true,
            onSave: ()=>{
                this.loadList();
            }
        }, {
            app: this
        });
        form.edit();
    },
    createFolder: function(){
        MWF.xDesktop.requireApp("Minder", "Common", null, false);
        var form = new MWF.xApplication.Minder.FolderForm(this, {
        }, {
            style: 'v10_mobile',
            hasTop: false,
            bottom: 0,
            height: '50%',
            'minHeight': 400,
            width: '100%',
            "closeByClickMask" : true,
            onSave: ()=>{
                this.loadList();
            }
        }, {
            app: this
        });
        form.create();
    },
    handleEventClick: function (e, data){
        this.openMinder(e, data);
    },
    openMinder: function(e, data){
        var appId = "MinderEditor"+data.id;
        var app = this.desktop.apps[appId];
        if (app){
            app.setCurrent();
        }else {
            this.desktop.openApplication(null, "MinderEditor", {
                "appId" : appId,
                "folderId" : data.folderId,
                "id" : data.id,
                "isEdited" : true,
                "isNew" : false
            });
        }
    }
});

if ((layout.mobile || COMMON.Browser.Platform.isMobile)){
    MWF.xApplication.Minder.Main = MWF.xApplication.Minder.MainMobile;
}else{
    MWF.xApplication.Minder.Main = MWF.xApplication.Minder.MainPc;
}


MWF.xApplication.Minder.History = new Class({
    initialize : function( app ){
        this.app = app;
        this.css = app.css;

        this.MAX_HISTORY = 100;

        this.lastSnap;
        this.patchLock;
        this.undoDiffs = [];
        this.redoDiffs = [];

        this.reset();
        this.app.addEvent('patchChange', this.changed.bind(this));
    },
    load : function( container ){
        if( this.node ){
            this.node.inject( container );
        }else{
            this.loadNode( container );
        }
    },
    loadNode : function( container ){
        this.node = new Element("div",{
            styles : this.css.historyNode
        }).inject( container );

        this.undoNode = new Element("div",{ styles : this.css.undoNode }).inject( this.node );

        this.redoNode = new Element("div",{  styles : this.css.redoNode }).inject( this.node );

        this.patchNode = new Element("div", { styles : this.css.patchNode }).inject( this.node );
    },
    reset: function () {
        this.undoDiffs = [];
        this.redoDiffs = [];
        this.lastSnap = "mine/root";
    },

    makeUndoDiff: function( snap ) {
        if (snap != this.lastSnap) {
            this.undoDiffs.push(snap);
            while (this.undoDiffs.length > this.MAX_HISTORY) {
                this.undoDiffs.shift();
            }
            this.lastSnap = snap;
            return true;
        }
    },

    makeRedoDiff:function( snap ) {
        this.redoDiffs.push( snap );
    },

    undo: function() {
        this.patchLock = true;
        var undoDiff = this.undoDiffs.pop();
        if (undoDiff) {
            this.app.applyPatches(undoDiff);
            this.makeRedoDiff( undoDiff );
        }
        this.patchLock = false;
    },

    redo: function() {
        this.patchLock = true;
        var redoDiff = this.redoDiffs.pop();
        if (redoDiff) {
            this.app.applyPatches(redoDiff);
            this.makeUndoDiff( redoDiff );
        }
        this.patchLock = false;
    },

    changed: function( snap ) {
        if (this.patchLock) return;
        if (this.makeUndoDiff( snap )) this.redoDiffs = [];
    },

    hasUndo: function() {
        return !!this.undoDiffs.length;
    },

    hasRedo: function() {
        return !!this.redoDiffs.length;
    }
});



