<template>
  <div class="settings">
    <!-- 头像 -->
    <div class="avatar-col">
      <label class="avatar-pick">
        <img v-if="imgString" :src="imgString" alt="头像" />
        <span v-else class="avatar-empty">上传头像</span>
        <input type="file" accept="image/png,image/jpeg,image/Webp" hidden @change="onPickAvatar" />
        <span class="avatar-mask">更换</span>
      </label>

      <p class="avatar-hint">支持 JPG / PNG，建议 200×200，大小不超过 2MB</p>
      <button class="avatar-reset" v-if="imgString" @click="clearAvatar">恢复默认头像</button>
    </div>

    <!-- 表单 -->
    <div class="form-col">
      <div class="form-head">
        <h3>个人资料</h3>
        <p>这些信息会展示在商品详情与交易记录里</p>
      </div>

      <label class="field">
        <span class="field-label">昵称</span>
        <input
          v-model="ruleForm.NickName"
          class="field-input"
          :class="{ err: errors.NickName }"
          placeholder="同学看到的名字"
          maxlength="20"
          @input="errors.NickName = ''"
        />
        <em v-if="errors.NickName" class="field-msg">{{ errors.NickName }}</em>
      </label>

      <label class="field">
        <span class="field-label">手机号</span>
        <input
          v-model="ruleForm.Telephone"
          class="field-input"
          :class="{ err: errors.Telephone }"
          placeholder="用于交易联系，可留空"
          maxlength="11"
          @input="errors.Telephone = ''"
        />
        <em v-if="errors.Telephone" class="field-msg">{{ errors.Telephone }}</em>
      </label>

      <label class="field">
        <span class="field-label">收货 / 自提地址</span>
        <input
          v-model="ruleForm.Address"
          class="field-input"
          placeholder="例如：集美大学诚毅学院东校园 7 栋 302"
          @input="errors.Address = ''"
        />
        <em v-if="errors.Address" class="field-msg">{{ errors.Address }}</em>
      </label>

      <div class="form-actions">
        <button class="btn-ghost" @click="resetForm">重置</button>
        <button class="btn-brand" :disabled="saving" @click="submit">
          {{ saving ? '保存中…' : '保存修改' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'settings',
  data() {
    return {
      noneBtnImg: false,
      url: '',
      imageUrl: '',
      imgString: '',
      // 原始头像（base64），用于"恢复默认"
      originAvatar: '',
      saving: false,
      errors: { NickName: '', Telephone: '', Address: '' },
      ruleForm: {
        Address: '',
        Mail: '',
        NickName: '',
        Avatar: '',
        Telephone: '',
      },
    };
  },
  mounted() {
    this.loadProfile();
  },
  methods: {
    loadProfile() {
      this.$http({ url: '/personal/getUserInfo', method: 'get' })
        .then(res => {
          const p = res.data || {};
          this.ruleForm.NickName = p.nickname || '';
          this.ruleForm.Address = p.address || '';
          this.ruleForm.Telephone = p.telephone || '';
          this.originAvatar = p.avatar ? 'data:image/Jpeg;base64,' + p.avatar : '';
          this.imgString = this.originAvatar;
          this.ruleForm.Avatar = p.avatar ? 1 : '';
        })
        .catch(() => {
          this.$emit('showmsg', { info: '资料加载失败', colorType: 3 });
        });
    },
    onPickAvatar(e) {
      const file = e.target.files && e.target.files[0];
      if (!file) return;
      if (!this.beforeAvatarUpload(file)) {
        e.target.value = '';
        return;
      }
      this.ruleForm.Avatar = 1;
      const reader = new FileReader();
      reader.onload = () => {
        this.imgString = reader.result;
      };
      reader.readAsDataURL(file);
      e.target.value = '';
    },
    clearAvatar() {
      this.imgString = this.originAvatar;
      this.ruleForm.Avatar = this.originAvatar ? 1 : '';
    },
    beforeAvatarUpload(file) {
      const okType = ['image/jpeg', 'image/jpg', 'image/png', 'image/Webp'].includes(file.type);
      const okSize = file.size / 1024 / 1024 < 2;
      if (!okType) this.$emit('showmsg', { info: '头像仅支持 JPG / PNG 格式', colorType: 3 });
      if (!okSize) this.$emit('showmsg', { info: '头像大小不能超过 2MB', colorType: 3 });
      return okType && okSize;
    },
    validate() {
      this.errors = { NickName: '', Telephone: '', Address: '' };
      const name = String(this.ruleForm.NickName || '').trim();
      const phone = String(this.ruleForm.Telephone || '').trim();
      const address = String(this.ruleForm.Address || '').trim();

      if (!name) this.errors.NickName = '请填写昵称';
      else if (name.length < 2 || name.length > 20) this.errors.NickName = '昵称长度在 2 到 20 个字符';

      if (phone && !/^1[3-9]\d{9}$/.test(phone)) this.errors.Telephone = '请输入正确的 11 位手机号';
      if (address && address.length < 3) this.errors.Address = '地址至少 3 个字符';

      return !this.errors.NickName && !this.errors.Telephone && !this.errors.Address;
    },
    resetForm() {
      this.loadProfile();
      this.errors = { NickName: '', Telephone: '', Address: '' };
    },
    submit() {
      if (!this.validate()) {
        this.$emit('showmsg', { info: '请检查表单内容', colorType: 3 });
        return;
      }

      const data = new FormData();
      data.append('address', this.ruleForm.Address);
      data.append('nickname', this.ruleForm.NickName);
      data.append('telephone', this.ruleForm.Telephone);
      data.append('avatar', this.imgString || '');

      this.saving = true;
      this.$http({
        url: '/personal/savePersonal',
        method: 'post',
        data,
      })
        .then(() => {
          this.saving = false;
          this.$emit('showmsg', { info: '保存成功，个人主页已同步', colorType: 1 });
          this.loadProfile();
          this.$emit('refresh');
        })
        .catch(() => {
          this.saving = false;
          this.$emit('showmsg', { info: '保存失败，请稍后重试', colorType: 3 });
        });
    },
  },
};
</script>

<style scoped>
.settings {
  display: grid;
  grid-template-columns: 216px minmax(0, 1fr);
  gap: 34px;
  align-items: start;
}

/* 头像 */
.avatar-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 22px 16px;
  border: 1px solid var(--cl-line);
  border-radius: 18px;
  background: var(--cl-surface);
}

