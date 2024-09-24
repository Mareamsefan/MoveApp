package com.example.moveapp.HelpFunctionsTest

import org.junit.Test
import org.junit.Assert.*
import com.example.moveapp.utility.HelpFunctions

class HelpUnitTests {

    @Test
    fun testCheckIfStringGetsHashed() {
        val testString = "DetteErMittPassord"

        val hashedPassword = HelpFunctions.passwordEncryptor(testString)

        assertNotEquals(testString, hashedPassword)
    }

    @Test
    fun testCheckIfTwoDifferentHashedStringsAreNotEqual() {
        val testString1 = "DetteErMittPassord"
        val testString2 = "ForskjelligePassord"

        val hashedPassword1 = HelpFunctions.passwordEncryptor(testString1)
        val hashedPassword2 = HelpFunctions.passwordEncryptor(testString2)
        println(hashedPassword1)
3
        assertNotEquals(hashedPassword1, hashedPassword2)
    }

    @Test
    fun testEmptyPasswordIsHashed(){
        val testString1 = ""
        val hashedPassword = HelpFunctions.passwordEncryptor(testString1)

        assertNotNull(hashedPassword)
    }


    @Test
    fun testVeryLongPasswordsAreHashed(){
        val testString1 = "a".repeat(1000)
        val hashedPassword = HelpFunctions.passwordEncryptor(testString1)

        assertNotNull(hashedPassword)
    }

    @Test
    fun testHashingPasswordGivesExpectedResult(){
        val testString1 = "DetteErMittPassord"
        val expectedHash = "f49296156128e306b68e6133155a6d2bf50b6d6b81cb7f05a2a1b29ad926a11e"

        val hashedPassword = HelpFunctions.passwordEncryptor(testString1)
        assertEquals(hashedPassword, expectedHash)
    }


    @Test
    fun testHashingWorksWithSpecialCharacters(){
        val testString1 = "D3tte£rMittPass¤rd!%>|"
        val hashedPassword = HelpFunctions.passwordEncryptor(testString1)

        assertNotNull(hashedPassword)
    }


    @Test
    fun testHashingIsCaseSensitive() {
        val testString1 = "DetteErMittPassord"
        val testString2 = "detteermittpassord"

        val hashedPassword1 = HelpFunctions.passwordEncryptor(testString1)
        val hashedPassword2 = HelpFunctions.passwordEncryptor(testString2)

        assertNotEquals(hashedPassword1, hashedPassword2)
    }

    @Test
    fun testSameStringGivesSameHash(){
        val testString1 = "DetteErMittPassord"

        val hashedPassword1 = HelpFunctions.passwordEncryptor(testString1)
        val hashedPassword2 = HelpFunctions.passwordEncryptor(testString1)

        assertEquals(hashedPassword1, hashedPassword2)
    }
}