package com.example.tutorme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import com.example.tutorme.AddClassActivity
import com.example.tutorme.databinding.ActivityAddClassBinding
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SchoolListLatLonTest {

    val obj = SchoolListActivity()

    @Test
    fun latLonIsCorrect() {
        assertEquals(obj.latLonInKm(39.103119, -84.512016, 39.983334, -82.983330), 163.58, 0.5)
    }

    @Test
    fun latLonIsCorrect2() {
        assertEquals(obj.latLonInKm(39.103119, -84.512016, 39.103119, -84.512016), 0.0, 0.0)
    }

    @Test
    fun latLonIsCorrect3() {
        assertEquals(obj.latLonInKm(39.983334, -82.983330, 39.758949, -84.191605), 106.09, 0.5)
    }

    @Test
    fun latLonIsCorrect4() {
        assertEquals(obj.latLonInKm(39.983334, -82.983330, 33.753746, -84.386330), 703.8, 1.0)
    }
}
