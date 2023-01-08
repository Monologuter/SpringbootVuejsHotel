<template>
  <div class="depart-main">
    <div class="d-flex justify-content-end mb-10" style="padding: 10px;">
      <el-button type="primary" style="width: 100px;" @click="addDepart">添加</el-button>
    </div>

    <el-table
        :data="departData"
        style="width: 100%"
        row-key="id"
        border
        :tree-props="{ hasChildren: 'hasChildren',children: 'children',}">
      <el-table-column
          prop="name"
          label="部门"
          width="300">
      </el-table-column>
      <el-table-column
          label="状态"
          align="center"
          width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status==1" type="success">正常</el-tag>
          <el-tag v-if="scope.row.status==0" type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="primary"
              @click="handleModify(scope.$index, scope.row)">编辑
          </el-button>
          <el-button
              :disabled="scope.row.children.length !== 0"
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹出框 -->
    <el-dialog
        :title="title"
        :visible.sync="dialogVisible"
        width="570px">
      <div class="contont">
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">父部门:</p>
          <el-cascader ref="cascader"
                       style="width: 350px;"
                       v-model="form.pId"
                       :options="deptList"
                       :props="{
                         checkStrictly: true,
                         expandTrigger: 'hover',
                         emitPath: false,
                         value: 'id',
                         label: 'name'
                       }">
            <template slot-scope="{ node, data }">
              <div @click="cascaderClick(data)">
                <span>{{ data.name }}</span>
                <span v-if="!node.isLeaf"> ({{ data.children.length }}) </span>
              </div>
            </template>
          </el-cascader>

        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">部门名称:</p>
          <el-input
              style="width: 350px;"
              placeholder="请输入部门名称"
              v-model="form.name"
              clearable>
          </el-input>
        </div>
        <div class="d-flex align-items-center mb-15">
          <p class="w-100 text-left">权限:</p>
          <el-select style="width: 350px;" v-model="form.role" placeholder="请选择">
            <el-option v-for="item in roleOptions" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </div>
        <div style="margin-top: 20px" class="d-flex align-items-center">
          <p class="w-100 text-left">状态:</p>
          <div>
            <el-radio v-model="form.status" label="1">正常</el-radio>
            <el-radio v-model="form.status" label="0">停用</el-radio>
          </div>
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
  name: "DepartmentManage",
  data() {
    return {
      deptList: [],
      departData: [],
      title: '',  //弹出框标题名
      dialogVisible: false, //添加弹出框
      value: '',
      departName: '',
      departId: '',
      deptValue: '',
      deptStatus: '1',  //状态
      pId: '',
      authority: '', //权限select
      role: '',
      roleOptions: [
        {
          label: '管理员',
          value: 'admin'
        },
        {
          label: '酒店管理员',
          value: 'hotel_admin'
        },
        {
          label: '酒店员工',
          value: 'hotel_member'
        },
        {
          label: '防疫人员',
          value: 'anti-epidemic'
        }
      ],
      form: {
        id: undefined,
        pId: undefined,
        name: undefined,
        role: undefined,
        status: undefined
      }


    }
  },
  methods: {
    //获取部门列表
    getDepartmentList() {
      let dept = {
        // deptVO: this.$route.query.data
      }
      post('/api/dept/list', dept)
          .then(res => {
            console.log(res);
            this.departData = res.data.data
          })
          .catch(err => {
            console.log(err);
          })
    },

    //
    cascaderClick(nodeData) {
      this.addrCode = nodeData.id;
      this.$refs.cascader.checkedValue = nodeData.id;
      this.$refs.cascader.computePresentText();
      this.$refs.cascader.toggleDropDownVisible(false);
      this.$message({
        message: '已选择：' + nodeData.name,
        type: 'success',
        duration: 1000
      });
    },

    //获取dialog下拉框数据
    getData() {
      let dept = {
        // deptVO: this.$route.query.data
      }
      post('/api/dept/list', dept).then(res => {
        this.deptList = this.getTreeData(res.data.data)
        console.log(res)
        console.log(this.deptList)
      })
    },
    getTreeData(data) {
      for (var i = 0; i < data.length; i++) {
        if (data[i].children.length < 1) {
          // children若为空数组，则将children设为undefined
          data[i].children = undefined;
        } else {
          // children若不为空数组，则继续 递归调用 本方法
          this.getTreeData(data[i].children);
        }
      }
      return data;
    },

    //确定按钮
    confirm() {
      let value = this.deptValue
      let data = this.form
      if (value == 'add') {
        this.deptRequest(value, data)
      } else if (value == 'modify') {
        data.id = this.deptId
        this.deptRequest(value, data)
      }

    },

    //请求方法
    deptRequest(url, data) {
      post('/api/dept/' + url, data)
          .then(res => {
            this.$message({
              message: '操作成功！',
              type: 'success',
              duration: 1000
            });
            console.log(res);
            this.$router.go(0);
          })
          .catch(err => {
            console.log(err);
          })
    },

    //增加部门
    addDepart() {
      this.form = {
        id: undefined,
        pId: undefined,
        name: undefined,
        role: undefined,
        status: undefined
      }
      this.dialogVisible = true
      this.title = '添加部门'
      this.deptValue = 'add'
    },

    //表格的删除按钮点击方法
    handleDelete(index, row) {
      get('/api/dept/delete/' + row.id)
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

    //表格的编辑按钮点击方法
    handleModify(index, row) {
      console.log(index);
      console.log(row);
      this.deptId = row.id
      this.form = row
      this.dialogVisible = true
      this.title = '修改部门'
      this.deptValue = 'modify'
    },

  },
  mounted() {
    this.getDepartmentList()
    this.getData()
  }
}
</script>

<style scoped>
.depart-main {
  width: 100%;
  background: #FFFFFF;
  box-shadow: 0px 5px 30px 0px rgba(22, 115, 255, 0.1);
}
</style>