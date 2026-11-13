/**
 * @readonly
 * @enum {String} ViewFilterDataLogic
 * @property {String} or color for a white square or piece.
 * @property {String} and color for a black square or piece.
 */

/**
 * ViewFilter 뷰 필터 조건
 * @typedef {Object} ViewFilter
 * @property {String} logic - 선택값: “and” 또는 “or”, 앞 조건과의 논리 연산 관계.
 * @property {String} path - 필터링할 data 데이터의 경로.
 * @property {String} comparison - 비교 연산자, 선택값:<br/>
 * <div style='padding-left:150px;'>
 * <b>equals</b> 또는 <b>==</b> 또는: 같음.<br/>
 * <b>notEquals</b> 또는 <b>!=</b>: 같지 않음.<br/>
 * <b>greaterThan</b> 또는 <b>></b>: 보다 큼.<br/>
 * <b>greaterThanOrEqualTo</b> 또는 <b>>=</b>: 크거나 같음.<br/>
 * <b>lessThan</b> 또는 <b><</b>: 보다 작음.<br/>
 * <b>lessThanOrEqualTo</b> 또는 <b><=</b>: 작거나 같음.<br/>
 * <b>like</b>: 부분 일치.<br/>
 * <b>notLike</b>: 일치하지 않음.<br/>
 * <b>range</b>: 일정 범위.<br/>
 * <b>in</b>: 특정 값들 중 하나.<br/>
 * </div>
 * @property {String} formatType  - 필터링 데이터의 데이터 타입, 선택값:
 * <div style='padding-left:150px;'>
 * <b>textValue</b>: 텍스트.<br/>
 * <b>numberValue</b>: 숫자.<br/>
 * <b>dateTimeValue</b>: 날짜 시간.<br/>
 * <b>dateValue</b> : 날짜.<br/>
 * <b>booleanValue</b> : 불리언 값.<br/>
 * </div>
 * @property {(String|Number|Boolean)} value - 필터 값, formatType에 맞는 데이터 유형의 값을 제공.
 * <br/>dateTimeValue 데이터 유형이면 날짜시간 형식 문자열 제공, 형식 예: “YYYY-MM-DD HH:MM:SS”.
 * <br/>dateValue 데이터 유형이면 날짜 형식 문자열 제공, 형식 예: “YYYY-MM-DD”.
 * <br/>comparison 값이 “range”이면 이 값은 범위의 첫 번째 값. comparison 값이 “in”이면 여러 값을 반각 쉼표","로 구분.
 * @property {(String|Number|Boolean)} otherValue  - comparison 값이 “range”이면 범위의 두 번째 값. comparison 값이 “range”가 아니면 무시.
 * @example
 *  {
 *    "logic":"and",
 *    "path":"$work.title",
 *    "comparison":"like",
 *    "value":"관련",
 *    "formatType":"textValue"
 * }
 * @example
 *  {
 *    "logic":"and",
 *    "path":"month",
 *    "comparison":"in",
 *    "value":"1월,2월,3월",
 *    "formatType":"textValue"
 * }
 */

/**
 * StatementFilter 조회 뷰의 필터 조건
 * @typedef {Object} StatementFilter
 * @property {String} path - 필터링할 data 데이터의 경로.
 * 구문 형식이 JPQL이면 조회 구문의 "테이블 별칭.필드명" 형식. 예: "o.title".
 * v8.0 이후 시스템에 네이티브 SQL이 추가되어 데이터베이스 필드명을 사용. 예: "xtitle".
 * @property {String} comparison - 비교 연산자, 선택값:<br/>
 * <div style='padding-left:150px;'>
 * <b>equals</b> 또는 <b>==</b> 또는: 같음.<br/>
 * <b>notEquals</b> 또는 <b>!=</b>: 같지 않음.<br/>
 * <b>greaterThan</b> 또는 <b>></b>: 보다 큼.<br/>
 * <b>greaterThanOrEqualTo</b> 또는 <b>=></b> : 크거나 같음.<br/>
 * <b>lessThan</b> 또는 <b><</b>: 보다 작음.<br/>
 * <b>lessThanOrEqualTo</b> 또는 <b><=</b>: 작거나 같음.<br/>
 * <b>like</b>: 부분 일치.<br/>
 * <b>notLike</b>: 일치하지 않음.<br/>
 * </div>
 * @property {String} formatType  - 필터링 데이터의 데이터 타입, 선택값:
 * <div style='padding-left:150px;'>
 * <b>textValue</b>: 텍스트.<br/>
 * <b>numberValue</b>: 숫자.<br/>
 * <b>dateTimeValue</b>: 날짜 시간.<br/>
 * <b>dateValue</b> : 날짜만.<br/>
 * <b>timeValue</b> : 시간만.<br/>
 * <b>booleanValue</b> : 불리언 값.<br/>
 * </div>
 * @property {(String|Number|Boolean)} value - 필터 값, formatType에 맞는 데이터 유형의 값 제공. dateTimeValue 유형이면 “YYYY-MM-DD HH:MM:SS” 형식의 날짜 문자열 제공.
 * dateValue 유형이면 “YYYY-MM-DD” 형식의 날짜 문자열, timeValue 유형이면 “HH:MM:SS” 형식의 시간 문자열 제공.
 * @example
 *{
 *    "path":"o.title", //조회 구문 형식이 jpql이면 o.title, 네이티브 sql이면 xtitle 사용
 *    "comparison":"like",
 *    "value":"관련",
 *    "formatType":"textValue"
 *}
 */

/**
 * StatementParameter  조회 뷰의 필터 조건 값 매개변수, 조회 구문 where 절의 ":person" 형태 매개변수에 값 할당<br/>
 * v8.0 이후 시스템에 물음표+숫자 형태의 매개변수(예: "?1")가 추가되었으며, 사용법은 ":field"와 같습니다.
 * 다음 규칙이 있습니다:<br/>
 * 1. 매개변수 이름이 다음 값이면 서버에서 자동 할당: person(현재 사용자), identityList(현재 사용자 신분 목록), unitList(현재 사용자 직속 조직), unitAllList(현재 사용자 소속 모든 조직), groupList(현재 사용자 소속 그룹). v8.0 이후 시스템이 자동 해석하므로 이런 매개변수는 전달할 필요 없음.<br/>
 * 2. 날짜를 비교하는 경우 Date 유형을 전달해야 합니다.<br/>
 * 3. 연산자가 like, noLike이면 부분 일치 조회이며 값은 "%{value}%".
 * 4、
 * @typedef {Object} StatementParameter
 * @example
 * {
 *    "person" : "", //v8.0 이후 시스템이 자동 해석하므로 이 파라미터를 전달할 필요 없음.
 *    "startTime" : (new Date("2020-01-01")), //날짜 형식
 *    "applicationName" : "%test%", //like 또는 notlike
 *    "processName" : "test프로세스", //그 외에는 확정된 값 입력
 *    "?1": "관련" //v8.0 이후 조회문은 물음표+숫자 형식 파라미터 지원
 * }
 */

/*********this.org 의 typedef 시작********/

/**
 * 이 API의 this.org 모듈에서 그룹을 조회 파라미터로 사용할 때 그룹의 distinguishedName, name, id, unique 속성값 또는 이 속성값을 포함한 객체를 사용할 수 있습니다.
 * @typedef {(String|Object)} GroupFlag
 * @example
 * //아래는 모두 유효한 파라미터
 *
 * //그룹 속성값
 * "업무보고열람자@ReportReader@G" //그룹 식별명
 * "cce8bc22-225a-4f85-8132-7374d546886e" //id
 * "ReportReader" //그룹 unique
 * "업무보고열람자" //그룹 이름
 *
 * //다음 속성을 포함한 객체
 * { "distinguishedName": "업무보고열람자@ReportReader@G" } //그룹 식별명
 * { "unique": "cce8bc22-225a-4f85-8132-7374d546886e" } //그룹 id
 * { "unique": "ReportReader" } //그룹 unique
 * { "name": "업무보고열람자" } //그룹 이름
 */


/**
 * 이 API의 this.org 모듈에서 사용자(개인)를 조회 파라미터로 사용할 때 사용자의 distinguishedName, name, id, unique 속성값 또는 이 속성을 포함한 객체를 사용할 수 있습니다.
 * @typedef {(String|Object)} PersonFlag
 * @example
 * //아래는 모두 유효한 파라미터
 *
 * //사용자 속성값
 * "김철수@lisi@P" //사용자 식별명
 * "cce8bc22-225a-4f85-8132-7374d546886e" //사용자 id
 * "lisi" //사용자 unique
 *
 * //다음 속성을 포함한 객체
 * { "distinguishedName": "김철수@lisi@P" } //사용자 식별명
 * { "id": "cce8bc22-225a-4f85-8132-7374d546886e" } //사용자 id
 * { "unique": "lisi" } //사용자 unique
 */

/**
 * 이 API의 this.org 모듈에서 역할을 조회 파라미터로 사용할 때 역할의 distinguishedName, name, id, unique 속성값 또는 이 속성을 포함한 객체를 사용할 수 있습니다.
 * @typedef {(String|Object)} RoleFlag
 * @example
 * //아래는 모두 유효한 파라미터

 * //역할 속성값
 * "PersonManager@PersonManagerSystemRole@R" //역할 식별명
 * "85874fbc-9af0-4a07-bc2f-0ab899cc4c7d" //역할 id
 * "PersonManagerSystemRole" //역할 unique
 * "PersonManager" //역할 이름
 *
 * //다음 속성을 포함한 객체
 * { "distinguishedName": "PersonManager@PersonManagerSystemRole@R" } //역할 식별명
 * { "id": "85874fbc-9af0-4a07-bc2f-0ab899cc4c7d" } //역할 id
 * { "unique": "PersonManagerSystemRole" } //역할 unique
 * { "name": "PersonManager" } //역할 이름
 */

/**
 * 이 API의 this.org 모듈에서 신분을 조회 파라미터로 사용할 때 신분의 distinguishedName, id, unique 속성값 또는 이 속성을 포함한 객체를 사용할 수 있습니다.
 * @typedef {(String|Object)} IdentityFlag
 * @example
 * //아래는 모두 유효한 파라미터

 * //역할 속성값
 * "홍길동@903dbb04-085c-4bb1-9c24-3009bd5f7372@I" //신분 식별명
 * "903dbb04-085c-4bb1-9c24-3009bd5f7372" //신분 id
 * "903dbb04-085c-4bb1-9c24-3009bd5f7372" //신분 unique, 시스템 기본 unique는 id와 동일
 *
 * //다음 속성을 포함한 객체
 * { "distinguishedName": "홍길동@903dbb04-085c-4bb1-9c24-3009bd5f7372@I" } //신분 식별명
 * { "id": "903dbb04-085c-4bb1-9c24-3009bd5f7372" } //신분 id
 * { "unique": "903dbb04-085c-4bb1-9c24-3009bd5f7372" } //신분 unique, 시스템 기본 unique는 id와 동일
 */

/**
 * 이 API의 this.org 모듈에서 조직을 조회 파라미터로 사용할 때 조직의 distinguishedName, id, unique 속성값 또는 이 속성을 포함한 객체를 사용할 수 있습니다.
 * @typedef {(String|Object)} UnitFlag
 * @example
 * //아래는 모두 유효한 파라미터

 * //조직 속성값
 * "개발부@kfb@U" //조직 식별명
 * "cce8bc22-225a-4f85-8132-7374d546886e" //조직 id
 * "kfb" //조직 unique
 *
 * //다음 속성을 포함한 객체
 * { "distinguishedName": "개발부@kfb@U" } //조직 식별명
 * { "id": "cce8bc22-225a-4f85-8132-7374d546886e" } //조직 id
 * { "unique": "kfb" } //조직 unique
 */

