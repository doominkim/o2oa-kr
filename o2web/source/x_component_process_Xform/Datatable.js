MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "DatatablePC", null, false);
MWF.xDesktop.requireApp("process.Xform", "DatatableMobile", null, false);
MWF.xDesktop.requireApp("process.Xform", "DatatableV10", null, false);
//if (COMMON.Browser.Platform.isMobile){

// if ((layout.mobile || COMMON.Browser.Platform.isMobile) && (o2.version["dev"] && o2.version["dev"]>=10)){
//     MWF.xApplication.process.Xform.Datatable = MWF.APPDatatable = MWF.xApplication.process.Xform.DatatableV10;
//     MWF.xApplication.process.Xform.Datatable$Title = MWF.APPDatatable$Title = MWF.xApplication.process.Xform.DatatablePC$Title;
//     MWF.xApplication.process.Xform.Datatable$Data = MWF.APPDatatable$Data = MWF.xApplication.process.Xform.DatatablePC$Data;
// }else
//기존 모바일 양식에 맞추기 위해 설정을 추가함. form.js의 _getModuleType에서 최종 유형을 가져옴.
// if ((layout.mobile || COMMON.Browser.Platform.isMobile)){
//     MWF.xApplication.process.Xform.Datatable = MWF.APPDatatable = MWF.xApplication.process.Xform.DatatableMobile;
//     MWF.xApplication.process.Xform.Datatable$Title = MWF.APPDatatable$Title = MWF.xApplication.process.Xform.DatatableMobile$Title;
//     MWF.xApplication.process.Xform.Datatable$Data = MWF.APPDatatable$Data = MWF.xApplication.process.Xform.DatatableMobile$Data;
// }else{
//     MWF.xApplication.process.Xform.Datatable = MWF.APPDatatable = MWF.xApplication.process.Xform.DatatablePC;
//     MWF.xApplication.process.Xform.Datatable$Title = MWF.APPDatatable$Title = MWF.xApplication.process.Xform.DatatablePC$Title;
//     MWF.xApplication.process.Xform.Datatable$Data = MWF.APPDatatable$Data = MWF.xApplication.process.Xform.DatatablePC$Data;
// }
