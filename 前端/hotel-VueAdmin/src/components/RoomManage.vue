<template>
  <div class="room-manage-main">
    <div class="d-flex align-items-center justify-content-between mt-10 pl-5 pr-5">

      <div class="d-flex align-items-center w-percent-100">
        <div class="d-flex align-items-center ml-5 mr-5">
          <p style="white-space: nowrap" class="mr-5 font-16">楼层：</p>
          <el-select v-model="floorNum" placeholder="请选择">
            <el-option
                v-for="item in floorList"
                :key="item"
                :label="item"
                :value="item">
            </el-option>
          </el-select>
        </div>
        <div class="d-flex align-items-center ml-5 mr-5">
          <p style="white-space: nowrap" class="mr-5 font-16">房号：</p>
          <el-input v-model="roomNum" placeholder="请输入房号"></el-input>
        </div>
        <div class="d-flex align-items-center ml-5 mr-5">
          <p style="white-space: nowrap" class="mr-5 font-16">房类型:</p>
          <el-select v-model="roomType" placeholder="请选择">
            <el-option
                v-for="item in roomTypeList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </div>
        <div class="d-flex align-items-center ml-5 mr-5">
          <p style="white-space: nowrap" class="mr-5 font-16">房间性质:</p>
          <el-select v-model="isIsolation" placeholder="请选择">
            <el-option
                v-for="item in isIsolationOption"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </div>
      </div>

      <div class="d-flex align-items-center ml-5 mr-5">
        <el-button type="primary" @click="getRoomList">查询</el-button>
        <el-button type="danger" @click="reset">重置</el-button>
      </div>

    </div>

    <ul class="mt-10 room-list d-flex flex-wrap">
      <li class="cursor" v-for="(item,index) in roomList" @click="roomHandle(item)">
        <span :class="classList[item.status]" class="iconfont icon-menleianzhuang"></span>
        <h3 :style="item.isIsolation != 0 ? 'color:red':''">{{ item.isIsolation != 0 ? '* ' + item.name : item.name }}</h3>
        <h5>{{ item.roomTypeName }}</h5>
        <p>{{ statusList[item.status] }}</p>
      </li>
    </ul>

<!--    第一层对话框,操作层-->
    <el-dialog
        :title="current.name"
        :visible.sync="roomHandleVisible"
        width="420px"
    >
      <p class="text-left ml-10">房间信息：</p>
      <el-button
          class="mr-10"
          @click="checkRoom"
      >房间详情</el-button>
      <el-button
          type="primary"
          class="ml-10 mb-10 mt-10 mr-10"
          @click="editRoom"
      >编辑房间</el-button>
      <el-button
          type="info"
          class="ml-10 mb-10 mt-10"
          v-if="current.isIsolation == 1"
          :disabled="current.status != 1"
          @click="checkInInfo"
      >入住信息</el-button>
      <p class="text-left mt-10 ml-10">房间操作：</p>
      <el-button
          type="success"
          class="mr-10 mt-10"
          :disabled="current.status == 1 || current.status == 4"
          v-if="current.isIsolation == 0"
          @click="checkInHandler"
      >登记入住
      </el-button>
      <el-button
          type="success"
          class="mr-10 mt-10"
          :disabled="current.status != 1"
          v-else
          @click="changeRoomHandler"
      >换 房
      </el-button>
      <el-button
          type="danger"
          class="ml-10 mb-10 mt-10 mr-10"
          :disabled="current.status != 1"
          v-if="current.isIsolation == 0"
          @click="checkOutHandler"
      >退房结账</el-button>
      <el-button
          type="danger"
          class="ml-10 mb-10 mt-10 mr-10"
          v-else
          :disabled="current.status != 1"
          @click="isolationCheckOutVisible = true"
      >退 房</el-button>
      <el-button
          type="warning"
          class="ml-10 mb-10 mt-10"
          :disabled="current.status != 4"
          @click="cleanRoom"
      >房间消毒</el-button>

    </el-dialog>

