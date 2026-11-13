MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("cms.Xform", "widget.Comment", null, false);
/** @class Comment 댓글 컴포넌트.
 * @o2cn 댓글 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var comment = this.form.get("name"); //컴포넌트 조회
 * //방법 2
 * var comment = this.target; //컴포넌트 이벤트 스크립트에서 조회
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {CMS}
 * @hideconstructor
 */
MWF.xApplication.cms.Xform.Comment = MWF.CMSComment =  new Class(
    /** @lends MWF.xApplication.process.Xform.Comment# */
    {
	Extends: MWF.APP$Module,
	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");

        if (!this.isReadable){ 
            this.node?.addClass('hide');
            return ''
        }


        var config = {};
        if(this.json.editorProperties){
            config = Object.clone(this.json.editorProperties);
        }
        if (this.json.config){
            if (this.json.config.code){
                var obj = this.form.Macro.exec(this.json.config.code, this);
                Object.each(obj, function(v, k){
                    config[k] = v;
                });
            }
        }

        /**
         * @summary 댓글 컴포넌트는 this.comment로 기능 구현
         * @member {MWF.xApplication.cms.Xform.widget.Comment}
         * @example
         *  //스크립트에서 이 컴포넌트를 가져올 수 있음
         * var field = this.form.get("fieldId"); //컴포넌트 객체 조회
         * var items = field.comment.editor; //댓글 편집기 객체 조회
         */
        if( !this.downloading ){
            this.comment = new MWF.xApplication.cms.Xform.widget.Comment( this.form.app, this.node, {
                "documentId" : this.form.businessData.document.id,
                "countPerPage" : this.json.countPerPage || 10,
                "isAllowModified" : this.json.isAllowModified,
                "isAllowPublish" : this.isEditable && this.json.isAllowPublish,
                "isAdmin" : this.form.app.isAdmin,
                "editorProperties" : config
            });
            this.comment.load();
        }
	}
}); 