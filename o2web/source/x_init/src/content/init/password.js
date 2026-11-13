import {component} from '@o2oa/oovm';
import {setPassword} from '../../common/action.js';
import {notice} from '../../common/notice.js';

const template = `
<div class="pane_content">
    <div class="input_title">비밀번호 설정</div>
    <oo-input oo-model="secret.passStr" type="password" left-icon="password" placeholder="관리자 비밀번호를 입력하세요" skin="icon-right:var(--oo-color-main)" right-icon="{{($.secret.passStr && $m.checkPassword($.secret.passStr)) ? 'check' : ''}}"></oo-input>
    <div style="color: red; padding-left: 1em; font-size:0.875rem; height: 1rem;"><span oo-if="!$m.checkPassword($.secret.passStr)">비밀번호는 8자 이상이어야 하며 영문자, 숫자, 특수문자(#?!@$%^&*-)를 포함해야 합니다</span></div>
    
    <div class="input_title">비밀번호 확인</div>
    <oo-input oo-model="secret.confirmPass" type="password" left-icon="password" placeholder="비밀번호를 다시 입력하세요" skin="icon-right:var(--oo-color-main)" right-icon="{{($.secret.passStr && $.secret.confirmPass && $m.checkConfirm($.secret.passStr, $.secret.confirmPass)) ? 'check' : ''}}"></oo-input>
    <div style="color: red; padding-left: 1em; font-size:0.875rem; height: 1rem;"><span oo-if="!$m.checkConfirm($.secret.passStr, $.secret.confirmPass)">비밀번호 확인이 설정한 비밀번호와 일치하지 않습니다</span></div>
</div>
<div class="actions">
    <oo-button @click="setPassword">다음</oo-button>
</div>
`;
export default component({
    template,
    autoUpdate: true,

    checkConfirm(passStr, confirmPass){
        return !passStr || !confirmPass || passStr===confirmPass;
    },
    checkPassword(str){
        // const regex = /^(?=.*[a-z])(?=.*\d).{6,30}$/;
        // return !str || regex.test(str);
        if (!str || str.length < 8) return false;
        const hasLetter = /[a-zA-Z]/.test(str);
        const hasNumber = /\d/.test(str);
        const hasSpecialChar = /[#?!@$%^&*-]/.test(str);
        return hasLetter && hasNumber && hasSpecialChar;
    },
    async setPassword() {
        if (!this.bind.secret.passStr || !this.bind.secret.confirmPass || !this.checkConfirm(this.bind.secret.passStr, this.bind.secret.confirmPass) || !this.checkPassword(this.bind.secret.passStr)) {
            notice.msg('관리자 비밀번호 설정', '관리자(xadmin)의 비밀번호를 설정해야 합니다!', 'error', {
                container: this.$p.paneNode,
                location: 'topRight'
            })
            return false;
        }
        await setPassword(this.bind.secret.passStr);
        this.bind.secret.passStr = this.bind.secret.passStr;

        const step = this.$p.bind.step + 1;
        this.$p.bind.step = (step < 5) ? step : 0;
    }

});
