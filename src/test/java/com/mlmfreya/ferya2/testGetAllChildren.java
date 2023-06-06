package com.mlmfreya.ferya2;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.UserRepository;
import com.mlmfreya.ferya2.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testGetAllChildren {

    @Test
    public void testGetAllChildren() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(mockUserRepository);

        // Create a simple user network
        User user1 = createUser(null, null);
        User user2 = createUser(null, null);
        User user3 = createUser(user1, user2);
        User user4 = createUser(null, null);
        User user5 = createUser(null, null);
        User user6 = createUser(user4, user5);
        User rootUser = createUser(user3, user6);

        // Test getAllChildren method
        List<User> allChildren = userService.getAllChildren(rootUser);
        assertEquals(6, allChildren.size());
        assertTrue(allChildren.contains(user1));
        assertTrue(allChildren.contains(user2));
        assertTrue(allChildren.contains(user3));
        assertTrue(allChildren.contains(user4));
        assertTrue(allChildren.contains(user5));
        assertTrue(allChildren.contains(user6));
    }

    private User createUser(User leftChild, User rightChild) {
        User user = new User();
        user.setLeftChild(leftChild);
        user.setRightChild(rightChild);
        return user;
    }
}
