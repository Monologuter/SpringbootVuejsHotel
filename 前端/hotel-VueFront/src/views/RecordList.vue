<template>

  <div style="min-height: 100vh">
    
    <!-- 导航栏 -->
    <top-nav></top-nav>

    <div class="hotel-list-bg d-flex align-items-center justify-content-center position-relative">
      <img src="../assets/images/hotel-list-bg.png" alt="" />
    </div>

    <div class="record-list-main">

      <div class="record-list-content">

        <div class="record-list-content-option d-flex align-items-center justify-content-between">

          <div
            :class="isIsolation ? 'active' : ''"
            class="order-status cursor"
            @click="isolationRecordsHandle(!isIsolation)">

            <img :src="require(`../assets/images/record1.png`)" alt="">

            <div>
              <h3 class="font-18">{{ $t('recordList.isolateRecords') }}</h3>
              <p class="font-16">{{ isolationNum}}</p>
            </div>

          </div>

          <div
            :class="isReview ? 'active' : ''"
            class="order-status cursor"
            @click="reviewRecordsHandle(!isReview)">

            <img :src="require(`../assets/images/record5.png`)" alt="">

            <div>
              <!-- <h3 class="font-22">自申报记录</h3> -->
              <h3 class="font-18">{{ $t('recordList.isolateRequestRecords') }}</h3>
              <p class="font-16">{{ reviewListNum }}</p>
            </div>

          </div>          

          <div
            :class="isApply ? 'active' : ''"
            class="order-status cursor"
            @click="applyRecordsHandle(!isApply)">

            <img :src="require(`../assets/images/material.png`)" alt="">

            <div>
              <h3 class="font-18">{{ $t('recordList.materialRequestRecords') }}</h3>
              <p class="font-16">{{ applyListNum }}</p>
            </div>

          </div>

        </div>

        <!-- 隔离记录 -->
        <div v-if="isIsolation" class="d-flex flex-column justify-content-between">

          <ul class="order-list">
            <li v-for="(item, index) in isolationList">
              <div class="d-flex align-items-center justify-content-between mb-15 font-20">
                <p>{{ item.status | isolationStatusFilter }}</p>
                <div>
                  <el-button
                    style="
                      min-width: 14rem;
                      
                      height: 4rem;
                      padding: 0 1rem;
                      font-size: 1.6rem;"
                    @click="getCheckRecords(item.id)">{{ $t('recordList.detectRecords') }}</el-button>
                  <el-button
                    style="
                      min-width: 14rem;
                      
                      height: 4rem;
                      padding: 0 1rem;
                      font-size: 1.6rem;"                  
                    v-if="item.status == 0"
                    type="primary"
                    @click="applyHandle(item.id)">{{ $t('recordList.materialRequest') }}</el-button>
                </div>
              </div>
              <div class="d-flex align-items-center justify-content-between font-16 color-6">
                <div>
                  <p class="mb-10">
                    {{ $t("orderList.hotelName") }} {{ item.hotelName }}
                  </p>

                  <div class="flex flex-row align-items-center mb-10">
                    <p class="mr-10">{{ $t("recordList.isolateID") }} {{ item.id }}</p>
                    <p class="ml-10 cursor" @click="openBarcode(item.id)"><i class="el-icon-full-screen flex flex-row align-items-end"><span class="font-14">&nbsp;条形码</span></i></p>
                  </div>

                  <p class="isolation-time mb-10">
                    <span class="mr-10">{{ $t("recordList.isolateStartTime") }} {{ item.checkInTime | dateTimeFormat }}</span>
                    <span>{{ $t("recordList.isolateEndTime") }} {{ item.checkOutTime | dateTimeFormat }}</span>
                  </p>
                  <p class="mb-10">{{ $t("recordList.source") }} {{ item.province }}{{ item.city }}</p>
                  <div class="d-flex align-items-center">
                    <p>{{ $t("orderList.roomType") }} {{ item.roomTypeName | roomNameFormat }}</p>
                    <p class="ml-10 mr-10" style="color: #e0e0e0">|</p>
                    <p>{{ $t("isolationList.roomName") }} {{ item.roomName }}</p>
                  </div>
                </div>
                <p v-if="item.pay">{{ $t("orderList.spend") }}￥{{ item.pay }}</p>
              </div>
            </li>
          </ul>

          <div v-show="pageNum > 5" class="isolation-pagination d-flex align-items-center justify-content-center">
            <el-pagination
                :small="smallPagination"
                background
                :current-page.sync="currentPage1"
                :page-size="5"
                :pager-count="5"
                @current-change="isolationCurrent"
                @prev-click="isolationPrev"
                @next-click="isolationNext"
                layout="prev, pager, next"
                :total="isolationNum">
            </el-pagination>
          </div>

        </div>

        <!-- 自申报记录 -->
        <!-- 隔离入住申请记录 -->
        <div v-else-if="isReview" class="d-flex flex-column justify-content-between">
          <ul class="order-list">
            <li v-for="(item, index) in reviewList">
              <div class="d-flex align-items-center justify-content-between mb-15 font-20">
                <p>{{ item.reviewStatus | applyStatusFilter }}</p>
              </div>
              <div class="d-flex align-items-center justify-content-between font-16 color-6">
                <div>
                  <p class="mb-10">{{ $t('recordList.applicantName') }} {{ item.name }}</p>
                  <p class="mb-10">{{ $t('recordList.idNum') }} {{ item.idCard }}</p>
                  <p class="mb-10">{{ $t('recordList.tel') }} {{ item.phone }}</p>
                  <div class="flex flex-row align-items-center mb-10">
                    <p class="mr-10">{{ $t('recordList.declareID') }} {{ item.id }}</p>
                    <p class="ml-10 cursor" @click="openBarcode(item.id)"><i class="el-icon-full-screen flex flex-row align-items-end"><span class="font-14">&nbsp;条形码</span></i></p>
                  </div>
                  <p class="mb-10">{{ $t('recordList.source') }} {{ item.province }}{{ item.city }}</p>
                  <p class="isolation-time mb-10">
                    <span class="mr-10">{{ $t('recordList.isolateStartTime') }} {{ item.checkInTime | dateTimeFormat }}</span>
                    <span>{{ $t('recordList.isolateEndTime') }} {{ item.checkOutTime | dateTimeFormat }}</span>
                  </p>
                  <p v-if="item.reviewStatus == 2" style="color: red;" class="mb-10">{{ $t('recordList.refuse') }} {{ item.remark }}</p>
                </div>
              </div>
            </li>
          </ul>

          <div v-show="reviewListNum > 5" class="apply-pagination d-flex align-items-center justify-content-center">
            <el-pagination
                :small="smallPagination"
                background
                :current-page.sync="currentPage2"                
                :page-size="5"
                :pager-count="5"
                @current-change="reviewCurrent"
                @prev-click="reviewPrev"
                @next-click="reviewNext"
                layout="prev, pager, next"
                :total="reviewListNum">
            </el-pagination>
          </div>

        </div>        

        <!-- 物资申请记录 -->
        <div v-else-if="isApply" class="d-flex flex-column justify-content-between">
          <ul class="order-list">
            <li v-for="(item, index) in applyList">
              <div class="d-flex align-items-center justify-content-between mb-15 font-20">
                <p>{{ item.reviewStatus | applyStatusFilter }}</p>
              </div>
              <div class="d-flex align-items-center justify-content-between font-16 color-6">
                <div>
                  <p class="mb-10">{{ $t('recordList.appliedItems') }} {{ item.applyThing }}</p>
                  <p class="mb-10">{{ $t('recordList.appliedNum') }} {{ "" + item.applyNum + item.thingUnit }}</p>
                  <p class="mb-10">{{ $t('recordList.appliedID') }} {{ item.id }}</p>
                  <p class="mb-10">{{ $t('recordList.appliedInstructions') }} {{ item.applyRemark }}</p>
                  <p style="color: red" v-if="item.reviewRemarks" class="mb-10">{{ $t('recordList.auditInstructions') }} {{ item.reviewRemarks }}</p>
                </div>
                <p v-if="item.pay">{{ $t("orderList.spend") }}￥{{ item.pay }}</p>
              </div>
            </li>
          </ul>

          <div v-show="applyListNum > 5" class="material-pagination d-flex align-items-center justify-content-center">
            <el-pagination
                :small="smallPagination"
                background
                :current-page.sync="currentPage3"
                :page-size="5"
                :pager-count="5"
                @current-change="applyCurrent"
                @prev-click="applyPrev"
                @next-click="applyNext"
                layout="prev, pager, next"
                :total="applyListNum">
            </el-pagination>
          </div>

        </div>


      </div>

      <!-- 暂无订单，改为暂无记录 -->
      <!-- <div v-show="orderList.length == 0" class="order-null text-center font-30">{{ $t('orderList.noOrder') }}</div> -->

      <Footer></Footer>

      <!-- 检测记录dialog -->
      <el-dialog
          class="web-detect-record"
          :title="$t('recordList.detectRecords')"
          :visible.sync="checkVisible"
          width="570px">
        <el-table :data="ownTestData">
          <el-table-column
            property="checkTime"
            :label="$t('recordList.detectionTime')"
            :formatter="eTableDateTime"
            align="center"
            width="250px">
          </el-table-column>
          <el-table-column
            property="checkType"
            :label="$t('recordList.detectionType')"
            align="center">
            <template slot-scope="scope">
                <el-tag v-if="scope.row.checkType==0" type="danger">{{ $t('recordList.temperature') }}</el-tag>
                <el-tag v-if="scope.row.checkType==1" >{{ $t('recordList.nucleicAcidDetection') }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column 
            property="checkRes"
            :label="$t('recordList.DetectionResults')"
            align="center">
            <template slot-scope="scope">
                <el-tag v-if="scope.row.checkRes==0">{{ $t('recordList.negative') }}</el-tag>
                <el-tag v-else-if="scope.row.checkRes==1" type="danger">{{ $t('recordList.positive') }}</el-tag>
                <el-tag v-else type="danger">{{ scope.row.checkRes }}°</el-tag>
            </template>           
          </el-table-column>
        </el-table>
      </el-dialog>

      <!-- 检测记录dialog 移动端 -->
      <el-drawer
        :title="$t('recordList.detectRecords')"
        custom-class="app-detect-record"
        :visible.sync="checkVisibleApp"
        direction="btt">
        <ul class="app-detect-record-list">
          <li v-for="i in ownTestData">
            <div class="d-flex justify-content-between font-24 mb-15">
              <p>{{ $t('recordList.detectionTime') }}</p>
              <p>{{ i.checkTime | dateTimeFormat }}</p>
            </div>
            <div class="d-flex justify-content-between font-24 mb-15">
              <p>{{ $t('recordList.detectionType') }}</p>
              <p>{{ i.checkType==0?$t('recordList.temperature'):$t('recordList.nucleicAcidDetection') }}</p>
            </div>            
            <div class="d-flex justify-content-between font-24 mb-15">
              <p>{{ $t('recordList.DetectionResults') }}</p>
              <p class="color-70d" v-if="i.checkRes==0">{{ $t('recordList.negative') }}</p>
              <p class="color-red" v-if="i.checkRes==1">{{ $t('recordList.positive') }}</p>
            </div>            
          </li>
        </ul>                                                    
      </el-drawer>      

      <!-- 物资申请dialog -->
      <el-dialog
          :title="$t('recordList.materialApplication')"
          class="web-material-apply"
          :visible.sync="applyVisible"
          width="570px">
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">{{ $t('recordList.applyMaterial') }}</p>
          <el-input
              style="width: 350px;"
              :placeholder="$t('common.inputTips')"
              v-model="applyForm.applyThing"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">{{ $t('recordList.appliedNum') }}</p>
          <el-input-number
              v-model="applyForm.applyNum"
              :precision="2"
              :step="0.1"
              :max="10"
              style="width: 350px;"
              :placeholder="$t('common.inputTips')"
              clearable
          ></el-input-number>

        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">{{ $t('recordList.applyUnit') }}</p>
          <el-input
              style="width: 350px;"
              :placeholder="$t('common.inputTips')"
              v-model="applyForm.thingUnit"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">{{ $t('recordList.Urgency') }}</p>
          <el-select style="width: 350px;" v-model="applyForm.emergencyLevel" :placeholder="$t('common.inputTips')">
            <el-option v-for="item in emergencyLevelOptions" :label="item.label | urgency" :value="item.value"></el-option>
          </el-select>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">{{ $t('recordList.applyRemarks') }}</p>
          <el-input
              style="width: 350px;"
              type="textarea"
              :placeholder="$t('common.inputTips')"
              v-model="applyForm.applyRemark"
              maxlength="70"
              show-word-limit
          >
          </el-input>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="applyVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="confirmApply">{{ $t('common.confirm') }}</el-button>
        </span>

      </el-dialog>      

      <!-- 物资申请dialog 移动端 -->
      <el-drawer
        :title="$t('recordList.materialApplication')"
        custom-class="app-material-apply"
        :visible.sync="applyVisibleApp"
        direction="btt">
        <ul class="material-apply-list">
          <li class="d-flex align-items-center mb-10">
            <p class="w-80 text-left font-24">{{ $t('recordList.applyMaterial') }}</p>
            <el-input
                style="width: calc(100% - 80px)"
                :placeholder="$t('common.inputTips')"
                v-model="applyForm.applyThing"
                clearable>
            </el-input>            
          </li>
          <li class="d-flex align-items-center mb-10">
            <p class="w-80 text-left font-24">{{ $t('recordList.appliedNum') }}</p>
            <el-input
                style="width: calc(100% - 80px)"
                :placeholder="$t('common.inputTips')"
                v-model="applyForm.applyNum"
                clearable>
            </el-input>            
          </li>
          <li class="d-flex align-items-center mb-10">
            <p class="w-80 text-left font-24">{{ $t('recordList.applyUnit') }}</p>
            <el-input
                style="width: calc(100% - 80px)"
                :placeholder="$t('common.inputTips')"
                v-model="applyForm.thingUnit"
                clearable>
            </el-input>            
          </li>
          <li class="d-flex align-items-center mb-10">
            <p class="w-80 text-left font-24">{{ $t('recordList.Urgency') }}</p>
            <el-select
                style="width: calc(100% - 80px)"
                :placeholder="$t('common.inputTips')"
                v-model="applyForm.emergencyLevel">
              <el-option v-for="item in emergencyLevelOptions" :label="item.label | urgency" :value="item.value"></el-option>
            </el-select>            
          </li>
          <li class="d-flex align-items-center mb-10">
            <p class="w-80 text-left font-24">{{ $t('recordList.applyRemarks') }}</p>
            <el-input
                style="width: calc(100% - 80px)"
                type="textarea"
                :placeholder="$t('common.inputTips')"
                v-model="applyForm.applyRemark"
                maxlength="70"
                show-word-limit>
            </el-input>         
          </li>          
        </ul>
        <div class="d-flex align-items-end justify-content-center mt-10 mb-15">
          <el-button type="primary" @click="confirmApply">{{ $t('common.confirm') }}</el-button>
        </div>
      </el-drawer>         

    </div>

    <el-dialog
        class="wallet-dialog"
        :visible.sync="barcodeVisible"
        width="95%"
        center
    >
      <div class="m-auto">
        <barcode :value="barcodeValue" class="m-auto">
          ID获取异常,请重新打开!
        </barcode>
      </div>


    </el-dialog>
  </div>
</template>

<script>
import { get, post } from "../utils/request";
import TopNav from "../components/TopNav";
import Footer from "../components/Footer.vue";
import VueBarcode from 'vue-barcode';

export default {
  name: "RecordList",
  components: {
    TopNav,
    Footer,
    'barcode': VueBarcode
  },
  data() {
    return {
      barcodeVisible: false,
      barcodeValue: undefined,
      currentPage1: 1,
      currentPage2: 1,
      currentPage3: 1,
      smallPagination: false,
      checkVisible: false,
      checkVisibleApp: false,
      applyVisible: false,
      applyVisibleApp: false,
      // orderList: [],
      isolationList: [],
      testOptions: [
          {
              value: 0,
              label: '体温'
          },
          {
              value: 1,
              label: '核酸'
          }
      ],
      resultOptions: [
          {
              value: 0,
              label: '阴性'
          },
          {
              value: 1,
              label: '阳性'
          }
      ],      
      applyList: [],
      pageNum: 0,
      isolationNum: 0,
      reviewListNum: 0,
      reviewList: [],
      // pageNum: 0,
      statusList: [],
      isIsolation: true,
      isApply: false,
      isReview: false,
      applyForm: {
        applyThing: "",
        applyNum: 0,
        thingUnit: "",
        emergencyLevel: "",
        applyRemark: "",
      },
      emergencyLevelOptions: [
        {
          value: 0,
          label: "不紧急",
        },
        {
          value: 1,
          label: "普通紧急",
        },
        {
          value: 2,
          label: "非常紧急",
        },
      ],
      statusTextList: ["未支付", "已支付", "已关闭"],
      check: {
        name: '',
        checkType: '',
        checkRes: '',
        checkTime: '',
      },
      form: {},
      ownTestData: [],
      applyListNum: 0,
    };
  },
  filters: {
    applyStatusFilter(value) {
      const lang = localStorage.getItem("lang");
      if (lang == "zh" || lang == null) {
        switch (value) {
          case 0:
            value = "未审核";
            break;
          case 1:
            value = "已同意";
            break;
          case 2:
            value = "已拒绝";
            break;
          default:
            value = "已拒绝";
            break;
        }
        return value;
      } else if (lang == "en") {
        switch (value) {
          case 0:
            value = "Not reviewed";
            break;
          case 1:
            value = "Approved";
            break;
          case 2:
            value = "Rejected";
            break;
          default:
            value = "Rejected";
            break;
        }
        return value;
      }
    },
    isolationStatusFilter(value) {
      const lang = localStorage.getItem("lang");
      if (lang == "zh" || lang == null) {
        switch (value) {
          case 0:
            value = "正在隔离";
            break;
          case 1:
            value = "解除隔离";
            break;
          case 2:
            value = "阳性入院";
            break;
          default:
            value = "阳性入院";
            break;
        }
        return value;
      } else if (lang == "en") {
        switch (value) {
          case 0:
            value = "Isolating";
            break;
          case 1:
            value = "Free";
            break;
          case 2:
            value = "Positive";
            break;
          default:
            value = "Positive";
            break;
        }
        return value;
      }
    },
    statusFilter(value) {
      const lang = localStorage.getItem("lang");
      if (lang == "zh" || lang == null) {
        switch (value) {
          case "0":
            value = "未支付";
            break;
          case "1":
            value = "已支付";
            break;
          case "2":
            value = "已关闭";
            break;
          case "3":
            value = "已入住";
            break;
          default:
            value = "已完成";
            break;
        }
        return value;
      } else if (lang == "en") {
        switch (value) {
          case "0":
            value = "Unpaid";
            break;
          case "1":
            value = "Paid";
            break;
          case "2":
            value = "Closed";
            break;
          case "3":
            value = "Checked in";
            break;
          default:
            value = "Completed";
            break;
        }
        return value;
      }
    },
    roomNameFormat(val) {
      const lang = localStorage.getItem("lang");
      if (lang == "zh"  || lang == null) {
        return val;
      } else if (lang == "en") {
        if (val == "单人房") {
          return (val = "Single-bed room");
        } else if (val == "豪华大床房") {
          return (val = "Deluxe single Room");
        } else if (val == "双人房") {
          return (val = "Double room");
        } else if (val == "总统套房") {
          return (val = "Presidential suite");
        } else if (val == "经济房") {
          return (val = "Economy Room");
        } else {
          return val;
        }
      }
    },
    urgency(value){
      const lang = localStorage.getItem("lang");
      if (lang == "zh"  || lang == null) {
        return value
      } else if (lang == "en") {
        switch (value) {
          case "不紧急":
            value = "Not urgent";
            break;
          case "普通紧急":
            value = "Urgent";
            break;
          case "非常紧急":
            value = "Very urgent";
            break;
          default:
            break;
        }
        return value;        
      }
    }
  },
  mounted() {
    this.getIsolationRecords()
    this.getApplyRecords()
    this.getReviewList()
    if (window.innerWidth < 768) { /*  滚动条17px */
      this.smallPagination = true
    } else {
      this.smallPagination = false
    }    
    window.onresize = () => {
      if (window.innerWidth < 768) { /*  滚动条17px */
        this.smallPagination = true
      } else {
        this.smallPagination = false
      }
    }    
  },
  methods: {
    openBarcode(id) {
      this.barcodeValue = id
      this.barcodeVisible = true
    },
    // 隔离记录 Start
    // 点击隔离记录大按钮函数
    isolationRecordsHandle(val) {
      this.isIsolation = val;
      if (val) {
        this.isApply = false;
        this.isReview = false;
        this.currentPage1 = 1;
        this.getIsolationRecords();
      }
    },
    // 页面渲染
    getIsolationRecords() {
      let data = {
        page: {
          page: 1,
          size: 5,
        },
      };
      this.isolationRecordsRequest(data);
    },
    //获取隔离列表请求
    isolationRecordsRequest(data) {
      post("/api/isolationInfo/my", data)
        .then((res) => {
          console.log(res);
          this.isolationList = res.data.data.records;
          this.isolationNum = res.data.data.total;
        })
        .catch((err) => {
          console.log(err);
        });
    },

    //选择页码
    isolationCurrent(num) {
      let data = {
        page: {
          page: num,
          size: 5
        }
      }
      this.isolationRecordsRequest(data)
    },

    //上一页
    isolationPrev(num) {
      let data = {
        page: {
          page: num,
          size: 5
        }
      }
      this.isolationRecordsRequest(data)
    },

    //下一页
    isolationNext(num) {
      let data = {
        page: {
          page: num,
          size: 5
        }
      }
      this.isolationRecordsRequest(data)
    },     

    // 获取检测记录函数
    getCheckRecords(id) {
      let data = {
        uId: id,
        page: {
          page: 1,
          size: 5,
        },
      };
      post("api/checkRecords/ownPage", data)
        .then((res) => {
          console.log(res);
          this.ownTestData = res.data.data.records
          if (window.innerWidth >= 768) {
            this.checkVisible = true
          } else {
            this.checkVisibleApp = true
          }
        })
        .catch((err) => {
          console.error(err);
        });
    },

    // 物资申请函数，打开弹出框
    applyHandle(id) {
      if (window.innerWidth >= 768) {
        this.applyVisible = true;
      } else {
        this.applyVisibleApp = true
      }
      this.applyForm = {
        applyThing: "",
        applyNum: 0,
        thingUnit: "",
        emergencyLevel: "",
        applyRemark: "",
        uType: 1,
        isolationId:id
      };
    },    

    // 弹出框申请物资确认按钮函数
    confirmApply() {
      let data = this.applyForm;
      post("api/materialsApply/apply", data).then((res) => {
        if (res.data.code === "200") {
          this.$notify.success({
            title: "成功",
            message: "申请成功",
          });
          if (window.innerWidth >= 768) {
            this.applyVisible = false;
          } else {
            this.applyVisibleApp = false
          }
        } else {
          this.$message({
            message: res.data.msg,
            type: "error",
          });
        }
      });
    },
    // 隔离记录 End

    // 自申报记录 Start
    // 点击函数
    reviewRecordsHandle(val) {
      this.isReview = val;
      if (val) {
        this.isIsolation = false;
        this.isApply = false;
        this.currentPage2 = 1;
        this.getReviewList();
      }
    },

    // 列表获取函数
    getReviewList() {
      const data = {
        page: {
          page: 1,
          size: 5,
        }
      }
      this.reviewListRequest(data)
    },
    // 列表请求函数
    reviewListRequest(data) {
      post('/api/review/my',data)
        .then( res => {
          console.log(res);
          this.reviewList = res.data.data.records
          this.reviewListNum = res.data.data.total;
        })
        .catch( err => {
          console.error(err);
        })
    },
    //选择页码
    reviewCurrent(num) {
      let data = {
        page: {
          page: num,
          size: 5
        }
      }
      this.reviewListRequest(data)
    },

    //上一页
    reviewPrev(num) {
      let data = {
        page: {
          page: num,
          size: 5
        }
      }
      this.reviewListRequest(data)
    },

    //下一页
    reviewNext(num) {
      let data = {
        page: {
          page: num,
          size: 5
        }
      }
      this.reviewListRequest(data)
    }, 

    // 自申报记录 End

    // 物资申请记录 Start
    // 点击申请记录大按钮函数
    applyRecordsHandle(val) {
      this.isApply = val;
      if (val) {
        this.isIsolation = false;
        this.isReview = false;
        this.currentPage3 = 1
        this.getApplyRecords();
      }
    },

    // 获取申请记录函数
    getApplyRecords() {
      const data = {
        page: {
          page: 1,
          size: 5,
        }
      }
      this.ApplyRecordsRequest(data)
    },
    ApplyRecordsRequest(data) {
      post("api/materialsApply/page", data)
        .then( res => {
          console.log(res);
          this.applyList = res.data.data.records;      
          this.applyListNum = res.data.data.total;
        })
    },
        //选择页码
    applyCurrent(num) {
      let data = {
        page: {
          page: num,
          size: 5
        }
      }
      this.ApplyRecordsRequest(data)
    },

    //上一页
    applyPrev(num) {
      let data = {
        page: {
          page: num,
          size: 5
        }
      }
      this.ApplyRecordsRequest(data)
    },

    //下一页
    applyNext(num) {
      let data = {
        page: {
          page: num,
          size: 5
        }
      }
      this.ApplyRecordsRequest(data)
    },   
    // 申请记录 End

    // 日期时间格式化
    eTableDateTime(row, column, cellValue, index){
        const dateTime = new Date(cellValue) // Date实例

        const YYYY = dateTime.getFullYear() // 获取当前年份
        const MM = dateTime.getMonth() + 1 // 获取当前月份
        const DD = dateTime.getDate() // 获取当前天数
        const hh = this.fillPrefix(dateTime.getHours()) // 获取当前小时，并判断是否需要补零
        const mm = this.fillPrefix(dateTime.getMinutes()) // 获取当前分钟，并判断是否需要补零
        const ss = this.fillPrefix(dateTime.getSeconds()) // 获取当前秒数，并判断是否需要补零
        // 返回格式化之后的当前时间
        return `${YYYY}-${MM}-${DD} ${hh}:${mm}:${ss}`
        // return `${YYYY}-${MM}-${DD}`
    },

    // 补零函数
    fillPrefix(val){
        return val > 9 ? val : `0${val}` // 个位数时间进行补零操作
    },

  },
};
</script>

<style scoped>
.record-list-main {
  height: calc(100vh - 220px);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background-color: #ecf5ff;
}

.t-nav {
  width: 100%;
  height: 68px;
  background: #fff;
  opacity: 0.8;
  z-index: 999;
  position: fixed;
  top: 0;
  left: 0;
}

.nav-content {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 100%;
}

.nav-content ul {
  display: flex;
  align-items: center;
  height: 100%;
}

.nav-content li {
  font-size: 18px;
  margin: 0 30px;
}

.nav-bt {
  width: 123px;
  height: 34px;
  line-height: 34px;
  background: #28b535;
  border-radius: 16px;
  border: 0;
  color: #fff;
}

.nav-content a {
  color: #a1a09e;
}

.nav-content a:hover {
  color: #323230;
}

.hotel-list-bg {
  width: 100%;
  height: 220px;
}

.hotel-list-bg img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 220px;
}

