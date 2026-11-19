<template>
  <main class="glass-login">
    <div class="soft-studio" aria-hidden="true"></div>
    <img class="original-glass" src="/assets/login-artwork-full.png" alt="" aria-hidden="true" fetchpriority="high" />

    <section class="glass-login-card" :class="{ 'registration-card': mode !== 'login' }" aria-labelledby="auth-heading">
      <svg class="reference-brand-mark" viewBox="0 0 110 74" aria-hidden="true">
        <defs>
          <linearGradient id="glass-brand-black" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#1d2120" /><stop offset="1" stop-color="#070909" /></linearGradient>
          <linearGradient id="glass-brand-silver" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#bcc2c7" /><stop offset="1" stop-color="#939da5" /></linearGradient>
        </defs>
        <rect x="49" y="3" width="39" height="73" rx="19.5" transform="rotate(39 68.5 39.5)" fill="url(#glass-brand-silver)" />
        <rect x="13" y="3" width="39" height="73" rx="19.5" transform="rotate(39 32.5 39.5)" fill="url(#glass-brand-black)" stroke="#3b4140" stroke-width="1.5" />
      </svg>

      <template v-if="mode === 'login'">
        <header class="welcome">
          <h1 id="auth-heading" class="welcome-heading">Welcome Back</h1>
          <p class="sign-in-description">Sign in to continue to your account</p>
        </header>

        <form class="credentials" novalidate :aria-busy="busy" @submit.prevent="signIn">
          <div class="field-group">
            <label class="sr-only" for="glass-email">Email address</label>
            <div class="input-shell" :class="{ invalid: errors.account }">
              <svg class="input-icon" viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="5" width="18" height="14" rx="2" /><path d="m4 7 8 6 8-6" /></svg>
              <input id="glass-email" ref="accountInput" v-model.trim="account" name="username" type="text" inputmode="email" autocomplete="username" autocapitalize="none" spellcheck="false" placeholder="Email address" :aria-invalid="!!errors.account" :aria-describedby="errors.account ? 'account-error' : undefined" :disabled="busy" @input="errors.account = ''" />
            </div>
            <p v-if="errors.account" id="account-error" class="field-error" role="alert">{{ errors.account }}</p>
          </div>

          <div class="field-group">
            <label class="sr-only" for="glass-password">Password</label>
            <div class="input-shell" :class="{ invalid: errors.password }">
              <svg class="input-icon" viewBox="0 0 24 24" aria-hidden="true"><rect x="5" y="10" width="14" height="11" rx="2" /><path d="M8 10V7a4 4 0 0 1 8 0v3M12 14v3" /></svg>
              <input id="glass-password" ref="passwordInput" v-model="password" name="password" :type="showPassword ? 'text' : 'password'" autocomplete="current-password" placeholder="Password" :aria-invalid="!!errors.password" :aria-describedby="errors.password ? 'password-error' : undefined" :disabled="busy" @input="errors.password = ''" />
              <button class="hide-password-icon" type="button" :aria-label="showPassword ? 'Hide password' : 'Show password'" :aria-pressed="showPassword" @click="showPassword = !showPassword">
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6-10-6-10-6Z" /><circle cx="12" cy="12" r="2.5" /><path v-if="!showPassword" d="m3 3 18 18" /></svg>
              </button>
            </div>
            <p v-if="errors.password" id="password-error" class="field-error" role="alert">{{ errors.password }}</p>
          </div>

          <p v-if="feedback" class="form-feedback" :class="{ success: feedbackSuccess }" role="status">{{ feedback }}</p>
          <button class="log-in-button" type="submit" :disabled="busy"><span v-if="busy" class="loading-spinner" aria-hidden="true"></span>{{ busy ? 'Logging in…' : 'Log In' }}</button>
        </form>

        <p class="sign-up-invitation">Don't have an account? <button type="button" :disabled="busy" @click="changeMode('register')">Sign up</button></p>
      </template>

      <template v-else>
        <header class="welcome">
          <h1 id="auth-heading" class="welcome-heading">{{ mode === 'verify' ? 'Check your email' : 'Create account' }}</h1>
          <p class="sign-in-description">{{ mode === 'verify' ? `Enter the code sent to ${registration.email}` : 'A fresh start for your campus life' }}</p>
        </header>
        <form class="registration-form" novalidate :aria-busy="busy" @submit.prevent="registerAccount">
          <template v-if="mode === 'register'">
            <label class="registration-field" for="register-email">Email address
              <input id="register-email" v-model.trim="registration.email" name="email" type="email" autocomplete="email" placeholder="you@university.edu" :disabled="busy" />
            </label>
            <label class="registration-field" for="register-nickname">Nickname
              <input id="register-nickname" v-model.trim="registration.nickname" name="nickname" autocomplete="nickname" placeholder="What should we call you?" :disabled="busy" />
            </label>
            <label class="registration-field" for="register-password">Password
              <input id="register-password" v-model="registration.password" name="new-password" type="password" autocomplete="new-password" placeholder="At least 6 characters" :disabled="busy" />
            </label>
          </template>
          <label v-else class="registration-field" for="register-code">Verification code
            <input id="register-code" v-model.trim="registration.code" name="code" type="text" inputmode="numeric" autocomplete="one-time-code" placeholder="6-digit code" maxlength="6" :disabled="busy" />
          </label>
          <p v-if="feedback" class="form-feedback" role="alert">{{ feedback }}</p>
          <button class="log-in-button" type="submit" :disabled="busy"><span v-if="busy" class="loading-spinner" aria-hidden="true"></span>{{ busy ? 'Please wait…' : mode === 'verify' ? 'Create account' : 'Send verification code' }}</button>
        </form>
        <p class="sign-up-invitation">Already have an account? <button type="button" :disabled="busy" @click="changeMode('login')">Log in</button></p>
      </template>
    </section>
  </main>
