package com.ytkf2.bahceasist.core.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TurkeyLocationRegistryTest {

    @Test
    fun verifyTotalProvincesCount() {
        val provinces = TurkeyLocationRegistry.allProvinces
        assertEquals(81, provinces.size)
        assertTrue(provinces.contains("Ankara"))
        assertTrue(provinces.contains("İstanbul"))
        assertTrue(provinces.contains("İzmir"))
    }

    @Test
    fun verifyTotalDistrictsCount() {
        val districts = TurkeyLocationRegistry.allDistricts
        assertEquals(973, districts.size)
    }

    @Test
    fun verifyAnkaraContainsKahramankazan() {
        val ankaraDistricts = TurkeyLocationRegistry.getDistricts("Ankara")
        assertTrue("Ankara must have districts", ankaraDistricts.isNotEmpty())

        val kahramankazan = ankaraDistricts.find { it.district == "Kahramankazan" }
        assertNotNull("Kahramankazan must exist in Ankara", kahramankazan)
        assertEquals("Ankara", kahramankazan?.province)
        assertEquals(40.2053, kahramankazan?.latitude ?: 0.0, 0.001)
        assertEquals(32.6806, kahramankazan?.longitude ?: 0.0, 0.001)
    }

    @Test
    fun verifySearchKahramankazanAndKazan() {
        // Search by full name
        val resultsFull = TurkeyLocationRegistry.search("kahramankazan")
        assertTrue("Searching 'kahramankazan' should find Kahramankazan",
            resultsFull.any { it.district == "Kahramankazan" && it.province == "Ankara" })

        // Search by legacy / common name 'kazan'
        val resultsKazan = TurkeyLocationRegistry.search("kazan")
        assertTrue("Searching 'kazan' should match Kahramankazan",
            resultsKazan.any { it.district == "Kahramankazan" && it.province == "Ankara" })

        // Search by multi-word 'ankara kazan'
        val resultsMulti = TurkeyLocationRegistry.search("ankara kazan")
        assertTrue("Searching 'ankara kazan' should match Ankara, Kahramankazan",
            resultsMulti.any { it.district == "Kahramankazan" && it.province == "Ankara" })

        // Search by uppercase and Turkish characters
        val resultsUpper = TurkeyLocationRegistry.search("KAHRAMANKAZAN")
        assertTrue("Searching 'KAHRAMANKAZAN' should match",
            resultsUpper.any { it.district == "Kahramankazan" && it.province == "Ankara" })
    }

    @Test
    fun verifyCoordinatesValidity() {
        // Turkey is roughly within 35-43 lat, 25-45 lon
        for (district in TurkeyLocationRegistry.allDistricts) {
            assertTrue("Latitude out of range for ${district.displayName}: ${district.latitude}",
                district.latitude in 35.0..43.0)
            assertTrue("Longitude out of range for ${district.displayName}: ${district.longitude}",
                district.longitude in 25.0..45.0)
        }
    }
}
