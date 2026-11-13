import {component as content} from '@o2oa/oovm';
import {lp, o2, component} from '@o2oa/component';
import style from './style.scope.css';
import template from './template.html';

export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            showClear: false,
            placeholder: lp.components.timeMinutesSelectorClosePlaceholder,
            value: "", // 선택한 분 수(숫자), 예: 30
            showValue: "", // 프론트엔드 표시 시간 형식: x시간x분
            key:"", // 되돌려 쓸 객체 key 값, 다단계는 .으로 구분, 예: form.startTime
            hourList: [],
            chooseHour: '',
            minuteList: [],
            chooseMinute: '',
        };
    },
    // 컴포넌트 이벤트
    afterRender() {
      if (this.bind.value != '') {
        const v = parseInt(this.bind.value);
        let hour = parseInt(v/60);
        this.bind.chooseHour = `${hour}`;
        let y = v % 60;
        if (y == 0 && hour == 0) {
          this.bind.chooseMinute = '';
          this.bind.chooseHour = '';
        } else {
          this.bind.chooseMinute = `${y}`;
        }
      }
      this.showValueChange()
      // hourList 초기화 
      this.initHourList();
      // minuteList 초기화
      this.initMiniteList();
      this.selectorDom = this.dom.querySelector(".o-time-minutes-selctor");
      let bindDom = this.dom.querySelector(".input");
      bindDom.addEventListener('click', (event) => {
        this.selectorDom.show();
        this.addCloseEventToParent();
      });
      
    },
    selectorHide() {
        this.selectorDom.hide();
        this.$parent.dom.removeEventListener('mousedown', this.hideEvent);
    },
    // 선택기 닫기
    selectoOutHide(e) {
      // 드롭다운 범위 계산, 범위 밖 클릭 시 숨김
        const eCoor = this.selectorDom.getBoundingClientRect();
        const elementCoords =  {
          width: this.selectorDom.clientWidth,
          height: this.selectorDom.clientHeight,
          top: eCoor.top,
          bottom: eCoor.bottom,
          left: eCoor.left,
          right: eCoor.right
        }
        if(((e.pageX < elementCoords.left || e.pageX > (elementCoords.left + elementCoords.width)) ||
				(e.pageY < elementCoords.top || e.pageY > (elementCoords.top + elementCoords.height))) ) {
          this.selectorHide();
        }
    },
    // 선택기 닫기 이벤트 추가
    addCloseEventToParent() {
      this.hideEvent = this.selectoOutHide.bind(this);
      this.$parent.dom.addEventListener('mousedown', this.hideEvent );
    },
    initHourList() {
      let hourList = [];
      hourList.push('');
      for (let index = 0; index < 9; index++) {
        hourList.push(`${index}`);
      }
      this.bind.hourList = hourList;
    },
    initMiniteList() {
      let minuteList = [];
      if (this.bind.chooseHour == '') {
        minuteList.push('');
      } else {
        if (this.bind.chooseHour == '0') { // 0시간일 때는 0분 불가
          for (let index = 1; index < 60; index++) {
            minuteList.push(`${index}`);
          }
        } else { // 시간이 있을 때는 0분 가능
          for (let index = 0; index < 60; index++) {
            minuteList.push(`${index}`);
          }
        }
      }
      this.bind.minuteList = minuteList;
      
    },
    // 전환
    changeChooseHour(hour) {
      debugger;
      this.bind.chooseHour = hour
      this.initMiniteList();
      this.bind.chooseMinute = this.bind.minuteList[0];
    },
    leaveIcon() {
      this.bind.showClear = false;
    },
    enterIcon() {
      this.bind.showClear = true;
    },
    clickIconClearValue() {
      if (this.bind.showClear) {
        this.clearValue();
      }
    },
    clearValue() {
      this.bind.value = "";
      this.spitOutValue();
    },
    showValueChange() {
      const v = parseInt(this.bind.value);
      if (v < 60) {
        if (this.bind.chooseHour == '' && this.bind.chooseMinute == '' ) {
          this.bind.showValue = lp.components.timeMinutesSelectorClosePlaceholder;
        } else {
          this.bind.showValue = `${this.bind.chooseMinute}${lp.components.timeMinutesSelectorMinute}`;
        }
      } else {
        if (this.bind.chooseHour == '' && this.bind.chooseMinute == '' ) {
          this.bind.showValue = lp.components.timeMinutesSelectorClosePlaceholder;
        } else {
          this.bind.showValue = `${this.bind.chooseHour}${lp.components.timeMinutesSelectorHour}${this.bind.chooseMinute}${lp.components.timeMinutesSelectorMinute}`;
        }
      }
    },
    selected(minute) {
      this.bind.chooseMinute = minute;
      let v = 0;
      if (this.bind.chooseHour != '') {
        v += parseInt(this.bind.chooseHour) * 60;
        if (this.bind.chooseMinute != '') {
          v += parseInt(this.bind.chooseMinute);
        }
        this.bind.value = `${v}`;
      } else {
        this.bind.value = '';
      }
      // 표시
      this.showValueChange();
      this.selectorHide();
      this.spitOutValue();
    },
    spitOutValue() {
      if (this.$parent && this.$parent.setSelectorValue) {
        this.$parent.setSelectorValue(this.bind.key, this.bind.value);
      }
    }
     
});
