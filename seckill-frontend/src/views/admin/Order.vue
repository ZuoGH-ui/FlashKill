<template>
  <div class="order-admin">
    <div class="toolbar">
      <el-select v-model="status" placeholder="订单状态" clearable @change="load">
        <el-option label="待支付" :value="0" />
        <el-option label="已支付" :value="1" />
        <el-option label="已取消" :value="2" />
        <el-option label="已超时" :value="3" />
      </el-select>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="orderNo" label="订单号" width="220" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="goodsId" label="商品ID" width="100" />
      <el-table-column prop="amount" label="金额" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning">待支付</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已支付</el-tag>
          <el-tag v-else-if="row.status === 2" type="info">已取消</el-tag>
          <el-tag v-else type="danger">已超时</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="180" />
    </el-table>

    <el-pagination
      style="margin-top: 20px"
      :current-page="page"
      :page-size="size"
      :total="total"
      @current-change="handlePageChange"
      layout="total, prev, pager, next"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminOrderList } from '../../api/admin'

const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const status = ref(null)

onMounted(() => { load() })

async function load() {
  const res = await getAdminOrderList(page.value, size.value, status.value)
  list.value = res.data.records
  total.value = res.data.total
}

function handlePageChange(p) {
  page.value = p
  load()
}
</script>

<style scoped>
.order-admin {
  padding: 20px;
}
.toolbar {
  margin-bottom: 20px;
}
</style>