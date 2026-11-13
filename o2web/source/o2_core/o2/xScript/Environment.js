MWF.xScript = MWF.xScript || {};
MWF.xScript.Environment = function(ev){
    var _data = ev.data;
    var _form = ev.form;
    var _forms = ev.forms;

    this.appType = "process";

    this.library = COMMON;
    //this.library.version = "4.0";

    this.power = {
        "isManager": MWF.AC.isProcessManager() || _form.businessData.control.allowReroute,
        "isReseter": _form.businessData.control.allowReset,
        "isDelete": _form.businessData.control.allowDeleteWork,
        "isPront": true,
        "isPrint": true
    };
    //data
    var getJSONData = function(jData){
        return new MWF.xScript.JSONData(jData, function(data, key, _self){
            var p = {"getKey": function(){return key;}, "getParent": function(){return _self;}};
            while (p && !_forms[p.getKey()]) p = p.getParent();
            //if (p) if (p.getKey()) if (_forms[p.getKey()]) _forms[p.getKey()].resetData();
            var k = (p) ? p.getKey() : "";
            if (k) if(_forms[k]){
                if(_forms[k].resetData){
                    _forms[k].resetData();
                    if (_forms[k].form){
                        if (_forms[k].form.relatedModules?.[_forms[k].json.id]){
                            _forms[k].form.relatedModules?.[_forms[k].json.id].forEach((module)=>{
                                module?.reload();
                            })
                        }
                        if (_forms[k].form.relatedDisplayModules && _forms[k].form.relatedDisplayModules[(_forms[k].json.id)]){
                            _forms[k].form.relatedDisplayModules[(_forms[k].json.id)].forEach((o)=>{
                                o.module?._checkDisplay(o.display);
                            })
                        }
                        if (_forms[k].form.relatedValueModules && _forms[k].form.relatedValueModules[(_forms[k].json.id)]){
                            _forms[k].form.relatedValueModules[(_forms[k].json.id)].forEach((o)=>{
                                o.module?._checkValue(o.value);
                            })
                        }
                    }
                }
            } 
            //if(p) if(p.getKey()) if(_forms[p.getKey()]) if(_forms[p.getKey()].render) _forms[p.getKey()].render();
        }, "", null, _form);
    };


    this.setData = function(data){

        /**
         * data 객체는 프로세스 플랫폼에서는 프로세스 인스턴스의 업무 데이터이고, 콘텐츠 관리 플랫폼에서는 문서 인스턴스의 업무 데이터입니다.<br/>
         * 이 데이터는 일반적으로 사용자가 만든 양식으로 수집되며, 스크립트로 생성하고 추가·삭제·수정·조회할 수도 있습니다.<br/>
         * data 객체는 기본적으로 Object 객체이므로 Object 접근 방식으로 모든 데이터에 접근할 수 있지만, 데이터 추가·삭제 방식은 조금 다릅니다.
         * @module data
         * @o2cn 업무 데이터
         * @o2category web
         * @o2ordernumber 10
         * @example
         * //폼 또는 프로세스의 각 내장 스크립트에서 this를 통해 현재 인스턴스의 업무 데이터를 가져올 수 있습니다. 예:
         * var data = this.data;
         */
        this.data = getJSONData(data);

        /**
         * data 객체의 데이터 접근 또는 수정.<br/><br/>
         * data 데이터는 양식에서 가져온 데이터를 저장하며, 모든 속성은 동적이고 형식과 접근 방식은 JSON과 유사합니다.<br/>
         * 양식 스크립트에서 data 객체를 사용하면 data와 양식 편집 가능 요소가 양방향 바인딩됩니다.<br/>
         * data 객체를 변경하면 양식 요소가 자동으로 갱신되고, 양식 편집 가능 요소를 수정하면 data 객체도 자동으로 수정됩니다.<br/>
         * <b>데이터 할당(this.data.subject = '')은 양식에 해당 필드 컴포넌트가 있을 때만 적용됩니다. 모든 경우에 적용되는 this.data.add("subject","",true) 메서드 사용을 권장합니다.</b>
         * @member {String|Number} [[property]]
         * @memberOf module:data
         * @instance
         * @example
         * var value = this.data.subject;  //이름이 subject인 데이터 값 조회
         *
         * //subject 값을 '123'으로 수정.
         * //주의: 이 방식으로 새 필드를 만들려면 현재 양식에 이름이 ‘subject’인 필드 컴포넌트가 있어야 합니다.
         * //양식에 해당 컴포넌트가 없으면 this.data.add('subject','123',true)를 사용할 수 있습니다. 기존 필드에 값을 할당할 때는 이 제한이 없습니다.
         * this.data.subject = '123';
         *
         * @example
         * <caption>
         * <b>프로세스 문서의 데이터 그리드 값 조회</b>:<br/>
         * 다음과 같은 데이터 그리드가 있을 때:
         *     <img src="img/module/data/datagrid.jpg">
         * 데이터 그리드 설계는 다음과 같습니다(데이터 그리드 id: datagrid):
         *     <img src="img/module/data/datagridDesign.jpg">
         * </caption>
         * //프로세스 문서의 데이터 그리드 값 조회
         * var data = this.data.datagrid;
         *
         * //가져온 data 값의 형식은 다음과 같습니다:
         * {
         *   "data": [
         *       {
         *           "amountCol": { "amount": "12000" },
         *          "countCol": { "number": "10" },
         *          "nameCol": { "name": "휴대폰" },
         *          "priceCol": { "price": "1200" }
         *      },
         *      {
         *          "amountCol": { "amount": "15000" },
         *          "countCol": { "number": "5" },
         *          "nameCol": { "name": "컴퓨터" },
         *          "priceCol": { "price": "3000" }
         *      }
         *  ],
         *  "total": {
         *      "amountCol": "27000",
         *      "countCol": "15"
         *  }
         * }
         *
         *
         * //데이터 그리드의 기타 데이터 조회:
         *
         * //데이터 그리드의 첫 번째 데이터 조회
         * var data = this.data.datagrid.data[0];
         *
         * //데이터 그리드 첫 번째 데이터의 nameCol 열 값 조회
         * var data = this.data.datagrid.data[0].nameCol.name;
         *
         * //데이터 그리드의 amountCol 열 합계 값 조회
         * var data = this.data.datagrid.total.amountCol;
         *
         *@example
         * <caption>
         * <b>데이터 그리드의 데이터 수정</b></br>
         * 이 예제로 수정하면 데이터 그리드는 다음과 같이 바뀝니다:</br>
         *     <img src="img/module/data/datagrid2.jpg">
         * </caption>
         * //데이터 그리드 첫 번째 데이터의 nameCol 열 값 수정
         * this.data.datagrid.data[0].nameCol.name='태블릿';
         */

        /**
         * data 객체에 데이터 노드 하나 추가.
         * @instance
         * @method add
         * @memberOf module:data
         * @param {(String|Number)} key - 추가할 새 데이터 노드 이름 또는 배열 인덱스.
         * @param {(String|Number|Array|JsonObject)} value - 새 데이터 노드의 값.
         * @param {Boolean} [overwrite] - 추가할 노드가 이미 있으면 덮어쓸지 여부. 기본값 false.
         * @return {(String|Number|Array|JsonObject)} 새로 추가된 데이터 노드 또는 기존 동명 노드.
         * @o2syntax
         * var newData = this.data.add(key, value, overwrite);
         * @example
         * //data에 이름이 "remark", 값이 "I am remark"인 데이터 추가
         * this.data.add("remark", "I am remark");
         * @example
         * //data에 이름이 "person"인 Object 객체 데이터 추가
         * var person = this.data.add("person", {});
         * person.add("name", "Tom");
         * person.add("age", 23);
         *
         * //또는 이렇게 할 수 있음
         * var person = this.data.add("person", {name: "Tom", "age": "23"});
         * @example
         * //data에 이름이 "orders"인 배열 객체 데이터 추가
         * var orders = this.data.add("orders", []);
         * orders.add({name: "phone", count: 5});
         * orders.add({name: "computer", count: 10});
         * orders[0].add("count", 10, true);  //첫 번째 데이터의 count를 10으로 수정
         *
         * //또는 이렇게 할 수 있음
         * var orders = this.data.add("orders", [
         *  {name: "phone", count: 5},
         *  {name: "computer", count: 10}
         * ]);
         * //첫 번째 데이터를 name은 mobile, count는 10으로 수정
         * orders.add(0, {name: "mobile", count: 10}, true);
         */

        /**data 객체 저장. 이벤트를 발생시키지 않음.
         * queryLoad, beforeSave, afterSave에서 이 메서드 사용은 권장하지 않음.
         * @method save
         * @static
         * @memberOf module:data
         * @param {Function} [callback] - 저장 성공 후 콜백 함수.
         * @o2syntax
         * this.data.save(callback);
         * @example
         * this.data.save(function(json){
         *   this.form.notice("save success!", "success")
         *});
         */
        this.data.save = function(callback){
            _form.saveFormData(callback)
            // var formData = {
            //     "data": data,
            //     "sectionList": _form.getSectionList()
            // };
            // _form.workAction.saveSectionData(function(){if (callback) callback();}.bind(this), null, (ev.work.id || ev.workCompleted.id), formData);
        }
    };
    this.setData(_data);
    //task
    //this.task = ev.task;
    //this.task.process = function(routeName, opinion, callback){
    //    _form.submitWork(routeName, opinion, callback);
    //};
    //inquiredRouteList
    //this.inquiredRouteList = null;

    //workContext

    var _getWorkContextList = function(method, id, callback, error){
        var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
        var ecb = (error && o2.typeOf(error)==="function") ? error : null;
        var list;
        var p = o2.Actions.get("x_processplatform_assemble_surface")[method](id, function(json){
            list = json.data;
            if (cb) cb(list);
            return list;
        }, ecb, !!callback);
        return (!!callback) ? p : list;
    };
    /**
     * workContext로 프로세스 관련 프로세스 인스턴스 객체 데이터를 조회할 수 있습니다.
     * @module workContext
     * @o2cn 프로세스 인스턴스
     * @o2category web
     * @o2range {Process}
     * @o2ordernumber 20
     * @o2syntax
     * //양식이나 프로세스의 각 내장 스크립트에서 this로 현재 프로세스 인스턴스 데이터를 조회할 수 있습니다. 예:
     * var context = this.workContext;
     */
    this.workContext = {
        //  * <div><br/>
        //  * 아래 Work 객체와 WorkCompleted 객체는 백엔드가 반환한 데이터이며, 프런트엔드 스크립트에서 두 객체를 다음과 같이 수정·보완했습니다:
        // * </div>
        // * <pre><code class='language-js'>{
        //     *      "creatorPersonDn": "홍길동@zhangsan@P",		//작성자, 시스템이 생성한 경우 비어 있을 수 있음.
        //     *      "creatorPerson": "홍길동",  //작성자 이름
        //     *      "creatorIdentityDn": "홍길동@481c9edc-5fb5-41f1-b5c2-6ea609082cdb@I",		//작성자 Identity, 시스템이 생성한 경우 비어 있을 수 있음.
        //     *      "creatorIdentity": "홍길동" //작성자 이름
        //     *      "creatorUnitDn": "개발부@c448d8bb-98b8-4305-9d3f-12537723cfcc@U", //작성자 조직 전체 이름, 시스템이 생성한 경우.
        //     *      "creatorUnit": "개발부",  //작성자 조직 이름
        //     *      "creatorDepartment": "개발부",  //작성자 조직 이름, creatorUnit과 같음
        //     *      "creatorCompany": "xx회사"  //작성자 최상위 조직 이름, creatorUnitLevelName의 첫 구간
        //     * }</code></pre>

        /**
         * 현재 프로세스 인스턴스 객체 조회: work 객체 또는 workCompleted 객체.
         * @method getWork
         * @static
         * @return {(Work|WorkCompleted)} 프로세스 인스턴스 객체; 프로세스가 종료된 경우 종료된 프로세스 인스턴스 객체 반환.
         * @o2ActionOut x_processplatform_assemble_surface.WorkAction.manageGet|example=WorkParsed|extension=Work|ignoreNoDescr=true|ignoreProps=[properties,manualTaskIdentityMatrix]|Work 객체:
         * @o2ActionOut x_processplatform_assemble_surface.WorkCompletedAction.get|example=WorkCompletedParsed|extension=Work|ignoreProps=[properties,data,taskCompletedList,readCompletedList,reviewList,recordList,workLogList,storeForm,mobileStoreForm]|WorkCompleted 객체:
         * @o2syntax
         * var work = this.workContext.getWork();
         */
        "getWork": function(){return ev.work || ev.workCompleted;},

        /**
         * 현재 프로세스 인스턴스가 있는 활동 노드 객체 조회: activity 객체.
         * @method getActivity
         * @static
         * @return {(Activity|Null)} 현재 프로세스 인스턴스가 있는 활동 노드 객체, 프로세스 인스턴스가 이미 완료된 경우 null 반환.
         * <pre><code class='language-js'>{
         *      "id": "801087c5-a4e6-4b91-bf4d-a81cdaa04471", //노드 ID
         *      "name": "처리",  //노드 이름
         *      "description": "", //노드 설명
         *      "alias": "",  //노드 별칭
         *      "resetRange": "department", //처리자 재지정 범위
         *      "resetCount": 0,  //처리자 재지정 수
         *      "allowReset": true, //재지정 허용 여부
         *      "manualMode": "single", //처리 방식: 단일 single, 병렬 parallel, 순차 queue, 선점 grab
         *      "customData": { //노드의 사용자 정의 속성, 설정하지 않으면 출력하지 않음
         *
         *      }
         * }</code></pre>
         * @o2syntax
         * var activity = this.workContext.getActivity();
         */
        "getActivity": function(){return ev.activity || null;},

        // * <div><br/>
        //     * 아래 Task 객체는 백엔드에서 반환한 데이터이며, 스크립트에서 다음과 같이 수정·보완했습니다:
        // * </div>
        // * <pre><code class='language-js'>{
        // *      "personDn": "홍길동@zhangsan@P",		//생성자, 시스템이 생성한 경우 비어 있을 수 있음.
        // *      "person": "홍길동",  //생성자 이름
        // *      "identityDn": "홍길동@481c9edc-5fb5-41f1-b5c2-6ea609082cdb@I",		//생성자 Identity, 시스템이 생성한 경우 비어 있을 수 있음.
        // *      "identity": "홍길동" //생성자 이름
        // *      "unitDn": "개발부@c448d8bb-98b8-4305-9d3f-12537723cfcc@U", //생성자 조직 전체 이름, 시스템이 생성한 경우.
        // *      "unit": "개발부",  //생성자 조직 이름
        // *      "department": "개발부",  //생성자 조직 이름, unit
        // *      "customData": {},  //프로세스 활동에 설정된 사용자 정의 속성
        // * }</code></pre>

        /**
         * 현재 프로세스 인스턴스가 진행 중이고 현재 사용자에게 결재 대기가 있으면 현재 사용자의 결재 대기 객체를, 그렇지 않으면 null을 반환합니다.
         * @summary 현재 프로세스에서 현재 사용자와 관련된 결재 대기 객체 조회: task 객체.
         * @o2ActionOut x_processplatform_assemble_surface.TaskAction.get|example=TaskParsed|extension=Task|Task 객체:
         * @method getTask
         * @static
         * @return {(Task|Null)} 현재 사용자의 결재 대기 작업 객체: task. 현재 사용자에게 이 프로세스 인스턴스의 결재 대기가 없거나 프로세스 인스턴스가 이미 종료된 경우 null 반환.
         * @o2syntax
         * var task = this.workContext.getTask();
         */
        "getTask": function(){return ev.task || null;},

        /**
         * 현재 프로세스 인스턴스의 모든 결재 대기 객체 조회. 프로세스 인스턴스가 이미 완료된 경우 빈 배열 반환.
         * @method getTaskList
         * @o2ActionOut x_processplatform_assemble_surface.TaskAction.listWithWork|example=Task
         * @static
         * @param {Function|Boolean} [callback] 결재 대기 배열을 정상 조회했을 때의 콜백 또는 불리언 값. true로 판단되면 이 메서드는 비동기로 실행되어 Promise를 반환하고, 그렇지 않으면 동기로 실행됨
         * @param {Function} [error] 결재 대기 배열 조회 오류 시 콜백.
         * @return {(Task[]|Promise)} 결재 대기 작업 목록, 또는 결재 대기 객체 목록으로 resolve되는 Promise 객체.
         * @o2syntax
         * //이 예시는 동기로 실행
         * var taskList = this.workContext.getTaskList();
         * @o2syntax
         * //이 예시는 비동기로 실행
         * this.workContext.getTaskList( function(taskList){
         *     //taskList는 결재 대기 배열
         * });
         * @o2syntax
         * //이 예시는 Promise 사용
         * this.workContext.getTaskList(true).then(function(taskList){
         *     //taskList는 결재 대기 배열
         * });
         */
        "getTaskList": function(callback, error){
            if( ev.work.completedTime )return [];
            return _getWorkContextList("listTaskByWork", ev.work.id, callback, error);
            // var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            // var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            // var list;
            // var p = o2.Actions.get("x_processplatform_assemble_surface").listTaskByWork(ev.work.id, function(json){
            //     list = json.data;
            //     if (cb) cb(list);
            //     return list;
            // }, ecb, !!callback);
            // return (!!callback) ? p : list;
        },
        /**
         * 현재 업무 문서의 job으로 현재 프로세스 인스턴스의 모든 결재 대기 객체 조회. 프로세스 인스턴스가 이미 완료된 경우 빈 배열 반환.
         * @method getTaskListByJob
         * @o2ActionOut x_processplatform_assemble_surface.TaskAction.listWithJob|example=Task
         * @static
         * @param {Function|Boolean} [callback] 결재 대기 배열을 정상 조회했을 때의 콜백 또는 불리언 값. true로 판단되면 이 메서드는 비동기로 실행되어 Promise를 반환하고, 그렇지 않으면 동기로 실행됨
         * @param {Function} [error] 결재 대기 배열 조회 오류 시 콜백.
         * @return {(Task[]|Promise)} 결재 대기 작업 목록, 또는 결재 대기 객체 목록으로 resolve되는 Promise 객체.
         * @o2syntax
         * //이 예시는 동기로 실행
         * var taskList = this.workContext.getTaskListByJob();
         * @o2syntax
         * //이 예시는 비동기로 실행
         * this.workContext.getTaskListByJob( function(taskList){
         *     //taskList는 결재 대기 배열
         * });
         * @o2syntax
         * //이 예시는 Promise 사용
         * this.workContext.getTaskListByJob(true).then(function(taskList){
         *     //taskList는 결재 대기 배열
         * });
         */
        "getTaskListByJob": function(callback, error){
            return _getWorkContextList("listTaskByJob", ev.work.job, callback, error);
            // var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            // var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            // var list;
            // var p = o2.Actions.get("x_processplatform_assemble_surface").listTaskByJob(ev.work.job, function(json){
            //     list = json.data;
            //     if (cb) cb(list);
            //     return list;
            // }, ecb, !!callback);
            // return (!!callback) ? p : list;
        },

        /**
         * 현재 프로세스 인스턴스의 모든 처리 완료 객체 조회. 프로세스 인스턴스를 처리한 사람이 없으면 빈 배열 반환.
         * @method getTaskCompletedList
         * @static
         * @param {Function|Boolean} [callback] 처리 완료 배열을 정상 조회했을 때의 콜백 또는 불리언 값. true로 판단되면 이 메서드는 비동기로 실행되어 Promise를 반환하고, 그렇지 않으면 동기로 실행됨
         * @param {Function} [error] 처리 완료 배열 조회 실패 시 콜백.
         * @return {(TaskCompleted[]|Promise)} 처리 완료 업무 목록 또는 처리 완료 객체 목록을 resolve하는 Promise 객체.
         * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithWork|example=Task
         * @o2syntax
         * //이 예시는 동기로 실행
         * var taskCompletedList = this.workContext.getTaskCompletedList();
         * @o2syntax
         * //이 예시는 비동기로 실행
         * this.workContext.getTaskCompletedList(function(taskCompletedList){
         *     //taskCompletedList는 처리 완료 배열
         * });
         * @o2syntax
         * //이 예시는 Promise 사용
         * this.workContext.getTaskCompletedList(true).then(function(taskCompletedList){
         *     //taskCompletedList는 처리 완료 배열
         * });
         */
        "getTaskCompletedList": function(callback, error){
            return _getWorkContextList("listTaskCompletedByWorkOrWorkCompleted", ev.work.id, callback, error);
            // var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            // var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            // var list;
            // var p = o2.Actions.get("x_processplatform_assemble_surface").listTaskCompletedByWork(ev.work.id, function(json){
            //     list = json.data;
            //     if (cb) cb(list);
            // }, ecb, !!callback);
            // return (!!callback) ? p : list;
        },

        /**
         * 현재 업무 문서의 job으로 현재 프로세스 인스턴스의 모든 처리 완료 객체를 조회합니다. 처리한 사람이 없으면 빈 배열을 반환합니다.
         * @method getTaskCompletedListByJob
         * @static
         * @param {Function|Boolean} [callback] 처리 완료 배열을 정상 조회했을 때의 콜백 또는 불리언 값. true로 판단되면 이 메서드는 비동기로 실행되어 Promise를 반환하고, 그렇지 않으면 동기로 실행됨
         * @param {Function} [error] 처리 완료 배열 조회 실패 시 콜백.
         * @return {(TaskCompleted[]|Promise)} 처리 완료 업무 목록 또는 처리 완료 객체 목록을 resolve하는 Promise 객체.
         * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithJob|example=Task
         * @o2syntax
         * //이 예시는 동기로 실행
         * var taskCompletedList = this.workContext.getTaskCompletedListByJob();
         * @o2syntax
         * //이 예시는 비동기로 실행
         * this.workContext.getTaskCompletedListByJob( function(taskCompletedList){
         *     //taskCompletedList는 처리 완료 배열
         * });
         * @o2syntax
         * //이 예시는 Promise 사용
         * this.workContext.getTaskCompletedListByJob(true).then(function(taskCompletedList){
         *     //taskCompletedList는 처리 완료 배열
         * });
         */
        "getTaskCompletedListByJob": function(callback, error){
            return _getWorkContextList("listTaskCompletedByJob", ev.work.job, callback, error);
            // var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            // var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            // var list;
            // var p = o2.Actions.get("x_processplatform_assemble_surface").listTaskCompletedByJob(ev.work.job, function(json){
            //     list = json.data;
            //     if (cb) cb(list);
            // }, ecb, !!callback);
            // return (!!callback) ? p : list;
        },

        /**
         * @summary 현재 프로세스 인스턴스의 모든 열람 대기 객체 배열을 조회합니다. 열람 대기가 없으면 빈 배열을 반환합니다.
         * @method getReadList
         * @static
         * @param {Function|Boolean} [callback] 열람 대기 배열 조회 성공 콜백 또는 불리언 값. true로 판단되면 비동기로 실행하여 Promise를 반환하고, 아니면 동기 실행
         * @param {Function} [error] 열람 대기 배열 조회 실패 시 콜백.
         * @return {(Read[]|Promise)} 현재 프로세스 인스턴스의 모든 열람 대기 객체 배열 또는 열람 대기 객체 목록을 resolve하는 Promise 객체.
         * @o2ActionOut x_processplatform_assemble_surface.ReadAction.get|example=Read
         * @o2syntax
         * //이 예시는 동기로 실행
         * var readList = this.workContext.getReadList();
         * @o2syntax
         * //이 예시는 비동기로 실행
         * this.workContext.getReadList( function(readList){
         *     //readList는 열람 대기 배열
         * });
         * @o2syntax
         * //이 예시는 Promise 사용
         * this.workContext.getReadList(true).then(function(readList){
         *     //readList는 열람 대기 배열
         * });
         */
        "getReadList": function(callback, error){
            return _getWorkContextList("listReadByWorkOrWorkCompleted", ev.work.id, callback, error);
            // var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            // var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            // var list;
            // o2.Actions.get("x_processplatform_assemble_surface").listReadByWork(ev.work.id, function(json){
            //     list = json.data;
            //     if (cb) cb(list);
            // }, ecb, !!cb);
            // return list;
        },

        /**
         * @summary 현재 업무 문서의 job으로 현재 프로세스 인스턴스의 모든 열람 대기 객체를 조회합니다. 열람 대기가 없으면 빈 배열을 반환합니다.
         * @method getReadListByJob
         * @static
         * @param {Function|Boolean} [callback] 열람 대기 배열 조회 성공 콜백 또는 불리언 값. true로 판단되면 비동기로 실행하여 Promise를 반환하고, 아니면 동기 실행
         * @param {Function} [error] 열람 대기 배열 조회 실패 시 콜백.
         * @return {(Read[]|Promise)} 현재 프로세스 인스턴스의 모든 열람 대기 객체 배열 또는 열람 대기 객체 목록을 resolve하는 Promise 객체.
         * @o2ActionOut x_processplatform_assemble_surface.ReadAction.listWithJob|example=Read
         * @o2syntax
         * //이 예시는 동기로 실행
         * var readList = this.workContext.getReadListByJob();
         * @o2syntax
         * //이 예시는 비동기로 실행
         * this.workContext.getReadListByJob( function(readList){
         *     //readList는 열람 대기 배열
         * });
         * @o2syntax
         * //이 예시는 Promise 사용
         * this.workContext.getReadListByJob(true).then(function(readList){
         *     //readList는 열람 대기 배열
         * });
         */
        "getReadListByJob": function(callback, error){
            return _getWorkContextList("listReadByJob", ev.work.job, callback, error);
            // var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            // var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            // var list;
            // o2.Actions.get("x_processplatform_assemble_surface").listReadByJob(ev.work.job, function(json){
            //     list = json.data;
            //     if (cb) cb(list);
            // }, ecb, !!cb);
            // return list;
        },

        /**
         * @summary 현재 프로세스 인스턴스의 모든 열람 완료 객체를 조회합니다. 열람 완료가 없으면 빈 배열을 반환합니다.
         * @method getReadCompletedList
         * @static
         * @param {Function|Boolean} [callback] 열람 완료 배열 조회 성공 콜백 또는 불리언 값. true로 판단되면 비동기로 실행하여 Promise를 반환하고, 아니면 동기 실행
         * @param {Function} [error] 열람 완료 배열 조회 실패 시 콜백.
         * @return {(ReadCompleted[]|Promise)} 현재 프로세스 인스턴스의 모든 열람 완료 객체 배열 또는 열람 완료 객체 목록을 resolve하는 Promise 객체.
         * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithWork|example=Read
         * @o2syntax
         * //이 예시는 동기로 실행
         * var readCompletedList = this.workContext.getReadCompletedList();
         * @o2syntax
         * //이 예시는 비동기로 실행
         * this.workContext.getReadCompletedList( function(readCompletedList){
         *     //readCompletedList는 열람 완료 배열
         * });
         * @o2syntax
         * //이 예시는 Promise 사용
         * this.workContext.getReadCompletedList(true).then(function(readCompletedList){
         *     //readCompletedList는 열람 완료 배열
         * });
         */
        "getReadCompletedList": function(callback, error){
            return _getWorkContextList("listReadCompletedByWorkOrWorkCompleted", ev.work.id, callback, error);
            // var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            // var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            // var list;
            // o2.Actions.get("x_processplatform_assemble_surface").listReadCompletedByWork(ev.work.id, function(json){
            //     list = json.data;
            //     if (cb) cb(list);
            // }, ecb, !!cb);
            // return list;
        },

        /**
         * @summary 현재 업무 문서의 job으로 현재 프로세스 인스턴스의 모든 열람 완료 객체를 조회합니다. 열람 완료가 없으면 빈 배열을 반환합니다.
         * @method getReadCompletedListByJob
         * @static
         * @param {Function|Boolean} [callback] 열람 완료 배열 조회 성공 콜백 또는 불리언 값. true로 판단되면 비동기로 실행하여 Promise를 반환하고, 아니면 동기 실행
         * @param {Function} [error] 열람 완료 배열 조회 실패 시 콜백.
         * @return {(ReadCompleted[]|Promise)} 현재 프로세스 인스턴스의 모든 열람 완료 객체 배열 또는 열람 완료 객체 목록을 resolve하는 Promise 객체.
         * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithJob|example=Read
         * @o2syntax
         * //이 예시는 동기로 실행
         * var readCompletedList = this.workContext.getReadCompletedListByJob();
         * @o2syntax
         * //이 예시는 비동기로 실행
         * this.workContext.getReadCompletedListByJob( function(readCompletedList){
         *     //readCompletedList는 열람 완료 배열
         * });
         * @o2syntax
         * //이 예시는 Promise 사용
         * this.workContext.getReadCompletedListByJob(true).then(function(readCompletedList){
         *     //readCompletedList는 열람 완료 배열
         * });
         */
        "getReadCompletedListByJob": function(callback, error){
            return _getWorkContextList("listReadCompletedByJob", ev.work.job, callback, error);
            // var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            // var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            // var list;
            // o2.Actions.get("x_processplatform_assemble_surface").listReadCompletedByJob(ev.work.job, function(json){
            //     list = json.data;
            //     if (cb) cb(list);
            // }, ecb, !!cb);
            // return list;
        },

        /**
         * @summary 현재 업무 문서의 job으로 현재 프로세스 인스턴스의 모든 review 객체를 조회합니다. review가 없으면 빈 배열을 반환합니다.
         * @method getReviewList
         * @static
         * @param {Function|Boolean} [callback] review 배열 조회 성공 콜백 또는 불리언 값. true로 판단되면 비동기로 실행하여 Promise를 반환하고, 아니면 동기 실행
         * @param {Function} [error] 열람 완료 배열 조회 실패 시 콜백.
         * @return {(Review[]|Promise)} 현재 프로세스 인스턴스의 모든 review 객체 배열 또는 review 객체 목록을 resolve하는 Promise 객체.
         * @o2ActionOut x_processplatform_assemble_surface.ReviewAction.listWithJob|example=Review
         * @o2syntax
         * //이 예시는 동기로 실행
         * var reviewList = this.workContext.getReviewList();
         * @o2syntax
         * //이 예시는 비동기로 실행
         * this.workContext.getReviewList( function(reviewList){
         *     //reviewList는 review 객체 배열
         * });
         * @o2syntax
         * //이 예시는 Promise 사용
         * this.workContext.getReviewList(true).then(function(reviewList){
         *     //reviewList는 review 객체 배열
         * });
         */
        "getReviewList": function(callback, error){
            return _getWorkContextList("listReviewByJob", ev.work.job, callback, error);
            // var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            // var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            // var list;
            // o2.Actions.get("x_processplatform_assemble_surface").listReadCompletedByJob(ev.work.job, function(json){
            //     list = json.data;
            //     if (cb) cb(list);
            // }, ecb, !!cb);
            // return list;
        },
        /**
         * @summary getReviewList 메서드와 같습니다.
         * @method getReviewListByJob
         * @static
         * @see module:workContext.getReviewList
         */
        "getReviewListByJob": this.getReviewList,
        /**
         * @summary getTaskListByJob 메서드의 별칭.
         * @method getJobTaskList
         * @static
         * @see module:workContext.getTaskListByJob
         */
        "getJobTaskList": this.getTaskListByJob,
        /**
         * @summary getReadListByJob 메서드의 별칭.
         * @method getJobReadList
         * @static
         * @see module:workContext.getReadListByJob
         */
        "getJobReadList": this.getReadListByJob,
        /**
         * @summary getTaskCompletedListByJob 메서드의 별칭.
         * @method getJobTaskCompletedList
         * @static
         * @see module:workContext.getTaskCompletedListByJob
         */
        "getJobTaskCompletedList": this.getTaskCompletedListByJob,
        /**
         * @summary getReadCompletedListByJob 메서드의 별칭.
         * @method getJobReadCompletedList
         * @static
         * @see module:workContext.getReadCompletedListByJob
         */
        "getJobReadCompletedList": this.getReadCompletedListByJob,
        /**
         * @summary getReviewList 메서드와 같습니다.
         * @method getJobReviewList
         * @static
         * @see module:workContext.getReviewList
         */
        "getJobReviewList": this.getReviewList,

        /**
         * @summary 현재 사용자의 프로세스 인스턴스에 대한 권한 조회.
         * @method getControl
         * @static
         * @return {WorkControl} 프로세스 인스턴스 권한 객체.
         * <pre><code class='language-js'>{
         *        "allowVisit": true,             //업무 문서 접근 허용 여부
         *        "allowFlow": true,              //계속 진행 허용 여부(제출 또는 처리자 재설정 또는 추가 결재 허용)
         *        "allowProcessing": true,        //제출 허용 여부
         *        "allowReadProcessing": false,   //열람 대기 존재 여부
         *        "allowSave": true,              //업무 데이터 저장 허용 여부
         *        "allowReset": false,            //처리자 재설정 허용 여부
         *        "allowReroute": false,          //조정 허용 여부
         *        "allowDelete": true,             //프로세스 인스턴스 삭제 허용 여부
         *        "allowAddSplit": false,         //분기 추가 허용 여부
         *        "allowRetract": false,          //회수 허용 여부
         *        "allowRollback": false,         //프로세스 되돌리기 허용 여부
         *        "allowPress": false,             //처리 독촉 알림 발송 허용 여부
         *        "allowGoBack": false,         //반려 허용 여부
         *        "allowAddTask": false,          //추가 결재 허용 여부
         *        "allowPause": false,         //결재 대기 보류 허용 여부
         *        "allowResume": false,             //결재 대기 보류 해제 허용 여부
         * }</code></pre>
         * @o2syntax
         * var control = this.workContext.getControl();
         */
        "getControl": function(){return ev.control;},
        /**
         * @summary 현재 프로세스 인스턴스의 모든 프로세스 기록(WorkLog) 조회.
         * @method getWorkLogList
         * @static
         * @return {WorkLog[]} 프로세스 기록 객체.
         * @o2ActionOut x_processplatform_assemble_surface.WorkLogAction.listWithJob|example=WorkLog|ignoreProps=[properties,goBackFromActivityType]
         * @o2syntax
         * var workLogList = this.workContext.getWorkLogList();
         */
        "getWorkLogList": function(){return ev.workLogList;},
        /**
         * @summary 현재 프로세스 인스턴스의 모든 프로세스 기록(Record) 조회.
         * @method getRecordList
         * @o2ActionOut x_processplatform_assemble_surface.RecordAction.listWithJob|example=Record
         * @static
         * @return {Record[]} 프로세스 기록(Record) 객체.
         * @o2syntax
         * var recordList = this.workContext.getRecordList();
         */
        "getRecordList": function(){return ev.recordList;},
        /**
         * @summary 현재 프로세스 인스턴스의 첨부파일 객체 목록 조회.
         * @method getAttachmentList
         * @static
         * @param {Function|boolean} [callback] Funcation을 전달하면 첨부파일 객체 배열을 정상적으로 가져온 비동기 호출의 콜백으로 사용;
         * true를 전달하면 비동기 요청으로 첨부파일 목록을 가져와 Promise 객체를 반환; false를 전달하면 동기 요청으로 첨부파일 목록을 가져옴;
         * 파라미터를 전달하지 않으면 로컬 캐시의 attachmentList 객체를 바로 반환.
         * @param {Function} [error] 첨부파일 객체 배열 조회 오류 시 콜백.
         * @return {WorkAttachmentData[]} 첨부파일 데이터.
         * @o2ActionOut x_processplatform_assemble_surface.AttachmentAction.getWithWorkOrWorkCompleted|example=Attachment|ignoreProps=[properties]
         * @o2syntax
         * //로컬 캐시에서 첨부파일 목록 조회
         * var attachmentList = this.workContext.getAttachmentList();
         *
         * //동기 요청으로 첨부파일 목록 조회
         * var attachmentList = this.workContext.getAttachmentList(false);
         *
         * //비동기 요청으로 첨부파일 목록 조회
         * var promise = this.workContext.getAttachmentList(true);
         * promise.then(function(attachmentList){
         *     //attachmentList 첨부파일 객체 배열
         * })
         *
         * //비동기 요청으로 첨부파일 목록 조회
         * this.workContext.getAttachmentList( function(attachmentList){
         *     //attachmentList 첨부파일 객체 배열
         * });
         */
        "getAttachmentList": function(callback, error){
            if (!callback && callback !== false) {
                return ev.attachmentList;
            }

            var cb = (callback && o2.typeOf(callback)==="function") ? callback : null;
            var ecb = (error && o2.typeOf(error)==="function") ? error : null;
            var list;
            var p = o2.Actions.load("x_processplatform_assemble_surface").AttachmentAction.listWithJob(ev.work.job, function(json){
                list = json.data;
                if (cb) cb(list);
                return list;
            }, ecb, !!callback);
            return (callback) ? p : list;
        },
        /**
         * @summary 현재 결재 대기의 선택 가능한 경로 조회. task 객체의 routeNameList 값과 동일.
         * @method getRouteList
         * @static
         * @return {String[]} 경로 문자열 배열.
         * @o2syntax
         * var routeList = this.workContext.getRouteList();
         */
        "getRouteList": function(){return (ev.task) ? ev.task.routeNameList: null;},
        "getInquiredRouteList": function(){return null;}
        // /**
        //  * @summary 프로세스 인스턴스 제목 재설정.
        //  * @method setTitle
        //  * @static
        //  * @param {String} title - 제목 문자열.
        //  * @o2syntax
        //  * this.workContext.setTitle(title);
        //  * @example
        //  * this.workContext.setTitle("제목");
        //  */
        // "setTitle": function(title){
        //     if (!this.workAction){
        //         MWF.require("MWF.xScript.Actions.WorkActions", null, false);
        //         this.workAction = new MWF.xScript.Actions.WorkActions();
        //     }
        //     this.workAction.setTitle(ev.work.id, {"title": title});
        // }
    };
    this.workContent = this.workContext;
    var _redefineWorkProperties = function(work){
        if (work){
            work.creatorPersonDn = work.creatorPerson ||"";
            work.creatorUnitDn = work.creatorUnit ||"";
            work.creatorUnitDnList = work.creatorUnitList ||"";
            work.creatorIdentityDn = work.creatorIdentity ||"";
            var o = {
                "creatorPerson": {"get": function(){return this.creatorPersonDn.substring(0, this.creatorPersonDn.indexOf("@"));}},
                "creatorUnit": {"get": function(){return this.creatorUnitDn.substring(0, this.creatorUnitDn.indexOf("@"));}},
                "creatorDepartment": {"get": function(){return this.creatorUnitDn.substring(0, this.creatorUnitDn.indexOf("@"));}},
                "creatorIdentity": {"get": function(){return this.creatorIdentityDn.substring(0, this.creatorIdentityDn.indexOf("@"));}},
                // "creatorUnitList": {
                //     "get": function(){
                //         var v = [];
                //         this.creatorUnitDnList.each(function(dn){
                //             v.push(dn.substring(0, dn.indexOf("@")))
                //         });
                //         return v;
                //     }
                // },
                "creatorCompany": {"get": function(){
                        if (this.creatorUnitLevel || this.creatorUnitLevelName){
                            var level = (this.creatorUnitLevel || this.creatorUnitLevelName).split("/");
                            return level[0];
                        }else{
                            return this.creatorUnitDn.substring(0, this.creatorUnitDn.indexOf("@"));
                        }
                    }}
            };
            MWF.defineProperties(work, o);
        }
        return work;
    };
    var _redefineTaskProperties = function(task){
        if (task){
            task.personDn = task.person || "";
            task.unitDn = task.unit || "";
            task.unitDnList = task.unitList || "";
            task.identityDn = task.identity || "";
            var o = {
                "person": {"get": function(){return this.personDn.substring(0, this.personDn.indexOf("@"));}},
                "unit": {"get": function(){return this.unitDn.substring(0, this.unitDn.indexOf("@"));}},
                "department": {"get": function(){return this.unitDn.substring(0, this.unitDn.indexOf("@"));}},
                "identity": {"get": function(){return this.identityDn.substring(0, this.identityDn.indexOf("@"));}},
                // "unitList": {
                //     "get": function(){
                //         var v = [];
                //         this.unitDnList.each(function(dn){
                //             v.push(dn.substring(0, dn.indexOf("@")))
                //         });
                //         return v;
                //     }
                // },
                "company": {"get": function(){return this.unitList[0];}}
            };
            MWF.defineProperties(task, o);
        }
        return task;
    };
    _redefineWorkProperties(this.workContext.getWork());
    _redefineTaskProperties(_redefineWorkProperties(this.workContext.getTask()));


    //dict
    this.Dict = MWF.xScript.createDict((_form.businessData.work || _form.businessData.workCompleted).application, "process");

    //unit
    var orgActions = null;
    var getOrgActions = function(){
        // if (!orgActions){
        //     MWF.xDesktop.requireApp("Org", "Actions.RestActions", null, false);
        //     orgActions = new MWF.xApplication.Org.Actions.RestActions ();
        // }
        if (!orgActions){
            MWF.require("MWF.xScript.Actions.UnitActions", null, false);
            orgActions = new MWF.xScript.Actions.UnitActions();
        }
    };
    var getNameFlag = function(name){
        var t = typeOf(name);
        if (t==="array"){
            var v = [];
            name.each(function(id){
                v.push((typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id);
            });
            return v;
        }else{
            return [(t==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name];
        }
    };


    this.org = {
        //그룹***************
        //그룹 조회--그룹 객체 배열 반환
        /**
         그룹 식별자로 해당 그룹 객체 배열 조회: group 객체 배열
         */
        getGroup: function(name, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;

            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listGroup(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = null;
            // orgActions.listGroup(data, function(json){v = json.data;}, null, false);
            // return (v && v.length===1) ? v[0] : v;
        },
        //하위 그룹 조회--그룹 객체 배열 반환
        //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
        listSubGroup: function(name, nested, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;
            // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
            //     v = json.data;
            //     return v;
            // }.ag().catch(function(json){ return json; });
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listSubGroupNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listSubGroupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;

            // var v = null;
            // if (nested){
            //     orgActions.listSubGroupNested(data, function(json){v = json.data;}, null, false);
            // }else{
            //     orgActions.listSubGroupDirect(data, function(json){v = json.data;}, null, false);
            // }
            // return v;
        },
        //상위 그룹 조회--그룹 객체 배열 반환
        //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
        listSupGroup:function(name, nested, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise
            if (nested){
                var promise = orgActions.listSupGroupNested(data, cb, null, !!async);
            }else{
                var promise = orgActions.listSupGroupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
            // var v = null;
            // if (nested){
            //     orgActions.listSupGroupNested(data, function(json){v = json.data;}, null, false);
            // }else{
            //     orgActions.listSupGroupDirect(data, function(json){v = json.data;}, null, false);
            // }
            // return v;
        },
        //사용자 소속 그룹(중첩)--그룹 객체 배열 반환
        listGroupWithPerson:function(name, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listGroupWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
            // var v = null;
            // orgActions.listGroupWithPerson(data, function(json){v = json.data;}, null, false);
            // return v;
        },
        //신분 소속 그룹(중첩)--그룹 객체 배열 반환
        listGroupWithIdentity:function(identity, async){
            getOrgActions();
            var data = {"identityList": getNameFlag(identity)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listGroupWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //그룹의 역할 보유 여부--true, false 반환
        groupHasRole: function(name, role, async){
            getOrgActions();
            nameFlag = (typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
            var data = {"group":nameFlag,"roleList":getNameFlag(role)};

            var v = false;
            var cb = function(json){
                v = json.data.value;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.groupHasRole(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = false;
            // orgActions.groupHasRole(data, function(json){v = json.data.value;}, null, false);
            // return v;
        },

        //역할***************
        //역할 조회--역할 객체 배열 반환
        getRole: function(name, async){
            getOrgActions();
            var data = {"roleList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listRole(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = null;
            // orgActions.listRole(data, function(json){v = json.data;}, null, false);
            // return (v && v.length===1) ? v[0] : v;
        },
        //사용자의 모든 역할(중첩)--역할 객체 배열 반환
        listRoleWithPerson:function(name, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listRoleWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
            // var v = null;
            // orgActions.listRoleWithPerson(data, function(json){v = json.data;}, null, false);
            // return v;
        },

        //사용자***************
        //사용자의 역할 보유 여부--true, false 반환
        personHasRole: function(name, role, async){
            getOrgActions();
            nameFlag = (typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
            var data = {"person":nameFlag,"roleList":getNameFlag(role)};

            var v = false;
            var cb = function(json){
                v = json.data.value;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.personHasRole(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = false;
            // orgActions.personHasRole(data, function(json){v = json.data.value;}, null, false);
            // return v;
        },

        //사용자 조회, 신분, 신분 소속 조직, 개인 소속 그룹, 개인 보유 역할 포함.
        getPersonData: function(name, async){
            getOrgActions();
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.getPerson(null, cb, null, !!async, {"flag": name});
            return (!!async) ? promise : v;
        },

        //사용자 조회--사용자 객체 배열 반환
        getPerson: function(name, async, findCN){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};
            if( o2.typeOf(findCN) === "boolean"){
                data.useNameFind = findCN;
            }
            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //하위 사용자 조회--사용자 객체 배열 반환
        //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
        listSubPerson: function(name, nested, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listPersonSubNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listPersonSubDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //상위 사용자 조회--사용자 객체 배열 반환
        //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
        listSupPerson: function(name, nested, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listPersonSupNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listPersonSupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //그룹의 모든 사용자 조회--사용자 객체 배열 반환
        listPersonWithGroup: function(name, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithGroup(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //역할의 모든 사용자 조회--사용자 객체 배열 반환
        listPersonWithRole: function(name, async){
            getOrgActions();
            var data = {"roleList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise
            promise = orgActions.listPersonWithRole(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //신분의 모든 사용자 조회--사용자 객체 배열 반환
        listPersonWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //신분의 모든 사용자 조회--사용자 객체 배열 또는 사용자 객체 반환
        getPersonWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                v =  (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //조직 구성원 사용자 조회--사용자 객체 배열 반환
        //nested  불리언  true 중첩된 모든 구성원; false 직접 구성원; 기본값 false;
        listPersonWithUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listPersonWithUnitNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listPersonWithUnitDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //속성으로 사용자 조회--사용자 객체 배열 반환
        //name  string 속성 이름
        //value  string 속성 값
        listPersonWithAttribute: function(name, value, async){
            getOrgActions();
            var data = {"name": name, "attribute": value};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //속성으로 사용자 조회--사용자 전체 이름 배열 반환
        //name  string 속성 이름
        //value  string 속성 값
        listPersonNameWithAttribute: function(name, value, async){
            getOrgActions();
            var data = {"name": name, "attribute": value};
            var v = null;
            var cb = function(json){
                v = json.data.personList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithAttributeValue(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //사용자 속성************
        //사용자 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
        appendPersonAttribute: function(person, attr, values, success, failure, async){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"attributeList":values,"name":attr,"person":personFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.appendPersonAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
            //orgActions.appendPersonAttribute(data, cb, null, !!async);
        },
        //사용자 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
        setPersonAttribute: function(person, attr, values, success, failure, async){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"attributeList":values,"name":attr,"person":personFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.setPersonAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            //
            // orgActions.setPersonAttribute(data, cb, null, !!async);
        },
        //사용자 속성 값 조회
        getPersonAttribute: function(person, attr, async){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"name":attr,"person":personFlag};
            var v = null;
            var cb = function(json){
                v = json.data.attributeList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.getPersonAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //사용자의 모든 속성 이름 목록
        listPersonAttributeName: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonAttributeName(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //사용자의 모든 속성 목록
        listPersonAllAttribute: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonAllAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //신분**********
        //신분 조회
        getIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //사용자의 신분 목록
        listIdentityWithPerson: function(name, async, findCN){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            if( o2.typeOf(findCN) === "boolean"){
                data.useNameFind = findCN;
            }
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listIdentityWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //조직 구성원 신분 조회--신분 객체 배열 반환
        //nested  불리언  true 중첩된 모든 구성원; false 직접 구성원; 기본값 false;
        listIdentityWithUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;

            // var cb = function(json){
            //     v = json.data;
            //     if (async && o2.typeOf(async)=="function") return async(v);
            //     return v;
            // }.ag().catch(function(json){ return json; });

            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var method = (nested) ? "listIdentityWithUnitNested" : "listIdentityWithUnitDirect";
            var promise = orgActions[method](data, cb, null, !!async);
            promise.name = "org";

            //
            // if (nested){
            //     orgActions.listIdentityWithUnitNested(data, cb, null, !!async);
            // }else{
            //     orgActions.listIdentityWithUnitDirect(data, cb, null, !!async);
            // }
            return (!!async) ? promise : v;
        },

        //조직**********
        //조직 조회
        getUnit: function(name, async, findCN){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            if( o2.typeOf(findCN) === "boolean"){
                data.useNameFind = findCN;
            }
            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnit(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //조직의 하위 조회--조직 객체 배열 반환
        //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
        listSubUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listUnitSubNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listUnitSubDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //조직의 상위 조회--조직 객체 배열 반환
        //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
        //async 불리언 true 비동기 요청
        listSupUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listUnitSupNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listUnitSupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;

            // if (callback){
            //     if (nested){
            //         orgActions.listUnitSupNested(data, function(json){v = json.data; o2.runCallback(callback, "success", [v], this);});
            //     }else{
            //         orgActions.listUnitSupDirect(data, function(json){v = json.data; o2.runCallback(callback, "success", [v], this);});
            //     }
            // }else{
            //     var v = null;
            //     if (nested){
            //         orgActions.listUnitSupNested(data, function(json){v = json.data;}, null, false);
            //     }else{
            //         orgActions.listUnitSupDirect(data, function(json){v = json.data;}, null, false);
            //     }
            //     return v;
            // }
        },

        listSupUnitWithLevel: function(name, level, async){
            var v;
            var supUnitList = this.listSupUnit( name, true, !!async);
            var unitList = this.getUnit( name, false, !!async );

            var cb = function(sups, units){
                v = [].concat(
                    sups,
                    typeOf( units ) === "object" ? [units] : units
                ).filter(function (u){
                    return u.level === level;
                });
                if (async && o2.typeOf(async)==="function") return async(v);
                return v;
            };

            if( typeof supUnitList.then === 'function' ){
                return Promise.all([supUnitList, unitList]).then(function( result){
                    return cb(result[0], result[1]);
                });
            }else{
                return cb(supUnitList, unitList);
            }
        },

        listSupUnitWithType: function(name, type, async){
            var v;
            var supUnitList = this.listSupUnit( name, true, !!async);
            var unitList = this.getUnit( name, false, !!async );

            var cb = function(sups, units){
                v = [].concat(
                    sups,
                    typeOf( units ) === "object" ? [units] : units
                ).filter(function (u){
                    return (u.typeList || []).contains( type );
                });
                if (async && o2.typeOf(async)==="function") return async(v);
                return v;
            };

            if( typeof supUnitList.then === 'function' ){
                return Promise.all([supUnitList, unitList]).then(function( result){
                    return cb(result[0], result[1]);
                });
            }else{
                return cb(supUnitList, unitList);
            }
        },

        //개인 신분으로 조직 조회
        //flag 숫자    몇 번째 단계의 조직을 조회할지 나타냄
        //     문자열  지정 유형의 조직 조회를 나타냄
        //     비어 있음     직접 소속 조직 조회를 나타냄
        getUnitByIdentity: function(name, flag, async){
            getOrgActions();
            var getUnitMethod = "current";
            var v;
            if (flag){
                if (typeOf(flag)==="string") getUnitMethod = "type";
                if (typeOf(flag)==="number") getUnitMethod = "level";
            }

            var cb;
            var promise;
            switch (getUnitMethod){
                case "current":
                    var data = {"identityList":getNameFlag(name)};

                    // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                    //     v = json.data;  v=(v&&v.length===1) ? v[0] : v; return v;
                    // }.ag().catch(function(json){ return json; });


                    cb = function(json){
                        v = json.data;  v=(v&&v.length===1) ? v[0] : v;
                        if (async && o2.typeOf(async)=="function") return async(v);
                        return v;
                    };


                    promise = orgActions.listUnitWithIdentity(data, cb, null, !!async);
                    break;
                case "type":
                    var data = {"identity":(typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,"type":flag};

                    cb = function(json){
                        v = json.data;
                        if (async && o2.typeOf(async)=="function") return async(v);
                        return v;
                    };

                    // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                    //     v = json.data;  return v;
                    // }.ag().catch(function(json){ return json; });

                    promise = orgActions.getUnitWithIdentityAndType(data, cb, null, !!async);
                    break;
                case "level":
                    var data = {"identity":(typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,"level":flag};

                    cb = function(json){
                        v = json.data;  v=(v&&v.length===1) ? v[0] : v;
                        if (async && o2.typeOf(async)=="function") return async(v);
                        return v;
                    };

                    // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                    //     v = json.data;  return v;
                    // }.ag().catch(function(json){ return json; });

                    promise = orgActions.getUnitWithIdentityAndLevel(data, cb, null, !!async);
                    break;
            }
            return (!!async) ? promise : v;
        },
        //신분 소속 조직의 모든 상위 조직 목록
        listAllSupUnitWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitSupNestedWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //사용자가 소속된 모든 조직 조회
        listUnitWithPerson: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //사용자 소속 조직의 모든 상위 조직 목록
        listAllSupUnitWithPerson: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitSupNestedWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //조직 속성으로 일치하는 모든 조직 조회
        listUnitWithAttribute: function(name, attribute, async){
            getOrgActions();
            var data = {"name":name,"attribute":attribute};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            promise = orgActions.listUnitWithAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //조직 직무로 일치하는 모든 조직 조회
        listUnitWithDuty: function(name, id, async){
            getOrgActions();
            var data = {"name":name,"identity":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitWithDuty(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //조직 직무***********
        //지정 조직 직무의 신분 조회
        getDuty: function(duty, id, async){
            getOrgActions();
            var data = {"name":duty,"unit":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
            var v = null;

            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.getDuty(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //신분의 모든 직무 이름 조회
        listDutyNameWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listDutyNameWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //조직의 모든 직무 이름 조회
        listDutyNameWithUnit: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listDutyNameWithUnit(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //조직의 모든 직무 조회
        listUnitAllDuty: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitAllDuty(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //최상위 조직 목록
        listTopUnit: function(async){
            var action = MWF.Actions.get("x_organization_assemble_control");
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = action.listTopUnit(cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //조직 속성**************
        //조직 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
        appendUnitAttribute: function(unit, attr, values, success, failure, async){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"attributeList":values,"name":attr,"unit":unitFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.appendUnitAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            //
            // orgActions.appendPersonAttribute(data, cb, null, !!async);

            // orgActions.appendUnitAttribute(data, function(json){
            //     if (json.data.value){
            //         if (success) success();
            //     }else{
            //         if (failure) failure(null, "", "append values failed");
            //     }
            // }, function(xhr, text, error){
            //     if (failure) failure(xhr, text, error);
            // }, false);
        },
        //조직 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
        setUnitAttribute: function(unit, attr, values, success, failure, async){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"attributeList":values,"name":attr,"unit":unitFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.setUnitAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            // orgActions.setUnitAttribute(data, cb, null, !!async);

            // orgActions.setUnitAttribute(data, function(json){
            //     if (json.data.value){
            //         if (success) success();
            //     }else{
            //         if (failure) failure(null, "", "append values failed");
            //     }
            // }, function(xhr, text, error){
            //     if (failure) failure(xhr, text, error);
            // }, false);
        },
        //조직 속성 값 조회
        getUnitAttribute: function(unit, attr, async){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"name":attr,"unit":unitFlag};
            var v = null;
            var cb = function(json){
                v = json.data.attributeList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.getUnitAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //조직의 모든 속성 이름 목록
        listUnitAttributeName: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitAttributeName(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //조직의 모든 속성 목록
        listUnitAllAttribute: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitAllAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        }
    };

    this.Action = (function(){
        var actions = [];
        return function(root, json){
            var action = actions[root] || (actions[root] = new MWF.xDesktop.Actions.RestActions("", root, ""));
            action.getActions = function(callback){
                if (!this.actions) this.actions = {};
                Object.merge(this.actions, json);
                if (callback) callback();
            };
            this.invoke = function(option){
                action.invoke(option)
            }
        }
    })();

    // this.service = {
    //     "jaxwsClient":{},
    //     "jaxrsClient":{}
    // };

    var lookupAction = null;
    var getLookupAction = function(callback){
        if (!lookupAction){
            MWF.require("MWF.xDesktop.Actions.RestActions", function(){
                lookupAction = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "");
                lookupAction.getActions = function(actionCallback){
                    this.actions = {
                        //"lookup": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}"},
                        //"getView": {"uri": "/jaxrs/view/{id}/design"}
                        "lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
                        "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
                    };
                    if (actionCallback) actionCallback();
                };
                if (callback) callback();
            });
        }else{
            if (callback) callback();
        }
    };

    var _renderViewContainerMobile = function(title, viewerGenerator, okCallback, notCloseOnOK){
        const node = new Element('div.mwf_selectView_node');
        const html = `<div class="mwf_selectView_content invisible" data-o2-element="contentNode">
                            <div class="mwf_selectView_title">${title || ''}</div>
                            <div class="mwf_selectView_view" data-o2-element="viewNode"></div>
                            <div class="mwf_selectView_action">
                                <oo-button type="light" class="mwf_selectView_action_close hide" data-o2-events="click:selectCancel">${o2.LP.widget.close}</oo-button>
                                <oo-button type="light" class="mwf_selectView_action_cancel" data-o2-events="click:selectCancel">${o2.LP.widget.cancel}</oo-button>
                                <oo-button class="mwf_selectView_action_ok" data-o2-events="click:selectOk">${o2.LP.widget.ok}</oo-button>
                            </div>
                          </div>`;
        const o = {
            selectOk: function(e){
                okCallback && okCallback();
                if( !notCloseOnOK ){
                    this.selectCancel(e);
                }
            },
            selectCancel: function(e){
                this.contentNode.removeClass('visible');
                this.contentNode.addClass('invisible');
                window.setTimeout(()=>{
                    node.destroy();
                }, 200);
            }
        };
        node.loadHtmlText(html, {module: o});
        document.body.appendChild(node);

        // MWF.xDesktop.requireApp("query.Query", "Viewer", ()=>{
        //     viewer = new MWF.xApplication.query.Query.Viewer(o.viewNode, viewJson, {"style": "select"}, _form.app, _form.Macro);
        //     viewer.addEvent('selectRow', (row)=>{
        //         row.node.addClass('selectedRow');
        //     });
        //     viewer.addEvent('unselectRow', (row)=>{
        //         row.node.removeClass('selectedRow');
        //     });
        // });

        window.setTimeout(()=>{
            viewerGenerator(o.viewNode, o);
        }, 200);

        requestAnimationFrame(()=>{
            o.contentNode.removeClass('invisible');
            o.contentNode.addClass('visible');
        });
        o.contentNode.addEventListener('click', (e)=>{
            e.stopPropagation(e);
        });
        node.addEventListener('click', (e)=>{
            o.selectCancel(e);
        });
    }
    this._renderViewContainerMobile = _renderViewContainerMobile;

    var selectViewMobile = function (viewJson, okCallback, dialogOptions, viewOptions, loadedCallback){
        if(!viewOptions)viewOptions = {"style": "select"};
        if(!dialogOptions)dialogOptions = {};

        var viewer = null;
        _renderViewContainerMobile(
            dialogOptions.title || viewJson.caption,
            (viewNode)=>{
                MWF.xDesktop.requireApp("query.Query", "Viewer", ()=>{
                    viewer = new MWF.xApplication.query.Query.Viewer(viewNode, viewJson, viewOptions, _form.app, _form.Macro);
                    viewer.addEvent('selectRow', (row)=>{
                        row.node.addClass('selectedRow');
                    });
                    viewer.addEvent('unselectRow', (row)=>{
                        row.node.removeClass('selectedRow');
                    });
                    if(loadedCallback)loadedCallback(viewer);
                });
            },
            ()=>{
                if(okCallback)okCallback(viewer.getData());
            }
        );
    };
    var selectViewPc = function(viewJson, okCallback, dlalogOptions, viewOptions, loadedCallback){

            if(!viewOptions)viewOptions = {"style": "select"};

            var options =  dlalogOptions || {};
            var width = options.width || viewJson.width || "972";
            var height = options.height || viewJson.height || "672";
            var style = options.style || "v10_view";
            if (layout.mobile){
                var size = document.body.getSize();
                width = size.x;
                height = size.y;
                style = "viewmobile";
            }

            var opts = Object.assign({}, options, {
                title: options.title || viewJson.caption || "select view",
                style: style,
                width: width.toInt(),
                height: height.toInt(),
                zindex: options.zindex,
                html: "<div style='height: 100%;flex:1;'></div>",
                maxHeightPercent: layout.mobile ? "100%" : "98%",
                maskNode: layout.mobile ? $(document.body) :  _form.app.content,
                container: layout.mobile ? $(document.body) : _form.app.content,
                buttonList: [
                    {
                        "text": MWF.LP.process.button.ok,
                        "action": function(){
                            //if (callback) callback(_self.view.selectedItems);
                            if (okCallback) okCallback(_self.view.getData());
                            this.close();
                        }
                    },
                    {
                        "text": MWF.LP.process.button.cancel,
                        "action": function(){this.close();}
                    }
                ]
            });

            var _self = this;
            MWF.require("MWF.xDesktop.Dialog", function(){
                var dlg = o2.DL.open(opts);
                if (layout.mobile){
                    var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                    var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                    if (backAction) backAction.addEvent("click", function(e){
                        dlg.close();
                    }.bind(this));
                    if (okAction) okAction.addEvent("click", function(e){
                        //if (okCallback) okCallback(this.view.selectedItems);
                        if (okCallback) okCallback(this.view.getData());
                        dlg.close();
                    }.bind(this));
                }

                MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
                    this.view = new MWF.xApplication.query.Query.Viewer(dlg.content.getFirst(), viewJson, viewOptions, _form.app, _form.Macro);
                    if(loadedCallback)loadedCallback(this.view);
                }.bind(this));
            }.bind(this));
    };
    this.view = {
        "lookup": function(view, callback, async){
            var filterList = {"filterList": (view.filter || null)};
            return MWF.Actions.load("x_query_assemble_surface").ViewAction.executeWithQuery(view.view, view.application, filterList, function(json){
                var data = {
                    "grid": json.data.grid || json.data.groupGrid,
                    "groupGrid": json.data.groupGrid
                };
                if (callback) callback(data);
                return data;
            }, null, async);
        },

        "lookupV1": function(view, callback){
            getLookupAction(function(){
                lookupAction.invoke({"name": "lookup","async": true, "parameter": {"view": view.view, "application": view.application},"success": function(json){
                        var data = {
                            "grid": json.data.grid,
                            "groupGrid": json.data.groupGrid
                        };
                        if (callback) callback(data);
                    }.bind(this)});
            }.bind(this));
        },
        "select": function(view, okCallback, dialogOptions, viewOptions, loadedCallback){
            if( view.view || view.viewName || view.name || view.viewId ){
                var viewJson = {
                    "application": view.application || _form.json.application,
                    "viewName": view.viewName || view.view || view.name || "",
                    "isTitle": typeOf( view.isTitle ) === 'string' ? view.isTitle : ((view.isTitle===false) ? "no" : "yes"),
                    "select": typeOf( view.select ) === 'string' ? view.select : ((view.isMulti===false) ? "single" : "multi"),
                    "filter": view.filter
                };
                if( view.hasOwnProperty('viewId') )viewJson.viewId = view.viewId;
                // if( view.hasOwnProperty('titleStyles') )viewJson.titleStyles = view.titleStyles;
                // if( view.hasOwnProperty('itemStyles') )viewJson.itemStyles = view.itemStyles;
                // if( view.hasOwnProperty('isExpand') )viewJson.isExpand = view.isExpand;
                // if( view.hasOwnProperty('showActionbar') )viewJson.showActionbar = view.showActionbar;
                // if( view.hasOwnProperty('defaultSelectedScript') )viewJson.defaultSelectedScript = view.defaultSelectedScript;
                // if( view.hasOwnProperty('selectedAbleScript') )viewJson.selectedAbleScript = view.selectedAbleScript;
                for( var key in view){
                    if( !viewJson.hasOwnProperty(key) ){
                        viewJson[key] = view[key];
                    }
                }
                //if (layout.mobile && o2.version.dev===10){
                var styleType = _form.json?.formStyleType || _form.viewJson?.viewStyleType;
                if (layout.mobile && styleType === 'v10') {
                    selectViewMobile(viewJson, okCallback, dialogOptions, viewOptions, loadedCallback);
                }else{
                    selectViewPc(viewJson, okCallback, dialogOptions, viewOptions, loadedCallback);
                }
            }
        }
    }


    var selectStatementMobile = function (statementJson, okCallback, dialogOptions, statementOptions, loadedCallback){
            if(!statementOptions)statementOptions = {"style": "select"};
            if(!dialogOptions)dialogOptions = {};

            var viewer = null;
            _renderViewContainerMobile(
                dialogOptions.title || statementJson.caption,
                (viewNode)=>{
                    MWF.xDesktop.requireApp("query.Query", "Statement", ()=>{
                        viewer = new MWF.xApplication.query.Query.Statement( viewNode, statementJson, statementOptions, _form.app, _form.Macro);
                        viewer.addEvent('selectRow', (row)=>{
                            row.node.addClass('selectedRow');
                        });
                        viewer.addEvent('unselectRow', (row)=>{
                            row.node.removeClass('selectedRow');
                        });
                        if(loadedCallback)loadedCallback(viewer);
                    });
                },
                ()=>{
                    if(okCallback)okCallback(viewer.getData());
                }
            );
    };
    var selectStatementPc = function(statementJson, okCallback, dialogOptions, statementOptions, loadedCallback){

            if(!statementOptions)statementOptions = {"style": "select"};

            var options =  dialogOptions || {};
            var width = options.width || statementJson.width || "972";
            var height = options.height || statementJson.height || "672";
            var style = options.style || "v10_view";

            if (layout.mobile) {
                var size = document.body.getSize();
                width = size.x;
                height = size.y;
                style = "viewmobile";
            }

            var opts = Object.assign({}, options, {
                    title: options.title || statementJson.caption || "select view",
                    style: style,
                    width: width.toInt(),
                    height: height.toInt(),
                    zindex: options.zindex,
                    html: "<div style='height: 100%;flex:1;'></div>",
                    maxHeightPercent: layout.mobile ? "100%" : "98%",
                    maskNode: layout.mobile ? $(document.body) :  _form.app.content,
                    container: layout.mobile ? $(document.body) : _form.app.content,
                    buttonList: [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function () {
                                //if (callback) callback(_self.view.selectedItems);
                                if (okCallback) okCallback(_self.statement.getData());
                                this.close();
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function () { this.close(); }
                        }
                    ]
                });
            var _self = this;
            MWF.require("MWF.xDesktop.Dialog", function () {
                var dlg = o2.DL.open(opts);
                if (layout.mobile) {
                    var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                    var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                    if (backAction) backAction.addEvent("click", function (e) {
                        dlg.close();
                    }.bind(this));
                    if (okAction) okAction.addEvent("click", function (e) {
                        if (okCallback) okCallback(this.statement.getData());
                        dlg.close();
                    }.bind(this));
                }

                MWF.xDesktop.requireApp("query.Query", "Statement", function () {
                    this.statement = new MWF.xApplication.query.Query.Statement(dlg.content.getFirst(), statementJson, statementOptions, _form.app, _form.Macro);
                    if(loadedCallback)loadedCallback(this.statement);
                }.bind(this));
            }.bind(this));
    };

    this.statement = {
        execute: function (obj, callback, async) {
            if( obj.format ){
                return this._execute(obj, callback, async, obj.format);
            }else{
                if( this.needCheckFormat(obj) ){
                    var result;
                    var p = MWF.Actions.load("x_query_assemble_surface").StatementAction.getFormat(obj.name, function(json){
                        result = this._execute(obj, callback, async, json.data.format);
                        return result;
                    }.bind(this), null, async);
                    return result || p;
                }else{
                    return this._execute(obj, callback, async, "");
                }

            }
        },
        needCheckFormat: function(s){
            if( s.format )return false;
            if( typeOf(s.parameter) === "object" ){
                for( var p in s.parameter ){
                    if( typeOf( s.parameter[p] ) === "date" )return true;
                }
            }
            if( typeOf(s.filter) === "array" ){
                for( var i=0; i< s.filter.length; i++){
                    var fType = s.filter[i].formatType;
                    if( ["dateTimeValue", "datetimeValue", "dateValue", "timeValue"].contains( fType ) )return true;
                }
            }
            return false;
        },
        _execute: function(statement, callback, async, format){
            var parameter = this.parseParameter(statement.parameter, format);
            var filterList = this.parseFilter(statement.filter, parameter, format);
            var obj = {
                "filterList": filterList,
                "parameter" : parameter
            };
            return MWF.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
                statement.name, statement.mode || "data", statement.page || 1, statement.pageSize || 20, obj,
                function (json) {
                    if (callback) callback(json);
                    return json;
                }, null, async);
        },
        parseFilter : function( filter, parameter , format){
            if( typeOf(filter) !== "array" )return [];
            if( !parameter )parameter = {};
            var filterList = [];
            ( filter || [] ).each( function (d) {
                if( !d.logic )d.logic = "and";

                //var parameterName = d.path.replace(/\./g, "_");
                var pName = d.path.replace(/\./g, "_");

                var parameterName = pName;
                var suffix = 1;
                while( parameter[parameterName] ){
                    parameterName = pName + "_" + suffix;
                    suffix++;
                }

                var value = d.value;
                if( d.comparison === "like" || d.comparison === "notLike" ){
                    if( value.substr(0, 1) !== "%" )value = "%"+value;
                    if( value.substr(value.length-1,1) !== "%" )value = value+"%";
                    parameter[ parameterName ] = value; //"%"+value+"%";
                }else{
                     if( ["sql", "sqlScript"].contains(format) ) {
                            if (d.formatType === "numberValue") {
                                value = parseFloat(value);
                            }
                        }else{
                            if (d.formatType === "dateTimeValue" || d.formatType === "datetimeValue") {
                                value = "{ts '" + value + "'}"
                            } else if (d.formatType === "dateValue") {
                                value = "{d '" + value + "'}"
                            } else if (d.formatType === "timeValue") {
                                value = "{t '" + value + "'}"
                            } else if (d.formatType === "numberValue") {
                                value = parseFloat(value);
                            }
                        }
                    parameter[ parameterName ] = value;
                }
                d.value = parameterName;

                filterList.push( d );
            }.bind(this));
            return filterList;
        },
        parseParameter : function( obj, format ){
                if( typeOf(obj) !== "object" )return {};
                var parameter = {};
                //전달된 매개변수
                for( var p in obj ){
                    var value = obj[p];
                    if( typeOf( value ) === "date" ){
                        if( ["sql", "sqlScript"].contains(format) ){
                            value = value.format("db");
                        }else{
                            value = "{ts '"+value.format("db")+"'}"
                        }
                    }
                    parameter[ p ] = value;
                }
                return parameter;
            },
        "select": function(statement, okCallback, dialogOptions, statementOptions, loadedCallback){
            if( statement.name || statement.statementName || statement.statementId || statement.statement ) {
                var statementJson = {
                    "application": statement.application || _form.json.application,
                    "statementName": statement.statementName || statement.name || statement.statement || "",
                    "isTitle": typeOf(statement.isTitle) === 'string' ? statement.isTitle : ((statement.isTitle === false) ? "no" : "yes"),
                    "select": typeOf(statement.select) === 'string' ? statement.select : ((statement.isMulti === false) ? "single" : "multi"),
                     "filter": statement.filter,
                     "parameter": statement.parameter
                };
                if (statement.name)statementJson.statementId = statement.statementId;
                if (statement.statementId) statementJson.statementId = statement.statementId;
                // if (statement.hasOwnProperty('titleStyles')) statementJson.titleStyles = statement.titleStyles;
                // if (statement.hasOwnProperty('itemStyles')) statementJson.itemStyles = statement.itemStyles;
                // if (statement.hasOwnProperty('isExpand')) statementJson.isExpand = statement.isExpand;
                // if (statement.hasOwnProperty('showActionbar')) statementJson.showActionbar = statement.showActionbar;
                // if (statement.hasOwnProperty('defaultSelectedScript')) statementJson.defaultSelectedScript = statement.defaultSelectedScript;
                // if (statement.hasOwnProperty('selectedAbleScript')) statementJson.selectedAbleScript = statement.selectedAbleScript;
                for (var key in statement) {
                    if (!statementJson.hasOwnProperty(key)) {
                        statementJson[key] = statement[key];
                    }
                }
                //if (layout.mobile && o2.version.dev === 10) {
                var styleType = _form.json?.formStyleType || _form.viewJson?.viewStyleType;
                if (layout.mobile && styleType === 'v10') {
                    selectStatementMobile(statementJson, okCallback, dialogOptions, statementOptions, loadedCallback);
                } else {
                    selectStatementPc(statementJson, okCallback, dialogOptions, statementOptions, loadedCallback);
                }
            }
        }
    };

    this.importer = {
        "upload": function (options, callback, async) {
            MWF.xDesktop.requireApp("query.Query", "Importer", function () {
                var importer = new MWF.xApplication.query.Query.Importer(_form.app.content, options, {}, _form.app, _form.Macro);
                importer.addEvent("afterImport", function (data) {
                    if(callback)callback(data);
                });
                importer.load();
            }.bind(this));
        },
        "downloadTemplate": function(options, fileName, callback){
            MWF.xDesktop.requireApp("query.Query", "Importer", function () {
                var importer = new MWF.xApplication.query.Query.Importer(_form.app.content, options, {}, _form.app, _form.Macro);
                importer.downloadTemplate(fileName, callback);
            }.bind(this));
        }
    };


    //include 스크립트 참조
    //optionsOrName : {
    //  type : "", 기본값 process, portal  process  cms 가능
    //  application : "", 포털/프로세스/CMS의 이름/별칭/id, 기본값은 현재 앱
    //  name : "" // 스크립트 이름/별칭/id
    //}
    //또는 name: "" // 스크립트 이름/별칭/id
    // if( !window.includedScripts ){
    //     var includedScripts = window.includedScripts = [];
    // }else{
    //     var includedScripts = window.includedScripts;
    // }

    /**
     * 프로세스, 포털, 콘텐츠 관리 또는 서비스 관리에서 스크립트 설정을 만들 수 있습니다.</br>
     * 기본적으로 시스템은 javascript 형식으로 편집합니다.
     * 이때 <code class='language-js'>this.include()</code>로 스크립트 설정을 참조할 수 있습니다.</br>
     * v9.2 이후 스크립트 설정 이름이 .json, .html 또는 .css로 끝나면 시스템이 해당 편집기를 로드합니다.
     * 이 경우 다음 방법으로 내용을 조회할 수 있습니다.
     * <pre><code class='language-js'>this.includeHmtl();
     * this.includeCss();
     * this.includeJson();
     * </code></pre>
     * @module include
     * @o2cn 스크립트 설정 관련 api
     * @o2category web
     * @o2ordernumber 140
     */

    /**
     * this.include는 메서드로, 프로세스, 포털, 콘텐츠 관리 또는 서비스 관리에서 스크립트 설정을 만들었을 때 this.include()로 스크립트 설정을 참조할 수 있습니다.<br/>
     * v8.0 이후 버전에 서비스 관리의 스크립트 설정이 추가되었습니다.<br/>
     * <b>(양식의 사전 로드 스크립트 사용을 권장하며, 로드 여부 판단이 필요할 때만 이 메서드로 스크립트를 로드하세요. 이때 비동기 로드가 양식 로드 속도에 도움이 됩니다.)</b><br/>
     * @method include
     * @methodOf module:include
     * @param {(String|Object|String[]|Object[])} optionsOrName 스크립트 식별 문자열(배열) 또는 객체(배열).
     * <pre><code class='language-js'>
     * //이 앱의 스크립트 설정을 참조하려면 options를 String 또는 String Array로 설정.
     * this.include("initScript") //스크립트 설정의 이름, 별칭 또는 id
     * this.include(["initScript","initScript2"]) //문자열 배열 가능
     *
     * //다른 앱의 스크립트 설정을 참조하려면 options를 Object 또는 Object Array로 설정;
     * this.include({
     *       //type: 앱 유형. portal  process  cms  service 가능.
     *       //이 옵션이 없거나 값이 빈 문자열이면 참조하는 스크립트와 참조되는 스크립트 설정의 유형이 같음을 의미함.
     *       //예를 들어 포털 A 앱 스크립트에서 포털 B 앱의 스크립트 설정을 참조하면 type은 생략 가능.
     *       type : "portal",
     *       application : "홈", // 포털, 프로세스, CMS의 이름, 별칭, id. 기본값은 현재 앱, 스크립트가 서비스 관리에 있으면 이 매개변수는 무시
     *       name : "initScript" // 스크립트 설정의 이름, 별칭 또는 id
     * })
     * this.include([  //객체와 문자열 혼합 배열도 가능
     *  {
     *       type : "portal",
     *       application : "홈",
     *       name : "initScript"
     *  },
     *  "initScript2"
     * ])
     *
     * //서비스 관리의 스크립트 참조
     * this.include({
     *   "type": "service",
     *   "name": "scriptName"
     * });
     *
     * //프로세스 관리의 스크립트 참조
     * this.include({
     *   "type": "process",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     *
     * //콘텐츠 관리의 스크립트 참조
     * this.include({
     *   "type": "cms",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     *
     * //포털 관리의 스크립트 참조
     * this.include({
     *   "type": "portal",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     * </code></pre>
     * @param {Function} [callback] 로드 후 실행할 콜백 메서드
     * @param {Boolean} [async] 비동기 로드 여부
     * @o2syntax
     * //양식, 프로세스, 뷰, 쿼리 뷰의 각 내장 스크립트에서 this.include()로 이 앱이나 다른 앱의 스크립트 설정을 참조할 수 있습니다. 예:
     * this.include( optionsOrName, callback, async )
     * @example
     * <caption>
     *    <b>예제 1:</b>공통 스크립트에 현재 사용자 이름을 반환하는 메서드를 정의하고, 각 포털 앱에서 이 메서드로 사용자 이름을 표시합니다.<br/>
     *     1. 포털 앱에 commonApp 앱이 있고, 이 앱에 initScript라는 스크립트를 만들어 메서드를 정의합니다.
     *     <img src='img/module/include/define1.png' />
     * </caption>
     * //메서드 정의
     * this.define("getUserName", function(){
     *   return ( layout.desktop.session.user || layout.user ).name
     * }.bind(this))
     * @example
     * <caption>
     *      2. 포털 페이지에 'queryLoad' 이벤트를 추가하고 이벤트에서 initScript 스크립트 설정을 불러옵니다.
     *     <img src='img/module/include/define2.png' style='max-width:700px;'/>
     * </caption>
     * this.include({
     *      type : "portal",
     *      application : "commonApp",
     *      name : "initScript"
     * })
     *
     * @example
     * <caption>
     *  3. 포털 페이지의 'load' 이벤트에서 메서드를 사용합니다.<br/>
     *     <img src='img/module/include/define3.png' style='max-width:700px;'/>
     * </caption>
     * var userNameNode = this.page.get("userName").node; //Dom 객체 조회
     * var urerName = this.getUserName(); //initScript 스크립트의 메서드 사용
     * userNameNode.set("text", urerName ); //DOM 객체에 값 설정
     */

    /**
     * 스크립트 설정에 저장된 것이 html 파일(이름이 .html로 끝남)일 때, this.includeHtml로 html 내용을 가져올 수 있습니다.
     * @method includeHtml
     * @since v9.2
     * @methodOf module:include
     * @param {(String|Object)} optionsOrName 스크립트 식별 문자열 또는 객체.
     * <pre><code class='language-js'>
     * //현재 앱의 html(스크립트 설정 내)을 가져오려면 options를 String으로 설정합니다.
     * this.includeHtml("test.html") //스크립트 설정의 이름, 별칭 또는 id
     *
     * //다른 앱의 html을 가져오려면 options를 Object로 설정합니다;
     * this.includeHtml({
     *       //type: 앱 유형. portal  process  cms  service 가능.
     *       //이 옵션이 없거나 값이 빈 문자열이면 참조하는 스크립트와 참조되는 스크립트 설정의 유형이 같음을 의미함.
     *       //예를 들어 포털 A 앱 스크립트에서 포털 B 앱의 스크립트 설정을 참조하면 type은 생략 가능.
     *       type : "portal",
     *       application : "홈", // 포털, 프로세스, CMS의 이름, 별칭, id. 기본값은 현재 앱, 스크립트가 서비스 관리에 있으면 이 매개변수는 무시
     *       name : "test.html" // 스크립트 설정의 이름, 별칭 또는 id
     * })
     *
     * //서비스 관리의 html 가져오기
     * this.includeHtml({
     *   "type": "service",
     *   "name": "scriptName"
     * });
     *
     * //프로세스 관리의 html 가져오기
     * this.includeHtml({
     *   "type": "process",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     *
     * //콘텐츠 관리의 html 가져오기
     * this.includeHtml({
     *   "type": "cms",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     *
     * //포털 관리의 html 가져오기
     * this.includeHtml({
     *   "type": "portal",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     * </code></pre>
     * @param {Function} [callback] 로드 후 실행할 콜백 메서드
     * @param {Boolean} [async] 비동기 로드 여부, 기본값 true
     * @o2syntax
     * this.includeHtml( optionsOrName, callback, async )
     * @example
     * //promise로 html 가져오기
     * //현재 앱의 스크립트 라이브러리에 html을 설정, 이름은 test.html
     * var promise = this.includeHtml("test.html");
     * promise.then(function(html){
     *      //양식에 식별자가 div인 컨테이너가 있다고 가정
     *      var div = this.form.get('div').node;
     *      //html을 div 노드에 렌더링, api o2.loadHtmlText 참고
     *     o2.loadHtmlText(html, {dom: div})
     * }.bind(this))
     * @example
     * //콜백에서 html 가져오기
     * //현재 앱의 스크립트 라이브러리에 html을 설정, 이름은 test.html
     * this.includeHtml("test.html", function(html){
     *      //양식에 식별자가 div인 컨테이너가 있다고 가정
     *      var div = this.form.get('div').node;
     *      //html을 div 노드에 렌더링, api o2.loadHtmlText 참고
     *      o2.loadHtmlText(html, {dom: div})
     * }.bind(this))
     * @example
     * //동기로 html 가져오기(권장하지 않음)
     * //포털의 스크립트 라이브러리에 html을 설정, 이름은 test.html
     * var html = this.includeHtml({
     *   "type": "portal",
     *   "application": "appName", //포털 식별자
     *   "name": "test.html"
     * }, null, false);
     * var div = this.form.get('div').node; //양식/페이지에 식별자가 div인 컨테이너가 있다고 가정
     * div.set('html', html); //html을 div 노드에 렌더링
     */


    /**
     * 스크립트 설정에 저장된 것이 css 파일(이름이 .css로 끝남)일 때, this.includeCss로 css 내용을 가져올 수 있습니다.
     * @method includeCss
     * @since v9.2
     * @methodOf module:include
     * @param {(String|Object)} optionsOrName 스크립트 식별 문자열 또는 객체.
     * <pre><code class='language-js'>
     * //현재 앱의 css(스크립트 설정 내)를 가져오려면 options를 String으로 설정합니다.
     * this.includeCss("test.css") //스크립트 설정의 이름, 별칭 또는 id
     *
     * //다른 앱의 html을 가져오려면 options를 Object로 설정합니다;
     * this.includeCss({
     *       //type: 앱 유형. portal  process  cms  service 가능.
     *       //이 옵션이 없거나 값이 빈 문자열이면 참조하는 스크립트와 참조되는 스크립트 설정의 유형이 같음을 의미함.
     *       //예를 들어 포털 A 앱 스크립트에서 포털 B 앱의 스크립트 설정을 참조하면 type은 생략 가능.
     *       type : "portal",
     *       application : "홈", // 포털, 프로세스, CMS의 이름, 별칭, id. 기본값은 현재 앱, 스크립트가 서비스 관리에 있으면 이 매개변수는 무시
     *       name : "initScript" // 스크립트 설정의 이름, 별칭 또는 id
     * })
     *
     * //서비스 관리의 html 가져오기
     * this.includeCss({
     *   "type": "service",
     *   "name": "scriptName"
     * });
     *
     * //프로세스 관리의 html 가져오기
     * this.includeCss({
     *   "type": "process",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     *
     * //콘텐츠 관리의 html 가져오기
     * this.includeCss({
     *   "type": "cms",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     *
     * //포털 관리의 html 가져오기
     * this.includeCss({
     *   "type": "portal",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     * </code></pre>
     * @param {Function} [callback] 로드 후 실행할 콜백 메서드
     * @param {Boolean} [async] 비동기 로드 여부, 기본값 true
     * @o2syntax
     * this.includeCss( optionsOrName, callback, async )
     * @example
     * //promise로 css 텍스트 가져오기
     * //현재 앱의 스크립트 라이브러리에 css를 설정, 이름은 test.css
     * var promise = this.includeCss("test.css");
     * promise.then(function(cssText){
     *      //css를 양식/페이지에 적용, 자세한 내용은 api: o2.loadCssText 참고
     *     var node = this.form.getApp().content;
     *     o2.loadCssText(cssText, {dom: node})
     * }.bind(this))
     * @example
     * //콜백에서 css 텍스트 가져오기
     * //현재 앱의 스크립트 라이브러리에 css를 설정, 이름은 test.css
     * this.includeCss("test.css", function(cssText){
     *      o2.loadCssText(cssText, {dom: document.body}) //css를 전역으로 렌더링
     * }.bind(this))
     * @example
     * //동기로 css 텍스트 가져오기(권장하지 않음)
     * //포털의 스크립트 라이브러리에 css를 설정, 이름은 test.css
     * var cssText = this.includeCss({
     *   "type": "portal",
     *   "application": "appName", //포털 식별자
     *   "name": "test.css"
     * }, null, false);
     * var node = this.form.getApp().content;
     * o2.loadCssText(cssText, {dom: node})
     */

    /**
     * 스크립트 설정에 저장된 것이 json 파일(이름이 .json으로 끝남)일 때, this.includeJson으로 json 내용을 가져올 수 있습니다.
     * @method includeJson
     * @since v9.2
     * @methodOf module:include
     * @param {(String|Object)} optionsOrName 스크립트 식별 문자열 또는 객체.
     * <pre><code class='language-js'>
     * //현재 앱의 json(스크립트 설정 내)을 가져오려면 options를 String으로 설정합니다.
     * this.includeJson("test.json") //스크립트 설정의 이름, 별칭 또는 id
     *
     * //다른 앱의 html을 가져오려면 options를 Object로 설정합니다;
     * this.includeJson({
     *       //type: 앱 유형. portal  process  cms  service 가능.
     *       //이 옵션이 없거나 값이 빈 문자열이면 참조하는 스크립트와 참조되는 스크립트 설정의 유형이 같음을 의미함.
     *       //예를 들어 포털 A 앱 스크립트에서 포털 B 앱의 스크립트 설정을 참조하면 type은 생략 가능.
     *       type : "portal",
     *       application : "홈", // 포털, 프로세스, CMS의 이름, 별칭, id. 기본값은 현재 앱, 스크립트가 서비스 관리에 있으면 이 매개변수는 무시
     *       name : "initScript" // 스크립트 설정의 이름, 별칭 또는 id
     * })
     *
     * //서비스 관리의 json 객체 가져오기
     * this.includeJson({
     *   "type": "service",
     *   "name": "scriptName"
     * });
     *
     * //프로세스 관리의 json 객체 가져오기
     * this.includeJson({
     *   "type": "process",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     *
     * //콘텐츠 관리의 json 객체 가져오기
     * this.includeJson({
     *   "type": "cms",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     *
     * //포털 관리의 json 객체 가져오기
     * this.includeJson({
     *   "type": "portal",
     *   "application": "appName",
     *   "name": "scriptName"
     * });
     * </code></pre>
     * @param {Function} [callback] 로드 후 실행할 콜백 메서드
     * @param {Boolean} [async] 비동기 로드 여부, 기본값 true
     * @o2syntax
     * this.includeJson( optionsOrName, callback, async )
     * @example
     * //promise로 json 데이터 가져오기
     * //현재 앱의 스크립트 라이브러리에 json을 설정, 이름은 test.json
     * var promise = this.includeJson("test.json");
     * promise.then(function(json){
     *      //json은 가져온 데이터
     * }.bind(this))
     * @example
     * //콜백에서 json 데이터 가져오기
     * //현재 앱의 스크립트 라이브러리에 json을 설정, 이름은 test.json
     * this.includeJson("test.json", function(json){
     *      //json은 가져온 데이터
     * }.bind(this))
     * @example
     * //동기로 json 데이터 가져오기(권장하지 않음)
     * //포털의 스크립트 라이브러리에 css를 설정, 이름은 test.json
     * var json = this.includeJson({
     *   "type": "portal",
     *   "application": "appName", //포털 식별자
     *   "name": "test.json"
     * }, null, false);
     */

    var _getScriptAction = function ( type ){
        var scriptAction;
        switch (type) {
            case "portal" :
                if (this.scriptActionPortal) {
                    scriptAction = this.scriptActionPortal;
                } else {
                    MWF.require("MWF.xScript.Actions.PortalScriptActions", null, false);
                    scriptAction = this.scriptActionPortal = new MWF.xScript.Actions.PortalScriptActions();
                }
                break;
            case "process" :
                if (this.scriptActionProcess) {
                    scriptAction = this.scriptActionProcess;
                } else {
                    MWF.require("MWF.xScript.Actions.ScriptActions", null, false);
                    scriptAction = this.scriptActionProcess = new MWF.xScript.Actions.ScriptActions();
                }
                break;
            case "cms" :
                if (this.scriptActionCMS) {
                    scriptAction = this.scriptActionCMS;
                } else {
                    MWF.require("MWF.xScript.Actions.CMSScriptActions", null, false);
                    scriptAction = this.scriptActionCMS = new MWF.xScript.Actions.CMSScriptActions();
                }
                break;
            case "service" :
                if (this.scriptActionService) {
                    scriptAction = this.scriptActionService;
                } else {
                    MWF.require("MWF.xScript.Actions.ServiceScriptActions", null, false);
                    scriptAction = this.scriptActionService = new MWF.xScript.Actions.ServiceScriptActions();
                }
                break;
        }
        return scriptAction;
    }


    //캐시 이름, 별칭, id
    var _parseScriptImportList = function (json, type){
        var includedScripts = [];
        var importedList = json.data.importedList || [];
        importedList.each(function (flag) {
            if (type === "portal") {
                includedScripts.push(type + "-" + json.data.portal + "-" + flag);
                if (json.data.portalName) includedScripts.push(type + "-" + json.data.portalName + "-" + flag);
                if (json.data.portalAlias) includedScripts.push(type + "-" + json.data.portalAlias + "-" + flag);
            } else if (type === "cms") {
                includedScripts.push(type + "-" + json.data.appId + "-" + flag);
                if (json.data.appName) includedScripts.push(type + "-" + json.data.appName + "-" + flag);
                if (json.data.appAlias) includedScripts.push(type + "-" + json.data.appAlias + "-" + flag);
            } else if (type === "process") {
                includedScripts.push(type + "-" + json.data.application + "-" + flag);
                if (json.data.appName) includedScripts.push(type + "-" + json.data.appName + "-" + flag);
                if (json.data.appAlias) includedScripts.push(type + "-" + json.data.appAlias + "-" + flag);
            }else if (type === "service") {
                includedScripts.push(type + "-" + flag);
            }
        });
        return includedScripts.concat(importedList);
    }

    var includedScripts = [];
    var _includeSingle = function( optionsOrName , callback, async){
        var options = optionsOrName;
        if( typeOf( options ) === "string" ){
            options = { name : options };
        }
        var name = options.name;
        var type;
        if( options.type === "service" ){
            type = options.type;
        }else{
            type = ( options.type && options.application ) ?  options.type : "process";
        }
        var application = options.application || _form.json.application;
        var key = type +"-" + application + "-"  + name;
        if( type === "service" ){
            key = type + "-" + name;
        }

        //js는 이미 로드했으면 다시 로드하지 않음
        if (includedScripts.indexOf( key )> -1){
            if (callback) callback.apply(this);
            return;
        }

        var successCallback = function (json) {
            if (json.data) {
                includedScripts.push(key);
                includedScripts = includedScripts.concat( _parseScriptImportList(json, type) );
                if( (options.enableAnonymous || options.anonymous ) && type === "cms" ){
                    MWF.CMSMacro.exec(json.data.text, this)
                }else{
                    MWF.Macro.exec(json.data.text, this);
                }
                if (callback) callback.apply(this);
            } else {
                if (callback) callback.apply(this);
            }
        }.bind(this);

        if (( options.enableAnonymous || options.anonymous ) && type === "cms") {
            o2.Actions.load("x_cms_assemble_control").ScriptAnonymousAction.getWithAppWithName(application, name, successCallback, null, !!async);
        } else {
            var scriptAction = _getScriptAction.call(this, type);
            if( type === "service" ){
                scriptAction.getScriptByName(name, includedScripts, successCallback, null, !!async);
            }else{
                scriptAction.getScriptByName(application, name, includedScripts, successCallback, null, !!async);
            }
        }
    };
    this.include = function( optionsOrName , callback, async){
        if (o2.typeOf(optionsOrName)==="array"){
            if (!!async){
                var count = optionsOrName.length;
                var loaded = 0;
                optionsOrName.each(function(option){
                    _includeSingle.apply(this, [option, function(){
                        loaded++;
                        if (loaded>=count) if (callback) callback.apply(this);
                    }.bind(this), true]);
                }.bind(this));

            }else{
                optionsOrName.each(function(option){
                    _includeSingle.apply(this, [option]);
                }.bind(this));
                if (callback) callback.apply(this);
            }
        }else{
            _includeSingle.apply(this, [optionsOrName , callback, async])
        }
    };

    var includedSourceMap = {};
    var _includeSource = function (optionsOrName, callback, async, fileType) {
        var options = typeOf(optionsOrName) === "string" ? {name: optionsOrName} : optionsOrName;
        var name = options.name;
        var type = options.type === "service" ? options.type : ((options.type && options.application) ? options.type : "portal");
        var application = options.application || _form.json.application;
        var key = type === "service" ? (type + "-" + name) : (type + "-" + application + "-" + name);
        var data, result;
        if( includedSourceMap[key] ){
            data = includedSourceMap[key];
            if(callback)callback( data.text );
            return !!async ? Promise.resolve( data.text ) : data.text;
        }
        var successCallback = function (json) {
            if (json.data) {
                var includeds = [key];
                includeds = includeds.concat( _parseScriptImportList(json, type) );
                includeds.each(function(k){
                    includedSourceMap[k] = json.data;
                })
                result = json.data.text;
                if( fileType === 'json' ){
                    result = JSON.parse(result);
                }
                if (callback) callback.call(this, result);
            } else {
                result = '';
                if (callback) callback.call(this, '');
            }
            return result;
        }.bind(this);
        var p;
        if (( options.enableAnonymous || options.anonymous ) && type === "cms") {
            p = o2.Actions.load("x_cms_assemble_control").ScriptAnonymousAction.getWithAppWithName(application, name, !!async ? null : successCallback, null, !!async);
        } else {
            var scriptAction = _getScriptAction.call(this, type);
            if( type === "service" ){
                p = scriptAction.getScriptByName(name, includedScripts, successCallback, !!async ? null : successCallback, null, !!async);
            }else{
                p = scriptAction.getScriptByName(application, name, includedScripts, !!async ? null : successCallback, null, !!async);
            }
        }
        return !!async ? p.then( successCallback ) : result;
    };
    this.includeHtml = function (optionsOrName, callback, async){
        return _includeSource.apply(this, [optionsOrName, callback, async!==false, 'html'])
    };
    this.includeJson = function (optionsOrName, callback, async){
        return _includeSource.apply(this, [optionsOrName, callback, async!==false, 'json'])
    };
    this.includeCss = function (optionsOrName, callback, async){
        return _includeSource.apply(this, [optionsOrName, callback, async!==false, 'css']);
    };

    /**
     * this.define은 메서드입니다. 프로세스, 포털 또는 콘텐츠 관리에서 스크립트 설정을 만들고, 스크립트 설정에서 this.define()으로 자신만의 메서드를 정의할 수 있습니다.<br/>
     * 이 방식으로 메서드를 정의하면 서로 다른 앱에서 같은 메서드 이름을 사용해도 충돌하지 않습니다.
     * @module define
     * @o2cn 메서드 정의
     * @o2category web
     * @o2ordernumber 150
     * @param {(String)} name 정의할 메서드 이름.
     * @param {Function} fun  정의할 메서드
     * @param {Boolean} [overwrite] 정의한 메서드의 덮어쓰기 허용 여부. 기본값 true.
     * @o2syntax
     * this.define(name, fun, overwrite)
     * @example
     * <caption>
     *    <b>예시:</b> 공통 스크립트에 현재 사용자 이름을 반환하는 메서드를 정의하고, 각 포털 앱에서 이 메서드로 사용자 이름을 표시합니다.<br/>
     *     1. 포털 앱에 commonApp 앱이 있고, 이 앱에 initScript라는 스크립트를 만들어 메서드를 정의합니다.
     *     <img src='img/module/include/define1.png' />
     * </caption>
     * //메서드 정의
     * this.define("getUserName", function(){
     *   return ( layout.desktop.session.user || layout.user ).name
     * }.bind(this))
     * @example
     * <caption>
     *      2. 포털 페이지에 'queryLoad' 이벤트를 추가하고 이벤트에서 initScript 스크립트 설정을 불러옵니다.
     *     <img src='img/module/include/define2.png' style='max-width:700px;'/>
     * </caption>
     * this.include({
     *      type : "portal",
     *      application : "commonApp",
     *      name : "initScript"
     * })
     *
     * @example
     * <caption>
     *  3. 포털 페이지의 'load' 이벤트에서 메서드를 사용합니다.<br/>
     *     <img src='img/module/include/define3.png' style='max-width:700px;'/>
     * </caption>
     * var userNameNode = this.page.get("userName").node; //Dom 객체 조회
     * var urerName = this.getUserName(); //initScript 스크립트의 메서드 사용
     * userNameNode.set("text", urerName ); //DOM 객체에 값 설정
     */
    this.define = function(name, fun, overwrite){
        var over = true;
        if (overwrite===false) over = false;
        var o = {};
        o[name] = {"value": fun, "configurable": over};
        MWF.defineProperties(this, o);
    }.bind(this);


    /**
     * 코드 조각 실행, eval과 유사. Function으로 감싸서 실행.
     * @module exec()
     * @o2category server.common
     * @o2ordernumber 151
     * @o2cn 메서드 정의
     * @param text {String} 실행할 코드 조각
     * @param bind {Object} 코드 조각에서 this가 가리키는 대상
     * @param arg {Object} 전달 인자. 예: {x:1, y:'5'}, 코드 조각에서 x와 y 변수를 사용할 수 있음.
     * @param throwError {boolean} 코드 조각 실행 오류 시 오류를 던질지 여부
     * @returns {any} 실행한 코드 조각의 반환값
     */
    this.exec = function(text, bind, arg=null, throwError=true){
        const b = bind || globalThis;
        const p = (arg) ? Object.values(arg) : [];
        const k = (arg) ? Object.keys(arg).join(',') : '';
        try {
            return Function('return function('+k+'){' + text + '}')().apply(b, p);
        }catch(e){
            if (throwError) throw e;
            console.error(e);
            return '';
        }
    }

    //프런트엔드 이벤트에 비동기 호출이 있고, 비동기 호출이 끝난 뒤 페이지 로드를 계속하려면,
    //호출 전에 var resolve = this.wait(); 실행
    //비동기 호출이 끝난 뒤 resolve.cb(); 실행
    //현재는 양식의 queryload 이벤트만 이 방법을 지원함.

    /**
     * this.wait는 비동기 호출 시 페이지 로드를 처리하는 메서드입니다.<br/>
     * 사용 시나리오: 속도를 높이기 위해 외부 리소스(데이터 사전, 외부 JS, 콘텐츠 관리 문서 등)를 한꺼번에 로드한 뒤 양식을 로드해야 할 때.<br/>
     * this.wait는 this.goon과 함께 사용해야 합니다.<br/>
     * <b>현재 프로세스 양식의 queryload 이벤트에서만 이 메서드를 지원합니다.</b>
     * @module wait
     * @o2cn 양식 대기
     * @o2category web
     * @o2range {Process}
     * @o2syntax
     * var resolve = this.wait(); //양식 페이지 로드 중지
     *
     * if (resolve && resolve.cb){
     *      resolve.cb(); //resolve.cb() 메서드로 양식 로드 계속 진행
     * }else{
     *      //비동기가 발생하지 않았다면 resolve.cb 메서드는 존재하지 않음,
     *      //콜백에서 this.goon();을 사용해 양식 로드를 계속함
     *      this.goon();
     * }
     * @example
     * <caption>데이터 사전, 콘텐츠 관리 문서 데이터, 조건에 따라 가져오는 스크립트를 로드한 후 양식을 로드해야 합니다.</caption>
     *
     * var resolve = this.wait(); //this.wait()로 양식 로드가 콜백을 기다리게 함
     * var scriptLoaded = false; //스크립트 로드 완료 표시, 조건부 스크립트만 this.include() 사용 권장, 그 외에는 사전 로드 스크립트가 더 빠름.
     * var documentLoaded = false; //콘텐츠 관리 문서 로드 완료 표시
     * var dictLoaded = true; //데이터 사전 로드 완료 표시
     *
     * //모든 리소스가 로드되었는지 확인하고, 그렇다면 양식 로드 계속
     * var checkLoad = function(){
     *     if (scriptLoaded && documentLoaded && dictLoaded){ //모든 리소스 로드 완료
     *       if (resolve && resolve.cb){
     *            resolve.cb(); //resolve.cb() 메서드로 양식 로드 계속 진행
     *        }else{
     *            //비동기가 발생하지 않았다면 resolve.cb 메서드는 존재하지 않음,
     *            //콜백에서 this.goon();을 사용해 양식 로드를 계속함
     *            this.goon();
     *        }
     *      }
     * }.bind(this);
     *
     * //콘텐츠 관리 문서 로드 판단
     * if( this.data.documentId ){
     *      //콘텐츠 관리 문서 비동기 로드
     *      o2.Actions.get("x_cms_assemble_control").getDocument(this.data.documentId, function (json) {
     *          this.form.documentJson = json; //다른 곳에서 사용할 수 있도록 데이터를 this.form에 저장
     *          documentLoaded = true; //콘텐츠 관리 로드 완료 표시
     *          checkLoad(); //모든 리소스 로드 완료 여부 확인
     *      }.bind(this), null, true); //true는 비동기 로드 표시
     *  }else{
     *     documentLoaded = true; ////콘텐츠 관리 로드 완료 표시
     *     checkLoad(); //모든 리소스 로드 완료 여부 확인
     * }
     *
     * //스크립트 로드 판단
     * if( this.data.scriptName ){ //scriptName을 판단 조건으로 가정
     *      //스크립트 로드
     *     this.include( this.data.scriptName, function(){  //두 번째 인자는 비동기 로드 후 콜백
     *         scriptLoaded = true; //스크립트 로드 완료 표시
     *         checkLoad(); //모든 리소스 로드 완료 여부 확인
     *     }, true ); //세 번째 인자는 비동기 여부
     * }else{
     *      scriptLoaded = true; ////스크립트 로드 완료 표시
     *     checkLoad(); //모든 리소스 로드 완료 여부 확인
     * }
     *
     * //데이터 사전 bulletinDictionary의 category 데이터 로드
     * var dict = new Dict("bulletinDictionary");
     * dict.get("category", function(data){ //성공 콜백
     *          this.form.bulletinCategory = data; //다른 곳에서 사용할 수 있도록 데이터를 this.form에 저장
     *          dictLoaded = true; //데이터 사전 로드 완료 표시
     *          checkLoad(); //모든 리소스 로드 완료 여부 확인
     *    }.bind(this), function(xhr){ //오류 콜백
     *          dictLoaded = true; ////데이터 사전 로드 완료 표시
     *          checkLoad(); //모든 리소스 로드 완료 여부 확인
     *    }, true //비동기 실행
     * )
     */
    this.wait = function(){
        var _self = this;
        resolve = {"cb":  _self.goon.bind(_self)};
        var setResolve = function(callback){
            resolve.cb = callback;
        }.bind(this);
        this.target.event_resolve = setResolve;
        return resolve;
    }
    //this.wait와 함께 사용,
    //비동기가 없으면 resolve.cb 메서드가 없으므로,
    //콜백에서 this.goon();을 사용해 양식 로드를 계속함
    this.goon = function(){
        this.target.event_resolve = null;
    }


    /**
     * 서비스 인터페이스 호출.
     * @module exec()
     * @o2category server.common
     * @o2ordernumber 151
     * @o2cn 메서드 정의
     * @param name {String} 호출할 서비스 이름
     * @param data {Object} 호출할 서비스의 요청 데이터
     * @returns {promise} 호출 결과
     */
    this.invoke = function(name, data){
        return this.Actions.load('x_program_center').InvokeAction.execute(name, data);
    }

    //프런트엔드 객체 전용-----------------------------------------
    //form

    /**
     * form 객체는 프로세스 양식 또는 콘텐츠 관리 양식에서 사용할 수 있습니다.(프런트엔드 스크립트에서만 사용 가능).
     * @module form
     * @o2cn 프로세스 및 콘텐츠 관리 양식
     * @o2category web
     * @o2range {Process|CMS}
     * @o2ordernumber 40
     * @o2syntax
     * //프로세스 양식과 콘텐츠 관리의 프런트엔드 스크립트에서 다음과 같이 this로 form 객체를 가져올 수 있습니다:
     * var form = this.form;
     */
    this.page = this.form = {
        /**
         * 현재 양식의 기본 정보 조회.
         * @method getInfor
         * @static
         * @return {Object} 양식의 기본 정보.
         * <pre><code class='language-js'>{
         *    "id": "db3b2766-93a1-4058-b522-0edb922bd84f",   //양식 ID
         *    "name": "경비 신청 양식",                         //양식 이름
         *    "alias": "경비 신청 양식",                        //양식 별칭
         *    "description": "",                              //양식 설명
         *    "application": "1dc23336-6be6-402b-bed6-36e707a1dd17",  //앱 ID
         *    "lastUpdatePerson": "XX@huqi@P",                //최종 수정자
         *    "lastUpdateTime": "2018-09-30 22:46:30",        //최종 수정 시간
         *    "icon": "...",                                  //양식 아이콘
         * }</code></pre>
         * @o2syntax
         * var form = this.form.getInfor();
         */
        "getInfor": function(){return ev.formInfor;},

        "infor": ev.formInfor,

        /**
         * 현재 문서를 연 component 객체 조회. 플랫폼의 각 창 앱이 하나의 component 객체이며, 여기서 가져오는 객체는 x_component_process_Work입니다.
         * @method getApp
         * @static
         * @return {x_component_process_Work}현재 문서를 연 component 객체.
         * @o2syntax
         * var app = this.form.getApp();
         * @example
         * var app = this.form.getApp();
         //모든 component 객체에는 다음 메서드가 있습니다.
         app.openInNewWindow();  //현재 앱을 새 창에서 열기
         app.setCurrent();   //현재 앱을 활성 상태로 설정
         app.minSize();      //앱 창 최소화
         app.maxSize();      //앱 창 최대화
         app.restoreSize();  //앱 창 복원
         app.refresh();      //앱 새로 고침
         app.close();        //앱 닫기
         app.setTitle(str);  //앱 제목 설정
         app.dialog(option); //대화 상자 표시(MWF.widget.Dialog 참고)

         //알림 메시지 표시
         app.notice(content, type, target, where, offset);

         //확인 상자 표시
         app.confirm(type, e, title, text, width, height, ok, cancel);

         //정보 상자 표시
         app.alert(type, e, title, text, width, height);

         //앱에 이벤트 바인딩
         app.addEvent(type, fun);
         */
        "getApp": function(){return _form.app;},

        "app": _form.app,

        /**
         * Form에 대응하는 DOM 객체 조회.
         * @method node
         * @static
         * @return {HTMLDivElement} 현재 form에 대응하는 div 객체.
         * @o2syntax
         * var node = this.form.node();
         */
        "node": function(){return _form.node;},


        // "readonly": _form.options.readonly,

        /**
         * 양식 요소 객체 조회.<br/>
         * <table>
         *    <tr><td>Actionbar(작업 표시줄)</td><td>Address(주소 입력란)</td><td>Attachment(첨부파일란)</td><td>Button(버튼)</td></tr>
         *    <tr><td>Calendar(날짜 입력란)</td><td>Checkbox(다중 선택 버튼)</td><td>Combox(콤보 상자)</td><td>Datagrid(데이터 그리드)</td></tr>
         *    <tr><td>Div(컨테이너)</td><td>Htmleditor(서식 있는 텍스트 편집기)</td><td>Html(내장 html)</td><td>Iframe(삽입 Iframe)</td></tr>
         *    <tr><td>Image(이미지)</td><td>Label(텍스트)</td><td>Log(프로세스 의견)</td><td>Monitor(프로세스 모니터링)</td></tr>
         *    <tr><td>Number(숫자 입력란)</td><td>Office(office 컨트롤)</td><td>Opinion(의견란)</td><td>Org(사용자·조직 선택)</td></tr>
         *    <tr><td>Radio(단일 선택 버튼)</td><td>Select(선택 상자)</td><td>Sidebar(사이드 작업 표시줄)</td><td>Stat(통계 컴포넌트)</td></tr>
         *    <tr><td>Subform(하위 양식)</td><td>Tab(탭)</td><td>Table(표)</td><td>Textarea(여러 줄 입력)</td></tr>
         *    <tr><td>Textfield(텍스트 입력란)</td><td>Tree(트리 컨트롤)</td><td>View(뷰 컴포넌트)</td><td>ViewSelector(뷰 선택 컴포넌트)</td></tr>
         *    <tr><td>Documenteditor(공문 편집기)</td><td>ImageClipper(이미지 편집기)</td><td></td><td></td></tr>
         * </table>
         * @method get
         * @static
         * @return {FormComponent} 이 문서의 Classes 탐색에서 FormComponents를 참고하세요.
         * @param {String} name 필드 식별자
         * @param {String} [subformName] 하위 양식/부품 식별자. 개발자가 하위 양식을 삽입할 때 시스템은 입력형(백엔드에 값 저장) 컴포넌트의 이름 중복을 검사하며, 중복이면 삽입을 허용하지 않습니다.
         * 단, 레이아웃 컴포넌트(div 등)의 이름 중복은 허용됩니다. 시스템은 양식을 표시할 때 하위 양식의 컴포넌트 식별자가 사용 중인지 판단하고, 사용 중이면 컴포넌트 앞에 "하위 양식 식별자_"를 자동으로 붙입니다. 예: 주 양식에 "div_1"이 있으면 하위 양식 "subform1"의 "div_1" 컴포넌트 식별자는 "subform1_div_1"이 됩니다.
         * 이 인자는 이런 경우에 하위 양식의 컴포넌트를 올바르게 반환하는 데 사용됩니다.
         * @o2syntax
         * var field = this.form.get(name);
         * @o2syntax
         * var field = this.form.get(name, subformName);
         * @example
         * var field = this.form.get("subject");
         * @example
         * var field = this.form.get("div", "subform1"); //하위 양식 “subform1”의 div 조회, 하위 양식에 이 컴포넌트가 없고 주 양식에 있으면 주 양식의 컴포넌트 반환.
         */
        "get": function(name,subformName ){
            if( !_form.all )return null;
            if( subformName ){
                if( _form.all[subformName +"_"+ name] )return _form.all[subformName +"_"+ name];
                return _form.all[name];
            }else{
                return _form.all[name];
            }
            // return (_form.all) ? _form.all[name] : null;
        },

        /**
         * 양식에서 입력 가능한 필드 요소 객체 조회.<br/>
         * <table>
         *    <tr><td>Address(주소 입력란)</td><td>Attachment(첨부파일란)</td><td>Calendar(날짜 입력란)</td><td>Checkbox(다중 선택 버튼)</td></tr>
         *    <tr><td>Combox(콤보 상자)</td><td>Datagrid(데이터 그리드)</td><td>Htmleditor(서식 있는 텍스트 편집기)</td><td>Number(숫자 입력란)</td></tr>
         *    <tr><td>Org(사용자·조직 선택)</td><td>Radio(단일 선택 버튼)</td><td>Select(선택 상자)</td><td>Textarea(여러 줄 입력)</td></tr>
         *    <tr><td>Textfield(텍스트 입력란)</td><td></td><td></td><td></td></tr>
         * </table>
         * @method getField
         * @static
         * @return {FormComponent} 이 문서의 Classes 탐색에서 FormComponents를 참고하세요.
         * @param {String} name 필드 식별자
         * @o2syntax
         * var field = this.form.getField(name);
         * @example
         * var field = this.form.getField("subject");
         */
        "getField": function(name){return _forms[name];},

        "getAction": function(){return _form.workAction},
        "getDesktop": function(){return _form.app.desktop},

        /**업무 데이터 조회
         * @method getData
         * @static
         * @see module:data
         * @o2syntax
         *  var data = this.form.getData();
         * @return {Object} 양식에 바인딩된 업무 데이터 반환.
         */
        "getData": function(){return new MWF.xScript.JSONData(_form.getData());},

        /**현재 양식에 바인딩된 업무 데이터 저장.<br/>
         * this.form.save()는 beforeSave와 afterSave 이벤트를 발생시키므로 beforeSave와 afterSave에서는 이 메서드를 사용할 수 없습니다. 또한 queryLoad에서의 사용도 권장하지 않습니다.
         * @method save
         * @static
         * @param {Function} [callback] - 저장 후 콜백
         * @param {Boolean} [silent] - 무음 여부, 저장 성공 알림 표시 여부, 기본값 false
         * @o2syntax
         * this.form.save(callback, silent);
         * @example
         *  this.form.save(function(){
         *      //do someting
         *  }, true);
         */
        "save": function(callback, silent){_form.saveWork(callback, silent); },

        /**
         *현재 양식 닫기
         * @method close
         * @static
         * @example
         * this.form.close();
         */
        "close": function(){_form.closeWork();},

        /**
         *현재 결재 대기 보류
         * @method pauseTask
         * @static
         * @example
         * this.form.pauseTask();
         */
        "pauseTask": function(){_form.pauseTask();},

        /**
         *결재 대기를 보류 상태에서 정상 상태로 복원
         * @method resumeTask
         * @static
         * @example
         * this.form.resumeTask();
         */
        "resumeTask": function(){_form.resumeTask();},

        /**이 검증은 의견 검증, 경로 검증을 포함하지 않음. 보통 제출 화면을 띄울 때 검증에 사용
         * @summary 양식의 모든 컴포넌트 검증 설정과 「진행 검증」 스크립트로 검증.<b>(프로세스 양식에서만 사용 가능)</b>
         * @method verify
         * @static
         * @o2syntax
         * this.form.verify()
         *  @example
         *  if( !this.form.verify() ){
         *      return false;
         *  }
         *  @example
         *  this.form.verify().then(flag=>{
         *      //양식에 비동기 검증이 포함되어 있으면 이 방식으로 검증 결과를 얻음
         *  })
         *  @return {Boolean|Promise} 검증 통과 여부, 양식에 비동기 검증이 있으면 Promise 반환
         */
        "verify": function(){
            var customValidated = _form.formCustomValidation("", "");
            if( customValidated === false ){
                return false;
            }
            var formValidated = _form.formValidation("", "");
            if( formValidated === false ){
                return false;
            }

            var promiseList = [customValidated, formValidated].filter(result=>{
                return result instanceof Promise;
            })

            if(promiseList.length === 0){
                return true;
            }

            return Promise.all(promiseList).then(resultArr => {
                return resultArr.every(res => String(res) === "true");
            });

            //return !(!_form.formCustomValidation("", "") || !_form.formValidation("", ""));
        },

        /**
         * @summary 양식 내 모든 컴포넌트의 검증 설정과 양식의 “게시 검증” 스크립트로 검증.<b>(콘텐츠 관리 양식에서만 사용 가능)</b>
         * @method verifyPublish
         * @static
         * @o2syntax
         * this.form.verifyPublish()
         *  @example
         *  if( !this.form.verifyPublish() ){
         *      return false;
         *  }
         *  @example
         *  this.form.verifyPublish().then(flag=>{
         *      //양식에 비동기 검증이 포함되어 있으면 이 방식으로 검증 결과를 얻음
         *  })
         *  @return {Boolean|Pormise} 검증 통과 여부, 양식에 비동기 검증이 포함되어 있으면 Promise를 반환
         */
        "verifyPublish": function(isSave){
            const formValidated = _form.formValidation(isSave ? "" : "publish");
            if( formValidated === false ){
                return false;
            }

            const submitValidated = _form[isSave ? 'formSaveValidation' : 'formPublishValidation']();
            if( submitValidated === false ){
                return false;
            }

            var promiseList = [formValidated, submitValidated].filter(result=>{
                return result instanceof Promise;
            })

            if(promiseList.length === 0){
                return true;
            }

            return Promise.all(promiseList).then(resultArr => {
                return resultArr.every(res => String(res) === "true");
            });
            //return !(!_form.formValidation(isSave ? "" : "publish") || !_form[isSave ? 'formSaveValidation' : 'formPublishValidation']());
        },



        /**현재 양식이 연 프로세스 인스턴스를 진행시킴.<b>(프로세스 양식에서만 사용 가능)</b><br/>
         * this.workContext.getControl().allowProcessing으로 현재 사용자의 진행 권한 여부를 판단할 수 있습니다.<br/>
         * this.form.process()는 beforeSave, afterSave, beforeProcess, afterProcess 이벤트를 발생시키므로 위 이벤트에서는 이 메서드를 사용할 수 없습니다.
         * @method process
         * @static
         * @param {Object} [option] - 프로세스 관련 데이터, 이 인자가 없으면 경로 선택 및 의견 입력 상자 표시<br/>
         * 형식은 다음과 같습니다:
         <pre><code class="language-js">
         {
            "routeName": "", //다음 단계로 진행할 때 선택할 경로 이름
            "opinion": "", //진행 의견
            "callback": function(json){} //콜백 메서드, json 인자가 있으면 정상 진행이며 json은 진행 후 반환된 데이터.
      }
         </code></pre>
         * @example
         //인자 없이: 경로 선택 및 의견 입력 상자 표시
         this.form.process();
         * @example
         //인자 포함: 진행
         this.form.process({
            "routeName": "결재 요청",
            "opinion": "동의",
            "callback": function(json){
                if(json)this.form.notice("process success", "success");
            }.bind(this)
      });
         */
        "process": function(option){
            var op = _form.getOpinion();
            var mds = op.medias;
            if (option){
                _form.submitWork(option.routeName, option.opinion, mds, option.callback,
                    option.processor, null, option.appendTaskIdentityList, option.processorOrgList, option.callbackBeforeSave );
            }else{
                _form.processWork();
            }
        },

         /**현재 양식이 연 프로세스 인스턴스를 진행시킴. option 인자를 전달하면 process 메서드와 완전히 같고, option 인자가 없으면 새 계속 진행 화면 사용.<b>(프로세스 양식에서만 사용 가능)</b><br/>
         * this.workContext.getControl().allowProcessing으로 현재 사용자의 진행 권한 여부를 판단할 수 있습니다.<br/>
         * this.form.process()는 beforeSave, afterSave, beforeProcess, afterProcess 이벤트를 발생시키므로 위 이벤트에서는 이 메서드를 사용할 수 없습니다.
         * @method process
         * @static
         * @param {Object} [option] - 프로세스 관련 데이터, 이 인자가 없으면 경로 선택 및 의견 입력 상자 표시<br/>
         */
        "flowWork": function(option){
            var op = _form.getOpinion();
            var mds = op.medias;
            if (option){
                _form.submitWork(option.routeName, option.opinion, mds, option.callback,
                    option.processor, null, option.appendTaskIdentityList, option.processorOrgList, option.callbackBeforeSave );
            }else{
                _form.flowWork();
            }
        },

        /**현재 문서의 결재 대기 처리자를 재지정.<b>(프로세스 양식에서만 사용 가능)</b><br/>
         * this.workContext.getControl().allowReset으로 현재 사용자의 처리자 재지정 권한 여부를 판단할 수 있습니다.<br/>
         * @method reset
         * @static
         * @param {Object} [option] - 처리자 재지정 관련 인자, 이 인자가 없으면 처리자 재지정 대화 상자 표시<br/>
         * 형식은 다음과 같습니다:
         <pre><code class="language-js">
         {
            "names": "", //{Array|String} 재지정할 신분
            "opinion": "", //진행 의견
            "success": function(){}, //재지정 성공 후 콜백 메서드
            "failure": function(){} //재지정 실패 후 콜백 메서드
        }
         </code></pre>
         * @example
         //인자 없이: 처리자 재지정 대화 상자 표시, 사용자가 확인하면 beforeReset, afterReset 이벤트가 발생하므로 이 두 이벤트에서는 이 메서드를 사용할 수 없음.
         this.form.reset();
         * @example
         //인자 포함: 백엔드 서비스를 직접 호출해 재지정, beforeReset, afterReset 이벤트 발생 안 함
         this.form.reset({
            "names": ["홍길동@zhangsan@I"],
            "opinion": "위임 처리",
            "success": function(json){
                this.form.notice("reset success", "success");
            }.bind(this),
            "failure": function(xhr, text, error){
                //xhr--HttpRequest 요청 객체
                //text--HttpResponse 내용 텍스트
                //error--오류 정보
                this.form.notice("reset failure:"+error, "error");
            }.bind(this)
        });
         */
        "reset": function(option){
            if (!option){
                if (_form.businessData.control["allowReset"]) _form.resetWork();
            }else{
                _form.resetWorkToPeson(option.names, option.opinion, option.routeName || "", option.success, option.failure);
            }
        },

        /**문서 회수 작업, 이전 처리자가 이미 진행된 문서를 회수.<b>(프로세스 양식에서만 사용 가능)</b><br/>
         * 이 작업은 이전 처리자가 문서를 진행한 후 다음 처리자가 아직 처리하지 않았을 때만 실행할 수 있습니다.<br/>
         * this.workContext.getControl().allowRetract로 현재 사용자의 회수 권한 여부를 판단할 수 있습니다.<br/>
         * @method retract
         * @static
         * @param {Object} [option] - 회수 관련 인자, option 인자를 제공하지 않으면 회수 대화 상자 표시.<br/>
         * 형식은 다음과 같습니다:
         <pre><code class="language-js">
         {
            "success": function(){}, //회수 성공 후 콜백 메서드
            "failure": function(){} //회수 실패 후 콜백 메서드
        }
         </code></pre>
         * @example
         //인자 없이: 회수 대화 상자 표시, 사용자가 확인하면 beforeRetract, afterRetract 이벤트가 발생하므로 이 두 이벤트에서는 이 메서드를 사용할 수 없음.
         this.form.retract();
         * @example
         //인자 포함: 백엔드 서비스를 직접 호출해 회수, beforeRetract, afterRetract 이벤트 발생 안 함
         this.form.retract({
            "success": function(json){
                this.form.notice("retract success", "success");
            }.bind(this),
            "failure": function(xhr, text, error){
                //xhr--HttpRequest 요청 객체
                //text--HttpResponse 내용 텍스트
                //error--오류 정보
                this.form.notice("retract failure: "+error, "error");
            }.bind(this)
        });
         */
        "retract": function(option){
            if (!option){
                if (_form.businessData.control["allowRetract"]) _form.retractWork();
            }else{
                _form.doRetractWork(option.success, option.failure);
            }
        },

        /**분기된 업무 문서에 분기 추가.<b>(프로세스 양식에서만 사용 가능)</b><br/>
         * this.workContext.getControl().allowAddSplit으로 현재 사용자의 권한 여부를 판단할 수 있습니다.<br/>
         * @method addSplit
         * @static
         * @param {Object} [option] - 분기 추가 관련 인자, option 인자를 제공하지 않으면 분기 추가 대화 상자 표시.<br/>
         * 형식은 다음과 같습니다:
         <pre><code class="language-js">
         {
            "value" : [], //splitValueList 추가할 분기 값, 분기 값은 프로세스 분기 노드 설정에 따름
            "trimExist" : true, //이미 존재하는 분기 값 제외.
            "success": function(){}, //실행 성공 후 콜백 메서드
            "failure": function(){} //실행 실패 후 콜백 메서드
        }
         </code></pre>
         * @example
         //인자 없이: 분기 추가 대화 상자 표시, 사용자가 확인하면 beforeAddSplit, afterAddSplit 이벤트 발생
         this.form.addSplit();
         * @example
         //인자 포함: 백엔드 서비스를 직접 호출해 분기 추가, beforeAddSplit, afterAddSplit 이벤트 발생 안 함
         this.form.addSplit({
            "value" : ["개발부@kfb@U"],
            "trimExist" : true,
            "success": function(json){
                this.form.notice("addSplit success", "success");
            }.bind(this),
            "failure": function(xhr, text, error){
                //xhr--HttpRequest 요청 객체
                //text--HttpResponse 내용 텍스트
                //error--오류 정보
                this.form.notice("addSplit failure: "+error, "error");
            }.bind(this)
        });
         */
        "addSplit": function(option){
            if (!option){
                if (_form.businessData.control["allowAddSplit"]) _form.addSplit();
            }else{
                _form.addSplitWork(option.value, option.trimExist, option.success, option.failure);
            }
        },


        "rollback": function(option){
            if (!option){
                if (_form.businessData.control["allowRollback"]) _form.rollback();
            }else{
                _form.doRollbackActionInvoke(option.log, option.flow, option.success, option.failure);
            }
        },

        /**현재 업무 문서 삭제.<b>(프로세스 양식에서만 사용 가능)</b><br/>
         * this.workContext.getControl().allowDeleteWork로 현재 사용자의 문서 삭제 권한 여부를 판단할 수 있습니다.<br/>
         * @method deleteWork
         * @static
         * @param {Object} [option] - 삭제 관련 인자, option 인자를 제공하지 않으면 삭제 대화 상자 표시.<br/>
         * 형식은 다음과 같습니다:
         <pre><code class="language-js">
         {
            "success": function(){}, //실행 성공 후 콜백 메서드
            "failure": function(){} //실행 실패 후 콜백 메서드
        }
         </code></pre>
         * @example
         //인자 없이: 삭제 확인 대화 상자 표시, 사용자가 확인하면 beforeDelete와 afterDelete 이벤트 발생
         this.form.deleteWork();
         * @example
         //인자 포함: 서비스를 직접 호출해 삭제, beforeDelete와 afterDelete 이벤트 발생 안 함
         this.form.deleteWork({
            "success": function(json){
                this.form.notice("deleteWork success", "success");
            }.bind(this),
            "failure": function(xhr, text, error){
                //xhr--HttpRequest 요청 객체
                //text--HttpResponse 내용 텍스트
                //error--오류 정보
                this.form.notice("deleteWork failure: "+error, "error");
            }.bind(this)
        });
         */
        "deleteWork": function(option){
            if (!option){
                if (_form.businessData.control["allowDelete"]) _form.deleteWork();
            }else{
                _form.doDeleteWork(option.success, option.failure);
            }
        },


        /**현재 업무 문서에 열람 대기 발송.<b>(프로세스 양식에서만 사용 가능)</b><br/>
         * 업무 문서를 볼 수 있는 사람은 모두 발송 권한이 있습니다.<br/>
         * this.form.sendRead()는 beforeSendRead, afterSendRead를 발생시키므로 위 이벤트에서는 이 메서드를 사용할 수 없습니다.
         * @method sendRead
         * @static
         * @param {Object} [option] - 열람 대기 발송 관련 인자, 이 인자가 없으면 열람 대기 발송 대화 상자 표시<br/>
         * 형식은 다음과 같습니다:
         <pre><code class="language-js">
         {
            "identityList": "", //{Array|String} 열람 대기를 발송할 신분
            "notify": true, //열람 대기 알림 발송 여부(서버 메시지 활성화 필요)
            "success": function(){}, //성공 후 콜백 메서드
            "failure": function(){} //실패 후 콜백 메서드
        }
         </code></pre>
         * @example
         //인자 없이: 열람 대기 발송 대화 상자 표시, 확인 후 beforeSendRead, afterSendRead 이벤트 발생
         this.form.sendRead();
         * @example
         //인자 포함: 백엔드 서비스를 직접 호출해 열람 대기 발송, beforeSendRead, afterSendRead 이벤트 발생
         this.form.sendRead({
            "identityList": ["홍길동@zhangsan@I"],
            "notify": false,
            "success": function(json){
                this.form.notice("send read success", "success");
            }.bind(this),
            "failure": function(xhr, text, error){
                //xhr--HttpRequest 요청 객체
                //text--HttpResponse 내용 텍스트
                //error--오류 정보
                this.form.notice("send read failure:"+error, "error");
            }.bind(this)
        });
         */
        "sendRead": function(option){
            if (!option){
                _form.sendRead();
            }else{
                if( option.identityList && typeOf(option.identityList) === "string" ){
                    option.identityList = [option.identityList];
                }
                _form.doSendRead(option);
            }
        },

        /**현재 업무 문서에 열람자(참조) 추가.<b>(프로세스 양식에서만 사용 가능)</b><br/>
         * 업무 문서를 볼 수 있는 사람은 모두 추가 권한이 있습니다.<br/>
         * this.form.addReview()는 beforeAddReview, afterAddReview를 발생시키므로 위 이벤트에서는 이 메서드를 사용할 수 없습니다.
         * @method addReview
         * @static
         * @param {Object} [option] - 열람자 추가 관련 인자, 이 인자가 없으면 열람자 추가 대화 상자 표시<br/>
         * 형식은 다음과 같습니다:
         <pre><code class="language-js">
         {
            "personList": "", //{Array|String} 추가할 열람자
            "success": function(){}, //성공 후 콜백 메서드
            "failure": function(){} //실패 후 콜백 메서드
        }
         </code></pre>
         * @example
         //인자 없이: 열람자 추가 대화 상자 표시, beforeAddReview와 afterAddReview 이벤트 발생.
         this.form.addReview();
         * @example
         //인자 포함: 백엔드 서비스를 직접 호출해 열람 대기 발송, beforeAddReview와 afterAddReview 이벤트 발생.
         this.form.addReview({
            "personList": ["홍길동@zhangsan@P"],
            "success": function(json){
                this.form.notice("add review success", "success");
            }.bind(this),
            "failure": function(xhr, text, error){
                //xhr--HttpRequest 요청 객체
                //text--HttpResponse 내용 텍스트
                //error--오류 정보
                this.form.notice("add review failure:"+error, "error");
            }.bind(this)
        });
         */
        "addReview": function(option){
            if (!option){
                _form.addReview();
            }else{
                if( option.personList && typeOf(option.personList) === "string" ){
                    option.personList = [option.personList];
                }
                _form.doAddReview(option);
            }
        },

        /**현재 업무 문서를 IM 채팅 대화에 공유.<b>(프로세스 양식에서만 사용 가능)</b><br/>
         * @method shareToIMChat
         * @static
         *
         * @example
         //매개변수 없음
         this.form.shareToIMChat();
         */
        "shareToIMChat": function(){
            _form.shareToIMChat();
        },

        /**결재 대기자 추가, 결재자 추가 등에 사용.<b>(프로세스 양식에서만 사용 가능)</b><br/>
         * this.workContext.getControl().allowAddTask로 현재 사용자의 권한 여부를 판단할 수 있습니다.<br/>
         * @method addTask
         * @static
         * @param {Object} [option] - 결재 대기자 추가 관련 인자, option 인자를 제공하지 않으면 결재자 추가 대화 상자 표시.<br/>
         * 형식은 다음과 같습니다:
         * <pre><code class="language-js">
         * {
         *   "mode" : "single", //결재자 추가 모드:single,queue,parallel
         *   "before" : true, //사전 추가 여부, false는 사후 추가.
         *   "distinguishedNameList": [], //추가 결재자의 신분 배열.
         *   "routeName" : "", //추가한 결재 대기가 프로세스 기록에 표시될 경로.
         *   "opinion" : "", //결재자 추가 의견
         *   "success": function(){}, //실행 성공 후 콜백 메서드
         *   "failure": function(){} //실행 실패 후 콜백 메서드
         * }
         </code></pre>
         * @example
         * //인자 없이: 결재자 추가 대화 상자 표시
         * this.form.addTask();
         * @example
         * //인자 포함: 인자에 따라 결재 대기 추가 실행
         * this.form.addTask({
         *   "mode" : "single",
         *   "before": false,
         *   "distinguishedNameList": ["홍길동@XXXX@I", "김철수@XXXX@I"],
         *   "routeName" : "검토자 추가",
         *   "opinion" : "홍길동, 김철수 검토 바랍니다",
         *   "success": function(json){
         *       this.form.notice("addTask success", "success");
         *   }.bind(this),
         *   "failure": function(xhr, text, error){
         *       //xhr--HttpRequest 요청 객체
         *       //text--HttpResponse 내용 텍스트
         *       //error--오류 정보
         *       this.form.notice("addTask failure: "+error, "error");
         *   }.bind(this)
         * });
         */
        "addTask": function(option){
            if (option){
                (function(callback){
                    if (_form.businessData.control["allowSave"]){
                        _form.saveFormData(callback);
                    }else{
                        if (callback) callback();
                    }
                })(function(){
                    // if (!option.identity){
                    //     option.identity = (_form.businessData.task) && _form.businessData.task.identityDn;
                    // }
                    // var workId = _form.businessData.work.id;
                    // o2.Actions.load("x_processplatform_assemble_surface").WorkAction.V2AddManualTaskIdentityMatrix(workId, option, option.success, option.failure);

                    // var taskId = _form.businessData.task.id;
                    // o2.Actions.load("x_processplatform_assemble_surface").TaskAction.v3Add(taskId, option, option.success, option.failure);

                    _form.doAddTaskToPeople(option.distinguishedNameList, option.opinion, option.mode, option.before, option.routeName || "", option.success, option.failure)
                });
            }else{
                if (_form.businessData.control["allowAddTask"]) _form.addTask();
            }
        },


        /**확인 및 닫기 버튼이 있는 확인 상자 표시
         * @method confirm
         * @static
         * @param {String} type - 표시할 정보 유형. 가능한 값: success 성공, info :정보, error :오류, wran : 경고
         * @param {String} title - 확인 상자 제목 표시줄 텍스트.
         * @param {String|Object} text - 확인 상자 내용 텍스트. 값이 html인 경우 아래 예시 「html로 내용 전달」 참고.
         * @param {Number} width - 확인 상자 너비.
         * @param {String} height - 확인 상자 높이.
         * @param {Function} ok - 「확인」 버튼 클릭 후 콜백 함수.
         * @param {Function} cancel - 「취소」 버튼 클릭 후 콜백 함수.
         * @example
         *this.form.confirm("wran", "삭제 확인", "삭제하시겠습니까?", 300, 100,function(){
         *   //삭제 코드 실행
         *   this.close();
         *}, function(){
         *   this.close();
         *});
         * @example
         * //html로 내용 전달, v8.1부터 지원
         *this.form.confirm("wran", "삭제 확인", {
         *     html: "삭제하시겠습니까!<br/>"
         *}, 300, 100,function(){
         *   //삭제 코드 실행
         *   this.close();
         *}, function(){
         *   this.close();
         *});
         */
        "confirm": function(type, title, text, width, height, ok, cancel, callback, mask, style){
            if ((arguments.length<=1) || o2.typeOf(arguments[1])==="string"){
                var p = MWF.getCenter({"x": width, "y": height});
                e = {"event": {"clientX": p.x,"x": p.x,"clientY": p.y,"y": p.y}};
                _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
            }else{
                e = (arguments.length>1) ? arguments[1] : null;
                title = (arguments.length>2) ? arguments[2] : null;
                text = (arguments.length>3) ? arguments[3] : null;
                width = (arguments.length>4) ? arguments[4] : null;
                height = (arguments.length>5) ? arguments[5] : null;
                ok = (arguments.length>6) ? arguments[6] : null;
                cancel = (arguments.length>7) ? arguments[7] : null;
                callback = (arguments.length>8) ? arguments[8] : null;
                mask = (arguments.length>9) ? arguments[9] : null;
                style = (arguments.length>10) ? arguments[10] : null;

                // var p = MWF.getCenter({"x": width, "y": height});
                // e = {"event": {"clientX": p.x,"x": p.x,"clientY": p.y,"y": p.y}};
                _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
            }
        },

        /**닫기 버튼이 있는 정보 상자 표시
         * @method alert
         * @static
         * @param {String} type - 표시할 정보 유형. 가능한 값: success 성공, info :정보, error :오류, wran : 경고
         * @param {String} title - 정보 상자 제목 표시줄 텍스트.
         * @param {String|Object} text - 정보 상자 내용 텍스트. 값이 html인 경우 아래 예시 「html로 내용 전달」 참고.
         * @param {Number} width - 정보 상자 너비.
         * @param {String} height - 정보 상자 높이.
         * @example
         * this.form.alert("wran", "필수 입력 알림", "제목을 입력해 주세요!", 300, 100);
         * @example
         * //html로 내용 전달
         * this.form.alert("wran", "필수 입력 알림", {
         *     html: "제목을 입력해 주세요!<br/>"
         * }, 300, 100);
         */
        "alert": function(type, title, text, width, height){
            _form.alert(type, title, text, width, height);
        },

        /**알림 상자 표시
         * @method notice
         * @static
         * @param {String} content - 표시할 정보 텍스트
         * @param {String} [type] - 표시할 정보 유형. 가능한 값: success 성공, info :정보, error :오류, warn : 경고
         * @param {Element} [target] - 정보 상자 표시 위치의 기준 DOM 객체.
         * @param {Object} [where] - target 기준 정보 상자의 x축·y축 표시 위치.<br/>
         * 예: {"x": "center", "y": "center"}<br/>
         x : <br/>
         　가로 위치는 「left」, 「right」, 「center」 사용 가능; 배열로 외부(outside)·내부(inside) 위치 정의 가능, 예: ['right', 'inside']<br/>
         y :<br/>
         　세로 위치는 「top」, 「bottom」, 「center」 사용 가능; 배열로 외부(outside)·내부(inside) 위치 정의 가능, 예: ['top', 'outside'].
         * @param {Object} [offset] - 상대 위치 오프셋, 음수 허용. 예: {"x": 10, "y": -10}
         * @param {Object} [option] - 기타 옵션. 예: { delayClose: 5000 } 5초 후 닫기
         * @example
         this.form.notice("this is my information", "info");
         */
        "notice": function(content, type, target, where, offset, option){
            _form.notice(content, type, target, where, offset, option);
        },

        /**대화 상자 레이어를 띄웁니다.
         * @method dialog
         * @static
         * @return {Object} 대화 상자 객체.
         * @param {(Object)} options
         * 팝업 옵션:<br/>
         * buttonList 인자가 있으면 ok, cancel 인자는 무효입니다.<br/>
         * 대화 상자 내용의 우선순위는 moduleName, content, url, html, text이며, 앞의 인자가 있으면 뒤의 인자는 무효입니다.<br/>
         * 팝업 객체 호출 후 이벤트 실행 순서는 onQueryLoad-->onPostLoad-->onQueryShow-->onPostShow입니다.<br/>
         * 기타 설명은 다음과 같습니다:
         * <pre><code class="language-js">{
         *   "style" : "default", //(string) 선택, 팝업 스타일, 기본값 user. 시스템 내장 스타일: user,blue_flat,o2,flat 등. 스타일 파일 위치: webserver\o2_core\o2\widget\$Dialog. 사용자 정의 스타일도 추가할 수 있으며 파일과 구조는 기존 스타일을 참고.
         *   "title" : "", //(string) 선택, 팝업 헤더 제목. isTitle이 true일 때 유효.
         *   "width" : 300, //(number) 선택, 팝업 너비. 기본값: 300
         *   "height" : 150, //(number) 선택, 팝업 높이. 기본값: 150
         *   "isTitle" : true, //(boolean) 선택, 팝업 제목 표시줄 여부. 기본값: true.
         *   "isMax" : false, //(boolean) 선택, 제목 표시줄 최대화 버튼(및 복원 버튼) 여부. 기본값: false.
         *   "isClose" : false, //(boolean) 선택, 제목 표시줄 닫기 버튼 여부. 기본값: false.
         *   "isResize" : true, //(boolean) 선택, 팝업 크기 조정 가능 여부. 기본값: true.
         *   "isMove" : true, //(boolean) 선택, 팝업 이동 가능 여부. 기본값: true.
         *   "offset" : {"x":-200, "y": -100}, //(object) 선택, 컨테이너(container) 기준 팝업 x축·y축 위치 오프셋. 비어 있으면 가운데.
         *   "mask" : true, //(boolean) 선택, 마스크 레이어 사용 여부. 기본값: true.
         *   "duration" : 200, //(number) 선택, 팝업 표시 애니메이션 시간. 기본값: 200.
         *   "zindex": 100, //(number) 선택, 팝업 z축 우선순위. 기본값 100(날짜 컨트롤 zindex는 300, 사용자 선택 컨트롤은 1000이므로 300보다 높지 않게 권장).
         *   "buttonList" : [
         *       {
         *           "type": "ok", //(string) 스타일, 컬러 배경 버튼
         *           "text": "확인", //(string) text: 버튼 표시 이름
         *           "action": function(){ //(function) 버튼 클릭 이벤트
         *               //do something, this는 이 대화 상자 객체
         *               this.close();
         *           }
         *       },
         *       {
         *           "type": "cancel", //(string) 스타일, 회색 배경 버튼
         *           "text": "취소",
         *           "action": function(){
         *               //do something
         *               this.close();
         *           }
         *       }
         *   ], //(Array) 선택, 하단 버튼 배열. 이 인자가 없으면 기본으로 확인·취소 두 버튼이 있으며, 이때 options에 ok 또는 cancel 메서드를 콜백으로 전달 가능. 빈 배열 “[]”을 전달하면 하단 버튼 없음.
         *   "ok": function(){}, //(function) 선택, options.buttonList가 없을 때 “확인” 버튼 콜백. true 반환 시 대화 상자 닫기. options.buttonList가 있으면 무시.
         *   "close": function(){}, //(function) 선택, options.buttonList가 없을 때 “취소” 버튼 콜백. true 반환 시 대화 상자 닫기. options.buttonList가 있으면 무시.
         *   "container" : this.form.getApp().content, //(Element/Dom) 선택, 팝업 레이어 dom의 부모 dom, 위치 기준 객체. 모바일은 기본으로 body에, PC는 기본으로 양식 노드가 있는 컨테이너(this.form.getApp().content)에 삽입.
         *   "moduleName": "div_1", //내용 인자, 우선순위 1, (String) 선택, 양식 컴포넌트 이름. 시스템이 해당 컴포넌트의 node를 대화 상자 내용으로 사용하며, 닫으면 노드가 원래 위치로 돌아감.
         *   "content": this.form.get("div1").node, //내용 인자, 우선순위 2, (Element/Dom) 선택, 대화 상자 내용. 노드가 document에 있으면 닫을 때 원래 위치로 돌아감.
         *   "url": "http://xxx/xxx.html", //내용 인자, 우선순위 3, (String) 선택, 이 인자가 가리키는 내용을 대화 상자 내용으로 사용.
         *   "html": "<div>html 내용</div>", //내용 인자, 우선순위 4, (String) 선택, 대화 상자 html 내용.
         *   "text": "텍스트 내용", //내용 인자, 우선순위 5, (String) 선택, 대화 상자 텍스트 내용.
         *   "onQueryClose": function(){}, //(function) 선택, 팝업 닫기 전 이벤트, this는 대화 상자 객체.
         *   "onPostClose": function(){}, //(function) 선택, 팝업 닫은 후 이벤트, this는 대화 상자 객체.
         *   "onQueryLoad": function(){}, //(function) 선택, 팝업 로드 전 이벤트, this는 대화 상자 객체.
         *   "onPostLoad": function(){}, //(function) 선택, 팝업 로드 후 이벤트, this는 대화 상자 객체.
         *   "onQueryShow": function(){}, //(function) 선택, 팝업 표시 전 이벤트, this는 대화 상자 객체.
         *   "onPostShow": function(){} //(function) 선택, 팝업 표시 후 이벤트, this는 대화 상자 객체.
         * }</code></pre>
         * @example
         * //html을 내용으로 대화 상자 열기
         * var _self = this;
         * var dlg = this.form.dialog({
         *   "title": "내용 입력",
         *   "width": "500",
         *   "height": "300",
         *   "html": "<div>내용:</div><div><input type='text'></div>",
         *   "ok": function(){
         *       var value = this.node.getElement("input").value; //this는 대화 상자 객체
         *       if( !value ){
         *           _self.form.notice("내용을 입력하세요", "info");
         *           return false; //false 반환 시 대화 상자를 닫지 않음
         *       }else{
         *           return true; //true 반환 시 대화 상자 닫기
         *       }
         *   }
         *});
         * @example
         * //양식의 div_1 컴포넌트를 내용으로 대화 상자 열기
         * var _self = this;
         * this.form.dialog({
         *      "title": "내용 입력",
         *     "width": "400",
         *      "height": "200",
         *      "moduleName": "div_1", //내용은 양식의 컴포넌트, 식별자 div_1
         *      "buttonList" : [
         *          {
         *              "type": "ok", //(string) 스타일, 컬러 배경 버튼
         *              "text": "확인", //(string) text: 버튼 표시 이름
         *              "action": function(){ //(function) 버튼 클릭 이벤트
         *                  //do something, this는 이 대화 상자 객체
         *                  var value = _self.form.get("textfield").getData(); //div_1 안의 컴포넌트 textfield 값 조회
         *                  if( !value ){
         *                    _self.form.notice("내용을 입력하세요","info");
         *                  }else{
         *                    this.close();
         *                  }
         *              }
         *          },
         *          {
         *              "type": "cancel", //(string) 스타일, 회색 배경 버튼
         *              "text": "취소",
         *              "action": function(){
         *                  //do something
         *                  this.close();
         *              }
         *          }
         *      ]
         * });
         * @example
         * //Dom 노드를 생성해 내용으로 대화 상자 열기
         * var _self = this;
         * var content = new Element("div");
         * new Element("label", {
         *    text: "내용:"
         * }).inject(content);
         *         new Element("input", {
         *    type: "text"
         * }).inject(content);

         * this.form.dialog({
         *     "title": "내용 입력",
         *     "width": "400",
         *     "height": "200",
         *     "zindex": 301, //z축 우선순위 301
         *     "content": content, //new Element로 생성한 내용. this.form.get(xx).node도 내용으로 사용 가능
         *     "buttonList" : [
         *         {
         *             "type": "ok", //(string) 스타일, 컬러 배경 버튼
         *             "text": "확인", //(string) text: 버튼 표시 이름
         *             "action": function(){ //(function) 버튼 클릭 이벤트
         *                 //do something, this는 이 대화 상자 객체
         *                 var value = this.node.getElement("input").get("value"); //대화 상자 노드 안 input 값 조회
         *                 if( !value ){
         *                   _self.form.notice("내용을 입력하세요","info");
         *                 }else{
         *                   this.close();
         *                 }
         *             }
         *         },
         *         {
         *             "type": "cancel", //(string) 스타일, 회색 배경 버튼
         *             "text": "취소",
         *             "action": function(){
         *                 //do something
         *                 this.close();
         *             }
         *         }
         *     ],
         *     "onQueryClose": function(){
         *           console.log("-onQueryClose-");
         *     },
         *     "onPostClose": function(){
         *           console.log("-onPostClose-");
         *     },
         *     "onQueryLoad":function(){
         *         console.log("-onQueryLoad-");
         *     },
         *     "onPostLoad": function(){
         *        console.log("-onPostLoad-");
         *     },
         *     "onQueryShow": function(){
         *          console.log("-onQueryshow-");
         *     },
         *     "onPostShow": function(){
         *         console.log("-onPostShow-");
         *     }.bind(this)
         *   });
         */
        "dialog": function ( options ) {
            return _form.dialog( options );
        },

        /**사용자·조직 선택 화면을 띄웁니다. 신분, 개인, 조직, 그룹의 단일 또는 복합 선택을 지원합니다. 이 메서드의 인자는 new MWF.O2Selector()에 적용됩니다.
         * @method selectOrg
         * @static
         * @return {Object} 사용자·조직 선택기 객체.
         * @param {Element} container - 사용자 선택 화면의 컨테이너. 기본값은 현재 앱의 컨테이너.
         * @param {Object} options - 사용자·조직 선택 옵션.<br/>
         * <pre><code class="language-js">{
         *  "type": "", //선택 유형, types와 택일. 가능한 값: identity(신분), person(개인), unit(조직), group(그룹),
         *  "types": [], //복합 선택, type과 택일. 예: ["identity", "person", "unit", "group"]
         *  "count": 0, //선택 개수, 0은 제한 없음
         *  "title": "", //선택 화면 title
         *  "values": [], //이미 선택된 값
         *
         *  "groups": [], //선택할 그룹 범위, 그룹 선택 시 유효.
         *
         *  "units": [], //선택할 조직 범위, 신분·조직 선택 시 유효
         *  "resultType" : "", //"person"(개인)으로 설정하면 결과를 개인으로 반환. 신분 선택 시 유효. 사용자를 선택하되 조직 계층으로 보여야 하는 경우에 사용.
         *  "dutys": [], //선택할 직무 범위, 신분 선택 시 유효.
         *  "categoryType": "", //unit 또는 duty 사용 가능. 직무 범위(dutys가 비어 있지 않음)를 지정한 경우 unit(조직) 또는 duty(직무) 기준 분류 표시, 기본값 unit. 신분 선택 시 유효.
         *
         *  "noUnit" : false, //신분 선택 시 include 옵션만 사용할지 여부.
         *  "include" : [], //추가 선택 항목. 신분 선택 시 units를 전달하지 않으면 전체가 선택되어 include가 적용되지 않으며, onUnit 옵션으로 include만 사용하도록 지정 가능.
         *  "exclude" : [], //제외할 선택 항목
         *
         *  "expandSubEnable" : true, //하위 단계 펼치기 허용 여부, 신분·조직 선택 시 유효
         *  "selectAllEnable" : true,  //분류의 하위 단계 전체 선택 허용 여부, 신분·조직 선택 시 유효
         *
         *  "level1Indent" : 10, //1단계 들여쓰기
         *  "indent" : 10, //이후 단계 들여쓰기
         *  "zIndex": 1000, //선택 화면 zIndex,
         *
         *  "onComplete" : function( selectedItemList ){
         *      //확인 클릭 시 실행
         *      //selectedItemList는 선택된 item 객체, 아래 selectedDataList는 선택된 데이터
         *      var selectedDataList = selectedItemList.map( function(item){
         *          return item.data;
         *      })
         *  },
         *  "onCancel" : function(selector) {
         *      //취소 클릭 시 이벤트, selector는 선택기, this는 선택기
         *  },
         *  "onQueryLoad" : function(selector) {
         *      //선택기 로드 전 이벤트, selector는 선택기, this는 선택기
         *  },
         *  "onLoad" : function(selector) {
         *      //선택기 로드 후 이벤트, selector는 선택기, this는 선택기
         *  },
         *  "onCancel" : function(selector) {
         *      //취소 클릭 시 이벤트, selector는 선택기, this는 선택기
         *  },
         *  "onQueryLoadCategory" : function(category) {
         *      //분류 로드 전 이벤트, category는 분류 객체, this는 선택기
         *  },
         *  "onPostLoadCategory" : function(category) {
         *      //분류 로드 후 이벤트, category는 분류 객체, this는 선택기
         *  },
         *  "onSelectCategory" : function(category){
         *      //분류 선택, category는 분류 객체, this는 선택기
         *  },
         *  "onUnselectCategory": function(category){
         *      //분류 선택 취소, category는 분류 객체, this는 선택기
         *  },
         *  "onQueryLoadItem" : function(item){
         *      //항목 로드 전 이벤트, item은 항목 객체, this는 선택기
         *  },
         *  "onPostLoadItem" : function(item){
         *      //항목 로드 후 이벤트, item은 항목 객체, this는 선택기
         *  },
         *  "onSelectItem" : function(item){
         *      //항목 선택 이벤트, item은 항목 객체, this는 선택기
         *  },
         *  "onUnselectItem" : function(item){
         *      //항목 선택 취소 이벤트, item은 항목 객체, this는 선택기
         *  },
         *  "onExpand" : function( obj ){
         *      //분류 펼치기, obj는 분류/항목, this는 선택기
         *  },
         *  "onCollapse" : function(obj){
         *       //분류 접기, obj는 분류/항목, this는 선택기
         *   }
         *}</code></pre>
         * @example
         * //신분 선택
         * var selector = this.form.selectOrg(null, {
         *   type: "identity",
         *   onComplete : function( selectedItemList ){
         *      //확인 클릭 시 실행
         *      //selectedItemList는 선택된 item 객체, 아래 selectedDataList는 선택된 데이터
         *      var selectedDataList = selectedItemList.map( function(item){
         *          return item.data;
         *      })
         *  }
         *});
         * @example
         * //지정 조직 내 신분 선택
         * var selector = this.form.selectOrg(null, {
         *   type: "identity",
         *   units: ["인디고@landzone@U"],
         *   onComplete : function( selectedItemList ){
         *  }
         *});
         * @example
         * //지정 직무로 신분 선택
         * var selector = this.form.selectOrg(null, {
         *   type: "identity",
         *   dutys: ["부서장"],
         *   onComplete : function( selectedItemList ){
         *  }
         *});
         * @example
         * //조직, 그룹, 신분, 개인 동시 선택
         * var selector = this.form.selectOrg(null, {
         *   types: ["unit", "group", "identity", "person"],
         *   onComplete : function( selectedItemList ){
         *  }
         *});
         */
        "selectOrg": function ( container, options,  delayLoad) {
            if( !container )container = _form.app.content;
            return new MWF.O2Selector(container, options, delayLoad);
        },

        /**양식에 이벤트를 추가합니다.
         * @method addEvent
         * @static
         * @param {String} type - 이벤트 이름, 본 API Classer->FormComponents->Form의 이벤트 참고
         * @param {Function} event - 이벤트 메서드.
         * @example
         this.form.addEvent("afterLoad", function(){
            this.form.notice("양식 로드 완료", "success");
        }.bind(this));
         */
        "addEvent": function(type, event ){_form.addEvent(type, event );},

        /**새 브라우저 창으로 현재 문서를 열어 인쇄에 사용합니다. <b>(프로세스 양식에서만 사용 가능)</b><br/>
         * 양식을 지정하지 않으면 양식 설계에서 지정한 인쇄 양식을 사용합니다.<br/>
         * @method print
         * @static
         * @param {String} [application] - 양식이 속한 프로세스 앱 ID 또는 이름. 생략하면 현재 앱.
         * @param {String} [form] - 양식 ID 또는 이름.
         * @example
         //새 창에서 현재 양식에 설정된 인쇄 양식으로 현재 문서 열기
         this.form.print();
         * @example
         //새 창에서 “주문 인쇄 양식”으로 현재 문서 열기
         this.form.print("주문 인쇄 양식");
         * @example
         //새 창에서 “주문 관리” 앱의 “주문 인쇄 양식”으로 현재 문서 열기
         this.form.print("주문 관리", "주문 인쇄 양식");
         */
        "print": function(application, form){
            if (arguments.length){
                var app = (arguments.length>1) ? arguments[0] : null;
                var formName = (arguments.length>1) ? arguments[1] : arguments[0];
                _form.printWork(app, formName);
            }else{
                _form.printWork();
            }
        },

        /**print 메서드와 같습니다. <b>(프로세스 양식에서만 사용 가능)</b><br/>
         * @method openWindow
         * @static
         * @see this.form.print()
         * @param {String} [application] - 양식이 속한 프로세스 앱 ID 또는 이름. 생략하면 현재 앱.
         * @param {String} [form] - 양식 ID 또는 이름.
         * @example
         this.form.openWindow();
         */
        "openWindow": function(application, form){
            if (arguments.length){
                var app = (arguments.length>1) ? arguments[0] : null;
                var formName = (arguments.length>1) ? arguments[1] : arguments[0];
                _form.openWindow(formName, app);
            }else{
                _form.openWindow();
            }
        },

        /** 진행 중이거나 완료된 프로세스 인스턴스를 엽니다.<br/>
         * @method openWork
         * @static
         * @param {String} [workId] - 진행 중인 프로세스 인스턴스 ID. workId와 workCompletedId 중 하나는 반드시 제공
         * @param {String} [workCompletedId] - 완료된 프로세스 인스턴스 ID.
         * @param {String} [title] - 모바일에서 열 때의 창 제목.
         * @param {Object} [options] - 기타 옵션(읽기 전용 인자 등).
         * @example
         * this.form.openWork(id, "", "work title");
         * @example
         * //읽기 전용으로 열기
         * this.form.openWork(id, "", "work title", {
         *   readonly : true
         * });
         */
        "openWork": function(workId, workCompletedId, title, options){
            var op = options || {};
            op.workId = workId;
            op.workCompletedId = workCompletedId;
            op.docTitle = title;
            op.appId = "process.Work"+(op.workId || op.workCompletedId);
            return layout.desktop.openApplication(this.event, "process.Work", op);
        },

        /** 프로세스의 jobId로 업무 문서를 엽니다.<br/>
         * @method openJob
         * @static
         * @param {String} id - 프로세스의 jobId. 프로세스가 분할되어 인스턴스가 여러 개(workId가 여러 개)여도 jobId는 유일함.
         * @param {Boolean} [choice] - 프로세스 인스턴스가 여러 개일 때 선택 화면을 띄울지 여부. false를 전달하면 첫 번째 업무 문서를 바로 엶.
         * @param {Object} [options] - 업무 문서를 열 때 전달할 옵션.
         * @param {Function} [callback] - 업무 문서 열기 성공 또는 실패 콜백. 성공 시 연 업무 문서 객체(데스크톱 모드) 또는 창 핸들(브라우저 탭 모드)을 받음. 실패 시 첫 번째 인자는 Error이며 cause 속성으로 jobId로 조회한 work 데이터를 얻을 수 있음.
         * @example
         this.form.openJob(jobId, true);
         * @example
         this.form.openJob(jobId, true, {}, function(handel){
            //Error.prototype.isPrototypeOf(handel)로 handel이 오류인지 판단.
            //열기에 성공하면 handel은 연 업무 문서 객체(데스크톱 모드) 또는 창 핸들(브라우저 탭 모드)
            //열기에 실패하면 handel은 Error 객체이며 cause 속성으로 jobId로 조회한 work 데이터를 얻을 수 있음
         });
         */
        "openJob": function(id, choice, options, callback){
            var workData = null, handel;
            o2.Actions.get("x_processplatform_assemble_surface").listWorkByJob(id, function(json){
                if (json.data) workData = json.data;
            }.bind(this), null, false);

            if( !layout.inBrowser && o2.typeOf(callback) === "function" ){
                if( !options )options = {};
                var queryLoad = options.onQueryLoad;
                options.onQueryLoad = function () {
                    if( o2.typeOf(queryLoad) === "function" )queryLoad.call(this);
                    callback(this);
                }
            }

            runCallback = function ( handel ) {
                if( o2.typeOf(callback) === "function" ) {
                    if (layout.inBrowser) {
                        callback(handel);
                    } else if (options && options.appId) {
                        if (layout.desktop && layout.desktop.apps && layout.desktop.apps[options.appId]) {
                            callback(layout.desktop.apps[options.appId], true);
                        }else{
                            callback(handel, false);
                        }
                    }else{
                        callback(handel, false);
                    }
                }
            };

            if (workData){
                var len = workData.workList.length + workData.workCompletedList.length;
                if (len){
                    if (len>1 && choice){
                        var node = new Element("div", {"styles": {"padding": "20px", "width": "500px"}}).inject(_form.node || _form.app.contentNode);
                        workData.workList.each(function(work){
                            var workNode = new Element("div", {
                                "styles": {
                                    "background": "#ffffff",
                                    "border-radius": "10px",
                                    "clear": "both",
                                    "margin-bottom": "10px",
                                    "height": "40px",
                                    "padding": "10px 10px"
                                }
                            }).inject(node);
                            var html = "<div style='height: 40px; width: 40px; float: left; background: url(../x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
                                "<div style='height: 40px; width: 40px; float: right'><div class='MWFAction' style='height: 20px; width: 40px; margin-top: 10px; border: 1px solid #999999; border-radius: 5px;text-align: center; cursor: pointer'>"+o2.LP.widget.open+"</div></div>"+
                                "<div style='height: 20px; line-height: 20px; margin: 0px 40px'>"+work.title+"</div>" +
                                "<div style='margin: 0px 40px'><div style='color:#999999; float: left; margin-right: 10px'>"+work.activityName+"</div>" +
                                "<div style='color:#999999; float: left; margin-right: 10px'>"+work.activityArrivedTime+"</div>" +
                                "<div style='color:#999999; float: left; margin-right: 10px'>"+(work.manualTaskIdentityText || "")+"</div></div>";
                            workNode.set("html", html);
                            var action = workNode.getElement(".MWFAction");
                            action.store("work", work);
                            action.addEvent("click", function(e){
                                var work = e.target.retrieve("work");
                                if (work){
                                   handel =  this.openWork(work.id, null, work.title, options);
                                   runCallback( handel );
                                }
                                dlg.close();
                            }.bind(this));

                        }.bind(this));
                        workData.workCompletedList.each(function(work){
                            var workNode = new Element("div", {
                                "styles": {
                                    "background": "#ffffff",
                                    "border-radius": "10px",
                                    "clear": "both",
                                    "margin-bottom": "10px",
                                    "height": "40px",
                                    "padding": "10px 10px"
                                }
                            }).inject(node);
                            var html = "<div style='height: 40px; width: 40px; float: left; background: url(../x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
                                "<div style='height: 40px; width: 40px; float: right'><div class='MWFAction' style='height: 20px; width: 40px; margin-top: 10px; border: 1px solid #999999; border-radius: 5px;text-align: center; cursor: pointer'>"+o2.LP.widget.open+"</div></div>"+
                                "<div style='height: 20px; line-height: 20px; margin: 0px 40px'>"+work.title+"</div>" +
                                "<div style='margin: 0px 40px'><div style='color:#999999; float: left; margin-right: 10px'>"+o2.LP.widget.workcompleted+"</div>" +
                                "<div style='color:#999999; float: left; margin-right: 10px'>"+work.completedTime+"</div>";
                            workNode.set("html", html);
                            var action = workNode.getElement(".MWFAction");
                            action.store("work", work);
                            action.addEvent("click", function(e){
                                var work = e.target.retrieve("work");
                                if (work){
                                    handel =  this.openWork(null, work.id, work.title, options);
                                    runCallback( handel );
                                }
                                dlg.close();
                            }.bind(this));

                        }.bind(this));
                        var height = node.getSize().y+20;
                        if (height>600) height = 600;

                        var dlg = o2.DL.open({
                            "title": o2.LP.widget.choiceWork,
                            "style" : "user",
                            "isResize": false,
                            "content": node,
                            "buttonList": [
                                {
                                    "type" : "cancel",
                                    "text": o2.LP.widget.close,
                                    "action": function(){dlg.close();}
                                }
                            ]
                        });
                    }else{
                        if (workData.workList.length){
                            var work =  workData.workList[0];
                            handel = this.openWork(work.id, null, work.title, options);
                            runCallback( handel );
                            return handel;
                        }else{
                            var work =  workData.workCompletedList[0];
                            handel = this.openWork(null, work.id, work.title, options);
                            runCallback( handel );
                            return handel;
                        }
                    }
                }else{
                    _form.notice(o2.LP.widget.noWork, 'warn');
                    runCallback(new Error("Can't open this Job", {
                        cause: workData
                    }));
                }
            }else{
                _form.notice(o2.LP.widget.noWork, 'warn');
                runCallback(new Error("Can't open this Job", {
                    cause: workData
                }));
            }
            // var op = options || {};
            // op.workId = id;
            // op.workCompletedId = completedId;
            // op.docTitle = title;
            // op.appId = "process.Work"+(op.workId || op.workCompletedId);
            // layout.desktop.openApplication(this.event, "process.Work", op);
        },

        /** 콘텐츠 관리 문서를 엽니다.<br/>
         * @method openDocument
         * @static
         * @param {String} id - 콘텐츠 관리 문서 인스턴스 ID.
         * @param {Boolean} [title] - 모바일 APP에서 열 때의 창 제목.
         * @param {Object} [options] - 기타 인자, 내용은 다음과 같음<br>
         * <pre><code class="language-js">{
         *   "readonly": true, //읽기 전용으로 열지 여부, 기본값 true
         *   "forceFormId": "xxxxxx", //편집·열람 구분 없이 이 양식 id로 열기, 우선 사용. 6.0 이전 버전은 printFormId 사용.
         *   "readFormId": "xxxxxx", //강제 열람 양식 id, 양식의 readFormId보다 우선. 6.0 이전 버전은 formId 사용.
         *   "editFormId": "xxxxxx", //강제 편집 양식 id, 양식의 formId보다 우선. 6.0 이전 버전은 formEditId 사용.
         *    "saveOnClose" : true, //초안을 닫을 때 자동 저장 여부
         *    "onPostPublish" : function( documentData ){ //게시 전 실행 메서드(데이터는 준비된 상태), 데스크톱 모드에서 열 때 유효
         *       //documentData는 문서 데이터
         *    },
         *    "onAfterPublish" : function( form, documentData ){ //게시 후 실행 메서드, 데스크톱 모드에서 열 때 유효
         *       //form은 콘텐츠 관리 Form 객체, documentData는 문서 데이터
         *    },
         *    "onAfterSave": function( form, documentData ){ //저장 후 실행 메서드, 데스크톱 모드에서 열 때 유효
         *       //form은 콘텐츠 관리 Form 객체, documentData는 문서 데이터
         *    },
         *    "onBeforeClose": function(){ //닫기 전 실행 메서드, 데스크톱 모드에서 열 때 유효
         *
         *    },
         *    "onPostDelete" : function(){ //문서 삭제 후 실행 메서드, 데스크톱 모드에서 열 때 유효
         *    }
         * }</code></pre>
         * @example
         this.form.openDocument(id, "document title");
         */
        "openDocument": function(id, title, options){
            var op = options || {};
            op.documentId = id;
            op.docTitle = title || "";
            op.appId = (op.appId) || ("cms.Document"+id);
            if( op.onPostPublish ){
                op.postPublish = op.onPostPublish;
                delete op.onPostPublish;
            }
            if( op.onAfterPublish ){
                op.afterPublish = op.onAfterPublish;
                delete op.onAfterPublish;
            }
            if( op.onAfterSave ){
                op.afterSave = op.onAfterSave;
                delete op.onAfterSave;
            }
            if( op.onBeforeClose ){
                op.beforeClose = op.onBeforeClose;
                delete op.onBeforeClose;
            }
            if( op.onPostDelete ){
                op.postDelete = op.onPostDelete;
                delete op.onPostDelete;
            }
            return layout.desktop.openApplication(this.event, "cms.Document", op);
        },

        /**포털 페이지를 엽니다.<br/>
         * @method openPortal
         * @static
         * @param {String} portal - 열 포털 앱의 이름, 별칭 또는 ID.
         * @param {String} [page] - 열 페이지의 이름, 별칭 또는 ID. 생략하면 포털 기본 홈페이지를 엶
         * @param {Object} [par] - 페이지를 열 때 전달할 인자.<br>열린 페이지에서 스크립트 this.page.parameters로 이 인자에 접근 가능.
         * @example
         this.form.openPortal(id, "", {"type": "my type"});
         */
        "openPortal": function (portal, page, par) {
            var action = MWF.Actions.get("x_portal_assemble_surface");
            action.getApplication(portal, function (json) {
                if (json.data) {
                    if (page) {
                        action.getPageByName(page, json.data.id, function (pageJson) {
                            var pageId = (pageJson.data) ? pageJson.data.id : "";
                            layout.desktop.openApplication(null, "portal.Portal", {
                                "portalId": json.data.id,
                                "pageId": pageId,
                                "parameters": par,
                                "appId": (par && par.appId) || ("portal.Portal" + json.data.id + pageId)
                            })
                        });
                    } else {
                        layout.desktop.openApplication(null, "portal.Portal", {
                            "portalId": json.data.id,
                            "parameters": par,
                            "appId": (par && par.appId) || ("portal.Portal" + json.data.id)
                        })
                    }
                }

            });
        },

        /**지정한 dom 요소 안에서 포털 페이지를 엽니다.<br/>
         * @method loadPortal
         * @static
         * @param {Element} content - 포털 페이지를 로드할 DOM 요소.
         * @param {String} portal - 열 포털 앱의 이름, 별칭 또는 ID.
         * @param {String} [page] - 열 페이지의 이름, 별칭 또는 ID. 생략하면 포털 기본 홈페이지를 엶
         * @param {Object} [data] - 포털의 업무 데이터, 포털에서 this.data로 접근 가능.
         * @param {Object} [par] - 페이지를 열 때 전달할 인자.<br>열린 페이지에서 스크립트 this.page.parameters로 이 인자에 접근 가능.
         * @example
         this.form.openPortal(id, "", {"type": "my type"});
         */
        "loadPortal": function (content, portal, page, data, par) {
            const app = new MWF.xApplication.portal.Portal.Main(layout.desktop, {
                portalId: portal,
                pageId: page,
                data: data,
                parameters: par
            });
            app.viewMode="Default";
            app.windowNode = content;
            app.setCurrent = function(){
                this.window.setCurrent();
            }
            app.setUncurrent = function(){
                this.window.setUncurrent();
            }
            app.close = function(){
                this.fireAppEvent("queryClose");
                this.window.close(function () {
                    this.window = null;
                    this.taskitem = null;
                    this.fireAppEvent("postClose");
                    o2.release(this);
                }.bind(this));
            }
            app.load(true, content);

            return app;
        },


        /**콘텐츠 관리 게시판(앱)을 엽니다.<br/>
         * @method openCMS
         * @static
         * @param {String} name - 콘텐츠 관리 게시판의 이름, 별칭 또는 ID.
         * @example
         this.form.openCMS("공지사항");
         */
        "openCMS": function(name){
            var action = MWF.Actions.get("x_cms_assemble_control");
            action.getColumn(name, function(json){
                if (json.data){
                    layout.desktop.openApplication(null, "cms.Module", {
                        "columnId": json.data.id,
                        "appId": "cms.Module"+json.data.id
                    });
                }
            });
        },

        /**프로세스 앱을 엽니다.<br/>
         * @method openProcess
         * @static
         * @param {String} name - 프로세스 앱의 이름, 별칭 또는 ID.
         * @example
         this.form.openProcess("재무 결재");
         */
        "openProcess": function(name){
            var action = MWF.Actions.get("x_processplatform_assemble_surface");
            action.getApplication(name, function(json){
                if (json.data){
                    layout.desktop.openApplication(null, "process.Application", {
                        "id": json.data.id,
                        "appId": "process.Application"+json.data.id
                    });
                }
            });
        },

        /**임의의 component 앱을 엽니다.<br/>
         * @method openApplication
         * @static
         * @param {String} name - 열 component 이름. component 이름은 “제어판-시스템 설정-화면 설정-모듈 배포”에서 찾을 수 있음(즉 “컴포넌트 경로”).
         * @param {Object} [options] - 열 component 관련 인자, 해당 앱 소스 Main.js의 options에 대응.
         * @param {Object} [status] - 열 component 상태, 사용자 조작 후 상태에 대응. <b>다음 방법으로 이 인자를 얻으세요:</b> 데스크톱 모드에서 앱을 더블클릭하면 열린 앱의 브라우저 주소에서 해당 status를 확인할 수 있습니다.
         * @example
         //회의 관리 열기
         this.form.openApplication("Meeting");
         * @example
         //회의 관리 주간 뷰 열기
         this.form.openApplication("Meeting", null, {"action":"toWeek" });
         * @example
         //진행 중인 프로세스 인스턴스를 엽니다. this.form.openWork(id, "", "work title");와 효과가 같습니다
         this.form.openApplication("process.Work", {
            "workId": id,   //프로세스 인스턴스 ID
            "width": "1200",    //너비
            "height": "800",    //높이
            "docTitle": "work title",   //APP 창 제목
            "appId": "process.Work"+id  //새로 연 component 인스턴스의 고유 이름
        });
         */
        "openApplication":function(name, options, status){
            return layout.desktop.openApplication(null, name, options, status);
        },

        /**콘텐츠 관리 문서를 생성합니다.
         * @method createDocument
         * @static
         * @param {(String|Object)} [columnOrOptions]
         * 인자를 전달하지 않으면 플랫폼 전체 게시판 범위의 선택 화면을 띄웁니다.<br/>
         * String이면 콘텐츠 관리 앱(게시판)의 이름, 별칭 또는 ID입니다.<br/>
         * Object이면 이 인자 뒤의 인자는 생략하고 다음 형식의 내용을 전달합니다:
         * <pre><code class="language-js">{
         *   "column" : column, //(string) 선택, 콘텐츠 관리 앱(게시판)의 이름, 별칭 또는 ID
         *   "category" : category, //(string) 선택, 생성할 문서가 속한 분류의 이름, 별칭 또는 ID
         *   "data" : data, //(json object) 선택, 문서 생성 시 기본 업무 데이터
         *   "identity" : identity, //(string | Array) 선택, 문서 생성에 사용할 신분. 비어 있고 현재 사용자의 신분이 여러 개이면 신분 선택 대화 상자를 띄움. 배열이면 배열 범위 내 신분을 선택하게 함. 그 외에는 기본 신분 사용.
         *   "callback" : callback, //(funcation) 선택, 문서 생성 후 콜백 함수.
         *   "target" : target, //(boolean) 선택, true이면 현재 페이지에서 생성된 문서를 엶. 아니면 새 창. 기본값 false. (현재 양식 또는 페이지를 브라우저에서 단독으로 열었을 때 유효.)
         *   "latest" : latest, //(boolean) 선택, true이면 현재 사용자가 이 분류의 문서를 이미 만들고 게시하지 않은 경우 그 문서를 새 문서로 사용. 아니면 새 문서 생성. 기본값 true.
         *   "selectColumnEnable" : selectColumnEnable, //(boolean) 선택, 앱과 분류를 선택해 문서를 생성할 수 있는지 여부. category 인자가 있으면 기본값 false, 아니면 true.
         *   "ignoreTitle" : ignoreTitle //(boolean) 선택, false이면 생성 시 제목 입력 필수. 기본값 false.
         *   "restrictToColumn" : restrictToColumn //(boolean) 선택, true이면 전달한 게시판 안에서만 분류를 선택하도록 제한. 기본값 false.
         * }</code></pre>
         * @param {String} [category] - 생성할 문서가 속한 분류의 이름, 별칭 또는 ID
         * @param {Object} [data] - 문서 생성 시 기본 업무 데이터
         * @param {String} [identity | Array] - 선택, 문서 생성에 사용할 신분. 비어 있고 현재 사용자의 신분이 여러 개이면 신분 선택 대화 상자를 띄움. 배열이면 배열 범위 내 신분을 선택하게 함. 그 외에는 기본 신분 사용.
         * @param {Function} [callback] - 문서 생성 후 콜백 함수
         * @param {Boolean} [target] - true이면 현재 페이지에서 생성된 문서를 엶. 아니면 새 창. 기본값 false. (현재 양식 또는 페이지를 브라우저에서 단독으로 열었을 때 유효.)
         * @param {Boolean} [latest] - true이면 현재 사용자가 이 분류의 문서를 이미 만들고 게시하지 않은 경우 그 문서를 새 문서로 사용. 아니면 새 문서 생성. 기본값 true.
         * @param {Boolean} [selectColumnEnable] - 앱과 분류를 선택해 문서를 생성할 수 있는지 여부. category 인자가 있으면 기본값 false, 아니면 true.
         * @param {Boolean} [ignoreTitle] - false이면 생성 시 제목 입력 필수. 기본값 false.
         * @param {Boolean} [restrictToColumn] - true이면 전달한 게시판 안에서만 분류를 선택하도록 제한. 기본값 false.
         * @example
         //공지사항 생성
         this.form.createDocument("", "공지사항");
         * @example
         //공지사항 생성, 제목: XX 관련 공지, 생성 후 알림
         this.form.createDocument("", "공지사항", {"subject": "XX 관련 공지"}, function(json){
            this.form.notice("생성되었습니다!", "success");
        }.bind(this));
         * @example
         //공지사항 생성, 제목: XX 관련 공지, 생성 후 알림
         this.form.createDocument({
            category : "공지사항",
            data : {"subject": "XX 관련 공지"},
            callback : function(documentId, json, documentApp){
              if( layout.inBrowser ){ //브라우저 모드
                //documentApp은 문서의 window 객체
              }else{
                //documentApp은 문서 document app 객체
              }
                this.form.notice("생성되었습니다!", "success");
            }.bind(this)
         });
         */
        "createDocument": function (columnOrOptions, category, data, identity, callback, target, latest, selectColumnEnable, ignoreTitle, restrictToColumn) {
            var column = columnOrOptions;
            var onAfterPublish, onPostPublish;
            if (typeOf(columnOrOptions) == "object") {
                column = columnOrOptions.column;
                category = columnOrOptions.category;
                data = columnOrOptions.data;
                identity = columnOrOptions.identity;
                callback = columnOrOptions.callback;
                target = columnOrOptions.target;
                latest = columnOrOptions.latest;
                selectColumnEnable = columnOrOptions.selectColumnEnable;
                ignoreTitle = columnOrOptions.ignoreTitle;
                restrictToColumn = columnOrOptions.restrictToColumn;
                onAfterPublish = columnOrOptions.onAfterPublish;
                onPostPublish = columnOrOptions.onPostPublish;
            }
            if (target) {
                if (layout.app && layout.app.inBrowser) {
                    layout.app.content.empty();
                    layout.app = null;
                }
            }

            MWF.xDesktop.requireApp("cms.Index", "Newer", function () {
                var starter = new MWF.xApplication.cms.Index.Newer(null, null, _form.app, null, {
                    "documentData": data,
                    "identity": identity,

                    "ignoreTitle": ignoreTitle === true,
                    "ignoreDrafted": latest === false,
                    "selectColumnEnable": !category || selectColumnEnable === true,
                    "restrictToColumn": restrictToColumn === true || (!!category && selectColumnEnable !== true),

                    "categoryFlag": category, //category id or name
                    "columnFlag": column, //column id or name,
                    "onStarted": function (documentId, data, windowHandle) {
                        if (callback) callback(documentId, data, windowHandle);
                    },
                    "onPostPublish": function () {
                        if(onPostPublish)onPostPublish();
                    },
                    "onAfterPublish": function () {
                        if(onAfterPublish)onAfterPublish();
                    }
                });
                starter.load();
            })
        },

        /**프로세스 인스턴스를 시작합니다. 인자가 없으면 프로세스 시작 화면을 띄워 현재 사용자가 시작 권한이 있는 프로세스를 표시합니다. app을 전달하면 app의 프로세스를 표시하는 시작 화면을 띄웁니다. app과 process를 전달하면 바로 프로세스를 시작합니다.<br/>
         * @method startProcess
         * @static
         * @param {String|Array} [app]  - 프로세스 앱의 이름, 별칭 또는 ID.
         * @param {String} [process]  - 시작할 프로세스의 이름, 별칭 또는 ID.
         * @param {Object} [data]   - 프로세스 시작 시 기본 업무 데이터. 첨부파일을 전달하려면 $attachmentList, 관련 문서를 전달하려면 $correlationTargetList 추가
         * <pre><code class='language-js'>{
         *      "subject": "아무개 제목",  //업무 데이터 필드
         *      "count": 123,		//업무 데이터 필드
         *      "$attachmentList": [ //$attachmentList 전달 시 첨부파일을 새 업무 문서로 복사하며 업무 데이터에는 넣지 않음. (copyFrom이 없으면 업무 데이터에 넣음)
         *          {
         *              'id':'원본 첨부파일 id',
         *              'name':'첨부파일 이름',
         *              'site':'첨부파일 컴포넌트의 site',
         *              'isSoftCopy':'소프트 복사 여부, 기본값 false. true이면 실제 저장 첨부파일을 복사하지 않고 공유하며 프로세스 첨부파일만 지원',
         *              'copyFrom':'첨부파일 출처(cms 또는 processPlatform)'
         *          }
         *      ],
         *      "$correlationTargetList": [ //$correlationTargetList 전달 시 문서를 새 업무 문서에 연결하며 업무 데이터에는 넣지 않음
         *          {
         *              'type':'연결 대상 유형(cms 또는 processPlatform)',
         *              'bundle':'연결 대상 식별자',
         *              'site':'관련 문서 컴포넌트의 site',
         *              'view':'출처 뷰'
         *          }
         *      ]
         * }</code></pre>
         * @param {String | Array} [identity]  - 프로세스 시작에 사용할 신분. 비어 있거나 빈 문자열이고 현재 사용자의 신분이 여러 개이면 신분 선택 대화 상자를 띄움. 배열이면 배열 범위 내 신분을 선택하게 함. 그 외에는 기본 신분 사용.
         * @param {Function} [callback]  - 프로세스 시작 후 콜백 함수, 시작 데이터를 얻을 수 있음.
         * @param {Boolean} [target]  - true이면 현재 페이지에서 시작된 프로세스 인스턴스를 엶. 아니면 새 창. 기본값 false. (현재 양식 또는 페이지를 브라우저에서 단독으로 열었을 때 유효.)
         * @param {Boolean} [latest]  - true이면 현재 사용자가 이 프로세스의 인스턴스를 이미 만들고 진행하지 않은 경우 그 인스턴스를 새 인스턴스로 사용. 아니면 새 인스턴스 생성. 기본값 false.
         * @param {Function} [afterCreated]  - 프로세스 생성 후 콜백, 생성된 프로세스 Work 객체(데스크톱 모드) 또는 Window 객체(브라우저 모드)를 얻을 수 있음.
         * @param {Boolean} [skipDraftCheck]  - 신규 생성 검사 생략 여부(기본은 프로세스의 신규 생성 검사 설정에 따름). true이면 신규 생성 검사를 하지 않음.
         * @example
         * //프로세스 시작 화면 띄우기
         * this.form.startProcess();
         * @example
         * //공문 관리 시작 화면 띄우기
         * this.form.startProcess("공문 관리");
         * @example
         * //공문 관리와 인사 관리 시작 화면 띄우기
         * this.form.startProcess(["공문 관리","인사 관리"]);
         * @example
         * //발신 문서 관리 인스턴스 시작
         this.form.startProcess("공문 관리", "발신 문서 관리");
         * @example
         * //발신 문서 관리 인스턴스 시작, 제목: my file title, 시작 후 알림
         * this.form.startProcess("공문 관리", "발신 문서 관리", {"title": "my file title"}, "홍길동@kfb_zhangsan@I", function(json){
         *   this.form.notice("create file success!", "success");
         * }, false, false, function(workApp){
         *     if( layout.inBrowser ){ //브라우저 모드
         *       //workApp은 프로세스의 window 객체
         *     }else{
         *       //workApp은 프로세스 Work app 객체
         *     }
         * });
         */
        "startProcess": function(app, process, data, identity, callback, target, latest, afterCreated, skipDraftCheck){
            if (arguments.length>2){
                for (var i=2; i<arguments.length; i++){
                    if (typeOf(arguments[i])=="boolean"){
                        target = arguments[i];
                        break;
                    }
                }
            }

            if (target){
                if (layout.app && layout.app.inBrowser){
                    //layout.app.content.empty();
                    layout.app.$openWithSelf = true;
                }
            }
            if (!app || !process){
                var cmpt = this.getApp();
                o2.requireApp([["process.TaskCenter", "lp."+o2.language], ["process.TaskCenter", ""]],"", function(){
                    var obj = {
                        "lp": o2.xApplication.process.TaskCenter.LP,
                        "content": cmpt.content,
                        "addEvent": function(type, fun){
                            cmpt.addEvent(type, fun);
                        },
                        "getAction": function (callback) {
                            if (!this.action) {
                                this.action = o2.Actions.get("x_processplatform_assemble_surface");
                                if (callback) callback();
                            } else {
                                if (callback) callback();
                            }
                        },
                        "desktop": layout.desktop,
                        "refreshAll": function(){},
                        "notice": cmpt.notice,
                    }
                    o2.JSON.get("../x_component_process_TaskCenter/$Main/default/css.wcss", function(data){
                        obj.css = data;
                    }, false);

                    if (!cmpt.processStarter) cmpt.processStarter = new o2.xApplication.process.TaskCenter.Starter(obj);
                    cmpt.processStarter.load({
                        "appFlag": app
                    });
                }, true, true);
                return "";
            }
            MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", null, false);
            var action = MWF.Actions.get("x_processplatform_assemble_surface").getProcessByName(process, app, function(json){
                if (json.data){
                    var attachmentList = [];
                    var correlationTargetList = [];
                    if( data && data.$attachmentList && o2.typeOf(data.$attachmentList) === 'array' ){
                        attachmentList = data.$attachmentList.filter(function(d){
                            return !!d.copyFrom;
                        });
                        data.$attachmentList = data.$attachmentList.filter(function(d){
                            return !d.copyFrom;
                        });
                        if( !data.$attachmentList.length )delete data.$attachmentList;
                    }
                    if( data && data.$correlationTargetList && o2.typeOf(data.$correlationTargetList) === 'array' ){
                        correlationTargetList = data.$correlationTargetList;
                        delete data.$correlationTargetList;
                    }
                    var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(json.data, _form.app, {
                        "workData": data,
                        "attachmentList": attachmentList,
                        "correlationTargetList": correlationTargetList,
                        "identity": identity,
                        "latest": latest,
                        "skipDraftCheck": skipDraftCheck,
                        "onStarted": function(data, title, processName){
                            var application;
                            if (data.work){
                                var work = data.work;
                                var options = {
                                    "draft": work,
                                    "draftData":data.data||{},
                                    "appId": "process.Work"+(new o2.widget.UUID).toString(),
                                    "desktopReload": false
                                };
                                if( !layout.inBrowser && afterCreated )options.onPostLoadForm = afterCreated;
                                application = layout.desktop.openApplication(null, "process.Work", options);
                            }else{
                                var currentTask = [];
                                data.each(function(work){
                                    if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
                                }.bind(this));

                                if (currentTask.length==1){
                                    var options = {"workId": currentTask[0], "appId": currentTask[0]};
                                    if( !layout.inBrowser && afterCreated )options.onPostLoadForm = afterCreated;
                                    application = layout.desktop.openApplication(null, "process.Work", options);
                                }else{}
                            }

                            if (callback) callback(data);

                            if(layout.inBrowser && afterCreated){
                                afterCreated(application)
                            }
                        }.bind(this)
                    });
                    starter.load();
                }
            });
        },

        addRelated: function(path, module){
            if (!_form.relatedModules) _form.relatedModules = {};
            if (!_form.relatedModules[path]) _form.relatedModules[path] = new Set();
            _form.relatedModules[path].add(module);
        }
    };

    /**
     * 양식 편집 가능 여부를 조회합니다. 읽기 전용.
     * @member readonly
     * @static
     * @return {Boolean} 읽기 전용 여부.
     * @o2syntax
     * var readonly = this.form.readonly;
     */
    Object.defineProperty(this.form, "readonly", {
        get: function(){ return  !!_form.options.readonly; }
    });

    /**
     * 사용자가 제출할 때 선택한 라우트. 양식의 “의견 검증”과 “라우트 검증” 스크립트에서만 사용 가능. 읽기 전용. <b>(프로세스 양식에서만 사용 가능)</b>.
     * @member currentRouteName
     * @memberOf module:form
     * @static
     * @return {String} 사용자가 선택한 라우트.
     * @o2syntax
     * var currentRouteName = this.form.currentRouteName;
     */
    //this.form.currentRouteName = _form.json.currentRouteName;

    /**
     * 사용자가 제출할 때 선택한 라우트 별칭. 양식의 “의견 검증”과 “라우트 검증” 스크립트에서만 사용 가능. 읽기 전용. <b>(프로세스 양식에서만 사용 가능)</b>.
     * @member currentRouteAlias
     * @memberOf module:form
     * @static
     * @return {String} 사용자가 선택한 라우트 별칭.
     * @o2syntax
     * var currentRouteAlias = this.form.currentRouteAlias;
     */
    //this.form.currentRouteAlias = _form.json.currentRouteAlias;

    /**
     * 사용자가 제출할 때 입력한 의견. 양식의 “의견 검증”과 “라우트 검증” 스크립트에서만 사용 가능. 읽기 전용. <b>(프로세스 양식에서만 사용 가능)</b>.
     * @member opinion
     * @memberOf module:form
     * @static
     * @return {String} 사용자가 입력한 의견.
     * @o2syntax
     * var opinion = this.form.opinion;
     */
    //this.form.opinion = _form.json.opinion;

    /**
     * 제출 시 사용자의 손글씨 의견과 녹음 의견. 양식의 “의견 검증”과 “라우트 검증” 스크립트에서만 사용 가능. 읽기 전용. <b>(프로세스 양식에서만 사용 가능)</b>.
     * @member medias
     * @memberOf module:form
     * @static
     * @return {Blob[]} 손글씨 의견과 녹음 의견 배열. 둘 다 HTML5 blob 유형 파일.
     * @o2syntax
     * var medias = this.form.medias;
     */
    this.form.medias = [];

    this.target = ev.target;
    this.event = ev.event;
    this.status = ev.status;


    this.session = layout.desktop.session;
    this.Actions = o2.Actions;

    this.query = function(option){
        // options = {
        //      "name": "statementName",
        //      "data": "json data",
        //      "firstResult": 1,
        //      "maxResults": 100,
        //      "success": function(){},
        //      "error": function(){},
        //      "async": true or false, default is true
        // }
        if (option){
            var json = (option.data) || {};
            if (option.firstResult) json.firstResult = option.firstResult.toInt();
            if (option.maxResults) json.maxResults = option.maxResults.toInt();
            o2.Actions.get("x_query_assemble_surface").executeStatement(option.name, json, success, error, options.async);
        }
    }
    this.Table = MWF.xScript.createTable();
};

if( !MWF.xScript.createTable )MWF.xScript.createTable = function(){
    return function(name){
        this.name = name;
        this.action = o2.Actions.load("x_query_assemble_surface").TableAction;

        this.listRowNext = function(id, count, success, error, async){
            return this.action.listRowNext(this.name, id, count, success, error, async);
        };
        this.listRowPrev = function(id, count, success, error, async){
            return this.action.listRowPrev(this.name, id, count, success, error, async);
        };
        this.listRowSelect = function(where, orderBy, size, success, error, async){
            return this.action.listRowSelect(this.name, {"where": where, "orderBy": orderBy, "size": size || ""}, success, error, async);
        };
        this.listRowSelectWhere = function(where, success, error, async){
            return this.action.listRowSelectWhere(this.name, where, success, error, async);
        };
        this.rowCountWhere = function(where, success, error, async){
            return this.action.rowCountWhere(this.name, where, success, error, async);
        };
        this.deleteRow = function(id, success, error, async){
            return this.action.rowDelete(this.name, id, success, error, async);
        };
        this.deleteAllRow = function(success, error, async){
            return this.action.rowDeleteAll(this.name, success, error, async);
        };
        this.getRow = function(id, success, error, async){
            return this.action.rowGet(this.name, id, success, error, async);
        };
        this.insertRow = function(data, success, error, async){
            return this.action.rowInsert(this.name, data, success, error, async);
        };
        this.addRow = function(data, success, error, async){
            return this.action.rowInsertOne(this.name, data, success, error, async);
        };
        this.updateRow = function(id, data, success, error, async){
            return this.action.rowUpdate(this.name, id, data, success, error, async);
        };
        this.partUpdateRow = function(id, data, success, error, async){
            return this.action.rowPartUpdate(this.name, id, data, success, error, async);
        };
    }
};

var getArrayJSONData = function(jData, p, _form){
    return new MWF.xScript.JSONData(jData, function(data, key, _self){
        var p = {"getKey": function(){return key;}, "getParent": function(){return _self;}};
        while (p && !_form.forms[p.getKey()]) p = p.getParent();
        //if (p) if (p.getKey()) if (_forms[p.getKey()]) _forms[p.getKey()].resetData();
        var k = (p) ? p.getKey() : "";
        if (k) if(_form.forms[k]) if(_form.forms[k].resetData) _form.forms[k].resetData();
        //if(p) if(p.getKey()) if(_forms[p.getKey()]) if(_forms[p.getKey()].render) _forms[p.getKey()].render();
    }, "", p, _form);
};
if ( !MWF.xScript.JSONData )MWF.xScript.JSONData = function(data, callback, key, parent, _form){
    var getter = function(data, callback, k, _self){
        return function(){
            var t = typeOf(data[k]);
            if (["array","object"].indexOf(t)===-1){
                return data[k]
            }else{
                if (t==="array"){
                    //if (!MWF.xScript.ArrayData){
                    //     var ArrayData = function(data, callback, key, parent, _form){
                    //         MWF.xScript.JSONData.call(this, data, callback, key, parent, _form);
                    //         Array.call(this, data);
                    //     };
                    //     Object.assign(ArrayData.prototype, MWF.xScript.JSONData.prototype);
                    //     Object.assign(ArrayData.prototype, Array.prototype);
                    //}
                    //return new MWF.xScript.ArrayData(data[k], callback, k, _self, _form);
                    //return new ArrayData(data[k], callback, k, _self, _form)
                    // var arr = Array.clone(data[k]);
                    // for (x in arr){
                    //     if (typeof x === 'number' && !isNaN(x)){
                    //         arr
                    //     }
                    // }

                    //return data[k];
                    if (window.Proxy){
                        var arr = new Proxy(data[k], {
                            get: function(o, k){
                                return (o2.typeOf(o[k])==="object") ? getArrayJSONData(o[k], _self, _form) : o[k];
                            },
                            set: function(o, k, v){
                                o[k] = v;
                                if (callback) callback(o, k, _self);
                                return true;
                            }
                        });
                        return arr;
                    }else{
                        var arr =[];
                        data[k].forEach(function(d, i){
                            arr.push((o2.typeOf(d)==="object") ? getArrayJSONData(d, _self, _form) : d);
                        });
                        return arr;
                    }


                    // var arr =[];
                    // data[k].forEach(function(d, i){
                    //     arr.push((o2.typeOf(d)==="object") ? getArrayJSONData(d, _self, _form) : d);
                    // });
                    // return arr;
                    //return getArrayJSONData(data[k], _self, _form);
                }else{
                    return new MWF.xScript.JSONData(data[k], callback, k, _self, _form);
                }
                // var obj =
                // if (t==="array") obj.constructor = Array;
                // return obj;
            }
            //return (["array","object"].indexOf(typeOf(data[k]))===-1) ? data[k] : new MWF.xScript.JSONData(data[k], callback, k, _self, _form);
        };
    };
    var setter = function(data, callback, k, _self){
        return function(v){
            data[k] = v;
            //debugger;
            //this.add(k, v, true);
            if (callback) callback(data, k, _self);
        }
    };
    var define = function(){
        var o = {};
        for (var k in data) o[k] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, k, this]),"set": setter.apply(this, [data, callback, k, this])};
        o["length"] = {"get": function(){return Object.keys(data).length;}};
        o["some"] = {"get": function(){return data.some;}};
        MWF.defineProperties(this, o);

        var methods = {
            "getKey": {"value": function(){ return key; }},
            "getParent": {"value": function(){ return parent; }},
            "toString": {"value": function() { return data.toString();}},
            "setSection": {"value": function(newKey, newValue){
                    this.add(newKey, newValue, true);
                    try {
                        var path = [this.getKey()];
                        p = this.getParent();
                        while (p && p.getKey()){
                            path.unshift(p.getKey());
                            p = p.getParent();
                        }
                        if (path.length) _form.sectionListObj[path.join(".")] = newKey;
                    }catch(e){

                    }
                }},

            "add": {"value": function(newKey, newValue, overwrite, noreset){
                    if( newKey.test(/^\d+$/) ){
                        throw new Error("Field name '"+newKey+"' cannot contain only numbers" );
                    }
                    if (arguments.length<2 || newKey.indexOf("..")===-1){
                        var flag = true;
                        var type = typeOf(data);
                        if (type==="array"){
                            if (arguments.length<2){
                                data.push(newKey);
                                newValue = newKey;
                                newKey = data.length-1;
                            }else{
                                if (!newKey && newKey!==0){
                                    data.push(newValue);
                                    newKey = data.length-1;
                                }else{
                                    if (newKey>=data.length){
                                        data.push(newValue);
                                        newKey = data.length-1;
                                    }else{
                                        if (overwrite) data[newKey] = newValue;
                                        newValue = data[newKey];
                                        flag = false;
                                    }
                                }
                            }
                            if (flag){
                                var o = {};
                                o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
                                MWF.defineProperties(this, o);
                            }
                            if (!noreset) this[newKey] = newValue;
                        }else if (type==="object"){
                            if (!this.hasOwnProperty(newKey)){
                                if (!data[newKey] || overwrite){
                                    data[newKey] = newValue;
                                }
                                newValue = data[newKey];

                                if (flag){
                                    var o = {};
                                    o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
                                    MWF.defineProperties(this, o);
                                }
                                if (!noreset) this[newKey] = newValue;
                            }else{
                                if (!Object.getOwnPropertyDescriptor(this, newKey).get){
                                    var o = {};
                                    o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
                                    MWF.defineProperties(this, o);
                                }
                                if (overwrite){
                                    data[newKey] = newValue;
                                    if (!noreset)  this[newKey] = newValue;
                                }
                            }
                        }
                        return this[newKey];
                    }else{
                        var keys = newKey.split("..");
                        var kk = keys.shift();
                        var d = this.add(kk, {}, false, true);
                        if (keys.length) return d.add(keys.join(".."), newValue, overwrite, noreset);
                        return d;
                    }
                }},
            "check": {
                "value": function(kk, v){
                    var value = typeOf( v ) === "null" ? "" : v;
                    this.add(kk, value, false, true);
                }
            },
            "del": {"value": function(delKey){
                    if (!this.hasOwnProperty(delKey)) return null;
                    // delete data[delKey];
                    // delete this[delKey];
                    data[delKey] = "";
                    this[delKey] = "";
                    return this;
                }}
        };
        MWF.defineProperties(this, methods);

        //this.getKey = function(){ return key; };
        //this.getParent = function(){ return parent; };
        //this.toString = function() { return data.toString();};
        //this.add = function(newKey, newValue, overwrite){
        //    var flag = true;
        //    var type = typeOf(data);
        //    if (!this.hasOwnProperty(newKey)){
        //        if (type=="array"){
        //            if (arguments.length<2){
        //                data.push(newKey);
        //                newValue = newKey;
        //                newKey = data.length-1;
        //            }else{
        //                debugger;
        //                if (!newKey && newKey!=0){
        //                    data.push(newValue);
        //                    newKey = data.length-1;
        //                }else{
        //                    flag == false;
        //                }
        //            }
        //        }else{
        //            data[newKey] = newValue;
        //        }
        //        //var valueType = typeOf(newValue);
        //        //var newValueData = newValue;
        //        //if (valueType=="object" || valueType=="array") newValueData = new MWF.xScript.JSONData(newValue, callback, newKey, this);
        //        //if (valueType=="null") newValueData = new MWF.xScript.JSONData({}, callback, newKey, this);
        //        if (flag){
        //            var o = {};
        //            o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
        //            MWF.defineProperties(this, o);
        //        }
        //        this[newKey] = newValue;
        //    }else{
        //        if (overwrite) this[newKey] = newValue;
        //    }
        //
        //    //var valueType = typeOf(newValue);
        //    //var newValueData = newValue;
        //    //if (valueType=="object" || valueType=="array") newValueData = new MWF.xScript.JSONData(newValue, callback, newKey, this);
        //    //if (valueType=="null") newValueData = new MWF.xScript.JSONData({}, callback, newKey, this);
        //    //
        //    //this[newKey] = newValueData;
        //
        //    return this[newKey];
        //};
        //this.del = function(delKey){
        //    if (!this.hasOwnProperty(delKey)) return null;
        //    delete data[newKey];
        //    delete this[newKey];
        //    return this;
        //};
    };

    var type = typeOf(data);
    if (type==="object" || type==="array") define.apply(this);
};

//MWF.xScript.createDict = function(application){
//    return function(name){
//        var applicationId = application;
//        this.name = name;
//        //MWF.require("MWF.xScript.Actions.DictActions", null, false);
//        var action = MWF.Actions.get("x_processplatform_assemble_surface");
//
//        this.get = function(path, success, failure){
//            debugger;
//            var value = null;
//            if (path){
//                var arr = path.split(/\./g);
//                var ar = arr.map(function(v){
//                    return encodeURIComponent(v);
//                });
//                //var p = path.replace(/\./g, "/");
//                var p = ar.join("/");
//                action.getDictData(encodeURIComponent(this.name), applicationId, p, function(json){
//                    value = json.data;
//                    if (success) success(json.data);
//                }, function(xhr, text, error){
//                    if (failure) failure(xhr, text, error);
//                }, false);
//            }else{
//                action.getDictRoot(encodeURIComponent(this.name), applicationId, function(json){
//                    value = json.data;
//                    if (success) success(json.data);
//                }, function(xhr, text, error){
//                    if (failure) failure(xhr, text, error);
//                }, false);
//            }
//
//            return value;
//        };
//
//        this.set = function(path, value, success, failure){
//            var p = path.replace(/\./g, "/");
//            action.setDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
//                if (success) success(json.data);
//            }, function(xhr, text, error){
//                if (failure) failure(xhr, text, error);
//            });
//        };
//        this.add = function(path, value, success, failure){
//            var p = path.replace(/\./g, "/");
//            action.addDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
//                if (success) success(json.data);
//            }, function(xhr, text, error){
//                if (failure) failure(xhr, text, error);
//            });
//        };
//        this["delete"] = function(path, success, failure){
//            var p = path.replace(/\./g, "/");
//            action.deleteDictData(encodeURIComponent(this.name), applicationId, p, function(json){
//                if (success) success(json.data);
//            }, function(xhr, text, error){
//                if (failure) failure(xhr, text, error);
//            });
//        };
//        this.destory = this["delete"];
//    }
//};
// var dictLoaded = {};
if( !MWF.xScript.dictLoaded )MWF.xScript.dictLoaded = {};

if( !MWF.xScript.createDict ){
    MWF.xScript.addDictToCache = function ( options, path, json ) {
        if( !path )path = "root";
        if( path.indexOf("root") !== 0 )path = "root." + path ;

        var type = options.appType || "process";
        var enableAnonymous = ( options.enableAnonymous || options.anonymous ) || false;

        var appFlagList = [];
        if( options.application )appFlagList.push( options.application );
        if( options.appId )appFlagList.push( options.appId );
        if( options.appName )appFlagList.push( options.appName );
        if( options.appAlias )appFlagList.push( options.appAlias );

        var dictFlagList = [];
        if( options.id )dictFlagList.push( options.id );
        if( options.name )dictFlagList.push( options.name );
        if( options.alias )dictFlagList.push( options.alias );

        var cache = {};
        cache[path] = json;

        for( var i=0; i<appFlagList.length; i++ ){
            for( var j=0; j<dictFlagList.length; j++ ){
                var k = dictFlagList[j] + type + appFlagList[i] + enableAnonymous;
                if( !MWF.xScript.dictLoaded[k] ){
                    MWF.xScript.dictLoaded[k] = cache; //같은 객체를 가리킴
                    // MWF.xScript.dictLoaded[k][path] = json; //서로 다른 객체를 가리킴
                }else if( i===0 && j===0 ){
                    MWF.xScript.setDictToCache( k, path ,json );
                    var arr = path.split(/\./g);
                    var p;
                    var cache = MWF.xScript.dictLoaded[k];
                    for( var l=0 ; l<arr.length; l++ ){
                        p = l === 0 ? arr[0] : ( p + "." + arr[l] );
                        if( cache[ p ] )break;
                    }
                    if( p ){
                        var mathP = p+".";
                        Object.keys( cache ).each( function( path, idx){
                            if( path.indexOf( mathP ) === 0 )delete cache[path];
                        })
                    }
                }
            }
        }
    };

    MWF.xScript.getMatchedDict = function(key, path){
        if( !path )path = "root";
        if( path.indexOf("root") !== 0 )path = "root." + path ;

        var arr = path.split(/\./g);
        if( MWF.xScript.dictLoaded[key] ){
            var dicts = MWF.xScript.dictLoaded[key];
            var list = Array.clone(arr);
            var p;
            var dict;
            for( var i=0 ; i<arr.length; i++ ){
                p = i === 0 ? arr[0] : ( p + "." + arr[i] );
                list.shift();
                if( dicts[ p ] ){
                    dict = dicts[ p ];
                    break;
                }
            }
            return {
                dict : dict,
                unmatchedPathList : list
            }
        }
        return {
            dict : null,
            unmatchedPathList : list
        }
    };

    MWF.xScript.insertDictToCache = function(key, path, json){
        var p = path;
        if( !p )p = "root";
        if( p.indexOf("root") !== 0 )p = "root." + p ;

        if( MWF.xScript.dictLoaded[key] ){
            var matchedDict = MWF.xScript.getMatchedDict( key, path );
            var dict = matchedDict.dict;
            var list = matchedDict.unmatchedPathList;
            if( !dict ) {
                MWF.xScript.dictLoaded[key][p] = json;
            }else if( !list || list.length === 0 ){
                MWF.xScript.dictLoaded[key][p] = json;
            }else{
                for( var j=0; j<list.length-1; j++ ){
                    if( !dict[ list[j] ] ){
                        dict[ list[j] ] = {};
                    }
                    dict = dict[ list[j] ];
                }
                var lastPath = list[list.length-1];
                if( !dict[lastPath] ){
                    dict[lastPath] = json;
                }else if( typeOf( dict[lastPath] ) === "array" ){
                    dict[lastPath].push( json );
                }
            }
        }else{
            MWF.xScript.dictLoaded[key] = {};
            MWF.xScript.dictLoaded[key][p] = json;
        }
    };


    MWF.xScript.setDictToCache = function(key, path, json){
        var p = path;
        if( !p )p = "root";
        if( p.indexOf("root") !== 0 )p = "root." + p ;

        if( MWF.xScript.dictLoaded[key] ){
            var matchedDict = MWF.xScript.getMatchedDict( key, path );
            var dict = matchedDict.dict;
            var list = matchedDict.unmatchedPathList;
            if( !dict ){
                MWF.xScript.dictLoaded[key][p] = json;
            }else if( !list || list.length === 0 ){
                MWF.xScript.dictLoaded[key][p] = json;
            }else{
                for( var j=0; j<list.length-1; j++ ){
                    if( !dict[ list[j] ] ){
                        dict[ list[j] ] = {};
                    }
                    dict = dict[ list[j] ];
                }
                dict[list[list.length-1]] = json;
            }
        }else{
            MWF.xScript.dictLoaded[key] = {};
            MWF.xScript.dictLoaded[key][p] = json;
        }
    };

    MWF.xScript.getDictFromCache = function( key, path ){
        var matchedDict = MWF.xScript.getMatchedDict( key, path );
        var dict = matchedDict.dict;
        var list = matchedDict.unmatchedPathList;
        if( dict ){
            for( var j=0; j<list.length; j++ ){
                dict = dict[ list[j] ];
                if( !dict )return null;
            }
            return dict;
        }
        return null;
    };

    MWF.xScript.deleteDictToCache = function(key, path){
        var matchedDict = MWF.xScript.getMatchedDict( key, path );
        var dict = matchedDict.dict;
        var list = matchedDict.unmatchedPathList;

        if( dict){
            for( var j=0; j<list.length-1; j++ ){
                dict = dict[ list[j] ];
                if( !dict )return;
            }
            if( list.length ){
                delete dict[list[list.length-1]];
            }
        }
    };


    MWF.xScript.createDict = function(application, appType){
        //optionsOrName : {
        //  type : "", //기본값 process, process  cms  portal 가능
        //  application : "", //프로세스/CMS의 이름/별칭/id, 기본값은 현재 앱
        //  name : "", // 데이터 사전 이름/별칭/id
        //  anonymous : false //로그인하지 않은 상태에서 CMS 데이터 사전 읽기 허용, 이 매개변수 이름은 enableAnonymous도 가능
        //}
        //또는 name: "" // 데이터 사전 이름/별칭/id
        return function(optionsOrName){
            var options = optionsOrName;
            if( typeOf( options ) == "string" ){
                options = {
                    name : options,
                    type: appType,
                    application: application
                };
            }
            var name = this.name = options.name;
            var type;
            if( options.type === "service"){
                type = options.type;
            }else{
                type = ( options.type && options.application ) ?  options.type : "process";
            }
            var applicationId = options.application || application;
            var enableAnonymous = ( options.enableAnonymous || options.anonymous ) || false;

            var opt = {
                "appType" : type,
                "name" : name,
                "appId" : applicationId,
                "enableAnonymous" : enableAnonymous
            };

            var key = name+type+applicationId+enableAnonymous;
            // if (!dictLoaded[key]) dictLoaded[key] = {};
            // this.dictData = dictLoaded[key];

            //MWF.require("MWF.xScript.Actions.DictActions", null, false);
            var action;
            if( type === "cms" ) {
                action = MWF.Actions.get("x_cms_assemble_control");
            }else if( type === "portal" ){
                action = MWF.Actions.get("x_portal_assemble_surface");
            }else if( type === "service" ){
                key = name+type+enableAnonymous;
                action = MWF.Actions.get("x_program_center");
            }else{
                action = MWF.Actions.get("x_processplatform_assemble_surface");
            }

            var encodePath = function( path ){
                var arr = path.split(/\./g);
                var ar = arr.map(function(v){
                    return encodeURIComponent(v);
                });
                return ( type === "portal" || type === "service" ) ? ar.join(".") : ar.join("/");
            };

            this.get = function(path, success, failure, async, refresh){
                var value = null;

                if (success===true) async=true;
                if (failure===true) async=true;

                if (!refresh ){
                    var data = MWF.xScript.getDictFromCache( key, path );
                    if( data ){
                        if (success && o2.typeOf(success)=="function") success( data );
                        if( !!async ){
                            return Promise.resolve( data );
                        }else{
                            return data;
                        }
                    }
                }

                // var cb = function(json){
                //     value = json.data;
                //     MWF.xScript.addDictToCache(opt, path, value);
                //     if (success && o2.typeOf(success)=="function") value = success(json.data);
                //     return value;
                // }.ag().catch(function(xhr, text, error){ if (failure && o2.typeOf(failure)=="function") return failure(xhr, text, error); });

                var cb = function(json){
                    value = json.data;
                    MWF.xScript.addDictToCache(opt, path, value);
                    if (success && o2.typeOf(success)=="function") value = success(json.data);
                    return value;
                };

                var promise;
                if( type === "service" ){
                    if (path){
                        var p = encodePath( path );
                        promise = action.getDictData(encodeURIComponent(this.name), p, cb, null, !!async, false);
                    }else{
                        promise = action.getDictRoot(this.name, cb, null, !!async, false);
                    }
                }else{
                    if (path){
                        var p = encodePath( path );
                        promise = action[ ( (enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData" ) ](encodeURIComponent(this.name), applicationId, p, cb, null, !!async, false);
                    }else{
                        promise = action[ ( (enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot" ) ](this.name, applicationId, cb, null, !!async, false);
                    }
                }
                return (!!async) ? promise : value;

                // if (path){
                //     var p = encodePath( path );
                //     //var p = path.replace(/\./g, "/");
                //     action[ ( (enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData" ) ](encodeURIComponent(this.name), applicationId, p, function(json){
                //         value = json.data;
                //         // this.dictData[path] = value;
                //         MWF.xScript.addDictToCache(opt, path, value);
                //         if (success) success(json.data);
                //     }.bind(this), function(xhr, text, error){
                //         if (failure) failure(xhr, text, error);
                //     }, !!async);
                // }else{
                //     action[ ( (enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot" ) ](this.name, applicationId, function(json){
                //         value = json.data;
                //         // this.dictData["root"] = value;
                //         MWF.xScript.addDictToCache(opt, path, value);
                //         if (success) success(json.data);
                //     }.bind(this), function(xhr, text, error){
                //         if (failure) failure(xhr, text, error);
                //     }, !!async);
                // }

                //return value;
            };

            this.set = function(path, value, success, failure){
                var p = encodePath( path );
                //var p = path.replace(/\./g, "/");
                var successCallback = function(json){
                    MWF.xScript.setDictToCache(key, path, value);
                    if (success) return success(json.data);
                };
                var failureCallback = function(xhr, text, error){
                    if (failure) return failure(xhr, text, error);
                };
                if( type === "service" ){
                    return action.setDictData(encodeURIComponent(this.name), p, value, successCallback, failureCallback, false, false);
                }else{
                    return action.setDictData(encodeURIComponent(this.name), applicationId, p, value, successCallback, failureCallback, false, false);
                }
            };
            this.add = function(path, value, success, failure){
                var p = encodePath( path );
                //var p = path.replace(/\./g, "/");
                var successCallback = function(json){
                    MWF.xScript.insertDictToCache(key, path, value);
                    if (success) return success(json.data);
                };
                var failureCallback = function(xhr, text, error){
                    if (failure) return failure(xhr, text, error);
                };
                if( type === "service" ) {
                    return action.addDictData(encodeURIComponent(this.name), p, value, successCallback, failureCallback, false, false);
                }else{
                    return action.addDictData(encodeURIComponent(this.name), applicationId, p, value, successCallback, failureCallback, false, false);
                }
            };
            this["delete"] = function(path, success, failure){
                var p = encodePath( path );
                //var p = path.replace(/\./g, "/");
                var successCallback = function(json){
                    MWF.xScript.deleteDictToCache(key, path);
                    if (success) return success(json.data);
                };
                var failureCallback = function(xhr, text, error){
                    if (failure) return failure(xhr, text, error);
                };
                if( type === "service" ) {
                    return action.deleteDictData(encodeURIComponent(this.name), p, successCallback, failureCallback, false, false);
                }else{
                    return action.deleteDictData(encodeURIComponent(this.name), applicationId, p, successCallback, failureCallback, false, false);
                }
            };
            this.destory = this["delete"];
        }
    };
}
