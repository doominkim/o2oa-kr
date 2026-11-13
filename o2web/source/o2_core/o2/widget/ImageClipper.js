o2.widget = o2.widget || {};

o2.widget.ImageClipper = o2.ImageClipper = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default",
		"path": o2.session.path+"/widget/$ImageClipper/",
		"imageUrl" : "",

		"action" : null, //업로드 서비스, 선택. 설정하지 않으면 공용 이미지 서비스 사용
		"method": "", //action의 메서드 사용
		"parameter": {}, //action 사용 시 url 매개변수

		"data": null, //formdata의 data, H5에서 유효
		"reference": "",  //공용 이미지 서비스로 업로드할 때의 매개변수
		"referenceType" : "", //공용 이미지 서비스로 업로드할 때의 매개변수, 현재 processPlatformJob, processPlatformForm, portalPage, cmsDocument, forumDocument 지원
		"description" : "",

		"aspectRatio" : 1, //생성 이미지의 가로세로 비율, 0은 제한 없음
		"resultMaxSize" : 800, //생성 이미지의 최대 너비 또는 높이

		"editorSize" : 340, //H5에서 유효, 그래픽 컨테이너
		"frameMinSize" : 30, //H5에서 유효, 선택 상자의 최소 너비
		"previewerSize" : 260, //H5에서 유효, 미리보기 영역 크기

		"showPreviewer" : true,	//H5에서 유효
		"fromLocalEnable" : true,  //H5에서 유효, 로컬 이미지
		"fromFileEnable" : true, //H5에서 유효, 클라우드 파일 이미지
		"resetEnable" : false, //H5에서 유효
		"uploadSourceEnable" : true, //H5에서 유효
		"downloadSourceEnable": true //H5에서 유효
	},
	initialize: function(node, options){
		this.node = node;
		if( isNaN( options.aspectRatio ) )options.aspectRatio = 0;
		if( isNaN( options.resultMaxSize ) )options.resultMaxSize = 800;
		this.setOptions(options);

		this.path = this.options.path || (o2.session.path+"/widget/$ImageClipper/");
		this.cssPath = this.path + this.options.style+"/css.wcss";

		this._loadCss();
		this.fireEvent("init");
	},
	load: function( imageBase64 ){
		this.container = new Element("div.container", { styles :  this.css.container}).inject(this.node);

		this.container.addEvent("selectstart", function(e){
			e.preventDefault();
			e.stopPropagation();
		});

		if( this.checkBroswer() ){
			//this._loadWithSWF();
			this._loadWithH5( imageBase64 );
		}else{
			this._loadWithSWF();
		}

	},
	_loadWithSWF : function(){
		this.clipper = new o2.widget.FlashImageClipper( this.container, this.options, this );
		this.clipper.load()
	},
	_loadWithH5 : function( imageBase64 ){
		this.clipper = new o2.widget.HTML5ImageClipper( this.container, this.options, this );
		this.clipper.load( imageBase64 )
	},
	_openDownloadDialog: function(url, saveName){
		/**
		 * 다운로드 대화상자를 여는 공통 메서드, 구체적인 호환성은 테스트하지 않음
		 * @param url 다운로드 주소, blob 객체도 가능, 필수
		 * @param saveName 저장 파일 이름, 선택
		 */
		if( Browser.name !== 'ie' ){
			if(typeof url == 'object' && url instanceof Blob){
				url = URL.createObjectURL(url); // blob 주소 생성
			}
			var aLink = document.createElement('a');
			aLink.href = url;
			aLink.download = saveName || ''; // HTML5에 새로 추가된 속성으로 저장 파일 이름 지정, 확장자는 생략 가능, 주의: file:/// 모드에서는 적용되지 않음
			var event;
			if(window.MouseEvent && typeOf( window.MouseEvent ) == "function" ) event = new MouseEvent('click');
			else
			{
				event = document.createEvent('MouseEvents');
				event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
			}
			aLink.dispatchEvent(event);
		}else{
			window.navigator.msSaveBlob( url, saveName);
		}
	},
	download: function(){
		// var image = this.getBase64Image();
		// if( image ){
		// 	this._openDownloadDialog( this.getBase64Image(), this.clipper.fileName || new Date().format("db") );
		// }

	},
	downloadSourceImage: function(){
		this.clipper.downloadSourceImage();
	},
	downloadResizedImage: function(){
		this.clipper.downloadResizedImage();
	},
	uploadImage: function(  success, failure  ){
		this.clipper.uploadImage( success, failure )
	},
	getFormData : function(){
		return this.clipper.getFormData()
	},
	getResizedImage : function(){
		return this.clipper.getResizedImage();
	},
	getBase64Code : function(){
		return this.clipper.getBase64Code();
	},
	getBase64Image: function(){
		return this.clipper.getBase64Image();
	},
	close : function(){
		this.clipper.close();
	},
	checkBroswer : function(){
		if( window.Uint8Array && window.HTMLCanvasElement && window.atob && window.Blob){
			this.available = true;
			return true;
		}else{
			this.available = false;
			//this.container.set("html", "<p>사용 중인 브라우저가 다음 기능을 지원하지 않습니다:</p><ul><li>canvas</li><li>Blob</li><li>Uint8Array</li><li>FormData</li><li>atob</li></ul>");
			return false;
		}
	}
});

