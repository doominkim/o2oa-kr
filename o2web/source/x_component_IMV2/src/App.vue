<script setup>
import {ref, onMounted, onUnmounted, nextTick, provide, inject, render, h, getCurrentInstance} from 'vue'
import {o2, lp} from "@o2oa/component";
import IMAside from "./components/IMAside.vue";
import IMMain from "./components/IMMain.vue";
import ChooseConversationPage from "./pages/ChooseConversationPage.vue";
import MsgHistoryPage from "./pages/MsgHistoryPage.vue";
import ImConfigPage from "./pages/ImConfigPage.vue";
import MyCollectionPage from "./pages/MyCollectionPage.vue";
import {EventName} from "./utils/eventBus.js";
import {getBaiduMapUrl, getImFileDownloadUrl, imAction} from "./utils/actions.js";
import {imConfig, imGlobalOptions, windowState} from './store.js';

// 메인 앱 인스턴스 가져오기(메인 앱 마운트 후 호출 보장)
const appInstance = getCurrentInstance()?.appContext;

const windowStateInstance = windowState();
const imConfigInstance = imConfig();
const imGlobalOptionsInstance = imGlobalOptions();
// eventBus
const eventBus = inject('eventBus')

// 외부에서 전달된 options
const { options } = defineProps(['options'])

let openChooseConversationEventName = null

const resizeListener = ()=> windowStateInstance.updateWindowWidth()

onMounted(() => {
  window.addEventListener("resize", resizeListener)
  resizeListener() // 값 초기화
  // options에 따라 특수 상황 처리, 예: 특정 대화 열기
  console.debug('APP =====>  onMounted  options', options)
  // 기본으로 열 대화, openConversation은 conversation 객체
  if (options && options.openConversation && options.openConversation.id && options.openConversation.personList) {
    openConversationForOutside(options.openConversation)
  } else if (options && options.chatToPerson) {
    chatToPerson(options.chatToPerson)
  }
  if (options) {
    hideSide.value = !!options.hideSide
    imGlobalOptionsInstance.setOptions({
      hideSide: !!options.hideSide
    })
    console.debug('APP =====>  imGlobalOptions', imGlobalOptionsInstance.hideSide)
  }

  // 대화 선택기 열기, data에 콜백 eventName이 포함되어야 함
  eventBus.subscribe(EventName.openChooseConversation, (data) => {
    console.debug("openChooseConversation", data)
    showChooseConversation.value = true
    openChooseConversationEventName = data.eventName
  })
  eventBus.subscribe(EventName.openMsg, (data) => {
    console.debug("openMsg", data)
    openMsg(data)
  })
  eventBus.subscribe(EventName.changeIMConfig, (data) => {
    console.debug("changeIMConfig", data)
    loadImConfig()
  })
  // 설정 읽기
  loadImConfig()
  // websocket 메시지 수신 대기
  if (layout.desktop && layout.desktop.socket && layout.desktop.socket.addImListener) {
    console.debug('websocket 메시지 addImListener => ')
    layout.desktop.socket.addImListener("im_revoke", imWsRevokeMessage);
    layout.desktop.socket.addImListener("im_create", imWsCreateMessage);
    layout.desktop.socket.addImListener("im_conversation", imWsConversationMessage);
  }
  if (layout.desktop) {
    if (!layout.desktop.im) {
      layout.desktop.im = {}
    }
    layout.desktop.im.openChat = openConversationForOutside
    layout.desktop.im.chatToPerson = chatToPerson
  }
})
onUnmounted(() => {
  window.removeEventListener("resize", resizeListener)
  // eventBus.unsubscribe(EventName.openConversation)
  eventBus.unsubscribe(EventName.openChooseConversation)
  eventBus.unsubscribe(EventName.openMsg)
  layout.desktop.im.openChat = undefined
  layout.desktop.im.chatToPerson = undefined
  console.debug('APP =========== > onUnmounted')
})

const chatToPerson = async (person) => {
  if (!person) {
    return
  }
  console.debug('APP === 》 chatToPerson', person)
  const conv = await imAction('create', {
    type: 'single',
    personList: [person],
  })
  if (conv) {
    openConversationForOutside(conv)
  }
}

