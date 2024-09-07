package todo.api.todo.repository;

import static org.springframework.util.StringUtils.hasText;
import static todo.api.todo.entity.QTodos.todos;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import todo.api.todo.entity.QTodos;
import todo.api.todo.entity.Todos;
import todo.api.todo.entity.enums.TodosStatus;
import todo.api.todo.entity.request.TodoListReq;
import todo.api.todo.entity.response.QTodoRes;
import todo.api.todo.entity.response.TodoRes;

@Log4j2
@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TodoRes> findTodoList(Long userId, Pageable pageable, TodoListReq req) {

        List<TodoRes> list = queryFactory
                .select(new QTodoRes(
                        todos.id,
                        todos.title,
                        todos.description,
                        todos.status,
                        todos.priority,
                        todos.createdAt
                ))
                .from(todos)
                .where(
                        todos.user.id.eq(userId),
                        search(req.getTypes(), req.keyword()),
                        statusEq(req.status())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders(todos, pageable.getSort()))
                .fetch();

        JPAQuery<Todos> countQuery = queryFactory
                .select(todos)
                .where(
                        todos.user.id.eq(userId),
                        search(req.getTypes(), req.keyword()),
                        statusEq(req.status())
                )
                .from(todos);

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchCount);
    }


    private OrderSpecifier[] orders(QTodos todos, Sort sort) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        // Sort 정보에서 각 정렬 조건을 추출하여 OrderSpecifier로 변환
        for (Sort.Order order : sort) {
            PathBuilder entityPath = new PathBuilder<>(todos.getType(), todos.getMetadata());
            OrderSpecifier orderSpecifier = new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    entityPath.get(order.getProperty())
            );
            orderSpecifiers.add(orderSpecifier);
        }

        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(todos.id.desc());
        }

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    private BooleanBuilder search(String[] types, String keyword) {
        if (types == null || !hasText(keyword)) {
            return null;
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        for (String type : types) {

            switch (type) {
                case "title":
                    booleanBuilder.or(todos.title.contains(keyword));
                    break;
                case "description":
                    booleanBuilder.or(todos.description.contains(keyword));
                    break;
            }
        }

        return booleanBuilder;
    }

    private BooleanExpression statusEq(TodosStatus status) {
        return status != null ? todos.status.eq(status) : null;
    }

}
