package com.mlmfreya.ferya2;

import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.UserRepository;
import com.mlmfreya.ferya2.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void testGetReferredUsers() {
        // Mock the dependencies of UserService
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        // Pass the mocked dependencies to the UserService constructor
        UserService userService = new UserService(userRepository);

        // Create a simple user network for testing
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        User user4 = new User();
        user1.setLeftChild(user2);
        user1.setRightChild(user3);
        user2.setLeftChild(user4);

        List<User> referredUsers = userService.getReferredUsers(user1);

        // Check that the referredUsers list contains the correct users
        assertTrue(referredUsers.contains(user1));
        assertTrue(referredUsers.contains(user2));
        assertTrue(referredUsers.contains(user3));
        assertEquals(4, referredUsers.size());
    }
}
