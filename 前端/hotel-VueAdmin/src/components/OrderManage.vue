<template>

  <div class="order-main">
    <div class="d-flex justify-content" style="padding: 10px;">
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
        <p>状态：</p>
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
        <p>房间类型：</p>
        <el-select v-model="searchParams.roomType" placeholder="请选择">
          <el-option
              v-for="item in roomTypeOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
          </el-option>
        </el-select>
      </div>
      <div class="block search">
        <p>来访地：</p>
        <el-input v-model="searchParams.province" placeholder="请输入来访地"></el-input>
      </div>
      <el-button-group class="d-flex justify-content">
        <el-button type="primary" icon="el-icon-search" @click="getOrderList">查询</el-button>
        <el-button type="info" icon="el-icon-refresh" @click="reset">重置</el-button>
      </el-button-group>
    </div>

    <div class="d-flex justify-content-end" style="padding: 10px;">
      <el-button type="success" style="width: 120px;" @click="download">导出成Excel</el-button>
    </div>
    
    <el-table
        stripe
        border
        :data="orderData"
        class="global-table"
        style="width: 100%;overflow: auto;">
      <el-table-column
          align="center"
          class-name="first-column"
          prop="hotelName"
          label="酒店名">
      </el-table-column>
      <el-table-column
          align="center"
          prop="customerName"
          label="客户名称">
      </el-table-column>
      <el-table-column
          align="center"
          prop="province"
          label="来访地">
      </el-table-column>
      <el-table-column
          align="center"
          prop="roomTypeName"
          label="房间类型">
      </el-table-column>
      <el-table-column
          align="center"
          prop="days"
          label="天数">
      </el-table-column>
      <el-table-column
          align="center"
          prop="pay"
          label="应付价格">
      </el-table-column>
      <el-table-column
          align="center"
          prop="lastPay"
          label="实付价格">
      </el-table-column>
      <el-table-column
          align="center"
          prop="status"
          label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status==0" type="danger">未支付</el-tag>
          <el-tag v-if="scope.row.status==1" >已支付</el-tag>
          <el-tag v-if="scope.row.status==2" type="info">已关闭</el-tag>
          <el-tag v-if="scope.row.status==3" type="warning">已入住</el-tag>
          <el-tag v-if="scope.row.status==4" type="success">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          prop="createTime"
          :formatter="dateFormatter"
          label="创建时间">
      </el-table-column>
      <el-table-column
          label="操作"
          width="100"
          align="center">
        <template slot-scope="scope">
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
    <!--<el-dialog-->
    <!--:title="title"-->
    <!--:visible.sync="dialogVisible"-->
    <!--width="570px">-->
    <!--<div class="contont">-->
    <!--<div class="d-flex align-items-center mb-15">-->
    <!--<p class="w-100 text-left">名称:</p>-->
    <!--<el-input-->
    <!--style="width: 350px;"-->
    <!--placeholder="请输入床的名称"-->
    <!--v-model="bedName"-->
    <!--clearable>-->
    <!--</el-input>-->
    <!--</div>-->
    <!--<div class="d-flex align-items-center mb-15">-->
    <!--<p class="w-100 text-left">规格:</p>-->
    <!--<el-input-->
    <!--style="width: 350px;"-->
    <!--placeholder="请输入床的规格"-->
    <!--v-model="bedSpec"-->
    <!--clearable>-->
    <!--</el-input>-->
    <!--</div>-->
    <!--<div class="d-flex align-items-center mb-15">-->
    <!--<p class="w-100 text-left">最大容量:</p>-->
    <!--<el-input-number-->
    <!--v-model="num"-->
    <!--:min="1"-->
    <!--:max="6">-->
    <!--</el-input-number>-->
    <!--</div>-->
    <!--<div class="d-flex align-items-center mb-15">-->
    <!--<p class="w-100 text-left">硬度:</p>-->
    <!--<div>-->
    <!--<el-radio v-model="bedStatus" label="1">软</el-radio>-->
    <!--<el-radio v-model="bedStatus" label="0">硬</el-radio>-->
    <!--</div>-->
    <!--</div>-->
    <!--</div>-->
    <!--<span slot="footer" class="dialog-footer">-->
    <!--<el-button @click="dialogVisible = false">取 消</el-button>-->
    <!--<el-button type="primary" @click="confirm">确 定</el-button>-->
    <!--</span>-->
    <!--</el-dialog>-->

  </div>

