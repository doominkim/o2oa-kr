import {component as content} from '@o2oa/oovm';
import {lp, layout} from '@o2oa/component';
import template from './template.html';
import style from './style.scope.css';
import { isEmpty, lpFormat } from '../../utils/common';
import { mobileAction, getPublicData } from '../../utils/actions';
 

export default content({
    template,
    style,
    autoUpdate: true,
    bind(){
        return {
            lp,
            person: {},
             // 출퇴근 체크 버튼
             checkInCycle: {
                allowFieldWork: false, // 외근 허용 여부
                requiredFieldWorkRemarks: true, // 외근 시 출퇴근 체크 필수 여부
                canCheckIn: false, // 출퇴근 체크 가능 여부
                title: lp.mobile.menu.checkIn, // 출퇴근 체크 버튼 이름
                time: "", // 현재 시간 표시
            },
            // 위치 정보
            location: {
                status: false, // 위치 확인 성공 여부
                title: lp.mobile.locationIng, // 주소 표시
                address: "", // 위치 주소
                // 위치 경위도
                lnglat: {}, 
                isNotFieldWork: false, //범위 내 출퇴근 체크 여부
            },
            bdKey: "Qac4WmBvHXiC87z3HjtRrbotCE3sC9Zg",
            // 출퇴근 체크 근무지
            workPlaceList:[], 
            recordItemList: [], // 출퇴근 체크 기록과 객체 목록
            nextCheckInRecord: null, // 다음 출퇴근 체크 대상
        };
    },
    afterRender() {
        this.startTickTime();
        // 사용자 정보
        this.getCurrentPerson();
        // 위치 정보
        this.loadBDMap();
        // 출퇴근 체크 데이터 조회
        this.getPreCheckData();
    },
    // 현재 사용자 정보
    getCurrentPerson() {
        if (layout.session && layout.session.user) {
            this.bind.person.name = layout.session.user.name;
            const identityList = layout.session.user.identityList;
            if (identityList && identityList.length > 0) {
                this.bind.person.unit = identityList[0].unitLevelName;
            }
            const dAction = o2.Actions.load("x_organization_assemble_control").PersonAction.action;
            let url =  dAction.getAddress() + dAction.actions.getIconWithPerson.uri;
            url = url.replace("{flag}", encodeURIComponent(layout.session.user.id));
            console.debug(url);
            this.bind.person.iconUrl = url;
        }
    },
    // 출퇴근 체크 버튼에 현재 시간 표시
    startTickTime() {
        this.tickTime();
        //TODO 언제 삭제? ????
        this.tickTimeInterval = setInterval(() => {
            this.tickTime();
        }, 1000);
    },
    tickTime() {
        const date = new Date();
        this.bind.checkInCycle.time = `${date.getHours() > 9 ? date.getHours() : '0'+date.getHours()}:${date.getMinutes() > 9 ? date.getMinutes() : '0'+date.getMinutes()}:${date.getSeconds() > 9 ? date.getSeconds() : '0'+date.getSeconds()}`;
    },
    // 지도 api 등 리소스 로드, 이후 거리 계산 등에 필요
    async loadBDMap() {
        const bdKey = await getPublicData("baiduAccountKey");
        const accountkey = bdKey || "sM5P4Xq9zsXGlco6RAq2CRDtwjR78WQB";
        this.bind.bdKey = accountkey;
        let apiPath = "http://api.map.baidu.com/getscript?v=2.0&ak="+accountkey+"&s=1&services=";
        if( window.location.protocol.toLowerCase() === "https:" ){
            window.HOST_TYPE = '2';
            apiPath = "//api.map.baidu.com/getscript?v=2.0&ak="+accountkey+"&s=1&services=";
        }
        if( !window.bMapV2ApiLoaded ){
            o2.load(apiPath, () => {
                this.location();
            });
        } else {
            this.location();
        }
    },
    // 위치 확인
    location() {
        window.bMapV2ApiLoaded = true;
        var self = this;
        const geolocation = new BMap.Geolocation();
        // SDK 보조 위치 확인 사용, app webview 지원
        // geolocation.enableSDKLocation();
        geolocation.getCurrentPosition(function(r){
            console.debug("위치 확인 결과", r);
            if(this.getStatus() == BMAP_STATUS_SUCCESS){
                console.debug('현재 위치: '+r.point.lng+','+r.point.lat);
                 self.bind.location.lnglat.longitude = r.point.lng;
                 self.bind.location.lnglat.latitude = r.point.lat;
                 self.getGeoAddress(r.point);
            } else {
                console.log("위치 확인에 실패했습니다.");
                self.bind.location.title = lp.mobile.locationError;
            } 
        }, {
            enableHighAccuracy: true,
            maximumAge: 0,
            SDKLocation: true,
        });
        // if (navigator.geolocation){
        //     try{
        //         navigator.geolocation.getCurrentPosition(this.callGpsLocation.bind(this), this.callGpsLocation.bind(this));
        //     }catch( e ){
        //         console.error(e);
        //         this.bind.location.title = lp.mobile.locationError;
        //     }
        // } else {
        //     console.error("위치 정보가 없습니다!...");
        //     this.bind.location.title = lp.mobile.locationError;
        // }
    },
    // gps 위치 주소 수신
    callGpsLocation(position) {
        console.debug("GPS 위치 정보", position);
        if (position && position.coords) {
            const latitude = position.coords.latitude;
            const longitude = position.coords.longitude;
            console.debug("latitude", latitude, "longitude", longitude);
            const gpsPoint = new BMap.Point(longitude, latitude);
            const convertor = new BMap.Convertor();
            const pointArr = [];
            pointArr.push(gpsPoint);
            console.debug("Baidu 좌표 변환 시작");
            convertor.translate(pointArr, 1, 5, this.translateBMapPointSuccess.bind(this));
        } else {
            console.error(" GPS 위치 확인 오류!");
            this.bind.location.title = lp.mobile.locationError;
        }
    },
    // Baidu 좌표 변환
    translateBMapPointSuccess(data) {
        console.debug("Baidu 좌표 변환 결과", data);
        if (data.status === 0 && data.points && data.points[0]) {
            console.debug('현재 위치: '+data.points[0].lng+','+data.points[0].lat);
            this.bind.location.lnglat.longitude = data.points[0].lng;
            this.bind.location.lnglat.latitude = data.points[0].lat;
            this.getGeoAddress(data.points[0]);
        } else {
            console.error("Baidu GPS 좌표 변환 오류!");
            this.bind.location.title = lp.mobile.locationError; 
        }
    },
    // Baidu 주소 조회
    async getGeoAddress(point) {
        console.debug("위치 상세 주소 조회 시작")
        const gc = new BMap.Geocoder();
        gc.getLocation(point, (rs) => {
            console.debug(rs);
            this.bind.location.status = true;
            this.bind.location.address = rs.address;
            this.bind.location.title = rs.address;
            this._calDistance();
        });
    },
    // 체크 데이터 조회
    async getPreCheckData() {
        const preCheckData = await mobileAction("preCheckIn");
        if (preCheckData) {
            this.bind.checkInCycle.canCheckIn = preCheckData.canCheckIn || false;
            this.bind.checkInCycle.allowFieldWork = preCheckData.allowFieldWork || false;
            this.bind.checkInCycle.requiredFieldWorkRemarks = preCheckData.requiredFieldWorkRemarks || false;
            this.bind.workPlaceList = preCheckData.workPlaceList || [];
            this._calDistance();
            if (this.bind.checkInCycle.canCheckIn) {
                const recordItemList = preCheckData.checkItemList || [];
                // 다음 출퇴근 체크 대상 데이터 찾기
                const nextList = recordItemList.filter(r=> r.checkInResult === 'PreCheckIn');
                if (nextList != null && nextList.length > 0) {
                    this.bind.nextCheckInRecord = nextList[0];
                    this.bind.checkInCycle.title = (nextList[0].checkInType === 'OnDuty') ? lp.onDuty : lp.offDuty;
                    this.bind.checkInCycle.canCheckIn = true;
                } else {
                    this.bind.nextCheckInRecord = null;
                    this.bind.checkInCycle.title = lp.mobile.menu.checkIn;
                    this.bind.checkInCycle.canCheckIn = false; // 출퇴근 체크 가능한 데이터 없음
                }
                // 출퇴근 체크 정보 목록 처리
                for (let i = 0; i < recordItemList.length; i++) {
                    const item = recordItemList[i];
                    let isRecord = false;
                    let recordTime = '';
                    if (item.checkInResult !== 'PreCheckIn') {
                        isRecord = true;
                        let signTime = item.recordDate || '';
                        if (signTime.length > 16) {
                          signTime = signTime.substring(11, 16);
                        }
                        recordTime = lpFormat(lp, 'mobile.checkInWithTime', {time: signTime});
                    }
                    item.recordTime = recordTime;
                    item.isRecord = isRecord; // 출퇴근 체크 완료 여부
                    item.checkInTypeString =  (item.checkInType === 'OnDuty') ? lp.onDuty : lp.offDuty;
                    let preDutyTime = item.preDutyTime || '';
                    if (isEmpty(item.shiftId)) {
                      preDutyTime = ''; // 교대조 정보가 없으면 자율 근무제 또는 휴무일이므로 출퇴근 체크 시간 미표시
                    }
                    item.preDutyTime = preDutyTime;
                    // 마지막으로 체크 완료된 기록인지 처리
                    if (item.checkInResult !== 'PreCheckIn') {
                        if (i == recordItemList.length-1) { // 마지막 항목
                            item.isLastRecord = true; // 마지막으로 체크 완료된 기록
                        } else {
                            const nextItem = recordItemList[i+1];
                            if (nextItem.checkInResult === 'PreCheckIn') {
                                item.isLastRecord = true;
                            }
                        }
                    }
                    recordItemList[i] = item;
                }
                this.bind.recordItemList = recordItemList;
            }
        } else {
            console.error("요청 오류: 체크 데이터가 반환되지 않았습니다!");
        }
    },
    
    // 거리 계산
    _calDistance() {
        if (this.bind.location.status && this.bind.workPlaceList.length > 0) {
            // 출퇴근 체크 범위 내인지 계산 시작
            if (!this.map) {
                this.map = new BMap.Map(".bmap"); 
            }
            for (let index = 0; index < this.bind.workPlaceList.length; index++) {
                const workPlace = this.bind.workPlaceList[index];
                const testA = new BMap.Point(workPlace.longitude, workPlace.latitude);
                const testB = new BMap.Point(this.bind.location.lnglat.longitude, this.bind.location.lnglat.latitude);
                const range = this.map.getDistance(testA, testB).toFixed(2);
                console.log(range);
                if (range <= workPlace.errorRange) {
                    console.log("범위 내 체크", workPlace);
                    this.bind.location.workPlace = workPlace;
                    this.bind.location.isNotFieldWork = true;
                    this.bind.location.title = workPlace.placeName;
                    break;
                }
            }
            console.log(this.bind);
        }
    },
    // 체크 업데이트
    updateCheckIn(record) {
        if (record && record.isLastRecord) {
            if (this.bind.location.isNotFieldWork) {
                // 정상 출퇴근 체크
                console.log('정상 체크!');
                this.checkInPost(record, this.bind.location.workPlace.id, false, null);
            } else {
                console.debug('외근 체크!');
                this.outSide(record);
            }
        }
    },
    // 출퇴근 체크 클릭
    actionCheckIn() {
        if (this.bind.checkInCycle.canCheckIn) {
            if (this.bind.location.isNotFieldWork) {
                // 정상 출퇴근 체크
                console.log('정상 체크!');
                this.checkInPost(this.bind.nextCheckInRecord, this.bind.location.workPlace.id, false, null);
            } else {
                console.debug('외근 체크!');
                this.outSide(this.bind.nextCheckInRecord);
            }
        }
    },
    // 외근 체크
    outSide(record) {
        if (!record) {
            console.error("잘못된 데이터입니다!");
            return;
        }
        if (!this.bind.checkInCycle.allowFieldWork) {
            o2.api.page.notice(lp.mobile.outsideNotAllow, "error");
            return;
        }
        // 외근 설명을 반드시 입력해야 합니다
        if (this.bind.checkInCycle.requiredFieldWorkRemarks) {
            var _self = this;
            o2.DL.open({
                "title": lp.mobile.outsideTitle,
                "width": "100%",
                "height": "150",
                "style" : "user",
                "html": "<div style='margin-top:10px;'><input style='width:100%;' type='text' placeholder='"+lp.mobile.outsideRemarkPlaceholder+"'></div>",
                "buttonList": [
                    {
                        "text": lp.positive,
                        "class":"comment_dlg_button_ok",
                        "action": function(){
                            const value = this.node.getElement("input").value; //this는 대화상자 객체를 가리킴
                            if( !value ){
                                o2.api.page.notice(lp.mobile.outsideRemarkPlaceholder, "error");
                            }else{
                                _self.checkInPost(record, null, true, value);
                                this.close();
                            }
                        }
                    },
                    {
                        "type": "cancel",
                        "text": lp.cancel,
                        "action": function(){this.close();}
                    }
                ]
            });
        } else {
            this.checkInPost(record, null, true, null);
        }
    },
    // 출퇴근 체크 데이터 제출
    async checkInPost(record, workPlaceId, isOutside, signDesc) {
        const post = {
            recordId: record.id,
            checkInType: record.checkInType,
            workPlaceId: workPlaceId,
            fieldWork: isOutside,
            signDescription: signDesc,
            latitude: this.bind.location.lnglat.latitude,
            longitude: this.bind.location.lnglat.longitude,
            recordAddress:  this.bind.location.address,
        };
        console.debug(post);
        const result = await mobileAction("checkIn", post);
        console.log(result);
        this.getPreCheckData();
    }
  
  
     
});
