import { exec } from "@o2oa/util";
import { lp } from "@o2oa/component";


/**
 * 출퇴근 체크 객체 기준으로 외근 체크이면 외근 체크 서식 정보 출력
 * @param {*} record 출퇴근 체크 객체 
 */
function fieldWorkFormat(record) {
  if (record && record.fieldWork) {
    let out = lp.appeal.fieldWork;
    if ( record.fieldWorkJobId) {
      if (record.fieldWorkJobStatus === 1) {
        out = lp.appeal.fieldWorkApprovaling;
      } else if (record.fieldWorkJobStatus === 2) {
        out = lp.appeal.fieldWorkApprovaled;
      }
    }
    return out;
  }

  return "";
}

/**
 * 비어 있는지 여부
 * 숫자 0도 비어 있지 않음
 * @param {*} data
 * @returns
 */
function isEmpty(data) {
  if (data === 0) {
    return false;
  }
  if (!data) {
    return true;
  }
  if (typeof data == "string") {
    return data === "" || data.replace(/(^s*)|(s*$)/g, "").length == 0;
  } else {
    return false;
  }
}

/**
 * 정수 여부(양수·음수 포함)
 * @param {*} inputData
 * @returns
 */
function isInt(inputData) {
  if (isPositiveInt(inputData) || isNegtiveInt(inputData)) {
    return true;
  } else {
    return false;
  }
}
/**
 * 음의 정수 여부(0 포함)
 * @param {*} inputData
 * @returns
 */
function isNegtiveInt(inputData) {
  const regNeg = /^\-[1-9][0-9]*$/; // 음의 정수
  if (regNeg.test(inputData)) {
    return true;
  } else {
    return false;
  }
}
/**
 * 양의 정수 여부
 * @param {*} inputData
 * @returns
 */
function isPositiveInt(inputData) {
  const regPos = /^\d+$/; // 양의 정수
  if (regPos.test(inputData)) {
    return true;
  } else {
    return false;
  }
}

/**
 * 다국어 텍스트 변환
 * @param {*} lp 언어 팩
 * @param {*} key 텍스트의 key 값, 여러 단계는 .으로 구분, 예: form.name
 * @param {*} options 변수 치환 객체, 예: {name: 'abc'}, form.name 안의 {name}을 abc로 치환
 * @returns
 */
function lpFormat(lp, key, options) {
  let v = (exec(`return this.${key}`, lp) || key).toString();
  if (options) {
    Object.keys(options).forEach((n) => {
      const reg = new RegExp(`\{${n}\}`, "g");
      v = v.replace(reg, options[n]);
    });
  }
  return v;
}

/**
 * 객체에 값 설정, 배열도 가능
 * 예: key='a.b' v=1이면 data 객체의 a 속성 아래 b 속성 값을 1로 설정, key='0.b' v=1이면 data 배열 첫 번째 객체의 b 속성 값을 1로 설정
 * @param {*} key 여러 단계는 .으로 구분
 * @param {*} v  저장할 값
 * @param {*} data 저장 대상 객체
 * @returns
 */
function setJSONValue(key, v, data) {
  var idList = key.split(".");
  idList = idList.map(function (d) {
    return d.test(/^\d+$/) ? d.toInt() : d;
  });
  var lastIndex = idList.length - 1;
  for (var i = 0; i <= lastIndex; i++) {
    var id = idList[i];
    if (!id && id !== 0) return;
    if (i === lastIndex) {
      data[id] = v;
    } else {
      var nexId = idList[i + 1];
      if (o2.typeOf(nexId) === "number") {
        //다음 ID가 숫자
        if (!data[id] && o2.typeOf(data[id]) !== "array") {
          data[id] = [];
        }
        if (nexId > data[id].length) {
          //최대 인덱스 초과, 버림
          return;
        }
      } else {
        //다음 ID가 문자열
        if (!data[id] || o2.typeOf(data[id]) !== "object") {
          data[id] = {};
        }
      }
      data = data[id];
    }
  }
}

// 날짜를 YYYY-MM-DD 형식으로 변환
const formatDate = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return `${year}-${month < 10 ? "0" + month : month}-${
    day < 10 ? "0" + day : day
  }`;
};
// 날짜를  YYYY-MM 형식으로 변환
const formatMonth = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  return `${year}-${month < 10 ? "0" + month : month}`;
}

