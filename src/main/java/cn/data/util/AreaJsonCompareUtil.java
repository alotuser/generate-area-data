package cn.data.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import cn.data.entity.Address;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;

public class AreaJsonCompareUtil {

	// 差异结果封装
	public static class DiffResult {
		// 已删除节点完整路径
		public List<String> deleted = new ArrayList<>();
		// 新增节点完整路径
		public List<String> added = new ArrayList<>();
		// 变更节点：路径+变更说明
		public Map<String, String> modified = new LinkedHashMap<>();
	}

	/**
	 * 入口方法：新旧json字符串对比
	 * 
	 * @param oldJson 旧文件json
	 * @param newJson 新生成树形json
	 * @return 全部差异
	 */
	public static DiffResult compareByTreeName(String oldJson, String newJson) {
		DiffResult result = new DiffResult();
		List<Address> oldTree = JSONUtil.toList(oldJson, Address.class);
		List<Address> newTree = JSONUtil.toList(newJson, Address.class);

		// 递归对比根节点，根路径为空
		compareNodeList(oldTree, newTree, "", result);
		return result;
	}

	/**
	 * 递归对比同一层级节点列表
	 * 
	 * @param oldList    旧层节点
	 * @param newList    新层节点
	 * @param parentPath 父级路径  
	 * @param diff       差异收集
	 */
	private static void compareNodeList(List<Address> oldList, List<Address> newList, String parentPath, DiffResult diff) {
		if (CollUtil.isEmpty(oldList) && CollUtil.isEmpty(newList)) {
			return;
		}
		// 构建 name -> node 映射
		Map<String, Address> oldMap = oldList.stream().collect(Collectors.toMap(Address::getName, o -> o));
		Map<String, Address> newMap = newList.stream().collect(Collectors.toMap(Address::getName, n -> n));

		Set<String> allNames = new HashSet<>();
		allNames.addAll(oldMap.keySet());
		allNames.addAll(newMap.keySet());

		for (String name : allNames) {
			String fullPath = parentPath.isEmpty() ? name : parentPath + "/" + name;
			Address oldNode = oldMap.get(name);
			Address newNode = newMap.get(name);

			// 1. 旧存在，新不存在 = 删除
			if (oldNode != null && newNode == null) {
				diff.deleted.add(fullPath);
				continue;
			}
			// 2. 新存在，旧不存在 = 新增
			if (oldNode == null && newNode != null) {
				diff.added.add(fullPath);
				// 递归对比新增节点的子节点
				compareNodeList(Collections.emptyList(), newNode.getChildren(), fullPath, diff);
				continue;
			}
			// 3. 同名节点，对比字段是否变更
			StringBuilder changeMsg = new StringBuilder();
			if (!Objects.equals(oldNode.getCode(), newNode.getCode())) {
				// changeMsg.append("code:").append(oldNode.getCode()).append("→").append(newNode.getCode()).append(" ");
			}
			// 可扩展对比level、type等，Address无type可忽略
			// if (!Objects.equals(oldNode.getLevel(), newNode.getLevel())) {}
			if (changeMsg.length() > 0) {
				diff.modified.put(fullPath, changeMsg.toString());
			}
			// 递归对比子节点
			List<Address> oldChild = oldNode.getChildren() == null ? new ArrayList<>() : oldNode.getChildren();
			List<Address> newChild = newNode.getChildren() == null ? new ArrayList<>() : newNode.getChildren();
			compareNodeList(oldChild, newChild, fullPath, diff);
		}
	}

}
