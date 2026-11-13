MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Div 컨테이너 컴포넌트.
 * @o2cn 컨테이너 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var div = this.form.get("name"); //컴포넌트 가져오기
 * //방법 2
 * var div = this.target; //컴포넌트 이벤트 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Div = MWF.APPDiv =  new Class({
    Extends: MWF.APP$Module,
      _loadUserInterface: function(){
        if (!this.isReadable && !!this.isHideUnreadable){
            this.node?.addClass('hide');
            this.node.style.display = 'none !important';
            // this.node.setStyle('display', 'none');
        }/*  */
    },
});