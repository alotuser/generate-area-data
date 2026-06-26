package cn.data.client;

import java.sql.SQLException;

import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.ds.simple.SimpleDSFactory;
import cn.hutool.setting.Setting;

public class DbClient {

	
	public static void initDataSource() throws SQLException {
		// 1. 创建一个 Setting 对象，按组名配置数据库连接
		Setting setting = new Setting();
		// 配置名为 "mysql" 的组（键名前缀为 ""）
		setting.set("url", "jdbc:mysql://192.168.1.74:3306/xxx?useSSL=false&serverTimezone=Asia/Shanghai");
		setting.set("user", "root");
		setting.set("pass", "xxxxxx");
		setting.set("driver", "com.mysql.cj.jdbc.Driver");

		DSFactory.create(setting);
		// 2. 创建数据源工厂（可使用 SimpleDSFactory 或 DSFactory.create）
		DSFactory factory = new SimpleDSFactory(setting); // 或者 DSFactory.create(setting)

		// 3. 将工厂设为全局，之后 Db.use("mysql") 就会使用此工厂中的数据源
		DSFactory.setCurrentDSFactory(factory);

		// Db.use("mysql").query("SELECT 1"); // 测试连接是否成功

	}
	
	
	
}
