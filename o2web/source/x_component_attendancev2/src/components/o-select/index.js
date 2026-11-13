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
            result: {}, // 선택된 값
            showValue: "", // 표시
            itemList: [], // 드롭다운 목록, 객체 포함
            itemValueKey: "value", // 옵션 저장 값 필드명
            itemNameKey: "name", // 옵션 표시 이름 필드명
        };
    },
    // 컴포넌트 이벤트
    afterRender() {
      this.selectorDom = this.dom.querySelector(".o-select-list");
      let bindDom = this.dom.querySelector(".input");
      bindDom.addEventListener('click', (event) => {
        this.selectorDom.show();
        this.addCloseEventToParent();
      });
      this.bind.showValue = this.showValueFun();
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
    // 표시 내용
    showValueFun() {
      if (this.bind.result && this.bind.result[this.bind.itemNameKey]) {
        return this.bind.result[this.bind.itemNameKey];
      }
      return lp.components.selectPlaceholder
    },
    // 선택
    changeSelect(item) {
      this.bind.result = item;
      this.bind.showValue = this.showValueFun();
      this.selectorHide();
      this.component.updateModel(this.bind.result);
    },
    clearValue() {
      this.bind.result = {};
      this.bind.showValue = this.showValueFun();
      this.component.updateModel({});
    },     
     
});
