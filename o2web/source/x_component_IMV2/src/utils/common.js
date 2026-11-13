import {dom, exec} from "@o2oa/util";
import {lp} from "@o2oa/component";
import {EventName} from "./eventBus.js";

// 디바운스
const debounce = (fn, delay = 300) => {
    let timer = null
    return (...args) => {
        if (timer) clearTimeout(timer)
        timer = window.setTimeout(() => {
            fn(...args)
        }, delay)
    }
}

/**
 * 올바른 url인지 여부
 * @param input
 * @returns {boolean}
 */
const isHttpUrl = (input) => {
    try {
        const u = new URL(input);
        return u.protocol === "http:" || u.protocol === "https:";
    } catch {
        return false;
    }
}

/**
 * url 포함 여부
 * @param text
 * @returns {boolean}
 */
const containsUrl = (text) => {
    const urlRegex = /(https?:\/\/[^\s]+)/g;
    return urlRegex.test(text);
}
/**
 * 하이퍼링크를 markdown 형식으로 변환
 * @param text
 * @returns {*}
 */
const convertUrlToMarkdown = (text) => {
    const urlRegex = /(https?:\/\/[^\s]+)/g;
    return text.replace(urlRegex, (url) => {
        return `[${url}](${url})`;
    });
}

/**
 * 현재 메시지가 클릭 이벤트 처리가 필요한지 판단
 * @param msg
 * @returns {boolean}
 */
const needClickMsg = (msg) => {
    if (!msg.body) {
        return false;
    }
    const body = JSON.parse(msg.body);
    return !(!body.type || body.type === 'text');

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
        return data === "" || data.replace(/(^s*)|(s*$)/g, "").length === 0;
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
    return isPositiveInt(inputData) || isNegativeInt(inputData);
}

/**
 * 음의 정수 여부(0 포함)
 * @param {*} inputData
 * @returns
 */
function isNegativeInt(inputData) {
    const regNeg = /^\-[1-9][0-9]*$/; // 음의 정수
    return regNeg.test(inputData);
}

/**
 * 양의 정수 여부
 * @param {*} inputData
 * @returns
 */