// 대화 열기(외부 호출용)
const openConversationForOutside = (conversation) => {
  console.debug('APP === 》 openConversationForOutside', conversation)
  if (!conversation) {
    return
  }
  nextTick(()=> {
    if (layout.desktop.apps && layout.desktop.apps["IMV2"] && layout.desktop.apps["IMV2"].setCurrent) {
      layout.desktop.apps["IMV2"].setCurrent();
    }
    if (conversation.personList && conversation.personList.contains(layout.session.user.distinguishedName)) {
      currentConversation.value = conversation
      eventBus.publish( EventName.initOpenedConversation, conversation)
    } else {
      console.error('현재 사용자는 이 대화방을 열 수 없습니다', conversation)
    }
  })
}
// 대화 클릭 시 채팅 창 열기
const openConversation = (conversation) => {
  console.debug("openConversation", conversation)
  currentConversation.value = conversation
}
// 채팅 정보 객체 수정
const updateOrDeleteConversation = ( conv ) => {
  console.debug("updateOrDeleteConversation", conv)
  currentConversation.value = conv
  eventBus.publish( EventName.refreshMyConversation ) // 목록 새로고침
}

provide('im-app', {
  openConversation,
  updateOrDeleteConversation
});

// websocket 메시지 생성
const imWsCreateMessage = (msg) => {
  console.debug('websocket 메시지 생성=====> ', msg)
  eventBus.publish( EventName.wsAddMsg, msg )
  eventBus.publish( EventName.refreshMyConversation ) // 목록 새로고침
}
// websocket 메시지 회수
const imWsRevokeMessage = (msg) => {
  console.debug('websocket 메시지 회수=====> ', msg)
  eventBus.publish( EventName.wsRevokeMsg, msg )
}
// websocket 대화 업데이트 메시지
const imWsConversationMessage = (conversation) => {
  console.debug('websocket 메시지 대화방 업데이트=====> ', conversation)
  eventBus.publish( EventName.refreshMyConversation ) // 목록 새로고침
}

const hideSide = ref(false)

const currentConversation = ref(null)

const showChooseConversation = ref(false)

