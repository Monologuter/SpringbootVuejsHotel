<template>

  <div class="review-main">

    <div class="d-flex justify-content-between" style="padding: 10px;">
      <div class="block search">
        <span class="demonstration">日期：</span>
        <el-date-picker
            v-model="searchParams.dateRange"
            value-format="yyyy-MM-dd"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期">
        </el-date-picker>
      </div>
      <div class="block search">
        <p>物品名称：</p>
        <el-input v-model="searchParams.applyThing" placeholder="请输入物品名称"></el-input>
      </div>
      <div class="block search">
        <p>审核结果：</p>
        <el-select v-model="searchParams.reviewStatus" placeholder="请选择">
          <el-option
              v-for="item in reviewStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
      </div>
      <el-button-group class="d-flex justify-content">
        <el-button type="primary" icon="el-icon-search" @click="getReviewList">查询</el-button>
        <el-button type="info" icon="el-icon-refresh" @click="reset">重置</el-button>
        <!--        <el-button type="primary" icon="el-icon-search" @click="add">添加</el-button>-->
      </el-button-group>
    </div>
    <div class="d-flex justify-content-end" style="padding: 10px;">
      <el-button type="danger" style="width: 100px;" @click="openFormDialog('add')">添加</el-button>
    </div>

    <!-- 表格 -->
    <el-table
        stripe
        border
        :data="materialsApplyData"
        style="width: 100%">
      <el-table-column
          align="center"
          prop="applyThing"
          label="物品名称">
      </el-table-column>
      <el-table-column
          align="center"
          prop="applyNum"
          label="申请数量">
      </el-table-column>
      <el-table-column
          align="center"
          prop="thingUnit"
          label="申请单位">
      </el-table-column>
      <el-table-column
          align="center"
          prop="emergencyLevel"
          label="紧急程度">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.emergencyLevel==0">不紧急</el-tag>
          <el-tag v-if="scope.row.emergencyLevel==1" type="warning">普通紧急</el-tag>
          <el-tag v-if="scope.row.emergencyLevel==2" type="danger">非常紧急</el-tag>
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          prop="createTime"
          :formatter="eTableDateTime"
          label="申请时间">
      </el-table-column>
<!--      <el-table-column-->
<!--          align="center"-->
<!--          prop="updateTime"-->
<!--          :formatter="eTableDateTime"-->
<!--          label="审核时间">-->
<!--      </el-table-column>-->
      <el-table-column
          align="center"
          prop="reviewStatus"
          label="审核状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.reviewStatus==0" type="warning">待审核</el-tag>
          <el-tag v-if="scope.row.reviewStatus==1" type="success">审核通过</el-tag>
          <el-tag v-if="scope.row.reviewStatus==2" type="danger">审核拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column
          label="操作"
          width="146"
          align="center">
        <template slot-scope="scope">
          <el-button
              size="mini"
              @click="openFormDialog('check',scope.row)">查看
          </el-button>
          <el-button
              size="mini"
              type="danger"
              :disabled="scope.row.reviewStatus!=0"
              @click="handleDelete(scope.$index, scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>


    <el-dialog
        title="物资申请"
        :visible.sync="dialogVisible"
        width="570px">
      <div class="material-application-main">

        <div class="application-content flex flex-column justify-content-center">
          <div class="d-flex align-items-center mb-15">
            <p class="w-120 text-left font-16">申请物品名称:</p>
            <el-input
                style="width: 350px;"
                placeholder="请输入物品名称"
                :disabled="editMode === 'check'"
                v-model="formApply.name"
                clearable>
            </el-input>
          </div>
          <div class="d-flex align-items-center mb-15">
            <p class="w-120 text-left font-16">申请数量:</p>
            <el-input
                style="width: 350px;"
                placeholder="请输入申请数量"
                :disabled="editMode === 'check'"
                v-model="formApply.num"
                clearable>
            </el-input>
          </div>
          <div class="d-flex align-items-center mb-15">
            <p class="w-120 text-left font-16">申请单位:</p>
            <el-input
                style="width: 350px;"
                placeholder="请输入申请物品单位"
                :disabled="editMode === 'check'"
                v-model="formApply.unit"
                clearable>
            </el-input>
          </div>
          <div class="d-flex align-items-center mb-15">
            <p class="w-120 text-left font-16">申请备注:</p>
            <el-input
                style="width: 350px;"
                placeholder="请输入申请备注"
                :disabled="editMode === 'check'"
                v-model="formApply.remark"
                clearable>
            </el-input>
          </div>
          <div class="d-flex align-items-center mb-15">
            <p class="w-120 text-left font-16">紧急程度:</p>
            <el-select :disabled="editMode === 'check'" style="width:350px;" v-model="formApply.emergencyLevel" placeholder="请选择">
              <el-option
                  v-for="item in situationOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
          </div>
          <div class="d-flex align-items-center mb-15" v-if="editMode === 'check'">
            <p class="w-120 text-left font-16">审核状态:</p>
            <el-tag v-if="formApply.reviewStatus == 0" type="warning">待审核</el-tag>
            <el-tag v-if="formApply.reviewStatus == 1" type="success">审核通过</el-tag>
            <el-tag v-if="formApply.reviewStatus == 2" type="danger">审核拒绝</el-tag>
          </div>
          <div class="d-flex align-items-center mb-15" v-if="editMode === 'check'">
            <p class="w-120 text-left font-16">审批备注:</p>
            <el-input
                style="width: 350px;"
                type="textarea"
                placeholder="无审批备注"
                :disabled="editMode === 'check'"
                v-model="formApply.reviewRemarks"
                clearable>
            </el-input>
          </div>
        </div>
        <div class="flex flex-row justify-content-end mt-10 mr-10">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="editMode !== 'check'" type="primary" @click="confirm">确定</el-button>
        </div>

      </div>
    </el-dialog>


    <!-- 分页器 -->
    <el-pagination
        class="mt-10 mb-10"
        background
        @current-change="handleCurrentChange"
        @prev-click="prevPage"
        @next-click="nextPage"
        layout="prev, pager, next"
        :total="pageNum">
    </el-pagination>


  </div>


