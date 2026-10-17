<template>
  <div class="activity-admin">
    <div class="toolbar">
      <el-button type="success" @click="openAdd">新增活动</el-button>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商品" width="200">
        <template #default="{ row }">
          {{ getGoodsName(row.goodsId) }}
        </template>
      </el-table-column>
      <el-table-column prop="seckillPrice" label="秒杀价" width="120" />
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column prop="startTime" label="开始时间" width="180" />
      <el-table-column prop="endTime" label="结束时间" width="180" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning">未开始</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">进行中</el-tag>
          <el-tag v-else type="info">已结束</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 1" size="small" type="danger" @click="handleEnd(row.id)">结束</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination style="margin-top: 20px" :current-page="page" :page-size="size" :total="total"
      @current-change="handlePageChange" layout="total, prev, pager, next" />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品">
          <el-select v-model="form.goodsId" filterable placeholder="请选择商品" style="width: 100%">
            <el-option v-for="g in goodsList" :key="g.id" :label="g.name + '（¥' + g.price + '）'" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="秒杀价">
          <el-input-number v-model="form.seckillPrice" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getActivityList,
  addActivity,
  updateActivity,
  endActivity,
  getGoodsList
} from '../../api/admin'

const list = ref([])
const goodsList = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增活动')
const form = reactive({
  id: null,
  goodsId: null,
  seckillPrice: 0,
  stock: 0,
  startTime: '',
  endTime: ''
})

onMounted(() => {
  load()
  loadGoods()
})

const filteredGoods = ref([])

function filterGoods(query) {
  if (!query) {
    filteredGoods.value = goodsList.value
    return
  }
  filteredGoods.value = goodsList.value.filter(
    g => g.name.includes(query) || String(g.id).includes(query)
  )
}

async function load() {
  const res = await getActivityList(page.value, size.value)
  list.value = res.data.records
  total.value = res.data.total
}

async function loadGoods() {
  const res = await getGoodsList(1, 1000)
  goodsList.value = res.data.records
  filteredGoods.value = res.data.records
}

function getGoodsName(goodsId) {
  const g = goodsList.value.find(item => item.id === goodsId)
  return g ? g.name : '未知商品'
}

function handlePageChange(p) {
  page.value = p
  load()
}

function openAdd() {
  dialogTitle.value = '新增活动'
  Object.assign(form, {
    id: null,
    goodsId: null,
    seckillPrice: 0,
    stock: 0,
    startTime: '',
    endTime: ''
  })
  dialogVisible.value = true
}

function openEdit(row) {
  dialogTitle.value = '编辑活动'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  if (!form.goodsId) {
    ElMessage.warning('请选择商品')
    return
  }
  if (!form.startTime || !form.endTime) {
    ElMessage.warning('请选择开始和结束时间')
    return
  }

  if (form.id) {
    await updateActivity(form)
    ElMessage.success('修改成功')
  } else {
    await addActivity(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  load()
}

async function handleEnd(id) {
  try {
    await ElMessageBox.confirm('确定结束这个活动吗？', '提示', { type: 'warning' })
    await endActivity(id)
    ElMessage.success('已结束')
    load()
  } catch (e) { }
}
</script>

<style scoped>
.activity-admin {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>