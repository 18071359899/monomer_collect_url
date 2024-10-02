import MyAxios from "@/utils/MyAxios";

export let copyDirectory = (pasteToDiectoryResp) => {
    return MyAxios.post("/directory/copy/", pasteToDiectoryResp);
}

export let cutDirectory = (pasteToDiectoryResp) => {
    return MyAxios.post("/directory/cut/", pasteToDiectoryResp);
}

export let deleteDirectory = (selectedIds) => {
    return MyAxios.post("/directory/delete/", selectedIds);
}

export let getDirectory = (pid) => {
    return MyAxios.get("/directory/get/", pid);
}

export let getPositionDirectory = (pid) => {
    return MyAxios.get("/directory/get/position/", pid);
}

export let searchDirectory = (search) => {
    return MyAxios.get("/directory/search/", { params:{ search } });
}

export let getUserSpaceApi = () => {
    return MyAxios.get("/directory/get/user/space");
}

export let listWebsiteRequest = () =>{
    return MyAxios.get("/website/list/");
}