</template>

<script>
import {get, post,reqFuc} from "../utils/request";
import axios from 'axios'

export default {
  name: "OrderManage",
  data() {
    return {
      pageNum: 0,
      orderData: [],
      dialogVisible: false,
      statusOptions: [{
        value: '0',
        label: '未支付'
      }, {
        value: '1',
        label: '已支付'
      }, {
        value: '2',
        label: '已关闭'
      }, {
        value: '3',
        label: '已入住'
      }, {
        value: '4',
        label: '已完成'
      }],
      roomTypeOptions: [],
      searchParams: {
        dateRange: undefined,
        beginTime: undefined,
        endTime: undefined,
        status: undefined,
        roomType: undefined,
        province: undefined
      }
    }
  },
  watch: {
    "searchParams.dateRange"(val, oldVal){//普通的watch监听
      console.log("a: "+val, oldVal);

      if (val) {
        // let split = val.toString().split(',');

        this.searchParams.beginTime = val[0]
        this.searchParams.endTime = val[1]
      }
      console.log("type: ",typeof(this.searchParams.beginTime))
      console.log("beginTime: ",this.searchParams.beginTime);
      console.log("endTime: ",this.searchParams.endTime);

    },
  },
  methods: {
    download() {
      let data = {
        page: {
          page: 1,
          size: 9999999
        },
        roomType: this.searchParams.roomType,
        status: this.searchParams.status,
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        province: this.searchParams.province
      }

      reqFuc(data)
          .then( res => {
              console.log(res);
              // let blob = new Blob([res.data],{type: 'application/vnd.ms-excel;charset=utf-8'});
              //
              // if (!!window.ActiveXObject || "ActiveXObject" in window) {
              //     window.navigator.msSaveOrOpenBlob(blob, 'fileName');
              // } else {
              //     const link = document.createElement('a');
              //     link.style.display = 'none';
              //     link.href = URL.createObjectURL(blob);
              //     link.setAttribute('download', 'fileName');
              //     document.body.appendChild(link);
              //     link.click();
              //     document.body.removeChild(link);
              // }

              const aLink = document.createElement('a')
              var blob = new Blob([res.data], { type: 'mimeMap.xlsx' })
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
          .catch( err => {
              console.log(err);
          })


    },
    reset() {
      this.searchParams = {
            dateRange: undefined,
            beginTime: undefined,
            endTime: undefined,
            status: undefined,
            roomType: undefined
      }
    },
    getRoomTypeList() {
      let data = {
        page:{
          page: 1,
          size: 999999
        },
      }
      post("api/roomType/page",data).then(res => {
        this.roomTypeOptions = res.data.data.records
        console.log("this.roomTypeOptions",this.roomTypeOptions)
      })
    },
    getOrderList() {
      let data = {
        page: {
          page: 1,
          size: 10
        },
        roomType: this.searchParams.roomType,
        status: this.searchParams.status,
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        province: this.searchParams.province
      }
      this.orderListRequest(data)
    },
    //
    orderListRequest(data) {
      post('/api/order/my', data)
          .then(res => {
            console.log(res);
            this.orderData = res.data.data.records
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
        },
        roomType: this.searchParams.roomType,
        status: this.searchParams.status,
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        province: this.searchParams.province
      }
      this.orderListRequest(data)
    },

    //上一页
    prevPage(num) {
      let data = {
        page: {
          page: num,
          size: 10
        },
        roomType: this.searchParams.roomType,
        status: this.searchParams.status,
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        province: this.searchParams.province
      }
      this.orderListRequest(data)

    },

    //下一页
    nextPage(num) {
      let data = {
        page: {
          page: num,
          size: 10
        },
        roomType: this.searchParams.roomType,
        status: this.searchParams.status,
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        province: this.searchParams.province
      }
      this.orderListRequest(data)
    },
    //删除床方法
    handleDelete(index, row) {
      get('/api/order/delete/' + row.id)
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
      dateFormatter(val){
          // console.log(val.createTime);
          var d = new Date(val.createTime);

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
    this.getOrderList()
    this.getRoomTypeList()
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