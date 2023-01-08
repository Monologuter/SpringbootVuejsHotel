<template>

  <div class="order-main">
    <div class="d-flex justify-content" style="padding: 10px;">
      <div class="block search">
        <span class="demonstration">隔离日期：</span>
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
        <p>人员状态：</p>
        <el-select v-model="searchParams.status" placeholder="请选择">
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
      </div>
      <div class="block search">
        <p>隔离原因：</p>
        <el-select v-model="searchParams.type" placeholder="请选择">
          <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
      </div>
      <div class="block search">
        <p>来访地：</p>
        <el-input v-model="searchParams.province" placeholder="请输入来访地"></el-input>
      </div>
      <el-button-group class="d-flex justify-content">
        <el-button type="primary" icon="el-icon-search" @click="getIsolationInfo">查询</el-button>
        <el-button type="info" icon="el-icon-refresh" @click="reset">重置</el-button>
      </el-button-group>
    </div>
    <div class="d-flex justify-content-end" style="padding: 10px;">
      <el-button type="success" style="width: 120px;" @click="download">导出成Excel</el-button>
    </div>
    <el-table
        stripe
        border
        :data="isolationData"
        class="global-table"
        style="width: 100%;overflow: auto;">
      <el-table-column
          align="center"
          prop="name"
          label="姓名">
      </el-table-column>
      <el-table-column
          align="center"
          prop="idCard"
          label="身份证"
          width="170"
      >
      </el-table-column>
      <el-table-column
          align="center"
          prop="type"
          label="隔离原因">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type==0" type="danger">密接</el-tag>
          <el-tag v-if="scope.row.type==1">应隔尽隔人员</el-tag>
          <el-tag v-if="scope.row.type==2" type="warning">入境人员</el-tag>
          <el-tag v-if="scope.row.type==3" type="info">自行进入中高风险地区人员</el-tag>
        </template>
      </el-table-column>

      <el-table-column
          align="center"
          prop="province"
          label="来访地">
        <template slot-scope="scope">
          {{ scope.row.province }} - {{ scope.row.city }}
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          prop="province"
          label="隔离酒店"
          min-width="150px"
      >
        <template slot-scope="scope">
          {{ scope.row.hotelName }} - {{ scope.row.roomName }}
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          prop="checkInTime"
          label="隔离时间">
        <template slot-scope="scope">
          {{ dateFormatter(scope.row.checkInTime) }}
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          prop="checkOutTime"
          label="解除隔离时间">
        <template slot-scope="scope">
          {{ dateFormatter(scope.row.checkOutTime) }}
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          prop="status"
          label="人员状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status==0" type="warning">正在隔离</el-tag>
          <el-tag v-if="scope.row.status==1" type="success">解除隔离</el-tag>
          <el-tag v-if="scope.row.status==2" type="danger">阳性入院</el-tag>
        </template>
      </el-table-column>
      <!--      <el-table-column-->
      <!--          align="center"-->
      <!--          prop="roomTypeName"-->
      <!--          label="房间类型">-->
      <!--      </el-table-column>-->
      <!--      <el-table-column-->
      <!--          align="center"-->
      <!--          prop="days"-->
      <!--          label="天数">-->
      <!--      </el-table-column>-->
      <!--      <el-table-column-->
      <!--          align="center"-->
      <!--          prop="pay"-->
      <!--          label="应付价格">-->
      <!--      </el-table-column>-->
      <!--      <el-table-column-->
      <!--          align="center"-->
      <!--          prop="lastPay"-->
      <!--          label="实付价格">-->
      <!--      </el-table-column>-->
      <!--      <el-table-column-->
      <!--          align="center"-->
      <!--          prop="createTime"-->
      <!--          :formatter="dateFormatter"-->
      <!--          label="创建时间">-->
      <!--      </el-table-column>-->
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
          <p class="w-100 text-left">姓名:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入姓名"
              v-model="form.name"
              :disabled="userValue == 'check'"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">身份证:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入身份证"
              v-model="form.idCard"
              :disabled="userValue == 'check'"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">手机号:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入手机号"
              v-model="form.phone"
              :disabled="userValue == 'check'"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">电子邮箱:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入电子邮箱地址"
              v-model="form.email"
              :disabled="userValue == 'check'"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">隔离类型:</p>
          <el-select style="width: 350px;" v-model="form.type" placeholder="请选择" :disabled="userValue == 'check'">
            <el-option v-for="item in typeOptions" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">隔离时间:</p>
          <el-date-picker
              v-model="form.checkInTime"
              type="datetime"
              placeholder="选择日期时间"
              align="right"
              :disabled="userValue == 'check'"
              :picker-options="pickerOptions">
          </el-date-picker>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">解除隔离时间:</p>
          <el-date-picker
              v-model="form.checkOutTime"
              type="datetime"
              placeholder="选择日期时间"
              align="right"
              :disabled="userValue == 'check'"
              :picker-options="pickerOptions">
          </el-date-picker>
        </div>
