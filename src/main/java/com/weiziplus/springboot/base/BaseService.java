package com.weiziplus.springboot.base;

import com.weiziplus.springboot.utils.StringUtil;
import com.weiziplus.springboot.utils.ToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础service
 *
 * @author wanglongwei
 * @data 2019/5/20 10:44
 */
@Slf4j
public class BaseService {

    @Autowired
    BaseMapper mapper;

    /**
     * 根据实体类class获取数据库表名
     *
     * @param nowClass
     * @return
     */
    private String getTableName(Class nowClass) {
        if (null == nowClass.getAnnotation(Table.class)) {
            throw new RuntimeException("当前实体类没有设置@Table注解==========" + nowClass);
        }
        Table table = (Table) nowClass.getAnnotation(Table.class);
        return table.value();
    }

    /**
     * 根据实体对象获取数据库表名
     *
     * @param object
     * @return
     */
    private String getTableName(Object object) {
        if (null == object || null == object.getClass()) {
            throw new RuntimeException("将实体对象转为map===Object为null");
        }
        //获取实体类对应数据库的表名
        if (null == object.getClass().getAnnotation(Table.class)) {
            throw new RuntimeException("当前实体类没有设置@Table注解==========" + object.getClass());
        }
        return object.getClass().getAnnotation(Table.class).value();
    }

    /**
     * 根据实体类class获取数据库表名
     *
     * @param nowClass
     * @return
     */
    private String getPrimaryKey(Class nowClass) {
        if (null == nowClass.getAnnotation(Table.class)) {
            throw new RuntimeException("当前实体类没有设置@Table注解==========" + nowClass);
        }
        Field[] fields = nowClass.getDeclaredFields();
        for (Field field : fields) {
            if (null != field.getAnnotation(Id.class)) {
                return field.getAnnotation(Id.class).value();
            }
        }
        throw new RuntimeException("当前实体类没有设置主键==========" + nowClass);
    }

