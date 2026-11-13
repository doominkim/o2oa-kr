MWF.xApplication.Template = MWF.xApplication.Template || {};
MWF.xApplication.Template.widget = MWF.xApplication.Template.widget || {};
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xApplication.Template.widget.CronPicker = new Class({
	Implements: [Options, Events],
	Extends: MTooltips,
	options: {
        style : "default",
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "auto", //x 轴上left center right, auto 系统自动计算
            y : "auto" //y轴上top middle bottom,  auto 系统自动计算
        },
        event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        modules : ["second","mintue","hour","date","month","week","year"],
        moudulesLp : { second : "초", mintue : "분", hour : "시", date : "일", month : "월", week : "요일", year : "년"},
        isShowResult : true,
        isShowText : true,
        value : ""
	},
    _loadCornCss: function(reload){
        this.cornCssPath = "../x_component_Template/widget/$CronPicker/"+this.options.style+"/css.wcss";
        var key = encodeURIComponent(this.cornCssPath);
        if (!reload && MWF.widget.css[key]){
            this.css = MWF.widget.css[key];
        }else{
            this.cornCssPath = (this.cornCssPath.indexOf("?")!=-1) ? this.cornCssPath+"&v="+COMMON.version : this.cornCssPath+"?v="+COMMON.version;
            var r = new Request.JSON({
                url: this.cornCssPath,
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function(responseJSON, responseText){
                    MWF.widget.css[key] = this.css = Object.merge( this.css || {} , responseJSON );
                }.bind(this),
                onError: function(text, error){
                    alert(error + text);
                }
            });
            r.send();
        }
    },
    _customNode : function( node ){
        this._loadCornCss();
        if( this.options.value ){
            this.values = this.options.value.split(" ");
        }
        this.formStyle = "cronpicker" + ( this.options.style == "default" ? "" : ("_"+this.options.style) );
        this.tabNode = new Element("div", {styles : this.css.tabNode }).inject( this.contentNode );
        this.options.modules.each( function( m , i ){
            var tab = this[ m + "Tab" ] = new Element("div", {
                text : this.options.moudulesLp[ m ],
                styles : this.css.tab
            }).inject( this.tabNode );
            this[ m + "Node" ] = new Element("div", {
                styles : this.css.moduleNode
            }).inject(this.contentNode);
            this[ "load"+ m.capitalize() +"Form" ]();
            tab.addEvents({
                click : function(){
                    var _self = this.obj;
                    if( _self.currentTab ){
                        _self.currentTab.setStyles(_self.css.tab );
                    }
                    if( _self.currentNode ){
                        _self.currentNode.setStyles({ "display" : "none" });
                    }
                    _self.currentTab = _self[ this.m + "Tab" ];
                    _self.currentNode = _self[ this.m + "Node" ];
                    _self[ this.m + "Tab" ].setStyles(_self.css.tab_current);
                    _self[ this.m + "Node" ].setStyles({ "display" : "" });
                }.bind( {obj : this, m : m} ),
                mouseover : function(){
                    var _self = this.obj;
                    if( _self[ this.m + "Tab" ] != _self.currentTab ){
                        _self[ this.m + "Tab"].setStyles(_self.css.tab_over);
                    }
                }.bind( {obj : this, m : m} ),
                mouseout : function(){
                    var _self = this.obj;
                    if( _self[ this.m + "Tab" ] != _self.currentTab ){
                        _self[ this.m + "Tab"].setStyles(_self.css.tab);
                    }
                }.bind( {obj : this, m : m} )
            });
            if( i == 0 )tab.click();
        }.bind(this));
        this.helpNode = new Element("a", {
            styles : this.css.helpNode,
            href : '../x_component_Template/widget/$CronPicker/cron_express_description.html',
            target: '_blank'
        }).inject(this.tabNode);

        if( this.options.isShowResult ){
            //this.createResultNode()
        }

        //this.resultInput = new Element("input").inject(this.contentNode);
    },
    setCronValue : function( value ){
        var values = value.split(" ");
        this.setSecondItemValue( values[0] );
        this.setMintueItemValue( values[1] );
        this.setHourItemValue( values[2] );
        this.setDateItemValue( values[3] );
        this.setMonthItemValue( values[4] );
        this.setWeekItemValue( values[5] );
        if( values.length > 6 )this.setYearItemValue( values[6] );
    },
    setValue : function(){
        var arr = [];
        ["second","mintue","hour","date","month","week","year"].each( function( m ){
            var value = this["set"+ m.capitalize() + "Value"]();
            if( value ){
                arr.push(value)
            }
        }.bind(this));
        //this.resultInput.set("value",arr.join(" "));
        this.fireEvent("select",[arr.join(" ")]);
    },
    addZero : function( str, length ){
        var zero = "";
        str = str.toString();
        for( var i=0; i<length; i++ ){
            zero = zero + "0";
        }
        var s = zero + str;
        return s.substr(s.length - length, length );
    },
    loadSecondForm : function(){
        var _self = this;
        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' style='margin-top: 10px; '>" +
            "<tr><td styles='formTableValue14' style='width:60px;'>" +
            "       <div item='second'></div>" +
            "   </td>" +
            "    <td styles='formTableValue14' style='vertical-align: top;'>" +
            "       <div>허용 와일드카드 [, - * /]</div>" +
            "       <div>범위 <span item='sfrom'></span>-<span item='sto'></span> 초</div>" +
            "       <div><span item='sbegin'></span>초부터 <span item='scount'></span>초마다 실행</div>" +
            "       <div></div>" +
            "</td></tr>" +
            "<tr><td styles='formTableValue14' item='sspecify' colspan='2' style='padding-left: 10px;'></td></tr>" +
            "</table>";
        this.secondNode.set("html", html);

        this.secondForm = new MForm(this.secondNode, {}, {
            isEdited: true,
            style : this.formStyle,
            hasColon : true,
            itemTemplate: {
                second : { type:"radio" ,selectValue : ["secondly","cycle","from","specify"], selectText: ["매초","주기","","지정"], defaultValue : "secondly", event : {
                    click : function(){ this.setValue() }.bind(this)
                }},
                sfrom : { tType : "number",   event : {  blur : function(){ this.setValue() }.bind(this) } },
                sto : { tType : "number",  event : {  blur : function(){ this.setValue() }.bind(this) } },
                sbegin : { tType : "number",  event : {  blur : function(){ this.setValue() }.bind(this) } },
                scount : { tType : "number",  event : {  blur : function(){ this.setValue() }.bind(this) } },
                sspecify : { type:"checkbox" , selectText : function(){
                    var text = [];
                    for( var i=0; i<60; i++ ){
                        text.push( _self.addZero( i, 2 ) );
                    }
                    return text;
                } , selectValue : function(){
                    var value = [];
                    for( var i=0; i<60; i++ ){
                        value.push( i );
                    }
                    return value;
                } , event : {  change : function(){ this.setValue() }.bind(this) } }
            }
        });
        this.secondForm.load();
        if( this.values )this.setSecondItemValue( this.values[0] );
    },
    setSecondValue : function(){
        if( !this.secondForm )return "*";
        var result = this.secondForm.getResult(false, null, false, false, true);
        switch ( result.second ){
            case "secondly":
                return "*";
                break;
            case "cycle":
                return result.sfrom+"-"+result.sto;
                break;
            case "from":
                return result.sbegin+"/"+result.scount;
                break;
            case "specify":
                return result.sspecify.length ? result.sspecify.join(",") : "?";
                break;
            default :
                return "*";
        }
    },
    setSecondItemValue : function( value ){
        if( !value )return;
        if( !this.secondForm )return;
        if( value == "*" ){
            this.secondForm.getItem("second").setValue("secondly");
        }else if(  value.indexOf("-") > -1 ){
            this.secondForm.getItem("second").setValue("cycle");
            var arr = value.split("-");
            this.secondForm.getItem("sfrom").setValue(arr[0] || "");
            this.secondForm.getItem("sto").setValue(arr[1] || "");
        }else if(  value.indexOf("/") > -1 ){
            this.secondForm.getItem("second").setValue("from");
            var arr = value.split("/");
            this.secondForm.getItem("sbegin").setValue(arr[0] || "");
            this.secondForm.getItem("scount").setValue(arr[1] || "");
        }else{
            this.secondForm.getItem("second").setValue("specify");
            this.secondForm.getItem("sspecify").setValue(value.split(","));
        }
    },
    getSecondDefaultValue : function(){
        if( this.options.value ){
            this.values = this.options.value.split(" ");
        }
        var d = {
            type : "secondly"
        };
        if( this.values ){
            var value = this.values[0];
            if( value == "*" ){
            }else if(  value.indexOf("-") > -1 ){
                d.type = "cycle";
                var arr = value.split("-");
                d.from = arr[0] || "";
                d.to = arr[1] || "";
            }else if(  value.indexOf("/") > -1 ){
                d.type = "from";
                var arr = value.split("/");
                d.sbegin = arr[0] || "";
                d.scount = arr[1] || "";
            }else{
                d.type = "specify";
                d.sspecify = value.split(",");
            }
        }
        return d;
    },

    loadMintueForm : function(){
        var _self = this;
        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' style='margin-top: 10px; '>" +
            "<tr><td styles='formTableValue14' style='width:80px;'>" +
            "       <div item='mintue'></div>" +
            "   </td>" +
            "    <td styles='formTableValue14' style='vertical-align: top;'>" +
            "       <div>허용 와일드카드 [, - * /]</div>" +
            "       <div>범위 <span item='mfrom'></span>-<span item='mto'></span> 분</div>" +
            "       <div><span item='mbegin'></span>분부터 <span item='mcount'></span>분마다 실행</div>" +
            "       <div></div>" +
            "</td></tr>" +
            "<tr><td styles='formTableValue14' item='mspecify' colspan='2' style='padding-left: 10px;'></td></tr>" +
            "</table>";
        this.mintueNode.set("html", html);

        this.mintueForm = new MForm(this.mintueNode, {}, {
            isEdited: true,
            style : this.formStyle,
            hasColon : true,
            itemTemplate: {
                mintue : { type:"radio" ,selectValue : ["mintuely","cycle","from","specify"], selectText: ["매분","주기","","지정"],defaultValue : "mintuely", event : {
                    click : function(){ this.setValue() }.bind(this)
                }},
                mfrom : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                mto : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                mbegin : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                mcount : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                mspecify : { type:"checkbox" , selectText : function(){
                    var text = [];
                    for( var i=0; i<60; i++ ){
                        text.push( _self.addZero( i, 2 ) );
                    }
                    return text;
                }, selectValue : function(){
                    var value = [];
                    for( var i=0; i<60; i++ ){
                        value.push( i );
                    }
                    return value;
                } , event : {  change : function(){ this.setValue() }.bind(this) } }
            }
        });
        this.mintueForm.load();
        if( this.values )this.setMintueItemValue( this.values[1] );
    },
    setMintueValue : function(){
        if( !this.mintueForm )return "*";
        var result = this.mintueForm.getResult(false, null, false, false, true);
        switch ( result.mintue ){
            case "mintuely":
                return "*";
                break;
            case "cycle":
                return result.mfrom+"-"+result.mto;
                break;
            case "from":
                return result.mbegin+"/"+result.mcount;
                break;
            case "specify":
                return result.mspecify.length ? result.mspecify.join(",") : "?";
                break;
            default :
                return "*";
        }
    },
    setMintueItemValue : function( value ){
        if( !value )return;
        if( !this.mintueForm )return;
        if( value == "*" ){
            this.mintueForm.getItem("mintue").setValue("mintuely");
        }else if(  value.indexOf("-") > -1 ){
            this.mintueForm.getItem("mintue").setValue("cycle");
            var arr = value.split("-");
            this.mintueForm.getItem("mfrom").setValue(arr[0] || "");
            this.mintueForm.getItem("mto").setValue(arr[1] || "");
        }else if(  value.indexOf("/") > -1 ){
            this.mintueForm.getItem("mintue").setValue("from");
            var arr = value.split("/");
            this.mintueForm.getItem("mbegin").setValue(arr[0] || "");
            this.mintueForm.getItem("mcount").setValue(arr[1] || "");
        }else{
            this.mintueForm.getItem("mintue").setValue("specify");
            this.mintueForm.getItem("mspecify").setValue(value.split(","));
        }
    },

    loadHourForm : function(){
        var _self = this;
        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' style='margin-top: 10px; '>" +
            "<tr><td styles='formTableValue14' style='width:80px;'>" +
            "       <div item='hour'></div>" +
            "   </td>" +
            "    <td styles='formTableValue14' style='vertical-align: top;'>" +
            "       <div>허용 와일드카드 [, - * /]</div>" +
            "       <div>범위 <span item='hfrom'></span>-<span item='hto'></span> 시</div>" +
            "       <div><span item='hbegin'></span>시부터 <span item='hcount'></span>시간마다 실행</div>" +
            "       <div></div>" +
            "</td></tr>" +
            "<tr><td styles='formTableValue14' item='hspecify' colspan='2' style='padding-left: 10px;'></td></tr>" +
            "</table>";
        this.hourNode.set("html", html);

        this.hourForm = new MForm(this.hourNode, {}, {
            isEdited: true,
            style : this.formStyle,
            hasColon : true,
            itemTemplate: {
                hour : { type:"radio" ,selectValue : ["hourly","cycle","from","specify"], selectText: ["매시","주기","","지정"], defaultValue : "hourly",  event : {
                    click : function(){ this.setValue() }.bind(this)
                }},
                hfrom : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                hto : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                hbegin : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                hcount : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                hspecify : { type:"checkbox" , selectText : function(){
                    var text = [];
                    for( var i=0; i<24; i++ ){
                        text.push( _self.addZero( i, 2 ) );
                    }
                    return text;
                } , selectValue : function(){
                    var value = [];
                    for( var i=0; i<24; i++ ){
                        value.push( i );
                    }
                    return value;
                } , event : {  change : function(){ this.setValue() }.bind(this) } }
            }
        });
        this.hourForm.load();
        if( this.values )this.setHourItemValue( this.values[2] );
    },
    setHourValue : function(){
        if( !this.hourForm )return "*";
        var result = this.hourForm.getResult(false, null, false, false, true);
        switch ( result.hour ){
            case "hourly":
                return "*";
                break;
            case "cycle":
                return result.hfrom+"-"+result.hto;
                break;
            case "from":
                return result.hbegin+"/"+result.hcount;
                break;
            case "specify":
                return result.hspecify.length ? result.hspecify.join(",") : "?";
                break;
            default :
                return "*";
        }
    },
    setHourItemValue : function( value ){
        if( !value )return;
        if( !this.hourForm )return;
        if( value == "*" ){
            this.hourForm.getItem("hour").setValue("hourly");
        }else if(  value.indexOf("-") > -1 ){
            this.hourForm.getItem("hour").setValue("cycle");
            var arr = value.split("-");
            this.hourForm.getItem("hfrom").setValue(arr[0] || "");
            this.hourForm.getItem("hto").setValue(arr[1] || "");
        }else if(  value.indexOf("/") > -1 ){
            this.hourForm.getItem("hour").setValue("from");
            var arr = value.split("/");
            this.hourForm.getItem("hbegin").setValue(arr[0] || "");
            this.hourForm.getItem("hcount").setValue(arr[1] || "");
        }else{
            this.hourForm.getItem("hour").setValue("specify");
            this.hourForm.getItem("hspecify").setValue(value.split(","));
        }
    },

    loadDateForm : function(){
        var _self = this;
        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' style='margin-top: 10px; '>" +
            "<tr><td styles='formTableValue14' style='width:120px;'>" +
            "       <div item='date'></div>" +
            "   </td>" +
            "    <td styles='formTableValue14' style='vertical-align: top;'>" +
            "       <div>허용 와일드카드 [, - * / L W ?]</div>" +
            "       <div>　</div>" +
            "       <div>범위 <span item='dfrom'></span>-<span item='dto'></span> 일</div>" +
            "       <div><span item='dbegin'></span>일부터 <span item='dcount'></span>일마다 실행</div>" +
            "       <div>매월 <span item='dnearly'></span>일에서 가장 가까운 근무일</div>" +
            "       <div></div>" +
            "       <div></div>" +
            "</td></tr>" +
            "<tr><td styles='formTableValue14' item='dspecify' colspan='2' style='padding-left: 10px;'></td></tr>" +
            "</table>";
        this.dateNode.set("html", html);

        this.dateForm = new MForm(this.dateNode, {}, {
            isEdited: true,
            style : this.formStyle,
            hasColon : true,
            itemTemplate: {
                date : { type:"radio" ,selectValue : ["daily","notspecify","cycle","from", "nearly", "last","specify"], selectText: ["매일","지정 안 함","주기","","","이번 달 마지막 날","지정"], defaultValue : "daily", event : {
                    click : function(){ this.setValue() }.bind(this)
                }},
                dfrom : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                dto : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                dbegin : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                dcount : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                dnearly : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                dspecify : { type:"checkbox" , selectText : function(){
                    var text = [];
                    for( var i=1; i<32; i++ ){
                        text.push( _self.addZero( i, 2 ) );
                    }
                    return text;
                }, selectValue : function(){
                    var value = [];
                    for( var i=1; i<32; i++ ){
                        value.push( i );
                    }
                    return value;
                } , event : {  change : function(){ this.setValue() }.bind(this) } }
            }
        });
        this.dateForm.load();
        if( this.values )this.setDateItemValue( this.values[3] );
    },
    setDateValue : function(){
        if( !this.dateForm )return "*";
        var result = this.dateForm.getResult(false, null, false, false, true);
        switch ( result.date ){
            case "daily":
                return "*";
                break;
            case "notspecify":
                return "?";
                break;
            case "cycle":
                return result.dfrom+"-"+result.dto;
                break;
            case "from":
                return result.dbegin+"/"+result.dcount;
                break;
            case "nearly":
                return result.dnearly+"W";
                break;
            case "last":
                return "L";
                break;
            case "specify":
                return result.dspecify.length ? result.dspecify.join(",") : "?";
                break;
            default :
                return "*";
        }
    },
    setDateItemValue : function( value ){
        if( !value )return;
        if( !this.dateForm )return;
        if( value == "*" ) {
            this.dateForm.getItem("date").setValue("daily");
        }else if( value == "?" ){
            this.dateForm.getItem("date").setValue("notspecify");
        }else if(  value.indexOf("-") > -1 ){
            this.dateForm.getItem("date").setValue("cycle");
            var arr = value.split("-");
            this.dateForm.getItem("dfrom").setValue(arr[0] || "");
            this.dateForm.getItem("dto").setValue(arr[1] || "");
        }else if(  value.indexOf("/") > -1 ){
            this.dateForm.getItem("date").setValue("from");
            var arr = value.split("/");
            this.dateForm.getItem("dbegin").setValue(arr[0] || "");
            this.dateForm.getItem("dcount").setValue(arr[1] || "");
        }else if( value.substr(value.length-1,1) == "W" ){
            this.dateForm.getItem("date").setValue("nearly");
            var dnearly = value.substr( 0, value.length - 1 );
            this.dateForm.getItem("dnearly").setValue(dnearly);
        }else if( value == "L" ){
            this.dateForm.getItem("date").setValue("last");
        }else{
            this.dateForm.getItem("date").setValue("specify");
            this.dateForm.getItem("dspecify").setValue(value.split(","));
        }
    },

    loadMonthForm : function(){
        var _self = this;
        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' style='margin-top: 10px; '>" +
            "<tr><td styles='formTableValue14' style='width:80px;'>" +
            "       <div item='month'></div>" +
            "   </td>" +
            "    <td styles='formTableValue14' style='vertical-align: top;'>" +
            "       <div>허용 와일드카드 [, - * / ?]</div>" +
            "       <div>　</div>" +
            "       <div>범위 <span item='mofrom'></span>-<span item='moto'></span> 월</div>" +
            "       <div><span item='mobegin'></span>월부터 <span item='mocount'></span>개월마다 실행</div>" +
            "       <div></div>" +
            "</td></tr>" +
            "<tr><td styles='formTableValue14' item='mospecify' colspan='2' style='padding-left: 10px;'></td></tr>" +
            "</table>";
        this.monthNode.set("html", html);

        this.monthForm = new MForm(this.monthNode, {}, {
            isEdited: true,
            style : this.formStyle,
            hasColon : true,
            itemTemplate: {
                month : { type:"radio" ,selectValue : ["monthly","notspecify","cycle","from", "specify"], selectText: ["매월","지정 안 함","주기","","지정"], defaultValue : "monthly", event : {
                    click : function(){ this.setValue() }.bind(this)
                }},
                mofrom : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                moto : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                mobegin : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                mocount : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                monearly : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                mospecify : { type:"checkbox" , selectText : function( ){
                    var text = [];
                    for( var i=1; i<13; i++ ){
                        text.push( _self.addZero( i, 2 ) );
                    }
                    return text;
                } , selectValue : function(){
                    var value = [];
                    for( var i=1; i<13; i++ ){
                        value.push( i );
                    }
                    return value;
                }, event : {  change : function(){ this.setValue() }.bind(this) } }
            }
        });
        this.monthForm.load();
        if( this.values )this.setMonthItemValue( this.values[4] );
    },
    setMonthValue : function(){
        if( !this.monthForm )return "*";
        var result = this.monthForm.getResult(false, null, false, false, true);
        switch ( result.month ){
            case "monthly":
                return "*";
                break;
            case "notspecify":
                return "?";
                break;
            case "cycle":
                return result.mofrom+"-"+result.moto;
                break;
            case "from":
                return result.mobegin+"/"+result.mocount;
                break;
            case "specify":
                return result.mospecify.length ? result.mospecify.join(",") : "?";
                break;
            default :
                return "*";
        }
    },
    setMonthItemValue : function( value ){
        if( !value )return;
        if( !this.monthForm )return;
        if( value == "*" ) {
            this.monthForm.getItem("month").setValue("monthly");
        }else if( value == "?" ){
                this.monthForm.getItem("month").setValue("notspecify");
        }else if(  value.indexOf("-") > -1 ){
            this.monthForm.getItem("month").setValue("cycle");
            var arr = value.split("-");
            this.monthForm.getItem("mofrom").setValue(arr[0] || "");
            this.monthForm.getItem("moto").setValue(arr[1] || "");
        }else if(  value.indexOf("/") > -1 ){
            this.monthForm.getItem("month").setValue("from");
            var arr = value.split("/");
            this.monthForm.getItem("mobegin").setValue(arr[0] || "");
            this.monthForm.getItem("mocount").setValue(arr[1] || "");
        }else{
            this.monthForm.getItem("month").setValue("specify");
            this.monthForm.getItem("mospecify").setValue(value.split(","));
        }
    },

    loadWeekForm : function(){
        var _self = this;
        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' style='margin-top: 10px; '>" +
            "<tr><td styles='formTableValue14' style='width:80px;'>" +
            "       <div item='week'></div>" +
            "   </td>" +
            "    <td styles='formTableValue14' style='vertical-align: top;'>" +
            "       <div>허용 와일드카드 [, - * / L # ?]</div>" +
            "       <div>　</div>" +
            "       <div>요일 범위 <span item='wfrom'></span>-<span item='wto'></span> </div>" +
            "       <div><span item='wnumber'></span>번째 주의 요일 <span item='wday'></span></div>" +
            "       <div>이번 달 마지막 주의 요일 <span item='wlast'></span></div>" +
            "       <div></div>" +
            "</td></tr>" +
            "<tr><td styles='formTableValue14' item='wspecify' colspan='2' style='padding-left: 10px;'></td></tr>" +
            "</table>";
        this.weekNode.set("html", html);

        this.weekForm = new MForm(this.weekNode, {}, {
            isEdited: true,
            style : this.formStyle,
            hasColon : true,
            itemTemplate: {
                week : { type:"radio" ,selectValue : ["weekly","notspecify","cycle","from", "last","specify"], selectText: ["매주","지정 안 함","주기","","","지정"], defaultValue : "notspecify", event : {
                    click : function(){ this.setValue() }.bind(this)
                }},
                wfrom : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                wto : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                wnumber : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                wday : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                wlast : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                wspecify : { type:"checkbox" , selectText : function(){
                    var text = [];
                    for( var i=1; i<8; i++ ){
                        text.push( _self.addZero( i, 2 ) );
                    }
                    return text;
                } , selectValue : function(){
                    var value = [];
                    for( var i=1; i<8; i++ ){
                        value.push( i );
                    }
                    return value;
                } , event : {  change : function(){ this.setValue() }.bind(this) } }
            }
        });
        this.weekForm.load();
        if( this.values )this.setWeekItemValue( this.values[5] );
    },
    setWeekValue : function(){
        if( !this.weekForm )return "?";
        var result = this.weekForm.getResult(false, null, false, false, true);
        switch ( result.week ){
            case "weekly":
                return "*";
                break;
            case "notspecify":
                return "?";
                break;
            case "cycle":
                return result.wfrom+"-"+result.wto;
                break;
            case "from":
                return result.wnumber+"/"+result.wday;
                break;
            case "last":
                return result.wlast + "L";
                break;
            case "specify":
                return result.wspecify.length ? result.wspecify.join(",") : "?";
                break;
            default :
                return "?";
        }
    },
    setWeekItemValue : function( value ){
        if( !value )return;
        if( !this.weekForm )return;
        if( value == "*" ) {
            this.weekForm.getItem("week").setValue("weekly");
        }else if( value == "?" ){
            this.weekForm.getItem("week").setValue("notspecify");
        }else if(  value.indexOf("-") > -1 ){
            this.weekForm.getItem("week").setValue("cycle");
            var arr = value.split("-");
            this.weekForm.getItem("wfrom").setValue(arr[0] || "");
            this.weekForm.getItem("wto").setValue(arr[1] || "");
        }else if(  value.indexOf("/") > -1 ){
            this.weekForm.getItem("week").setValue("from");
            var arr = value.split("/");
            this.weekForm.getItem("wbegin").setValue(arr[0] || "");
            this.weekForm.getItem("wcount").setValue(arr[1] || "");
        }else if( value.substr(value.length-1,1) == "L" ){
            this.weekForm.getItem("week").setValue("last");
            var wlast = value.substr( 0, value.length - 1 );
            this.weekForm.getItem("wlast").setValue(wlast);
        }else{
            this.weekForm.getItem("week").setValue("specify");
            this.weekForm.getItem("wspecify").setValue(value.split(","));
        }
    },

    loadYearForm : function(){
        var _self = this;
        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' style='margin-top: 10px; '>" +
            "<tr><td styles='formTableValue14' style='width:80px;'>" +
            "       <div item='year'></div>" +
            "   </td>" +
            "    <td styles='formTableValue14' style='vertical-align: top;'>" +
            "       <div>허용 와일드카드 [, - * /] 선택 입력</div>" +
            "       <div>　</div>" +
            "       <div>범위 <span item='yfrom'></span>-<span item='yto'></span>년</div>" +
            "</td></tr>" +
            "</table>";
        this.yearNode.set("html", html);

        this.yearForm = new MForm(this.yearNode, {}, {
            isEdited: true,
            style : this.formStyle,
            hasColon : true,
            itemTemplate: {
                year : { type:"radio" ,selectValue : ["notspecify","yearly","cycle"], selectText: ["지정 안 함","매년","주기"], defaultValue : "notspecify", event : {
                    click : function(){ this.setValue() }.bind(this)
                }},
                yfrom : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } },
                yto : { tType : "number", event : {  blur : function(){ this.setValue() }.bind(this) } }
            }
        });
        this.yearForm.load();
        if( this.values && this.values.length > 6 )this.setYearItemValue( this.values[6] );
    },
    setYearValue : function(){
        if( !this.yearForm )return "";
        var result = this.yearForm.getResult(false, null, false, false, true);
        switch ( result.year ){
            case "notspecify":
                return "";
                break;
            case "yearly":
                return "*";
                break;
            case "cycle":
                return result.yfrom+"-"+result.yto;
                break;
            default :
                return "";
        }
    },
    setYearItemValue : function( value ){
        if( !value )return;
        if( !this.yearForm )return;
        if( value == "*" ) {
            this.yearForm.getItem("year").setValue("yearly");
        }else if( value == "?" ){
            this.yearForm.getItem("year").setValue("notspecify");
        }else if(  value.indexOf("-") > -1 ) {
            this.yearForm.getItem("year").setValue("cycle");
            var arr = value.split("-");
            this.yearForm.getItem("yfrom").setValue(arr[0] || "");
            this.yearForm.getItem("yto").setValue(arr[1] || "");
        }
    }
	
});

