/** ***** BEGIN LICENSE BLOCK *****
 * |------------------------------------------------------------------------------|
 * | O2OA 활력 있는 업무, 무한한 창의    o2m.api.js                                            |
 * |------------------------------------------------------------------------------|
 * | Distributed under the AGPL license:                                          |
 * |------------------------------------------------------------------------------|
 * | Copyright © 2018, o2oa.net, o2server.io O2 Team                              |
 * | All rights reserved.                                                         |
 * |------------------------------------------------------------------------------|
 *
 *  This file is part of O2OA.
 *
 *  O2OA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  O2OA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ******/


/**
 * @summary
 * <b>o2m</b>은 O2OA 모바일 APP이 제공하는 네이티브 컨트롤 호출 기능으로, 개발자가 촬영·위치 조회 등 휴대폰 시스템 기능을 효율적으로 사용하도록 돕고, 스캔·네이티브 앱 열기·시간·사용자·조직 선택 등 업무 기능을 직접 사용할 수 있게 하여 포털에 네이티브에 가까운 경험을 제공함
 * <br/>
 * <b>o2m</b>은 O2OA 모바일 APP에서만 기능을 제공함
 * @namespace o2m
 * @o2cn 플랫폼 모바일 APP API
 * @o2category mobile
 * @o2ordernumber 13
 * @o2range {프로세스 양식|포털}
 * @o2syntax
 * // 모바일 프로세스 양식, 포털에서 사용 가능
 * window.o2m
 *
 */
