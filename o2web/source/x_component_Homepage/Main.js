MWF.xApplication.Homepage.options.multitask = false;
o2.requireApp("Homepage", "TaskContent", null, false);
MWF.xApplication.Homepage.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"mvcStyle": "style.css",
		"name": "Homepage",
		"icon": "icon.png",
		"width": "1200",
		"height": "800",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.Homepage.LP.title,
		"minHeight": 700,
		"cardGap": 20
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Homepage.LP;
		this.viewPath = this.path+this.options.style+"/view.html";
	},
	loadApplication: function(callback){
		this.content.loadHtml(this.viewPath, {"bind": {"lp": this.lp}, "module": this}, function(){
			if (!this.options.isRefresh){
				this.maxSize(function(){
					this.loadApp(callback);
				}.bind(this));
			}else{
				this.loadApp(callback);
			}
		}.bind(this));
	},
	loadApp: function(callback){
		//this.initNode();
		this.loadMyInfo();
		this.initNodeSize();

		this.taskLoaded = false;
		this.inforLoaded = false;
		this.meetingLoaded = false;
		this.fileLoaded = false;
		this.calenderLoaded = false;

		this.loadTaskContent(function(){ this.taskLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadInforContent(function(){ this.inforLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadMeetingContent(function(){ this.meetingLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadFileContent(function(){ this.fileLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadCalendarContent(function(){ this.calenderLoaded = true; this.checkAppLoaded(callback); }.bind(this));
	},
	checkAppLoaded: function(callback){
		if (this.taskLoaded && this.inforLoaded && this.meetingLoaded && this.fileLoaded && this.calenderLoaded){
			if (callback) callback();
		}
	},
	// initNode: function(){
	// 	this.node = this.content.getElement(".o2_homepage_content");
	// 	this.calendarContentNode = this.content.getElement(".o2_homepage_calendar_content");
	// 	this.taskContentNode = this.content.getElement(".o2_homepage_task_content");
	// 	this.meetingContentNode = this.content.getElement(".o2_homepage_meeting_content");
	// 	this.inforContentNode = this.content.getElement(".o2_homepage_infor_content");
	// 	this.fileContentNode = this.content.getElement(".o2_homepage_file_content");
	//
	// 	this.rightLayout = this.content.getElement(".o2_homepage_layout_right");
	// 	this.ltlLayout = this.content.getElement(".o2_homepage_layout_leftTopLeft");
	// 	this.ltrLayout = this.content.getElement(".o2_homepage_layout_leftTopRight");
	// 	this.lblLayout = this.content.getElement(".o2_homepage_layout_leftBottomLeft");
	// 	this.lbrLayout = this.content.getElement(".o2_homepage_layout_leftBottomRight");
	// },
	initNodeSize: function(){
		this.resizeNodeSize();
		this.addEvent("resize", this.resizeNodeSize.bind(this));
	},
	loadMyInfo: function(){
		var user = layout.session.user || {};
		var detail = layout.session.userDetail || {};
		var name = o2.name.cn(user.name || user.distinguishedName || "");
		var unit = "";
		if (detail.identityList && detail.identityList.length){
			unit = detail.identityList[0].unitName || o2.name.cn(detail.identityList[0].unit || "");
		}
		if (!unit && detail.unitList && detail.unitList.length) unit = detail.unitList[0].name || "";

		this.myInfoNameNode.set("text", name);
		this.myInfoUnitNode.set("text", unit || this.lp.noUnit);
		this.myInfoDateNode.set("text", (new Date()).format(this.lp.dateFormat));
		this.myInfoAvatarNode.set("text", name.substr(0, 1));

		var userNode = layout.desktop && layout.desktop.userInforNode;
		var bg = (userNode && userNode.getFirst()) ? userNode.getFirst().getStyle("background-image") : "";
		if (bg && bg!=="none"){
			this.myInfoAvatarNode.set("text", "").setStyle("background-image", bg);
		}
	},
	resizeNodeSize: function(){
		var size = this.content.getSize();
		var edge = this.node.getEdgeHeight();
		var height = size.y - edge;
		if (height<this.options.minHeight) height = this.options.minHeight;
		this.node.setStyle("height", ""+height+"px");

		var rowHeight = Math.floor((height - this.options.cardGap)/2);
		var rightHeight = height - this.calendarContentNode.getEdgeHeight();
		var sideHeight = height - this.myInfoNode.getSize().y - this.meetingContentNode.getEdgeHeight();

		this.calendarContentNode.setStyle("height", ""+rightHeight+"px");
		this.meetingContentNode.setStyle("height", ""+sideHeight+"px");
		this.taskContentNode.setStyle("height", ""+(rowHeight - this.taskContentNode.getEdgeHeight())+"px");
		this.inforContentNode.setStyle("height", ""+(rowHeight - this.inforContentNode.getEdgeHeight())+"px");
		this.fileContentNode.setStyle("height", ""+(rowHeight - this.fileContentNode.getEdgeHeight())+"px");
	},
	loadTaskContent: function(callback){
		this.taskContent = new MWF.xApplication.Homepage.TaskContent(this, this.taskContentNode, {
			"onLoad": function(){if (callback) callback();}
		});
	},
	loadInforContent: function(callback){
		o2.requireApp("Homepage", "InforContent", function(){
			this.inforContent = new MWF.xApplication.Homepage.InforContent(this, this.inforContentNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
	},
	loadMeetingContent: function(callback){
		o2.requireApp("Homepage", "MeetingContent", function(){
			this.inforContent = new MWF.xApplication.Homepage.MeetingContent(this, this.meetingContentNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
	},
	loadFileContent: function(callback){
		o2.requireApp("Homepage", "FileContent", function(){
			this.inforContent = new MWF.xApplication.Homepage.FileContent(this, this.fileContentNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
	},
	loadCalendarContent: function(callback){
		o2.requireApp("Homepage", "CalendarContent", function(){
			this.inforContent = new MWF.xApplication.Homepage.CalendarContent(this, this.calendarContentNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
	}
});



