package todo.api.notifications.repository;

import static todo.api.notifications.entity.QNotifications.notifications;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import todo.api.notifications.entity.Notifications;
import todo.api.notifications.entity.QNotifications;
import todo.api.notifications.entity.request.NotificationListReq;
import todo.api.notifications.entity.response.NotificationListRes;
import todo.api.notifications.entity.response.QNotificationListRes;

@Log4j2
@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NotificationListRes> findNotificationList(Long userId, Pageable pageable,
            NotificationListReq req) {

        List<NotificationListRes> list = queryFactory
                .select(new QNotificationListRes(
                        notifications.id,
                        notifications.dueDate,
                        notifications.todo.title
                ))
                .from(notifications)
                .where(
                        notifications.user.id.eq(userId),
                        titleContains(req.keyword()),
                        dueDateRange(req.from(), req.to())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders(notifications, pageable.getSort()))
                .fetch();

        JPAQuery<Notifications> countQuery = queryFactory
                .select(notifications)
                .where(
                        notifications.user.id.eq(userId),
                        titleContains(req.keyword()),
                        dueDateRange(req.from(), req.to())
                )
                .from(notifications);

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchCount);
    }


    private OrderSpecifier[] orders(QNotifications notifications, Sort sort) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        // Sort 정보에서 각 정렬 조건을 추출하여 OrderSpecifier로 변환
        for (Sort.Order order : sort) {
            PathBuilder entityPath = new PathBuilder<>(notifications.getType(),
                    notifications.getMetadata());
            OrderSpecifier orderSpecifier = new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    entityPath.get(order.getProperty())
            );
            orderSpecifiers.add(orderSpecifier);
        }

        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(notifications.dueDate.asc());
        }

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression dueDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) {
            return null;
        }
        return notifications.dueDate.between(from, to);
    }

    private BooleanExpression titleContains(String keyword) {
        return keyword != null ? notifications.todo.title.contains(keyword) : null;
    }
}
