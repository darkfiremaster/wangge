package com.shinemo.wangge.core.service.todo.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.shinemo.client.common.ListVO;
import com.shinemo.client.common.StatusEnum;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.domain.TodoLogDO;
import com.shinemo.todo.domain.TodoTypeDO;
import com.shinemo.todo.dto.TodoTypeDTO;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.enums.TodoMethodOperateEnum;
import com.shinemo.todo.enums.TodoStatusEnum;
import com.shinemo.todo.enums.TodoTypeEnum;
import com.shinemo.todo.error.TodoErrorCodes;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.query.TodoTypeQuery;
import com.shinemo.todo.vo.TodoDTO;
import com.shinemo.todo.vo.TodoIndexVO;
import com.shinemo.todo.vo.TodoTypeVO;
import com.shinemo.todo.vo.TodoVO;
import com.shinemo.wangge.core.aop.TodoLog;
import com.shinemo.wangge.core.service.todo.TodoAuthCheckService;
import com.shinemo.wangge.core.service.todo.TodoService;
import com.shinemo.wangge.dal.mapper.ThirdTodoMapper;
import com.shinemo.wangge.dal.mapper.TodoLogMapper;
import com.shinemo.wangge.dal.mapper.TodoTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:25
 * @Desc
 */
@Service
@Slf4j
public class TodoServiceImpl implements TodoService {

    @Resource
    private ThirdTodoMapper thirdTodoMapper;

    @Resource
    private TodoTypeMapper todoTypeMapper;

    @Resource
    private TodoLogMapper todoLogMapper;

    @Resource
    private TodoAuthCheckService todoAuthCheckService;

    @CreateCache(name = "TodoServiceImpl.todoTypeCache-", cacheType = CacheType.LOCAL, expire = 60 * 60)
    private Cache<String, TodoTypeVO> todoTypeCache;

    private static final String TODO_TYPE_CACHE_KEY = "todo_type_list";


    @TodoLog
    @Override
    public ApiResult<Void> operateTodoThing(TreeMap<String, Object> treeMap) {
        ApiResult<TodoDTO> result = todoAuthCheckService.checkSign(treeMap);
        if (!result.isSuccess()) {
            log.error("[operateTodoThing] check sign error, request:{},result:{}", treeMap, result);
            return ApiResult.fail(result.getMsg(), result.getCode());
        }

        TodoDTO todoDTO = result.getData();
        Assert.notBlank(todoDTO.getThirdId(), "thirdId is null");
        Assert.notNull(todoDTO.getThirdType(), "thirdType is null");
        Assert.notNull(todoDTO.getOperateType(), "operateType is null");
        Assert.notBlank(todoDTO.getOperatorMobile(), "operatorMobile is null");

        if (Objects.equals(todoDTO.getOperateType(), TodoMethodOperateEnum.CREATE.getId())) {
            return createTodo(todoDTO);
        } else if (Objects.equals(todoDTO.getOperateType(), TodoMethodOperateEnum.UPDATE.getId())) {
            return updateTodo(todoDTO);
        } else if (Objects.equals(todoDTO.getOperateType(), TodoMethodOperateEnum.DELETE.getId())) {
            return deleteTodo(todoDTO);
        } else {
            return ApiResultWrapper.fail(TodoErrorCodes.OPERATE_TYPE_ERROR);
        }
    }

    @Override
    public ApiResult<TreeMap> getTodoThirdRequest(TodoDTO todoDTO) {
        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("ignoreCheckSign", true);
        treeMap.put("postBody", todoDTO);
        return ApiResult.of(0, treeMap);
    }

    private ApiResult<Void> createTodo(TodoDTO todoDTO) {
        //校验参数
        Assert.notBlank(todoDTO.getTitle(), "title is null");
        Assert.notBlank(todoDTO.getRemark(), "remark is null");
        Assert.notNull(todoDTO.getStatus(), "status is null");
        Assert.notBlank(todoDTO.getLabel(), "label is null");
        Assert.notBlank(todoDTO.getOperatorTime(), "operatorTime is null");

        TodoQuery todoQuery = new TodoQuery();
        todoQuery.setThirdId(todoDTO.getThirdId());
        todoQuery.setThirdType(todoDTO.getThirdType());
        todoQuery.setMobile(todoDTO.getOperatorMobile());
        TodoDO todoDB = thirdTodoMapper.get(todoQuery);
        if (todoDB != null) {
            //修改
            updateTodo(todoDTO);
        } else {
            //新增
            TodoDO todoDO = getTodoDO(todoDTO);
            thirdTodoMapper.insert(todoDO);
            //log.info("[createTodo] create todo success,  todoDTO:{}", todoDTO);
        }

        return ApiResult.of(0);

    }

