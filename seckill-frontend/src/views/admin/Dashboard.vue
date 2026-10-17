<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <div class="stat-label">总订单数</div>
          <div class="stat-value">{{ data.totalOrder }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-label">已支付订单</div>
          <div class="stat-value">{{ data.paidOrder }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-label">总成交额</div>
          <div class="stat-value">¥{{ data.totalAmount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-label">支付成功率</div>
          <div class="stat-value">{{ (data.successRate * 100).toFixed(1) }}%</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <div ref="chartRef" style="height: 400px"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { getDashboard } from '../../api/admin'

const chartRef = ref()
const data = reactive({
  totalOrder: 0,
  paidOrder: 0,
  totalAmount: 0,
  successRate: 0
})

onMounted(async () => {
  const res = await getDashboard()
  Object.assign(data, res.data)
  renderChart()
})

function renderChart() {
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    title: { text: '订单状态分布' },
    tooltip: {},
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: data.paidOrder, name: '已支付' },
        { value: data.totalOrder - data.paidOrder, name: '其他' }
      ]
    }]
  })
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}
.stat-label {
  color: #999;
  font-size: 14px;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}
</style>