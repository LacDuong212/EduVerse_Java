package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.globalEnums.OrderStatus;
import com.eduverse.eduversebe.model.DraftVideo;
import com.eduverse.eduversebe.model.Order;
import com.eduverse.eduversebe.repository.DraftVideoRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CronService {

    private final MongoTemplate mongoTemplate;
    private final DraftVideoRepository draftVideoRepository;
    private final S3VideoService s3VideoService;

    @Scheduled(cron = "0 */10 * * * *")
    public void expireOrders() {
        Instant now = Instant.now();
        log.info("> Starting cron job: Expire orders at {}", now);

        Query query = new Query(
                Criteria.where("status").is(OrderStatus.pending)
                        .and("expiresAt").lt(now)
        );

        Update update = new Update().set("status", OrderStatus.cancelled);

        UpdateResult result = mongoTemplate.updateMulti(query, update, Order.class);

        if (result.getModifiedCount() > 0) {
            log.info("> Updated {} orders to CANCELLED status.", result.getModifiedCount());
        }
    }

    @Scheduled(cron = "0 15 * * * *")
    public void hourlyCleanup() {
        Instant now = Instant.now();
        log.info("> Starting cron job: Draft videos cleanup at {}", now);

        // Fetch expired records (using a limit for safety)
        List<DraftVideo> expiredVideos = draftVideoRepository.findTop500ByExpireAtBefore(now);

        if (expiredVideos.isEmpty()) {
            return;
        }

        List<String> keys = expiredVideos.stream()
                .map(DraftVideo::getKey)
                .toList();

        try {
            s3VideoService.deleteVideosByKeys(keys);

            draftVideoRepository.deleteAll(expiredVideos);

            log.info("> Successfully removed {} expired drafts from S3 and DB.", expiredVideos.size());
        } catch (Exception e) {
            log.error("> Error during draft video clean up: {}", e.getMessage());
        }
    }
}
