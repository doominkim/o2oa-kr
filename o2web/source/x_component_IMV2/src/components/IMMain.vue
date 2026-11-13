<script setup>
import {ref, watch, nextTick, useTemplateRef, onMounted, onUnmounted, inject} from 'vue';
import {lp} from '@o2oa/component';
import {
  chooseSingleFile, containsUrl,
  conversationPicker, convertUrlToMarkdown,
  createContextMenu,
  formatPersonName,
  needClickMsg,
  toDate,
  ymdhms
} from '../utils/common.js';
import { marked } from "../utils/marked.esm.js";
import {imAction} from '../utils/actions.js';
import ChatMsg from './ChatMsg.vue';
import ChatInfo from './ChatInfo.vue';
import EmojiPicker from './EmojiPicker.vue';
import {contentEscapeBackToSymbol} from '../utils/escapeSymbol.js';
import {uuid} from '@o2oa/util';
import {EventName} from '../utils/eventBus.js';
import {imConfig, imGlobalOptions, uploadFileList, windowState} from '../store.js';

const imConfigInstance = imConfig();
const uploadFileListInstance = uploadFileList();
const imGlobalOptionsInstance = imGlobalOptions();
const windowStateInstance = windowState();
// eventBus
const eventBus = inject('eventBus');

const {openConversation} = inject('im-app')

const {conv} = defineProps(['conv']);

const hideSide = ref(false);

const conversation = ref(null);

const chatBodyNodeRef = useTemplateRef('chatBodyNodeRef');
// 선택 모드 여부
const chooseMode = ref(false);
// 입력한 텍스트 메시지
const inputMsg = ref('');
// 데이터 로드 중 여부
const isLoading = ref(false);
// 추가 데이터 존재 여부
const hasMoreMsgData = ref(false);
// 메시지 목록
const msgList = ref([]);
//
const msgNode = ref();
// 선택한 메시지 데이터
const msgSelectedList = ref([]);
// 메시지 페이지 조회
const page = ref(1);
// 인용 메시지
const quoteMessage = ref(null);
// 이모티콘 선택
const showEmoji = ref(false);
//
const showChatInfo = ref(false);

onMounted(() => {
  eventBus.subscribe(EventName.wsAddMsg, (msg) => {
    console.debug('wsAddMsg', msg);
    if (!conversation.value) {
      return
    }
    if (msg && msg.conversationId === conversation.value.id) {
      const index = msgList.value.findIndex((e) => e.id === msg.id);
      if (index < 0) {
        msgList.value.push(msg);
        scrollToBottom();
      }
    }
  });
  eventBus.subscribe(EventName.wsRevokeMsg, (msg) => {
    console.debug('wsRevokeMsg', msg);
    if (!conversation.value) {
      return
    }
    if (msg && msg.conversationId === conversation.value.id) {
      const index = msgList.value.findIndex((e) => e.id === msg.id);
      if (index > -1) {
        msgList.value.splice(index, 1);
      }
    }
  });
  addDragEvents();
});
onUnmounted(() => {
  eventBus.unsubscribe(EventName.wsAddMsg);
  eventBus.unsubscribe(EventName.wsRevokeMsg);
  removeDragEvents();
});

watch(
    () => conv,
    (newConversation, old) => {
      console.debug('conv change', newConversation, old);
      conversation.value = newConversation;
      initMainPage();
    },
);

const quoteDescRef = useTemplateRef('quoteDescRef');

watch(quoteMessage, async () =>  {
  await nextTick() // DOM 업데이트 완료 대기
  const links = quoteDescRef.value?.querySelectorAll('a')
  if (links && links.length) {
    links.forEach(link => {
      if (!link.hasAttribute('target')) {
        link.setAttribute('target', '_blank')
        link.setAttribute('rel', 'noopener noreferrer')
      }
    })
  }
});

// 이벤트 조합
const eventMap = {
  dragenter: (e) => unionEvents('dragenter', e),
  dragover: (e) => unionEvents('dragover', e),
  dragleave: (e) => unionEvents('dragleave', e),
  drop: (e) => unionEvents('drop', e),
  paste: (e) => unionEvents('paste', e),
};

