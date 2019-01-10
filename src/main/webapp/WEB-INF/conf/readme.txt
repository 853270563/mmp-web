ibanking.cer	公钥
ibanking.jks	私钥

生成步骤：
Step1:
------------------------------
C:\Program Files\Java\jdk1.6.0_35\bin>keytool -genkey -alias ibs -keyalg RSA -ke
ystore e:/temp/ibanking.jks -storepass pncjks -keypass ibanking
您的名字与姓氏是什么？
  [Unknown]：  shang hai yi tong
您的组织单位名称是什么？
  [Unknown]：  yitong
您的组织名称是什么？
  [Unknown]：  yitong
您所在的城市或区域名称是什么？
  [Unknown]：  shanghai
您所在的州或省份名称是什么？
  [Unknown]：  shanghai
该单位的两字母国家代码是什么
  [Unknown]：  cn
CN=shang hai yi tong, OU=yitong, O=yitong, L=shanghai, ST=shanghai, C=cn 正确吗
？
  [否]：  y
  
Step2:
------------------------------
C:\Program Files\Java\jdk1.6.0_35\bin>keytool -export -alias ibs -file ibanking.
cer -keystore e:/temp/ibanking.jks -storepass pncjks
保存在文件中的认证 <ibanking.cer> 

