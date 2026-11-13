
MWF.xApplication.MinderEditor.History = new Class({
    initialize : function( minder ){
        this.minder = minder;

        this.MAX_HISTORY = 100;

        this.lastSnap;
        this.patchLock;
        this.undoDiffs = [];
        this.redoDiffs = [];

        this.reset();
        minder.on('contentchange', this.changed.bind(this));
        minder.on('import', this.reset.bind(this));
        minder.on('patch', this.updateSelection.bind(this));
    },
    reset: function () {
        this.undoDiffs = [];
        this.redoDiffs = [];
        this.lastSnap = this.minder.exportJson();
    },

    makeUndoDiff: function() {
        var headSnap = this.minder.exportJson();
        var diff = MWF.xApplication.MinderEditor.JsonDiff(headSnap, this.lastSnap);
        if (diff.length) {
            this.undoDiffs.push(diff);
            while (this.undoDiffs.length > this.MAX_HISTORY) {
                this.undoDiffs.shift();
            }
            this.lastSnap = headSnap;
            return true;
        }
    },

    makeRedoDiff:function() {
        var revertSnap = this.minder.exportJson();
        this.redoDiffs.push(MWF.xApplication.MinderEditor.JsonDiff(revertSnap, this.lastSnap));
        this.lastSnap = revertSnap;
    },

    undo: function() {
        this.patchLock = true;
        var undoDiff = this.undoDiffs.pop();
        if (undoDiff) {
            this.minder.applyPatches(undoDiff);
            this.makeRedoDiff();
        }
        this.patchLock = false;
    },

    redo: function() {
        this.patchLock = true;
        var redoDiff = this.redoDiffs.pop();
        if (redoDiff) {
            this.minder.applyPatches(redoDiff);
            this.makeUndoDiff();
        }
        this.patchLock = false;
    },

    changed: function() {
        if (this.patchLock) return;
        if (this.makeUndoDiff()) this.redoDiffs = [];
    },

    hasUndo: function() {
        return !!this.undoDiffs.length;
    },

    hasRedo: function() {
        return !!this.redoDiffs.length;
    },

    updateSelection: function(e) {
        if (!this.patchLock) return;
        var patch = e.patch;
        switch (patch.express) {
            case 'node.add':
                this.minder.select(patch.node.getChild(patch.index), true);
                break;
            case 'node.remove':
            case 'data.replace':
            case 'data.remove':
            case 'data.add':
                this.minder.select(patch.node, true);
                break;
        }
    }
});

/**
 * @Desc: 시스템 ctrl+c ctrl+v 등으로 노드를 가져오기/내보내기 위한 MIMETYPE 처리를 새로 추가하며, 시스템이 clipboardEvent를 지원하지 않거나 FF이면 이 class를 초기화하지 않음
 * @Editor: Naixor
 * @Date: 2015.9.21
 */
