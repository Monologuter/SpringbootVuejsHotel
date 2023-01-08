<template>
  <el-submenu v-if="menu.children && menu.children.length >= 1" :index="menu.id + ''">
    <template slot="title">
      <i :class="menu.icon"></i>
      <span slot="title">{{menu.name}}</span>
    </template>
    <TreeMenu v-for="item in menu.children" :key="item.menuId" :menu="item"></TreeMenu>
  </el-submenu>
  <el-menu-item v-else :index="menu.id + ''" @click="handleRoute(menu)">
    <i :class="menu.icon"></i>
    <span slot="title">{{menu.name}}</span>
  </el-menu-item>
</template>

<script>
export default {
  name: 'TreeMenu',
  props: {
    menu: {
      type: Object,
      required: true
    }
  },
  methods: {
    handleRoute (menu) {
      // if (this.$route.path == '/index/' && menu.path == ' ') {
      //   return
      // }
      // // const currentPath = /\w*(?:\/+)/.exec('/index/')
      // const currentPath = this.$route.path.replace(/^\/\S+[\/]/, '')
      // if (currentPath == menu.path) {
      //   return
      // } else {
      //   // 通过菜单URL跳转至指定路由
      //   this.$router.push({
      //     name: menu.path
      //   })
      // }

      localStorage.setItem('menuId',menu.id)
      // 通过菜单URL跳转至指定路由
      this.$router.push({
          name: menu.path
      }).catch(err => {})

    }
  }
}
</script>

<style>
.el-submenu .el-menu>.is-active {
  background: linear-gradient(90deg, #428DFF, #397DFF) !important;
  border-radius: 6px;
}
</style>