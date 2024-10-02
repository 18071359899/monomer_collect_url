//package com.collect.backend.utils;
//
//import cn.hutool.extra.spring.SpringUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.otter.canal.client.CanalConnector;
//import com.alibaba.otter.canal.client.CanalConnectors;
//import com.alibaba.otter.canal.protocol.CanalEntry;
//import com.alibaba.otter.canal.protocol.Message;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import com.collect.backend.common.enums.SynchronousTableEnum;
//import com.collect.backend.domain.entity.*;
//import com.collect.backend.domain.vo.resp.ShareVo;
//import com.google.common.base.CaseFormat;
//import com.google.gson.Gson;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.meilisearch.sdk.Client;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.mapping.Environment;
//import org.apache.logging.log4j.util.PropertiesUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.support.PropertiesLoaderUtils;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.net.InetSocketAddress;
//import java.util.*;
//
///**
// * @author ruipeng.qi
// **/
//@Component
//@Slf4j
//public class CanalClient {
//	//Canal服务地址
//	@Value("${lcwyyds.canal.connect}")
//	private  String SERVER_ADDRESS;
//
//	//Canal Server 服务端口号
//	private static final Integer PORT = 11111;
//
//	//目的地，其实Canal Service内部有一个队列
//	private static final String DESTINATION = "example";
//
//	//用户名和密码，但是目前不支持，只能为空
//	private static final String USERNAME = "";
//
//	//用户名和密码，但是目前不支持，只能为空
//	private static final String PASSWORD= "";
//	public static final int MILLIS = 1000;
//	private static final int batchSize = 1000;
//	private static final Gson gson = SpringUtil.getBean("getGsonBean");
//	private static final Client client = SpringUtil.getBean("getClientBean");
////	private StartAppTaskFirst startAppTaskFirst = SpringUtil.getBean(StartAppTaskFirst.class);
//	public <T> Class<T> getClassByTableName(String tableName){  //拿到表名对应的class类
//		SynchronousTableEnum synchronousTableEnum = SynchronousTableEnum.getByTableName(tableName);
//		if(Objects.isNull(synchronousTableEnum)) return null;
//		switch (synchronousTableEnum){
//			case WEBSITE:
//				return (Class<T>) Website.class;
//			case CATEGORY:
//				return (Class<T>) Category.class;
//			case SHARE:
//				return (Class<T>) Share.class;
//			case USER:
//				return (Class<T>) User.class;
//			case UPLOAD_FILE:
//				return (Class<T>) UploadFile.class;
//		}
//		return null;
//	}
//	public <T> String convertCanalUpdateData(List<CanalEntry.Column> columnsList,Class<T> clazz){
//		if(Objects.isNull(clazz)) return null;
//		Map<String,Object> result = new HashMap<>();
//		for (CanalEntry.Column column : columnsList) {
//			String name = column.getName();
//			String value = column.getValue();
//			name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
//			result.put(name,value);
//		}
//		T realData = JSONObject.toJavaObject(new JSONObject(result), clazz);
//		String toJson = gson.toJson(realData);
//		return toJson;
//	}
//
//	@Async("threadPoolTaskExecutor")
//	public void synchronousData(){  //同步meilisearch和mysql的数据
////		startAppTaskFirst.init(client);  //先进行全量更新
//		CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress(SERVER_ADDRESS, PORT), DESTINATION, USERNAME, PASSWORD);
//		canalConnector.connect();
//		//订阅所有消息
//		canalConnector.subscribe(".*\\..*");
//		//恢复到之前同步的那个位置
//		canalConnector.rollback();
//
//		System.out.println("连接成功");
//		while (true){
//			try {
//				Thread.sleep(MILLIS);  //指定一段时间运行一次
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			}
//			//获取指定数量的数据，但是不做确认标记，下一次取还会取到这些信息
//			Message message = canalConnector.getWithoutAck(batchSize);
//			//获取消息id
//			long batchId = message.getId();
//			if(batchId != -1){
//				System.out.println("msgId -> " + batchId);
//				printEnity(message.getEntries());
//				//提交确认
//				canalConnector.ack(batchId);
//				//处理失败，回滚数据
//				//canalConnector.rollback(batchId);
//			}
//		}
//	}
//
//	/**
//	 * 获取数据库表对应的主键id，通过canal返回的isKey属性判断，true为主键，false为不
//	 * @param columnList
//	 * @return
//	 */
//	public String getTableIdValue(List<CanalEntry.Column> columnList){
//		for (CanalEntry.Column column : columnList) {
//			boolean isKey = column.getIsKey();
//			if(isKey){
//				return column.getValue();
//			}
//		}
//		return null;
//	}
//	private  void printEnity(List<CanalEntry.Entry> entries) {
//		for (CanalEntry.Entry entry : entries) {
//			try {
//				String schemaName = entry.getHeader().getSchemaName();
//				if(StringUtils.isNotBlank(schemaName) && !schemaName.equals("collect_url")){  //指定数据库
//					continue;
//				}
//				String tableName = entry.getHeader().getTableName();
//				if(StringUtils.isNotBlank(tableName) && Objects.isNull(SynchronousTableEnum.getByTableName(tableName))){  //指定数据库表
//					continue;
//				}
//				if(entry.getEntryType() != CanalEntry.EntryType.ROWDATA){
//					continue;
//				}
//				CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
//				for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
//						switch (rowChange.getEventType()){
//							//如果希望监听多种事件，可以手动增加case
//							case INSERT:
//							case UPDATE:
//								client.index(tableName).updateDocuments(convertCanalUpdateData(rowData.getAfterColumnsList(),
//										getClassByTableName(tableName)));
//								break;
//							case DELETE:
//								List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
//								String tableIdValue = getTableIdValue(beforeColumnsList);
//									if(Objects.nonNull(tableIdValue))
//								client.index(tableName).deleteDocument(tableIdValue);
//								break;
//							default:
//						}
//					}
//			}catch (Throwable e){
//				log.error("同步出现问题 ",e);
//			}
//
//		}
//	}
//}