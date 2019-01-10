<?xml version="1.0" encoding="gbk"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gateway="http://www.gdb.com.cn/GDBGateway">
	<soapenv:Header>
		<gateway:HeadType>
			<gateway:versionNo>1</gateway:versionNo>
			<gateway:toEncrypt>0</gateway:toEncrypt>
			<gateway:commCode>510001</gateway:commCode>
			<gateway:commType>0</gateway:commType>
			<gateway:receiverId>CAPS</gateway:receiverId>
			<gateway:senderId>AFAAABS</gateway:senderId>
			<gateway:senderSN>${senderSN}</gateway:senderSN>
			<gateway:senderDate>${senderDate}</gateway:senderDate>
			<gateway:senderTime>${senderTime}</gateway:senderTime>
			<gateway:tradeCode>CAPS02</gateway:tradeCode>
			<gateway:gwErrorCode>01</gateway:gwErrorCode>
			<gateway:gwErrorMessage/>
		</gateway:HeadType>
	</soapenv:Header>
	<soapenv:Body>
		<gateway:NoAS400>
			<gateway:field name="bankNo"></gateway:field>
			<gateway:field name="idNBR">${idNBR}</gateway:field>
			<gateway:field name="custName"></gateway:field>
			<gateway:field name="barCode"></gateway:field>
		</gateway:NoAS400>
	</soapenv:Body>
</soapenv:Envelope>