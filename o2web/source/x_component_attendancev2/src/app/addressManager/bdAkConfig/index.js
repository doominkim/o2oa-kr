import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import { getPublicData, putPublicData } from '../../../utils/actions';
import { isEmpty } from '../../../utils/common';
import template from './temp.html';
import oInput from '../../../components/o-input';


export default content({
    template,
    components: {oInput},
    autoUpdate: true,
    bind(){
        return {
            lp,
            mapConfig: {
                mapType: "", //  amap baidu tx
                baiduAccountKey: "",
                aMapAccountKey: "",
                txMapAccountKey: "",
            },
            mapTypeList: [
                {
                    name: "없음",
                    key: ""
                },{
                    name: "Baidu 지도",
                    key: "baidu"
                },
                {
                    name: "AMap 지도",
                    key: "amap"
                },
                {
                    name: "Tencent 지도",
                    key: "tx"
                }
            ]
        };
    },
    close() {
        this.$topParent.publishEvent('address', {});
        this.$parent.closeFormVm();
    },
    chooseMapType(e) {
        const value = e.target.value;
        this.bind.mapConfig.mapType = value;
        console.debug("설정 유형: " + value);
    },
    async submitAdd() {
        debugger;
        if (this.bind.mapConfig.mapType === "baidu" && isEmpty(this.bind.mapConfig.baiduAccountKey)) {
            o2.api.page.notice(lp.workAddressBDSecretTitlePlaceholder, 'error');
            return ;
        }
        if (this.bind.mapConfig.mapType === "amap" && isEmpty(this.bind.mapConfig.aMapAccountKey)) {
            o2.api.page.notice(lp.workAddressBDSecretTitlePlaceholder, 'error');
            return ;
        }
        if (this.bind.mapConfig.mapType === "tx" && isEmpty(this.bind.mapConfig.txMapAccountKey)) {
            o2.api.page.notice(lp.workAddressBDSecretTitlePlaceholder, 'error');
            return ;
        }
        const bdKey = await putPublicData("attendanceMapConfig", this.bind.mapConfig);
        console.debug('설정했습니다', bdKey);
        this.close();
    },
   
});
