MWF.xDesktop.requireApp("cms.Xform", "Org", null, false);
/** @class Reader 독자 컴포넌트.
 * @o2cn 독자 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var field = this.form.get("fieldId"); //컴포넌트 객체 조회
 * //방법 2
 * var field = this.target; //컴포넌트 자체 스크립트(이벤트 스크립트, 기본값 스크립트, 검증 스크립트 등)에서 조회
 *
 * var data = field.getData(); //값 조회
 * field.setData(value); //값 설정
 * field.hide(); //필드 숨기기
 * var id = field.json.id; //필드 식별자 조회
 * var flag = field.isEmpty(); //필드가 비어 있는지 여부
 * @extends MWF.xApplication.process.Xform.Org
 * @o2category FormComponents
 * @o2range {CMS}
 * @hideconstructor
 */
MWF.xApplication.cms.Xform.Reader = MWF.CMSReader =  new Class({
	Extends: MWF.CMSOrg,
	iconStyle: "readerIcon"
});