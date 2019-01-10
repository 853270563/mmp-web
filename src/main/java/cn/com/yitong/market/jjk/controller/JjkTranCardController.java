package cn.com.yitong.market.jjk.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.core.base.Page;
import cn.com.yitong.core.base.WebUtils;
import cn.com.yitong.core.base.dao.Criteria;
import cn.com.yitong.core.base.dao.CriteriaExample;
import cn.com.yitong.core.base.web.BaseController;
import cn.com.yitong.market.jjk.model.JjkTranCard;
import cn.com.yitong.market.jjk.model.MggImageAtta;
import cn.com.yitong.market.jjk.service.CustomFileMngService;
import cn.com.yitong.market.jjk.service.JjkTranCardService;
import cn.com.yitong.market.jjk.service.MggImageAttaService;
import cn.com.yitong.market.jjk.vo.JjkTranCardSearchVo;
import cn.com.yitong.util.ConfigEnum;
import cn.com.yitong.util.CustomFileType;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.StringUtil;

/**
 * 
 * @author lc3@yitong.com.cn
 * 
 */
@Controller
public class JjkTranCardController extends BaseController {

	@Resource
	private JjkTranCardService jjkTranCardService;
	@Resource
	private CustomFileMngService customFileMngService;
	@Resource
	private MggImageAttaService mggImageAttaService;

	@RequestMapping("/jjk/debit/cardInfoAndPhotoAdd")
	@ResponseBody
	public Map<String, Object> cardInfoAndPhotoAdd(JjkTranCard cardInfo,
			MultipartHttpServletRequest request) throws SQLException,
			FileNotFoundException, IOException {
		Map<String, Object> rtn = new HashMap<String, Object>();
		cardInfo.setSignState(ConfigEnum.DICT_TRAN_DECARD_SIGN_STATE_SUBMIT
				.strVal());
		jjkTranCardService.insert(cardInfo);
		Map<String, MultipartFile> fileMap = request.getFileMap();
		Date date = new Date();
		for (Entry<String, MultipartFile> entry : fileMap.entrySet()) {
			MultipartFile file = entry.getValue();
			Collection<String> tmp = customFileMngService.saveMultipartFile(
					file, CustomFileType.IMG).values();
			for (String fileName : tmp) {
				MggImageAtta atta = new MggImageAtta();
				atta.setTransId(cardInfo.getTransId());
				atta.setTranCode(ConfigEnum.DICT_TRAN_CODE_OPEN_CARD.strVal());
				atta.setAttaDirUrl(fileName);
				atta.setAttaName(entry.getKey());
				atta.setAttaSize(new BigDecimal(file.getSize()));
				atta.setAttaType(ConfigEnum.DICT_ATTA_TYPE_IMAGE.strVal());
				atta.setAttaUpdaTime(date);
				mggImageAttaService.insert(atta);
			}
		}
		return WebUtils.returnSuccessMsg(rtn, null);
	}

	@RequestMapping("/jjk/debit/cardInfoAdd")
	@ResponseBody
	public Map<String, Object> cardInfoAdd(JjkTranCard cardInfo,
			HttpServletRequest req, HttpServletResponse rsp)
			throws SQLException, FileNotFoundException, IOException {
		Map<String, Object> rtn = new HashMap<String, Object>();
		cardInfo.setSignState(ConfigEnum.DICT_TRAN_DECARD_SIGN_STATE_SUBMIT
				.strVal());
		jjkTranCardService.insert(cardInfo);
		rtn.put("transId", cardInfo.getTransId());
		return WebUtils.returnSuccessMsg(rtn, null);
	}