    private ApiResult<Void> updateTodo(TodoDTO todoDTO) {

        //先查再改
        TodoQuery todoQuery = new TodoQuery();
        todoQuery.setThirdId(todoDTO.getThirdId());
        todoQuery.setThirdType(todoDTO.getThirdType());
        todoQuery.setMobile(todoDTO.getOperatorMobile());

        TodoDO todoDOFromDB = thirdTodoMapper.get(todoQuery);
        if (todoDOFromDB == null) {
            return ApiResultWrapper.fail(TodoErrorCodes.DATA_NOT_EXIST);
        }

        //转换对象
        TodoDO todoDO = getTodoDO(todoDTO);
        todoDO.setId(todoDOFromDB.getId());

        //修改
        thirdTodoMapper.update(todoDO);

        //log.info("[updateTodo] update todo success,todoDTO:{}", todoDTO);
        return ApiResult.of(0);
    }

    private ApiResult<Void> deleteTodo(TodoDTO todoDTO) {


        //先查再删
        TodoQuery todoQuery = new TodoQuery();
        todoQuery.setThirdId(todoDTO.getThirdId());
        todoQuery.setThirdType(todoDTO.getThirdType());
        todoQuery.setMobile(todoDTO.getOperatorMobile());

        TodoDO todoDO = thirdTodoMapper.get(todoQuery);
        if (todoDO == null) {
            return ApiResultWrapper.fail(TodoErrorCodes.DATA_NOT_EXIST);
        }

        //删除
        thirdTodoMapper.delete(todoDO.getId());

        //log.info("[deleteTodo] delete todo success, todoDTO:{}", todoDTO);
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<TodoTypeVO> getTypeList() {
        TodoTypeVO todoTypeVO = todoTypeCache.get(TODO_TYPE_CACHE_KEY);
        if (todoTypeVO != null) {
            return ApiResult.of(0, todoTypeVO);
        }

        TodoTypeQuery todoTypeQuery = new TodoTypeQuery();
        todoTypeQuery.setPageEnable(false);
        todoTypeQuery.setStatus(StatusEnum.NORMAL.getId());
        List<TodoTypeDO> todoTypeDOS = todoTypeMapper.find(todoTypeQuery);

        //组装VO
        Multimap<Integer, TodoTypeDO> multimap = ArrayListMultimap.create();
        for (TodoTypeDO todoTypeDO : todoTypeDOS) {
            multimap.put(todoTypeDO.getType(), todoTypeDO);
        }
        todoTypeVO = new TodoTypeVO();
        List<TodoTypeVO.TodoTypeListBean> todoTypeList = new ArrayList<>();
        TodoTypeEnum[] values = TodoTypeEnum.values();
        for (TodoTypeEnum todoTypeEnum : values) {
            Collection<TodoTypeDO> typeDOList = multimap.get(todoTypeEnum.getId());
            TodoTypeVO.TodoTypeListBean toDoTypeListBean = new TodoTypeVO.TodoTypeListBean();
            toDoTypeListBean.setName(todoTypeEnum.getName());
            List<TodoTypeVO.TodoTypeListBean.ChildListBean> childListBeanList =
                    typeDOList.stream()
                            .map(todoTypeDO -> new TodoTypeVO.TodoTypeListBean.ChildListBean(todoTypeDO.getName(), todoTypeDO.getTodoType(), null))
                            .collect(Collectors.toList());
            toDoTypeListBean.setChildren(childListBeanList);
            todoTypeList.add(toDoTypeListBean);
        }
        todoTypeVO.setToDoTypeList(todoTypeList);

        todoTypeCache.put(TODO_TYPE_CACHE_KEY, todoTypeVO);

        return ApiResult.of(0, todoTypeVO);
    }

    @Override
    public ApiResult<Void> clearTypeListCache() {
        todoTypeCache.remove(TODO_TYPE_CACHE_KEY);
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<ListVO<TodoVO>> getTodoList(TodoQuery todoQuery) {
        Assert.notNull(todoQuery, "todoQuery is null");
        Assert.notNull(SmartGridContext.getMobile(), "mobile is null");
        Assert.notNull(todoQuery.getTimeType(), "timeType is null");
        Assert.notNull(todoQuery.getPageSize(), "pageSize is null");
        Assert.notNull(todoQuery.getCurrentPage(), "currentPage is null");
        todoQuery.setMobile(SmartGridContext.getMobile());
        Page<TodoDO> page = PageHelper.startPage(todoQuery.getCurrentPage().intValue(), todoQuery.getPageSize().intValue());
        todoQuery.setStatus(TodoStatusEnum.NOT_FINISH.getId());//目前只查未完成的代办事项
        todoQuery.setOrderByEnable(true);
        todoQuery.putOrderBy("operator_time", false);
        List<TodoDO> todoDOS = thirdTodoMapper.find(todoQuery);
        ListVO<TodoVO> listVO = new ListVO();
        listVO.setTotalCount(page.getTotal());
        List<TodoVO> todoVOList = todoDOS.stream().map(todoDO -> {
            TodoVO todoVO = new TodoVO();
            BeanUtils.copyProperties(todoDO, todoVO);
            todoVO.setThirdTypeName(ThirdTodoTypeEnum.getById(todoVO.getThirdType()).getName());
            return todoVO;
        }).collect(Collectors.toList());
        listVO.setRows(todoVOList);
        listVO.setCurrentPage(todoQuery.getCurrentPage());
        listVO.setPageSize(todoQuery.getPageSize());
        return ApiResult.of(0, listVO);
    }


    @Override
    public ApiResult<TodoIndexVO> getIndexInfo() {
        Integer todayTodoCount = thirdTodoMapper.getTodayTodoCount(SmartGridContext.getMobile());
        Integer allTodoCount = thirdTodoMapper.getAllTodoCount(SmartGridContext.getMobile());
        TodoIndexVO todoIndexVO = new TodoIndexVO();
        todoIndexVO.setTodayTodoCount(todayTodoCount);
        todoIndexVO.setAllTodoCount(allTodoCount);
        return ApiResult.of(0, todoIndexVO);
    }

    @Async
    @Override
    public ApiResult<Void> insertTodoLog(TodoLogDO todoLogDO) {
        todoLogMapper.insert(todoLogDO);
        return ApiResult.of(0);
    }


    @Override
    public ApiResult<TodoTypeVO> getAllTodoTypeList() {
        TodoQuery todoQuery = new TodoQuery();
        todoQuery.setStatus(TodoStatusEnum.NOT_FINISH.getId());
        todoQuery.setMobile(SmartGridContext.getMobile());
        List<TodoTypeDTO> todoTypeDTOList = thirdTodoMapper.getAllTodoTypeList(todoQuery);

        //组装VO
        Multimap<Integer, TodoTypeDTO> multimap = ArrayListMultimap.create();
        for (TodoTypeDTO todoTypeDTO : todoTypeDTOList) {
            multimap.put(todoTypeDTO.getType(), todoTypeDTO);
        }
        TodoTypeVO todoTypeVO = new TodoTypeVO();
        List<TodoTypeVO.TodoTypeListBean> todoTypeList = new ArrayList<>();
        TodoTypeEnum[] values = TodoTypeEnum.values();
        for (TodoTypeEnum todoTypeEnum : values) {
            Collection<TodoTypeDTO> typeDTOList = multimap.get(todoTypeEnum.getId());
            TodoTypeVO.TodoTypeListBean toDoTypeListBean = new TodoTypeVO.TodoTypeListBean();
            toDoTypeListBean.setName(todoTypeEnum.getName());
            List<TodoTypeVO.TodoTypeListBean.ChildListBean> childListBeanList =
                    typeDTOList.stream()
                            .map(todoTypeDTO -> new TodoTypeVO.TodoTypeListBean.ChildListBean(todoTypeDTO.getName(), todoTypeDTO.getTodoType(), todoTypeDTO.getTodoCount()))
                            .collect(Collectors.toList());
            toDoTypeListBean.setChildren(childListBeanList);
            todoTypeList.add(toDoTypeListBean);
        }
        todoTypeVO.setToDoTypeList(todoTypeList);

        return ApiResult.of(0, todoTypeVO);
    }




    private TodoDO getTodoDO(TodoDTO todoDTO) {
        TodoDO todoDO = new TodoDO();
        BeanUtils.copyProperties(todoDTO, todoDO);
        //转换时间
        try {
            String operatorTime = todoDTO.getOperatorTime();
            if (!org.apache.commons.lang3.StringUtils.isBlank(operatorTime)) {
                todoDO.setOperatorTime(DateUtil.parse(operatorTime, "yyyy-MM-dd HH:mm:ss"));
            }
            if (!org.apache.commons.lang3.StringUtils.isBlank(todoDTO.getStartTime())) {
                todoDO.setStartTime(DateUtil.parse(todoDTO.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
            }
        } catch (Exception e) {
            log.error("[getTodoDO] date parse error,request:{}, errorMsg:{}", todoDTO, e.getMessage(), e);
            throw new ApiException(TodoErrorCodes.DATE_PARSE_ERROR);
        }


        return todoDO;
    }
}