// 분 수를 xx시간xx분 형식으로 변환
const convertMinutesToHoursAndMinutes = (minutes) => {
  var hours = Math.floor(minutes / 60); // 시간 수 계산
  var remainingMinutes = Math.floor(minutes % 60); // 남은 분 수 계산
  var result = "";

  if (hours > 0) {
    result += hours + " " + lp.hour;
  }

  if (remainingMinutes > 0 || result === "") {
    result += remainingMinutes + " " + lp.minute;
  }

  return result;
};

/**
 * 배열을 2차원 배열로 변환
 * @param {*} arr 1차원 배열
 * @param {*} subSize 하위 배열 길이
 * @returns 
 */
const  convertTo2DArray = (arr, subSize) => {
  const result = [];
  for (let i = 0; i < arr.length; i += subSize) {
    result.push(arr.slice(i, i + subSize));
  }
  return result;
}

/**
 * loading 표시
 * @param {*} component 컴포넌트 자체
 */
const showLoading = async (component, newText) => {
  const loading = (await import(`../components/o-loading/index.js`)).default;
  let bind = {}
  if (newText) {
    bind = { text: newText }
  }
  component.loadingVm = await loading.generate(document.body, {bind: bind}, component);
  await new Promise((resolve) => window.requestAnimationFrame(resolve));
}
/**
 * loading 닫기 
 * showLoading과 함께 사용해야 함
 * @param {*} component 컴포넌트 자체
 */
const hideLoading = async (component) => {
  if (component.loadingVm) {
    component.loadingVm.destroy();
    component.loadingVm = null;
  }
}

/**
 * 
 * @param {*} person 
 * @returns 
 */
const formatPersonName = (person) => {
  if (person && person.indexOf("@") > -1) {
    return person.split("@")[0];
  }
  return person;
}
/**
 * 전달된 날짜가 속한 달의 모든 날짜 가져오기
 * @param {Date} inputDate 
 * @returns []
 */
const getAllDatesInMonth = (inputDate) => {
  const result = [];
  const currentDate = new Date(inputDate);
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();
  // 해당 월의 첫날 확정
  const firstDayOfMonth = new Date(year, month, 1);
  // 첫날부터 월이 바뀔 때까지 날짜 증가
  let currentDateInMonth = firstDayOfMonth;
  while (currentDateInMonth.getMonth() === month) {
    result.push(new Date(currentDateInMonth));
    currentDateInMonth.setDate(currentDateInMonth.getDate() + 1);
  }
  return result;
}
/**
 * localStorage 저장
 * @param {*} key 
 * @param {*} item 
 */
const storageSet = (key, item) => {
  localStorage.setItem(key, JSON.stringify(item));
}
/**
 * localStorage 저장 데이터 가져오기
 * @param {*} key 
 * @returns 
 */
const storageGet = (key) => {
  const item = localStorage.getItem(key);
  // JSON.parse로 문자열을 JavaScript 객체로 복원
  return JSON.parse(item);
}

/**
 * 문자열의 특정 내용 치환
 * @param {*} originalString 원본 문자열
 * @param {*} searchString  치환될 내용
 * @param {*} replacement 치환할 내용
 * @returns 
 */
const replaceCustomString = (originalString, searchString, replacement) => {
  var replacedString = originalString.replace(new RegExp(searchString, 'g'), replacement);
  return replacedString;
}

/**
 * 전달된 숫자 개수에 따라 Excel 열 이름 배열 생성, 예: A,B,C,....,AA,AB,AC,....
 * @param len 개수
 * @returns {*[]}
 */
const generateExcelColumnNames = (len) =>{
  const columnNames = [];
  for (let i = 1; i <= len; i++) {
    let columnName = "";
    let quotient = i;
    while (quotient > 0) {
      const remainder = (quotient - 1) % 26;
      columnName = String.fromCharCode(65 + remainder) + columnName;
      quotient = Math.floor((quotient - 1) / 26);
    }
    columnNames.push(columnName);
  }
  return columnNames;
}

/**
 * 단일 파일 선택 기능, 콜백 함수 호출
 * @param callback 콜백 함수, 선택한 파일 반환
 */
const chooseSingleFile = (callback) => {
  const input = document.createElement('input');
  input.type = 'file';
  input.style.display = 'none';
  // change 이벤트 리스너 추가, 업로드된 파일 수신
  input.addEventListener('change', (event) => {
    const files = event.target.files;
    // 업로드된 파일 처리
    if (files && files.length > 0) {
      const file = files[0]
      if (callback) {
        callback(file)
      }
    }
  });
  input.click();
}


/**
 * WGS-84에서 GCJ-02(화성 좌표계)로 변환
 * 중국 본토 지역에 적용
 */
