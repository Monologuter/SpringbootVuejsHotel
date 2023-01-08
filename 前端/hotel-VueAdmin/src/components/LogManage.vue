<template>

  <div class="log-main">

    <div class="d-flex justify-content" style="padding: 10px">
      <div class="block search">
        <span class="demonstration">日期：</span>
        <el-date-picker
          v-model="searchParams.dateRange"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        >
        </el-date-picker>
      </div>
      <div class="block search">
        <p>日志类型：</p>
        <el-select v-model="searchParams.type" placeholder="请选择">
          <el-option
            v-for="item in Optioner"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </div>
      <div class="block search">
        <p>操作人：</p>
        <el-input v-model="searchParams.Optioner" placeholder=""></el-input>
      </div>
      <div class="block search">
        <p>日志内容：</p>
        <el-input v-model="searchParams.logContent" placeholder=""></el-input>
      </div>
      <el-button-group class="d-flex justify-content">
        <el-button type="primary" icon="el-icon-search" @click="getLogList">查询</el-button>
        <el-button type="info" icon="el-icon-refresh" @click="reset">重置</el-button>
      </el-button-group>
    </div>

    <div class="d-flex justify-content-end" style="padding: 10px">
      <el-button type="success" style="width: 120px" @click="download">导出成Excel</el-button>
    </div>

    <el-table
        stripe 
        border 
        :data="logData" 
        style="width: 100%">
      <el-table-column
        align="center" 
        prop="sketch" 
        label="日志内容">
      </el-table-column>
            <el-table-column
        align="center"
        prop="type" 
        label="日志类型">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type == 0" type="info">普通日志</el-tag>
          <el-tag v-if="scope.row.type == 1">数据修改</el-tag>
          <el-tag v-if="scope.row.type == 2" type="success">系统修改</el-tag>
          <el-tag v-if="scope.row.type == 3" type="warning">异常日志</el-tag>
          <el-tag v-if="scope.row.type == 4" type="danger">警告日志</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        align="center" 
        prop="createBy" 
        label="操作人">
      </el-table-column>
      <el-table-column
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        label="创建时间"
      >
      </el-table-column>
      <el-table-column 
        label="操作" 
        width="100"
        align="center">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="handleCheck(scope.$index, scope.row)"
            >查看
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
      :total="pageNum"
    >
    </el-pagination>

    <!-- 弹出框 -->
    <el-dialog
        :title="title"
        :visible.sync="dialogVisible"
        width="650px">

      <div class="contont">
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">日志内容:</p>
          <div>{{ details.sketch }}</div>
          <!-- <el-input
              style="width: 350px;"
              placeholder="请输入用户名"
              v-model="lee"
              :disabled="true"
              clearable>
          </el-input> -->
        </div>
        <div class="mb-15">
          <p class="w-100 text-left mb-10">日志详情:</p>
          <div class="d-flex align-items-center">
            <div v-if="details.type == 1" class="change-box">
              <ul>
                <li class="d-flex align-items-center change-list active" v-for="(val,key,index) in diffObj1">
                  <p class="mr-10">{{ key }}:</p>
                  <p>{{ val }}</p>
                </li>
              </ul>
            </div>
            <p v-if="details.type == 1" style="margin: 0px 10px">→</p>
            <div v-if="details.type < 2" class="change-box">
              <ul>
                <li class="d-flex align-items-center change-list active" v-for="(val,key,index) in diffObj2">
                  <p class="mr-10">{{ key }}:</p>
                  <p>{{ val }}</p>
                </li>                                
              </ul>
            </div>
            <div v-if="details.type == 3" class="change-box">
              <ul>
                <li class="d-flex align-items-center change-list warn">
                  <p>{{ warnText }}</p>
                </li>                                
              </ul>
            </div>            
          </div>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">创建人:</p>
          <div>{{ details.createBy }}</div>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">创建日期:</p>
          <div>{{ details.createTime | dateFormate }}</div>
        </div>                
      </div>

      <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>

    </el-dialog>

  </div>

</template>

<script>
import {get, post,logDownload} from "../utils/request";


