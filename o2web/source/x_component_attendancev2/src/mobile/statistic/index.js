import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import { myAction } from "../../utils/actions";
import template from './template.html';
import style from './style.scope.css';
 

export default content({
    template,
    style,
    autoUpdate: true,
    bind(){
        return {
            lp,
            year: 2023,
            month: 3,
            statistic: {}
        };
    },
    afterRender() {
        this.loadMyStatistic();
    },
    async loadMyStatistic() {
        // 현재 날짜 조회
        const today = new Date();
        // 현재 월 조회
        const currentMonth = today.getMonth();
        // 현재 연도 조회
        const currentYear = today.getFullYear();
        this.bind.year = currentYear;
        this.bind.month = (currentMonth + 1);
        // 해당 월의 일수 조회
        const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
        const startDate = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-01`;
        const endDate = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-${daysInMonth}`;
        const reqBody = {
            startDate: startDate,
            endDate: endDate,
        };
        const statistic = (await myAction("statistic", reqBody));
        if (statistic) {
            this.bind.statistic = statistic;
        }
    },
    statisticItemClass(number) {
        if (number && number > 0) {
            return "statistic-item-child";
        }
        return "statistic-item-child zero"
    }
     
});
