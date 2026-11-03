<template>
  <div class="publish">
    <!-- 步骤条 -->
    <ol class="steps">
      <li
        v-for="(s, i) in steps"
        :key="s"
        class="step"
        :class="{ on: active === i, done: active > i }"
      >
        <span class="step-no">
          <span v-if="active > i" class="step-check">✓</span>
          <template v-else>{{ i + 1 }}</template>
        </span>
        <span class="step-name">{{ s }}</span>
      </li>
    </ol>

    <Transition name="fade" mode="out-in">
      <!-- ============ 1 商品信息 ============ -->
      <div v-if="active === 0" class="panel" key="info">
        <div class="grid">
          <label class="field">
            <span class="label">商品名称</span>
            <input
              v-model="ruleForm.TradeName"
              class="input"
              :class="{ err: errors.TradeName }"
              maxlength="30"
              placeholder="例如：高等数学 同济第七版"
              @input="errors.TradeName = ''"
            />
            <em v-if="errors.TradeName" class="msg">{{ errors.TradeName }}</em>
          </label>

          <label class="field">
            <span class="label">商品分类</span>
            <div class="select-wrap">
              <select
                v-model="ruleForm.Category"
                class="input select"
                :class="{ err: errors.Category }"
                @change="errors.Category = ''"
              >
                <option value="" disabled>请选择分类</option>
                <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
              </select>
              <span class="select-arrow" aria-hidden="true"></span>
            </div>
            <em v-if="errors.Category" class="msg">{{ errors.Category }}</em>
          </label>

          <label class="field">
            <span class="label">预售价格</span>
            <div class="price-wrap">
              <span class="price-unit">¥</span>
              <input
                v-model="ruleForm.Price"
                class="input price-input"
                :class="{ err: errors.Price }"
                placeholder="0.00"
                @input="errors.Price = ''"
              />
            </div>
            <em v-if="errors.Price" class="msg">{{ errors.Price }}</em>
          </label>

          <label class="field">
            <span class="label">交易地址</span>
            <input
              v-model="ruleForm.ShippingAddress"
              class="input"
              :class="{ err: errors.ShippingAddress }"
              placeholder="例如：集美大学诚毅学院东校园 7 栋"
              @input="errors.ShippingAddress = ''"
            />
            <em v-if="errors.ShippingAddress" class="msg">{{ errors.ShippingAddress }}</em>
          </label>

          <label class="field span-2">
            <span class="label">商品简介</span>
            <textarea
              v-model="ruleForm.Description"
              class="input textarea"
              :class="{ err: errors.Description }"
              rows="4"
              maxlength="300"
              placeholder="写清楚成色、使用时长、配件是否齐全，成交更快"
              @input="errors.Description = ''"
            ></textarea>
            <em v-if="errors.Description" class="msg">{{ errors.Description }}</em>
          </label>
        </div>

        <div class="cover-row">
          <div>
            <p class="label">商品封面图</p>
            <label class="cover-pick">
              <img v-if="imgString" :src="imgString" alt="封面" />
              <span v-else class="cover-empty">
                <i class="plus">+</i>
                <span>上传封面</span>
              </span>
              <input type="file" accept="image/png,image/jpeg,image/Webp" hidden @change="onPickCover" />
            </label>
            <em v-if="errors.FirstPhoto" class="msg">{{ errors.FirstPhoto }}</em>
          </div>
          <p class="hint">
            建议 1:1 正方形、主体清晰。<br />
            支持 JPG / PNG，单张不超过 2MB。
          </p>
        </div>

        <div class="actions">
          <button class="btn-brand" @click="nextFromInfo">下一步</button>
        </div>
      </div>

      <!-- ============ 2 轮播图 ============ -->
      <div v-else-if="active === 1" class="panel" key="photos">
        <div class="panel-head">
          <h3>商品轮播图</h3>
          <p>最多 6 张，第一张会作为商品主图展示</p>
        </div>

        <div class="slides">
          <div v-for="(img, i) in slides" :key="i" class="slide">
            <img :src="img" alt="" />
            <button class="slide-del" @click="removeSlide(i)" aria-label="删除">×</button>
          </div>

          <label v-if="slides.length < 6" class="slide-add">
            <i class="plus">+</i>
            <span>{{ slides.length ? '继续添加' : '添加图片' }}</span>
            <input type="file" accept="image/png,image/jpeg,image/Webp" multiple hidden @change="onPickSlides" />
          </label>
        </div>

        <p v-if="!slides.length" class="hint">至少上传 1 张轮播图，多角度展示更容易卖出</p>

        <div class="actions">
          <button class="btn-ghost" @click="prev">上一步</button>
          <button class="btn-brand" @click="nextFromPhotos">下一步</button>
        </div>
      </div>

      <!-- ============ 3 确认提交 ============ -->
      <div v-else-if="active === 2" class="panel" key="confirm">
        <div class="panel-head">
          <h3>{{ isEdit ? '确认修改内容' : '确认发布信息' }}</h3>
          <p>{{ isEdit ? '保存后商品会重新进入审核队列，审核通过才恢复展示' : '提交后进入管理员审核，审核通过即在商品市场展示' }}</p>
        </div>

        <div class="confirm">
          <div class="confirm-cover">
            <img v-if="imgString" :src="imgString" alt="封面" />
            <span v-else class="cover-empty">未上传封面</span>
          </div>

          <div class="confirm-info">
            <p class="confirm-name">{{ ruleForm.TradeName }}</p>
            <p class="confirm-price">¥{{ ruleForm.Price }}</p>

            <div class="confirm-rows">
              <div class="confirm-row"><span>分类</span>{{ ruleForm.Category }}</div>
              <div class="confirm-row"><span>地址</span>{{ ruleForm.ShippingAddress }}</div>
              <div class="confirm-row"><span>简介</span>{{ ruleForm.Description }}</div>
              <div class="confirm-row"><span>轮播图</span>{{ slides.length }} 张</div>
            </div>
          </div>
        </div>

        <div v-if="slides.length" class="confirm-slides">
          <img v-for="(img, i) in slides" :key="i" :src="img" alt="" />
        </div>

        <div class="actions">
          <button class="btn-ghost" @click="prev">上一步</button>
          <button class="btn-brand" :disabled="submitting" @click="submit">
            {{ submitting ? '提交中…' : isEdit ? '保存修改' : '确认发布' }}
          </button>
        </div>
      </div>

      <!-- ============ 4 提交成功 ============ -->
      <div v-else class="panel done" key="done">
        <span class="done-check">✓</span>
        <h3>{{ isEdit ? '已保存，等待重新审核' : '已提交，等待审核' }}</h3>
        <p v-if="isEdit">修改后的商品会重新进入审核队列，审核通过后在商品市场恢复展示。</p>
        <p v-else>管理员审核通过后，商品会出现在「商品市场」与「我的闲置 · 在售」中。</p>
        <div class="actions center">
          <button v-if="!isEdit" class="btn-ghost" @click="resetAll">再发布一件</button>
          <button class="btn-brand" @click="finish">{{ isEdit ? '返回我的闲置' : '查看我的闲置' }}</button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
