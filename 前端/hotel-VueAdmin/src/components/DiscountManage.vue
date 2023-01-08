<template>

  <div class="discount-main">
    <div class="d-flex justify-content-end" style="padding: 10px;">
      <el-button type="primary" style="width: 100px;" @click="add">添加</el-button>
    </div>
    <el-table
        stripe
        border
        :data="discountData"
        class="global-table"
        style="width: 100%;overflow: auto;">
      <el-table-column
          align="center"
          prop="id"
          label="ID">
      </el-table-column>
      <el-table-column
          align="center"
          prop="name"
          label="名称">
      </el-table-column>
      <el-table-column
          align="center"
          prop="effectType"
          label="生效类型">
        <template slot-scope="scope">
          <div class="cell" v-if="scope.row.effectType == '2'"><el-tag>无门槛</el-tag></div>
          <div class="cell" v-if="scope.row.effectType == '0' || scope.row.effectType == '1'"><el-tag type="success">满减</el-tag></div>
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          prop="discountsType"
          label="折扣方式">
        <template slot-scope="scope">
          <div class="cell" v-if="scope.row.discountsType == '0'"><el-tag>立减</el-tag></div>
          <div class="cell" v-if="scope.row.discountsType == '1'"><el-tag type="success">折扣</el-tag></div>
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          prop="discounts"
          label="幅度">
        <template slot-scope="scope">
          <div class="cell" v-if="scope.row.discountsType == '0'">{{ scope.row.discounts + '元' }}</div>
          <div class="cell" v-if="scope.row.discountsType == '1'">{{ scope.row.discounts * 100 + '%' }}</div>
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          prop="description"
          label="描述">
      </el-table-column>
      <el-table-column
          label="操作"
          width="240"
          align="center">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="primary"
              @click="handleModify(scope.$index, scope.row)">编辑
          </el-button>
          <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

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


    <!-- 弹出框 -->
    <el-dialog
        :title="title"
        :visible.sync="dialogVisible"
        width="570px">
      <div class="contont">
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">名称:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入名称"
              v-model="form.name"
              maxlength="10"
              show-word-limit
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">描述:</p>
          <el-input
              type="textarea"
              :rows="2"
              style="width: 350px;"
              placeholder="请输入描述"
              v-model="form.description"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">折扣类型:</p>
          <div>
<!--            <el-radio v-model="form.type" label="0">酒店折扣</el-radio>-->
            <el-radio v-model="form.type" :label="1">酒店内个人折扣</el-radio>
<!--            <el-radio v-model="form.type" label="2">折扣</el-radio>-->
          </div>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">折扣方式:</p>
          <div>
            <el-radio v-model="form.discountsType" :label="0">立减</el-radio>
            <el-radio v-model="form.discountsType" :label="1">折扣</el-radio>
          </div>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">折扣幅度:</p>
          <el-input
              style="width: 350px;"
              type="number"
              placeholder="请输入折扣幅度"
              v-model="form.discounts"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">生效条件类型:</p>
          <div>
            <el-radio v-model="form.effectType" :label="0">满?天立减</el-radio>
            <el-radio v-model="form.effectType" :label="1">满?钱立减</el-radio>
            <el-radio v-model="form.effectType" :label="2">无门槛</el-radio>
          </div>
        </div>
        <div class="d-flex align-items-center mb-15" v-if="form.effectType == 0 || form.effectType == 1">
          <p class="w-100 text-left">生效条件:</p>
          <el-input
              placeholder="请输入生效的天数或钱数"
              style="width: 350px;"
              v-model="form.effectCondition"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">互斥性:</p>
          <div>
            <el-radio v-model="form.exclusiveType" :label="0">不互斥</el-radio>
            <el-radio v-model="form.exclusiveType" :label="1">与所有优惠券互斥</el-radio>
            <el-radio v-model="form.exclusiveType" :label="2">与指定优惠券互斥</el-radio>
          </div>
        </div>
        <div class="d-flex align-items-center mb-15" v-if="form.exclusiveType == '2'">
          <p class="w-100 text-left">互斥券ID:</p>
          <el-input
              placeholder="填写券码ID，多个ID请用英文字符逗号隔开"
              style="width: 350px;"
              v-model="form.exclusiveRange"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">有效期:</p>
          <el-date-picker
              v-model="form.validityTime"
              type="datetime"
              placeholder="选择日期时间">
          </el-date-picker>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="confirm">确 定</el-button>
        </span>
    </el-dialog>

  </div>

