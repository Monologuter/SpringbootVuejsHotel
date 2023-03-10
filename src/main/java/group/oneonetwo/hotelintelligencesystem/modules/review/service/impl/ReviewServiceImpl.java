package group.oneonetwo.hotelintelligencesystem.modules.review.service.impl;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import group.oneonetwo.hotelintelligencesystem.components.security.utils.AuthUtils;
import group.oneonetwo.hotelintelligencesystem.config.AlipayConfig;
import group.oneonetwo.hotelintelligencesystem.enums.AlipayEnums;
import group.oneonetwo.hotelintelligencesystem.enums.BalanceHandleMode;
import group.oneonetwo.hotelintelligencesystem.enums.OrderEnums;
import group.oneonetwo.hotelintelligencesystem.exception.CommonException;
import group.oneonetwo.hotelintelligencesystem.exception.SavaException;
import group.oneonetwo.hotelintelligencesystem.modules.isolationInfo.model.vo.IsolationInfoVO;
import group.oneonetwo.hotelintelligencesystem.modules.isolationInfo.service.IsolationInfoService;
import group.oneonetwo.hotelintelligencesystem.modules.order.model.po.OrderPO;
import group.oneonetwo.hotelintelligencesystem.modules.order.model.vo.OrderVO;
import group.oneonetwo.hotelintelligencesystem.modules.review.model.po.ReviewPO;
import group.oneonetwo.hotelintelligencesystem.modules.review.model.vo.ReviewVO;
import group.oneonetwo.hotelintelligencesystem.modules.review.service.ReviewService;
import group.oneonetwo.hotelintelligencesystem.modules.review.dao.ReviewMapper;
import group.oneonetwo.hotelintelligencesystem.modules.room.model.vo.RoomVO;
import group.oneonetwo.hotelintelligencesystem.modules.room.service.IRoomService;
import group.oneonetwo.hotelintelligencesystem.modules.room_type.model.po.RoomTypePO;
import group.oneonetwo.hotelintelligencesystem.modules.room_type.service.IRoomTypeServeice;
import group.oneonetwo.hotelintelligencesystem.modules.user.service.IUserService;
import group.oneonetwo.hotelintelligencesystem.modules.wallet.model.po.WalletPO;
import group.oneonetwo.hotelintelligencesystem.modules.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author ???
* @description ????????????review?????????????????????Service??????
* @createDate 2022-04-19 10:40:37
*/
@Service
public class ReviewServiceImpl implements ReviewService{

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    IRoomService roomService;

    @Autowired
    IRoomTypeServeice roomTypeServeice;

    @Autowired
    IsolationInfoService isolationInfoService;

    @Autowired
    ReviewService reviewService;


    @Autowired
    WalletService walletService;

    @Autowired
    ReviewMapper reviewMapper;

    @Autowired
    AuthUtils authUtils;

    @Autowired
    IUserService userService;

    @Autowired
    AlipayConfig alipayConfig;

    private static final Logger logger = LoggerFactory.getLogger(Object.class);

    @Override
    public ReviewPO selectOneById(String id) {
        ReviewPO reviewPO = reviewMapper.selectById(id);
        return reviewPO;
    }

    @Override
    public ReviewVO add(ReviewVO reviewVO) {
        if(reviewVO==null){
            throw new SavaException("??????????????????????????????:????????????");
        }
        ReviewPO reviewPO = new ReviewPO();
        BeanUtils.copyProperties(reviewVO,reviewPO);
        int insert=reviewMapper.insert(reviewPO);
        reviewVO.setId(reviewPO.getId());
        if(insert>0){
            return reviewVO;
        }
        throw new SavaException("????????????????????????");
    }

    @Override
    public ReviewVO selectOneByIdReturnVO(String id) {
        if(id==null){
            throw new CommonException(501,"????????????");
        }
        ReviewPO reviewPO = reviewMapper.selectById(id);
        ReviewVO reviewVO = new ReviewVO();
        if(reviewPO!=null){
            BeanUtils.copyProperties(reviewPO,reviewVO);
        }
        return reviewVO;
    }