.hotel-list-bg h3 {
  margin-bottom: 25px;
  font-size: 36px;
  font-weight: 400;
  color: #ffffff;
}

.hotel-list-content {
  display: flex;
  justify-content: center;
  padding-top: 30px;
}

.hotel-list {
}

.hotel-list li {
  display: flex;
  margin-bottom: 30px;
}

.hotel-list li .li-l {
  width: 370px;
  height: 423px;
  margin: 0px 30px;
}

.hotel-list li .li-l img {
  width: 100%;
  height: 100%;
}

.hotel-list li .li-r {
  width: 360px;
  margin: 0px 30px;
  position: relative;
}

.hotel-list li .li-r h4 {
  font-size: 36px;
  margin-bottom: 15px;
  font-weight: 400;
}

.hotel-list li .li-r h6 {
  font-size: 14px;
  margin-bottom: 28px;
  font-weight: 400;
}

.hotel-list li .li-r div {
  font-size: 15px;
  color: #898989;
  margin-bottom: 20px;
  overflow: auto;
  max-height: 206px;
}

.hotel-list li .li-r button {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 118px;
  height: 38px;
  line-height: 38px;
  background: #e1bd85;
  font-size: 12px;
  color: #ffffff;
  border-radius: 5px;
  border: none;
}

.hotel-list li:nth-child(even) .li-l {
  order: 2;
}

