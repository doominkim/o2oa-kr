import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";

import { getPublicData } from "../../../utils/actions";

const template = `
      <div class=" {{ $.isMax ? 'bd-map-container-fixed': 'bd-map-container'}}">
        <div class="bd-map" >
        <!-- Baidu 지도 영역 -->
        </div>
        <div class="icon bd-map-btn" @click="mapMaxOrMini" ><i class="{{  $.isMax ? 'o2icon-shrink' : 'o2icon-enlarge' }}"></i></div>
    </div>
`;

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
    this.loadBDMap();
  },
  // 지도 api 등 리소스 로드
  async loadBDMap() {
    this.iconUrl = "../x_component_attendancev2/$Main/default/us_mk_icon.png"; //아이콘 스타일, 배포 시 절대 경로로 수정
    if (!window.BDMapV2ApiLoaded) {
      const config = await getPublicData("attendanceMapConfig"); // 지도 설정
      let accountkey = "sM5P4Xq9zsXGlco6RAq2CRDtwjR78WQB";
      if (config && config.baiduAccountKey) {
        accountkey = config.baiduAccountKey;
      } else {
        console.error("지도 Key가 설정되지 않았습니다!");
      }
      let apiPath =
        "http://api.map.baidu.com/getscript?v=2.0&ak=" +
        accountkey +
        "&s=1&services=";
      if (window.location.protocol.toLowerCase() === "https:") {
        window.HOST_TYPE = "2";
        apiPath =
          "//api.map.baidu.com/getscript?v=2.0&ak=" +
          accountkey +
          "&s=1&services=";
      }
      o2.load(apiPath, () => {
        console.debug("지도 API 로드 완료, 지도를 불러옵니다!");
        window.BDMapV2ApiLoaded = true;
        this.loadViewOrLocation();
      });
    } else {
      this.loadViewOrLocation();
    }
  },
  loadViewOrLocation() {
    if (this.bind.form.status !== 'a') {
      this.loadMapView();
    } else { // 신규 등록 시 위치 찾기
      this.location();
    }
  },
  loadMapView() {
    const point = new BMap.Point(
      this.bind.form.longitude,
      this.bind.form.latitude
    );
    this.createMap(point);
  },
  // 지도 초기화
  location() {
    console.debug("위치 확인 시작!");
    const geolocation = new BMap.Geolocation();
    var self = this;
    geolocation.getCurrentPosition(
      function (r) {
        console.debug("위치 확인 결과", r);
        if (this.getStatus() == BMAP_STATUS_SUCCESS) {
          console.debug("현재 위치: " + r.point.lng + "," + r.point.lat);
          self.createMap(r.point);
        } else {
          console.error("위치 확인에 실패했습니다!");
          const point = new BMap.Point(120.135431, 30.27412);
          self.createMap(point);
        }
      },
      {
        enableHighAccuracy: true,
        maximumAge: 0,
        SDKLocation: true,
      }
    );
  },
  // 바이두 지도 로드
  createMap(point) {
    console.debug("지도 생성 시작!");
    this.mapNode = this.dom.querySelector(".bd-map");
    if (this.mapNode) {
      console.debug("지도 렌더링", point);
      if (!this.map) {
        this.map = new BMap.Map(this.mapNode); // Map 인스턴스 생성
      }
      this.map.centerAndZoom(point, 15); // 지도 초기화, 중심점 좌표와 지도 레벨 설정
      this.map.enableScrollWheelZoom(true); //마우스 휠 확대/축소 활성화
      this.addControls();
      if (!this.bind.form.isView) {
        this.addMapClick();
      }
      if (this.bind.form.status !== 'a') {
        this.addMarkPoint(point, this.bind.form.placeName);
      }
    }
  },
  // 지도 컨트롤 추가
  addControls() {
    //지도에 확대/축소 컨트롤 추가
    var ctrl_nav = new BMap.NavigationControl({
      anchor: BMAP_ANCHOR_TOP_RIGHT,
      type: BMAP_NAVIGATION_CONTROL_LARGE,
    });
    this.map.addControl(ctrl_nav);
    // 지역 선택기 추가
    this.map.addControl(
      new BMap.CityListControl({
        anchor: BMAP_ANCHOR_TOP_LEFT,
        offset: new BMap.Size(10, 20),
        // 도시 전환 전 이벤트
        onChangeBefore: function () {},
        // 도시 전환 후 이벤트
        onChangeAfter: function () {},
      })
    );
  },
  addMapClick() {
    this.map.addEventListener("click", (e) => {
      console.debug("클릭한 위경도: ", e);
      this.addMarkPoint(new BMap.Point(e.point.lng, e.point.lat));
      this.bind.form.longitude = e.point.lng;
      this.bind.form.latitude = e.point.lat;
    });
  },
  // 위치 지점 추가
  addMarkPoint(point, placeName) {
    this.map.clearOverlays(); // 먼저 지움
    const mIcon = new BMap.Icon(this.iconUrl, new BMap.Size(23, 25), {
      anchor: new BMap.Size(9, 25),
      imageOffset: new BMap.Size(-46, -21),
    });
    const marker = new BMap.Marker(point, {
      icon: mIcon,
      enableDragging: false,
    });
    if (placeName) {
      const label = new BMap.Label(placeName, {
        offset: new BMap.Size(0, -20),
      });
      marker.setLabel(label);
    }
    this.map.addOverlay(marker);
  },
  // 지도 확대·축소
  mapMaxOrMini() {
    this.bind.isMax = !this.bind.isMax;
  },
});
