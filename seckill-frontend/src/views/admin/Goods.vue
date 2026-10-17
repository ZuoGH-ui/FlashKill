<template>
  <div class="goods-admin">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索商品" style="width: 200px" @keyup.enter="load" />
      <el-button type="primary" @click="load">搜索</el-button>
      <el-button type="success" @click="openAdd">新增商品</el-button>
    </div>

    <el-table :data="list" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="price" label="价格" width="120" />
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column label="图片" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.image"
            :src="row.image"
            style="width: 50px; height: 50px"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 20px"
      :current-page="page"
      :page-size="size"
      :total="total"
      @current-change="handlePageChange"
      layout="total, prev, pager, next"
    />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            :show-file-list="false"
            :http-request="handleUpload"
            accept="image/*"
          >
            <img v-if="form.image" :src="form.image" class="upload-img" />
            <el-button v-else>点击上传</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="详情">
          <el-input v-model="form.detail" type="textarea" />
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
import { getGoodsList, addGoods, updateGoods, deleteGoods } from '../../api/admin'
import { uploadImage } from '../../api/upload'

const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('新增商品')
const form = reactive({ id: null, name: '', price: 0, stock: 0, image: '', detail: '' })

onMounted(() => { load() })

async function load() {
  const res = await getGoodsList(page.value, size.value, keyword.value)
  list.value = res.data.records
  total.value = res.data.total
}

function handlePageChange(p) {
  page.value = p
  load()
}

function openAdd() {
  dialogTitle.value = '新增商品'
  Object.assign(form, { id: null, name: '', price: 0, stock: 0, image: '', detail: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  dialogTitle.value = '编辑商品'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleUpload(options) {
  try {
    const res = await uploadImage(options.file)
    form.image = res.data.url
    ElMessage.success('上传成功')
  } catch (e) {
    ElMessage.error('上传失败')
  }
}

async function handleSave() {
  if (form.id) {
    await updateGoods(form)
    ElMessage.success('修改成功')
  } else {
    await addGoods(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  load()
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除这个商品吗？', '提示', { type: 'warning' })
    await deleteGoods(id)
    ElMessage.success('已删除')
    load()
  } catch (e) {}
}
</script>

<style scoped>
.goods-admin {
  padding: 20px;
}
.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.upload-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
}
</style>