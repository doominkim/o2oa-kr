MWF.xApplication.AppMarketV2.Application.Comment = new Class({
    Implements: [Options, Events],
    options: {
        "view": "applicationComment.html"
    },
    initialize: function(app, container, options){
        this.setOptions(options);
        this.app = app;
        this.appdata = this.app.appdata;
        this.container = container;
        this.viewPath = this.app.path+this.app.options.style+"/"+this.options.view;
        this.iconPath = this.app.path+this.app.options.style+"/icon/";
        this.actions = MWF.Actions.load("x_program_center");
        this.load();
    },
    load: function(){
        this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp}, "module": this}, function(){
            this.loadApplication(function(){
                this.fireEvent("load");
            }.bind(this));
        }.bind(this));
    },

    loadApplication: function(callback){
        if (!this.isLoading){
            if (!this.applicationsContentV){
                this.applicationsContentV = new MWF.xApplication.AppMarketV2.Application.Comment.ViewPage(this, {
                    "onLoad": function(){ if (callback) callback(); }
                });
            }else{
                this.applicationsContentV.load();
            }
        }
    }
});

MWF.xApplication.AppMarketV2.Application.Comment.ViewPage= new Class({
    Implements: [Options, Events],
    options: {
        "type": "commentViewPage"
    },
    initialize: function(content, options){
        this.setOptions(options);
        this.content = content;
        this.app = this.content.app;
        this.appdata = this.content.appdata;
        this.actions = this.app.actions;
        this.container = this.content.container;
        this.page = 1;
        this.pageSize = 100;
        this.querydata = {};
        this.bbsUrlPath = "";
        this.bbsUrl = "";
        this.load();

    },
    load: function(){
        if (this.app.collectToken=="" || this.app.collectUrl==""){
            //먼저 collcect 로그인
            this.actions.CollectAction.login(//플랫폼이 제공하는 메서드
                function( json ){ //서비스 호출 성공 콜백 함수, json은 서비스가 반환한 데이터
                    if (json.type && json.type=="success"){
                        data = json.data; //변수 data에 값 할당
                        this.app.collectUrl = data.collectUrl;
                        this.app.collectToken = data.collectToken;
                        this.loadCommentsGrade(this,this.commentsGrade.bind(this));
                        //this.loadCommentPower(this,this.commentsPower.bind(this));
                        this.loadCommentPower(this);
                        this.loadCommentsList(this,this.commentsView.bind(this));
                    }
                }.bind(this),null,false //동기 실행
            );
        }else{
            //this.loadBbsInfo(this);
            this.loadCommentsGrade(this,this.commentsGrade.bind(this));
            //this.loadCommentPower(this,this.commentsPower.bind(this));
            this.loadCommentPower(this);
            this.loadCommentsList(this,this.commentsView.bind(this));
        }
    },
    loadBbsInfo: function(content){
        this.actions.CollectAction.bbs(//플랫폼이 제공하는 메서드
            function( json ){ //서비스 호출 성공 콜백 함수, json은 서비스가 반환한 데이터
                if (json.type && json.type=="success"){debugger;
                    data = json.data; //변수 data에 값 할당
                    this.bbsUrlPath = data.bbsUrlPath;
                    this.bbsUrl = data.bbsUrl;
                }
            }.bind(this),null,false //동기 실행
        );
    },
    loadCommentsGrade: function(content,callback){
        this.loadBbsInfo(content);
        var json = null;
        var commenturl =  this.bbsUrlPath +'/x_bbs_assemble_control/jaxrs/subject/statgrade/sectionName/'+encodeURI(content.app.lp.title)+'/subjectType/'+encodeURI(content.appdata.name)+'?time='+(new Date()).getMilliseconds();
        debugger;
        var res = new Request.JSON({
            url: commenturl,
            headers : {'x-debugger' : true,'Authorization':content.app.collectToken,'c-token':content.app.collectToken},
            secure: false,
            method: "get",
            async: true,
            withCredentials: true,
            contentType : 'application/json',
            crossDomain : true,
            onSuccess: function(responseJSON, responseText){
                debugger;
                json = responseJSON;
                if (typeOf(callback).toLowerCase() == 'function'){
                    callback(responseJSON);
                }else{
                    o2.runCallback(callback, "success", [responseJSON, responseText]);
                }
            }.bind(this),
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure", [xhr]);
            }.bind(this),
            onError: function(text, error){
                o2.runCallback(callback, "error", [text, error]);
            }.bind(this)
        });
        res.send();
    },
    loadCommentPower: function(content,callback){
        //var bbsurl = content.app.lp.bbsurl;
        debugger;
        var commentbuttondiv = new Element("div",{"class":"o2_appmarket_application_comment_middle_tip"}).inject(this.content.applicationcommentmiddle);
        new Element("span",{
            "class":"o2_appmarket_application_introduce_tab_current",
            "text":content.app.lp.bbsNotice
        }).inject(commentbuttondiv);
        commentbuttondiv.addEvents({
            "click": function(e){
                debugger;
                window.open(content.bbsUrl);
            }
        })
    },
    loadCommentsList:function(content,callback){
        var commentdata = {};
        commentdata["sectionName"] = content.app.lp.title;
        commentdata["subjectType"] = content.appdata.name;
        //var commenturl = content.app.lp.commentpath+'/x_bbs_assemble_control/jaxrs/subject/filter/listsubjectinfo/page/1/count/10';
        var commenturl = this.bbsUrlPath+'/x_bbs_assemble_control/jaxrs/subject/filter/listsubjectinfo/page/1/count/10';
        debugger;
        var res = new Request.JSON({
            "url": commenturl,
            "headers" : {"Content-Type": "application/json; charset=utf-8","x-debugger" : true},
            secure: false,
            "method": "POST",
            async: true,
            emulation: false,
            noCache: true,
            withCredentials: true,
            crossDomain : true,
            "data": JSON.stringify(commentdata),
            onSuccess: function(responseJSON, responseText){
                debugger;
                json = responseJSON;
                if (typeOf(callback).toLowerCase() == 'function'){
                    callback(responseJSON);
                }else{
                    o2.runCallback(callback, "success", [responseJSON, responseText]);
                }
            }.bind(this),
            onFailure: function(xhr){
                debugger;
                o2.runCallback(callback, "requestFailure", [xhr]);
            }.bind(this),
            onError: function(text, error){
                debugger;
                o2.runCallback(callback, "error", [text, error]);
            }.bind(this)
        });
        debugger;
        res.send();
    },
    loadCommentsList_bak:function(content,callback){
        var json = null;
        var commenturl = content.app.collectUrl +'/o2_collect_assemble/jaxrs/comment/list/app/'+content.appdata.id+'?time='+(new Date()).getMilliseconds();
        var res = new Request.JSON({
            url: commenturl,
            headers : {'x-debugger' : true,'Authorization':content.app.collectToken,'c-token':content.app.collectToken},
            secure: false,
            method: "get",
            async: true,
            withCredentials: true,
            contentType : 'application/json',
            crossDomain : true,
            onSuccess: function(responseJSON, responseText){
                json = responseJSON;
                if (typeOf(callback).toLowerCase() == 'function'){
                    callback(responseJSON);
                }else{
                    o2.runCallback(callback, "success", [responseJSON, responseText]);
                }
            }.bind(this),
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure", [xhr]);
            }.bind(this),
            onError: function(text, error){
                o2.runCallback(callback, "error", [text, error]);
            }.bind(this)
        });
        res.send();
    },
    commentsGrade:function(commentdata){
        debugger;
        var commentcount = 0;
        var grade = 0;
        var totalgrade = 0;
        var commentratiolist = commentdata.data;
        var gradeList = ["0","0","0","0","0"];
        commentratiolist.each(function(pergrade){
            gradeList[parseInt(pergrade.grade)-1]=pergrade.count;
            commentcount +=parseInt(pergrade.count)
        }.bind(this));

        gradeList.each(function(pergrade,index){
            totalgrade += parseInt(pergrade)*(index+1)
        })
        if (commentcount>0){
            grade = this.numberFix(totalgrade/commentcount,1)
        }
        debugger;
        this.content.applicationcommenttopgrade.set("text",grade+"");
        var intgrade = parseInt(grade);
        var dotgrade = grade - intgrade;


        for (var tmpnum=0;tmpnum<intgrade;tmpnum++){
            new Element("img",{"src":this.content.iconPath+"blackfiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(this.content.applicationcommenttopangular)
        }
        if (dotgrade>=0.5){
            new Element("img",{"src":this.content.iconPath+"halffiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(this.content.applicationcommenttopangular);
            intgrade++;
        }
        for (var tmpnum=0;tmpnum<5-intgrade;tmpnum++){
            new Element("img",{"src":this.content.iconPath+"whitefiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(this.content.applicationcommenttopangular);
        }

        new Element("span",{
            "class":"o2_appmarket_application_introduce_memo_remark_inner_text",
            "text":this.app.lp.commentCountText.replace("{n}",commentcount)
        }).inject(this.content.applicationcommenttopangular);

        //별 5개
        new Element("div",{"text":"5","class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightfive);
        gradeangular = new Element("div",{"class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightfive);
        new Element("img",{"src":this.content.iconPath+"blackfiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(gradeangular)
        graderatiodiv = new Element("div",{"class":"o2_appmarket_application_comment_gradetotal"}).inject(this.content.applicationcommentrightfive);
        blackratiodiv = new Element("div",{"class":"o2_appmarket_application_comment_graderatio"}).inject(graderatiodiv);
        graderratiodivwidth = graderatiodiv.clientWidth;
        if (commentcount==0){
            blackratio = 0;
            blackratiodiv.setStyle("width","0px");
        }else{
            blackratio = parseInt(gradeList[4])/commentcount;
            blackratiowidth = blackratio*graderratiodivwidth;
            blackratiodiv.setStyle("width",blackratiowidth+"px");
        }
        new Element("div",{"text":this.numberFix(blackratio*100,1)+"%","class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightfive);
        debugger;
        //별 4개
        new Element("div",{"text":"4","class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightfour);
        gradeangular = new Element("div",{"class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightfour);
        new Element("img",{"src":this.content.iconPath+"blackfiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(gradeangular)
        graderatiodiv = new Element("div",{"class":"o2_appmarket_application_comment_gradetotal"}).inject(this.content.applicationcommentrightfour);
        blackratiodiv = new Element("div",{"class":"o2_appmarket_application_comment_graderatio"}).inject(graderatiodiv);
        graderratiodivwidth = graderatiodiv.clientWidth;
        if (commentcount==0){
            blackratio = 0;
            blackratiodiv.setStyle("width","0px");
        }else{
            blackratio = parseInt(gradeList[3])/commentcount;
            blackratiowidth = blackratio*graderratiodivwidth;
            blackratiodiv.setStyle("width",blackratiowidth+"px");
        }
        new Element("div",{"text":this.numberFix(blackratio*100,1)+"%","class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightfour);
        //별 3개
        new Element("div",{"text":"3","class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightthree);
        gradeangular = new Element("div",{"class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightthree);
        new Element("img",{"src":this.content.iconPath+"blackfiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(gradeangular)
        graderatiodiv = new Element("div",{"class":"o2_appmarket_application_comment_gradetotal"}).inject(this.content.applicationcommentrightthree);
        blackratiodiv = new Element("div",{"class":"o2_appmarket_application_comment_graderatio"}).inject(graderatiodiv);
        graderratiodivwidth = graderatiodiv.clientWidth;
        if (commentcount==0){
            blackratio = 0;
            blackratiodiv.setStyle("width","0px");
        }else{
            blackratio = (parseInt(gradeList[2])/commentcount).toFixed(4);
            blackratiowidth = blackratio*graderratiodivwidth;
            blackratiodiv.setStyle("width",blackratiowidth+"px");
        }
        new Element("div",{"text":this.numberFix(blackratio*100,1)+"%","class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightthree);
        //별 2개
        new Element("div",{"text":"2","class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrighttwo);
        gradeangular = new Element("div",{"class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrighttwo);
        new Element("img",{"src":this.content.iconPath+"blackfiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(gradeangular)
        graderatiodiv = new Element("div",{"class":"o2_appmarket_application_comment_gradetotal"}).inject(this.content.applicationcommentrighttwo);
        blackratiodiv = new Element("div",{"class":"o2_appmarket_application_comment_graderatio"}).inject(graderatiodiv);
        graderratiodivwidth = graderatiodiv.clientWidth;
        if (commentcount==0){
            blackratio = 0;
            blackratiodiv.setStyle("width","0px");
        }else{
            blackratio = (parseInt(gradeList[1])/commentcount).toFixed(4);
            blackratiowidth = blackratio*graderratiodivwidth;
            blackratiodiv.setStyle("width",blackratiowidth+"px");
        }
        new Element("div",{"text":this.numberFix(blackratio*100,1)+"%","class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrighttwo);
        //별 1개
        new Element("div",{"text":"1","class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightone);
        gradeangular = new Element("div",{"class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightone);
        new Element("img",{"src":this.content.iconPath+"blackfiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(gradeangular)
        graderatiodiv = new Element("div",{"class":"o2_appmarket_application_comment_gradetotal"}).inject(this.content.applicationcommentrightone);
        blackratiodiv = new Element("div",{"class":"o2_appmarket_application_comment_graderatio"}).inject(graderatiodiv);
        graderratiodivwidth = graderatiodiv.clientWidth;
        if (commentcount==0){
            blackratio = 0;
            blackratiodiv.setStyle("width","0px");
        }else{
            blackratio = (parseInt(gradeList[0])/commentcount).toFixed(4);
            blackratiowidth = blackratio*graderratiodivwidth;
            blackratiodiv.setStyle("width",blackratiowidth+"px");
        }
        new Element("div",{"text":this.numberFix(blackratio*100,1)+"%","class":"o2_appmarket_application_comment_top_right_graderatioItem"}).inject(this.content.applicationcommentrightone);
    },
    commentsPower:function(commentdata){
        var commentText = "";
        if (commentdata.type && commentdata.type=="success"){
            if (commentdata.data.value){
                commentText = this.app.lp.commented;
            }
        }
        if (this.appdata.installedVersion==""){
            commentText = this.app.lp.notInstall;
        }
        if (commentText == ""){
            var commentbuttondiv = new Element("div",{"class":"o2_appmarket_application_comment_middle_commentbutton"}).inject(this.content.applicationcommentmiddle);
            new Element("span",{
                "class":"o2_appmarket_application_comment_middle_commentbutton_InnerSpan",
                "text":this.app.lp.iNeedComment
            }).inject(commentbuttondiv);
            var _self = this;
            commentbuttondiv.addEvents({
                "click": function(e){
                    _self.createComment(e);
                }
            })

        }else{
            new Element("div",{"class":"o2_appmarket_application_comment_middle_tip","text":commentText}).inject(this.content.applicationcommentmiddle);
        }
    },
    commentsView:function(commentdata){
        var commentsList = commentdata.data;
        debugger
        commentsList.each(function(percomment){
            var commentcontentdiv = new Element("div",{"class":"o2_appmarket_application_comment_content"}).inject(this.content.applicationcommentbottom);
            var commentcontentleft = new Element("div",{"class":"o2_appmarket_application_comment_content_left"}).inject(commentcontentdiv);
            var iconpersondiv = new Element("div",{"class":"o2_appmarket_application_comment_content_left_icon"}).inject(commentcontentleft);
            new Element("img",{"src":this.content.iconPath+"icon_men.png"}).inject(iconpersondiv);
            debugger;
            new Element("div",{"class":"o2_appmarket_application_comment_content_left_name","text":percomment.creatorNameShort}).inject(commentcontentleft);
            var commentcontentright = new Element("div",{"class":"o2_appmarket_application_comment_content_right"}).inject(commentcontentdiv);
            var commentangulardiv = new Element("div").inject(commentcontentright);
            for (var tmpi=0;tmpi<parseInt(percomment.grade);tmpi++){
                new Element("img",{"src":this.content.iconPath+"blackfiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(commentangulardiv)
            }
            for (var tmpi=0;tmpi<5-parseInt(percomment.grade);tmpi++){
                new Element("img",{"src":this.content.iconPath+"whitefiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(commentangulardiv)
            }

            var content = percomment.content;
            var percommentConent = content.replace("<p>","").replace("</p>","");
            var subjectUrl = this.bbsUrlPath;
            if(subjectUrl.indexOf(":",8)>0){
                subjectUrl = subjectUrl.slice(0,subjectUrl.indexOf(":",8));
            }
            subjectUrl = subjectUrl+"/x_desktop/forum.html?app=ForumDocument&id="+percomment.id;
            debugger
            var subjectDiv= new Element("div",{"class":"o2_appmarket_application_comment_content_title"}).inject(commentcontentright);
            var subjectA = new Element("a",{"class":"o2_appmarket_application_comment_content_title_a","text":percomment.title,"href":subjectUrl,"target":"_blank"}).inject(subjectDiv);
            //subjectA.setStyle("text-decoration","none");
            var conentDiv = new Element("div",{"class":"o2_appmarket_application_comment_content_text"}).inject(commentcontentright);

            var conentHtml = percomment.content;
            conentDiv.set("html",conentHtml);

        }.bind(this))
    },
    commentsView_bak:function(commentdata){
        var commentsList = commentdata.data;
        commentsList.each(function(percomment){
            var commentcontentdiv = new Element("div",{"class":"o2_appmarket_application_comment_content"}).inject(this.content.applicationcommentbottom);
            var commentcontentleft = new Element("div",{"class":"o2_appmarket_application_comment_content_left"}).inject(commentcontentdiv);
            var iconpersondiv = new Element("div",{"class":"o2_appmarket_application_comment_content_left_icon"}).inject(commentcontentleft);
            new Element("img",{"src":this.content.iconPath+"icon_men.png"}).inject(iconpersondiv);
            new Element("div",{"class":"o2_appmarket_application_comment_content_left_name","text":percomment.person}).inject(commentcontentleft);
            var commentcontentright = new Element("div",{"class":"o2_appmarket_application_comment_content_right"}).inject(commentcontentdiv);
            var commentangulardiv = new Element("div").inject(commentcontentright);
            for (var tmpi=0;tmpi<parseInt(percomment.grade);tmpi++){
                new Element("img",{"src":this.content.iconPath+"blackfiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(commentangulardiv)
            }
            for (var tmpi=0;tmpi<5-parseInt(percomment.grade);tmpi++){
                new Element("img",{"src":this.content.iconPath+"whitefiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(commentangulardiv)
            }
            new Element("div",{"class":"o2_appmarket_application_comment_content_title","text":percomment.title}).inject(commentcontentright);
            new Element("div",{"class":"o2_appmarket_application_comment_content_text","text":percomment.comment,"title":percomment.comment}).inject(commentcontentright);

        }.bind(this))
    },
    reload: function(){
        if (!this.content.isLoading) {
            this.loadComments();
            this.loadCommentPower();
        }
    },
    emptyLoadContent: function(){
        this.content.commentnode.empty();
        this.content.isLoading = false;
    },
    createComment:function(targetnode){
        var content = new Element("div", {"styles": {"margin": "20px"}});
        var xingdiv = new Element("div").inject(content);
        new Element("div",{"class":"comment_dlg_title","text": this.app.lp.score }).inject(xingdiv);
        xingpngdiv = new Element("div",{"class":"comment_dlg_png"}).inject(xingdiv);
        var starnode;
        var _self = this;
        var selectstar = 0;
        for (var tmpi=0;tmpi<5;tmpi++){
            starnode = new Element("img",{"src":this.content.iconPath+"whitefiveangular.png","class":tmpi+"star comment_dlg_star"}).inject(xingpngdiv);
            starnode.store("id",tmpi);
            starnode.addEvents({
                "click": function(e){
                    var idnum = this.retrieve("id");
                    selectstar = idnum + 1;
                    var starblacknode = this;
                    var starwhitenode = this.nextElementSibling;
                    while(starwhitenode){
                        starwhitenode.set("src",_self.content.iconPath+"whitefiveangular.png");
                        starwhitenode = starwhitenode.nextElementSibling;
                    }
                    while(starblacknode){
                        starblacknode.set("src",_self.content.iconPath+"blackfiveangular.png");
                        starblacknode = starblacknode.previousElementSibling
                    }
                }
            })
        }
        var cleardiv = new Element("div",{"style":"clear:both"}).inject(content);
        var commentdiv = new Element("div").inject(content);
        new Element("div",{"class":"comment_dlg_title","text":"댓글: "}).inject(commentdiv);
        inputdiv = new Element("div",{"class":"comment_dlg_input_div"}).inject(commentdiv);
        commentcontentNode = new Element("textarea",{"class":"comment_dlg_input"}).inject(inputdiv);
        o2.DL.open({
            "title": this.app.lp.commentTitle,
            "content": content,
            "width": 740,
            "height": 520,
            "buttonList": [
                {
                    "text": this.app.lp.ok,
                    "class":"comment_dlg_button_ok",
                    "action": function(){
                        debugger;
                        if (selectstar==0 || commentcontentNode.get("value")==""){
                            MWF.xDesktop.notice("error", {x: "right", y:"top"}, this.app.lp.commentNotice);
                        }else{
                            var commentdata = {};
                            commentdata["title"] = "";
                            commentdata["application"] = _self.appdata.id;
                            commentdata["grade"] = selectstar+"";
                            commentdata["comment"] = commentcontentNode.get("value");
                            _self.submitComment(commentdata);
                            this.close();
                        }
                    }
                },
                {
                    "text": this.app.lp.cancel,
                    "class":"comment_dlg_button_cancel",
                    "action": function(){this.close();}
                }
            ]
        });
    },
    submitComment:function(commentdata){
        var tmpjson= JSON.stringify(commentdata);
        var commenturl = this.app.collectUrl +'/o2_collect_assemble/jaxrs/comment';
        var res = new Request.JSON({
            "url": commenturl,
            "headers" : {"Content-Type": "application/json; charset=utf-8","x-debugger" : true,"Authorization":this.app.collectToken,"c-token":this.app.collectToken},
            secure: false,
            "method": "POST",
            secure: false,
            emulation: false,
            noCache: true,
            withCredentials: true,
            crossDomain : true,
            "data": JSON.stringify(commentdata),
            onSuccess: function(responseJSON, responseText){
                debugger;
                MWF.xDesktop.notice("success", {x: "right", y:"top"}, this.app.lp.commentsuccess);
                this.app.loadIntroduceComment();
            }.bind(this),
            onFailure: function(xhr){
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, xhr);
            }.bind(this),
            onError: function(text, error){
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, text);
            }.bind(this)
        });
        res.send();
    },
    numberFix:function(data,n){
        var numbers = '';
        // 소수점 몇 자리 유지 후 0 추가
        for (var i = 0; i < n; i++) {
            numbers += '0';
        }
        var s = 1 + numbers;
        // 정수이면 뒤에 0 추가 필요
        var spot = "." + numbers;
        // Math.round 반올림
        //  parseFloat() 함수는 문자열을 해석해 부동소수점 수를 반환.
        var value = Math.round(parseFloat(data) * s) / s;
        // 소수점 뒤에서 분할
        var d = value.toString().split(".");
        if (d.length == 1) {
            value = value.toString();
            return value;
        }
        if (d.length > 1) {
            if (d[1].length < n) {
                value = value.toString() + "0";
            }
            return value;
        }
    }

})