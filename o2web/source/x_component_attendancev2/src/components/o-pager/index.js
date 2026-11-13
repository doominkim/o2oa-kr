import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import style from "./style.scope.css";
import template from "./temp.html";

export default content({
  style,
  template,
  autoUpdate: true,
  bind() {
    return {
      lp,
      pageList: ["1"],
      // pagerData: {
      //   // 부모 컴포넌트가 oo-prop으로 전달하는 객체, 주로 size, totalCount 전달 필요
      //   page: 1, //페이지 번호
      //   totalPage: 1,
      //   totalCount: content.watch(function() {
      //     console.debug("watch totalCount .....");
      //     console.debug(this.bind);
      //     this.numberCalc();
      //   },0),
      //   size: 15, // 항목 수
      // },
      inputPage: '', // 입력한 페이지 번호
    };
  },
  afterRender() {
    // todo 데이터 초기화
    this.numberCalc();
  },
  loadDataEvent() {
    this.component.dispatchEvent("refresh-data");
  },
  // 계산용, 전달 인자는 무의미, 페이지 새로고침용
  numberCalc() {
    let totalPage = parseInt(
      this.bind.totalCount / this.bind.size
    );
    const m = this.bind.totalCount % this.bind.size;
    if (m > 0) {
      totalPage += 1;
    }
    if (totalPage == 0) {
      totalPage = 1; // 최소 1
    }
    this.bind.totalPage = totalPage;
    let pageList = [];
    if (totalPage > 11) {
      // 최대 11개
      if (this.bind.page < 6) {
        for (let index = 0; index < 11; index++) {
          pageList.push(`${index + 1}`);
        }
      } else {
        const start = this.bind.page - 5;
        let end = this.bind.page + 5;
        if (end > totalPage) {
          end = totalPage;
        }
        for (let index = start; index < end + 1; index++) {
          pageList.push(`${index}`);
        }
      }
    } else {
      for (let index = 0; index < totalPage; index++) {
        pageList.push(`${index + 1}`);
      }
    }
    this.bind.pageList = pageList;
    return ""; // 사용 안 함
  },
  clickNumberPage(number) {
    this.bind.page = parseInt(number);
    this.loadDataEvent();
  },
  clickPrePage() {
    if (this.bind.page > 1) {
      this.bind.page -= 1;
    }
    this.loadDataEvent();
  },
  clickNextPage() {
    if (this.bind.page < this.bind.totalPage) {
      this.bind.page += 1;
    }
    this.loadDataEvent();
  },
  clickFirstPage() {
    this.bind.page = 1;
    this.loadDataEvent();
  },
  clickLastPage() {
    this.bind.page = this.bind.totalPage;
    this.loadDataEvent();
  },
  clickPositivePage() {
    const inPage = parseInt(this.bind.inputPage)
    if (inPage <= this.bind.totalPage && inPage > 0) {
      this.bind.page = inPage;
      this.loadDataEvent();
    } else {
      o2.api.page.notice(lp.components.errorPageNumber, 'error');
    }
  }
});
