import Vditor from "vditor";
import "vditor/dist/index.css";
import MyAxios from '@/utils/MyAxios.js'
//处理展开全部、收起按钮
export const handleClickExpandAllOrClose  = (share) => {
    if(share.expandAllOrClose === '展开全部'){
      share.expandAllOrClose = '收起'
    } 
    else if(share.expandAllOrClose === '收起') {
      share.expandAllOrClose = '展开全部'
    }
}

//根据展开、收起按钮设置高度
export const controlHeightByButton = (share) => {
    switch(share.expandAllOrClose){
      case '展开全部': 
      return 'max-height: 300px;';
      case '收起': 
      return "";
    }
}
//渲染markdown文本
export const renderingMarkDown = (shareList)=>{
  shareList.forEach(value=>{
    Vditor.preview(
    document.getElementById('content_' + value.id),value.content,
    {
      after() {
        if(document.getElementById('content_' + value.id).clientHeight >= 300){
            value.expandAllOrClose = '展开全部'
        }
      },
    }
  );
  })
}
const postUserBehavior = (data)  =>    {
  return MyAxios.post("/share/userBehavior/",data)
}
//点击喜欢按钮
export const handleClickLike  = (share,type) => {
  share.isLike = !share.isLike
  postUserBehavior({
    id: share.id,
    type: 0,
    isAdd: type
  }).then(res=>{
  if(res.code == undefined){
      share.like = res
  }
})
}
//点击收藏按钮
export const handleClickCollect = (share,type)=>{
  share.isCollect = !share.isCollect
  postUserBehavior({
    id: share.id,
    type: 1,
    isAdd: type
  }).then(res=>{
  if(res.code == undefined){
      share.collect = res
  }
})
}