// method
// 이벤트 바인딩 추가
const addDragEvents = () => {
  const mainNode = document.querySelector('.im-main');
  // 드래그 이벤트 바인딩, 파일을 끌어 업로드해 파일 메시지 전송
  for (let eventMapKey in eventMap) {
    mainNode.addEventListener(eventMapKey, eventMap[eventMapKey]);
  }
};
// 이벤트 바인딩 삭제
const removeDragEvents = () => {
  const mainNode = document.querySelector('.im-main');
  for (let eventMapKey in eventMap) {
    mainNode.removeEventListener(eventMapKey, eventMap[eventMapKey]);
  }
};
// 여러 이벤트 일괄 처리
const unionEvents = (eventName, e) => {
  console.debug(`unionEvents ================> ${eventName}`);
  // 기본 동작 방지(파일 열림 방지)
  if (eventName !== 'paste') {
    e.preventDefault();
    e.stopPropagation();
  }
  if (!conversation.value) {
    return;
  }
  switch (eventName) {
    case 'dragenter':
      dragEnterOverEvent(e);
      break;
    case 'dragover':
      dragEnterOverEvent(e);
      break;
    case 'dragleave':
      dragLeaveEvent(e);
      break;
    case 'drop':
      dragLeaveEvent(e);
      dragDropFileSendMsg(e);
      break;
    case 'paste':
      pasteFileSendMsg(e);
      break;
  }
};
// 드래그 진입/이탈 시 스타일 변경
const dragEnterOverEvent = () => {
  console.debug(`dragEnterOverEvent ================>`);
  const chatNode = document.querySelector('.im-chat');
  chatNode.classList.add('im-drag-style');
};
const dragLeaveEvent = () => {
  console.debug(`dragLeaveEvent ================>`);
  const chatNode = document.querySelector('.im-chat');
  chatNode.classList.remove('im-drag-style');
};
// 끌어 놓은 파일로 메시지 전송
const dragDropFileSendMsg = (e) => {
  console.debug(`dragDropFileSendMsg ================>`);
  if (e && e.dataTransfer && e.dataTransfer.files) {
    const files = e.dataTransfer.files;
    console.debug('드래그한 파일', files);
    [...files].forEach((file) => {
      if (file.type && file.type !== '') {
        _sendFileMsg(file);
      }
    });
  }
};
// 클립보드에서 파일 복사해 메시지 전송
const pasteFileSendMsg = (e) => {
  if (!e || !e.clipboardData || !e.clipboardData.items) {
    return;
  }
  console.debug(`pasteFileSendMsg ================>`);
  // 붙여넣은 내용 가져오기
  const items = e.clipboardData.items;
  // 클립보드의 모든 항목 순회
  for (let i = 0; i < items.length; i++) {
    const item = items[i];
    // 파일 유형인지 판단
    if (item.kind === 'file') {
      const file = item.getAsFile();
      if (file) {
        console.debug('붙여넣은 파일:', file);
        _sendFileMsg(file);
      }
    } else if (item.type.indexOf('image') > -1) {
      // 이미지 유형 처리, getAsFile로 Blob 객체 가져오기 가능
      const file = item.getAsFile();
      if (file) {
        console.debug('붙여넣은 이미지:', file);
        _sendFileMsg(file);
      }
    }
  }
};
// 현재 페이지 닫기
const closeMainPage = () => {
  openConversation(null)
}
// 데이터 초기화
const initMainPage = async () => {
  console.debug('imGlobalOptions', imGlobalOptionsInstance.hideSide);
  hideSide.value = imGlobalOptionsInstance.hideSide;
  msgSelectedList.value = [];
  chooseMode.value = false;
  showChatInfo.value = false;
  hasMoreMsgData.value = false;
  isLoading.value = false;
  quoteMessage.value = null;
  inputMsg.value = '';
  msgList.value = [];
  page.value = 1;
  await loadMsgByPage();
};

