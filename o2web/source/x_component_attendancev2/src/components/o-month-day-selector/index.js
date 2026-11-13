import {component as content} from '@o2oa/oovm';
import {lp, o2, component} from '@o2oa/component';
import style from './style.scope.css';
import template from './template.html';

// 월 날짜 선택 컴포넌트 
// MM-DD 형식으로 결과 반환
export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            showClear: false,
            value: "", // 최종 결과 MM-DD  
            showValue: "", // 프론트엔드 표시 시간 형식: x월x일
            key:"", // 되돌려 쓸 객체 key 값, 다단계는 .으로 구분, 예: form.startTime
            monthList: [],
            chooseMonth: '',
            dayList: [],
            chooseDay: '',
        };
    },
    // 컴포넌트 이벤트
    afterRender() {
      if (this.bind.value != '') {
        const valueArr = this.bind.value.split("-");
        this.bind.chooseMonth = parseInt(valueArr[0]);
        this.bind.chooseDay = parseInt(valueArr[1]);
      }
      this.showValueChange()
      // monthList 초기화 
      this.initMonthList();
      // dayList 초기화
      this.initDayList();
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
    initMonthList() {
      let monthList = [];
      for (let index = 1; index <= 12; index++) {
        monthList.push(index);
      }
      this.bind.monthList = monthList;
    },
    initDayList() {
      let dayList = [];
      if (this.bind.chooseMonth == 2) {// 2월 28일
        for (let index = 1; index <= 28; index++) {
          dayList.push(index);
        }
      } else {
         // 1, 3, 5, 7, 8, 10, 12월 31일
        if (this.bind.chooseMonth == 1 || this.bind.chooseMonth == 3 || this.bind.chooseMonth == 5 || this.bind.chooseMonth == 7 || this.bind.chooseMonth == 8 || this.bind.chooseMonth == 10 || this.bind.chooseMonth == 12) {
          for (let index = 1; index <= 31; index++) {
            dayList.push(index);
          }
        } else { // 기타 월 30일
          for (let index = 1; index <= 30; index++) {
            dayList.push(index);
          }
        }
      }
      this.bind.dayList = dayList;
      
    },
    // 전환
    changeChooseMonth(month) {
      this.bind.chooseMonth = month
      this.initDayList();
      this.bind.chooseDay = this.bind.dayList[0];
    },
    showValueChange() {
      const month = this.bind.chooseMonth < 10 ? `0${this.bind.chooseMonth}` : this.bind.chooseMonth;
      const dayStr = this.bind.chooseDay < 10 ? `0${this.bind.chooseDay}` : this.bind.chooseDay;
      this.bind.showValue = `${month}${lp.components.monthDaySelectorMonth}${dayStr}${lp.components.monthDaySelectorDay}`;
    },
    selected(day) {
      if (!this.bind.chooseMonth) {
        this.bind.chooseMonth = 1;
      }
      this.bind.chooseDay = day;
      const month = this.bind.chooseMonth < 10 ? `0${this.bind.chooseMonth}` : this.bind.chooseMonth;
      const dayStr = this.bind.chooseDay < 10 ? `0${this.bind.chooseDay}` : this.bind.chooseDay;
      this.bind.value = `${month}-${dayStr}`;
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
