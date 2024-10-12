package com.collect.backend.service.impl.search;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ErrorCode;
import com.collect.backend.common.ResultUtils;
import com.collect.backend.domain.vo.req.SearchReq;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.domain.vo.resp.UserVo;
import com.collect.backend.service.adpter.ShareAdpter;
import com.collect.backend.service.adpter.UserAdpter;
import com.collect.backend.service.search.SearchService;
import com.collect.backend.utils.assertBussion.AssertUtil;
import com.collect.backend.utils.ManageUserInfo;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.collect.backend.utils.date_string_convert.DateStringMutualConvertUtils.StrToDate;


@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ShareAdpter shareAdpter;
    @Autowired
    private UserAdpter userAdpter;
    @Autowired
    private Client client;
    @Override
    public BaseResponse<Page<?>> search(SearchReq searchReq) {
        AssertUtil.isNotEmpty(searchReq,"搜索条件不能为空");
        String type = searchReq.getType();
        String search = searchReq.getSearch();
        Integer page = (int) searchReq.getPage();
        Integer size = (int) searchReq.getSize();
        if("user".equals(type)){
            Index index = client.index("user");
            String[] strs = new String[]{"username"};
            SearchResult searchResult = (SearchResult) index.search(new SearchRequest(search)
                    .setAttributesToSearchOn(strs)
                    .setAttributesToHighlight(new String[] {"overview"})
                    .setHighlightPreTag("<span class=\"highlight\">")
                    .setHighlightPostTag("</span>")
                            .setLimit(size)
                            .setOffset((page-1) * size));
            //todo 将返回的_formatted对象中的高亮属性渲染到数据上 参考 https://www.meilisearch.com/docs/reference/api/search#highlight-tags
            Page<UserVo> userVoPage = new Page<>(searchReq.getPage(),searchReq.getSize(),searchResult.getEstimatedTotalHits());
            ArrayList<HashMap<String, Object>> hits = searchResult.getHits();
            userVoPage.setRecords(getUserVoList(hits));
            return ResultUtils.success(userVoPage);
        }else if("share".equals(type)){
            Index index = client.index("share");
            String[] strs = new String[]{"title","content","like","comment","reading","collect"};
            SearchResult searchResult = (SearchResult) index.search(new SearchRequest(search)
                    .setAttributesToSearchOn(strs)
                    .setLimit(size)
                    .setAttributesToHighlight(new String[] {"overview"})
                    .setHighlightPreTag("<span class=\"highlight\">")
                    .setHighlightPostTag("</span>")
                    .setOffset((page-1) * size));
            Page<ShareVo> shareVoPage = new Page<>(searchReq.getPage(),searchReq.getSize(),searchResult.getEstimatedTotalHits());
            ArrayList<HashMap<String, Object>> hits = searchResult.getHits();
            shareVoPage.setRecords(getShareVoList(hits));
            return ResultUtils.success(shareVoPage);
        }
        return ResultUtils.error(ErrorCode.FORBIDDEN_ERROR);
    }

    public String getFormatStr(){
        return "yyyy-MM-dd HH:mm";
    }
    private List<ShareVo> getShareVoList(ArrayList<HashMap<String, Object>> hits) {
        List<ShareVo> shareVOList = new ArrayList<>();
        Long userId = ManageUserInfo.getUser().getId();
        hits.forEach(hit->{
            String createTIme = (String) hit.get("createTime");
            hit.put("createTime",StrToDate(createTIme,getFormatStr()));
            String updateTime = (String) hit.get("updateTime");
            hit.put("updateTime",StrToDate(updateTime,getFormatStr()));
            ShareVo shareVo = JSONObject.toJavaObject(new JSONObject(hit), ShareVo.class);
            shareVOList.add(shareAdpter.getShareVo(shareVo,userId));
        });
        return shareVOList;
    }

    private List<UserVo> getUserVoList(ArrayList<HashMap<String, Object>> hits) {
            List<UserVo> userVos = new ArrayList<>();
            Long userId = ManageUserInfo.getUser().getId();
            hits.forEach(hit->{
                UserVo userVo = JSONObject.toJavaObject(new JSONObject(hit), UserVo.class);
                if(!userId.equals(userVo.getId())){   //剔除自己
                    userAdpter.setUserInfo(userVo,userId);
                    userVos.add(userVo);
                }
            });
            return userVos;
    }
}
