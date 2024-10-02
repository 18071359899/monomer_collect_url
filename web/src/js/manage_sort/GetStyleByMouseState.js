// 根据鼠标状态返回样式

//改变当前鼠标状态
export const changeState = (document, newMouseState) => {
    if (document.mouseState !== "active") {
      document.mouseState = newMouseState;
    }
  };
  //根据鼠标状态获取样式
export const getDocumentStyle = (mouseState) => {
    switch (mouseState) {
      case "hover":
        return "background-color:#f1f3f8;";
      case "active":
        return "background-color:#F2FAFF;";
      case "none":
        return "";
    }
  };
export const getDocumentIcon = (mouseState) => {
    switch (mouseState) {
      case "hover":
        return "display: inline-flex;";
      case "active":
        return "display: inline-flex;background-color: #0095ff;color: #cfebff;";
      case "none":
        return "";
    }
  };