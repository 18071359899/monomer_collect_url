//前端做添加评论的处理
export const addComment = (commentList,id,newComment) => {
    //级别：一级评论
    if (id === 0) {
        commentList.unshift(newComment);
        return;
    }

    for (let i = 0; i < commentList.length; i++) {
        let comment = commentList.at(i);
        //级别：二级评论
        if (comment.id === id) {
            //底下没有子评论
            if (comment.children === null) {
                comment.children = [];
            }
            comment.children.unshift(newComment);
            break;
        }

        let commentChildren = comment.children;
        commentChildren = commentChildren ||  [] 

        //判断回复的是否是子评论
        let isFind = false;
        //级别：多级评论
        for (let j = 0; j < commentChildren.length; j++) {
            let commentChild = commentChildren.at(j);
            if (commentChild.id === id) {
                commentChildren.splice(j + 1, 0, newComment);
                isFind = true;
                break;
            }
        }
        //找到了，提前返回
        if (isFind === true) {
            break;
        }


    }
}