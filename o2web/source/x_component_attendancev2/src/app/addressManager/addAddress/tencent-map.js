import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";

import { getPublicData } from "../../../utils/actions";

const template = `
  <div class=" {{ $.isMax ? 'bd-map-container-fixed': 'bd-map-container'}}">
      <div class="bd-map">
          <!-- Tencent 지도 -->
      </div>
      <div class="icon bd-map-btn" @click="mapMaxOrMini" ><i class="{{  $.isMax ? 'o2icon-shrink' : 'o2icon-enlarge' }}"></i></div>
  </div>
`;

// 텐센트 지도 컴포넌트
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
    this.loadTencentMap();
  },
  // 지도 api 등 리소스 로드
  async loadTencentMap() {
    if (!window.TencentMapApiLoaded) {
      const config = await getPublicData("attendanceMapConfig"); // 지도 설정
      let accountkey = "OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77";
      if (config && config.txMapAccountKey) {
        accountkey = config.txMapAccountKey;
      } else {
        console.error("지도 Key가 설정되지 않았습니다!");
      }
      const apiPath = "https://map.qq.com/api/gljs?v=1.exp&key=" + accountkey;
      o2.load(apiPath, () => {
        console.debug("Tencent 지도 API 로드 완료, 지도를 불러옵니다!");
        window.TencentMapApiLoaded = true;
        this.loadViewOrLocation();
      });
    } else {
      this.loadViewOrLocation();
    }
  },
  loadViewOrLocation() {
    if (this.bind.form.status !== "a") {
      this.loadMapView();
    } else {
      this.initDefaultNumberMapView();
    }
  },
  // 근무지 조회 시 인수의 위치 사용
  loadMapView() {
    const point = this.createLatLng(
      this.bind.form.longitude,
      this.bind.form.latitude
    );
    this.createMap(point);
  },
  // 기본으로 여는 지도 위치(고정값)
  initDefaultNumberMapView() {
    const point = this.createLatLng(120.135431, 30.27412);
    this.createMap(point);
  },
  createLatLng(lng, lat) {
    return new TMap.LatLng(Number(lat), Number(lng));
  },
  // 텐센트 지도 로드
  createMap(point) {
    console.debug("Tencent 지도 생성 시작!", point);
    this.mapNode = this.dom.querySelector(".bd-map");
    if (this.mapNode) {
      if (!this.map) {
        this.map = new TMap.Map(this.mapNode, {
          zoom: 17,
          center: point,
          viewMode: "2D",
        });
      } else {
        this.map.setCenter(point);
      }
      if (!this.bind.form.isView) {
        this.addMapClick();
      }
      if (this.bind.form.status !== "a") {
        this.addMarkPoint(point, this.bind.form.placeName);
      }
    }
  },
  addMapClick() {
    if (this.mapClickAdded) {
      return;
    }
    this.mapClickAdded = true;
    this.map.on("click", (e) => {
      console.debug("클릭한 위경도: ", e);
      const latitude = e.latLng.getLat();
      const longitude = e.latLng.getLng();
      const point = new TMap.LatLng(latitude, longitude);
      this.addMarkPoint(point);
      this.bind.form.longitude = longitude;
      this.bind.form.latitude = latitude;
    });
  },
  // 위치 지점 추가
  addMarkPoint(point, placeName) {
    const geometry = {
      id: "workplace",
      position: point,
    };
    if (placeName) {
      geometry.content = placeName;
    }
    if (this.markerLayer) {
      this.markerLayer.setGeometries([geometry]);
    } else {
      this.markerLayer = new TMap.MultiMarker({
        id: "workplace-marker-layer",
        map: this.map,
        geometries: [geometry],
      });
    }
  },
  // 지도 확대·축소
  mapMaxOrMini() {
    this.bind.isMax = !this.bind.isMax;
  },
});
