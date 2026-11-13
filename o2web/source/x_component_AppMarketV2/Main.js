MWF.require("MWF.widget.MaskNode", null, false);
o2.requireApp("AppMarketV2", "RecommendContent", null, false);
MWF.xApplication.AppMarketV2.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"mvcStyle": "style.css",
		"name": "AppMarketV2",
		"icon": "icon.png",
		"width": "1000",
		"height": "700",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.AppMarketV2.LP.title,
		"minHeight": 700
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.AppMarketV2.LP;
		this.actions = MWF.Actions.load("x_program_center");
		this.viewPath = this.path+this.options.style+"/view.html";
		this.iconPath = this.path+this.options.style+"/icon/";
	},
	mask: function(){
		if (!this.maskNode){
			this.marketnode.setStyle("overflow","hidden");
			this.maskNode = new MWF.widget.MaskNode(this.marketnode, {"style": "bam"});
			this.maskNode.load();
		}
	},
	unmask: function(){
		if (this.maskNode) this.maskNode.hide(function(){
			MWF.release(this.maskNode);
			this.maskNode = null;
			this.marketnode.setStyle("overflow","auto");
		}.bind(this));
	},
	loadApplication: function(callback){
		if (MWF.AC.isAdministrator()){	//this.checkO2Collect();

			this.content.loadHtml(this.viewPath, {"bind": {"lp": this.lp}, "module": this}, function(){
				if (!this.options.isRefresh){
					this.maxSize(function(){
						//클라우드 서버에 등록되었는지 확인
						this.actions.CollectAction.login(//플랫폼이 제공하는 메서드
							function( json ){ //서비스 호출 성공 콜백 함수, json은 서비스가 반환한 데이터
								if (json.type && json.type=="success"){
									this.loadApp(callback);
								}
							}.bind(this),
							function( json ){ //서비스 호출 성공 콜백 함수, json은 서비스가 반환한 데이터
								errtype = JSON.parse(json.response).type;
								if (errtype && errtype=="error"){
									//o2.xDesktop.notice("error", {x: "right", y:"top"}, JSON.parse(json.response).message+"시스템 설정 - 클라우드 서비스 설정 - 연결 설정에서 등록 후 O2 클라우드에 연결하십시오");
									this.loadCloudConnectTip(callback);
								}
							}.bind(this)
							,false //동기 실행
						);

					}.bind(this));
				}else{
					this.loadApp(callback);
				}
			}.bind(this));
		}else{
			o2.xDesktop.notice("error", {x: "right", y:"top"}, this.lp.accessDenyNotice);
		}
	},
	loadApp: function(callback){
		//this.initNode();
		this.initNodeSize();

		this.recommondLoaded = false;
		this.applicationsLoaded = true;

		this.loadRecommondContent(function(){ this.recommondLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadApplicationsContent(function(){ this.applicationsLoaded = true; this.checkAppLoaded(callback); }.bind(this));
	},
	loadCloudConnectTip:function(callback){
		this.initNodeSize();
		o2.requireApp("AppMarketV2", "CloudConnectTip", function(){
			this.cloudTip = new MWF.xApplication.AppMarketV2.CloudConnectTip(this, this.cloudConnectTipNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));

	},
	checkAppLoaded: function(callback){
		if (this.recommondLoaded && this.applicationsLoaded){
			if (callback) callback();
		}
	},
	initNodeSize: function(){
		this.resizeNodeSize();
		this.addEvent("resize", this.resizeNodeSize.bind(this));
	},
	resizeNodeSize: function(){
		var size = this.content.getSize();
		var edge = this.marketnode.getEdgeHeight();
		var height = size.y - edge;
		if (height<this.options.minHeight) height = this.options.minHeight;
		this.marketnode.setStyle("height", ""+height+"px");
	},
	loadRecommondContent: function(callback){
		debugger;
		this.recommendContent = new MWF.xApplication.AppMarketV2.RecommendContent(this, this.topRecommendNode, {
			"onLoad": function(){if (callback) callback();}
		});
	},

	loadApplicationsContent: function(callback){

		o2.requireApp("AppMarketV2", "ApplicationsContent", function(){
			this.applicationsContent = new MWF.xApplication.AppMarketV2.ApplicationsContent(this, this.applicationsNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));

	}
});