<!--    入住表单-->
    <el-dialog
        :title="current.name + '入住'"
        :visible.sync="checkInVisible"
        width="30%"
        custom-class="min-w-450"
    >
      <el-form ref="form" label-width="80px">
        <div style="display: flex;flex-direction: row;align-items: center">

          <el-form-item label="订单ID" style="margin-bottom: 0">
            <el-input v-model="checkInForm.orderId"
                      :disabled="!checkInForm.orderSwitch"
                      @blur="loadOrder"
            ></el-input>
          </el-form-item>
          <el-switch class="ml-10" v-model="checkInForm.orderSwitch"></el-switch>

        </div>

        <el-form-item label="会员ID" style="margin-top: 20px">
          <el-input v-model="checkInForm.customerId"></el-input>
        </el-form-item>
        <el-form-item style="text-align:left" label="来访地">
          <!-- <el-select v-model="checkInForm.province" placeholder="请选择来访地" style="margin-left: -96px"> -->
          <el-select v-model="checkInForm.province" placeholder="请选择来访地">
            <el-option
                v-for="item in provinceList"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="退房时间">
          <div style="text-align:left" class="block">
            <!--            <span class="demonstration">默认</span>-->
            <el-date-picker
                v-model="checkInForm.estimatedCheckOut"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="选择日期"
                :picker-options="pickerOptions">
            </el-date-picker>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
            <el-button @click="checkInVisible = false">取 消</el-button>
            <el-button type="primary" @click="checkIn">确 定</el-button>
      </span>
    </el-dialog>

    <!--    隔离房间入住信息dialog-->
    <el-dialog
        :title="current.name + '入住信息'"
        :visible.sync="checkInInfoVisible"
        width="30%"
        custom-class="min-w-450"
    >
      <div class="content">
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">姓名:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入名称"
              :disabled="true"
              v-model="checkInInfoData.name"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">身份证:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入身份证"
              :disabled="true"
              v-model="checkInInfoData.idCard"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">手机号:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入手机号"
              :disabled="true"
              v-model="checkInInfoData.phone"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">邮箱:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入邮箱"
              :disabled="true"
              v-model="checkInInfoData.email"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">人员类型:</p>
          <el-select style="width: 350px;" v-model="checkInInfoData.type" placeholder="请选择" :disabled="true">
            <el-option v-for="item in typeOptions" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
            <el-button @click="checkInInfoVisible = false">取 消</el-button>
      </span>
    </el-dialog>

    <!--    隔离房间换房dialog-->
    <el-dialog
        :title="current.name + '换房'"
        :visible.sync="changeRoomVisible"
        width="30%"
        custom-class="min-w-450"
    >
      <el-select v-model="newRoomId" placeholder="请选择">
        <el-option
            v-for="item in ableUseRoom"
            :label="item.name"
            :value="item.id">
          <span style="float: left">{{ item.name }}</span>
          <span style="float: right; color: #8492a6; font-size: 13px">{{ item.id }}</span>
        </el-option>
      </el-select>
      <span slot="footer" class="dialog-footer">
            <el-button @click="changeRoomVisible = false">取 消</el-button>
            <el-button type="primary" @click="changeRoom">确 定</el-button>
      </span>
    </el-dialog>
<!--    隔离房间退房选项-->
    <el-dialog
        :title="current.name + '退房'"
        :visible.sync="isolationCheckOutVisible"
        width="30%"
        custom-class="min-w-450"
    >
      <el-button
          class="mr-10"
          @click="isolationCheckOut(2)"
          type="danger"
      >阳性入院</el-button>
      <el-button
          type="success"
          class="ml-10 mb-10 mt-10 mr-10"
          @click="isolationCheckOut(1)"
      >解除隔离</el-button>
    </el-dialog>

