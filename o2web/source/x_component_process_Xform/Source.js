MWF.xDesktop.requireApp('process.Xform', 'Div', null, false);

/** @class Source 데이터 소스 컴포넌트.
 * @o2cn 데이터 소스
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var source = this.form.get("fieldId"); //데이터 소스 컴포넌트 가져오기
 * //방법 2
 * var source = this.target; //컴포넌트 자체 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.Div
 * @o2category FormComponents
 * @o2range {Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Source = MWF.APPSource = new Class(
    /** @lends MWF.xApplication.process.Xform.Source# */
    {
        Extends: MWF.APPDiv,
        options: {
            /**
             * 데이터 로드 후 실행, 아직 하위 컴포넌트는 로드되지 않았으며 this.target.data로 데이터를 가져와 수정할 수 있음.
             * @event MWF.xApplication.process.Xform.Source#postLoadData
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             */
            /**
             * 데이터와 하위 컴포넌트 로드 후 실행.
             * @event MWF.xApplication.process.Xform.Source#loadData
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             */
            moduleEvents: ['queryLoad', 'postLoad', 'load', 'postLoadData', 'loadData'],
        },

        _loadUserInterface: function () {
            if (!this.isReadable && !!this.isHideUnreadable){
                this.node?.addClass('hide');
                return;
            }

            this.currentPage = 1;
            this.data = null;
            if (this.json.path) {
                // this.loadPagination();
                if (this.json.sourceType == 'o2') {
                    if (this.json.path) this._getO2Source();
                } else {
                    if (!this.json.isDelay) this._getOtherSource();
                }
            }
        },
        _getO2Address: function () {
            try {
                this.json.service = JSON.parse(this.json.contextRoot);
            } catch (e) {
                this.json.service = {root: this.json.contextRoot, action: '', method: '', url: ''};
            }
            var addressObj = layout.serviceAddressList[this.json.service.root];

            var defaultPort = layout.config.app_protocol === 'https' ? '443' : '80';
            if (addressObj) {
                var appPort = addressObj.port || window.location.port;
                this.address =
                    layout.config.app_protocol +
                    '//' +
                    (addressObj.host || window.location.hostname) +
                    (!appPort || appPort.toString() === defaultPort ? '' : ':' + appPort) +
                    addressObj.context;
                // this.address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context;
            } else {
                var host = layout.desktop.centerServer.host || window.location.hostname;
                var port = layout.desktop.centerServer.port || window.location.port;
                this.address =
                    layout.config.app_protocol +
                    '//' +
                    host +
                    (!port || port.toString() === defaultPort ? '' : ':' + port) +
                    '/' +
                    this.json.service.root;
            }
        },
        //_getConfigParameters: function(){
        //
        //    return pars;
        //},
        _getO2Uri: function () {
            //var uri = this.json.path || this.json.selectPath;
            var uri = this.json.path;
            var pars = {};
            if (this.json.parameters) {
                Object.each(
                    this.json.parameters,
                    function (v, key) {
                        if (uri.indexOf('{' + key + '}') != -1) {
                            var reg = new RegExp('{' + key + '}', 'g');
                            uri = uri.replace(reg, encodeURIComponent(v && v.code ? this.form.Macro.exec(v.code, this) || '' : v));
                        } else {
                            pars[key] = v;
                        }
                    }.bind(this),
                );
            }

            var data = null;
            if (this.json.requestBody) {
                if (this.json.requestBody.code) {
                    data = this.form.Macro.exec(this.json.requestBody.code, this);
                }
            }

            if (
                this.json.httpMethod == 'GET' ||
                this.json.httpMethod == 'OPTIONS' ||
                this.json.httpMethod == 'HEAD' ||
                this.json.httpMethod == 'DELETE'
            ) {
                var tag = '?';
                if (uri.indexOf('?') != -1) tag = '&';
                Object.each(
                    pars,
                    function (v, k) {
                        var value = v && v.code ? this.form.Macro.exec(v.code, this) || '' : v;
                        uri = uri + tag + k + '=' + value;
                    }.bind(this),
                );
            } else {
                Object.each(
                    pars,
                    function (v, k) {
                        if (!data) data = {};
                        var value = v && v.code ? this.form.Macro.exec(v.code, this) || '' : v;
                        data[k] = value;
                    }.bind(this),
                );
            }
            this.body = data;
            this.uri = this.address + uri;
        },
        _getO2Source: function () {
            this._getO2Address();
            if (!this.json.isDelay) {
                this._getO2Uri();
                this._invoke(
                    function () {
                        this._loadSub(this.node);
                        // this._setPaginationCount();
                        this.fireEvent('loadData');
                    }.bind(this),
                );
            }
        },

        _getOtherSource: function () {
            if (this.json.otherHost) {
                this.address = this.json.otherHost;
                this._getO2Uri();

                var o = {
                    method: this.json.httpMethod,
                    headers: {
                        'Content-Type': 'application/json',
                    },
                };
                if (this.json.httpMethod == 'POST' || this.json.httpMethod == 'PUT') {
                    o.body = JSON.stringify(this.body);
                }
                fetch(this.uri, o)
                    .then((response) => response.json())
                    .then((json) => {
                        this.data = json;
                        this.fireEvent('postLoadData');
                        this._loadSub(this.node);
                        // this._setPaginationCount();
                        this.fireEvent('loadData');
                    });
            }
        },

        // _setPaginationCount: function (){
        //     if (this.json.countScript && this.json.countScript.code && this.pagination) {
        //         if (this.json.countScript.code) {
        //             var count = this.form.Macro.exec(this.json.countScript.code, this);
        //             this.pagination.setAttribute('total', count);
        //         }
        //     }
        // },
        // loadPagination: function (){
        //     var node = this.node.getElement('oo-pagination');
        //     if(node){
        //         node.destroy();
        //     }
        //     if( this.json.usePagination ){
        //         this.pagination = new Element('oo-pagination', {
        //             total: '300',
        //             pages: this.json.pages,
        //             size: this.json.size,
        //             'page-size': this.json.pageSize,
        //             jumper: this.json.jumper,
        //             first: this.json.first,
        //             last: this.json.last,
        //             'jumper-text': this.json.jumperText,
        //             // styles: this.css.moduleNodeMove,
        //             events: {
        //                 selectstart: function () {
        //                     return false;
        //                 },
        //             },
        //         }).inject(this.node);
        //         this.pagination.addEventListener('page', (e)=>{
        //             this.currentPage = e.detail;
        //             this.reload();
        //         });
        //         if (this.json.pagnationProperties) {
        //             this.pagination.set(this.json.pagnationProperties);
        //         }
        //         if (this.json.pagnationStyles) {
        //             this.pagination.setStyles(this.json.pagnationStyles);
        //         }
        //     }
        // },

        active: function () {
            if (this.json.sourceType === 'o2') {
                this._getO2Uri();
                this._invoke(
                    function () {
                        this._loadSub(this.node);
                        this.fireEvent('loadData');
                    }.bind(this),
                );
            } else {
                this._getOtherSource();
            }
        },
        _invoke: function (callback) {
            var cb = new MWF.xDesktop.Actions.RestActions.Callback(function (json) {
                /**
                 * @summary 현재 데이터 소스의 데이터, 데이터 소스 로드 완료 후에 값이 있음.
                 * @member {Array|Object|String|Number|Boolean|Null}
                 * @example
                 * var field = this.form.get("fieldId").data; //데이터 소스 데이터 가져오기
                 */
                this.data = json;
                this.fireEvent('postLoadData');
                if (callback) callback();
            }.bind(this), null);

            MWF.restful(
                this.json.httpMethod,
                this.uri,
                JSON.encode(this.body),
                cb,
                true,
                true,
            );
        },
        setBody: function (data) {
            this.body = data;
        },
        /**
         * @summary 모든 url 파라미터를 교체하되 컴포넌트는 새로 고치지 않음
         * @param {Object} url 파라미터
         * @example
         * //예: |기존 컴포넌트 url 파라미터:
         * { "page" : 1, "count" : 10  }
         *
         * this.form.get("fieldId").setParameters({"id":"662ede34-4e21-428a-9c3b-f1bf14d15650"});
         *
         * //실행 후 변경
         * {"id":"662ede34-4e21-428a-9c3b-f1bf14d15650"}
         */
        setParameters: function (json) {
            this.json.parameters = json;
            this._getO2Address();
            this._getO2Uri();
        },
        /**
         * @summary url 파라미터 추가, 컴포넌트는 새로 고치지 않음. 해당 파라미터 key가 이미 있으면 덮어씀
         * @param {Object} url 파라미터
         * @example
         * * //예: |기존 컴포넌트 url 파라미터:
         * { "page" : 1, "count" : 10  }
         *
         * this.form.get("fieldId").addParameters({
         *  "page" : 2,
         *  "id":"662ede34-4e21-428a-9c3b-f1bf14d15650"
         *  });
         *
         * //실행 후 변경
         * {
         *  "page" : 2,
         *  "count" : 10
         *  "id":"662ede34-4e21-428a-9c3b-f1bf14d15650"
         *  }
         */
        addParameters: function (json) {
            if (!this.json.parameters) this.json.parameters = {};
            Object.each(
                json,
                function (v, k) {
                    this.json.parameters[k] = v;
                }.bind(this),
            );
            this._getO2Address();
            this._getO2Uri();
        },
        /**
         * @summary 컴포넌트 다시 로드. loadData 이벤트 발생
         * @param {Boolean} notInit - false는 하위 데이터 소스와 데이터 텍스트를 다시 초기화하지 않음, true는 다시 초기화, 기본값 false
         * @param {Function} callback 로드 완료 후 콜백
         * @example
         * this.form.get("fieldId").reload(); //컴포넌트 다시 로드
         */
        reload: function (notInit, callback) {
            if (this.json.sourceType == 'o2') {
                this._getO2Uri();
                this._invoke(
                    function () {
                        this._loadSub(this.node, notInit);
                        this.fireEvent('loadData');
                        if (callback) callback();
                    }.bind(this),
                );
            } else {
                this._getOtherSource();
            }
        },
        _loadSub: function (dom, notInit) {
            var subDom = dom.getFirst();
            var module = null;
            while (subDom) {
                module = null;

                module = subDom.retrieve('module', null);
                if (module) {
                    if (module._loadJsonData) module._loadJsonData(notInit);
                } else {
                    this._loadSub(subDom);
                }

                //var type = subDom.get("MWFtype");
                //if (type){
                //    if (type=="sourceText"){
                //        module = subDom.retrieve("module");
                //        module._loadData();
                //    }else if (type=="subSource"){
                //        module = subDom.retrieve("module");
                //        module._loadData();
                //    }else{
                //        this._loadSub(subDom);
                //    }
                //}else{
                //   this._loadSub(subDom);
                //}
                subDom = subDom.getNext();
            }
        },
    },
);
