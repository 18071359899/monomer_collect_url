import Vditor from "vditor";
import "vditor/dist/index.css";
export let renderMarkDown = (dom,value) => {
    Vditor.preview(
        dom,value,
        {
          after() {
          },
        }
      );
}