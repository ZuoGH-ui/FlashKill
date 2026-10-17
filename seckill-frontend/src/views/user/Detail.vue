<template>
  <div class="detail" v-if="goods">
    <div class="header">
      <el-button link @click="$router.back()">← 返回</el-button>
    </div>

    <div class="content">
      <img :src="goods.image" class="goods-img" />
      <div class="info">
        <h1>{{ goods.name }}</h1>
        <div class="price">
          <span class="seckill-price">¥{{ goods.seckillPrice }}</span>
          <span class="original-price">¥{{ goods.originalPrice }}</span>
        </div>
        <div class="stock">库存：{{ goods.stock }}</div>
        <div class="time">
          <span v-if="goods.status === 0">距开始：{{ countdownText }}</span>
          <span v-else-if="goods.status === 1">距结束：{{ countdownText }}</span>
          <span v-else>活动已结束</span>
        </div>
        <el-button
          type="danger"
          size="large"
          :disabled="goods.status !== 1"
          :loading="loading"
          style="width: 100%; margin-top: 20px"
          @click="handleSeckill"
        >
          {{ goods.status === 1 ? '立即抢购' : '不可抢购' }}
        </el-button>
      </div>
    </div>

    <div class="desc">
      <h3>商品详情</h3>
      <p>{{ goods.detail }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGoodsDetail } from '../../api/goods'
import { executeSeckill, getSeckillResult } from '../../api/seckill'

const route = useRoute()
const router = useRouter()
const activityId = route.params.activityId

const goods = ref(null)
const loading = ref(false)
const now = ref(Date.now())
let timer = null

const countdownText = computed(() => {
  if (!goods.value) return ''
  const target = goods.value.status === 0
    ? new Date(goods.value.startTime).getTime()
    : new Date(goods.value.endTime).getTime()
  const diff = target - now.value
  if (diff <= 0) return '00:00:00'
  const h = String(Math.floor(diff / 3600000)).padStart(2, '0')
  const m = String(Math.floor((diff % 3600000) / 60000)).padStart(2, '0')
  const s = String(Math.floor((diff % 60000) / 1000)).padStart(2, '0')
  return `${h}:${m}:${s}`
})

onMounted(async () => {
  await loadDetail()
  timer = setInterval(() => { now.value = Date.now() }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

async function loadDetail() {
  try {
    const res = await getGoodsDetail(activityId)
    goods.value = res.data
  } catch (e) {}
}

async function handleSeckill() {
  loading.value = true
  try {
    await executeSeckill(activityId)
    ElMessage.success('抢购成功，订单生成中...')
    // 轮询结果
    pollResult()
  } catch (e) {
    loading.value = false
  }
}

function pollResult() {
  let count = 0
  const poll = setInterval(async () => {
    count++
    try {
      const res = await getSeckillResult(activityId)
      const result = res.data
      if (result && result.startsWith('success:')) {
        clearInterval(poll)
        loading.value = false
        const orderNo = result.split(':')[1]
        ElMessageBox.alert('抢购成功！', '提示', {
          confirmButtonText: '查看订单',
          callback: () => router.replace(`/order/${orderNo}`)
        })
      } else if (result === 'fail') {
        clearInterval(poll)
        loading.value = false
        ElMessage.error('抢购失败')
      }
    } catch (e) {}
    if (count >= 10) {
      clearInterval(poll)
      loading.value = false
      ElMessage.warning('处理超时，请稍后到订单页查看')
    }
  }, 1000)
}
</script>

<style scoped>
.detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}
.header {
  margin-bottom: 20px;
}
.content {
  display: flex;
  gap: 30px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
}
.goods-img {
  width: 400px;
  height: 400px;
  object-fit: cover;
  border-radius: 8px;
}
.info {
  flex: 1;
}
.info h1 {
  margin-bottom: 20px;
}
.price {
  margin-bottom: 20px;
}
.seckill-price {
  color: #f56c6c;
  font-size: 32px;
  font-weight: bold;
  margin-right: 15px;
}
.original-price {
  color: #999;
  text-decoration: line-through;
}
.stock, .time {
  color: #666;
  margin-bottom: 15px;
}
.desc {
  margin-top: 30px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
}
</style>