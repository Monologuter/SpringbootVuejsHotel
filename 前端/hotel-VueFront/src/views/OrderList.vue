<template>
  <div style="min-height: 100vh;">

    <!-- 导航栏 -->
    <top-nav></top-nav>

    <div class="hotel-list-bg d-flex align-items-center justify-content-center position-relative">
      <img src="../assets/images/hotel-list-bg.png" alt="">
    </div>

    <div class="order-list-main">

      <div class="order-list-content">

        <div class="d-flex align-items-center justify-content-between">

          <div v-for="(item,index) in statusList" class="order-status">
            <img :src="require(`../assets/images/status_${item.status}.png`)" alt="">
            <div>
              <!-- <h3 class="font-22">{{ statusTextList[item.status] }}</h3> -->
              <h3 class="font-20">{{ item.status | statusFilter }}</h3>
              <p class="font-18">{{ item.counts }}</p>
            </div>
          </div>

        </div>

        <ul class="order-list">
          <li v-for="(item,index) in orderList">
            <div class="d-flex align-items-center justify-content-between mb-15 font-20">
              <p>{{ item.status | statusFilter }}</p>
              <a v-if="item.status == 1" class="color-red cursor"
                 @click="cancelOrder(item.id)">{{ $t('orderList.cancelOrder') }}</a>
<!--              //////-->
              <a v-if="item.status == 0" class="color-red cursor"
                 @click="goPayOrder(item.id,item.lastPay)">{{ $t('orderList.goPayOrder') }}</a>

              <a v-if="item.status == 4 && !item.commentId" class="color-red cursor"
                 @click="writeComment(item)">{{ $t('orderList.writeComment') }}</a>
            </div>
            <div class="order-details d-flex align-items-center justify-content-between font-16 color-6">
              <div>
                <p class="mb-10">{{ $t('orderList.hotelName') }}{{ item.hotelName }}</p>
                <!-- <p class="mb-10">订单ID：{{ item.id }}</p> -->
                <div class="flex flex-row align-items-center mb-10">
                  <p class="mr-10">{{ $t('orderList.orderId') }}{{ item.id }}</p>
                  <p class="ml-10 cursor" @click="openBarcode(item.id)"><i class="el-icon-full-screen flex flex-row align-items-end"><span class="font-14">&nbsp;条形码</span></i></p>
                </div>
                <div class="d-flex align-items-center">
                  <p>{{ $t('orderList.roomType') }}{{ item.roomTypeName | roomNameFormat }}</p>
                  <p class="ml-10 mr-10" style="color: #e0e0e0"> | </p>
                  <p>{{ $t('orderList.day') }} {{ item.days }} {{ $t('orderList.dayNum') }}</p>
                </div>
              </div>
              <p>{{ $t('orderList.spend') }}￥{{ item.lastPay }} </p>
            </div>
          </li>
        </ul>

        <div v-if="pageNum > 10" class="order-pagination d-flex align-items-center justify-content-center">
          <el-pagination
              :small="smallPagination"
              :pager-count="5"
              background
              @current-change="handleCurrentChange"
              @prev-click="prevPage"
              @next-click="nextPage"
              layout="prev, pager, next"
              :total="pageNum">
          </el-pagination>
        </div>

      </div>

      <div v-show="orderList.length == 0" class="order-null text-center font-30">{{ $t('orderList.noOrder') }}</div>


      <Footer></Footer>

    </div>
    <el-dialog
        title="撰写评价"
        :visible.sync="writeCommentVisible"
        width="460px"
    >
      <div class="comment-box">

        <div class="block flex flex-column">
          <h3>{{ currentOrder.hotelName }}</h3>
          <div style="color: #999" class="d-flex align-items-center mt-5 mb-15">
            <p>{{ $t('orderList.roomType') }}{{ currentOrder.roomTypeName | roomNameFormat }}</p>
            <p class="ml-10 mr-10" style="color: #e0e0e0"> | </p>
            <p>{{ $t('orderList.day') }} {{ currentOrder.days }} {{ $t('orderList.dayNum') }}</p>
          </div>
          <div style="border-top: 1px solid #d9d9d9"></div>
        </div>

        <div class="block">
          <span class="demonstration">评分</span>
          <el-rate
              :allow-half="true"
              v-model="commentForm.score"
              :colors="colors"
              show-text>
          </el-rate>
        </div>


        <div class="block">
          <span class="demonstration">评价</span>
          <el-input
              type="textarea"
              :autosize="{ minRows: 2}"
              placeholder="可以对酒店设施、房间环境、服务等多方面进行评价哟！"
              maxlength="100"
              show-word-limit
              v-model="commentForm.content">
          </el-input>
        </div>

        <div class="block">
          <span class="demonstration">上传图片</span>
          <div class="d-flex align-items-center mb-15">
            <el-image style="width: 100px; height: 100px"
                      :src="commentForm.attachment"
                      v-if="commentForm.attachment"
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



        <span style="margin: 24px 6px 0 6px;display: block" slot="footer" class="dialog-footer">
          <el-button @click="writeCommentVisible = false">取 消</el-button>
          <el-button type="primary" @click="confirmComment">确 定</el-button>
        </span>


      </div>


    </el-dialog>

    <el-dialog
        class="wallet-dialog"
        :title="$t('hotelDetails.payTitle')"
        :visible.sync="payVisible"
        width="95%"
        center
    >
      <div class="flex flex-column pay-box align-items-center">
        <div>{{ $t('hotelDetails.payment') }}</div>
        <div class="pay-box-price"><span style="font-size: 26px">￥</span>{{ payForm.lastPay }}</div>
        <el-input :placeholder="$t('login.passwordTips')" v-model="payForm.walletPwd" show-password></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="pay">{{ $t('common.confirm') }}</el-button>
      </span>

    </el-dialog>

    <el-dialog
        class="wallet-dialog"
        :visible.sync="barcodeVisible"
        width="95%"
        center
    >
      <div class="m-auto">
        <barcode :value="barcodeValue" class="m-auto">
          订单ID获取异常,请重新打开!
        </barcode>
      </div>


    </el-dialog>

  </div>
