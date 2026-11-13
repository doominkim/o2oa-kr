MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.Monitor", null, false);
/** @class Monitor 프로세스 다이어그램 컴포넌트.
 * @o2cn 프로세스 다이어그램
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var attachment = this.form.get("name"); //컴포넌트 가져오기
 * //방법 2
 * var attachment = this.target; //컴포넌트 이벤트 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Monitor = MWF.APPMonitor =  new Class(
    /** @lends MWF.xApplication.process.Xform.Monitor# */
    {
    Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        this.node.empty();

        MWF.xDesktop.requireApp("process.Xform", "widget.Monitor", function(){
            var process = (this.form.businessData.work) ? this.form.businessData.work.process : this.form.businessData.workCompleted.process;
            /**
             * @summary 프로세스 다이어그램 객체, MWF.xApplication.process.Xform.widget.Monitor 클래스 인스턴스
             */
            this.monitor = new MWF.xApplication.process.Xform.widget.Monitor(this.node, this.form.businessData.workLogList, this.form.businessData.recordList, process,{
                "onPostLoad" : function(){
                    this.fireEvent("postLoad");
                }.bind(this)
            }, this);
        }.bind(this), false);
    }
});
