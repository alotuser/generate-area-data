package cn.data.util;

import java.util.ArrayList;
import java.util.List;

import cn.data.entity.Address;
import cn.data.entity.AreaNode;
import cn.hutool.core.collection.CollUtil;

public class AreaConvertUtil {

	
	
	 /**
     * 根节点转换（根节点code=00，parentId=null）
     * @param rootNode 接口返回最外层data节点
     * @return 所有省级Address集合
     */
    public static List<Address> convertRootToAddress(AreaNode rootNode) {
        if (rootNode == null || CollUtil.isEmpty(rootNode.getChildren())) {
            return new ArrayList<>();
        }
        // 根节点code 00，所有省份的父id是"00"
        String rootCode = rootNode.getCode();
        List<AreaNode> provinceList = rootNode.getChildren();
        List<Address> addressList = new ArrayList<>();

        for (AreaNode province : provinceList) {
            Address address = convertSingleNode(province, rootCode);
            addressList.add(address);
        }
        return addressList;
    }

    /**
     * 递归转换单个AreaNode为Address
     * @param source 源节点
     * @param parentCode 父级code，作为parentId
     * @return Address
     */
    private static Address convertSingleNode(AreaNode source, String parentCode) {
        Address target = new Address();
        // id 使用区划编码
        target.setId(source.getCode());
        target.setCode(source.getCode());
        target.setName(source.getName());
        // 父ID = 父节点编码
        target.setParentId(parentCode);

        List<AreaNode> sourceChildren = source.getChildren();
        if (CollUtil.isNotEmpty(sourceChildren)) {
            List<Address> childAddressList = new ArrayList<>();
            for (AreaNode child : sourceChildren) {
                // 子节点的父ID为当前节点code
                Address childAddress = convertSingleNode(child, source.getCode());
                childAddressList.add(childAddress);
            }
            target.setChildren(childAddressList);
        }
        return target;
    }
    
}
