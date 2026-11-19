<template>
  <div class="login-box">
    <Transition :name="pageAni" mode="out-in">
      <!-- ================= 登录 ================= -->
      <div v-if="pageInx == 1" class="login-panel login-screen" key="login">
        <div class="panel-head">
          <h1 id="login-title">Welcome Back</h1>
          <p>Sign in to continue to your account</p>
        </div>

        <label class="field">
          <svg class="field-icon" viewBox="0 0 24 24" aria-hidden="true"><rect x="3.5" y="5" width="17" height="14" rx="2" /><path d="m4.5 7 7.5 6 7.5-6" /></svg>
          <input
            v-model="account"
            class="field-input"
            aria-label="Email address"
            :class="{ err: errorType === 'accountErr' }"
            placeholder="Email address"
            autocomplete="username"
            @focus="unfocus()"
          />
          <em v-if="errorType === 'accountErr' && accounterr" class="field-msg">
            {{ accounterr }}
          </em>
        </label>

        <label class="field">
          <svg class="field-icon" viewBox="0 0 24 24" aria-hidden="true"><rect x="4.5" y="10" width="15" height="11" rx="2" /><path d="M8 10V7a4 4 0 0 1 8 0v3M12 14v3" /></svg>
          <input
            :type="passwordType"
            v-model="password"
            class="field-input password-input"
            aria-label="Password"
            :class="{ err: errorType === 'passwordErr' }"
            placeholder="Password"
            autocomplete="current-password"
            @focus="unfocus()"
          />
          <button class="password-toggle" type="button" :aria-label="passwordType === 'password' ? 'Show password' : 'Hide password'" @click="togglePassword">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M2.5 12s3.3-6 9.5-6 9.5 6 9.5 6-3.3 6-9.5 6-9.5-6-9.5-6Z" /><circle cx="12" cy="12" r="2.5" /><path v-if="passwordType === 'password'" d="m3 3 18 18" /></svg>
          </button>
          <em v-if="errorType === 'passwordErr' && passworderr" class="field-msg">
            {{ passworderr }}
          </em>
        </label>

        <button class="submit" :disabled="!permitClick" @click.stop="login_request()">
          <span v-if="regIsLoading" class="spin" aria-hidden="true"></span>
          {{ regIsLoading ? 'Logging in…' : 'Log In' }}
        </button>

        <div class="login-divider" aria-hidden="true"><span>or</span></div>

        <button class="google-login" type="button" @click="continueWithGoogle">
          <svg viewBox="0 0 48 48" aria-hidden="true">
            <path fill="#4285F4" d="M43.6 24.5c0-1.4-.1-2.8-.4-4.1H24v7.8h11a9.4 9.4 0 0 1-4.1 6.2v5.1h6.6c3.9-3.6 6.1-8.8 6.1-15Z" />
            <path fill="#34A853" d="M24 44c5.5 0 10.1-1.8 13.5-4.9l-6.6-5.1c-1.8 1.2-4.1 2-6.9 2-5.3 0-9.8-3.6-11.4-8.4H5.8v5.3A20 20 0 0 0 24 44Z" />
            <path fill="#FBBC05" d="M12.6 27.6a12 12 0 0 1 0-7.2v-5.3H5.8a20 20 0 0 0 0 17.8l6.8-5.3Z" />
            <path fill="#EA4335" d="M24 11.9c3 0 5.7 1 7.8 3.1l5.8-5.8C34.1 5.9 29.5 4 24 4A20 20 0 0 0 5.8 15.1l6.8 5.3c1.6-4.9 6.1-8.5 11.4-8.5Z" />
          </svg>
          <span>Continue with Google</span>
        </button>

        <p class="switch-line">
          Don’t have an account? <a @click="toRegister()">Sign up</a>
        </p>
      </div>

      <!-- ================= 注册 ================= -->
      <div v-else class="login-panel register-screen" key="register">
        <button class="back-line" @click="back()">
          <span aria-hidden="true">←</span> 返回登录
        </button>

        <template v-if="!isRegisterSubmit">
          <div class="panel-head">
            <h3>注册账号</h3>
            <p>使用校园邮箱注册，认证后即可发布与交易</p>
          </div>

          <label class="field">
            <span class="field-label">邮箱</span>
            <input
              v-model="account"
              class="field-input"
              :class="{ err: errorType === 'accountErr' }"
              placeholder="name@example.com"
              @focus="unfocus()"
            />
            <em v-if="errorType === 'accountErr' && accounterr" class="field-msg">{{ accounterr }}</em>
          </label>

          <label class="field">
            <span class="field-label">昵称</span>
            <input
              v-model="nickname"
              class="field-input"
              :class="{ err: errorType === 'nameErr' }"
              placeholder="同学们看到的名字"
              @focus="unfocus()"
            />
            <em v-if="errorType === 'nameErr' && namerr" class="field-msg">{{ namerr }}</em>
          </label>

          <label class="field">
            <span class="field-label">密码</span>
            <input
              :type="passwordType"
              v-model="password"
              class="field-input"
              :class="{ err: errorType === 'passwordErr' }"
              placeholder="至少 6 位"
              @focus="unfocus()"
            />
            <em v-if="errorType === 'passwordErr' && passworderr" class="field-msg">{{ passworderr }}</em>
          </label>

          <button class="submit" :disabled="!permitClick" @click.stop="register()">
            {{ regIsLoading ? '发送验证码…' : '发送验证码' }}
          </button>
        </template>

        <template v-else>
          <div class="panel-head">
            <h3>验证你的邮箱</h3>
            <p>验证码已发送至 {{ account }}</p>
          </div>

          <label class="field">
            <span class="field-label">验证码</span>
            <input
              v-model="code"
              class="field-input code-input"
              placeholder="请输入 6 位数字"
              @focus="unfocus()"
            />
            <em v-if="errormsg" class="field-msg">{{ errormsg }}</em>
          </label>

          <button class="submit" :disabled="!permitClick" @click.stop="register()">
            {{ buttonIsLoading ? '提交中…' : '完成注册' }}
          </button>
        </template>
      </div>
    </Transition>
  </div>
