import {component} from '@o2oa/oovm';
import {dom, cloneObject} from '@o2oa/util';
import {listDatabase, testDatabase, h2Check, h2Upgrade, h2Cancel, setDatabase, checkDatabase} from '../../common/action.js';

const template = `
<div class="pane_content" style="padding: 2rem 2rem">
    <div class="input_title" style="padding: 0.5rem 0; margin-top: 0">데이터베이스 선택</div>
    <div oo-if="$.databaseConfigured!==true && $.databaseConfigured!==false" style="padding: 3rem 2rem; text-align: center">
        <div class="icon loading" style="display: block; height: 40px; width: 40px; margin: auto"></div>
    </div>
    
    <div oo-else-if="$.databaseConfigured===true" style="padding: 3rem 2rem; text-align: center">
        <div class="icon ooicon-check"></div>
        <div style="padding: 1rem 0; color: var(--oo-color-text2)">데이터베이스 설정이 이미 초기화되었습니다! "다음"을 클릭하세요</div>
        <div style="padding: 1rem 0; font-size: 0.875rem; color: var(--oo-color-text3)">변경하려면 서버를 시작한 후 "시스템 설정"-"서버 설정"-"데이터베이스 설정"에서 변경하세요</div>
    </div>
    
    <div  oo-else style="color:#777777; font-size: 0.875rem">
        <div style="padding: 0.5em 0.3em; margin-top: 0.5rem; border: 1px solid #cccccc; border-radius: 0.5rem">
            <oo-radio-group name="database_type" col="4" oo-model="database.type" @change="changeDb" oo-element="databaseTypeNode">
                <oo-radio value="h2" name="database_type" style="margin: 0.3em 0em 0.3em 0">H2(내장 데이터베이스)</oo-radio>
                <oo-radio oo-each="$.databaseList" oo-item="db"  value="{{db.value.type}}" text="{{$.databaseName[db.value.type] || db.value.type}}" style="margin: 0.3em 0em 0.3em 0"></oo-radio>
            </oo-radio-group>
        </div>
    </div>
    <div oo-if="$.database.type!=='h2' && $.database.type!=='$configured'">
        <div class="input_title" style="padding: 0.5rem 0; margin-top: 1rem">데이터베이스 설정</div>
        <div style="color:#777777; font-size: 0.875rem">
            <div style="padding: 0.5em 0.3em; margin-top: 0.5rem; border: 1px solid #cccccc; border-radius: 0.5rem">
                <oo-textarea label="데이터베이스 연결: " style="margin-top:0.5rem; width: 98%" oo-model="externalDataSources.url" spellcheck="false"></oo-textarea>
                <div style="margin-top:1rem; margin-bottom:1rem; width: 98%; display: flex; justify-content: space-between;">
                    <oo-input label="사용자 이름: " style="width: 48%" oo-model="externalDataSources.username"></oo-input>
                    <oo-input label="비밀번호: " style="width: 48%" oo-model="externalDataSources.password"></oo-input>
                    <oo-input label="schema：" style="width: 48%" oo-model="externalDataSources.schema"></oo-input>
                </div>
                
                <div style="width: 98%; display: flex; justify-content: space-between;">
                    <div style="font-size: 0.725rem; width: calc(100% - 8.5rem); padding-left: 0.5rem; display: flex; align-items: center;">
                        <div class="loading" oo-element="testLoading"></div>
                        <div oo-if="$.testDbMessage==='success'" style="color: green">연결에 성공했습니다!</div>
                        <div oo-if="$.testDbMessage && $.testDbMessage!=='success'"  style="color: red">연결 실패: {{$.testDbMessage}}</div>
                    </div>
                    <oo-button @click="test" style="width: 8.5rem;">데이터베이스 연결 테스트</oo-button>
                </div>
               
            </div>
        </div>
    </div>
</div>
<div class="actions">
    <oo-button type="cancel" @click="stepPrev">이전</oo-button>
    <oo-button @click="nextStep">다음</oo-button>
</div>
`;
const style = `
.loading {
  position: relative;
  width: 20px;
  height: 20px;
  border: 2px solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;
  display: none;
  animation: circle infinite 0.75s linear;
}
.icon{
    font-size: 3rem;
}
.ooicon-check{
    color: green;
}
.ooicon-cancel{
    color: red;
}
`;

export default component({
    template,
    style,
    autoUpdate: true,

    bind() {
        listDatabase().then((list)=>{
            this.bind.databaseList = list;
        });
        this.databaseCheck();
        // h2Check().then((o)=>{
        //     this.bind.h2 = o;
        // });

        return {
            databaseName: {
                sqlserver: 'SQL Server',
                oracle: 'Oracle',
                postgresql: 'PostgreSQL',
                mysql: 'MySQL',
                dm: 'DM(Dameng)',
                kingbase: 'KingbaseES V7',
                kingbase8: 'KingbaseES V8',
                informix: 'Informix',
                gbase: 'GBase',
                // gbasemysql: 'GBase(MySql)',
                db2: 'DB2'
            },
            externalDataSources: {},
            testDbMessage: '',

            databaseConfigured: null,
            h2:{},
            h2_upgrade: 'no'
        }
    },
    async databaseCheck() {
        const flag = await checkDatabase();
        this.bind.databaseConfigured = flag;
    },
    changeDb(e){
        this.bind.testDbMessage = '';
        if (this.bind.database.type!=='h2'){
            const o = this.bind.databaseList.find((db)=>{
                return db.type === this.bind.database.type;
            });
            if (o) this.bind.externalDataSources = cloneObject(o.externalDataSources[0]);
        }
        this.bind.testDbMessage = '';
    },
    async test(e) {
        this.bind.testDbMessage = '';
        dom.setStyle(this.testLoading, 'display', 'block');
        e.target.setAttribute('disabled', true)
        const json = await testDatabase(this.bind.externalDataSources);
        if (json[0].success){
            this.bind.testDbMessage = 'success';
        }else{
            this.bind.testDbMessage = json[0].failureMessage;
        }
        dom.setStyle(this.testLoading, 'display', 'none');
        e.target.setAttribute('disabled', false);
    },
    stepPrev(){
        const step = this.$p.bind.step - 1;
        this.$p.bind.step = (step<0) ? 0 : step;
    },
    async nextStep() {
        if (this.bind.databaseConfigured){
            this.bind.database.type = '$configured';
        }else{
            if (this.bind.database.type !== 'h2') {
                await setDatabase(this.bind.externalDataSources);
                this.bind.database.type = this.bind.database.type;
                this.bind.database.url = this.bind.externalDataSources.url;
            } else {
                // if (this.bind.h2_upgrade === 'yes') {
                //     await h2Upgrade();
                // } else {
                //     await h2Cancel();
                // }

                this.bind.database.type = this.bind.database.type;
            }
        }

        const step = this.$p.bind.step + 1;
        this.$p.bind.step = (step < 5) ? step : 0;
    }

});
