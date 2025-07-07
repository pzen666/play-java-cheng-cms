package service;

import entity.FilterDTO;
import entity.WhereDTO;
import io.ebean.ExpressionList;

import java.lang.reflect.Field;

public class DynamicQueryService {

    /**
     * 根据传入的 JSON 构建动态查询条件
     * @param queryObject  查询对象
     * @param whereDTO     条件对象
     * @param entityClass  实体类Class
     * @return             返回最终查询对象
     * @param <T>
     */
    public static <T> ExpressionList<T> buildDynamicQuery(ExpressionList<T> queryObject, WhereDTO whereDTO, Class<T> entityClass) {
        ExpressionList<T> query = queryObject.where();
        if (whereDTO != null && !whereDTO.findValue.isEmpty()) {
            for (FilterDTO f : whereDTO.findValue) {
                String keyword = f.filterKeyword.trim();
                String rawValue = f.filter;
                String matchType = f.condition == null || f.condition.trim().isEmpty() ? "=" : f.condition.trim();
                if (keyword.isEmpty() || rawValue.isEmpty()) {
                    continue;
                }
                try {
                    // 获取字段类型
                    Field field = entityClass.getDeclaredField(keyword);
                    field.setAccessible(true);
                    // 根据字段类型转换值
                    Object value = convertValue(field.getType(), rawValue);
                    switch (matchType.toLowerCase()) {
                        case "=":
                            if ("and".equalsIgnoreCase(whereDTO.condition)) {
                                query.and().eq(keyword, value);
                            } else {
                                query.or().eq(keyword, value);
                            }
                            break;
                        case ">":
                            if ("and".equalsIgnoreCase(whereDTO.condition)) {
                                query.and().gt(keyword, value);
                            } else {
                                query.or().gt(keyword, value);
                            }
                            break;
                        case "<":
                            if ("and".equalsIgnoreCase(whereDTO.condition)) {
                                query.and().lt(keyword, value);
                            } else {
                                query.or().lt(keyword, value);
                            }
                            break;
                        case ">=":
                            if ("and".equalsIgnoreCase(whereDTO.condition)) {
                                query.and().ge(keyword, value);
                            } else {
                                query.or().ge(keyword, value);
                            }
                            break;
                        case "<=":
                            if ("and".equalsIgnoreCase(whereDTO.condition)) {
                                query.and().le(keyword, value);
                            } else {
                                query.or().le(keyword, value);
                            }
                            break;
                        case "!=":
                            if ("and".equalsIgnoreCase(whereDTO.condition)) {
                                query.and().ne(keyword, value);
                            } else {
                                query.or().ne(keyword, value);
                            }
                            break;
                        case "like":
                            if ("and".equalsIgnoreCase(whereDTO.condition)) {
                                query.and().like(keyword, "%" + value + "%");
                            } else {
                                query.or().like(keyword, "%" + value + "%");
                            }
                            break;
                        default:
                            if ("and".equalsIgnoreCase(whereDTO.condition)) {
                                query.and().eq(keyword, value);
                            } else {
                                query.or().ilike(keyword, "%" + value + "%");
                            }
                            break;
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("无效字段: " + keyword, e);
                }

            }
        }
        return query;
    }

    /**
     * 根据字段类型转换值
     */
    private static Object convertValue(Class<?> fieldType, String value) {
        if (fieldType == String.class) {
            return value;
        } else if (fieldType == Long.class || fieldType == long.class) {
            return Long.parseLong(value);
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return Integer.parseInt(value);
        } else if (fieldType == Double.class || fieldType == double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (fieldType == java.util.Date.class || fieldType == java.sql.Date.class) {
            return java.sql.Date.valueOf(java.time.LocalDate.parse(value));
        } else if (fieldType == java.time.LocalDate.class) {
            return java.time.LocalDate.parse(value);
        } else if (fieldType == java.time.LocalDateTime.class) {
            return java.time.LocalDateTime.parse(value);
        } else {
            return value;
        }
    }


}