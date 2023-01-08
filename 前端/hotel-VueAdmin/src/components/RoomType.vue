<template>
  <div class="room-type-main">
    <div class="d-flex justify-content-end" style="padding: 10px;">
      <el-button type="primary" style="width: 100px;" @click="add">添加</el-button>
    </div>
    <el-table
        stripe
        border
        :data="roomTypeData"
        class="global-table"
        style="width: 100%;overflow: auto;">
      <el-table-column
          align="center"
          prop="name"
          label="名称">
      </el-table-column>
      <el-table-column
          align="center"
          prop="fee"
          label="费用">
      </el-table-column>
      <el-table-column
          align="center"
          prop="isolationFee"
          label="隔离费用">
      </el-table-column>
      <el-table-column
          align="center"
          prop="maxLoad"
          label="最大容量">
      </el-table-column>
      <el-table-column
          align="center"
          prop="bed"
          label="床位数">
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
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">床类型:</p>
          <el-select v-model="form.bedType" filterable multiple placeholder="请选择">
            <el-option
                v-for="item in bedType"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">床位数:</p>
          <el-input-number
              v-model="form.bed"
              :min="1"
              :max="6">
          </el-input-number>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">最大容量:</p>
          <el-input-number
              v-model="form.maxLoad"
              :min="1"
              :max="6">
          </el-input-number>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">是否有窗:</p>
          <div>
            <el-radio v-model="form.haveWindow" :label="1">是</el-radio>
            <el-radio v-model="form.haveWindow" :label="0">否</el-radio>
          </div>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">网络情况:</p>
          <div>
            <el-radio v-model="form.network" :label="1">是</el-radio>
            <el-radio v-model="form.network" :label="0">否</el-radio>
          </div>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">早餐:</p>
          <div>
            <el-radio v-model="form.haveBreakfast" :label="1">是</el-radio>
            <el-radio v-model="form.haveBreakfast" :label="0">否</el-radio>
          </div>
        </div>

        <!--封面图片-->
        <!--<div class="d-flex align-items-center mb-15">-->
        <!--<p class="w-100">修改头像</p>-->
        <!--<img class="user-img" :src="avatarUrl" alt="">-->
        <!--<el-upload-->
        <!--ref="upload"-->
        <!--action="#"-->
        <!--class="avatar-uploader"-->
        <!--accept="image/png,image/gif,image/jpg,image/jpeg"-->
        <!--:show-file-list="false"-->
        <!--:on-change="onChangeFile"-->
        <!--:before-upload="beforeAvatarUpload"-->
        <!--:http-request="uploadImg">-->
        <!--<el-button-->
        <!--type="success"-->
        <!--plain-->
        <!--round-->
        <!--size="mini">-->
        <!--更改头像-->
        <!--</el-button>-->
        <!--</el-upload>-->
        <!--</div>-->


        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">费用:</p>
          <el-input
              style="width: 350px;"
              v-model="form.fee"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">隔离费用:</p>
          <el-input
              style="width: 350px;"
              v-model="form.isolationFee"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">排序:</p>
          <el-input-number
              v-model="form.sort"
              :min="0"
              :max="9999999">
          </el-input-number>
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
  name: "RoomType",
  data() {
    return {
      pageNum: 0,
      roomTypeData: [],
      dialogVisible: false,
      title: '',
      roomTypeValue: '',
      roomId: '',

      roomTypeName: '',
      bedNum: '',
      maxNum: '',
      isWindow: '0',
      network: '0',
      breakfast: '0',
      cost: '',
      sortNum: '',
      bedType: undefined,

      form: {
        name: undefined,
        maxLoad: undefined,
        bed: undefined,
        haveWindow: undefined,
        network: undefined,
        haveBreakfast: undefined,
        sort: undefined,
        fee: undefined,
        bedType: undefined,
        isolationFee:undefined
      }

    }
  },

  methods: {
    getRoomTypeList() {
      let data = {
        page: {
          page: 1,
          size: 9999999
        },
      }
      post("/api/bedType/page",data).then(res => {
        if (res.data.data.records) {
          this.bedType = res.data.data.records
        }
      })
    },
    getRoomList() {
      let data = {
        page: {
          page: 1,
          size: 10
        },
      }
      this.roomListRequest(data)
    },
    //获取房间类型列表请求
    roomListRequest(data) {
      post('/api/roomType/page', data)
          .then(res => {
            console.log(res);
            this.roomTypeData = res.data.data.records
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
      this.roomListRequest(data)
    },

    //上一页
    prevPage(num) {
      let data = {
        page: {
          page: num,
          size: 10
        }
      }
      this.roomListRequest(data)

    },

    //下一页
    nextPage(num) {
      let data = {
        page: {
          page: num,
          size: 10
        }
      }
      this.roomListRequest(data)
    },


    //添加按钮
    add() {
      this.form = {
        name: undefined,
        maxLoad: undefined,
        bed: undefined,
        haveWindow: undefined,
        network: undefined,
        haveBreakfast: undefined,
        sort: undefined,
        fee: undefined,
        bedType: undefined
      }
      this.title = '添加房间'
      this.roomTypeValue = 'add'
      this.dialogVisible = true
    },
    //修改床信息
    handleModify(index, row) {
      let split = row.bedType.split(",");
      console.log(index);
      console.log(row);
      this.form = row
      this.form.bedType = split
      this.roomId = row.id
      this.title = '修改房间类型信息'
      this.roomTypeValue = 'modify'
      this.dialogVisible = true
    },
    //删除床方法
    handleDelete(index, row) {
      get('/api/roomType/delete/' + row.id)
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
      let value = this.roomTypeValue
      let data = this.form
      let bedType = ''
      for (let i = 0; i < this.form.bedType.length; i++) {
        if (this.form.bedType[i]) {
          bedType += this.form.bedType[i]
          if (i != this.form.bedType.length - 1) {
            bedType += ','
          }
        }
      }
      this.form.bedType = bedType
      if (value == 'add') {
        this.roomRequest(value, data)
      } else if (value == 'modify') {
        data.id = this.roomId
        this.roomRequest(value, data)
      }
    },
    roomRequest(url, data) {
      post('/api/roomType/' + url, data)
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
    this.getRoomList()
    this.getRoomTypeList()
  }
}
</script>

<style scoped>
.room-type-main {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  background: #FFFFFF;
  box-shadow: 0px 5px 30px 0px rgba(22, 115, 255, 0.1);
}
</style>