o2.widget.FlashImageClipper = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default",
		"path": o2.session.path+"/widget/$ImageClipper/",
		"imageUrl" : "",

		"action" : null, //선택. 설정하지 않으면 공용 이미지 서비스 사용
		"method": "", //action의 메서드 사용
		"parameter": {}, //action 사용 시 url 매개변수

		"reference": "",  //공용 이미지 서비스로 업로드할 때의 매개변수
		"referenceType" : "", //공용 이미지 서비스로 업로드할 때의 매개변수, 현재 processPlatformJob, processPlatformForm, portalPage, cmsDocument, forumDocument 지원
		"description" : "",

		"aspectRatio" : 1, //생성 이미지의 가로세로 비율, 0은 제한 없음
		"resultMaxSize" : 800 //생성 이미지의 최대 너비 또는 높이
	},
	initialize: function(node, options, parent){
		this.container = node;
		this.setOptions(options);
		this.parent = parent;

		if( !this.options.aspectRatio ){
			this.options.aspectRatio = 1;
		}
		//this.path = this.options.path || (o2.session.path+"/widget/$ImageClipper/");
		//this.cssPath = this.path + this.options.style+"/css.wcss";

		this.Previer_MaxSize = 240;

		this.css = this.parent.css;
		this.fireEvent("init");
	},
	load : function(){
		window.uploadevent_flash = function( value ){
			if( typeOf( value ) == "number" ){
				value += '';
				switch(value){
					case '1':
						alert( o2.LP.widget.uploadSuccess );
						var time = new Date().getTime();
						ok();
						break;

					case '2':
						if( this.isUploading ){
							return 0;
						}else{
							this.isUploading = true;
							return 1;
						}
						//if(confirm('js call upload')){
						//	return 1;
						//}else{
						//	return 0;
						//}
						break;

					case '-1':
						this.isUploading = false;
						//alert("취소했습니다!");
						//cancel();

						break;
					case '-2':
						this.isUploading = false;
						alert( o2.LP.widget.uploadFail );
						window.location.href = "#";
						break;

					default:
					//alert(typeof(status) + ' ' + status);
				}
			}else{
				this.isUploading = false;
				var json = JSON.parse( value );
				if( json.type == "error" ){
					alert( json.message );
				}else{
					if(this.uploadSuccess)this.uploadSuccess( json.data );
				}
			}
		}.bind(this);

		this.getUploadUrl( function(){ 
			this.renderFlash()
		}.bind(this));
	},
	renderFlash : function(){
		this.contentNode = new Element("div.contentNode", {
			styles :  this.css.contentNode,
			id : "imageclipper_swf"
		}).inject(this.container);
		var path = o2.session.path+"/widget/$ImageClipper/";
		var imageUrl = this.options.imageUrl || path + "flash_default.png";
		if( this.options.aspectRatio > 1 ){
			var previewerWidth = this.Previer_MaxSize;
			var previewerHeight = this.Previer_MaxSize / this.options.aspectRatio;
		}else{
			var previewerWidth = this.Previer_MaxSize * this.options.aspectRatio;
			var previewerHeight = this.Previer_MaxSize;
		}
		COMMON.AjaxModule.load( path +"swfobject.js", function () {
			swfobject.embedSWF( path+"FaustCplus.swf", this.contentNode, "650", "370", "9.0.0", path+"expressInstall.swf", {
				"jsfunc":"uploadevent_flash",
				"imgUrl": imageUrl,
				"tip" : this.options.description,
				"aspectRatio" : this.options.aspectRatio,
				"reqContentDisposition" : "", //"" 또는 formData
				"imageUploadKey" : "thumb.jpg", //reqContentDisposition이 formdata일 때 유효, 파일 이름
				"imageSrcUploadKey" : "src.jpg", //reqContentDisposition이 formdata일 때 유효, 파일 이름
				"showZoom" : false,
				"showSaveBtns" : false,
				"showTurn" : false,
				"showColorAdjuster" : false,
				"pid":"75642723",
				"uploadSrc":false,
				"showBrow":true,
				"showCame":false,
				"pSize":"300|300|"+previewerWidth+"|"+previewerHeight, //|80|40|40|20"//,
				"uploadUrl": this.uploadUrl  //+encodeURI("<계산된 값>")+"?shortname=<계산된 값>?filepath=<계산된 값>?sizes=160|80|40?imgnames=face.jpg|face_medium.jpg|face_small.jpg"	//sizes 여러 이미지의 비율
			}, {
				menu: "false",
				scale: "noScale",
				allowFullscreen: "true",
				allowScriptAccess: "always",
				wmode:"transparent",
				bgcolor: "#FFFFFF"
			}, {
				id:"FaustCplus"
			}, function( swf ){
				this.swf = swf;
			}.bind(this));
		}.bind(this))
	},
	getUploadUrl : function( callback ){
		if( this.options.action ){
			this.action = (typeOf(this.options.action)=="string") ? o2.Actions.get(action).action : this.options.action;
			this.action.getActions(function(){
				var url = this.action.actions[this.options.method];
				url = this.action.address+url.uri;
				if(this.options.parameter){
					Object.each(this.options.parameter, function(v, k){
						url = url.replace("{"+k+"}", v);
					});
				}
				this.uploadUrl = url;
				if(callback)callback(url);
			}.bind(this));
		}else{
			//공용 이미지 서비스
			var addressObj = layout.serviceAddressList["x_file_assemble_control"];
			var defaultPort = layout.config.app_protocol==='https' ? "443" : "80";
			if (addressObj){
				var appPort = addressObj.port || window.location.port;
				var address = layout.config.app_protocol+"//"+(addressObj.host || window.location.hostname)+((!appPort || appPort.toString()===defaultPort) ? "" : ":"+appPort)+addressObj.context;
				// var address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context;
			}else{
				var host = layout.desktop.centerServer.host || window.location.hostname;
				var port = layout.desktop.centerServer.port || window.location.port;
				var address = layout.config.app_protocol+"//"+host+((!port || port.toString()===defaultPort) ? "" : ":"+port)+"/x_file_assemble_control";
			}
			var url = "/jaxrs/file/upload/referencetype/"+ this.options.referenceType + "/reference/" + this.options.reference + "/scale/" + this.options.resultMaxSize;
			this.uploadUrl = address+url;
			if(callback)callback(this.uploadUrl);
		}
	},
	uploadImage: function(  success, failure  ){
		this.uploadSuccess = success;
		if( this.swf ){
			this.swf.ref["jscall_updateAvatar"](); //JsCallAs는 flash의 addCallback 함수
		}
	},
	getFormData : function(){
		return true;
	},
	getResizedImage : function(){
		return true;
	},
	getBase64Code : function(){
		return true;
	},
	getBase64Image: function(){
		return true;
	},
	close : function(){

	}
});

