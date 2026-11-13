if (!FileReader.prototype.readAsBinaryString) {
    FileReader.prototype.readAsBinaryString = function (fileData) {
        var binary = "";
        var pt = this;
        var reader = new FileReader();
        reader.onload = function (e) {
            var bytes = new Uint8Array(reader.result);
            var length = bytes.byteLength;
            for (var i = 0; i < length; i++) {
                binary += String.fromCharCode(bytes[i]);
            }
            //pt.result  - readonly so assign binary
            pt.content = binary;
            pt.onload();
        };
        reader.readAsArrayBuffer(fileData);
    };
}

MWF.ExcelUtilsV2 = {
    loadExcelJS : function( callback ){
        if( !window.ExcelJS ){
            var uri = "../o2_lib/exceljs/babel-polyfill-6.2.js";
            var uri2 = "../o2_lib/exceljs/exceljs.min.js";
            COMMON.AjaxModule.load(uri, function(){
                COMMON.AjaxModule.load(uri2, function(){
                    callback();
                }.bind(this));
            }.bind(this));
        }else{
            callback();
        }
    },

    loadXLSX : function( callback ){
        if( !window.XLSX || !window.xlsxUtils ){
            var uri = "../x_component_Template/framework/xlsx/xlsx.full.js";
            var uri2 = "../x_component_Template/framework/xlsx/xlsxUtils.js";
            COMMON.AjaxModule.load(uri, function(){
                COMMON.AjaxModule.load(uri2, function(){
                    callback();
                }.bind(this))
            }.bind(this))
        }else{
            callback();
        }
    },

    openDownloadDialog: function(url, saveName, callback){
        /**
         * 다운로드 대화상자를 여는 공통 메서드, 구체적인 호환성은 테스트하지 않음
         * @param url 다운로드 주소, blob 객체도 가능, 필수
         * @param saveName 저장 파일 이름, 선택
         */
        if( Browser.name !== 'ie' ){
            if(typeof url == 'object' && url instanceof Blob){
                url = URL.createObjectURL(url); // blob 주소 생성
            }
            var aLink = document.createElement('a');
            aLink.href = url;
            aLink.download = saveName || ''; // HTML5에 새로 추가된 속성으로 저장 파일 이름 지정, 확장자는 생략 가능, 주의: file:/// 모드에서는 적용되지 않음
            var event;
            if(window.MouseEvent && typeOf( window.MouseEvent ) == "function" ) event = new MouseEvent('click');
            else
            {
                event = document.createEvent('MouseEvents');
                event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
            }
            aLink.dispatchEvent(event);
            if(callback)callback();
        }else{
            window.navigator.msSaveBlob( url, saveName);
            if(callback)callback();
        }
    },

    extractLettersAndNumbers: function(str) {
        var letters = "";
        var numbers = "";

        function addNumber( char ){
            if (/[0-9]/.test(char)) {
                numbers += char;
            } else {
                throw new Error(str+'은(는) 올바른 셀 형식이 아닙니다');
            }
        }

        var isLetterPart = true;

        for (var i = 0; i < str.length; i++) {
            var char = str.charAt(i);
            if (isLetterPart) {
                if (/[a-zA-Z]/.test(char)) {
                    letters += char;
                } else {
                    isLetterPart = false;
                    addNumber( char );
                }
            } else {
                addNumber( char );
            }
        }

        if (numbers === "") {
            throw new Error(str+'은(는) 올바른 셀 형식이 아닙니다');
        }

        return {
            letters: letters,
            numbers: numbers
        };
    },

    rectanglesIntersect: function(rect1, rect2) {
        // rect1의 오른쪽 변이 rect2의 왼쪽 변보다 왼쪽에 있는지 확인(즉 rect1이 완전히 rect2의 왼쪽에 있음)
        if (rect1.right <= rect2.left) return false;

        // rect1의 왼쪽 변이 rect2의 오른쪽 변보다 오른쪽에 있는지 확인(즉 rect1이 완전히 rect2의 오른쪽에 있음)
        if (rect1.left >= rect2.right)  return false;

        // rect1의 아래쪽 변이 rect2의 위쪽 변보다 위에 있는지 확인(즉 rect1이 완전히 rect2의 위에 있음)
        if (rect1.bottom <= rect2.top) return false;

        // rect1의 위쪽 변이 rect2의 아래쪽 변보다 아래에 있는지 확인(즉 rect1이 완전히 rect2의 아래에 있음)
        if (rect1.top >= rect2.bottom) return false;

        return true;
    },

    colName2Index: function (colName){
        colName = colName.toUpperCase();
        var index = 0;
        var multiplier = 1;

        for (var i = colName.length - 1; i >= 0; i--) {
            var charCode = colName.charCodeAt(i);
            // 'A'의 문자 코드는 65이므로 64를 빼서 대응하는 숫자(1 - 26)를 구함
            var digit = charCode - 64;
            index += digit * multiplier;
            multiplier *= 26;
        }

        return index;
    },

    index2ColName : function( index ){
        if (index < 0) {
            return null;
        }
        var num = 65;// A의 Unicode 코드
        var colName = "";
        do {
            if (colName.length > 0)index--;
            var remainder = index % 26;
            colName =  String.fromCharCode(remainder + num) + colName;
            index = (index - remainder) / 26;
        } while (index > 0);
        return colName;
    },

    uploadExcelFile : function ( callback ) {

        var uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" accept=\"csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel\" />";
        uploadFileAreaNode.set("html", html);

        var fileUploadNode = uploadFileAreaNode.getFirst();
        fileUploadNode.addEvent("change", function () {
            var files = fileNode.files;
            if (files.length) {
                var file = files.item(0);

                callback(file);
                uploadFileAreaNode.destroy();

            }
        }.bind(this));
        var fileNode = uploadFileAreaNode.getFirst();
        fileNode.click();
    },
};