.record-list-content {
  /* margin: 20px auto 0px auto;
  width: 80vw; */

  padding: 30px 12%;
  background-color: #ecf5ff;
}

.order-status {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: white;
  /* border-radius: 15px; */
  border-radius: 1.5rem;
  flex: 1;
  /* height: 120px; */
  height: 12rem;
  /* margin: 0px 10px;
  padding: 0px 20px; */
  margin: 0 1rem;
  padding: 0 0.5rem;  
}

.order-status > img {
  width: 5rem;
  height: 5rem;
  margin-right: 1.6rem;  
}

.order-status > div {
  display: flex;
  flex-direction: column;
  /* height: 60px; */
  height: 6rem;
  justify-content: space-between;
}

.order-list {
  /* margin: 20px 10px; */
  margin: 20px 1rem;
}

.order-list li {
  /* padding: 15px 20px;
  margin-bottom: 10px; */
  padding: 1.5rem 2rem;
  margin-bottom: 1rem;
  background-color: white;
  border-radius: 15px;
  flex: 1;
}

.el-pagination.is-background .btn-next,
.el-pagination.is-background .btn-prev,
.el-pagination.is-background .el-pager li {
  background-color: #ffffff !important;
}

.active {
  border: 1px solid #d2d2d3;
  background: #f5f7fa;
}