	@RequestMapping(value = "/jjk/debit/attaUpload", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> attaUpload(String transId,
			MultipartHttpServletRequest request) throws FileNotFoundException,
			IOException {
		Assert.notNull(transId, "transId不能为空");
		Assert.notNull(jjkTranCardService.findByPrimaryKey(transId), "找不到对应的开卡记录");
		Map<String, Object> rtn = new HashMap<String, Object>();

		Map<String, MultipartFile> fileMap = request.getFileMap();
		Date date = new Date();
		for (Entry<String, MultipartFile> entry : fileMap.entrySet()) {
			MultipartFile file = entry.getValue();
			Collection<String> tmp = customFileMngService.saveMultipartFile(
					file, CustomFileType.IMG).values();
			for (String fileName : tmp) {
				MggImageAtta atta = new MggImageAtta();
				atta.setTransId(transId);
				atta.setTranCode(ConfigEnum.DICT_TRAN_CODE_OPEN_CARD.strVal());
				atta.setAttaDirUrl(fileName);
				atta.setAttaName(entry.getKey());
				atta.setAttaSize(new BigDecimal(file.getSize()));
				atta.setAttaType(ConfigEnum.DICT_ATTA_TYPE_IMAGE.strVal());
				atta.setAttaUpdaTime(date);
				mggImageAttaService.insert(atta);
			}
		}
		return WebUtils.returnSuccessMsg(rtn, null);
	}

	@RequestMapping("/jjk/debit/selfBusiQuery")
	@ResponseBody
	public List<JjkTranCard> selfBusiQuery(HttpServletRequest request) {
		HttpSession  session = request.getSession() ;
		String login_id = (String)session.getAttribute(SessConsts.LOGIN_ID);
		Date startDate = StringUtil.isBlank(request.getParameter("startDate")) ? new Date() : DateUtil.parseDate(request.getParameter("startDate"), "yyyy/MM/dd");
		Date endDate = StringUtil.isBlank(request.getParameter("endDate")) ? new Date() : DateUtil.parseDate(request.getParameter("endDate"), "yyyy/MM/dd");
		endDate = new Date(endDate.getTime()+24*60*60*1000);
		login_id = StringUtil.isBlank(login_id) ? request.getParameter("LOGIN_ID") : login_id ;
		CriteriaExample<JjkTranCard> example = new CriteriaExample<JjkTranCard>();
		Criteria crit = example.createCriteria();
		crit.between(
				JjkTranCard.FL.signTime,
				startDate,endDate);
		
		crit.equalTo(JjkTranCard.FL.signUser, login_id) ;
		return jjkTranCardService.queryByExampleExt(example);
	}

	
	
	@RequestMapping("/jjk/debit/busiListQuery")
	@ResponseBody
	public Page<JjkTranCard> busiListQuery(JjkTranCardSearchVo searchVo,
			Page<JjkTranCard> page) {
		CriteriaExample<JjkTranCard> example = new CriteriaExample<JjkTranCard>();
		Criteria crit = example.createCriteria();
		if (null != searchVo.getSignStartTime()) {
			crit.between(
					JjkTranCard.FL.signTime,
					searchVo.getSignStartTime(),
					null == searchVo.getSignEndTime() ? new Date() : searchVo
							.getSignEndTime());
		}
		if (null != searchVo.getSignState()) {
			crit.equalTo(JjkTranCard.FL.signState, searchVo.getSignState());
		}
		if (null != searchVo.getSignUser()) {
			crit.equalTo(JjkTranCard.FL.signUser, searchVo.getSignUser());
		}
		return jjkTranCardService.queryPageByExample(example, page);
	}

	@RequestMapping("/jjk/debit/busiDetailQuery")
	@ResponseBody
	public Map<String, Object> busiDetailQuery(String transId) {
		Assert.hasText(transId, "transId 不能为空");
		JjkTranCard card = jjkTranCardService.findByPrimaryKey(transId);
		Assert.notNull(card, "数据库查不到对应记录");

		Map<String, Object> rtn = new HashMap<String, Object>();
		rtn.put("entity", card);

		CriteriaExample<MggImageAtta> example = new CriteriaExample<MggImageAtta>();
		Criteria crit = example.createCriteria();
		crit.equalTo(MggImageAtta.FL.transId, card.getTransId());
		crit.equalTo(MggImageAtta.FL.tranCode,
				ConfigEnum.DICT_TRAN_CODE_OPEN_CARD.strVal());
		List<MggImageAtta> attrList = mggImageAttaService.queryByExample(example);
		if (null != attrList) {
			for (MggImageAtta atta : attrList) {
				atta.setAttaUrl(CustomFileMngController.IMG_PARENT_URL
						+ atta.getAttaDirUrl());
			}
		}
		rtn.put("attrList", attrList);
		return WebUtils.returnSuccessMsg(rtn, null);
	}
	
	@ExceptionHandler({Exception.class})
	public void exceptionHandler(HttpServletRequest request, HttpServletResponse response,
			Exception e) {
		WebUtils.jsonExceptionHandler(response, e);
	}

}