const closeChooseConversation = (conversation) => {
  console.debug('APP === 》 closeChooseConversation', conversation)
  showChooseConversation.value = false
  if (conversation && openChooseConversationEventName) {
    eventBus.publish(openChooseConversationEventName, conversation)
  }
}
const loadImConfig = async () => {
  const config = await imAction('getImConfig')
  imConfigInstance.setImConfig(config)
}
// 메시지 열기
const openMsg = (msg) => {
  const body = JSON.parse(msg.body)
  if (body.type === 'messageHistory') {
    openMsgHistory(msg)
  } else if (body.type === "image") {
    _openImageView(getImFileDownloadUrl(body.fileId))
    // window.open(getImFileDownloadUrl(body.fileId))
  } else if (body.type === "process") {
    if (body.work) {
      o2.api.form.openWork(body.work, "", "");
    }
  }  else if (body.type === "cms") {
    if (body.docId) {
      o2.api.form.openDocument(body.docId);
    }
  } else if (body.type === "file") {
    // onlyOffice 설치됨
    if (layout.serviceAddressList["x_onlyofficefile_assemble_control"]
        && imConfigInstance.enableOnlyOfficePreview && body.fileExtension
        && (body.fileExtension.toLowerCase() === "docx" || body.fileExtension.toLowerCase() === "doc"
            || body.fileExtension.toLowerCase() === "xls" || body.fileExtension.toLowerCase() === "xlsx"
            || body.fileExtension.toLowerCase() === "ppt" || body.fileExtension.toLowerCase() === "pptx"
            || body.fileExtension.toLowerCase() === "pdf" || body.fileExtension.toLowerCase() === "csv"
            || body.fileExtension.toLowerCase() === "txt")) {
      const options = {
        "mode":"view",
        "jars" : "x_message_assemble_communicate",
        "fileName" : encodeURIComponent(body.fileName),
        "url" : encodeURIComponent(o2.filterUrl(`${getImFileDownloadUrl(body.fileId)}?${o2.tokenName}=${layout.session.token}`)),
        "appId":  "OnlyOfficeEditor" + body.fileId
      }
      layout.openApplication(null, "OnlyOfficeEditor", options);
      return;
    } else if (body.fileExtension && (body.fileExtension.toLowerCase() === "mp4" || body.fileExtension.toLowerCase() === "avi" || body.fileExtension.toLowerCase() === "ogg")) {
      console.log('동영상 파일은 다운로드할 필요가 없습니다!')
      return;
    }
    window.open(getImFileDownloadUrl(body.fileId));
  } else if (body.type === "location") {
    const url = getBaiduMapUrl(body.latitude, body.longitude, body.address, body.addressDetail);
    window.open(url);
  } else if (body.type === "link") {
    if (body.linkUrl) {
      window.open(body.linkUrl);
    }
  }
}
// 이미지 미리보기 기능
let _imageViewer;
const _openImageView = (imageUrl) => {
  const imgNode = document.createElement("img");
  imgNode.src = imageUrl;
  document.body.appendChild(imgNode);
  imgNode.style.display = "none";
  o2.loadCss("../o2_lib/viewer/viewer.css", document.body, () => {
    o2.load("../o2_lib/viewer/viewer.js", ()=> {
      _imageViewer = new Viewer(imgNode,{
        navbar : false,
        toolbar : true,
        hidden : () => {
          imgNode.remove();
          _imageViewer.destroy();
        }
      });
      _imageViewer.show();
    })
  })
}
// 채팅 기록 유형의 메시지 열기
const openMsgHistory = async (msg) => {
  const container = document.querySelector('.im-container')
    // const fragment = document.createDocumentFragment()
    // const msgHistoryPageComponent = createApp(MsgHistoryPage, {
    //   msg: msg
    // }).mount(fragment)
  // 컨테이너 요소 생성
  const el = document.createElement('div');
  // 메인 앱의 context로 vnode 생성
  const vnode = h(MsgHistoryPage);
  vnode.props = {
    msg: msg
  }
  // ** 핵심 코드, 메인 앱의 appContext 상속 **
  if (appInstance) {
    vnode.appContext = appInstance;
  }
  // vnode를 el에 렌더링
  render(vnode, el);
  $OOUI.dialog(lp.msgHistory, el, container, {buttons: '', canMove: false})
}
// 설정 페이지 열기
const openImConfigPage = () => {
  const container = document.querySelector('.im-container')
  // const fragment = document.createDocumentFragment()
  // const imConfigPage = createApp(ImConfigPage, {}).mount(fragment)
  // 컨테이너 요소 생성
  const el = document.createElement('div');
  // 메인 앱의 context로 vnode 생성
  const vnode = h(ImConfigPage);
  // ** 핵심 코드, 메인 앱의 appContext 상속 **
  if (appInstance) {
    vnode.appContext = appInstance;
  }
  // vnode를 el에 렌더링
  render(vnode, el);
  $OOUI.dialog(lp.setting, el, container, {buttons: '', canMove: false})
}
// 내 즐겨찾기 페이지 열기
const openMyCollectionPage = () => {
  const container = document.querySelector('.im-container')
  // 컨테이너 요소 생성
  const el = document.createElement('div');
  // 메인 앱의 context로 vnode 생성
  const vnode = h(MyCollectionPage);
  // ** 핵심 코드, 메인 앱의 appContext 상속 **
  if (appInstance) {
    vnode.appContext = appInstance;
  }
  // vnode를 el에 렌더링
  render(vnode, el);
  $OOUI.dialog(lp.msgCollectionTitle, el, container, {buttons: '', canMove: false})
}
</script>

<template>
  <div class="im-app im-container" :class=" windowStateInstance.isMobile ? 'im-app-s' : '' ">
    <div class="im-aside" :class="{ hidden: windowStateInstance.isMobile && currentConversation, w100:  windowStateInstance.isMobile}" v-if="!hideSide" >
      <IMAside @clickImConfig="openImConfigPage" @clickMyCollectionPage="openMyCollectionPage"/>
    </div>
    <div class="im-main">
      <IMMain :conv="currentConversation"/>
    </div>
  </div>
  <ChooseConversationPage v-if="showChooseConversation"
                          @closeChooseConversation="closeChooseConversation"></ChooseConversationPage>
</template>

<style scoped></style>
