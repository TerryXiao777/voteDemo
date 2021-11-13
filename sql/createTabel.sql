CREATE TABLE `tb_area` (
  `area_ipStart` varchar(20) DEFAULT NULL,
  `area_ipEnd` varchar(20) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_option` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `option_name` varchar(200) DEFAULT NULL,
  `option_ballot` int(11) DEFAULT NULL,
  `option_order` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_voter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `voter_ip` bigint(15) DEFAULT NULL,
  `voter_voteoption` int(11) DEFAULT NULL,
  `voter_votetime` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;