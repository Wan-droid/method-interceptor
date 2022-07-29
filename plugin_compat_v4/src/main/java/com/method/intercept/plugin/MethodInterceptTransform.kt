package com.method.intercept.plugin

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.google.common.io.Files
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.yaml.snakeyaml.Yaml
import java.io.*
import java.nio.file.attribute.FileTime
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class MethodInterceptTransform(configFile: File, outputDir: File, private val packagePrefix: String) : Transform() {
    private val intercept: Intercept
    private val outputLogFile = File(outputDir, RECORD_FILE_NAME)

    init {
        val yaml = Yaml()
        intercept = yaml.loadAs(FileInputStream(configFile), Intercept::class.java)

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        if (!outputLogFile.exists()) {
            outputLogFile.createNewFile()
        }
    }

    override fun getName() = "MethodInterceptTransform"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_JARS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT


    override fun isIncremental(): Boolean = true


    override fun transform(invocation: TransformInvocation) {
        val outputProvider = invocation.outputProvider
        if (!invocation.isIncremental) {
            outputProvider.deleteAll()
        }
        invocation.inputs.forEach { input ->
            input.jarInputs.forEach { jar ->
                val outputJar = outputProvider.getContentLocation(jar.name, jar.contentTypes, jar.scopes, Format.JAR)
                if (invocation.isIncremental) {
                    when (jar.status!!) {
                        Status.NOTCHANGED -> {}
                        Status.ADDED, Status.CHANGED -> handleJar(jar.file, outputJar)
                        Status.REMOVED -> FileUtils.delete(outputJar)
                    }
                } else {
                    handleJar(jar.file, outputJar)
                }
            }
            input.directoryInputs.forEach { dir ->
                val inputDir = dir.file
                val outputDir = outputProvider.getContentLocation(dir.name, dir.contentTypes, dir.scopes, Format.DIRECTORY)
                if (invocation.isIncremental) {
                    for ((inputFile, status) in dir.changedFiles.entries) {
                        when (status!!) {
                            Status.NOTCHANGED -> {}
                            Status.ADDED, Status.CHANGED -> if (!inputFile.isDirectory && inputFile.name.endsWith(SdkConstants.DOT_CLASS)) {
                                val out = toOutputFile(outputDir, inputDir, inputFile)
                                handleDir(inputDir, out)
                            }
                            Status.REMOVED -> {
                                val outputFile = toOutputFile(outputDir, inputDir, inputFile)
                                FileUtils.deleteIfExists(outputFile)
                            }
                        }
                    }
                } else {
                    FileUtils.getAllFiles(inputDir)
                        .filter { it!!.name.endsWith(SdkConstants.DOT_CLASS) }
                        .forEach { file ->
                            val out = toOutputFile(outputDir, inputDir, file)
                            handleDir(file, out)
                        }
                }
            }
        }
        super.transform(invocation)
    }

    private fun handleJar(inputJar: File, outputJar: File) {
        Files.createParentDirs(outputJar)

        val inputZip = ZipFile(inputJar)
        val outputZip = ZipOutputStream(BufferedOutputStream(FileOutputStream(outputJar)))
        try {
            val inEntries = inputZip.entries()
            while (inEntries.hasMoreElements()) {
                val entry: ZipEntry = inEntries.nextElement()
                val originalFile = BufferedInputStream(inputZip.getInputStream(entry))
                val outEntry = ZipEntry(entry.name)
                val className = outEntry.name.replace("/", ".")
                val newEntryContent = if (className.endsWith(".class")) {
                    internalTransform(originalFile)
                } else {
                    IOUtils.toByteArray(originalFile)
                }
                val crc32 = CRC32()
                crc32.update(newEntryContent)
                outEntry.crc = crc32.value
                outEntry.method = ZipEntry.STORED
                outEntry.size = newEntryContent.size.toLong()
                outEntry.compressedSize = newEntryContent.size.toLong()
                outEntry.lastAccessTime = FileTime.fromMillis(0)
                outEntry.lastModifiedTime = FileTime.fromMillis(0)
                outEntry.creationTime = FileTime.fromMillis(0)
                outputZip.putNextEntry(outEntry)
                outputZip.write(newEntryContent)
                outputZip.closeEntry()
            }
            outputZip.flush()
        } finally {
            outputZip.close()
            inputZip.close()
        }
    }

    private fun handleDir(inputFile: File, outputFile: File) {
        Files.createParentDirs(outputFile)
        FileInputStream(inputFile).use { fis ->
            FileOutputStream(outputFile).use { fos ->
                //transform
                internalTransform(fis, fos)
            }
        }
    }

    private fun internalTransform(inputStream: InputStream): ByteArray {
        val classReader = ClassReader(inputStream)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val visitor = createClassVisitor(intercept.intercept, outputLogFile, packagePrefix, classWriter)
        classReader.accept(visitor, 0)
        return classWriter.toByteArray()
    }

    private fun internalTransform(inputStream: InputStream, outputStream: OutputStream) {
        val classReader = ClassReader(inputStream)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val visitor = createClassVisitor(intercept.intercept, outputLogFile, packagePrefix, classWriter)
        classReader.accept(visitor, ClassReader.SKIP_FRAMES)
        val bytes = classWriter.toByteArray()
        outputStream.write(bytes)
    }

    private fun toOutputFile(outputDir: File, inputDir: File, inputFile: File): File {
        return File(outputDir, FileUtils.relativePossiblyNonExistingPath(inputFile, inputDir))
    }
}