MWF.xApplication.MinderEditor.ClipboardMimeType = new Class({
    initialize : function(){
        this.SPLITOR = '\uFEFF';
        this.MIMETYPE = {
            'application/km': '\uFFFF'
        };
        this.SIGN = {
            '\uFEFF': 'SPLITOR',
            '\uFFFF': 'application/km'
        };
    },
    /**
     * 일반 텍스트를 해당 데이터 형식에 맞는 텍스트로 감싸는 용도
     * @method process 			private
     * @param  {MIMETYPE} mimetype 데이터 형식
     * @param  {String} text     원본 텍스트
     * @return {String}          해당 데이터 형식에 맞는 텍스트
     * @example
     * 			var str = "123";
     * 			str = process('application/km', str); // 반환된 내용은 MimeType 판별을 거쳐 데이터 형식이 application/km으로 읽힘
     * 			process('text/plain', str); // 일반 텍스트가 아닌 정보를 받으면 새 데이터 형식으로 변환
     */
    process: function(mimetype, text) {
        text = text || "";
        if (!this.isPureText(text)) {
            var _mimetype = this.whichMimeType(text);
            if (!_mimetype) {
                throw new Error('unknow mimetype!');
            }
            text = this.getPureText(text);
        }
        if (mimetype === false) {
            return text;
        }
        return mimetype + this.SPLITOR + text;
    },

    /**
     * 데이터 형식 식별자 등록
     * @method registMimeTypeProtocol  	public
     * @param  {String} type 데이터 형식
     * @param  {String} sign 식별자
     */
    registMimeTypeProtocol : function(type, sign) {
        if (sign && this.SIGN[sign]) {
            throw new Error('sing has registed!');
        }
        if (type && !!this.MIMETYPE[type]) {
            throw new Error('mimetype has registed!');
        }
        this.SIGN[sign] = type;
        this.MIMETYPE[type] = sign;
    },

    /**
     * 등록된 데이터 형식의 프로토콜 조회
     * @method getMimeTypeProtocol  	public
     * @param  {String} type 데이터 형식
     * @param  {String} text|undefiend  텍스트 내용 또는 생략
     * @return {String|Function}
     * @example
     * 			text를 전달하지 않으면 해당 데이터 형식의 처리(process) 메서드를 바로 반환
     * 			텍스트를 전달하면 해당 process 메서드를 바로 호출해 처리하고, 처리된 내용을 반환
     * 			var m = new MimeType();
     * 			var kmprocess = m.getMimeTypeProtocol('application/km');
     * 			kmprocess("123") === m.getMimeTypeProtocol('application/km', "123");
     *
     */
    getMimeTypeProtocol : function(type, text) {
        var mimetype = this.MIMETYPE[type] || false;

        if (text === undefined) {
            return this.process(mimetype);
        };

        return this.process(mimetype, text);
    },
    getSpitor : function() {
        return this.SPLITOR;
    },

    getMimeType : function(sign) {
        if (sign !== undefined) {
            return this.SIGN[sign] || null;
        }
        return this.MIMETYPE;
    },

    isPureText : function(text) {
        if( !text )return true;
        return !(~text.indexOf(this.getSpitor()));
    },

    getPureText : function(text) {
        if (this.isPureText(text)) {
            return text;
        };
        return text.split(this.getSpitor())[1];
    },

    whichMimeType : function(text) {
        if (this.isPureText(text)) {
            return null;
        };
        return this.getMimeType(text.split(this.getSpitor())[0]);
    }
});

