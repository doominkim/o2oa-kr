import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";

import { getPublicData } from "../../../utils/actions";
const template = `
  <div class=" {{ $.isMax ? 'bd-map-container-fixed': 'bd-map-container'}}">
      <div class="bd-map" id="amap-container">
          <!-- AMap 지도 -->
      </div>
      <div class="icon bd-map-btn" @click="mapMaxOrMini" ><i class="{{  $.isMax ? 'o2icon-shrink' : 'o2icon-enlarge' }}"></i></div>
  </div>
`;

// Amap(가오더) 지도 컴포넌트
export default content({
  template,
  autoUpdate: true,
  bind() {
    return {
      lp,
      form: {
        longitude: "",
        latitude: "",
        status: "a", // a 신규, u 업데이트, v 조회
        isView: false,
      },
      isMax: false, // 지도 확대
    };
  },
  afterRender() {
    this.loadAMap();
  },
  // 지도 api 등 리소스 로드
  async loadAMap() {
    this.iconUrl = "../x_component_attendancev2/$Main/default/us_mk_icon.png"; //아이콘 스타일, 배포 시 절대 경로로 수정
    if (!window.AMapApiLoaded) {
      const config = await getPublicData("attendanceMapConfig"); // 지도 설정
      let accountkey =  "72232ca87eeec7abc23cf41a89ed7019";
      if (config && config.aMapAccountKey) {
        accountkey = config.aMapAccountKey;
      } else {
        console.error("지도 Key가 설정되지 않았습니다!");
      }
      let apiPath = "http://webapi.amap.com/maps?v=1.4.15&key=" + accountkey;
      if (window.location.protocol.toLowerCase() === "https:") {
        window.HOST_TYPE = "2";
        apiPath = "//webapi.amap.com/maps?v=1.4.15&key=" + accountkey;
      }
      o2.load(apiPath, () => {
        console.debug("AMap API 로드 완료, 지도를 불러옵니다!");
        window.AMapApiLoaded = true;
        this.loadViewOrLocation();
      });
    } else {
      this.loadViewOrLocation();
    }
  },
  loadViewOrLocation() {
    if (this.bind.form.status !== 'a') {
      this.loadMapView();
    } else {
      this.initDefaultNumberMapView();
    }
  },
  // 근무지 조회 시 인수의 위치 사용
  loadMapView() {
    const point = new AMap.LngLat(
      this.bind.form.longitude,
      this.bind.form.latitude
    );
    this.createMap(point);
  },
  // 기본으로 여는 지도 위치(고정값)
  initDefaultNumberMapView() {
    const point = new AMap.LngLat(109.173571, 18.328807);
    this.createMap(point);
  },
  // 바이두 지도 로드
  createMap(point) {
    console.debug("AMap 지도 생성 시작!", point);
    if (!this.map) {
      this.map = new AMap.Map("amap-container", {
        zoom: 17, //레벨
        center: point, //중심점 좌표
        viewMode: "3D", //3D 뷰 사용
      }); // Map 인스턴스 생성
    }

    this.addControls();
    if (!this.bind.form.isView) {
      // 근무지 생성 시 클릭 이벤트 추가 필요
      this.addMapClick();
    }
    if (this.bind.form.status !== 'a') {
      this.addMarkPoint(point, this.bind.form.placeName);
    }
  },
  // 지도 컨트롤 추가
  addControls() {
    // 툴바 플러그인, 축척 플러그인, 오버뷰 플러그인을 함께 로드
    AMap.plugin(
      [
        "AMap.ToolBar",
        "AMap.Scale",
        "AMap.OverView",
        "AMap.MapType",
        "AMap.Geolocation",
      ],
      () => {
        // 지도에 툴바 컨트롤 추가, 툴바 컨트롤은 확대·축소, 이동, 위치 찾기 등의 기능 버튼을 통합한 복합 컨트롤
        this.map.addControl(new AMap.ToolBar());
        // 지도에 축척 컨트롤 추가, 현재 레벨과 위도에서의 지도 축척 표시
        this.map.addControl(new AMap.Scale());
        if (!this.bind.form.isView) {
          console.log("위치 확인 시작!");
          // 근무지 생성 시 현재 위치 찾기 필요
          const geolocation = new AMap.Geolocation({
            enableHighAccuracy: true, //고정밀 위치 찾기 사용 여부, 기본값: true
            timeout: 10000, //10초 초과 시 위치 찾기 중지, 기본값: 무한대
            showCircle: false, //위치 찾기 성공 후 원으로 정확도 범위 표시, 기본값: true
            panToLocation: true, //위치 찾기 성공 후 찾은 위치를 지도 중심점으로 설정, 기본값: true
            zoomToAccuracy: true, //위치 찾기 성공 후 찾은 위치와 정확도 범위가 보이도록 지도 시야 조정, 기본값: false
          });
          this.map.addControl(geolocation);
          geolocation.getCurrentPosition();
        }
      }
    );
  },
  addMapClick() {
    this.map.on("click", (e) => {
      console.debug("클릭한 위경도: ", e);
      this.addMarkPoint(new AMap.LngLat(e.lnglat.getLng(), e.lnglat.getLat()));
      this.bind.form.longitude = e.lnglat.getLng();
      this.bind.form.latitude = e.lnglat.getLat();
    });
  },
  // 위치 지점 추가
  addMarkPoint(point, placeName) {
    this.map.clearMap(); // 먼저 지움
    const marker = new AMap.Marker({
      icon: new AMap.Icon(),
      position: point,
      label: {
        content: placeName || "",
        offset: new AMap.Pixel(0, -20),
      },
    });

    this.map.add(marker);
    this.addCircle(point, this.bind.form.errorRange || 200);
  },
  // 범위 circle 추가
  addCircle(point, radius) {
    //원형 Circle 인스턴스 생성
    const circle = new AMap.Circle({
        center: point, //원 중심
        radius: radius, //반경
        bubble: true, //오버레이 클릭 이벤트가 지도로 버블링되도록 허용, 지도 click 가로채기 방지
        // borderWeight: 3, //테두리 너비
        strokeColor: "#1791fc", //윤곽선 색상
        strokeOpacity: 1, //윤곽선 투명도
        strokeWeight: 1, //윤곽선 너비
        fillOpacity: 0.4, //원 채우기 투명도
        // strokeStyle: "dashed", //윤곽선 스타일
        fillColor: "#1791fc", //원 채우기 색상
        zIndex: 50, //원의 겹침 순서
    });
    this.map.add(circle); //지도에 원 추가
  },
  // 지도 확대·축소
  mapMaxOrMini() {
    this.bind.isMax = !this.bind.isMax;
  },
});
