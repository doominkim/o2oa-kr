import { createApp} from "vue";
import Loading from "../components/Loading.vue";

export default {

    install(app) {
        // LoadingComponent를 DOM에 동적 마운트
        const container = document.createElement("div");
        document.body.appendChild(container);

        const appInstance = createApp(Loading); // Loading 컴포넌트의 독립 인스턴스 생성
        appInstance.mount(container); // DOM에 마운트
        console.log('플러그인 등록 성공!')
    },
};