</template>

<script>
import {get, post} from "../utils/request";
import {dateFormat} from "../utils/dateTime";

export default {
  name: "HotelMaterialInfo",
  data() {
    return {
      dialogVisible: false,
      currentCheckStatus: 'readOnly',
      materialsApplyData: [], // 列表数据
      searchParams: { // 筛选框数据
        dateRange: '',
        beginTime: '',
        endTime: '',
        applyThing: '',
        reviewStatus: '',
        textarea: '',
        switch: false,
      },
      reviewStatusOptions: [
        {
          value: 0,
          label: '待审核'
        },
        {
          value: 1,
          label: '审核通过'
        },
        {
          value: 2,
          label: '审核拒绝'
        }
      ],
      situationOptions: [{
        value: 0,
        label: '不紧急'
      }, {
        value: 1,
        label: '普通紧急'
      }, {
        value: 2,
        label: '非常紧急'
      }],
      formApply: {
        name: '',
        num: '',
        unit: '',
        remark: '',
        status: '',
      },
      editMode: 'add',
      pageNum: 0,
      form: {
        textarea: '',
        switch: false,
      },
      currentIndex: '',
      page: '',
    }
  },
  watch: {
    "searchParams.dateRange"(val, oldVal) {
      if (val) {
        this.searchParams.beginTime = val[0]
        this.searchParams.endTime = val[1]
      }

    },
    "form.hotel"(val, oldVal) {
      this.getIsolationRoomType()
    }
  },
  mounted() {
    this.getReviewList()
  },
  methods: {
    // 页面初次渲染函数
    getReviewList() {
      const data = {
        page: {
          page: 1,
          size: 10
        },
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        applyThing: this.searchParams.applyThing,
        reviewStatus: this.searchParams.reviewStatus
      }
      this.page = 1
      this.reviewListRequest(data)
    },

    //删除物资申请记录

    handleDelete(index, row) {
      get('/api/materialsApply/delete/' + row.id)
          .then(res => {
            console.log(res);
            if (res.data.code == 200) {
              this.$message({
                message: '删除成功',
                type: 'success',
                duration: 1000
              });
              setTimeout(() => {
                this.$router.go(0)
              }, 1000)
            }
          })
          .catch(err => {
            console.log(err);
          })
    },


    // 列表获取函数
    reviewListRequest(data) {
      post('api/materialsApply/page', data)
          .then(res => {
            console.log(res);
            this.materialsApplyData = res.data.data.records
            this.pageNum = res.data.data.total

            this.currentIndex = 0
            let row = this.materialsApplyData[this.currentIndex]
            this.form = row
          })
          .catch(err => {
            console.error(err);
          })
    },

    //选择页码
    handleCurrentChange(num) {
      console.log(this.page);
      console.log(num);
      const data = {
        page: {
          page: num,
          size: 10
        },
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        applyThing: this.searchParams.applyThing,
        reviewStatus: this.searchParams.reviewStatus
      }
      this.reviewListRequest(data)
    },

    //上一页
    prevPage(num) {
      console.log(this.page);
      console.log(num);
      const data = {
        page: {
          page: num,
          size: 10
        },
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        applyThing: this.searchParams.applyThing,
        reviewStatus: this.searchParams.reviewStatus
      }
      this.reviewListRequest(data)
    },

    //下一页
    nextPage(num) {
      console.log(this.page);
      console.log(num);
      const data = {
        page: {
          page: num,
          size: 10
        },
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        applyThing: this.searchParams.applyThing,
        reviewStatus: this.searchParams.reviewStatus
      }
      this.reviewListRequest(data)
    },

    // 上一条
    prev() {
      this.currentIndex--
      let row = this.materialsApplyData[this.currentIndex]
      console.log(row);
      if (row) {
        this.form = row
      } else if (this.currentIndex == -1 && row == undefined) {
        this.page--
        this.prevPage(this.page)
        this.currentIndex = 9
        row = this.materialsApplyData[this.currentIndex]
        this.form = row
      } else {
        this.$message({
          message: '已经是第一条了！',
          type: 'warning',
          duration: 2000
        });
        return
      }
    },

    // 下一条
    next() {
      this.currentIndex++
      let row = this.materialsApplyData[this.currentIndex]
      console.log(row);
      if (row) {
        this.form = row
      } else if (this.currentIndex == 10 && row == undefined) {
        this.page++
        this.nextPage(this.page)
        this.currentIndex = 0
        row = this.materialsApplyData[this.currentIndex]
        this.form = row
      } else {
        this.$message({
          message: '已经是最后一条了！',
          type: 'warning',
          duration: 2000
        });
        return
      }
    },


    // 重置函数
    reset() {
      this.searchParams = {
        dateRange: '',
        beginTime: '',
        endTime: '',
        reviewStatus: '',
        applyThing: ''
      }
    },

    openFormDialog(val,row) {
      if (val === 'add') {
        this.formApply = {
          name: '',
          num: '',
          unit: '',
          remark: '',
          status: '',
        }
      }else if (val === 'check') {
        this.formApply = {
          name: row.applyThing,
          num: row.applyNum,
          unit: row.thingUnit,
          remark: row.applyRemarks,
          emergencyLevel: row.emergencyLevel,
          reviewRemarks: row.reviewRemarks,
          reviewStatus: row.reviewStatus
        }
        console.log(row)
      }
      this.dialogVisible = true
      this.editMode = val
    },
    confirm() {
      const data = {
        applyNum: this.formApply.num,
        applyRemarks: this.formApply.remark,
        applyThing: this.formApply.name,
        thingUnit: this.formApply.unit,
        emergencyLevel: this.formApply.emergencyLevel,
        // id: ''?
      }
      post('/api/materialsApply/apply', data)
          .then(res => {
            console.log(res);
            this.dialogVisible = false;
            this.$router.go(0);
          })
          .catch(err => {
            console.error(err);
          })
    },


    // 日期时间格式化
    eTableDateTime(row, column, cellValue, index) {
      const dateTime = new Date(cellValue) // Date实例

      const YYYY = dateTime.getFullYear() // 获取当前年份
      const MM = dateTime.getMonth() + 1 // 获取当前月份
      const DD = dateTime.getDate() // 获取当前天数
      const hh = this.fillPrefix(dateTime.getHours()) // 获取当前小时，并判断是否需要补零
      const mm = this.fillPrefix(dateTime.getMinutes()) // 获取当前分钟，并判断是否需要补零
      const ss = this.fillPrefix(dateTime.getSeconds()) // 获取当前秒数，并判断是否需要补零
      // 返回格式化之后的当前时间
      // return `${YYYY}-${MM}-${DD} ${hh}:${mm}:${ss}`
      return `${YYYY}-${MM}-${DD}`
    },

    // 补零函数
    fillPrefix(val) {
      return val > 9 ? val : `0${val}` // 个位数时间进行补零操作
    }

  }

}
</script>

<style scoped>
.review-main {
  display: flex;
  flex-direction: column;
  /* justify-content: space-between; */
  width: 100%;
  min-width: 1000px;
  height: 100%;
  background: #FFFFFF;
  box-shadow: 0px 5px 30px 0px rgba(22, 115, 255, 0.1);
}

.search {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin: 0 10px;
  font-size: 14px;
  white-space: nowrap;
}
</style>


