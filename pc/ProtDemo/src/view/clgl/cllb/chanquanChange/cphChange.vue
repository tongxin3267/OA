<template>
  <div>
    <Form
      ref="form"
      :model="formItem"
      :rules="ruleInline"
      :label-width="100"
      :styles="{top: '20px'}">
      <div style="overflow: auto;height: 400px;width:100%">
        <h3>变更信息</h3>
        <form-items :parent="v" :items="formItemGroup[0]"></form-items>
        <Divider/>
        <Row v-for="(r,k) of fileGroup">
          <Col>
            <h4>产权协议电子档案</h4>
            <add-img-list :urlList="r.urlList" :uploadUrl="r.uploadUrl" @removeFile="(mes)=>{removeFile(r,mes)}"
                          @addImg="(mes)=>{addImg(r,mes)}"></add-img-list>
          </Col>
        </Row>
      </div>
    </Form>
  </div>
</template>

<script>
  import addImgList from '../../../../components/addlistfileImg'

  export default {
    name: "cphChange",
    components: {addImgList},
    props: {
      choosedItem: {
        type: Object,
        default: function () {
          return {};
        }
      },
    },
    data() {
      return {
        v: this,
        showModal: true,
        editMode: true,
        formItem: {},
        ruleInline: {},
        saveUrl: this.apis.carcq.cphbg,
        fileGroup: {
          propertyFileurl: {type: '产权',urlType: 'propertyFileurl',urlList: [],uploadUrl: this.apis.upFile + '?targetPath=car'},
        },
        formItemGroup: [
          [
            {title: '新车牌号', key: 'xcph', required: true},
            {title: '备注', key: 'bz'},
          ],
        ],
        params: {
          id: ''
        }
      }
    },
    created() {
      this.parentItem = this.choosedItem;
      this.formItem = this.parentItem
      this.params.clId = this.formItem.id;
      for (let key in this.fileGroup) {
        if (key == 'id') {
          this.params.id = this.choosedItem['id']
        } else {
          // this.fileGroup[key].urlList = this.choosedItem[key].split(',')
          this.params[key] = this.choosedItem[key]
        }
      }
    },
    methods: {
      removeFile(r, mes) {
        var v = this
        let arr = r.urlList
        r.urlList.forEach((it, index) => {
          if (it == mes) {
            r.urlList.splice(index, 1)
            this.formItem[r.urlType] = r.urlList.join(',')
            return
          }
        })
      },
      addImg(r, mes) {
        r.urlList.push(mes);//将新添加的图片地址存到urlList 里
        this.formItem[r.urlType] = r.urlList.join(',')//将图片地址数组转成字符串
      },

      save() {
        this.util.save(this);
      },
      saveSuccess() {
        this.$emit('close', null);
      }
    }
  }
</script>

<style scoped>

</style>