    /**
     * 根据实体类对象插入数据
     *
     * @param object
     * @return
     */
    private Map<String, Object> handleTableInsert(Object object) {
        Map<String, Object> result = new HashMap<>(3);
        result.put("TABLE_NAME", getTableName(object));

        //存放表的字段
        List<String> columns = new ArrayList<>();
        //存放表的字段值
        List<Object> values = new ArrayList<>();
        //获取实体类所有变量
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            //查看变量是否有注解
            if (null == field.getAnnotation(Column.class) && null == field.getAnnotation(Id.class)) {
                continue;
            }
            Object value;
            try {
                field.setAccessible(true);
                value = field.get(object);
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException("实体类get方法出错==========" + object.getClass() + "---" + e);
            }
            //如果值为null，则不处理
            if (null == value) {
                continue;
            }
            if (null != field.getAnnotation(Id.class)) {
                //添加表的主键
                columns.add(field.getAnnotation(Id.class).value());
                result.put("KEY_ID", field.getAnnotation(Id.class).value());
            } else {
                //添加表的普通字段
                columns.add(field.getAnnotation(Column.class).value());
            }
            values.add(value);
        }
        if (columns.size() != values.size()) {
            throw new RuntimeException("实体类反射字段和值的数量不一致" + object.getClass());
        }
        result.put("COLUMNS", columns);
        result.put("VALUES", values);
        return result;
    }

    /**
     * 根据实体类数组插入数据
     *
     * @param list
     * @return
     */
    private Map<String, Object> handleTableListInsert(List<Object> list) {
        Map<String, Object> result = new HashMap<>(3);
        result.put("TABLE_NAME", getTableName(list.get(0)));

        //存放表的字段
        List<String> columns = new ArrayList<>();
        //存放字段的值
        List<List<Object>> valuesList = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);
            //存放表的字段值
            List<Object> values = new ArrayList<>();
            //获取实体类所有变量
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                //查看变量是否有注解
                if (null == field.getAnnotation(Column.class) && null == field.getAnnotation(Id.class)) {
                    continue;
                }
                Object value;
                try {
                    field.setAccessible(true);
                    value = field.get(object);
                    field.setAccessible(false);
                } catch (Exception e) {
                    throw new RuntimeException("实体类get方法出错==========" + object.getClass() + "---" + e);
                }
                values.add(value);
                //第一次循环将字段添加到数组里面
                if (i > 0) {
                    continue;
                }
                if (null != field.getAnnotation(Id.class)) {
                    //添加表的主键
                    columns.add(field.getAnnotation(Id.class).value());
                } else {
                    //添加表的普通字段
                    columns.add(field.getAnnotation(Column.class).value());
                }
            }
            valuesList.add(values);
        }
        result.put("COLUMNS", columns);
        result.put("VALUES_LIST", valuesList);
        return result;
    }

    /**
     * 根据实体类对象更新数据
     *
     * @param object
     * @return
     */
    private Map<String, Object> handleTableUpdate(Object object) {
        Map<String, Object> result = new HashMap<>(3);
        result.put("TABLE_NAME", getTableName(object));

        //存放表的字段和字段值
        List<Map<String, Object>> columnValues = new ArrayList<>();
        //获取实体类所有变量
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            //查看变量是否有注解
            if (null == field.getAnnotation(Column.class) && null == field.getAnnotation(Id.class)) {
                continue;
            }
            Object value;
            try {
                field.setAccessible(true);
                value = field.get(object);
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException("实体类get方法出错==========" + object.getClass() + "---" + e);
            }
            //获取表的主键
            if (null != field.getAnnotation(Id.class)) {
                result.put("KEY_ID", field.getAnnotation(Id.class).value());
                result.put("KEY_VALUE", value);
                continue;
            }
            //如果值为null，则不处理
            if (null == value) {
                continue;
            }
            columnValues.add(new HashMap<String, Object>(2) {{
                put("column", field.getAnnotation(Column.class).value());
                put("value", value);
            }});
        }
        result.put("COLUMNS_VALUES", columnValues);
        return result;
    }

    /**
     * 新增
     *
     * @param object
     * @return
     */
    protected int baseInsert(Object object) {
        return baseInsert(object, false);
    }

    /**
     * 新增
     *
     * @param object
     * @param getAutoIncrementPrimaryKey 自增主键赋值给实体变量
     * @return
     */
    protected int baseInsert(Object object, boolean getAutoIncrementPrimaryKey) {
        if (null == object) {
            return 0;
        }
        Map<String, Object> map = handleTableInsert(object);
        int insert = mapper.insert(map);
        if (!getAutoIncrementPrimaryKey) {
            return insert;
        }
        //获取实体类所有变量
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            //获取表的主键
            if (null == field || null == field.getAnnotation(Id.class)) {
                continue;
            }
            try {
                String longTypeName = Long.class.getTypeName();
                String stringTypeName = String.class.getTypeName();
                String integerTypeName = Integer.class.getTypeName();
                String typeName = field.getGenericType().getTypeName();
                String keyId = StringUtil.valueOf(map.get("KEY_ID"));
                field.setAccessible(true);
                if (longTypeName.equals(typeName)) {
                    field.set(object, Long.valueOf(keyId));
                } else if (stringTypeName.equals(typeName)) {
                    field.set(object, keyId);
                } else if (integerTypeName.equals(typeName)) {
                    field.set(object, ToolUtil.valueOfInteger(keyId));
                }
                field.setAccessible(false);
                break;
            } catch (IllegalAccessException e) {
                log.debug("baseInsert插入数据错误，如果不使用返回的自增主键，该消息可以忽略，详情:" + e);
            }
        }
        return insert;
    }

    /**
     * 新增多个
     *
     * @param list
     * @return
     */
    protected int baseInsertList(List<Object> list) {
        if (null == list || 0 >= list.size()) {
            return 0;
        }
        return mapper.insertList(handleTableListInsert(list));
    }

    /**
     * 删除
     *
     * @param nowClass
     * @param id
     * @return
     */
    protected int baseDeleteByClassAndId(Class nowClass, Long id) {
        if (null == nowClass || null == id) {
            return 0;
        }
        return mapper.deleteById(getTableName(nowClass), id);
    }

    /**
     * 删除
     *
     * @param nowClass
     * @param id
     * @return
     */
    protected int baseDeleteByClassAndId(Class nowClass, String id) {
        if (null == nowClass || null == id || StringUtil.isBlank(id)) {
            return 0;
        }
        return mapper.deleteById(getTableName(nowClass), id);
    }

    /**
     * 删除多个
     *
     * @param nowClass
     * @param ids
     * @return
     */
    protected int baseDeleteByClassAndIds(Class nowClass, Long[] ids) {
        if (null == nowClass || null == ids || 0 >= ids.length) {
            return 0;
        }
        return mapper.deleteByIds(getTableName(nowClass), ids);
    }

    /**
     * 删除多个
     *
     * @param nowClass
     * @param ids
     * @return
     */
    protected int baseDeleteByClassAndIds(Class nowClass, String[] ids) {
        if (null == nowClass || null == ids || 0 >= ids.length) {
            return 0;
        }
        return mapper.deleteByIds(getTableName(nowClass), ids);
    }

    /**
     * 修改数据
     *
     * @param object
     * @return
     */
    protected int baseUpdate(Object object) {
        return mapper.update(handleTableUpdate(object));
    }

    /**
     * 根据id查询
     *
     * @param nowClass
     * @param id
     * @return
     */
    protected Map<String, Object> baseFindByClassAndId(Class nowClass, Long id) {
        if (null == nowClass || null == id) {
            return null;
        }
        return mapper.findById(getTableName(nowClass), id);
    }

    /**
     * 根据id查询
     *
     * @param nowClass
     * @param id
     * @return
     */
    protected Map<String, Object> baseFindByClassAndId(Class nowClass, String id) {
        if (null == nowClass || null == id || StringUtil.isBlank(id)) {
            return null;
        }
        return mapper.findById(getTableName(nowClass), id);
    }

    /**
     * 获取所有数据
     *
     * @param nowClass
     * @return
     */
    protected List<Map<String, Object>> baseFindAllByClass(Class nowClass) {
        return mapper.findAll(getTableName(nowClass), getPrimaryKey(nowClass));
    }

    /**
     * 根据唯一前缀和方法参数创建唯一redis的key
     *
     * @param onlyPrefix
     * @param objects
     * @return
     */
    protected String createRedisKey(String onlyPrefix, Object... objects) {
        StringBuffer stringBuffer = new StringBuffer(onlyPrefix);
        for (Object object : objects) {
            stringBuffer.append("_666_").append(object);
        }
        return StringUtil.valueOf(stringBuffer);
    }
}