MWF.ExcelImporter = new Class({
    Implements: [Options, Events],
    options:{
        sheet2JsonOptions: {
            //참고: https://docs.sheetjs.com/docs/api/utilities/array/#array-output
            header: 1          // 첫 행이 헤더이면 반환 데이터는 행마다 배열, 아니면 행마다 객체
            // range: 2,           // 앞의 2행 건너뜀
            // defval: null,       // 빈 셀은 null로 채움
            // rawNumbers: true,   // 지수 표기법 방지
            // dateNF: "yyyy-mm-dd" // 날짜 형식
        },
        dateColIndexes: [
            [], //첫 번째 sheet의 날짜
            [] //두 번째 sheet의 날짜...
        ],
        needSheetName: false
    },
    initialize: function( options ){
        this.setOptions(options);
    },
    execute: function ( callback ) {
        return new Promise((resolve)=>{
            this.sheets = [];
            MWF.ExcelUtilsV2.uploadExcelFile(function (file){
                this.file = file;
                MWF.ExcelUtilsV2.loadXLSX( function(){
                    this._readWorkSheetFromFile(file, ()=>{
                        this._setWorksheetsFormate();
                        var result = this._sheet2json( callback );
                        resolve(result);
                    });
                }.bind(this));

            }.bind(this));
        });
    },
    _readWorkSheetFromFile: function( file, callback ){
        var reader = new FileReader();
        var workbook, data;
        reader.onload = function (e) {
            if (!e) {
                data = reader.content;
            }else {
                data = e.target.result;
            }
            workbook = window.XLSX.read(data, {
                type:'binary',
                cellText:false,
                cellDates:true,
                dateNF:'yyyy-mm-dd HH:mm:ss'
            });

            // 모든 워크시트 정보 순회(숨김 상태 포함)
            const sheetStates = workbook.Workbook?.Sheets || [];
            sheetStates.forEach((sheetInfo, index) => {
                const sheetName = workbook.SheetNames[index];
                const isHidden = sheetInfo.state === "hidden";
                if( !sheetName.endsWith('O2Validation') && !isHidden ){
                    const worksheet = workbook.Sheets[sheetName];
                    this.sheets.push({
                        name: sheetName,
                        worksheet: worksheet
                    });
                }
            });

            if(callback)callback();
        }.bind(this);
        reader.readAsBinaryString(file);
    },
    _setWorksheetsFormate: function(){
        this.sheets.forEach((sheet, i)=>{

            var dateColIndexes = this.options.dateColIndexes || [];

            var dateColArray = ( dateColIndexes[i] || [] ).map( function (idx) {
                return MWF.ExcelUtilsV2.index2ColName( idx );
            }.bind(this));

            this._setWorksheetFormate(sheet, dateColArray);
        })
    },
    _setWorksheetFormate: function (sheet, dateColArray){
        var worksheet = sheet.worksheet;

        if( !dateColArray.length )return;

        var rowCount;
        if( worksheet['!range'] ){
            rowCount = worksheet['!range'].e.r;
        }else{
            var ref = worksheet['!ref'];
            var arr = ref.split(":");
            if(arr.length === 2){
                rowCount = parseInt( arr[1].replace(/[^0-9]/ig,"") );
            }
        }
        if( !rowCount )return;

        for( var i=0; i<dateColArray.length; i++ ){
            for( var j=1; j<=rowCount; j++ ){
                var cell = worksheet[ dateColArray[i]+j ];
                if( cell ){
                    delete cell.w; // remove old formatted text
                    cell.z = 'yyyy-mm-dd HH:mm:ss'; // set cell format
                    window.XLSX.utils.format_cell(cell); // this refreshes the formatted text.
                }
            }
        }
    },
    _sheet2json : function( callback ){

        var opt = this.options.sheet2JsonOptions;
        opt.raw = false;
        opt.dateNF = 'yyyy-mm-dd HH:mm:ss'; //'yyyy-mm-dd';

        var result = [];
        this.sheets.forEach((sheet)=>{
            var json = window.XLSX.utils.sheet_to_json( sheet.worksheet, opt );
            this.options.needSheetName ?
                result.push({name: sheet.name, data: json}) :
                result.push(json);
        });
        if(callback)callback(result);

        return result;
    }
});