</template>

<script setup>
import { nextTick, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../api/request'
import { useUserStore } from '../stores/user'

const emit = defineEmits(['showmsg'])
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
// 独立表单状态：不读取旧登录组件、历史输入或缓存中的账号密码。
const mode = ref('login')
const account = ref('')
const password = ref('')
const showPassword = ref(false)
const busy = ref(false)
const feedback = ref('')
const feedbackSuccess = ref(false)
const errors = reactive({ account: '', password: '' })
const registration = reactive({ email: '', nickname: '', password: '', code: '' })
const accountInput = ref(null)
const passwordInput = ref(null)

function changeMode(nextMode) {
  mode.value = nextMode
  feedback.value = ''
  feedbackSuccess.value = false
  errors.account = ''
  errors.password = ''
  password.value = ''
  showPassword.value = false
  registration.password = ''
  registration.code = ''
  nextTick(() => document.getElementById(nextMode === 'login' ? 'glass-email' : 'register-email')?.focus())
}

async function signIn() {
  if (busy.value) return
  feedback.value = ''
  feedbackSuccess.value = false
  errors.account = account.value ? '' : 'Please enter your email or account.'
  errors.password = password.value ? '' : 'Please enter your password.'
  if (errors.account || errors.password) {
    await nextTick()
    ;(errors.account ? accountInput.value : passwordInput.value)?.focus()
    return
  }
  busy.value = true
  try {
    const { data } = await http.post('/user/login', null, { params: { account: account.value, pwd: password.value } })
    if (!data?.token) throw new Error('Unable to sign in. Please try again.')
    userStore.setLogin(data)
    emit('showmsg', { info: '登录成功', colorType: 1 })
    const redirect = route.query.redirect
    await router.replace(typeof redirect === 'string' && redirect.startsWith('/') && !redirect.startsWith('//') && !redirect.startsWith('/login') ? redirect : '/customer')
  } catch (error) {
    feedback.value = error.response?.data?.error || error.friendly || error.message || 'Unable to sign in. Please try again.'
  } finally {
    busy.value = false
  }
}

async function registerAccount() {
  if (busy.value) return
  feedback.value = ''
  feedbackSuccess.value = false
  if (mode.value === 'register') {
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registration.email)) {
      feedback.value = 'Please enter a valid email address.'
      document.getElementById('register-email')?.focus()
      return
    }
    if (!registration.nickname) {
      feedback.value = 'Please enter your nickname.'
      document.getElementById('register-nickname')?.focus()
      return
    }
    if (registration.password.length < 6) {
      feedback.value = 'Your password must be at least 6 characters.'
      document.getElementById('register-password')?.focus()
      return
    }
  } else if (!/^\d{6}$/.test(registration.code)) {
    feedback.value = 'Please enter the 6-digit verification code.'
    document.getElementById('register-code')?.focus()
    return
  }
  busy.value = true
  try {
    if (mode.value === 'register') {
      const { data } = await http.get('/user/getCode', { params: { email: registration.email } })
      if (data?.code === 401) throw new Error(data.msg || 'Unable to send verification code.')
      mode.value = 'verify'
      await nextTick()
      document.getElementById('register-code')?.focus()
    } else {
      await http.post('/user/register', null, { params: { email: registration.email, account: registration.nickname, password: registration.password, code: registration.code } })
      changeMode('login')
      account.value = registration.email
      feedbackSuccess.value = true
      feedback.value = 'Your account is ready. Sign in to get started.'
    }
  } catch (error) {
    feedback.value = error.response?.data?.error || error.friendly || error.message || 'Something went wrong. Please try again.'
  } finally {
    busy.value = false
  }
}
</script>