(function () {
    this.o2m = {
        version: {
            v: "1.1.0",
            build: "2021.01.20",
            info: "O2OA 활기찬 업무, 무한한 창의. Copyright © 2021, o2oa.net O2 Team All rights reserved."
        },
        log: function (message) {
            window.o2android && window.o2android.o2mLog ? window.o2android.o2mLog(message) : window.webkit.messageHandlers.o2mLog.postMessage(message);
        },
        events: {
            onReload: [],
            onWorkClose: [],
            onDocumentClose: []
        }
    }

    this.o2m.portalEvent = {
        reload: function () {
            if (o2m.events.onReload && o2m.events.onReload.length > 0) {
                for (let i = 0; i < o2m.events.onReload.length; i++) {
                    const f = o2m.events.onReload[i]
                    if (typeof f === "function") {
                        f()
                    }
                }
            } else {
                window.o2m.util.navigation.refresh()
            }
        },
        workClose: function () {
            if (o2m.events.onWorkClose && o2m.events.onWorkClose.length > 0) {
                for (let i = 0; i < o2m.events.onWorkClose.length; i++) {
                    const f = o2m.events.onWorkClose[i]
                    if (typeof f === "function") {
                        f()
                    }
                }
            } else {
                window.o2m.util.navigation.refresh()
            }
        },
        documentClose: function () {
            if (o2m.events.onDocumentClose && o2m.events.onDocumentClose.length > 0) {
                for (let i = 0; i < o2m.events.onDocumentClose.length; i++) {
                    const f = o2m.events.onDocumentClose[i]
                    if (typeof f === "function") {
                        f()
                    }
                }
            } else {
                window.o2m.util.navigation.refresh()
            }
        }
    }

    /**
     * 이벤트 추가
     * @method addEvent
     * @memberOf o2m
     * @static
     * @param {String} name 이벤트 이름 (reload, workClose, documentClose)
     * @param {Function} events 실행 메서드
     * @example
     * const reloadFun = function(){
     *     //업무 실행
     * }
     * o2m.addEvent("reload", reloadFun ); //이벤트 추가
     * reloadFun.remove(); //이벤트에서 삭제
     * o2m.removeEvent("onReload", reloadFun ); //이벤트에서 삭제
     */
    this.o2m.addEvent = function (name, ...events) {
        switch (name) {
            case "reload":
                events.map((f) => {
                    f.remove = function () {
                        o2m.events.onReload.splice(o2m.events.onReload.indexOf(this), 1);
                    }.bind(f);
                    o2m.events.onReload.push(f)
                });
                break;
            case "workClose":
                events.map((f) => {
                    f.remove = function () {
                        o2m.events.onWorkClose.splice(o2m.events.onWorkClose.indexOf(this), 1);
                    }.bind(f);
                    o2m.events.onWorkClose.push(f);
                });
                break;
            case "documentClose":
                events.map((f) => {
                    f.remove = function () {
                        o2m.events.onDocumentClose.splice(o2m.events.onDocumentClose.indexOf(this), 1);
                    }.bind(f);
                    o2m.events.onDocumentClose.push(f)
                })
                break
        }
    };

    this.o2m.removeEvent = function (name, ...events) {
        switch (name) {
            case "reload":
                events.map((f) => {
                    const index = o2m.events.onReload.indexOf(f);
                    if (index > -1) {
                        o2m.events.onReload.splice(index, 1);
                    }
                });
                break;
            case "workClose":
                events.map((f) => {
                    const index = o2m.events.onWorkClose.indexOf(f);
                    if (index > -1) {
                        o2m.events.onWorkClose.splice(index, 1);
                    }
                });
                break;
            case "documentClose":
                events.map((f) => {
                    const index = o2m.events.onDocumentClose.indexOf(f);
                    if (index > -1) {
                        o2m.events.onDocumentClose.splice(index, 1);
                    }
                });
                break;
        }
    };

    /**
     * 포털 로드 완료 후 실행
     * @method portalLoaded
     * @memberOf o2m
     * @static
     * @param {String} callback 작업 버튼 클릭 시 실행 함수
     * @param {String} iconClassName 작업 버튼 icon className
     * @param {Object[]} menuList 작업 버튼 목록 [{"title": "메뉴 이름", "callback": "콜백 함수 이름"}]
     * @example
     * window.o2m.portalLoaded('callbackName'); //작업 버튼 추가
     */
    this.o2m.portalLoaded = function (callback, iconClassName, menuList) {
        if (!callback && !menuList) {
            console.error('매개변수가 올바르지 않습니다. callback과 menuList가 비어 있습니다')
            return;
        }
        const body = {
            type: 'portalLoaded',
            data: {
                rightActionCallback: callback
            }
        };
        if (iconClassName) {
            body.data.rightActionIcon = iconClassName;
        }
        if (menuList) {
            body.data.menuList = menuList;
        }
        if (window.flutter_inappwebview && window.flutter_inappwebview.callHandler) {
            window.flutter_inappwebview.callHandler('o2android', JSON.stringify(body));
            console.debug('flutter_inappwebview portalLoaded loaded =============== ');
        } else if (window.o2f) {
            window.o2f.addReady(()=> {
                window.o2android.postMessage(JSON.stringify(body));
                console.debug('o2f portalLoaded loaded =============== ');
            });
        } else if (window.o2android && window.o2android.postMessage) {
            window.o2android.postMessage(JSON.stringify(body));
            console.debug('o2android portalLoaded loaded =============== ');
        } else {
            console.error('portalLoaded 실행 실패: 환경 정보를 읽지 못했습니다')
        }
    }

    this.o2m._uploadFileCallback = function (result) {
        console.log("o2m _uploadFileCallback ", result);
    };

    /**
     * 파일 업로드 후 파일 id 반환
     * @method uploadFile
     * @memberOf o2m
     * @static
     * @param {Object} args 전달 객체
     * <pre><code class='language-js'>{
     *  "callback": function,  // 업로드 결과 반환 함수
     *  "onlyTakePhoto": false // 촬영만 가능
     * }</code></pre>
     * @example
     * o2m.uploadFile({
     *  callback : function(result) {//callback은 button 클릭 후 호출됨}
     * });
     *
     */
    this.o2m.uploadFile = function (args) {
        if (!args) {
            console.error("o2m.uploadFile: 매개변수 오류!")
            return;
        }
        const callback = args.callback ? args.callback : null;
        if (callback && typeof callback === "function") {
            o2m._uploadFileCallback  = callback;
        } else {
            console.error("o2m.uploadFile: callback 매개변수가 누락되었습니다")
            return;
        }
        const onlyTakePhoto = args.onlyTakePhoto || false;
        const body = {
            type: "uploadFile",
            data: {
                callback: "o2m._uploadFileCallback",
                onlyTakePhoto: onlyTakePhoto
            }
        };
        window.o2android.postMessage(JSON.stringify(body));
    }

    /** ***** BEGIN NOTIFICATION BLOCK *****
     @ignore
     @summary notification 모듈
      alert
      confirm
      prompt
      vibrate
      toast
      actionSheet
      showLoading
      hideLoading
      * ***** END NOTIFICATION BLOCK ******/

    this.o2m.notification = {};

    const _notification_post = function (body, onFail) {
        if (body == null) {
            if (onFail && typeof onFail === "function") {
                onFail("매개변수 오류!");
                return
            }
        }
        const message = JSON.stringify(body);
        if (window.flutter_inappwebview && window.flutter_inappwebview.callHandler) {
            window.flutter_inappwebview.callHandler('o2mNotification', message);
        } else if (window.o2mNotification && window.o2mNotification.postMessage) {
            window.o2mNotification.postMessage(message);
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.o2mNotification) {
            window.webkit.messageHandlers.o2mNotification.postMessage(message);
        } else {
            if (onFail && typeof onFail === "function") {
                onFail("O2OA 모바일 앱에서 사용하세요!");
            }
        }
    };


    this.o2m.notification.alertSuccess = function () {
        console.log("notification alert back");
    };
    const _o2m_n_alert = function (alert) {
        const message = alert && alert.message ? alert.message : "";
        const title = alert && alert.title ? alert.title : "";
        const buttonName = alert && alert.buttonName ? alert.buttonName : "";
        const onSuccess = alert && alert.onSuccess ? alert.onSuccess : null;
        const onFail = alert && alert.onFail ? alert.onFail : null;
        if (message === "") {
            if (typeof onFail === "function") {
                onFail("메시지 내용은 비워 둘 수 없습니다!");
            }
            return;
        }
        if (onSuccess && typeof onSuccess === "function") {
            o2m.notification.alertSuccess = onSuccess;
        }
        const body = {
            type: "alert",
            callback: "o2m.notification.alertSuccess",
            data: {
                message: message,
                title: title,
                buttonName: buttonName,
            }
        };
        _notification_post(body, onFail);
    };
    /**
     * 네이티브 Alert 알림 팝업
     * @method alert
     * @memberOf o2m
     * @o2membercategory notification
     * @static
     * @param {Object} obj 알림 창에 전달할 객체
     * <pre><code class='language-js'>{
     *  "title": "알림",  //메시지 제목, 비워 둘 수 있음
     *  "message": "여기는 메시지 내용입니다", //메시지 내용
     *  "buttonName": "확인", //확인 버튼 이름
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/notification_alert.jpeg">
     * </caption>
     * o2m.notification.alert({
     *  message: "안녕하세요",
     *  title: "알림",//비워 둘 수 있음
     *  buttonName: "확인함",
     *  onSuccess : function() {//onSuccess는 button 클릭 후 호출됨},
     *  onFail : function(err) {}
     * });
     *
     */
    this.o2m.notification.alert = _o2m_n_alert;

    //notification.confirm
    this.o2m.notification.confirmSuccess = function (index) {
        console.log("notification confirm back, click button index: " + index);
    };
    const _o2m_n_confirm = function (c) {
        const buttonLabels = c && c.buttonLabels ? c.buttonLabels : ["확인", "취소"];
        const message = c && c.message ? c.message : "";
        const title = c && c.title ? c.title : "";
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (message === "") {
            if (typeof onFail === "function") {
                onFail("메시지 내용(message)은 비워 둘 수 없습니다!");
            }
            return;
        }
        if (buttonLabels.length != 2) {
            if (typeof onFail === "function") {
                onFail("버튼 이름 배열의 길이는 2여야 합니다!");
            }
            return;
        }
        if (onSuccess && typeof onSuccess === "function") {
            o2m.notification.confirmSuccess = onSuccess;
        }
        const body = {
            type: "confirm",
            callback: "o2m.notification.confirmSuccess",
            data: {
                message: message,
                title: title,
                buttonLabels: buttonLabels,
            }
        };
        _notification_post(body, onFail);
    }
    /**
     * 네이티브 confirm 알림 팝업
     * @method confirm
     * @memberOf o2m
     * @o2membercategory notification
     * @static
     * @param {Object} obj 알림 창에 전달할 객체
     * <pre><code class='language-js'>{
     *  "title": "알림",  //메시지 제목, 비워 둘 수 있음
     *  "message": "여기는 메시지 내용입니다", //메시지 내용
     *  "buttonLabels": ["확인", "취소"], //버튼 이름 Array[String]
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/notification_confirm.jpeg">
     * </caption>
     * o2m.notification.confirm({
     *  message: "저를 좋아하나요?",
     *  title: "알림",//비워 둘 수 있음
     *  buttonLabels: ['좋아함', '안 좋아함'],
     *  onSuccess : function(buttonIndex) {
     *    //onSuccess는 button 클릭 후 호출됨
     *    //buttonIndex: 0 클릭된 버튼의 인덱스 값, Number 유형, 0부터 시작
     * },
     *  onFail : function(err) {}
     * });
     *
     */
    this.o2m.notification.confirm = _o2m_n_confirm;

    //notification.prompt
    this.o2m.notification.promptSuccess = function (result) {
        console.log("notification prompt back, click button result: " + result);
    };
    const _o2m_n_prompt = function (c) {
        const buttonLabels = c && c.buttonLabels ? c.buttonLabels : ["확인", "취소"];
        const message = c && c.message ? c.message : "";
        const title = c && c.title ? c.title : "";
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (message === "") {
            if (typeof onFail === "function") {
                onFail("메시지 내용(message)은 비워 둘 수 없습니다!");
            }
            return;
        }
        if (buttonLabels.length != 2) {
            if (typeof onFail === "function") {
                onFail("버튼 이름 배열의 길이는 2여야 합니다!");
            }
            return;
        }
        if (onSuccess && typeof onSuccess === "function") {
            o2m.notification.promptSuccess = onSuccess;
        }
        const body = {
            type: "prompt",
            callback: "o2m.notification.promptSuccess",
            data: {
                message: message,
                title: title,
                buttonLabels: buttonLabels,
            }
        };
        _notification_post(body, onFail);
    }
    /**
     * 네이티브 prompt 알림 팝업
     * @method prompt
     * @memberOf o2m
     * @o2membercategory notification
     * @static
     * @param {Object} obj prompt에 전달할 객체
     * <pre><code class='language-js'>{
     *  "title": "알림",  //메시지 제목, 비워 둘 수 있음
     *  "message": "여기는 메시지 내용입니다", //메시지 내용
     *  "buttonLabels": ['계속', '그만'], //버튼 이름 Array[String]
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/notification_prompt.jpeg">
     * </caption>
     * o2m.notification.prompt({
     *  message: "다시 말해 줄래요?",
     *  title: "알림",//비워 둘 수 있음
     *  buttonLabels: ['계속', '그만'],
     *  onSuccess : function(result) {
     *     //result는 json 형식의 문자열이며, 내용은 다음과 같음:
     *     //{
     *     //    buttonIndex: 0, //클릭된 버튼의 인덱스 값, Number 유형, 0부터 시작
     *     //    value: '' //입력한 값
     *     //}
     * },
     *  onFail : function(err) {}
     * });
     *
     */
    this.o2m.notification.prompt = _o2m_n_prompt;


    //notification.vibrate
    this.o2m.notification.vibrateSuccess = function () {
        console.log("notification vibrate back, click button");
    };
    const _o2m_n_vibrate = function (c) {
        const duration = c && c.duration ? c.duration : 300;
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;

        if (onSuccess && typeof onSuccess === "function") {
            o2m.notification.vibrateSuccess = onSuccess;
        }
        const body = {
            type: "vibrate",
            callback: "o2m.notification.vibrateSuccess",
            data: {
                duration: duration
            }
        };
        _notification_post(body, onFail);
    };

    /**
     * 휴대폰 진동
     * @method vibrate
     * @memberOf o2m
     * @o2membercategory notification
     * @static
     * @param {Object} obj 진동에 전달할 객체
     * <pre><code class='language-js'>{
     *  "duration": 300,  //진동 시간, android에서 설정 가능 iOS는 무시
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.notification.vibrate({
     *  duration: 300,
     *  onSuccess : function() {
     * },
     *  onFail : function(err) {}
     * });
     *
     */
    this.o2m.notification.vibrate = _o2m_n_vibrate;

    //notification.toast
    this.o2m.notification.toastSuccess = function () {
        console.log("notification toast back, click button");
    };
    const _o2m_n_toast = function (c) {
        const duration = c && c.duration ? c.duration : 300;
        const message = c && c.message ? c.message : "";
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (message === "") {
            if (typeof onFail === "function") {
                onFail("메시지 내용(message)은 비워 둘 수 없습니다!");
            }
            return;
        }
        if (onSuccess && typeof onSuccess === "function") {
            o2m.notification.toastSuccess = onSuccess;
        }
        const body = {
            type: "toast",
            callback: "o2m.notification.toastSuccess",
            data: {
                duration: duration,
                message: message
            }
        };
        _notification_post(body, onFail);
    };

    /**
     * toast 알림
     * @method toast
     * @memberOf o2m
     * @o2membercategory notification
     * @static
     * @param {Object} obj toast에 전달할 객체
     * <pre><code class='language-js'>{
     *  "message": "여기는 알림 정보입니다",
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.notification.toast({
     *  message: "알림 메시지 내용",
     *  onSuccess : function() {
     * },
     *  onFail : function(err) {}
     * });
     *
     */
    this.o2m.notification.toast = _o2m_n_toast;

    //notification.actionSheet
    this.o2m.notification.actionSheetSuccess = function (buttonIndex) {
        console.log("notification actionSheet back, click button:" + buttonIndex);
    };
    const _o2m_n_actionSheet = function (c) {
        const title = c && c.title ? c.title : "";
        const cancelButton = c && c.cancelButton ? c.cancelButton : "취소";
        const otherButtons = c && c.otherButtons ? c.otherButtons : [];
        const tooManyButtons = c && c.tooManyButtons ? c.tooManyButtons : false;
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (otherButtons.length < 1) {
            if (typeof onFail === "function") {
                onFail("기타 버튼 목록은 비워 둘 수 없습니다!");
            }
            return;
        }
        if (onSuccess && typeof onSuccess === "function") {
            o2m.notification.actionSheetSuccess = onSuccess;
        }
        const body = {
            type: "actionSheet",
            callback: "o2m.notification.actionSheetSuccess",
            data: {
                title: title,
                cancelButton: cancelButton,
                otherButtons: otherButtons,
                tooManyButtons: tooManyButtons
            }
        };
        _notification_post(body, onFail);
    };
    /**
     * 하단 팝업 메뉴
     * @method actionSheet
     * @memberOf o2m
     * @o2membercategory notification
     * @static
     * @param {Object} obj actionSheet에 전달할 객체
     * <pre><code class='language-js'>{
     *  "title": "누가 최고일까요?",//제목
     *  "cancelButton": '취소', //취소 버튼 텍스트
     *  "otherButtons": ["손오공","저팔계","삼장법사","사오정"], //기타 선택 버튼 이름
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/notification_action_sheet.jpeg">
     * </caption>
     * o2m.notification.actionSheet({
     *  title: "누가 최고일까요?", //제목
     *  cancelButton: '취소', //취소 버튼 텍스트
     *  otherButtons: ["손오공","저팔계","삼장법사","사오정"],
     *  onSuccess : function(buttonIndex) {
     *  //buttonIndex: 0 클릭된 버튼의 인덱스 값, Number, 0부터 시작, 취소 버튼은 -1
     * },
     *  onFail : function(err) {}
     * });
     *
     */
    this.o2m.notification.actionSheet = _o2m_n_actionSheet;

    //notification.showLoading
    this.o2m.notification.showLoadingSuccess = function () {
        console.log("notification showLoading back");
    };
    const _o2m_n_showLoading = function (c) {
        const text = c && c.text ? c.text : "";
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.notification.showLoadingSuccess = onSuccess;
        }
        const body = {
            type: "showLoading",
            callback: "o2m.notification.showLoadingSuccess",
            data: {
                text: text
            }
        };
        _notification_post(body, onFail);
    };
    /**
     * Loading 레이어 표시, hideLoading과 함께 사용
     * @method showLoading
     * @memberOf o2m
     * @o2membercategory notification
     * @static
     * @param {Object} obj showLoading에 전달할 객체
     * <pre><code class='language-js'>{
     *  text: "열심히 로딩 중..", //loading에 표시할 문자, 비어 있으면 문자 표시 안 함
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/notification_showLoading.jpeg">
     * </caption>
     * o2m.notification.showLoading({
     *  text: "열심히 로딩 중..", //loading에 표시할 문자, 비어 있으면 문자 표시 안 함
     *  onSuccess : function() {
     * },
     *  onFail : function(err) {}
     * });
     *
     */
    this.o2m.notification.showLoading = _o2m_n_showLoading;

    //notification.hideLoading
    this.o2m.notification.hideLoadingSuccess = function () {
        console.log("notification hideLoading back");
    };
    const _o2m_n_hideLoading = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.notification.hideLoadingSuccess = onSuccess;
        }
        const body = {
            type: "hideLoading",
            callback: "o2m.notification.hideLoadingSuccess",
            data: {}
        };
        _notification_post(body, onFail);
    }
    /**
     * Loading 레이어 숨기기
     * @method hideLoading
     * @memberOf o2m
     * @o2membercategory notification
     * @static
     * @param {Object} obj hideLoading에 전달할 객체
     * <pre><code class='language-js'>{
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.notification.hideLoading({
     *  onSuccess : function() {
     * },
     *  onFail : function(err) {}
     * });
     *
     */
    this.o2m.notification.hideLoading = _o2m_n_hideLoading;


    /** ***** BEGIN UTIL BLOCK *****
     * @ignore
     * @summary
     util 모듈
     date
     o2m.util.date.datePicker
     o2m.util.date.timePicker
     o2m.util.date.dateTimePicker
     calendar
     o2m.util.calendar.chooseOneDay
     o2m.util.calendar.chooseDateTime
     o2m.util.calendar.chooseInterval
     device
     o2m.util.device.getPhoneInfo
     o2m.util.device.scan
     o2m.util.deveice.location
     navigation
     o2m.util.navigation.setTitle
     o2m.util.navigation.close
     o2m.util.navigation.goBack
     o2m.util.navigation.openInnerApp
     o2m.util.navigation.openOtherApp
     o2m.util.navigation.openWindow
     o2m.util.navigation.share


     * ***** END UTIL BLOCK ******/

    this.o2m.util = {
        date: {},
        calendar: {},
        device: {},
        navigation: {}
    };

    const _util_post = function (body, onFail) {
        if (body == null) {
            if (onFail && typeof onFail === "function") {
                onFail("매개변수 오류!");
                return;
            }
        }
        const message = JSON.stringify(body);
        if (window.flutter_inappwebview && window.flutter_inappwebview.callHandler) {
            window.flutter_inappwebview.callHandler('o2mUtil', message);
        } else if (window.o2mUtil && window.o2mUtil.postMessage) {
            window.o2mUtil.postMessage(message);
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.o2mUtil) {
            window.webkit.messageHandlers.o2mUtil.postMessage(message);
        } else {
            if (onFail && typeof onFail === "function") {
                onFail("O2OA 모바일 앱에서 사용하세요!");
            }
        }
    };

    //o2m.util.date.datePicker
    this.o2m.util.date.datePickerSuccess = function (result) {
        console.log("util date datePicker back, result:" + result);
    };
    const _o2m_u_date_datePicker = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        const value = c && c.value ? c.value : "";
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.date.datePickerSuccess = onSuccess;
        }
        const body = {
            type: "date.datePicker",
            callback: "o2m.util.date.datePickerSuccess",
            data: {
                value: value
            }
        };
        _util_post(body, onFail);
    };

    /**
     * 날짜 선택기
     * @method datePicker
     * @memberOf o2m
     * @o2membercategory util.date
     * @static
     * @param {Object} obj  datePicker에 전달할 객체
     * <pre><code class='language-js'>{
     *  value: '2019-04-17', //기본 표시 날짜
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/util_date_picker.jpeg">
     * </caption>
     * o2m.util.date.datePicker({
     * value: '2019-04-17', //기본 표시 날짜
     * onSuccess : function(result) {
     *     //onSuccess는 완료 클릭 후 호출됨
     *     {
     *         value: "2019-02-10"
     *     }
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.date.datePicker = _o2m_u_date_datePicker;

    //o2m.util.date.timePicker
    this.o2m.util.date.timePickerSuccess = function (result) {
        console.log("util date timePicker back, result:" + result);
    };
    const _o2m_u_date_timePicker = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        const value = c && c.value ? c.value : "";
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.date.timePickerSuccess = onSuccess;
        }
        const body = {
            type: "date.timePicker",
            callback: "o2m.util.date.timePickerSuccess",
            data: {
                value: value
            }
        };
        _util_post(body, onFail);
    };
    /**
     * 시간 선택기
     * @method timePicker
     * @memberOf o2m
     * @o2membercategory util.date
     * @static
     * @param {Object} obj  timePicker에 전달할 객체
     * <pre><code class='language-js'>{
     *  value: '14:00', //기본 표시 시간
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/util_time_picker.jpeg">
     * </caption>
     * o2m.util.date.timePicker({
     * value: '14:00', //기본 표시 시간
     * onSuccess : function(result) {
     *     //onSuccess는 완료 클릭 후 호출됨
     *     {
     *         value: "18:10"
     *     }
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.date.timePicker = _o2m_u_date_timePicker;


    //o2m.util.date.dateTimePicker
    this.o2m.util.date.dateTimePickerSuccess = function (result) {
        console.log("util date dateTimePicker back, result:" + result);
    };
    const _o2m_u_date_dateTimePicker = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        const value = c && c.value ? c.value : "";
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.date.dateTimePickerSuccess = onSuccess;
        }
        const body = {
            type: "date.dateTimePicker",
            callback: "o2m.util.date.dateTimePickerSuccess",
            data: {
                value: value
            }
        };
        _util_post(body, onFail);
    };
    /**
     * 날짜·시간 선택기
     * @method dateTimePicker
     * @memberOf o2m
     * @o2membercategory util.date
     * @static
     * @param {Object} obj  dateTimePicker에 전달할 객체
     * <pre><code class='language-js'>{
     *  value: '2019-05-05 14:00', //기본 표시 시간
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/util_date_time_picker.jpeg">
     * </caption>
     * o2m.util.date.dateTimePicker({
     * value: '2019-05-05 14:00', //기본 표시 시간
     * onSuccess : function(result) {
     *     //onSuccess는 완료 클릭 후 호출됨
     *     {
     *         value: "2019-03-18 18:10"
     *     }
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.date.dateTimePicker = _o2m_u_date_dateTimePicker;


    //o2m.util.calendar.chooseOneDay
    this.o2m.util.calendar.chooseOneDaySuccess = function (result) {
        console.log("util calendar chooseOneDay back, result:" + result);
    };
    const _o2m_u_calendar_chooseOneDay = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        const value = c && c.value ? c.value : "";
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.calendar.chooseOneDaySuccess = onSuccess;
        }
        const body = {
            type: "calendar.chooseOneDay",
            callback: "o2m.util.calendar.chooseOneDaySuccess",
            data: {
                value: value
            }
        };
        _util_post(body, onFail);
    };
    /**
     * 월력 날짜 선택기
     * @method chooseOneDay
     * @memberOf o2m
     * @o2membercategory util.calendar
     * @static
     * @param {Object} obj  chooseOneDay에 전달할 객체
     * <pre><code class='language-js'>{
     *  value: '2019-05-05', //기본 표시 날짜
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/util_choose_one_day.jpeg">
     * </caption>
     * o2m.util.calendar.chooseOneDay({
     * value: '2019-05-05', //기본 표시 날짜
     * onSuccess : function(result) {
     *     //onSuccess는 완료 클릭 후 호출됨
     *     {
     *         value: "2019-03-18"
     *     }
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.calendar.chooseOneDay = _o2m_u_calendar_chooseOneDay;


    //o2m.util.calendar.chooseDateTime
    this.o2m.util.calendar.chooseDateTimeSuccess = function (result) {
        console.log("util calendar chooseDateTime back, result:" + result);
    };
    const _o2m_u_calendar_chooseDateTime = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        const value = c && c.value ? c.value : "";
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.calendar.chooseDateTimeSuccess = onSuccess;
        }
        const body = {
            type: "calendar.chooseDateTime",
            callback: "o2m.util.calendar.chooseDateTimeSuccess",
            data: {
                value: value
            }
        };
        _util_post(body, onFail);
    };
    /**
     * 월력 날짜·시간 선택기
     * @method chooseDateTime
     * @memberOf o2m
     * @o2membercategory util.calendar
     * @static
     * @param {Object} obj  chooseDateTime에 전달할 객체
     * <pre><code class='language-js'>{
     *  value: '2019-05-05 11:00', //기본 표시 시간
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/util_choose_date_time.jpeg">
     * </caption>
     * o2m.util.calendar.chooseDateTime({
     * value: '2019-05-05 11:00', //기본 표시 시간
     * onSuccess : function(result) {
     *     //onSuccess는 완료 클릭 후 호출됨
     *     {
     *         value: "2019-03-18 18:45"
     *     }
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.calendar.chooseDateTime = _o2m_u_calendar_chooseDateTime;


    //o2m.util.calendar.chooseInterval
    this.o2m.util.calendar.chooseIntervalSuccess = function (result) {
        console.log("util calendar chooseInterval back, result:" + result);
    };
    const _o2m_u_calendar_chooseInterval = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        const startDate = c && c.startDate ? c.startDate : "";
        const endDate = c && c.endDate ? c.endDate : "";
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.calendar.chooseIntervalSuccess = onSuccess;
        }
        const body = {
            type: "calendar.chooseInterval",
            callback: "o2m.util.calendar.chooseIntervalSuccess",
            data: {
                startDate: startDate,
                endDate: endDate
            }
        };
        _util_post(body, onFail);
    };
    /**
     * 월력 날짜 구간 선택기
     * @method chooseInterval
     * @memberOf o2m
     * @o2membercategory util.calendar
     * @static
     * @param {Object} obj  chooseInterval에 전달할 객체
     * <pre><code class='language-js'>{
     *  startDate: '2019-05-05',
     *  endDate: '2019-05-06',
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/util_choose_interval.jpeg">
     * </caption>
     * o2m.util.calendar.chooseInterval({
     * startDate: '2019-05-05',
     * endDate: '2019-05-06',
     * onSuccess : function(result) {
     *     //onSuccess는 완료 클릭 후 호출됨
     *     {
     *         startDate: "2019-05-05",
     endDate: "2019-05-06",
     *     }
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.calendar.chooseInterval = _o2m_u_calendar_chooseInterval;


    //o2m.util.device.getPhoneInfo
    this.o2m.util.device.getPhoneInfoSuccess = function (result) {
        console.log("util calendar chooseInterval back, result:" + result);
    };
    const _o2m_u_device_getPhoneInfo = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.device.getPhoneInfoSuccess = onSuccess;
        }
        const body = {
            type: "device.getPhoneInfo",
            callback: "o2m.util.device.getPhoneInfoSuccess",
            data: {}
        };
        _util_post(body, onFail);
    };
    /**
     * 휴대폰 기본 정보 조회
     * @method getPhoneInfo
     * @memberOf o2m
     * @o2membercategory util.device
     * @static
     * @param {Object} obj  getPhoneInfo에 전달할 객체
     * <pre><code class='language-js'>{
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.util.device.getPhoneInfo({
     * onSuccess : function(result) {
     *     {
     *      screenWidth: 1080, // 휴대폰 화면 너비
     *      screenHeight: 1920, // 휴대폰 화면 높이
     *      brand:'Mi', // 휴대폰 브랜드
     *      model:'Note4', // 휴대폰 모델
     *      version:'7.0'. // 버전
     *      netInfo:'wifi' , // 네트워크 유형 wifi/4g/3g
     *      operatorType :'xx' // 통신사 정보
     *     }
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.device.getPhoneInfo = _o2m_u_device_getPhoneInfo;


    //o2m.util.device.rotate
    this.o2m.util.device.rotateSuccess = function (result) {
        console.log("util device rotate back, result:" + result);
    };
    const _o2m_u_device_rotate = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.device.rotateSuccess = onSuccess;
        }
        const body = {
            type: "device.rotate",
            callback: "o2m.util.device.rotateSuccess",
            data: {}
        };
        _util_post(body, onFail);
    };
    /**
     * 화면 회전
     * @method rotate
     * @memberOf o2m
     * @o2membercategory util.device
     * @static
     * @param {Object} obj  rotate
     * <pre><code class='language-js'>{
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.util.device.rotate({
     * onSuccess : function(result) {
     * },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.device.rotate = _o2m_u_device_rotate;


    //o2m.util.device.scan
    this.o2m.util.device.scanSuccess = function (result) {
        console.log("util device scan back, result:" + result);
    };
    const _o2m_u_device_scan = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.device.scanSuccess = onSuccess;
        }
        const body = {
            type: "device.scan",
            callback: "o2m.util.device.scanSuccess",
            data: {}
        };
        _util_post(body, onFail);
    };
    /**
     * QR 코드 스캔
     * @method scan
     * @memberOf o2m
     * @o2membercategory util.device
     * @static
     * @param {Object} obj  scan에 전달할 객체
     * <pre><code class='language-js'>{
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.util.device.scan({
     * onSuccess : function(result) {
     *     // result 반환 결과가 곧 스캔 결과
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.device.scan = _o2m_u_device_scan;


    //o2m.util.device.location
    this.o2m.util.device.locationSuccess = function (result) {
        console.log("util device location back, result:" + result);
    };
    const _o2m_u_device_location = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.device.locationSuccess = onSuccess;
        }
        const body = {
            type: "device.location",
            callback: "o2m.util.device.locationSuccess",
            data: {}
        };
        _util_post(body, onFail);
    };
    /**
     * 1회 위치 조회
     * @method location
     * @memberOf o2m
     * @o2membercategory util.device
     * @static
     * @param {Object} obj  location에 전달할 객체
     * <pre><code class='language-js'>{
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.util.device.location({
     * onSuccess : function(result) {
     *     //여기서 바이두 좌표계의 위치 정보를 반환
     *     {
     *      'latitude': 39.903578, // 위도
     *      'longitude': 116.473565, // 경도
     *      'address': '주소 설명'
     *      }
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.device.location = _o2m_u_device_location;


    //o2m.util.device.localAuth
    this.o2m.util.device.localAuthSuccess = function (result) {
        console.log("util device localAuth back, result:" + result);
    };
    /**
     * 모바일 생체 인증
     * @method localAuth
     * @memberOf o2m
     * @o2membercategory util.device
     * @static
     * @param {Object} obj  localAuth 에 전달할 객체
     * <pre><code class='language-js'>{
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.util.device.localAuth({
     * onSuccess : function(result) {
     *     // result 는 json 문자열
     *     {
     *      "value":  false // 인증 성공 여부
     *      }
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.device.localAuth = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.device.localAuthSuccess = onSuccess;
        }
        const body = {
            type: "device.localAuth",
            callback: "o2m.util.device.localAuthSuccess",
            data: {}
        };
        _util_post(body, onFail);
    };


    //o2m.util.navigation.setTitle
    this.o2m.util.navigation.setTitleSuccess = function (result) {
        console.log("util calendar chooseInterval back, result:" + result);
    };
    const _o2m_u_navigation_setTitle = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        const title = c && c.title ? c.title : "";
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.navigation.setTitleSuccess = onSuccess;
        }
        const body = {
            type: "navigation.setTitle",
            callback: "o2m.util.navigation.setTitleSuccess",
            data: {
                title: title
            }
        };
        _util_post(body, onFail);
    };
    /**
     * 네이티브 페이지 제목 설정
     * @method setTitle
     * @memberOf o2m
     * @o2membercategory util.navigation
     * @static
     * @param {Object} obj  setTitle에 전달할 객체
     * <pre><code class='language-js'>{
     *  title : '내비게이션 제목',
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.util.navigation.setTitle({
     * title : '내비게이션 제목',
     * onSuccess : function() {
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.navigation.setTitle = _o2m_u_navigation_setTitle;


    //o2m.util.navigation.close
    this.o2m.util.navigation.closeSuccess = function (result) {
        console.log("util calendar chooseInterval back, result:" + result);
    };
    const _o2m_u_navigation_close = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.navigation.closeSuccess = onSuccess;
        }
        const body = {
            type: "navigation.close",
            callback: "o2m.util.navigation.closeSuccess",
            data: {}
        };
        _util_post(body, onFail);
    };
    /**
     * 현재 네이티브 페이지 닫기
     * @method close
     * @memberOf o2m
     * @o2membercategory util.navigation
     * @static
     * @example
     * o2m.util.navigation.close();
     */
    this.o2m.util.navigation.close = _o2m_u_navigation_close;


    //o2m.util.navigation.goBack
    this.o2m.util.navigation.goBackSuccess = function (result) {
        console.log("util calendar chooseInterval back, result:" + result);
    };
    const _o2m_u_navigation_goBack = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.navigation.goBackSuccess = onSuccess;
        }
        const body = {
            type: "navigation.goBack",
            callback: "o2m.util.navigation.goBackSuccess",
            data: {}
        };
        _util_post(body, onFail);
    };
    /**
     * 상위 네이티브 페이지로 돌아가기
     * @method goBack
     * @memberOf o2m
     * @o2membercategory util.navigation
     * @static
     * @example
     * o2m.util.navigation.goBack();
     */
    this.o2m.util.navigation.goBack = _o2m_u_navigation_goBack;


    //o2m.util.navigation.openInnerApp  내부 앱 열기(포털 포함)
    this.o2m.util.navigation.openInnerAppSuccess = function (result) {
        console.log("util navigation openInnerApp back, result:" + result);
    };
    const _o2m_u_navigation_openInnerApp = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.navigation.openInnerAppSuccess = onSuccess;
        }
        const appKey = c && c.appKey ? c.appKey : "";
        const portalFlag = c && c.portalFlag ? c.portalFlag : "";
        const portalTitle = c && c.portalTitle ? c.portalTitle : "";
        const portalPage = c && c.portalPage ? c.portalPage : "";
        const parameters = c && c.parameters ? c.parameters : {};
        const body = {
            type: "navigation.openInnerApp",
            callback: "o2m.util.navigation.openInnerAppSuccess",
            data: {
                appKey: appKey,
                portalFlag: portalFlag,
                portalTitle: portalTitle,
                portalPage: portalPage,
                parameters: parameters
            }
        };
        _util_post(body, onFail);
    };

    /**
     * 내부 앱 열기
     * @method openInnerApp
     * @memberOf o2m
     * @o2membercategory util.navigation
     * @static
     * @param {Object} obj  openInnerApp에 전달할 객체
     * <pre><code class='language-js'>{
     *  appKey : 'attendance',  // 앱의 key:  task(결재 대기), taskcompleted(처리 완료), read(열람 대기), readcompleted(열람 완료), meeting(회의 관리), clouddisk(클라우드 드라이브), bbs(포럼), cms(정보 센터), attendance(근태), calendar(일정), mindMap(마인드맵), portal(포털, 포털은 portalFlag와 portalTitle 전달 필요)
     *  portalFlag : '포털 식별자',
     *  portalTitle : '포털 제목',
     *  portalPage : '포털 페이지 id',
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.util.navigation.openInnerApp({
     * appKey : 'attendance',
     * onSuccess : function() {
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.navigation.openInnerApp = _o2m_u_navigation_openInnerApp;

    //o2m.util.navigation.openOtherApp 기능이 불완전하고 제한이 많아 당분간 공개하지 않음
    this.o2m.util.navigation.openOtherAppSuccess = function (result) {
        console.log("util navigation openOtherApp back, result:" + result);
    };
    const _o2m_u_navigation_openOtherApp = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.navigation.openOtherAppSuccess = onSuccess;
        }
        const schema = c && c.schema ? c.schema : "";
        const body = {
            type: "navigation.openOtherApp",
            callback: "o2m.util.navigation.openOtherAppSuccess",
            data: {
                schema: schema
            }
        };
        _util_post(body, onFail);
    };
    this.o2m.util.navigation.openOtherApp = _o2m_u_navigation_openOtherApp;


    //o2m.util.navigation.openWindow 새 창에서 웹 페이지 열기 포털
    this.o2m.util.navigation.openWindowSuccess = function (result) {
        console.log("util navigation openWindow back, result:" + result);
    };
    const _o2m_u_navigation_openWindow = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.navigation.openWindowSuccess = onSuccess;
        }
        const url = c && c.url ? c.url : "";
        const body = {
            type: "navigation.openWindow",
            callback: "o2m.util.navigation.openWindowSuccess",
            data: {
                url: url
            }
        };
        _util_post(body, onFail);
    };

    /**
     * 새 창에서 웹 페이지 열기
     * @method openWindow
     * @memberOf o2m
     * @o2membercategory util.navigation
     * @static
     * @param {Object} obj  openWindow에 전달할 객체
     * <pre><code class='language-js'>{
     *  url : 'https://www.o2oa.net',  // 열 주소
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * o2m.util.navigation.openWindow({
     * url : 'https://www.o2oa.net',
     * onSuccess : function() {
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.navigation.openWindow = _o2m_u_navigation_openWindow;


    //o2m.util.navigation.openInBrowser 휴대폰 기본 브라우저에서 현재 페이지 열기
    this.o2m.util.navigation.openInBrowserSuccess = function (result) {
        console.log("util navigation openInBrowser back, result:" + result);
    };
    const _o2m_u_navigation_openInBrowser = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.navigation.openInBrowserSuccess = onSuccess;
        }
        const body = {
            type: "navigation.openInBrowser",
            callback: "o2m.util.navigation.openInBrowserSuccess",
            data: {}
        };
        _util_post(body, onFail);
    };

    /**
     * 휴대폰 기본 브라우저에서 현재 페이지 열기
     * @method openInBrowser
     * @memberOf o2m
     * @o2membercategory util.navigation
     * @static
     * @example
     * o2m.util.navigation.openInBrowser({
     * onSuccess : function() {
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.navigation.openInBrowser = _o2m_u_navigation_openInBrowser;


    //o2m.util.navigation.clearCache 캐시 정리 후 페이지 새로 고침
    this.o2m.util.navigation.clearCacheSuccess = function (result) {
        console.log("util navigation clearCache back, result:" + result);
    };
    const _o2m_u_navigation_clearCache = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.navigation.clearCacheSuccess = onSuccess;
        }
        const body = {
            type: "navigation.clearCache",
            callback: "o2m.util.navigation.clearCacheSuccess",
            data: {}
        };
        _util_post(body, onFail);
    };

    /**
     * 캐시 정리 후 페이지 새로 고침
     * @method clearCache
     * @memberOf o2m
     * @o2membercategory util.navigation
     * @static
     * @example
     * o2m.util.navigation.clearCache({
     * onSuccess : function() {
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.navigation.clearCache = _o2m_u_navigation_clearCache;


    //o2m.util.navigation.share 이미지 공유
    this.o2m.util.navigation.shareSuccess = function (result) {
        console.log("util navigation share back, result:" + result);
    };
    const _o2m_u_navigation_share = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        const base64 = c && c.base64 ? c.base64 : "";
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.navigation.shareSuccess = onSuccess;
        }
        const body = {
            type: "navigation.share",
            callback: "o2m.util.navigation.shareSuccess",
            data: {
                base64: base64
            }
        };
        _util_post(body, onFail);
    };

    /**
     * 이미지 공유 기능, 현재 base64 공유만 지원
     * @method share
     * @memberOf o2m
     * @o2membercategory util.navigation
     * @static
     * @example
     * o2m.util.navigation.share({
     * base64: '이미지의 base64 인코딩';
     * onSuccess : function() {
     *  },
     *  onFail : function(err) {}
     *});
     */
    this.o2m.util.navigation.share = _o2m_u_navigation_share;

    //o2m.util.navigation.refresh 새로 고침
    this.o2m.util.navigation.refreshSuccess = function (result) {
        console.log("util navigation refresh back, result:" + result);
    };
    const _o2m_u_navigation_refresh = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        const base64 = c && c.base64 ? c.base64 : "";
        if (onSuccess && typeof onSuccess === "function") {
            o2m.util.navigation.refreshSuccess = onSuccess;
        }
        const body = {
            type: "navigation.refresh",
            callback: "o2m.util.navigation.refreshSuccess",
            data: {
                base64: base64
            }
        };
        _util_post(body, onFail);
    };


    this.o2m.util.navigation.refresh = _o2m_u_navigation_refresh;


    /** ***** BEGIN BIZ BLOCK *****
     * @ignore
     * @summary
     biz 모듈
     contact
     o2m.biz.contact.PersonPicker
     o2m.biz.contact.IdentityPicker
     o2m.biz.contact.departmentsPicker
     o2m.biz.contact.ComplexPicker
     o2m.biz.contact.GroupPicker
     o2m.biz.file.previewDoc

     * ***** END UTIL BLOCK ******/

    this.o2m.biz = {
        contact: {},
        file: {}
    };

    const _biz_post = function (body, onFail) {
        if (body == null) {
            if (onFail && typeof onFail === "function") {
                onFail("매개변수 오류!");
                return;
            }
        }
        const message = JSON.stringify(body);
        if (window.flutter_inappwebview && window.flutter_inappwebview.callHandler) {
            window.flutter_inappwebview.callHandler('o2mBiz', message);
        } else if (window.o2mBiz && window.o2mBiz.postMessage) {
            window.o2mBiz.postMessage(message)
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.o2mBiz) {
            window.webkit.messageHandlers.o2mBiz.postMessage(message);
        } else {
            if (onFail && typeof onFail === "function") {
                onFail("O2OA 모바일 앱에서 사용하세요!");
            }
        }
    };

    //o2m.biz.workClose()
    const _o2m_b_work_close = function () {
        if (window.o2android && window.o2android.closeWork) {
            window.o2android.closeWork("");
        } else if (window.o2android && window.o2android.postMessage) {
            const body = {
                type: "closeWork"
            };
            window.o2android.postMessage(JSON.stringify(body));
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.closeWork) {
            window.webkit.messageHandlers.closeWork.postMessage("");
        } else {
            console.log("O2OA 모바일 앱에서 사용하세요, workClose");
        }
    }
    /**
     * 현재 업무 문서 페이지 닫기, <b>업무 문서 양식에서만 사용 가능</b>
     * @method workClose
     * @memberOf o2m
     * @o2membercategory biz
     * @static
     * @example
     * o2m.biz.workClose();
     */
    this.o2m.biz.workClose = _o2m_b_work_close;

    // o2m.biz.createDocument
    const _o2m_b_createDocument = function (columnOrOptions, category, data, latest,  ignoreTitle) {
        let column = columnOrOptions;
        if (typeOf(columnOrOptions) == "object") {
            column = columnOrOptions.column;
            category = columnOrOptions.category;
            data = columnOrOptions.data;
            latest = columnOrOptions.latest;
            ignoreTitle = columnOrOptions.ignoreTitle;
        }
        const options = {};
        if (column) {
            options["column"] = column
        }
        if (category) {
            options["category"] = category
        }
        if (data) {
            options["data"] = data
        }

        if (latest) {
            options["latest"] = (latest === 'true' || latest === true)
        }
        if (ignoreTitle) {
            options["ignoreTitle"] = (ignoreTitle === 'true' || ignoreTitle === true)
        }
        const body = {
            type: "createO2CmsDocument",
            data: options
        }
        if (window.o2android && window.o2android.postMessage) {
            window.o2android.postMessage(JSON.stringify(body));
        } else if (window.o2android && window.o2android.createO2CmsDocument){
            window.o2android.createO2CmsDocument(JSON.stringify(options));
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.createO2CmsDocument) {
            window.webkit.messageHandlers.createO2CmsDocument.postMessage(options);
        }
    }
    /**
     * 콘텐츠 관리 문서를 생성합니다.
     * @method createDocument
     * @memberOf o2m
     * @o2membercategory biz
     * @static
     * @param {(String|Object)} [columnOrOptions]
     * 인자를 전달하지 않으면 플랫폼 전체 게시판 범위의 선택 화면을 띄웁니다.<br/>
     * String이면 콘텐츠 관리 앱(게시판)의 이름, 별칭 또는 ID입니다.<br/>
     * Object이면 이 인자 뒤의 인자는 생략하고 다음 형식의 내용을 전달합니다:
     * <pre><code class="language-js">{
     *   "column" : column, //(string) 선택, 콘텐츠 관리 앱(게시판)의 이름, 별칭 또는 ID
     *   "category" : category, //(string) 선택, 생성할 문서가 속한 분류의 이름, 별칭 또는 ID
     *   "data" : data, //(json object) 선택, 문서 생성 시 기본 업무 데이터
     *   "latest" : latest, //(boolean) 선택, true이면 현재 사용자가 이 분류의 문서를 이미 만들고 게시하지 않은 경우 그 문서를 새 문서로 사용. 아니면 새 문서 생성. 기본값 true.
     *   "ignoreTitle" : ignoreTitle //(boolean) 선택, false이면 생성 시 제목 입력 필수. 기본값 false.
     * }</code></pre>
     * @param {String} [category] - 생성할 문서가 속한 분류의 이름, 별칭 또는 ID
     * @param {Object} [data] - 문서 생성 시 기본 업무 데이터
     * @param {Boolean} [latest] - true이면 현재 사용자가 이 분류의 문서를 이미 만들고 게시하지 않은 경우 그 문서를 새 문서로 사용. 아니면 새 문서 생성. 기본값 true.
     * @param {Boolean} [ignoreTitle] - false이면 생성 시 제목 입력 필수. 기본값 false.
     * @example
     * //공지사항 생성
     * o2m.biz.createDocument("", "공지사항");
     * @example
     *          //공지사항 생성, 제목: XX 관련 공지, 생성 후 알림
     *          o2m.biz.createDocument({
     *             category : "공지사항",
     *             data : {"subject": "XX에 관한 공지"}
     *          });
     */
    this.o2m.biz.createDocument = _o2m_b_createDocument;

    //o2m.biz.contact.departmentsPicker
    this.o2m.biz.contact.departmentsPickerSuccess = function (result) {
        console.log("biz contact departmentsPicker back, result:" + result);
    };
    const _o2m_b_contact_department_picker = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.biz.contact.departmentsPickerSuccess = onSuccess;
        }
        const topList = c && c.topList ? c.topList : [];
        const orgType = c && c.orgType ? c.orgType : "";
        const multiple = c && c.multiple ? c.multiple : false;
        const maxNumber = c && c.maxNumber ? c.maxNumber : 0;
        const pickedDepartments = c && c.pickedDepartments ? c.pickedDepartments : [];
        const body = {
            type: "contact.departmentPicker",
            callback: "o2m.biz.contact.departmentsPickerSuccess",
            data: {
                topList: topList,
                orgType: orgType,
                multiple: multiple,
                maxNumber: maxNumber,
                pickedDepartments: pickedDepartments,
            }
        };
        _biz_post(body, onFail);
    };
    /**
     * 주소록 부서 선택
     * @method departmentsPicker
     * @memberOf o2m
     * @o2membercategory biz
     * @static
     * @param {Object} obj  departmentsPicker에 전달할 객체
     * <pre><code class='language-js'>{
     *  topList: [],//Array[String] 선택 가능한 최상위 조직 목록, 전달하지 않거나 빈 목록이면 전체 조직 표시
     *  orgType: "",//String 선택 가능한 조직 유형. 비어 있으면 모든 조직 유형 선택 가능
     *  multiple:true, //다중 선택 여부
     *  maxNumber: 0, //Int multiple이 true일 때 선택 가능한 최대 부서 수
     *  pickedDepartments:[],//Array[String] 이미 선택한 부서 distinguishedName 목록
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/biz_dept_choose.png">
     * </caption>
     * o2m.biz.contact.departmentsPicker({
     * topList: [],//전달하지 않거나 빈 목록이면 전체 조직 표시
     * orgType: "",//비워 둘 수 있음 특정 유형의 조직만 표시
     * multiple:true, //다중 선택 여부
     * maxNumber: 0, //최대 선택 수
     * pickedDepartments:[],//선택한 부서
     * onSuccess : function(result) {
     *     //반환 결과 예시
     *     {
     *       departments:[{
     *       "id":"xxxx",
     *       "name":"부서 이름",
     *       "unique":"xxxx",
     *       "distinguishedName":"부서@xxxx@U",
     *       "typeList":["xxxx"],
     *       "shortName":"xxxx",
     *       "level": 0,
     *       "levelName":"xxxx",
     *       }]
     *     }
     *
     * },
     * onFail : function(err) {}
     * });
     */
    this.o2m.biz.contact.departmentsPicker = _o2m_b_contact_department_picker;


    //o2m.biz.contact.IdentityPicker
    this.o2m.biz.contact.IdentityPickerSuccess = function (result) {
        console.log("biz contact IdentityPicker back, result:" + result);
    };
    const _o2m_b_contact_identity_picker = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.biz.contact.IdentityPickerSuccess = onSuccess;
        }
        const topList = c && c.topList ? c.topList : [];
        const multiple = c && c.multiple ? c.multiple : false;
        const maxNumber = c && c.maxNumber ? c.maxNumber : 0;
        const pickedIdentities = c && c.pickedIdentities ? c.pickedIdentities : [];
        const duty = c && c.duty ? c.duty : [];
        const body = {
            type: "contact.identityPicker",
            callback: "o2m.biz.contact.IdentityPickerSuccess",
            data: {
                topList: topList,
                multiple: multiple,
                maxNumber: maxNumber,
                pickedIdentities: pickedIdentities,
                duty: duty,
            }
        };
        _biz_post(body, onFail);
    };
    /**
     * 주소록 신분 선택
     * @method IdentityPicker
     * @memberOf o2m
     * @o2membercategory biz
     * @static
     * @param {Object} obj  IdentityPicker에 전달할 객체
     * <pre><code class='language-js'>{
     *  topList: [],//Array[String] 선택 가능한 최상위 조직 목록, 전달하지 않거나 빈 목록이면 전체 조직 표시
     *  multiple:true, //Boolean 다중 선택 여부
     *  maxNumber: 0, //Int multiple이 true일 때 선택 가능한 최대 신분 수
     *  pickedIdentities:[],//Array[String] 이미 선택한 신분 distinguishedName 목록
     *  duty: [],//Array[String] 선택 가능한 사용자 직무
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/biz_identity_choose.png">
     * </caption>
     * o2m.biz.contact.IdentityPicker({
     * topList: [],//전달하지 않거나 빈 목록이면 전체 조직 표시
     * multiple:true, //다중 선택 여부
     * maxNumber: 0, //최대 선택 수
     * pickedIdentities:[],//선택한 신분 목록
     * duty: [],//사용자 직무
     * onSuccess : function(result) {
     *     //반환 결과 예시
     *     {
     *       identities:[{
     *         "id":"xxxx",
     *         "name":"이름",
     *         "distinguishedName":"이름@xxxx@I",
     *         "person":"xxx",
     *         "unique":"xxxx",
     *         "unit":"xxx",
     *         "unitName":"xxxx",
     *         "unitLevel": 0,
     *         "unitLevelName":"xxxx",
     *         "personName":"xxxx",
     *         "personUnique":"xxx",
     *         "personDn":"xxx"
     *         }]
     *     }
     *
     * },
     * onFail : function(err) {}
     * });
     */
    this.o2m.biz.contact.IdentityPicker = _o2m_b_contact_identity_picker;


    //o2m.biz.contact.GroupPicker
    this.o2m.biz.contact.GroupPickerSuccess = function (result) {
        console.log("biz contact GroupPicker back, result:" + result);
    };
    const _o2m_b_contact_group_picker = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.biz.contact.GroupPickerSuccess = onSuccess;
        }
        const multiple = c && c.multiple ? c.multiple : false;
        const maxNumber = c && c.maxNumber ? c.maxNumber : 0;
        const pickedGroups = c && c.pickedGroups ? c.pickedGroups : [];
        const body = {
            type: "contact.groupPicker",
            callback: "o2m.biz.contact.GroupPickerSuccess",
            data: {
                multiple: multiple,
                maxNumber: maxNumber,
                pickedGroups: pickedGroups,
            }
        };
        _biz_post(body, onFail);
    };
    /**
     * 그룹 선택
     * @method GroupPicker
     * @memberOf o2m
     * @o2membercategory biz
     * @static
     * @param {Object} obj  GroupPicker에 전달할 객체
     * <pre><code class='language-js'>{
     *  multiple:true, //Boolean 다중 선택 여부
     *  maxNumber: 0, //Int multiple이 true일 때 선택 가능한 최대 신분 수
     *  pickedGroups:[],//Array[String] 이미 선택한 그룹 distinguishedName 목록
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/biz_group_choose.png">
     * </caption>
     * o2m.biz.contact.GroupPicker({
     * multiple:true, //다중 선택 여부
     * maxNumber: 0, //최대 선택 수
     * pickedGroups:[],//선택한 그룹 목록
     * onSuccess : function(result) {
     *     //반환 결과 예시
     *     {
     *        groups:[{
     *           "id":"xxxx",
     *           "name":"그룹 이름",
     *           "distinguishedName":"그룹 이름@xxxx@G"
     *           "unique":"xxxx",
     *           }]
     *     }
     *
     * },
     * onFail : function(err) {}
     * });
     */
    this.o2m.biz.contact.GroupPicker = _o2m_b_contact_group_picker;


    //o2m.biz.contact.PersonPicker
    this.o2m.biz.contact.PersonPickerSuccess = function (result) {
        console.log("biz contact PersonPicker back, result:" + result);
    };
    const _o2m_b_contact_person_picker = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.biz.contact.PersonPickerSuccess = onSuccess;
        }
        const multiple = c && c.multiple ? c.multiple : false;
        const maxNumber = c && c.maxNumber ? c.maxNumber : 0;
        const pickedUsers = c && c.pickedUsers ? c.pickedUsers : [];
        const body = {
            type: "contact.personPicker",
            callback: "o2m.biz.contact.PersonPickerSuccess",
            data: {
                multiple: multiple,
                maxNumber: maxNumber,
                pickedUsers: pickedUsers,
            }
        };
        _biz_post(body, onFail);
    };
    /**
     * 사용자 선택
     * @method PersonPicker
     * @memberOf o2m
     * @o2membercategory biz
     * @static
     * @param {Object} obj  PersonPicker에 전달할 객체
     * <pre><code class='language-js'>{
     *  multiple:true, //Boolean 다중 선택 여부
     *  maxNumber: 0, //Int multiple이 true일 때 선택 가능한 최대 신분 수
     *  pickedUsers:[],//Array[String] 이미 선택한 사용자 distinguishedName 목록
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/biz_person_choose.png">
     * </caption>
     * o2m.biz.contact.PersonPicker({
     * multiple:true, //다중 선택 여부
     * maxNumber: 0, //최대 선택 수
     * pickedUsers:[],//선택한 사용자 목록
     * onSuccess : function(result) {
     *     //반환 결과 예시
     *     {
     *         users:[{
     *           "id":"xxx",
     *           "name":"이름",
     *           "unique":"xxx",
     *           "distinguishedName":"이름@xxxx@P"
     *           "genderType":"xxx",
     *           "employee":"xxx",
     *           "mail":"xxx",
     *           "weixin":"xxx",
     *           "qq":"xxx",
     *           "mobile":"xxx",
     *           "officePhone":"xxx"
     *           }]
     *     }
     *
     * },
     * onFail : function(err) {}
     * });
     */
    this.o2m.biz.contact.PersonPicker = _o2m_b_contact_person_picker;


    //o2m.biz.contact.ComplexPicker
    this.o2m.biz.contact.ComplexPickerSuccess = function (result) {
        console.log("biz contact ComplexPicker back, result:" + result);
    };
    const _o2m_b_contact_complex_picker = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.biz.contact.ComplexPickerSuccess = onSuccess;
        }
        const pickMode = c && c.pickMode ? c.pickMode : [];
        const multiple = c && c.multiple ? c.multiple : false;
        const maxNumber = c && c.maxNumber ? c.maxNumber : 0;
        const topList = c && c.topList ? c.topList : [];
        const orgType = c && c.orgType ? c.orgType : "";
        const duty = c && c.duty ? c.duty : [];
        const pickedGroups = c && c.pickedGroups ? c.pickedGroups : [];
        const pickedUsers = c && c.pickedUsers ? c.pickedUsers : [];
        const pickedIdentities = c && c.pickedIdentities ? c.pickedIdentities : [];
        const pickedDepartments = c && c.pickedDepartments ? c.pickedDepartments : [];
        const body = {
            type: "contact.complexPicker",
            callback: "o2m.biz.contact.ComplexPickerSuccess",
            data: {
                pickMode: pickMode,
                multiple: multiple,
                maxNumber: maxNumber,
                topList: topList,
                orgType: orgType,
                duty: duty,
                pickedGroups: pickedGroups,
                pickedUsers: pickedUsers,
                pickedIdentities: pickedIdentities,
                pickedDepartments: pickedDepartments,
            }
        };
        _biz_post(body, onFail);
    };
    /**
     * 복합 선택기, 여러 데이터를 선택하도록 설정 가능
     * @method ComplexPicker
     * @memberOf o2m
     * @o2membercategory biz
     * @static
     * @param {Object} obj  ComplexPicker에 전달할 객체
     * <pre><code class='language-js'>{
     *  pickMode: ["departmentPicker", "identityPicker"], //Array[String] 선택기 유형, 가능한 값: departmentPicker, identityPicker, groupPicker, personPicker
     *  topList: Array[String] 선택 가능한 최상위 조직 목록, 전달하지 않거나 빈 목록이면 전체 조직 표시
     *  duty: Array[String] 선택 가능한 사용자 직무
     *  orgType: String 선택 가능한 조직 유형. 비어 있으면 모든 조직 유형 선택 가능
     *  multiple:true, //Boolean 다중 선택 여부
     *  maxNumber: 0, //Int multiple이 true일 때 선택 가능한 최대 신분 수
     *  pickedDepartments: Array[String] 이미 선택한 부서 distinguishedName 목록
     *  pickedIdentities: Array[String] 이미 선택한 신분 distinguishedName 목록
     *  pickedGroups:  Array[String] 이미 선택한 그룹 distinguishedName 목록
     *  pickedUsers:[],//Array[String] 이미 선택한 사용자 distinguishedName 목록
     *  "onSuccess": function,  //성공 콜백
     *  "onFail": function, //실패 콜백
     * }</code></pre>
     * @example
     * <caption>
     * 예시 효과:<br/>
     * <img src="img/module/o2m/biz_complex_choose.png">
     * </caption>
     * o2m.biz.contact.ComplexPicker({
     * pickMode: ["departmentPicker", "identityPicker"], //선택기 유형
     * topList: [],//전달하지 않거나 빈 목록이면 전체 조직 표시
     * orgType: "",//비워 둘 수 있음 특정 유형의 조직만 표시
     * duty: [],//사용자 직무
     * multiple:true, //다중 선택 여부
     * maxNumber: 0, //최대 선택 수
     * pickedDepartments:[],//선택한 부서
     * pickedIdentities:[],//선택한 신분 목록
     * pickedGroups:[],//선택한 그룹 목록
     * pickedUsers:[],//선택한 사용자 목록
     * onSuccess : function(result) {
     *     //반환 결과 예시
     *     {
     *       departments:[{
     *       "id":"xxxx",
     *       "name":"부서 이름",
     *       "unique":"xxxx",
     *       "distinguishedName":"부서@xxxx@U",
     *       "typeList":["xxxx"],
     *       "shortName":"xxxx",
     *       "level": 0,
     *       "levelName":"xxxx",
     *       }],
     *       identities:[{
     *       "id":"xxxx",
     *       "name":"이름",
     *       "distinguishedName":"이름@xxxx@I",
     *       "person":"xxx",
     *       "unique":"xxxx",
     *       "unit":"xxx",
     *       "unitName":"xxxx",
     *       "unitLevel": 0,
     *       "unitLevelName":"xxxx",
     *       "personName":"xxxx",
     *       "personUnique":"xxx",
     *       "personDn":"xxx"
     *       }],
     *       groups:[{
     *       "id":"xxxx",
     *       "name":"그룹 이름",
     *       "distinguishedName":"그룹 이름@xxxx@G"
     *       "unique":"xxxx",
     *       }],
     *       users:[{
     *       "id":"xxx",
     *       "name":"이름",
     *       "unique":"xxx",
     *       "distinguishedName":"이름@xxxx@P"
     *       "genderType":"xxx",
     *       "employee":"xxx",
     *       "mail":"xxx",
     *       "weixin":"xxx",
     *       "qq":"xxx",
     *       "mobile":"xxx",
     *       "officePhone":"xxx"
     *       }]
     *     }
     * },
     * onFail : function(err) {}
     * });
     */
    this.o2m.biz.contact.ComplexPicker = _o2m_b_contact_complex_picker;


    this.o2m.biz.file.PreviewDocSuccess = function (result) {
        console.log("biz file preview doc back, result:" + result);
    };
    const _o2m_b_file_preview = function (c) {
        const onSuccess = c && c.onSuccess ? c.onSuccess : null;
        const onFail = c && c.onFail ? c.onFail : null;
        if (onSuccess && typeof onSuccess === "function") {
            o2m.biz.file.PreviewDocSuccess = onSuccess;
        }
        const fileUrl = c && c.url ? c.url : "";
        const fileName = c && c.fileName ? c.fileName : "";

        const body = {
            type: "file.previewDoc",
            callback: "o2m.biz.file.PreviewDocSuccess ",
            data: {
                url: fileUrl,
                fileName: fileName
            }
        };
        _biz_post(body, onFail);
    };
    // 파일 미리보기 추후 삭제 예정
    this.o2m.biz.file.PreviewDoc = _o2m_b_file_preview;

})();