<!--    详情/编辑表单-->
    <el-dialog
        :title="current.name + current.infoTitle"
        :visible.sync="infoVisible"
        width="40%"
        custom-class="min-w-650"
    >
      <div class="d-flex flex-row mt-10">
        <div class="blue-label"></div>
        <p style="font-weight: bold">房间基础信息</p>
      </div>
      <div class="contont">
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">名称:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入名称"
              :disabled="current.infoMode == 'check'"
              v-model="form.name"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">房间类型:</p>
          <el-select
              :disabled="current.infoMode == 'check'"
              v-model="form.type"
              placeholder="请选择">
            <el-option
                v-for="item in roomTypeList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">楼层:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入楼层"
              :disabled="current.infoMode == 'check'"
              v-model="form.floor"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">朝向:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入房间朝向"
              :disabled="current.infoMode == 'check'"
              v-model="form.direction"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">房间类型:</p>
          <el-switch
              :disabled="current.infoMode == 'check'"
              v-model="form.isIsolation"
              active-text="隔离房间"
              inactive-text="普通房间">
          </el-switch>
        </div>
      </div>


      <div v-if="current.infoMode == 'check'" class="d-flex flex-row mt-10" style="margin-top: 30px">
        <div class="blue-label"></div>
        <p style="font-weight: bold">房间详情信息</p>
      </div>
      <div v-if="current.infoMode == 'check'" class="contont">
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">名称:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入名称"
              disabled
              v-model="form.rtName"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">床位数:</p>
          <el-input-number
              disabled
              v-model="form.bed"
              :min="1"
              :max="6">
          </el-input-number>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">最大容量:</p>
          <el-input-number
              disabled
              v-model="form.maxLoad"
              :min="1"
              :max="6">
          </el-input-number>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">是否有窗:</p>
          <div>
            <el-radio disabled v-model="form.haveWindow" :label="1">是</el-radio>
            <el-radio disabled v-model="form.haveWindow" :label="0">否</el-radio>
          </div>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">网络情况:</p>
          <div>
            <el-radio disabled v-model="form.network" :label="1">是</el-radio>
            <el-radio disabled v-model="form.network" :label="0">否</el-radio>
          </div>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">早餐:</p>
          <div>
            <el-radio disabled v-model="form.haveBreakfast" :label="1">是</el-radio>
            <el-radio disabled v-model="form.haveBreakfast" :label="0">否</el-radio>
          </div>
        </div>

        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">费用:</p>
          <el-input
              disabled
              style="width: 350px;"
              v-model="form.fee"
              clearable>
          </el-input>
        </div>
      </div>


      <div v-if="current.infoMode == 'check'" class="d-flex flex-row mt-10" style="margin-top: 30px">
        <div class="blue-label"></div>
        <p style="font-weight: bold">床位详情信息</p>
      </div>
      <div v-if="current.infoMode == 'check'" class="contont">
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">名称:</p>
          <el-input
              disabled
              style="width: 350px;"
              placeholder="请输入床的名称"
              v-model="form.bedName"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">规格:</p>
          <el-input
              disabled
              style="width: 350px;"
              placeholder="请输入床的规格"
              v-model="form.specification"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">最大容量:</p>
          <el-input-number
              disabled
              v-model="form.bedMaxLoad"
              :min="1"
              :max="6">
          </el-input-number>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">硬度:</p>
          <div>
            <el-radio disabled v-model="form.hardness" label="0">超软</el-radio>
            <el-radio disabled v-model="form.hardness" label="1">偏软</el-radio>
            <el-radio disabled v-model="form.hardness" label="2">适中</el-radio>
            <el-radio disabled v-model="form.hardness" label="3">偏硬</el-radio>
            <el-radio disabled v-model="form.hardness" label="4">硬</el-radio>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="infoVisible = false">取 消</el-button>
        <el-button v-if="current.infoMode != 'check'" type="primary" @click="confirmEdit">确 定</el-button>
      </span>

    </el-dialog>

  </div>
