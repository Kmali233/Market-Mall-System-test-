package com.hmall.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hmall.api.client.CartClient;
import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.CartClearMessageDTO;
import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.OrderDetailDTO;

import com.hmall.common.exception.BadRequestException;
import com.hmall.common.utils.UserContext;

import com.hmall.trade.constants.MQConstants;
import com.hmall.trade.domain.dto.OrderFormDTO;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.domain.po.OrderDetail;
import com.hmall.trade.mapper.OrderMapper;
import com.hmall.trade.service.IOrderDetailService;
import com.hmall.trade.service.IOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xxx
 * @since 2023-05-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {


    private final IOrderDetailService detailService;
    private final ItemClient itemClient;
    private final CartClient cartClient;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @GlobalTransactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        // 1.订单数据
        Order order = new Order();
        // 1.1.查询商品
        List<OrderDetailDTO> detailDTOS = orderFormDTO.getDetails();
        // 1.2.获取商品id和数量的Map
        Map<Long, Integer> itemNumMap = detailDTOS.stream()
                .collect(Collectors.toMap(OrderDetailDTO::getItemId, OrderDetailDTO::getNum));
        Set<Long> itemIds = itemNumMap.keySet();
        // 1.3.查询商品
        // 服务远程调用RPC
        List<ItemDTO> items = itemClient.queryItemByIds(itemIds);
        if (items == null || items.size() < itemIds.size()) {
            throw new BadRequestException("商品不存在");
        }

        // 1.4.基于商品价格、购买数量计算商品总价：totalFee
        int total = 0;
        for (ItemDTO item : items) {
            total += item.getPrice() * itemNumMap.get(item.getId());
        }
        order.setTotalFee(total);
        // 1.5.其它属性
        order.setPaymentType(orderFormDTO.getPaymentType());
        order.setUserId(UserContext.getUser());
        order.setStatus(1);
        // 1.6.将Order写入数据库order表中
        save(order);

        // 2.保存订单详情
        List<OrderDetail> details = buildDetails(order.getId(), items, itemNumMap);
        detailService.saveBatch(details);

        // 3.清理购物车商品 - 改为MQ异步通知
        try {
            CartClearMessageDTO msg = new CartClearMessageDTO()
                    .setItemIds(itemIds);
            rabbitTemplate.convertAndSend("trade.topic", "order.create", msg);
        } catch (Exception e) {
            log.error("清理购物车消息发送失败，订单id：{}", order.getId(), e);
        }

        // 4.发送延迟消息，检测订单支付状态
        rabbitTemplate.convertAndSend(
                MQConstants.DELAY_EXCHANGE_NAME,
                MQConstants.DELAY_ORDER_KEY,
                order.getId(),
                message -> {
                    message.getMessageProperties().setDelay(10000);
                    return message;
                }
        );

        // 5.扣减库存
        try {
            itemClient.deductStock(detailDTOS);
        } catch (Exception e) {
            throw new RuntimeException("库存不足！");
        }
        return order.getId();
    }

    @Override
    public void markOrderPaySuccess(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(2);
        order.setPayTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @GlobalTransactional
    public void cancelOrder(Long orderId) {
        // 1、查询订单
        Order order = getById(orderId);
        if(order == null){
            throw new BadRequestException("订单不存在");
        }

        // 2、等幂性判断：如果订单已经关闭，则直接返回
        if(order.getStatus() == 5){
            return;
        }

        // 3、验证订单状态：只有未支付的订单才可以取消
        if(order.getStatus() != 1){
            throw new BadRequestException("订单状态不正确，无法取消");
        }

        // 4、更新订单状态为已关闭
        Order updateOrder = new Order();
        updateOrder.setId(orderId);
        updateOrder.setStatus(5);
        updateOrder.setCloseTime(LocalDateTime.now());
        updateById(updateOrder);

        // 5、查询订单详细，恢复库存
        List<OrderDetail> details = detailService.lambdaQuery()
                .eq(OrderDetail::getOrderId, orderId)
                .list();

        // 6、调用商品服务恢复库存
        List<OrderDetailDTO> detailDTOS = details.stream()
                .map(detail -> {
                    OrderDetailDTO dto = new OrderDetailDTO();
                    dto.setItemId(detail.getItemId());
                    dto.setNum(detail.getNum());
                    return dto;
                })
                .collect(Collectors.toList());

        // 加异常处理
        try {
            itemClient.restoreStock(detailDTOS);
        } catch (Exception e) {
            log.error("恢复库存失败，订单id：{}", orderId, e);
            throw new RuntimeException("取消订单失败");
        }

        itemClient.restoreStock(detailDTOS);
    }


    private List<OrderDetail> buildDetails(Long orderId, List<ItemDTO> items, Map<Long, Integer> numMap) {
        List<OrderDetail> details = new ArrayList<>(items.size());
        for (ItemDTO item : items) {
            OrderDetail detail = new OrderDetail();
            detail.setName(item.getName());
            detail.setSpec(item.getSpec());
            detail.setPrice(item.getPrice());
            detail.setNum(numMap.get(item.getId()));
            detail.setItemId(item.getId());
            detail.setImage(item.getImage());
            detail.setOrderId(orderId);
            details.add(detail);
        }
        return details;
    }




}
