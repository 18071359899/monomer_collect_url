<template>
  <div class="loginBox" v-if="currView === 'login'">
    <div class="sign-up">
      <h1 class="sign-up-title">登 陆 界 面</h1>
      <input
        type="text"
        class="sign-up-input"
        placeholder="请输入用户名"
        autofocus
        v-model="loginUsername"
      />
      <input
        type="password"
        class="sign-up-input"
        placeholder="请输入密码"
        v-model="loginPassword"
      />
      <img
        :src="'data:image/png;base64,' + captchaObject.imgCode"
        alt=""
        class="captchaImg"
        @click="getCaptcha"
      />
      <input
        v-model="code"
        type="text"
        class="sign-up-input"
        id="code"
        placeholder="请输入验证码"
      />
      <button class="sign-up-button" @click="login">登陆</button>
      <p class="sign-up-msg">
        没有账号？<a @click="currView = 'resgiser'" class="link">立即注册</a>
      </p>
    </div>
  </div>
  <div class="registerBox" v-else-if="currView === 'resgiser'">
    <div class="sign-up">
      <h1 class="sign-up-title">注 册 界 面</h1>
      <input
        type="text"
        class="sign-up-input"
        placeholder="请输入用户名"
        autofocus
        v-model="resgiserUsername"
      />
      <input
        type="password"
        class="sign-up-input"
        placeholder="请输入密码"
        v-model="resgiserPassword"
      />
      <input
        type="password"
        class="sign-up-input"
        placeholder="请再次输入密码"
        v-model="resgiserConfirmPassword"
      />
      <button class="sign-up-button" @click="resgister">注册</button>
      <p class="sign-up-msg">
        已有账号？<a @click="currView = 'login'" class="link">立即登陆</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import myAxios from "@/utils/MyAxios.js";
import { ElMessage } from "element-plus";
import router from "@/router";
const currView = ref("login");
const loginUsername = ref("");
const loginPassword = ref("");
const resgiserUsername = ref("");
const resgiserPassword = ref("");
const resgiserConfirmPassword = ref("");
const code = ref("");

let captchaObject = reactive({
  imgCode: "",
  uuid: "",
});
onMounted(() => {
  getCaptcha();
});
const getCaptcha = () => {
  myAxios.get("/captcha").then((res) => {
    if (res.error_message === "successfully") {
      captchaObject.imgCode = res.img;
      captchaObject.uuid = res.uuid;
    }
  });
};
const login = () => {
  myAxios({
    url: "/user/account/token/",
    method: "post", //put
    params: {
      username: loginUsername.value,
        password: loginPassword.value,
        code: code.value,
        uuid: captchaObject.uuid,
    },
  }).then((res) => {
    if (res.error_message === "successfully") {
      ElMessage({ type: "success", message: "登陆成功" });
      localStorage.setItem("COLLECT_URL_TOKEN", res.token);
      router.push({name:'home'})
    } else {
      if(res.error_message){
        ElMessage({type: 'warning',message: res.error_message})
      }
      getCaptcha();
    }
  });
};
const resgister = () => {
  myAxios({
    url: "/user/account/register/",
    method: "post", 
    params: {
      username: resgiserUsername.value,
      password: resgiserPassword.value,
      confirmPassword: resgiserConfirmPassword.value,
    },
  }).then((res) => {
    if (res.error_message === "successfully") {
      ElMessage({ type: "success", message: "注册成功" });
      currView.value = "login";
    } else {
      ElMessage({ type: "warning", message: res.error_message });
    }
  });
};
</script>
<style scoped>
.captchaImg {
  margin-top: -10px;
  margin-bottom: 10px;
  cursor: pointer;
}
.link {
  color: #409eff;
  cursor: pointer;
}
.sign-up-msg {
  margin-top: 20px;
  text-align: center;
}
body {
  font: 13px/20px "Helvetica Neue", Helvetica, Arial, sans-serif;
  color: #404040;
  background: #596778;
}
.sign-up {
  position: relative;
  margin: 100px auto;
  width: 280px;
  padding: 33px 25px 29px;
  background: white;
  border-bottom: 1px solid #c4c4c4;
  border-radius: 5px;
  -webkit-box-shadow: 0 1px 5px rgba(0, 0, 0, 0.25);
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.25);
}

.sign-up:before,
.sign-up:after {
  content: "";
  position: absolute;
  bottom: 1px;
  left: 0;
  right: 0;
  height: 10px;
  background: inherit;
  border-bottom: 1px solid #d2d2d2;
  border-radius: 4px;
}

