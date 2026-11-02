<template>
  <div class="login-box">
    <Transition :name="pageAni" mode="out-in">
      <!-- ================= 登录 ================= -->
      <div v-if="pageInx == 1" class="login-panel" key="login">
        <div class="panel-head">
          <h3>欢迎回来</h3>
          <p>登录后即可管理你的收藏、订单与闲置</p>
        </div>

        <label class="field">
          <span class="field-label">账号</span>
          <input
            v-model="account"
            class="field-input"
            :class="{ err: errorType === 'accountErr' }"
            placeholder="邮箱，或演示账号 demo"
            autocomplete="username"
            @focus="unfocus()"
          />
          <em v-if="errorType === 'accountErr' && accounterr" class="field-msg">
            {{ accounterr }}
          </em>
        </label>

        <label class="field">
          <span class="field-label">密码</span>
          <input
            :type="passwordType"
            v-model="password"
            class="field-input"
            :class="{ err: errorType === 'passwordErr' }"
            placeholder="请输入密码"
            autocomplete="current-password"
            @focus="unfocus()"
          />
          <em v-if="errorType === 'passwordErr' && passworderr" class="field-msg">
            {{ passworderr }}
          </em>
        </label>

        <button class="submit" :disabled="!permitClick" @click.stop="login_request()">
          <span v-if="regIsLoading" class="spin" aria-hidden="true"></span>
          {{ regIsLoading ? '登录中…' : '登录' }}
        </button>

        <p class="switch-line">
          还没有账号？<a @click="toRegister()">立即注册</a>
        </p>
        <p class="demo-hint">演示账号 demo / 123456 · 管理员 admin / 123456</p>
      </div>

      <!-- ================= 注册 ================= -->
      <div v-else class="login-panel" key="register">
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
          localStorage.setItem('token', res.data.token);
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
.login-box {
  width: 100%;
}

.login-panel {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.panel-head h3 {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.6px;
  color: var(--cl-ink);
}

.panel-head p {
  margin-top: 6px;
  font-size: 12.5px;
  color: var(--cl-muted);
}

.back-line {
  align-self: flex-start;
  font-size: 12px;
  color: var(--cl-muted);
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.18s;
}

.back-line:hover {
  color: var(--cl-brand-strong);
}

.field {
  display: block;
}

.field-label {
  display: block;
  margin-bottom: 6px;
  font-size: 12px;
  font-weight: 600;
  color: var(--cl-text);
}

.field-input {
  width: 100%;
  height: 44px;
  padding: 0 14px;
  border: 1px solid var(--cl-line);
  border-radius: 10px;
  background: #fff;
  font-size: 13.5px;
  color: var(--cl-ink);
  transition: border-color 0.18s, box-shadow 0.18s;
}

.field-input::placeholder {
  color: var(--cl-muted);
}

.field-input:focus {
  border-color: rgba(0, 166, 126, 0.55);
  box-shadow: 0 0 0 3px rgba(0, 166, 126, 0.1);
}

.field-input.err {
  border-color: rgba(190, 80, 60, 0.55);
  box-shadow: 0 0 0 3px rgba(190, 80, 60, 0.08);
}

/* 覆盖浏览器自动填充的浅蓝色背景 */
.field-input:-Webkit-autofill,
.field-input:-Webkit-autofill:hover,
.field-input:-Webkit-autofill:focus {
  -Webkit-box-shadow: 0 0 0 1000px #fff inset;
  -Webkit-text-fill-color: var(--cl-ink);
  transition: background-color 9999s ease-out;
}

.field-msg {
  display: block;
  margin-top: 5px;
  font-size: 11.5px;
  font-style: normal;
  color: #b4503a;
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
  height: 46px;
  margin-top: 4px;
  border-radius: 10px;
  background: var(--cl-brand);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  transition: background 0.18s, transform 0.18s, opacity 0.18s;
}

.submit:hover:not(:disabled) {
  background: var(--cl-brand-strong);
  transform: translateY(-1px);
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
  to {
    transform: rotate(360deg);
  }
}

.switch-line {
  text-align: center;
  font-size: 12.5px;
  color: var(--cl-muted);
}

.switch-line a {
  color: var(--cl-brand-strong);
  font-weight: 600;
  cursor: pointer;
}

.switch-line a:hover {
  text-decoration: underline;
}

.demo-hint {
  text-align: center;
  font-size: 11px;
  color: var(--cl-muted-2);
  letter-spacing: 0.2px;
}

/* 页面切换 */
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
</style>