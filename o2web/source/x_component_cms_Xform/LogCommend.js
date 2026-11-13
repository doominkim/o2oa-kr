MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("cms.Xform", "widget.LogCommend", null, false);
/** @class CMSLog 문서 열람 로그 컴포넌트.
 * @o2cn 문서 열람 로그 컴포넌트
 * @alias CMSLog
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var log = this.form.get("name"); //컴포넌트 조회
 * //방법 2
 * var log = this.target; //컴포넌트 이벤트 스크립트에서 조회
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {CMS}
 * @hideconstructor
 */
MWF.xApplication.cms.Xform.LogCommend = MWF.CMSLogCommend =  new Class(
    /** @lends CMSLog# */
{
	Extends: MWF.APP$Module,
	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");
        if (!this.isReadable){ 
            this.node?.addClass('hide');
            return ''
        }
        /**
         * @summary log 컴포넌트는 this.log로 기능 구현
         * @member {MWF.xApplication.cms.Xform.widget.Log}
         * @example
         *  //스크립트에서 이 컴포넌트를 가져올 수 있음
         * var field = this.form.get("fieldId"); //컴포넌트 객체 조회
         * var items = field.log.items; //로그 행 객체 조회
         */
        this.log = new MWF.xApplication.cms.Xform.widget.LogCommend( this.form.app, this.node, {
            "documentId" : this.form.businessData.document.id,
            "mode" : this.json.mode,
            "textStyle" : this.json.textStyle
        });
        this.log.load();
	}
});
