import {component} from '@o2oa/oovm';
import {dom} from '@o2oa/util';
import template from './main.html?raw';
import password from './init/password.js';
import database from './init/database.js';
import restore from './init/restore.js';
import initInfo from './init/initInfo.js';
import execute from './init/execute.js';

import explain from './init/common/explain.js'
import {serverStatus} from "../common/action.js";
export default component({
    template,
    autoUpdate: true,
    components: {password, explain, database, restore, initInfo, execute},

    async bind() {
        const status = await serverStatus();
        const step = (status.status === 'waiting') ? 0 : 4;
        return {
            explain: [
                {
                    icon: 'password',
                    title: '관리자 비밀번호 설정',
                    textList: ['서버를 처음 시작할 때는 최고 관리자(xadmin)의 비밀번호를 설정해야 합니다.', '비밀번호는 8자 이상이어야 하며 숫자, 영문자, 특수문자(#?!@$%^&*-)를 모두 포함해야 합니다.', '이 비밀번호를 꼭 기억하세요!']
                },
                {
                    icon: 'database',
                    title: '데이터베이스 설정',
                    textList: [
                        'O2OA 플랫폼에는 H2 데이터베이스가 내장되어 있습니다. 내장형 인메모리 데이터베이스로 개발 환경이나 기능 시연 환경에 적합하며, 운영 환경에는 적합하지 않습니다.',
                        '운영 환경으로 사용하려면 성능이 더 높고 안정적인 상용 데이터베이스를 사용하는 것이 좋습니다. 예: Mysql8, Oracle12C, SQLServer 2012 등',
                        '이 서버 초기화 페이지에서 내장 H2 데이터베이스나 외부 데이터베이스를 선택할 수 있습니다.',
                        '그 밖의 데이터베이스 설정은 서버 초기화 후 "시스템 설정" 앱에서 할 수 있습니다'
                    ]
                },
                {
                    icon: 'import',
                    title: '초기 데이터',
                    textList: [
                        '다른 서버에서 내보낸 데이터 패키지를 이 페이지에서 가져와 애플리케이션을 빠르게 복원하거나 구축할 수 있습니다.',
                        '기존 서버에서 내보내기(ctl -dd 명령 또는 "시스템 설정"의 "데이터베이스 설정"에서 실행)를 하면 서버 디렉터리 "o2server/local/dump" 아래에 "dumpData_시간" 폴더가 생성됩니다. 이 폴더를 zip 파일로 압축하면 서버 초기화 시 모든 데이터를 가져올 수 있습니다',
                        '데이터 내보내기에 대한 자세한 내용: ',
                        '<a href="https://www.o2oa.net/cms/serverdeployment/256.html" style="color: #ffffff; text-indent: 0em; display: block;" target="_blank">《데이터 내보내기·가져오기와 시스템 데이터 백업》</a>',
                        '<a href="https://www.o2oa.net/cms/videoproduct/455.html" style="color: #ffffff; text-indent: 0em; display: block;" target="_blank">《시스템 설정-서비스 설정-데이터베이스 설정》</a>'
                    ]
                },
                {
                    icon: 'import',
                    title: '초기화 정보',
                    textList: [
                        '서버 초기 설정이 준비되었습니다. 초기화 정보를 확인하세요.',
                        '"실행" 버튼을 클릭하면 서버 초기화를 실행하며, 완료되면 서버가 자동으로 시작됩니다.',
                        '"취소" 버튼을 클릭하면 서버 초기화를 취소하고 초기화 서버를 종료합니다. 서버를 다시 수동으로 시작해 초기 설정을 완료할 수 있습니다.'
                    ]
                },
                {
                    icon: 'reload',
                    title: '초기화 실행',
                    textList: [
                        '서버가 초기화 작업을 실행하고 있습니다. 완료되면 서버가 자동으로 시작됩니다.',
                    ]
                },
                {
                    icon: 'cancel',
                    title: '초기화 취소',
                    textList: [
                        '서버 초기화를 취소하여 서버가 종료되었습니다. 서버를 다시 수동으로 시작해 초기 설정을 완료할 수 있습니다',
                    ]
                }
            ],
            step,
            serverStop: false
        }
    },

    afterRender(){
        window.setTimeout(()=>{
            dom.setStyles(this.titleNode, {
                top: 0,
                opacity: 1
            });
        }, 600);
        window.setTimeout(()=>{
            dom.setStyles(this.paneNode, {
                opacity: 1
            });
            dom.setStyles(this.bottomNode, {
                opacity: 1
            });
        }, 1100);

    }
});
