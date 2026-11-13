o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Eldate Element UI 기반 날짜 선택 컴포넌트.
 * @o2cn 날짜 선택
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var input = this.form.get("name"); //컴포넌트 조회
 * //방법 2
 * var input = this.target; //컴포넌트 이벤트 스크립트에서 조회
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 * @see {@link https://element.eleme.cn/#/zh-CN/component/date-picker|Element UI DatePicker 날짜 선택기}
 */
MWF.xApplication.process.Xform.Eldate = MWF.APPEldate =  new Class(
    /** @lends MWF.xApplication.process.Xform.Eldate# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * input이 포커스를 잃을 때 발생. this.event[0]은 컴포넌트 인스턴스
         * @event MWF.xApplication.process.Xform.Eldate#blur
         * @see {@link https://element.eleme.cn/#/zh-CN/component/date-picker|날짜 선택 컴포넌트의 Events 장}
         */
        /**
         * input이 포커스를 얻을 때 발생. this.event[0]은 컴포넌트 인스턴스
         * @event MWF.xApplication.process.Xform.Eldate#focus
         * @see {@link https://element.eleme.cn/#/zh-CN/component/date-picker|날짜 선택 컴포넌트의 Events 장}
         */
        /**
         * 사용자가 선택 값을 확인할 때 발생. this.event[0]은 컴포넌트 바인딩 값이며, 형식은 바인딩 값과 같고 value-format으로 제어 가능
         * @event MWF.xApplication.process.Xform.Eldate#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/date-picker|날짜 선택 컴포넌트의 Events 장}
         */
        "elEvents": ["focus", "blur", "change"]
    },
    _loadMergeReadContentNode: function( contentNode, data ){
        var d = o2.typeOf( data.data ) === "array" ? data.data : [data.data];
        contentNode.set("text", d.join( this.json.rangeSeparator ? " "+this.json.rangeSeparator+" " : " ~ " ) );
    },
    _queryLoaded: function(){
        this._loadReadEditAbeld();
        var data = this._getBusinessData();
        if (data){
            if( ["monthrange","daterange"].contains(this.json.selectType) ) {
                if (typeOf(data) === "string") this._setBusinessData([data, ""]);
            }else if( ["dates"].contains(this.json.selectType) ){
                if (typeOf(data) === "string") this._setBusinessData([data]);
            }else{
                if( typeOf(data) === "array" )this._setBusinessData(data[0] || "");
            }
        }
    },
    __setReadonly: function(data){
        if (this.isReadonly()){
            var format = this.json.format || this.json.valueFormat;
            if( o2.typeOf(data) === "array" ){
                if( ["monthrange","daterange"].contains(this.json.selectType) ) {
                    var ds = data.map(function (d){
                        return this.isValidDate(d) ? this.formatDate(new Date(d), format) : d;
                    }.bind(this));
                    this.node.set("text", this.json.rangeSeparator ? ds.join(this.json.rangeSeparator) : ds);
                }else{
                    this.node.set("text", this.isValidDate(data) ? this.formatDate(new Date(data), format) : data );
                }
            }else{
                this.node.set("text", this.isValidDate(data) ? this.formatDate(new Date(data), format) : data );
            }
            if( this.json.elProperties ){
                this.node.set(this.json.elProperties );
            }
            if (this.json.elStyles){
                this.node.setStyles( this._parseStyles(this.json.elStyles) );
            }

            if( !this.eventLoaded ){
                this._loadDomEvents();
                this.eventLoaded = true;
            }

            this.fireEvent("postLoad");
            this.fireEvent("load");
            this.isLoaded = true;
        }
    },
    _appendVueData: function(){
        if (!this.json.isReadonly && !this.form.json.isReadonly) this.json.isReadonly = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.editable) this.json.editable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.valueFormat) this.json.valueFormat = this.json.format || "";
        if (!this.json.prefixIcon) this.json.prefixIcon = "";
        if (!this.json.description) this.json.description = "";
        if (!this.json.popperClass) this.json.popperClass = "";
        this.json.pickerOptions = {
            firstDayOfWeek: this.json.firstDayOfWeek.toInt()
        }
        if (this.json.disabledDate && this.json.disabledDate.code){
            this.json.pickerOptions.disabledDate = function(date){
                return this.form.Macro.fire(this.json.disabledDate.code, this, date);
            }.bind(this)
        }

        this._setPopperClass();
    },
    _createElementHtml: function() {
        var html = "<el-date-picker";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :type=\"selectType\"";
        html += " :readonly=\"isReadonly\"";
        html += " :disabled=\"disabled\"";
        html += " :editable=\"editable\"";
        html += " :clearable=\"clearable\"";
        html += " :size=\"size\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :range-separator=\"rangeSeparator\"";
        html += " :start-placeholder=\"startPlaceholder\"";
        html += " :end-placeholder=\"endPlaceholder\"";
        html += " :value-format=\"valueFormat\"";
        html += " :format=\"format\"";
        html += " :picker-options=\"pickerOptions\"";
        html += " :popper-class=\"popperClass\"";
        // html += " :picker-options=\"{" +
            // ":firstDayOfWeek=firstDayOfWeek," +
            // ":disabledDate=\"disabledDateFun\""+
            // "}\"";

        this.options.elEvents.forEach(function(k){
            html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
        });

        if (this.json.elProperties){
            Object.keys(this.json.elProperties).forEach(function(k){
                if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
            }, this);
        }

        if (this.json.elStyles) html += " :style=\"elStyles\"";

        html += ">";

        if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-date-picker>";

        return html;
    },
    getInputData: function(){
        var data = this.json[this.json.$id];
        if( data === null ){
            if( ["monthrange","daterange"].contains(this.json.selectType) ) {
                return [];
            }else if( ["dates"].contains(this.json.selectType) ){
                return [];
            }else{
                return "";
            }
        }
        return this.json[this.json.$id];
    },

        getExcelData: function(){
            var value = this.getData();
            return o2.typeOf(value) === "array" ? value.join(", ") : value;
        },
        setExcelData: function(data){
            var arr = this.stringToArray(data);
            this.excelData = arr;
            var value = arr.length === 0  ? arr[0] : arr;
            this.setData(value, true);
        },
        isValidDate: function(dateString) {
            if( !dateString ){
                return false;
            }
            var date = new Date(dateString);
            return !isNaN(date.getTime());
        },
        formatDate: function (date, format) {
            var o = {
                'M+': date.getMonth() + 1, // 월
                'd+': date.getDate(), // 일
                'h+': date.getHours() % 12 === 0 ? 12 : date.getHours() % 12, // 시간
                'H+': date.getHours(), // 시간
                'm+': date.getMinutes(), // 분
                's+': date.getSeconds(), // 초
                'q+': Math.floor((date.getMonth() + 3) / 3), // 분기
                d: date.getDay(),
                S: date.getMilliseconds(), // 밀리초
                a: date.getHours() < 12 ? 'am' : 'pm', // 오전/오후
                A: date.getHours() < 12 ? 'AM' : 'PM' // AM/PM
            };
            if (/(y+)/.test(format)) {
                format = format.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
            }
            if (/(W+)/.test(format)) {
                o['W+'] = this.weekNumberOfDate(date);
            }
            for (let k in o) {
                if (new RegExp('(' + k + ')').test(format)) {
                    format = format.replace(
                        RegExp.$1,
                        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
                    );
                }
            }
            return format;
        },
        weekNumberOfDate: function ( date ){
            var target  = new Date(date.getTime());
            var dayNumber   =  ( 7 + date.getDay() - parseInt( (this.json.firstDayOfWeek || 0) ) ) % 7;
            target.setDate(target.getDate() - dayNumber + 3);
            var firstThursday = target.valueOf();
            target.setMonth(0, 1);
            if (target.getDay() !== 4) {
                target.setMonth(0, 1 + ((4 - target.getDay()) + 7) % 7);
            }
            return 1 + Math.ceil((firstThursday - target) / 608400000); // 604800000 = 7 * 24 * 3600 * 1000
        }
});