.avatar-pick {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  background: #fff;
  border: 1px dashed #c8d4cf;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.18s;
}

.avatar-pick:hover {
  border-color: var(--cl-brand);
}

.avatar-pick img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-empty {
  font-size: 12px;
  color: var(--cl-muted);
}

.avatar-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(15, 27, 22, 0.5);
  color: #fff;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.18s;
}

.avatar-pick:hover .avatar-mask {
  opacity: 1;
}

.avatar-hint {
  margin-top: 14px;
  text-align: center;
  font-size: 11px;
  line-height: 1.6;
  color: var(--cl-muted);
}

.avatar-reset {
  margin-top: 10px;
  font-size: 11.5px;
  color: var(--cl-brand-strong);
}

.avatar-reset:hover {
  text-decoration: underline;
}

/* 表单 */
.form-col {
  min-width: 0;
}

.form-head h3 {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: -0.3px;
  color: var(--cl-ink);
}

.form-head p {
  margin-top: 4px;
  font-size: 12px;
  color: var(--cl-muted);
}

.field {
  display: block;
  margin-top: 18px;
}

.field-label {
  display: block;
  margin-bottom: 7px;
  font-size: 12.5px;
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

.field-msg {
  display: block;
  margin-top: 6px;
  font-size: 11.5px;
  font-style: normal;
  color: #b4503a;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 26px;
  padding-top: 18px;
  border-top: 1px solid var(--cl-line);
}

.btn-ghost,
.btn-brand {
  height: 40px;
  padding: 0 22px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.18s;
}

.btn-ghost {
  border: 1px solid #cfdad5;
  background: #fff;
  color: var(--cl-text);
}

.btn-ghost:hover {
  border-color: rgba(0, 166, 126, 0.45);
  color: var(--cl-brand-strong);
}

.btn-brand {
  background: var(--cl-brand);
  color: #fff;
}

.btn-brand:hover:not(:disabled) {
  background: var(--cl-brand-strong);
  transform: translateY(-1px);
}

.btn-brand:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

@media (max-width: 860px) {
  .settings {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  .avatar-col {
    padding: 18px 14px;
  }
}
</style>