const loadMoreMsgData = () => {
  if (!hasMoreMsgData.value) {
    return;
  }
  page.value += 1;
  loadMsgByPage();
};
//
const loadMsgByPage = async () => {
  if (!conversation.value || !conversation.value.id) {
    console.info(' conversation 비어 있음', conversation.value);
    return;
  }
  if (isLoading.value) {
    return;
  }
  isLoading.value = true;
  const data = {conversationId: conversation.value.id};
  const list = await imAction('msgListByPaging', page.value, 20, data);
  if (list && list.length > 0) {
    const newList = list.reverse();
    if (page.value > 1) {
      msgList.value.unshift(...newList);
    } else {
      msgList.value = newList;
      // 맨 아래로 스크롤
      scrollToBottom();
    }
    hasMoreMsgData.value = list.length >= 20;
  } else {
    hasMoreMsgData.value = false;
  }
  isLoading.value = false;
};
// 대화 제목
const conversationName = (conversation) => {
  if (conversation.type === 'single') {
    let chatPerson = '';
    if (conversation.personList && conversation.personList instanceof Array) {
      for (let j = 0; j < conversation.personList.length; j++) {
        const person = conversation.personList[j];
        if (person !== layout.session.user.distinguishedName) {
          chatPerson = person;
        }
      }
      if (chatPerson) {
        return formatPersonName(chatPerson);
      }
    }
  }
  return conversation.title;
};
// 클릭해 메시지 전송
const clickSendMsg = () => {
  const text = inputMsg.value;
  if (text.trim() === '') {
    $OOUI.notice.warn(lp.alert, lp.noMessage);
    return;
  }
  const time = ymdhms(new Date());
  const body = {body: text, type: 'text'};
  const bodyJson = JSON.stringify(body);

  const textMessage = {
    id: `${uuid()}`,
    conversationId: conversation.value.id,
    body: bodyJson,
    createPerson: layout.session.user.distinguishedName,
    createTime: time,
    sendStatus: 1,
  };
  if (quoteMessage.value && quoteMessage.value.id) {
    textMessage.quoteMessageId = quoteMessage.value.id;
    textMessage.quoteMessage = quoteMessage.value;
  }
  // 화면에 추가
  msgList.value.push(textMessage);
  _sendTextMsg(textMessage);
  // 대화 업데이트
  quoteMessage.value = null;
  inputMsg.value = '';
  scrollToBottom();
};
const _sendTextMsg = async (textMessage) => {
  await _sendMsgOnline(textMessage);
  eventBus.publish(EventName.refreshMyConversation, 'sendMsg');
};
const _sendMsgOnline = async (msg) => {
  const res = await imAction('msgCreate', msg);
  if (res) {
    console.log('메시지 전송 성공', res);
  }
};
// 입력창 엔터 이벤트
const inputKeyupEvent = (e) => {
  if (e.keyCode === 13) {
    e.preventDefault();
    if (e.ctrlKey === true) {
      const text = inputMsg.value;
      inputMsg.value = text + '\n';
      document.getElementById('chatMsgNode').focus();
    } else {
      clickSendMsg();
    }
  }
};
const clickSendFile = () => {
  chooseSingleFile((file) => {
    if (file) {
      _sendFileMsg(file);
    }
  });
};
const _sendFileMsg = async (file) => {
  await uploadFileListInstance.addUploadFileAndSendMessage(file, conversation.value.id)
  // const formData = new FormData();
  // formData.append('file', file);
  // formData.append('fileName', file.name);
  // const fileExt = file.name.substring(file.name.lastIndexOf('.'));
  // // 이미지 메시지
  // let type;
  // if (fileExt.toLowerCase() === '.webp' && canUseWebP()) {
  //     type = 'image';
  // } else if (
  //     fileExt.toLowerCase() === '.bmp' ||
  //     fileExt.toLowerCase() === '.jpeg' ||
  //     fileExt.toLowerCase() === '.png' ||
  //     fileExt.toLowerCase() === '.jpg'
  // ) {
  //     type = 'image';
  // } else {
  //     // 파일 메시지
  //     type = 'file';
  // }
  // //파일 업로드
  // const res = await imUploadFile(conversation.value.id, type, formData, file);
  // console.debug(res);
  // if (res) {
  //     const fileId = res.id;
  //     const fileExtension = res.fileExtension;
  //     const fileName = res.fileName;
  //     const body = {
  //         body: type === 'image' ? lp.msgTypeImage : lp.file,
  //         type: type,
  //         fileId: fileId,
  //         fileExtension: fileExtension,
  //         fileName: fileName,
  //     };
  //     const message = {
  //         id: `${uuid()}`,
  //         conversationId: conversation.value.id,
  //         body: JSON.stringify(body),
  //         createPerson: layout.session.user.distinguishedName,
  //         createTime: ymdhms(new Date()),
  //         sendStatus: 1,
  //     };
  //     // 화면에 추가
  //     msgList.value.push(message);
  //     await _sendMsgOnline(message);
  //     eventBus.publish(EventName.refreshMyConversation, 'sendMsg');
  //     scrollToBottom();
  // }
};
// 인용 메시지 삭제
const deleteQuoteMessage = () => {
  quoteMessage.value = null;
};
// 인용 메시지 내용
const quoteMsgContent = (quoteMessage) => {
  if (!quoteMessage || !quoteMessage.body || !quoteMessage.createPerson) {
    console.error('잘못된 인용 메시지');
    return '';
  }
  const msgBody = JSON.parse(quoteMessage.body);
  let name = formatPersonName(quoteMessage.createPerson);
  name += ': ';
  let lastMessage = msgBody.body;
  if (msgBody.type === 'process') {
    let title = msgBody.title;
    if (!title) {
      title = '【' + msgBody.processName + '】- ' + lp.noTitle;
    }
    name +=  title;
  } else if (msgBody.type === 'cms') {
    name +=  msgBody.title || '';
  } else  if (msgBody.type !== 'emoji' && msgBody.type !== 'image') { // 이모티콘과 이미지는 내용을 추가할 필요 없이 name:만 필요
    let quoteDesc = contentEscapeBackToSymbol(lastMessage);
    if (containsUrl(quoteDesc)) {
      quoteDesc = marked.parse(convertUrlToMarkdown(quoteDesc))
    }
    name += quoteDesc
    if (msgBody.type === 'file') {
      name += ' ' + msgBody.fileName;
    }
  } else {
    name += lastMessage;
  }
  return name;
};
// 메시지 선택
const clickSelectMsg = (msg) => {
  const i = msgSelectedList.value.findIndex((m) => m.id === msg.id);
  if (i > -1) {
    msgSelectedList.value.splice(i, 1);
  } else {
    msgSelectedList.value.push(msg);
  }
};
// 메시지 열기
const clickOpenMsg = (msg) => {
  if (chooseMode.value) {
    clickSelectMsg(msg);
  } else {
    // 메시지 열기
    if (!needClickMsg(msg)) {
      return;
    }
    eventBus.publish(EventName.openMsg, msg);
  }
};
// 인용 메시지
const clickOpenQuoteMsg = (msg) => {
  console.debug('open quote msg  === main', msg);
  if (!chooseMode.value) {
    eventBus.publish(EventName.openMsg, msg);
  }
};
// 이모티콘 선택
const chooseEmoji = (emoji) => {
  console.log('선택한 이모티콘', emoji);
  const text = inputMsg.value;
  inputMsg.value = text + emoji;
};
const openEmojiPicker = () => {
  showEmoji.value = true;
};
const closeEmojiPicker = () => {
  showEmoji.value = false;
};
// 오른쪽 클릭 메뉴
const msgContextMenu = (msg, e) => {
  e.preventDefault();
  const menuList = []; // 메뉴 목록
  if (imConfigInstance.enableRevokeMsg === true) {
    const createPerson = msg.createPerson;
    const me = layout.session.user.distinguishedName;
    let revokeMinute = imConfigInstance.revokeOutMinute ?? 2;
    if (revokeMinute <= 0) {
      revokeMinute = 2;
    }
    let createTime = toDate(msg.createTime);
    if (revokeMinute > 0 && new Date().getTime() - createTime.getTime() < revokeMinute * 60 * 1000 && createPerson === me) {
      menuList.push({id: 'revokeMsg', text: lp.msgMenuItemRevokeMsg});
    }
  }
  // 전달
  menuList.push({id: 'forward', text: lp.msgMenuItemForwardMsg});
  // 즐겨찾기
  menuList.push({id: 'collection', text: lp.msgMenuItemCollectionMsg});
  // 선택
  menuList.push({id: 'select', text: lp.msgMenuItemSelectMsg});
  // 인용
  menuList.push({id: 'quote', text: lp.msgMenuItemQuoteMsg});
  console.debug('우클릭 메뉴', menuList);
  createContextMenu({
    callback: (menu) => {
      switch (menu.id) {
        case 'revokeMsg':
          _revokeMsg(msg);
          break;
        case 'forward':
          _forwardMsg(msg);
          break;
        case 'collection':
          _collectMsg(msg);
          break;
        case 'quote':
          _quoteMsg(msg);
          break;
        case 'select':
          _openChooseMode(msg);
          break;
      }
    },
    menuList: menuList,
    top: e.clientY,
    left: e.clientX,
  });
};
// 선택
const _openChooseMode = (msg) => {
  chooseMode.value = true;
  msgSelectedList.value = [msg];
  console.debug('선택 모드', msg);
};
// 전달
const _forwardMsg = (msg) => {
  conversationPicker(eventBus).then((conv) => {
    if (conv) {
      console.debug('전달', msg, conv);
      _forwardMsgListOnline(conv, [msg]);
    }
  });
};
// 회수
const _revokeMsg = (msg) => {
  console.debug('회수', msg);
  imAction('msgRevoke', msg.id);
  msgList.value.splice(
      msgList.value.findIndex((e) => e.id === msg.id),
      1,
  );
};
// 인용
const _quoteMsg = (msg) => {
  console.debug('인용', msg);
  quoteMessage.value = msg;
};
// 즐겨찾기
const _collectMsg = (msg) => {
  console.debug('즐겨찾기', msg);
  _collectMsgListOnline([msg]);
};
const _collectMsgListOnline = async (msgList) => {
  const body = {
    msgIdList: msgList.map((e) => e.id),
  };
  const res = await imAction('msgCollectionSave', body);
  if (res) {
    console.debug('즐겨찾기 성공', res);
    $OOUI.notice.success(lp.alert, lp.msgCollectionSuccess);
  }
};
// 메시지 전달
const _forwardMsgListOnline = async (conv, list) => {
  if (!conv || !list || list.length < 1) {
    return;
  }
  const time = ymdhms(new Date());
  for (let i = 0; i < list.length; i++) {
    let msg = list[i];
    msg.id = `${uuid()}`;
    msg.conversationId = conv.id;
    msg.createPerson = layout.session.user.distinguishedName;
    msg.createTime = time;
    await _sendMsgOnline(msg);
    if (conv.id === conversation.value.id) {
      msgList.value.push(msg);
    }
  }
  eventBus.publish(EventName.refreshMyConversation, 'sendMsg');
};
// 메시지 개별 전달
const clickOneByOneForward = () => {
  if (msgSelectedList.value.length < 1) {
    $OOUI.notice.warn(lp.alert, lp.msgNeedSelectMessage);
    return;
  }
  conversationPicker(eventBus).then((conv) => {
    if (conv) {
      console.debug('하나씩 전달', conv);
      _forwardMsgListOnline(conv, msgSelectedList.value);
      closeChooseMode();
    }
  });
};
// 메시지 병합 전달
const clickMergeForward = () => {
  if (msgSelectedList.value.length < 1) {
    $OOUI.notice.warn(lp.alert, lp.msgNeedSelectMessage);
    return;
  }
  let list = msgSelectedList.value.slice();
  // 역순
  list.sort(function (a, b) {
    return new Date(b.createTime) - new Date(a.createTime);
  });
  let descList;
  if (list.length > 4) {
    descList = list.slice(0, 4);
  } else {
    descList = list.slice();
  }
  let desc = '';
  for (let i = 0; i < descList.length; i++) {
    const msg = descList[i];
    let name = msg.createPerson;
    if (msg.createPerson.indexOf('@') !== -1) {
      name = name.substring(0, msg.createPerson.indexOf('@'));
    }
    const body = JSON.parse(msg.body);
    let content = body.body;
    if (body.type === 'text') {
      content = contentEscapeBackToSymbol(body.body);
    } else if (body.type === 'emoji') {
      content = lp.msgTypeEmoji;
    }
    desc += name + ': ' + content + '\n';
  }
  let title = '그룹 대화 기록';
  if (conversation.value.type === 'single') {
    title =
        conversation.value.personList
            .map((p) => {
              return formatPersonName(p);
            })
            .join(',') + '의 대화 기록';
  }
  const msgbody = {
    body: lp.msgTypeHistory,
    type: 'messageHistory',
    messageHistoryTitle: title,
    messageHistoryDesc: desc,
    messageHistoryIds: list.map((e) => e.id),
  };
  const message = {
    id: '',
    conversationId: conversation.value.id,
    body: JSON.stringify(msgbody),
    createPerson: layout.session.user.distinguishedName,
    createTime: '',
    sendStatus: 1,
  };
  conversationPicker(eventBus).then((conv) => {
    if (conv) {
      console.debug('묶어서 전달 merge ', conv);
      _forwardMsgListOnline(conv, [message]);
      closeChooseMode();
    }
  });
};
// 메시지 즐겨찾기
const clickCollectionMsg = () => {
  if (msgSelectedList.value.length < 1) {
    $OOUI.notice.warn(lp.alert, lp.msgNeedSelectMessage);
    return;
  }
  let list = msgSelectedList.value.slice();
  // 순서
  list.sort(function (a, b) {
    return new Date(a.createTime) - new Date(b.createTime);
  });
  _collectMsgListOnline(list);
  closeChooseMode();
};
// 선택 모드 닫기
const closeChooseMode = () => {
  chooseMode.value = false;
  msgSelectedList.value = [];
};
// 맨 아래로 스크롤
const scrollToBottom = () => {
  nextTick(() => {
    msgNode.value[msgList.value.length - 1].scrollIntoView();
  });
};

