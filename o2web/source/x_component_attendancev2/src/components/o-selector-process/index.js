import {component as content} from '@o2oa/oovm';
import {lp} from '@o2oa/component';
import style from './style.scope.css';
import template from './temp.html';

export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            showClear: false,
            label: "", // 제목 텍스트
            isRequried: false, // 빨간 별
            // selectorTitle: "", // 선택기 제목
            // value: [], // 프로세스 객체 목록
            // showValue: "", // 프로세스 객체 name
            // placeholder: "", // 데이터가 없을 때 표시할 내용
        };
    },
    afterRender() {
        this.showValueFun();
    },
    showValueFun() {
      debugger;
      if (this.bind.value.length > 0) {
        let newShowValue = [];
        for (let index = 0; index < this.bind.value.length; index++) {
          const element = this.bind.value[index];
          newShowValue.push(element["name"]||""); // name 필드
        }
        this.bind.showValue = newShowValue.join(", ");
      }
    },
    leaveIcon() {
      this.bind.showClear = false;
    },
    enterIcon() {
      this.bind.showClear = true;
    },
    clickOpenO2Selector() {
      MWF.requireApp("Selector","package", function(){
        var options = {
            "types": ["process"],
            "style":"v10",
            "count": 1,
            "expand": false,
            "title": this.bind.selectorTitle,
            "values": this.bind.value,
            "onComplete": function(items) {
              this.changeValue(items);
            }.bind(this)
        };
        new MWF.O2Selector(document.body, options)
      }.bind(this));
    },
    clickIconClearValue() {
      if (this.bind.showClear) {
        this.clearValue();
      }
    },
    clearValue() {
      this.bind.value = [];
      this.bind.showValue = "";
      // oo-model에 되돌려 쓰기
      this.component.updateModel([]);
    },
    changeValue(items) {
       let newValue = [];
       if (items) {
        items.forEach(element => {
          if (element.data) {
            newValue.push(element.data);
          }
        });
        this.bind.value = newValue;
        this.showValueFun();
        // oo-model에 되돌려 쓰기
        this.component.updateModel(newValue);
       }
    },
     
});
