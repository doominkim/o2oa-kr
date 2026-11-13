import {component} from '@o2oa/oovm';
import {Dialog} from '@o2oa/ui';
import {executeServer, stopServer} from '../../common/action.js';

const template = `
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title">초기화 정보</div>
    
    <div class="infoArea">
        <div class="line">
            <div oo-if="$.secret.passStr" class="info"><span class="icon ooicon-check"></span>관리자 비밀번호가 설정되었습니다</div>
            <div oo-if="!$.secret.passStr" class="info"><span class="icon ooicon-cancel"></span>관리자 비밀번호가 설정되지 않았습니다</div>
        </div>
        
        <div class="line">
            <div class="info"><span class="icon ooicon-check"></span>데이터베이스: {{$.database.type==='$configured' ? '초기화된 데이터베이스' : $.database.type}}</div>
        </div>
        
        <div class="line">
            <div oo-if="$.restore.name" class="info"><span class="icon ooicon-check"></span>가져올 데이터 파일: {{$.restore.name}}</div>
            <div oo-if="!$.restore.name" class="info"><span class="icon ooicon-cancel"></span>데이터 파일을 가져오지 않음</div>
        </div>
    </div>
    
    <div>
        <div oo-if="$.status==='unknown'" class="icon ooicon-error" style="color:#ffc42bde"></div>
        <div oo-if="$.status==='waiting'" class="info">서버 초기화 실행을 기다리는 중 ... </div>
    </div>
  
</div>
<div class="actions">
    <oo-button type="cancel" @click="stepPrev">이전</oo-button>
    <oo-button type="cancel" @click="stepCancel">취소</oo-button>
    <oo-button @click="nextStep">실행</oo-button>
</div>
<input type="file" @change="uploadFile" oo-element="uploadFileNode" style="display: none"/>
`;
const style = `
.info {
    display: flex;
    align-items: center;
}
.line{
    padding: 1rem;
}
.icon{
    font-size: 2rem;
    margin-right: 0.3rem;
}
.ooicon-check{
    color: green;
}
.ooicon-cancel{
    color: #999999;
}
.infoArea{
    display: flex;
    flex-direction: column;
    height: 16rem;
    justify-content: center;
    padding-left: 2rem;
}
`;
export default component({
    template,
    style,
    autoUpdate: true,

    stepPrev(){
        const step = this.$p.bind.step - 1;
        this.$p.bind.step = (step<0) ? 0 : step;
    },
    async stepCancel(e) {
        this.dialog = new Dialog(e.target.parentElement, {
            modalArea: this.$p.dom.firstElementChild,
            content: `초기화 서버를 종료하시겠습니까?`,
            width: '30em',
            position: "center",
            events: {
                'ok': async () => {
                    await stopServer();
                    this.$p.bind.step = 4;
                    this.$p.bind.serverStop = true;
                },
            }
        });
        this.dialog.show();
    },
    nextStep(e){
        this.dialog = new Dialog(e.target.parentElement, {
            modalArea: this.$p.dom.firstElementChild,
            content: `초기화 작업을 실행하시겠습니까?`,
            width: '30em',
            position: "center",
            events: {
                'ok': async () => {
                    await executeServer();
                    const step = this.$p.bind.step + 1;
                    this.$p.bind.step = (step < 5) ? step : 0;
                },
            }
        });
        this.dialog.show();
    }

});
