package com.ptm.service;

import com.ptm.entity.Project;
import com.ptm.entity.User;
import com.ptm.mapper.ProjectMapper;
import com.ptm.mapper.ProjectMemberMapper;
import com.ptm.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectMemberMapper projectMemberMapper;

    @InjectMocks
    private ProjectService projectService;

    private Project normalProject;

    @BeforeEach
    void setUp() {
        normalProject = new Project();
        normalProject.setId(1L);
        normalProject.setName("测试项目");
        normalProject.setCode("P001");
        normalProject.setDescription("这是一个测试项目");
        normalProject.setDept("技术部");
        normalProject.setStatus(1);
        normalProject.setStartDate("2024-01-01");
        normalProject.setEndDate("2024-12-31");
        normalProject.setCreateBy(1L);
    }

    @Nested
    @DisplayName("列表查询测试")
    class ListTest {

        @Test
        @DisplayName("查询所有项目 - 返回项目列表")
        void listSuccess() {
            List<Project> projectList = Collections.singletonList(normalProject);
            when(projectMapper.findAll()).thenReturn(projectList);

            ResponseResult<List<Project>> result = projectService.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals("测试项目", result.getData().get(0).getName());
            verify(projectMapper).findAll();
        }

        @Test
        @DisplayName("项目列表为空时返回空列表")
        void listEmpty() {
            when(projectMapper.findAll()).thenReturn(Collections.emptyList());

            ResponseResult<List<Project>> result = projectService.list();

            assertEquals(200, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("根据ID查询测试")
    class GetByIdTest {

        @Test
        @DisplayName("查询项目成功")
        void getByIdSuccess() {
            when(projectMapper.findById(1L)).thenReturn(normalProject);

            ResponseResult<Project> result = projectService.getById(1L);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("P001", result.getData().getCode());
        }

        @Test
        @DisplayName("项目不存在 - 返回404")
        void getByIdNotFound() {
            when(projectMapper.findById(999L)).thenReturn(null);

            ResponseResult<Project> result = projectService.getById(999L);

            assertEquals(404, result.getCode());
            assertEquals("项目不存在", result.getMsg());
            assertNull(result.getData());
        }
    }

    @Nested
    @DisplayName("新增项目测试")
    class AddTest {

        @Test
        @DisplayName("新增项目成功 - 默认状态为1")
        void addSuccessWithDefaultStatus() {
            Project newProject = new Project();
            newProject.setName("新项目");
            newProject.setCode("P002");
            when(projectMapper.insert(any(Project.class))).thenReturn(1);

            ResponseResult<Void> result = projectService.add(newProject);

            assertEquals(200, result.getCode());
            assertEquals(1, newProject.getStatus());
            verify(projectMapper).insert(newProject);
        }

        @Test
        @DisplayName("新增项目成功 - 使用自定义状态")
        void addSuccessWithCustomStatus() {
            Project newProject = new Project();
            newProject.setName("停用项目");
            newProject.setCode("P003");
            newProject.setStatus(0);

            when(projectMapper.insert(any(Project.class))).thenReturn(1);

            ResponseResult<Void> result = projectService.add(newProject);

            assertEquals(200, result.getCode());
            assertEquals(0, newProject.getStatus());
            verify(projectMapper).insert(newProject);
        }
    }

    @Nested
    @DisplayName("更新项目测试")
    class UpdateTest {

        @Test
        @DisplayName("更新项目成功")
        void updateSuccess() {
            Project updateProject = new Project();
            updateProject.setId(1L);
            updateProject.setName("项目更新");
            updateProject.setCode("P001");

            when(projectMapper.findById(1L)).thenReturn(normalProject);
            when(projectMapper.update(any(Project.class))).thenReturn(1);

            ResponseResult<Void> result = projectService.update(updateProject);

            assertEquals(200, result.getCode());
            verify(projectMapper).update(updateProject);
        }

        @Test
        @DisplayName("更新不存在的项目 - 返回404")
        void updateNotFound() {
            Project updateProject = new Project();
            updateProject.setId(999L);

            when(projectMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = projectService.update(updateProject);

            assertEquals(404, result.getCode());
            assertEquals("项目不存在", result.getMsg());
            verify(projectMapper, never()).update(any(Project.class));
        }
    }

    @Nested
    @DisplayName("删除项目测试")
    class DeleteTest {

        @Test
        @DisplayName("删除项目成功 - 无未完成工时")
        void deleteSuccess() {
            when(projectMapper.findById(1L)).thenReturn(normalProject);
            when(projectMapper.countUnfinishedTimeEntries(1L)).thenReturn(0L);
            when(projectMemberMapper.deleteByProjectId(1L)).thenReturn(1);
            when(projectMapper.deleteById(1L)).thenReturn(1);

            ResponseResult<Void> result = projectService.delete(1L);

            assertEquals(200, result.getCode());
            verify(projectMemberMapper).deleteByProjectId(1L);
            verify(projectMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除项目时存在未完成工时 - 返回400")
        void deleteWithUnfinishedTimeEntries() {
            when(projectMapper.findById(1L)).thenReturn(normalProject);
            when(projectMapper.countUnfinishedTimeEntries(1L)).thenReturn(3L);

            ResponseResult<Void> result = projectService.delete(1L);

            assertEquals(400, result.getCode());
            assertTrue(result.getMsg().contains("未完成"));
            assertTrue(result.getMsg().contains("3"));
            verify(projectMemberMapper, never()).deleteByProjectId(anyLong());
            verify(projectMapper, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("删除不存在的项目 - 返回404")
        void deleteNotFound() {
            when(projectMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = projectService.delete(999L);

            assertEquals(404, result.getCode());
            assertEquals("项目不存在", result.getMsg());
            verify(projectMapper, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("获取项目成员测试")
    class GetMembersTest {

        @Test
        @DisplayName("获取项目成员成功")
        void getMembersSuccess() {
            User member1 = new User();
            member1.setId(1L);
            member1.setName("张三");
            User member2 = new User();
            member2.setId(2L);
            member2.setName("李四");
            List<User> members = Arrays.asList(member1, member2);

            when(projectMapper.findById(1L)).thenReturn(normalProject);
            when(projectMemberMapper.findMembersByProjectId(1L)).thenReturn(members);

            ResponseResult<List<User>> result = projectService.getMembers(1L);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(2, result.getData().size());
            verify(projectMemberMapper).findMembersByProjectId(1L);
        }

        @Test
        @DisplayName("获取不存在的项目成员 - 返回404")
        void getMembersProjectNotFound() {
            when(projectMapper.findById(999L)).thenReturn(null);

            ResponseResult<List<User>> result = projectService.getMembers(999L);

            assertEquals(404, result.getCode());
            assertEquals("项目不存在", result.getMsg());
            verify(projectMemberMapper, never()).findMembersByProjectId(anyLong());
        }

        @Test
        @DisplayName("获取项目成员为空时返回空列表")
        void getMembersEmpty() {
            when(projectMapper.findById(1L)).thenReturn(normalProject);
            when(projectMemberMapper.findMembersByProjectId(1L)).thenReturn(Collections.emptyList());

            ResponseResult<List<User>> result = projectService.getMembers(1L);

            assertEquals(200, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("更新项目成员测试")
    class UpdateMembersTest {

        @Test
        @DisplayName("更新项目成员成功 - 全量替换")
        void updateMembersSuccess() {
            List<Long> userIds = Arrays.asList(2L, 3L, 4L);

            when(projectMapper.findById(1L)).thenReturn(normalProject);
            when(projectMemberMapper.deleteByProjectId(1L)).thenReturn(1);
            when(projectMemberMapper.insertByParams(anyLong(), anyLong())).thenReturn(1);

            ResponseResult<Void> result = projectService.updateMembers(1L, userIds);

            assertEquals(200, result.getCode());
            verify(projectMemberMapper).deleteByProjectId(1L);
            verify(projectMemberMapper, times(3)).insertByParams(anyLong(), anyLong());
            verify(projectMemberMapper).insertByParams(1L, 2L);
            verify(projectMemberMapper).insertByParams(1L, 3L);
            verify(projectMemberMapper).insertByParams(1L, 4L);
        }

        @Test
        @DisplayName("更新项目成员 - 传null userIds仅清空")
        void updateMembersWithNullUserIds() {
            when(projectMapper.findById(1L)).thenReturn(normalProject);
            when(projectMemberMapper.deleteByProjectId(1L)).thenReturn(1);

            ResponseResult<Void> result = projectService.updateMembers(1L, null);

            assertEquals(200, result.getCode());
            verify(projectMemberMapper).deleteByProjectId(1L);
            verify(projectMemberMapper, never()).insertByParams(anyLong(), anyLong());
        }

        @Test
        @DisplayName("更新项目成员 - 传空列表仅清空")
        void updateMembersWithEmptyUserIds() {
            when(projectMapper.findById(1L)).thenReturn(normalProject);
            when(projectMemberMapper.deleteByProjectId(1L)).thenReturn(1);

            ResponseResult<Void> result = projectService.updateMembers(1L, Collections.emptyList());

            assertEquals(200, result.getCode());
            verify(projectMemberMapper).deleteByProjectId(1L);
            verify(projectMemberMapper, never()).insertByParams(anyLong(), anyLong());
        }

        @Test
        @DisplayName("更新不存在的项目成员 - 返回404")
        void updateMembersProjectNotFound() {
            when(projectMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = projectService.updateMembers(999L, Collections.singletonList(1L));

            assertEquals(404, result.getCode());
            assertEquals("项目不存在", result.getMsg());
            verify(projectMemberMapper, never()).deleteByProjectId(anyLong());
            verify(projectMemberMapper, never()).insertByParams(anyLong(), anyLong());
        }
    }
}