</template>

<script>
import { useUserStore } from '../stores/user'

export default {
  name: 'login',
  data() {
    return {
      // 当前页面：1 登录 / 2 注册
      pageInx: 1,
      account: '',
      nickname: '',
      code: '',
      passwordType: 'password',
      password: '',
      // 错误类型与提示
      errorType: '',
      errormsg: '',
      accounterr: '',
      passworderr: '',
      namerr: '',
      pageAni: 'toRe',
      // 注册验证码步骤
      isRegisterSubmit: 0,
      regIsLoading: false,
      buttonIsLoading: false,
      permitClick: true,
    };
  },
  methods: {
    togglePassword() {
      this.passwordType = this.passwordType === 'password' ? 'text' : 'password';
    },
    continueWithGoogle() {
      this.$emit('showmsg', { info: 'Google 登录暂不可用', colorType: 3 });
    },
    login_request() {
      this.unfocus();
      if (this.account === '') {
        this.errorType = 'accountErr';
        this.accounterr = '请输入账号';
        return;
      }
      if (this.password === '') {
        this.errorType = 'passwordErr';
        this.passworderr = '请输入密码';
        return;
      }

      this.regIsLoading = true;
      this.permitClick = false;
      this.$http({
        method: 'post',
        url: '/user/login',
        params: {
          account: this.account,
          pwd: this.password,
        },
      })
        .then(res => {
          this.regIsLoading = false;
          this.permitClick = true;
          // 登录态统一写入 Pinia（内部同步 token 到请求层与 localStorage）
          useUserStore().setLogin(res.data);
          this.$emit('login', res.data);
        })
        .catch(err => {
          if (err.code != 'ERR_NETWORK') {
            if (err.response && err.response.status == 500) {
              this.errorType = 'accountErr';
              this.accounterr = '账号或密码错误';
            }
          } else {
            this.errorType = 'passwordErr';
            this.passworderr = '请检查网络';
          }
          this.regIsLoading = false;
          this.permitClick = true;
        });
    },
    reflash() {
      this.errorType = '';
      this.accounterr = '';
      this.passworderr = '';
      this.account = '';
      this.password = '';
    },
    unfocus() {
      this.errorType = '';
      this.accounterr = '';
      this.passworderr = '';
      this.errormsg = '';
    },
    toRegister() {
      this.pageAni = 'toRe';
      this.pageInx = 2;
    },
    back() {
      this.pageAni = 'toFo';
      this.pageInx = 1;
      this.isRegisterSubmit = 0;
      this.unfocus();
    },
    register() {
      if (!this.isRegisterSubmit) {
        if (this.account == '') {
          this.errorType = 'accountErr';
          this.accounterr = '邮箱为空';
          return;
        }
        if (this.nickname == '') {
          this.errorType = 'nameErr';
          this.namerr = '昵称为空';
          return;
        }
        if (this.password == '') {
          this.errorType = 'passwordErr';
          this.passworderr = '密码为空';
          return;
        }

        this.regIsLoading = true;
        this.permitClick = false;
        this.$http({ method: 'get', url: '/user/getCode', params: { email: this.account } })
          .then(res => {
            this.regIsLoading = false;
            this.permitClick = true;
            const info = res.data;
            if (info && info.code == 401) {
              this.errorType = 'accountErr';
              this.accounterr = info.msg;
              this.$emit('showmsg', { info: info.msg, colorType: 3 });
            } else {
              this.isRegisterSubmit = 1;
              this.pageAni = 'toRe';
            }
          })
          .catch(() => {
            this.regIsLoading = false;
            this.permitClick = true;
            this.$emit('showmsg', { info: '网络异常', colorType: 3 });
          });
        return;
      }

      if (this.code == '') {
        this.errormsg = '请输入验证码';
        return;
      }

      this.unfocus();
      this.permitClick = false;
      this.buttonIsLoading = true;
      this.$http({
        method: 'post',
        url: '/user/register',
        params: {
          code: this.code,
          account: this.nickname,
          password: this.password,
          email: this.account,
        },
      })
        .then(() => {
          this.$emit('showmsg', { info: '注册成功，请用邮箱和密码登录', colorType: 1 });
          this.permitClick = true;
          this.buttonIsLoading = false;
          this.back();
        })
        .catch(err => {
          const data = err && err.response && err.response.data;
          this.$emit('showmsg', { info: (data && data.error) || '注册失败', colorType: 3 });
          this.buttonIsLoading = false;
          this.permitClick = true;
        });
    },
  },
  watch: {
    // 回调参数顺序为 (新值, 旧值)
    pageInx(newVal, oldVal) {
      this.pageAni = newVal < oldVal ? 'toFo' : 'toRe';
    },
  },
};
</script>

