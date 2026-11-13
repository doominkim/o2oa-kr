import {o2} from '@o2oa/component';

/**
 * 공통 설정 조회
 * @param {*} name
 * @returns
 */
function getPublicData(name) {
    return new Promise((resolve) => {
        o2.UD.getPublicData(name, (dData) => resolve(dData));
    });
}
/**
 * 공통 설정 지정
 * @param {*} name
 * @param {*} value
 * @returns
 */
function putPublicData(name, value) {
    return new Promise((resolve) => {
        o2.UD.putPublicData(name, value, (dData) => resolve(dData));
    });
}

/**
 * 백엔드 api 호출, data만 반환
 * @param {*} content
 * @param {*} action
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
async function doAction(content, action, method, ...args) {
    const m = o2.Actions.load(content)[action][method];
    const json = await m.apply(m, ...args);
    return json.data;
}
/**
 * 백엔드 api 호출, response 객체 전체 반환
 * @param {*} content
 * @param {*} action
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
async function doActionBackResult(content, action, method, ...args) {
    const m = o2.Actions.load(content)[action][method];
    return await m.apply(m, ...args);
}

/**
 * 조직 관리 사용자 API
 * x_organization_assemble_control
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function personAction(method, ...args) {
    return doAction('x_organization_assemble_control', 'PersonAction', method, args);
}

/**
 * 개인 API PersonAction
 * x_organization_assemble_personal
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function personalAction(method, ...args) {
    return doAction('x_organization_assemble_personal', 'PersonAction', method, args);
}

/**
 * UnitAction
 * x_organization_assemble_control
 * @param method
 * @param args
 * @returns {Promise<*>}
 */
function orgUnitAction(method, ...args) {
    return doAction('x_organization_assemble_control', 'UnitAction', method, args);
}
/**
 * PersonAction
 * x_organization_assemble_control
 * @param method
 * @param args
 * @returns {Promise<*>}
 */
function orgPersonAction(method, ...args) {
    return doAction('x_organization_assemble_control', 'PersonAction', method, args);
}
/**
 * IdentityAction
 * x_organization_assemble_control
 * @param method
 * @param args
 * @returns {Promise<*>}
 */
function orgIdentityAction(method, ...args) {
    return doAction('x_organization_assemble_control', 'IdentityAction', method, args);
}

/**
 * UnitAction
 * x_organization_assemble_express
 * @param method
 * @param args
 * @returns {Promise<*>}
 */
function orgExpressUnitAction(method, ...args) {
    return doAction('x_organization_assemble_express', 'UnitAction', method, args);
}

/**
 * 채팅 API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function imAction(method, ...args) {
    return doAction('x_message_assemble_communicate', 'ImAction', method, args);
}

/**
 * 채팅 업로드 Promise
 * @param conversationId
 * @param type
 * @param formData
 * @param file 파일 객체
 * @param progressCallback 진행 상황 반환 함수
 * @returns {Promise<unknown>}
 */
function imUploadFile(conversationId, type, formData, file, progressCallback) {
    return new Promise((res, rej) => {
        const action = o2.Actions.load('x_message_assemble_communicate').ImAction;
        action.action.targetModule = {
            module: {
                addFormDataMessage: (file) => {
                    console.log('진행률 표시줄 생성', file);
                    //진행 막대 처리용 객체 반환
                    return {
                        //업로드 완료, 실패 또는 취소 시 진행 막대 제거
                        clearMessageProgress: () => {
                            console.log('진행률 표시줄 제거', file);
                        },
                        //진행 막대 업데이트
                        updateProgress: (progress) => {
                            console.log('진행률 표시줄 업데이트', file, progress);
                            if (progressCallback) {
                                progressCallback(progress)
                            }
                        },
                        //진행 막대 제목 업데이트
                        setMessageTitle: (title) => {
                            console.log('진행률 표시줄 제목 업데이트', file, title);
                        },
                        //진행 막대 텍스트 업데이트
                        setMessageText: (text) => {
                            console.log('진행률 표시줄 텍스트 업데이트', file, text);
                        },
                        //업로드 시작
                        transferStart: () => {
                            console.log('업로드 시작', file);
                        },

                        //업로드 실패
                        transferFailed: () => {
                            console.log('업로드 실패', file);
                        },

                        //업로드 취소
                        transferCanceled: () => {
                            console.log('업로드 취소', file);
                        },

                        //업로드 완료
                        transferComplete: () => {
                            console.log('업로드 완료', file);
                        },

                        //이 data는 호환성을 위해 코드 오류를 막는 용도이며 실제로는 사용되지 않음
                        data: {id: ''},
                    };
                },
            },
            file: file,
        };

        action.uploadFile(
            conversationId,
            type,
            formData,
            file,
            (json) => {
                if (json.data) {
                    res(json.data);
                } else {
                    res(undefined);
                }
            },
            (error) => {
                console.error(error);
                rej(error);
            },
        );

        // o2.Actions.load("x_message_assemble_communicate").ImAction.uploadFile(conversationId, type, formData, file,  (json)=> {
        //   if (json.data) {
        //     res(json.data)
        //   } else {
        //     res(undefined)
        //   }
        // },  (error)=> {
        //   console.error(error)
        //   rej(error)
        // })
    });
}

/**
 * 아바타 url 가져오기
 * 기본은 그룹 채팅 아바타
 * @param person
 * @returns {*}
 */
function getAvatarUrl(person) {
    const orgAction = o2.Actions.get('x_organization_assemble_control');
    return person ? orgAction.getPersonIcon(person) : new URL('../assets/group.png', import.meta.url).href;
}

/**
 * 대화 아바타 url
 * @param conversationId
 * @returns {string}
 */
function conversationIconUrl(conversationId) {
    const action = o2.Actions.get('x_message_assemble_communicate').action;
    let url = action.getAddress() + '/jaxrs/im/conversation/{id}/icon';
    url = url.replace('{id}', encodeURIComponent(conversationId));
    url += '?' + new Date().getTime();
    return url;
}

//크기별 이미지 url
const getImFileUrlWithWH = (id, width, height) => {
    const action = o2.Actions.get('x_message_assemble_communicate').action;
    let url = action.getAddress() + action.actions.imgFileDownloadWithWH.uri;
    url = url.replace('{id}', encodeURIComponent(id));
    url = url.replace('{width}', encodeURIComponent(width));
    url = url.replace('{height}', encodeURIComponent(height));
    return url;
};
//file 다운로드 url
const getImFileDownloadUrl = (id) => {
    var action = o2.Actions.get('x_message_assemble_communicate').action;
    var url = action.getAddress() + action.actions.imgFileDownload.uri;
    url = url.replace('{id}', encodeURIComponent(id));
    return url;
};

//바이두 지도로 주소 열기
const getBaiduMapUrl = (lat, longt, address, content) => {
    return (
        'https://api.map.baidu.com/marker?location=' +
        lat +
        ',' +
        longt +
        '&title=' +
        address +
        '&content=' +
        content +
        '&output=html&src=net.o2oa.map'
    );
};

export {
    doAction,
    doActionBackResult,
    orgUnitAction,
    orgPersonAction,
    orgExpressUnitAction,
    orgIdentityAction,
    getPublicData,
    putPublicData,
    personAction,
    personalAction,
    imAction,
    imUploadFile,
    getAvatarUrl,
    getImFileUrlWithWH,
    getImFileDownloadUrl,
    getBaiduMapUrl,
    conversationIconUrl,
};
