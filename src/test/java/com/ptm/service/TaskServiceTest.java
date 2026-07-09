package com.ptm.service;

import com.ptm.entity.Task;
import com.ptm.mapper.TaskMapper;
import com.ptm.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task normalTask;

    @BeforeEach
    void setUp() {
        normalTask = new Task();
        normalTask.setId(1L);
        normalTask.setName("开发任务");
        normalTask.setCode("T001");
        normalTask.setProjectId(1L);
        normalTask.setStatus(1);
        normalTask.setCreateBy(1L);
    }

    @Nested
    @DisplayName("列表查询测试")
    class ListTest {

        @Test
        @DisplayName("查询所有任务 - 返回任务列表")
        void listSuccess() {
            List<Task> taskList = Collections.singletonList(normalTask);
            when(taskMapper.findAll()).thenReturn(taskList);

            ResponseResult<List<Task>> result = taskService.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals("开发任务", result.getData().get(0).getName());
            verify(taskMapper).findAll();
        }

        @Test
        @DisplayName("任务列表为空时返回空列表")
        void listEmpty() {
            when(taskMapper.findAll()).thenReturn(Collections.emptyList());

            ResponseResult<List<Task>> result = taskService.list();

            assertEquals(200, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("根据ID查询测试")
    class GetByIdTest {

        @Test
        @DisplayName("查询任务成功")
        void getByIdSuccess() {
            when(taskMapper.findById(1L)).thenReturn(normalTask);

            ResponseResult<Task> result = taskService.getById(1L);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("T001", result.getData().getCode());
        }

        @Test
        @DisplayName("任务不存在 - 返回404")
        void getByIdNotFound() {
            when(taskMapper.findById(999L)).thenReturn(null);

            ResponseResult<Task> result = taskService.getById(999L);

            assertEquals(404, result.getCode());
            assertEquals("任务不存在", result.getMsg());
            assertNull(result.getData());
        }
    }

    @Nested
    @DisplayName("新增任务测试")
    class AddTest {

        @Test
        @DisplayName("新增任务成功 - 默认状态为1")
        void addSuccessWithDefaultStatus() {
            Task newTask = new Task();
            newTask.setName("新任务");
            newTask.setCode("T002");
            newTask.setProjectId(1L);

            when(taskMapper.insert(any(Task.class))).thenReturn(1);

            ResponseResult<Void> result = taskService.add(newTask);

            assertEquals(200, result.getCode());
            assertEquals(1, newTask.getStatus());
            verify(taskMapper).insert(newTask);
        }

        @Test
        @DisplayName("新增任务成功 - 使用自定义状态")
        void addSuccessWithCustomStatus() {
            Task newTask = new Task();
            newTask.setName("已停用任务");
            newTask.setCode("T003");
            newTask.setProjectId(1L);
            newTask.setStatus(0);

            when(taskMapper.insert(any(Task.class))).thenReturn(1);

            ResponseResult<Void> result = taskService.add(newTask);

            assertEquals(200, result.getCode());
            assertEquals(0, newTask.getStatus());
            verify(taskMapper).insert(newTask);
        }
    }

    @Nested
    @DisplayName("更新任务测试")
    class UpdateTest {

        @Test
        @DisplayName("更新任务成功")
        void updateSuccess() {
            Task updateTask = new Task();
            updateTask.setId(1L);
            updateTask.setName("开发任务V2");
            updateTask.setCode("T001");

            when(taskMapper.findById(1L)).thenReturn(normalTask);
            when(taskMapper.update(any(Task.class))).thenReturn(1);

            ResponseResult<Void> result = taskService.update(updateTask);

            assertEquals(200, result.getCode());
            verify(taskMapper).update(updateTask);
        }

        @Test
        @DisplayName("更新不存在的任务 - 返回404")
        void updateNotFound() {
            Task updateTask = new Task();
            updateTask.setId(999L);

            when(taskMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = taskService.update(updateTask);

            assertEquals(404, result.getCode());
            assertEquals("任务不存在", result.getMsg());
            verify(taskMapper, never()).update(any(Task.class));
        }
    }

    @Nested
    @DisplayName("删除任务测试")
    class DeleteTest {

        @Test
        @DisplayName("删除任务成功")
        void deleteSuccess() {
            when(taskMapper.findById(1L)).thenReturn(normalTask);
            when(taskMapper.deleteById(1L)).thenReturn(1);

            ResponseResult<Void> result = taskService.delete(1L);

            assertEquals(200, result.getCode());
            verify(taskMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的任务 - 返回404")
        void deleteNotFound() {
            when(taskMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = taskService.delete(999L);

            assertEquals(404, result.getCode());
            assertEquals("任务不存在", result.getMsg());
            verify(taskMapper, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("按项目查询任务测试")
    class ListByProjectTest {

        @Test
        @DisplayName("按项目ID查询任务成功")
        void listByProjectSuccess() {
            List<Task> taskList = Collections.singletonList(normalTask);
            when(taskMapper.findByProjectId(1L)).thenReturn(taskList);

            ResponseResult<List<Task>> result = taskService.listByProject(1L);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals(1L, result.getData().get(0).getProjectId());
            verify(taskMapper).findByProjectId(1L);
        }

        @Test
        @DisplayName("按项目ID查询任务 - 项目无任务返回空列表")
        void listByProjectEmpty() {
            when(taskMapper.findByProjectId(999L)).thenReturn(Collections.emptyList());

            ResponseResult<List<Task>> result = taskService.listByProject(999L);

            assertEquals(200, result.getCode());
            assertTrue(result.getData().isEmpty());
            verify(taskMapper).findByProjectId(999L);
        }
    }
}
