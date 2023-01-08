<template>
  <div class="hotel-main">
    <div>
      <div class="d-flex justify-content" style="padding: 10px;">
        <div class="ml-10 mr-10 d-flex align-items-center">
          <p class="mr-5" style="width: 70px;font-size: 14px">酒店名:</p>
          <el-input
              placeholder="请输入酒店名"
              prefix-icon="el-icon-search"
              v-model="name">
          </el-input>
        </div>
        <div class="ml-10 mr-10 d-flex align-items-center">
          <p class="mr-5" style="width: 70px;font-size: 14px">地址:</p>
          <el-input
              placeholder="请输入地址"
              prefix-icon="el-icon-search"
              v-model="address">
          </el-input>
        </div>

        <el-button style="margin-left: 10px" @click="getHotelList" size="small">查询</el-button>
      </div>
      <div class="d-flex justify-content-end" style="padding: 10px;">
        <el-button type="primary" style="width: 100px;" @click="addHotel">添加</el-button>
      </div>

      <el-table
          stripe
          size="small "
          :data="hotelData"
          style="width: 100%"
          border>
        <el-table-column
            type="index"
            label="序号"
            width="60"
            align="center">
        </el-table-column>
        <el-table-column
            prop="name"
            label="酒店名称"
            width="200">
        </el-table-column>
        <el-table-column
            prop="address"
            label="地址">
        </el-table-column>
<!--        <el-table-column-->
<!--            label="设置"-->
<!--            width="240"-->
<!--            align="center">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button-->
<!--                size="mini"-->
<!--                type="warning"-->
<!--                @click="setHotel(scope.$index, scope.row)">设置为隔离酒店-->
<!--            </el-button>            -->
<!--          </template>-->
<!--        </el-table-column>        -->
        <el-table-column
            label="操作"
            width="240"
            align="center">
          <template slot-scope="scope">
            <el-button
                size="mini"
                @click="handleCheck(scope.$index, scope.row)">查看
            </el-button>
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
    </div>


    <el-pagination
        class="mb-15"
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
          <p class="w-100 text-left">绑定部门:</p>
          <el-select style="width: 350px;" v-model="form.deptId" placeholder="请选择" :disabled="hotelValue == 'check'">
            <el-option v-for="item in deptList" :value="item.id" :label="item.name"></el-option>
          </el-select>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">酒店名称:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入酒店名称"
              v-model="form.name"
              :disabled="hotelValue == 'check'"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">酒店地址:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入酒店地址"
              v-model="form.address"
              :disabled="hotelValue == 'check'"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">停车场:</p>
          <el-input
              style="width: 350px;"
              placeholder="是否有停车场"
              v-model="form.parkingLot"
              :disabled="hotelValue == 'check'"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">徽章(多个用逗号隔开):</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入徽章内容"
              v-model="form.badge"
              :disabled="hotelValue == 'check'"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">酒店类型:</p>
          <el-switch
              :disabled="hotelValue == 'check'"
              v-model="form.allowIsolation"
              active-text="隔离酒店"
              inactive-text="普通酒店">
          </el-switch>
        </div>
        <div class="d-flex align-items-center mt-10">
          <p class="w-100 text-left">封面:</p>
          <div class="d-flex align-items-center mb-15">
            <el-image style="width: 100px; height: 100px"
                      :src="form.cover"
                      fit="fit">
            </el-image>
            <el-upload
                ref="upload"
                action="#"
                class="avatar-uploader ml-10"
                accept="image/png,image/gif,image/jpg,image/jpeg"
                :show-file-list="false"
                :on-change="onChangeFile"
                :before-upload="beforeAvatarUpload"
                :http-request="uploadImg">
              <el-button
                  type="success"
                  plain
                  round
                  size="mini">
                选择图片
              </el-button>
            </el-upload>
          </div>
        </div>

      </div>
      <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="confirm" v-if="hotelValue != 'check'">确 定</el-button>
        </span>
    </el-dialog>

<!--    <el-dialog-->
<!--      title="提示"-->
<!--      :visible.sync="sureDialog"-->
<!--      width="300px">-->
<!--      <span>确定要将该酒店设置成隔离酒店吗？</span>-->
<!--      <span slot="footer" class="dialog-footer">-->
<!--        <el-button @click="sureDialog = false">取 消</el-button>-->
<!--        <el-button type="primary" @click="confirmSet">确 定</el-button>-->
<!--      </span>-->
<!--    </el-dialog>    -->

  </div>

</template>

<script>
import {get, post} from "../utils/request";