<style scoped>
.glass-login {
  --unit: min(0.0390625vw, 0.0694444svh);
  --ink: #080a0a;
  --muted: #6e7580;
  --line: #d8dbe2;
  position: relative;
  isolation: isolate;
  display: flex;
  min-height: 100vh;
  min-height: 100svh;
  align-items: center;
  overflow: hidden;
  background: #edeff2;
  color: var(--ink);
  font-family: Inter, Arial, Helvetica, sans-serif;
  -webkit-font-smoothing: antialiased;
}
.glass-login,
.glass-login * { box-sizing: border-box; }
.glass-login button,
.glass-login input { font: inherit; }
.soft-studio {
  position: absolute;
  z-index: -2;
  inset: 0;
  background: radial-gradient(ellipse at 19% 32%, #f4f5f8 0%, #edf0f4 37%, transparent 70%), linear-gradient(115deg, #eef0f4, #d7e1df);
}
.original-glass {
  position: absolute;
  z-index: -1;
  top: 0;
  right: 0;
  width: 56.484375%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  mix-blend-mode: multiply;
  -webkit-mask-image: linear-gradient(to right, transparent 0%, #000 18%, #000 100%);
  mask-image: linear-gradient(to right, transparent 0%, #000 18%, #000 100%);
  user-select: none;
  pointer-events: none;
}
.glass-login-card {
  display: flex;
  width: calc(954 * var(--unit));
  min-height: calc(1154 * var(--unit));
  flex: none;
  flex-direction: column;
  margin: calc(70 * var(--unit)) 0 calc(76 * var(--unit)) calc(159 * var(--unit));
  padding: calc(115 * var(--unit)) calc(104 * var(--unit)) calc(62 * var(--unit));
  border: 2px solid #ffffffe6;
  border-radius: calc(58 * var(--unit));
  background: #ffffff78;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}
.reference-brand-mark { display: block; width: calc(110 * var(--unit)); height: calc(74 * var(--unit)); flex: none; margin-left: calc(-5 * var(--unit)); overflow: visible; }
.welcome { margin-top: calc(55 * var(--unit)); }
.welcome-heading { margin: 0; font-size: calc(68 * var(--unit)); font-weight: 600; letter-spacing: -0.035em; line-height: 1.25; }
.sign-in-description { margin: calc(8 * var(--unit)) 0 0; color: var(--muted); font-size: calc(30 * var(--unit)); font-weight: 400; line-height: 1.25; }
.credentials { display: flex; flex-direction: column; margin-top: calc(74 * var(--unit)); gap: calc(32 * var(--unit)); }
.input-shell {
  display: flex;
  width: 100%;
  height: calc(94 * var(--unit));
  min-height: 48px;
  align-items: center;
  gap: calc(31 * var(--unit));
  padding: 0 calc(31 * var(--unit));
  border: max(1px, calc(2 * var(--unit))) solid #d8dbe2b8;
  border-radius: calc(24 * var(--unit));
  background: #ffffff33;
  transition: border-color 160ms ease, box-shadow 160ms ease, background 160ms ease;
}
.input-shell:focus-within { border-color: #828c88; background: #ffffff66; box-shadow: 0 0 0 3px #3a5a4c13; }
.input-icon { width: calc(32 * var(--unit)); height: calc(32 * var(--unit)); flex: none; fill: none; stroke: #59616a; stroke-width: 1.7; stroke-linecap: round; stroke-linejoin: round; }
.input-shell input { width: 100%; min-width: 0; height: 100%; padding: 0; border: 0; outline: 0; background: transparent; color: var(--ink); font-size: calc(30 * var(--unit)); }
.input-shell input::placeholder,
.registration-field input::placeholder { color: #747c86; opacity: 1; }
.hide-password-icon { display: grid; width: 44px; height: 44px; flex: none; place-items: center; margin-right: -10px; padding: 10px; border: 0; border-radius: 8px; background: transparent; color: #7b838b; cursor: pointer; }
.hide-password-icon svg { width: calc(32 * var(--unit)); height: calc(32 * var(--unit)); fill: none; stroke: currentColor; stroke-width: 1.6; stroke-linecap: round; stroke-linejoin: round; }
.hide-password-icon:hover { color: #242c28; }
.log-in-button {
  display: flex;
  width: 100%;
  height: calc(90 * var(--unit));
  min-height: 48px;
  align-items: center;
  justify-content: center;
  padding: 0 12px;
  border: 1px solid #000;
  border-radius: calc(24 * var(--unit));
  background: #0b0c0c;
  color: #fff;
  font-size: calc(29 * var(--unit));
  font-weight: 500;
  cursor: pointer;
  transition: background 160ms ease, border-color 160ms ease, opacity 160ms ease;
}
.log-in-button { margin-top: calc(6 * var(--unit)); }
.log-in-button:hover:not(:disabled) { background: #292f2d; }
.log-in-button:active:not(:disabled) { background: #404844; }
.sign-up-invitation { display: flex; align-items: center; justify-content: center; gap: calc(16 * var(--unit)); margin: calc(42 * var(--unit)) 0 0; color: var(--muted); font-size: calc(25 * var(--unit)); line-height: 1.25; white-space: nowrap; }
.sign-up-invitation button { min-height: 44px; padding: 0; border: 0; border-radius: 4px; background: transparent; color: var(--ink); font-weight: 500; cursor: pointer; }
.sign-up-invitation button:hover:not(:disabled) { text-decoration: underline; text-underline-offset: 4px; }
.glass-login button:focus-visible { outline: 3px solid #648376; outline-offset: 4px; }
.glass-login button:disabled { opacity: 0.55; cursor: wait; }
.input-shell.invalid { border-color: #b3443d; }
.field-error { margin: 7px 0 0; color: #a3312b; font-size: max(12px, calc(23 * var(--unit))); line-height: 1.4; }
.form-feedback { margin: 0; color: #a3312b; font-size: max(13px, calc(24 * var(--unit))); line-height: 1.45; overflow-wrap: anywhere; }
.form-feedback.success { color: #2c6345; }
.registration-form { display: flex; flex-direction: column; gap: calc(25 * var(--unit)); margin-top: calc(42 * var(--unit)); }
.registration-field { display: flex; flex-direction: column; gap: 8px; color: #49515a; font-size: max(13px, calc(24 * var(--unit))); line-height: 1.4; }
.registration-field input { width: 100%; min-width: 0; height: calc(85 * var(--unit)); min-height: 46px; padding: 0 15px; border: 1px solid var(--line); border-radius: calc(20 * var(--unit)); outline: 0; background: #ffffff50; color: var(--ink); }
.registration-field input:focus { border-color: #828c88; box-shadow: 0 0 0 3px #3a5a4c13; }
.registration-card .welcome-heading { font-size: calc(59 * var(--unit)); }
.registration-card .sign-in-description { overflow-wrap: anywhere; font-size: calc(27 * var(--unit)); line-height: 1.5; }
.loading-spinner { width: 18px; height: 18px; flex: none; border: 2px solid #ffffff55; border-top-color: #fff; border-radius: 50%; animation: glass-spin 700ms linear infinite; }
.sr-only { position: absolute; width: 1px; height: 1px; padding: 0; margin: -1px; overflow: hidden; clip: rect(0, 0, 0, 0); white-space: nowrap; border: 0; }
@keyframes glass-spin { to { transform: rotate(360deg); } }

@media (max-width: 900px) {
  .glass-login { --unit: 0.55px; justify-content: center; padding: 36px 20px; }
  .original-glass { width: 100%; opacity: 0.42; object-position: 65% center; }
  .glass-login-card { width: min(100%, 480px); min-height: 0; margin: 0; padding: 46px 42px 34px; border-radius: 30px; background: #f8f9fbd9; backdrop-filter: blur(22px); -webkit-backdrop-filter: blur(22px); }
  .welcome-heading { font-size: 36px; }
  .sign-in-description { font-size: 16px; line-height: 1.5; }
  .input-shell input { font-size: 16px; }
  .credentials { margin-top: 36px; gap: 18px; }
  .sign-up-invitation { gap: 8px; font-size: 13px; }
  .registration-card .welcome-heading { font-size: 32px; }
  .registration-card .sign-in-description { font-size: 15px; }
  .registration-field { font-size: 14px; }
  .registration-field input { font-size: 16px; }
}
@media (max-width: 480px) {
  .glass-login { padding: 24px 16px; }
  .glass-login-card { padding: 34px 24px 26px; border-radius: 26px; }
  .welcome { margin-top: 28px; }
  .welcome-heading { font-size: clamp(28px, 8.5vw, 36px); }
  .sign-in-description { font-size: 14px; }
  .input-shell { gap: 13px; padding-left: 15px; padding-right: 12px; }
  .sign-up-invitation { margin-top: 24px; font-size: 12px; }
}
@media (min-width: 901px) and (max-height: 680px) {
  .glass-login { --unit: 0.5px; min-height: 680px; }
  .glass-login-card { margin-left: 6.21vw; }
}
@media (prefers-reduced-motion: reduce) {
  .glass-login *,
  .glass-login *::before,
  .glass-login *::after { animation: none !important; transition: none !important; }
}
</style>
