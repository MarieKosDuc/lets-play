package com.mariekd.letsplay.authentication;

import com.mariekd.letsplay.authentication.entities.Role;
import com.mariekd.letsplay.authentication.repositories.RoleRepository;
import com.mariekd.letsplay.authentication.services.implementations.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByName() {
        Role mockRole = new Role();
        mockRole.setName("USER");
        when(roleRepository.findByName("USER")).thenReturn(mockRole);

        Role role = roleService.findByName("USER");
        assertNotNull(role);
        assertEquals("USER", role.getName());
    }
}