export default {
  name: 'publish',
  props: {
    userid: { type: [Number, String], default: 0 },
    // 编辑模式：传入商品 id 时回填表单并调用更新接口
    editId: { type: [Number, String], default: 0 },
  },
  data() {
    return {
      steps: ['商品信息', '上传展示图片', '确认提交'],
      active: 0,
      categories: [],
      imgString: '',
      slides: [],
      submitting: false,
      loadingEdit: false,
      ruleForm: {
        TradeName: '',
        Price: '',
        Description: '',
        ShippingAddress: '',
        Category: '',
        FirstPhoto: '',
      },
      errors: {
        TradeName: '',
        Price: '',
        Description: '',
        ShippingAddress: '',
        Category: '',
        FirstPhoto: '',
      },
    };
  },
  computed: {
    isEdit() {
      return Number(this.editId) > 0;
    },
  },
  mounted() {
    this.getCategories();
    if (this.isEdit) this.loadForEdit();
  },
  methods: {
    // 编辑模式：回填商品信息与图片
    loadForEdit() {
      this.loadingEdit = true;
      this.$http({ method: 'post', url: '/commodity/queryCommodity', params: { commodityId: Number(this.editId) } })
        .then(res => {
          const info = res.data || {};
          this.ruleForm.TradeName = info.tradeName || '';
          this.ruleForm.Price = info.price != undefined ? String(info.price) : '';
          this.ruleForm.Category = info.category || '';
          this.ruleForm.Description = info.description || '';
          this.ruleForm.ShippingAddress = info.shippingAddress || '';
          const list = Array.isArray(info.slideShow) ? info.slideShow : [];
          if (list.length) {
            const pre = 'data:image/Jpeg;base64,';
            this.ruleForm.FirstPhoto = 1;
            this.imgString = pre + list[0];
            this.slides = list.map(b => pre + b);
          }
          this.loadingEdit = false;
        })
        .catch(err => {
          this.loadingEdit = false;
          this.$emit('showmsg', { info: (err && err.friendly) || '商品信息加载失败', colorType: 3 });
        });
    },
    getCategories() {
      this.$http({ url: '/category/getAllCategories', method: 'get' })
        .then(res => {
          const list = Array.isArray(res.data) ? res.data : [];
          this.categories = list
            .map(c => (typeof c === 'string' ? c : c.name || c.categoryName || ''))
            .filter(Boolean);
        })
        .catch(() => {
          this.$emit('showmsg', { info: '分类加载失败', colorType: 3 });
        });
    },

    clearErrors() {
      Object.keys(this.errors).forEach(k => {
        this.errors[k] = '';
      });
    },

    validateInfo() {
      this.clearErrors();
      const f = this.ruleForm;
      let ok = true;

      if (!String(f.TradeName).trim()) {
        this.errors.TradeName = '请填写商品名称';
        ok = false;
      } else if (String(f.TradeName).trim().length > 30) {
        this.errors.TradeName = '商品名称不超过 30 个字符';
        ok = false;
      }

      if (!f.Category) {
        this.errors.Category = '请选择商品分类';
        ok = false;
      }

      const price = String(f.Price).trim();
      if (!price) {
        this.errors.Price = '请填写价格';
        ok = false;
      } else if (!/^\d+(\.\d{1,2})?$/.test(price) || Number(price) <= 0) {
        this.errors.Price = '请输入大于 0 的数字，最多两位小数';
        ok = false;
      }

      if (String(f.ShippingAddress).trim().length < 3) {
        this.errors.ShippingAddress = '请填写交易地址（至少 3 个字）';
        ok = false;
      }

      if (String(f.Description).trim().length < 3) {
        this.errors.Description = '商品简介至少 3 个字';
        ok = false;
      }

      if (!this.imgString) {
        this.errors.FirstPhoto = '请上传商品封面图';
        ok = false;
      }

      return ok;
    },

    nextFromInfo() {
      if (!this.validateInfo()) {
        this.$emit('showmsg', { info: '请检查商品信息', colorType: 3 });
        return;
      }
      this.active = 1;
    },
    nextFromPhotos() {
      if (!this.slides.length) {
        this.$emit('showmsg', { info: '请至少上传 1 张轮播图', colorType: 3 });
        return;
      }
      this.active = 2;
    },
    prev() {
      if (this.active > 0) this.active -= 1;
    },

    readFile(file) {
      return new Promise((resolve, reject) => {
        if (!this.checkFile(file)) {
          reject(new Error('invalid'));
          return;
        }
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result);
        reader.onerror = reject;
        reader.readAsDataURL(file);
      });
    },
    checkFile(file) {
      const okType = ['image/jpeg', 'image/jpg', 'image/png', 'image/Webp'].includes(file.type);
      const okSize = file.size / 1024 / 1024 < 2;
      if (!okType) this.$emit('showmsg', { info: '仅支持 JPG / PNG / WebP 图片', colorType: 3 });
      if (!okSize) this.$emit('showmsg', { info: '单张图片不能超过 2MB', colorType: 3 });
      return okType && okSize;
    },
    onPickCover(e) {
      const file = e.target.files && e.target.files[0];
      if (!file) return;
      this.readFile(file)
        .then(base64 => {
          this.imgString = base64;
          this.ruleForm.FirstPhoto = 1;
          this.errors.FirstPhoto = '';
        })
        .catch(() => {})
        .finally(() => {
          e.target.value = '';
        });
    },
    onPickSlides(e) {
      const files = Array.from(e.target.files || []);
      if (!files.length) return;
      const room = 6 - this.slides.length;
      const picked = files.slice(0, Math.max(0, room));
      if (files.length > room) {
        this.$emit('showmsg', { info: `最多 6 张，已自动保留前 ${room} 张`, colorType: 3 });
      }
      Promise.all(picked.map(f => this.readFile(f).catch(() => null)))
        .then(list => {
          this.slides = this.slides.concat(list.filter(Boolean)).slice(0, 6);
        })
        .finally(() => {
          e.target.value = '';
        });
    },
    removeSlide(i) {
      this.slides.splice(i, 1);
    },

    submit() {
      if (this.active !== 2 || this.submitting) return;
      if (!this.validateInfo()) {
        this.active = 0;
        return;
      }

      // 编辑模式：调用更新接口，保存后重新进入审核
      if (this.isEdit) {
        const data = new FormData();
        data.append('commodityId', Number(this.editId));
        data.append('tradeName', this.ruleForm.TradeName);
        data.append('price', this.ruleForm.Price);
        data.append('firstPhoto', this.imgString);
        data.append('shippingAddress', this.ruleForm.ShippingAddress);
        data.append('description', this.ruleForm.Description);
        data.append('category', this.ruleForm.Category);
        // 封面已单独传，轮播图里去掉重复的封面
        const slides = this.slides.filter(s => s !== this.imgString);
        data.append('slideShow', JSON.stringify(slides));

        this.submitting = true;
        this.$http({ method: 'post', url: '/commodity/updateCommodityInfo', data })
          .then(() => {
            this.submitting = false;
            this.active = 3;
            this.$emit('showmsg', { info: '修改已保存，等待重新审核', colorType: 1 });
            this.$emit('refresh');
          })
          .catch(err => {
            this.submitting = false;
            this.$emit('showmsg', { info: (err && err.friendly) || '保存失败，请稍后重试', colorType: 3 });
          });
        return;
      }

      const data = new FormData();
      data.append('tradeName', this.ruleForm.TradeName);
      data.append('price', this.ruleForm.Price);
      data.append('firstPhoto', this.imgString);
      data.append('shippingAddress', this.ruleForm.ShippingAddress);
      data.append('description', this.ruleForm.Description);
      data.append('category', this.ruleForm.Category);
      data.append('sellerId', 12);

      this.submitting = true;
      this.$http({ method: 'post', url: '/commodity/launchCommodityInfo', data })
        .then(res => {
          const commodityId = res.data && res.data.commodityId;
          return this.$http({
            method: 'post',
            url: '/commodity/saveSlideShow',
            data: { base64: this.slides, id: 12 },
            params: { commodityId },
          });
        })
        .then(() => {
          this.submitting = false;
          this.active = 3;
          this.$emit('showmsg', { info: '提交成功，已进入管理员审核队列', colorType: 1 });
          this.$emit('refresh');
        })
        .catch(() => {
          this.submitting = false;
          this.$emit('showmsg', { info: '发布失败，请稍后重试', colorType: 3 });
        });
    },
    resetAll() {
      this.active = 0;
      this.imgString = '';
      this.slides = [];
      this.ruleForm = {
        TradeName: '',
        Price: '',
        Description: '',
        ShippingAddress: '',
        Category: '',
        FirstPhoto: '',
      };
      this.clearErrors();
    },
    finish() {
      this.resetAll();
      this.$emit('done');
    },
  },
};
</script>