MWF.xApplication.MinderEditor.Clipboard = new Class({
    initialize : function( editor ){
        this.editor = editor;
        this.minder = editor.minder;
        this.Data = window.kityminder.data;

        if (!this.minder.supportClipboardEvent || kity.Browser.gecko) {
            return;
        }

        this.fsm = this.editor.fsm;
        this.receiver = this.editor.receiver;
        this.MimeType = this.editor.MimeType;

        this.kmencode = this.MimeType.getMimeTypeProtocol('application/km');
        this.decode = this.Data.getRegisterProtocol('json').decode;
        this._selectedNodes = [];


        /**
         * editor의 receiver가 clipboard 이벤트를 포함한 모든 이벤트를 일괄 처리
         * @Editor: Naixor
         * @Date: 2015.9.24
         */
        document.addEventListener('copy', this.beforeCopy.bind(this));
        document.addEventListener('cut', this.beforeCut.bind(this));
        document.addEventListener('paste', this.beforePaste.bind(this));
    },
    /*
     * 여러 노드 복사·붙여넣기 처리 추가
     */
    encode: function (nodes) {
        var _nodes = [];
        for (var i = 0, l = nodes.length; i < l; i++) {
            _nodes.push( this.minder.exportNode(nodes[i]));
        }
        return kmencode(  this.Data.getRegisterProtocol('json').encode(_nodes));
    },

    beforeCopy : function (e) {
        if (document.activeElement == this.receiver.element) {
            var clipBoardEvent = e;
            var state = this.fsm.state();

            switch (state) {
                case 'input': {
                    break;
                }
                case 'normal': {
                    var nodes = [].concat(this.minder.getSelectedNodes());
                    if (nodes.length) {
                        // 붙여넣기로 복사된 노드의 id 정보가 모두 같으므로 이 알고리즘을 사용
                        // 의문점: node.getParent()나 node.parent를 사용하면 선택되지 않은 노드가 선택된 노드로 렌더링되는 이상 현상이 생기므로, 직접 거슬러 올라가는 방식 대신 isAncestorOf를 사용
                        if (nodes.length > 1) {
                            var targetLevel;
                            nodes.sort(function(a, b) {
                                return a.getLevel() - b.getLevel();
                            });
                            targetLevel = nodes[0].getLevel();
                            if (targetLevel !== nodes[nodes.length-1].getLevel()) {
                                var plevel, pnode,
                                    idx = 0, l = nodes.length, pidx = l-1;

                                pnode = nodes[pidx];

                                while (pnode.getLevel() !== targetLevel) {
                                    idx = 0;
                                    while (idx < l && nodes[idx].getLevel() === targetLevel) {
                                        if (nodes[idx].isAncestorOf(pnode)) {
                                            nodes.splice(pidx, 1);
                                            break;
                                        }
                                        idx++;
                                    }
                                    pidx--;
                                    pnode = nodes[pidx];
                                }
                            }
                        }
                        var str = encode(nodes);
                        clipBoardEvent.clipboardData.setData('text/plain', str);
                    }
                    e.preventDefault();
                    break;
                }
            }
        }
    },

    beforeCut : function (e) {
        if (document.activeElement == this.receiver.element) {
            if (this.minder.getStatus() !== 'normal') {
                e.preventDefault();
                return;
            };

            var clipBoardEvent = e;
            var state = this.fsm.state();

            switch (this.state) {
                case 'input': {
                    break;
                }
                case 'normal': {
                    var nodes = this.minder.getSelectedNodes();
                    if (nodes.length) {
                        clipBoardEvent.clipboardData.setData('text/plain', encode(nodes));
                        this.minder.execCommand('removenode');
                    }
                    e.preventDefault();
                    break;
                }
            }
        }
    },

    beforePaste : function(e) {
        if (document.activeElement == this.receiver.element) {
            if (this.minder.getStatus() !== 'normal') {
                e.preventDefault();
                return;
            };

            var clipBoardEvent = e;
            var state = this.fsm.state();
            var textData = clipBoardEvent.clipboardData.getData('text/plain');

            switch (state) {
                case 'input': {
                    // input 상태에서 형식이 application/km이면 paste 작업을 하지 않음
                    if (!this.MimeType.isPureText(textData)) {
                        e.preventDefault();
                        return;
                    };
                    break;
                }
                case 'normal': {
                    /*
                     * normal 상태에서 선택한 노드에 붙여넣어 하위 노드 텍스트를 가져오는 경우를 별도 처리
                     */
                    var sNodes = this.minder.getSelectedNodes();

                    if (this.MimeType.whichMimeType(textData) === 'application/km') {
                        var nodes = this.decode(this.MimeType.getPureText(textData));
                        var _node;
                        sNodes.forEach(function(node) {
                            // 붙여넣기 로직에서 하위 노드 재정렬로 순서가 뒤집히는 것을 막기 위해 복사할 때 역순으로 처리
                            for (var i = nodes.length-1; i >= 0; i--) {
                                _node = this.minder.createNode(null, node);
                                this.minder.importNode(_node, nodes[i]);
                                this._selectedNodes.push(_node);
                                node.appendChild(_node);
                            }
                        });
                        this.minder.select(this._selectedNodes, true);
                        this._selectedNodes = [];

                        this.minder.refresh();
                    }else if (clipBoardEvent.clipboardData && clipBoardEvent.clipboardData.items[0].type.indexOf('image') > -1) {
                        //var imageFile = clipBoardEvent.clipboardData.items[0].getAsFile();
                        //var serverService = angular.element(document.body).injector().get('server');
                        //
                        //return serverService.uploadImage(imageFile).then(function (json) {
                        //    var resp = json.data;
                        //    if (resp.errno === 0) {
                        //        this.minder.execCommand('image', resp.data.url);
                        //    }
                        //});
                    }
                    else {
                        sNodes.forEach(function(node) {
                            this.minder.Text2Children(node, textData);
                        });
                    }
                    e.preventDefault();
                    break;
                }
            }
        }
    }

});

