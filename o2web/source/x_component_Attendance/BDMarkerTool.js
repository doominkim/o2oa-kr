/**
 * @fileoverview 바이두 지도의 표시 추가 도구 클래스, 외부 공개.
 * 사용자가 지도를 클릭해 점 마커를 추가할 수 있으며, 마커 아이콘 스타일을 설정할 수 있음.
 * 주 진입 클래스는 <a href="symbols/BMapLib.MarkerTool.html">MarkerTool</a>,
 * Baidu Map API 1.2 기반.
 *
 * @author Baidu Map Api Group
 * @version 1.2
 */

/**
 * @namespace BMap의 모든 library 클래스는 BMapLib 네임스페이스 아래에 둠
 */
var BMapLib = window.BMapLib = BMapLib || {};
if(typeof BMapLib._toolInUse == "undefined"){

    BMapLib._toolInUse = false; //이 도구의 사용 중 여부, 여러 마우스 도구를 동시에 사용하는 상황 방지
                                //예: 사용자가 표시 추가 도구를 열면 거리 측정 도구를 열 수 없음.
}
(function() {
    /**
     * baidu tangram 코드 부분, tangram 코드는 클래스 상속,
     * 이벤트 발생, 이벤트 바인딩 등 기본 기능을 제공하며 여러 브라우저와 호환됨. tangram 기반으로 MarkerTool 구축
     * 비교적 빠름.
     */
    var baidu = baidu || {guid : "$BAIDU$"};

    (function() {
        // 페이지 단위로 유일한 일부 속성은 window[baidu.guid]에 마운트해야 함
        window[baidu.guid] = {};

        /**
         * 원본 객체의 모든 속성을 대상 객체로 복사
         * @name baidu.extend
         * @function
         * @grammar baidu.extend(target, source)
         * @param {Object} target 대상 객체
         * @param {Object} source 원본 객체
         * @returns {Object} 대상 객체
         */
        baidu.extend = function (target, source) {
            for (var p in source) {
                if (source.hasOwnProperty(p)) {
                    target[p] = source[p];
                }
            }
            return target;
        };

        /**
         * @ignore
         * @namespace
         * @baidu.lang 언어 수준 캡슐화, 타입 판단·모듈 확장·기본 클래스 상속 및 객체 사용자 정의 이벤트 지원 포함.
         * @property guid 객체의 고유 식별자
         */
        baidu.lang = baidu.lang || {};

        /**
         * 현재 페이지의 고유 식별 문자열 반환.
         * @function
         * @grammar baidu.lang.guid()
         * @returns {String} 현재 페이지의 고유 식별 문자열
         */
        baidu.lang.guid = function() {
            return "TANGRAM__" + (window[baidu.guid]._counter ++).toString(36);
        };

        window[baidu.guid]._counter = window[baidu.guid]._counter || 1;

        /**
         * 모든 클래스 인스턴스의 컨테이너
         * key는 각 인스턴스의 guid
         */
        window[baidu.guid]._instances = window[baidu.guid]._instances || {};

        /**
         * Tangram 상속 메커니즘이 제공하는 기본 클래스, 사용자는 baidu.lang.Class를 상속해 속성과 메서드를 얻을 수 있음.
         * @function
         * @name baidu.lang.Class
         * @grammar baidu.lang.Class(guid)
         * @param {string} guid	객체의 고유 식별자
         * @meta standard
         * @remark baidu.lang.Class와 그 하위 클래스의 인스턴스는 모두 전역 고유 식별자 guid를 가짐.
         * guid는 생성자에서 생성되므로 baidu.lang.Class를 상속한 클래스는 직접 또는 간접적으로 그 생성자를 호출해야 함.<br>
         * baidu.lang.Class 생성자에서 guid를 생성하는 방식은 guid의 유일성을 보장하므로 각 인스턴스는 전역 고유 guid를 가짐.
         */
        baidu.lang.Class = function(guid) {
            this.guid = guid || baidu.lang.guid();
            window[baidu.guid]._instances[this.guid] = this;
        };

        /**
         * 대상 매개변수가 string 타입 또는 String 객체인지 판단
         * @name baidu.lang.isString
         * @function
         * @grammar baidu.lang.isString(source)
         * @param {Any} source 대상 매개변수
         * @shortcut isString
         * @meta standard
         *
         * @returns {boolean} 유형 판단 결과
         */
        baidu.lang.isString = function (source) {
            return '[object String]' == Object.prototype.toString.call(source);
        };

        /**
         * 대상 매개변수가 function 또는 Function 인스턴스인지 판단
         * @name baidu.lang.isFunction
         * @function
         * @grammar baidu.lang.isFunction(source)
         * @param {Any} source 대상 매개변수
         * @returns {boolean} 유형 판단 결과
         */
        baidu.lang.isFunction = function (source) {
            return '[object Function]' == Object.prototype.toString.call(source);
        };

        /**
         * 기본 toString 메서드를 재정의해 반환 정보를 더 정확하게 함.
         * @return {string} 객체의 String 표현
         */
        baidu.lang.Class.prototype.toString = function(){
            return "[object " + (this._className || "Object" ) + "]";
        };

        /**
         * 객체가 보유한 리소스(주로 사용자 정의 이벤트) 해제.
         * @name dispose
         * @grammar obj.dispose()
         */
        baidu.lang.Class.prototype.dispose = function(){
            delete window[baidu.guid]._instances[this.guid];
            for(var property in this){
                if (!baidu.lang.isFunction(this[property])) {
                    delete this[property];
                }
            }
            this.disposed = true;
        };

        /**
         * 사용자 정의 이벤트 객체.
         * @function
         * @name baidu.lang.Event
         * @grammar baidu.lang.Event(type[, target])
         * @param {string} type	 이벤트 유형 이름. 이벤트와 일반 메서드를 구분하기 쉽도록 이벤트 유형 이름은 반드시 "on"(소문자)으로 시작해야 함.
         * @param {Object} [target]이벤트를 발생시킨 객체
         * @meta standard
         * @remark 이 모듈을 가져오면 Class에 이벤트 확장 메서드 3개(addEventListener, removeEventListener, dispatchEvent)가 자동으로 추가됨.
         * @see baidu.lang.Class
         */
        baidu.lang.Event = function (type, target) {
            this.type = type;
            this.returnValue = true;
            this.target = target || null;
            this.currentTarget = null;
        };

        /**
         * 객체의 이벤트 리스너 등록. baidu.lang.Event를 가져온 후 Class 하위 클래스 인스턴스에서 이 메서드를 사용할 수 있음.
         * @grammar obj.addEventListener(type, handler[, key])
         * @param 	{string}   type         사용자 정의 이벤트 이름
         * @param 	{Function} handler      사용자 정의 이벤트 발생 시 호출할 콜백 함수
         * @param 	{string}   [key]		이벤트 리스너 함수에 지정하는 이름, 제거 시 사용 가능. 제공하지 않으면 전역 고유 key를 기본 생성.
         * @remark 	이벤트 유형은 대소문자 구분. 사용자 정의 이벤트 이름이 소문자 "on"으로 시작하지 않으면 "on"을 붙여 판단하므로 "click"과 "onclick"은 같은 이벤트로 간주됨.
         */
        baidu.lang.Class.prototype.addEventListener = function (type, handler, key) {
            if (!baidu.lang.isFunction(handler)) {
                return;
            }
            !this.__listeners && (this.__listeners = {});
            var t = this.__listeners, id;
            if (typeof key == "string" && key) {
                if (/[^\w\-]/.test(key)) {
                    throw("nonstandard key:" + key);
                } else {
                    handler.hashCode = key;
                    id = key;
                }
            }
            type.indexOf("on") != 0 && (type = "on" + type);
            typeof t[type] != "object" && (t[type] = {});
            id = id || baidu.lang.guid();
            handler.hashCode = id;
            t[type][id] = handler;
        };

        /**
         * 객체의 이벤트 리스너 제거. baidu.lang.Event를 가져온 후 Class 하위 클래스 인스턴스에서 이 메서드를 사용할 수 있음.
         * @grammar obj.removeEventListener(type, handler)
         * @param {string}   type     이벤트 유형
         * @param {Function|string} handler  제거할 이벤트 리스너 함수 또는 리스너 함수의 key
         * @remark 	두 번째 매개변수 handler가 해당 사용자 정의 이벤트에 바인딩되어 있지 않으면 아무것도 하지 않음.
         */
        baidu.lang.Class.prototype.removeEventListener = function (type, handler) {
            if (baidu.lang.isFunction(handler)) {
                handler = handler.hashCode;
            } else if (!baidu.lang.isString(handler)) {
                return;
            }
            !this.__listeners && (this.__listeners = {});
            type.indexOf("on") != 0 && (type = "on" + type);
            var t = this.__listeners;
            if (!t[type]) {
                return;
            }
            t[type][handler] && delete t[type][handler];
        };

        /**
         * 사용자 정의 이벤트를 발생시켜 바인딩된 함수를 모두 실행. baidu.lang.Event를 가져온 후 Class 하위 클래스 인스턴스에서 이 메서드를 사용할 수 있음.
         * @grammar obj.dispatchEvent(event, options)
         * @param {baidu.lang.Event|String} event 	Event 객체 또는 이벤트 이름(1.1.1부터 지원)
         * @param {Object} options 확장 매개변수, 포함된 속성 키-값이 Event 객체에 확장됨(1.2부터 지원)
         * @remark addEventListenr로 바인딩한 사용자 정의 이벤트 콜백 함수 외에 객체에 직접 바인딩한 사용자 정의 이벤트도 호출.
         * 예:<br>
         * myobj.onMyEvent = function(){}<br>
         * myobj.addEventListener("onMyEvent", function(){});
         */
        baidu.lang.Class.prototype.dispatchEvent = function (event, options) {
            if (baidu.lang.isString(event)) {
                event = new baidu.lang.Event(event);
            }
            !this.__listeners && (this.__listeners = {});
            options = options || {};
            for (var i in options) {
                event[i] = options[i];
            }
            var i, t = this.__listeners, p = event.type;
            event.target = event.target || this;
            event.currentTarget = this;
            p.indexOf("on") != 0 && (p = "on" + p);
            baidu.lang.isFunction(this[p]) && this[p].apply(this, arguments);
            if (typeof t[p] == "object") {
                for (i in t[p]) {
                    t[p][i].apply(this, arguments);
                }
            }
            return event.returnValue;
        };

        /**
         * 타입 생성자 간 상속 관계 구성
         * @name baidu.lang.inherits
         * @function
         * @grammar baidu.lang.inherits(subClass, superClass[, className])
         * @param {Function} subClass 하위 클래스 생성자
         * @param {Function} superClass 상위 클래스 생성자
         * @param {string} className 클래스명 식별자
         * @remark subClass가 superClass의 prototype을 상속하도록 함,
         * 따라서 subClass의 인스턴스는 superClass의 prototype에 정의된 모든 속성과 메서드를 사용할 수 있음.<br>
         * 이 함수는 실제로 subClass와 superClass의 프로토타입 체인을 연결하고 subClass의 constructor를 보정함.<br>
         * <strong>주의: 생성자를 상속하려면 subClass 안에서 call을 호출해야 함. 자세한 내용은 아래 demo 예제 참고</strong>
         * @shortcut inherits
         * @meta standard
         * @see baidu.lang.Class
         */
        baidu.lang.inherits = function (subClass, superClass, className) {
            var key, proto,
                selfProps = subClass.prototype,
                clazz = new Function();
            clazz.prototype = superClass.prototype;
            proto = subClass.prototype = new clazz();
            for (key in selfProps) {
                proto[key] = selfProps[key];
            }
            subClass.prototype.constructor = subClass;
            subClass.superClass = superClass.prototype;

            if ("string" == typeof className) {
                proto._className = className;
            }
        };

    })();


    /**
     * MarkerTool 코드 부분, 이 클래스는 기본 클래스 baidu.lang.Class를 상속하여 사용자 정의 이벤트 발송이 쉬움. 예: markend 이벤트 발송
     * @exports MarkerTool as BMapLib.MarkerTool
     */
    var MarkerTool =
    /**
     * MarkerTool 클래스 생성자
     * @class 지도에 마커를 추가하는 클래스, 지도 클릭으로 점 마커를 추가하는 <b>진입점</b>.
     * 이 클래스를 인스턴스화한 후 클래스가 제공하는 <a href="symbols/BMapLib.MarkerTool.html#open">open</a>
     * 메서드를 호출하여 점 마커 추가 상태를 시작할 수 있음.
     *
     * @constructor
     * @param {Map} map Baidu map 인스턴스 객체
     * @param {Json Object} opts 선택 입력 매개변수, 필수 아님. 입력 가능한 옵션:
     * <br />"<b>icon</b>" : {Icon} 마커에 사용할 아이콘, 마커 표시 시 마우스 따라다니는 스타일도 이 속성으로 설정
     * <br />"<b>followText</b>" : {String} 마우스를 따라 이동하는 설명 문구, 기본값은 빈 값
     * <br />"<b>autoClose</b>" : {Boolean} Marker 추가 후마다 도구 자동 닫기 여부
     *
     * @example <b>참고 예제:</b><br />
     * var map = new BMap.Map("container");<br />map.centerAndZoom(new BMap.Point(116.404, 39.915), 15);<br />var mkrTool = new BMapLib.MarkerTool(map, {followText: "점 하나 추가"});
     */
        BMapLib.MarkerTool = function(map, opts){
            baidu.lang.Class.call(this);//기본 클래스 baidu.lang.Class의 생성자 상속
            this._map = map;
            this._opts = {
                icon: MarkerTool.SYS_ICONS[8], //기본으로 빨간 물방울 스타일 선택
                followText: "지도를 클릭해 근무지를 추가하세요", //마우스 따라다니는 문구 안내
                autoClose: true //마커 추가 완료 후 이 도구를 닫을지 여부
            };

            baidu.extend(this._opts, opts);//사용자 설정 매개변수로 기본 설정 매개변수 덮어쓰기

            this._isOpen = false; // 컨트롤 항목의 현재 상태
            this._opts.followText = this._checkStr(this._opts.followText); //문자열 유효성 검사

            this._followMarker = null; //마우스 따라다니는 Marker
            this._followLabel = null; //마우스 따라다니는 문구 안내
        };

    baidu.lang.inherits(MarkerTool, baidu.lang.Class , "MarkerTool");//기본 클래스 baidu.lang.Class의 모든 prototype 속성에 연결된 메서드 상속

    /**
     * 도구 열기
     * @return {Boolean} true는 열기 성공, false는 열기 실패
     */
    MarkerTool.prototype.open = function(){
        if(!this._map){
            return false;
        }
        if (this._isOpen == true){
            return true;
        }
        if (BMapLib._toolInUse){
            return false;
        }
        BMapLib._toolInUse = true; //현재 마우스 상태가 사용 중임. 마우스 도구가 여러 개 있으면,
        //이 변수로 지도 위에 한 가지 마우스 조작 상태만 존재하도록 제한할 수 있음
        this._isOpen = true;

        // mousemove 및 click 이벤트 바인딩
        if (!this._binded){
            this._bind();
            this._binded = true;
        }
        //따라다니는 Marker 초기화
        if (!this._followMarker) {
            this._followMarker = new BMap.Marker(this._map.getCenter(), {offset: new BMap.Size(-10, -10)}); //-10픽셀 오프셋, cursor 문제 해결
            this._map.addOverlay(this._followMarker);
            this._followMarker.setZIndex(1000); // 따라다니는 Marker의 z축 높이 설정
            this._followMarker.hide();
        }

        //따라다니는 Label 초기화
        if (!this._followLabel){
            this._followLabel = new BMap.Label(this._opts.followText, {offset: new BMap.Size(20, 0)});
        }

        this._preCursor = this._map.getDefaultCursor(); //현재 마우스 cursor 기록
        this._map.setDefaultCursor("url(" + MarkerTool.CUR_IMG + "), default");//마우스 스타일 설정

        return true;
    };
    /**
     * 도구 닫기
     * @return 반환값 없음
     */
    MarkerTool.prototype.close = function(){
        if (!this._isOpen){
            return;
        }

        //이벤트 바인딩 해제
        this._map.removeEventListener("mousemove", this._mouseMoveHandler);
        this._map.removeEventListener("click", this._clickHandler);

        this._followMarker.hide();//따라다니는 marker 숨기기
        this._map.setDefaultCursor(this._preCursor);//마우스 스타일 설정
        BMapLib._toolInUse = false;
        this._isOpen = false;
        this._binded = false;
    };
    /**
     * 마커 아이콘 및 마우스 따라다니는 스타일 설정
     * @param {Icon} icon 마커 스타일 및 마우스 따라다니는 스타일, 사용자가 Icon을 쉽게 설정하도록 시스템에서
     * 24가지 기본 아이콘 제공: BMapLib.MarkerTool.SYS_ICON[0] -- BMapLib.MarkerTool.SYS_ICON[23]
     */
    MarkerTool.prototype.setIcon = function(icon){
        if (!icon || !(icon instanceof BMap.Icon)){
            return;
        }
        this._opts.icon = icon;
    };
    /**
     * 마커 아이콘 및 마우스 따라다니는 스타일 조회
     * @return {Icon} 현재 마커 및 마우스 따라다니는 스타일
     */
    MarkerTool.prototype.getIcon = function(){
        return this._opts.icon;
    };
    /**
     * 문자열 유효성 검사, xss 취약점 입력 문자 제거
     * @return {String} 유효한 문자
     */
    MarkerTool.prototype._checkStr = function(str){
        if (!str){
            return "";
        }
        return str.replace(/</g, "&lt;").replace(/>/g, "&gt;");
    };
    /**
     * 지도의 mousemove 및 click 이벤트 바인딩
     * @return 반환값 없음
     */
    MarkerTool.prototype._bind = function(){
        var me = this;
        if (!me._isOpen){//도구 열림 여부 판단
            return;
        }

        //mousemove 이벤트 바인딩
        me._mouseMoveHandler = function(evt){
            var pt = evt.point;
            me._followMarker.setIcon(me._opts.icon); //매번 최신 Icon 확인
            me._followMarker.setPosition(pt);
            me._followMarker.setLabel(me._followLabel);
            me._followMarker.show();
        };
        me._map.addEventListener("mousemove", me._mouseMoveHandler);

        //click 이벤트 바인딩
        me._clickHandler = function(evt){
            var evtPix = evt.pixel;
            var iconPix = new BMap.Pixel(evtPix.x - 10, evtPix.y - 10); //_followMarker의 -10픽셀 문제 보정, cursor 문제 해결
            var pt = me._map.pixelToPoint(iconPix);

            var mkr = new BMap.Marker(pt, {
                icon: me._opts.icon,
                enableDragging : true
            });
            me._map.addOverlay(mkr);

            /**
             * 마커 추가 과정에서 지도를 클릭해 마커 추가를 마칠 때마다 이벤트를 발송하는 인터페이스
             * @name MarkerTool#onmarkend
             * @event
             * @param {Event Object} e 콜백 함수는 event 매개변수를 반환하며, 다음 반환값 포함:
             * <br />"<b>type</b> : {String} 이벤트 유형
             * <br />"<b>target</b>: {MarkerTool} 현재 MarkerTool 객체
             * <br />"<b>marker</b>: {Marker} 현재 추가된 Marker 마커
             *
             * @example <b>참고 예제:</b><br />
             * mkrTool.addEventListener("markend", function(e) {  alert(e.type);  });
             */
            var event = new baidu.lang.Event("onmarkend");
            event.marker = mkr;
            me.dispatchEvent(event);

            if(me._opts.autoClose){ //도구 자동 닫기
                me.close();
            }
        };
        me._map.addEventListener("click", me._clickHandler);
    };

    MarkerTool.CUR_IMG = "../x_component_Attendance/$AddressExplorer/default/icon/transparent.cur"; //마우스 투명 스타일, 배포 시 절대 경로로 수정
    MarkerTool.ICON_IMG = "../x_component_Attendance/$AddressExplorer/default/icon/us_mk_icon.png"; //아이콘 스타일, 배포 시 절대 경로로 수정
    MarkerTool.SYS_ICONS = [//MarkerTool이 제공하는 시스템 스타일, 사용자 선택용
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(0, 0)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(-23, 0)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(-46, 0)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(-69, 0)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(-92, 0)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(6, 21), imageOffset: new BMap.Size(-115, 0)}),

        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(0, -21)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-23, -21)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-46, -21)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-69, -21)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-92, -21)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(23, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-115, -21)}),

        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(0, -46)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(-23, -46)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(-46, -46)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(-69, -46)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(-92, -46)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(21, 21), {anchor: new BMap.Size(1, 21), imageOffset: new BMap.Size(-115, -46)}),

        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(25, 25), {anchor: new BMap.Size(12, 25), imageOffset: new BMap.Size(0, -67)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(25, 25), {anchor: new BMap.Size(12, 25), imageOffset: new BMap.Size(-25, -67)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(24, 25), {anchor: new BMap.Size(12, 25), imageOffset: new BMap.Size(-50, -67)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(25, 25), {anchor: new BMap.Size(12, 25), imageOffset: new BMap.Size(-75, -67)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(25, 25), {anchor: new BMap.Size(12, 25), imageOffset: new BMap.Size(-100, -67)}),
        new BMap.Icon(MarkerTool.ICON_IMG, new BMap.Size(19, 25), {anchor: new BMap.Size(9, 25), imageOffset: new BMap.Size(-125, -67)})
    ];

})();//클로저 끝