MWF.ExcelExporter = new Class({
    Implements: [Options, Events],
    options:{
        fileName: "",
        worksheet: [{
            'isTemplate': false,
            'hasTitle': true,
            'headText': '',
            'headStyle': {
                font: { name: 'Malgun Gothic', family: 4, size: 20, bold: true },
                alignment: { vertical: 'middle', horizontal: 'center', wrapText: true }
            },
            'columnTitleStyle': {
                font: { name: 'Malgun Gothic', family: 4, size: 12, bold: true },
                alignment: { vertical: 'middle', horizontal: 'center', wrapText: true }
            },
            'columnContentStyle': {
                font: { name: 'Malgun Gothic', family: 4, size: 12, bold: false },
                alignment: { vertical: 'middle', horizontal: 'center', wrapText: true }
            },
            sheetName: "",
            colWidthArr: [],
            dateIndexArray: [],
            numberIndexArray: [],
            offsetRowIndex: 0,
            offsetColumnIndex: 0,
            startAddress: '' //예: H12
        }]
    },
    initialize: function( options ){
        if( !MWF.xApplication.Template.LP ){
            MWF.xDesktop.requireApp("Template", "lp." + MWF.language, null, false);
        }

        this.setOptions(options);
    },
    execute: function(data, callback, mergeConfig=[]){
        // var array = [["이름","성별","학력","전공","생년월일","졸업일"]];
        // array.push([ "홍길동","남","대학교 학사","컴퓨터","2001-1-2","2019-9-2" ]);
        // array.push([ "김철수","남","전문대학","수학","1998-1-2","2018-9-2" ]);
        // this.exportToExcel(array, "내보내기 데이터"+(new Date).format("db"));

        return new Promise((resolve)=>{
            MWF.ExcelUtilsV2.loadExcelJS(function (){
                this.workbook = new ExcelJS.Workbook();
                this.worksheets = [];
                this.sheetExporters = [];

                var ps = this.options.worksheet.map((config, i)=>{
                    if( !config.sheetName ){
                        config.sheetName = 'Sheet'+i;
                    }

                    var worksheet = this.workbook.worksheets[i];

                    if( !worksheet )worksheet = this.workbook.addWorksheet(config.sheetName);
                    this.worksheets.push(worksheet);

                    var exporter = new MWF.ExcelExporter.Sheet(worksheet, config);
                    this.sheetExporters.push(exporter);
                    return exporter.execute(data[i], ()=>{}, mergeConfig[i]);
                });
                Promise.all(ps).then(()=>{
                    this.fireEvent('beforeDownload', [this]);
                    this.downloadExcel(callback);
                    resolve();
                });
            }.bind(this));
        });


    },
    downloadExcel: function(callback){
        this.workbook.xlsx.writeBuffer().then(function(buffer){
            var blob = new Blob([buffer]);
            MWF.ExcelUtilsV2.openDownloadDialog(blob, this.options.fileName + ".xlsx", callback);
        }.bind(this));
    }
});

