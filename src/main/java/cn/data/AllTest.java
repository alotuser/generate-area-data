package cn.data;

import java.sql.SQLException;
import java.util.List;

import cn.data.client.AreaApiClient;
import cn.data.client.DbClient;
import cn.data.entity.OriginArea;
import cn.data.util.AreaJsonCompareUtil;
import cn.data.util.AreaJsonCompareUtil.DiffResult;
import cn.data.util.AreaTreeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.db.Db;
import cn.hutool.json.JSONUtil;

public class AllTest {

	// 历史基准JSON文件路径
	private static final String OLD_JSON_PATH = "D:/areaData.json";
	// 新生成JSON文件输出路径
	private static final String NEW_JSON_PATH = "D:/area_new.json";

	public static void main(String[] args) throws SQLException {

		
		
		//初始化数据库
		DbClient.initDataSource();

		try {
			
			// 步骤1：请求官方接口，解析数据保存到本地数据库 t_origin_area（脚本在src/main/resources下）
            // saveApiDataToDb();

            // 步骤2：查询本地全库，生成树形JSON并写入文件
            generateTreeJsonFile();

            // 步骤3：新旧JSON按名称递归对比，输出差异结果
            compareJsonDiff();
            
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 1. 请求官方接口保存到本地数据库 示例：你原有港澳台/直辖市插入逻辑，可替换为真实http接口拉取入库
	 */
	public static void saveApiDataToDb() throws SQLException {
		// 模拟：此处替换为HTTP请求获取接口返回树形数据，循环insert入库
		// 清空旧数据（可选，按需注释）
		Db.use().execute("TRUNCATE TABLE t_origin_area");

		AreaApiClient.syncArea();

		System.out.println("步骤1完成：接口数据已入库 t_origin_area");
	}

	/**
	 * 2. 查询本地数据库生成json文件写入磁盘
	 */
	public static void generateTreeJsonFile() throws SQLException {
		// 查询全表
		String whereStr = "";

		// whereStr= "WHERE CODE LIKE '82%'";

		List<OriginArea> entityList = Db.use().query("SELECT code,name,level,type FROM t_origin_area " + whereStr, OriginArea.class);

		// 调用工具生成树形JSON字符串 写入本地文件
		FileUtil.writeString(AreaTreeUtil.generateAreaTreeJson(entityList), NEW_JSON_PATH, "UTF-8");

		System.out.println("步骤2完成：新树形JSON已生成 -> " + NEW_JSON_PATH);
	}

	/**
	 * 3. 对比新旧json文件，按name逐层对比输出差异
	 */
	public static void compareJsonDiff() {
		// 判断旧基准文件是否存在
		if (!FileUtil.exist(OLD_JSON_PATH)) {
			System.out.println("旧基准文件不存在，跳过对比");
			return;
		}
		// 读取新旧文件

		// 1. 读取本地旧json文件
		String oldJson = FileUtil.readUtf8String("D:\\areaData.json");
		// 2. 新json调用之前AreaTreeUtil生成
		String newJson = FileUtil.readUtf8String(NEW_JSON_PATH);
		// 执行对比
		DiffResult diff = AreaJsonCompareUtil.compareByTreeName(oldJson, newJson);

		// 打印差异结果
		System.out.println("\n==================== 对比差异结果 ====================");
		System.out.println("【新增地区节点】");
		diff.added.forEach(System.out::println);
		FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(diff.added), NEW_JSON_PATH+".added");
		
		System.out.println("【删除地区节点】");
		diff.deleted.forEach(System.out::println);
		FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(diff.deleted), NEW_JSON_PATH+".deleted");
		
		//System.out.println("\n【同名节点字段变更】");
		//diff.modified.forEach((path, msg) -> System.out.printf("%s  -> %s%n", path, msg));
	}

}
