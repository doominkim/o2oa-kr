MWF.xDesktop.requireApp('process.Xform', '$Module', null, false);
//COMMON.AjaxModule.load("JSONTemplate", null, false);
/** @class SubSource 하위 데이터 소스.
 * @o2cn 하위 데이터 소스
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var subSource = this.form.get("fieldId"); //컴포넌트 가져오기
 * //방법 2
 * var subSource = this.target; //컴포넌트 자체 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.SubSource = MWF.APPSubSource = new Class(
    /** @lends MWF.xApplication.process.Xform.SubSource# */
    {
        Extends: MWF.APP$Module,
        options: {
            /**
             * 데이터 로드 후 실행, 아직 하위 컴포넌트는 로드되지 않았으며 this.target.data로 데이터를 가져와 수정할 수 있음.
             * @event MWF.xApplication.process.Xform.SubSource#postLoadData
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             */
            /**
             * 데이터와 하위 컴포넌트 로드 후 실행.
             * @event MWF.xApplication.process.Xform.SubSource#loadData
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             */
            moduleEvents: ['queryLoad', 'postLoad', 'load', 'postLoadData', 'loadData'],
        },
        load: function () {
            /**
             * @ignore
             * @member parentLine
             * @memberOf MWF.xApplication.process.Xform.SubSource#
             */

            this._loadModuleEvents();
            this._queryLoaded();
            this._loadUserInterface();
            // this._loadStyles();
            //this._loadEvents();
            // this._loadDomEvents();

            this._afterLoaded();
        },
        _loadUserInterface: function () {
            if (!this.isReadable && !!this.isHideUnreadable){
                this.node?.addClass('hide');
                return;
            }

            this.loopNodes = [];
            this.subSourceItems = [];
            var node = new Element('div.subsource').inject(this.node, 'before');
            // var node = this.node.clone(false).inject(this.node, "before");
            // this.node.inject(node);
            this.loopNode = this.node.dispose();
            this.node = node;
            var id = node.get('id');
            node.set('id', '');
            this.node.set({
                id: id,
                mwftype: node.get('mwftype'),
            });
            this.node.store('module', this);
            this._loadJsonData();
        },
        _getSource: function () {
            var parent = this.node.getParent();
            while (
                parent &&
                parent.get('MWFtype') != 'source' &&
                parent.get('MWFtype') != 'subSource' &&
                parent.get('MWFtype') != 'subSourceItem'
            )
                parent = parent.getParent();
            return parent ? parent.retrieve('module') : null;
        },
        _getSourceData: function (sourceData) {
            var data = sourceData;
            if (this.json.jsonPath != '.') {
                var paths = this.json.jsonPath.split('.');
                paths.each(
                    function (p) {
                        data = data[p];
                    }.bind(this),
                );
            }
            /**
             * @summary 현재 하위 데이터 소스의 데이터, 상위 데이터 소스 로드 완료 후에 값이 있음.
             * @member {Array|Object|String|Number|Boolean|Null}
             * @example
             * var field = this.form.get("fieldId").data; //하위 데이터 소스 데이터 가져오기
             */
            this.data = data;
        },
        _loopSub: function (dom, i) {
            var _self = this;
            var moduleNodes = this.form._getModuleNodes(dom);
            moduleNodes.each(
                function (node) {
                    var json = this.form._getDomjson(node);
                    var subJson = Object.clone(json);
                    subJson.id = subJson.id + '_' + i;
                    node.set('id', subJson.id);

                    var module = this.form._loadModule(
                        subJson,
                        node,
                        function () {
                            if (_self.widget) this.widget = _self.widget;
                        },
                        true,
                    );
                    //this.modules.push(module);
                }.bind(this),
            );
        },
        _loopData: function () {
            var _self = this;
            this.data.each(
                function (d, i) {
                    var node = this.loopNode.clone(true, true);
                    node.inject(this.node);
                    var json = Object.clone(this.json);
                    json.id = json.id + '_' + i;
                    json.type = 'SubSourceItem';
                    node.set({
                        id: json.id,
                        mwftype: 'subSourceItem',
                    });

                    var module = this.form._loadModule(
                        json,
                        node,
                        function () {
                            if (_self.widget) this.widget = _self.widget;
                            this.data = d;
                            this.position = i;
                        },
                        true,
                    );
                    this.subSourceItems.push(module);
                    this.loopNodes.push(node);

                    this._loopSub(node, i);
                }.bind(this),
            );
        },
        _initSubSource: function () {
            if (this.loopNode) {
                var moduleNodes = this.form._getModuleNodes(this.node);
                moduleNodes.each(
                    function (node) {
                        var module = node.retrieve('module');
                        if (module) {
                            if (module.json.type == 'SubSource') {
                                module._initSubSource();
                            } else {
                                MWF.release(module);
                            }
                        }
                    }.bind(this),
                );
                this.node.empty();
            }
            this.loopNodes = [];
            this.subSourceItems = [];
        },
        _loadJsonData: function (notInit) {
            if (!notInit) this._initSubSource();
            this.source = this._getSource();
            if (this.source) {
                if (this.source.data) {
                    this._getSourceData(this.source.data);
                    this.fireEvent('postLoadData');
                    if (typeOf(this.data) !== 'array') this.data = [this.data];
                    if (typeOf(this.data) == 'array') {
                        this._loopData();
                        this.fireEvent('loadData');
                    } else {
                        var _self = this;
                        this.form._loadModules(
                            this.node,
                            function () {
                                if (_self.widget) this.widget = _self.widget;
                            },
                            true,
                        );
                    }

                    //this.tmpDiv = new Element("div");
                    //var html = "{loop:"+this.json.jsonPath+"}"+this.node.outerHTML+"{/loop:"+this.json.jsonPath+"}";
                    ////this.template = new Template();
                    ////var loopHtml = this.template.substitute("{"+this.json.jsonPath+"}", this.source.data);
                    //this.node.set("text", this.text);
                }
            }
        },
    },
);

MWF.xApplication.process.Xform.SubSourceItem = MWF.APPSubSourceItem = new Class({
    Extends: MWF.APP$Module,
    _loadUserInterface: function () {
        this.loopNodes = [];
        this.subSourceItems = [];
    },
});