    @Override
    public ReviewVO save(ReviewVO reviewVO) {
        if (reviewVO==null){
            throw new CommonException(501,"??????????????????????????????");
        }
        ReviewVO check = selectOneByIdReturnVO(reviewVO.getId());
        if(check==null){
            throw new CommonException(4004,"?????????id???:"+reviewVO.getId()+"?????????");
        }
        ReviewPO reviewPO = new ReviewPO();
        BeanUtils.copyProperties(reviewVO,reviewPO);
        int save=reviewMapper.updateById(reviewPO);
        if(save>0){
            return reviewVO;
        }
        throw new SavaException("????????????????????????");
    }

    @Override
    public Integer deleteById(String id) {
        ReviewVO check = selectOneByIdReturnVO(id);
        if(check==null){
            throw  new CommonException(4004,"?????????id???"+id+"?????????");
        }
        int i=reviewMapper.deleteById(id);
        return i;

    }

    @Override
    public Page<ReviewVO> getPage(ReviewVO reviewVO) {
        Page<ReviewPO> page=new Page<>(reviewVO.getPage().getPage(),reviewVO.getPage().getSize());
        return reviewMapper.getPage(page,reviewVO);

    }
    //id
    @Override
    public ReviewVO getCheck(ReviewVO reviewVO) {
        reviewVO.setUid(authUtils.getUid());
        ReviewVO add = null;
        if(reviewVO.getType()==0 || reviewVO.getType()==1){
            reviewVO.setPayStatus(2);
            add = add(reviewVO);
        }else {

            if(userService.selectOneById(reviewVO.getUid())==null){
                throw new CommonException(501,"???????????????");
            }
            RoomTypePO roomTypePO = roomTypeServeice.selectOneById(reviewVO.getRoomType());
            Integer isolationFee = roomTypePO.getIsolationFee();
            reviewVO.setPayStatus(0);
            reviewVO.setTotalFee((int)isolationFee*14);
            add = add(reviewVO);
        }
        return add;
    }

    /**
     * ??????????????????
     * @param id
     * @param walletPwd
     */
    @Override
    public void payDeclaration(String id, String walletPwd) {
        ReviewPO reviewPO = selectOneById(id);
        //????????????????????????????????????????????????
        if (reviewPO.getPayStatus() == 2 || reviewPO.getPayStatus() == 1) {
            return;
        }
        WalletPO walletPO = walletService.getWalletPOByUid(authUtils.getUid());
        boolean matches = bCryptPasswordEncoder.matches(walletPwd, walletPO.getPassword());
        if (matches) {
            walletService.editBalance(BalanceHandleMode.REDUCE.getCode(), Double.valueOf(reviewPO.getTotalFee()));

            //????????????????????????
            ReviewVO changeStatus = new ReviewVO();
            changeStatus.setPayStatus(1);
            save(changeStatus);
        }else {
            throw new CommonException("??????????????????");
        }

    }

    //    ?????????????????????????????????
    /**
     * ???????????????????????????
     * @param id
     */
    @Override
    public String payDeclarationForAlipay(String id) throws JSONException, AlipayApiException {
        ReviewPO reviewPO = selectOneById(id);
        //????????????????????????????????????????????????
        if (reviewPO.getPayStatus() == 2 || reviewPO.getPayStatus() == 1) {
            throw new CommonException("??????????????????????????????,???????????????????????????????????????");
        }
        //???????????????????????????
        AlipayClient alipayClient = alipayConfig.getAlipayClient();
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl("");
        request.setReturnUrl("");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", id);
        bizContent.put("total_amount", reviewPO.getTotalFee());
        bizContent.put("subject", "????????????-????????????");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.put("qr_pay_mode",1);
        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        logger.info("alipay-response:",response.getBody());
        if(response.isSuccess()){
            logger.info("prePayOrderForAlipay????????????");
            return response.getBody().toString();
        } else {
            logger.warn("prePayOrderForAlipay????????????");
        }
        return null;
    }

