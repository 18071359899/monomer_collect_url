import MyAxios from "@/utils/MyAxios";

export let addCategory = (document) => {
    return MyAxios.post("/category/add/", document);
}

export let updateCategory = (document) => {
    return MyAxios.post("/category/update/", document);
}