/* 媒体查询 */
@media screen and (max-width: 767.9px) { /* 页面测试无法显示767，实际是767.2px */
  .hotel-list-bg {
    height: 120px;
  }
  .hotel-list-bg img {
    height: 120px;
  }
  .record-list-main {
    height: calc(100vh - 120px);
  }
  .record-list-content {
    padding: 10px 3%;
  }
  .record-list-content-option {
    flex-direction: column;
    align-items: start;
  }
  .order-status {
    width: 100%;
    margin: 0 0 1rem 0;
    box-sizing: border-box;
    flex: initial;
    justify-content: flex-start;
    padding-left: 20%;
  }
  .order-list {
    margin: 10px 0;
  }
  .isolation-time>span{
    display: block;
  }
  .order-list li {
    border-radius: 10px;
  }
  /* .isolation-pagination {
    display: none;
  }
  .apply-pagination {
    display: none;
  }
  .material-pagination {
    display: none;
  } */
  .app-detect-record-list {
    padding: 0 2rem;
    overflow: auto;
  }
  .app-detect-record-list li {
    border-bottom: 1px solid #999;
    margin-bottom: 1.5rem;
  }
  .material-apply-list {
    padding: 0 2rem;
    overflow: auto;
  }


  /* .web-detect-record { 正常情况下没隐藏，缩小到<768px则隐藏 
    display: none;
  } */
  /* .web-material-apply {
    display: none;
  }
  .app-detect-record {
    display: block;
  } */

}
</style>