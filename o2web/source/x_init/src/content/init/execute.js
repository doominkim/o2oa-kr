import {component} from '@o2oa/oovm';
import {serverStatus, echoServer} from '../../common/action.js';

const template = `
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title">서버 초기화 실행</div>
    
    <div class="infoArea">
        <div oo-if="$.status==='waiting' || $.status==='running' || $.status==='starting'" class="loading" oo-element="testLoading"></div>
        <div oo-if="$.status==='success' || $.status==='started'" class="icon ooicon-check"></div>
        <div oo-if="$.status==='unknown'" class="icon ooicon-error" style="color:#ffc42bde"></div>
        
        <div oo-if="$.status==='waiting'" class="info">서버 초기화 실행을 기다리는 중 ... </div>
        <div oo-if="$.status==='running'" class="info">서버 초기화를 실행하는 중 ... </div>
        <div oo-if="$.status==='starting'" class="info">O2OA 서버를 시작하는 중 ... </div>
        
        <div oo-if="$.status==='success'" class="info">서버 초기화에 성공했습니다. 곧 O2OA 서버를 시작합니다!</div>
        <div oo-if="$.status==='started'" class="info">서버 초기화에 성공했습니다. O2OA 서버가 시작되었습니다!</div>
        <div oo-if="$.status==='started'" class="info"><oo-button style="margin-left:0" @click="gotoIndex">시스템 로그인 페이지로 이동</oo-button></div>
        
        <div oo-if="$.status==='failure' || $.status==='stop'" class="icon ooicon-cancel"></div>
        <div oo-if="$.status==='failure'" class="info">서버 초기화에 실패했습니다 </div>
        <div oo-if="$.status==='failure'" class="info" style="font-size: 0.875rem; color: red; text-align: center;">{{$.failureMessage || $.messages.join(', ')}}</div>
        <div oo-if="$.status==='failure'" class="info" style="font-size: 0.875rem; color: #666666">서버를 다시 시작한 후 초기 설정을 다시 진행할 수 있습니다!</div>
        
        <div oo-if="$.status==='unknown'" class="info">서버 상태를 가져올 수 없습니다. 서버 콘솔 정보를 확인하세요</div>
        
        <div oo-if="$.status==='stop'" class="info">초기화 서버가 종료되었습니다. 서버를 다시 수동으로 시작해 초기 설정을 완료할 수 있습니다.</div>
        
<!--        <div class="line">-->
<!--            <div oo-if="$.secret.passStr" class="info"><span class="icon ooicon-check"></span>관리자 비밀번호가 설정되었습니다</div>-->
<!--            <div oo-if="!$.secret.passStr" class="info"><span class="icon ooicon-cancel"></span>관리자 비밀번호가 설정되지 않았습니다</div>-->
<!--        </div>-->
<!--        -->
<!--        <div class="line">-->
<!--            <div class="info"><span class="icon ooicon-check"></span>데이터베이스: {{$.database.type}}</div>-->
<!--        </div>-->
<!--        -->
<!--        <div class="line">-->
<!--            <div oo-if="$.restore.name" class="info"><span class="icon ooicon-check"></span>가져올 데이터 파일: {{$.restore.name}}</div>-->
<!--            <div oo-if="!$.restore.name" class="info"><span class="icon ooicon-cancel"></span>데이터 파일을 가져오지 않음</div>-->
<!--        </div>-->
    </div>
  
</div>
`;
const style = `
.info {
    display: flex;
    align-items: center;
    margin: 0.5rem;
}
.line{
    padding: 1rem;
}
.icon{
    font-size: 4rem;
    margin: 1rem;
}
.ooicon-check{
    color: #66cc80;
}
.ooicon-cancel{
    color: red;
}
.infoArea{
    display: flex;
    flex-direction: column;
    height: 25rem;
    justify-content: center;
    padding-left: 2rem;
    align-items: center;
}
.loading {
  position: relative;
  width: 3rem;
  height: 3rem;
  border: 0.25rem solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;
  animation: circle infinite 0.75s linear;
  margin: 2rem;
}
`;
export default component({
    template,
    style,
    autoUpdate: true,

    bind(){
        return {
            status: 'success',
            messages: [],
            failureMessage: '',
            checkCount: 0
        }
    },

    async afterRender() {
       if (this.$p.bind.serverStop){
           this.bind.status = 'stop';
       }else{
           this.checkCount = 0;
           this.timeoutCheck();
       }
    },
    async timeoutCheck() {
        if (this.bind.status==='starting'){
            await this.checkServer();
        }else{
            await this.check();
        }

        if (this.checkCount>150){
            this.bind.status = 'unknown';
        }else{
            if (this.bind.status==='started'){

            }else if (this.bind.status==='failure'){

            }else{
                window.setTimeout(() => {
                    this.timeoutCheck();
                }, 2000);
            }
        }
    },
    async check() {
        const json = await serverStatus();
        this.bind.status = json.status;
        this.bind.failureMessage = json.failureMessage || '';
        this.bind.messages = json.messages || [];
        this.checkCount++;
    },
    async checkServer() {
        const json = await echoServer();
        this.bind.status = json.status;
        this.bind.failureMessage = json.failureMessage || '';
        this.bind.messages = json.messages || [];
        this.checkCount++;
    },
    gotoIndex(){
        window.location=`/?${(new Date()).getTime()}`;
    }
});
