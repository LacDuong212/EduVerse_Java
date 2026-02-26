package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.dto.response.CourseEarningDataResponse;
import com.eduverse.eduversebe.dto.response.MonthlyDataItemResponse;
import com.eduverse.eduversebe.repository.OrderRepository;
import com.eduverse.eduversebe.repository.projection.MonthlyEarningProjection;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final OrderRepository orderRepository;

    private List<MonthlyDataItemResponse> fillMissingMonthsHelper(
            List<MonthlyEarningProjection> raw,
            Instant start,
            Instant end
    ) {
        Map<YearMonth, Double> dataMap = raw.stream()
                .collect(Collectors.toMap(
                        p -> YearMonth.of(p.getYear(), p.getMonth()),
                        MonthlyEarningProjection::getTotalEarning
                ));

        YearMonth startMonth = YearMonth.from(
                start.atZone(ZoneOffset.UTC)
        );
        YearMonth endMonth = YearMonth.from(
                end.atZone(ZoneOffset.UTC)
        );

        List<MonthlyDataItemResponse> result = new ArrayList<>();
        YearMonth cursor = startMonth;

        while (!cursor.isAfter(endMonth)) {
            result.add(MonthlyDataItemResponse.builder()
                    .period(cursor)
                    .value(dataMap.getOrDefault(cursor, 0.0))
                    .build());
            cursor = cursor.plusMonths(1);
        }

        return result;
    }

    public List<MonthlyDataItemResponse> getCoursesMonthlyEarningPast12Months(List<String> courseIds) {
        Instant start = YearMonth
                .now()
                .minusMonths(11)
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        Instant end = Instant.now();

        return fillMissingMonthsHelper(
                orderRepository.getCoursesMonthlyEarningFromRange(courseIds, start, end),
                start,
                end
        );
    }

    public List<CourseEarningDataResponse> getTop5EarningCoursesThisMonth(List<String> courseIds) {
        List<ObjectId> cIds = courseIds.stream()
                .map(ObjectId::new)
                .toList();
        int limit = Math.min(5, cIds.size());

        YearMonth now = YearMonth.now(ZoneOffset.UTC);
        Instant startOfMonth = now
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();
        Instant startOfNextMonth = now
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        return orderRepository.getTopEarningCoursesThisMonth(cIds, startOfMonth, startOfNextMonth, limit)
                .stream()
                .map(c -> CourseEarningDataResponse.builder()
                        .id(c.getCourseId())
                        .title(c.getTitle())
                        .totalEarning(c.getTotalEarning())
                        .totalSales(c.getTotalSales())
                        .build())
                .toList();
    }
}
