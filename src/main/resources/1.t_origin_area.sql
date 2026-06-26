

DROP TABLE IF EXISTS `t_origin_area`;

CREATE TABLE `t_origin_area` (
  `code` varchar(32) NOT NULL COMMENT '区划编码，唯一主键',
  `name` varchar(64) DEFAULT NULL COMMENT '地区名称（根节点code=00名称为null）',
  `level` tinyint(4) NOT NULL COMMENT '层级：省/直辖市、地级市、区县',
  `type` varchar(32) DEFAULT '' COMMENT '地区类型：省、直辖市、地级市、市辖区、自治州、特别行政区等',
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='接口原始全国省市区县数据表';


INSERT INTO t_origin_area (`code`,`name`,`level`,`type`) VALUES ('1101','北京城区',2,'市辖区');

INSERT INTO t_origin_area (`code`,`name`,`level`,`type`) VALUES ('1201','天津城区',2,'市辖区');

INSERT INTO t_origin_area (`code`,`name`,`level`,`type`) VALUES ('3101','上海城区',2,'市辖区');
 
INSERT INTO t_origin_area (`code`,`name`,`level`,`type`) VALUES ('5001','重庆城区',2,'市辖区');


-- 香港省级
INSERT INTO t_origin_area (`code`,`name`,`level`,`type`)
VALUES ('81','香港特别行政区',1,'特别行政区')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`),`level`=VALUES(`level`),`type`=VALUES(`type`);

-- 香港18个分区 
INSERT INTO t_origin_area (`code`,`name`,`level`,`type`)
VALUES
('8101','中西区',2,'市辖区'),
('8102','湾仔区',2,'市辖区'),
('8103','东区',2,'市辖区'),
('8104','南区',2,'市辖区'),
('8105','油尖旺区',2,'市辖区'),
('8106','深水埗区',2,'市辖区'),
('8107','九龙城区',2,'市辖区'),
('8108','黄大仙区',2,'市辖区'),
('8109','观塘区',2,'市辖区'),
('8110','荃湾区',2,'市辖区'),
('8111','葵青区',2,'市辖区'),
('8112','北区',2,'市辖区'),
('8113','西贡区',2,'市辖区'),
('8114','沙田区',2,'市辖区'),
('8115','屯门区',2,'市辖区'),
('8116','大埔区',2,'市辖区'),
('8117','元朗区',2,'市辖区'),
('8118','离岛区',2,'市辖区');

-- 澳门省级
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('82','澳门特别行政区','1','特别行政区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('8201','花地玛堂区','2','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('8202','望德堂区','2','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('8203','风顺堂区','2','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('8204','花王堂区','2','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('8205','大堂区','2','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('8206','路凼填海区','2','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('8207','嘉模堂区','2','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('8208','圣方济各堂区','2','市辖区');


-- 台湾省级
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('71','台湾省','1','省');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7101','台北市','2','地级市');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('710101','中正区','3','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('710101001','文祥里','4','里');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('710101002','华光里','4','里');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('710101003','永昌里','4','里');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('710102','大同区','3','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('710102001','建明里','4','里');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('710102002','民权里','4','里');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('710103','中山區','3','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('710104','万华区','3','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('710105','信义区','3','市辖区');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7102','新北市','2','地级市');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7103','桃园市','2','地级市');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7104','台中市','2','地级市');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7105','台南市','2','地级市');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7106','高雄市','2','地级市');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7107','基隆市','2','县级市');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7108','新竹市','2','县级市');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7109','嘉义市','2','县级市');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7110','新竹县','2','县');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7111','苗栗县','2','县');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7112','彰化县','2','县');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7113','南投县','2','县');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7114','云林县','2','县');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7115','嘉义县','2','县');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7116','屏东县','2','县');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7117','宜兰县','2','县');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7118','花莲县','2','县');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7119','台东县','2','县');
insert into `t_origin_area` (`code`, `name`, `level`, `type`) values('7120','澎湖县','2','县');