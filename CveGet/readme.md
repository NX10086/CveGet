# CveGet #

## 项目介绍  ##
根据关键字从NVD官网爬虫，对所需的CVE详细信息进行获取（为了结果更加准确，需人工干预）
源地址：[https://nvd.nist.gov/vuln/search](https://nvd.nist.gov/vuln/search "源地址")
## 功能介绍 ##


1. 根据关键字进行搜素!

2. 根据每个CVE的summary进行人工筛选选择要留下的CVE进行接受

3. 最终会生成以keyword进行命名的json格式文件（位置在程序根目录）

## 开发环境 ##
- 操作系统：win10
- java版本-1.8.0_261
- 开发环境-IntelliJ IDEA 2019.1.3 x64

## 如何操作 ##
运行代码CVE.java文件即可


注意：人工干预过程中会不停向NVD发出请求，过程会很慢，需要全部过滤之后才能生成json文件。