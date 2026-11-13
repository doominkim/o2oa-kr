MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);

/** @class Stat 통계 컴포넌트.
 * @o2cn 통계 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var stat = this.form.get("fieldId"); //컴포넌트 가져오기
 * //방법 2
 * var stat = this.target; //컴포넌트 자체 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Stat = MWF.APPStat =  new Class(
    /** @lends MWF.xApplication.process.Xform.Stat# */
{
	Extends: MWF.APP$Module,
    options: {
        /**
         * 컴포넌트 비동기 로드 완료 시 발생.
         * @event MWF.xApplication.process.Xform.Stat#loadStat
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 차트 로드 완료 시 발생.
         * @event MWF.xApplication.process.Xform.Stat#afterLoadStat
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "loadStat", "loadChart"]
    },

    _loadUserInterface: function(){
        this.node.empty();
    },
    _afterLoaded: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        this.node.setStyle("min-height", "100px");
        this.loadStat();
    },
    active: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }
        
        if (this.stat) this.stat.loadStatData();
    },
    reload: function(){
        this._loadReadEditAbeld();
	    this.active();
    },
    loadStat: function( json ){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        var viewJson = Object.merge(this.getDefaultJson(), json || {});
	    if ( viewJson.application && viewJson.statName ){
            MWF.xDesktop.requireApp("query.Query", "Statistician", function(){
                /**
                 * @summary Statistician 컴포넌트, 플랫폼이 이 컴포넌트로 통계 로직을 실행
                 * @member {MWF.xApplication.query.Query.Statistician}
                 * @example
                 *  //스크립트에서 이 컴포넌트를 가져올 수 있음
                 * var field = this.form.get("fieldId").stat; //컴포넌트 객체 가져오기
                 */
                this.stat = new MWF.xApplication.query.Query.Statistician(this.form.app, this.node, viewJson, {
                    "resizeNode": (this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
                    "onLoaded": function(){
                        this.fireEvent("loadStat");
                    }.bind(this),
                    "onLoadChart": function(){
                        this.fireEvent("loadChart");
                    }.bind(this)
                });
            }.bind(this));
        }
    },
    getDefaultJson: function(){
	    if( this.json.queryStat ){
	        return {
                "application": this.json.queryStat.appName,
                "statName": this.json.queryStat.name,
                "isChart": (this.json.isChart!=="no"),
                "isLegend": (this.json.isLegend!=="no"),
                "isTable": (this.json.isTable!=="no"),
                "isRowToColumn": (this.json.isRowToColumn!=="no"),
                "tableNodeStyles": this.json.tableNodeStyles,
                "chartNodeStyles": this.json.chartNodeStyles
            };
        }else{
	        return {};
        }
    },

    /**
     * @summary 통계 다시 로드.
     *  @param json {Object} 로드 옵션
     *  <pre><code class='language-js'>[{
     *     "application": "",   //데이터 센터 앱 이름
     *     "statName": "",         //통계 이름
     *     "isChart": true, //차트 표시 여부
     *     "isLegend": true, //범례 표시 여부
     *     "isTable": true, //표 표시 여부
     *     "isRowToColumn": true, //행열 전환 표시 여부
     * }]</code></pre>
     *  @example
     *  this.form.get("fieldId").reloadStat({
     *      "application": "데이터 센터 앱 이름",
     *      "statName": "통계 이름"
     *  });
     *  @return {Boolean} 검증 통과 여부
     */
    reloadStat: function(json){
        var viewJson = Object.merge(this.getDefaultJson(), json || {});
	    if( viewJson.application && viewJson.statName ){
	        if( this.stat ){
                this.stat.reload(viewJson);
            }else{
                this.loadStat(viewJson);
            }
        }
    },
    /**
     * @summary 통계 데이터 가져오기.
     *  @return {Ojbect} 통계 데이터.
     *  @example
     *  var data = this.form.get("fieldId").getData();
     *  @return {Boolean} 검증 통과 여부
     */
    getData: function(){
        if (!this.stat) return null;
        if (!this.stat.stat) return null;
        return this.stat.stat.data;
    }
});