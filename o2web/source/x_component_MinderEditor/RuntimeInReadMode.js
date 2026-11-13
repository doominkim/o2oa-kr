//키 입력에 따라 상태 머신 전환 제어
MWF.xApplication.MinderEditor.JumpingInReadMode = function( editor ) {
/**
 * @Desc: 아래에서 receiver.enable()과 receiver.disable()로
 *        div의 contenteditable 속성을 바꾸는 hack을 사용해, 핫박스를 연 뒤에도 브라우저 입력을 막지 못하는 bug를 해결;
 *        특히: win의 FF는 이 경우 먼저 blur한 뒤 focus해야 해결되지만, 그러면 사용자의
 *             입력기 상태가 사라지므로 FF는 당분간 처리하지 않음
 * @Editor: Naixor
 * @Date: 2015.09.14
 */
    var fsm = editor.fsm;
    var receiver = editor.receiver;
    var container = editor.contentNode;

    // normal -> *
    receiver && receiver.listen('normal', function(e) {
        //console.log(  "receiver.listen('normal'" );
        // edit 모드 진입 처리 중 첫 글자를 잃지 않도록 이때 receiver는 반드시 enable 상태여야 함
        receiver && receiver.enable();

        /**
         * check
         * @editor Naixor
         * @Date 2015-12-2
         */
        switch (e.type) {
            case 'keydown': {
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


    container.addEventListener('contextmenu', function(e) {
        e.preventDefault();
    });

};
