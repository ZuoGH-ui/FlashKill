<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">秒杀系统</h2>

      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" size="large" maxlength="11" />
        </el-form-item>

        <el-form-item prop="code">
          <div class="code-row">
            <el-input v-model="form.code" placeholder="请输入验证码" size="large" maxlength="6" />
            <el-button size="large" :disabled="countdown > 0" @click="handleSendCode">
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 管理员登录入口 -->
      <div class="admin-entry">
        <el-button link @click="goAdminLogin">管理员登录 →</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { sendCode, login } from '../../api/user'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const countdown = ref(0)

const form = reactive({ phone: '', code: '' })
const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

async function handleSendCode() {
  if (!/^1\d{10}$/.test(form.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  try {
    await sendCode(form.phone)
    ElMessage.success('验证码已发送，请查看控制台')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (e) {}
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form.phone, form.code)
    userStore.setLogin(res.data)
    ElMessage.success('登录成功')
    router.replace('/')
  } catch (e) {
  } finally {
    loading.value = false
  }
}

// 跳管理员登录
function goAdminLogin() {
  router.replace('/admin/login')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 20px;
}
.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
.code-row {
  display: flex;
  gap: 10px;
}
.admin-entry {
  text-align: center;
  margin-top: 10px;
}
.admin-entry .el-button {
  color: #999;
  font-size: 14px;
}
.admin-entry .el-button:hover {
  color: #409eff;
}
</style>