MWF.ExcelExporter.Sheet = new Class({
    Implements: [Options, Events],
    options:{
        'isTemplate': false,
        'hasTitle': true,
        'headText': '',
        'headStyle': {
            font: { name: 'Malgun Gothic', family: 4, size: 20, bold: true },
            alignment: { vertical: 'middle', horizontal: 'center', wrapText: true }
        },
        'columnTitleStyle': {
            font: { name: 'Malgun Gothic', family: 4, size: 12, bold: true },
            border: { top: {style:'thin'}, left: {style:'thin'}, bottom: {style:'thin'}, right: {style:'thin'} },
            alignment: { vertical: 'middle', horizontal: 'center', wrapText: true }
        },
        'columnContentStyle': {
            font: { name: 'Malgun Gothic', family: 4, size: 12, bold: false },
            border: { top: {style:'thin'}, left: {style:'thin'}, bottom: {style:'thin'}, right: {style:'thin'} },
            alignment: { vertical: 'middle', horizontal: 'center', wrapText: true }
        },
        sheetName: "Sheet1",
        colWidthArr: [],
        dateIndexArray: [],
        numberIndexArray: [],
        offsetRowIndex: 0,
        offsetColumnIndex: 0,
        startAddress: '' //예: H12
    },
    initialize: function( worksheet, options ){
        if( !MWF.xApplication.Template.LP ){
            MWF.xDesktop.requireApp("Template", "lp." + MWF.language, null, false);
        }

        this.worksheet = worksheet;
        this.workbook = worksheet.workbook;

        this.setOptions(options);

        //startAddress 예: H12
        if( this.options.startAddress ){
            this._parseStartColRow();
        }
    },
    _parseStartColRow:function (){
        var obj = this.options.startAddress  ?
            MWF.ExcelUtilsV2.extractLettersAndNumbers(this.options.startAddress) :
            {letters:'A', numbers:'1'};

        this.options.offsetColumnIndex = parseInt(MWF.ExcelUtilsV2.colName2Index(obj.letters)) - 1;
        this.options.offsetRowIndex = parseInt(obj.numbers) - 1;
    },
    execute: function(data, callback, mergeConfig){
        // var array = [["이름","성별","학력","전공","생년월일","졸업일"]];
        // array.push([ "홍길동","남","대학교 학사","컴퓨터","2001-1-2","2019-9-2" ]);
        // array.push([ "김철수","남","전문대학","수학","1998-1-2","2018-9-2" ]);
        // this.exportToExcel(array, "내보내기 데이터"+(new Date).format("db"));
        //세 번째 인수
        // [
        //     {
        //         row: 0,           // 헤더 기준 시작 행
        //         col: 2,           // 헤더 기준 시작 열
        //         rowSpan: 2,       // 아래로 병합할 행 수
        //         colSpan: 3        // 오른쪽으로 병합할 열 수
        //     },
        //     { row: 0, col: 2, rowSpan: 2 },          // 세로 병합
        //     { row: 0, col: 3, colSpan: 2 },          // 가로 병합
        //     { start: 'C3', end: 'E5' },              // 영역 병합(가장 직관적)
        //     { type: 'title', level: 0 }              // 의미 기반 병합(고급)
        // ]

        this.mergeConfig = mergeConfig || [];

        this.fireEvent('beforeAppendData', [this]);

        if( this.options.hasTitle ){
            this.titleArray = data.shift();
        }

        this.contentArray = data;

        this.startRowIndex = this.options.offsetRowIndex;

        return this._execute( callback );
    },
    _execute: function ( callback ){

        this._setColsWidth();

        this.appendDataToSheet();

        var hasValidation = false;
        var ps = this.titleArray.map(function( title ){
            if( o2.typeOf(title) === 'object' && title.options ){
                hasValidation = true;
                return title.options;
            }
            return null;
        });
        if(hasValidation){
            return Promise.all(ps).then(function(args){
                for( var i=0; i<args.length; i++ ){
                    if(args[i])this.titleArray[i].optionsValue = args[i];
                }
                this.setDataValidation();
                if(callback)callback();
                return this.worksheet;
            }.bind(this));
        }else{
            if(callback)callback();
            return this.worksheet;
        }
    },
    _setColsWidth: function (){
        var colWidthArr = this.options.colWidthArr;
        var offsetColumnIndex = this.options.offsetColumnIndex;

        ( this.titleArray || this.contentArray[0] || []).each( function( title, i ){
            var column = this.worksheet.getColumn(i+1+ offsetColumnIndex);
            column.width = colWidthArr[i] ? (colWidthArr[i] / 10) : 20;
        }.bind(this));

    },

    _setTitleCellStyle: function (cell){
        //cell.font = { name: 'Malgun Gothic', family: 4, size: 12, bold: true };
        // cell.fill = { type: 'pattern', pattern:'solid', fgColor:{argb:'FFFFFF'} };
        //cell.alignment = { vertical: 'middle', horizontal: 'center', wrapText: true };
        Object.each(this.options.columnTitleStyle || {}, function (value, key){
            cell[key] = value;
        });
    },

    _setDataCellStyle: function(cell, index){
        var isDate = this.options.dateIndexArray.contains( index );
        var isNumber = this.options.numberIndexArray.contains( index );
        var style = this.options.columnContentStyle || {};
        if( isDate ){
            cell.numFmt = 'yyyy-mm-dd HH:MM:SS';
        }else if( isNumber ){
            if( style.alignment && style.alignment.wrapText ){
                style.alignment.wrapText = false;
            }
        }else{
            cell.numFmt = '@';
        }
        Object.each(style || {}, function (value, key){
            cell[key] = value;
        });
    },

    _getRow: function (rowIndex){
        return this.worksheet.getRow(rowIndex);
    },

    _appendHead: function (){
        var rowIndex = this.options.offsetRowIndex+1;
        var colStartIndex = this.options.offsetColumnIndex;
        var colEndIndex = this.titleArray.length-1+colStartIndex;

        var headRow = this._getRow(rowIndex);
        var headCell = headRow.getCell(1+colStartIndex);
        headCell.value = this.options.headText;
        Object.each(this.options.headStyle || {}, function (value, key){
            headCell[key] = value;
        });
        this.worksheet.mergeCells(
            MWF.ExcelUtilsV2.index2ColName(colStartIndex)+rowIndex+
            ":"+
            MWF.ExcelUtilsV2.index2ColName(colEndIndex)+rowIndex
        );
    },

    _parseTitle: function (){
        if( this.options.hasTitle ){
            //헤더 분류 처리
            this.titleDataParsed = [];
            this.maxTitleLevel = 1;
            this.titleArray.each(function (title, i){
                var text = o2.typeOf(title) === 'object' ? title.text : title;
                var texts = ( text || " " ).split('\\');
                this.maxTitleLevel = Math.max( this.maxTitleLevel, texts.length );
                this.titleDataParsed.push( texts );
            }.bind(this));
        }else{
            this.maxTitleLevel = 0;
        }
    },

    _appendTitleValue: function (){
        var offsetRowIndex = this.options.offsetRowIndex;
        var offsetColumnIndex = this.options.offsetColumnIndex;

        for( var level=0 ;level<this.maxTitleLevel; level++ ){
            var titleRow = this._getRow(level+1+offsetRowIndex);

            var lastValue = '';

            this.titleDataParsed.each( function(titles, i){
                if( !titles[level] )return;
                if( lastValue !== titles[level] ){
                    var cell = titleRow.getCell(i+1+offsetColumnIndex);
                    cell.value = titles[level];
                    this._setTitleCellStyle(cell);

                    lastValue = titles[level];
                }

            }.bind(this));
        }
    },

    _mergeTitleCell: function (){
        var offsetRowIndex = this.options.offsetRowIndex;
        var offsetColumnIndex = this.options.offsetColumnIndex;

        var lastValue, lastCell, lastIndex, lastTitles, lastAvailableIndex;

        var checkAndMerge = function (){
            var startColName, starRowIndex, endColName, endRowIndex;
            startColName = MWF.ExcelUtilsV2.index2ColName(lastIndex+offsetColumnIndex);
            starRowIndex = level+1+offsetRowIndex;
            endColName = MWF.ExcelUtilsV2.index2ColName(lastAvailableIndex+offsetColumnIndex);
            endRowIndex = (lastTitles[level+1] ? level+1 : this.maxTitleLevel)+offsetRowIndex;
            if( startColName !== endColName || starRowIndex !== endRowIndex  ){
                this.worksheet.mergeCells(startColName+starRowIndex+':'+endColName+endRowIndex);
            }
        }.bind(this);

        for( var level=0 ;level<this.maxTitleLevel; level++ ){
            var titleRow = this.worksheet.getRow(level+1+offsetRowIndex);

            lastValue = '';
            lastCell = null;
            lastIndex = -1;
            lastTitles = null;
            lastAvailableIndex = -1;

            this.titleDataParsed.each( function(titles, i){
                var value = titles[level];
                if( !value ){
                    if( lastValue && lastTitles && lastCell ){

                        lastAvailableIndex = i - 1;
                        if( i===0 || this.titleDataParsed[i-1][level] ){
                            checkAndMerge();
                        }

                        lastValue = value;
                        lastCell = null;
                        lastIndex = i;
                        lastTitles = titles;
                    }
                    return;
                }
                if( lastValue !== value ){
                    var cell = titleRow.getCell(i+1+offsetColumnIndex);

                    if(lastTitles && lastCell && (i===0 || this.titleDataParsed[i-1][level]) ){
                        checkAndMerge();
                    }

                    lastValue = value;
                    lastCell = cell;
                    lastIndex = i;
                    lastTitles = titles;
                }

                lastAvailableIndex = i;

            }.bind(this));

            if(lastTitles && lastCell && lastTitles[level] ){
                checkAndMerge();
            }

        }
    },

    _appendTitle: function(){
        //헤더 분류 처리
        if( !this.titleDataParsed || !this.titleDataParsed.length ){
            this._parseTitle();
        }

        this._appendTitleValue();

        this._mergeTitleCell();
    },

    _appendContent: function(){
        this.contentArray.each(function( rowData, i ){
            var rowIndex = i+1+this.maxTitleLevel+this.options.offsetRowIndex;
            var contentRow = this._getRow(rowIndex);
            rowData.each(function( content, j ){
                var cell = contentRow.getCell(j+1+this.options.offsetColumnIndex);
                cell.value = content;
                this._setDataCellStyle( cell, j);
            }.bind(this));
        }.bind(this));
    },

    _setTemplateContent: function(){
        for (var rowIndex = 0; rowIndex < 3000; rowIndex++) {
            var index = rowIndex+1+this.maxTitleLevel+this.options.offsetRowIndex;
            var row = this.worksheet.getRow( index );
            this.titleDataParsed.each( function(titles, colIndex){
                var cell = row.getCell(colIndex+1+this.options.offsetColumnIndex);
                this._setDataCellStyle( cell, colIndex);
            }.bind(this) );
        }
    },

    appendDataToSheet: function (){
        if( this.options.headText ){
            this._appendHead();
            this.options.offsetRowIndex++;
        }

        if(this.options.hasTitle){
            this._appendTitle();
        }

        if( this.contentArray.length ){
            this._appendContent();
        }else if( this.options.isTemplate && this.options.hasTitle ){
            this._setTemplateContent();
        }

        this._applyMergeRules();
    },
    _applyMergeRules: function(){
        debugger;
        var cfg = this.mergeConfig || [];

        cfg.each(function(rule){
            if(rule.start && rule.end){
                this._mergeByRange(rule.start, rule.end);
            }
            else if(rule.row != null && rule.col != null){
                this._mergeBySpan(rule);
            }
            else if(rule.type === 'title'){
                this._mergeTitleByRule(rule);
            }
        }.bind(this));
    },
    _mergeByRange: function(start, end){
        var s = MWF.ExcelUtilsV2.extractLettersAndNumbers(start);
        var e = MWF.ExcelUtilsV2.extractLettersAndNumbers(end);

        var startCol = MWF.ExcelUtilsV2.colName2Index(s.letters) - 1 + this.options.offsetColumnIndex;
        var startRow = parseInt(s.numbers) - 1 + this.options.offsetRowIndex;
        var endCol = MWF.ExcelUtilsV2.colName2Index(e.letters) - 1 + this.options.offsetColumnIndex;
        var endRow = parseInt(e.numbers) - 1 + this.options.offsetRowIndex;

        this.worksheet.mergeCells(
            MWF.ExcelUtilsV2.index2ColName(startCol) + (startRow + 1) +
            ':' +
            MWF.ExcelUtilsV2.index2ColName(endCol) + (endRow + 1)
        );
    },
    _mergeBySpan: function(rule){
        var r = rule.row + this.options.offsetRowIndex;
        var c = rule.col + this.options.offsetColumnIndex;

        var endR = r + (rule.rowSpan || 1) - 1;
        var endC = c + (rule.colSpan || 1) - 1;

        this.worksheet.mergeCells(
            MWF.ExcelUtilsV2.index2ColName(c) + (r + 1) +
            ':' +
            MWF.ExcelUtilsV2.index2ColName(endC) + (endR + 1)
        );
    },
    _mergeTitleByRule: function(rule){
        var level = rule.level || 0;
        var col = rule.colIndex;
        var span = rule.colSpan || 1;

        var r = level + this.options.offsetRowIndex;
        var c = col + this.options.offsetColumnIndex;
        var endC = c + span - 1;

        this.worksheet.mergeCells(
            MWF.ExcelUtilsV2.index2ColName(c) + (r + 1) +
            ':' +
            MWF.ExcelUtilsV2.index2ColName(endC) + (r + 1)
        );
    },
    setDataValidation: function (){
        var validationSheetName = this.options.sheetName.replaceAll('-', '_')+'O2Validation';
        var validationSheet = this.workbook.addWorksheet(validationSheetName);
        validationSheet.state = 'hidden'; //hidden 숨김   veryHidden “숨기기/숨기기 취소” 대화 상자에서도 워크시트 숨김

        var colIndex = 0;
        this.titleArray.each(function(title, i){
            var optionsValue = o2.typeOf(title) === 'object' && title.optionsValue;
            if( !optionsValue )return;

            colIndex++;

            var optionsArray = o2.typeOf(optionsValue) === "array" ? optionsValue : [optionsValue];
            validationSheet.getColumn(colIndex).values = optionsArray;

            var colName = MWF.ExcelUtilsV2.index2ColName(colIndex-1);

            var lp = MWF.xApplication.Template.LP.excelUtils;

            var dataValidation = {
                type: 'list',
                allowBlank: true,
                showErrorMessage: true,
                showInputMessage: false,
                formulae: ['='+validationSheetName+'!$'+colName+'$1:$'+colName+'$'+optionsArray.length], // 여기서 Validation Sheet의 내용을 참조 '=Validation!A1:A3'
                //formulae: ['"'+optionsArray.join(",")+'"'],
                promptTitle: lp.promptTitle,
                prompt: lp.prompt,
                errorTitle: lp.errorTitle,
                error: lp.error
            };

            var offsetRowIndex = this.options.offsetRowIndex;
            var dataColName = MWF.ExcelUtilsV2.index2ColName(i+this.options.offsetColumnIndex);
            for (var rowIndex = 2+offsetRowIndex; rowIndex <= 3000+offsetRowIndex; rowIndex++) {
                const cell = this.worksheet.getCell(dataColName+rowIndex);
                cell.dataValidation = dataValidation;
            }
        }.bind(this));
    }
});

