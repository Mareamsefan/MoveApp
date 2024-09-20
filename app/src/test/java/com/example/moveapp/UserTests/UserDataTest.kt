package com.example.moveapp.UserTests

import org.junit.Test
import org.junit.Assert.*
import com.example.moveapp.data.UserData
import com.example.moveapp.utility.UserTypeEnum

class UserDataTest {

    @Test
    fun testDefaultInstantiation() {
        val user = UserData()

        // Ensure the default values are set correctly
        assertNotNull(user.userId)
        assertEquals("", user.username)
        assertEquals("", user.email)
        assertEquals("", user.password)
        assertEquals("", user.location)
        assertNull(user.dateRegistered)  // The timestamp should be null by default
        assertEquals("", user.profilePictureUrl)
        assertTrue(user.favorites.isEmpty())  // Ensure the list is empty
        assertEquals(UserTypeEnum.REGULAR, user.userType)  // Default user type should be REGULAR
    }

    @Test
    fun testInstantiationWithArgumentsShouldNotHaveDefaultValues(){
        val user = UserData(username = "username", email = "email", password = "password", location="location")

        assertEquals("username", user.username)
        assertEquals("email", user.email)
        assertEquals("password", user.password)
        assertEquals("location", user.location)
    }

    @Test
    fun testUniqueIdIsCreatedOnInstansiation(){
        val user1 = UserData()
        val user2 = UserData()

        assertNotNull(user1.userId)
        assertNotNull(user2.userId)
        assertNotEquals(user1.userId, user2.userId)
    }

    @Test
    fun testPropertiesAreMutable(){
        val user = UserData(username = "username")

        user.username = "NewUsername"

        assertEquals(user.username, "NewUsername")

    }
}