o2.widget.HTML5ImageClipper = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default",
		"path": o2.session.path+"/widget/$ImageClipper/",
		"imageUrl" : "",

		"editorSize" : 340, //그래픽 컨테이너
		"aspectRatio" : 1, //생성 이미지의 가로세로 비율, 0은 제한 없음
		"frameMinSize" : 30, //선택 상자의 최소 너비
		"previewerSize" : 260, //미리보기 영역 크기
		"resultMaxSize" : 800, //생성 이미지의 최대 너비 또는 높이

		"action" : null, //선택. 설정하지 않으면 공용 이미지 서비스 사용
		"method": "", //action의 메서드 사용
		"parameter": {}, //action 사용 시 url 매개변수

		"data": null, //formdata의 data
		"reference": "",  //공용 이미지 서비스로 업로드할 때의 매개변수
		"referenceType" : "", //공용 이미지 서비스로 업로드할 때의 매개변수, 현재 processPlatformJob, processPlatformForm, portalPage, cmsDocument, forumDocument 지원

		"uploadSourceEnable" : true, //원본 이미지 업로드 허용 여부
		"downloadSourceEnable": true,

		"showPreviewer" : true,
		"fromLocalEnable" : true,  //로컬 이미지
		"fromFileEnable" : true, //클라우드 파일 이미지
		"resetEnable" : false,
		"description" : ""
	},
	initialize: function(node, options, parent){
		this.container = node;
		this.setOptions(options);
		this.parent = parent;

		//this.path = this.options.path || (o2.session.path+"/widget/$ImageClipper/");
		//this.cssPath = this.path + this.options.style+"/css.wcss";

		this.fileName = "untitled.png";
		this.fileType = "image/png";
		this.fileSize = null;

		this.css = this.parent.css;
		this.fireEvent("init");
	},
	load : function( imageBase64 ){
		this.lastPoint=null;

		this.loadToolBar();

		this.contentNode = new Element("div.contentNode", { styles :  this.css.contentNode}).inject(this.container);
		this.loadEditorNode();
		this.loadResultNode();

		if( this.options.description ){
			this.loadDescriptionNode();
		}

		if( this.options.imageUrl ){
			this.loadImageAsUrl( this.options.imageUrl );
		}
		if( imageBase64 ){
			this.loadImageAsFile( this.base64ToBlob( imageBase64 ) );
		}
	},
	uploadImage: function(  success, failure  ){
		if( this.resizedImage ){
			if( this.options.action ){
				this.action = (typeOf(this.options.action)=="string") ? o2.Actions.get(this.options.action).action : this.options.action;
				this.action.invoke({
					"name": this.options.method,
					"async": true,
					"data": this.getFormData(),
					"file": this.resizedImage,
					"parameter": this.options.parameter,
					"success": function(json){
						success(json)
					}.bind(this)
				});
			}else{
				//공용 이미지 업로드 서비스
				var maxSize = this.options.resultMaxSize;
				if( this.uploadSourceInput && this.uploadSourceInput.get("checked") ){
					maxSize = 0; //원본 이미지 업로드
				}
				o2.xDesktop.uploadImageByScale(
					this.options.reference,
					this.options.referenceType,
					maxSize,
					this.getFormData(),
					this.resizedImage,
					success,
					failure
				);
			}
		}else{
		}
	},
	downloadSourceImage: function(){
		if( this.imageNode && this.imageNode.get("src") ) {
			this.parent._openDownloadDialog( this.imageNode.get("src"), this.fileName || new Date().format("db"));
		}
	},
	downloadResizedImage: function(){
		if( this.resizedImage ){
			this.parent._openDownloadDialog( this.resizedImage, this.fileName || new Date().format("db"));
		}
	},
	getFormData : function(){
		var formData = new FormData();
		formData.append('file',this.resizedImage, this.fileName );
		if( this.options.data ){
			Object.each(this.options.data, function(v, k){
				formData.append(k, v)
			});
		}
		return formData;
	},
	getResizedImage : function(){
		return this.resizedImage;
	},
	getBase64Code : function(){
		return this.base64Code;
	},
	getBase64Image: function(){
		if( !this.base64Code )return null;
		return 'data:'+ this.fileType +';base64,' + this.base64Code;
	},
	checkBroswer : function(){
		if( window.Uint8Array && window.HTMLCanvasElement && window.atob && window.Blob){
			this.available = true;
			return true;
		}else{
			this.available = false;
			//this.container.set("html", "<p>사용 중인 브라우저가 다음 기능을 지원하지 않습니다:</p><ul><li>canvas</li><li>Blob</li><li>Uint8Array</li><li>FormData</li><li>atob</li></ul>");
			return false;
		}
	},
	close : function(){
		this.docBody.removeEvent("touchmove",this.bodyMouseMoveFun);
		this.docBody.removeEvent("mousemove",this.bodyMouseMoveFun);
		this.docBody.removeEvent("touchend",this.bodyMouseEndFun);
		this.docBody.removeEvent("mouseup",this.bodyMouseEndFun);

		this.container.destroy();
		delete this;
	},
	loadToolBar: function(){
		this.uploadToolbar = new Element("div.uploadToolbar", {
			"styles" : this.css.uploadToolbar
		}).inject(this.container);
		//var width = this.options.editorSize;
		//this.uploadToolbar.setStyle( "width" ,  width+ "px");

		if( this.options.fromLocalEnable ){
			this.uploadLocalImage = new Element("button.uploadActionNode",{
				"styles" : this.css.uploadActionNode,
				"text" : o2.LP.widget.selectLocalImage
			}).inject(this.uploadToolbar);
			this.uploadLocalImage.addEvents({
				"click": function(){ this.fileNode.click(); }.bind(this)
			});


			this.fileNode = new Element("input.file", {
				"type" : "file",
				"accept":"image/*",
				"styles" : {"display":"none"}
			}).inject(this.container);
			this.fileNode.addEvent("change", function(event){
				var file=this.fileNode.files[0];
				this.fileType = file.type;
				this.fileName = file.name;
				this.fileSize = file.size;
				this.loadImageAsFile( file );
			}.bind(this));
		}

		this._createUploadButtom();

		this.uploadCloudFileArea = new Element("span").inject( this.uploadToolbar );
		if( this.options.fromFileEnable ){
			var url = o2.session.path+"/xDesktop/$Layout/applications.json";
			o2.getJSON(url, function(json){
				json.each( function(obj){
					if( obj.name == "File" || obj.path == "File" ){
						this.uploadCloudFile = new Element("button.uploadActionNode",{
							"styles" : this.css.uploadActionNode,
							"text" : o2.LP.widget.selectCloudImage
						}).inject(this.uploadCloudFileArea );
						this.uploadCloudFile.addEvents({
							"click": function(){ this.selectFileImage(
								function( url, id ,attachmentInfo ){
									this.fileName = attachmentInfo.name;
									this.fileType = ["jpeg","jpg"].contains( attachmentInfo.extension.toLowerCase() ) ? "image/jpeg" : "image/png" ;
									this.fileSize = attachmentInfo.length;
									this.loadImageAsUrl( url );
								}.bind(this)
							); }.bind(this)
						});
					}
				}.bind(this));
			}.bind(this));
		}

		if( this.options.uploadSourceEnable ){
			this.uploadSourceInput = new Element( "input", {
				type : "checkbox",
				events : {
					change : function(){
						this.clipImage();
					}.bind(this)
				}
			}).inject( this.uploadToolbar );
			new Element("span",{
				text : o2.LP.widget.uploadOriginalImage
			}).inject( this.uploadToolbar );
		}

		if( this.options.resetEnable ){
			this.resetAction = new Element("button.resetAction",{
				"styles" : this.css.resetActionNode,
				"text" : o2.LP.widget.empty
			}).inject(this.uploadToolbar);
			this.resetAction.addEvents({
				"click": function(){ this.reset(); }.bind(this)
			});
		}

		if( this.options.downloadSourceEnable ){
			this.downloadSourceAction = new Element("button.downloadSourceAction",{
				"styles" : this.css.resetActionNode,
				"text" : o2.LP.widget.download
			}).inject(this.uploadToolbar);
			this.downloadSourceAction.addEvents({
				"click": function(){ this.downloadSourceImage(); }.bind(this)
			});
		}


	},
	_createUploadButtom : function(){

	},
	reset: function(){
		this.fileName = "untitled.png";
		this.fileType = "image/png";
		this.fileSize = null;
		this.resizedImage = "";
		this.base64Code = "";
		this.resetImage();
		this.setFrameSize({width:0, height:0});
		this.frameOffset.top = 0;
		this.frameOffset.left = 0;
		this.frameNode.setStyles({
			top:0,
			left:0
		});
		this.resultNode.empty();
		this.editorContainer.setStyles( this.css.editorContainer );
		this.imageNode.setStyle("display","none");
		this.innerNode.setStyles({
			"width" : 0,
			"height" : 0
		})
	},
	selectFileImage : function( callback ){
		var _self = this;
		o2.xDesktop.requireApp("File", "FileSelector", function(){
			_self.selector_cloud = new o2.xApplication.File.FileSelector( document.body ,{
				"style" : "default",
				"title": o2.LP.widget.selectCloudImage,
				"copyToPublic" : false,
				//"reference" :  _self.options.reference,
				//"referenceType" : _self.options.referenceType,
				"listStyle": "preview",
				"selectType" : "images",
				"onPostSelectAttachment" : function( url, id, attachmentInfor ){
					if(callback)callback(url, id, attachmentInfor );
				}
			});
			_self.selector_cloud.load();
		}, true);
	},
	loadResultNode: function(){
		if( this.options.showPreviewer ){
			this.resultContainer = new Element("div", {"styles":this.css.resultContainer}).inject(this.contentNode);
			var containerHeight = Math.max( this.options.editorSize ,this.options.previewerSize ) - (parseInt(this.resultContainer.getStyle("padding-left"))*2);
			this.resultContainer.setStyles( {
				"width": this.options.previewerSize+"px",
				"height": containerHeight +"px"
			} );

			this.resultTitleNode = new Element("div", {
					"styles":this.css.resultTitleNode,
					"text" : o2.LP.widget.preview
				}
			).inject(this.resultContainer);

			var titleHeight = this.resultTitleNode.getSize().y;
			var nodeHeight = this.options.aspectRatio ? ( this.options.previewerSize / this.options.aspectRatio) : this.options.previewerSize ;
			this.resultNode = new Element("div.resultNode", {
				"styles":this.css.resultNode
			}).inject(this.resultContainer);
			this.resultNode.setStyles( {
				"padding-top": (containerHeight-titleHeight-nodeHeight)/2 - 10 +"px"
			} );
		}else{
			this.resultNode = new Element("div", {
				styles : {display : "none"}
			}).inject(this.contentNode);
		}
	},
	loadEditorNode: function(){
		this.docBody = window.document.body;

		this.editorContainer = new Element("div.editorContainer", { styles :  this.css.editorContainer}).inject(this.contentNode);
		this.editorContainer.setStyles( {
			"width": this.options.editorSize+"px",
			"height": this.options.editorSize+"px"
		} );

		this.editorNode = new Element("div.editorNode", { styles :  this.css.editorNode}).inject(this.editorContainer);

		this.innerNode = new Element("div.innerNode",{ styles :  this.css.innerNode } ).inject(this.editorNode);

		this.imageNode = new Element("img",{
			styles :  this.css.imageNode,
			crossOrigin :"use-credentials"
		}).inject(this.innerNode);
		this.imageNode.ondragstart = function(){
			return false;
		};

		this.frameNode = new Element("div.frameNode",{ styles :  this.css.frameNode }).inject(this.innerNode);
		this.frameOffset={ top:0, left:0 };
		this.frameNode.addEvents({
			"touchstart" : function(ev){ this.getOffset(ev) }.bind(this),
			"mousedown" : function(ev){ this.getOffset(ev) }.bind(this),
			"touchmove" : function(ev){
				if(!this.lastPoint)return;
				var offset= this.getOffset(ev);
				if( this.resizeMode ){
					this.resizeFrames( offset );
				}else{
					this.moveFrames( offset );
				}
				ev.stopPropagation();
			}.bind(this),
			"mousemove" : function(ev){
				if(!this.lastPoint)return;
				var offset= this.getOffset(ev);
				if( this.resizeMode ){
					this.resizeFrames( offset );
				}else{
					this.moveFrames( offset );
				}
				ev.stopPropagation();
			}.bind(this),
			"touchend" : function(ev){
				this.lastPoint=null;
				if( this.resizeMode ){
					this.frameNode.setStyle("cursor", "move" );
					this.docBody.setStyle("cursor", "default" );
					this.resizeMode = false;
				}
				this.clipImage();
				ev.stopPropagation();
			}.bind(this),
			"mouseup" : function(ev){
				this.lastPoint=null;
				if( this.resizeMode ){
					this.frameNode.setStyle("cursor", "move" );
					this.docBody.setStyle("cursor", "default" );
					this.resizeMode = false;
				}
				this.clipImage();
				ev.stopPropagation();
			}.bind(this)
		});

		this.reizeNode = new Element("div.reizeNode",{ styles :  this.css.reizeNode }).inject(this.frameNode);
		this.reizeNode.addEvents({
			"touchstart" : function(ev){
				this.frameNode.setStyle("cursor", "nw-resize" );
				this.docBody.setStyle("cursor", "nw-resize" );
				this.resizeMode = true;
				this.getOffset(ev);
				ev.stopPropagation();
			}.bind(this),
			"mousedown" : function(ev){
				this.frameNode.setStyle("cursor", "nw-resize" );
				this.docBody.setStyle("cursor", "nw-resize" );
				this.resizeMode = true;
				this.getOffset(ev);
				ev.stopPropagation();
			}.bind(this),
			"touchmove" : function(ev){
				if(!this.lastPoint)return;
				var offset= this.getOffset(ev);
				this.resizeFrames( offset );
				ev.stopPropagation();
			}.bind(this),
			"mousemove" : function(ev){
				if(!this.lastPoint)return;
				var offset= this.getOffset(ev);
				this.resizeFrames( offset );
				ev.stopPropagation();
			}.bind(this),
			"touchend" : function(ev){
				this.frameNode.setStyle("cursor", "move" );
				this.docBody.setStyle("cursor", "default" );
				this.resizeMode = false;
				this.lastPoint=null;
				this.clipImage();
				ev.stopPropagation();
			}.bind(this),
			"mouseup" : function(ev){
				this.frameNode.setStyle("cursor", "move" );
				this.docBody.setStyle("cursor", "default" );
				this.resizeMode = false;
				this.lastPoint=null;
				this.clipImage();
				ev.stopPropagation();
			}.bind(this)
		});

		this.bodyMouseMoveFun = this.bodyMouseMove.bind(this);
		this.docBody.addEvent("touchmove", this.bodyMouseMoveFun);
		this.docBody.addEvent("mousemove", this.bodyMouseMoveFun);

		this.bodyMouseEndFun = this.bodyMouseEnd.bind(this);
		this.docBody.addEvent("touchend", this.bodyMouseEndFun);
		this.docBody.addEvent("mouseup", this.bodyMouseEndFun);
	},
	loadDescriptionNode: function(){
		new Element("div",{
			"styles": this.css.descriptionNode,
			"text": this.options.description
		}).inject( this.container )
	},
	bodyMouseMove: function(ev){
		if(!this.lastPoint)return;
		if( this.resizeMode ){
			var offset= this.getOffset(ev);
			this.resizeFrames( offset );
		}
	},
	bodyMouseEnd: function(ev){
		this.lastPoint=null;
		if( this.resizeMode ){
			this.frameNode.setStyle("cursor", "move" );
			this.docBody.setStyle("cursor", "default" );
			this.resizeMode = false;
			this.clipImage();
		}
	},
	clipImage: function(){
		this.resultNode.empty();

		var nh=this.imageNode.naturalHeight,
			nw=this.imageNode.naturalWidth,
			max = (this.uploadSourceInput && this.uploadSourceInput.get("checked")) ? 0 : this.options.resultMaxSize,
			size,
			ratio;

		ratio = this.options.aspectRatio ? this.options.aspectRatio : (this.frameOffset.size.width / this.frameOffset.size.height );
		if( max == 0 || ( nh<=max && nw<=max )){
			size = this.getRatioMaxSize(nw, nh , ratio);
		}else{
			var min = Math.min(max, nh, nw);
			size = this.getRatioMaxSize(min, min, ratio);
		}

		var canvas = new Element("canvas", size);
		var	ctx=canvas.getContext('2d'),
			scale=nw/this.offset.width,
			x=this.frameOffset.left*scale,
			y=this.frameOffset.top*scale,
			w=this.frameOffset.size.width*scale,
			h=this.frameOffset.size.height*scale;

		ctx.drawImage(this.imageNode,x,y,w,h,0,0,size.width,size.height);
		var src=canvas.toDataURL( this.fileType );
		this.canvas=canvas;
		canvas.inject(this.resultNode);

		src=src.split(',')[1];
		if(!src){
			this.resizedImage = null;
			this.base64Code = "";
			return;
		}
		this.base64Code = src;
		src=window.atob(src);

		var ia = new Uint8Array(src.length);
		for (var i = 0; i < src.length; i++) {
			ia[i] = src.charCodeAt(i);
		}

		this.resizedImage = new Blob([ia], {type: this.fileType });
		var fileName = "image_"+new Date().getTime();
		if( this.fileType && this.fileType.contains("/") ) {
			this.resizedImage.name = fileName + "." + this.fileType.split("/")[1];
		}else{
			this.resizedImage.name = fileName + ".unknow";
		}

		var min = Math.min(this.options.previewerSize, nh, nw, this.options.resultMaxSize);
		size = this.getRatioMaxSize(min, min, ratio);
		canvas.setStyles({
			width : size.width + "px",
			height : size.height + "px"
		});
	},
	loadImageAsFile: function( file ){
		this.resetImage();
		this.editorContainer.setStyles( this.css.editorContainer_active );
		this.imageNode.setStyle("display","");

		this.setFrameSize({width:0, height:0});
		this.frameOffset.top = 0;
		this.frameOffset.left = 0;
		this.frameNode.setStyles({
			top:0,
			left:0
		});
		var reader=new FileReader();
		reader.onload=function(){
			this.imageNode.src=reader.result;
			reader = null;
			//this.setImageSize();
			//this.setFrameSize( this.getDefaultSize() );
			//this.clipImage();
			this.onImageLoad();
		}.bind(this);
		reader.readAsDataURL(file);
	},
	loadImageAsUrl: function( url ){
		this.resetImage();
		this.editorContainer.setStyles( this.css.editorContainer_active );
		this.imageNode.setStyle("display","");
		this.setFrameSize({width:0, height:0});
		this.frameOffset.top = 0;
		this.frameOffset.left = 0;
		this.frameNode.setStyles({
			top:0,
			left:0
		});
		this.onImageLoadFun = this.onImageLoad.bind(this);
		this.imageNode.addEvent( "load",this.onImageLoadFun );
		this.imageNode.src = url;
	},
	onImageLoad: function(){
		var nh=this.imageNode.naturalHeight,
			nw=this.imageNode.naturalWidth;
		if( isNaN(nh) || isNaN(nw) || nh == 0 || nw == 0 ){
			setTimeout( function(){
				this.onImageLoad();
			}.bind(this), 100 );
		}else{
			this._onImageLoad();
		}
	},
	_onImageLoad: function(){
		this.setImageSize();
		this.setFrameSize( this.getDefaultSize() );
		this.clipImage();
		if(this.onImageLoadFun){
			this.imageNode.removeEvent("load", this.onImageLoadFun);
			this.onImageLoadFun = null;
		}
	},
	resetImage: function(){
		this.imageNode.src='';
		if( this.canvas )this.canvas.destroy();
	},
	setImageSize: function(){
		var nh=this.imageNode.naturalHeight,
			nw=this.imageNode.naturalWidth,
			size;
		if( nw > nh ){
			size = {
				width : this.options.editorSize,
				height : this.options.editorSize * (nh / nw)
			}
		}else{
			size = {
				width : this.options.editorSize  * (nw / nh),
				height : this.options.editorSize
			}
		}
		this.offset = size;
		this.imageNode.setStyles( size );
	},
	setFrameSize: function(size){
		this.frameOffset.size=size;

		return this.frameNode.setStyles({
			width:size.width+'px',
			height:size.height+'px'
		});
	},
	getDefaultSize: function(){
		this.innerNode.setStyles({
			"width" : this.offset.width,
			"height" : this.offset.height,
			"margin-left" : (this.options.editorSize-this.offset.width)/2 +"px",
			"margin-top" : (this.options.editorSize-this.offset.height)/2 +"px"
		});
		if( this.options.aspectRatio ){
			return this.getRatioMaxSize(this.offset.width, this.offset.height);
		}else{
			//var min = Math.min(this.offset.width, this.offset.height);
			//return { width : min, height : min };
			return {
				width : this.offset.width,
				height : this.offset.height
			};
		}
	},
	getRatioMaxSize: function( width, height , radio ){
		if( !radio )radio = this.options.aspectRatio;
		var r = width / height;
		if( r > radio ){
			return {
				width : height * radio,
				height : height
			}
		}else{
			return {
				width : width,
				height : width / radio
			}
		}
	},
	resizeFrames: function( offset ){
		var x=offset.x;
		if( x == 0 )return;

		var	y=offset.y;
		if( y == 0 )return;

		var	top=this.frameOffset.top,
			left=this.frameOffset.left,
			size=this.frameOffset.size,
			width=this.offset.width,
			height=this.offset.height,
			ratio = this.options.aspectRatio,
			w,
			h;

		if( ratio ){
			if( Math.abs(x)/Math.abs(y) > ratio ){
				if( x+size.width+left>width ){
					return;
				}else{
					w = x + size.width;
					h = w / ratio;
					if( h+top > height ){
						return;
					}
				}
			}else{
				if(y+size.height+top>height){
					return;
				}else{
					h = y+ size.height;
					w = h * ratio;
				}
				if( w+left > width ){
					return;
				}
			}
		}else{
			if( x+size.width+left>width ){
				return;
			}else{
				w = x + size.width
			}
			if(y+size.height+top>height){
				return;
			}else{
				h = y+ size.height;
			}
		}

		var minWidth = this.options.frameMinSize;
		var minHeight = ratio ? minWidth / ratio : minWidth;
		w=w< minWidth ?minWidth:w;
		h=h< minHeight ? minHeight:h;

		this.frameNode.setStyles({
			width:w+'px',
			height:h+'px'
		});


		this.frameOffset.size={
			width : w,
			height : h
		}
	},
	moveFrames: function(offset){
		var x=offset.x,
			y=offset.y,
			top=this.frameOffset.top,
			left=this.frameOffset.left,
			size=this.frameOffset.size,
			width=this.offset.width,
			height=this.offset.height;

		if(x+size.width+left>width){
			x=width-size.width;
		}else{
			x=x+left;
		}

		if(y+size.height+top>height){
			y=height-size.height;
		}else{
			y=y+top;
		}
		x=x<0?0:x;
		y=y<0?0:y;
		this.frameNode.setStyles({
			top:y+'px',
			left:x+'px'
		});

		this.frameOffset.top=y;
		this.frameOffset.left=x;
	},
	getOffset: function(event){
		event=event.event;
		var x,y;
		if(event.touches){
			var touch=event.touches[0];
			x=touch.clientX;
			y=touch.clientY;
		}else{
			x=event.clientX;
			y=event.clientY;
		}

		if(!this.lastPoint){
			this.lastPoint={
				x:x,
				y:y
			};
		}

		var offset={
			x:x-this.lastPoint.x,
			y:y-this.lastPoint.y
		};
		this.lastPoint={
			x:x,
			y:y
		};
		return offset;
	},
	base64ToBlob : function( base64 ){
		if( base64.substr( 0, 10 ) == 'data:image' ){
			var bytes=window.atob(base64.split(',')[1]);        //url의 헤더를 제거하고 byte로 변환
		}else{
			var bytes=window.atob(base64)
		}

		//예외 처리, ascii 코드가 0보다 작은 값을 0보다 크게 변환
		var ab = new ArrayBuffer(bytes.length);
		var ia = new Uint8Array(ab);
		for (var i = 0; i < bytes.length; i++) {
			ia[i] = bytes.charCodeAt(i);
		}

		return new Blob( [ab] , {type : this.fileType });
	}

});