const WGS84_TO_GCJ02 = {
  // 상수 정의
  a: 6378245.0, // 크라소프스키 타원체 장반경
  f: 1.0 / 298.3, // 크라소프스키 타원체 편평률
  ee: 0.00669342162296594323, // 이심률 제곱
  pi: 3.1415926535897932384626, // π
  x_pi: 3.14159265358979324 * 3000.0 / 180.0,

  /**
   * 좌표가 중국 내에 있는지 판단
   * 참고: 홍콩·마카오·대만 및 해외 좌표는 변환을 권장하지 않음
   */
  isInChina(lat, lon) {
    return (lon >= 72.004 && lon <= 135.05 && lat >= 3.86 && lat <= 53.55);
  },

  /**
   * 주 변환 함수
   * @param {number} lat WGS-84 위도
   * @param {number} lon WGS-84 경도
   * @returns {object} {latitude: gcjLat, longitude: gcjLon}
   */
  transform(lat, lon) {
    // 국내가 아니면 원래 좌표를 그대로 반환(해외는 암호화하지 않음)
    if (!this.isInChina(lat, lon)) {
      return { latitude: lat, longitude: lon };
    }

    let dLat = this.transformLat(lon - 105.0, lat - 35.0);
    let dLon = this.transformLon(lon - 105.0, lat - 35.0);
    
    let radLat = lat / 180.0 * this.pi;
    let magic = Math.sin(radLat);
    magic = 1 - this.ee * magic * magic;
    
    const sqrtMagic = Math.sqrt(magic);
    
    dLat = (dLat * 180.0) / ((this.a * (1 - this.ee)) / (magic * sqrtMagic) * this.pi);
    dLon = (dLon * 180.0) / (this.a / sqrtMagic * Math.cos(radLat) * this.pi);
    
    return {
      latitude: lat + dLat,
      longitude: lon + dLon
    };
  },

  // 위도 오프셋 계산
  transformLat(x, y) {
    let ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
    ret += (20.0 * Math.sin(6.0 * x * this.pi) + 20.0 * Math.sin(2.0 * x * this.pi)) * 2.0 / 3.0;
    ret += (20.0 * Math.sin(y * this.pi) + 40.0 * Math.sin(y / 3.0 * this.pi)) * 2.0 / 3.0;
    ret += (160.0 * Math.sin(y / 12.0 * this.pi) + 320 * Math.sin(y * this.pi / 30.0)) * 2.0 / 3.0;
    return ret;
  },

  // 경도 오프셋 계산
  transformLon(x, y) {
    let ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
    ret += (20.0 * Math.sin(6.0 * x * this.pi) + 20.0 * Math.sin(2.0 * x * this.pi)) * 2.0 / 3.0;
    ret += (20.0 * Math.sin(x * this.pi) + 40.0 * Math.sin(x / 3.0 * this.pi)) * 2.0 / 3.0;
    ret += (150.0 * Math.sin(x / 12.0 * this.pi) + 300.0 * Math.sin(x / 30.0 * this.pi)) * 2.0 / 3.0;
    return ret;
  }
};

/**
 * 두 위경도 사이의 거리 계산(단위: 미터)
 * @param {number} lat1 시작점 위도
 * @param {number} lon1 시작점 경도
 * @param {number} lat2 끝점 위도
 * @param {number} lon2 끝점 경도
 * @returns {number} 거리(미터)
 */
function getDistance(lat1, lon1, lat2, lon2) {
  const R = 6378137; // 지구 반경(단위: 미터, WGS84 표준 사용)
  const rad = Math.PI / 180;

  // 각도를 라디안으로 변환
  const lat1Rad = lat1 * rad;
  const lat2Rad = lat2 * rad;
  const deltaLat = (lat2 - lat1) * rad;
  const deltaLon = (lon2 - lon1) * rad;

  // Haversine 공식 핵심 계산
  const a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
            Math.cos(lat1Rad) * Math.cos(lat2Rad) *
            Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
  
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  
  return R * c; // 거리 반환(미터)
}

export {
  fieldWorkFormat,
  getAllDatesInMonth,
  formatPersonName,
  setJSONValue,
  lpFormat,
  isInt,
  isNegtiveInt,
  isPositiveInt,
  isEmpty,
  formatDate,
  formatMonth,
  convertMinutesToHoursAndMinutes,
  convertTo2DArray,
  showLoading,
  hideLoading,
  storageSet,
  storageGet,
  replaceCustomString,
  generateExcelColumnNames,
  chooseSingleFile,
  WGS84_TO_GCJ02,
  getDistance,
};