.sign-up:after {
  bottom: 3px;
  border-color: #dcdcdc;
}

.sign-up-title {
  margin: -25px -25px 25px;
  padding: 15px 25px;
  line-height: 35px;
  font-size: 26px;
  font-weight: 300;
  color: #aaa;
  text-align: center;
  text-shadow: 0 1px rgba(255, 255, 255, 0.75);
  background: #f7f7f7;
}

.sign-up-title:before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 8px;
  background: #c4e17f;
  border-radius: 5px 5px 0 0;
  background-image: -webkit-linear-gradient(
    left,
    #c4e17f,
    #c4e17f 12.5%,
    #f7fdca 12.5%,
    #f7fdca 25%,
    #fecf71 25%,
    #fecf71 37.5%,
    #f0776c 37.5%,
    #f0776c 50%,
    #db9dbe 50%,
    #db9dbe 62.5%,
    #c49cde 62.5%,
    #c49cde 75%,
    #669ae1 75%,
    #669ae1 87.5%,
    #62c2e4 87.5%,
    #62c2e4
  );
  background-image: -moz-linear-gradient(
    left,
    #c4e17f,
    #c4e17f 12.5%,
    #f7fdca 12.5%,
    #f7fdca 25%,
    #fecf71 25%,
    #fecf71 37.5%,
    #f0776c 37.5%,
    #f0776c 50%,
    #db9dbe 50%,
    #db9dbe 62.5%,
    #c49cde 62.5%,
    #c49cde 75%,
    #669ae1 75%,
    #669ae1 87.5%,
    #62c2e4 87.5%,
    #62c2e4
  );
  background-image: -o-linear-gradient(
    left,
    #c4e17f,
    #c4e17f 12.5%,
    #f7fdca 12.5%,
    #f7fdca 25%,
    #fecf71 25%,
    #fecf71 37.5%,
    #f0776c 37.5%,
    #f0776c 50%,
    #db9dbe 50%,
    #db9dbe 62.5%,
    #c49cde 62.5%,
    #c49cde 75%,
    #669ae1 75%,
    #669ae1 87.5%,
    #62c2e4 87.5%,
    #62c2e4
  );
  background-image: linear-gradient(
    to right,
    #c4e17f,
    #c4e17f 12.5%,
    #f7fdca 12.5%,
    #f7fdca 25%,
    #fecf71 25%,
    #fecf71 37.5%,
    #f0776c 37.5%,
    #f0776c 50%,
    #db9dbe 50%,
    #db9dbe 62.5%,
    #c49cde 62.5%,
    #c49cde 75%,
    #669ae1 75%,
    #669ae1 87.5%,
    #62c2e4 87.5%,
    #62c2e4
  );
}

input {
  font-family: inherit;
  color: inherit;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
}

.sign-up-input {
  width: 100%;
  height: 50px;
  margin-bottom: 25px;
  padding: 0 15px 2px;
  font-size: 17px;
  background: white;
  border: 2px solid #ebebeb;
  border-radius: 4px;
  -webkit-box-shadow: inset 0 -2px #ebebeb;
  box-shadow: inset 0 -2px #ebebeb;
}

.sign-up-input:focus {
  border-color: #62c2e4;
  outline: none;
  -webkit-box-shadow: inset 0 -2px #62c2e4;
  box-shadow: inset 0 -2px #62c2e4;
}

.lt-ie9 .sign-up-input {
  line-height: 48px;
}

.sign-up-button {
  position: relative;
  vertical-align: top;
  width: 100%;
  height: 54px;
  padding: 0;
  font-size: 22px;
  color: white;
  text-align: center;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
  background: #f0776c;
  border: 0;
  border-bottom: 2px solid #d76b60;
  border-radius: 5px;
  cursor: pointer;
  -webkit-box-shadow: inset 0 -2px #d76b60;
  box-shadow: inset 0 -2px #d76b60;
}

.sign-up-button:active {
  top: 1px;
  outline: none;
  -webkit-box-shadow: none;
  box-shadow: none;
}

:-moz-placeholder {
  color: #ccc;
  font-weight: 300;
}

::-moz-placeholder {
  color: #ccc;
  opacity: 1;
  font-weight: 300;
}

::-webkit-input-placeholder {
  color: #ccc;
  font-weight: 300;
}

:-ms-input-placeholder {
  color: #ccc;
  font-weight: 300;
}

::-moz-focus-inner {
  border: 0;
  padding: 0;
}
</style>
