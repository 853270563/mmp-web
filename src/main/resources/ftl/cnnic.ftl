<?xml version="1.0" encoding="GBK"?>
<soapenv:Envelope
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:gateway="http://www.agree.com.cn/GDBGateway">
	<soapenv:Header>
		<gateway:HeadType>
			<gateway:versionNo>1</gateway:versionNo>
			<gateway:toEncrypt>0</gateway:toEncrypt>
			<gateway:commCode>500001</gateway:commCode>
			<gateway:commType>0</gateway:commType>
			<gateway:receiverId>CISP</gateway:receiverId>
			<gateway:senderId>TIMS</gateway:senderId>
			<gateway:senderSN>${senderSN}</gateway:senderSN>
			<gateway:senderDate>${senderDate}</gateway:senderDate>
			<gateway:senderTime>${senderTime}</gateway:senderTime>
			<gateway:tradeCode>CISP01</gateway:tradeCode>
			<gateway:gwErrorCode>01</gateway:gwErrorCode>
			<gateway:gwErrorMessage></gateway:gwErrorMessage>
		</gateway:HeadType>
	</soapenv:Header>
	<soapenv:Body>
		<gateway:NoAS400>
			<gateway:field name="templateCodeName">GFCISP</gateway:field>
			<gateway:field name="G_TRANSCODE">CISP01</gateway:field>
			<gateway:field name="orSenderID">TIMS</gateway:field>
			<gateway:field name="orSenderChannel">006</gateway:field>
			<gateway:field name="orSenderSN">${orSenderSN}</gateway:field>
			<gateway:field name="orSenderDate">${orSenderDate}</gateway:field>
			<gateway:field name="isForceCheck">0</gateway:field>
			<gateway:field name="IsNeedPhoto">1</gateway:field>
			<gateway:field name="citizenName">${citizenName}</gateway:field>
			<gateway:field name="citizenID">${citizenID}</gateway:field>
			<gateway:field name="businessType">test</gateway:field>
			<gateway:field name="BusinessZone">test11</gateway:field>
			<gateway:field name="checkReason">1</gateway:field>
			<gateway:field name="checkComment"></gateway:field>
		</gateway:NoAS400>
	</soapenv:Body>
</soapenv:Envelope>