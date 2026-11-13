o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Eltree Element UI 기반 트리 컨트롤.
 * @o2cn 트리 컨트롤
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|Element UI Tree 트리 컨트롤}
 */
MWF.xApplication.process.Xform.Eltree = MWF.APPEltree =  new Class(
    /** @lends MWF.xApplication.process.Xform.Eltree# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 노드 클릭 시 콜백. this.event[0]은 data 속성에 전달된 배열에서 해당 노드에 대응하는 객체;
         * this.event[1]은 노드에 대응하는 Node; this.event[2]는 노드 컴포넌트 자체
         * @event MWF.xApplication.process.Xform.Eltree#node-click
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 노드를 마우스 오른쪽 버튼으로 클릭할 때 발생. this.event[0]은 Event;
         * this.event[1]은 data 속성에 전달된 배열에서 해당 노드에 대응하는 객체; this.event[2]는 노드에 대응하는 Node;
         * this.event[3]은 노드 컴포넌트 자체
         * @event MWF.xApplication.process.Xform.Eltree#node-contextmenu
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 노드 선택 상태가 변경될 때 콜백. this.event[0]은 data 속성에 전달된 배열에서 해당 노드에 대응하는 객체;
         * this.event[1]은 노드 자체의 선택 여부; this.event[2]는 노드의 하위 트리에 선택된 노드가 있는지 여부
         * @event MWF.xApplication.process.Xform.Eltree#check-change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 체크박스를 클릭할 때 발생. this.event[0]은 data 속성에 전달된 배열에서 해당 노드에 대응하는 객체;
         * this.event[1]은 트리의 현재 선택 상태 객체로, checkedNodes, checkedKeys, halfCheckedNodes, halfCheckedKeys 네 속성 포함
         * @event MWF.xApplication.process.Xform.Eltree#check
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 현재 선택 노드가 변경될 때 발생하는 이벤트. this.event[0]은 현재 노드의 데이터;
         * this.event[1]은 현재 노드의 Node 객체
         * * @event MWF.xApplication.process.Xform.Eltree#current-change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 노드가 펼쳐질 때 발생하는 이벤트. this.event[0]은 data 속성에 전달된 배열에서 해당 노드에 대응하는 객체;
         * this.event[1]은 노드에 대응하는 Node; this.event[2]는 노드 컴포넌트 자체
         * * @event MWF.xApplication.process.Xform.Eltree#node-expand
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 노드가 닫힐 때 발생하는 이벤트. this.event[0]은 data 속성에 전달된 배열에서 해당 노드에 대응하는 객체;
         * this.event[1]은 노드에 대응하는 Node; this.event[2]는 노드 컴포넌트 자체
         * * @event MWF.xApplication.process.Xform.Eltree#node-collapse
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 노드 드래그 시작 시 발생하는 이벤트. this.event[0]은 드래그되는 노드에 대응하는 Node;
         * this.event[1]은 Event
         * * @event MWF.xApplication.process.Xform.Eltree#node-drag-start
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 드래그가 다른 노드에 진입할 때 발생하는 이벤트. this.event[0]은 드래그되는 노드에 대응하는 Node;
         * this.event[1]은 진입한 노드에 대응하는 Node; this.event[2]는 event
         * * @event MWF.xApplication.process.Xform.Eltree#node-drag-enter
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 드래그가 어떤 노드를 벗어날 때 발생하는 이벤트. this.event[0]은 드래그되는 노드에 대응하는 Node;
         * this.event[1]은 진입한 노드에 대응하는 Node; this.event[2]는 event
         * * @event MWF.xApplication.process.Xform.Eltree#node-drag-leave
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 노드를 드래그하는 중 발생하는 이벤트(브라우저의 mouseover 이벤트와 유사)	. this.event[0]은 드래그되는 노드에 대응하는 Node;
         * this.event[1]은 진입한 노드에 대응하는 Node; this.event[2]는 event
         * * @event MWF.xApplication.process.Xform.Eltree#node-drag-over
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 드래그 종료 시(성공하지 않았을 수 있음) 발생하는 이벤트. this.event[0]은 드래그되는 노드에 대응하는 Node;
         * this.event[1]은 드래그 종료 시 마지막으로 진입한 노드(비어 있을 수 있음);
         * this.event[2]는 드래그되는 노드의 놓인 위치(before, after, inner)
         * this.event[3]은 event
         * @event MWF.xApplication.process.Xform.Eltree#node-drag-end
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        /**
         * 드래그가 성공적으로 완료될 때 발생하는 이벤트. this.event[0]은 드래그되는 노드에 대응하는 Node;
         * this.event[1]은 드래그 종료 시 마지막으로 진입한 노드;
         * this.event[2]는 드래그되는 노드의 놓인 위치(before, after, inner)
         * this.event[3]은 event
         * @event MWF.xApplication.process.Xform.Eltree#node-drop
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|트리 컴포넌트의 Events 장}
         */
        "elEvents": ["node-click", "node-contextmenu", "check-change", "check", "current-change","node-expand",
            "node-collapse","node-drag-start","node-drag-enter","node-drag-leave","node-drag-over","node-drag-end","node-drop"]
    },
    __setReadonly: function(data){
        if (this.isReadonly()) {
            this.node.set("text", data);
            if( this.json.elProperties ){
                this.node.set(this.json.elProperties );
            }
            if (this.json.elStyles){
                this.node.setStyles( this._parseStyles(this.json.elStyles) );
            }

        }
    },
    _loadNode: function(){
        // if (this.isReadonly()) this.json.disabled = true;
        if (!this.isReadable){
            this.node?.addClass('hide');
        }else{
            this._loadNodeEdit();
        }
    },
    _appendVueData: function(){
        if (!this.json.emptyText) this.json.emptyText = "";
        // if (!this.json.renderAfterExpand) this.json.renderAfterExpand = true;
        if (!this.json.highlightCurrent) this.json.highlightCurrent = false;
        if (!this.json.defaultExpandAll) this.json.defaultExpandAll = false;
        // if (!this.json.expandOnClickNode) this.json.expandOnClickNode = true;
        if (!this.json.accordion) this.json.accordion = false;
        if (!this.json.indent) this.json.indent = 16;

        if (this.json.currentNodeKey && this.json.currentNodeKey.code){
            this.json.currentNodeKey = this.form.Macro.fire(this.json.currentNodeKey.code, this);
        }
        if( !this.json.defaultExpandAll && this.json.defaultExpandedKeys && this.json.defaultExpandedKeys.code ){
            this.json.defaultExpandedKeys = this.form.Macro.fire(this.json.defaultExpandedKeys.code, this);
        }
        if( this.json.showCheckbox && this.json.defaultCheckedKeys && this.json.defaultCheckedKeys.code ){
            this.json.defaultCheckedKeys = this.form.Macro.fire(this.json.defaultCheckedKeys.code, this);
        }
        // if( this.json.lazy && this.json.loadFun && this.json.loadFun.code  ){
        //     this.json.lazyLoadFun = function(node, resolve){
        //         return this.form.Macro.fire(this.json.loadFun.code, this, {
        //             node: node,
        //             resolve: resolve
        //         });
        //     }.bind(this)
        // }
        if( this.json.draggable ){
            if( this.json.allowDrag && this.json.allowDrag.code ){
                this.json.allowDragFun = function(node){
                    return this.form.Macro.fire(this.json.allowDrag.code, this, node);
                }.bind(this)
            }
            if( this.json.allowDrop && this.json.allowDrop.code ){
                this.json.allowDropFun = function(node){
                    return this.form.Macro.fire(this.json.allowDrop.code, this, node);
                }.bind(this)
            }
        }

        if (this.json.dataType === "script"){
            this.json.data = this.form.Macro.exec(((this.json.dataScript) ? this.json.dataScript.code : ""), this);
        }else{
            this.json.data = this.json.dataJson;
        }
        this.parseData();
    },
    _createElementHtml: function() {
        var html = "<el-tree";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :data=\"data\"";
        html += " :empty-text=\"emptyText\"";
        html += " :props=\"defaultProps\"";
        html += " :render-after-expand=\"renderAfterExpand\"";
        html += " :highlight-current=\"highlightCurrent\"";
        html += " :default-expand-all=\"defaultExpandAll\"";
        html += " :expand-on-click-node=\"expandOnClickNode\"";
        html += " :accordion=\"accordion\"";
        html += " :indent=\"indent\"";


        if( this.json.nodeKey ){
            html += " :node-key=\"nodeKey\"";
        }
        if( this.json.currentNodeKey && this.json.currentNodeKey.code ){
            html += " :current-node-key=\"currentNodeKey\"";
        }
        if( !this.json.defaultExpandAll && this.json.defaultExpandedKeys && this.json.defaultExpandedKeys.code ){
            html += " :default-expanded-keys=\"defaultExpandedKeys\"";
        }
        if( this.json.showCheckbox ){
            html += " :show-checkbox=\"showCheckbox\"";
            html += " :check-on-click-node=\"checkOnClickNode\"";
            html += " :check-strictly=\"checkStrictly\"";
            if( this.json.defaultCheckedKeys && this.json.defaultCheckedKeys.code ){
                html += " :default-checked-keys=\"defaultCheckedKeys\"";
            }
        }
        // if( this.json.lazy ){
        //     html += " :lazy=\"lazy\"";
        //     if( this.json.loadFun && this.json.loadFun.code ){
        //         html += " :load=\"lazyLoadFun\"";
        //     }
        // }
        if( this.json.draggable ){
            html += " :draggable=\"draggable\"";
            if( this.json.allowDrag && this.json.allowDrag.code ){
                html += " :allow-drag=\"allowDragFun\"";
            }
            if( this.json.allowDrop && this.json.allowDrop.code ){
                html += " :allow-drop=\"allowDropFun\"";
            }
        }


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

        html += "</el-tree>";
        return html;
    },
     parseData: function ( data ) {
        if( !data )data = this.json.data;
        var config = {};
         //label과 children을 defaultOptions 배열로 변환
         if( this.json.defaultProps ){
             var p = this.json.defaultProps;
             if( p.children !== "children" && o2.typeOf(p.children)==="string") config.children = p.children;
             if( p.label !== "label" && o2.typeOf(p.label)==="string" )config.label = p.label;
             if( p.disabled !== "disabled" && o2.typeOf(p.disabled)==="string")config.disabled = p.disabled;
             if( p.label !== "isLeaf" && o2.typeOf(p.isLeaf)==="string")config.isLeaf = p.isLeaf;
         }
        //id를 node-key로 변환,
        if( this.json.nodeKey && this.json.nodeKey !== "id" )config.id = this.json.nodeKey;
        if( Object.keys(config).length > 0 ){
            this._parseData(data, config);
        }
     },
     _parseData: function ( data, config ) {
         if( o2.typeOf(data) === "array" ){
             data.each(function(d){ this._parse(d, config) }.bind(this))
         }else{
             this._parse(data, config);
         }
     },
     _parse: function (data, config) {
         Object.each(config, function (value, key) {
             if( data[key] )data[value] = data[key];
         });
         var children = data[ config.children || "children" ];
         if(children && o2.typeOf(children)==="array" )this._parseData( children, config );
     }

});
