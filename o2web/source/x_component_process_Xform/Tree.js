MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/**트리 컴포넌트 데이터 구조
 * @typedef {Object} TreeData
 * @example
 * [
 * {
 *    "expand": true, //기본 펼침 여부
 *    "title": "", //잎 노드에 마우스를 올렸을 때 표시할 텍스트
 *    "text": "루트 노드", //잎 노드 텍스트
 *    "action": "", //실행할 스크립트
 *    "default": true, //기본 선택 여부
 *    "icon": "folder.png", //아이콘
 *    "sub": [ //이 노드의 하위 노드
 *      {
 *        "expand": true,
 *        "title": "",
 *        "text": "[none]",
 *        "action": "",
 *        "default": false,
 *        "icon": "folder.png",
 *        "sub": []
 *      },
 *      ...
 *    ]
 *  }
 * ]
 */

/** @class Tree 트리 컴포넌트.
 * @o2cn 트리 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var datagrid = this.form.get("name"); //컴포넌트 가져오기
 * //방법 2
 * var datagrid = this.target; //컴포넌트 이벤트 스크립트에서 가져오기
 * @see {@link TreeData|트리 컴포넌트 데이터 구조}
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Tree = MWF.APPTree =  new Class(
	/** @lends MWF.xApplication.process.Xform.Tree# */
{
	Extends: MWF.APP$Module,
	options: {
		/**
		 * 트리 비동기 로드 전 실행. this.target은 현재 컴포넌트를 가리킴.
		 * @event MWF.xApplication.process.Xform.Tree#beforeLoadTree
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
		 */
		/**
		 * 트리 비동기 로드 후 실행. this.target은 현재 컴포넌트를 가리킴.
		 * @event MWF.xApplication.process.Xform.Tree#afterLoadTree
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
		 */
		/**
		 * 트리 잎 로드 전 실행. this.target은 로드되는 잎을 가리킴.
		 * @event MWF.xApplication.process.Xform.Tree#beforeLoadTreeNode
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
		 */
		/**
		 * 트리 잎 로드 후 실행. this.target은 로드되는 잎을 가리킴.
		 * @event MWF.xApplication.process.Xform.Tree#afterLoadTreeNode
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
		 */
		/**
		 * 트리 잎 로드 후 실행. this.target은 로드되는 잎을 가리킴.
		 * @event MWF.xApplication.process.Xform.Tree#expand
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
		 */
		/**
		 * 노드를 접을 때 실행. this.target은 접힌 노드를 가리킴.
		 * @event MWF.xApplication.process.Xform.Tree#collapse
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
		 */
		/**
		 * 노드 선택 전 실행. 이때 기존 선택 노드는 아직 해제되지 않음. this.target은 선택된 노드를 가리킴.
		 * @event MWF.xApplication.process.Xform.Tree#beforeSelect
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
		 */
		/**
		 * 노드 선택 후 실행. this.target은 선택된 노드를 가리킴.
		 * @event MWF.xApplication.process.Xform.Tree#afterSelect
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
		 */
		"moduleEvents": ["load", "queryLoad", "postLoad", "beforeLoadTree", "afterLoadTree", "beforeLoadTreeNode", "afterLoadTreeNode", "expand", "collapse", "beforeSelect", "afterSelect"]
	},

	_loadUserInterface: function(){
		this.node.empty();

		if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

		MWF.require("MWF.widget.Tree", function(){
			var options = {"style":"form"};
			if( this.json.events && typeOf(this.json.events) === "object" ){
				[
					{ "beforeLoadTree" :  "onQueryLoad" },
					{ "afterLoadTree" :  "onPostLoad" },
					{ "beforeLoadTreeNode" :  "onBeforeLoadTreeNode" },
					{ "afterLoadTreeNode" :  "onAfterLoadTreeNode" },
					{ "expand" :  "onPostExpand" },
					{ "collapse" :  "onPostCollapse" },
					{ "beforeSelect" : "onBeforeSelect" },
					{ "afterSelect" : "onAfterSelect" }
				].each( function (obj) {
					var moduleEvent = Object.keys(obj)[0];
					var treeEvent = obj[moduleEvent];
					if( this.json.events[moduleEvent] && this.json.events[moduleEvent].code ){
						options[treeEvent] = function( target ){
							return this.form.Macro.fire(this.json.events[moduleEvent].code, target || this);
						}.bind(this)
					}
				}.bind(this));
			}

			/**
			 * @summary 트리 컴포넌트, 플랫폼이 이 컴포넌트로 트리 기능을 구현, 비동기로 로드됨
			 * @member {o2.widget.Tree}
			 * @example
			 *  //스크립트에서 이 컴포넌트를 가져올 수 있음
			 * var tree = this.form.get("fieldId").tree; //컴포넌트 객체 가져오기
			 * var children = tree.children[]; //첫 번째 단계 트리 잎 가져오기
			 * tree.reLoad( json ); //트리 전체에 데이터를 다시 설정하고 다시 로드
			 */
			this.tree = new MWF.widget.Tree(this.node, options);
			this.tree.form = this.form;

			this._setTreeWidgetStyles();


			var treeData = this.json.data;
			if (this.json.dataType == "script") treeData = this.form.Macro.exec(((this.json.dataScript) ? this.json.dataScript.code : ""), this);

			this.tree.load(treeData);
		}.bind(this));
	},
	_setTreeWidgetStyles: function(){
		this.tree.css.areaNode = this.json.areaNodeStyle;
		this.tree.css.treeItemNode = this.json.treeItemNodeStyle;
		this.tree.css.textDivNode = this.json.textDivNodeStyle;
		this.tree.css.textDivNodeSelected = this.json.textDivNodeSelectedStyle;
	}
}); 