</template>

<script>
import {get, post} from "../utils/request";

export default {
  name: "DiscountManage",
  data() {
    return {
      pageNum: 0,
      discountData: [],
      dialogVisible: false,
      title: '',
      discountValue: '',
      discountId: '',
      discountName: '',
      discountType: '',
      textarea: '',
      discountNum: '',
      discountCond: '0',
      dayNum: '',
      form: {
        id: undefined,
        name: undefined,
        description: undefined,
        type: undefined,
        discountsType: undefined,
        discounts: undefined,
        effectType: undefined,
        effectCondition: undefined,
        exclusiveType: undefined,
        exclusiveRange: undefined,
        validityTime: undefined
      }
    }
  },
  methods: {
    getDiscountList() {
      let data = {
        page: {
          page: 1,
          size: 10
        },
      }
      this.discountListRequest(data)
    },
    //获取折扣列表请求
    discountListRequest(data) {
      post('/api/discounts/page', data)
          .then(res => {
            console.log(res);
            this.discountData = res.data.data.records
            this.pageNum = res.data.data.total
          })
          .catch(err => {
            console.log(err);
          })
    },

    //选择页码
    handleCurrentChange(num) {
      let data = {
        page: {
          page: num,
          size: 10
        }
      }
      this.discountListRequest(data)
    },

    //上一页
    prevPage(num) {
      let data = {
        page: {
          page: num,
          size: 10
        }
      }
      this.discountListRequest(data)

    },

    //下一页
    nextPage(num) {
      let data = {
        page: {
          page: num,
          size: 10
        }
      }
      this.discountListRequest(data)
    },


    //添加按钮
    add() {
      this.form = {
        id: undefined,
        name: undefined,
        description: undefined,
        type: undefined,
        discountsType: undefined,
        discounts: undefined,
        effectType: undefined,
        effectCondition: undefined,
        exclusiveType: undefined,
        exclusiveRange: undefined,
        validityTime: undefined
      }
      this.title = '添加折扣'
      this.discountValue = 'add'
      this.dialogVisible = true
    },
    //修改折扣信息
    handleModify(index, row) {
      console.log(index);
      console.log(row);
      this.form = row
      this.form.discountId = row.id
      this.title = '修改折扣信息'
      this.discountValue = 'modify'
      this.dialogVisible = true
    },
    //删除折扣方法
    handleDelete(index, row) {
      get('/api/discounts/delete/' + row.id)
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

    confirm() {
      let value = this.discountValue
      //
      let data = {
        name: this.form.name,
        description: this.form.description,
        type: this.form.type,
        discounts: this.form.discounts,
        discountsType: this.form.discountsType,
        effectType: this.form.effectType,
        effectCondition: this.form.effectCondition,
        validityTime: this.form.validityTime,
        exclusiveType: this.form.exclusiveType,
        exclusiveRange: this.form.exclusiveRange
      }

      if (value == 'add') {
        this.discountRequest(value, data)
      } else if (value == 'modify') {
        data.id = this.form.discountId
        this.discountRequest(value, data)
      }
    },
    discountRequest(url, data) {
      post('/api/discounts/' + url, data)
          .then(res => {
            console.log(res);
            if (res.data.code == 200) {
              this.$message({
                message: '操作成功！',
                type: 'success',
                duration: 1000
              });
              this.$router.go(0);
            } else if (res.data.code == 1001) {
              this.$message({
                message: res.data.msg,
                type: 'warning',
                duration: 1000
              });

            }
          })
          .catch(err => {
            console.log(err);
          })
    }

  },
  mounted() {
    this.getDiscountList()
  }
}
</script>

<style scoped>
.discount-main {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  background: #FFFFFF;
  box-shadow: 0px 5px 30px 0px rgba(22, 115, 255, 0.1);
}

.w-100 {
  width: 130px;
}
</style>