<style scoped>
.publish {
  width: 100%;
}

/* 步骤条 */
.steps {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 20px;
  margin-bottom: 22px;
  border-bottom: 1px solid var(--cl-line);
}

.step {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.step + .step::before {
  content: '';
  height: 1px;
  flex: 0 0 20px;
  background: var(--cl-line);
}

.step-no {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  border: 1px solid var(--cl-line);
  background: #fff;
  color: var(--cl-muted);
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s;
}

.step-name {
  font-size: 12.5px;
  color: var(--cl-muted);
  white-space: nowrap;
}

.step.on .step-no {
  background: var(--cl-brand);
  border-color: var(--cl-brand);
  color: #fff;
}

.step.on .step-name {
  color: var(--cl-ink);
  font-weight: 600;
}

.step.done .step-no {
  background: var(--cl-brand-soft);
  border-color: rgba(0, 166, 126, 0.35);
  color: var(--cl-brand-strong);
}

.step.done .step-name {
  color: var(--cl-text);
}

.step-check {
  font-size: 12px;
}

/* 表单 */
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px 18px;
}

.field {
  display: block;
  min-width: 0;
}

.span-2 {
  grid-column: 1 / -1;
}

.label {
  display: block;
  margin-bottom: 7px;
  font-size: 12.5px;
  font-weight: 600;
  color: var(--cl-text);
}

