/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.rocketmq.client.consumer.rebalance;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.alibaba.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import com.alibaba.rocketmq.client.log.ClientLogger;
import com.alibaba.rocketmq.common.message.MessageQueue;


/**
 * Average Hashing queue algorithm
 *
 * @author fuchong<yubao.fyb@alibaba-inc.com>
 * @author manhong.yqd<manhong.yqd@taobao.com>
 * @since 2013-7-24
 */
public class AllocateMessageQueueAveragely implements AllocateMessageQueueStrategy {
    private final Logger log = ClientLogger.getLog();


    @Override
    public String getName() {
        return "AVG";
    }

    //一个队列只能被一个消费者消费，但一个消费者可能消费多个队列。所以，当消息队列少于消费者时，多余的消费者有可能饿死。cidAll:消费组下所有 消费者id
    @Override
    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll,
                                       List<String> cidAll) {
        if (currentCID == null || currentCID.length() < 1) {
            throw new IllegalArgumentException("currentCID is empty");
        }
        if (mqAll == null || mqAll.isEmpty()) {
            throw new IllegalArgumentException("mqAll is null or mqAll empty");
        }
        if (cidAll == null || cidAll.isEmpty()) {
            throw new IllegalArgumentException("cidAll is null or cidAll empty");
        }

        List<MessageQueue> result = new ArrayList<MessageQueue>();
        if (!cidAll.contains(currentCID)) {
            log.info("[BUG] ConsumerGroup: {} The consumerId: {} not in cidAll: {}", //
                    consumerGroup, //
                    currentCID,//
                    cidAll);
            return result;
        }

        int index = cidAll.indexOf(currentCID);//当前消费组的索引位置
        int mod = mqAll.size() % cidAll.size();
        int averageSize =//averageSize消费者可消费的队列队列数少于或等于消费者数，则averageSize为1；
                mqAll.size() <= cidAll.size() ? 1 : (mod > 0 && index < mod ? mqAll.size() / cidAll.size()
                        + 1 : mqAll.size() / cidAll.size());
        int startIndex = (mod > 0 && index < mod) ? index * averageSize : index * averageSize + mod;
        int range = Math.min(averageSize, mqAll.size() - startIndex);
        for (int i = 0; i < range; i++) {
            result.add(mqAll.get((startIndex + i) % mqAll.size()));
        }
        return result;
    }
    /****
     * // 基本原则，每个队列只能被一个consumer消费
    // 当messageQueue个数小于等于consume的时候，排在前面（在list中的顺序）的consumer消费一个queue，index大于messageQueue之后的consumer消费不到queue，也就是为0
    // 当messageQueue个数大于consumer的时候，分两种情况
    //     当有余数（mod > 0）并且index < mod的时候，当前comsumer可以消费的队列个数是 mqAll.size() / cidAll.size() + 1
    //     可以整除或者index 大于余数的时候，队列数为：mqAll.size() / cidAll.size()
     */
}
