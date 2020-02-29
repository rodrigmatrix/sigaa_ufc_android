package com.rodrigmatrix.sigaaufc.internal.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rodrigmatrix.sigaaufc.persistence.entity.File
import com.rodrigmatrix.sigaaufc.persistence.entity.Grade
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class ExtensionsTest {

    @Test
    fun `given 2 equal list return empty list`(){
        val list = listOf(
            File("1", "1", "arquivo", ""),
            File("2", "1", "arquivo", "")
        )
        val list2 = listOf(
            File("1", "1", "arquivo", ""),
            File("2", "1", "arquivo", "")
        )
        assert(list.getUncommonElements(list2).isEmpty())
    }

    @Test
    fun `given 2 different list return size of list1 - list2`(){
        val list = listOf(
            File("1", "1", "arquivo", ""),
            File("2", "1", "arquivo", "")
        )
        val list2 = listOf(
            File("1", "1", "arquivo", ""),
            File("2", "1", "arquivo", ""),
            File("3", "1", "arquivo", ""),
            File("4", "1", "arquivo", "")
        )
        val uncommon = list.getUncommonElements(list2)
        assert(uncommon.size == abs(list.size - list2.size))
    }

    @Test
    fun `given 2 different list with new element on middle return that element`(){
        val middleElement = File("3", "1", "arquivo", "")
        val list = listOf(
            File("1", "1", "arquivo", ""),
            File("2", "1", "arquivo", "")
        )
        val list2 = listOf(
            File("1", "1", "arquivo", ""),
            middleElement,
            File("2", "1", "arquivo", "")
        )
        val uncommon = list.getUncommonElements(list2)
        assert((uncommon.size == 1 && uncommon.first() == middleElement))
    }

    @Test
    fun `given 2 different list with 2 new elements on middle return those elements`(){
        val middleElement = File("3", "1", "arquivo", "")
        val secondMiddleElement = File("4", "1", "arquivo", "")
        val list = listOf(
            File("1", "1", "arquivo", ""),
            File("2", "1", "arquivo", "")
        )
        val list2 = listOf(
            File("1", "1", "arquivo", ""),
            middleElement,
            secondMiddleElement,
            File("2", "1", "arquivo", "")
        )
        val uncommon = list.getUncommonElements(list2)
        assert((uncommon.size == 2 && (uncommon.first() == middleElement) && (uncommon[1] == secondMiddleElement)))
    }

    @Test
    fun `given empty list return all elements from the other list`(){
        val list = listOf<File>()
        val list2 = listOf(
            File("1", "1", "arquivo", ""),
            File("3", "1", "arquivo", ""),
            File("4", "1", "arquivo", ""),
            File("2", "1", "arquivo", "")
        )
        val uncommon = list.getUncommonElements(list2)
        assert(uncommon.size == list2.size)
    }


}