// 스크롤 이벤트, 맨 위로 스크롤 시 메시지 추가 로드
const msgListScrollEvent = () => {
  //맨 위에 도달하면 다음 데이터 로드
  const chatBodyNode = document.querySelector('#chatBodyNode');
  if (chatBodyNode.scrollTop === 0) {
    if (hasMoreMsgData.value) {
      // 추가 데이터 있음
      // 1초 간격, 빈번한 호출 방지
      setTimeout(() => {
        //다음에 맨 위에 도달할 수 있도록 scrollTop을 10으로 설정
        chatBodyNode.scrollTop = 10;
        //데이터 로드
        loadMoreMsgData();
      }, 300);
    }
  }
};

const showImChat = () => {
  if (windowStateInstance.isMobile) {
    if (conversation.value && conversation.value.id) {
      return !showChatInfo.value
    } else {
      return false
    }
  } else {
    return (conversation.value && conversation.value.id)
  }
}
</script>

<template>
  <div class="im-chat-row">
    <div class="im-chat" v-if=" showImChat() ">
      <!-- 진행 막대 -->
      <div class="im-chat-progress" v-if="uploadFileListInstance.uploadFileList.length > 0">
        <div v-for="file in uploadFileListInstance.uploadFileList" :key="file.id"
             class="im-chat-progress-bar-container">
          <div class="im-chat-progress-bar" :style=" `width: ${file.progress}%;` "></div>
          <div class="im-chat-progress-bar-label">{{ file.name }}</div>
        </div>
      </div>
      <div class="im-chat-header" v-if="!hideSide">
        <div class="im-chat-header-left">
          <div class="im-chat-header-menu" v-if="windowStateInstance.isMobile" @click="closeMainPage">
            <i class="ooicon-process-goback icon"></i>
          </div>
        </div>
        <div class="im-chat-header-center">
          {{ conversationName(conversation) }}
        </div>
        <div class="im-chat-header-right">
          <div class="im-chat-header-menu" @click="showChatInfo = true">
            <i class="ooicon-point3 icon"></i>
          </div>
        </div>
      </div>

      <div class="im-chat-body" id="chatBodyNode" ref="chatBodyNodeRef" @scroll="msgListScrollEvent">
        <div class="chat-no-more-data" v-if="!hasMoreMsgData">
          {{ lp.msgLoadNoMoreData }}
        </div>
        <div v-for="msg in msgList" :key="msg.id" ref="msgNode">
          <div class="im-chat-msg-body">
            <div class="im-chat-msg-select" v-if="chooseMode" @click="clickSelectMsg(msg)">
              <i
                  :class="
                                    msgSelectedList.findIndex((m) => m.id === msg.id) > -1
                                        ? 'ooicon-checkbox-checked mainColor_color icon'
                                        : 'ooicon-checkbox-unchecked icon'
                                "
              ></i>
            </div>
            <div class="im-chat-msg-body-content" @contextmenu="msgContextMenu(msg, $event)">
              <ChatMsg :msg="msg" @clickOpenQuoteMsg="clickOpenQuoteMsg" @clickOpenMsg="clickOpenMsg"></ChatMsg>
            </div>
          </div>
        </div>
      </div>

      <div class="im-chat-footer">
        <div class="im-chat-footer-choose" v-if="chooseMode">
          <div class="flex-3">
            <oo-button :oo-title="lp.chatChooseModeOneByOneForward" @click="clickOneByOneForward">
              {{ lp.chatChooseModeOneByOneForward }}
            </oo-button>
            <oo-button :oo-title="lp.chatChooseModeMergeForward" @click="clickMergeForward">
              {{ lp.chatChooseModeMergeForward }}
            </oo-button>
            <oo-button :oo-title="lp.chatChooseModeCollection" @click="clickCollectionMsg">
              {{ lp.chatChooseModeCollection }}
            </oo-button>
          </div>
          <div class="flex-1">
            <oo-button :oo-title="lp.cancel" type="cancel" @click="closeChooseMode">{{ lp.cancel }}</oo-button>
          </div>
        </div>
        <div class="im-chat-footer-input" v-else>
          <div class="im-chat-footer-input-tool">
            <div class="icon-btn" @click="openEmojiPicker">
              <i class="ooicon-emote icon"></i>
            </div>
            <div class="icon-btn" @click="clickSendFile">
              <i class="ooicon-folder-open icon"></i>
            </div>
          </div>
          <div class="im-chat-footer-input-text">
            <textarea id="chatMsgNode" v-model="inputMsg" :placeholder="lp.enterMessage"
                      @keydown="inputKeyupEvent"></textarea>
          </div>
          <div class="im-chat-footer-input-bottom">
            <div class="quote-msg">
              <div class="im-chat-quote-message-box" v-if="quoteMessage">
                <div class="im-chat-quote-message-desc" ref="quoteDescRef" v-html="quoteMsgContent(quoteMessage)"></div>
                <div class="im-icon-btn" @click="deleteQuoteMessage">
                  <i class="ooicon-close icon"></i>
                </div>
              </div>
            </div>
            <div class="key-tips">{{ lp.sendKeyTips }}</div>
            <div class="send-btn" @click="clickSendMsg">
              <!--            <oo-button :oo-title="lp.send" @click="clickSendMsg">{{ lp.send }}</oo-button>-->
              <i class="ooicon-send icon"></i>
            </div>
          </div>
        </div>
      </div>
      <!--    emoji 선택 -->
      <EmojiPicker v-if="showEmoji" @chooseEmoji="chooseEmoji" @closeEmojiPicker="closeEmojiPicker"></EmojiPicker>
    </div>
    <!--  채팅 정보 -->
    <ChatInfo v-if="showChatInfo && conversation" :conversation="conversation"
              @closeChatInfo="showChatInfo = false"></ChatInfo>
  </div>
</template>

<style scoped></style>