export default {
  name: "LogManage",
  data() {
    return {
        Optioner: [{
            value: 0,
            label: '普通日志'
        }, {
            value: 1,
            label: '数据修改'
        }, {
            value: 2,
            label: '系统修改'
        }, {
            value: 3,
            label: '异常日志'
        }, {
            value: 4,
            label: '警告日志'
        }],        
        searchParams: {
            dateRange: undefined,
            beginTime: undefined,
            endTime: undefined,
            type: undefined,
            Optioner: undefined,
            logContent: undefined,
        },
        pageNum: 0,
        logData: [],
        title: '',
        dialogVisible: false,
        lee: 'lee',
        details: '',
        diffObj1: '',
        diffObj2: '',
        warnText: ''
        
    };
  },

  mounted() {
    this.getLogList()
  },
  watch: {
    "searchParams.dateRange"(val, oldVal){//普通的watch监听

      if (val) {
        this.searchParams.beginTime = val[0]
        this.searchParams.endTime = val[1]
      }

    },
  },
  filters: {
      // 日期格式化函数
      dateFormate(val){
        // console.log(val.createTime);
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
  methods: {
    // 下载Excel表格
    download(){
      const data = {
        page: {
          page: 1,
          size: 9999999
        },
        type: this.searchParams.type,
        createBy: this.searchParams.Optioner,
        beginTime: this.searchParams.beginTime,
        endTime: this.searchParams.endTime,
        sketch: this.searchParams.logContent
      }

      logDownload(data)
        .then( res => {
          console.log(res);

          const aLink = document.createElement('a')
          const blob = new Blob([res.data],{ type: 'mimeMap.xlsx' })
          const patt = new RegExp('filename=([^;]+\\.[^\\.;]+);*')
          const contentDisposition = decodeURI(res.headers['content-disposition'])
          const result = patt.exec(contentDisposition)
          let fileName = result[1]
          fileName = fileName.replace(/\"/g, '')
          aLink.href = URL.createObjectURL(blob)
          aLink.setAttribute('download',fileName)
          document.body.appendChild(aLink)
          aLink.click()
          document.body.appendChild(aLink)

        })
        .catch( err => {
          console.error(err);
        })

    },
    
    // 重置
    reset() {
        this.searchParams = {
            dateRange: undefined,
            beginTime: undefined,
            endTime: undefined,
            type: undefined,
            Optioner: undefined,
            logContent: undefined,            
        }
    },

    // 获取日志列表
    getLogList(){
        let data = {
            page: {
                page: 1,
                size: 10
                
            },
            type: this.searchParams.type,
            // type: 1,
            createBy: this.searchParams.Optioner,
            beginTime: this.searchParams.beginTime,
            endTime: this.searchParams.endTime,
            sketch: this.searchParams.logContent
        }
        this.logListRequest(data)
    },

    //获取日志列表的请求
    logListRequest(data){
        post('/api/logs/page', data)
            .then(res => {
                console.log(res);
                console.log(res.data.data.records);
                this.logData = res.data.data.records
                this.pageNum = res.data.data.total
            })
            .catch(err => {
                console.log(err);
            })
    },

    // 选择页码
    handleCurrentChange(num) {
        let data = {
            page: {
                page: num,
                size: 10
            },
            type: this.searchParams.type,
            createBy: this.searchParams.Optioner,
            beginTime: this.searchParams.beginTime,
            endTime: this.searchParams.endTime,
            sketch: this.searchParams.logContent
        }
        this.logListRequest(data)
    },

    // 上一页
    prevPage(num) {
        let data = {
            page: {
                page: num,
                size: 10
            },
            type: this.searchParams.type,
            createBy: this.searchParams.Optioner,
            beginTime: this.searchParams.beginTime,
            endTime: this.searchParams.endTime,
            sketch: this.searchParams.logContent
        }
        this.logListRequest(data)
    },

    // 下一页
    nextPage(num) {
        let data = {
            page: {
                page: num,
                size: 10
            },
            type: this.searchParams.type,
            createBy: this.searchParams.Optioner,
            beginTime: this.searchParams.beginTime,
            endTime: this.searchParams.endTime,
            sketch: this.searchParams.logContent
        }
        this.logListRequest(data)
    },

    // 查看详情
    handleCheck(index, row) {
      this.dialogVisible = true
      this.title = '查看详情'
      get(`/api/logs/get/${row.id}`)
        .then(res => {
          console.log(res);
          this.details = res.data.data
          const data = res.data.data
          const result = res.data.data.detail.split('@*@')
          console.log(res.data.data.detail);
          if (data.type == 0) {
            const data1 = JSON.parse(data.detail)
            const data2 = JSON.parse(data.detail)
            this.diffObj1 = data1
            this.diffObj2 = data1              
          } else if (data.type == 1) {
            const data1 = JSON.parse(result[0])
            const data2 = JSON.parse(result[1])
            const resultArray = this.diff(data1,data2)
            this.diffObj1 = resultArray[0]
            this.diffObj2 = resultArray[1]
          } else if (data.type == 3) {
            this.warnText = data.detail   
          }



          // function diff(json1,json2) {

          //     for (var key in json1) {//循环遍历其中一个json对象
          //         if (typeof (json1[key]) != "object") {
          //             if (json2[key] != null) {
          //                 if (json1[key] == json2[key]) {
          //                     delete json1[key];
          //                     delete json2[key];
          //                 }
          //             }
          //         }
          //         else {
          //             if (json1[key].length >= 0) {
          //                 for (i = 0; i < json1[key].length; i++) {
          //                     this.Getdifferent(json1[key][i], json2[key][i]);
          //                 }
          //             }
          //             else {
          //                 this.Getdifferent(json1[key], json2[key]);
          //             }
          //         }
          //     }
          //     return [json1,json2]
          // }          
          
          // const AdchinaJson = {};

          // AdchinaJson.Getdifferent = function (json1, json2) {
            
          //     for (var key in json1) {//循环遍历其中一个json对象
          //     // debugger
          //         if (typeof (json1[key]) != "object") {
          //             if (json2[key] != null) {
          //                 if (json1[key] == json2[key]) {
          //                     delete json1[key];
          //                     delete json2[key];
          //                 }
          //             }
          //         }
          //         else {
          //             if (json1[key].length >= 0) {
          //                 for (i = 0; i < json1[key].length; i++) {
          //                     this.Getdifferent(json1[key][i], json2[key][i]);
          //                 }
          //             }
          //             else {
          //                 this.Getdifferent(json1[key], json2[key]);
          //             }
          //         }
          //     }
          // }

          // AdchinaJson.Json2Str = function (o) {
          //     var arr = [];
          //     var fmt = function (s) {
          //         if (typeof s == 'object' && s != null) return AdchinaJson.Json2Str(s);
          //         return /^(string|number)$/.test(typeof s) ? "'" + s + "'" : s;
          //     }
          //     for (var i in o) arr.push("'" + i + "':" + fmt(o[i]));
          //     return '{' + arr.join(',') + '}';
          // }

        })
        .catch(err => {
          console.error(err);
        })
    },
    diff(json1,json2) {

        for (var key in json1) {//循环遍历其中一个json对象
            if (typeof (json1[key]) != "object") {
                if (json2[key] != null) {
                    if (json1[key] == json2[key]) {
                        delete json1[key];
                        delete json2[key];
                    }
                }
            }
            else {
                if (json1[key].length >= 0) {
                    for (i = 0; i < json1[key].length; i++) {
                        this.Getdifferent(json1[key][i], json2[key][i]);
                    }
                }
                else {
                    this.Getdifferent(json1[key], json2[key]);
                }
            }
        }
        return [json1,json2]
    },     
    // 日期格式化函数
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
};
</script>

<style scoped>
.change-box {
  flex: 1;
  text-align: left;
  padding: 0px 10px;
}
.change-list {
  padding: 2px 5px;
  border-radius: 3px;
  margin-bottom: 10px;
}
.change-list:last-child {
  margin-bottom: 0;
}
.change-list.active {
  background-color: #aaefef;
}
.change-list.warn {
  background-color: #fdf6ec;
  color: #e6a23c;
}
.log-main {
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