<!--        <div class="d-flex align-items-center mb-15">-->
<!--          <p class="w-100 text-left">酒店:</p>-->
<!--          <el-input-->
<!--              style="width: 350px;"-->
<!--              placeholder="请输入酒店"-->
<!--              v-model="form.hotel"-->
<!--              :disabled="true"-->
<!--              clearable>-->
<!--          </el-input>-->
<!--        </div>-->
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">酒店:</p>
          <el-select style="width: 350px;" v-model="form.hotelId" placeholder="请选择" disabled>
            <el-option v-for="item in hotelOptions" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">房间名:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入房间名"
              v-model="form.roomName"
              :disabled="true"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15"  v-if="userValue != 'check'">
          <p class="w-100 text-left"></p>
          <el-button @click="handleChangeRoom(form.id)" type="danger">
            更改房间
          </el-button>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">人员状态:</p>
          <el-select style="width: 350px;" v-model="form.status" placeholder="请选择" :disabled="userValue == 'check'">
            <el-option v-for="item in statusOptions" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </div>

      </div>
      <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="confirm" v-if="userValue != 'check'">确 定</el-button>
        </span>
    </el-dialog>
    <!-- 弹出框 -->
    <el-dialog
        title="更改房间"
        :visible.sync="changeDialogVisible"
        width="570px">
      <div class="content">
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">酒店:</p>
          <el-select style="width: 350px;" v-model="changeForm.hotelId" placeholder="请选择">
            <el-option v-for="item in hotelOptions" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">房间类型:</p>
          <el-select style="width: 350px;" v-model="changeForm.roomType" placeholder="请选择">
            <el-option v-for="item in roomTypeOptions" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">房间:</p>
          <el-select style="width: 350px;" v-model="changeForm.roomId" placeholder="请选择">
            <el-option v-for="item in roomOptions" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
            <el-button @click="changeDialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="changeRoom">确 定</el-button>
        </span>
    </el-dialog>


  </div>

</template>

<script>
import {formDataPost, get, post, reqIsolation} from "../utils/request";
import axios from 'axios'

