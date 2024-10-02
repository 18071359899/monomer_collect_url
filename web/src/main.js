import { createApp } from 'vue'
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.min.js";
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/icons'
import SvgIcon from "@/components/svgicon/IndexComponent.vue";
import locale from '@/assets/locale/cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createPinia } from 'pinia'
const app = createApp(App).use(router).use(ElementPlus, {
    locale
  }).use(ElementPlus).use(createPinia())

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}


app.component("svg-icon", SvgIcon);
app.mount('#app')



