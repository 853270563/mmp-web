<?xml version="1.0" encoding="GBK"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:gateway="http://www.gdb.com.cn/GDBGateway">
	<soapenv:Header>
		<gateway:HeadType>
			<gateway:versionNo>1</gateway:versionNo>
			<gateway:toEncrypt>0</gateway:toEncrypt>
			<gateway:commCode>500001</gateway:commCode>
			<gateway:commType>0</gateway:commType>
			<gateway:receiverId>GFJ3</gateway:receiverId>
			<gateway:senderId>AFAAABS</gateway:senderId>
			<gateway:senderSN>${senderSN}</gateway:senderSN>
			<gateway:senderDate>${senderDate}</gateway:senderDate>
			<gateway:senderTime>${senderTime}</gateway:senderTime>
			<gateway:tradeCode>GFJJ02</gateway:tradeCode>
			<gateway:gwErrorCode>01</gateway:gwErrorCode >
			<gateway:gwErrorMessage></gateway:gwErrorMessage >
		</gateway:HeadType>
	</soapenv:Header>
	<soapenv:Body>
		<gateway:NoAS400>
			<GFJJ>
				<HEAD>
					<BATCHID>${BATCHID}</BATCHID>
					<CHANNELID>05</CHANNELID>
					<SETIME>${SETIME}</SETIME>
					<BATCH1></BATCH1>
					<BATCH2></BATCH2>
					<BATCH3></BATCH3>
					<BATCH4></BATCH4>
					<BATCH5></BATCH5>
				</HEAD>
				<BODY>
					<BARCODE ID="${ID}" BANKID="${BANKID}" DATATYPE="${DATATYPE}" TABCODE="${TABCODE}" REMARK1="" REMARK2="" REMARK3="">
						<DATA>
							<DID>${DID}</DID>
							<TABLECODE>${TABLECODE}</TABLECODE>
							<REMARK4></REMARK4>
							<REMARK5></REMARK5>
							<REMARK6></REMARK6>
							<VALUE>${VALUE}</VALUE>
						</DATA>						
						</BARCODE>
					</BODY>
				</GFJJ>
			</gateway:NoAS400>
		</soapenv:Body>
	</soapenv:Envelope>
