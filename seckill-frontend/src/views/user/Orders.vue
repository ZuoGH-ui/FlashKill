<template>
  <div class="orders">
    <div class="header">
      <el-button link @click="$router.back()">← 返回</el-button>
      <h2>我的订单</h2>
    </div>

    <div v-if="orders.length === 0" class="empty">
      <el-empty description="暂无订单" />
    </div>

    <div v-else class="list">
      <el-card
        v-for="o in orders"
        :key="o.orderNo"
        class="order-card"
        shadow="hover"
      >
        <div class="order-content">
          <img :src="o.goodsImage" class="goods-img" />
          <div class="order-info">
            <h3>{{ o.goodsName }}</h3>
            <div class="amount">¥{{ o.amount }}</div>
            <div class="status">
              <el-tag v-if="o.status === 0" type="warning">待支付</el-tag>
              <el-tag v-else-if="o.status === 1" type="success">已支付</el-tag>
              <el-tag v-else-if="o.status === 2" type="info">已取消</el-tag>
              <el-tag v-else type="danger">已超时</el-tag>
            </div>
            <div class="time">{{ o.createTime }}</div>
          </div>
          <div class="actions">
            <el-button
              v-if="o.status === 0"
              type="primary"
              size="small"
              @click="handlePay(o.orderNo)"
            >去支付</el-button>
            <el-button
              v-if="o.status === 0"
              size="small"
              @click="handleCancel(o.orderNo)"
            >取消</el-button>
            <el-button
              size="small"
              @click="$router.push(`/order/${o.orderNo}`)"
            >详情</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, cancelOrder, payOrder } from '../../api/order'

const orders = ref([])

onMounted(() => {
  loadOrders()
})

async function loadOrders() {
  try {
    const res = await getOrderList()
    orders.value = res.data
  } catch (e) {}
}

async function handlePay(orderNo) {
  try {
    await payOrder(orderNo)
    ElMessage.success('支付成功')
    loadOrders()
  } catch (e) {}
}

async function handleCancel(orderNo) {
  try {
    await ElMessageBox.confirm('确定取消这个订单吗？', '提示', { type: 'warning' })
    await cancelOrder(orderNo)
    ElMessage.success('已取消')
    loadOrders()
  } catch (e) {}
}
</script>

<style scoped>
.orders {
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
.list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.order-content {
  display: flex;
  align-items: center;
  gap: 20px;
}
.goods-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}
.order-info {
  flex: 1;
}
.order-info h3 {
  margin-bottom: 5px;
}
.amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}
.time {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}
</style>