MWF.xApplication.MinderEditor.Input = new Class({
    initialize : function( editor ){
        this.editor = editor;
        this.fsm = editor.fsm;
        this.minder = editor.minder;
        this.popmenu = editor.popmenu;
        this.receiver = editor.receiver;
        if( !this.receiver ){
            return;
        }
        this.receiverElement = this.receiver.element;
        this.isGecko = window.kity.Browser.gecko;
        this.debug = this.editor.debug;
        this.setupReciverElement();
        this.setupFsm();
        this.setupPopmenu();
    },
    setupFsm: function () {

        // when jumped to input mode, enter
        this.fsm.when('* -> input', this.enterInputMode.bind(this));

        // when exited, commit or exit depends on the exit reason
        this.fsm.when('input -> *', function(exit, enter, reason) {
            switch (reason) {
                case 'input-cancel':
                    return this.exitInputMode();
                case 'input-commit':
                default:
                    return this.commitInputResult();
            }
        }.bind(this));

        // lost focus to commit
        this.receiver.onblur(function (e) {
            if (this.fsm.state() == 'input') {
                this.fsm.jump('normal', 'input-commit');
            }
        }.bind(this));

        this.minder.on('beforemousedown', function () {
            if (this.fsm.state() == 'input') {
                this.fsm.jump('normal', 'input-commit');
            }
        }.bind(this));

        this.minder.on('dblclick', function() {
            if (this.minder.getSelectedNode() && this.minder._status !== 'readonly') {
                this.editText();
            }
        }.bind(this));
    },


// let the receiver follow the current selected node position
    setupReciverElement: function () {
        if (this.debug.flaged) {
            //this.receiverElement.classList.add('debug');
        }

        this.receiverElement.onmousedown = function(e) {
            e.stopPropagation();
        };

        this.minder.on('layoutallfinish viewchange viewchanged selectionchange', function(e) {
            // viewchange event is too frequenced, lazy it
            if (e.type == 'viewchange' && this.fsm.state() != 'input') return;

            this.updatePosition();
        }.bind(this));

        this.updatePosition();
    },

// edit entrance in popmenu
    setupPopmenu: function () {
        //popmenu.state('main').button({
        //    position: 'center',
        //    label: '편집',
        //    key: 'F2',
        //    enable: function() {
        //        return minder.queryCommandState('text') != -1;
        //    },
        //    action: editText
        //});
    },


    /**
     * 글꼴 판별 추가: 편집 상태에서 ctrl/cmd + b/i로 적용한 굵게·기울임이 표시와 일치하도록 보장
     * @editor Naixor
     * @Date 2015-12-2
     */
// edit for the selected node
    editText: function() {
        var node = this.minder.getSelectedNode();
        if (!node) {
            return;
        }
        var textContainer = this.receiverElement;
        this.receiverElement.innerText = "";

        if (node.getData('font-weight') === 'bold') {
            var b = document.createElement('b');
            textContainer.appendChild(b);
            textContainer = b;
        }
        if (node.getData('font-style') === 'italic') {
            var i = document.createElement('i');
            textContainer.appendChild(i);
            textContainer = i;
        }
        textContainer.innerText = this.minder.queryCommandValue('text') || "";

        if (this.isGecko) {
            this.receiver.fixFFCaretDisappeared();
        }
        this.fsm.jump('input', 'input-request');
        this.receiver.selectAll();
    },

    /**
     * 글꼴 판별 추가: 편집 상태에서 ctrl/cmd + b/i로 적용한 굵게·기울임이 표시와 일치하도록 보장
     * @editor Naixor
     * @Date 2015-12-2
     */
    enterInputMode: function() {
        var node = this.minder.getSelectedNode();
        var receiverElement = this.receiverElement;
        //receiverElement.contentEditable = true;
        if (node) {
            var fontSize = node.getData('font-size') || node.getStyle('font-size');
            receiverElement.style.fontSize = fontSize + 'px';
            receiverElement.style.minWidth = 0;
            receiverElement.style.display = "";
            receiverElement.style.minWidth = receiverElement.clientWidth + 'px';
            receiverElement.style.fontWeight = node.getData('font-weight') || '';
            receiverElement.style.fontStyle = node.getData('font-style') || '';
            receiverElement.classList.add('input');
            receiverElement.focus();
        }
    },

    /**
     * 텍스트 제출 작업으로 처리
     * @Desc: 다른 노드의 글자를 다른 노드로 복사할 때 일부 브라우저(chrome)가 자동으로 span 태그를 감싸므로, 아래 로직으로 얻는 것이 text 노드가 아닌 span 노드가 되어 undefined가 발생함
     * @Warning: 아래 코드는 [].slice.call로 HTMLDomCollection을 Array로 변환하며, ie8 이하에서는 문제가 있음
     * @Editor: Naixor
     * @Date: 2015.9.16
     */
    commitInputText: function(textNodes) {
        var text = '';
        var TAB_CHAR = '\t',
            ENTER_CHAR = '\n',
            STR_CHECK = /\S/,
            SPACE_CHAR = '\u0020',
        // FF, SG, BD, LB, IE 등 브라우저에서 SPACE의 charCode가 32와 160 두 가지인 경우를 처리
            SPACE_CHAR_REGEXP = new RegExp('(\u0020|' + String.fromCharCode(160) + ')'),
            BR = document.createElement('br');
        var isBold = false,
            isItalic = false;

        for (var str,
                 _divChildNodes,
                 space_l, space_num, tab_num,
                 i = 0, l = textNodes.length; i < l; i++) {
            str = textNodes[i];

            switch (Object.prototype.toString.call(str)) {
                // 일반적인 경우 처리
                case '[object HTMLBRElement]': {
                    text += ENTER_CHAR;
                    break;
                }
                case '[object Text]': {
                    // SG에서는 이유 없이 &nbsp;가 붙어 이후 판단에 영향을 주므로 제거!
                    /**
                     * FF의 wholeText는 다음 문제를 일으킴:
                     *     |123| -> 노드에 문자열을 입력하면 TextNode는 [#Text 123]
                     *     제출 후 다시 편집하여 뒤에 문자 몇 개를 추가
                     *     |123abc| -> 이때 TextNode는 [#Text 123, #Text abc]이지만, 둘 중 어느 것의 wholeText를 가져와도 전체 내용 123abc가 됨
                     * 위 BUG는 FF에만 있으므로 wholeText를 textContent로 변경
                     */
                    str = str.textContent.replace("&nbsp;", " ");

                    if (!STR_CHECK.test(str)) {
                        space_l = str.length;
                        while (space_l--) {
                            if (SPACE_CHAR_REGEXP.test(str[space_l])) {
                                text += SPACE_CHAR;
                            } else if (str[space_l] === TAB_CHAR) {
                                text += TAB_CHAR;
                            }
                        }
                    } else {
                        text += str;
                    }
                    break;
                }
                // ctrl + b/i는 글꼴에 <b>/<i> 태그를 붙여 굵게와 기울임을 구현
                case '[object HTMLElement]': {
                    switch (str.nodeName) {
                        case "B": {
                            isBold = true;
                            break;
                        }
                        case "I": {
                            isItalic = true;
                            break;
                        }
                        default: {}
                    }
                    [].splice.apply(textNodes, [i, 1].concat([].slice.call(str.childNodes)));
                    l = textNodes.length;
                    i--;
                    break;
                }
                // span 태그가 추가된 경우는 일반적인 경우로 바꿔 위쪽 처리로 넘김
                case '[object HTMLSpanElement]': {
                    [].splice.apply(textNodes, [i, 1].concat([].slice.call(str.childNodes)));
                    l = textNodes.length;
                    i--;
                    break;
                }
                // 태그가 image 태그이면 올바른 url인지 확인하고, 맞으면 불러옴
                case '[object HTMLImageElement]': {
                    if (str.src) {
                        if (/http(|s):\/\//.test(str.src)) {
                            minder.execCommand("Image", str.src, str.alt);
                        } else {
                            // data:image 프로토콜인 경우
                        }
                    };
                    break;
                }
                // div 태그가 추가된 경우는 일반적인 경우로 바꿔 위쪽 처리로 넘김
                case '[object HTMLDivElement]': {
                    _divChildNodes = [];
                    for (var di = 0, l = str.childNodes.length; di < l; di++) {
                        _divChildNodes.push(str.childNodes[di]);
                    }
                    _divChildNodes.push(BR);
                    [].splice.apply(textNodes, [i, 1].concat(_divChildNodes));
                    l = textNodes.length;
                    i--;
                    break;
                }
                default: {
                    if (str && str.childNodes.length) {
                        _divChildNodes = [];
                        for (var di = 0, l = str.childNodes.length; di < l; di++) {
                            _divChildNodes.push(str.childNodes[di]);
                        }
                        _divChildNodes.push(BR);
                        [].splice.apply(textNodes, [i, 1].concat(_divChildNodes));
                        l = textNodes.length;
                        i--;
                    } else {
                        if (str && str.textContent !== undefined) {
                            text += str.textContent;
                        } else {
                            text += "";
                        }
                    }
                    // // 스타일이 있는 다른 노드가 붙여넣어지면 textContent를 바로 가져오고, 가져올 수 없으면 비움
                }
            }
        };

        text = text.replace(/^\n*|\n*$/g, '');
        text = text.replace(new RegExp('(\n|\r|\n\r)(\u0020|' + String.fromCharCode(160) + '){4}', 'g'), '$1\t');

        this.minder.getSelectedNode().setText(text);
        if (isBold) {
            this.minder.queryCommandState('bold') || this.minder.execCommand('bold');
        } else {
            this.minder.queryCommandState('bold') && this.minder.execCommand('bold');
        }

        if (isItalic) {
            this.minder.queryCommandState('italic') || this.minder.execCommand('italic');
        } else {
            this.minder.queryCommandState('italic') && this.minder.execCommand('italic');
        }
        this.exitInputMode();
        return text;
    },

    /**
     * 노드의 텍스트 정보가 다음인지 판단
     * @Desc: 다른 노드의 글자를 다른 노드로 복사할 때 일부 브라우저(chrome)가 자동으로 span 태그를 감싸므로, 아래 로직으로 얻는 것이 text 노드가 아닌 span 노드가 되어 undefined가 발생함
     * @Notice: 이 로직은 kityminder-core/core/data로 분리해, 특정 노드에 importJson하는 이벤트를 별도로 추가해야 함
     * @Editor: Naixor
     * @Date: 2015.9.16
     */
    commitInputNode: function (node, text) {
        try {
            this.minder.decodeData('text', text).then(function(json) {
                function importText(node, json, minder) {
                    var data = json.data;

                    node.setText(data.text || '');

                    var childrenTreeData = json.children || [];
                    for (var i = 0; i < childrenTreeData.length; i++) {
                        var childNode = minder.createNode(null, node);
                        importText(childNode, childrenTreeData[i], minder);
                    }
                    return node;
                }
                importText(node, json, this.minder);
                this.minder.fire("contentchange");
                this.minder.getRoot().renderTree();
                this.minder.layout(300);
            }.bind(this));
        } catch (e) {
            this.minder.fire("contentchange");
            this.minder.getRoot().renderTree();

            // 마인드맵 노드로 변환할 수 없으면 처리하지 않음
            if (e.toString() !== 'Error: Invalid local format') {
                throw e;
            }
        }
    },

    commitInputResult: function() {
        /**
         * @Desc: 다음과 같이 처리:
         *             사용자 입력에 따라 새 노드 생성 여부를 판단
         *        fix #83 https://github.com/fex-team/kityminder-editor/issues/83
         * @Editor: Naixor
         * @Date: 2015.9.16
         */
        var textNodes = [].slice.call(this.receiverElement.childNodes);

        /**
         * @Desc: setTimeout을 추가한 이유: ie에서 receiverElement.innerHTML=""이 뒤쪽
         * 		  commitInputText에서 textContent 사용 시 오류를 일으킴. 이유는 묻지 말 것!
         * @Editor: Naixor
         * @Date: 2015.12.14
         */
        setTimeout(function () {
            // 내용이 너무 커서 SVG 위치가 어긋나는 문제 해결
            this.receiverElement.innerHTML = "";
        }.bind(this), 0);
        var node = this.minder.getSelectedNode();

        textNodes = this.commitInputText(textNodes);
        this.commitInputNode(node, textNodes);

        if (node.type == 'root') {
            var rootText = this.minder.getRoot().getText();
            this.minder.fire('initChangeRoot', {text: rootText});
        }
    },

    exitInputMode: function () {
        //this.receiverElement.contentEditable = false;
        this.receiverElement.style.cursor = "default";
        this.receiverElement.classList.remove('input');
        this.receiver.selectAll();
    },

    updatePosition: function() {
        var focusNode = this.minder.getSelectedNode();
        if (!focusNode) return;

        if (!this.timer) {
            this.timer = setTimeout(function() {
                var box = focusNode.getRenderBox('TextRenderer');
                this.receiverElement.style.left = Math.round(box.x) + 'px';
                this.receiverElement.style.top = (this.debug.flaged ? Math.round(box.bottom + 30) : Math.round(box.y)) + 'px';
                //receiverElement.focus();
                this.timer = 0;
            }.bind(this));
        }
    }
});

