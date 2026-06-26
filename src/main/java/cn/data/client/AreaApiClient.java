package cn.data.client;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.data.entity.AreaNode;
import cn.data.entity.AreaResponse;
import cn.hutool.core.lang.Console;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

public class AreaApiClient {
	// 接口基础地址
	private static final String BASE_URL = "https://dmfw.mca.gov.cn/9095/xzqh/getList?code=341300000000&&maxLevel=2";
	// 请求超时 10秒
	private static final int TIMEOUT = -1;

	// 固定常量编码 测试单个省份
	private static final String TEST_CODE = "340000000000";

	/**
	 * 根据12位完整区域编码 + level 截取父级短编码
	 * 
	 * @param fullCode 12位完整编码，例：341302001000
	 * @param level    层级 1/2/3/4
	 * @return 截取后的短编码
	 */
	public static String getShortCodeByLevel(String fullCode, int level) {
		if (StrUtil.isBlank(fullCode)) {
			return "";
		}
		int len;
		switch (level) {
		case 1:
			len = 2;
			break;
		case 2:
			len = 4;
			break;
		case 3:
			len = 6;
			break;
		case 4:
			len = 9;
			break;
		default:
			throw new IllegalArgumentException("仅支持level：1、2、3、4");
		}
		// StrUtil.sub(str, start, end) 左闭右开
		return StrUtil.sub(fullCode, 0, len);
	}

	/**
	 * 通用区划查询接口
	 * 
	 * @param areaCode 区划编码 根节点传00
	 * @param maxDeep  层级深度 2=省市，3=省市区县
	 * @return 区划树形响应
	 */
	public static AreaResponse getAreaTree(String areaCode) {
		// 封装请求参数
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("code", areaCode);
		paramMap.put("maxLevel", 2);

		UrlBuilder urlBuilder = UrlBuilder.of(BASE_URL);
		urlBuilder.setQuery(UrlQuery.of(paramMap));

		String url = urlBuilder.build();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 避免请求过快被封禁，按需调整

		do {

			// Hutool 构建GET请求，拼接参数
			HttpRequest request = HttpUtil.createGet(url).timeout(TIMEOUT);

			// 携带token鉴权（按需打开）
			// request.header("token", "你的登录token");

			String jsonStr = request.execute().body();
			AreaResponse response = JSONUtil.toBean(jsonStr, AreaResponse.class);

			// 校验接口返回状态
			if (response == null || !Integer.valueOf(200).equals(response.getStatus())) {
				String msg = response != null ? response.getMessage() : "接口无返回数据";
				Console.error("接口请求失败，URL: {}, 响应状态: {}, 消息: {}", url, response != null ? response.getStatus() : "null", msg);
				continue; // 请求失败，继续重试
			}

			return response;

		} while (true);

	}

	/**
	 * 业务1：查询全国所有 省 + 地级市（maxdeep=2，不加载区县）
	 */
	public static AreaResponse getAllProvinceAndCity() {
		return getAreaTree("00");
	}

	/**
	 * 业务2：根据城市编码，查询该城市下所有区县（maxdeep=3）
	 * 
	 * @param cityCode 城市编码，如110100、440300
	 */
	public static AreaResponse getDistrictByCityCode(String cityCode) {
		return getAreaTree(cityCode);
	}

	public static void insertOrUpdate(AreaNode area) throws SQLException {

		String code = area.getCode();
		code = getShortCodeByLevel(code, area.getLevel()); // 根据层级截取短编码

		Entity entity = Entity.create("t_origin_area").set("code", code) // 主键字段
				.set("name", area.getName()).set("type", area.getType()).set("level", area.getLevel());

		Db.use().insertOrUpdate(entity, "code"); // 根据 code 字段进行插入或更新

	}

	/**
	 * 入口：同步所有省/市/区/街道
	 * 
	 * @throws SQLException
	 */
	public static void syncArea() throws SQLException {
		// 1. 获取全国省市两级数据
		AreaResponse allArea = AreaApiClient.getAllProvinceAndCity();
		List<AreaNode> provinceList = allArea.getData().getChildren();

		System.out.println("======= 全国省市列表 =======");

		for (AreaNode province : provinceList) {
//			if (!TEST_CODE.equals(province.getCode())) {
//				continue;
//			}
			// 从省份开始递归，层级0
			syncChildArea(province, 0);
		}
	}

	/**
	 * 递归同步当前区域及其所有下级子区域
	 * 
	 * @param currArea 当前区域节点
	 * @param level    层级，控制打印缩进
	 * @throws SQLException
	 */
	private static void syncChildArea(AreaNode currArea, int level) throws SQLException {
		// 打印缩进空格
		String space = StrUtil.repeat("  ", level);
		// 保存当前区域到库
		insertOrUpdate(currArea);
		// 打印信息
		System.out.printf("%s%s-%s%n", space, currArea.getCode(), currArea.getName());

		// 查询当前区域下一级子节点

		List<AreaNode> childList = currArea.getChildren();
		// 没有下级直接结束递归
		if (childList == null || childList.isEmpty()) {
			if (currArea.getLevel().intValue() == 4) {
				return;
			}
			AreaResponse childResp = AreaApiClient.getDistrictByCityCode(currArea.getCode());
			if (childResp.getData() == null || childResp.getData().getChildren() == null || childResp.getData().getChildren().isEmpty()) {
				return;
			}
			childList = childResp.getData().getChildren();
		}

		// 循环子节点，层级+1继续递归
		for (AreaNode child : childList) {
			syncChildArea(child, level + 1);
		}
	}

}
