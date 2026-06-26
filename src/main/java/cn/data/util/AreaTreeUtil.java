package cn.data.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.data.entity.Address;
import cn.data.entity.OriginArea;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

public class AreaTreeUtil {

    /**
     * 根据level获取父级短编码（parentId）
     */
    private static String getParentCode(String currCode, int level) {
        if (level == 1) {
            return null;
        }
        if (level == 2) {
            // 二级：取前2位省级
            return StrUtil.sub(currCode, 0, 2);
        }
        if (level == 3) {
            // 三级：取前4位区县
            return StrUtil.sub(currCode, 0, 4);
        }
        if (level == 4) {
            // 四级：取前6位乡镇
            return StrUtil.sub(currCode, 0, 6);
        }
        return null;
    }

    /**
     * OriginArea 转 Address
     */
    private static Address convertToAddress(OriginArea area) {
        Address addr = new Address();
        String code = area.getCode();
        int level = area.getLevel();

        addr.setId(code);
        addr.setCode(code);
        addr.setName(area.getName());
        addr.setParentId(getParentCode(code, level));
        return addr;
    }

    /**
     * 递归组装树形
     * @param allList 全量地址集合
     * @param parentCode 父编码
     * @return 子节点列表
     */
    private static List<Address> buildTree(List<Address> allList, String parentCode) {
        List<Address> children = allList.stream()
                .filter(item -> Objects.equals(item.getParentId(), parentCode))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(children)) {
            return null;
        }
        for (Address child : children) {
            List<Address> sub = buildTree(allList, child.getId());
            child.setChildren(sub);
        }
        return children;
    }

    /**
     * 入口：查询全表数据，生成树形JSON字符串
     */
    public static String generateAreaTreeJson(List<OriginArea> dbAreaList) {
        // 1. 全部转为Address
        List<Address> allAddressList = dbAreaList.stream()
                .map(AreaTreeUtil::convertToAddress)
                .collect(Collectors.toList());

        // 2. 取根节点 level=1（parentId=null）
        List<Address> rootTree = buildTree(allAddressList, null);

        // 3. 输出格式化JSON字符串
       
        return  JSONUtil.toJsonStr(rootTree);
    }

    // 测试示例
    public static void main(String[] args) {
        // 1. 此处替换为你的MyBatis查询：mapper.selectAll()
        List<OriginArea> dbData = new ArrayList<>();

        // 2. 生成树形JSON
        String jsonStr = generateAreaTreeJson(dbData);
        System.out.println(jsonStr);
    }
}
