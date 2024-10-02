//package com.collect.backend.utils;
//
//import com.collect.backend.dao.*;
//import com.collect.backend.domain.entity.*;
//import com.google.gson.Gson;
//import com.meilisearch.sdk.Client;
//import com.meilisearch.sdk.Index;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//
//
///**
// * 进行mysql到meilisearch的全量更新
// */
//@Component
//@Slf4j
//public class StartAppTaskFirst {
//    @Autowired
//    private ShareDao shareDao;
//    @Autowired
//    private UserDao userDao;
//
//    @Autowired
//    private CategoryDao categoryDao;
//    @Autowired
//    private WebsiteDao websiteDao;
//    @Autowired
//    private UploadFileDao uploadFileDao;
//    @Autowired
//    private Gson gson;
//    @Autowired
//    private Client client;
//    @PostConstruct
//    //开始时执行
//    public void init() {
//        try {
//            client.deleteIndex("category");
//            client.deleteIndex("website");
//            client.deleteIndex("user");
//            client.deleteIndex("share");
//
//            client.createIndex("category","id");
//            client.createIndex("website","id");
//            client.createIndex("user","id");
//            client.createIndex("share","id");
//            client.createIndex("upload_file","id");
//            Index category = client.index("category");
//            Index website = client.index("website");
//            Index user = client.index("user");
//            Index share = client.index("share");
//            Index uploadFile = client.index("upload_file");
//            List<Category> categories = categoryDao.getBaseMapper().selectList(null);
//            List<Website> websites = websiteDao.getBaseMapper().selectList(null);
//            List<User> users = userDao.getBaseMapper().selectList(null);
//            List<Share> shares = shareDao.getBaseMapper().selectList(null);
//            List<UploadFile> uploadFileList = uploadFileDao.getBaseMapper().selectList(null);
//
//
//            category.addDocuments(gson.toJson(categories));
//            website.addDocuments(gson.toJson(websites));
//            user.addDocuments(gson.toJson(users));
//            share.addDocuments(gson.toJson(shares));
//            uploadFile.addDocuments(gson.toJson(uploadFileList));
//
//            category.updateFilterableAttributesSettings(new String[]{"isDelete","userId"});
//            website.updateFilterableAttributesSettings(new String[]{"isDelete","userId"});
//            uploadFile.updateFilterableAttributesSettings(new String[]{"isDelete","userId"});
//        }catch (Throwable e){
//            log.error("全量更新失败");
//        }
//
//    }
//}
