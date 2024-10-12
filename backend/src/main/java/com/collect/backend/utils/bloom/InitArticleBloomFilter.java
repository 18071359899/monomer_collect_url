package com.collect.backend.utils;

import com.collect.backend.dao.ShareDao;
import com.collect.backend.domain.entity.Share;
import com.collect.backend.domain.vo.req.common.CursorPageBaseReq;
import com.collect.backend.domain.vo.resp.ShareVo;
import com.collect.backend.domain.vo.resp.common.CursorPageBaseResp;
import com.collect.backend.service.impl.share.ShareServiceImpl;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.collect.backend.common.Constants.ARTICLE_DETAILS_BL_KEY;

@Component
public class InitArticleBloomFilter implements InitializingBean {
  @Autowired
  private ShareDao shareDao;
  @Autowired
  private RedissonClient redissonClient;
    public CursorPageBaseResp<Share> getCursorPageBaseResp(CursorPageBaseReq cursorPageBaseReq){
        CursorPageBaseResp<Share> cursorPageBaseResp =  shareDao.getSharePage(cursorPageBaseReq);
        if (CollectionUtils.isEmpty(cursorPageBaseResp.getList())) {
            return CursorPageBaseResp.empty();
        }
        List<Share> records = cursorPageBaseResp.getList();
        CursorPageBaseResp<Share> init = CursorPageBaseResp.init(cursorPageBaseResp,records);
        return init;
    }
  @Override
  @Async("threadPoolTaskExecutor")
  public void afterPropertiesSet() throws Exception {
      RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(ARTICLE_DETAILS_BL_KEY);
      bloomFilter.tryInit(100000000, 0.01);
   // 构建文章布隆过滤器
      CursorPageBaseReq cursorPageBaseReq = new CursorPageBaseReq();
      cursorPageBaseReq.setPageSize(500);
      while (true){
          CursorPageBaseResp<Share> cursorPageBaseResp = getCursorPageBaseResp(cursorPageBaseReq);
          Boolean isLast = cursorPageBaseResp.getIsLast();
          List<Long> collect = cursorPageBaseResp.getList().stream().map(Share::getId).collect(Collectors.toList());
          for (Long id : collect) {
              bloomFilter.add(id);
          }
          if(isLast){  //是最后一页
              break;
          }
          cursorPageBaseReq.setCursor(cursorPageBaseResp.getCursor());
      }
 }
}