MWF.TemplateExcelExporter = new Class({
    Extends: MWF.ExcelExporter,
    _getToken: function(){
        var token = (layout.config && layout.config.sessionStorageEnable) ? sessionStorage.getItem("o2LayoutSessionToken") : "";
        if (!token) {
            if (layout.session && (layout.session.user || layout.session.token)) {
                token = layout.session.token;
                if (!token && layout.session.user && layout.session.user.token) token = layout.session.user.token;
            }
        }
        return token;
    },
    _readWorkbookFromRemoteFile: function(url, callback) {
        MWF.ExcelUtilsV2.loadExcelJS(function(){
            var xhr = new XMLHttpRequest();
            xhr.open('get', url, true);
            xhr.responseType = 'arraybuffer';
            xhr.onload = function(e) {
                if(xhr.status === 200) {
                    if(callback) callback(xhr.response);
                }
            };
            xhr.send();
        });
    },
    execute: function (templateUrl, data,  callback){

        var uri = new URI( templateUrl );
        if( !uri.getData( o2.tokenName ) ){
            var token = {};
            token[ o2.tokenName ] = this._getToken();
            uri.setData(token, true);
        }
        var _uri = o2.filterUrl(uri.toString());

        this._readWorkbookFromRemoteFile( _uri, function(arraybuffer){
            //이름이 dataList인 셀이 삽입 위치
            var wk = new ExcelJS.Workbook();
            wk.xlsx.load( arraybuffer ).then(function(workbook) {

                this.workbook = workbook;

                this.worksheet = this.workbook.worksheets[0];
                if( !this.worksheet )this.worksheet = this.workbook.addWorksheet('Sheet1');

                this.fireEvent('beforeAppendData', [this]);

                if( this.options.hasTitle ){
                    this.titleArray = data.shift();
                }

                this.contentArray = data;

                this.startRowIndex = this.options.offsetRowIndex;

                this._execute( callback );

            }.bind(this));
        }.bind(this));
    },
    _execute: function ( callback ){

        if( this.options.hasTitle ){
            this._parseTitle();
        }

        this._storeOrginalMerges();

        this._setColsWidth();

        this.appendDataToSheet();

        var hasValidation = false;
        var ps = this.titleArray.map(function( title ){
            if( o2.typeOf(title) === 'object' && title.options ){
                hasValidation = true;
                return title.options;
            }
            return null;
        });
        if(hasValidation){
            Promise.all(ps).then(function(args){
                for( var i=0; i<args.length; i++ ){
                    if(args[i])this.titleArray[i].optionsValue = args[i];
                }
                this.setDataValidation();
                this._restoreOrginalMerges();
                this.fireEvent('beforeDownload', [this]);
                this.downloadExcel(callback);
            }.bind(this));
        }else{
            this.fireEvent('beforeDownload', [this]);
            this._restoreOrginalMerges();
            this.downloadExcel(callback);
        }
    },
    appendDataToSheet: function (){
        if( this.options.headText ){
            this._appendHead();
            this.options.offsetRowIndex++;
        }

        if(this.options.hasTitle){
            this._appendTitle();
        }

        if( this.contentArray.length ){
            this._appendContent();
        }else if( this.options.isTemplate && this.options.hasTitle ){
            this._setTemplateContent();
        }

        this._removeTemplateRow();
    },
    _removeTemplateRow: function (){
        this.worksheet.spliceRows(this.startRowIndex+1+this._getAddRowLength(), 1);
    },
    _getRow: function (newRowIdx){
        return this.worksheet.insertRow( newRowIdx );
    },
    _getAddRowLength: function () {
        var length = 0;
        if( this.options.headText )length++;
        if( this.options.hasTitle )length += this.maxTitleLevel;
        if( this.contentArray.length )length += this.contentArray.length;
        return length;
    },

    _storeOrginalMerges: function (){
        if( !this.worksheet.hasMerges )return;

        var rectangle = {
            top: this.startRowIndex+1,
            bottom: this.startRowIndex+this._getAddRowLength(),
            left: this.options.offsetColumnIndex+1,
            right: this.options.offsetColumnIndex+1+(( this.titleArray || this.contentArray ).length),
        };

        this.originalMerges = {};
        Object.each(this.worksheet._merges, function (mergRect, cellName){
            if( MWF.ExcelUtilsV2.rectanglesIntersect(rectangle, mergRect) ){
                this.originalMerges[cellName] = Object.clone(mergRect.model);
            }
        }.bind(this));

        Object.each(this.originalMerges, function (rect, cellName){
            this.worksheet.unMergeCells( cellName );
        }.bind(this));
    },
    _restoreOrginalMerges: function(){
        if( !this.originalMerges || Object.keys(this.originalMerges).length === 0 )return;

        var rowOffset = this._getAddRowLength()-1;

        Object.each(this.originalMerges, function( infor, key ){
            if( infor.top > this.startRowIndex ){
                this.worksheet.mergeCells( infor.top + rowOffset, infor.left, infor.bottom + rowOffset, infor.right );
            }
        }.bind(this));
    }
});
