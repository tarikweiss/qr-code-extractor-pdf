package de.tarikweiss.pdfqr

import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO


fun main(args: Array<String>) {
    val pdfPath = args[0]

    val file = File(pdfPath)
    if (!file.canRead()) {
        throw IOException("File not readable!")
    }

    val pdfDocument = Loader.loadPDF(file)

    val pageResults = mutableListOf<PageResult>()

    pdfDocument.pages.forEachIndexed { index, page ->
        val images = getImagesFromPage(page)

        val pageData = mutableListOf<String>()

        images.forEach {
            val outStream = ByteArrayOutputStream()
            ImageIO.write(it, "PNG", outStream)

            val inStream = ByteArrayInputStream(outStream.toByteArray())
            val bufferedImage: BufferedImage = ImageIO.read(inStream)
            val luminanceSource: LuminanceSource = BufferedImageLuminanceSource(bufferedImage)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(luminanceSource))
            val result = MultiFormatReader().decode(binaryBitmap)

            pageData.add(result.text)
        }

        if (pageData.size > 0) {
            pageResults.add(PageResult(index + 1, pageData))
        }
    }

    println(Json.encodeToString(Result(pageResults)))
}

private fun getImagesFromPage(page: PDPage): List<RenderedImage> {
    return getImagesFromResources(page.resources)
}

private fun getImagesFromResources(resources: PDResources): List<RenderedImage> {
    val images: MutableList<RenderedImage> = ArrayList()

    for (xObjectName in resources.xObjectNames) {
        val xObject = resources.getXObject(xObjectName)

        if (xObject is PDFormXObject) {
            images.addAll(getImagesFromResources(xObject.resources))

            continue
        }

        if (xObject is PDImageXObject) {
            images.add(xObject.image)
        }
    }

    return images
}

@Serializable
data class Result(val pages: List<PageResult>)

@Serializable
data class PageResult(
    val pageNumber: Int,
    val decodedData: List<String>
)