<style scoped>
.login-box,
.login-box * {
  box-sizing: border-box;
}

.login-box {
  width: 100%;
}

.login-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.panel-head {
  margin: 0 0 14px;
}

.panel-head h1,
.panel-head h3 {
  margin: 0;
  color: #111;
  font-size: 34px;
  font-weight: 650;
  line-height: 1.2;
}

.panel-head p {
  margin: 12px 0 0;
  color: #687078;
  font-size: 16px;
  line-height: 1.5;
}

.back-line {
  align-self: flex-start;
  min-height: 44px;
  padding: 0 4px;
  color: #687078;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: color 0.18s;
}

.back-line:hover {
  color: #111;
}

.field {
  position: relative;
  display: block;
}

.field-label {
  display: block;
  margin-bottom: 7px;
  color: #333a3f;
  font-size: 13px;
  font-weight: 600;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.field-icon {
  position: absolute;
  top: 16px;
  left: 17px;
  width: 21px;
  height: 21px;
  fill: none;
  stroke: #5f666b;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
  pointer-events: none;
}

.field-input {
  display: block;
  width: 100%;
  height: 56px;
  padding: 0 16px;
  border: 1px solid #d9dcdf;
  border-radius: 14px;
  outline: none;
  background: rgba(255, 255, 255, 0.3);
  color: #111;
  font-size: 16px;
  transition: border-color 0.18s, box-shadow 0.18s, background 0.18s;
}

.field:has(.field-icon) .field-input {
  padding-left: 52px;
}

.password-input {
  padding-right: 54px !important;
}

.field-input::placeholder {
  color: #737a80;
  opacity: 1;
}

.field-input:focus {
  border-color: #555e62;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(21, 44, 35, 0.08);
}

.field-input.err {
  border-color: #b4503a;
  box-shadow: 0 0 0 3px rgba(180, 80, 58, 0.08);
}

.field-input:-webkit-autofill,
.field-input:-webkit-autofill:hover,
.field-input:-webkit-autofill:focus {
  -webkit-box-shadow: 0 0 0 1000px #fff inset;
  -webkit-text-fill-color: #111;
  transition: background-color 9999s ease-out;
}

.password-toggle {
  position: absolute;
  top: 6px;
  right: 6px;
  display: grid;
  width: 44px;
  height: 44px;
  place-items: center;
  padding: 0;
  border: 0;
  border-radius: 10px;
  background: transparent;
  color: #687078;
  cursor: pointer;
}

.password-toggle svg {
  width: 20px;
  height: 20px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.password-toggle:hover {
  color: #111;
}

.field-msg {
  display: block;
  margin: 6px 2px 0;
  color: #b4503a;
  font-size: 12px;
  font-style: normal;
}

.code-input {
  letter-spacing: 3px;
}

.submit {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  min-height: 56px;
  margin-top: 2px;
  padding: 0 16px;
  border: 0;
  border-radius: 14px;
  background: #111;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  transition: background 0.18s, transform 0.18s, opacity 0.18s;
}

.submit:hover:not(:disabled) {
  background: #2b3330;
  transform: translateY(-1px);
}

.submit:focus-visible,
.password-toggle:focus-visible,
.switch-line a:focus-visible,
.back-line:focus-visible {
  outline: 3px solid rgba(41, 89, 68, 0.35);
  outline-offset: 2px;
}

.submit:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.spin {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.45);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.switch-line {
  margin: 24px 0 0;
  text-align: center;
  color: #687078;
  font-size: 14px;
}

.switch-line a {
  margin-left: 6px;
  color: #111;
  font-weight: 600;
  cursor: pointer;
}

.switch-line a:hover {
  text-decoration: underline;
}

.login-screen {
  display: flex;
  height: 100%;
  flex-direction: column;
  gap: 0;
}

.login-screen .panel-head {
  margin: 0 0 calc(36px * var(--auth-scale, 1));
}

.login-screen .panel-head h1 {
  font-size: calc(32.5px * var(--auth-scale, 1));
  font-weight: 600;
  line-height: 1.2;
}

.login-screen .panel-head p {
  margin-top: calc(4px * var(--auth-scale, 1));
  color: #687078;
  font-size: calc(15px * var(--auth-scale, 1));
  line-height: 1.5;
}

.login-screen .field + .field {
  margin-top: calc(16px * var(--auth-scale, 1));
}

.login-screen .field-icon {
  top: calc(23px * var(--auth-scale, 1));
  left: calc(17px * var(--auth-scale, 1));
  width: calc(18px * var(--auth-scale, 1));
  height: calc(18px * var(--auth-scale, 1));
  transform: translateY(-50%);
}

.login-screen .field-input {
  height: calc(46px * var(--auth-scale, 1));
  padding: 0 calc(16px * var(--auth-scale, 1)) 0 calc(48px * var(--auth-scale, 1));
  border-color: #d9dcdf;
  border-radius: calc(10px * var(--auth-scale, 1));
  background: rgba(255, 255, 255, 0.28);
  font-size: calc(15px * var(--auth-scale, 1));
}

.login-screen .field-input::placeholder {
  color: #858b90;
}

.login-screen .field:has(.field-icon) .field-input {
  padding-left: calc(48px * var(--auth-scale, 1));
}

.login-screen .password-input {
  padding-right: calc(48px * var(--auth-scale, 1)) !important;
}

.login-screen .password-toggle {
  top: 1px;
  right: calc(2px * var(--auth-scale, 1));
  width: calc(44px * var(--auth-scale, 1));
  height: calc(44px * var(--auth-scale, 1));
}

.login-screen .password-toggle svg {
  width: calc(20px * var(--auth-scale, 1));
  height: calc(20px * var(--auth-scale, 1));
}

.login-screen .submit {
  min-height: calc(45px * var(--auth-scale, 1));
  height: calc(45px * var(--auth-scale, 1));
  margin-top: calc(20px * var(--auth-scale, 1));
  border-radius: calc(11px * var(--auth-scale, 1));
  background: #050505;
  font-size: calc(14.5px * var(--auth-scale, 1));
  font-weight: 500;
}

.login-screen .submit:hover:not(:disabled) {
  background: #191919;
}

.login-divider {
  display: flex;
  align-items: center;
  gap: calc(16px * var(--auth-scale, 1));
  margin-top: calc(16px * var(--auth-scale, 1));
  color: #858b90;
  font-size: calc(13px * var(--auth-scale, 1));
  line-height: 1.4;
}

.login-divider::before,
.login-divider::after {
  height: 1px;
  flex: 1;
  content: '';
  background: #d9dcdf;
}

.google-login {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: calc(12px * var(--auth-scale, 1));
  width: 100%;
  min-height: calc(46px * var(--auth-scale, 1));
  margin-top: calc(20px * var(--auth-scale, 1));
  padding: 0 calc(16px * var(--auth-scale, 1));
  border: 1px solid #d9dcdf;
  border-radius: calc(10px * var(--auth-scale, 1));
  background: rgba(255, 255, 255, 0.18);
  color: #111;
  font-size: calc(14.5px * var(--auth-scale, 1));
  font-weight: 500;
  cursor: pointer;
  transition: background 0.18s, border-color 0.18s;
}

.google-login:hover {
  border-color: #b9bec2;
  background: rgba(255, 255, 255, 0.7);
}

.google-login:focus-visible {
  outline: 3px solid rgba(41, 89, 68, 0.35);
  outline-offset: 2px;
}

.google-login svg {
  flex: none;
  width: calc(18px * var(--auth-scale, 1));
  height: calc(18px * var(--auth-scale, 1));
}

.login-screen .switch-line {
  margin: auto 0 0;
  color: #687078;
  font-size: calc(13px * var(--auth-scale, 1));
  line-height: 1.5;
}

.login-screen .switch-line a {
  margin-left: calc(6px * var(--auth-scale, 1));
  color: #111;
  font-weight: 600;
}

.login-screen .field-msg {
  font-size: calc(12px * var(--auth-scale, 1));
}

@media (max-width: 900px) {
  .login-screen {
    height: auto;
  }

  .login-screen .panel-head h1 { font-size: 32px; }
  .login-screen .panel-head p { font-size: 15px; }
  .login-screen .field-input { height: 52px; font-size: 15px; }
  .login-screen .field-icon { top: 26px; width: 20px; height: 20px; }
  .login-screen .password-toggle { top: 4px; width: 44px; height: 44px; }
  .login-screen .submit { min-height: 52px; height: 52px; margin-top: 18px; font-size: 15px; }
  .login-screen .switch-line { margin-top: 20px; font-size: 14px; }
}

.toRe-enter-active,
.toRe-leave-active,
.toFo-enter-active,
.toFo-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.toRe-enter-from,
.toFo-leave-to {
  opacity: 0;
  transform: translateX(12px);
}

.toRe-leave-to,
.toFo-enter-from {
  opacity: 0;
  transform: translateX(-12px);
}

@media (max-width: 480px) {
  .login-panel { gap: 16px; }
  .panel-head { margin-bottom: 10px; }
  .panel-head h1,
  .panel-head h3 { font-size: 30px; }
  .panel-head p { font-size: 14px; }
  .switch-line { margin-top: 16px; }
}
</style>