</template>

<script>
import {formDataPost, get, post} from "../utils/request";
import TopNav from '../components/TopNav'
import Footer from '../components/Footer.vue';
import VueBarcode from 'vue-barcode';

export default {
  name: "OrderList",
  components: {
    TopNav,
    Footer,
    'barcode': VueBarcode
  },
  data() {
    return {
      payVisible:false,
      barcodeVisible: false,
      barcodeValue: undefined,
      payForm: {
        orderId: undefined,
        lastPay: undefined,
        walletPwd: undefined
      },
      smallPagination: false,
      applyVisible: false,
      writeCommentVisible: false,
      orderList: [],
      isolationList: [],
      applyList: [],
      pageNum: 0,
      statusList: [],
      isIsolation: false,
      isApply: false,
      colors: ['#99A9BF', '#F7BA2A', '#ff7300'],
      currentOrder: {},
      commentForm: {
        score: undefined,
        content: undefined,
        attachment: undefined
      },
      applyForm: {
        applyThing: '',
        applyNum: 0,
        thingUnit: '',
        emergencyLevel: '',
        applyRemark: '',
      },
      emergencyLevelOptions: [{
        value: 0,
        label: '不紧急'
      }, {
        value: 1,
        label: '普通紧急'
      }, {
        value: 2,
        label: '非常紧急'
      },],
      // statusTextList: [this.$t('orderList.status0'),this.$t('orderList.status1'),this.$t('orderList.status2')],
      statusTextList: ['未支付', '已支付', '已关闭']
    }
  },
  filters: {
    applyStatusFilter(value) {
      const lang = localStorage.getItem('lang')
      if (lang == 'zh' || lang == null) {
        switch (value) {
          case 0:
            value = '未审核'
            break;
          case 1:
            value = '已同意'
            break;
          case 2:
            value = '已拒绝'
            break;
          default:
            value = '已拒绝'
            break;
        }
        return value
      } else if (lang == 'en') {
        switch (value) {
          case 0:
            value = 'Not reviewed'
            break;
          case 1:
            value = 'Approved'
            break;
          case 2:
            value = 'Rejected'
            break;
          default:
            value = 'Rejected'
            break;
        }
        return value
      }
    },
    isolationStatusFilter(value) {
      const lang = localStorage.getItem('lang')
      if (lang == 'zh' || lang == null) {
        switch (value) {
          case 0:
            value = '正在隔离'
            break;
          case 1:
            value = '解除隔离'
            break;
          case 2:
            value = '阳性入院'
            break;
          default:
            value = '阳性入院'
            break;
        }
        return value
      } else if (lang == 'en') {
        switch (value) {
          case 0:
            value = 'Isolating'
            break;
          case 1:
            value = 'Free'
            break;
          case 2:
            value = 'Positive'
            break;
          default:
            value = 'Positive'
            break;
        }
        return value
      }
    },
    statusFilter(value) {
      const lang = localStorage.getItem('lang')
      if (lang == 'zh' || lang == null) {
        switch (value) {
          case '0':
            value = '未支付'
            break;
          case '1':
            value = '已支付'
            break;
          case '2':
            value = '已关闭'
            break;
          case '3':
            value = '已入住'
            break;
          default:
            value = '已完成'
            break;
        }
        return value
      } else if (lang == 'en') {
        switch (value) {
          case '0':
            value = 'Unpaid'
            break;
          case '1':
            value = 'Paid'
            break;
          case '2':
            value = 'Closed'
            break;
          case '3':
            value = 'Checked in'
            break;
          default:
            value = 'Completed'
            break;
        }
        return value
      }
    },
    roomNameFormat(val) {
      const lang = localStorage.getItem('lang')
      if (lang == 'zh') {
        return val
      } else if (lang == 'en') {
        if (val == '单人房') {
          return val = 'Single-bed room'
        } else if (val == '豪华大床房') {
          return val = 'Deluxe single Room'
        } else if (val == '双人房') {
          return val = 'Double room'
        } else if (val == '总统套房') {
          return val = 'Presidential suite'
        } else if (val == '经济房') {
          return val = 'Economy Room'
        } else {
          return val
        }
      }
    }
  },
  mounted() {
    this.getOrderList()
    this.getStatus()
    if (window.innerWidth < 768) {
      this.smallPagination = true
    } else {
      this.smallPagination = false
    }    
    window.onresize = () => {
      if (window.innerWidth < 768) {
        this.smallPagination = true
      } else {
        this.smallPagination = false
      }
    }    
  },
  methods: {

    //图片改变调用函数
    onChangeFile(file) {
      this.commentForm.attachment = URL.createObjectURL(file.raw);

    },
    //图片上传之前的回调函数
    beforeAvatarUpload(file) {
      const isLt2M = file.size / 1024 / 1024 < 5;
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 5MB!');
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
      fd.append("key", "comment");

      post("/upload/img", fd, config)
          .then(res => {
            console.log(res);
            this.commentForm.attachment = 'http://' + res.data.data
          })
          .catch(err => {
            console.log(err);
          })
    },

    openBarcode(id) {
      this.barcodeValue = id
      this.barcodeVisible = true
    },

    writeComment(order) {
      this.writeCommentVisible = true
      this.currentOrder = order
    },
    confirmComment() {
      console.log(this.commentForm)
      let data = this.commentForm
      data.orderId = this.currentOrder.id
      post("/api/orderComment/write",data).then(res => {
        if (res.data.code == 200) {
          this.$message({
            message: this.$t('common.success'),
            type: 'success'
          });
          this.writeCommentVisible = false
          this.$router.go(0)
        } else {
          this.$message.error(res.data.msg);
        }
      })
    },
    getStatus() {
      const data = {
        page: {
          page: 1,
          size: 10
        }
      }
      post('/api/order/count', data)
          .then(res => {
            console.log(res);
            let resStatusList = res.data.data
            resStatusList.map(element => {
              let statusObj = {}
              statusObj.counts = element.counts
              statusObj.status = element.status
              this.statusList.push(statusObj)
              return resStatusList
            });
            console.log(resStatusList);
            console.log(this.statusList);
          })
          .catch(err => {
            console.log(err);
          })
    },
    getOrderList() {
      let data = {
        page: {
          page: 1,
          size: 10
        }
      }
      this.orderListRequest(data)
    },
    //获取订单列表请求
    orderListRequest(data) {
      post('/api/order/my', data)
          .then(res => {
            console.log(res);
            this.orderList = res.data.data.records
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
      this.orderListRequest(data)
      // window.scrollTo(0, 0)
    },

    //上一页
    prevPage(num) {
      let data = {
        page: {
          page: num,
          size: 10
        }
      }
      this.orderListRequest(data)
      // window.scrollTo(0, 0)
    },

    //下一页
    nextPage(num) {
      let data = {
        page: {
          page: num,
          size: 10
        }
      }
      this.orderListRequest(data)
      // window.scrollTo(0, 0)
    },

    cancelOrder(id) {
      get('/api/order/cancel/' + id)
          .then(res => {
            console.log(res);
            if (res.data.code == 200) {
              this.$message({
                message: this.$t('orderList.success'),
                type: 'success'
              });
              this.$router.go(0)
            }
          })
          .catch(err => {
            console.log(err);
          })
    },
    goPayOrder(id,pay) {
      this.payVisible=true;
      this.payForm.lastPay=pay;
      this.payForm.orderId=id;
    },
    pay() {
      let data = {
        orderId: this.payForm.orderId,
        walletPwd: this.payForm.walletPwd
      }
      formDataPost("/api/order/payOrder",data).then(res => {
        if (res.data.code == 200) {
          this.$message({
            message: this.$t('hotelList.success'),
            type: 'success'
          });
          this.payVisible = false
          this.confirmOrderVisible = false
          this.payForm = {
            orderId: undefined,
            lastPay: undefined,
            walletPwd: undefined
          }
          this.$router.go(0)
        } else {
          this.$message({
            message: res.data.msg,
            type: 'warning',
            duration: 4000
          });
        }

      })
    },

  }
}
</script>

<style scoped>
.order-list-main {
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
  background: #28B535;
  border-radius: 16px;
  border: 0;
  color: #fff;
}

.nav-content a {
  color: #A1A09E;
}

.nav-content a:hover {
  color: #323230
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
  color: #FFFFFF;
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
  background: #E1BD85;
  font-size: 12px;
  color: #FFFFFF;
  border-radius: 5px;
  border: none;
}

.hotel-list li:nth-child(even) .li-l {
  order: 2;
}

.order-list-content {
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
  flex: 1;
  /* border-radius: 15px;  
  height: 120px;
  margin: 0px 10px;
  padding: 0px 20px; */
  border-radius: 1.5rem;  
  height: 12rem;
  margin: 0 0.8rem;
  padding: 0 1.5rem;  
}

.order-status > img {
  /* width: 60px;
  height: 60px;
  margin-right: 22px; */
  width: 5.5rem;
  height: 5.5rem;
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
  margin: 20px 0.8rem;
}

.order-list li {
  padding: 25px;
  margin-bottom: 10px;
  background-color: white;
  border-radius: 15px;
  flex: 1;
}

.el-pagination.is-background .btn-next, .el-pagination.is-background .btn-prev, .el-pagination.is-background .el-pager li {
  background-color: #ffffff !important;
}

.isolation {
  border: 1px solid #d2d2d3;
  background: #f5f7fa;
}

.comment-box {
  margin-top: -30px;
}

.block {
  margin: 18px 6px;
}

.demonstration {
  display: block;
  margin-bottom: 8px;
}

.pay-box-price {
  font-size: 56px;
  font-weight: 700;
  color: #ff4d6a;
  width: 100%;
  text-align: center;
  margin: 10px 0 30px 0;
}

@media screen and (max-width: 767.9px) { /* 页面测试无法显示767，实际是767.2px */ 
  .hotel-list-bg {
    height: 120px;
  }
  .hotel-list-bg img {
    height: 120px;
  }
  .order-list-main {
    height: calc(100vh - 120px);
  }
  .order-list-content>div.justify-content-between {
    flex-wrap: wrap;
  }
  .order-list-content {
    padding: 10px 3%;
  }
  .order-status {
    margin: 1rem 2%;
    padding: 0px 0.5rem;
    width: 43%;
    flex: inherit;  
  }  
  .order-list {
    margin-top: 10px;
  }
  .order-list li {
    padding: 10px 15px;
    border-radius: 8px;
  }
  .order-details {
    flex-direction: column;
    align-items: start;
  }
  .order-details>div {
    margin-bottom: 1rem;
  }
}



</style>