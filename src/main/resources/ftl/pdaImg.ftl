<?xml version="1.0" encoding="GBK"?>
<COSP>
	<HEAD>
		<DATASTYLE>扫描类型</ DATASTYLE>
		<MAILCODE>邮包号</MAILCODE>
		<BATCHCODE>批次号</BATCHCODE>
		<BATCHNUM>批次份数</BATCHNUM>
		<BANKCODE>行所号</BANKCODE>
		<IMGSTATUS>影像组状态</IMGSTATUS>
		<GROUPTIME>分组时间</GROUPTIME>		
		<BARCODEDATE>条形码打印日期</BARCODEDATE>
		<SCANOPERID>扫描操作员ID</SCANOPERID>
		<CORPID>公司ID</CORPID>
		<CHANNELID>渠道ID</CHANNELID>
	</HEAD>
	<BODY>
		<OPERATION   BARCODE =条形码  TIGI_DOCTYPE=资料类型  AIDI_P_BARCODE =补件原件条码号 TABLECODE=表单代码 FORMCODE_X=原点定位横坐标 FORMCODE_Y=原点定位纵坐标 YUYIN_BARCODE=预印条码号>
		<IMG  NUMBER=序号 NAME=名称 DIRECT =正反面 IMGSIZE=影像大小/ >
		<IMG  NUMBER=序号 NAME=名称 DIRECT =正反面 IMGSIZE=影像大小/ >
		<IMG  NUMBER=序号 NAME=名称 DIRECT =正反面 IMGSIZE=影像大小/ >
		</OPERATION >
	</BODY>
</COSP>