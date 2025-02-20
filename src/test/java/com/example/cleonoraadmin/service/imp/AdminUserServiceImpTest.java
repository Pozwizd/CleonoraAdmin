package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.UploadFile;
import com.example.cleonoraadmin.entity.AdminRole;
import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.mapper.AdminUserMapper;
import com.example.cleonoraadmin.model.admin.AdminUserProfileRequest;
import com.example.cleonoraadmin.model.admin.AdminUserProfileResponse;
import com.example.cleonoraadmin.model.admin.AdminUserRequest;
import com.example.cleonoraadmin.model.admin.AdminUserResponse;
import com.example.cleonoraadmin.repository.AdminUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImpTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private AdminUserMapper adminUserMapper;

    @Mock
    private UploadFile uploadFile;

    @InjectMocks
    private AdminUserServiceImp adminUserService;

    @Test
    void testFindByUsername_UserFound() {
        String username = "test@example.com";
        AdminUser mockUser = new AdminUser();
        mockUser.setEmail(username);

        when(adminUserRepository.findByEmail(username)).thenReturn(Optional.of(mockUser));

        AdminUser result = adminUserService.findByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getEmail());
        verify(adminUserRepository, times(1)).findByEmail(username);
    }

    @Test
    void testFindByUsername_UserNotFound() {
        String username = "nonexistent@example.com";

        when(adminUserRepository.findByEmail(username)).thenReturn(Optional.empty());

        AdminUser result = adminUserService.findByUsername(username);

        assertNull(result);
        verify(adminUserRepository, times(1)).findByEmail(username);
    }

    @Test
    void testSaveNewUser() {
        AdminUser adminUser = new AdminUser();
        adminUser.setEmail("newuser@example.com");

        when(adminUserRepository.save(adminUser)).thenReturn(adminUser);

        AdminUser savedUser = adminUserService.saveNewUser(adminUser);

        assertNotNull(savedUser);
        assertEquals(adminUser.getEmail(), savedUser.getEmail());
        verify(adminUserRepository, times(1)).save(adminUser);
    }

    @Test
    void testGetPageAllAdminUsers() {
        // Arrange
        int page = 0;
        int size = 10;
        String search = "test";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));

        // Создаем список пользователей
        AdminUser user1 = new AdminUser();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@example.com");

        AdminUser user2 = new AdminUser();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setEmail("jane@example.com");

        List<AdminUser> users = Arrays.asList(user1, user2);

        Page<AdminUser> userPage = new PageImpl<>(users, pageRequest, users.size());

        // Создаем ответы
        AdminUserResponse response1 = new AdminUserResponse();
        response1.setId(1L);
        response1.setName("John");
        response1.setEmail("john@example.com");

        AdminUserResponse response2 = new AdminUserResponse();
        response2.setId(2L);
        response2.setName("Jane");
        response2.setEmail("jane@example.com");

        List<AdminUserResponse> responses = Arrays.asList(response1, response2);

        when(adminUserRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(userPage);
        when(adminUserMapper.adminUsertoResponsePage(userPage)).thenReturn(new PageImpl<>(responses, pageRequest, responses.size()));

        Page<AdminUserResponse> result = adminUserService.getPageAllAdminUsers(page, size, search);

        assertNotNull(result);
        assertEquals(responses.size(), result.getContent().size());
        assertEquals("John", result.getContent().get(0).getName());
        assertEquals("Jane", result.getContent().get(1).getName());

        verify(adminUserRepository, times(1)).findAll(any(Specification.class), eq(pageRequest));
        verify(adminUserMapper, times(1)).adminUsertoResponsePage(userPage);
    }

    @Test
    void testIfUserMoreThan_True() {
        long count = 5L;
        int threshold = 3;

        when(adminUserRepository.count()).thenReturn(count);

        boolean result = adminUserService.ifUserMoreThan(threshold);

        assertTrue(result);
        verify(adminUserRepository, times(1)).count();
    }

    @Test
    void testIfUserMoreThan_False() {
        long count = 2L;
        int threshold = 3;

        when(adminUserRepository.count()).thenReturn(count);

        boolean result = adminUserService.ifUserMoreThan(threshold);

        assertFalse(result);
        verify(adminUserRepository, times(1)).count();
    }

    @Test
    void testGetAdminUserById_UserFound() {
        Long id = 1L;
        AdminUser mockUser = new AdminUser();
        mockUser.setId(id);

        when(adminUserRepository.findById(id)).thenReturn(Optional.of(mockUser));

        Optional<AdminUser> result = adminUserService.getAdminUserById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(adminUserRepository, times(1)).findById(id);
    }

    @Test
    void testGetAdminUserById_UserNotFound() {
        Long id = 1L;

        when(adminUserRepository.findById(id)).thenReturn(Optional.empty());

        Optional<AdminUser> result = adminUserService.getAdminUserById(id);

        assertFalse(result.isPresent());
        verify(adminUserRepository, times(1)).findById(id);
    }

    @Test
    void testGetAdminUserResponseById_UserFound() {
        Long id = 1L;
        AdminUser mockUser = new AdminUser();
        mockUser.setId(id);
        AdminUserResponse mockResponse = new AdminUserResponse();

        when(adminUserRepository.findById(id)).thenReturn(Optional.of(mockUser));
        when(adminUserMapper.adminUsertoResponse(mockUser)).thenReturn(mockResponse);

        AdminUserResponse result = adminUserService.getAdminUserResponseById(id);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(adminUserRepository, times(1)).findById(id);
        verify(adminUserMapper, times(1)).adminUsertoResponse(mockUser);
    }

    @Test
    void testGetAdminUserResponseById_UserNotFound() {
        Long id = 1L;

        when(adminUserRepository.findById(id)).thenReturn(Optional.empty());
        when(adminUserMapper.adminUsertoResponse(null)).thenReturn(null);

        AdminUserResponse result = adminUserService.getAdminUserResponseById(id);

        assertNull(result);
        verify(adminUserRepository, times(1)).findById(id);
        verify(adminUserMapper, times(1)).adminUsertoResponse(null);
    }

    @Test
    void testSaveNewUserFromRequest() {
        AdminUserRequest request = new AdminUserRequest();
        request.setEmail("newuser@example.com");
        AdminUser mockUser = new AdminUser();
        AdminUserResponse mockResponse = new AdminUserResponse();

        when(adminUserMapper.adminUserResponsetoEntity(request)).thenReturn(mockUser);
        when(adminUserRepository.save(mockUser)).thenReturn(mockUser);
        when(adminUserMapper.adminUsertoResponse(mockUser)).thenReturn(mockResponse);

        AdminUserResponse result = adminUserService.saveNewUserFromRequest(request);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(adminUserMapper, times(1)).adminUserResponsetoEntity(request);
        verify(adminUserRepository, times(1)).save(mockUser);
        verify(adminUserMapper, times(1)).adminUsertoResponse(mockUser);
    }

    @Test
    void testUpdateAdminUser() {
        Long id = 1L;
        AdminUserRequest request = new AdminUserRequest();
        request.setEmail("updated@example.com");
        AdminUser existingUser = new AdminUser();
        existingUser.setAvatar("old-avatar.png");
        AdminUser updatedUser = new AdminUser();
        AdminUserResponse mockResponse = new AdminUserResponse();

        when(adminUserRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(adminUserMapper.adminUserResponsetoEntity(request)).thenReturn(updatedUser);
        when(adminUserRepository.save(updatedUser)).thenReturn(updatedUser);
        when(adminUserMapper.adminUsertoResponse(updatedUser)).thenReturn(mockResponse);

        AdminUserResponse result = adminUserService.updateAdminUser(id, request);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        assertEquals("old-avatar.png", updatedUser.getAvatar());
        verify(adminUserRepository, times(1)).findById(id);
        verify(adminUserMapper, times(1)).adminUserResponsetoEntity(request);
        verify(adminUserRepository, times(1)).save(updatedUser);
        verify(adminUserMapper, times(1)).adminUsertoResponse(updatedUser);
    }

    @Test
    void testDeleteAdminUserById_Success() {
        Long id = 1L;

        doNothing().when(adminUserRepository).deleteById(id);

        boolean result = adminUserService.deleteAdminUserById(id);

        assertTrue(result);
        verify(adminUserRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteAdminUserById_Failure() {
        Long id = 1L;

        doThrow(new RuntimeException("Database error")).when(adminUserRepository).deleteById(id);

        boolean result = adminUserService.deleteAdminUserById(id);

        assertFalse(result);
        verify(adminUserRepository, times(1)).deleteById(id);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        String username = "test@example.com";
        AdminUser mockUser = new AdminUser();
        mockUser.setEmail(username);
        mockUser.setPassword("password");
        mockUser.setRole(AdminRole.ADMIN);

        when(adminUserRepository.findByEmail(username)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = adminUserService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(adminUserRepository, times(1)).findByEmail(username);
    }

    @Test
    void testGetAdminUserProfile_UserFound() {
        String username = "test@example.com";
        AdminUser mockUser = new AdminUser();
        mockUser.setEmail(username);
        AdminUserProfileResponse mockProfileResponse = new AdminUserProfileResponse();

        when(adminUserRepository.findByEmail(username)).thenReturn(Optional.of(mockUser));
        when(adminUserMapper.adminUserToProfileResponse(mockUser)).thenReturn(mockProfileResponse);

        AdminUserProfileResponse result = adminUserService.getAdminUserProfile(username);

        assertNotNull(result);
        assertEquals(mockProfileResponse, result);
        verify(adminUserRepository, times(1)).findByEmail(username);
        verify(adminUserMapper, times(1)).adminUserToProfileResponse(mockUser);
    }

    @Test
    void testGetAdminUserProfile_UserNotFound() {
        String username = "nonexistent@example.com";

        when(adminUserRepository.findByEmail(username)).thenReturn(Optional.empty());
        when(adminUserMapper.adminUserToProfileResponse(null)).thenReturn(null);

        AdminUserProfileResponse result = adminUserService.getAdminUserProfile(username);

        assertNull(result);
        verify(adminUserRepository, times(1)).findByEmail(username);
        verify(adminUserMapper, times(1)).adminUserToProfileResponse(null);
    }

    @Test
    void testGetUserByEmail_UserFound() {
        String email = "test@example.com";
        AdminUser mockUser = new AdminUser();
        mockUser.setEmail(email);

        when(adminUserRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        Optional<AdminUser> result = adminUserService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(adminUserRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        String email = "nonexistent@example.com";

        when(adminUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<AdminUser> result = adminUserService.getUserByEmail(email);

        assertFalse(result.isPresent());
        verify(adminUserRepository, times(1)).findByEmail(email);
    }

    @Test
    void testUpdateAdminUserProfile_Success() {
        AdminUserProfileRequest request = new AdminUserProfileRequest();
        request.setEmail("updated@example.com");

        AdminUser updatedUser = new AdminUser();
        updatedUser.setEmail(request.getEmail());

        AdminUserProfileResponse mockProfileResponse = new AdminUserProfileResponse();

        when(adminUserMapper.adminUserProfileRequestToEntity(request, uploadFile)).thenReturn(updatedUser);
        when(adminUserRepository.save(updatedUser)).thenReturn(updatedUser);
        when(adminUserMapper.adminUserToProfileResponse(updatedUser)).thenReturn(mockProfileResponse);

        AdminUserProfileResponse result = adminUserService.updateAdminUserProfile(request);

        assertNotNull(result);
        assertEquals(mockProfileResponse, result);
        verify(adminUserMapper, times(1)).adminUserProfileRequestToEntity(request, uploadFile);
        verify(adminUserRepository, times(1)).save(updatedUser);
        verify(adminUserMapper, times(1)).adminUserToProfileResponse(updatedUser);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonexistent@example.com";

        when(adminUserRepository.findByEmail(username)).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> adminUserService.loadUserByUsername(username)
        );

        assertEquals("User not found with username: " + username, exception.getMessage());

        verify(adminUserRepository, times(1)).findByEmail(username);
    }
}