//키 입력에 따라 상태 머신 전환 제어
MWF.xApplication.MinderEditor.JumpingInEditMode = function( editor ) {
/**
 * @Desc: 아래에서 receiver.enable()과 receiver.disable()로
 *        div의 contenteditable 속성을 바꾸는 hack을 사용해, 핫박스를 연 뒤에도 브라우저 입력을 막지 못하는 bug를 해결;
 *        특히: win의 FF는 이 경우 먼저 blur한 뒤 focus해야 해결되지만, 그러면 사용자의
 *             입력기 상태가 사라지므로 FF는 당분간 처리하지 않음
 * @Editor: Naixor
 * @Date: 2015.09.14
 */

    var fsm = editor.fsm;
    var minder = editor.minder;
    var receiver = editor.receiver;
    var container = editor.contentNode;
    var receiverElement = receiver.element;
    var popmenu = editor.popmenu;

    // Nice: http://unixpapa.com/js/key.html
    function isIntendToInput(e) {
        if (e.ctrlKey || e.metaKey || e.altKey) return false;

        // a-zA-Z
        if (e.keyCode >= 65 && e.keyCode <= 90) return true;

        // 0-9 및 그 위의 기호
        if (e.keyCode >= 48 && e.keyCode <= 57) return true;

        // 숫자 키패드 영역 (Enter 제외)
        if (e.keyCode != 108 && e.keyCode >= 96 && e.keyCode <= 111) return true;

        // 숫자 키패드 영역 (Enter 제외)
        // @yinheli from pull request
        if (e.keyCode != 108 && e.keyCode >= 96 && e.keyCode <= 111) return true;

        // 입력기
        if (e.keyCode == 229 || e.keyCode === 0) return true;

        return false;
    }

    // normal -> *
    receiver.listen('normal', function(e) {
        //console.log(  "receiver.listen('normal'" );
        // edit 모드 진입 처리 중 첫 글자를 잃지 않도록 이때 receiver는 반드시 enable 상태여야 함
        receiver.enable();
        // normal -> popmenu
        if (e.is('Space')) {
            e.preventDefault();
            // safari에서는 Space가 popmenu를 띄우는데, 이때 Space가 receiver에 흔적을 남기므로 지움
            if (kity.Browser.safari) {
                receiverElement.innerHTML = '';
            }
            return fsm.jump('popmenu', 'space-trigger');
        }

        /**
         * check
         * @editor Naixor
         * @Date 2015-12-2
         */
        switch (e.type) {
            case 'keydown': {
                if (minder.getSelectedNode()) {
                    if (isIntendToInput(e)) {
                        return fsm.jump('input', 'user-input');
                    }
                } else {
                    receiverElement.innerHTML = '';
                }
                // normal -> normal shortcut
                fsm.jump('normal', 'shortcut-handle', e);
                break;
            }
            case 'keyup': {
                break;
            }
            default: {}
        }
    });

    // popmenu -> normal
    receiver.listen('popmenu', function(e) {
        //console.log(  "receiver.listen('popmenu')" );
        receiver.disable();
        e.preventDefault();
        var handleResult = popmenu.dispatch(e);
        if (popmenu.state() == Popmenu.STATE_IDLE && fsm.state() == 'popmenu') {
            return fsm.jump('normal', 'popmenu-idle');
        }
        //else{
        //    return fsm.jump('normal', 'shortcut-handle', e);
        //}
    });

    // input => normal
    receiver.listen('input', function(e) {
        //console.log(  "receiver.listen('input'" );
        receiver.enable();
        if (e.type == 'keydown') {
            if (e.is('Enter')) {
                e.preventDefault();
                return fsm.jump('normal', 'input-commit');
            }
            if (e.is('Esc')) {
                e.preventDefault();
                return fsm.jump('normal', 'input-cancel');
            }
            if (e.is('Tab') || e.is('Shift + Tab')) {
                e.preventDefault();
            }
        } else if (e.type == 'keyup' && e.is('Esc')) {
            e.preventDefault();
            return fsm.jump('normal', 'input-cancel');
        }
    });


    //////////////////////////////////////////////
    /// 마우스 오른쪽 버튼으로 핫박스 호출
    /// 판단 기준: 누른 위치와 뗀 위치가 같음
    //////////////////////////////////////////////
    var downX, downY;
    var MOUSE_RB = 2; // 마우스 오른쪽 버튼

    container.addEventListener('mousedown', function(e) {
        if (e.button == MOUSE_RB) {
            e.preventDefault();
        }

        if (fsm.state() == 'popmenu') {
            popmenu.active(Popmenu.STATE_IDLE);
            fsm.jump('normal', 'blur');
            downX = e.clientX;
            downY = e.clientY;
        } else if (fsm.state() == 'normal' && e.button == MOUSE_RB) {
            downX = e.clientX;
            downY = e.clientY;
        }
    }, false);

    container.addEventListener('mousewheel', function(e) {
        if (fsm.state() == 'popmenu') {
            popmenu.active(Popmenu.STATE_IDLE);
            fsm.jump('normal', 'mousemove-blur');
        }
    }, false);

    container.addEventListener('contextmenu', function(e) {
        e.preventDefault();
    });

    container.addEventListener('mouseup', function(e) {
        if (fsm.state() != 'normal' ) {
            return;
        }
        if (e.button != MOUSE_RB || e.clientX != downX || e.clientY != downY) {
            return;
        }
        if (!minder.getSelectedNode()) {
            return;
        }
        fsm.jump('popmenu', 'content-menu');
    }, false);

    // 핫박스가 제대로 실행되기 전에 닫히지 않도록 핫박스 이벤트 버블링 차단
    //popmenu.$element.addEventListener('mousedown', function(e) {
    //    e.stopPropagation();
    //});
};

//
//MWF.xApplication.MinderEditor.Popmenu = function( editor ){
//    var fsm = editor.fsm;
//    var minder = editor.minder;
//    var receiver = editor.receiver;
//    var container = editor.contentNode;
//
//    var popmenu = new Hotbox(container);
//
//    popmenu.setParentFSM(fsm);
//
//    fsm.when('normal -> popmenu', function(exit, enter, reason) {
//        var node = minder.getSelectedNode();
//        var position;
//        if (node) {
//            var box = node.getRenderBox();
//            position = {
//                x: box.cx,
//                y: box.cy
//            };
//        }
//        popmenu.active('main', position);
//    });
//
//    fsm.when('normal -> normal', function(exit, enter, reason, e) {
//        if (reason == 'shortcut-handle') {
//            var handleResult = popmenu.dispatch(e);
//            if (handleResult) {
//                e.preventDefault();
//            } else {
//                minder.dispatchKeyEvent(e);
//            }
//        }
//    });
//
//    fsm.when('modal -> normal', function(exit, enter, reason, e) {
//        if (reason == 'import-text-finish') {
//            receiver.element.focus();
//        }
//    });
//}
