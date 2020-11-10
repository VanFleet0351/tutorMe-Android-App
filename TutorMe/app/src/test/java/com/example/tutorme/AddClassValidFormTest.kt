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
class AddClassValidFormTest {

    val obj = AddClassActivity()

    @Test
    fun invalidClassIsCorrect() {
        assertEquals(obj.invalidClass("","",1), true)
    }

    @Test
    fun invalidClassIsCorrect2() {
        assertEquals(obj.invalidClass("CSE","",1), true)
    }

    @Test
    fun invalidClassIsCorrect3() {
        assertEquals(obj.invalidClass("CSE","2221",-1), true)
    }

    @Test
    fun invalidClassIsCorrect4() {
        assertEquals(obj.invalidClass("","2221",0), true)
    }

    @Test
    fun validClassIsCorrect() {
        assertEquals(obj.invalidClass("CSE","2221",0), false)
    }

    @Test
    fun validClassIsCorrect2() {
        assertEquals(obj.invalidClass("MATH","3345",1), false)
    }

}
