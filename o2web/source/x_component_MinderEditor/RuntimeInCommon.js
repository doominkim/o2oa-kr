
// 노드 드래그 시 키보드 이벤트 차단용
MWF.xApplication.MinderEditor.Drag = new Class({
    initialize : function( editor ){
        this.editor = editor;
        this.fsm = editor.fsm;
        this.minder = editor.minder;
        this.popmenu = editor.popmenu;
        this.receiver =  editor.receiver;
        this.receiverElement = this.receiver?.element;
        this.setupFsm();

        var downX, downY;
        var MOUSE_HAS_DOWN = 0;
        var MOUSE_HAS_UP = 1;
        var BOUND_CHECK = 20;
        var flag = MOUSE_HAS_UP;
        var maxX, maxY, osx, osy, containerY;
        var freeHorizen = this.freeHorizen = false;
        var freeVirtical = this.freeVirtical = false;
        this.frame = null;

        this.minder.on('mousedown', function(e) {
            flag = MOUSE_HAS_DOWN;
            var rect = this.minder.getPaper().container.getBoundingClientRect();
            downX = e.originEvent.clientX;
            downY = e.originEvent.clientY;
            containerY = rect.top;
            maxX = rect.width;
            maxY = rect.height;
        }.bind(this));

        this.minder.on('mousemove', function(e) {
            if ( this.fsm.state() === 'drag' && flag == MOUSE_HAS_DOWN && this.minder.getSelectedNode()
                && (Math.abs(downX - e.originEvent.clientX) > BOUND_CHECK
                || Math.abs(downY - e.originEvent.clientY) > BOUND_CHECK)) {
                osx = e.originEvent.clientX;
                osy = e.originEvent.clientY - containerY;

                if (osx < BOUND_CHECK) {
                    this.move('right', BOUND_CHECK - osx);
                } else if (osx > maxX - BOUND_CHECK) {
                    this.move('left', BOUND_CHECK + osx - maxX);
                } else {
                    freeHorizen = true;
                }
                if (osy < BOUND_CHECK) {
                    this.move('bottom', osy);
                } else if (osy > maxY - BOUND_CHECK) {
                    this.move('top', BOUND_CHECK + osy - maxY);
                } else {
                    freeVirtical = true;
                }
                if (freeHorizen && freeVirtical) {
                    this.move(false);
                }
            }
            if (this.fsm.state() !== 'drag'
                && flag === MOUSE_HAS_DOWN
                && this.minder.getSelectedNode()
                && (Math.abs(downX - e.originEvent.clientX) > BOUND_CHECK
                || Math.abs(downY - e.originEvent.clientY) > BOUND_CHECK)) {

                if (this.fsm.state() === 'popmenu') {
                    popmenu.active(Popmenu.STATE_IDLE);
                }

                return this.fsm.jump('drag', 'user-drag');
            }
        }.bind(this));

        window.addEventListener('mouseup', function () {
            flag = MOUSE_HAS_UP;
            if (this.fsm.state() === 'drag') {
                this.move(false);
                return this.fsm.jump('normal', 'drag-finish');
            }
        }.bind(this), false);
    },
    setupFsm: function(){

        // when jumped to drag mode, enter
        this.fsm.when('* -> drag', function() {
            // now is drag mode
        });

        this.fsm.when('drag -> *', function(exit, enter, reason) {
            if (reason == 'drag-finish') {
                // now exit drag mode
            }
        });
    },
    move: function(direction, speed) {
        if (!direction) {
            this.freeHorizen = this.freeVirtical = false;
            this.frame && kity.releaseFrame(this.frame);
            this.frame = null;
            return;
        }
        if (!this.frame) {
            this.frame = kity.requestFrame((function (direction, speed, minder) {
                return function (frame) {
                    switch (direction) {
                        case 'left':
                            minder._viewDragger.move({x: -speed, y: 0}, 0);
                            break;
                        case 'top':
                            minder._viewDragger.move({x: 0, y: -speed}, 0);
                            break;
                        case 'right':
                            minder._viewDragger.move({x: speed, y: 0}, 0);
                            break;
                        case 'bottom':
                            minder._viewDragger.move({x: 0, y: speed}, 0);
                            break;
                        default:
                            return;
                    }
                    frame.next();
                };
            })(direction, speed, this.minder));
        }
    }
});