MWF.xScript = MWF.xScript || {};
MWF.xScript.ViewEnvironment = function (ev) {
    var _form = ev.view;

    /**
     * 문자열, 현재 앱 유형.<br/><br/>
     * <table>
     *   <tr>
     *      <th>값</th>
     *      <th>앱 유형</th>
     *   </tr>
     *   <tr>
     *      <td>cms</td>
     *      <td>콘텐츠 관리</td>
     *   </tr>
     *   <tr>
     *      <td>process</td>
     *      <td>프로세스 관리</td>
     *   </tr>
     *   <tr>
     *      <td>portal</td>
     *      <td>포털 관리</td>
     *   </tr>
     *   <tr>
     *      <td>query</td>
     *      <td>데이터 센터</td>
     *   </tr>
     * </table>
     * @module appType
     * @o2cn 앱 유형
     * @o2category web
     * @o2ordernumber 170
     * @instance
     * @example
     * var title;
     * if( this.appType === "cms"  ){
     *     title = this.documentContext.getDocument().title
     * }else if( this.appType === "process"  ){
     *     title = this.workContext.getWork().title
     * }
     **/
    this.appType = "query";

    this.library = COMMON;
    //this.library.version = "4.0";

    //data
    // var getJSONData = function (jData) {
    //     return new MWF.xScript.JSONData(jData, function (data, key, _self) {
    //         var p = { "getKey": function () { return key; }, "getParent": function () { return _self; } };
    //         while (p && !_forms[p.getKey()]) p = p.getParent();
    //         if (p) if (p.getKey()) if (_forms[p.getKey()]) _forms[p.getKey()].resetData();
    //     });
    // };
    // this.setData = function (data) {
    //     this.data = getJSONData(data);
    //     this.data.save = function (callback) {
    //         var formData = {
    //             "data": data,
    //             "sectionList": _form.getSectionList()
    //         };
    //         form.workAction.saveData(function (json) { if (callback) callback(); }.bind(this), null, work.id, jData);
    //     }
    // };
    // this.setData(_data);

    //dict
    /**
     * this.Dict는 유틸리티 클래스로, 프로세스, 콘텐츠 관리, 포털, 서비스 관리에서 데이터 사전을 만들었다면 this.Dict 클래스로 데이터 사전을 추가·삭제·수정·조회할 수 있습니다.<br/>
     * v8.0부터 포털과 서비스 관리에서 데이터 사전 생성을 지원합니다.
     * @module Dict
     * @o2cn 데이터 사전
     * @o2category web
     * @o2ordernumber 120
     * @param {(String|Object)} optionsOrName 데이터 사전 식별 문자열 또는 객체.
     * <div>이 앱의 데이터 사전을 다루려면 optionsOrName을 string으로 설정합니다.</div>
     * <pre><code class='language-js'>var dict = new this.Dict("bulletinDictionary"); //데이터 사전의 이름, 별칭 또는 id
     * </code></pre>
     * <div>다른 앱의 데이터 사전을 다루려면 options를 JsonObject로 설정합니다</div>
     * <pre><code class='language-js'>var dict = new this.Dict({
     *     //type: 앱 유형. process  cms  portal service 가능.
     *     //프로세스와 콘텐츠 관리에서 이 옵션이 없거나 값이 빈 문자열이면 참조하는 스크립트와 참조되는 스크립트 설정 유형이 같음을 의미합니다.
     *     //예를 들어 프로세스 A 앱 스크립트에서 프로세스 B 앱의 스크립트 설정을 참조하면 type을 생략할 수 있습니다.
     *     //이전 버전과의 호환을 위해 포털에서 사용할 때는 type을 지정해야 하며, 그렇지 않으면 기본값은 process입니다
     *    type : "cms",
     *    application : "bulletin", //데이터 사전이 있는 프로세스, 포털, CMS의 이름, 별칭, id, 기본값은 현재 앱, 서비스 관리에서는 무시
     *    name : "bulletinDictionary", // 데이터 사전의 이름, 별칭, id
     *    anonymous : true //로그인하지 않은 사용자의 cms 데이터 사전 읽기 허용, type이 cms일 때만 유효, 기본값 false, 파라미터 이름은 enableAnonymous도 가능
     * });
     *
     * //서비스 관리의 데이터 사전 참조
     * var dict = new this.Dict({
     *    "type": "service",
     *    "name": "dictName"
     * });
     *
     * //프로세스 관리의 데이터 사전 참조
     * var dict = new this.Dict({
     *    "type": "process",
     *    "application": "appName",
     *    "name": "dictName"
     * });
     *
     * //콘텐츠 관리의 데이터 사전 참조
     * var dict = new this.Dict({
     *     "type": "cms",
     *     "application": "appName",
     *    "name": "dictName"
     * });
     *
     * //포털 관리의 데이터 사전 참조
     * var dict = new this.Dict({
     *    "type": "portal",
     *    "application": "appName",
     *    "name": "dictName"
     * });
     *
     * </code></pre>
     * @return {Object} Dict 객체
     * @o2syntax
     * //페이지, 양식, 프로세스의 각 내장 스크립트에서 this.Dict()로 현재 앱 또는 다른 앱의 데이터 사전 데이터를 추가·삭제·수정·조회할 수 있습니다. 예:
     * var dict = new this.Dict( options )
     */

    /**
     * 경로에 따라 데이터 사전의 데이터를 조회합니다.
     * @method get
     * @methodOf module:Dict
     * @static
     * @param {String} [path] 데이터 사전의 데이터 경로, 한글 사용 가능. 다단계 경로는 점(.)으로 구분. 값이 비어 있으면 데이터 사전의 모든 데이터를 조회.
     * @param {Function} [success] 데이터 조회 성공 시 콜백 함수.
     * @param {Function} [failure] 데이터 조회 실패 시 콜백.
     * @param {Boolean} [async] 비동기 실행 여부, 기본값 false.
     * @param {Boolean} [refresh] 로컬 캐시를 무시하고 서버에서 직접 조회할지 여부, 기본값 false. 스크립트에서 데이터 사전 값이 변하는 경우(예: 번호) true로 설정해야 함.
     * @return {(Promise|Object|Array|String|Number|Boolean)}
     * async가 true이면 Promise({@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise 설명})를 반환;
     * 그렇지 않으면 데이터 사전의 데이터를 반환하며, 유형은 데이터 사전 설정 시 지정한 것과 같습니다.
     * @o2syntax
     * var data = dict.get( path, success, failure, async, refresh )
     * @example
     * var dict = new this.Dict("bulletinDictionary");
     *
     * //매개변수가 없으면 모든 데이터를 동기 조회
     *  var data = dict.get()
     *
     *  //동기 실행, category 아래 key가 subCategory인 데이터 조회
     *  var data = dict.get("category.subCategory");
     *
     * //비동기 실행, 콜백으로 데이터 처리, category가 배열이면 0번째 항목 조회
     * dict.get("category.0", function(data){
     *      //data는 데이터 사전의 데이터
     *  }, function(xhr){
     *      //xhr은 xmlHttpRequest
     *  }, true //비동기 실행
     * )
     *
     * //비동기 실행, Promise로 결과 처리
     * var promise = dict.get("category", null, null, true);
     * promise.then( function(data){
     *     //data는 데이터 사전의 데이터
     * })
     * @example
     * <caption>
     *     아래 그림과 같이 설정된 데이터 사전
     * <img src='img/module/Dict/dict.png' />
     * </caption>
     * var dict = new this.Dict({
     *     //type: 앱 유형. process 또는 cms.
     *     //이 옵션이 없거나 값이 빈 문자열이면 참조하는 스크립트와 참조되는 스크립트 설정의 유형이 같음을 의미함.
     *     //예를 들어 프로세스 A 앱 스크립트에서 프로세스 B 앱의 스크립트 설정을 참조하면 type을 생략할 수 있습니다.
     *    type : "cms",
     *    application : "bulletin", //프로세스, CMS의 이름·별칭·id, 기본값은 현재 앱
     *    name : "bulletinDictionary", // 데이터 사전의 이름, 별칭, id
     *    anonymous : true //로그인하지 않은 상태에서 cms 데이터 사전 읽기 허용, type이 process이면 무효, 기본값 false, 매개변수 이름은 enableAnonymous도 가능
     * });
     *
     * var data = dict.get();
     * //data의 값은
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        }
     *    ]
     * }
     *
     *  var category = dict.get("category");
     *  //category의 값은
     *  [
     *     {
     *        "enable": true,
     *        "sequence": 1.0,
     *        "text": "회사 공지",
     *        "value": "company"
     *    },
     *     {
     *       "enable": "false",
     *       "sequence": 2.0,
     *        "text": "부서 공지",
     *        "value": "department"
     *    }
     *  ]
     *
     *  var array0 = dict.get("category.0");
     *  //array0의 값은
     *  {
     *    "enable": true,
     *    "sequence": 1.0,
     *    "text": "회사 공지",
     *    "value": "company"
     * }
     *
     * var enable = dict.get("category.0.eanble");
     * //enable의 값은 true
     */

    /**
     * 경로에 따라 데이터 사전에 데이터를 추가합니다.
     * @method add
     * @methodOf module:Dict
     * @instance
     * @param {String} path 데이터 사전의 데이터 경로, 한글 사용 가능. 다단계 경로는 점(.)으로 구분. path에 이미 데이터가 있고 기존 데이터가 배열이면 배열에 항목 추가, 배열이 아니면 오류 발생.
     * @param {(Object|Array|String|Number|Boolean)} data 추가할 데이터
     * @param {Function} [success] 데이터 추가 성공 시 콜백 함수.
     * @param {Function} [failure] 데이터 추가 오류 시 콜백 함수.
     * @o2syntax
     * dict.add( path, data, success, failure )
     * @example
     * var dict = new this.Dict("bulletinDictionary");
     *
     * dict.add( "category", { text : "시스템 공지", value : "system" }, function(data){
     *    //data 형식 예
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id는 데이터 사전 ID
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     * @example
     * <caption>
     *     get 메서드 예제의 데이터 사전에 값 할당, 예:
     * </caption>
     * var dict = new this.Dict("bulletinDictionary");
     *
     * dict.add( "category", { text : "시스템 공지", value : "system" }, function(data){
     *    //data 형식 예
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id는 데이터 사전 ID
     *    //}
     * }, function(xhr, text, error){
     *    //xhr은 xmlHttpRequest, text는 오류 텍스트, error는 Error 객체
     * });
     *     //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        },
     *        {
     *            "text": "시스템 공지",
     *            "value": "system"
     *        }
     *    ]
     * }
     *
     *  dict.add( "category.2.sequence", 3 );
     *     //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        },
     *        {
     *            "sequence" : 3.0,
     *            "text": "시스템 공지",
     *            "value": "system"
     *        }
     *    ]
     * }

     * dict.add( "archiveOptions", {
     *    "yes" : "예",
     *    "no" : "아니요"
     * });
     *     //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        },
     *        {
     *            "sequence" : 3.0,
     *            "text": "시스템 공지",
     *            "value": "system"
     *        }
     *
     *    ],
     *    "archiveOptions" : {
     *        "yes" : "예",
     *        "no" : "아니요"
     *    }
     * }
     * @example
     * <caption>아래는 잘못된 할당 예:</caption>
     * dict.add( "category.3", { text : "시스템 공지", value : "system" }); //오류, 배열 인덱스에 직접 값을 할당할 수 없음
     *
     * dict.add( "category.1.value", { text : "시스템 공지" } ); //오류, 이미 존재하는 비배열 경로에는 값을 할당할 수 없음
     */

    /**
     * 경로에 따라 데이터 사전의 데이터 수정.
     * @method set
     * @methodOf module:Dict
     * @instance
     * @param {String} path 데이터 사전의 데이터 경로, 한글 사용 가능. 다단계 경로는 점(.)으로 구분. 데이터 경로가 없으면 오류 발생.
     * @param {(Object|Array|String|Number|Boolean)} data 수정 후 데이터
     * @param {Function} [success] 데이터 설정 성공 시 콜백 함수.
     * @param {Function} [failure] 데이터 설정 오류 시 콜백 함수.
     * @o2syntax
     * dict.set( path, data, success, failure )
     * @example
     * var dict = new this.Dict("bulletinDictionary");
     *
     * dict.set( "category", { text : "시스템 공지", value : "system" }, function(data){
     *    //data 형식 예
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id는 데이터 사전 ID
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     * @example
     * <caption>
     *     Example add의 데이터 사전에 값 할당. 예:
     * </caption>
     * var dict = new this.Dict("bulletinDictionary");
     *
     * dict.set( "archiveOptions", [ { text : "예" }, { text : "아니요" } ]);
     *      //데이터 사전 값이 다음과 같이 변경됨
     *  {
     *     "category": [
     *         {
     *             "enable": true,
     *             "sequence": 1.0,
     *             "text": "회사 공지",
     *             "value": "company"
     *         },
     *         {
     *             "enable": "false",
     *             "sequence": 2.0,
     *             "text": "부서 공지",
     *             "value": "department"
     *         },
     *         {
     *             "sequence" : 3.0,
     *             "text": "시스템 공지",
     *             "value": "system"
     *         }
     *
     *     ],
     *     "archiveOptions" : [ { text : "예" }, { text : "아니요" } ]
     * }
     *
     * dict.set( "category.2", { text : "현급 공지", value : "county" }, function(data){
     *     //data 형식 예
     *     //{
     *     //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id는 데이터 사전 ID
     *     //}
     *  }, function(xhr){
     *     //xhr은 xmlHttpRequest
     *  });
     *
     *   /데이터 사전 값이 다음과 같이 변경됨
     *  {
     *     "category": [
     *         {
     *             "enable": true,
     *             "sequence": 1.0,
     *             "text": "회사 공지",
     *             "value": "company"
     *         },
     *         {
     *             "enable": "false",
     *             "sequence": 2.0,
     *             "text": "부서 공지",
     *             "value": "department"
     *         },
     *         {
     *             "text": "현급 공지",
     *             "value": "county"
     *         }
     *     ],
     *     "archiveOptions" : [ { text : "예" }, { text : "아니요" } ]
     * }
     *
     * dict.set( "category.1.sequence", 3 );
     * dict.set( "category.2.sequence", 2 );
     *      //데이터 사전 값이 다음과 같이 변경됨
     *      {
     *     "category": [
     *         {
     *             "enable": true,
     *             "sequence": 1.0,
     *             "text": "회사 공지",
     *             "value": "company"
     *         },
     *         {
     *             "enable": "false",
     *             "sequence": 3.0,
     *             "text": "부서 공지",
     *             "value": "department"
     *         },
     *         {
     *             "sequence": 2.0,
     *             "text": "현급 공지",
     *             "value": "county"
     *         }
     *     ],
     *     "archiveOptions" : [ { text : "예" }, { text : "아니요" } ]
     * }
     * @example
     * <caption>
     *     다음은 잘못된 할당입니다:
     * </caption>
     * dict.set( "category_1", { text : "회사 공지" } ); //오류, category_1이 데이터 사전에 존재하지 않음
     */

    /**
     * 경로에 따라 데이터 사전의 데이터 삭제.
     * @method delete
     * @methodOf module:Dict
     * @instance
     * @param {String} path 데이터 사전의 데이터 경로, 한글 사용 가능. 다단계 경로는 점(.)으로 구분. 데이터 경로가 없으면 오류 발생. 배열의 항목을 삭제할 때는 마지막 항목만 삭제 가능.
     * @param {Function} [success] 데이터 삭제 성공 시 콜백 함수.
     * @param {Function} [failure] 데이터 삭제 오류 시 콜백 함수.
     * @o2syntax
     * dict.delete( path, success, failure )
     * @example
     * var dict = new this.Dict("bulletinDictionary");
     *
     * dict.delete( "category", function(){
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     * @example
     * <caption>
     *     Example set의 데이터 사전에 값 할당. 예:
     * </caption>
     * var dict = new this.Dict("bulletinDictionary");
     *
     * dict.delete( "archiveOptions");
     * //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 3.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        },
     *        {
     *            "sequence": 2.0,
     *            "text": "현급 공지",
     *            "value": "county"
     *        }
     *    ]
     * }
     *
     * dict.delete( "category.2.sequence", function(data){
     *    //data 형식 예
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id는 데이터 사전 ID
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     * //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 3.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        },
     *        {
     *            "text": "현급 공지",
     *            "value": "county"
     *        }
     *    ]
     * }
     *
     * dict.delete( "category.2");
     * //category는 배열이므로 마지막 항목, 즉 인덱스 2만 삭제 가능
     * //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 3.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        }
     *    ]
     * }
     * @example
     * <caption>
     *     다음은 잘못된 삭제입니다:
     * </caption>
     * dict.delete( "category_1" ); //오류, category_1이 데이터 사전에 존재하지 않음
     */

    this.Dict = MWF.xScript.createDict();
    //org
    var orgActions = null;
    var getOrgActions = function () {
        if (!orgActions) {
            MWF.require("MWF.xScript.Actions.UnitActions", null, false);
            orgActions = new MWF.xScript.Actions.UnitActions();
        }
    };
    var getNameFlag = function (name) {
        var t = typeOf(name);
        if (t === "array") {
            var v = [];
            name.each(function (id) {
                v.push((typeOf(id) === "object") ? (id.distinguishedName || id.id || id.unique || id.name) : id);
            });
            return v;
        } else {
            return [(t === "object") ? (name.distinguishedName || name.id || name.unique || name.name) : name];
        }
    };

    /**
     * this.org로 조직의 사용자, 사용자 속성, 조직, 조직 속성, 신분, 그룹, 역할을 조회할 수 있습니다.
     * @module org
     * @o2cn 조직 조회
     * @o2category web
     * @o2ordernumber 100
     * @o2syntax
     * //프로세스 양식, 콘텐츠 관리 양식, 포털 페이지, 뷰, 조회 뷰에서 this로 현재 인스턴스의 org 객체를 가져올 수 있습니다. 예:
     * var org = this.org;
     */
    this.org = {
        //신분**********
        //신분 조회
        /**
         신분 식별자로 해당 신분 객체 또는 배열 조회
         * @method getIdentity
         * @o2membercategory identity
         * @methodOf module:org
         * @static
         * @param {IdentityFlag|IdentityFlag[]} name - 신분의 distinguishedName, id, unique 속성 값, 신분 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|IdentityData|IdentityData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그 외에는 신분 반환, 단건은 Object, 다건은 Array.
         * @o2ActionOut x_organization_assemble_express.IdentityAction.listObject|example=Identity|ignoreNoDescr=true|ignoreProps=[woUnitDutyList,woUnit,woGroupList]
         * @o2syntax
         * //동기 실행, 신분 반환, 단건은 객체, 다건은 배열.
         * var identityList = this.org.getIdentity( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.getIdentity( name, true);
         * promise.then(function(identityList){
         *     //identityList 는 반환된 신분, 단건은 객체, 다건은 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 신분 조회
         * this.org.getIdentity( name, function(identityList){
         *     //identityList 는 반환된 신분, 단건은 객체, 다건은 배열.
         * })
         */
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
        /**
         * 사용자 식별자로 해당 신분 객체 배열 조회.
         * @method listIdentityWithPerson
         * @o2membercategory identity
         * @methodOf module:org
         * @static
         * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @param {(Boolean)} [findCN] 한글 이름(예: 홍길동) 추가 검색 여부, 기본값 false. true이면 unique와 distingiushedName 일치 외에 신분의 첫 구간에서 일치하는 모든 신분을 검색(정확히 일치).
         * @return {Promise|IdentityData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그 외에는 신분 객체 배열 반환.
         * @o2ActionOut x_organization_assemble_express.IdentityAction.listWithPersonObject|example=Identity
         * @o2syntax
         * //동기 실행, 신분 객체 배열 반환.
         * var identityList = this.org.listIdentityWithPerson( person );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listIdentityWithPerson( person, true);
         * promise.then(function(identityList){
         *     //identityList 반환된 신분 객체 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listIdentityWithPerson( person, function(identityList){
         *     //identityList 반환된 신분 객체 배열.
         * })
         */
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
        /**
         * 조직 식별자로 해당 신분 객체 배열 조회: identity 객체 배열.
         * @method listIdentityWithUnit
         * @o2membercategory identity
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
         * @param {Boolean} [nested] true는 하위 포함 모든 신분 구성원, false는 직속 신분 구성원, 기본값 false.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|IdentityData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그 외에는 신분 객체 배열 반환.
         * @o2ActionOut x_organization_assemble_express.IdentityAction.listWithUnitSubNestedObject|example=Identity
         * @o2syntax
         * //동기 실행, 직속 조직 신분 객체 배열 반환.
         * var identityList = this.org.listIdentityWithUnit( unit );
         *
         *
         * //동기 실행, 하위 포함 조직 신분 객체 배열 반환.
         * var identityList = this.org.listIdentityWithUnit( unit, true );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listIdentityWithUnit( unit, false, true);
         * promise.then(function(identityList){
         *     //identityList 반환된 직속 조직 신분 객체 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listIdentityWithUnit( unit, false, function(identityList){
         *     //identityList 반환된 직속 조직 신분 객체 배열.
         * })
         */
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
        /**
         조직 식별자로 해당 조직 조회: unit 객체 또는 배열
         * @method getUnit
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @param {(Boolean)} [findCN] 한글 이름(예: 종합부) 추가 검색 여부, 기본값 false. true이면 unique와 distingiushedName 일치 외에 이름의 첫 구간에서 일치하는 모든 부서를 검색(정확히 일치).
         * @return {Promise|UnitData|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그 외에는 조직 반환, 단건은 Object, 다건은 Array.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listObject|example=Unit
         * @o2syntax
         * //동기 실행, 조직 반환, 단건은 객체, 다건은 배열.
         * var unitList = this.org.getUnit( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.getUnit( name, true);
         * promise.then(function(unitList){
         *     //unitList 는 반환된 조직, 단건은 객체, 다건은 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조직 조회
         * this.org.getUnit( name, function(unitList){
         *     //unitList 는 반환된 조직, 단건은 객체, 다건은 배열.
         * })
         */
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
        /**
         조직 식별자로 하위 조직 객체 배열 조회: unit 객체 배열.
         * @method listSubUnit
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
         * @param {Boolean} [nested]  true 중첩된 모든 하위 조직, false 직속 하위 조직, 기본값 false.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 조직 배열 반환.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSubNestedObject|example=Unit
         * @o2syntax
         * //동기 실행, 중첩 하위 조직 배열 반환.
         * var unitList = this.org.listSubUnit( name, true );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listSubUnit( name, false, true);
         * promise.then(function(unitList){
         *     //unitList 반환된 직속 하위 조직 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listSubUnit( name, true, function(unitList){
         *     //unitList 반환된 중첩 하위 조직 배열.
         * })
         */
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
        /**
         조직 식별자로 상위 조직 객체 배열 일괄 조회: unit 객체 배열.
         * @method listSupUnit
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
         * @param {Boolean} [nested]  true 중첩된 모든 상위 조직, false 직속 상위 조직, 기본값 false.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 조직 배열 반환.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSupNestedObject|example=Unit
         * @o2syntax
         * //동기 실행, 중첩 상위 조직 배열 반환.
         * var unitList = this.org.listSupUnit( name, true );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listSupUnit( name, false, true);
         * promise.then(function(unitList){
         *     //unitList 반환된 직속 상위 조직 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listSupUnit( name, true, function(unitList){
         *     //unitList 반환된 중첩 상위 조직 배열.
         * })
         */
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


        /**
         조직 식별자와 지정 계층으로 상위 조직 객체 배열 일괄 조회(전달된 배열 포함): unit 객체 배열.
         * @method listSupUnitWithLevel
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
         * @param {Number} level  지정 계층
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 조직 배열 반환.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSupNestedObject|example=Unit
         * @o2syntax
         * //동기 실행, 중첩 상위 조직 배열 반환.
         * var unitList = this.org.listSupUnitWithLevel( name, level );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listSupUnitWithLevel( name, level, true);
         * promise.then(function(unitList){
         *     //unitList 반환된 직속 상위 조직 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listSupUnitWithLevel( name, level, function(unitList){
         *     //unitList 반환된 중첩 상위 조직 배열.
         * })
         */
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


        /**
         조직 식별자와 지정 조직 유형으로 상위 조직 객체 배열 일괄 조회(전달된 배열 포함): unit 객체 배열.
         * @method listSupUnitWithType
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
         * @param {String} type  지정 조직 유형
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 조직 배열 반환.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSupNestedObject|example=Unit
         * @o2syntax
         * //동기 실행, 중첩 상위 조직 배열 반환.
         * var unitList = this.org.listSupUnitWithType( name, level );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listSupUnitWithType( name, level, true);
         * promise.then(function(unitList){
         *     //unitList 반환된 직속 상위 조직 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listSupUnitWithType( name, level, function(unitList){
         *     //unitList 반환된 중첩 상위 조직 배열.
         * })
         */
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
        /**
         개인 신분으로 조직 조회: unit 객체 또는 배열.
         * @method getUnitByIdentity
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {IdentityFlag} name - 신분의 distinguishedName, id, unique 속성값, 신분 객체.
         * @param {String|Number} [flag]  값이 숫자이면 몇 번째 계층의 조직을 조회.<br/> 값이 문자열이면 지정 유형의 조직을 조회.<br/> 값이 비어 있으면 직속 조직을 조회.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 해당 조직 반환, 단일은 객체, 복수는 배열.
         * @o2ActionOut x_organization_assemble_express.UnitAction.getWithIdentityWithLevelObject|example=Unit
         * @o2syntax
         * //동기 실행, 직속 조직 반환, 단일은 객체, 복수는 배열.
         * var unitList = this.org.getUnitByIdentity( name );
         *
         * //동기 실행, 첫 번째 계층 조직 반환, 단일은 객체, 복수는 배열.
         * var unitList = this.org.getUnitByIdentity( name, 1 );
         *
         * * //동기 실행, 유형이 company인 조직 반환, 단일은 객체, 복수는 배열.
         * var unitList = this.org.getUnitByIdentity( name, "company" );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.getUnitByIdentity( name, null, true);
         * promise.then(function(unitList){
         *     //unitList 직속 조직 반환, 단일은 객체, 복수는 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.getUnitByIdentity( name, 1, function(unitList){
         *     //unitList 첫 번째 계층 조직 반환, 단일은 객체, 복수는 배열.
         * })
         */
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
        /**
         * 신분이 속한 조직 일괄 조회, 상위 조직 객체 재귀 조회.
         * @method listAllSupUnitWithIdentity
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {IdentityFlag|IdentityFlag[]} name - 신분의 distinguishedName, id, unique 속성 값, 신분 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 조직 배열 반환.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listWithIdentitySupNestedObject|example=Unit
         * @o2syntax
         * //동기 실행, 조직 배열 반환.
         * var unitList = this.org.listAllSupUnitWithIdentity( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listAllSupUnitWithIdentity( name, true);
         * promise.then(function(unitList){
         *     //unitList 조직 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listAllSupUnitWithIdentity( name, function(unitList){
         *     //unitList 조직 배열 반환.
         * })
         */
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
        /**
         * 개인 식별자로 조직 객체 구성원 일괄 조회: Unit 객체 배열.
         * @method listUnitWithPerson
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 조직 배열 반환.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listWithPersonObject|example=Unit
         * @o2syntax
         * //동기 실행, 조직 배열 반환.
         * var unitList = this.org.listUnitWithPerson( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listUnitWithPerson( name, true);
         * promise.then(function(unitList){
         *     //unitList 조직 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listUnitWithPerson( name, function(unitList){
         *     //unitList 조직 배열 반환.
         * })
         */
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
        /**
         * 개인 식별자로 소속 조직 및 모든 상위 조직 일괄 조회: Unit 객체 배열.
         * @method listAllSupUnitWithPerson
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 개인 소속 조직 및 모든 상위 조직 반환.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listWithPersonSupNestedObject|example=Unit
         * @o2syntax
         * //동기 실행, 조직 배열 반환.
         * var unitList = this.org.listAllSupUnitWithPerson( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listAllSupUnitWithPerson( name, true);
         * promise.then(function(unitList){
         *     //unitList 조직 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listAllSupUnitWithPerson( name, function(unitList){
         *     //unitList 조직 배열 반환.
         * })
         */
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
        /**
         * 조직 속성으로 일치하는 모든 조직 조회.
         * @method listUnitWithAttribute
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {String} attributeName 조직 속성 이름.
         * @param {String} attributeValue 조직 속성값.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 조직 배열 반환.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitAttributeObject|example=Unit
         * @o2syntax
         * //동기 실행, 조직 배열 반환.
         * var unitList = this.org.listUnitWithAttribute( attributeName, attributeName );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listUnitWithAttribute( attributeName, attributeName, true);
         * promise.then(function(unitList){
         *     //unitList 조직 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listUnitWithAttribute( attributeName, attributeName, function(unitList){
         *     //unitList 조직 배열 반환.
         * })
         */
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
        /**
         * 조직 직무로 일치하는 모든 조직 조회.
         * @method listUnitWithDuty
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {String} dutyName 조직 직무 이름.
         * @param {IdentityFlag} identity 신분의 distinguishedName, id, unique 속성값, 신분 객체.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 조직 배열 반환.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitDutyObject|example=Unit
         * @o2syntax
         * //동기 실행, 조직 배열 반환.
         * var unitList = this.org.listUnitWithDuty( dutyName, identity );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listUnitWithDuty( dutyName, identity, true);
         * promise.then(function(unitList){
         *     //unitList 조직 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listUnitWithDuty( dutyName, identity, function(unitList){
         *     //unitList 조직 배열 반환.
         * })
         */
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

        /**
         * 모든 최상위 조직 나열.
         * @method listTopUnit
         * @o2membercategory unit
         * @methodOf module:org
         * @static
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|UnitData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 최상위 조직 배열 반환.
         * @o2ActionOut x_organization_assemble_express.UnitAction.listObject|example=Unit
         * @o2syntax
         * //동기 실행, 최상위 조직 배열 반환.
         * var unitList = this.org.listTopUnit();
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listTopUnit(true);
         * promise.then(function(unitList){
         *     //unitList 최상위 조직 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listTopUnit(function(unitList){
         *     //unitList 최상위 조직 배열 반환.
         * })
         */
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

        //사용자
        //사용자 조회, 신분, 신분 소속 조직, 개인 소속 그룹, 개인 보유 역할 포함.
        /**
         사용자 식별자로 해당 사용자 객체 조회, 신분, 신분 소속 조직, 개인 소속 그룹, 개인 보유 역할 포함.
         * @method getPersonData
         * @o2membercategory person
         * @methodOf module:org
         * @static
         * @param {String} name - 사용자의 distinguishedName, id, unique 속성값, 사용자 이름.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|PersonData} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 객체 반환.
         * @o2ActionOut x_organization_assemble_express.PersonAction.get|example=PersonData
         * @o2syntax
         * //동기 실행, 사용자 객체 반환.
         * var person = this.org.getPersonData( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.getPersonData( name, true);
         * promise.then(function(person){
         *     //personList 반환된 사용자 객체.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 사용자 조회
         * this.org.getPersonData( name, function(person){
         *     //personList 반환된 사용자 객체.
         * })
         */
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
        /**
         사용자 식별자로 해당 사용자 객체 또는 배열 조회: person 객체 또는 배열
         * @method getPerson
         * @o2membercategory person
         * @methodOf module:org
         * @static
         * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @param {(Boolean)} [findCN] 한글 이름(예: 홍길동)을 추가로 찾을지 여부, 기본값 false. true이면 unique와 distingiushedName 일치 외에 이름 첫 부분에서 일치하는 모든 사람을 찾음(정확히 일치).
         * @return {Promise|PersonData|PersonData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 반환, 단일은 Object, 복수는 Array.
         * @o2ActionOut x_organization_assemble_express.PersonAction.listObject|example=Person
         * @o2syntax
         * //동기 실행, 사용자 반환, 단일은 객체, 복수는 배열.
         * var personList = this.org.getPerson( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.getPerson( name, true);
         * promise.then(function(personList){
         *     //personList 반환된 사용자, 단일은 객체, 복수는 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 사용자 조회
         * this.org.getPerson( name, function(personList){
         *     //personList 반환된 사용자, 단일은 객체, 복수는 배열.
         * })
         */
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
            // var v = null;
            // orgActions.listPerson(data, function(json){v = json.data;}, null, false);
            // return (v && v.length===1) ? v[0] : v;
        },
        //하위 사용자 조회--사용자 객체 배열 반환
        //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
        /**
         사용자 식별자로 하위 사용자 객체 배열 조회: person 객체 배열. 상하 관계는 사용자의 보고 대상 값(superior)으로 결정.
         * @method listSubPerson
         * @o2membercategory person
         * @methodOf module:org
         * @static
         * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
         * @param {Boolean} [nested]  true 중첩된 모든 하위 사용자, false 직속 하위 사용자, 기본값 false.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|PersonData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 배열 반환.
         * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonSubDirectObject|example=Person
         * @o2syntax
         * //동기 실행, 중첩 하위 사용자 배열 반환.
         * var personList = this.org.listSubPerson( name, true );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listSubPerson( name, false, true);
         * promise.then(function(personList){
         *     //personList 반환된 직속 하위 사용자 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listSubPerson( name, true, function(personList){
         *     //personList 반환된 중첩 하위 사용자 배열.
         * })
         */
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
        /**
         *사용자 식별자로 상위 사용자 객체 배열 조회: person 객체 배열. 상하 관계는 사용자의 보고 대상 값(superior)으로 결정.
         * @method listSupPerson
         * @o2membercategory person
         * @methodOf module:org
         * @static
         * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
         * @param {Boolean} [nested]  true 중첩된 모든 상위 사용자, false 직속 상위 사용자, 기본값 false.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|PersonData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 배열 반환.
         * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonSupDirectObject|example=Person
         * @o2syntax
         * //동기 실행, 중첩 상위 사용자 배열 반환.
         * var personList = this.org.listSupPerson( name, true );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listSupPerson( name, false, true);
         * promise.then(function(personList){
         *     //personList 반환된 직속 상위 사용자 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listSupPerson( name, true, function(personList){
         *     //personList 반환된 중첩 상위 사용자 배열.
         * })
         */
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
        /**
         * 그룹 식별자로 사용자 객체 구성원 조회: person 객체 배열.
         * @method listPersonWithGroup
         * @o2membercategory person
         * @methodOf module:org
         * @static
         * @param {GroupFlag|GroupFlag[]} name - 그룹의 distinguishedName, name, id, unique 속성값, 그룹 객체 또는 이 속성값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|PersonData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 객체 배열 반환.
         * @o2ActionOut x_organization_assemble_express.PersonAction.listWithGroupObject|example=Person
         * @o2syntax
         * //동기 실행, 사용자 배열 반환.
         * var personList = this.org.listPersonWithGroup( group );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listPersonWithGroup( group, true);
         * promise.then(function(personList){
         *     //personList 반환된 사용자 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listPersonWithGroup( group, function(personList){
         *     //personList 반환된 사용자 배열.
         * })
         */
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
        /**
         * 역할 식별자로 사용자 객체 배열 조회: person 객체 배열.
         * @method listPersonWithRole
         * @o2membercategory person
         * @methodOf module:org
         * @static
         * @param {RoleFlag|RoleFlag[]} name - 역할의 distinguishedName, name, id, unique 속성값, 역할 객체 또는 이 속성값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|PersonData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 객체 배열 반환.
         * @o2ActionOut x_organization_assemble_express.PersonAction.listWithRoleObject|example=Person
         * @o2syntax
         * //동기 실행, 사용자 배열 반환.
         * var personList = this.org.listPersonWithRole( role );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listPersonWithRole( role, true);
         * promise.then(function(personList){
         *     //personList 반환된 사용자 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listPersonWithRole( role, function(personList){
         *     //personList 반환된 사용자 배열.
         * })
         */
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
        /**
         * 신분 식별자로 사용자 객체 구성원 조회: person 객체 배열.
         * @method listPersonWithIdentity
         * @o2membercategory person
         * @methodOf module:org
         * @static
         * @param {IdentityFlag|IdentityFlag[]} name - 신분의 distinguishedName, id, unique 속성 값, 신분 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|PersonData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 객체 배열 반환.
         * @o2ActionOut x_organization_assemble_express.PersonAction.listWithIdentityObject|example=Person
         * @o2syntax
         * //동기 실행, 사용자 배열 반환.
         * var personList = this.org.listPersonWithIdentity( identity );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listPersonWithIdentity( identity, true);
         * promise.then(function(personList){
         *     //personList 반환된 사용자 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listPersonWithIdentity( identity, function(personList){
         *     //personList 반환된 사용자 배열.
         * })
         */
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
        /**
         * 조직 식별자로 사용자 객체 구성원 조회: person 객체 배열.
         * @method listPersonWithUnit
         * @o2membercategory person
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
         * @param {Boolean} [nested] 조직 및 하위 조직의 사용자를 중첩 조회할지 여부, true는 중첩, flase는 직속 조직 조회. 기본값 false
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|PersonData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 객체 배열 반환.
         * @o2ActionOut x_organization_assemble_express.PersonAction.listWithUnitSubDirectObject|example=Person
         * @o2syntax
         * //동기 실행, 조직의 직속 사용자 배열 반환.
         * var personList = this.org.listPersonWithUnit( unit );
         *
         * //동기 실행, 조직 및 중첩 하위 조직의 모든 사용자 배열 반환.
         * var personList = this.org.listPersonWithUnit( unit, true );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listPersonWithUnit( unit, false, true);
         * promise.then(function(personList){
         *     //personList 반환된 조직의 직속 사용자 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listPersonWithUnit( unit, false, function(personList){
         *     //personList 반환된 그룹의 직속 사용자 배열.
         * })
         */
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
        /**
         * 사용자 속성 이름과 속성값으로 사용자 객체 구성원 조회: person 객체 배열.
         * @method listPersonWithAttribute
         * @o2membercategory person
         * @methodOf module:org
         * @static
         * @param {String} name 사용자 속성 이름.
         * @param {String} value 사용자 속성값.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|PersonData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 객체 배열 반환.
         * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonAttributeObject|example=Person
         * @o2syntax
         * //동기 실행, 해당 속성 이름과 속성값을 가진 사용자 배열 반환.
         * var personList = this.org.listPersonWithAttribute( name, value );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listPersonWithAttribute( name, value, true);
         * promise.then(function(personList){
         *     //personList 해당 속성 이름과 속성값을 가진 사용자 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listPersonWithAttribute( name, value, function(personList){
         *     //personList 해당 속성 이름과 속성값을 가진 사용자 배열 반환.
         * })
         */
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

        //조직 직무***********
        //지정 조직 직무의 신분 조회
        /**
         * 직무 이름과 조직 이름으로 신분 조회.
         * @method getDuty
         * @o2membercategory duty
         * @methodOf module:org
         * @static
         * @param {String} dutyName 조직 직무 이름.
         * @param {UnitFlag} unit 조직의 distinguishedName, id, unique 속성값, 조직 객체.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|IdentityData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 신분 배열 반환.
         * @o2ActionOut x_organization_assemble_express.UnitDutyAction.getWithUnitWithName|example=Identity
         * @o2syntax
         * //동기 실행, 신분 배열 반환.
         * var identityList = this.org.getDuty( dutyName, unit );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.getDuty( dutyName, unit, true);
         * promise.then(function(identityList){
         *     //identityList 신분 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.getDuty( dutyName, unit, function(unitList){
         *     //unitList 신분 배열 반환.
         * })
         */
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
        /**
         * 신분의 모든 직무 이름 일괄 조회.
         * @method listDutyNameWithIdentity
         * @o2membercategory duty
         * @methodOf module:org
         * @static
         * @param {IdentityFlag|IdentityFlag[]} identity - 신분의 distinguishedName, id, unique 속성값, 신분 객체 또는 이 속성값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|String[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 직무 이름 배열 반환.
         * @o2syntax
         * //동기 실행, 직무 이름 배열 반환.
         * var dutyNameList = this.org.listDutyNameWithIdentity( identity );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listDutyNameWithIdentity( identity, true);
         * promise.then(function(dutyNameList){
         *     //dutyNameList 직무 이름 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listDutyNameWithIdentity( identity, function(dutyNameList){
         *     //dutyNameList 직무 이름 배열 반환.
         * })
         */
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
        //조직의 모든 직무 이름 일괄 조회
        /**
         * 조직의 모든 직무 이름 일괄 조회.
         * @method listDutyNameWithUnit
         * @o2membercategory duty
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} unit - 조직의 distinguishedName, id, unique 속성값, 조직 객체 또는 이 속성값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|String[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 직무 이름 배열 반환.
         * @o2syntax
         * //동기 실행, 직무 이름 배열 반환.
         * var dutyNameList = this.org.listDutyNameWithUnit( unit );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listDutyNameWithUnit( unit, true);
         * promise.then(function(dutyNameList){
         *     //dutyNameList 직무 이름 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listDutyNameWithUnit( unit, function(dutyNameList){
         *     //dutyNameList 직무 이름 배열 반환.
         * })
         */
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
        /**
         * 조직의 모든 직무 일괄 조회.
         * @method listUnitAllDuty
         * @o2membercategory duty
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} unit - 조직의 distinguishedName, id, unique 속성값, 조직 객체 또는 이 속성값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|Object[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 직무 배열 반환
         * @o2ActionOut x_organization_assemble_express.UnitDutyAction.listWithUnitObject|example=Duty
         * @o2syntax
         * //동기 실행, 직무 배열 반환.
         * var dutyList = this.org.listUnitAllDuty( unit );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listUnitAllDuty( unit, true);
         * promise.then(function(dutyList){
         *     //dutyList 직무 배열 반환.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listUnitAllDuty( unit, function(dutyList){
         *     //dutyList 직무 배열 반환.
         * })
         */
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

        //그룹***************
        //그룹 조회--그룹 객체 배열 반환
        /**
         그룹 식별자로 해당 그룹 객체 또는 배열 조회: group 객체 또는 배열
         * @method getGroup
         * @o2membercategory group
         * @methodOf module:org
         * @static
         * @param {GroupFlag|GroupFlag[]} name - 그룹의 distinguishedName, name, id, unique 속성값, 그룹 객체 또는 이 속성값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|GroupData|GroupData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 그룹 반환, 단일은 Object, 복수는 Array.
         * @o2ActionOut x_organization_assemble_express.GroupAction.listObject|example=Group
         * @o2syntax
         * //동기 실행, 그룹 반환, 단일은 Object, 복수는 Array.
         * var groupList = this.org.getGroup( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.getGroup( name, true);
         * promise.then(function(groupList){
         *     //groupList 반환된 그룹, 단일은 Object, 복수는 Array.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 그룹 조회
         * this.org.getGroup( name, function(groupList){
         *     //groupList 반환된 그룹, 단일은 Object, 복수는 Array.
         * })
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
        /**
         그룹 식별자로 하위 그룹 객체 배열 조회: group 객체 배열.
         * @method listSubGroup
         * @o2membercategory group
         * @methodOf module:org
         * @static
         * @param {GroupFlag|GroupFlag[]} name - 그룹의 distinguishedName, name, id, unique 속성값, 그룹 객체 또는 이 속성값과 객체의 배열.
         * @param {Boolean} [nested]  true 중첩된 모든 하위 그룹, false 직속 하위 그룹, 기본값 false.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|GroupData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 그룹 배열 반환.
         * @o2ActionOut x_organization_assemble_express.GroupAction.listWithGroupSubDirectObject|example=Group
         * @o2syntax
         * //동기 실행, 중첩 하위 그룹 배열 반환.
         * var groupList = this.org.listSubGroup( name, true );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listSubGroup( name, false, true);
         * promise.then(function(groupList){
         *     //groupList 반환된 직속 하위 그룹 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 그룹 조회
         * this.org.listSubGroup( name, true, function(groupList){
         *     //groupList 반환된 중첩 하위 그룹 배열.
         * })
         */
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
        /**
         그룹 식별자로 상위 그룹 객체 배열 조회: group 객체 배열.
         * @method listSupGroup
         * @o2membercategory group
         * @methodOf module:org
         * @static
         * @param {GroupFlag|GroupFlag[]} name - 그룹의 distinguishedName, name, id, unique 속성값, 그룹 객체 또는 이 속성값과 객체의 배열.
         * @param {Boolean} [nested]  true 중첩된 모든 상위 그룹, false 직속 상위 그룹, 기본값 false.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|GroupData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 그룹 배열 반환.
         * @o2ActionOut x_organization_assemble_express.GroupAction.listWithGroupSupDirectObject|example=Group
         * @o2syntax
         * //동기 실행, 중첩 상위 그룹 배열 반환.
         * var groupList = this.org.listSupGroup( name, true );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listSupGroup( name, false, true);
         * promise.then(function(groupList){
         *     //groupList 반환된 직속 상위 그룹 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 그룹 조회
         * this.org.listSupGroup( name, true, function(groupList){
         *     //groupList 반환된 중첩 상위 그룹 배열.
         * })
         */
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
        /**
         * 사용자 식별자로 모든 그룹 객체 배열 조회. 그룹에 그룹(group) 구성원이 있고 그 구성원에 해당 사용자가 포함되면 그 그룹도 반환.
         * @method listGroupWithPerson
         * @o2membercategory group
         * @methodOf module:org
         * @static
         * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|GroupData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 그룹 객체 배열 반환.
         * @o2ActionOut x_organization_assemble_express.GroupAction.listWithPersonObject|example=Group
         * @o2syntax
         * //동기 실행, 그룹 배열 반환.
         * var groupList = this.org.listGroupWithPerson( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listGroupWithPerson( name, true);
         * promise.then(function(groupList){
         *     //groupList 반환된 그룹 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 그룹 조회
         * this.org.listGroupWithPerson( name, function(groupList){
         *     //groupList 반환된 그룹 배열.
         * })
         */
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
        /**
         * 신분 식별자로 모든 그룹 객체 배열 조회. 그룹에 그룹(group) 구성원이 있고 그 구성원에 해당 신분이 포함되면 그 그룹도 반환.
         * @method listGroupWithIdentity
         * @o2membercategory group
         * @methodOf module:org
         * @static
         * @param {IdentityFlag|IdentityFlag[]} identity - 신분의 distinguishedName, id, unique 속성값, 신분 객체 또는 이 속성값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|GroupData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 그룹 객체 배열 반환.
         * @o2ActionOut x_organization_assemble_express.GroupAction.listWithPersonObject|example=Group
         * @o2syntax
         * //동기 실행, 그룹 배열 반환.
         * var groupList = this.org.listGroupWithIdentity( identity );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listGroupWithIdentity( identity, true);
         * promise.then(function(groupList){
         *     //groupList 반환된 그룹 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 그룹 조회
         * this.org.listGroupWithPerson( identity, function(groupList){
         *     //groupList 반환된 그룹 배열.
         * })
         */
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

        //역할***************
        //역할 조회--역할 객체 배열 반환
        /**
         * 역할 식별자로 해당 역할 객체 또는 배열 조회.
         * @method getRole
         * @o2membercategory role
         * @methodOf module:org
         * @static
         * @param {RoleFlag|RoleFlag[]} name - 역할의 distinguishedName, name, id, unique 속성값, 역할 객체 또는 이 속성값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|RoleData|RoleData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 역할 반환, 단일은 Object, 복수는 Array.
         * @o2ActionOut x_organization_assemble_express.RoleAction.listObject|example=Role
         * @o2syntax
         * //동기 실행, 역할 반환, 단일은 객체, 복수는 배열.
         * var roleList = this.org.getRole( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.getRole( name, true);
         * promise.then(function(roleList){
         *     //roleList 반환된 역할, 단일은 객체, 복수는 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 역할 조회, 단일은 객체, 복수는 배열
         * this.org.getRole( name, function(roleList){
         *     //roleList 반환된 판단 결과.
         * })
         */
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
        /**
         * 사용자 식별자로 모든 역할 객체 배열 조회. 역할에 그룹(group) 구성원이 있고 그 그룹에 해당 사용자가 포함되면 그 역할도 반환.
         * @method listRoleWithPerson
         * @o2membercategory role
         * @methodOf module:org
         * @static
         * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|RoleData[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 역할 객체 배열 반환.
         * @o2ActionOut x_organization_assemble_express.RoleAction.listWithPersonObject|example=Role
         * @o2syntax
         * //동기 실행, 역할 배열 반환.
         * var roleList = this.org.listRoleWithPerson( name );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listRoleWithPerson( name, true);
         * promise.then(function(roleList){
         *     //roleList 반환된 역할 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 역할 조회
         * this.org.listRoleWithPerson( name, function(roleList){
         *     //roleList 반환된 역할 배열.
         * })
         */
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
        /**
         * 사용자의 역할 보유 여부.
         * @method personHasRole
         * @o2membercategory role
         * @methodOf module:org
         * @static
         * @param {PersonFlag} name - 사용자의 distinguishedName, id, unique 속성값, 사용자 객체.
         * @param {RoleFlag|RoleFlag[]} roleList - 역할의 distinguishedName, name, id, unique 속성값, 역할 객체 또는 이 속성값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|Boolean} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자가 역할을 보유하면 true, 아니면 false 반환.
         * @o2syntax
         * //동기 실행, 판단 결과 반환.
         * var flag = this.org.personHasRole( name, roleList );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.personHasRole( name, roleList, true);
         * promise.then(function(flag){
         *     //flag 반환된 판단 결과.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 판단 결과 조회
         * this.org.personHasRole( name, roleList, function(flag){
         *     //flag 반환된 판단 결과.
         * })
         */
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
        //그룹의 역할 보유 여부--true, false 반환
        /**
         * 그룹의 역할 보유 여부.
         * @method groupHasRole
         * @o2membercategory role
         * @methodOf module:org
         * @static
         * @param {GroupFlag} name - 그룹의 distinguishedName, name, id, unique 속성값, 그룹 객체.
         * @param {RoleFlag|RoleFlag[]} roleList - 역할의 distinguishedName, name, id, unique 속성값, 역할 객체 또는 이 속성값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Promise|Boolean} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 그룹이 역할을 보유하면 true, 아니면 false 반환.
         * @o2syntax
         * //동기 실행, 판단 결과 반환.
         * var groupList = this.org.groupHasRole( name, roleList );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.groupHasRole( name, roleList, true);
         * promise.then(function(flag){
         *     //flag 반환된 판단 결과.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 판단 결과 조회
         * this.org.groupHasRole( name, roleList, function(flag){
         *     //flag 반환된 판단 결과.
         * })
         */
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

        //사용자 속성************
        //사용자 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
        /**
         * 사용자 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
         * @method appendPersonAttribute
         * @o2membercategory personAttribute
         * @methodOf module:org
         * @static
         * @param {PersonFlag} person - 사용자의 distinguishedName, id, unique 속성값, 사용자 객체.
         * @param {String} attr 속성 이름.
         * @param {String[]} values 속성값, 반드시 배열.
         * @param {Function} [success] 실행 성공 콜백.
         * @param {Function} [failure] 실행 실패 콜백.
         * @param {(Boolean)} [async] 파라미터가 boolean이면 비동기 실행 여부, 기본값 false.
         * @o2syntax
         * //동기 실행
         * this.org.appendPersonAttribute( person, attribute, valueArray);
         *
         * //비동기 실행
         * this.org.appendPersonAttribute( person, attribute, valueArray, function(){
         *     //실행 성공 콜백
         * }, null, true);
         */
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

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            //
            // orgActions.appendPersonAttribute(data, cb, null, !!async);
        },
        //사용자 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
        /**
         * 사용자 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
         * @method setPersonAttribute
         * @o2membercategory personAttribute
         * @methodOf module:org
         * @static
         * @param {PersonFlag} person - 사용자의 distinguishedName, id, unique 속성값, 사용자 객체.
         * @param {String} attr 속성 이름.
         * @param {String[]} values 속성값, 반드시 배열.
         * @param {Function} [success] 실행 성공 콜백.
         * @param {Function} [failure] 실행 실패 콜백.
         * @param {(Boolean)} [async] 파라미터가 boolean이면 비동기 실행 여부, 기본값 false.
         * @o2syntax
         * //동기 실행
         * this.org.setPersonAttribute( person, attribute, valueArray);
         *
         * //비동기 실행
         * this.org.setPersonAttribute( person, attribute, valueArray, function(){
         *     //실행 성공 콜백
         * }, null, true);
         */
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
        /**
         사용자와 속성 이름으로 속성값 배열 조회.
         * @method getPersonAttribute
         * @o2membercategory personAttribute
         * @methodOf module:org
         * @static
         * @param {PersonFlag} person - 사용자의 distinguishedName, id, unique 속성값, 사용자 객체.
         * @param {String} attr 속성 이름.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {String[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 속성값 배열 반환,
         * 예: <pre><code class='language-js'>[ value1, value2 ]</code></pre>
         * @o2syntax
         * //동기 실행, 해당 사용자의 속성값 배열 반환.
         * var attributeList = this.org.getPersonAttribute( person, attr );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.getPersonAttribute( person, attr, true);
         * promise.then(function(attributeList){
         *     //attributeList 반환된 해당 사용자의 속성값 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.getPersonAttribute( person, attr, function(attributeList){
         *     //attributeList 반환된 해당 사용자의 속성값 배열.
         * })
         */
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
        /**
         사용자의 모든 속성 이름 배열 나열.
         * @method listPersonAttributeName
         * @o2membercategory personAttribute
         * @methodOf module:org
         * @static
         * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {String[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 속성 이름 배열 반환,
         * 예: <pre><code class='language-js'>[ attributeName1, attributeName2 ]</code></pre>
         * @o2syntax
         * //동기 실행, 사용자의 모든 속성 이름 배열 반환.
         * var attributeNameList = this.org.listPersonAttributeName( person );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listPersonAttributeName( person, true);
         * promise.then(function(attributeNameList){
         *     //attributeNameList 사용자의 모든 속성 이름 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listPersonAttributeName( person, function(attributeNameList){
         *     //attributeNameList 사용자의 모든 속성 이름 배열.
         * })
         */
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
        /**
         사용자의 모든 속성 객체 배열 나열.
         * @method listPersonAllAttribute
         * @o2membercategory personAttribute
         * @methodOf module:org
         * @static
         * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Object[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 사용자 속성 객체 배열 반환, 예:
         * <pre><code class='language-js'>[{
         *    "name": "주소",
         *    "person": "홍길동@zhangsan@P",
         *    "attributeList": [
         *        "서울시","부산시"
         *    ]
         * }]</code></pre>
         * @o2syntax
         * //동기 실행, 사용자의 모든 속성 객체 배열 반환.
         * var attributeObjectList = this.org.listPersonAllAttribute( person );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listPersonAllAttribute( person, true);
         * promise.then(function(attributeObjectList){
         *     //attributeObjectList 사용자의 모든 속성 객체 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listPersonAllAttribute( person, function(attributeObjectList){
         *     //attributeObjectList 사용자의 모든 속성 객체 배열.
         * })
         */
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

        //조직 속성**************
        //조직 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
        /**
         * 조직 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
         * @method appendUnitAttribute
         * @o2membercategory unitAttribute
         * @methodOf module:org
         * @static
         * @param {UnitFlag} unit - 조직의 distinguishedName, id, unique 속성값, 조직 객체.
         * @param {String} attribute 속성 이름.
         * @param {String[]} valueArray 속성값, 반드시 배열.
         * @param {Function} [success] 실행 성공 콜백.
         * @param {Function} [failure] 실행 실패 콜백.
         * @param {(Boolean)} [async] 파라미터가 boolean이면 비동기 실행 여부, 기본값 false.
         * @o2syntax
         * //동기 실행
         * this.org.appendUnitAttribute( unit, attribute, valueArray);
         *
         * //비동기 실행
         * this.org.appendUnitAttribute( unit, attribute, valueArray, function(){
         *     //실행 성공 콜백
         * }, null, true);
         */
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
        /**
         * 조직 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
         * @method setUnitAttribute
         * @o2membercategory unitAttribute
         * @methodOf module:org
         * @static
         * @param {UnitFlag} unit - 조직의 distinguishedName, id, unique 속성값, 조직 객체.
         * @param {String} attribute 속성 이름.
         * @param {String[]} valueArray 속성값, 반드시 배열.
         * @param {Function} [success] 실행 성공 콜백.
         * @param {Function} [failure] 실행 실패 콜백.
         * @param {(Boolean)} [async] 파라미터가 boolean이면 비동기 실행 여부, 기본값 false.
         * @o2syntax
         * //동기 실행
         * this.org.setUnitAttribute( unit, attribute, valueArray);
         *
         * //비동기 실행
         * this.org.setUnitAttribute( unit, attribute, valueArray, function(){
         *     //실행 성공 콜백
         * }, null, true);
         */
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
        /**
         조직 식별자와 속성 이름으로 해당 속성값 조회.
         * @method getUnitAttribute
         * @o2membercategory unitAttribute
         * @methodOf module:org
         * @static
         * @param {UnitFlag} unit - 조직의 distinguishedName, id, unique 속성값, 조직 객체.
         * @param {String} attr 속성 이름.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {String[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 속성값 배열 반환,
         * 예: <pre><code class='language-js'>[ value1, value2 ]</code></pre>
         * @o2syntax
         * //동기 실행, 해당 조직의 속성값 배열 반환.
         * var attributeList = this.org.getUnitAttribute( unit, attr );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.getUnitAttribute( unit, attr, true);
         * promise.then(function(attributeList){
         *     //attributeList 반환된 해당 조직의 속성값 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.getUnitAttribute( unit, attr, function(attributeList){
         *     //attributeList 반환된 해당 조직의 속성값 배열.
         * })
         */
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
        /**
         조직의 모든 속성 이름 배열 나열.
         * @method listUnitAttributeName
         * @o2membercategory unitAttribute
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {String[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 조직 속성 이름 배열 반환,
         * 예: <pre><code class='language-js'>[ attributeName1, attributeName2 ]</code></pre>
         * @o2syntax
         * //동기 실행, 조직의 모든 속성 이름 배열 반환.
         * var attributeNameList = this.org.listUnitAttributeName( unit );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listUnitAttributeName( unit, true);
         * promise.then(function(attributeNameList){
         *     //attributeNameList 조직의 모든 속성 이름 배열.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listUnitAttributeName( unit, function(attributeNameList){
         *     //attributeNameList 조직의 모든 속성 이름 배열.
         * })
         */
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
        /**
         조직의 모든 속성 객체 배열을 나열합니다.
         * @method listUnitAllAttribute
         * @o2membercategory unitAttribute
         * @methodOf module:org
         * @static
         * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
         * @param {(Boolean|Function)} [asyncOrCallback] boolean이면 비동기 실행 여부, 기본값 false. function이면 콜백 메서드.
         * @return {Object[]} async가 true이면 반환
         * {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise}。
         * 그렇지 않으면 조직 속성 객체 배열을 반환합니다. 예:
         * <pre><code class='language-js'>[{
         *    "name": "부서 유형",
         *    "unit": "개발부@kfb@U",
         *    "attributeList": [
         *        "생산 부서",
         *        "2급 부서"
         *    ]
         * }]</code></pre>
         * @o2syntax
         * //동기 실행, 조직의 모든 속성 객체 배열을 반환합니다.
         * var attributeObjectList = this.org.listUnitAllAttribute( unit );
         *
         * //비동기 실행, Promise 객체 반환
         * var promise = this.org.listUnitAllAttribute( unit, true);
         * promise.then(function(attributeObjectList){
         *     //attributeObjectList는 조직의 모든 속성 객체 배열입니다.
         * })
         *
         * //비동기 실행, 콜백 메서드에서 조회
         * this.org.listUnitAllAttribute( unit, function(attributeObjectList){
         *     //attributeObjectList는 조직의 모든 속성 객체 배열입니다.
         * })
         */
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

    this.Action = (function () {
        var actions = [];
        return function (root, json) {
            var action = actions[root] || (actions[root] = new MWF.xDesktop.Actions.RestActions("", root, ""));
            action.getActions = function (callback) {
                if (!this.actions) this.actions = {};
                Object.merge(this.actions, json);
                if (callback) callback();
            };
            this.invoke = function (option) {
                action.invoke(option)
            }
        }
    })();
    // this.service = {
    //     "jaxwsClient": {},
    //     "jaxrsClient": {}
    // };

    var lookupAction = null;
    var getLookupAction = function (callback) {
        if (!lookupAction) {
            MWF.require("MWF.xDesktop.Actions.RestActions", function () {
                lookupAction = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "");
                lookupAction.getActions = function (actionCallback) {
                    this.actions = {
                        //"lookup": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}"},
                        //"getView": {"uri": "/jaxrs/view/{id}/design"}
                        "lookup": { "uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method": "PUT" },
                        "getView": { "uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}" }
                    };
                    if (actionCallback) actionCallback();
                }
                if (callback) callback();
            });
        } else {
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

    /**
     * view 객체로 뷰 데이터를 조회하거나 뷰 데이터를 선택할 수 있습니다.<br/>
     * @module view
     * @o2cn 뷰 실행
     * @o2category web
     * @o2ordernumber 70
     * @o2syntax
     * //프로세스 양식, 콘텐츠 관리 양식 또는 포털 페이지에서 this로 view 객체를 가져올 수 있습니다. 예:
     * var view = this.view;
     */
    this.view = {
        /**
         * 지정한 뷰의 데이터를 조회합니다.
         * @method lookup
         * @static
         * @param {Object} view - 접근할 뷰 정보. 데이터 형식은 다음과 같습니다:<br/>
         * <caption>아래 filter 매개변수는 <a href='global.html#ViewFilter'>ViewFilter</a> 참고</caption>
         * <pre><code class='language-js'>
         * {
         *  "view" : "testView", //(String) 필수, 뷰의 이름·별칭 또는 ID
         *  "application" : "test데이터 센터 앱", //(String) 필수, 뷰가 속한 데이터 앱의 이름·별칭 또는 ID
         *  "filter": [ //(Array of Object) 선택, 뷰 필터 조건. json 배열 형식이며 각 배열 요소가 하나의 필터 조건을 나타냄.
         *       {
         *           "logic":"and",
         *           "path":"$work.title",
         *           "comparison":"like",
         *           "value":"7월",
         *           "formatType":"textValue"
         *       }
         *  ]
         * }
         * </code></pre>
         * @param {Function} callback - 접근 성공 후 콜백 함수
         * @param {Boolean} [async] - 동기 또는 비동기 호출. true: 비동기, false: 동기. 기본값 true.
         * @return {Promise} Promise 반환
         * @o2syntax
         * //콜백 메서드로 데이터 조회
         * this.view.lookup(view, callback, async);
         *
         * //Promise 객체 반환 후 처리
         * var promise = this.view.lookup( view );
         * promise.then(function(data){
         *     //data는 반환된 데이터입니다.
         * })
         * @example
         * //“재무 관리” 앱의 “경비 결재 데이터” 뷰의 데이터 조회
         * //필터 조건은 제목($work.title)이 “7월”을 포함(like).
         * this.view.lookup({
         *   "view": "경비 결재 데이터",
         *   "application": "재무 관리",
         *   "filter": [
         *       {
         *           "logic":"and",
         *           "path":"$work.title",
         *           "comparison":"like",
         *           "value":"7월",
         *           "formatType":"textValue"
         *       }
         *   ]
         *}, function(data){
         *   var grid = data.grid; //필터링된 데이터
         *   var groupGrid = data.groupGrid; //분류가 있으면 분류가 포함된 데이터
         *   //......
         *});
         * @example
         * //위와 같음, 반환값으로 데이터 조회
         * var promise = this.view.lookup({
         *   "view": "경비 결재 데이터",
         *   "application": "재무 관리",
         *   "filter": [
         *       {
         *           "logic":"and",
         *           "path":"$work.title",
         *           "comparison":"like",
         *           "value":"7월",
         *           "formatType":"textValue"
         *       }
         *   ]
         *});
         * promise.then(function(data){
         *   var grid = data.grid; //필터링된 데이터
         *   var groupGrid = data.groupGrid; //분류가 있으면 분류가 포함된 데이터
         *   //......
         *})
         * @example
         * //“재무 관리” 앱의 “경비 결재 데이터” 뷰의 데이터 조회
         * //필터 조건은 제목($work.title)이 “7월”을 포함(like)하고 총금액이 500보다 크고 5000보다 작음
         * this.view.lookup({
         *   "view": "경비 결재 데이터",
         *   "application": "재무 관리",
         *   "filter": [
         *       {
         *           "logic":"and",
         *           "path":"$work.title",
         *           "comparison":"like",
         *           "value":"7월",
         *           "formatType":"textValue"
         *       },
         *       {
         *           "logic":"and",
         *           "path":"amount",
         *           "comparison":"range",
         *           "value":500,
         *           "otherValue":5000,
         *           "formatType":"numberValue"
         *       },
         *   ]
         *}, function(data){
         *   var grid = data.grid; //필터링된 데이터
         *   var groupGrid = data.groupGrid; //분류가 있으면 분류가 포함된 데이터
         *   //......
         *});
         */
        "lookup": function (view, callback, async) {
            var filterList = { "filterList": (view.filter || null) };
            return MWF.Actions.load("x_query_assemble_surface").ViewAction.executeWithQuery(view.view, view.application, filterList, function (json) {
                var data = {
                    "grid": json.data.grid || json.data.groupGrid,
                    "groupGrid": json.data.groupGrid
                };
                if (callback) callback(data);
                return data;
            }, null, async);
        },

        "lookupV1": function (view, callback) {
            getLookupAction(function () {
                lookupAction.invoke({
                    "name": "lookup", "async": true, "parameter": { "view": view.view, "application": view.application }, "success": function (json) {
                        var data = {
                            "grid": json.data.grid,
                            "groupGrid": json.data.groupGrid
                        };
                        if (callback) callback(data);
                    }.bind(this)
                });
            }.bind(this));
        },

        /**
         * 뷰를 통해 데이터를 선택합니다.
         * @method select
         * @static
         * @param {Object} view - 접근할 뷰 정보. 데이터 형식은 다음과 같습니다:<br/>
         * <caption>아래 filter 매개변수는 <a href='global.html#ViewFilter'>ViewFilter</a> 참고</caption>
         * <pre><code class='language-js'>
         * {
         *  "view" : "testView", //(String) 필수, 뷰의 이름·별칭 또는 ID
         *  "application" : "test데이터 센터 앱", //(String) 필수, 뷰가 속한 데이터 앱의 이름·별칭 또는 ID
         *  "isTitle" : true, //(Boolean) 선택, 뷰 제목 표시 여부. 기본값 true
         *  "isMulti" : true,  //(Boolean) 선택, 다중 선택 허용 여부. 기본값 true
         *  "width" : 700, //(Number) 선택, 선택 창 너비. 기본값 700
         *  "height" : 400,  //(Number) 선택, 선택 창 높이. 기본값 400
         *  "caption" : "제목", //(String) 선택, 선택 창 제목
         *  "filter": [ //(Array of Object) 선택, 뷰 필터 조건. json 배열 형식이며 각 배열 요소가 하나의 필터 조건을 나타냄.
         *       {
         *           "logic":"and",
         *           "path":"$work.title",
         *           "comparison":"like",
         *           "value":"7월",
         *           "formatType":"textValue"
         *       }
         *  ]
         * }
         * </code></pre>
         * @param {Function} callback - 필수, 선택 완료 후 “확인”을 클릭하면 실행되는 콜백 함수.
         * @o2syntax
         * this.view.select(view, callback);
         * @example
         * this.view.select({
         *    "application": "자재 관리",  //데이터 센터의 앱
         *    "view": "자재 뷰",     //뷰 이름
         *    "isMulti": false,           //단일 선택만 허용
         * }, function(items) {
         *    //데이터를 선택하면 해당 데이터를 양식 입력 필드에 할당
         *    if (items.length) {
         *        //자재명, 양식 입력 필드명은 “materialName”, 뷰 열 이름은 “ylmc”
         *        this.data.materialName = items[0].data.ylmc;
         *        //규격, 양식 입력 필드명은 “specification”, 뷰 열 이름은 “gg”
         *        this.data.specification = items[0].data.gg;
         *        //단가, 양식 입력 필드명은 “price”, 뷰 열 이름은 “dj”
         *        this.data.price = items[0].data.dj;
         *    }
         * }.bind(this));
         */
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
    };


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
            html: "<div style='height: 100%; flex:1;'></div>",
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

    /**
     * statement 객체로 쿼리 문을 실행하거나 쿼리 결과를 선택할 수 있습니다.<br/>
     * @module statement
     * @o2cn 쿼리 뷰 실행
     * @o2category web
     * @o2ordernumber 90
     * @o2syntax
     * //프로세스 양식, 콘텐츠 관리 양식, 포털 페이지 또는 뷰에서 this로 statement 객체를 가져올 수 있습니다. 예:
     * var statement = this.statement;
     */
    this.statement = {
        /**
         * 지정한 쿼리 문을 실행합니다.
         * @method execute
         * @static
         * @param {Object} statement - 실행할 쿼리 문 정보. 데이터 형식은 다음과 같습니다:
         * <div>아래 filter 매개변수는 <a href='global.html#StatementFilter'>StatementFilter</a> 참고,
         * parameter 매개변수는 <a href='global.html#StatementParameter'>StatementParameter</a> 참고</div>
         * <pre><code class='language-js'>
         * {
         *  "name" : "tesStatement", //(String) 필수, 쿼리 설정의 이름·별칭 또는 ID
         *  "mode" : "all", //(String) 필수, “all”·“data”·“count” 중 하나. all은 쿼리 문과 총수 문을 함께 실행, data는 쿼리 문 실행, count는 총수 문 실행
         *  "page" : 1, //(number) 선택, 현재 페이지 번호, 기본값 1
         *  "pageSize" : 20, //(number) 선택, 페이지당 데이터 수, 기본값 20
         *  "filter": [ //(Array) 선택, 쿼리 필터 조건. json 배열 형식이며 각 배열 요소가 하나의 필터 조건, 각 요소의 데이터 형식은 다음과 같음:
         *       {
         *           "path":"o.title", //조회 구문 형식이 jpql이면 o.title, 네이티브 sql이면 xtitle 사용
         *           "comparison":"like",
         *           "value":"관련",
         *           "formatType":"textValue"
         *       }
         *  ],
         *  parameter : {
         *       "person" : "", //매개변수 이름이 다음 값이면 서버에서 기본값 할당: person(현재 사용자), identityList(현재 사용자 신분 목록), unitList(현재 사용자의 직속 조직), unitAllList(현재 사용자의 모든 조직), groupList(현재 사용자의 그룹), roleList(현재 사용자의 역할). v8.0 이후 시스템이 자동 해석하므로 이런 매개변수는 전달할 필요 없음.
         *       "startTime" : (new Date("2020-01-01")), //날짜를 비교하는 경우 Date 타입 전달 필요
         *       "applicationName" : "%test%", //연산자가 like, noLike이면 모호 검색
         *       "processName" : "test프로세스", //그 외에는 확정된 값 입력
         *       "?1": "관련" //v8.0 이후 조회문은 물음표+숫자 형식 파라미터 지원
         *     }
         * }
         * </code></pre>
         * @param {Function} callback - 접근 성공 후 콜백 함수
         * @param {Boolean} [async] - 동기 또는 비동기 호출. true: 비동기, false: 동기. 기본값 true.
         * @return {Promise} Promise 반환
         * @o2syntax
         * this.statement.execute(statement, callback, async);
         *
         * //Promise 객체 반환 후 처리
         * var promise = this.statement.execute( statement );
         * promise.then(function(data){
         *     //data는 반환된 데이터입니다.
         * })
         * @example
         * //“task” 쿼리의 데이터 조회
         * //쿼리 문은 select o from Task o where (o.person = :person) and (o.startTime > :startTime) and (o.applicationName like :applicationName) and (o.processName = :processName)
         * //총수 문은 select count(o.id) from Task o where (o.person = :person) and (o.startTime > :startTime) and (o.applicationName like :applicationName) and (o.processName = :processName)
         * //필터 조건은 제목 o.title이 “7월”을 포함(like).
         * this.statement.execute({
         *  "name": "task",
         *  "mode" : "all",
         *  "filter": [
         *      {
         *      "path":"o.title", //조회 구문 형식이 jpql이면 o.title, 네이티브 sql이면 xtitle 사용
         *      "comparison":"like",
         *      "value":"7월",
         *      "formatType":"textValue"
         *      }
         * ],
         * "parameter" : {
         *     "person" : "", //매개변수 이름이 다음 값이면 서버에서 기본값 할당: person(현재 사용자), identityList(현재 사용자 신분 목록), unitList(현재 사용자의 직속 조직), unitAllList(현재 사용자의 모든 조직), groupList(현재 사용자의 그룹), roleList(현재 사용자의 역할). v8.0 이후 시스템이 자동 해석하므로 이런 매개변수는 전달할 필요 없음.
         *     "startTime" : (new Date("2020-01-01")), //날짜를 비교하는 경우 Date 타입 전달 필요
         *     "applicationName" : "%test%", //연산자가 like, noLike이면 모호 검색
         *     "processName" : "test프로세스", //그 외에는 확정된 값 입력
         *     "?1": "관련" //v8.0 이후 조회문은 물음표+숫자 형식 파라미터 지원
         *   }
         * }, function(json){
         *  var count = json.count; //총수 문 실행 후 반환된 숫자
         *  var list = json.data; //쿼리 문 실행 후 반환된 배열
         *   //......
         * });
         * @example
         * //위와 같음, 반환값으로 결과 수신
         * var promise = this.statement.execute({
         *  "name": "task",
         *  "mode" : "all",
         *  "filter": [
         *      {
         *      "path":"o.title", //조회 구문 형식이 jpql이면 o.title, 네이티브 sql이면 xtitle 사용
         *      "comparison":"like",
         *      "value":"7월",
         *      "formatType":"textValue"
         *      }
         * ],
         * "parameter" : {
         *     "person" : "", //매개변수 이름이 다음 값이면 서버에서 기본값 할당: person(현재 사용자), identityList(현재 사용자 신분 목록), unitList(현재 사용자의 직속 조직), unitAllList(현재 사용자의 모든 조직), groupList(현재 사용자의 그룹), roleList(현재 사용자의 역할). v8.0 이후 시스템이 자동 해석하므로 이런 매개변수는 전달할 필요 없음.
         *     "startTime" : (new Date("2020-01-01")), //날짜를 비교하는 경우 Date 타입 전달 필요
         *     "applicationName" : "%test%", //연산자가 like, noLike이면 모호 검색
         *     "processName" : "test프로세스", //그 외에는 확정된 값 입력
         *    "?1": "관련" //v8.0 이후 조회문은 물음표+숫자 형식 파라미터 지원
         *   }
         * });
         * promise.then(function(json){
         *  var count = json.count; //총수 문 실행 후 반환된 숫자
         *  var list = json.data; //쿼리 문 실행 후 반환된 배열
         *   //......
         * })
         */
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
        parseFilter: function (filter, parameter, format) {
            if (typeOf(filter) !== "array") return [];
            if( !parameter )parameter = {};
            var filterList = [];
            (filter || []).each(function (d) {
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
                if (d.comparison === "like" || d.comparison === "notLike") {
                    if (value.substr(0, 1) !== "%") value = "%" + value;
                    if (value.substr(value.length - 1, 1) !== "%") value = value + "%";
                    parameter[parameterName] = value; //"%"+value+"%";
                } else {
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
                    parameter[parameterName] = value;
                }
                d.value = parameterName;

                filterList.push(d);
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

        /**
         * 쿼리 유형이 "select"이고 쿼리 뷰가 설정되어 있으면 이 메서드로 데이터를 선택할 수 있습니다.
         * @method select
         * @static
         * @param {Object} statement - 접근할 쿼리 설정 정보. 데이터 형식은 다음과 같습니다:
         * <div>아래 filter 매개변수는 <a href='global.html#StatementFilter'>StatementFilter</a> 참고,
         * parameter 매개변수는 <a href='global.html#StatementParameter'>StatementParameter</a> 참고</div>
         * <pre><code class='language-js'>
         * {
         *  "name" : "tesStatement", //(String) 필수, 쿼리 설정의 이름·별칭 또는 ID
         *  "isTitle" : true, //(Boolean) 선택, 뷰 제목 표시 여부. 기본값 true
         *  "isMulti" : true,  //(Boolean) 선택, 다중 선택 허용 여부. 기본값 true
         *  "width" : 700, //(Number) 선택, 선택 창 너비. 기본값 700
         *  "height" : 400,  //(Number) 선택, 선택 창 높이. 기본값 400
         *  "caption" : "제목", //(String) 선택, 선택 창 제목
         *  "filter": [ //(Array) 선택, 쿼리 필터 조건. json 배열 형식이며 각 배열 요소가 하나의 필터 조건, 각 요소의 데이터 형식은 다음과 같음:
         *       {
         *           "path":"o.title", //조회 구문 형식이 jpql이면 o.title, 네이티브 sql이면 xtitle 사용
         *           "comparison":"like",
         *           "value":"관련",
         *           "formatType":"textValue"
         *       }
         *  ],
         *  parameter : {
         *       "person" : "", //매개변수 이름이 다음 값이면 서버에서 기본값 할당: person(현재 사용자), identityList(현재 사용자 신분 목록), unitList(현재 사용자의 직속 조직), unitAllList(현재 사용자의 모든 조직), groupList(현재 사용자의 그룹), roleList(현재 사용자의 역할). v8.0 이후 시스템이 자동 해석하므로 이런 매개변수는 전달할 필요 없음.
         *       "startTime" : (new Date("2020-01-01")), //날짜를 비교하는 경우 Date 타입 전달 필요
         *       "applicationName" : "%test%", //연산자가 like, noLike이면 모호 검색
         *       "processName" : "test프로세스", //그 외에는 확정된 값 입력
         *       "?1": "관련" //v8.0 이후 조회문은 물음표+숫자 형식 파라미터 지원
         *     }
         * }
         * </code></pre>
         * @param {Function} callback - 접근 성공 후 콜백 함수
         * @o2syntax
         * this.statement.select(statement, callback);
         * @example
         * this.statement.select({
         *     "name": "자재 쿼리",     //쿼리 이름
         *     "isMulti": false,           //단일 선택만 허용
         * }, function(items) {
         *     //데이터를 선택하면 해당 데이터를 양식 입력 필드에 할당
         *     if (items.length) {
         *         //자재명, 양식 입력 필드명은 “materialName”, 쿼리 문이 반환하는 필드명은 “ylmc”
         *         this.data.materialName = items[0].ylmc;
         *         //규격, 양식 입력 필드명은 “specification”, 쿼리 문이 반환하는 필드명은 “gg”
         *         this.data.specification = items[0].gg;
         *         //단가, 양식 입력 필드명은 “price”, 쿼리 문이 반환하는 필드명은 “dj”
         *         this.data.price = items[0].dj;
         *     }
         * }.bind(this));
         */
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

    /**
     * importer 객체로 가져오기 모델의 Excel 데이터 가져오기 기능을 실행할 수 있습니다.<br/>
     * @module importer
     * @o2cn 데이터 가져오기
     * @o2category web
     * @o2ordernumber 93
     * @o2syntax
     * //프로세스 양식, 콘텐츠 관리 양식, 포털 페이지 또는 뷰에서 this로 statement 객체를 가져올 수 있습니다. 예:
     * var importer = this.importer;
     */
    this.importer = {
        /**
         * 지정한 가져오기 모델로 업로드합니다.
         * @method upload
         * @static
         * @param {Object} options - 실행할 가져오기 모델 옵션. 데이터 형식은 다음과 같습니다:
         * <pre><code class='language-js'>{
         *  "name" : "testImporter", //(String) 필수, 가져오기 모델의 이름·별칭 또는 ID
         *  "application" : "testQuery" //(String) 필수, 가져오기 모델이 속한 앱의 이름·별칭 또는 ID
         * }
         * </code></pre>
         * @param {Function} [callback] - 가져오기 성공 후 콜백 함수
         * @param {Boolean} [async] - 동기 또는 비동기 호출. true: 비동기, false: 동기. 기본값 true.
         * @o2syntax
         * this.importer.upload(options, callback, async);
         * @example
         * this.importer.upload({
         *  "name": "testImporter",
         *  "application" : "testQuery",
         * }, function(json){
         *
         * });
         */
        "upload": function (options, callback, async) {
            MWF.xDesktop.requireApp("query.Query", "Importer", function () {
                var importer = new MWF.xApplication.query.Query.Importer(_form.app.content, options, {}, _form.app, _form.Macro);
                importer.addEvent("afterImport", function (data) {
                    if(callback)callback(data);
                });
                importer.load();
            }.bind(this));

            // MWF.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
            //     statement.name, statement.mode || "data", statement.page || 1, statement.pageSize || 20, obj,
            //     function (json) {
            //         if (callback) callback(json);
            //     }, null, async);
        },
        /**
         * 지정한 가져오기 모델의 Excel 템플릿을 다운로드합니다.
         * @method downloadTemplate
         * @static
         * @param {Object} options - 실행할 가져오기 모델 옵션. 데이터 형식은 다음과 같습니다:
         * <pre><code class='language-js'>{
         *  "name" : "testImporter", //(String) 필수, 가져오기 모델의 이름·별칭 또는 ID
         *  "application" : "testQuery" //(String) 필수, 가져오기 모델이 속한 앱의 이름·별칭 또는 ID
         * }
         * </code></pre>
         * @param {String} fileName - 내보낼 Excel 이름
         * @param {Function} callback - 데이터를 정리해 내보내기 전에 실행하는 메서드, 받을 수 있는 매개변수:
         * <pre><code class='language-js'>{
         *  "data" : ["제목","기안자"], //내보낼 헤더 배열
         *  "colWidthArray" : [200, 150] //열 너비
         * }
         * </code></pre>
         * @o2syntax
         * this.importer.downloadTemplate(object, fileName, callback);
         * @example
         * this.importer.downloadTemplate({
         *  "name": "testImporter",
         *  "application" : "testQuery",
         * },"가져오기 템플릿", function( object ){
         *     //항목 하나 추가
         *     object.data[0].push("비고");
         *     object.colWidthArray.push(300)
         * });
         */
        "downloadTemplate": function(options, fileName,callback){
            MWF.xDesktop.requireApp("query.Query", "Importer", function () {
                var importer = new MWF.xApplication.query.Query.Importer(_form.app.content, options, {}, _form.app, _form.Macro);
                importer.downloadTemplate(fileName, callback);
            }.bind(this));
        }
    };

    //include 스크립트 참조
    //optionsOrName : {
    //  type : "", 기본값 portal, portal  process  cms 가능
    //  application : "", 포털/프로세스/CMS의 이름/별칭/id, 기본값은 현재 앱
    //  name : "" // 스크립트 이름/별칭/id
    //}
    //또는 name: "" // 스크립트 이름/별칭/id
    // if( !window.includedScripts ){
    //     var includedScripts = window.includedScripts = [];
    // }else{
    //     var includedScripts = window.includedScripts;
    // }
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
    var _includeSingle = function (optionsOrName, callback, async) {
        var options = optionsOrName;
        if (typeOf(options) === "string") {
            options = { name: options };
        }
        var name = options.name;
        var type;
        if( options.type === "service" ){
            type = options.type;
        }else{
            type = (options.type && options.application) ? options.type : "portal";
        }
        var application = options.application || _form.json.application;
        var key = type + "-" + application + "-" + name;
        if( type === "service" ){
            key = type + "-" + name;
        }

        //js는 이미 로드했으면 다시 로드하지 않음
        if (includedScripts.indexOf(key) > -1) {
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

    this.define = function (name, fun, overwrite) {
        var over = true;
        if (overwrite === false) over = false;
        var o = {};
        o[name] = { "value": fun, "configurable": over };
        MWF.defineProperties(this, o);
    }.bind(this);

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

    this.invoke = function(name, data){
        return this.Actions.load('x_program_center').InvokeAction.execute(name, data);
    }


    //프런트엔드 객체 전용-----------------------------------------
    //form

    /**
     * 쿼리 설계에서 select 문을 사용하고 뷰를 설정한 경우 쿼리 뷰에서 이 장의 API를 사용할 수 있습니다.<br/>
     * queryStatement 객체는 쿼리 뷰에서 사용 가능합니다. 많은 메서드가 queryView와 비슷합니다.<b>(프론트엔드 스크립트 전용)</b><br/>
     * @module queryStatement
     * @o2cn 쿼리 뷰
     * @o2category web
     * @o2range {QueryStatement}
     * @o2ordernumber 80
     * @borrows module:queryView.confirm as confirm
     * @borrows module:queryView.alert as alert
     * @borrows module:queryView.notice as notice
     * @borrows module:queryView.dialog as dialog
     * @borrows module:queryView.selectOrg as selectOrg
     * @borrows module:queryView.addEvent as addEvent
     * @borrows module:queryView.openWork as openWork
     * @borrows module:queryView.openJob as openJob
     * @borrows module:queryView.openDocument as openDocument
     * @borrows module:queryView.openPortal as openPortal
     * @borrows module:queryView.openCMS as openCMS
     * @borrows module:queryView.openProcess as openProcess
     * @borrows module:queryView.openApplication as openApplication
     * @borrows module:queryView.createDocument as createDocument
     * @borrows module:queryView.startProcess as startProcess
     * @o2syntax
     * //쿼리 뷰에서 this로 queryStatement 객체를 가져올 수 있습니다. 예:
     * var queryStatement = this.queryStatement;
     */

    /**
     * 쿼리 뷰가 포털 페이지, 프로세스 양식 또는 콘텐츠 관리 양식에 삽입된 경우 쿼리 뷰의 스크립트 작성 위치에서 이 메서드로 페이지나 양식의 컨텍스트를 가져올 수 있습니다.
     * @method getParentEnvironment
     * @memberOf module:queryStatement
     * @static
     * @return {MWF.xScript.Environment|MWF.xScript.CMSEnvironment} 페이지 또는 양식의 컨텍스트.
     * @o2syntax
     * this.queryStatement.getParentEnvironment();
     * @example
     * var env = this.queryStatement.getParentEnvironment(); //뷰가 페이지에 삽입된 경우 뷰에서 페이지 컨텍스트 조회
     * env.page.toPortal( "공문 포털" ); //page의 toPage()를 호출해 다른 포털로 이동
     */

    /**
     * 쿼리 뷰 현재 페이지의 기본 정보를 조회합니다.
     * @method getPageInfor
     * @memberOf module:queryStatement
     * @static
     * @return {Object} 현재 페이지 정보, 형식은 다음과 같음:
     *<pre><code class='language-js'>{
     *     "pages": 3, //총 페이지 수
     *     "perPageCount": 50, //페이지당 건수
     *     "currentPageNumber": 1 // 현재 페이지 번호
     * }
     * </code></pre>
     * @o2syntax
     * this.queryStatement.getPageInfor();
     */

    /**
     * 현재 페이지의 데이터를 조회합니다.
     * @method getPageData
     * @memberOf module:queryStatement
     * @static
     * @return {Object[]|Array[]} 현재 페이지 데이터.
     * <div>데이터 형식은 jpql 문 작성 방식에 따라 다름</div>
     * 예:  "select o from table o"는 json 배열 반환
     *<pre><code class='language-js'>[
     {
     "id" : "id1",
     "title" : "title1"
     },
     {
     "id" : "id2",
     "title" : "title2"
     },
     ...
     *]
     * </code></pre>
     * 예: "select id, title from table o"는 2차원 배열 반환:
     *<pre><code class='language-js'>[
     ["id1", "title1"],
     ["id2", "title2"],
     ...
     *]
     *</code></pre>
     * @o2syntax
     * var data = this.queryStatement.getPageData();
     */

    /**
     * 지정한 페이지로 이동합니다.
     * @method toPage
     * @memberOf module:queryStatement
     * @static
     * @param {Number} pageNumber - 이동할 페이지 번호.
     * @param {Function} [callback ] - 이동한 페이지 데이터 로드 완료 후 콜백 메서드.
     * @o2syntax
     * var data = this.queryStatement.toPage( pageNumber, callback );
     * @example
     * //　2페이지로 이동하고 해당 페이지 데이터 조회.
     * this.queryStatement.toPage( 2, function(){
     *      var data = this.queryStatement.getPageData();
     * }.bind(this) )
     */

    /**
     * 쿼리 뷰에서 다중 선택을 허용한 경우 이 메서드로 현재 페이지 항목을 모두 선택할 수 있습니다.
     * @method selectAll
     * @memberOf module:queryStatement
     * @static
     * @o2syntax
     * this.queryStatement.selectAll();
     */

    /**
     * 쿼리 뷰에서 다중 선택을 허용한 경우 이 메서드로 선택된 항목을 해제할 수 있습니다.
     * @method unSelectAll
     * @memberOf module:queryStatement
     * @static
     * @o2syntax
     * this.queryStatement.unSelectAll();
     */

    /**
     * 선택된 항목의 데이터를 조회합니다.
     * @method getSelectedData
     * @memberOf module:queryStatement
     * @static
     * @return {Object[]|Array[]} 선택된 항목의 데이터.
     * <div>데이터 형식은 jpql 문 작성 방식에 따라 다름</div>
     * 예:  "select o from table o"는 json 배열 반환
     *<pre><code class='language-js'>[
     {
     "id" : "id1",
     "title" : "title1"
     },
     {
     "id" : "id2",
     "title" : "title2"
     },
     ...
     *]
     * </code></pre>
     * 예: "select id, title from table o"는 2차원 배열 반환:
     *<pre><code class='language-js'>[
     ["id1", "title1"],
     ["id2", "title2"],
     ...
     *]
     *</code></pre>
     * @o2syntax
     * var data = this.queryStatement.getSelectedData();
     */

    /**queryStatement에 대응하는 DOM 객체를 조회합니다.
     * @method node
     * @static
     * @methodOf module:queryStatement
     * @see module:form.node
     */


    /**
     * 쿼리 뷰를 다시 로드합니다.
     * @method reload
     * @methodOf module:queryStatement
     * @static
     * @o2syntax
     * this.queryStatement.reload( callback );
     */


    /**
     * queryView 객체는 뷰에서 사용 가능합니다. 많은 메서드가 form과 비슷합니다.<b>(프론트엔드 스크립트 전용)</b><br/>
     * @module queryView
     * @o2cn 뷰
     * @o2category web
     * @o2range {QueryView}
     * @o2ordernumber 60
     * @o2syntax
     * //뷰에서 this로 queryView 객체를 가져올 수 있습니다. 예:
     * var queryView = this.queryView;
     */
    this.page = this.form = this.queryView = this.queryStatement = {
        /**
         * 뷰가 포털 페이지, 프로세스 양식 또는 콘텐츠 관리 양식에 삽입된 경우 뷰의 스크립트 작성 위치에서 이 메서드로 페이지나 양식의 컨텍스트를 가져올 수 있습니다.
         * @method getParentEnvironment
         * @static
         * @return {MWF.xScript.Environment|MWF.xScript.CMSEnvironment} 페이지 또는 양식의 컨텍스트.
         * @o2syntax
         * this.queryView.getParentEnvironment();
         * @example
         * var env = this.queryView.getParentEnvironment(); //뷰가 페이지에 삽입된 경우 뷰에서 페이지 컨텍스트 조회
         * env.page.toPortal( "공문 포털" ); //page의 toPage()를 호출해 다른 포털로 이동
         */
        "getParentEnvironment" : function () { return _form.getParentEnvironment(); }, //뷰가 삽입된 양식 또는 페이지의 컨텍스트

        /**
         * 쿼리의 설정 정보를 조회합니다.
         * @method getStatementInfor
         * @memberOf module:queryStatement
         * @static
         * @return {Object} 쿼리 설정 정보.
         * <pre><code class='language-js'>{
         *    "query": "26d21c71-5114-4496-8ca1-a69e56324841", //소속 앱 id
         *    "id": "ee334220-66d3-4f78-afce-8ccf6b995c8c", //쿼리 id
         *    "name": "테스트 쿼리", //이름
         *    "alias": "", //별칭
         *    "description": "", //설명
         *    "table": "", //사용자 정의 테이블 id
         *    "entityClassName": "com.x.processplatform.core.entity.content.Task", //시스템 테이블 이름
         *    "entityCategory": "official", //테이블 유형 official(시스템 테이블) 또는 dynamic(사용자 정의 테이블)
         *    "format": "jpql", //문 유형, jpql 또는 script(스크립트), v8.0 이후 sql, sqlScript 추가
         *    "type": "select", //select/update/delete
         *    "data": "SELECT o FROM Task o where o.person = :person", //쿼리 문
         *    "countData": "SELECT count(o.id) FROM Task o where o.person = :person", //총수 문
         *    "countScriptText" : "", //총수 문 스크립트
         *    "scriptText" : "", //쿼리 문 스크립트
         *    "viewJson": { ... } //뷰 관련 정보
         * }</code></pre>
         * @o2syntax
         * this.queryStatement.getStatementInfor();
         */
        "getStatementInfor" : function () { return _form.getStatementInfor ? _form.getStatementInfor() : null; },

        /**
         * 쿼리의 설정 정보를 조회합니다.
         * @method getViewInfor
         * @memberOf module:queryView
         * @static
         * @return {Object} 뷰 설정 정보.
         * <pre><code class='language-js'>{
         *      "application": "db9fc893-7dbc-4e0f-a617-99089d2c6323", //뷰가 속한 앱
         *      "query": "db9fc893-7dbc-4e0f-a617-99089d2c6323",  //뷰가 속한 앱, application과 같음
         *      "name": "뷰 사용자 정의 테스트", //뷰 이름
         *      "viewName": "뷰 사용자 정의 테스트",  //뷰 이름, name과 같음
         *      "isExpand": "no",  //분류가 있으면 기본 펼침 여부
         *      "id": "705ce967-2f9c-425c-8873-3bd729249e1d", //뷰 id
         *      "alias": "", //뷰 별칭
         *      "description": "",  //뷰 설명
         *      "display": true, //뷰 표시 여부
         *      "type": "cms", //뷰에 삽입되는 데이터 유형, cms 또는 process
         *      "count": 2000, //최대 2000건 반환
         *      "pageSize": 20, //페이지당 건수
         *      "createTime": "2019-09-02 10:18:27",
         *      "updateTime": "2020-03-26 15:53:03"
         * }</code></pre>
         * @o2syntax
         * this.queryView.getViewInfor();
         */
        "getViewInfor" : function () { return _form.getViewInfor(); },

        /**
         * 뷰 현재 페이지의 기본 정보를 조회합니다.
         * @method getPageInfor
         * @memberOf module:queryView
         * @static
         * @return {Object} 현재 페이지 정보, 형식은 다음과 같음:
         *<pre><code class='language-js'>{
         *     "pages": 3, //총 페이지 수
         *     "perPageCount": 50, //페이지당 건수
         *     "currentPageNumber": 1 // 현재 페이지 번호
         * }
         * </code></pre>
         * @o2syntax
         * this.queryView.getPageInfor();
         */
        "getPageInfor" : function () { return _form.getPageInfor(); },

        /**
         * 현재 페이지의 데이터를 조회합니다.
         * @method getPageData
         * @memberOf module:queryView
         * @static
         * @return {Object[]} 현재 페이지 데이터.
         * <div>분류가 없을 때 데이터 형식은 다음과 같음:</div>
         *<pre><code class='language-js'>[
         *   {
         *       "bundle": "099ed3c9-dfbc-4094-a8b7-5bfd6c5f7070", //cms의 documentId, process의 jobId
         *      "data": {  //뷰에 설정된 데이터
         *        "title": "근태 관리-설정-통계 주기 설정", //열 이름 및 열 값
         *        "time": "2018-08-25 11:29:45"
         *      }
         *    },
         *   ...
         *]
         * </code></pre>
         * 분류가 있을 때 데이터 형식은 다음과 같음:
         *<pre><code class='language-js'>[
         *  {
         *   "group": "업무 일지",  //분류1
         *   "list": [  //분류 아래 데이터
         *     {
         *       "bundle": "001257be-725a-43cf-9679-3892bbab696a", //cms의 documentId, process의 jobId
         *       "data": { //뷰에 설정된 데이터
         *         "title": "제목",  //열 이름 및 열 값
         *         "time": "2018-07-31 15:39:13",
         *         "category": "업무 일지"
         *       }
         *     },
         *     ...
         *   ]
         *  },
         *  ...
         *]
         *</code></pre>
         * @o2syntax
         * var data = this.queryView.getPageData();
         */
        "getPageData" : function () { return _form.getPageData(); },

        /**
         * 지정한 페이지로 이동합니다.
         * @method toPage
         * @memberOf module:queryView
         * @static
         * @param {Number} pageNumber - 이동할 페이지 번호.
         * @param {Function} [callback ] - 이동한 페이지 데이터 로드 완료 후 콜백 메서드.
         * @o2syntax
         * var data = this.queryView.toPage( pageNumber, callback );
         * @example
         * //　2페이지로 이동하고 해당 페이지 데이터 조회.
         * this.queryView.toPage( 2, function(){
         *      var data = this.queryView.getPageData();
         * }.bind(this) )
         */
        "toPage" : function ( pageNumber, callback ) { return _form.toPage(pageNumber, callback); },

        /**
         * 뷰에서 다중 선택을 허용한 경우 이 메서드로 현재 페이지 항목을 모두 선택할 수 있습니다.
         * @method selectAll
         * @memberOf module:queryView
         * @static
         * @o2syntax
         * this.queryView.selectAll();
         */
        "selectAll" : function () { return _form.selectAll(); },

        /**
         * 뷰에서 다중 선택을 허용한 경우 이 메서드로 선택된 항목을 해제할 수 있습니다.
         * @method unSelectAll
         * @memberOf module:queryView
         * @static
         * @o2syntax
         * this.queryView.unSelectAll();
         */
        "unSelectAll" : function () { return _form.unSelectAll(); },

        /**
         * 선택된 항목의 데이터를 조회합니다.
         * @method getSelectedData
         * @memberOf module:queryView
         * @static
         * @return {Object[]} 선택된 항목의 데이터.
         * <div>형식은 다음과 같음:</div>
         * <pre><code class='language-js'>
         * [
         {
         "bundle": "099ed3c9-dfbc-4094-a8b7-5bfd6c5f7070", //cms의 documentId, process의 jobId
         "data": {  //뷰에 설정된 데이터
         "title": "근태 관리-설정-통계 주기 설정", //열 이름 및 열 값
         "time": "2018-08-25 11:29:45"
         }
         },
         ...
         * ]
         </code></pre>
         * @o2syntax
         * var data = this.queryView.getSelectedData();
         */
        "getSelectedData" : function () { return _form.getSelectedData(); },

        /**
         * 뷰의 필터 조건을 설정합니다. 이 메서드는 뷰의 기본 필터 조건(뷰 개발 시 추가한 조건)을 수정하지 않고 그 위에 추가합니다.
         * @method setFilter
         * @memberOf module:queryView
         * @static
         * @param {(ViewFilter[]|ViewFilter|Null)} [filter] 필터 조건.<br/>
         * 매개변수를 전달하지 않거나 null 또는 빈 배열이면 뷰 기본값이 아닌 필터 조건을 모두 제거.<br/>
         * 객체 또는 비어 있지 않은 배열을 전달하면 매개변수는 다음과 같음:
         * <pre><code class='language-js'>[
         *    {
         *       "logic":"and",
         *       "path":"$work.title",
         *       "comparison":"like",
         *       "value":"7월",
         *       "formatType":"textValue"
         *   }
         *]
         * </code></pre>
         * @param {Function} [callback] 필터링 후 데이터를 다시 로드한 뒤 콜백 메서드.
         * @o2syntax
         * this.queryView.setFilter( filter );
         */
        "setFilter" : function ( filter, callback ) { return _form.setFilter(filter, callback); },

        /**
         * 쿼리 문 where 절의 필터 조건을 추가합니다.
         * @method setStatementFilter
         * @memberOf module:queryStatement
         * @static
         * @param {(StatementFilter[]|Null)} [filter] 필터 조건.<br/>
         * 필터 조건. 매개변수를 전달하지 않거나 null 또는 빈 배열이면 뷰 기본값이 아닌 필터 조건을 모두 제거.<br/>
         * 비어 있지 않은 배열을 전달하면 매개변수는 다음과 같음:
         * <pre><code class='language-js'>[
         *    {
         *      "path":"o.title", //조회 구문 형식이 jpql이면 o.title, 네이티브 sql이면 xtitle 사용
         *      "comparison":"like",
         *      "value":"관련",
         *      "formatType":"textValue"
         *  }
         *]
         * </code></pre>
         * @param {StatementParameter} [parameter] 필터 조건. 쿼리 문 where 절의 ":person" 형태 매개변수 부분에 값을 할당, 매개변수는 다음과 같음:
         * <pre><code class='language-js'>
         * //쿼리 문이 select count(o.id) from Read o where (o.person = :person) and (o.startTime > :startTime) and (o.applicationName like :applicationName) and (o.processName = :processName)라고 가정.
         * //가능한 매개변수는 다음과 같음:
         * {
         *    "person" : "", //보안상 매개변수 이름이 다음 값이면 값을 입력할 필요 없이 서버에서 기본값 할당: person(현재 사용자), identityList(현재 사용자 신분 목록), unitList(현재 사용자의 직속 조직), unitAllList(현재 사용자의 모든 조직), groupList(현재 사용자의 그룹), roleList(현재 사용자의 역할). v8.0 이후 시스템이 자동 해석하므로 이런 매개변수는 전달할 필요 없음.
         *    "startTime" : (new Date("2020-01-01")), //날짜를 비교하는 경우 Date 타입 전달 필요
         *    "applicationName" : "%test%", //연산자가 like, noLike이면 모호 검색
         *    "processName" : "test프로세스", //그 외에는 확정된 값 입력
         *    "?1": "관련" //v8.0 이후 조회문은 물음표+숫자 형식 파라미터 지원
         * }
         * </code></pre>
         * @param {Function} [callback] 필터링 후 데이터를 다시 로드한 뒤 콜백 메서드.
         * @o2syntax
         * this.queryStatement.setStatementFilter( filter, parameter, callback );
         */
        "setStatementFilter" : function ( filter , parameter, callback) { return _form.setFilter(filter, parameter, callback); },

        /**
         * 현재 뷰를 다른 뷰로 전환합니다.
         * @method switchView
         * @memberOf module:queryView
         * @static
         * @param {Object} options 이동에 필요한 매개변수 설정. 설명은 다음과 같음:
         * <div>아래 filter 속성은 <a href='global.html#ViewFilter'>ViewFilter</a> 참고</div>
         * <pre><code class='language-js'>{
         *     "application": application, //필수, 뷰가 속한 앱 id
         *     "viewName": viewName, //필수, 뷰 이름
         *     "filter": [
         *         {
         *            "logic":"and",
         *             "path":"$work.title",
         *             "comparison":"like",
         *             "value":"7월",
         *             "formatType":"textValue"
         *         }
         *     ], //선택, 뷰 필터 조건(ViewFilter) 추가, 전달하지 않으면 원래 뷰 설정 사용, 원래 뷰 설정을 제거하려면 빈 배열 [] 전달
         *     "isTitle": "yes", //선택, 뷰 제목 행 표시 여부, 가능한 값: yes no
         *     "select": "none", //선택, 새 뷰 선택 허용 여부, 전달하지 않으면 원래 뷰 설정 사용, 가능한 값: 선택 불가 none, 단일 선택 single, 다중 선택 multi
         *     "titleStyles": {
         *       "color" : "red",
         *       "font-size" : "14px"
         *     }, //선택, 제목 행 스타일, 전달하지 않으면 원래 뷰 설정 사용
         *     "itemStyles": {
         *       "color" : "#333",
         *       "font-size" : "12px"
         *     }, //선택, 내용 행 스타일, 전달하지 않으면 원래 뷰 설정 사용
         *     "isExpand": "no", //선택, 분류 기본 펼침 여부, 전달하지 않으면 원래 뷰 설정 사용, 가능한 값: yes no
         *   }
         * </code></pre>
         * @o2syntax
         * this.queryView.switchView( options );
         */
        "switchView" : function ( options ) { return _form.switchView(options); },

        /**
         * 현재 쿼리 뷰를 다른 쿼리 뷰로 전환합니다.
         * @method switchStatement
         * @memberOf module:queryStatement
         * @static
         * @param {Object} options 이동에 필요한 매개변수 설정. 설명은 다음과 같음:
         * <div>아래 filter 속성은 <a href='global.html#StatementFilter'>StatementFilter</a> 참고,
         * parameter 속성은 <a href='global.html#StatementParameter'>StatementParameter</a> 참고</div>
         * <pre><code class='language-js'>this.queryStatement.switchStatement({
         *     "statementId": statementId, //필수, 쿼리의 이름·별칭·id
         *     "isTitle": "yes", //선택, 뷰 제목 행 표시 여부, 가능한 값: yes no
         *     "select": "multi", //선택, 새 뷰 선택 허용 여부, 전달하지 않으면 원래 뷰 설정 사용, 가능한 값: 선택 불가 none, 단일 선택 single, 다중 선택 multi
         *     "showActionbar": false, //선택, 작업 표시줄 표시 여부
         *     "filter": [  //선택, 쿼리 문 where 절의 필터 조건 추가
         *       {
         *         "path": "o.title", //쿼리 문 형식이 jpql이면 o.title, 네이티브 sql이면 xtitle 사용
         *         "title": "제목",
         *         "type": "filter",
         *         "comparison": "like",
         *         "formatType": "textValue",
         *         "value": "테스트"
         *       }
         *     ],
         *     //쿼리 문이 select count(o.id) from Read o where (o.person = :person) and (o.startTime > :startTime) and (o.applicationName like :applicationName) and (o.processName = :processName)라고 가정
         *     "parameter" : { //선택, 쿼리 문 where 절의 ":person" 형태 매개변수 부분에 값 할당
         *       "person" : "", //보안상 매개변수 이름이 다음 값이면 값을 입력할 필요 없이 서버에서 기본값 할당: person(현재 사용자), identityList(현재 사용자 신분 목록), unitList(현재 사용자의 직속 조직), unitAllList(현재 사용자의 모든 조직), groupList(현재 사용자의 그룹), roleList(현재 사용자의 역할). v8.0 이후 시스템이 자동 해석하므로 이런 매개변수는 전달할 필요 없음.
         *       "startTime" : (new Date("2020-01-01")), //날짜를 비교하는 경우 Date 타입 전달 필요
         *       "applicationName" : "%test%", //연산자가 like, noLike이면 모호 검색
         *       "processName" : "test프로세스" //기타는 확정 값 입력
         *       "?1": "관련" //v8.0 이후 조회문은 물음표+숫자 형식 파라미터 지원
         *     }
         *   })
         * </code></pre>
         * @o2syntax
         * this.queryStatement.switchStatement( options );
         */
        "switchStatement" : function ( options ) { if(_form.switchStatement)_form.switchStatement(options) ; },

        /**
         * 뷰를 다시 로드합니다.
         * @method reload
         * @methodOf module:queryView
         * @static
         * @o2syntax
         * this.queryView.reload( callback );
         */
        "reload" : function ( callback ) { _form.reload( callback ); },

        // "getInfor": function () { return ev.pageInfor; },
        // "infor": ev.pageInfor,
        /**현재 페이지를 연 component 객체 조회.
         * @method getApp
         * @static
         * @see module:form.getApp
         */
        getApp: function () { return _form.app; },
        // "app": _form.app,

        /**queryView에 대응하는 DOM 객체를 조회합니다.
         * @method node
         * @static
         * @methodOf module:queryView
         * @see module:form.node
         */
        "node": function () { return _form.node; },

        // "get": function (name) { return (_form.all) ? _form.all[name] : null; },
        // "getWidgetModule": function (widget, moduleName) {
        //     if (!_form.widgetModules || !_form.widgetModules[widget]) return null;
        //     var module = _form.widgetModules[widget][moduleName];
        //     return module || null;
        // },
        // "getField": function (name) { return _forms[name]; },
        // "getAction": function () { return _form.workAction },
        "getDesktop": function () { return _form.app ? _form.app.desktop : null},
        // "getData": function () { return new MWF.xScript.JSONData(_form.getData()); },
        //"save": function(callback){_form.saveWork(callback);},
        // "close": function () { _form.closeWork(); },

        // "print": function (application, form) {
        //     _form.printWork(application, form);
        // },

        /**확인 창을 띄웁니다.
         * @method confirm
         * @static
         * @methodOf module:queryView
         * @see module:form.confirm
         */
        "confirm": function (type, title, text, width, height, ok, cancel, callback, mask, style) {
            // var p = MWF.getCenter({"x": width, "y": height});
            // e = {"event": {"clientX": p.x,"x": p.x,"clientY": p.y,"y": p.y}};
            // _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
            if ((arguments.length <= 1) || o2.typeOf(arguments[1]) === "string") {
                var p = MWF.getCenter({ "x": width, "y": height });
                e = { "event": { "clientX": p.x, "x": p.x, "clientY": p.y, "y": p.y } };
                _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
            } else {
                e = (arguments.length > 1) ? arguments[1] : null;
                title = (arguments.length > 2) ? arguments[2] : null;
                text = (arguments.length > 3) ? arguments[3] : null;
                width = (arguments.length > 4) ? arguments[4] : null;
                height = (arguments.length > 5) ? arguments[5] : null;
                ok = (arguments.length > 6) ? arguments[6] : null;
                cancel = (arguments.length > 7) ? arguments[7] : null;
                callback = (arguments.length > 8) ? arguments[8] : null;
                mask = (arguments.length > 9) ? arguments[9] : null;
                style = (arguments.length > 10) ? arguments[10] : null;
                _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
            }
        },

        /**닫기 버튼이 있는 정보 창을 표시합니다.
         * @method alert
         * @static
         * @methodOf module:queryView
         * @see module:form.alert
         */
        "alert": function(type, title, text, width, height){
            _form.alert(type, title, text, width, height);
        },

        /**정보 창을 표시합니다.
         * @method notice
         * @static
         * @methodOf module:queryView
         * @see module:form.notice
         */
        "notice": function (content, type, target, where, offset, option) {
            _form.notice(content, type, target, where, offset, option);
        },

        /**대화상자 열기
         * @method dialog
         * @static
         * @see module:form.dialog
         */
        "dialog": function ( options ) {
            return  _form.dialog( options );
        },

        /**사용자 조직 선택 화면 열기
         * @method selectOrg
         * @static
         * @see module:form.selectOrg
         */
        "selectOrg": function ( container, options,  delayLoad) {
            if( !container )container = _form.app.content;
            MWF.xDesktop.requireApp("Selector", "package", null, false);
            return new MWF.O2Selector(container, options, delayLoad);
        },

        /**　뷰에 이벤트를 추가합니다.
         * @method addEvent
         * @static
         * @methodOf module:queryView
         * @see module:form.addEvent
         */
        "addEvent": function (e, f) { _form.addEvent(e, f); },

        // "openWindow": function (form, app) {
        //     _form.openWindow(form, app);
        // },
        // "toPage": function (name, par, nohis) {
        //     _form.app.toPage(name, par, nohis);
        // },
        // "toPortal": function (portal, page, par) {
        //     _form.app.toPortal(portal, page, par);
        // },

        /**진행 중이거나 완료된 프로세스 인스턴스를 엽니다.
         * @method openWork
         * @static
         * @methodOf module:queryView
         * @see module:form.openWork
         */
        "openWork": function (id, completedId, title, options) {
            var op = options || {};
            op.workId = id;
            op.workCompletedId = completedId;
            op.docTitle = title;
            op.appId = "process.Work" + (op.workId || op.workCompletedId);
            return layout.desktop.openApplication(this.event, "process.Work", op);
        },

        /**프로세스의 jobId로 업무 문서를 엽니다.
         * @method openJob
         * @static
         * @methodOf module:queryView
         * @see module:form.openJob
         */
        "openJob": function (id, choice, options, callback) {
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
        },

        /**콘텐츠 관리 문서를 엽니다.
         * @method openDocument
         * @static
         * @methodOf module:queryView
         * @see module:form.openDocument
         */
        "openDocument": function (id, title, options) {
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

        /**포털 페이지를 엽니다.
         * @method openPortal
         * @static
         * @methodOf module:queryView
         * @see module:form.openPortal
         */
        "openPortal": function (name, page, par) {
            var action = MWF.Actions.get("x_portal_assemble_surface");
            action.getApplication(name, function (json) {
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
        /**포털 페이지 열기
         * @method loadPortal
         * @static
         * @see module:form.loadPortal
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
            app.load(true, content);

            return app;
        },

        /**콘텐츠 관리 게시판을 엽니다.
         * @method openCMS
         * @static
         * @methodOf module:queryView
         * @see module:form.openCMS
         */
        "openCMS": function (name) {
            var action = MWF.Actions.get("x_cms_assemble_control");
            action.getColumn(name, function (json) {
                if (json.data) {
                    layout.desktop.openApplication(null, "cms.Module", {
                        "columnId": json.data.id,
                        "appId": "cms.Module" + json.data.id
                    });
                }
            });
        },

        /**프로세스 앱을 엽니다.
         * @method openProcess
         * @static
         * @methodOf module:queryView
         * @see module:form.openProcess
         */
        "openProcess": function (name) {
            var action = MWF.Actions.get("x_processplatform_assemble_surface");
            action.getApplication(name, function (json) {
                if (json.data) {
                    layout.desktop.openApplication(null, "process.Application", {
                        "id": json.data.id,
                        "appId": "process.Application" + json.data.id
                    });
                }
            });
        },

        /**임의의 component 앱을 엽니다.
         * @method openApplication
         * @static
         * @methodOf module:queryView
         * @see module:form.openApplication
         */
        "openApplication": function (name, options, status) {
            return layout.desktop.openApplication(null, name, options, status);
        },

        /**콘텐츠 관리 문서를 생성합니다.
         * @method createDocument
         * @static
         * @methodOf module:queryView
         * @see module:form.createDocument
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
            // 모바일 app 관련 구현
            if (layout.mobile) {
                var options = {};
                if (column) {
                    options["column"] = column
                }
                if (category) {
                    options["category"] = category
                }
                if (data) {
                    options["data"] = data
                }
                if (identity) {
                    options["identity"] = identity
                }
                if (typeof(latest) == 'undefined' || latest === null) {
                    options["latest"] = true
                } else {
                    options["latest"] = latest
                }
                if (typeof(ignoreTitle) == 'undefined' || ignoreTitle === null) {
                    options["ignoreTitle"] = false
                } else {
                    options["ignoreTitle"] = ignoreTitle
                }
                if (window.o2android && window.o2android.postMessage) {
                    var body = {
                        type: "createO2CmsDocument",
                        data: options
                    };
                    window.o2android.postMessage(JSON.stringify(body));
                    return;
                } else if (window.o2android && window.o2android.createO2CmsDocument){
                    window.o2android.createO2CmsDocument(JSON.stringify(options));
                    return;
                } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.createO2CmsDocument) {
                    window.webkit.messageHandlers.createO2CmsDocument.postMessage(options);
                    return;
                }
            }
            // 아래는 pc용
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

        /**　프로세스 인스턴스를 시작합니다.
         * @method startProcess
         * @static
         * @methodOf module:queryView
         * @see module:form.startProcess
         */
        "startProcess": function (app, process, data, identity, callback, target, latest, afterCreated, skipDraftCheck) {

            if (arguments.length > 2) {
                for (var i = 2; i < arguments.length; i++) {
                    if (typeOf(arguments[i]) == "boolean") {
                        target = arguments[i];
                        break;
                    }
                }
            }

            if (target) {
                if (layout.app && layout.app.inBrowser) {
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
            var action = MWF.Actions.get("x_processplatform_assemble_surface").getProcessByName(process, app, function (json) {
                if (json.data) {
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
                        "onStarted": function (data, title, processName) {
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
                                    application =layout.desktop.openApplication(null, "process.Work", options);
                                }else{}
                            }

                            // var currentTask = [];
                            // data.each(function (work) {
                            //     if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
                            // }.bind(this));
                            //
                            // if (currentTask.length == 1) {
                            //     var options = { "workId": currentTask[0], "appId": currentTask[0] };
                            //     layout.desktop.openApplication(null, "process.Work", options);
                            // } else { }

                            if (callback) callback(data);

                            if(layout.inBrowser && afterCreated){
                                afterCreated(application)
                            }
                        }.bind(this)
                    });
                    starter.load();
                }
            });
        }
        //"app": _form.app
    };
    // this.form.currentRouteName = _form.json.currentRouteName;
    // this.form.opinion = _form.json.opinion;

    this.target = ev.target;
    this.event = ev.event;
    this.status = ev.status;

    /**
     * 프론트엔드 스크립트에서 this.session.user로 현재 사용자 정보를 가져올 수 있습니다.<br/>
     * @module session
     * @o2cn 현재 사용자
     * @o2category web
     * @o2ordernumber 110
     * @o2syntax
     * var user = this.session.user;
     * @example
     * //현재 사용자 정보 조회
     * var user = this.session.user
     * @return {Object} 현재 사용자 정보, 내용과 형식은 다음과 같음:
     * <pre><code class='language-js'>{
     *    "id": "267a7bcc-f27a-49c8-8364-f1c12061085a",       //사용자 ID
     *    "genderType": "m",                                  //성별
     *    "icon": "...",                                      //프로필 사진
     *    "signature": "",                                    //개인 서명
     *    "name": "홍길동",                                     //이름
     *    "employee": "zhansan",                                 //사번
     *    "unique": "zhansan",                                   //고유 식별자
     *    "distinguishedName": "xx@zhansan@P",                   //사용자 전체 이름
     *    "superior": "",                                     //상급자 id
     *    "changePasswordTime": "2017-03-13",                 //비밀번호 변경 시간
     *    "lastLoginTime": "2019-01-02",                      //마지막 로그인 시간
     *    "mail": "zhansan@zoneland.net",                        //이메일 주소
     *    "weixin": "",                                       //위챗 ID
     *    "qq": "",                                           //QQ
     *    "mobile": "18057190078",                            //휴대폰 번호
     *    "officePhone": "",                                  //사무실 전화
     *    "createTime": "2017-03-13 12:27:04",                //사용자 생성 시간
     *    "updateTime": "2019-01-02 13:00:04",                //사용자 수정 시간
     *    "token": "...",                         //현재 사용자 token
     *    "roleList": [                                       //사용자 역할
     *      "ProcessPlatformCreator@ProcessPlatformCreatorSystemRole@R",
     *      "ProcessPlatformManager@ProcessPlatformManagerSystemRole@R",
     *      "Manager@ManagerSystemRole@R"
     *   ],
     *    "identityList": [                                   //사용자 신분 목록
     *      {
     *        "id": "709328c8-44a0-4f5d-a3fa-3c31208232d5",                       //신분 ID
     *        "name": "xx",                                                       //신분 이름
     *        "unique": "709328c8-44a0-4f5d-a3fa-3c31208232d5",                   //신분 고유 식별자
     *        "distinguishedName": "xx@709328c8-44a0-4f5d-a3fa-3c31208232d5@I",   //신분 전체 이름
     *        "person": "267a7bcc-f27a-49c8-8364-f1c12061085a",                   //사용자 ID
     *        "unit": "d5356fd4-6675-45ad-9a00-5eff20b83dfa",                     //소속 조직 ID
     *        "unitName": "개발부",                                               //소속 조직 이름
     *        "unitLevel": 2,                                                     //소속 조직 레벨
     *        "unitLevelName": "인디고/개발부",                                 //소속 조직 계층 이름
     *        "major": true                                                       //주 신분 여부
     *      },
     *      {
     *        "id": "343510af-57c2-4a55-a1f2-f30d7af6d284",
     *        "description": "",
     *        "name": "xx",
     *        "unique": "343510af-57c2-4a55-a1f2-f30d7af6d284",
     *        "distinguishedName": "xx@343510af-57c2-4a55-a1f2-f30d7af6d284@I",
     *        "person": "267a7bcc-f27a-49c8-8364-f1c12061085a",
     *        "unit": "108b1b7c-cc78-49ab-9ab1-e67073bd6541",
     *        "unitName": "개발부",
     *        "unitLevel": 2,
     *        "unitLevelName": "인디고/개발부",
     *        "major": false
     *      }
     *    ]
     *  }
     * </code></pre>
     */
    this.session = layout.desktop.session;



    /**
     * 이 문서는 프론트엔드 스크립트에서 Actions로 플랫폼 RESTful 서비스를 호출하는 방법을 설명합니다.<br/>
     * 다음 주소에서 서비스 목록 조회: http://server/x_program_center/jest/list.html (v7.2 이전 버전은 포트 20030 추가 필요)
     * @module Actions
     * @o2cn 서비스 호출
     * @o2category web
     * @o2ordernumber 130
     * @o2syntax
     * //Actions 조회
     * this.Actions
     */

    /**
     * 플랫폼의 서비스 루트마다 도메인이나 포트가 다를 수 있으며, this.Actions.getHost로 서비스 루트에 대응하는 host를 가져올 수 있습니다.
     * @method getHost
     * @methodOf module:Actions
     * @static
     * @param {String} root 플랫폼 RESTful 서비스 루트, 서비스 목록은 http://server/x_program_center/jest/list.html 참고.(v7.2 이전 버전은 포트 20030 추가 필요)
     *예:<pre><code class='language-js'>
     * "x_processplatform_assemble_surface" //프로세스 플랫폼 관련 서비스 루트
     * </code></pre>
     * @return {String} 해당 서비스의 host. 예: http://127.0.0.1 (v7.2 이전 버전은 포트 20020이 붙을 수 있음)
     * @o2syntax
     * var actions = this.Actions.getHost( root );
     */

    /**
     * 플랫폼에는 플랫폼이 제공하는 서비스를 호출하기 위한 Actions 객체가 미리 준비되어 있으며, this.Actions.load로 이 메서드들을 가져올 수 있습니다.
     * @method load
     * @methodOf module:Actions
     * @instance
     * @param {String} root 플랫폼 RESTful 서비스 루트. 구체적인 서비스 목록은 http://server/x_program_center/jest/list.html 참고 (v7.2 이전 버전은 포트 20030 필요).
     * 예:
     *<pre><code class='language-js'>
     * "x_processplatform_assemble_surface" //프로세스 플랫폼 관련 서비스 루트
     * </code></pre>
     * @return {Object} 이후 서비스 호출에 사용할 action 객체 반환
     * @o2syntax
     * var actions = this.Actions.load( root );
     * @o2syntax
     * //프로세스 플랫폼 서비스 객체 조회.
     * var processAction = this.Actions.load("x_processplatform_assemble_surface");
     * @o2syntax
     * <caption>
     *     this.Actions.load(root) 메서드로 action 객체를 얻으면 이 서비스의 메서드에 접근할 수 있습니다.<br/>
     *     메서드 접근 규칙은 다음과 같습니다:
     *  </caption>
     *  var promise = this.Actions.load( root )[actionName][methodName]( arguements );
     *
     *  promise : Promise 객체, promise.then으로 반환 데이터를 받을 수 있음
     *      promise.then(
     *        function(json){
     *             //json은 반환된 데이터
     *        }
     *      )
     *
     *  root : 플랫폼 서비스 루트 이름. 예: x_processplatform_assemble_surface
     *
     *  actionName : 서비스 아래 Action 분류 이름. 예: TaskAction
     *
     *  methodName : Action 분류 아래 메서드 이름. 예: get
     *
     *  arguements : 호출할 RESTful 서비스의 관련 매개변수. 순서대로 전달해야 하며, 상황에 따라 일부 매개변수는 생략할 수 있습니다. 매개변수 순서는 다음과 같습니다:
     *
     *      uri 매개변수, data/formData(Post, Put 메서드), file(첨부파일), success, failure, async.
     *
     *      uri 매개변수: uri 매개변수가 여러 개이면 순서대로 전달해야 합니다.
     *
     *      data(formData) 매개변수: 서버로 제출할 데이터. 첨부파일 업로드 시 formData를 전달합니다. POST와 PUT 메서드는 전달해야 하며, GET과 DELETE 메서드는 생략합니다.
     *
     *      file 매개변수: POST 또는 PUT 메서드에서만 유효하며, 첨부파일 업로드 시 전달하고 그 외에는 생략할 수 있습니다.
     *
     *      success 매개변수: 서비스 실행 성공 시 콜백 메서드. 형식: function(json){
     *          json은 서버 서비스가 반환한 데이터
     *      }。
     *
     *      failure 매개변수: 서비스 실행 실패 시 콜백 메서드. 형식: function(xhr){
     *          xhr XmlHttpRequest 객체, 서버 요청 실패 시 값이 있음
     *          return true; //v6.3부터 true를 반환하면 시스템이 오류 메시지를 자동으로 띄우지 않음. 그렇지 않으면 오류 메시지를 띄움.
     *       }
     *      이 매개변수는 생략할 수 있으며, 생략하면 시스템이 오류 메시지를 자동으로 띄웁니다.
     *
     *      async : 메서드 동기/비동기 실행 여부, 기본값 true.
     *
     *      urlEncode : url 인코딩 여부, 기본값 true.
     *
     *      cache : 캐시 사용 여부, 기본값 true.
     *  @o2syntax
     *  <caption>
     *  반환 데이터 처리 방법은 두 가지이며 하나를 선택하면 됩니다:<br/>
     *  1. 이 메서드가 return하는 결과는 Promise 객체이며, promise.then() 메서드로 처리할 수 있습니다.<br/>
     *  {@link https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise|Promise 설명}<br/>
     *  2. success 메서드를 첫 번째 매개변수로 하여 결과 처리
     *  </caption>
     *  promise.then(
     *    function(json){
     *         //json은 반환된 데이터
     *    }
     *  )
     *
     *  //success: arguements 중 첫 번째 function 객체
     *  function(json){
     *    //json은 서버 서비스가 반환한 데이터
     *  }
     *  @example
     * <caption>
     *     <b>예시 1:</b>
     *     x_processplatform_assemble_surface 서비스로 현재 사용자의 결재 대기 목록 조회:<br/>
     *     해당 서비스의 조회 페이지 http://server/x_processplatform_assemble_surface/jest/index.html (v7.2 이전 버전은 포트 20020 필요)에서<br/>
     *     다음 화면을 볼 수 있습니다:<img src="img/module/Actions/Actions.png"/>
     *     TaskAction의 V2ListPaging 서비스가 현재 사용자의 결재 대기를 나열하는 서비스임을 찾을 수 있습니다.<br/>
     *     이 서비스의 정보는 다음과 같습니다:<br/>
     *     1. actionName: TaskAction<br/>
     *     2. methodName: V2ListPaging<br/>
     *     3. url 매개변수 2개: page(페이지), size(페이지당 개수)<br/>
     *     4. 일련의 body 매개변수<br/>
     *     5. 이 서비스 메서드 유형은 POST<br/>
     *     이 정보로 다음과 같은 메서드를 구성할 수 있습니다:
     * </caption>
     * var processAction = this.Actions.load("x_processplatform_assemble_surface"); //action 조회
     * var method = processAction.TaskAction.V2ListPaging; //목록 메서드 조회
     * //실행 방법 1
     * method(
     *  1,  //uri 첫 번째 매개변수, uri 매개변수가 없으면 생략 가능
     *  20, //uri 두 번째 매개변수, uri 매개변수가 없으면 생략 가능, 다른 uri 매개변수가 있으면 쉼표(,)로 구분
     *  {   //body 매개변수, POST와 PUT 요청은 반드시 전달해야 하며 빈 객체 가능
     *      processList : [xxx] //구체적인 매개변수
     *  },
     *  function(json){ //정상 호출 콜백
     *       //json.data로 서비스 반환 데이터 획득
     *  },
     *  function(xhr){ //선택, 오류 호출 콜백
     *      //xhr은 XmlHttpRequest 객체, 서버 요청 실패 시 값이 있음
     *      var responseJSON = JSON.parse( xhr.responseText ) //xhr.responseText {String}는 서버가 반환한 오류 정보
     *      //responseJSON은 아래 설명 참고
     *
     *      var message = responseJSON.message; //message는 오류 안내 문구
     *  },
     *  true //선택, true는 비동기 호출, false는 동기 호출, 기본값 true
     * );
     *
     * //실행 방법 2
     * var promise = method( 1, 20, {processList : [xxx]} )
     * promise.then( function(json){
     *     //json.data로 서비스 반환 데이터 획득
     * })
     * @example
     * <caption>오류 정보 responseJSON의 형식</caption>
     * {
     *       "type": "error", //유형은 오류
     *       "message": "식별자 343434 인 Task 객체가 존재하지 않습니다.", //안내 문구
     *       "date": "2020-12-29 17:02:13", //오류 발생 시간
     *       "prompt": "com.x.base.core.project.exception.ExceptionEntityNotExist" //서버 오류 클래스
     *}
     * @example
     * <caption>
     *     <b>예시 2:</b>
     *      프로세스 인스턴스의 workid를 알고 있을 때, 스크립트에서 데이터를 조회해 수정 후 저장.
     * </caption>
     * //서비스 목록에서 data 조회 서비스인 DataAction의 getWithWork 메서드 확인
     * //서비스 목록에서 data 갱신 서비스인 DataAction의 updateWithWork 메서드 확인
     *
     * var workid = "cce8bc22-225a-4f85-8132-7374d546886e";
     * var data;
     * this.Actions.load("x_processplatform_assemble_surface").DataAction.getWithWork( //플랫폼이 제공하는 메서드
     *      workid, //uri 매개변수
     *      function( json ){ //서비스 호출 성공 콜백 함수, json은 서비스가 반환한 데이터
     *          data = json.data; //변수 data에 값 할당
     *      }.bind(this),
     *      false //동기 실행
     * )
     *
     * data.subject = "새 제목"; //데이터 수정
     * this.Actions.load("x_processplatform_assemble_surface").DataAction.updateWithWork(
     *      workid, //uri 매개변수
     *      data, //저장할 데이터
     *      function(){ //서비스 호출 성공 콜백 함수
     *          o2.xDesktop.notice("success", {"y":"top", "x": "right"}, "저장되었습니다");  //안내, {"y":"top", "x": "right"}는 안내창을 상단 오른쪽에 표시
     *      }.bind(this)
     * );
     */

    /**
     * <b>더 이상 사용되지 않습니다.</b> 플랫폼에는 Action 호출 메서드가 미리 준비되어 있으며, this.Actions.get으로 이 메서드들을 가져올 수 있습니다. RESTful 설정 파일은 {서버 디렉터리}/webServer/o2_core/o2/xAction/services 아래에 있습니다.
     * @method get
     * @deprecated
     * @methodOf module:Actions
     * @instance
     * @param {String} root 플랫폼 RESTful 서비스 루트. 구체적인 서비스 목록은 http://server/x_program_center/jest/list.html 참고 (v7.2 이전 버전은 포트 20030 필요).
     *예:<pre><code class='language-js'>
     *  "x_processplatform_assemble_surface" //프로세스 플랫폼 관련 서비스 루트
     * </code></pre>
     * @return {String} 해당 서비스 루트의 host. 예: http://127.0.0.1 (v7.2 이전 버전은 포트 20020이 붙을 수 있음)
     * @o2syntax
     * var actions = this.Actions.get( root );
     * actions[ methodName ]( arguements );
     *
     * or
     *
     * this.Actions.get( root )[methodName]( arguements );
     * @o2syntax
     * methodName : (string) 메서드 이름.
     * arguements : load 메서드의 arguements 설명 참고
     * @example
     * <caption><b>예시 1:</b>프로세스 인스턴스의 workid를 알고 있을 때, 스크립트에서 데이터를 조회해 수정 후 저장.</caption>
     * //데이터 조회 메서드 이름은 getWorkData, uri는 /jaxrs/data/work/{workid}
     * //데이터 갱신 메서드 이름은 saveData, uri는 /jaxrs/data/work/{workid}
     * var workid = "cce8bc22-225a-4f85-8132-7374d546886e";
     * var action = this.Actions.get("x_processplatform_assemble_surface");
     * var data;
     * action.getWorkData( //플랫폼이 제공하는 메서드
     *      workid, //uri 매개변수
     *      function( json ){ //서비스 호출 성공 콜백 함수, json은 서비스가 반환한 데이터
     *          data = json.data; //변수 data에 값 할당
     *      }.bind(this),
     *      false //동기 실행
     * );

     * data.subject = "새 제목"; //데이터 수정
     * action.saveData(
     *      workid, //uri 매개변수
     *      data, //저장할 데이터
     *      function(){ //서비스 호출 성공 콜백 함수
     *          o2.xDesktop.notice("success", {"y":"top", "x": "right"}, "저장되었습니다");  //안내, {"y":"top", "x": "right"}는 안내창을 상단 오른쪽에 표시
     *      }.bind(this)
     * );
     * @example
     * <caption><b>예시 2:</b>프로세스 앱의 workid를 알고 있을 때, 프로세스 인스턴스를 페이지별로 나열.</caption>
     * //데이터 조회 메서드 이름은 listWorkNext, uri는 {"uri": "/jaxrs/work/list/{id}/next/{count}/application/{applicationId}"},
     * var id = "(0)"; //첫 페이지이면 id는 (0), 아니면 이전 페이지 마지막 프로세스 인스턴스의 id 전달
     * var count = 10;
     * var applicationId = "dde8bc22-225a-4f85-8132-7374d546886e";
     * this.Actions.get("x_processplatform_assemble_surface").listWorkNext(
     *      id, //listWorkNext 서비스에는 uri 매개변수가 3개 있으며 순서대로 나열해야 함
     *      count,
     *      applicationId,
     *       function( json ){
     *          //json.data
     *      }.bind(this)
     * );
     */
    this.Actions = o2.Actions;

    this.query = function (option) {
        // options = {
        //      "name": "statementName",
        //      "data": "json data",
        //      "firstResult": 1,
        //      "maxResults": 100,
        //      "success": function(){},
        //      "error": function(){},
        //      "async": true or false, default is true
        // }
        if (option) {
            var json = (option.data) || {};
            if (option.firstResult) json.firstResult = option.firstResult.toInt();
            if (option.maxResults) json.maxResults = option.maxResults.toInt();
            o2.Actions.get("x_query_assemble_surface").executeStatement(option.name, json, success, error, options.async);
        }
    };

    /**
     * this.Table은 도구 클래스로, 이 클래스로 데이터 센터의 데이터 테이블을 추가·삭제·수정·조회할 수 있습니다.
     * @module Table
     * @o2cn 데이터 테이블 실행
     * @o2category web
     * @o2ordernumber 135
     * @param {String} tableName 데이터 테이블의 id, 이름 또는 별칭.
     * @return {Object} table 객체
     * @o2syntax
     * //페이지, 양식, 프로세스의 각 내장 스크립트에서 this.Table()로 Table 객체를 반환받을 수 있습니다. 예:
     * var table = new this.Table( tableName )
     */

    /**
     * 테이블의 행 객체 나열, 다음 페이지.
     * @method listRowNext
     * @methodOf module:Table
     * @instance
     * @param {String} id  현재 페이지 마지막 데이터의 Id, 첫 페이지이면 "(0)" 사용.
     * @param {String|Number} count 다음 페이지의 행 수
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.listRowNext( id, count, success, failure, async )
     * //또는
     * var promise = table.listRowNext( id, count );
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     *
     * table.listRowNext( "0", 20, function(data){
     *    //data 형식 예
     *    //{
     *    //    "type": "success",
     *    //    "data":[
     *    //       {
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //데이터 id
     *    //        "createTime": "2021-11-01 16:23:41", //데이터 생성 시간
     *    //        "updateTime": "2021-11-01 16:23:41", //데이터 갱신 시간
     *    //         ... //정의된 필드(열)와 값
     *    //        }
     *   //     ],
     *   //       "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */

    /**
     * 테이블의 행 객체 나열, 이전 페이지.
     * @method listRowPrev
     * @methodOf module:Table
     * @instance
     * @param {String} id  현재 페이지 첫 데이터의 Id, 마지막 페이지이면 "(0)" 사용.
     * @param {String|Number} count 이전 페이지의 행 수
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.listRowPrev( id, count, success, failure, async )
     * //또는
     * var promise = table.listRowPrev( id, count );
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     *
     * table.listRowPrev( "0", 20, function(data){
     *    //data 형식 예
     *    //{
     *    //    "type": "success",
     *    //    "data":[
     *    //       {
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //데이터 id
     *    //        "createTime": "2021-11-01 16:23:41", //데이터 생성 시간
     *    //        "updateTime": "2021-11-01 16:23:41", //데이터 갱신 시간
     *    //         ... //정의된 필드(열)와 값
     *    //        }
     *   //     ],
     *   //       "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */

    /**
     * 조건에 따라 테이블 데이터 조회.
     * @method listRowSelect
     * @methodOf module:Table
     * @instance
     * @param {String} [where] 조회 조건, jpql 문법 형식, o.name='zhangsan', 빈 값 허용.
     * @param {String} [orderBy] 정렬 조건, 형식: o.updateTime desc, 빈 값 허용
     * @param {String|Number} [size] 반환 결과 개수, 빈 값 허용.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.listRowSelect( where, orderBy, size, success, failure, async )
     * //또는
     * var promise = table.listRowSelect( where, orderBy, size );
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     *
     * //name 필드가 zhangsan인 데이터 조회, 결과는 updateTime 역순
     * table.listRowSelect( "o.name='zhangsan'", "o.updateTime desc", 20, function(data){
     *    //data 형식 예
     *    //{
     *    //    "type": "success",
     *    //    "data":[
     *    //       {
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //데이터 id
     *    //        "createTime": "2021-11-01 16:23:41", //데이터 생성 시간
     *    //        "updateTime": "2021-11-01 16:23:41", //데이터 갱신 시간
     *    //         ... //정의된 필드(열)와 값
     *    //        }
     *   //     ],
     *   //       "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */

    /**
     * where로 개수 통계.
     * @method rowCountWhere
     * @methodOf module:Table
     * @instance
     * @param {String} where 조회 조건, jpql 문법 형식, o.name='zhangsan'.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.rowCountWhere( where, success, failure, async )
     * //또는
     * var promise = table.rowCountWhere( where );
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     *
     * //name 필드가 zhangsan인 데이터 조회, 결과는 updateTime 역순
     * table.rowCountWhere( "o.name='zhangsan", function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": 5 //조건에 맞는 데이터 총 건수
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */

    /**
     * 데이터 테이블에서 지정 id의 레코드 삭제.
     * @method deleteRow
     * @methodOf module:Table
     * @instance
     * @param {String} id 삭제할 레코드의 id.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.deleteRow( id, success, failure, async )
     * //또는
     * var promise = table.deleteRow( id );
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     *
     * table.deleteRow( "e1f89185-d8b0-4b66-9e34-aed3323d0d79", function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true는 삭제 성공, false는 해당 데이터 없음
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */

    /**
     * 지정 테이블의 모든 행 데이터 삭제.
     * @method deleteAllRow
     * @methodOf module:Table
     * @instance
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.deleteAllRow( success, failure, async )
     * //또는
     * var promise = table.deleteAllRow();
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     *
     * table.deleteAllRow( function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": 1 //삭제된 건수, 0은 데이터 없음
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */

    /**
     * 데이터 테이블에서 지정 id의 레코드 조회.
     * @method getRow
     * @methodOf module:Table
     * @instance
     * @param {id} 조회할 레코드의 id.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.getRow( id, success, failure, async )
     * //또는
     * var promise = table.getRow( id );
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     *
     * table.getRow( "e1f89185-d8b0-4b66-9e34-aed3323d0d79", function(data){
     *    //data 형식 예
     *    //{
     *    //    "type": "success",
     *    //    "data":{
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //데이터 id
     *    //        "createTime": "2021-11-01 16:23:41", //데이터 생성 시간
     *    //        "updateTime": "2021-11-01 16:23:41", //데이터 갱신 시간
     *    //         ... //정의된 필드(열)와 값
     *    //     },
     *   //     "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */

    /**
     * 데이터 테이블에 데이터 일괄 삽입.
     * @method insertRow
     * @methodOf module:Table
     * @instance
     * @param {Object[]} data 삽입할 데이터.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.insertRow( data, success, failure, async )
     * //또는
     * var promise = table.insertRow( data );
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     * var data = [
     *  {
     *    "subject": "제목 1",
     *    ... //기타 필드
     *  },
     *  ...
     * ];
     * table.insertRow( data, function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true는 삽입 성공
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */

    /**
     * 데이터 테이블에 단건 데이터 삽입.
     * @method addRow
     * @methodOf module:Table
     * @instance
     * @param {Object} data 삽입할 데이터.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.addRow( data, success, failure, async )
     * //또는
     * var promise = table.addRow( data );
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     * var data = {
     *    "subject": "제목 1",
     *    ... //기타 필드
     *  };
     * table.addRow( data, function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "id": 2cf3a20d-b166-490b-8d29-05544db3d79b //true는 수정 성공
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */

    /**
     * 데이터 테이블의 단건 데이터 수정.
     * @method updateRow
     * @methodOf module:Table
     * @instance
     * @param {String} id 수정할 데이터 id.
     * @param {Object} data 수정할 데이터.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.updateRow( id, data, success, failure, async )
     * //또는
     * var promise = table.updateRow( id, data );
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     * var data = {
     *    "id" : "2cf3a20d-b166-490b-8d29-05544db3d79b",
     *    "subject": "제목 1",
     *    ... //기타 필드
     *  };
     * table.updateRow( "2cf3a20d-b166-490b-8d29-05544db3d79b", data, function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true는 수정 성공
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */

    /**
     * 데이터 테이블의 단건 데이터 부분 수정.
     * @method partUpdateRow
     * @methodOf module:Table
     * @instance
     * @param {String} id 수정할 데이터 id.
     * @param {Object} data 수정할 일부 데이터, 나머지 데이터는 변경 없음.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @param {Boolean} [async] 비동기 호출 여부, 기본값 true.
     * @return {Promise} Promise 반환
     * @o2syntax
     * table.partUpdateRow( id, data, success, failure, async )
     * //또는
     * var promise = table.partUpdateRow( id, data );
     * promise.then(function(json){
     *     //json은 반환된 데이터
     * })
     * @example
     * var table = new this.Table("table1");
     * var data = {
     *    "id" : "2cf3a20d-b166-490b-8d29-05544db3d79b",
     *    "subject": "제목 1"
     *  };
     * table.partUpdateRow( "2cf3a20d-b166-490b-8d29-05544db3d79b", data, function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true는 수정 성공
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
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
        //  type : "", //기본값 process, process  cms 가능
        //  application : "", //프로세스/CMS의 이름/별칭/id, 기본값은 현재 앱
        //  name : "", // 데이터 사전 이름/별칭/id
        //  anonymous : false //로그인하지 않은 상태에서 CMS 데이터 사전 읽기 허용, 매개변수 이름은 enableAnonymous도 가능
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
            if (type === "cms") {
                action = MWF.Actions.get("x_cms_assemble_control");
            } else if( type === "portal" ){
                action = MWF.Actions.get("x_portal_assemble_surface");
            }else if( type === "service" ){
                key = name+type+enableAnonymous;
                action = MWF.Actions.get("x_program_center");
            } else {
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


