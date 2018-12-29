<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:gateway="http://www.agree.com.cn/GDBGateway">
    <soapenv:Header>
        <gateway:HeadType>
            <gateway:versionNo>${versionNo}</gateway:versionNo>
            <gateway:receiverId>${receiverId}</gateway:receiverId>
            <gateway:senderId>${senderId}</gateway:senderId>
            <gateway:senderSN>${senderSN}</gateway:senderSN>
            <gateway:senderDate>${senderDate}</gateway:senderDate>
            <gateway:senderTime>${senderTime}</gateway:senderTime>
            <gateway:tradeCode>${tradeCode}</gateway:tradeCode>
        </gateway:HeadType>
    </soapenv:Header>
    <soapenv:Body>
        <gateway:NoAS400>
            <gateway:field name="userCode">${userCode}</gateway:field>
        </gateway:NoAS400>
    </soapenv:Body>
</soapenv:Envelope>