MWF.xApplication.MinderEditor.FSM = new Class({
    initialize: function( defaultState ){
        this.currentState = defaultState;
        this.BEFORE_ARROW = ' - ';
        this.AFTER_ARROW = ' -> ';
        this.handlers = [];
        this.debug = new MWF.xApplication.MinderEditor.Debug('fsm');
    },
    /**
     * 상태 전환
     *
     * 모든 상태 전환 감시자에게 알림
     *
     * @param  {string} newState  새 상태 이름
     * @param  {any} reason 전환 사유, 전환 감시자에게 매개변수로 전달 가능
     */
    jump: function(newState, reason) {
        if (!reason) throw new Error('Please tell fsm the reason to jump');

        var oldState = this.currentState;
        var notify = [oldState, newState].concat([].slice.call(arguments, 1));
        var i, handler;

        // 전환 전
        for (i = 0; i < this.handlers.length; i++) {
            handler = this.handlers[i];
            if (this.handlerConditionMatch(handler.condition, 'before', oldState, newState)) {
                if (handler.apply(null, notify)) return;
            }
        }

        this.currentState = newState;
        this.debug.log('[{0}] {1} -> {2}', reason, oldState, newState);

        // 전환 후
        for (i = 0; i < this.handlers.length; i++) {
            handler = this.handlers[i];
            if (this.handlerConditionMatch(handler.condition, 'after', oldState, newState)) {
                handler.apply(null, notify);
            }
        }
        return this.currentState;
    },
    /**
     * 현재 상태 반환
     * @return {string}
     */
    state : function() {
        return this.currentState;
    },
    /**
     * 상태 전환 감시자 추가
     *
     * @param {string} condition
     *     감시 시점
     *         "* => *" (기본값)
     *
     * @param  {Function} handler
     *     감시 함수, 상태 전환 시 세 개의 매개변수를 받음
     *         * from - 전환 전 상태
     *         * to - 전환 후 상태
     *         * reason - 전환 사유
     */
    when : function(condition, handler) {
        this.debug.log('[{0}] {1} ', condition, handler );
        if (arguments.length == 1) {
            handler = condition;
            condition = '* -> *';
        }

        var when, resolved, exit, enter;

        resolved = condition.split(this.BEFORE_ARROW);
        if (resolved.length == 2) {
            when = 'before';
        } else {
            resolved = condition.split(this.AFTER_ARROW);
            if (resolved.length == 2) {
                when = 'after';
            }
        }
        if (!when) throw new Error('Illegal fsm condition: ' + condition);

        exit = resolved[0];
        enter = resolved[1];

        handler.condition = {
            when: when,
            exit: exit,
            enter: enter
        };

        this.handlers.push(handler);
    },
    handlerConditionMatch: function (condition, when, exit, enter) {
        if (condition.when != when) return false;
        if (condition.enter != '*' && condition.enter != enter) return false;
        if (condition.exit != '*' && condition.exit != exit) return;
        return true;
    }
});

