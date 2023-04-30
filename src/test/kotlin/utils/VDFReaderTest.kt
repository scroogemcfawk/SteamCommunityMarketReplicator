package utils

import org.json.JSONObject
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions
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
        val inputFile = this::class.java.getResource("VDFReaderTest/getEncoding_ASCII.ti")?.toURI()?.let { File(it) } ?: fail()
        assertTrue(VDFReader.getEncoding(inputFile) == Charsets.US_ASCII)
    }

    @Test
    @DisplayName("VDFReader.getEncoding() UTF-16")
    fun test_getEncoding_UTF16() {
        val inputFile = this::class.java.getResource("VDFReaderTest/getEncoding_UTF16.ti")?.toURI()?.let { File(it) } ?: fail()
        assertTrue(VDFReader.getEncoding(inputFile).contains(Charsets.UTF_16))
    }

    @Test
    @DisplayName("VDFReader.read(File) file exists")
    fun test_read_f_exists() {
        val actualJSON = this::class.java.getResource("VDFReaderTest/test_read_exists.ti")?.toURI()?.let { VDFReader.read(File(it)) } ?: fail(
            "Input file not found"
        )
        val expectedJSON =
            this::class.java.getResource("VDFReaderTest/test_read_exists.to")?.toURI()?.let { JSONObject(File(it).readText()) } ?: fail(
                "Target file not found"
            )
        JSONAssert.assertEquals(expectedJSON, actualJSON, JSONCompareMode.STRICT)
    }

    @Test
    @DisplayName("VDFReader.read(String) file exists")
    fun test_read_s_exists() {
        val actualJSON = this::class.java.getResource("VDFReaderTest/test_read_exists.ti")?.toURI()?.toPath()?.toString()?.let {
            VDFReader.read(it)
        } ?: fail(
            "Input file not found"
        )
        val expectedJSON =
            this::class.java.getResource("VDFReaderTest/test_read_exists.to")?.toURI()?.let { JSONObject(File(it).readText()) } ?: fail(
                "Target file not found"
            )
        JSONAssert.assertEquals(expectedJSON, actualJSON, JSONCompareMode.STRICT)
    }

    @Test
    @DisplayName("VDFReader.read(String) file does not exist")
    fun test_read_does_not_exist() {
        try {
            VDFReader.read("does_not_exist")
            fail("Read file that does not exist")
        } catch (_: Exception) { }
    }

    @Test
    @DisplayName("VDFReader.read() file with invalid format of braces")
    fun test_invalid_format_braces() {
        val inputFile = this::class.java.getResource("VDFReaderTest/invalid_format_braces.ti")?.toURI()?.toPath()?.toString()?.let {
            File(it)
        } ?: fail(
            "Input file not found"
        )
        try {
            VDFReader.read(inputFile)
            fail("Read a file with invalid format of braces")
        } catch (_: Exception) { }
    }

    @Test
    @DisplayName("VDFReader.read() file with invalid format of key value pair")
    fun test_invalid_format_pair() {
        val inputFile = this::class.java.getResource("VDFReaderTest/invalid_format_key_value.ti")?.toURI()?.toPath()?.toString()?.let {
            File(it)
        } ?: fail(
            "Input file not found"
        )
        try {
            println(VDFReader.read(inputFile))
            fail("Read a file with invalid format of key value")
        } catch (_: Exception) { }
    }

    @Test
    @DisplayName("VDFReader.read() items_game.txt. Proceed if \$CSITEMS is not set.")
    fun test_items_game() {
        val csitems = System.getenv("CSITEMS")
        Assumptions.assumeTrue(csitems.isNotBlank())
        VDFReader.read(csitems)
    }

    @Test
    @DisplayName("VDFReader.read() items_game.txt. Proceed if \$CSENGLISH is not set.")
    fun test_csgo_english() {
        val csitems = System.getenv("CSENGLISH")
        Assumptions.assumeTrue(csitems.isNotBlank())
        VDFReader.read(csitems)
    }
}