    /**
     * ???????????????????????????
     * @param id
     */
    @Override
    public Boolean checkDeclarationPayStatusForAlipay(String id) throws AlipayApiException, JSONException {
        ReviewPO reviewPO = selectOneById(id);
        //????????????????????????????????????????????????
        if (reviewPO.getPayStatus() == 2 || reviewPO.getPayStatus() == 1) {
            return true;
        }

        //????????????
        AlipayClient alipayClient = alipayConfig.getAlipayClient();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", id);//?????????
        //bizContent.put("trade_no", "2014112611001004680073956707");//?????????
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            logger.info("checkPayOrderForAlipay????????????");
            Gson gson = new Gson();
            Map<String,String> responseMap = (Map<String, String>) gson.fromJson(response.getBody(), new HashMap<String, String>().getClass()).get("alipay_trade_query_response");
            //????????????
            if (AlipayEnums.TRADE_SUCCESS.equals(responseMap.get("trade_status"))){
                //????????????????????????
                ReviewVO changeStatus = new ReviewVO();
                changeStatus.setPayStatus(1);
                changeStatus.setId(id);
                save(changeStatus);

                logger.info("????????????");
                return true;
            }
        }else {
            logger.warn("checkPayOrderForAlipay????????????");
        }
        throw new CommonException("????????????,?????????!");
    }

    //admin  id  review_status
    @Override
    public void getReviews(ReviewVO reviewVO) {
        if(reviewVO.getReviewStatus()==2){
            if(reviewVO.getRemark()==null || reviewVO.getRemark()=="") {
                throw new CommonException(501, "???????????????????????????");
            }
            //???????????????????????????????????????????????????
            ReviewPO reviewPO = selectOneById(reviewVO.getId());
            if(reviewPO.getType()==2 || reviewPO.getType()==3){
                RoomTypePO roomTypePO = roomTypeServeice.selectOneById(reviewPO.getRoomType());
                WalletPO walletPO = walletService.getWalletPOByUid(reviewPO.getUid());
                walletPO.setBalance(walletPO.getBalance()+roomTypePO.getIsolationFee());
                walletService.save(walletPO);
            }
            reviewPO.setRemark(reviewVO.getRemark());
            reviewPO.setReviewStatus(2);
            ReviewVO reviewVO1 = new ReviewVO();
            BeanUtils.copyProperties(reviewPO,reviewVO1);
            reviewService.save(reviewVO1);
            return;
        }

        ReviewPO reviewPO = selectOneById(reviewVO.getId());


        RoomVO roomVO = roomService.isolationCheckIn(reviewVO.getHotelId(), reviewVO.getRoomType(), null);
        IsolationInfoVO isolationInfoVO = new IsolationInfoVO();
        isolationInfoVO.setName(reviewPO.getName());
        isolationInfoVO.setuId(reviewPO.getUid());
        isolationInfoVO.setIdCard(reviewPO.getIdCard());
        isolationInfoVO.setType(reviewPO.getType());
        isolationInfoVO.setPhone(reviewPO.getPhone());
        isolationInfoVO.setEmail(reviewPO.getEmail());
        isolationInfoVO.setHotelId(reviewPO.getHotelId());
        isolationInfoVO.setRoomType(roomVO.getType());
        isolationInfoVO.setPay(reviewPO.getTotalFee());
        isolationInfoVO.setCheckInTime(reviewPO.getCheckInTime());
        isolationInfoVO.setCheckOutTime(reviewPO.getCheckOutTime());
        isolationInfoVO.setRoomId(roomVO.getId());
        isolationInfoVO.setRoomName(roomVO.getName());
        isolationInfoVO.setProvince(reviewPO.getProvince());
        isolationInfoVO.setCity(reviewPO.getCity());
        isolationInfoVO.setStatus(0);
        isolationInfoService.add(isolationInfoVO);
        ReviewVO reviewVO1 = new ReviewVO();
        reviewPO.setReviewStatus(1);
        BeanUtils.copyProperties(reviewPO,reviewVO1);
        reviewService.save(reviewVO1);

    }

    @Override
    public ReviewPO selectByUID(String id) {
        QueryWrapper<ReviewPO> wrapper = new QueryWrapper<>();
        wrapper.eq("u_id",id);
        List<ReviewPO> reviewPOS = reviewMapper.selectList(wrapper);
        return reviewPOS.get(0);
    }

    @Override
    public Page<ReviewVO> my(ReviewVO reviewVO) {
        String uid = authUtils.getUid();
        reviewVO.setUid(uid);
        Page<ReviewVO> page = this.getPage(reviewVO);
        return page;
    }


}