export default {
  name: "OrderManage",
  data() {
    return {
      pageNum: 0,
      isolationData: [
        // {
        //   id: '1',
        //   name: '庄若霞',
        //   idCard: '44030120001213787X',
        //   type: 0,
        //   phone: '13012345678',
        //   email: '1234567@qq.com',
        //   hotelId: '1',
        //   hotelName: 'OneOneTwo国际度假酒店',
        //   roomType: '1463764265293885441',
        //   pay: 0,
        //   checkInTime: '2022-4-20',
        //   checkOutTime: '2022-5-4',
        //   roomId: '10000002',
        //   roomName: '隔离房间1',
        //   province: '广东省',
        //   city: '深圳市',
        //   status: 2
        // },
        // {
        //   id: '2',
        //   name: '曾汝静',
        //   idCard: '441221200012295432',
        //   type: 2,
        //   phone: '13087654321',
        //   email: '7654321@qq.com',
        //   hotelId: '1',
        //   hotelName: 'OneOneTwo国际度假酒店',
        //   roomType: '1463764265293885441',
        //   pay: 0,
        //   checkInTime: '2022-4-20',
        //   checkOutTime: '2022-5-4',
        //   roomId: '10000001',
        //   roomName: '隔离房间2',
        //   province: '广东省',
        //   city: '肇庆市',
        //   status: 1
        // },
        // {
        //   id: '3',
        //   name: '郑佳容',
        //   idCard: '421231200011114321',
        //   type: 1,
        //   phone: '13091823742',
        //   email: '192874@qq.com',
        //   hotelId: '1',
        //   hotelName: 'OneOneTwo国际度假酒店',
        //   roomType: '1463764265293885441',
        //   pay: 0,
        //   checkInTime: '2022-4-22',
        //   checkOutTime: '2022-5-6',
        //   roomId: '10000003',
        //   roomName: '隔离房间3',
        //   province: '广东省',
        //   city: '广州市',
        //   status: 0
        // }
      ],
      dialogVisible: false,
      changeDialogVisible: false,
      pickerOptions: {
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date());
          }
        }, {
          text: '昨天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24);
            picker.$emit('pick', date);
          }
        }, {
          text: '明天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() + 3600 * 1000 * 24);
            picker.$emit('pick', date);
          }
        }, {
          text: '-7天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', date);
          }
        }, {
          text: '-14天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 14);
            picker.$emit('pick', date);
          }
        }, {
          text: '+7天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() + 3600 * 1000 * 24 * 7);
            picker.$emit('pick', date);
          }
        }, {
          text: '+14天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() + 3600 * 1000 * 24 * 14);
            picker.$emit('pick', date);
          }
        },]
      },
      typeOptions: [{
        value: 0,
        label: '密接'
      }, {
        value: 1,
        label: '应隔尽隔人员'
      }, {
        value: 2,
        label: '入境人员'
      }, {
        value: 3,
        label: '自行进入中高风险地区人员'
      }],
      statusOptions: [{
        value: 0,
        label: '正在隔离'
      }, {
        value: 1,
        label: '解除隔离'
      }, {
        value: 2,
        label: '阳性入院'
      }],
      hotelOptions: [],
      roomTypeOptions: [],
      roomOptions: [],
      userValue: '',
      changeForm: {
        isolationInfoId: '',
        hotelId: '',
        roomType: '',
        roomId: ''
      },
      form: {},
      searchParams: {
        dateRange: undefined,
        beginTime: undefined,
        endTime: undefined,
        status: undefined,
        type: undefined,
        province: undefined
      },
      title: ''
    }
  },
  watch: {
    "changeForm.roomType"(val, oldVal) {
      this.getIsolationRoom()
    },
    "changeForm.hotelId"(val, oldVal) {
      this.getIsolationRoomType()
    },
    "searchParams.dateRange"(val, oldVal) {//普通的watch监听
      console.log("a: " + val, oldVal);

      if (val) {
        // let split = val.toString().split(',');

        this.searchParams.beginTime = val[0]
        this.searchParams.endTime = val[1]
      }
      console.log("type: ", typeof (this.searchParams.beginTime))
      console.log("beginTime: ", this.searchParams.beginTime);
      console.log("endTime: ", this.searchParams.endTime);

    },
  },
  methods: {
    handleChangeRoom(isolationInfoId){
      this.changeForm = {
        isolationInfoId: isolationInfoId,
        hotelId: '',
        roomType: '',
        roomId: ''
      }
      this.changeDialogVisible = true

    },
    changeRoom() {
      let data = {
        newRoomId: this.changeForm.roomId,
        currentRoomId: this.form.roomId
      }
      formDataPost("api/room/changeRoom",data).then(res => {
        if (res.data.code === "200") {
          this.getOneInfo(this.form.id)
          this.changeDialogVisible = false
        }else {
          this.$message({
            message: res.data.msg,
            type: 'warning',
            duration: 1000
          });
        }
      })


    },
    getOneInfo(val) {
      get("api/isolationInfo/get/"+val).then(res => {
        this.form = res.data.data
      })
    },
    getIsolationInfo(num) {
      console.log("num",num)
      let data = {
        page: {
          page: 1,
          size: 10
        },
        type: this.searchParams.type,
        status: this.searchParams.status,
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        province: this.searchParams.province
      }
      if (num && !isNaN(num)) {
        data.page.page = num
      }
      post("api/isolationInfo/page",data).then(res => {
        this.isolationData = res.data.data.records
        this.pageNum = res.data.data.total        
      })
    },
    getIsolationRoom() {
      let data = {
        type: this.changeForm.roomType,
        isIsolation: 1,
        status: 0
      }
      post("api/room/getAllList", data).then(res => {
        this.roomOptions = res.data.data
      })
    },
    getIsolationRoomType() {
      let data = {
        isIsolation: 1
      };
      if (this.changeForm.hotelId) {
        get("api/roomType/currentRoomTypeList/" + this.changeForm.hotelId, data).then(res => {
          console.log(res)
          this.roomTypeOptions = res.data.data
        })
      }
    },
    getIsolationHotelList() {
      let data = {
        page: {
          page: 1,
          size: 99999
        },
        allowIsolation: 1
      }
      post("/api/hotel/page", data).then(res => {
        console.log(res.data.data)
        this.hotelOptions = res.data.data.records
      })
    },
    download() {
      let data = {
        page: {
          page: 1,
          size: 9999999
        },
        type: this.searchParams.type,
        status: this.searchParams.status,
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        province: this.searchParams.province
      }

      reqIsolation(data)
          .then(res => {
            console.log(res);


            const aLink = document.createElement('a')
            var blob = new Blob([res.data], {type: 'mimeMap.xlsx'})
            // //从response的headers中获取filename, 后端response.setHeader("Content-disposition", "attachment; filename=xxxx.docx") 设置的文件名;
            var patt = new RegExp('filename=([^;]+\\.[^\\.;]+);*')
            var contentDisposition = decodeURI(res.headers['content-disposition'])
            var result = patt.exec(contentDisposition)
            var fileName = result[1]
            fileName = fileName.replace(/\"/g, '')
            aLink.href = URL.createObjectURL(blob)
            aLink.setAttribute('download', fileName) // 设置下载文件名称
            document.body.appendChild(aLink)
            aLink.click()
            document.body.appendChild(aLink)
          })
          .catch(err => {
            console.log(err);
          })


      // post("/api/order/download",data).then(function (res) {
      //     console.log("res",res.data)
      //
      //     const blobData = new Blob([JSON.stringify(res.data)], {
      //         type: 'application/json'
      //     })
      //     console.log(blobData);
      //     let blob = new Blob([blobData],{type: 'application/vnd.ms-excel;charset=utf-8'});
      //
      //     if (!!window.ActiveXObject || "ActiveXObject" in window) {
      //         window.navigator.msSaveOrOpenBlob(blob, 'fileName');
      //     } else {
      //         const link = document.createElement('a');
      //         link.style.display = 'none';
      //         link.href = URL.createObjectURL(blob);
      //         link.setAttribute('download', 'fileName');
      //         document.body.appendChild(link);
      //         link.click();
      //         document.body.removeChild(link);
      //     }
      //     // resolve(res);
      //
      //
      //
      //

      //
      //
      //
      //
      //   // 得到请求到的数据后，对数据进行处理
      //   // let blob = new Blob([blobData], {type: 'application/vnd.ms-excel;charset=utf-8'});// 创建一个类文件对象：Blob对象表示一个不可变的、原始数据的类文件对象
      //   // let fileName = decodeURI(res.headers['content-disposition']);// 设置文件名称,decodeURI：可以对后端使用encodeURI() 函数编码过的 URI 进行解码。encodeURI() 是后端为了解决中文乱码问题
      //   // // console.log(fileName)
      //   // if (fileName) {// 根据后端返回的数据处理文件名称
      //   //   fileName = fileName.substring(fileName.indexOf('=') + 1);
      //   // }
      //   // const link = document.createElement('a')// 创建一个a标签
      //   // link.download = fileName;// 设置a标签的下载属性
      //   // link.style.display = 'none';// 将a标签设置为隐藏
      //   // link.href = URL.createObjectURL(blob);// 把之前处理好的地址赋给a标签的href
      //   // document.body.appendChild(link);// 将a标签添加到body中
      //   // link.click();// 执行a标签的点击方法
      //   // URL.revokeObjectURL(link.href) // 下载完成释放URL 对象
      //   // document.body.removeChild(link)// 移除a标签
      // })
    },
    reset() {
      this.searchParams = {
        dateRange: undefined,
        beginTime: undefined,
        endTime: undefined,
        status: undefined,
        type: undefined,
        province: undefined
      }
    },
    getRoomTypeList() {
      let data = {
        page: {
          page: 1,
          size: 999999
        },
      }
      post("api/roomType/page", data).then(res => {
        this.roomTypeOptions = res.data.data.records
        console.log("this.roomTypeOptions", this.roomTypeOptions)
      })
    },

    //选择页码
    handleCurrentChange(num) {
      this.getIsolationInfo(num)
    },

    //上一页
    prevPage(num) {
      this.getIsolationInfo(num)

    },

    //下一页
    nextPage(num) {
      this.getIsolationInfo(num)
    },
    //删除方法
    handleDelete(index, row) {
      get('/api/isolationInfo/delete/' + row.id)
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
    handleCheck(index, row) {
      this.userValue = 'check'
      this.dialogVisible = true
      this.title = '查看用户信息'
      this.form = row
    },

    //修改用户信息
    handleModify(index, row) {
      console.log(index);
      console.log(row);
      // this.getOneInfo(row.id)
      this.form = row
      this.dialogVisible = true
      this.title = '修改用户信息'
      this.userValue = 'modify'
    },

    confirm() {
      let value = this.userValue
      let data = {
        id: undefined,
        name: this.form.name,
        idCard: this.form.idCard,
        type: this.form.type,
        phone: this.form.phone,
        email: this.form.email,
        checkInTime: this.form.checkInTime,
        checkOutTime: this.form.checkOutTime,
        province: this.form.province,
        city: this.form.city,
        status: this.form.status
      }
      if (value == 'add') {
        this.userRequest(value, data)
      } else if (value == 'modify') {
        data.id = this.form.id
        if (this.tempPassword == this.form.password) {
          this.form.password = undefined
        }
        // data.deptId = this.deptId
        this.userRequest(value, data)
      }
    },

    //请求方法
    userRequest(url, data) {
      post('/api/isolationInfo/' + url, data)
          .then(res => {
            console.log(res);
            if (res.data.code === "200") {
              this.$message({
                message: '操作成功！',
                type: 'success',
                duration: 1000
              });
              this.$router.go(0);
            } else {
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

    dateFormatter(val) {
      // console.log(val);
      var d = new Date(val);

      var year = d.getFullYear();       //年
      var month = d.getMonth() + 1;     //月
      var day = d.getDate();            //日

      var hh = d.getHours();            //时
      var mm = d.getMinutes();          //分
      var ss = d.getSeconds();           //秒

      var clock = year + "/";

      if (month < 10)
        clock += "0";

      clock += month + "/";

      if (day < 10)
        clock += "0";

      clock += day + " ";

      if (hh < 10)
        clock += "0";

      clock += hh + ":";
      if (mm < 10) clock += '0';
      clock += mm + ":";

      if (ss < 10) clock += '0';
      clock += ss;
      return (clock);
    }

  },
  mounted() {
    this.getIsolationHotelList()
    this.getIsolationInfo()
    // this.getOrderList()
    // this.getRoomTypeList()
  }
}
</script>

<style scoped>
.order-main {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
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
