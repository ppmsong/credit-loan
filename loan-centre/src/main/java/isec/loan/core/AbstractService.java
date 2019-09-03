package isec.loan.core;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * 基于通用MyBatis Mapper插件的Service接口的实现
 */
public abstract class AbstractService<T> {

    @Autowired
    protected Mapper<T> mapper;

    // 当前泛型真实类型的Class
    private Class<T> modelClass;

    public AbstractService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
    }

    public int save(T model) {
        return mapper.insertSelective(model);
    }

    public int save(List<T> models) {
        return mapper.insertList(models);
    }

    public int deleteById(String id) {
        return mapper.deleteByPrimaryKey(id);
    }

    public int deleteByIds(String ids) {
        return mapper.deleteByIds(ids);
    }

    public int update(T model) {
        //会对所有非空字段进行更新
        return mapper.updateByPrimaryKeySelective(model);
    }
    
	public int updateByWhere(T model, String where) {
		Condition condition = new Condition(modelClass);
		condition.createCriteria().andCondition(where);
		return mapper.updateByConditionSelective(model, condition);
	}
    

    public T findById(String id) {
        return mapper.selectByPrimaryKey(id);
    }


   /* public T findBy(String fieldName, Object value) {
        try {
            T model = modelClass.newInstance();
            Field
                    field = modelClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(model, value);
            return mapper.selectOne(model);
        } catch
                (ReflectiveOperationException e) {

        }
    }*/


    public T findBy(String fieldName, Object value) throws TooManyResultsException {

        Condition condition = new Condition(modelClass);
        condition.createCriteria().andCondition(fieldName + "=" + value);
        List<T> list = mapper.selectByCondition(condition);
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;

    }



    public List<T> findBys(String fieldName, Object value) throws TooManyResultsException {

        Condition condition = new Condition(modelClass);
        condition.createCriteria().andCondition(fieldName + "=" + value);
        return mapper.selectByCondition(condition);
    }

    public List<T> findByIds(String ids) {
        return mapper.selectByIds(ids);
    }

    public List<T> getByCondition(Condition condition) {
        return mapper.selectByCondition(condition);
    }
    
	public List<T> findByWhere(String where) throws TooManyResultsException {
		Condition condition = new Condition(modelClass);
		condition.createCriteria().andCondition(where);
		return mapper.selectByCondition(condition);
	}
	
	public T findOneByWhere(String where) throws TooManyResultsException {
		Condition condition = new Condition(modelClass);
		condition.createCriteria().andCondition(where);
		List<T> list = mapper.selectByCondition(condition);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		if (list.size() != 1) {
			throw new RuntimeException("查询结果有多条记录");
		}
		return list.get(0);
	}

    public List<T> findAll() {
        return mapper.selectAll();
    }
}