</template>

<script>
import {formDataPost, get, post} from "../utils/request";
import { isJSON } from "../utils/isJSON";

export default {
  name: "RoomManage",
  data() {
    return {
      roomHandleVisible: false,
      checkInVisible: false,
      infoVisible: false,
      isolationCheckOutVisible: false,
      ws: null,
      roomList: [],
      floorList: [],
      roomTypeList: [],
      classList: ['status-0', 'status-1', 'status-2','status-3'],
      statusList: ['未入住', '已入住', '已预订','暂定','待消毒'],
      roomNum: '',
      isIsolation: '',
      isIsolationOption: [{
        value: '0',
        label: '普通房间'
      }, {
        value: '1',
        label: '隔离房间'
      }],
      options: [{
        value: '选项1',
        label: '黄金糕'
      }, {
        value: '选项2',
        label: '双皮奶'
      }, {
        value: '选项3',
        label: '蚵仔煎'
      }, {
        value: '选项4',
        label: '龙须面'
      }, {
        value: '选项5',
        label: '北京烤鸭'
      }],
      floorNum: '',
      roomType: '',
      changeRoomVisible: false,
      checkInInfoVisible: false,
      ableUseRoom: [],
      newRoomId: undefined,
      current: {
        id: undefined,
        name: undefined,
        status: undefined,
        infoMode: "check",
        infoTitle: "房间详情",
      },
      checkInInfoData: {
        name: '',
        idCard: '',
        phone: '',
        email: '',
        type: ''
      },
      pickerOptions: {
          disabledDate(v){
              return v.getTime() < new Date().getTime() - 86400000;
          }
      },      
      form: {
        id: undefined,
        name: undefined,
        type: undefined,
        floor: undefined,
        direction: undefined,

        rtName: undefined,
        maxLoad: undefined,
        bed: undefined,
        haveWindow: undefined,
        network: undefined,
        haveBreakfast: undefined,
        sort: undefined,
        fee: undefined,
        bedType: undefined,

        bedName: undefined,
        specification: undefined,
        bedMaxLoad: undefined,
        hardness: undefined,
      },

      checkInForm: {
        customerId: undefined,
        orderId: undefined,
        province: undefined,
        orderSwitch: false,
        estimatedCheckOut: undefined
      },
      provinceList: [
        {
          value: '北京市',
          label: '北京市'
        }, {
          value: '上海市',
          label: '上海市'
        }, {
          value: '天津市',
          label: '天津市'
        }, {
          value: '重庆市',
          label: '重庆市'
        }, {
          value: '河北省',
          label: '河北省'
        }, {
          value: '山西省',
          label: '山西省'
        }, {
          value: '内蒙古自治区',
          label: '内蒙古自治区'
        }, {
          value: '辽宁省',
          label: '辽宁省'
        }, {
          value: '吉林省',
          label: '吉林省'
        }, {
          value: '黑龙江省',
          label: '黑龙江省'
        }, {
          value: '江苏省',
          label: '江苏省'
        }, {
          value: '浙江省',
          label: '浙江省'
        }, {
          value: '安徽省',
          label: '安徽省'
        }, {
          value: '福建省',
          label: '福建省'
        }, {
          value: '江西省',
          label: '江西省'
        }, {
          value: '山东省',
          label: '山东省'
        }, {
          value: '河南省',
          label: '河南省'
        }, {
          value: '湖北省',
          label: '湖北省'
        }, {
          value: '湖南省',
          label: '湖南省'
        }, {
          value: '广东省',
          label: '广东省'
        }, {
          value: '广西壮族自治区',
          label: '广西壮族自治区'
        }, {
          value: '海南省',
          label: '海南省'
        }, {
          value: '四川省',
          label: '四川省'
        }, {
          value: '贵州省',
          label: '贵州省'
        }, {
          value: '云南省',
          label: '云南省'
        }, {
          value: '西藏自治区',
          label: '西藏自治区'
        }, {
          value: '陕西省',
          label: '陕西省'
        }, {
          value: '甘肃省',
          label: '甘肃省'
        }, {
          value: '宁夏回族自治区',
          label: '宁夏回族自治区'
        }, {
          value: '青海省',
          label: '青海省'
        }, {
          value: '新疆维吾尔族自治区',
          label: '新疆维吾尔族自治区'
        }, {
          value: '台湾省',
          label: '台湾省'
        }, {
          value: '香港',
          label: '香港'
        }, {
          value: '澳门',
          label: '澳门'
        }],
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
    }
  },
  created() {
    this.initWebSocket()
  },
  mounted() {
    this.getRoomList()
    this.getFloorList()
    this.getRoomTypeList()
  },
  destroyed() {
    this.ws.close() //离开路由之后断开websocket连接
  },
  methods: {
    websocketonopen() { //连接建立之后执行send方法发送数据
      // let actions = {"test":"12345"};
      // this.websocketsend(JSON.stringify(actions));
    },
    websocketonerror() {//连接建立失败重连
      this.initWebSocket();
    },
    websocketonmessage(e) { //数据接收,e.data数据有时不是JSON格式
    let redata
      if (isJSON(e.data)) {
        redata = JSON.parse(e.data); // 个别数据不是JSON格式数据
      } else {
        redata = e.data
      }
      // const redata = JSON.parse(e.data); // 个别数据不是JSON格式数据
      // const redata = e.data
      console.log("redata",redata);
      for (let i = 0; i < this.roomList.length; i++) {
        if (this.roomList[i].id == redata.id) {
          this.roomList[i].status = redata.status
          this.roomList[i].isIsolation = redata.isIsolation
        }
      }
      this.$forceUpdate()
    },
    websocketsend(Data) {//数据发送
      this.websock.send(Data);
    },
    websocketclose(e) {  //关闭
      console.log('断开连接', e);
    },
    initWebSocket() { //初始化weosocket
      let token = localStorage.getItem('Token')
      let tokenModify = token.split(' ')[1]
      this.ws = new WebSocket('ws://106.52.219.171:8188/wsServer?Authentication=' + tokenModify)
      // this.ws = new WebSocket(`ws://106.52.219.171:8105/wsServer?Authentication=${tokenModify}`)
      this.ws.onmessage = this.websocketonmessage;
      this.ws.onopen = this.websocketonopen;
      this.ws.onerror = this.websocketonerror;
      this.ws.onclose = this.websocketclose;
    },

    getRoomList() {
      let data = {
        floor: this.floorNum,
        name: this.roomNum,
        type: this.roomType,
        isIsolation: this.isIsolation
      }
      post('/api/room/getAllList', data)
          .then(res => {
            console.log(res);
            this.roomList = res.data.data
          })
          .catch(err => {
            console.log(err);
          })
    },

    getFloorList() {
      get("/api/room/floor").then(res => {
        this.floorList = res.data.data
      })
    },

    getRoomTypeList() {
      let data = {
        page: {
          page: 1,
          size: 999999
        },
      }
      post("/api/roomType/page",data).then(res => {
        this.roomTypeList = res.data.data.records
      })
    },

    isolationCheckOut(val) {
      let data = {
        status:val,
        roomId: this.current.id
      }
      formDataPost("api/room/isolationCheckOut",data).then(res => {
        if (res.data.code === "200") {
          this.$notify.success({
            title: '成功',
            message: "退房成功"
          });
        }else{
          this.$message({
            message: res.data.msg,
            type: 'error'
          });
        }
      })
    },
    changeRoomHandler() {
      this.newRoomId = undefined
      this.changeRoomVisible = true
      let data = {
        hotelId: this.current.hotelId,
        isIsolation: this.current.isIsolation
      }
      post("api/room/getAllList",data).then(res => {
        console.log(res)
        this.ableUseRoom = res.data.data
      })
    },
    changeRoom() {
      let data = {
        currentRoomId: this.current.id,
        newRoomId: this.newRoomId
      }
      formDataPost("api/room/changeRoom",data).then(res => {
        if (res.data.code === "200") {
          this.$notify.success({
            title: '成功',
            message: "换房成功"
          });
          this.changeRoomVisible = false
        }else{
          this.$message({
            message: res.data.msg,
            type: 'error'
          });
        }
      })
    },
    cleanRoom() {
      get("api/room/cleanRoom/"+this.current.id).then(res => {
        if (res.data.code === "200") {
          this.$notify.success({
            title: '成功',
            message: "房间已成功消毒!"
          });
          this.infoVisible = false
          // this.getRoomList()
        }else {
          this.$notify.error({
            title: '错误',
            message: res.data.msg
          });
        }
      })
    },
    checkInInfo() {
      this.checkInInfoData = {
        name: '',
        idCard: '',
        phone: '',
        email: '',
        type: ''
      }
      get("api/room/checkInfo/"+this.current.id).then(res => {
        if (res.data.code === "200") {
          this.checkInInfoData = res.data.data
          this.checkInInfoVisible = true
        }else {
          this.$notify.error({
            title: '错误',
            message: res.data.msg
          });
        }
      })
    },
    roomHandle(item) {
      // alert(item.id)
      this.current = item

      this.roomHandleVisible = true
    },

    confirmEdit() {
      this.form.id = this.current.id
      if(this.form.isIsolation) {
        this.form.isIsolation = 1
      }else {
        this.form.isIsolation = 0
      }
      let data = this.form
      post("/api/room/modify",data).then(res => {
        if (res.data.code === "200") {
          this.$notify.success({
            title: '成功',
            message: "修改成功"
          });
          this.infoVisible = false
          // this.getRoomList()
        }else {
          this.$notify.error({
            title: '错误',
            message: res.data.msg
          });
        }
      })
    },

    checkInHandler() {
      this.checkInForm = {
        id: this.current.id,
        customerId: undefined,
        orderId: undefined,
        province: undefined,
        orderSwitch: false,
        estimatedCheckOut: undefined
      }
      this.checkInVisible = true
    },

    checkOutHandler() {
      let that = this
      get("/api/room/checkOut/" + this.current.id).then(res => {
        that.roomHandleVisible = false
        console.log(res);      
        if (res.data.code == 200) {                                                                        
          this.$message({
              message: '订单完成！',
              type: 'success'
          });
        } else {                                      
          this.$message({
              message: res.data.msg,
              type: 'error'
          });
        }
        // alert(res.data.data)
      })
    },

    checkIn() {
      let data = this.checkInForm
      let that = this
      post("/api/room/checkIn",data).then(res => {
        if (res.data.code === "200") {
          let fee = res.data.data.pay
          let lastFee = res.data.data.lastPay
          let orderId = res.data.data.id
          that.roomHandleVisible = false
          that.checkInVisible = false
          console.log(res);
          this.$message({
              message: `<p>订单号:${orderId}</p><p>总价:${fee}元</p><p>折后价:${lastFee}元</p>`,
              type: 'success',
              customClass: 'order-cost',
              dangerouslyUseHTMLString: true,
              offset: 180,
              duration: 5000
          });          
          // alert("订单号:" + orderId + "\n总价:" + fee + "元\n折后价:" + lastFee + "元")
        } else {
          this.$message({
            message: res.data.msg,
            type: 'error',
            duration: 2000
          });
        }

      })
    },

    loadOrder() {
      get("/api/order/get/" + this.checkInForm.orderId).then(res => {
        this.checkInForm.customerId = res.data.data.customerId
        this.checkInForm.province = res.data.data.province
        let time = this.dateFormat("YYYY-mm-dd", new Date(res.data.data.estimatedCheckOut))
        this.checkInForm.estimatedCheckOut = time
        console.log("this.checkInForm", this.checkInForm)
      }).catch(err => {
        // console.log(err)
        this.$notify.error({
          title: '错误',
          message: "找不到该订单"
        });
      })
    },

    dateFormat(fmt, date) {
      let ret;
      const opt = {
        "Y+": date.getFullYear().toString(),        // 年
        "m+": (date.getMonth() + 1).toString(),     // 月
        "d+": date.getDate().toString(),            // 日
        "H+": date.getHours().toString(),           // 时
        "M+": date.getMinutes().toString(),         // 分
        "S+": date.getSeconds().toString()          // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
      };
      for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
          fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
        }
        ;
      }
      ;
      return fmt;
    },

    checkRoom() {
      this.current.infoMode = "check"
      this.current.infoTitle = "房间信息详情"
      console.log("this.current.id",this.current.id)
      get("/api/room/getDetail/" + this.current.id).then(res => {
        console.log("getDetail",res)
        this.form = res.data.data
      })
      this.infoVisible = true
    },

    editRoom() {
      this.current.infoMode = "edit"
      this.current.infoTitle = "房间信息编辑"
      // get("/api/room/getDetail/" + this.current.id).then(res => {
      get(`/api/room/getDetail/${this.current.id}`) // ES6写法
        .then(res => {
          console.log("getDetail",res)
          this.form = res.data.data
          if(this.form.isIsolation == 1) {
            this.form.isIsolation = true
          } else {
            this.form.isIsolation = false
          }
        })
        .catch( err => {
          console.error(err);
        })
      this.infoVisible = true // 
    },

    reset() {
      this.roomType = ''
      this.roomNum = ''
      this.floorNum = ''
      this.isIsolation = ''
    }

    // communication(){
    //     let token = localStorage.getItem('Token')
    //     let tokenModify = token.split(' ')[1]
    //     this.ws = new WebSocket('ws://8.130.179.210:8081/wsServer?Authentication=' + tokenModify)
    //
    //     // this.ws.onmessage = this.websocketonmessage;
    //     console.log(this.ws);
    //     //查看当前webSocket连接状态
    //     //0:正在连接
    //     //1:连接成功
    //     //2:连接正在关闭
    //     //3:连接已经关闭 或 打开连接失败
    //     console.log(this.ws.readyState);
    //
    //     //连接成功的回调函数
    //     this.ws.onopen = function (res) {
    //         console.log(res);
    //     }
    //
    //     //收到服务器数据后的回调函数
    //     this.ws.onmessage = function (res) {
    //         console.log(res);
    //     }
    //
    //     //连接关闭后的回调函数
    //     this.ws.onclose = function () {
    //
    //     }
    //
    //     //向服务器发送数据
    //     // ws.send()
    //
    //     //关闭连接
    //     // ws.close()
    // },
  }
}
</script>

<style scoped>
.room-manage-main {
  display: flex;
  flex-direction: column;
  width: 100%;
  min-height: calc(100vh - 120px);
  /*height: 100%;*/
  background: #FFFFFF;
  box-shadow: 0px 5px 30px 0px rgba(22, 115, 255, 0.1);
}

.room-list {
  padding: 10px 20px;
}

.room-list li {
  /*margin: 10px;*/
  position: relative;
  color: gray;
}

.room-list li > span {
  font-size: 220px;
}

.room-list li h3 {
  font-size: 22px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  top: 23%;
}

.room-list li h5 {
  font-size: 18px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  top: 38%;
}

.room-list li p {
  font-size: 14px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  top: 66%;
}

.status-0 {
  color: #67C23A;
}

.status-1 {
  color: #F56C6C;
}

.status-2 {
  color: #E6A23C;
}

.status-3 {
  color: purple;
}

.status-4 {
  color: #909399;
}

.contont {
  margin: 10px 60px 30px 36px;
}


</style>
