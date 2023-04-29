package utils

import org.json.JSONObject
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.fail
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.io.File
import kotlin.io.path.toPath

class VDFReaderTest {

    @Test
    @DisplayName("VDFReader.getEncoding() ASCII")
    fun test_getEncoding_ASCII() {
        val file = this::class.java.getResource("VDFReaderTest/getEncoding_ASCII.ti")?.toURI()?.let { File(it) } ?: fail()
        assertTrue(VDFReader.getEncoding(file) == Charsets.US_ASCII)
    }

    @Test
    @DisplayName("VDFReader.getEncoding() UTF-16")
    fun test_getEncoding_UTF16() {
        val file = this::class.java.getResource("VDFReaderTest/getEncoding_UTF16.ti")?.toURI()?.let { File(it) } ?: fail()
        assertTrue(VDFReader.getEncoding(file).contains(Charsets.UTF_16))
    }

    @Test
    @DisplayName("VDFReader.read(File) file exists")
    fun test_read_f_exists() {
        val input = this::class.java.getResource("VDFReaderTest/test_read_exists.ti")?.toURI()?.let { VDFReader.read(File(it)) } ?: fail(
            "Input file not found"
        )
        val target =
            this::class.java.getResource("VDFReaderTest/test_read_exists.to")?.toURI()?.let { JSONObject(File(it).readText()) } ?: fail(
                "Target file not found"
            )
        JSONAssert.assertEquals(input, target, JSONCompareMode.STRICT)
    }

    @Test
    @DisplayName("VDFReader.read(String) file exists")
    fun test_read_s_exists() {
        val input = this::class.java.getResource("VDFReaderTest/test_read_exists.ti")?.toURI()?.toPath()?.toString()?.let {
            VDFReader.read(it)
        } ?: fail(
            "Input file not found"
        )
        val target =
            this::class.java.getResource("VDFReaderTest/test_read_exists.to")?.toURI()?.let { JSONObject(File(it).readText()) } ?: fail(
                "Target file not found"
            )
        JSONAssert.assertEquals(input, target, JSONCompareMode.STRICT)
    }

    @Test
    @DisplayName("VDFReader.read(String) file does not exist")
    fun test_read_does_not_exist() {
        try {
            VDFReader.read("does_not_exist")
            fail("Read file that does not exist")
        } catch (_: Exception) { }
    }
}