//키보드 이벤트 수신/분배기
MWF.xApplication.MinderEditor.Receiver = new Class({
    initialize: function( editor ){
        this.editor = editor;

        this.minder = editor.minder;
        this.fsm = editor.fsm;
        this.key = editor.key;

        // 이벤트를 받는 div
        var element = this.element = document.createElement('div');
        element.contentEditable = true;
        /**
         * @Desc: tabindex 속성을 추가해 element의 contenteditable이 true든 false든 focus와 blur 이벤트를 갖도록 함
         * @Editor: Naixor
         * @Date: 2015.09.14
         */
        //element.setAttribute("tabindex", -1);
        element.classList.add('receiver');
        //if( !layout.mobile ){
            element.onkeydown = element.onkeypress = element.onkeyup = this.dispatchKeyEvent.bind(this);
        //}
        this.editor.contentNode.appendChild(element);


        this.selectAll();
        this.minder.on('beforemousedown', this.selectAll.bind(this));
        this.minder.on('receiverfocus', this.selectAll.bind(this));

        this.minder.on('readonly', function() {
            // minder의 이벤트 수신 차단, receiver와 popmenu 삭제
            this.minder.disable();
            this.element.parentElement.removeChild(this.element);
            //this.editor.popmenu.$container.removeChild(editor.popmenu.$element);
        }.bind(this));

        // 리스너, 수신된 이벤트는 모든 리스너에 전달됨
        this.listeners = [];

    },
    selectAll: function() {
        if(layout.mobile)return;
        // 선택된 항목이 있도록 보장
        if (!this.element.innerHTML) this.element.innerHTML = '&nbsp;';
        var range = document.createRange();
        var selection = window.getSelection();
        range.selectNodeContents(this.element);
        selection.removeAllRanges();
        selection.addRange(range);
        this.element.focus();
    },
    /**
     * @Desc: 입력기 차단 문제 해결을 위해 enable과 disable 메서드 추가
     * @Editor: Naixor
     * @Date: 2015.09.14
     */
    enable: function() {
        this.element.setAttribute("contenteditable", true);
    },
    disable: function() {
        this.element.setAttribute("contenteditable", false);
    },
    /**
     * @Desc: hack FF에서 div contenteditable의 커서 소실 BUG
     * @Editor: Naixor
     * @Date: 2015.10.15
     */
    fixFFCaretDisappeared: function() {
        this.element.removeAttribute("contenteditable");
        this.element.setAttribute("contenteditable", "true");
        this.element.blur();
        this.element.focus();
    },
    /**
     * mouse 이벤트로 receiver 포커스 소실을 판단하는 대신 이 이벤트 사용
     * @editor Naixor
     * @Date 2015-12-2
     */
    onblur: function (handler) {
        this.element.onblur = handler;
    },
    // 지정 상태의 이벤트 수신, state를 전달하지 않으면 모든 상태 수신
    listen : function(state, listener) {
        if (arguments.length == 1) {
            listener = state;
            state = '*';
        }
        listener.notifyState = state;
        this.listeners.push(listener);
    },
    dispatchKeyEvent: function (e) {
        var _self = this;
        e.is = function(keyExpression) {
            var subs = keyExpression.split('|');
            for (var i = 0; i < subs.length; i++) {
                if (_self.key.is(this, subs[i])) return true;
            }
            return false;
        };
        var listener, jumpState;
        for (var i = 0; i < this.listeners.length; i++) {

            listener = this.listeners[i];
            // 수신 상태가 아닌 리스너 무시
            if (listener.notifyState != '*' && listener.notifyState != this.fsm.state()) {
                continue;
            }

            /**
             *
             * 모든 리스너는 한 가지 처리 방식만 허용: 상태 전환.
             * 리스너가 전환을 결정하면 전환할 상태를 반환.
             * 각 이벤트는 하나의 리스너만 상태 전환 가능
             * 전환 동작은 리스너가 직접 수행(전환 시 reason 전달이 필요할 수 있으므로), 전환 결과만 반환하면 됨.
             * 예:
             *
             * ```js
             *  receiver.listen('normal', function(e) {
                 *      if (isSomeReasonForJumpState(e)) {
                 *          return fsm.jump('newstate', e);
                 *      }
                 *  });
             * ```
             */
            if ( listener.call(null, e)) {
                return;
            }
        }
    }
});