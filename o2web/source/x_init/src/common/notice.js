import {Notice} from "@o2oa/ui";

/**메시지 알림 상자
 * @Object notice
 */
const notice = {
    /**오류 메시지 표시
     * @method error
     * @param {String} title 오류 제목
     * @param {String} text  오류 설명
     * @param {Object} options  메시지 상자 표시 파라미터
     * @param {boolean} normal 기본 스타일 사용 여부
     */
    error: (title, text, options= {}, normal)=>{
        const normalStyle =  {skin: 'default', location: 'topRight', marginTop: 10, duration: 5000};
        if (normal) options = Object.assign(options, normalStyle);
        const opts = Object.assign({
            title, text, duration: 0, skin: 'banner', marginTop: 0, type: 'error'
        }, options);
        new Notice(opts);

        const e = Error(text, {cause: opts.err});
        e.name = title;
        throw e;
    },

    /**메시지 표시
     * @method msg
     * @param {String} title 메시지 제목
     * @param {String} text  메시지 설명
     * @param {String} type  메시지 유형 error success info warn
     * @param {Object} options 메시지 상자 표시 파라미터
     */
    msg: (title, text, type, options = {})=>{
        const opts = Object.assign({  title, text, duration: 5000, type, location: 'topRight', marginTop: 10}, options);
        new Notice(opts);
    },

    /**성공 메시지 표시
     * @method msg
     * @param {String} title 메시지 제목
     * @param {String} text  메시지 설명
     * @param {Object} options 메시지 상자 표시 파라미터
     */
    success:  (title, text, options= {} )=>{
        notice.msg(title, text, 'success', options);
    },

    /**일반 메시지 표시
     * @method msg
     * @param {String} title 메시지 제목
     * @param {String} text  메시지 설명
     * @param {Object} options 메시지 상자 표시 파라미터
     */
    info:  (title, text, options = {})=>{
        notice.msg(title, text, 'info', options);
    },

    /**경고 메시지 표시
     * @method msg
     * @param {String} title 메시지 제목
     * @param {String} text  메시지 설명
     * @param {Object} options 메시지 상자 표시 파라미터
     */
    warn:  (title, text, options = {})=>{
        notice.msg(title, text, 'warn', options);
    }
}
export {notice};
