<template>
  <div class="order-detail" v-if="order">
    <div class="header">
      <el-button link @click="$router.back()">← 返回</el-button>
      <h2>订单详情</h2>
    </div>

    <el-card>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="商品">{{ order.goodsName }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ order.amount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="order.status === 0" type="warning">待支付</el-tag>
          <el-tag v-else-if="order.status === 1" type="success">已支付</el-tag>
          <el-tag v-else-if="order.status === 2" type="info">已取消</el-tag>
          <el-tag v-else type="danger">已超时</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
      </el-descriptions>

      <div class="actions" v-if="order.status === 0">
        <el-button type="primary" @click="handlePay">去支付</el-button>
        <el-button @click="handleCancel">取消订单</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, cancelOrder, payOrder } from '../../api/order'

const route = useRoute()
const router = useRouter()
const orderNo = route.params.orderNo
const order = ref(null)

onMounted(() => {
  loadDetail()
})

async function loadDetail() {
  try {
    const res = await getOrderDetail(orderNo)
    order.value = res.data
  } catch (e) {}
}

async function handlePay() {
  try {
    await payOrder(orderNo)
    ElMessage.success('支付成功')
    loadDetail()
  } catch (e) {}
}

async function handleCancel() {
  try {
    await ElMessageBox.confirm('确定取消这个订单吗？', '提示', { type: 'warning' })
    await cancelOrder(orderNo)
    ElMessage.success('已取消')
    loadDetail()
  } catch (e) {}
}
</script>

<style scoped>
.order-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}
.header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}
.actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}
</style>