function isPositiveInt(inputData) {
    const regPos = /^\d+$/; // 양의 정수
    return regPos.test(inputData);
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

//
/**
 * 객체에 값 설정, 배열도 가능
 * 예: key='a.b' v=1이면 data 객체의 a 속성 아래 b 속성 값을 1로 설정, key='0.b' v=1이면 data 배열 첫 번째 객체의 b 속성 값을 1로 설정
 * @param {*} key 여러 단계는 .으로 구분
 * @param {*} v  저장할 값
 * @param {*} data 저장 대상 객체
 * @returns
 */
function setJSONValue(key, v, data) {
    let idList = key.split(".");
    idList = idList.map(function (d) {
        return d.test(/^\d+$/) ? d.toInt() : d;
    });
    let lastIndex = idList.length - 1;
    for (let i = 0; i <= lastIndex; i++) {
        let id = idList[i];
        if (!id && id !== 0) return;
        if (i === lastIndex) {
            data[id] = v;
        } else {
            let nexId = idList[i + 1];
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

// 날짜를  YYYY-MM 형식으로 변환
const formatMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    return `${year}-${month < 10 ? "0" + month : month}`;
}

const toDate = (str) => {
    const tmpArr = str.split(' ')
    if (!tmpArr[1]) tmpArr.push('0:0:0')
    const dateArr = tmpArr[0].split('-')
    const timeArr = tmpArr[1].split(':')
    return new Date(
        dateArr[0],
        parseInt(dateArr[1]) - 1,
        dateArr[2],
        timeArr[0],
        timeArr[1],
        timeArr[2]
    )
}

// 날짜를 YYYY-MM-DD 형식으로 변환
const ymd = (date) => {
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const d = date.getDate()
    const monthStr = month > 9 ? `${month}` : `0${month}`
    const dStr = d > 9 ? `${d}` : `0${d}`
    return `${year}-${monthStr}-${dStr}`
}
// 날짜를 YYYY-MM-DD HH:mm:ss 형식으로 변환
const ymdhms = (date) => {
    const hour = `${date.getHours() > 9 ? '' + date.getHours() : '0' + date.getHours()}`
    const m = `${date.getMinutes() > 9 ? '' + date.getMinutes() : '0' + date.getMinutes()}`
    const s = `${date.getSeconds() > 9 ? '' + date.getSeconds() : '0' + date.getSeconds()}`
    return `${ymd(date)} ${hour}:${m}:${s}`
}

// 대화 목록 표시 시간
const friendlyTime = (date) => {
    let input
    if (typeof date === 'string') {
        if (date === '') {
            return ''
        }
        input = new Date(date)
    } else if (date instanceof Date) {
        input = date
    } else {
        return ''
    }
    const now = new Date()
    const timeDifference = now.getTime() - input.getTime()
    if (timeDifference < 0) {
        return '미래'
    }
    const second = timeDifference / 1000
    if (second < 60) {
        return '방금'
    }
    const minutes = second / 60
    if (minutes < 60) {
        return `${Math.floor(minutes)}분 전`
    }
    const hours = minutes / 60
    if (hours < 24) {
        return `${Math.floor(hours)}시간 전`
    }
    const days = hours / 24
    if (Math.floor(days) === 1) {
        const yesterday = new Date(new Date().setDate(new Date().getDate() - 1))
        if (input.getDate() === yesterday.getDate()) {
            return '어제'
        } else {
            return '그저께'
        }
    } else if (Math.floor(days) === 2) {
        const beforeYesterday = new Date(new Date().setDate(new Date().getDate() - 2))
        if (input.getDate() === beforeYesterday.getDate()) {
            return '그저께'
        } else {
            return '3일 전'
        }
    } else if (days < 31) {
        return `${Math.floor(days)}일 전`
    } else if (days >= 31 && days <= 2 * 31) {
        return '1개월 전'
    } else if (days >= 2 * 31 && days <= 3 * 31) {
        return '2개월 전'
    } else {
        return ymd(input)
    }
}


// 메시지 목록 표시 시간
const chatMsgShowTimeFormat = (date) => {
    let input
    if (typeof date === 'string') {
        if (date === '') {
            return ''
        }
        input = new Date(date)
    } else if (date instanceof Date) {
        input = date
    } else {
        return ''
    }
    const now = new Date()
    const hour = `${input.getHours() > 9 ? '' + input.getHours() : '0' + input.getHours()}`
    const m = `${input.getMinutes() > 9 ? '' + input.getMinutes() : '0' + input.getMinutes()}`
    const d = `${input.getDate() > 9 ? '' + input.getDate() : '0' + input.getDate()}`
    const month = `${input.getMonth() + 1 > 9 ? '' + (input.getMonth() + 1) : '0' + (input.getMonth() + 1)}`
    if (
        now.getFullYear() === input.getFullYear() &&
        now.getMonth() === input.getMonth() &&
        now.getDate() === input.getDate()
    ) {
        return `${hour}:${m}`
    } else {
        const timeDifference = now.getTime() - input.getTime()
        const days = timeDifference / 1000 / 60 / 60 / 24
        if (Math.floor(days) === 1) {
            const yesterday = new Date(new Date().setDate(new Date().getDate() - 1))
            if (input.getDate() === yesterday.getDate()) {
                return `어제 ${hour}:${m}`
            } else {
                return `그저께 ${hour}:${m}`
            }
        } else if (Math.floor(days) === 2) {
            const beforeYesterday = new Date(new Date().setDate(new Date().getDate() - 2))
            if (input.getDate() === beforeYesterday.getDate()) {
                return `그저께 ${hour}:${m}`
            } else {
                return `3일 전 ${hour}:${m}`
            }
        } else if (now.getFullYear() === input.getFullYear()) {
            return `${month}-${d} ${hour}:${m}`
        } else {
            return `${input.getFullYear()}-${month}-${d} ${hour}:${m}`
        }
    }
}

// 분 수를 xx시간xx분 형식으로 변환
const convertMinutesToHoursAndMinutes = (minutes) => {
    const hours = Math.floor(minutes / 60); // 시간 수 계산
    const remainingMinutes = Math.floor(minutes % 60); // 남은 분 수 계산
    let result = "";

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
const convertTo2DArray = (arr, subSize) => {
    const result = [];
    for (let i = 0; i < arr.length; i += subSize) {
        result.push(arr.slice(i, i + subSize));
    }
    return result;
}

// /**
//  * loading 표시
//  * @param {*} component 컴포넌트 자체
//  * @param newText loading 텍스트, 비워 둘 수 있음
//  */
// const showLoading = async (component, newText) => {
//   const loading = (await import(`../components/o-loading/index.js`)).default;
//   let bind = {}
//   if (newText) {
//     bind = { text: newText }
//   }
//   component.loadingVm = await loading.generate(document.body, {bind: bind}, component);
// }
// /**
//  * loading 닫기
//  * showLoading과 함께 사용해야 함
//  * @param {*} component 컴포넌트 자체
//  */
// const hideLoading = async (component) => {
//   if (component.loadingVm) {
//     component.loadingVm.destroy();
//   }
// }

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
    // 첫날부터 월이 바뀔 때까지 날짜 증가
    let currentDateInMonth = new Date(year, month, 1);
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
    return originalString.replace(new RegExp(searchString, 'g'), replacement);
}

/**
 * 전달된 숫자 개수에 따라 Excel 열 이름 배열 생성, 예: A,B,C,....,AA,AB,AC,....
 * @param len 개수
 * @returns {*[]}
 */
const generateExcelColumnNames = (len) => {
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


let _contextMenuNode
let _closeContextMenuClickEvent
/**
 *
 * @param options { callback, menuList: [{text: "이름", id: "메뉴 id"}], // 메뉴 목록, top, left, target : 바인딩 대상, bodyDom: 바인딩 대상이 있는 body 요소 }
 */
const createContextMenu = (options) => {
    const callback = options.callback
    const menuList = options.menuList

    if (!menuList || !callback) {
        console.error('올바른 매개변수가 전달되지 않았습니다!')
        return
    }
    closeContextMenu()
    let top = options.top
    let left = options.left
    if (!top || !left) {
        const target = options.target
        if (target) {
            const rect = target.getBoundingClientRect()
            const width = rect.width;
            const height = rect.height;
            top = rect.top + height / 2;    // 요소 중심 위치
            left = rect.left + width / 2;  // 요소에서 페이지 왼쪽까지의 거리
        }
    }
    const bodyDom = options.bodyDom ? options.bodyDom : document.body;
    // 메뉴 생성
    _contextMenuNode = dom('div.chat-menu-list', {
        style: 'position: fixed; z-index: 999; top: ' + top + 'px;  left: ' + left + 'px;'
    })
    bodyDom.insertAdjacentElement('beforeend', _contextMenuNode)
    for (let i = 0; i < menuList.length; i++) {
        const menu = menuList[i]
        const liNode = dom('li', {text: menu.text})
        _contextMenuNode.insertAdjacentElement('beforeend', liNode)
        liNode.dataset.menu = JSON.stringify(menu) // 데이터 바인딩
        liNode.addEventListener('click', (event) => {
            event.preventDefault()
            // 바인딩된 데이터 가져오기
            const menu = JSON.parse(event.target.dataset.menu)
            console.debug('=====> 메뉴 클릭', menu)
            callback(menu)
            closeContextMenu()
        })
    }
    // 닫기 이벤트 바인딩
    _closeContextMenuClickEvent = (event) => {
        event.preventDefault()
        closeContextMenu()
        if (_closeContextMenuClickEvent) {
            bodyDom.removeEventListener('click', _closeContextMenuClickEvent)
        }
    }
    bodyDom.addEventListener('click', _closeContextMenuClickEvent)
}
const closeContextMenu = () => {
    if (_contextMenuNode) {
        _contextMenuNode.remove()
        _contextMenuNode = null
    }
}

/**
 * 대화 선택기
 * @returns {Promise<unknown>}
 */
const conversationPicker = async (eventBus) => {
    return new Promise((res, _) => {
        const name = 'im-main-forward'
        eventBus.subscribe(name, (data) => {
            console.debug("선택 결과 반환", data)
            res(data)
            eventBus.unsubscribe(name)
        })
        eventBus.publish(EventName.openChooseConversation, {eventName: name})
    })
}

const fileExtIcon = (ext) => {
    if (ext) {
        if (ext === "jpg" || ext === "jpeg") {
            return new URL("../assets/file_icons/icon_file_jpeg.png", import.meta.url).href;
        } else if (ext === "gif") {
            return new URL("../assets/file_icons/icon_file_gif.png", import.meta.url).href;
        } else if (ext === "png") {
            return new URL("../assets/file_icons/icon_file_png.png", import.meta.url).href;
        } else if (ext === "tiff") {
            return new URL("../assets/file_icons/icon_file_tiff.png", import.meta.url).href;
        } else if (ext === "bmp" || ext === "webp") {
            return new URL("../assets/file_icons/icon_file_img.png", import.meta.url).href;
        } else if (ext === "ogg" || ext === "mp3" || ext === "wav" || ext === "wma") {
            return new URL("../assets/file_icons/icon_file_mp3.png", import.meta.url).href;
        } else if (ext === "mp4") {
            return new URL("../assets/file_icons/icon_file_mp4.png", import.meta.url).href;
        } else if (ext === "avi") {
            return new URL("../assets/file_icons/icon_file_avi.png", import.meta.url).href;
        } else if (ext === "mov" || ext === "rm" || ext === "mkv") {
            return new URL("../assets/file_icons/icon_file_rm.png", import.meta.url).href;
        } else if (ext === "doc" || ext === "docx") {
            return new URL("../assets/file_icons/icon_file_word.png", import.meta.url).href;
        } else if (ext === "xls" || ext === "xlsx") {
            return new URL("../assets/file_icons/icon_file_excel.png", import.meta.url).href;
        } else if (ext === "ppt" || ext === "pptx") {
            return new URL("../assets/file_icons/icon_file_ppt.png", import.meta.url).href;
        } else if (ext === "html") {
            return new URL("../assets/file_icons/icon_file_html.png", import.meta.url).href;
        } else if (ext === "pdf") {
            return new URL("../assets/file_icons/icon_file_pdf.png", import.meta.url).href;
        } else if (ext === "txt" || ext === "json") {
            return new URL("../assets/file_icons/icon_file_txt.png", import.meta.url).href;
        } else if (ext === "zip") {
            return new URL("../assets/file_icons/icon_file_zip.png", import.meta.url).href;
        } else if (ext === "rar") {
            return new URL("../assets/file_icons/icon_file_rar.png", import.meta.url).href;
        } else if (ext === "7z") {
            return new URL("../assets/file_icons/icon_file_arch.png", import.meta.url).href;
        } else if (ext === "ai") {
            return new URL("../assets/file_icons/icon_file_ai.png", import.meta.url).href;
        } else if (ext === "att") {
            return new URL("../assets/file_icons/icon_file_att.png", import.meta.url).href;
        } else if (ext === "au") {
            return new URL("../assets/file_icons/icon_file_au.png", import.meta.url).href;
        } else if (ext === "cad") {
            return new URL("../assets/file_icons/icon_file_cad.png", import.meta.url).href;
        } else if (ext === "cdr") {
            return new URL("../assets/file_icons/icon_file_cdr.png", import.meta.url).href;
        } else if (ext === "eps") {
            return new URL("../assets/file_icons/icon_file_eps.png", import.meta.url).href;
        } else if (ext === "exe") {
            return new URL("../assets/file_icons/icon_file_exe.png", import.meta.url).href;
        } else if (ext === "iso") {
            return new URL("../assets/file_icons/icon_file_iso.png", import.meta.url).href;
        } else if (ext === "link") {
            return new URL("../assets/file_icons/icon_file_link.png", import.meta.url).href;
        } else if (ext === "swf") {
            return new URL("../assets/file_icons/icon_file_flash.png", import.meta.url).href;
        } else if (ext === "psd") {
            return new URL("../assets/file_icons/icon_file_psd.png", import.meta.url).href;
        } else if (ext === "tmp") {
            return new URL("../assets/file_icons/icon_file_tmp.png", import.meta.url).href;
        }
    }
    return new URL("../assets/file_icons/icon_file_unkown.png", import.meta.url).href;
}


// 브라우저의 WebP 지원 여부 확인
const canUseWebP = () => {
    const elem = document.createElement('canvas');
    if (elem.getContext && elem.getContext('2d')) {
        return elem.toDataURL('image/webp').indexOf('data:image/webp') === 0;
    }
    return false
}

export {
    getAllDatesInMonth,
    formatPersonName,
    setJSONValue,
    lpFormat,
    isInt,
    isNegativeInt,
    isPositiveInt,
    isEmpty,
    toDate,
    ymd,
    ymdhms,
    friendlyTime,
    chatMsgShowTimeFormat,
    formatMonth,
    convertMinutesToHoursAndMinutes,
    convertTo2DArray,
    // showLoading,
    // hideLoading,
    storageSet,
    storageGet,
    replaceCustomString,
    generateExcelColumnNames,
    chooseSingleFile,
    createContextMenu,
    conversationPicker,
    fileExtIcon,
    canUseWebP,
    isHttpUrl,
    debounce,
    containsUrl,
    convertUrlToMarkdown,
    needClickMsg
};
