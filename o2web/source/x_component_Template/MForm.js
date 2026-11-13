/*
외부 메서드
 getResult(verify, separator, isAlert, onlyModified, keepAllData )
 반환: 검증하지 않거나 검증을 통과하면 양식의 데이터 반환, 검증을 통과하지 못하면 null 반환
 파라미터:
 verify 검증 여부,
 separator 다중 값 필드의 구분자,
 isAlert verify가 true일 때 검증 실패를 사용자에게 알릴지 여부,
 onlyModified 수정된 결과만 조회,
 keepAllData: 모든 MDOMItem 객체의 값을 조회하고 생성되지 않은 객체의 data 유지

 getItem( name )
 name으로 MDOMItem 객체 반환

 verify(isAlert)
 사용자가 필수 객체를 입력했는지 검증
 */
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
var MForm = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options : {
        style : "default",
        mvcStyle: "",
        isNew : false,
        isEdited : false,
        emptyItemContainer : true,
        showNotEmptyFlag : false,

        //batch: 모든 item의 오류를 검증하고 각 item의 모든 오류를 함께 검증, 오류는 Item 필드 뒤 또는 지정 영역에 표시;
        //batchSingle: 모든 item의 오류를 검증하되 각 item은 오류 하나만 검증, 오류는 Item 필드 뒤 또는 지정 영역에 표시;
        //alert: 검증 중 실패한 Item을 만나면 app.notice()로 메시지를 띄운 뒤 중단,
        //single: 검증 중 실패한 Item을 만나면 필드 뒤 또는 지정 영역에 오류 정보를 표시한 뒤 중단
        verifyType : "alert",
        itemTemplateUrl : "",
        containerHtml : null,
        itemTemplate : null,
        hasColon : false
    },
    initialize: function (container, data, options , app, css ) {

        this.setOptions(options);

        this.path = "../x_component_Template/$MForm/";
        if( this.options.style ) {
            this.cssPath = "../x_component_Template/$MForm/" + this.options.style + "/css.wcss";
            this._loadCss();
        }else{
            this.css = {};
        }
        if( css ){
            this.css = Object.merge( this.css, css )
        }

        this.app = app;
        this.container = $(container);

        this.data = typeOf( data ) === "object" ? [data] : data;

        this.isSourceDataEmpty = false;
        if( !this.data || this.data == "" ){
            this.isSourceDataEmpty = true;
            this.data = [{}];
        }

        if(this.options.mvcStyle){
            this.container.loadCss("../x_component_Template/$MForm/" + this.options.mvcStyle + "/style.css");
        }

        this.itemTemplateUrl = this.options.itemTemplateUrl;
        this.itemTemplate = this.options.itemTemplate;
        this.items = null;

        this.labelContainers = null;
        this.itemContainers = null;

        this.valSeparator = /,|;|\^\^|\|/g; //다중 값 객체이면 사용자가 선택한 여러 값의 구분자
    },
    load: function () {
        this.fireEvent("queryLoad");

        this.loadTemplate( function(){
            //itemTemplate에 name이 없으면 Key를 할당
            for( var it in this.itemTemplate ){
                if( !this.itemTemplate[it]["name"] ){
                    this.itemTemplate[it]["name"] = it;
                }
                this.itemTemplate[it]["key"] = it;
            }

            //itemTemplate의 name과 Key가 다르면 name에 따라 itemTemplate 할당
            var json = {};
            for( var it in this.itemTemplate ){
                if( it != this.itemTemplate[it]["name"] ){
                    json[ this.itemTemplate[it]["name"] ] = this.itemTemplate[it];
                }
            }
            for( var it in json ){
                this.itemTemplate[it] = json[it];
            }

            this.items = {};
            this.itemsByKey = {};

            if( this.options.containerHtml ){
                this.container.set("html",this.options.containerHtml);
            }
            this.labelContainers = this.container.getElements("[lable]");
            this.itemContainers = this.container.getElements("[item]");
            this.formatStyles();

            (this.options.isEdited || this.options.isNew) ? this.loadEdit() : this.loadRead();

            this.fireEvent("postLoad");
        }.bind(this) )
    },
    formatStyles: function(){
        this.container.getElements("[styles]").each(function(el){
            var styles = el.get("styles");
            if( styles && this.css[styles] ){
                el.setStyles( this.css[styles] )
            }
        }.bind(this));
        this.container.getElements("[class]").each(function(el){
            var className = el.get("class");
            if( className && this.css[className] ){
                el.setStyles( this.css[className] )
            }
        }.bind(this))
    },
    loadTemplate : function( callback ){
        if ( !this.itemTemplate && this.itemTemplateUrl) {
            MWF.getJSON(this.itemTemplateUrl, function(json){
                this.itemTemplate = json;
                if( callback )callback();
            }.bind(this));
        }else{
            if( callback )callback();
        }
    },
    reload: function (){
        this.itemTemplate = {};
        this.items = {};
        this.itemsByKey = {};
        this.itemContainers.each(function(node){
            node.empty();
        });
        this.load();
    },
    changeMode: function(keepData){
        debugger;
        // this.itemContainers.each(function(node){
        //     node.empty();
        // });
        // this.items = {};
        // this.itemsByKey = {};
        //
        // this.isEdited ? this.loadRead() : this.loadEdit();

        for(var key in this.items){
            this.isEdited ? this.items[key].readMode(keepData) : this.items[key].editMode(keepData)
        }
        this.isEdited = !this.isEdited;
    },
    loadEdit : function() {
        if (this.options.isNew) {
            this.formatEdit(this.itemTemplate, true);
        } else if (!this.isSourceDataEmpty) {
            if (typeOf(this.data) != "array") {
                this.data = [this.data]
            }
            for (var i = 0; i < this.data.length; i++) {
                var d = this.data[i];
                var items = this.itemTemplate;
                for (var it in d ) {
                    if (items[it]) {
                        items[it].value = d[it];
                    }
                }
                this.formatEdit(items, false);
            }
            for (var it in this.itemTemplate ) {
                this.itemTemplate[it].value = "";
            }
        } else {
            this.formatEdit(this.itemTemplate, true);
        }
    },
    loadRead : function() {
        if (this.options.isNew) {
            this.formatRead(this.itemTemplate);
        } else if (!this.isSourceDataEmpty) {
            if ( typeOf(this.data) != "array") {
                this.data = [this.data]
            }
            for (var i = 0; i < this.data.length; i++) {
                var d = this.data[i];
                var items = this.itemTemplate;
                for (var it in d ) {
                    if (items[it]) {
                        items[it].value = d[it];
                    }
                }
                this.formatRead(items);
            }
            for (var it in this.itemTemplate ) {
                this.itemTemplate[it].value = "";
            }
        }
    },
    //formatRead : function(itemData) {
    //    var self = this;
    //
    //    this.labelContainers.each(function( el ) {
    //        var obj = itemData[el.get("lable")];
    //        if (!obj)
    //            return;
    //        el.set("text",obj.text);
    //    });
    //
    //    this.itemContainers.each(function( el ) {
    //        var obj = itemData[el.get("item")];
    //        if (!obj)
    //            return;
    //
    //        if( obj.style ){
    //            el.setStyles( obj.style )
    //        }
    //
    //        if( typeOf( obj.value ) == "function" ) {
    //            var value = obj.value();
    //        }else if( typeOf( obj.value ) == "boolean" ){
    //                var value = obj.value.toString();
    //        }else{
    //            var value = (obj.value ? obj.value : "").toString();
    //        }
    //        if( obj.type == "hidden" ) {
    //            el.hide();
    //            el.set("html",value.replace( this.valSeparator, ",").replace(/\n/g,"<br/>") )
    //        }else if( obj.type == "radio" || obj.type == "select" || obj.type == "checkbox" || obj.type == "multiselect" ) {
    //            var values = value.split( this.valSeparator );
    //            if( obj.selectText && obj.selectValue ){
    //                var selectValues = this._getSelectOpt( obj.selectValue );
    //                var selectTexts = this._getSelectOpt( obj.selectText );
    //                var result = [];
    //                for( i=0;i<selectValues.length;i++){
    //                    if( values.contains( selectValues[i] ) ){
    //                        result.push( selectTexts[i] )
    //                    }
    //                }
    //                el.set("html",result.join(","));
    //            }else{
    //                el.set("html",value.replace( this.valSeparator, ",").replace(/\n/g,"<br/>") )
    //            }
    //        }else if( obj.type == "rtf"){
    //            el.set("html",value )
    //        } else {
    //            el.set("html",value.replace( this.valSeparator, ",").replace(/\n/g,"<br/>") )
    //        }
    //    }.bind(this))
    //},
    formatRead : function(itemData) {
        var self = this;
        this.isEdited = false;
        this.labelContainers.each(function( el ) {
            var obj = itemData[el.get("lable")];
            if (!obj)
                return;
            el.set("text",obj.text + (self.options.hasColon ? "：" : "") );
        });
        this.itemContainers.each(function( el ) {
            var obj = itemData[el.get("item")];
            if (!obj)
                return;
            if (self.options.emptyItemContainer) {
                el.set("html","");
            }
            obj.isEdited = false;
            self.loadItem(obj, el);
        })
    },
    _getSelectOpt: function( option ){
        var opt = option;
        if( typeOf( opt ) == "function" ){
            opt = opt.call();
        }
        return typeOf( opt ) == "array" ? opt : opt.split( this.valSeparator );
    },
    formatEdit : function(itemData, isNew, unid) {
        var self = this;
        this.isEdited = true;
        this.labelContainers.each(function( el ) {
            var obj = itemData[el.get("lable")];
            if (!obj)
                return;
            var text = obj.text + (self.options.hasColon ? "：" : "");
            if (self.options.showNotEmptyFlag && obj.notEmpty) {
                el.set("html", text + "<span style='color:red;'>*</span>")
            } else {
                el.set("text",text);
            }
        });
        this.itemContainers.each(function( el ) {
            var obj = itemData[el.get("item")];
            if (!obj)
                return;
            if (self.options.emptyItemContainer) {
                el.set("html","");
            }
            self.loadItem(obj, el);
        })
    },
    loadItem : function(template, container) {
        //if( template.disable )return;

        template.objectId = template.name;
        var item = new MDomItem(container, template, this, this.app, this.css );
        if( this.options.verifyType == "batchSingle" ){
            item.options.warningType = "single";
        }else{
            item.options.warningType = this.options.verifyType;
        }
        //item.parent = this;
        item.load();

        this.items[template.objectId] = item;
        this.itemsByKey[template.key] = item;
    },
    enableItem: function( itemName ){
        if( itemName && this.items[itemName] ){
            var item = this.items[itemName];
            if( item.options.disable ){
                item.enable();
            }
        }
    },
    disableItem: function( itemName ){
        if( itemName && this.items[itemName] ){
            var item = this.items[itemName];
            if( !item.options.disable ){
                item.disable();
            }
        }
    },
    verify : function(isShowWarming) {
        var flag = true;
        for (var it in this.items ) {
            if (!this.items[it].verify(isShowWarming)) {
                if (this.options.verifyType == "batch" || this.options.verifyType == "batchSingle") {
                    flag = false;
                } else {
                    return false;
                }
            }
        }
        return flag;
    },
    getItemsKeyValue : function(separator , onlyModified ) {
        //separator 다중 값 병합 구분자
        var key_value = {};
        for (var it in this.items ) {
            var item = this.items[it];
            if(['oo-button', 'button', 'a'].contains((item.options.type || 'text').toLowerCase())){
                continue;
            }
            var value = onlyModified ? item.getModifiedValue() : item.getValue();
            if( value != null ){
                if (typeOf(value) === "array") {
                    key_value[item.options.objectId] = ( typeOf(separator) == "string" ? value.join(separator) : value );
                } else {
                    key_value[item.options.objectId] = value;
                }
            }
        }
        return key_value;
    },
    getResult : function(verify, separator, isShowWarming, onlyModified, keepAllData ) {
        if ( !verify || this.verify(isShowWarming)) {
            if( keepAllData ){
                var result = this.data[0];
                var keyValue = this.getItemsKeyValue(separator, onlyModified);
                for( var key in keyValue ){
                    result[ key ] = keyValue[ key ];
                }
                return result;
            }else{
                return this.getItemsKeyValue(separator, onlyModified);
            }
        } else {
            return false;
        }
    },
    get: function (name){
        return this.getItem(name);
    },
    getItem : function( name ){
        return this.items[name] || this.itemsByKey[name];
    },
    clearWarning: function( type ){
        for (var it in this.items ) {
            var item = this.items[it];
            if( !type ){
                item.clearWarning( "empty" );
                item.clearWarning( "invalid" );
            }else{
                item.clearWarning( type )
            }
        }
    },
    reset: function(){
        for (var it in this.items ) {
            var item = this.items[it];
            item.reset();
        }
    },
    destroy : function(){
        Object.each(this.items, function(item){
            item.destroy();
        }.bind(this));
        this.container.empty();
        MWF.release(this);
    }
});