.input {
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

.input::placeholder {
  color: var(--cl-muted);
}

.input:focus {
  border-color: rgba(0, 166, 126, 0.55);
  box-shadow: 0 0 0 3px rgba(0, 166, 126, 0.1);
  outline: none;
}

.input.err {
  border-color: rgba(190, 80, 60, 0.55);
  box-shadow: 0 0 0 3px rgba(190, 80, 60, 0.08);
}

.textarea {
  height: auto;
  padding: 12px 14px;
  line-height: 1.7;
  resize: vertical;
  font-family: inherit;
}

.select-wrap {
  position: relative;
}

.select {
  appearance: none;
  padding-right: 34px;
  cursor: pointer;
}

.select-arrow {
  position: absolute;
  right: 15px;
  top: 50%;
  margin-top: -2px;
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 5px solid var(--cl-muted);
  pointer-events: none;
}

.price-wrap {
  position: relative;
}

.price-unit {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 13px;
  color: var(--cl-muted);
}

.price-input {
  padding-left: 30px;
}

.msg {
  display: block;
  margin-top: 6px;
  font-size: 11.5px;
  font-style: normal;
  color: #b4503a;
}

/* 封面 */
.cover-row {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  margin-top: 20px;
}

.cover-pick {
  position: relative;
  width: 132px;
  height: 132px;
  border-radius: 14px;
  border: 1px dashed #c8d4cf;
  background: var(--cl-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.18s;
}

.cover-pick:hover {
  border-color: var(--cl-brand);
}

.cover-pick img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 11.5px;
  color: var(--cl-muted);
}

.plus {
  font-style: normal;
  font-size: 20px;
  line-height: 1;
  color: var(--cl-muted-2);
}

.hint {
  margin-top: 8px;
  font-size: 11.5px;
  line-height: 1.7;
  color: var(--cl-muted);
}

/* 轮播图 */
.panel-head h3 {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: -0.3px;
  color: var(--cl-ink);
}

.panel-head p {
  margin-top: 4px;
  font-size: 12px;
  color: var(--cl-muted);
}

.slides {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(132px, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.slide {
  position: relative;
  aspect-ratio: 1;
  border-radius: 14px;
  overflow: hidden;
  background: var(--cl-surface);
  border: 1px solid var(--cl-line);
}

.slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.slide-del {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: rgba(15, 27, 22, 0.6);
  color: #fff;
  font-size: 13px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.18s;
}

.slide-del:hover {
  background: rgba(15, 27, 22, 0.85);
}

.slide-add {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  aspect-ratio: 1;
  border-radius: 14px;
  border: 1px dashed #c8d4cf;
  background: var(--cl-surface);
  color: var(--cl-muted);
  font-size: 11.5px;
  cursor: pointer;
  transition: border-color 0.18s, color 0.18s;
}

.slide-add:hover {
  border-color: var(--cl-brand);
  color: var(--cl-brand-strong);
}

/* 确认 */
.confirm {
  display: flex;
  gap: 22px;
  margin-top: 18px;
}

.confirm-cover {
  flex-shrink: 0;
  width: 160px;
  height: 160px;
  border-radius: 14px;
  overflow: hidden;
  background: var(--cl-surface);
  border: 1px solid var(--cl-line);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: var(--cl-muted);
}

.confirm-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.confirm-info {
  flex: 1;
  min-width: 0;
}

.confirm-name {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: var(--cl-ink);
}

.confirm-price {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 700;
  color: var(--cl-brand-strong);
}

.confirm-rows {
  margin-top: 12px;
}

.confirm-row {
  display: flex;
  gap: 16px;
  padding: 7px 0;
  font-size: 12.5px;
  color: var(--cl-ink);
  border-bottom: 1px dashed #eef2f0;
}

.confirm-row span {
  width: 60px;
  flex-shrink: 0;
  color: var(--cl-muted);
}

.confirm-slides {
  display: flex;
  gap: 8px;
  margin-top: 16px;
  flex-wrap: wrap;
}

.confirm-slides img {
  width: 74px;
  height: 74px;
  border-radius: 10px;
  object-fit: cover;
}

/* 成功 */
.done {
  text-align: center;
  padding: 34px 10px 10px;
}

.done-check {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: var(--cl-brand-soft);
  color: var(--cl-brand-strong);
  font-size: 22px;
}

.done h3 {
  margin-top: 14px;
  font-size: 19px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: var(--cl-ink);
}

.done p {
  margin-top: 8px;
  font-size: 12.5px;
  color: var(--cl-muted);
}

/* 按钮 */
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 26px;
  padding-top: 18px;
  border-top: 1px solid var(--cl-line);
}

.actions.center {
  justify-content: center;
}

.btn-ghost,
.btn-brand {
  height: 42px;
  padding: 0 24px;
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 860px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .cover-row {
    flex-direction: column;
    gap: 10px;
  }
  .confirm {
    flex-direction: column;
  }
  .slides {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
</style>