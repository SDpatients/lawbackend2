import java.util.ArrayList;
import java.util.List;

public class WriteCreditorClaimData {

    public static void main(String[] args) {
        String filePath = "d:\\Ai\\lawbackend2\\creditor_claims.xls";
        
        List<CreditorClaimData> dataList = generateVirtualData();
        
        ExcelWriter.writeToLocalFile(filePath, dataList, CreditorClaimData.class);
        
        System.out.println("虚拟数据已成功写入Excel文件: " + filePath);
        System.out.println("共写入 " + dataList.size() + " 条数据");
    }

    private static List<CreditorClaimData> generateVirtualData() {
        List<CreditorClaimData> dataList = new ArrayList<>();
        
        // 生成第1条数据
        CreditorClaimData data1 = new CreditorClaimData();
        data1.setReceiptNumber("ZJ2025010001");
        data1.setCreditor("上海华东建筑有限公司");
        data1.setDeclarationTime("2025-01-10");
        data1.setAddress("上海市浦东新区东方路1000号/200120");
        data1.setContactPhone("13801651234");
        data1.setDeclarationAmount("5,250,000.00");
        data1.setNature("企业");
        data1.setLegalRepresentative("张伟");
        data1.setAgent("李娜");
        data1.setAgentPhone("13912345678");
        data1.setClaimNature("普通债权");
        data1.setClaimType("工程债权");
        data1.setAccountName("上海华东建筑有限公司");
        data1.setBankName("中国工商银行上海陆家嘴支行");
        data1.setAccountNumber("1001123456789012");
        data1.setLitigation("否");
        data1.setRemarks("工程款及违约金");
        dataList.add(data1);
        
        // 生成第2条数据
        CreditorClaimData data2 = new CreditorClaimData();
        data2.setReceiptNumber("ZJ2025010002");
        data2.setCreditor("李明");
        data2.setDeclarationTime("2025-01-11");
        data2.setAddress("北京市海淀区知春路82号/100080");
        data2.setContactPhone("18612345678");
        data2.setDeclarationAmount("120,000.00");
        data2.setNature("个人");
        data2.setLegalRepresentative("-");
        data2.setAgent("王律师");
        data2.setAgentPhone("15098765432");
        data2.setClaimNature("普通债权");
        data2.setClaimType("借款");
        data2.setAccountName("李明");
        data2.setBankName("中国建设银行北京中关村支行");
        data2.setAccountNumber("6217001234567890123");
        data2.setLitigation("否");
        data2.setRemarks("个人借贷，已到期");
        dataList.add(data2);
        
        // 生成第3条数据
        CreditorClaimData data3 = new CreditorClaimData();
        data3.setReceiptNumber("ZJ2025010003");
        data3.setCreditor("深圳创新科技集团");
        data3.setDeclarationTime("2025-01-12");
        data3.setAddress("深圳市南山区科技园南区D栋/518057");
        data3.setContactPhone("0755-66889900");
        data3.setDeclarationAmount("3,000,000.00");
        data3.setNature("企业");
        data3.setLegalRepresentative("王磊");
        data3.setAgent("赵敏");
        data3.setAgentPhone("13666554433");
        data3.setClaimNature("有财产担保债权");
        data3.setClaimType("货款");
        data3.setAccountName("深圳创新科技集团");
        data3.setBankName("招商银行深圳南山支行");
        data3.setAccountNumber("755912345678901");
        data3.setLitigation("是");
        data3.setRemarks("设备抵押");
        dataList.add(data3);
        
        // 生成第4条数据
        CreditorClaimData data4 = new CreditorClaimData();
        data4.setReceiptNumber("ZJ2025010004");
        data4.setCreditor("北京天成商贸有限公司");
        data4.setDeclarationTime("2025-01-15");
        data4.setAddress("北京市朝阳区建国门外大街1号/100022");
        data4.setContactPhone("010-65881234");
        data4.setDeclarationAmount("850,000.00");
        data4.setNature("企业");
        data4.setLegalRepresentative("刘强");
        data4.setAgent("-");
        data4.setAgentPhone("-");
        data4.setClaimNature("普通债权");
        data4.setClaimType("货款");
        data4.setAccountName("北京天成商贸有限公司");
        data4.setBankName("中国银行北京国贸支行");
        data4.setAccountNumber("4563512345678888");
        data4.setLitigation("否");
        data4.setRemarks("2023年度货款");
        dataList.add(data4);
        
        // 生成第5条数据
        CreditorClaimData data5 = new CreditorClaimData();
        data5.setReceiptNumber("ZJ2025010005");
        data5.setCreditor("张华");
        data5.setDeclarationTime("2025-01-16");
        data5.setAddress("广州市天河区体育西路108号/510620");
        data5.setContactPhone("15920123456");
        data5.setDeclarationAmount("50,000.00");
        data5.setNature("个人");
        data5.setLegalRepresentative("-");
        data5.setAgent("-");
        data5.setAgentPhone("-");
        data5.setClaimNature("普通债权");
        data5.setClaimType("服务费");
        data5.setAccountName("张华");
        data5.setBankName("中国农业银行广州天河支行");
        data5.setAccountNumber("6228481234567890123");
        data5.setLitigation("否");
        data5.setRemarks("咨询服务费未支付");
        dataList.add(data5);
        
        // 生成第6条数据
        CreditorClaimData data6 = new CreditorClaimData();
        data6.setReceiptNumber("ZJ2025010006");
        data6.setCreditor("成都永盛食品厂");
        data6.setDeclarationTime("2025-01-18");
        data6.setAddress("成都市金牛区蜀汉路25号/610036");
        data6.setContactPhone("028-87654321");
        data6.setDeclarationAmount("1,520,000.00");
        data6.setNature("企业");
        data6.setLegalRepresentative("陈浩");
        data6.setAgent("孙悦");
        data6.setAgentPhone("13711223344");
        data6.setClaimNature("普通债权");
        data6.setClaimType("原料款");
        data6.setAccountName("成都永盛食品厂");
        data6.setBankName("中国工商银行成都金牛支行");
        data6.setAccountNumber("1001987654321098");
        data6.setLitigation("否");
        data6.setRemarks("2024年第二季度原料供应");
        dataList.add(data6);
        
        // 生成第7条数据
        CreditorClaimData data7 = new CreditorClaimData();
        data7.setReceiptNumber("ZJ2025010007");
        data7.setCreditor("王静");
        data7.setDeclarationTime("2025-01-20");
        data7.setAddress("杭州市西湖区文三路500号/310013");
        data7.setContactPhone("18857123456");
        data7.setDeclarationAmount("300,000.00");
        data7.setNature("个人");
        data7.setLegalRepresentative("-");
        data7.setAgent("周律师");
        data7.setAgentPhone("13900998877");
        data7.setClaimNature("普通债权");
        data7.setClaimType("借款");
        data7.setAccountName("王静");
        data7.setBankName("中国工商银行杭州西湖支行");
        data7.setAccountNumber("1001654321098765");
        data7.setLitigation("是");
        data7.setRemarks("已申请支付令");
        dataList.add(data7);
        
        // 生成第8条数据
        CreditorClaimData data8 = new CreditorClaimData();
        data8.setReceiptNumber("ZJ2025010008");
        data8.setCreditor("南京绿色家园房地产");
        data8.setDeclarationTime("2025-01-22");
        data8.setAddress("南京市鼓楼区中山北路200号/210008");
        data8.setContactPhone("025-83211234");
        data8.setDeclarationAmount("12,500,000.00");
        data8.setNature("企业");
        data8.setLegalRepresentative("周涛");
        data8.setAgent("吴昊");
        data8.setAgentPhone("13800112233");
        data8.setClaimNature("有财产担保债权");
        data8.setClaimType("借款");
        data8.setAccountName("南京绿色家园房地产");
        data8.setBankName("中国建设银行南京鼓楼支行");
        data8.setAccountNumber("6217009876543210987");
        data8.setLitigation("是");
        data8.setRemarks("土地及在建工程抵押");
        dataList.add(data8);
        
        // 生成第9条数据
        CreditorClaimData data9 = new CreditorClaimData();
        data9.setReceiptNumber("ZJ2025010009");
        data9.setCreditor("天津港务物流有限公司");
        data9.setDeclarationTime("2025-01-25");
        data9.setAddress("天津市滨海新区天津港保税区/300456");
        data9.setContactPhone("022-25788990");
        data9.setDeclarationAmount("680,000.00");
        data9.setNature("企业");
        data9.setLegalRepresentative("郑东");
        data9.setAgent("-");
        data9.setAgentPhone("-");
        data9.setClaimNature("普通债权");
        data9.setClaimType("物流服务费");
        data9.setAccountName("天津港务物流有限公司");
        data9.setBankName("中国银行天津滨海支行");
        data9.setAccountNumber("4563522233445555");
        data9.setLitigation("否");
        data9.setRemarks("仓储及运输费用");
        dataList.add(data9);
        
        // 生成第10条数据
        CreditorClaimData data10 = new CreditorClaimData();
        data10.setReceiptNumber("ZJ2025010010");
        data10.setCreditor("西安秦风文化传播");
        data10.setDeclarationTime("2025-01-28");
        data10.setAddress("西安市雁塔区小寨西路25号/710061");
        data10.setContactPhone("029-85201234");
        data10.setDeclarationAmount("450,000.00");
        data10.setNature("企业");
        data10.setLegalRepresentative("吴建军");
        data10.setAgent("林芳");
        data10.setAgentPhone("13544332211");
        data10.setClaimNature("普通债权");
        data10.setClaimType("广告费");
        data10.setAccountName("西安秦风文化传播");
        data10.setBankName("中国银行西安雁塔支行");
        data10.setAccountNumber("4563588776655444");
        data10.setLitigation("否");
        data10.setRemarks("品牌推广服务费");
        dataList.add(data10);
        
        return dataList;
    }
}