export default {
  name: "HotelManage",
  data() {
    return {
      name: undefined,
      address: undefined,
      hotelData: [],
      title: '',
      page: '',
      departData: '',
      hotelName: '',
      hotelAddress: '',
      hotelValue: '',
      hotelId: '',
      deptId: '',
      parking: '',
      pageNum: 0,
      dialogVisible: false,
      deptList: [],
      form: {
        hotelId: '',
        deptId: '',
        parkingLot: '',
        name: undefined,
        address: undefined,
        badge: '',
        allowIsolation:''
      },
      sureDialog: false,
      currentHotelId: ''
    }
  },
  methods: {
    //获取酒店列表
    getHotelList() {
      let data = {
        page: {
          page: 1,
          size: 10
        },
        name: this.name,
        address: this.address
      }
      let dept = {}
      //获取酒店列表
      this.hotelListRequest(data)
    },

    //获取酒店列表请求
    hotelListRequest(data) {
      post('/api/hotel/page', data)
          .then(res => {
            console.log(res);
            this.hotelData = res.data.data.records
            this.pageNum = res.data.data.total
          })
          .catch(err => {
            console.log(err);
          })
    },

    //删除酒店方法
    handleDelete(index, row) {
      post('/api/hotel/delete/' + row.id)
          .then(res => {
            console.log(res);
            this.$message({
              message: '删除成功',
              type: 'success',
              duration: 1000
            });
            setTimeout(() => {
              this.$router.go(0)
            }, 1000)
          })
          .catch(err => {
            console.log(err);
          })
    },

    //增加酒店方法
    addHotel() {
      this.form = {}
      this.dialogVisible = true
      this.title = '添加酒店'
      this.hotelValue = 'add'
      this.getDeptList()
    },

    handleCheck(index, row) {
      this.hotelValue = 'check'
      this.dialogVisible = true
      this.title = '查看酒店信息'
      this.form = row
    },

    //修改酒店信息
    handleModify(index, row) {
      console.log(index);
      console.log("row",row);
      this.form = row
      if (row.allowIsolation == 1){
        this.form.allowIsolation = true
      }else{
        this.form.allowIsolation = false
      }
      this.dialogVisible = true
      this.title = '修改酒店信息'
      this.hotelValue = 'modify'
    },

    setHotel(index, row) {
      console.log(index);
      console.log(row);
      console.log(row.id);
      this.currentHotelId = row.id
      this.sureDialog = true

    },

    confirmSet() {
      get(`/api/hotel/allow/${this.currentHotelId}`)
        .then( res => {
          console.log(res);
          if (res.data.code == 200) {
            this.$message({
                message: '操作成功',
                type: 'success',
                duration: 2000
            });     
            this.sureDialog = false       
          }
        })
        .catch( err => {
          console.error(err);
        })
    },

    //
    confirm() {
      let value = this.hotelValue
      let data = {
        deptId: this.form.deptId,
        name: this.form.name,
        address: this.form.address,
        parkingLot: this.form.parkingLot,
        cover: this.form.cover,
        badge: this.form.badge,
        allowIsolation: this.form.allowIsolation
      }
      if (value == 'add') {
        this.hotelRequest(value, data)
      } else if (value == 'modify') {
        data.id = this.form.id
        // data.deptId = this.deptId
        this.hotelRequest(value, data)
      }
    },

    //请求方法
    hotelRequest(url, data) {
      if(data.allowIsolation) {
        data.allowIsolation = 1
      }else {
        data.allowIsolation = 0
      }
      console.log("data",data)
      post('/api/hotel/' + url, data)
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
    },

    //选择页码
    handleCurrentChange(num) {
      let data = {
        page: {
          page: num,
          size: 10
        }
      }
      this.hotelListRequest(data)
    },

    //上一页
    prevPage(num) {
      let data = {
        page: {
          page: num,
          size: 10
        }
      }
      this.hotelListRequest(data)

    },

    //下一页
    nextPage(num) {
      let data = {
        page: {
          page: num,
          size: 10
        }
      }
      this.hotelListRequest(data)
    },

    getDeptList() {
      let vo = {}
      post('/api/dept/list', vo).then(res => {
        this.deptList = res.data.data
      }).catch(err => {
        console.log(err)
      })
    },
    //图片改变调用函数
    onChangeFile(file) {
      this.form.cover = URL.createObjectURL(file.raw);

    },
    //图片上传之前的回调函数
    beforeAvatarUpload(file) {
      const isLt2M = file.size / 1024 / 1024 < 5;
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 5MB!');
      }
      return isLt2M;
    },
    //图片上传函数
    uploadImg(params) {
      console.log(params);
      const config = {
        headers: {'Content-Type': 'multipart/form-data'},
      }
      const file = params.file

      //formdata
      const fd = new FormData();//通过form数据格式来传
      fd.append("img", file); //传文件
      fd.append("key", "hotel_cover");

      post("/upload/img", fd, config)
          .then(res => {
            console.log(res);
            this.form.cover = 'http://' + res.data.data
            this.$message({
              message: '上传成功',
              type: 'success',
              duration: 1000
            });
          })
          .catch(err => {
            console.log(err);
          })
    },

  },

  mounted() {
    this.getHotelList()
    this.getDeptList()
  },

}
</script>

<style scoped>
.hotel-main {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  background: #FFFFFF;
  box-shadow: 0px 5px 30px 0px rgba(22, 115, 255, 0.1);
}
</style>